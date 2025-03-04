package cz.cuni.mff.stankoti.photo.util;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.stankoti.photo.db.DBFile;

public class FileSystem {
    public static char checkPath(String path) {
        File file = new File(path);

        if (file.exists()) {
            if (file.isFile()) {
                return 'F'; // It's a file
            } else if (file.isDirectory()) {
                return 'D'; // It's a directory
            }
        }
        return 'E'; // Path doesn't exist
    }

    public static List<String> filesInDirectory(String directory) {
        List<String> listOfFiles = new ArrayList<>();

        File dir = new File(directory);
        if (dir.isDirectory()) {
            try {
                // add the full absolute directory path as the first element in the result
                listOfFiles.add(dir.getCanonicalPath());

                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        listOfFiles.add(file.getCanonicalPath());
                    }
                }
            } catch (IOException e) {
                // System.err.println("Error resolving canonical path: " + e.getMessage());
            }
        }

        return listOfFiles;
    }

    public static DBFile getFileInformation(String filename) {
        String location = "";
        String fname = "";
        String extension = "";
        String timestamp = "";
        long size = 0L;
        long checksum = 0L;
        Set<String> keywords = new HashSet<>();
        Set<String> metadata = new HashSet<>();

        DBFile dbFile = new DBFile();

        try {
            File file = new File(filename);
            if (file.isFile()) {
                String parentPath = file.getParent(); // Absolute parent path
                location = parentPath != null ? new File(parentPath).getCanonicalPath() : "";

                String fullName = file.getName();   // Filename with extension
                int lastDotIndex = fullName.lastIndexOf('.');
                if (lastDotIndex > 0 && lastDotIndex < fullName.length() - 1) {
                    // File has an extension
                    fname = fullName.substring(0, lastDotIndex);
                    extension = fullName.substring(lastDotIndex + 1);
                } else {
                    // No extension or starts with dot
                    fname = fullName;
                }

                // HH for 24-hour format,
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss").withZone(ZoneId.systemDefault());  // Use system timezone
                timestamp = formatter.format(Instant.ofEpochMilli(file.lastModified()));

                size = file.length();
                checksum = calculateChecksum(file);
                //metadata = ...

                dbFile.setLocation(location);
                dbFile.setFilename(fname);
                dbFile.setExtension(extension);
                dbFile.setTimestamp(timestamp);
                dbFile.setSize(size);
                dbFile.setChecksum(checksum);
                dbFile.setKeywords(keywords);
                dbFile.setMetadata(metadata);
            }
        } catch (IOException e) {
            // System.err.println("Error resolving canonical path: " + e.getMessage());
        }

        return dbFile;
    }

    public static long calculateChecksum(File file) {
        return 0L;
    }
}