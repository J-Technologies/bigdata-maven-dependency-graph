package nl.ordina.jtech.mavendependencygraph.model;

import java.io.Serializable;

public class ArtifactEdge implements Serializable {
    private final int source;
    private final int destination;
    private Scope scope;

    public ArtifactEdge(final ArtifactVertex source, final ArtifactVertex destination, final Scope scope) {
        this.source = source.hashCode();
        this.destination = destination.hashCode();
        this.scope = scope;
    }

    public int getSource() {
        return source;
    }

    public int getDestination() {
        return destination;
    }

    public Scope getScope() {
        return scope;
    }
}
