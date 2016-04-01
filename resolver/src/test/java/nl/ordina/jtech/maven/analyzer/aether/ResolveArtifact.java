/*
 * Copyright (C) 2012 Pieter van der Meer (pieter(at)elucidator.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.ordina.jtech.maven.analyzer.aether;

import nl.ordina.jtech.mavendependencygraph.model.DependencyGraph;
import org.junit.Test;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Resolves a single artifact.
 */
public class ResolveArtifact {

    @Test
    public void resolve() throws DependencyCollectionException {
        ArtifactResolver resolver = new ArtifactResolver();
        String artifactCoordinate = "org.apache.maven:maven-aether-provider:3.1.0";
        DependencyGraph dependencyGraph = resolver.resolveToDependencyGraph(new DefaultArtifact(artifactCoordinate));
        System.out.println(dependencyGraph.toJson());
    }

}


