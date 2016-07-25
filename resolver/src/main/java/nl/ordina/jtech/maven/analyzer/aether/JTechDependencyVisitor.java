package nl.ordina.jtech.maven.analyzer.aether;

import nl.ordina.jtech.mavendependencygraph.model.ArtifactPackaging;
import nl.ordina.jtech.mavendependencygraph.model.ArtifactVertex;
import nl.ordina.jtech.mavendependencygraph.model.DependencyGraph;
import nl.ordina.jtech.mavendependencygraph.model.Scope;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

public class JTechDependencyVisitor implements DependencyVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JTechDependencyVisitor.class);
    private PrintStream out;
    private String currentIndent = "";
    private int currentIndentation = 0;

    private DependencyGraph localDependencies;

    private ArtifactVertex firstLevelArtifactVertex;

    public JTechDependencyVisitor() {
        this(null);
    }

    public JTechDependencyVisitor(PrintStream out) {
        this.out = (out != null) ? out : System.out;
    }

    public DependencyGraph getLocalDependencies() {
        return localDependencies;
    }

    public void setLocalDependencies(DependencyGraph localDependencies) {
        this.localDependencies = localDependencies;
    }

    public boolean visitEnter(DependencyNode node) {
        currentIndentation += 1;
        out.println(currentIndent + node + "(" + currentIndentation + ")");

        // get the source node and remember it
        if (currentIndentation == 1) {
            firstLevelArtifactVertex = getArtifactVertexFromArtifactCoordinate(node.getDependency());
        }
        // get the nodes on the second level (the direct dependencies), and add these with the first node to the graph
        else if (currentIndentation == 2) {
            ArtifactVertex secondLevelArtifactVerteX = getArtifactVertexFromArtifactCoordinate(node.getDependency());

            if (isValidVersion(secondLevelArtifactVerteX.getVersion())) {
                LOGGER.info("   ----> Source artifact vertex and destination artifact vertex being added...");
                LOGGER.info("   ----> Adding dependency nr. " + (localDependencies.getEdges().size() + 1));

                Scope scope = deriveScope(node.getDependency());

                localDependencies.addDependency(firstLevelArtifactVertex, secondLevelArtifactVerteX, scope);
            } else {
                LOGGER.info("   ----> Artifact vertex NOT being added due to INVALID version ...");
            }
        } else {
            LOGGER.info("   ----> Artifact vertex NOT being added...");

        }

        if (currentIndent.length() <= 0) {
            currentIndent = "+- ";
        } else {
            currentIndent = "|  " + currentIndent;
        }
        return true;
    }

    private boolean isValidVersion(String version) {
        return !version.contains("$");
    }


    // Ugly; have to derive things from the artifact coordinate string
    private ArtifactVertex getArtifactVertexFromArtifactCoordinate(Dependency dependency) {
        String groupId = dependency.getArtifact().getGroupId();
        String artifactId = dependency.getArtifact().getArtifactId();
        String version = dependency.getArtifact().getVersion();
        ArtifactPackaging packaging = derivePackaging(dependency.getArtifact());
        String classifier = dependency.getArtifact().getClassifier();
        ArtifactVertex artifactVertex = new ArtifactVertex(groupId, artifactId, packaging, version, classifier);

        LOGGER.info(" from split -> " + artifactVertex.toString());
        return artifactVertex;
    }


    public boolean visitLeave(DependencyNode node) {
        currentIndent = currentIndent.substring(3, currentIndent.length());
        currentIndentation -= 1;
        return true;
    }

    private Scope deriveScope(Dependency dependency) {
        return Scope.parseFromString(dependency.getScope());
    }

    private ArtifactPackaging derivePackaging(Artifact artifact) {
        return ArtifactPackaging.parseFromString(artifact.getExtension());
    }

}
