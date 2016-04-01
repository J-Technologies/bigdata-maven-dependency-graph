package nl.ordina.jtech.mavendependencygraph.indexer
  
import java.io.ObjectOutputStream
import java.net.Socket
import java.net.ServerSocket
import java.io.PrintWriter
import scala.util.Try
import scala.util.Failure
import scala.util.Success

object Server {
  
  def serve (port: Int, write: (PrintWriter) => Int) : Option[Int] = {

    println("Ready to serve requests from a Sparkling planet.")
    val listener: ServerSocket = new ServerSocket(port)
    val socket: Try[Socket] = Try(listener.accept())
    val count: Option[Int] = socket match {
      case Success(soc) => {
        val writer: Try[PrintWriter] = Try(new PrintWriter(soc.getOutputStream(), true))
        writer match {
          case Success(wri) => {
            val cnt = Some(write(wri))
            soc.close()
            cnt
          }
          case Failure(f) => println("Failure: " + f); None
        }
      }
      case Failure(f) => println("Failure: " + f); None
    }
    listener.close()
    count
  }
}