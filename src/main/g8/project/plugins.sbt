addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "2.1.0")

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.8.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.0")

addSbtPlugin("com.scalaone" % "xsbt-profile" % "1.2.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.1.1")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")

addSbtPlugin("net.databinder.giter8" % "giter8-scaffold" % "0.6.8")

libraryDependencies ++= Seq(
  "com.spotify" % "docker-client" % "5.0.2"
)
