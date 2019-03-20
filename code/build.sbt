name := "Logic Programming"

scalaVersion := "2.12.8"

//addCompilerPlugin("io.tryp" % "splain" % "0.4.0" cross CrossVersion.patch)

// scalacOptions += "-Xlog-implicits"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.21"

initialCommands in console := """
  |import ncreep._
  |import HList._""".stripMargin