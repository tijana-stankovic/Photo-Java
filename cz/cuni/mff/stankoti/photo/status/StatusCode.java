package cz.cuni.mff.stankoti.photo.status;

public enum StatusCode {
    NO_ERROR,
    UNKNOWN_COMMAND,
    DB_FILE_DOES_NOT_EXIST,
    DB_PATH_DOES_NOT_EXIST,
    DB_FILE_DIR_DOES_NOT_EXIST,
    DB_FILE_DIR_KEYWORD_DOES_NOT_EXIST,
    DB_FILE_INCOMPATIBLE_FORMAT,
    DB_FILE_READ_ERROR,
    DB_FILE_NOT_SERIALIZABLE,
    DB_FILE_WRITE_ERROR,
    UNEXPECTED_STATUS,
    INVALID_NUMBER_OF_ARGUMENTS,
    PATH_DOES_NOT_EXIST,
    FILE_SYSTEM_ERROR
}
