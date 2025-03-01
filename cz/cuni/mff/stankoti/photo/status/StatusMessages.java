package cz.cuni.mff.stankoti.photo.status;

import java.util.HashMap;

public class StatusMessages {
    private static final HashMap<StatusCode, String> statusMessages = new HashMap<>();

    static {
        statusMessages.put(StatusCode.NO_ERROR, "No error.");
        statusMessages.put(StatusCode.UNKNOWN_COMMAND, "Unknown command. Use Help or H for a list of available commands.");
    }

    public static String getStatusMessage(StatusCode statusCode) {
        return statusMessages.getOrDefault(statusCode, "WARNING: Unknown program status code.");
    }
}
