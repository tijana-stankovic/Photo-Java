package cz.cuni.mff.stankoti.photo.db;

import cz.cuni.mff.stankoti.photo.util.MetadataInfo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class DBFile implements Serializable {  // Implement the Serializable interface
    private static final long serialVersionUID = 1L;
    private int id;
    private String location;
    private String filename;
    private String extension;
    private String timestamp;
    private long size;
    private long checksum;
    private Set<String> keywords;
    private Set<MetadataInfo> metadata;

    // This constructor is required for deserialization
    public DBFile() {
        id = 0;
        location = "";
        filename = "";
        extension = "";
        timestamp = "";
        size = 0L;
        checksum = 0L;
        keywords = new HashSet<>();
        metadata = new HashSet<>();
    }

    public DBFile(int id, 
                String location, String filename, String extension, 
                String timestamp, long size, long checksum, 
                Set<String> keywords, Set<MetadataInfo> metadata) {
        setID(id);
        setLocation(location);
        setFilename(filename);
        setExtension(extension);
        setTimestamp(timestamp);
        setSize(size);
        setChecksum(checksum);
        setKeywords(keywords);
        setMetadata(metadata);
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        assert id > 0 : "File ID must be positive!";
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        assert location != null && !location.isEmpty() : "File location must be specified!";
        this.location = location;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        assert filename != null && !filename.isEmpty() : "Filename must be specified!";
        this.filename = filename;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        assert extension != null && !extension.isEmpty() : "Extension must be specified!";
        this.extension = extension;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        assert timestamp != null && !timestamp.isEmpty() : "Timestamp must be specified!";
        this.timestamp = timestamp;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        assert size >= 0 : "Size must not be negative!";
        this.size = size;
    }

    public long getChecksum() {
        return checksum;
    }

    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        if (keywords == null) {
            this.keywords = new HashSet<>();
        } else {
            this.keywords = keywords;
        }
    }

    public Set<MetadataInfo> getMetadata() {
        return metadata;
    }

    public void setMetadata(Set<MetadataInfo> metadata) {
        if (metadata == null) {
            this.metadata = new HashSet<>();
        } else {
            this.metadata = metadata;
        }
    }

    public void addKeyword(String keyword) {
        assert keyword != null && !keyword.isEmpty() : "Keyword must be specified!";
        if (keyword != null) {
            keywords.add(keyword.toUpperCase());
        }
    }

    public void removeKeyword(String keyword) {
        keywords.remove(keyword.toUpperCase());
    }

    public void addMetadata(MetadataInfo metadataInfo) {
        assert metadataInfo != null : "Metadata must be specified!";
        if (metadata != null) {
            metadata.add(metadataInfo);
        }
    }

    public void removeMetadata(MetadataInfo metadataInfo) {
        metadata.remove(metadataInfo);
    }
}
