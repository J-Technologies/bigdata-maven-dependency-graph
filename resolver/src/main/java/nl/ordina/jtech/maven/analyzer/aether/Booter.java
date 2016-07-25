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

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A helper to boot the repository system and a repository system session.
 */
public class Booter {

    public static org.eclipse.aether.RepositorySystem newRepositorySystem() {
        return ManualRepositorySystemFactory.newRepositorySystem();
    }

    public static org.eclipse.aether.DefaultRepositorySystemSession newRepositorySystemSession(org.eclipse.aether.RepositorySystem system) {
        org.eclipse.aether.DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        org.eclipse.aether.repository.LocalRepository localRepo = new org.eclipse.aether.repository.LocalRepository("target/local-repo");
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        session.setTransferListener(new ConsoleTransferListener());
        session.setRepositoryListener(new ConsoleRepositoryListener());

        return session;
    }

    public static List<RemoteRepository> newRepositories(RepositorySystem system, RepositorySystemSession session) {
        return new ArrayList<>(Collections.singletonList(newCentralRepository()));
    }


    public static RemoteRepository newCentralRepository() {
        //return new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/").build();
        //return new RemoteRepository.Builder("central", "default", "http://jtechbd-cldsrvc.cloudapp.net:8090/nexus/content/repositories/maven").build();
        return new RemoteRepository.Builder("central", "default", "http://jtechbd-nexus:8090/nexus/content/repositories/maven").build();
    }

}
