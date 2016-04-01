package nl.ordina.jtech.mavendependencygraph.model;

public enum ArtifactPackaging {
    Jar, War, Ear, Pom;

    public static ArtifactPackaging parseFromString(String packaging) {
        switch (packaging) {
            case "jar":
                return ArtifactPackaging.Jar;
            case "war":
                return ArtifactPackaging.War;
            case "ear":
                return ArtifactPackaging.Ear;
            case "pom":
                return ArtifactPackaging.Pom;
            default:
                return null;
        }
    }
}
