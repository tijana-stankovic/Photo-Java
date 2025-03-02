package cz.cuni.mff.stankoti.photo.status;

import java.util.HashMap;

public class StatusMessages {
    private static final HashMap<StatusCode, String> statusMessages = new HashMap<>();

    static {
        statusMessages.put(StatusCode.NO_ERROR, "No error.");
        statusMessages.put(StatusCode.UNEXPECTED_STATUS, "WARNING: Unexpected program status.");
        statusMessages.put(StatusCode.UNKNOWN_COMMAND, "ERROR: Unknown command. Use Help or H for a list of available commands.");
        statusMessages.put(StatusCode.DB_FILE_DOES_NOT_EXIST, "WARNING: The database file does not exist. A new file will be created.");
        statusMessages.put(StatusCode.DB_FILE_INCOMPATIBLE_FORMAT, "ERROR: The database file is in incompatible format.");
        statusMessages.put(StatusCode.DB_FILE_READ_ERROR, "ERROR: An error occurred while reading the database file.");
        statusMessages.put(StatusCode.DB_FILE_WRITE_ERROR, "ERROR: An error occurred while writing to the database file.");
    }

    public static String getStatusMessage(StatusCode statusCode) {
        return statusMessages.getOrDefault(statusCode, "WARNING: Unknown program status code.");
    }    
}
