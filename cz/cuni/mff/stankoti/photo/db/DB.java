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
import java.util.Map;
import java.util.Set;

public class DB {
    public static final String DEFAULT_DB_FILENAME = "photo_db.pdb";

    private DBData data;

    private String dbFilename;
    private transient boolean dataChanged = true;
    private StatusCode statusCode;

    public DB(String dbFilename) {
        setStatusCode(StatusCode.NO_ERROR);
        data = new DBData();
        this.dbFilename = dbFilename;
        ReadDB();
    }

    public boolean isChanged() {
        return dataChanged;
    }

    public boolean isSaved() {
        return !dataChanged;
    }

    public void dataChanged(boolean dataChanged) {
        this.dataChanged = dataChanged;
    }

    public void dataSaved(boolean dataSaved) {
        dataChanged = !dataSaved;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

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

    public int nextFileID() {
        return data.nextFileID();
    }

    public int getFileID(String fullpath) {
        return data.getFileID(fullpath);
    }

    public int getFileID(String location, String filename, String extension) {
        return data.getFileID(location, filename, extension);
    }

    public DBFile getFile(int fileID) {
        return data.getFile(fileID);
    }

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

    public void addKeyword(String keyword, int fileID) {
        DBFile file = data.getFile(fileID);
        if (file != null) {
            file.addKeyword(keyword);
            data.addFileKeyword(keyword, fileID);
            dataChanged(true);
        }
    }

    public void removeKeyword(String keyword, int fileID) {
        DBFile file = data.getFile(fileID);
        if (file != null) {
            file.removeKeyword(keyword);
            data.removeFileKeyword(keyword, fileID);
            dataChanged(true);
        }
    }
}
