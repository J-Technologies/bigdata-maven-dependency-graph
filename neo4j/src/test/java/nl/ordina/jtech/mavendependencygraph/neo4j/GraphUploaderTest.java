package nl.ordina.jtech.mavendependencygraph.neo4j;

import com.google.gson.Gson;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.Test;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;
import org.neo4j.test.server.HTTP;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Class: GraphUploaderTest
 */
public class GraphUploaderTest {


    public static final String DEPENDENCY_GRAPH = "{\"vertices\":[{\"id\":1178203917,\"groupId\":\"javax.activation\",\"artifactId\":\"activation\",\"version\":\"1.1\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":1197076530,\"groupId\":\"com.sun.mail\",\"artifactId\":\"javax.mail\",\"version\":\"1.5.0\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":1778276833,\"groupId\":\"javax\",\"artifactId\":\"javaee-api\",\"version\":\"7.0\",\"classifier\":\"\",\"packaging\":\"Jar\"}],\"edges\":[{\"source\":1778276833,\"destination\":1197076530,\"scope\":\"Provided\"},{\"source\":1197076530,\"destination\":1178203917,\"scope\":\"Provided\"}]}";
    public static final String CONTEXT_ROOT = "/myExtension";
    public static final String POST_ACTION = CONTEXT_ROOT + "/dependency/graph";
    public static final String GET_ACTION = CONTEXT_ROOT + "/dependency";

    private static final Gson GSON = new Gson();

    @Test
    public void testMyExtension() throws Exception {
        // Given
        try (ServerControls server = TestServerBuilders.newInProcessBuilder()
                .withExtension(CONTEXT_ROOT, GraphUploader.class)
                .newServer()) {


            // When
            HTTP.Response post = HTTP.POST(server.httpURI().resolve(POST_ACTION).toString(), HTTP.RawPayload.quotedJson(DEPENDENCY_GRAPH));
            assertThat(post.status(), is(HttpResponseStatus.OK.code()));
            assertThat(post.rawContent(), is("Submitted"));

            boolean consumed = false;
            do {
                HTTP.Response response = HTTP.GET(server.httpURI().resolve(GET_ACTION).toString());
                UploaderStatus uploaderStatus = GSON.fromJson(response.rawContent(), UploaderStatus.class);
                if (uploaderStatus.getExecutedCount() == 1) {
                    Thread.sleep(100);
                    consumed = true;
                }
            } while (!consumed);

            Result result = server.graph().execute("MATCH (n) return n");
            assertTrue(result.resultAsString(),result.resultAsString().contains("javaee-api"));
        }
    }
}