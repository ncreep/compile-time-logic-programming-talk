name := "Logic Programming"

scalaVersion := "2.12.4"

// addCompilerPlugin("io.tryp" % "splain" % "0.2.7" cross CrossVersion.patch)

// scalacOptions += "-Xlog-implicits"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.7"

initialCommands in console := """
  |import ncreep._
  |import HList._""".stripMargin