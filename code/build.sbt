name := "Logic Programming"

scalaVersion := "2.13.0"

// addCompilerPlugin("io.tryp" % "splain" % "0.4.1" cross CrossVersion.patch)

// scalacOptions += "-Xlog-implicits"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.25"

initialCommands in console := """
  |import ncreep._
  |import HList._""".stripMargin
