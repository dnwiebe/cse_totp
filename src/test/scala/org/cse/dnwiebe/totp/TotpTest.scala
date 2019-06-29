package org.cse.dnwiebe.totp

import java.time.temporal.ChronoUnit
import java.time.{Duration, Instant}

import org.scalatest.path

class TotpTest extends path.FunSpec {

  describe ("A default Totp") {
    val subject = new Totp

    describe ("having generated two Identities") {
      val oneIdentity = subject.generateIdentity ()
      val anotherIdentity = subject.generateIdentity ()

      it ("shows that Identity::equals works") {
        val oneIdentityCopy = oneIdentity

        assert (oneIdentity !== null)
        assert (oneIdentity !== "booga")
        assert (oneIdentity === oneIdentityCopy)
        assert (oneIdentity !== anotherIdentity)
      }
    }

    describe ("instructed to generate ten Identities") {
      val identities = (0 until 10).map (_ => subject.generateIdentity ())

      it ("does so so that no two are equal") {
        for (i <- 0 until 10; j <- 0 until 10 if i != j) {
          assert (identities (i) !== identities (j))
        }
      }

      it ("produces passwords that are all of the default length") {
        val now = Instant.now()
        val timestamps = (0 until 10).map (offset => now.plus (offset, ChronoUnit.DAYS))
        for (
          identity <- identities;
          timestamp <- timestamps
        ) {
          val password = subject.generatePassword(identity, timestamp)
          assert (password.length == 6)
        }
      }
    }

    describe ("probed for the beginning and end of a time step") {
      val timestamp = Instant.now ()
      val firstMillisecond = scanForArticulationPoint(subject, timestamp, -1000)
      val millisecondAfterLast = scanForArticulationPoint(subject, timestamp, 1000)

      it ("shows that the default time step is 60 seconds") {
        assert (Duration.between (firstMillisecond, millisecondAfterLast).get (ChronoUnit.SECONDS) === 60)
      }
    }
  }

  describe ("A Totp with a non-default time step") {
    val subject = new Totp (timeStepSeconds = 120)

    describe ("probed for the beginning and end of a time step") {
      val timestamp = Instant.now ()
      val firstMillisecond = scanForArticulationPoint(subject, timestamp, -1000)
      val millisecondAfterLast = scanForArticulationPoint(subject, timestamp, 1000)

      it ("shows that the time step is correct") {
        assert (Duration.between (firstMillisecond, millisecondAfterLast).get (ChronoUnit.SECONDS) === 120)
      }
    }
  }

  describe ("A Totp with a non-default password length") {
    val subject = new Totp (passwordLength = 8)
    val identity = subject.generateIdentity()

    describe ("asked for a series of passwords") {
      val now = Instant.now()
      val passwords = (0 until 10).map (offset => subject.generatePassword(identity, now.plus (offset, ChronoUnit.DAYS)))

      it ("makes them all of the proper length") {
        passwords.foreach (password => assert (password.length === 8))
      }
    }
  }

  private def scanForArticulationPoint (subject: Totp, start: Instant, stepMillis: Int): Instant = {
    val identity = subject.generateIdentity ()
    val startPassword = subject.generatePassword (identity, start)
    var prev = start
    var curr = start.plusMillis (stepMillis)
    while (subject.generatePassword (identity, curr) == startPassword) {
      prev = curr
      curr = curr.plusMillis (stepMillis)
    }
    if (prev.isBefore (curr)) {
      probeForArticulationPoint (subject, prev, curr)
    }
    else {
      probeForArticulationPoint (subject, curr, prev)
    }
  }

  private def probeForArticulationPoint (subject: Totp, begin: Instant, end: Instant): Instant = {
    if (end.toEpochMilli - begin.toEpochMilli <= 1L) {
      return end
    }
    val identity = subject.generateIdentity ()
    val beginPassword = subject.generatePassword (identity, begin)
    val endPassword = subject.generatePassword (identity, end)
    if (beginPassword == endPassword) {
      throw new IllegalArgumentException ("No articulation point")
    }
    val interval = end.toEpochMilli - begin.toEpochMilli
    val middle = begin.plusMillis (interval / 2L)
    val middlePassword = subject.generatePassword (identity, middle)
    if (middlePassword == beginPassword) {
      probeForArticulationPoint (subject, middle, end)
    }
    else if (middlePassword == endPassword) {
      probeForArticulationPoint (subject, begin, middle)
    }
    else {
      throw new IllegalStateException ("More than one articulation point")
    }
  }
}
