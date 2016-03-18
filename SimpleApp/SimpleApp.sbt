name := "SimpleApp"

version:= "1.0"

scalaVersion := "2.11.2"

libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % "1.6.0" 

libraryDependencies += "org.apache.spark" % "spark-streaming-mqtt_2.10" % "1.6.0" 

resolvers += "MQTT repository" at "https://repo.eclipse.org/content/repositories/paho-releases/"

mergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) => MergeStrategy.discard
      case _ => MergeStrategy.discard
    }
  case _ => MergeStrategy.first
}


