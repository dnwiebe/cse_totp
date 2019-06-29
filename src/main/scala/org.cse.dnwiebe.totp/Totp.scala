package org.cse.dnwiebe.totp

import java.security.Key
import java.time.Instant
import java.util.Date
import java.util.concurrent.TimeUnit

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator
import javax.crypto.KeyGenerator

trait Identity

class Totp (timeStepSeconds: Int = 60, passwordLength: Int = 6) {
  private val totpg = new TimeBasedOneTimePasswordGenerator (timeStepSeconds, TimeUnit.SECONDS, passwordLength)
  private val formatString = String.format ("%%0%dd", passwordLength);

  def generateIdentity(): Identity = KeyIdentity()

  def generatePassword(identity: Identity, timestamp: Instant): String = {
    val keyIdentity = identity.asInstanceOf[KeyIdentity]
    val key = keyIdentity.key
    val numericPassword = totpg.generateOneTimePassword(key, new Date (timestamp.toEpochMilli))
    String.format (formatString, numericPassword)
  }
}

private object KeyIdentity {
  private val KEY_GENERATOR = KeyGenerator.getInstance("HmacSHA256")

  def apply (): KeyIdentity = {
    KeyIdentity(KEY_GENERATOR.generateKey())
  }
}

private case class KeyIdentity (key: Key) extends Identity
