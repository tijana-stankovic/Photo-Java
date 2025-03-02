package cz.cuni.mff.stankoti.photo.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DBData implements Serializable {  // Implement the Serializable interface
    private static final long serialVersionUID = 1L;
    private int lastFileID;
    private Map<Integer, File> files;

    public DBData() {
        lastFileID = 0;
        files = new HashMap<>();
    }

    public int getLastFileID() {
        return lastFileID;
    }

    public void setLastFileID(int newID) {
        lastFileID = newID;
    }

    public int nextFileID() {
        setLastFileID(getLastFileID() + 1);
        return getLastFileID();
    }

    public void addFile(File file) {
        files.put(file.getId(), file);
    }

    public void removeFile(int fileId) {
        if (files.containsKey(fileId)) {
            files.remove(fileId);
        }
    }
}
