enablePlugins(PackPlugin)

organization := "com.example"
name := "loadtest"

version := "1.0"

scalaVersion := "2.12.10"

val gatlingVersion = "3.4.2"

resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "io.gatling" % "gatling-app" % gatlingVersion,
  "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion exclude("io.gatling", "gatling-recorder"),
  "org.rogach" %% "scallop" % "4.0.2",
  "org.apache.logging.log4j" % "log4j-core" % "2.14.0",
  "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.10.2"
)

packMain := Map("loadtest" -> "com.example.load.LoadTestRunner")
packJvmOpts := Map("loadtest" -> Seq(
  """-Xmx1G \
    |-XX:+HeapDumpOnOutOfMemoryError \
    |-XX:+UseG1GC \
    |-XX:+ParallelRefProcEnabled \
    |-XX:MaxInlineLevel=20 \
    |-XX:MaxTrivialSize=12 \
    |-XX:-UseBiasedLocking \
    |-Dio.netty.tryReflectionSetAccessible=false \
    |--illegal-access=permit \
    |--add-modules java.se \
    |--add-exports java.base/jdk.internal.ref=ALL-UNNAMED \
    |--add-exports java.base/jdk.internal.misc=ALL-UNNAMED \
    |--add-opens java.base/java.lang=ALL-UNNAMED \
    |--add-opens java.base/java.nio=ALL-UNNAMED \
    |--add-opens java.base/sun.nio.ch=ALL-UNNAMED \
    |--add-opens java.management/sun.management=ALL-UNNAMED \
    |--add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED""".stripMargin))

Compile / run / fork := true
javaOptions ++= {
  val props = sys.props.toList
  props.map { case (key, value) =>
    s"-D$key=$value"
  }
}
