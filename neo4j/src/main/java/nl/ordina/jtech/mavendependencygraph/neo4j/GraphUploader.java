package nl.ordina.jtech.mavendependencygraph.neo4j;

import com.google.gson.Gson;
import nl.ordina.jtech.mavendependencygraph.model.ArtifactVertex;
import nl.ordina.jtech.mavendependencygraph.model.DependencyGraph;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Class: GraphUploader
 */
@Path("/dependency")
public class GraphUploader {
    private static final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(5000);
    private static final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 1, TimeUnit.HOURS, queue, new ThreadPoolExecutor.CallerRunsPolicy());
    private static final Gson GSON = new Gson();
    private static long executeCount = 0;
    private final GraphDatabaseService database;
    private static long skippedCount;
    private static long errorCount;

    public GraphUploader(@Context GraphDatabaseService database) throws IOException, TimeoutException {
        this.database = database;
        //createIndices();
    }

    private void createIndices() {
        try {
            IndexDefinition indexDefinition;
            try (Transaction tx = database.beginTx()) {
                Schema schema = database.schema();
                indexDefinition = schema.indexFor(DynamicLabel.label(Neo4JConstants.MAVEN_ARTIFACT_NODE_TYPE))
                        .on(Neo4JConstants.MAVEN_ARTIFACT_HASH)
                        .create();
                tx.success();
            }
        } catch (ConstraintViolationException e) {
            System.err.println("Warning index already there");
        }
    }

    private void createVertexWhenNotExists(final ArtifactVertex vertex) {
        Result execute = DependencyGraphConverter.matchVertex(vertex).execute(database);
        if (!execute.hasNext()) {
            DependencyGraphConverter.createVertex(vertex).execute(database);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String status() {
        return UploaderStatus.build(executeCount, skippedCount, errorCount, (ThreadPoolExecutor) executorService).toJson();
    }


    @POST
    @Path("/graph")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response uploadSubGraph(final String graphJson) throws IOException, InterruptedException {
        System.out.println("Graph => " + graphJson);
        executorService.submit(() -> {
            final DependencyGraph graph = GSON.fromJson(graphJson, DependencyGraph.class);
            if (hasData(graph)) {
                try (final Transaction transaction = database.beginTx()) {
                    graph.getVertices().stream().forEach(this::createVertexWhenNotExists);
                    CypherQuery query = DependencyGraphConverter.relations(graph);
                    query.execute(database);
                    transaction.success();
                    executeCount++;
                } catch (Exception e) {
                    errorCount++;
                    e.printStackTrace();
                }
            } else {
                skippedCount++;
            }
        });

        return Response.ok().entity("Submitted").build();

    }

    private boolean hasData(DependencyGraph graph) {
        return !(graph.getEdges().isEmpty() || graph.getVertices().isEmpty());
    }
}
