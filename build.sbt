ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "hdfs_example"
  )

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "3.2.1"
libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.5", "org.slf4j" % "slf4j-simple" % "1.7.5")
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "3.2.0"
//libraryDependencies += "log4j" % "log4j" % "1.2.16"
libraryDependencies += "com.typesafe" % "config" % "1.4.2"