package cz.cuni.mff.stankoti.photo.status;

import java.util.HashMap;

/**
  * Class for mapping status codes to their respective messages.
  */
public class StatusMessages {
    private static final HashMap<StatusCode, String> statusMessages = new HashMap<>();

    // Initializes the status messages for each status code.
    static {
        statusMessages.put(StatusCode.NO_ERROR, "No error.");
        statusMessages.put(StatusCode.UNEXPECTED_STATUS, "WARNING: Unexpected program status.");
        statusMessages.put(StatusCode.UNKNOWN_COMMAND, "ERROR: Unknown command. Use Help or H for a list of available commands.");
        statusMessages.put(StatusCode.DB_FILE_DOES_NOT_EXIST, "WARNING: The database file does not exist. A new file will be created.");
        statusMessages.put(StatusCode.DB_PATH_DOES_NOT_EXIST, "ERROR: The path does not exists in the database.");
        statusMessages.put(StatusCode.DB_FILE_DIR_DOES_NOT_EXIST, "ERROR: The file or directory does not exist in the database.");
        statusMessages.put(StatusCode.DB_FILE_DIR_KEYWORD_DOES_NOT_EXIST, "ERROR: The file, directory, or keyword does not exist in the database.");
        statusMessages.put(StatusCode.DB_FILE_INCOMPATIBLE_FORMAT, "ERROR: The database file is in incompatible format.");
        statusMessages.put(StatusCode.DB_FILE_READ_ERROR, "ERROR: An error occurred while reading the database file.");
        statusMessages.put(StatusCode.DB_FILE_NOT_SERIALIZABLE, "ERROR: Data object is not serializable.%nCheck: All data classes (DBData, DBFile, etc.) must implement the Serializable interface.");
        statusMessages.put(StatusCode.DB_FILE_WRITE_ERROR, "ERROR: An error occurred while writing to the database file.");
        statusMessages.put(StatusCode.INVALID_NUMBER_OF_ARGUMENTS, "ERROR: Invalid number of arguments.");
        statusMessages.put(StatusCode.PATH_DOES_NOT_EXIST, "ERROR: Path does not exists.");
        statusMessages.put(StatusCode.FILE_SYSTEM_ERROR, "ERROR: Error reading file system.");
        statusMessages.put(StatusCode.FILE_SYSTEM_NOT_FILE, "WARNING: Not a file.");
        statusMessages.put(StatusCode.FILE_SYSTEM_NOT_IMAGE, "WARNING: File is not an image.");
    }

    /**
      * Default constructor.
      * (defined to prevent Javadoc warning)
      */
      public StatusMessages() {}   

    /**
      * Returns the message corresponding to the given status code.
      *
      * @param statusCode the status code
      * @return the corresponding message
      */
    public static String getStatusMessage(StatusCode statusCode) {
        return statusMessages.getOrDefault(statusCode, "WARNING: Unknown program status code.");
    }    
}
