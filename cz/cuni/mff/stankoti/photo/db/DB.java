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
    private transient boolean saved = false;
    private StatusCode statusCode;

    public DB(String dbFilename) {
        setStatusCode(StatusCode.NO_ERROR);
        data = new DBData();
        this.dbFilename = dbFilename;
        ReadDB();
    }

    public boolean getSaved() {
        return saved;
    }
    
    public void setSaved(boolean saved) {
        this.saved = saved;
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
            setSaved(true);
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
            setSaved(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFile(File file) {
        data.addFile(file);
        setSaved(false);
    }

    public void removeFile(int fileId) {
        data.removeFile(fileId);
        setSaved(false);
    }

    public int nextFileID() {
        return data.nextFileID();
    }
}
