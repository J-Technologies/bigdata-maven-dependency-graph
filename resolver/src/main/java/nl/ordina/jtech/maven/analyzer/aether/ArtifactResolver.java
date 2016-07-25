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

import nl.ordina.jtech.mavendependencygraph.model.ArtifactPackaging;
import nl.ordina.jtech.mavendependencygraph.model.ArtifactVertex;
import nl.ordina.jtech.mavendependencygraph.model.DependencyGraph;
import nl.ordina.jtech.mavendependencygraph.model.Scope;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Resolver for artifacts
 */
public class ArtifactResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactResolver.class);

    private final RepositorySystem system;
    private final RemoteRepository repo;
    private final RepositorySystemSession session;


    public ArtifactResolver() {
        system = Booter.newRepositorySystem();
        session = Booter.newRepositorySystemSession(system);
        repo = Booter.newCentralRepository();
    }

    public static void main(String[] args) {
        ArtifactResolver artifactResolver = new ArtifactResolver();
        try {
            String artifactCoordinate = "org.apache.maven.plugins:maven-compiler-plugin:2.3";
            DefaultArtifact artifact = new DefaultArtifact(artifactCoordinate);
            DependencyGraph dependencyGraph = artifactResolver.resolveToDependencyGraph(artifact);

            LOGGER.info("/===========================================\\");
            LOGGER.info("Num: " + dependencyGraph.getEdges().size());
        } catch (DependencyCollectionException e) {
            LOGGER.error("Exception leaked up till main: ", e);
        }
    }

    public DependencyGraph resolveToDependencyGraph(Artifact artifact) throws DependencyCollectionException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Resolving: " + artifact);
        }

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(new Dependency(artifact, "")); //FIXME: Scope shouldn't matter
        collectRequest.addDependency(new Dependency(artifact, JavaScopes.COMPILE));
        collectRequest.addRepository(repo);


        CollectResult collectResult;
        try {
            collectResult = system.collectDependencies(session, collectRequest);
        } catch (DependencyCollectionException e) {
            LOGGER.error("Dependencies not resolved for artifact " + artifact, e);
            collectResult = e.getResult();
        }

        JTechDependencyVisitor jTechVisitor = new JTechDependencyVisitor();
        DependencyGraph localDependencies = new DependencyGraph();
        jTechVisitor.setLocalDependencies(localDependencies);
        if (collectResult.getRoot() != null) {
            collectResult.getRoot().accept(jTechVisitor);
        }

        DependencyGraph resultingDependencyGraph = jTechVisitor.getLocalDependencies();
        if (resultingDependencyGraph.getVertices().isEmpty()) {
            LOGGER.error("No valid dependencies found for " + artifact);
        }
        return resultingDependencyGraph;
    }

    public DependencyGraph resolveToDependencyGraphv2(Artifact artifact) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Resolving: " + artifact);
        }

        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact(artifact);
        descriptorRequest.setRepositories(Booter.newRepositories(system, session));

        ArtifactDescriptorResult descriptorResult = null;
        try {
            descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);
        } catch (ArtifactDescriptorException e) {
            e.printStackTrace();
        }

        ArtifactVertex parent = new ArtifactVertex(artifact.getGroupId(), artifact.getArtifactId(), derivePackaging(artifact), artifact.getVersion(), artifact.getClassifier());
        DependencyGraph dependencyGraph = new DependencyGraph();

        if (descriptorResult != null) {
            for (Dependency dependency : descriptorResult.getDependencies()) {
                System.out.println(dependency);
                ArtifactVertex dest = new ArtifactVertex(dependency.getArtifact().getGroupId(), dependency.getArtifact().getArtifactId(), derivePackaging(dependency.getArtifact()), dependency.getArtifact().getVersion(), dependency.getArtifact().getClassifier());
                dependencyGraph.addDependency(parent, dest, deriveScope(dependency));
            }
        }


        if (dependencyGraph.getVertices().isEmpty()) {
            LOGGER.error("No valid dependencies found for " + artifact);
        }
        return dependencyGraph;
    }

    private Scope deriveScope(Dependency dependency) {
        return Scope.parseFromString(dependency.getScope());
    }

    private ArtifactPackaging derivePackaging(Artifact artifact) {
        return ArtifactPackaging.parseFromString(artifact.getExtension());
    }


}
