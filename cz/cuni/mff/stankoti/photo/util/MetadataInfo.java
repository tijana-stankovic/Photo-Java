package cz.cuni.mff.stankoti.photo.util;

import java.io.Serializable;

public class MetadataInfo implements Serializable {  // Implement the Serializable interface
    private String directory;
    private String tag;
    private String description;

    public MetadataInfo(String directory, String tag, String description) {
        setDirectory(directory);
        setTag(tag);
        setDescription(description);
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = (directory != null) ? directory : "";
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = (tag != null) ? tag : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description : "";
    }
}
