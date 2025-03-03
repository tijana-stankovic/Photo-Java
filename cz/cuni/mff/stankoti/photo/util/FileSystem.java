package cz.cuni.mff.stankoti.photo.util;

import java.io.File;

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
}