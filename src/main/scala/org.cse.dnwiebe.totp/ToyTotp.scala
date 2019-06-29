package org.cse.dnwiebe.totp
import java.time.Instant

import scala.util.Random

class ToyTotp (timeStepSeconds: Int = 60, passwordLength: Int = 6) extends Totp {
  private val modulus = Math.pow(10.0, passwordLength).asInstanceOf[Long]
  private val formatString = String.format ("%%0%dd", passwordLength)

  override def generateIdentity (): Identity = ToyIdentity()

  override def generatePassword (identity: Identity, timestamp: Instant): String = {
    val longIdentity = identity.asInstanceOf[ToyIdentity].id
    val truncatedTimestamp = timestamp.toEpochMilli / (timeStepSeconds * 1000)
    val product = Math.abs(truncatedTimestamp * longIdentity)
    val numericPassword = product % modulus
    String.format (formatString, numericPassword)
  }
}

private object ToyIdentity {
  private val KEY_GENERATOR = new Random()

  def apply (): ToyIdentity = {
    ToyIdentity (KEY_GENERATOR.nextLong())
  }
}

private case class ToyIdentity (id: Long) extends Identity
