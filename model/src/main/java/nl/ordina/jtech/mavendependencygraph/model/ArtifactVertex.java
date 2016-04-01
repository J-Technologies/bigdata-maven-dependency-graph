package nl.ordina.jtech.mavendependencygraph.model;

import java.io.Serializable;

public class ArtifactVertex implements Serializable {
    private final int id;
    private final String groupId;
    private final String artifactId;
    private String version;
    private String classifier;
    private ArtifactPackaging packaging;

    public ArtifactVertex(final String groupId, final String artifactId, final ArtifactPackaging packaging, final String version, String classifier) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.packaging = packaging;
        this.classifier = classifier;
        this.id = hashCode();
    }

    public ArtifactVertex(final String groupId, final String artifactId, final ArtifactPackaging packaging, final String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.packaging = packaging;
        this.classifier = "";
        this.id = hashCode();
    }

    public String gav() {
        return gav(":");
    }

    public String gav(final String sep) {
        return (groupId + sep + artifactId + sep + packaging + sep + (classifier != null ? classifier + sep : "") + version).replaceAll("[-.]", sep);
    }

    public int getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getClassifier() {
        return classifier;
    }

    public ArtifactPackaging getPackaging() {
        return packaging;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArtifactVertex that = (ArtifactVertex) o;

        if (!groupId.equals(that.groupId)) return false;
        if (!artifactId.equals(that.artifactId)) return false;
        if (!version.equals(that.version)) return false;
        if (classifier != null ? !classifier.equals(that.classifier) : that.classifier != null) return false;
        return packaging == that.packaging;

    }

    @Override
    public int hashCode() {
        int result = groupId.hashCode();
        result = 31 * result + artifactId.hashCode();
        result = 31 * result + version.hashCode();
        result = 31 * result + (classifier != null ? classifier.hashCode() : 0);
        result = 31 * result + packaging.hashCode();
        return result;
    }
    
	@Override
	public String toString()
	{
		return "ArtifactVertex [id='" + id + "', groupId='" + groupId + "', artifactId='" + artifactId + "', version='" + version + "', classifier='" + classifier + "', packaging='"
		        + packaging + "']";
	}
    
    
}
