name := "TOTP"

version := "0.1"

scalaVersion := "2.13.0"

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test
libraryDependencies += "com.github.jchambers" % "java-otp" % "java-otp-0.1.0"
