./spark-1.5.1-bin-hadoop2.6/bin/spark-submit --class nl.ordina.jtech.mavendependencygraph.spark.App --master spark://jtechbd-spark-m:7077 --deploy-mode client spark-1.0.0-SNAPSHOT-driver.jar jtechbd-spark-m 9999 http://jtechbd-neo4j:7474/maven/dependency/graph