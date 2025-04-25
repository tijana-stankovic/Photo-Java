package cz.cuni.mff.stankoti.photo.db;

import cz.cuni.mff.stankoti.photo.util.MetadataInfo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
  * This class contains structures and methods for handling information related to a single file.
  */
public class DBFile implements Serializable {  // implement the Serializable interface
    /**
      * Unique identifier for the serialized class version.
      */
    private static final long serialVersionUID = 1L;
    /**
      * File object unique identifier.
      */
    private int id;
    /**
      * File absolute path (including filename).
      */
    private String fullpath;
    /**
      * File location (directory).
      */
    private String location;
    /**
      * File filename only (without path and extension).
      */
    private String filename;
    /**
      * File extension.
      */
    private String extension;
    /**
      * File timestamp.
      */
    private String timestamp;
    /**
      * File size.
      */
    private long size;
    /**
      * File CRC32 checksum.
      */
    private long checksum;
    /**
      * Set of keywords associated with the file.
      */
    private Set<String> keywords;
    /**
      * Set of metadata tags contained in the file.
      */
    private Set<MetadataInfo> metadata;
    /**
      * Set of exact duplicates of this file.
      */
    private Set<Integer> duplicates;
    /**
      * Set of potential duplicates of this file.
      */
    private Set<Integer> potentialDuplicates;

    /**
      * Default constructor required for deserialization.
      */
    public DBFile() {
        id = 0;
        fullpath = "";
        location = "";
        filename = "";
        extension = "";
        timestamp = "";
        size = 0L;
        checksum = 0L;
        keywords = new HashSet<>();
        metadata = new HashSet<>();
        duplicates = new HashSet<>();
        potentialDuplicates = new HashSet<>();
    }

    /**
      * Parameterized constructor to initialize DBFile with specific values.
      *
      * @param id the unique identifier of the file
      * @param fullpath the full path of the file
      * @param location the location (directory/folder) of the file
      * @param filename the name of the file (without path and extension)
      * @param extension the file extension
      * @param timestamp the timestamp of the file
      * @param size the size of the file in bytes
      * @param checksum the checksum of the file
      * @param keywords the set of keywords associated with the file
      * @param metadata the set of metadata information (tags) contained in the file
      * @param duplicates the set of file IDs corresponding to files that are identical to the file
      * @param potentialDuplicates the set of file IDs corresponding to files that are potentially identical to the file
      */
    public DBFile(int id, 
                String fullpath, String location, String filename, String extension, 
                String timestamp, long size, long checksum, 
                Set<String> keywords, Set<MetadataInfo> metadata,
                Set<Integer> duplicates, Set<Integer> potentialDuplicates) {
        setID(id);
        setFullpath(fullpath);
        setLocation(location);
        setFilename(filename);
        setExtension(extension);
        setTimestamp(timestamp);
        setSize(size);
        setChecksum(checksum);
        setKeywords(keywords);
        setMetadata(metadata);
        setDuplicates(duplicates);
        setPotentialDuplicates(potentialDuplicates);
    }

    /**
      * Gets the file identifier.
      *
      * @return the file ID
      */
    public int getID() {
        return id;
    }

    /**
      * Sets the file identifier.
      *
      * @param id the file ID
      */
    public void setID(int id) {
        assert id > 0 : "File ID must be positive!";
        this.id = id;
    }

    /**
      * Gets the full path of the file.
      *
      * @return the full path of the file
      */
    public String getFullpath() {
        return fullpath;
    }

    /**
      * Sets the full path of the file.
      *
      * @param fullpath the full path of the file
      */
    public void setFullpath(String fullpath) {
        assert fullpath != null && !fullpath.isEmpty() : "File path must be specified!";
        this.fullpath = fullpath;
    }

    /**
      * Gets the location (directory) of the file.
      *
      * @return the location of the file
      */
    public String getLocation() {
        return location;
    }

    /**
      * Sets the location (directory) of the file.
      *
      * @param location the location of the file
      */
    public void setLocation(String location) {
        assert location != null && !location.isEmpty() : "File location must be specified!";
        this.location = location;
    }

    /**
      * Gets the name of the file.
      *
      * @return the filename
      */
    public String getFilename() {
        return filename;
    }

    /**
      * Sets the name of the file.
      *
      * @param filename the filename
      */
    public void setFilename(String filename) {
        assert filename != null && !filename.isEmpty() : "Filename must be specified!";
        this.filename = filename;
    }

    /**
      * Gets the file extension.
      *
      * @return the file extension
      */
    public String getExtension() {
        return extension;
    }

    /**
      * Sets the file extension.
      *
      * @param extension the file extension
      */
    public void setExtension(String extension) {
        assert extension != null && !extension.isEmpty() : "Extension must be specified!";
        this.extension = extension;
    }

    /**
      * Gets the timestamp of the file.
      *
      * @return the timestamp
      */
    public String getTimestamp() {
        return timestamp;
    }

    /**
      * Sets the timestamp of the file.
      *
      * @param timestamp the timestamp
      */
    public void setTimestamp(String timestamp) {
        assert timestamp != null && !timestamp.isEmpty() : "Timestamp must be specified!";
        this.timestamp = timestamp;
    }

    /**
      * Gets the size of the file in bytes.
      *
      * @return the file size
      */
    public long getSize() {
        return size;
    }

    /**
      * Sets the size of the file in bytes.
      *
      * @param size the file size
      */
    public void setSize(long size) {
        assert size >= 0 : "Size must not be negative!";
        this.size = size;
    }

    /**
      * Gets the checksum of the file.
      *
      * @return the checksum
      */
    public long getChecksum() {
        return checksum;
    }

    /**
      * Sets the checksum of the file.
      *
      * @param checksum the checksum
      */
    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }

    /**
      * Gets the set of keywords associated with the file.
      *
      * @return the set of keywords
      */
    public Set<String> getKeywords() {
        return keywords;
    }

    /**
      * Sets the set of keywords associated with the file.
      *
      * @param keywords the set of keywords
      */
    public void setKeywords(Set<String> keywords) {
        if (keywords == null) {
            this.keywords = new HashSet<>();
        } else {
            this.keywords = keywords;
        }
    }

    /**
      * Gets the set of metadata information contained in the file.
      *
      * @return the set of metadata information
      */
    public Set<MetadataInfo> getMetadata() {
        return metadata;
    }

    /**
      * Sets the set of metadata information contained in the file.
      *
      * @param metadata the set of metadata information
      */
    public void setMetadata(Set<MetadataInfo> metadata) {
        if (metadata == null) {
            this.metadata = new HashSet<>();
        } else {
            this.metadata = metadata;
        }
    }

    /**
      * Gets the set of file IDs corresponding to files that are identical to the file.
      *
      * @return the set of IDs corresponding to the file duplicates
      */
    public Set<Integer> getDuplicates() {
        return duplicates;
    }

    /**
      * Sets the set of file IDs corresponding to files that are identical to the file.
      *
      * @param duplicates the set of IDs corresponding to the file duplicates
      */
    public void setDuplicates(Set<Integer> duplicates) {
        if (duplicates == null) {
            this.duplicates = new HashSet<>();
        } else {
            this.duplicates = duplicates;
        }
    }

    /**
      * Gets the set of file IDs corresponding to files that are potentially identical to the file.
      *
      * @return the set of IDs corresponding to the file potential duplicates
      */
    public Set<Integer> getPotentialDuplicates() {
        return potentialDuplicates;
    }

    /**
      * Sets the set of file IDs corresponding to files that are potentially identical to the file.
      *
      * @param potentialDuplicates the set of IDs corresponding to the file potential duplicates
      */
    public void setPotentialDuplicates(Set<Integer> potentialDuplicates) {
        if (potentialDuplicates == null) {
            this.potentialDuplicates = new HashSet<>();
        } else {
            this.potentialDuplicates = potentialDuplicates;
        }
    }

    /**
      * Adds a keyword to the set of keywords associated with the file.
      *
      * @param keyword the keyword to add
      */
    public void addKeyword(String keyword) {
        assert keyword != null && !keyword.isEmpty() : "Keyword must be specified!";
        keywords.add(keyword.toUpperCase());
    }

    /**
      * Removes a keyword from the set of keywords associated with the file.
      *
      * @param keyword the keyword to remove
      */
    public void removeKeyword(String keyword) {
        keywords.remove(keyword.toUpperCase());
    }

    /**
      * Adds one metadata information (tag) to the set of metadata associated with the file.
      *
      * @param metadataInfo the metadata information (tag) to add
      */
    public void addMetadata(MetadataInfo metadataInfo) {
        assert metadataInfo != null : "Metadata must be specified!";
        metadata.add(metadataInfo);
    }

    /**
      * Removes one metadata information (tag) from the set of metadata associated with the file.
      *
      * @param metadataInfo the metadata information (tag) to remove
      */
    public void removeMetadata(MetadataInfo metadataInfo) {
        metadata.remove(metadataInfo);
    }

    /**
      * Adds a duplicate file ID to the set of duplicate file IDs.
      *
      * @param duplicateFileID the duplicate file ID to add
      */
    public void addDuplicate(Integer duplicateFileID) {
        assert duplicateFileID > 0 : "Duplicate file ID must be positive!";
        duplicates.add(duplicateFileID);
    }

    /**
      * Removes a duplicate file ID from the set of duplicate file IDs.
      *
      * @param duplicateFileID the duplicate file ID to remove
      */
    public void removeDuplicate(Integer duplicateFileID) {
        duplicates.remove(duplicateFileID);
    }

    /**
      * Adds a potential duplicate file ID to the set of potential duplicate file IDs.
      *
      * @param potentialDuplicateFileID the potential duplicate file ID to add
      */
    public void addPotentialDuplicate(Integer potentialDuplicateFileID) {
        assert potentialDuplicateFileID > 0 : "Potential duplicate file ID must be positive!";
        potentialDuplicates.add(potentialDuplicateFileID);
    }

    /**
      * Removes a potential duplicate file ID from the set of potential duplicate file IDs.
      *
      * @param potentialDuplicateFileID the potential duplicate file ID to remove
      */
    public void removePotentialDuplicate(Integer potentialDuplicateFileID) {
        potentialDuplicates.remove(potentialDuplicateFileID);
    }
}
