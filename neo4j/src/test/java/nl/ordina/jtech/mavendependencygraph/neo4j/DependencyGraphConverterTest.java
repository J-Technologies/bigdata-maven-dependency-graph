package nl.ordina.jtech.mavendependencygraph.neo4j;

import nl.ordina.jtech.mavendependencygraph.model.ArtifactVertex;
import nl.ordina.jtech.mavendependencygraph.model.DependencyGraph;
import org.junit.Test;
import org.neo4j.test.SuppressOutput;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.neo4j.server.rest.transactional.ResultDataContent.graph;

/**
 * Class: DependencyGraphConverterTest
 */
public class DependencyGraphConverterTest {

    @Test
    public void testMatch() throws Exception {
        GraphCreator.getGraph().getVertices().stream().map(DependencyGraphConverter::matchVertex).forEach(System.out::println);

    }

    @Test
    public void createNode() throws Exception {
        GraphCreator.getGraph().getVertices().stream().map(DependencyGraphConverter::createVertex).forEach(System.out::println);

    }

    @Test
    public void DumpJson() throws Exception {

        System.out.println("GraphCreator.getGraph().toJson() = " + GraphCreator.getGraph().toJson());
    }

    @Test
    public void relations() throws Exception {
        System.out.println(DependencyGraphConverter.relations(GraphCreator.getGraph()));

        DependencyGraphConverter.relations(GraphCreator.getGraph());

    }

    @Test
    public void name() throws Exception {
        DependencyGraph graph = GraphCreator.getGraph();
        DependencyGraphConverter converter = new DependencyGraphConverter();
        Map<Integer, ArtifactVertex> mappedVertices = graph.getVertices().stream().collect(Collectors.toMap(ArtifactVertex::getId, f -> f));

        Stream<CypherQuery> cypherQueryStream = graph.getEdges().stream().flatMap(artifactEdge -> converter.createEdgeMatches(artifactEdge, mappedVertices));
        Stream<CypherQuery> distinct = cypherQueryStream.distinct();
        List<CypherQuery> collect = distinct.collect(Collectors.toList());
        assertThat(collect.size(), is(3));
    }
}