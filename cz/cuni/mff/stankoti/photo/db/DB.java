package cz.cuni.mff.stankoti.photo.db;

import cz.cuni.mff.stankoti.photo.status.StatusCode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int addFile(DBFile file) {
        int oldFileID = data.getFileID(file.getLocation(), file.getFilename(), file.getExtension());

        if (oldFileID != 0) {
            removeFile(oldFileID);
        }

        data.addFile(file);
        int fileID = file.getID();
        data.addFileLocation(file.getLocation(), fileID);
        data.addFileFilename(file.getFilename(), fileID);
        data.addFileExtension(file.getExtension(), fileID);
        data.addFileTimestamp(file.getTimestamp(), fileID);
        data.addFileSize(file.getSize(), fileID);
        data.addFileChecksum(file.getChecksum(), fileID);
        for (String keyword : file.getKeywords()) {
            data.addFileKeyword(keyword, fileID);
        }
        for (String metadataString : file.getMetadata()) {
            data.addFileKeyword(metadataString, fileID);
        }
    
        dataChanged(true);

        return oldFileID;
    }

    public void removeFile(int fileID) {
        DBFile file = data.getFile(fileID);
        data.removeFile(fileID);
        data.removeFileLocation(file.getLocation(), fileID);
        data.removeFileFilename(file.getFilename(), fileID);
        data.removeFileExtension(file.getExtension(), fileID);
        data.removeFileTimestamp(file.getTimestamp(), fileID);
        data.removeFileSize(file.getSize(), fileID);
        data.removeFileChecksum(file.getChecksum(), fileID);
        for (String keyword : file.getKeywords()) {
            data.removeFileKeyword(keyword, fileID);
        }
        for (String metadataString : file.getMetadata()) {
            data.removeFileKeyword(metadataString, fileID);
        }

        dataChanged(true);
    }

    public int nextFileID() {
        return data.nextFileID();
    }
}
