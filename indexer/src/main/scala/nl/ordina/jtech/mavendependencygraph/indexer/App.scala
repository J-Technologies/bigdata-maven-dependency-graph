package nl.ordina.jtech.mavendependencygraph.indexer

import java.io.File
import java.util.Set

import scala.collection.Iterable
import scala.collection.JavaConversions.asScalaSet
import org.apache.maven.index.ArtifactInfo
import org.apache.maven.index.MAVEN
import java.io.PrintWriter
import java.lang
import java.lang.Thread

import scala.annotation.tailrec

case class Config(indexLocation: File = new File("."), port: Int = 0, throttle: Int = 0)

object App {

  def main(args: Array[String]): Unit = {

    val parser = new scopt.OptionParser[Config]("indexer") {
      head("indexer", "")
      opt[File]('i', "indexLocation") required () valueName ("<file>") action { (x, c) =>
        c.copy(indexLocation = x)
      } text ("indexLocation is the local path to the maven index")
      opt[Int]('p', "port") required () valueName ("<port>") action { (x, c) =>
        c.copy(port = x)
      } text ("port is the port to send the data to")
      opt[Int]('t', "throttle") optional () valueName ("<throttle>") action { (x, c) =>
        c.copy(throttle = x)
      } text ("throttle is the number of milliseconds to wait between every send")
    }
    // parser.parse returns Option[C]
    parser.parse(args, Config()) map { config =>
      (new App(config)).run
    } getOrElse {
      // arguments are bad, usage message will have been displayed
    }

  }

}

class App(config: Config) {
  val searcher = new Searcher(config.indexLocation.getCanonicalPath)

  def run {
    // update the index
    searcher.update
  
    
    val totalArtifacts: Option[Int] = Server.serve(config.port, (out) =>
      // send the gav's in batches from 'a' to 'z'
      processArtifacts(searcher, out, 'a' to 'z', 0, config.throttle)
    );

    totalArtifacts match {
      case Some(cnt) => println(s"Done. %d artifacts found.".format(cnt))
      case None => println(s"Done. Some error happened.")
    }
    
  }
  
  @tailrec
  private def processArtifacts(searcher: Searcher, out: PrintWriter, chars: Iterable[Char], acc: Int, throttle: Int): Int = {
    if (chars.isEmpty) return acc

    // search for artifacts
    val results: Set[ArtifactInfo] = searcher.search(MAVEN.ARTIFACT_ID, chars.head + "*")
    println(s"found %d artifacts starting with the letter %s".format(results.size, chars.head))

    // flush buffer and send batch
    out.flush
    for (ai <- results) {
      out.println(Array(ai.groupId, ai.artifactId, ai.version, ai.packaging, ai.classifier).mkString(";"))
      throttle match {
        case 0 =>
        case x => Thread.sleep(x)
      }
    }

    return processArtifacts(searcher, out, chars.tail, acc + results.size, throttle)
  }

}
