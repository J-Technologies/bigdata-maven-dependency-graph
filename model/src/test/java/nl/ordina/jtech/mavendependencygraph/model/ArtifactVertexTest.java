package nl.ordina.jtech.mavendependencygraph.model;

import org.junit.Test;

/**
 * Class: ArtifactVertexTest
 */
public class ArtifactVertexTest {

    @Test
    public void testConvert() throws Exception {
        ArtifactVertex model = new ArtifactVertex("nl.ordina", "model", ArtifactPackaging.Jar, "1,0,0");
        //System.out.println("model.toJson() = " + model.toJson());
    }

    @Test
    public void testMultipleDependencies() throws Exception {
//        [INFO] +- javax:javaee-api:jar:7.0:provided
//                [INFO] |  \- com.sun.mail:javax.mail:jar:1.5.0:provided
//                [INFO] |     \- javax.activation:activation:jar:1.1:provided


        ArtifactVertex javaeeApi = new ArtifactVertex("javax", "javaee-api", ArtifactPackaging.Jar, "7.0");//, provided
        ArtifactVertex javaxMail = new ArtifactVertex("com.sun.mail", "javax.mail", ArtifactPackaging.Jar, "1.5.0");//:provided
        ArtifactVertex activation = new ArtifactVertex("javax.activation", "activation", ArtifactPackaging.Jar, "1.1");//:provided


        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.addDependency(javaeeApi, javaxMail, Scope.Provided);
        dependencyGraph.addDependency(javaxMail, activation, Scope.Provided);

        System.out.println("dependencyGraph.toJson() = " + dependencyGraph.toJson());

    }
}