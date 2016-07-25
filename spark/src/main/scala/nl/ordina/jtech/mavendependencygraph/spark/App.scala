package nl.ordina.jtech.mavendependencygraph.spark

import nl.ordina.jtech.maven.analyzer.aether.ArtifactResolver
import nl.ordina.jtech.mavendependencygraph.model.DependencyGraph
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{Logging, SparkConf}
import org.eclipse.aether.artifact.DefaultArtifact

import scala.util.{Success, Try}
import scalaj.http.{Http, HttpResponse}

object App extends Logging {

  def main(args: Array[String]): Unit = {
    val (host, port, url) = parseInputArgs(args)
    val conf = new SparkConf().setMaster("spark://jtechbd-spark-m:7077").setAppName("maven-streaming")
    val ssc = new StreamingContext(conf, Seconds(30))

    val dstream = ssc.socketTextStream(host, port)
    dstream.map { record =>
      logInfo("Record: " + record)
      MavenEntry(record)
    }.map(resolveSubGraph)
      .filter(_.isDefined)
      .map(_.get)
      .foreachRDD(graphRDD => sendGraphToNeo(graphRDD, url))

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }

  def resolveSubGraph(mavenEntry: MavenEntry): Option[DependencyGraph] = {
    val resolver: ArtifactResolver = new ArtifactResolver()
    val artifactCoordinate = mavenEntry.groupId + ":" + mavenEntry.artifactId + ":" + mavenEntry.version
    logInfo("ArtificactCoordinate: " + artifactCoordinate)
    val dependencyGraph = resolver.resolveToDependencyGraphv2(new DefaultArtifact(artifactCoordinate))
    dependencyGraph match {
      case null => None
      case _ => Some(dependencyGraph)
    }
  }

  def sendGraphToNeo(graphs: RDD[DependencyGraph], url: String): Unit = {
    graphs.foreach(graph => {
      val json: String = graph.toJson
      logInfo("json: " + json)
      Try {
        val response: HttpResponse[String] = Http(url).header("content-type", "application/json").postData(json).asString
        logInfo("Response: " + response.code)
      } recoverWith {
        case e =>
          logError(s"NonFatal exception occured while posting json: $json", e)
          Success()
      }
    })
  }

  def parseInputArgs(args: Array[String]): (String, Int, String) = {
    if (args.length != 3) {
      println(
        """
          |Please provide the following arguments:
          | args0: host
          | args1: port
          | args2: neo endpoint url
        """.stripMargin)
    }
    val host = args(0)
    val port = args(1).toInt
    val url = args(2)
    (host, port, url)
  }
}