package org.cse.dnwiebe.totp

import java.security.Key
import java.time.Instant
import java.util.Date
import java.util.concurrent.TimeUnit

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator
import javax.crypto.KeyGenerator

class RealTotp (timeStepSeconds: Int = 60, passwordLength: Int = 6) extends TotpBase (passwordLength) with Totp {
  private val totpg = new TimeBasedOneTimePasswordGenerator (timeStepSeconds, TimeUnit.SECONDS, passwordLength)

  override def generateIdentity (): Identity = KeyIdentity ()

  override def generatePassword (identity: Identity, timestamp: Instant): String = {
    val keyIdentity = identity.asInstanceOf [KeyIdentity]
    val key = keyIdentity.key
    val numericPassword = totpg.generateOneTimePassword (key, new Date (timestamp.toEpochMilli))
    formatPassword (numericPassword)
  }
}

private object KeyIdentity {
  private val KEY_GENERATOR = KeyGenerator.getInstance ("HmacSHA256")

  def apply (): KeyIdentity = {
    KeyIdentity (KEY_GENERATOR.generateKey ())
  }
}

private case class KeyIdentity (key: Key) extends Identity
