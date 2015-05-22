import sbt.Keys._

name := "wysiwygtopdf"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  javaEbean,
  cache,
  "it.innove" % "play2-pdf" % "1.1.3",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.1",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  filters
)

resolvers ++= Seq(
    "Apache" at "http://repo1.maven.org/maven2/",
    "jBCrypt Repository" at "http://repo1.maven.org/maven2/org/",
    "Sonatype OSS Snasphots" at "http://oss.sonatype.org/content/repositories/snapshots"
)

lazy val root = (project in file(".")).enablePlugins(play.PlayJava)
