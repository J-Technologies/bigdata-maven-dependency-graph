package nl.ordina.jtech.mavendependencygraph.neo4j;

import nl.ordina.jtech.mavendependencygraph.model.ArtifactPackaging;
import nl.ordina.jtech.mavendependencygraph.model.ArtifactVertex;
import nl.ordina.jtech.mavendependencygraph.model.DependencyGraph;
import nl.ordina.jtech.mavendependencygraph.model.Scope;

/**
 * Class: GraphCreator
 */
public class GraphCreator {

    public static DependencyGraph getGraph() {
        DependencyGraph graph = new DependencyGraph();
        ArtifactVertex javaeeApi = new ArtifactVertex("javax", "javaee-api", ArtifactPackaging.Jar, "7.0");//, provided
        ArtifactVertex javaxMail = new ArtifactVertex("com.sun.mail", "javax.mail", ArtifactPackaging.Jar, "1.5.0");//:provided
        ArtifactVertex activation = new ArtifactVertex("javax.activation", "activation", ArtifactPackaging.Jar, "1.1");//:provided

        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.addDependency(javaeeApi, javaxMail, Scope.Provided);
        dependencyGraph.addDependency(javaxMail, activation, Scope.Compile);

        return dependencyGraph;
    }
}
