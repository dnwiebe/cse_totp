package org.cse.dnwiebe.totp

import java.time.Instant

trait Identity

trait Totp {
  def generateIdentity (): Identity

  def generatePassword (identity: Identity, timestamp: Instant): String
}
