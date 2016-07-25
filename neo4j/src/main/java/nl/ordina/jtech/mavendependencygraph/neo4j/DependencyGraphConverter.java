package nl.ordina.jtech.mavendependencygraph.neo4j;

import nl.ordina.jtech.mavendependencygraph.model.ArtifactEdge;
import nl.ordina.jtech.mavendependencygraph.model.ArtifactVertex;
import nl.ordina.jtech.mavendependencygraph.model.DependencyGraph;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.ordina.jtech.mavendependencygraph.neo4j.CypherQuery.cypher;
import static nl.ordina.jtech.mavendependencygraph.neo4j.Neo4JConstants.*;

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

    public static final String MAVEN_NODE_CREATE = MAVEN_ARTIFACT_NODE_TYPE + " { " +
            MAVEN_ARTIFACT_HASH + ": \"%s\", " +
            MAVEN_ARTIFACT_GROUP_ID + ": \"%s\", " +
            MAVEN_ARTIFACT_ARTIFACT_ID + ": \"%s\", " +
            MAVEN_ARTIFACT_CLASSIFIER + ": \"%S\", " +
            MAVEN_ARTIFACT_PACKAGING + ": \"%s\", " +
            MAVEN_ARTIFACT_VERSION + ": \"%s\" }";

    private static final String CYPHER_RELATION_FORMAT = "create unique (_%s) -[:%s]->(_%s)";

    private static final String MATCH_ARTIFACT = "MATCH (n:" + MAVEN_ARTIFACT_NODE_TYPE + " { " + MAVEN_ARTIFACT_HASH + ":\"%s\"}) return n";

    private static final String MATCH_ARTIFACT_2 = "(_%s:" + MAVEN_ARTIFACT_NODE_TYPE + "{"  + MAVEN_ARTIFACT_HASH + ":\"%s\"})";

    private static final String CREATE_ARTIFACT = "create (n:" + MAVEN_NODE_CREATE + ")";

    protected static CypherQuery build(final ArtifactEdge edge, final Map<Integer, ArtifactVertex> vertices) {
        return cypher(String.format(CYPHER_RELATION_FORMAT, vertices.get(edge.getSource()).gav("_"), edge.getScope(), vertices.get(edge.getDestination()).gav("_")));
    }

    protected static Stream<CypherQuery> createEdgeMatches(final ArtifactEdge edge, final Map<Integer, ArtifactVertex> vertices) {
        ArtifactVertex sourceVertex = vertices.get(edge.getSource());
        ArtifactVertex destinationVertex = vertices.get(edge.getDestination());
        CypherQuery source = cypher(String.format(MATCH_ARTIFACT_2, sourceVertex.gav("_"), sourceVertex.getId()));
        CypherQuery destination = cypher(String.format(MATCH_ARTIFACT_2, destinationVertex.gav("_"), destinationVertex.getId()));
        return Arrays.asList(source, destination).stream();
    }

    public static CypherQuery matchVertex(final ArtifactVertex vertex) {
        return cypher(String.format(MATCH_ARTIFACT, vertex.getId()));
    }

    public static CypherQuery createVertex(final ArtifactVertex vertex) {
        return cypher(String.format(CREATE_ARTIFACT, vertex.getId(), vertex.getGroupId(), vertex.getArtifactId(), vertex.getClassifier(), vertex.getPackaging(), vertex.getVersion()));
    }

    public static CypherQuery relations(final DependencyGraph graph) {

        Map<Integer, ArtifactVertex> mappedVertices = graph.getVertices().stream().collect(Collectors.toMap(ArtifactVertex::getId, f -> f));

        CypherQuery vertixesMatch = graph.getEdges().stream().flatMap(artifactEdge -> createEdgeMatches(artifactEdge, mappedVertices)).distinct().collect(CypherQuery.joining(",")).prepend("match");
        CypherQuery edges = graph.getEdges().stream().map(edge -> build(edge, mappedVertices)).collect(CypherQuery.joining("\n"));

        return Arrays.asList(vertixesMatch, edges).stream().collect(CypherQuery.joining("\n"));


    }
}
