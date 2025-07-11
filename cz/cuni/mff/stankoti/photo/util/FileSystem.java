package cz.cuni.mff.stankoti.photo.util;

import cz.cuni.mff.stankoti.photo.db.DBFile;
import cz.cuni.mff.stankoti.photo.status.StatusCode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.CRC32;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

/**
  * A class with methods for file system access and reading directories, files, and metadata information.
  */
public class FileSystem {
    /**
      * Status code of the last executed File System operation.
      */
    private static StatusCode statusCode;
    
    /**
      * Gets the last FileSystem operation status code.
      *
      * @return the last FileSystem operation status code
      */
    public static StatusCode getStatusCode() {
        return statusCode;
    }

    /**
      * Sets a FileSystem operation status code.
      *
      * @param newStatusCode the FileSystem operation status code
      */
    public static void setStatusCode(StatusCode newStatusCode) {
        statusCode = newStatusCode;
    }

    /**
      * Default constructor.
      * (defined to prevent Javadoc warning)
      */
    public FileSystem() {}
        
    /**
      * Checks the type of the given path.
      *
      * @param path the path to check
      * @return 'F' if it's a file, 'D' if it's a directory, 'E' if it doesn't exist
      */
    public static char checkPath(String path) {
        File file = new File(path);

        if (file.exists()) {
            if (file.isFile()) {
                return 'F'; // it's a file
            } else if (file.isDirectory()) {
                return 'D'; // it's a directory
            }
        }
        return 'E'; // path doesn't exist
    }

    /**
      * Gets a list of all files in the given directory.
      *
      * @param directory the directory path
      * @return a list of file paths in the directory (first element in the list is the directory path itself)
      */
    public static List<String> filesInDirectory(String directory) {
        List<String> listOfFiles = new ArrayList<>();

        setStatusCode(StatusCode.NO_ERROR);

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
                setStatusCode(StatusCode.FILE_SYSTEM_ERROR);
            }
        }

        return listOfFiles;
    }


    /**
      * Gets detailed information about a file.
      *
      * @param filename the name of the file to get information about
      * @return a DBFile object containing the file information
      */
    public static DBFile getFileInformation(String filename) {
        String fullpath = "";
        String location = "";
        String fname = "";
        String extension = "";
        String timestamp = "";
        long size = 0L;
        long checksum = 0L;
        Set<String> keywords = new HashSet<>();
        Set<MetadataInfo> metadata = new HashSet<>();

        setStatusCode(StatusCode.NO_ERROR);

        DBFile dbFile = new DBFile();

        try {
            File file = new File(filename);
            if (file.isFile()) {
                fullpath = file.getCanonicalPath(); // full filepath

                String parentPath = file.getParent(); // absolute parent path
                location = parentPath != null ? new File(parentPath).getCanonicalPath() : "";

                String fullName = file.getName();   // filename with extension
                int lastDotIndex = fullName.lastIndexOf('.');
                if (lastDotIndex > 0 && lastDotIndex < fullName.length() - 1) {
                    // file has an extension
                    fname = fullName.substring(0, lastDotIndex);
                    extension = fullName.substring(lastDotIndex + 1);
                } else {
                    // no extension or starts with dot
                    fname = fullName;
                }

                dbFile.setFullpath(fullpath);
                dbFile.setLocation(location);
                dbFile.setFilename(fname);
                dbFile.setExtension(extension);

                // HH for 24-hour format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss").withZone(ZoneId.systemDefault());  // use system timezone
                timestamp = formatter.format(Instant.ofEpochMilli(file.lastModified()));

                size = file.length();
                checksum = calculateChecksum(file);
                metadata = readMetadata(file);

                dbFile.setTimestamp(timestamp);
                dbFile.setSize(size);
                dbFile.setChecksum(checksum);
                dbFile.setKeywords(keywords);
                dbFile.setMetadata(metadata);
            } else {
                setStatusCode(StatusCode.FILE_SYSTEM_NOT_FILE);
            }
        } catch (IOException e) {
            setStatusCode(StatusCode.FILE_SYSTEM_ERROR);
        }               

        return dbFile;
    }

    /**
      * Calculates the checksum of a file using CRC32.
      * <p>
      * Based on:
      * <ul>
      *     <li>https://stackoverflow.com/questions/7776069/confirming-file-content-against-hash</li>
      *     <li>https://stackoverflow.com/questions/75138079/how-to-calculate-checksum-with-inputstream-and-then-use-it-again?utm_source=chatgpt.com</li>
      * </ul>
      *
      * @param file the file to calculate the checksum for
      * @return the checksum value
      */
    public static long calculateChecksum(File file) {
        setStatusCode(StatusCode.NO_ERROR);
        CRC32 crc = new CRC32();
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                crc.update(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            setStatusCode(StatusCode.FILE_SYSTEM_ERROR);
        }
        return crc.getValue();
    }

    /**
      * Extracts the filename from a given path.
      *
      * @param filename the full path of the file
      * @return the extracted filename
      */
    public static String extractFilename(String filename) {
        // find the last occurrence of the file separator
        int lastSeparatorIndex = filename.lastIndexOf('/');
        if (lastSeparatorIndex == -1) {
            lastSeparatorIndex = filename.lastIndexOf('\\');
        }
    
        // extract the filename
        String filenameOnly = (lastSeparatorIndex == -1) ? filename : filename.substring(lastSeparatorIndex + 1);
    
        return filenameOnly;
    }

    /**
      * Formats the file size in a human-readable format.
      *
      * @param sizeInBytes the size of the file in bytes
      * @return the formatted file size
      */
    public static String formatFileSize(long sizeInBytes) {
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        double size = sizeInBytes;
        int unitIndex = 0;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    /**
      * Reads all metadata information from a file.
      *
      * @param file the file to read metadata from
      * @return a set of MetadataInfo objects containing the metadata information
      */
    public static Set<MetadataInfo> readMetadata(File file) {
        setStatusCode(StatusCode.NO_ERROR);
        Set<MetadataInfo> metadataSet = new HashSet<>();

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            if (metadata != null) {
                for (Directory directory : metadata.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        metadataSet.add(new MetadataInfo(directory.getName(), tag.getTagName(), tag.getDescription()));
                    }
                }
            } else {
                setStatusCode(StatusCode.FILE_SYSTEM_NOT_IMAGE);
            }
        } catch (ImageProcessingException e) {
            setStatusCode(StatusCode.FILE_SYSTEM_NOT_IMAGE);
        } catch (IOException e) {
            setStatusCode(StatusCode.FILE_SYSTEM_ERROR);
        }

        return metadataSet;
    }   

    /**
      * Compares two files to check if they are identical.
      *
      * @param path1 the path of the first file
      * @param path2 the path of the second file
      * @return true if the files are identical, false otherwise
      */
    public static boolean compareFiles(String path1, String path2) {
        setStatusCode(StatusCode.NO_ERROR);

        try {
            File file1 = new File(path1);
            File file2 = new File(path2);

            if (!file1.isFile() || !file2.isFile()) {
                return false;
            }

            if (file1.length() != file2.length()) {
                return false;
            }

            try (FileInputStream stream1 = new FileInputStream(file1);
                FileInputStream stream2 = new FileInputStream(file2)) {

                byte[] buffer1 = new byte[1024];
                byte[] buffer2 = new byte[1024];

                int bytesRead1;
                int bytesRead2;

                while ((bytesRead1 = stream1.read(buffer1)) != -1) {
                    bytesRead2 = stream2.read(buffer2);

                    if (bytesRead1 != bytesRead2 || !Arrays.equals(buffer1, buffer2)) {
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            setStatusCode(StatusCode.FILE_SYSTEM_ERROR);
            return false;
        }

        return true;
    }
}
