package cz.cuni.mff.stankoti.photo.status;

/**
  * Enum representing various status codes for the Photo application.
  */
public enum StatusCode {
    /**
      * No error.
      */
    NO_ERROR,
    /**
      * Unexpected program status.
      */
    UNEXPECTED_STATUS,
      /**
      * Unknown command.
      */
    UNKNOWN_COMMAND,
    /**
      * The database file does not exist.
      */
    DB_FILE_DOES_NOT_EXIST,
    /**
      * The path does not exists in the database.
      */
    DB_PATH_DOES_NOT_EXIST,
    /**
      * The file or directory does not exist in the database.
      */
    DB_FILE_DIR_DOES_NOT_EXIST,
    /**
      * The file, directory, or keyword does not exist in the database.
      */
    DB_FILE_DIR_KEYWORD_DOES_NOT_EXIST,
    /**
      * The database file is in incompatible format.
      */
    DB_FILE_INCOMPATIBLE_FORMAT,
    /**
      * An error occurred while reading the database file.
      */
    DB_FILE_READ_ERROR,
    /**
      * Data object is not serializable.
      */
    DB_FILE_NOT_SERIALIZABLE,
    /**
      * An error occurred while writing to the database file.
      */
    DB_FILE_WRITE_ERROR,
    /**
      * Invalid number of arguments.
      */
    INVALID_NUMBER_OF_ARGUMENTS,
    /**
      * Path does not exists.
      */
    PATH_DOES_NOT_EXIST,
    /**
      * Error reading file system.
      */
    FILE_SYSTEM_ERROR,
    /**
      * Not a file.
      */
    FILE_SYSTEM_NOT_FILE,
    /**
      * File is not an image.
      */
    FILE_SYSTEM_NOT_IMAGE
}
