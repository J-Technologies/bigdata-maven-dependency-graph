package nl.ordina.jtech.mavendependencygraph.spark

case class MavenEntry(groupId: String, artifactId: String, version: String, packaging: String, classifier: String)

object MavenEntry {
  def apply(s: String): MavenEntry = {
    val splits = s.split(";")
    val groupId = splits(0)
    val artifactId = splits(1)
    val version = splits(2)
    val packaging = splits(3)
    val classifier = splits(4)
    MavenEntry(groupId, artifactId, version, packaging, classifier)
  }
}