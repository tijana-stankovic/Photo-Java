package cz.cuni.mff.stankoti.photo.db;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class File implements Serializable {  // Implement the Serializable interface
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

    public File() {
        setId(0);
        setLocation("");
        setFilename("");
        setExtension("");
        setTimestamp("");
        setSize(0);
        setChecksum(0L);
        setKeywords(new HashSet<>());
        setMetadata(new HashSet<>());
    }

    public File(int id, 
                String location, String filename, String extension, 
                String timestamp, int size, long checksum, 
                Set<String> keywords, Set<String> metadata) {
        setId(id);
        setLocation(location);
        setFilename(filename);
        setExtension(extension);
        setTimestamp(timestamp);
        setSize(size);
        setChecksum(checksum);
        setKeywords(keywords);
        setMetadata(metadata);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
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
        if (keyword != null) {
            keywords.add(keyword);
        }
    }

    public void removeKeyword(String keyword) {
        keywords.remove(keyword);
    }

    public void addMetadata(String metadataString) {
        if (metadataString != null) {
            metadata.add(metadataString);
        }
    }

    public void removeMetadata(String metadataString) {
        metadata.remove(metadataString);
    }    
}
