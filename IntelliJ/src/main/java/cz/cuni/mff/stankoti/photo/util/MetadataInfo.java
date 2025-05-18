package cz.cuni.mff.stankoti.photo.util;

import java.io.Serializable;

/**
  * This class is a supporting structure for one metadata tag.
  */
public class MetadataInfo implements Serializable {  // implement the Serializable interface
    /**
      * The metadata tag's directory.
      */
    private String directory;
    /**
      * The metadata tag's name.
      */
    private String tag;
    /**
      * A description of the tag's value.
      */
    private String description;

    /**
      * Constructor to initialize MetadataInfo with metadata tag's directory, name, and value description.
      * (Metadata directory is a collection of metadata tags.)
      *
      * @param directory the metadata tag's directory
      * @param tag the metadata tag's name
      * @param description a description of the tag's value
      */
    public MetadataInfo(String directory, String tag, String description) {
        setDirectory(directory);
        setTag(tag);
        setDescription(description);
    }

    /**
      * Gets the metadata tag's directory.
      *
      * @return the tag's directory
      */
    public String getDirectory() {
        return directory;
    }

    /**
      * Sets the metadata tag's directory.
      * If the provided directory is null, it sets an empty string.
      *
      * @param directory the tag's directory
      */
    public void setDirectory(String directory) {
        this.directory = (directory != null) ? directory : "";
    }

    /**
      * Gets the metadata tag's name.
      *
      * @return the tag's name
      */
    public String getTag() {
        return tag;
    }

    /**
      * Sets the metadata tag's name.
      * If the provided name is null, it sets an empty string.
      *
      * @param tag the tag's name
      */
    public void setTag(String tag) {
        this.tag = (tag != null) ? tag : "";
    }

    /**
      * Gets the metadata tag's value description.
      *
      * @return the tag's value description
      */
    public String getDescription() {
        return description;
    }

    /**
      * Sets the metadata tag's value description.
      * If the provided description is null, it sets an empty string.
      *
      * @param description the tag's value description
      */
    public void setDescription(String description) {
        this.description = (description != null) ? description : "";
    }
}
