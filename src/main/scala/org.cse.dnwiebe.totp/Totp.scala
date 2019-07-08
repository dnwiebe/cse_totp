package org.cse.dnwiebe.totp

import java.time.Instant

trait Identity

trait Totp {
  def generateIdentity (): Identity

  def generatePassword (identity: Identity, timestamp: Instant): String
}

abstract class TotpBase (val passwordLength: Int) {
  private val formatString = String.format ("%%0%dd", passwordLength)

  protected def formatPassword (numericPassword: Long): String = String.format (formatString, numericPassword)
}
