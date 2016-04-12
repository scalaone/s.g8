organization := "$organization$"

name := "$name$"

version := "$version$"

scalaVersion := "$scala_version$"

scalacOptions ++= Seq("-deprecation", "-feature")

resolvers ++= Seq("softprops-maven" at "http://dl.bintray.com/content/softprops/maven",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "spray repo" at "http://repo.spray.io")

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatra" %% "scalatra" % "$scalatra_version$",
  "org.scalatra" %% "scalatra-swagger" % "$scalatra_version$",
  "org.scalatra" %% "scalatra-json" % "$scalatra_version$",
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "com.typesafe.slick" %% "slick" % "$slick_version$",
  "com.typesafe.slick" %% "slick-hikaricp" % "$slick_version$",
  "com.typesafe.slick" %% "slick-extensions" % "3.1.0",
  "me.lessis" %% "courier" % "0.1.3",
  "org.quartz-scheduler" % "quartz" % "2.2.2",
  "commons-net" % "commons-net" % "3.4",
  "io.spray" %% "spray-client" % "1.3.3",
  "com.typesafe.akka" %% "akka-actor" % "2.3.14",
  "com.google.guava" % "guava" % "19.0",
  "com.zaxxer" % "HikariCP-java6" % "2.3.13",
  "mysql" % "mysql-connector-java" % "5.1.38",
  "joda-time" % "joda-time" % "2.9.2",
  "com.typesafe.slick" %% "slick-codegen" % "$slick_version$",
  "org.scalatra" %% "scalatra-specs2" % "$scalatra_version$" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.2" % "runtime",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
)

enablePlugins(JettyPlugin)
containerPort := 9090

enablePlugins(SbtTwirl)

giter8.ScaffoldPlugin.scaffoldSettings

javaOptions in Jetty ++= Seq(
  "-Xdebug",
  "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
)

lazy val slick = TaskKey[Seq[File]]("slick")
lazy val slickCodeGenTask = (sourceDirectory, fullClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val outputDir = (dir / "main" / "slick").getPath
  toError(r.run("$package$.utils.CodeGen", cp.files, Array(outputDir), s.log))
  val name = outputDir + "Tables.scala"
  Seq(file(name))
}

slick <<= slickCodeGenTask