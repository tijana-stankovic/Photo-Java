package cz.cuni.mff.stankoti.photo.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DBData implements Serializable {  // Implement the Serializable interface
    private static final long serialVersionUID = 1L;
    private int lastFileID;
    private Map<Integer, DBFile> files;
    private Map<String, Set<Integer>> locations;
    private Map<String, Set<Integer>> filenames;
    private Map<String, Set<Integer>> extensions;
    private Map<String, Set<Integer>> timestamps;
    private Map<Long, Set<Integer>> sizes;
    private Map<Long, Set<Integer>> checksums;
    private Map<String, Set<Integer>> keywords;
    private Map<String, Set<Integer>> metadataTags;

    public DBData() {
        lastFileID = 0;
        files = new HashMap<>();
        locations = new HashMap<>();
        filenames = new HashMap<>();
        extensions = new HashMap<>();
        timestamps = new HashMap<>();
        sizes = new HashMap<>();
        checksums = new HashMap<>();
        keywords = new HashMap<>();
        metadataTags = new HashMap<>();
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

    public DBFile getFile(int fileID) {
        return files.getOrDefault(fileID, null);
    }

    public void addFile(DBFile file) {
        files.put(file.getID(), file);
    }

    public void removeFile(int fileID) {
        if (files.containsKey(fileID)) {
            files.remove(fileID);
        }
    }

    public void addFileLocation(String location, int fileID) {
        locations.computeIfAbsent(location, k -> new HashSet<>()).add(fileID);
    }

    public void removeFileLocation(String location, int fileID) {
        Set<Integer> fileIDs = locations.get(location);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                locations.remove(location);
            }
        }
    }

    public void addFileFilename(String filename, int fileID) {
        filenames.computeIfAbsent(filename, k -> new HashSet<>()).add(fileID);
    }

    public void removeFileFilename(String filename, int fileID) {
        Set<Integer> fileIDs = filenames.get(filename);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                filenames.remove(filename);
            }
        }
    }

    public void addFileExtension(String extension, int fileID) {
        extensions.computeIfAbsent(extension, k -> new HashSet<>()).add(fileID);
    }

    public void removeFileExtension(String extension, int fileID) {
        Set<Integer> fileIDs = extensions.get(extension);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                extensions.remove(extension);
            }
        }
    }

    public void addFileTimestamp(String timestamp, int fileID) {
        timestamps.computeIfAbsent(timestamp, k -> new HashSet<>()).add(fileID);
    }

    public void removeFileTimestamp(String timestamp, int fileID) {
        Set<Integer> fileIDs = timestamps.get(timestamp);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                timestamps.remove(timestamp);
            }
        }
    }

    public void addFileSize(long size, int fileID) {
        sizes.computeIfAbsent(size, k -> new HashSet<>()).add(fileID);
    }

    public void removeFileSize(long size, int fileID) {
        Set<Integer> fileIDs = sizes.get(size);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                sizes.remove(size);
            }
        }
    }

    public void addFileChecksum(long checksum, int fileID) {
        checksums.computeIfAbsent(checksum, k -> new HashSet<>()).add(fileID);
    }

    public void removeFileChecksum(long checksum, int fileID) {
        Set<Integer> fileIDs = checksums.get(checksum);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                checksums.remove(checksum);
            }
        }
    }

    public void addFileKeyword(String keyword, int fileID) {
        keywords.computeIfAbsent(keyword, k -> new HashSet<>()).add(fileID);
    }

    public void removeFileKeyword(String keyword, int fileID) {
        Set<Integer> fileIDs = keywords.get(keyword);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                keywords.remove(keyword);
            }
        }
    }

    public void addFileMetadataTag(String metadataTag, int fileID) {
        metadataTags.computeIfAbsent(metadataTag, k -> new HashSet<>()).add(fileID);
    }

    public void removeFileMetadataTag(String metadataTag, int fileID) {
        Set<Integer> fileIDs = metadataTags.get(metadataTag);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                metadataTags.remove(metadataTag);
            }
        }
    }

    // Method to get the file ID which exists in all three sets
    public int getFileID(String location, String filename, String extension) {
        Set<Integer> filenameIDs = filenames.getOrDefault(filename, new HashSet<>());
        Set<Integer> locationIDs = locations.getOrDefault(location, new HashSet<>());
        Set<Integer> extensionIDs = extensions.getOrDefault(extension, new HashSet<>());

        for (int fileID : filenameIDs) {
            if (locationIDs.contains(fileID) && extensionIDs.contains(fileID)) {
                return fileID;
            }
        }
        return 0;
    }
}
