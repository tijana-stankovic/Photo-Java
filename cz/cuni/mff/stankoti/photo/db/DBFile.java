package cz.cuni.mff.stankoti.photo.db;

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
    private int size;
    private long checksum;
    private Set<String> keywords;
    private Set<String> metadata;    

    // This constructor is required for deserialization
    public DBFile() {
        id = 0;
        location = "";
        filename = "";
        extension = "";
        timestamp = "";
        size = 0;
        checksum = 0L;
        keywords = new HashSet<>();
        metadata = new HashSet<>();
    }

    public DBFile(int id, 
                String location, String filename, String extension, 
                String timestamp, int size, long checksum, 
                Set<String> keywords, Set<String> metadata) {
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
        assert extension != null : "Extension must not be null!";
        this.extension = extension;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        assert timestamp != null && !timestamp.isEmpty() : "Timestamp must be specified!";
        this.timestamp = timestamp;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
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

    public Set<String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Set<String> metadata) {
        if (metadata == null) {
            this.metadata = new HashSet<>();
        } else {
            this.metadata = metadata;
        }
    }

    public void addKeyword(String keyword) {
        assert keyword != null && !keyword.isEmpty() : "Keyword must be specified!";
        if (keyword != null) {
            keywords.add(keyword);
        }
    }

    public void removeKeyword(String keyword) {
        keywords.remove(keyword);
    }

    public void addMetadata(String metadataString) {
        assert metadataString != null && !metadataString.isEmpty() : "Metadata must be specified!";
        if (metadataString != null) {
            metadata.add(metadataString);
        }
    }

    public void removeMetadata(String metadataString) {
        metadata.remove(metadataString);
    }    
}
