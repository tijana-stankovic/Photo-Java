package cz.cuni.mff.stankoti.photo.db;

import java.io.Serializable;
import java.util.List;

public class File implements Serializable {  // Implement the Serializable interface
    private static final long serialVersionUID = 1L;
    public int id;
    public String location;
    public String filename;
    public String extension;
    public String timestamp;
    public int size;
    public long checksum;
    public List<String> keywords;
    public List<String> metadata;

    public File(int id, String location, String filename, String extension, String timestamp, int size, long checksum, List<String> keywords, List<String> metadata) {
        this.id = id;
        this.location = location;
        this.filename = filename;
        this.extension = extension;
        this.timestamp = timestamp;
        this.size = size;
        this.checksum = checksum;
        this.keywords = keywords;
        this.metadata = metadata;
    }
}
