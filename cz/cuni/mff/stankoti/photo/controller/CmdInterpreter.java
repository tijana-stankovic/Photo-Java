package cz.cuni.mff.stankoti.photo.controller;

import cz.cuni.mff.stankoti.photo.status.StatusCode;
import cz.cuni.mff.stankoti.photo.db.*;
import cz.cuni.mff.stankoti.photo.view.*;
import cz.cuni.mff.stankoti.photo.util.FileSystem;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

public class CmdInterpreter {
    private DB db;
    private View view;
    private StatusCode statusCode;
    private boolean quitSignal;
    private CLI cli;

    public CmdInterpreter(DB db, View view) {
        this.db = db;
        this.view = view;
        statusCode = StatusCode.NO_ERROR;
        quitSignal = false;
        cli = null;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public boolean getQuitSignal() {
        return quitSignal;
    }

    public void setQuitSignal(boolean quitSignal) {
        this.quitSignal = quitSignal;
    }

    public void setCLI(CLI cli) {
        this.cli = cli;
    }

    public void executeCommand(Command cmd) {
        setStatusCode(StatusCode.NO_ERROR);

        String command = cmd.command.toUpperCase();

        switch (command) {
            case "" -> {} // do nothing
            case "H", "HELP" -> help(cmd.args);
            case "AB", "ABOUT" -> about();
            case "E", "X", "EXIT" -> exit();
            case "SAVE" -> save();
            case "A", "ADD" -> add(cmd.args);
            case "AK" -> addKeyword(cmd.args);
            case "R", "REMOVE" -> remove(cmd.args);
            case "RK" -> removeKeyword(cmd.args);
            case "L", "LIST" -> list(cmd.args);
            case "D", "DETAILS" -> details(cmd.args);
            case "DUP", "DD", "DUPLICATES" -> duplicates(cmd.args);
            case "S", "SCAN" -> scan(cmd.args);

            default -> {
                setStatusCode(StatusCode.UNKNOWN_COMMAND);
                view.printStatus(getStatusCode());
            }
        }
    }

    private void help(String[] args) {
        view.print("List of available commands:");
        view.print("- HELP (H)");
        view.print("  Display page with list of commands.");
        view.print("- ABOUT (AB)");
        view.print("  Display information about program.");
        view.print("- EXIT (E, X)");
        view.print("  Exiting the program.");
        view.print("  If there are unsaved changes, the program will display a control question.");
        view.print("- SAVE");
        view.print("  Saving the current memory state to a local file.");
        view.print("  Default name for this file: photo_db.pdb");
        view.print("  The name of the file can be specified as a parameter when starting the program.");
        view.print("- ADD (A)");
        view.print("  ...");
        view.print("- AK");
        view.print("  ...");
        view.print("- REMOVE (R)");
        view.print("  ...");
        view.print("- RK");
        view.print("  ...");
        view.print("- LIST (L)");
        view.print("  ...");
        view.print("- DETAILS (D)");
        view.print("  ...");
        view.print("- DUPLICATES (DUP, DD)");
        view.print("  ...");
        view.print("- SCAN (S)");
        view.print("  ...");
    }

    private void about() {
        view.print("About...");
    }

    private void exit() {
        if (db.isChanged()) {
            assert cli != null : "Interpreter CLI is not initialized!";

            char response = cli.askYesNo(view, "There are unsaved changes. Do you want to save them?", true);
            switch (response) {
                case 'Y' -> {
                    save();
                    if (db.getStatusCode() == StatusCode.NO_ERROR) {
                        setQuitSignal(true);
                    }
                }
                case 'N' -> setQuitSignal(true);
                case 'C' -> { }
                default -> { }
            }
        } else {
            setQuitSignal(true);
        }
    }

    private void save() {
        if (db.isChanged()) {
            db.WriteDB();

            switch (db.getStatusCode()) {
                case StatusCode.NO_ERROR -> view.print("Changes saved successfully.");
                case StatusCode.DB_FILE_NOT_SERIALIZABLE,
                     StatusCode.DB_FILE_WRITE_ERROR -> {
                        view.printStatus(db.getStatusCode());
                        assert db.getStatusCode() != StatusCode.DB_FILE_NOT_SERIALIZABLE;
                     }
                default -> view.printStatus(StatusCode.UNEXPECTED_STATUS);
            }
        } else {
            view.print("There are no changes to save.");
        }
    }

    private void add(String[] args) {
        if (args.length >= 2 && args[0].toUpperCase().equals("KEYWORD")) {
            addKeyword(Arrays.copyOfRange(args, 1, args.length));
            return;
        } 

        if (args.length != 1) {
            setStatusCode(StatusCode.INVALID_NUMBER_OF_ARGUMENTS);
            view.printStatus(getStatusCode());
            return;
        } 

        String path = args[0];
        switch(FileSystem.checkPath(path)) {
            case 'F' -> addFile(path, false);
            case 'D' -> addDirectory(path);
            case 'E' -> {
                setStatusCode(StatusCode.PATH_DOES_NOT_EXIST);
                view.printStatus(getStatusCode());    
            }
            default -> { assert false : "Unknown FileSystem.checkPath() result!"; }
        }
    }

    private void addFile(String filename, boolean fullPath) {
        String filenameOnly = filename;
        if (fullPath) {
            filenameOnly = FileSystem.extractFilename(filename);
        }
        view.print("Processing file '" + filenameOnly + "'... ", false );
        DBFile file = FileSystem.getFileInformation(filename);
        if (!file.getLocation().isEmpty()) {
            if (db.addFile(file) == 0) {
                view.print("Added.");
            } else {
                view.print("Updated.");
            }
        } else {
            view.print("Skipped.");
        }
    }

    private void addDirectory(String directory) {
        view.print("Processing directory '" + directory + "'... ", false );
        List<String> listOfFiles = FileSystem.filesInDirectory(directory);
        if (listOfFiles.size() == 0) {
            setStatusCode(StatusCode.FILE_SYSTEM_ERROR);
            view.printStatus(getStatusCode());    
            return;
        }

        view.print("(found " + (listOfFiles.size() - 1) + " file(s))");
        view.print("Full path: " + listOfFiles.get(0));
        for (int i = 1; i < listOfFiles.size(); i++) {
            addFile(listOfFiles.get(i), true);
        }
    }

    private void addKeyword(String[] args) {
        if (args.length != 2) {
            setStatusCode(StatusCode.INVALID_NUMBER_OF_ARGUMENTS);
            view.printStatus(getStatusCode());
            return;
        } 

        String keyword = args[0].toUpperCase();
        String path = args[1];

        Set<Integer> fileIDs = db.getFileIDs(path, 'F');
        if (fileIDs != null) {
            view.print("Adding the keyword '" + keyword + "' to the specified file.");
        } else {
            fileIDs = db.getFileIDs(path, 'D');
            if (fileIDs != null) {
                view.print("Adding the keyword '" + keyword + "' to files in the specified directory.");
                view.print("(found " + fileIDs.size() + " file(s))");
            }
        }

        if (fileIDs != null) {
            for (Integer fileId : fileIDs) {
                addKeywordToFile(keyword, fileId);
            }
        } else {
            setStatusCode(StatusCode.DB_FILE_DIR_DOES_NOT_EXIST);
            view.printStatus(getStatusCode());
        } 
    }

    private void addKeywordToFile(String keyword, int fileID) {
        DBFile file = db.getFile(fileID);
        view.print("Processing file '" + file.getFilename() + "." + file.getExtension() + "'... ", false );
        db.addKeyword(keyword, fileID);
        view.print("Ok (fileID = " + fileID + ").");
    }

    private void remove(String[] args) {
        if (args.length >= 2 && args[0].toUpperCase().equals("KEYWORD")) {
            removeKeyword(Arrays.copyOfRange(args, 1, args.length));
            return;
        } 

        if (args.length != 1) {
            setStatusCode(StatusCode.INVALID_NUMBER_OF_ARGUMENTS);
            view.printStatus(getStatusCode());
            return;
        } 

        String path = args[0];
        Set<Integer> fileIDs = db.getFileIDs(path, 'F');
        if (fileIDs == null) {
            fileIDs = db.getFileIDs(path, 'D');
            if (fileIDs != null) {
                view.print("Processing directory '" + path + "'... ", false );
                view.print("(found " + fileIDs.size() + " file(s))");
            } else {
                setStatusCode(StatusCode.DB_FILE_DIR_DOES_NOT_EXIST);
                view.printStatus(getStatusCode());    
            }
        }

        if (fileIDs != null) {
            for (Integer fileId : new ArrayList<>(fileIDs)) {
                removeFile(fileId);
            }
        }
    }

    private void removeFile(int fileID) {
        DBFile file = db.getFile(fileID);
        view.print("Processing file '" + file.getFilename() + "." + file.getExtension() + "'... ", false );
        db.removeFile(fileID);
        view.print("Removed.");
    }

    private void removeKeyword(String[] args) {
        if (args.length != 2) {
            setStatusCode(StatusCode.INVALID_NUMBER_OF_ARGUMENTS);
            view.printStatus(getStatusCode());
            return;
        } 

        String keyword = args[0].toUpperCase();
        String path = args[1];

        Set<Integer> fileIDs = db.getFileIDs(path, 'F');
        if (fileIDs != null) {
            view.print("Removing the keyword '" + keyword + "' from the specified file.");
        } else {
            fileIDs = db.getFileIDs(path, 'D');
            if (fileIDs != null) {
                view.print("Removing the keyword '" + keyword + "' from files in the specified directory.");
                view.print("(found " + fileIDs.size() + " file(s))");
            }
        }

        if (fileIDs != null) {
            for (Integer fileId : fileIDs) {
                removeKeywordFromFile(keyword, fileId);
            }
        } else {
            setStatusCode(StatusCode.DB_FILE_DIR_DOES_NOT_EXIST);
            view.printStatus(getStatusCode());
        } 
    }

    private void removeKeywordFromFile(String keyword, int fileID) {
        DBFile file = db.getFile(fileID);
        view.print("Processing file '" + file.getFilename() + "." + file.getExtension() + "'... ", false );
        db.removeKeyword(keyword, fileID);
        view.print("Ok (fileID = " + fileID + ").");
    }

    private void list(String[] args) {
        list(args, false);
    }

    private void list(String[] args, Boolean allDetails) {
        if (args.length != 1) {
            setStatusCode(StatusCode.INVALID_NUMBER_OF_ARGUMENTS);
            view.printStatus(getStatusCode());
            return;
        } 

        String path = args[0];
        Character detailsLevel = ' ';
        Set<Integer> fileIDs = db.getFileIDs(path, 'F');
        if (fileIDs != null) {
            if (allDetails) {
                detailsLevel = 'A'; // print all details
            } else {
                detailsLevel = 'F'; // print file info only
            } 
            view.print("The specified file exists in the database.");
        } else {
            fileIDs = db.getFileIDs(path, 'D');
            if (fileIDs != null) {
                if (allDetails) {
                    detailsLevel = 'A'; // print all details
                } else {
                    detailsLevel = 'F'; // print file info only
                }
                view.print("The specified directory exists in the database.");
                view.print("(found " + fileIDs.size() + " file(s))");
            } else {
                String keyword = args[0].toUpperCase();
                fileIDs = db.getFileIDs(keyword, 'K');
                if (fileIDs != null) {
                    if (allDetails) {
                        detailsLevel = 'A'; // print all details
                    } else {
                       detailsLevel = 'D'; // print file info + directory name
                    }
                    view.print("The specified keyword exists in the database.");
                    view.print("(found " + fileIDs.size() + " file(s))");
                }    
            }
        }

        if (fileIDs != null) {
            for (Integer fileId : fileIDs) {
                listFileInfo(fileId, detailsLevel);
            }
        } else {
            setStatusCode(StatusCode.DB_FILE_DIR_KEYWORD_DOES_NOT_EXIST);
            view.printStatus(getStatusCode());
        } 
    }

    private void listFileInfo(int fileID, Character detailsLevel) {
        DBFile file = db.getFile(fileID);

        String filenameWithExtension = file.getFilename() + "." + file.getExtension();
        String formattedTimestamp = formatedDateTime(file.getTimestamp());
        String fileSize = FileSystem.formatFileSize(file.getSize());
        String formattedOutput;

        String prefix = "   ";
        if (detailsLevel == 'F' || detailsLevel == 'D' ) { // File info or Directory info
            if (filenameWithExtension.length() + fileSize.length() + 3 <= 60) {
                formattedOutput = String.format("%-60s   %s   %s", filenameWithExtension, formattedTimestamp, fileSize);
            } else {
                formattedOutput = filenameWithExtension + "   " + formattedTimestamp + "   " + fileSize;
            }
            view.print(formattedOutput);

            if (detailsLevel == 'D') { // Directory info
                view.print(prefix + "in: " + file.getLocation());
            }

            if (file.getDuplicates().size() > 0) {
                view.print(prefix + "Duplicates: " + file.getDuplicates().size());
            }
            if (file.getPotentialDuplicates().size() > 0) {
                view.print(prefix + "Potential duplicates: " + file.getPotentialDuplicates().size());
            }
        } else if (detailsLevel == 'A') { // All info
            view.print(filenameWithExtension);
            view.print(prefix + "in: " + file.getLocation());
            view.print(prefix + "ID: " + file.getID());
            view.print(prefix + "Timestamp: " + formattedTimestamp);
            view.print(prefix + "Size: " + fileSize + " (" + file.getSize() + "byte(s))");
            view.print(prefix + "CRC32: " + file.getChecksum());

            view.print(prefix + "Keywords: ", false);
            for (String keyword : file.getKeywords()) {
                view.print(keyword + " ", false);
            }
            view.print("");

            Set<Integer> duplicates = file.getDuplicates();
            if (duplicates.size() > 0) {
                view.print(prefix + "Duplicates: " + duplicates.size());
                for (int duplicateFileID : duplicates) {
                    DBFile duplicateFile = db.getFile(duplicateFileID);
                    view.print(prefix + prefix + duplicateFile.getFullpath());
                }
            }

            duplicates = file.getPotentialDuplicates();
            if (duplicates.size() > 0) {
                view.print(prefix + "Potential duplicates: " + duplicates.size());
                for (int duplicateFileID : duplicates) {
                    DBFile duplicateFile = db.getFile(duplicateFileID);
                    view.print(prefix + prefix + duplicateFile.getFullpath());
                }
            }

            view.print(prefix + "Metadata:");
            for (var metadataTag : file.getMetadata()) {
                view.print(prefix + prefix + metadataTag.getDirectory() + " " + metadataTag.getTag() + " " + metadataTag.getDescription());
            }
            view.print("------------------------------------------------------");
        } 
    }

    private String formatedDateTime(String dateTime) {
        String year = dateTime.substring(0, 4);
        String month = dateTime.substring(4, 6);
        String day = dateTime.substring(6, 8);
        String hour = dateTime.substring(9, 11);
        String minute = dateTime.substring(11, 13);
        String second = dateTime.substring(13, 15);
        String formattedDateTime = day + "." + month + "." + year + " " + hour + ":" + minute + ":" + second;
        return formattedDateTime;
    }

    private void details(String[] args) {
        list(args, true);
    }

    private void duplicates(String[] args) {
        if (args.length != 1) {
            setStatusCode(StatusCode.INVALID_NUMBER_OF_ARGUMENTS);
            view.printStatus(getStatusCode());
            return;
        } 

        String path = args[0];
        Set<Integer> fileIDs = db.getFileIDs(path, 'F');
        if (fileIDs != null) {
            view.print("The specified file exists in the database.");
        } else {
            fileIDs = db.getFileIDs(path, 'D');
            if (fileIDs != null) {
                view.print("The specified directory exists in the database.");
                view.print("(found " + fileIDs.size() + " file(s))");
            } else {
                String keyword = args[0].toUpperCase();
                fileIDs = db.getFileIDs(keyword, 'K');
                if (fileIDs != null) {
                    view.print("The specified keyword exists in the database.");
                    view.print("(found " + fileIDs.size() + " file(s))");
                }    
            }
        }

        if (fileIDs != null) {
            Map<Integer, Integer> allDuplicatesFound = new HashMap<>();
            for (Integer fileId : new ArrayList<>(fileIDs)) {
                findDuplicates(fileId, allDuplicatesFound);
            }
            if (allDuplicatesFound.size() == 0) {
                view.print("No duplicates found.");
            }
        } else {
            setStatusCode(StatusCode.DB_FILE_DIR_KEYWORD_DOES_NOT_EXIST);
            view.printStatus(getStatusCode());
        } 
    }

    private void findDuplicates(int fileID, Map<Integer, Integer> allDuplicatesFound) {
        DBFile file = db.getFile(fileID);
        view.print(file.getFullpath() + "... ", false );

        Integer numOfDuplicates = allDuplicatesFound.get(fileID);
        if (numOfDuplicates == null) {
            Map<Integer, Integer> newDuplicatesFound = db.processDuplicates(fileID);
            allDuplicatesFound.putAll(newDuplicatesFound);
            numOfDuplicates = allDuplicatesFound.get(fileID);
        }
        if (numOfDuplicates != null) {
            view.print(numOfDuplicates + " duplicate(s)" );
        } else {
            view.print("no duplicates." );
        }
    }

    private void scan(String[] args) {
        view.print("Scan...");
    }
}
