package nl.ordina.jtech.mavendependencygraph.neo4j;

import nl.ordina.jtech.mavendependencygraph.model.ArtifactEdge;
import nl.ordina.jtech.mavendependencygraph.model.ArtifactVertex;
import nl.ordina.jtech.mavendependencygraph.model.DependencyGraph;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.ordina.jtech.mavendependencygraph.neo4j.Neo4JConstants.CYPHER_MERGE;
import static nl.ordina.jtech.mavendependencygraph.neo4j.Neo4JConstants.MAVEN_ARTIFACT_ARTIFACT_ID;
import static nl.ordina.jtech.mavendependencygraph.neo4j.Neo4JConstants.MAVEN_ARTIFACT_CLASSIFIER;
import static nl.ordina.jtech.mavendependencygraph.neo4j.Neo4JConstants.MAVEN_ARTIFACT_GROUP_ID;
import static nl.ordina.jtech.mavendependencygraph.neo4j.Neo4JConstants.MAVEN_ARTIFACT_NODE_TYPE;
import static nl.ordina.jtech.mavendependencygraph.neo4j.Neo4JConstants.MAVEN_ARTIFACT_PACKAGING;
import static nl.ordina.jtech.mavendependencygraph.neo4j.Neo4JConstants.MAVEN_ARTIFACT_VERSION;

/**
 * Class: DependencyGraphConverter
 * Convert a Graph to a Cypher form:
 * <p/>
 * Relations are converted to:
 * <pre>
 *     create unique (n) -[:DEPENDS_ON]->( y: MavenEntry {groupId:"xza.bb",
 *            artifactId: "bar",
 *            version: "1.0.1"})
 *            return n, y
 * </pre>
 * <p/>
 * Artifacts are converted to:
 * <pre>
 *  merge (n: MavenEntry {
 *          groupId:"zaa.bb",
 *          artifactId: "bar",
 *          version: "1.0.1"})
 * </pre>
 * <p/>
 * <p/>
 * Resulting in a complete query in the form of:
 * <pre>
 *  merge (n: MavenEntry {
 *      groupId:"zaa.bb",
 *      artifactId: "bar",
 *      version: "1.0.1"})
 *  merge (y: MavenEntry {
 *      groupId:"zaa.cc",
 *      artifactId: "foo",
 *      version: "1.0.1"})
 *  create unique (n) -[:DEPENDS_ON]->(y)
 * </pre>
 */
public class DependencyGraphConverter {

    public static final String NODE_FORMAT = CYPHER_MERGE + " (%s: " +
            MAVEN_ARTIFACT_NODE_TYPE + " { " +
            MAVEN_ARTIFACT_GROUP_ID + ": \"%s\", " +
            MAVEN_ARTIFACT_ARTIFACT_ID + ": \"%s\", " +
            MAVEN_ARTIFACT_CLASSIFIER + ": \"%S\", " +
            MAVEN_ARTIFACT_PACKAGING + ": \"%s\", " +
            MAVEN_ARTIFACT_VERSION + ": \"%s\" " +
            "})";
    private static final String CYPHER_RELATION_FORMAT = "create unique (%s) -[:%s]->(%s)";

    public static String inCypher(final DependencyGraph graph) {
        Map<Integer, ArtifactVertex> mappedVertices = graph.getVertices().stream().collect(Collectors.toMap(ArtifactVertex::getId, f -> f));

        Stream<String> cypherVertexStream = graph.getVertices().stream().map(DependencyGraphConverter::mergeNode);
        Stream<String> cypherRelationStream = graph.getEdges().stream().map(artifactEdge -> build(artifactEdge, mappedVertices));

        return Stream.concat(cypherVertexStream, cypherRelationStream).collect(Collectors.joining("\n"));
    }

    private static String mergeNode(final ArtifactVertex vertex) {
        return String.format(NODE_FORMAT, vertex.gav("_"), vertex.getGroupId(), vertex.getArtifactId(), vertex.getClassifier(), vertex.getPackaging(), vertex.getVersion());
    }

    private static String build(final ArtifactEdge edge, final Map<Integer, ArtifactVertex> vertices) {

        return String.format(CYPHER_RELATION_FORMAT, vertices.get(edge.getSource()).gav("_"), edge.getScope(), vertices.get(edge.getDestination()).gav("_"));
    }
}
