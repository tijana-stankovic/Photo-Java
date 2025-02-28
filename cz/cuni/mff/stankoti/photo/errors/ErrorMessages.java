package cz.cuni.mff.stankoti.photo.errors;

import java.util.HashMap;

public class ErrorMessages {
    private static final HashMap<ErrorCode, String> errorMessages = new HashMap<>();

    static {
        errorMessages.put(ErrorCode.NO_ERROR, "No error.");
        errorMessages.put(ErrorCode.UNKNOWN_COMMAND, "Unknown command. Use Help or H for a list of available commands.");
    }

    public static String getErrorMessage(ErrorCode errorCode) {
        return errorMessages.getOrDefault(errorCode, "Unknown error.");
    }    
}
