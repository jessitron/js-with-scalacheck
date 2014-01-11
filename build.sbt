name := "canHazJS"

version := "0.1"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq("org.scalatest" % "scalatest_2.10" % "2.0" % "test",
                "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test",
                "org.scalacheck" %% "scalacheck" % "1.10.1" % "test")
