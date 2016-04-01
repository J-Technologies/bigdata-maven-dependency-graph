package nl.ordina.jtech.mavendependencygraph.indexer

import java.io.File
import java.util.ArrayList
import java.util.Date
import java.util.Set

import org.apache.lucene.search.BooleanClause.Occur
import org.apache.lucene.search.BooleanQuery
import org.apache.maven.index.ArtifactInfo
import org.apache.maven.index.Field
import org.apache.maven.index.FlatSearchRequest
import org.apache.maven.index.Indexer
import org.apache.maven.index.MAVEN
import org.apache.maven.index.context.IndexCreator
import org.apache.maven.index.expr.SourcedSearchExpression
import org.apache.maven.index.updater.IndexUpdateRequest
import org.apache.maven.index.updater.IndexUpdateResult
import org.apache.maven.index.updater.IndexUpdater
import org.apache.maven.index.updater.WagonHelper
import org.apache.maven.wagon.Wagon
import org.apache.maven.wagon.events.TransferEvent
import org.apache.maven.wagon.events.TransferListener
import org.apache.maven.wagon.observers.AbstractTransferListener
import org.codehaus.plexus.DefaultContainerConfiguration
import org.codehaus.plexus.DefaultPlexusContainer
import org.codehaus.plexus.PlexusConstants

class Searcher(indexLocation: String) {

  val MAVEN_REPOSITORY_URL = "http://repo1.maven.org/maven2"
  
  val config = new DefaultContainerConfiguration()
  config.setClassPathScanning(PlexusConstants.SCANNING_INDEX)
  val plexusContainer = new DefaultPlexusContainer(config)
  val indexer = plexusContainer.lookup(classOf[Indexer])

  val indexers = new ArrayList[IndexCreator]()
  indexers.add(plexusContainer.lookup(classOf[IndexCreator], "min"))

  val centralLocalCache = new File(indexLocation, "central-cache")
  val centralIndexDir = new File(indexLocation, "central-index")

  val centralContext = indexer.createIndexingContext("central-context", "central",
    centralLocalCache, centralIndexDir, MAVEN_REPOSITORY_URL, null, true, true, indexers)

  def update() {

    val indexUpdater = plexusContainer.lookup(classOf[IndexUpdater])
    val httpWagon = plexusContainer.lookup(classOf[Wagon], "http")

    val listener: TransferListener = new AbstractTransferListener() {
      override def transferStarted(transferEvent: TransferEvent) {
        println("  Downloading " + transferEvent.getResource().getName())
      }

      override def transferProgress(transferEvent: TransferEvent, buffer: Array[Byte], length: Int) {}

      override def transferCompleted(transferEvent: TransferEvent) {
        println(" - Done");
      }
    }
    val resourceFetcher = new WagonHelper.WagonFetcher(httpWagon, listener, null, null)

    val centralContextCurrentTimestamp: Date = centralContext.getTimestamp()
    val updateRequest = new IndexUpdateRequest(centralContext, resourceFetcher)
    val updateResult: IndexUpdateResult = indexUpdater.fetchAndUpdateIndex(updateRequest)

    if (updateResult.isFullUpdate()) {
      println("Full update happened!")
    } else if (updateResult.getTimestamp().equals(centralContextCurrentTimestamp)) {
      println("No update needed, index is up to date!")
    } else {
      println(s"Incremental update happened, change covered %s - %s period."
        .format(centralContextCurrentTimestamp, updateResult.getTimestamp()))
    }

  }

  def search(field: Field, expr: String) : Set[ArtifactInfo] = {
    val bq = new BooleanQuery
    bq.add(constructBooleanOrQuery(MAVEN.PACKAGING, Seq("jar", "war", "ear")), Occur.MUST)
    bq.add(indexer.constructQuery(field, new SourcedSearchExpression(expr)), Occur.MUST)
    
    val response = indexer.searchFlat(new FlatSearchRequest(bq, centralContext))
    response.getResults
  }

  def constructBooleanOrQuery(field: Field, packageTypes: Seq[String]) : BooleanQuery = {
    val packagingQuery = new BooleanQuery
    for (pt <- packageTypes) {
      packagingQuery.add(indexer.constructQuery(field, new SourcedSearchExpression(pt)), Occur.SHOULD)
    }
    packagingQuery
  }
}