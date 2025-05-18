package cz.cuni.mff.stankoti.photo.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
  * This class contains the internal representation of the Photo database.
  * <p>
  * It contains the internal structure and provides methods for manipulating data (files, folders, keywords, etc.) in the database.
  * </p>
  */
public class DBData implements Serializable {  // implement the Serializable interface
    /**
      * Unique identifier for the serialized class version.
      */
    private static final long serialVersionUID = 1L;
    /**
      * Last used ID of the file object.
      */
    private int lastFileID;
    /**
      * File object ID index.
      * Mapping the file object ID to the corresponding file object in the database.
      */
    private Map<Integer, DBFile> files;
    /**
      * Absolute filename path index.
      * Mapping (1:1) the absolute filename path (including filename) to the ID of corresponding file object in the database.
      */
    private Map<String, Integer> fullpaths;
    /**
      * Location (directory) index.
      * Mapping the directory to the set of files located in that directory.
      */
    private Map<String, Set<Integer>> locations;
    /**
      * Filename index.
      * Mapping the filename to the set of files with that filename.
      */
    private Map<String, Set<Integer>> filenames;
    /**
      * File extension index.
      * Mapping the extension to the set of files with that extension.
      */
    private Map<String, Set<Integer>> extensions;
    /**
      * Timestamp index.
      * Mapping the timestamp to the set of files with that timestamp.
      */
    private Map<String, Set<Integer>> timestamps;
    /**
      * Size index.
      * Mapping the file size to the set of files with that size.
      */
    private Map<Long, Set<Integer>> sizes;
    /**
      * Checksum index.
      * Mapping checksum values to the set of files that have that checksum.
      */
    private Map<Long, Set<Integer>> checksums;
    /**
      * Keyword index.
      * Mapping the keyword to the set of files associated with that keyword.
      */
    private Map<String, Set<Integer>> keywords;
    /**
      * Metadata tag index.
      * Mapping the metadata tag to the set of files containing that tag.
      */
    private Map<String, Set<Integer>> metadataTags;
    /**
      * Duplicates index.
      * Set of all file object IDs with at least one duplicate
      */
    private Set<Integer> duplicates;
    /**
      * Duplicates index.
      * Set of all file object IDs with at least one potential duplicate
      */
    private Set<Integer> potentialDuplicates;

    /**
      * Default constructor for database initialization.
      */
    public DBData() {
        lastFileID = 0;
        files = new HashMap<>();
        fullpaths = new HashMap<>();
        locations = new HashMap<>();
        filenames = new HashMap<>();
        extensions = new HashMap<>();
        timestamps = new HashMap<>();
        sizes = new HashMap<>();
        checksums = new HashMap<>();
        keywords = new HashMap<>();
        metadataTags = new HashMap<>();
        duplicates = new HashSet<>();
        potentialDuplicates = new HashSet<>();
    }

    /**
      * Gets the last ID assigned to a file in the database.
      *
      * @return the last ID of the file
      */
    public int getLastFileID() {
        return lastFileID;
    }

    /**
      * Sets the last ID assigned to a file in the database.
      *
      * @param newID the new last ID of the file
      */
    public void setLastFileID(int newID) {
        lastFileID = newID;
    }

    /**
      * Generates the next file ID by incrementing the last file ID.
      *
      * @return the next file ID
      */
    public int nextFileID() {
        setLastFileID(getLastFileID() + 1);
        return getLastFileID();
    }

    /**
      * Retrieves a sorted list of all keywords in the database.
      *
      * @return a sorted list of keywords
      */
    public List<String> getKeywords() {
        List<String> keys = new ArrayList<>(keywords.keySet());
        Collections.sort(keys);
        return keys;
    }

    /**
      * Retrieves a sorted list of all directories in the database.
      *
      * @return a sorted list of directories
      */
    public List<String> getDirectories() {
        List<String> dirs = new ArrayList<>(locations.keySet());
        Collections.sort(dirs);
        return dirs;
    }

    /**
      * Gets the file object associated with the specified file ID.
      *
      * @param fileID the ID of the file object to retrieve
      * @return the file object associated with the specified ID, or null if not found
      */
    public DBFile getFile(int fileID) {
        return files.getOrDefault(fileID, null);
    }

    /**
      * Adds a file object to the database.
      *
      * @param file the file object to add
      */
    public void addFile(DBFile file) {
        files.put(file.getID(), file);
    }

    /**
      * Removes a file object from the database.
      *
      * @param fileID the ID of the file object to remove
      */
    public void removeFile(int fileID) {
        files.remove(fileID);
    }

    /**
      * Connects the specified filename path with the file object ID.
      *
      * @param fullpath the full path of the file
      * @param fileID the ID of the file object
      */
    public void addFilePath(String fullpath, int fileID) {
        assert fullpath != null && !fullpath.isEmpty() : "Path must be specified!";
        fullpaths.put(fullpath, fileID);
    }

    /**
      * Disconnects the specified filename path from the file object ID.
      *
      * @param fullpath the full path of the file
      * @param fileID the ID of the file object
      */
    public void removeFilePath(String fullpath, int fileID) {
        fullpaths.remove(fullpath);
    }

    /**
      * Adds a file object ID to the specified file location (directory).
      *
      * @param location the location (directory) of the file
      * @param fileID the ID of the file object
      */
    public void addFileLocation(String location, int fileID) {
        assert location != null && !location.isEmpty() : "File location must be specified!";
        locations.computeIfAbsent(location, k -> new HashSet<>()).add(fileID);
    }

    /**
      * Removes a file object ID from the specified file location (directory).
      *
      * @param location the location (directory) of the file
      * @param fileID the ID of the file object
      */
    public void removeFileLocation(String location, int fileID) {
        Set<Integer> fileIDs = locations.get(location);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                locations.remove(location);
            }
        }
    }

    /**
      * Adds a file object ID to the specified filename.
      *
      * @param filename the filename of the file
      * @param fileID the ID of the file object
      */
    public void addFileFilename(String filename, int fileID) {
        assert filename != null && !filename.isEmpty() : "Filename must be specified!";
        filenames.computeIfAbsent(filename, k -> new HashSet<>()).add(fileID);
    }

    /**
      * Removes a file object ID from the specified filename.
      *
      * @param filename the filename of the file
      * @param fileID the ID of the file object
      */
    public void removeFileFilename(String filename, int fileID) {
        Set<Integer> fileIDs = filenames.get(filename);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                filenames.remove(filename);
            }
        }
    }

    /**
      * Adds a file object ID to the specified file extension.
      *
      * @param extension the extension of the file
      * @param fileID the ID of the file object
      */
    public void addFileExtension(String extension, int fileID) {
        assert extension != null && !extension.isEmpty() : "File extension must be specified!";
        extensions.computeIfAbsent(extension, k -> new HashSet<>()).add(fileID);
    }

    /**
      * Removes a file object ID from the specified file extension.
      *
      * @param extension the extension of the file
      * @param fileID the ID of the file object
      */
    public void removeFileExtension(String extension, int fileID) {
        Set<Integer> fileIDs = extensions.get(extension);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                extensions.remove(extension);
            }
        }
    }

    /**
      * Adds a file object ID to the specified timestamp.
      *
      * @param timestamp the timestamp of the file
      * @param fileID the ID of the file object
      */
    public void addFileTimestamp(String timestamp, int fileID) {
        assert timestamp != null && !timestamp.isEmpty() : "File timestamp must be specified!";
        timestamps.computeIfAbsent(timestamp, k -> new HashSet<>()).add(fileID);
    }

    /**
      * Removes a file object ID from the specified timestamp.
      *
      * @param timestamp the timestamp of the file
      * @param fileID the ID of the file object
      */
    public void removeFileTimestamp(String timestamp, int fileID) {
        Set<Integer> fileIDs = timestamps.get(timestamp);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                timestamps.remove(timestamp);
            }
        }
    }

    /**
      * Adds a file object ID to the specified file size.
      *
      * @param size the size of the file
      * @param fileID the ID of the file object
      */
    public void addFileSize(long size, int fileID) {
        assert size >= 0 : "Size must not be negative!";
        sizes.computeIfAbsent(size, k -> new HashSet<>()).add(fileID);
    }

    /**
      * Removes a file object ID from the specified file size.
      *
      * @param size the size of the file
      * @param fileID the ID of the file object
      */
    public void removeFileSize(long size, int fileID) {
        Set<Integer> fileIDs = sizes.get(size);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                sizes.remove(size);
            }
        }
    }

    /**
      * Adds a file object ID to the specified checksum.
      *
      * @param checksum the checksum of the file
      * @param fileID the ID of the file object
      */
    public void addFileChecksum(long checksum, int fileID) {
        checksums.computeIfAbsent(checksum, k -> new HashSet<>()).add(fileID);
    }

    /**
      * Removes a file object ID from the specified checksum.
      *
      * @param checksum the checksum of the file
      * @param fileID the ID of the file object
      */
    public void removeFileChecksum(long checksum, int fileID) {
        Set<Integer> fileIDs = checksums.get(checksum);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                checksums.remove(checksum);
            }
        }
    }

    /**
      * Adds a file object ID to the specified keyword.
      *
      * @param keyword the keyword
      * @param fileID the ID of the file object
      */
    public void addFileKeyword(String keyword, int fileID) {
        assert keyword != null && !keyword.isEmpty() : "Keyword must be specified!";
        keywords.computeIfAbsent(keyword.toUpperCase(), k -> new HashSet<>()).add(fileID);
    }

    /**
      * Removes a file object ID from the specified keyword.
      *
      * @param keyword the keyword
      * @param fileID the ID of the file object
      */
    public void removeFileKeyword(String keyword, int fileID) {
        Set<Integer> fileIDs = keywords.get(keyword.toUpperCase());
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                keywords.remove(keyword.toUpperCase());
            }
        }
    }

    /**
      * Adds a file object ID to the specified metadata tag name.
      *
      * @param metadataTag the tag name
      * @param fileID the ID of the file object
      */
    public void addFileMetadataTag(String metadataTag, int fileID) {
        assert metadataTag != null && !metadataTag.isEmpty() : "Metadata tag must be specified!";
        metadataTags.computeIfAbsent(metadataTag, k -> new HashSet<>()).add(fileID);
    }

    /**
      * Removes a file object ID from the specified metadata tag name.
      *
      * @param metadataTag the tag name
      * @param fileID the ID of the file object
      */
    public void removeFileMetadataTag(String metadataTag, int fileID) {
        Set<Integer> fileIDs = metadataTags.get(metadataTag);
        if (fileIDs != null) {
            fileIDs.remove(fileID);
            if (fileIDs.isEmpty()) {
                metadataTags.remove(metadataTag);
            }
        }
    }

    /**
      * Gets the ID of the file with the specified full file path.
      *
      * @param fullpath the full path of the file
      * @return the file ID, or 0 if not found
      */
    public int getFileID(String fullpath) {
        Integer fileID = fullpaths.get(fullpath);
        if (fileID == null) {
            fileID = 0;
        }
        return fileID;
    }

    /**
      * Gets the file ID which exists in all three sets (location (dir), filename, and extension).
      *
      * @param location the location (directory) of the file
      * @param filename the filename of the file
      * @param extension the extension of the file
      * @return the file ID, or 0 if not found
      */
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

    /**
      * Finds IDs of all files with the specified size and checksum.
      *
      * @param size the size of the file
      * @param checksum the checksum of the file
      * @return a set of file IDs matching the specified size and checksum
      */
    public Set<Integer> findPotentialDuplicatesIDs(long size, long checksum) {
        Set<Integer> foundIDs = new HashSet<>();
        Set<Integer> sizeIDs = sizes.getOrDefault(size, new HashSet<>());
        Set<Integer> checksumIDs = checksums.getOrDefault(checksum, new HashSet<>());

        for (int fileID : sizeIDs) {
            if (checksumIDs.contains(fileID)) {
                foundIDs.add(fileID);
            }
        }

        return foundIDs;
    }

    /**
      * Gets IDs of all files in the specified location (directory).
      *
      * @param location the location (directory) of the files
      * @return a set of file IDs in the specified location
      */
    public Set<Integer> getFileIDsInLocation(String location) {
        Set<Integer> fileIDs = locations.get(location);
        return fileIDs;
    }

    /**
      * Gets IDs of all files with the specified keyword.
      *
      * @param keyword the keyword associated with the files
      * @return a set of file IDs with the specified keyword
      */
    public Set<Integer> getFileIDsWithKeyword(String keyword) {
        Set<Integer> fileIDs = keywords.get(keyword);
        return fileIDs;
    }

    /**
      * Adds a file ID to the list of duplicates.
      *
      * @param duplicateFileID the ID of the file
      */
    public void addDuplicate(Integer duplicateFileID) {
        assert duplicateFileID > 0 : "Duplicate file ID must be positive!";
        duplicates.add(duplicateFileID);
    }

    /**
      * Removes a file ID from the list of duplicates.
      *
      * @param duplicateFileID the ID of the file to remove
      */
    public void removeDuplicate(Integer duplicateFileID) {
        duplicates.remove(duplicateFileID);
    }

    /**
      * Adds a file ID to the list of potential duplicates.
      *
      * @param potentialDuplicateFileID the ID of the file
      */
    public void addPotentialDuplicate(Integer potentialDuplicateFileID) {
        assert potentialDuplicateFileID > 0 : "Potential duplicate file ID must be positive!";
        potentialDuplicates.add(potentialDuplicateFileID);
    }

    /**
      * Removes a file ID from the list of potential duplicates.
      *
      * @param potentialDuplicateFileID the ID of the file to remove
      */
    public void removePotentialDuplicate(Integer potentialDuplicateFileID) {
        potentialDuplicates.remove(potentialDuplicateFileID);
    }

    /**
      * Gets statistics about the database.
      *
      * @return a map containing statistics about the database
      */
    public Map<String, Integer> getDBStatistics() {
        Map<String, Integer> dbStatistics = new HashMap<>();
        dbStatistics.put("FILES", files.size());
        dbStatistics.put("DIRS", locations.size());
        dbStatistics.put("KEYS", keywords.size());
        dbStatistics.put("DUPS", duplicates.size());
        dbStatistics.put("DUP?S", potentialDuplicates.size());
        return dbStatistics;
    }
}
