package cz.cuni.mff.stankoti.photo.db;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DB {
    public static void WriteDB(ArrayList<File> files, String dbFilename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dbFilename))) {
            out.writeObject(files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<File> ReadDB(String dbFilename) {
        ArrayList<File> files = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dbFilename))) {
            files = (ArrayList<File>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return files;
    }    
}
