import scalariform.formatter.preferences._
import bintray.Keys._

name := "play-json-support"

version := "0.1.0"

organization := "octalmind"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.11.6", "2.10.5")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0-RC1",
  "com.typesafe.akka" %% "akka-http-scala-experimental" % "1.0-RC1",
  "com.typesafe.play" %% "play-json" % "2.3.8",
  "com.typesafe.akka" %% "akka-http-testkit-scala-experimental" % "1.0-RC1" % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

scalacOptions ++= Seq(
  "-language:implicitConversions",
  "-unchecked",
  "-feature",
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Xfatal-warnings",
  "-Xlint",
  "-Xfuture",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)

libraryDependencies ++= {
  if (scalaBinaryVersion.value startsWith "2.10")
    Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full))
  else Nil
}

scalariformSettings
ScalariformKeys.preferences := FormattingPreferences()
    .setPreference(RewriteArrowSymbols, true)
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)

// publishMavenStyle := false
bintrayPublishSettings
repository in bintray := "maven"
bintrayOrganization in bintray := None