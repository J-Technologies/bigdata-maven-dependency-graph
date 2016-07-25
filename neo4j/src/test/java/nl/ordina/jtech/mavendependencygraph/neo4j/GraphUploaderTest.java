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


   // public static final String DEPENDENCY_GRAPH = "{\"vertices\":[{\"id\":1178203917,\"groupId\":\"javax.activation\",\"artifactId\":\"activation\",\"version\":\"1.1\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":1197076530,\"groupId\":\"com.sun.mail\",\"artifactId\":\"javax.mail\",\"version\":\"1.5.0\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":1778276833,\"groupId\":\"javax\",\"artifactId\":\"javaee-api\",\"version\":\"7.0\",\"classifier\":\"\",\"packaging\":\"Jar\"}],\"edges\":[{\"source\":1778276833,\"destination\":1197076530,\"scope\":\"Provided\"},{\"source\":1197076530,\"destination\":1178203917,\"scope\":\"Provided\"}]}";
   public static final String DEPENDENCY_GRAPH =                                "{\"vertices\":[{\"id\":1852587263,\"groupId\":\"javax\",\"artifactId\":\"javaee-api\",\"version\":\"7.0\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":1271386960,\"groupId\":\"com.sun.mail\",\"artifactId\":\"javax.mail\",\"version\":\"1.5.0\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":1252514347,\"groupId\":\"javax.activation\",\"artifactId\":\"activation\",\"version\":\"1.1\",\"classifier\":\"\",\"packaging\":\"Jar\"}],\"edges\":[{\"source\":1852587263,\"destination\":1271386960,\"scope\":\"Provided\"},{\"source\":1271386960,\"destination\":1252514347,\"scope\":\"Compile\"}]}";
    public static final String EMPTY_DEPENDENCY_GRAPH =                                "{\"vertices\":[],\"edges\":[]}";
    public static final String CONTEXT_ROOT = "/myExtension";
    public static final String POST_ACTION = CONTEXT_ROOT + "/dependency/graph";
    public static final String GET_ACTION = CONTEXT_ROOT + "/dependency";
    public static final String DATA_1 = "{\"vertices\":[{\"id\":1631527839,\"groupId\":\"org.apache.directory.api\",\"artifactId\":\"api-ldap-client-api\",\"version\":\"1.0.0-M17\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-423525824,\"groupId\":\"org.apache.directory.api\",\"artifactId\":\"api-asn1-ber\",\"version\":\"1.0.0-M17\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":2039784661,\"groupId\":\"org.apache.directory.api\",\"artifactId\":\"api-util\",\"version\":\"1.0.0-M17\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":600907731,\"groupId\":\"org.slf4j\",\"artifactId\":\"slf4j-api\",\"version\":\"1.7.2\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":1611809201,\"groupId\":\"org.apache.directory.server\",\"artifactId\":\"apacheds-protocol-ldap\",\"version\":\"2.0.0-M12\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-1790504093,\"groupId\":\"org.apache.directory.jdbm\",\"artifactId\":\"apacheds-jdbm1\",\"version\":\"2.0.0-M2\",\"classifier\":\"\",\"packaging\":\"Unknown\"},{\"id\":127845435,\"groupId\":\"bouncycastle\",\"artifactId\":\"bcprov-jdk15\",\"version\":\"140\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-1576594175,\"groupId\":\"org.apache.directory.api\",\"artifactId\":\"api-ldap-codec-core\",\"version\":\"1.0.0-M17\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":835307534,\"groupId\":\"org.apache.directory.server\",\"artifactId\":\"apacheds-core\",\"version\":\"2.0.0-M12\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-683546007,\"groupId\":\"org.apache.directory.server\",\"artifactId\":\"apacheds-kerberos-codec\",\"version\":\"2.0.0-M12\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":373130454,\"groupId\":\"org.apache.directory.api\",\"artifactId\":\"api-ldap-extras-sp\",\"version\":\"1.0.0-M17\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-927000076,\"groupId\":\"org.apache.directory.api\",\"artifactId\":\"api-ldap-model\",\"version\":\"1.0.0-M17\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":838649745,\"groupId\":\"org.apache.mina\",\"artifactId\":\"mina-core\",\"version\":\"2.0.7\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":114977451,\"groupId\":\"org.apache.directory.server\",\"artifactId\":\"apacheds-jdbm-partition\",\"version\":\"2.0.0-M12\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-304899679,\"groupId\":\"org.apache.directory.server\",\"artifactId\":\"apacheds-core-api\",\"version\":\"2.0.0-M12\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-173953677,\"groupId\":\"org.apache.directory.server\",\"artifactId\":\"apacheds-protocol-shared\",\"version\":\"2.0.0-M12\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":1152185950,\"groupId\":\"org.apache.directory.api\",\"artifactId\":\"api-ldap-extras-codec-api\",\"version\":\"1.0.0-M17\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-1347991855,\"groupId\":\"org.apache.directory.api\",\"artifactId\":\"api-ldap-extras-util\",\"version\":\"1.0.0-M17\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":37058863,\"groupId\":\"org.apache.directory.server\",\"artifactId\":\"apacheds-i18n\",\"version\":\"2.0.0-M12\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-871342901,\"groupId\":\"org.apache.directory.api\",\"artifactId\":\"api-ldap-extras-codec\",\"version\":\"1.0.0-M17\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-258379929,\"groupId\":\"org.apache.directory.api\",\"artifactId\":\"api-ldap-schema-data\",\"version\":\"1.0.0-M17\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":131662406,\"groupId\":\"commons-lang\",\"artifactId\":\"commons-lang\",\"version\":\"2.6\",\"classifier\":\"\",\"packaging\":\"Jar\"}],\"edges\":[{\"source\":1611809201,\"destination\":835307534,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":-304899679,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":37058863,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":-173953677,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":-1790504093,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":114977451,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":-683546007,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":131662406,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":-423525824,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":1631527839,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":1152185950,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":-871342901,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":373130454,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":-1347991855,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":-1576594175,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":-927000076,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":-258379929,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":2039784661,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":838649745,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":127845435,\"scope\":\"Compile\"},{\"source\":1611809201,\"destination\":600907731,\"scope\":\"Compile\"}]}";
    public static final String DATA_2 = "{\"vertices\":[{\"id\":-772366602,\"groupId\":\"com.github.chrisbanes.actionbarpulltorefresh\",\"artifactId\":\"library\",\"version\":\"+\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-2139580392,\"groupId\":\"com.jakewharton.hugo\",\"artifactId\":\"hugo-annotations\",\"version\":\"1.0.1\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-262238273,\"groupId\":\"com.jakewharton\",\"artifactId\":\"butterknife\",\"version\":\"4.0.+\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":162656880,\"groupId\":\"eu.inmite.android.lib\",\"artifactId\":\"android-styled-dialogs\",\"version\":\"1.1.2\",\"classifier\":\"\",\"packaging\":\"Unknown\"},{\"id\":770722802,\"groupId\":\"de.keyboardsurfer.android.widget\",\"artifactId\":\"crouton\",\"version\":\"1.8.+\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-1158444810,\"groupId\":\"com.squareup\",\"artifactId\":\"otto\",\"version\":\"1.3.+\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":-50926288,\"groupId\":\"com.android.support\",\"artifactId\":\"support-v4\",\"version\":\"+\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":1799473767,\"groupId\":\"com.squareup.dagger\",\"artifactId\":\"dagger-compiler\",\"version\":\"1.2.1\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":1017473375,\"groupId\":\"com.squareup.dagger\",\"artifactId\":\"dagger\",\"version\":\"1.2.1\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":1704911235,\"groupId\":\"cz.kinst.jakub\",\"artifactId\":\"androidbase\",\"version\":\"0.1.3\",\"classifier\":\"\",\"packaging\":\"Jar\"},{\"id\":353505279,\"groupId\":\"com.squareup.phrase\",\"artifactId\":\"phrase\",\"version\":\"1.0.3\",\"classifier\":\"\",\"packaging\":\"Jar\"}],\"edges\":[{\"source\":1704911235,\"destination\":1017473375,\"scope\":\"Compile\"},{\"source\":1704911235,\"destination\":-262238273,\"scope\":\"Compile\"},{\"source\":1704911235,\"destination\":-50926288,\"scope\":\"Compile\"},{\"source\":1704911235,\"destination\":353505279,\"scope\":\"Compile\"},{\"source\":1704911235,\"destination\":-1158444810,\"scope\":\"Compile\"},{\"source\":1704911235,\"destination\":-772366602,\"scope\":\"Compile\"},{\"source\":1704911235,\"destination\":1799473767,\"scope\":\"Compile\"},{\"source\":1704911235,\"destination\":-2139580392,\"scope\":\"Compile\"},{\"source\":1704911235,\"destination\":770722802,\"scope\":\"Compile\"},{\"source\":1704911235,\"destination\":162656880,\"scope\":\"Compile\"}]}";

    private static final Gson GSON = new Gson();

    @Test
    public void submitGraphToExtension() throws Exception {
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

    @Test
    public void test2() throws Exception {
        // Given
        try (ServerControls server = TestServerBuilders.newInProcessBuilder()
                .withExtension(CONTEXT_ROOT, GraphUploader.class)
                .newServer()) {


            // When
            HTTP.Response post = HTTP.POST(server.httpURI().resolve(POST_ACTION).toString(), HTTP.RawPayload.quotedJson(DATA_2));
            assertThat(post.status(), is(HttpResponseStatus.OK.code()));
            assertThat(post.rawContent(), is("Submitted"));

            boolean consumed = false;
            do {
                HTTP.Response response = HTTP.GET(server.httpURI().resolve(GET_ACTION).toString());
                UploaderStatus uploaderStatus = GSON.fromJson(response.rawContent(), UploaderStatus.class);
                if (uploaderStatus.getExecutedCount() == 1) {
                    Thread.sleep(300);
                    consumed = true;
                }
            } while (!consumed);

            HTTP.Response response = HTTP.GET(server.httpURI().resolve(GET_ACTION).toString());
            UploaderStatus uploaderStatus = GSON.fromJson(response.rawContent(), UploaderStatus.class);
            System.out.println("uploaderStatus.getRelationsCreated() = " + uploaderStatus.getRelationsCreated());

//            Result execute = server.graph().execute("match n return(n)");
//            System.out.println("execute.resultAsString() = " + execute.resultAsString());

            Result execute = server.graph().execute("MATCH p=(a)-[r]->(b)\n" +
                    "RETURN a, r, b");
            System.out.println("execute.resultAsString() = " + execute.resultAsString());
        }


    }

    @Test
    public void submitEmptyGraphToExtension() throws Exception {
        // Given
        try (ServerControls server = TestServerBuilders.newInProcessBuilder()
                .withExtension(CONTEXT_ROOT, GraphUploader.class)
                .newServer()) {


            // When
            HTTP.Response post = HTTP.POST(server.httpURI().resolve(POST_ACTION).toString(), HTTP.RawPayload.quotedJson(EMPTY_DEPENDENCY_GRAPH));
            assertThat(post.status(), is(HttpResponseStatus.OK.code()));
            assertThat(post.rawContent(), is("Submitted"));

            boolean consumed = false;
            do {
                HTTP.Response response = HTTP.GET(server.httpURI().resolve(GET_ACTION).toString());
                UploaderStatus uploaderStatus = GSON.fromJson(response.rawContent(), UploaderStatus.class);
                if (uploaderStatus.getSkippedCount() == 1) {
                    Thread.sleep(100);
                    consumed = true;
                }
            } while (!consumed);
        }
    }
}