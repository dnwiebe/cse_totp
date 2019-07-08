package org.cse.dnwiebe.totp

import java.time.Instant

import scala.util.Random

class ToyTotp (timeStepSeconds: Int = 60, passwordLength: Int = 6) extends TotpBase (passwordLength) with Totp {
  private val modulus = Math.pow (10.0, passwordLength).asInstanceOf [Long]

  override def generateIdentity (): Identity = ToyIdentity ()

  override def generatePassword (identity: Identity, timestamp: Instant): String = {
    val longIdentity = identity.asInstanceOf [ToyIdentity].id
    val truncatedTimestamp = timestamp.toEpochMilli / (timeStepSeconds * 1000)
    val numericPassword = Math.abs (truncatedTimestamp * longIdentity) % modulus
    formatPassword (numericPassword)
  }
}

private object ToyIdentity {
  private val KEY_GENERATOR = new Random ()

  def apply (): ToyIdentity = {
    ToyIdentity (KEY_GENERATOR.nextLong ())
  }
}

private case class ToyIdentity (id: Long) extends Identity
