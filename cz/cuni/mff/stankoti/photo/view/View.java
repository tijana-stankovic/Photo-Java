package cz.cuni.mff.stankoti.photo.view;

import java.util.Map;

import cz.cuni.mff.stankoti.photo.status.StatusCode;
import cz.cuni.mff.stankoti.photo.status.StatusMessages;

/**
  * A class with methods for displaying various information to the user.
  */
public class View {
    /**
      * Default constructor.
      * (defined to prevent Javadoc warning)
      */
    public View() {}

    /**
      * Displays information about the Photo application.
      */
    public void fullProgramInfo() {
        String version = "1.0";
        String projectName = "Photo Organizer";
        String course = "Programming in Java Language - NPRG013 - student project";
        String author = "Tijana Stankovic";
        String email = "tijana.stankovic@gmail.com";
        String university = "Charles University, Faculty of Mathematics and Physics";

        System.out.println();
        System.out.println(projectName + " [v " + version + "]");
        System.out.println(course);
        System.out.println("(c) " + author + ", " + email);
        System.out.println(university);
        System.out.println();
    }

    /**
      * Prints a prompt symbol to the screen.
      */
    public void printPrompt() {
        String prompt = "> ";
        System.out.print(prompt);
    }

    /**
      * Prints a line of text to the screen.
      * 
      * @param line the line of text to be printed
      */
    public void print(String line) {
        System.out.println(line);
    }

    /**
      * Prints a line of text to the screen, with or without a new line.
      * 
      * @param line the line of text to be printed
      * @param newLine if true, prints a new line after the text; if false, prints the text without a new line
      */
    public void print(String line, boolean newLine) {
        if (newLine) {
            System.out.println(line);
        } else {
            System.out.print(line);
        }
    }

    /**
      * Prints the status message corresponding to the given status code.
      * 
      * @param statusCode the status code for which the message is to be printed
      */
    public void printStatus(StatusCode statusCode) {
        System.out.printf(StatusMessages.getStatusMessage(statusCode) + "%n");
    }

    /**
      * Prints the current database statistics.
      * 
      * @param dbStatistics a map containing the database statistics
      */
    public void printDBStatistics(Map<String, Integer> dbStatistics) {
        print("Current database statistics:");
        int fileCount = dbStatistics.get("FILES");
        if (fileCount > 0) {
            print("   - Number of files: " + fileCount);
            print("   - Number of directories: " + dbStatistics.get("DIRS") + " (use 'LD' command to get a list)");
            print("   - Number of keywords: " + dbStatistics.get("KEYS") + " (use 'LK' command to get a list)");
        } else {
            print("   - Number of files: 0 (use 'ADD' command to add files to the database)");
        }
    }
}
