package nl.ordina.jtech.mavendependencygraph.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencyGraph implements GSonConverter, Serializable {
    private Set<ArtifactVertex> vertices = new HashSet<>();
    private List<ArtifactEdge> edges = new ArrayList<>();

    public void addDependency(final ArtifactVertex from, final ArtifactVertex dest, final Scope type) {
        edges.add(new ArtifactEdge(from, dest, type));
        vertices.add(from);
        vertices.add(dest);
    }

    public Set<ArtifactVertex> getVertices() {
        return vertices;
    }

    public List<ArtifactEdge> getEdges() {
        return edges;
    }

    
    
    
}
