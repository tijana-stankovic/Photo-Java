package cz.cuni.mff.stankoti.photo.db;

import cz.cuni.mff.stankoti.photo.status.StatusCode;
import cz.cuni.mff.stankoti.photo.util.MetadataInfo;
import cz.cuni.mff.stankoti.photo.util.FileSystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
  * The top-level class of the Model.
  * <p>
  * It provides methods for accessing the Photo database as a whole.
  * </p>
  */
public class DB {
    /**
      * Default file name from which the program reads database data.
      */
    public static final String DEFAULT_DB_FILENAME = "photo_db.pdb";

    /**
      * A database that contains information about all files, as well as index structures for quick access and searching.
      */
    private DBData data;

    /**
      * Current filename where the program saves database data.
      */
    private String dbFilename;
    /**
      * A flag indicating whether the memory database data has changed compared to the last saved state.
      */
    private transient boolean dataChanged = true;
    /**
      * Status code of the last executed DB operation.
      */
    private StatusCode statusCode;

    /**
      * Creates a new DB instance based on data in the specified database filename.
      * Initializes the database and reads the data from the database file.
      *
      * @param dbFilename the name of the database file
      */
    public DB(String dbFilename) {
        setStatusCode(StatusCode.NO_ERROR);
        setDbFilename(dbFilename);
        data = new DBData();
        ReadDB();
    }

    /**
      * Return the database data 'changed' status (changed or not).
      *
      * @return true if the database data has been changed, false otherwise
      */
    public boolean isChanged() {
        return dataChanged;
    }

    /**
      * Return the database data 'saved' status (saved or not).
      *
      * @return true if the data has been saved, false otherwise
      */
    public boolean isSaved() {
        return !dataChanged;
    }

    /**
      * Sets the database data 'changed' status.
      *
      * @param dataChanged the new database data 'changed' status
      */
    public void dataChanged(boolean dataChanged) {
        this.dataChanged = dataChanged;
    }

    /**
      * Sets the database data 'saved' status.
      *
      * @param dataSaved the new database data 'saved' status
      */
    public void dataSaved(boolean dataSaved) {
        dataChanged = !dataSaved;
    }

    /**
      * Gets the current database filename.
      *
      * @return the database filename
      */
    public String getDbFilename() {
        return dbFilename;
    }

    /**
      * Sets the database filename (but, the database data will be saved on next SAVE command).
      * If the new filename is different from the current one, marks the database data as changed.
      *
      * @param dbFilename the new database filename
      */
    public void setDbFilename(String dbFilename) {
        assert dbFilename != null && !dbFilename.isEmpty() : "DB filename must be specified!";
        if (this.dbFilename == null || !this.dbFilename.equals(dbFilename)) {
            this.dbFilename = dbFilename;
            dataChanged(true);
        }
    }

    /**
      * Gets the last DB operation status code.
      *
      * @return the last DB operation status code
      */
    public StatusCode getStatusCode() {
        return statusCode;
    }

    /**
      * Sets a DB operation status code.
      *
      * @param statusCode the DB operation status code
      */
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    /**
      * Reads the database data from the external database file.
      */
    public void ReadDB() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dbFilename))) {
            data = (DBData) in.readObject();
            dataChanged(false);
            setStatusCode(StatusCode.NO_ERROR);
        } catch (FileNotFoundException e) { // File not found
            setStatusCode(StatusCode.DB_FILE_DOES_NOT_EXIST);
        } catch (ClassNotFoundException | InvalidClassException e ) { // Class not found or Class version mismatch
            setStatusCode(StatusCode.DB_FILE_INCOMPATIBLE_FORMAT);
        } catch (IOException e) { // Read error
            setStatusCode(StatusCode.DB_FILE_READ_ERROR);
            // e.printStackTrace();
        }    
    }

    /**
      * Writes the database data to the external database file.
      */
    public void WriteDB() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dbFilename))) {
            out.writeObject(data);
            dataSaved(true);
            setStatusCode(StatusCode.NO_ERROR);
        } catch (NotSerializableException e) {
            setStatusCode(StatusCode.DB_FILE_NOT_SERIALIZABLE);
        } catch (IOException e) {
            setStatusCode(StatusCode.DB_FILE_WRITE_ERROR);
            //e.printStackTrace();
        }
    }

    /**
      * Adds a file object (with image information) to the database.
      * If the image information already exists, updates them.
      *
      * @param file the file object to add
      * @return the ID of the old (updated) file object if it existed, 0 otherwise
      */
    public int addFile(DBFile file) {
        Set<String> keywords = null;
        int oldFileID = data.getFileID(file.getFullpath());

        if (oldFileID != 0) {
            file.setID(oldFileID);
            keywords = this.getFile(oldFileID).getKeywords();
            removeFile(oldFileID);
        } else {
            file.setID(this.nextFileID());
        }

        data.addFile(file);
        int fileID = file.getID();
        data.addFilePath(file.getFullpath(), fileID);
        data.addFileLocation(file.getLocation(), fileID);
        data.addFileFilename(file.getFilename(), fileID);
        data.addFileExtension(file.getExtension(), fileID);
        data.addFileTimestamp(file.getTimestamp(), fileID);
        data.addFileSize(file.getSize(), fileID);
        data.addFileChecksum(file.getChecksum(), fileID);
        if (oldFileID != 0) {
            for (String keyword : keywords) {
                this.addKeyword(keyword, fileID);
            }
        }
        for (MetadataInfo metadataInfo : file.getMetadata()) {
            data.addFileMetadataTag(metadataInfo.getTag(), fileID);
        }
        
        for (int potentialDuplicateFileID : data.findPotentialDuplicatesIDs(file.getSize(), file.getChecksum())) {
            if (potentialDuplicateFileID != fileID) {
                file.addPotentialDuplicate(potentialDuplicateFileID);
                DBFile potentialDuplicateFile = data.getFile(potentialDuplicateFileID);
                potentialDuplicateFile.addPotentialDuplicate(fileID);
                data.addPotentialDuplicate(fileID);
                data.addPotentialDuplicate(potentialDuplicateFileID);
                addKeyword("DUP?", fileID);
                addKeyword("DUP?", potentialDuplicateFileID);
            }
        }
    
        dataChanged(true);

        return oldFileID;
    }

    /**
      * Removes a file object (with image information) from the database.
      *
      * @param fileID the ID of the file object to remove
      */
    public void removeFile(int fileID) {
        DBFile file = data.getFile(fileID);
        data.removeFile(fileID);
        data.removeFilePath(file.getFullpath(), fileID);
        data.removeFileLocation(file.getLocation(), fileID);
        data.removeFileFilename(file.getFilename(), fileID);
        data.removeFileExtension(file.getExtension(), fileID);
        data.removeFileTimestamp(file.getTimestamp(), fileID);
        data.removeFileSize(file.getSize(), fileID);
        data.removeFileChecksum(file.getChecksum(), fileID);
        for (String keyword : file.getKeywords()) {
            data.removeFileKeyword(keyword, fileID);
        }
        for (MetadataInfo metadataInfo : file.getMetadata()) {
            data.removeFileMetadataTag(metadataInfo.getTag(), fileID);
        }

        removeFileDuplicateInformation(file);

        dataChanged(true);
    }

    /**
      * Removes all information about duplicates and potential duplicates connected with the specified file object.
      *
      * @param file the file object whose duplicate information to remove
      */
    public void removeFileDuplicateInformation(DBFile file) {
        int fileID = file.getID();
        
        for (int duplicateFileID : file.getDuplicates()) {
            DBFile duplicateFile = data.getFile(duplicateFileID);
            duplicateFile.removeDuplicate(fileID);
            if (duplicateFile.getDuplicates().isEmpty()) {
                data.removeDuplicate(duplicateFileID);
                removeKeyword("DUP", duplicateFileID);
            }
        }
        for (int potentialDuplicateFileID : file.getPotentialDuplicates()) {
            DBFile potentialDuplicateFile = data.getFile(potentialDuplicateFileID);
            potentialDuplicateFile.removePotentialDuplicate(fileID);
            if (potentialDuplicateFile.getPotentialDuplicates().isEmpty()) {
                data.removePotentialDuplicate(potentialDuplicateFileID);
                removeKeyword("DUP?", potentialDuplicateFileID);
            }
        }

        file.setDuplicates(null);
        file.setPotentialDuplicates(null);
        data.removeDuplicate(fileID);
        removeKeyword("DUP", fileID);
        data.removePotentialDuplicate(fileID);
        removeKeyword("DUP?", fileID);

        dataChanged(true);
    }

    /**
      * By comparing the contents of the files, it looks for duplicates of the file whose ID is specified.
      * Based on the set of found duplicates, it updates information about duplicates and 
      * potential duplicates for all files in that set.
      *
      * @param fileID the ID of the file object to check for duplicates
      * @return a map of file objects IDs and the number of duplicates found for each
      */
    public Map<Integer, Integer> processDuplicates(int fileID) {
        Set<Integer> duplicatesIDs = new HashSet<>();
        duplicatesIDs.add(fileID);
        DBFile file = data.getFile(fileID);
        for (int duplicateFileID : data.findPotentialDuplicatesIDs(file.getSize(), file.getChecksum())) {
            if (duplicateFileID != fileID) {
                DBFile duplicateFile = data.getFile(duplicateFileID);
                if (FileSystem.compareFiles(file.getFullpath(), duplicateFile.getFullpath())) {
                    duplicatesIDs.add(duplicateFileID);
                }
            }
        }

        Map<Integer, Integer> duplicatesFound = new HashMap<>();
        int numOfDuplicates = duplicatesIDs.size() - 1;
        if (numOfDuplicates > 0) {
            for (int fID : duplicatesIDs) {
                removeFileDuplicateInformation(data.getFile(fID));
                duplicatesFound.put(fID, numOfDuplicates);
            }
            for (int fID : duplicatesIDs) {
                file = data.getFile(fID);
                for (int fDupID : duplicatesIDs) {
                    if (fID != fDupID) {
                        file.addDuplicate(fDupID);
                        addKeyword("DUP", fID);
                        data.addDuplicate(fID);
                    }
                }
            }
        } else {
            removeFileDuplicateInformation(file);
        }

        dataChanged(true);

        return duplicatesFound;
    }

    /**
      * Return the next file ID.
      *
      * @return the next file ID
      */
    public int nextFileID() {
        return data.nextFileID();
    }

    /**
      * Gets the ID of the file with the specified full file path.
      *
      * @param fullpath the full path of the file
      * @return the file ID, or 0 if not found
      */
    public int getFileID(String fullpath) {
        return data.getFileID(fullpath);
    }

    /**
      * Gets the ID of the file with the specified combination location (directory) + filename + extension.
      *
      * @param location the location (directory) of the file
      * @param filename the filename of the file
      * @param extension the extension of the file
      * @return the file ID, or 0 if not found
      */
    public int getFileID(String location, String filename, String extension) {
        return data.getFileID(location, filename, extension);
    }

    /**
      * Gets the file object associated with the specified file ID.
      *
      * @param fileID the ID of the file object to retrieve
      * @return the file object associated with the specified ID, or null if not found
      */
    public DBFile getFile(int fileID) {
        return data.getFile(fileID);
    }

    /**
      * Gets the set of file objects IDs based on the specified key and 'location'.
      *
      * @param key the key to search for (can be full filename path or directory path or keyword)
      * @param where the 'location' to search ('F' for filename full path, 'D' for directory, 'K' for keyword)
      * @return a set of file IDs matching the key and location
      */
    public Set<Integer> getFileIDs(String key, char where) {
        Set<Integer> fileIDs = null;
        switch (Character.toUpperCase(where)) {
            case 'F' -> {
                int fileID = data.getFileID(key);
                if (fileID != 0) {
                    fileIDs = new HashSet<>();
                    fileIDs.add(fileID);
                }
            }
            case 'D' -> fileIDs = data.getFileIDsInLocation(key);
            case 'K' -> fileIDs = data.getFileIDsWithKeyword(key);
            default -> { assert false : "Method DB.getFileIDs() - Invalid 'where' parameter value!"; }
        }
        return fileIDs;
    }

    /**
      * Connects keyword and file object ID.
      *
      * @param keyword the keyword to connect with file object
      * @param fileID the ID of the file object
      */
    public void addKeyword(String keyword, int fileID) {
        DBFile file = data.getFile(fileID);
        if (file != null) {
            file.addKeyword(keyword);
            data.addFileKeyword(keyword, fileID);
            dataChanged(true);
        }
    }

    /**
      * Disconnects keyword from file object ID.
      *
      * @param keyword the keyword to disconnect from file object
      * @param fileID the ID of the file object
      */
    public void removeKeyword(String keyword, int fileID) {
        DBFile file = data.getFile(fileID);
        if (file != null) {
            file.removeKeyword(keyword);
            data.removeFileKeyword(keyword, fileID);
            dataChanged(true);
        }
    }

    /**
      * Gets a sorted list of all keywords in the database.
      *
      * @return a sorted list of keywords
      */
    public List<String> getKeywords() {
        return data.getKeywords();
    }

    /**
      * Gets a sorted list of all directories in the database.
      *
      * @return a sorted list of directories
      */
    public List<String> getDirectories() {
        return data.getDirectories();
    }

    /**
      * Gets statistics about the database.
      *
      * @return a map containing statistics about the database
      */
    public Map<String, Integer> getDBStatistics() {
        return data.getDBStatistics();
    }
}
