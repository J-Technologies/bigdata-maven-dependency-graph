package nl.ordina.jtech.mavendependencygraph.model;

// The current core packaging values are: pom, jar, maven-plugin, ejb, war, ear, rar, par.
public enum ArtifactPackaging {
    Jar, War, Ear, Pom, Ejb, Rar, Par, Maven_Plugin, Unknown;

    public static ArtifactPackaging parseFromString(String packaging) {
        switch (packaging.toLowerCase()) {
            case "jar":
                return ArtifactPackaging.Jar;
            case "war":
                return ArtifactPackaging.War;
            case "ear":
                return ArtifactPackaging.Ear;
            case "pom":
                return ArtifactPackaging.Pom;
            case "ejb":
                return ArtifactPackaging.Ejb;
            case "rar":
                return ArtifactPackaging.Rar;
            case "par":
                return ArtifactPackaging.Par;
            case "maven-plugin":
                return ArtifactPackaging.Maven_Plugin;
            default:
                return ArtifactPackaging.Unknown;
        }
    }
}
