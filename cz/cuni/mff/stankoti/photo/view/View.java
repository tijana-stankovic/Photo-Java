package cz.cuni.mff.stankoti.photo.view;

import java.util.Map;

import cz.cuni.mff.stankoti.photo.status.StatusCode;
import cz.cuni.mff.stankoti.photo.status.StatusMessages;

public class View {
    public void fullProgramInfo() {
        String version = "0.9";
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

    public void printPrompt() {
        String prompt = "> ";
        System.out.print(prompt);
    }

    public void print(String line) {
        System.out.println(line);
    }

    public void print(String line, boolean newLine) {
        if (newLine) {
            System.out.println(line);
        } else {
            System.out.print(line);
        }
    }
        
    public void printStatus(StatusCode statusCode) {
        System.out.printf(StatusMessages.getStatusMessage(statusCode) + "%n");
    }

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
