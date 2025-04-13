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

public class FileSystem {
    private static StatusCode statusCode;

    public static StatusCode getStatusCode() {
        return statusCode;
    }

    public static void setStatusCode(StatusCode newStatusCode) {
        statusCode = newStatusCode;
    }

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

                dbFile.setFullpath(fullpath);
                dbFile.setLocation(location);
                dbFile.setFilename(fname);
                dbFile.setExtension(extension);

                // HH for 24-hour format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss").withZone(ZoneId.systemDefault());  // Use system timezone
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

    public static String extractFilename(String filename) {
        // Find the last occurrence of the file separator
        int lastSeparatorIndex = filename.lastIndexOf('/');
        if (lastSeparatorIndex == -1) {
            lastSeparatorIndex = filename.lastIndexOf('\\');
        }
    
        // Extract the filename
        String filenameOnly = (lastSeparatorIndex == -1) ? filename : filename.substring(lastSeparatorIndex + 1);
    
        return filenameOnly;
    }

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
