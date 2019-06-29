package org.cse.dnwiebe.totp

trait TotpFactory {
  def make (timeStepSeconds: Int = 60, passwordLength: Int = 6): Totp
}

class RealTotpFactory extends TotpFactory {
  override def make (timeStepSeconds: Int = 60, passwordLength: Int = 6): Totp = new RealTotp (timeStepSeconds, passwordLength)
}

class ToyTotpFactory extends TotpFactory {
  override def make (timeStepSeconds: Int = 60, passwordLength: Int = 6): Totp = new ToyTotp (timeStepSeconds, passwordLength)
}
