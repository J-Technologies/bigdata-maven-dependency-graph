package nl.ordina.jtech.mavendependencygraph.neo4j;

import nl.ordina.jtech.mavendependencygraph.model.ArtifactEdge;
import nl.ordina.jtech.mavendependencygraph.model.ArtifactVertex;
import nl.ordina.jtech.mavendependencygraph.model.DependencyGraph;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static final String MAVEN_NODE = MAVEN_ARTIFACT_NODE_TYPE + " { " +
            MAVEN_ARTIFACT_HASH + ": \"%s\", " +
            MAVEN_ARTIFACT_GROUP_ID + ": \"%s\", " +
            MAVEN_ARTIFACT_ARTIFACT_ID + ": \"%s\", " +
            MAVEN_ARTIFACT_CLASSIFIER + ": \"%S\", " +
            MAVEN_ARTIFACT_PACKAGING + ": \"%s\", " +
            MAVEN_ARTIFACT_VERSION + ": \"%s\" ";
    public static final String MAVEN_NODE_CREATE = MAVEN_ARTIFACT_NODE_TYPE + " { " +
            MAVEN_ARTIFACT_HASH + ": \"%s\", " +
            MAVEN_ARTIFACT_GROUP_ID + ": \"%s\", " +
            MAVEN_ARTIFACT_ARTIFACT_ID + ": \"%s\", " +
            MAVEN_ARTIFACT_CLASSIFIER + ": \"%S\", " +
            MAVEN_ARTIFACT_PACKAGING + ": \"%s\", " +
            MAVEN_ARTIFACT_VERSION + ": \"%s\" }";
    public static final String NODE_FORMAT = CYPHER_MERGE + " (%s: " + MAVEN_NODE + "})";
    private static final String CYPHER_RELATION_FORMAT = "create (%s) -[:%s]->(%s)";

    public static final String MATCH_ARTIFACT = "MATCH (n:" + MAVEN_ARTIFACT_NODE_TYPE + " { " + MAVEN_ARTIFACT_HASH + ":\"%s\"}) return n";

    private static final String CYPHER_RELATION_FORMAT_2 =
            "(_%s:" + MAVEN_ARTIFACT_NODE_TYPE + "{"  + MAVEN_ARTIFACT_HASH + ":\"%s\"}), " +
            "(_%s:" + MAVEN_ARTIFACT_NODE_TYPE + "{"  + MAVEN_ARTIFACT_HASH + ":\"%s\"})";
    public static final String CREATE_RELATION =
            "create (_%s)-[:%s]->(_%s)";

    public static final String CREATE_ARTIFACT = "create (n:" + MAVEN_NODE_CREATE + ")";

    public static String inCypher(final DependencyGraph graph) {
        Map<Integer, ArtifactVertex> mappedVertices = graph.getVertices().stream().collect(Collectors.toMap(ArtifactVertex::getId, f -> f));

        Stream<String> cypherVertexStream = graph.getVertices().stream().map(DependencyGraphConverter::mergeNode);
        Stream<String> cypherRelationStream = graph.getEdges().stream().map(artifactEdge -> build(artifactEdge, mappedVertices));

        return Stream.concat(cypherVertexStream, cypherRelationStream).collect(Collectors.joining("\n"));
    }

    private static String mergeNode(final ArtifactVertex vertex) {
        return String.format(NODE_FORMAT, vertex.gav("_"), vertex.getId(), vertex.getGroupId(), vertex.getArtifactId(), vertex.getClassifier(), vertex.getPackaging(), vertex.getVersion());
    }

    private static String build(final ArtifactEdge edge, final Map<Integer, ArtifactVertex> vertices) {
        return String.format(CYPHER_RELATION_FORMAT, "_"  + vertices.get(edge.getSource()).getId(), edge.getScope(), "_"  + vertices.get(edge.getDestination()).getId());
    }

    private static String createVertixMatch(final ArtifactEdge edge, final Map<Integer, ArtifactVertex> vertices) {
        ArtifactVertex sourceVertex = vertices.get(edge.getSource());
        ArtifactVertex destinationEdge = vertices.get(edge.getDestination());
        return String.format(CYPHER_RELATION_FORMAT_2,
                sourceVertex.hashCode(),
                sourceVertex.getId(),
                destinationEdge.hashCode(),
                destinationEdge.getId(),
                sourceVertex.hashCode(),
                edge.getScope(),
                destinationEdge.hashCode()
                );
    }

    public static String matchVertex(final ArtifactVertex vertex) {
        return String.format(MATCH_ARTIFACT, vertex.getId());
    }

    public static String createVertex(final ArtifactVertex vertex) {
        return String.format(CREATE_ARTIFACT, vertex.getId(), vertex.getGroupId(), vertex.getArtifactId(), vertex.getClassifier(), vertex.getPackaging(), vertex.getVersion());
    }

    public static String relations(final DependencyGraph graph) {

        Map<Integer, ArtifactVertex> mappedVertices = graph.getVertices().stream().collect(Collectors.toMap(ArtifactVertex::getId, f -> f));
        Stream<String> vertixesMatch = graph.getEdges().stream().map(artifactEdge -> createVertixMatch(artifactEdge, mappedVertices));

        String matches = "match " + vertixesMatch.collect(Collectors.joining(","));

        String edges = graph.getEdges().stream().map(edge -> build(edge, mappedVertices)).collect(Collectors.joining("\n"));
        return matches + "\n" + edges;


    }
}
