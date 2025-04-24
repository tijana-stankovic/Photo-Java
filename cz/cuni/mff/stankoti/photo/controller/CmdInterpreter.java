package cz.cuni.mff.stankoti.photo.controller;

import cz.cuni.mff.stankoti.photo.status.StatusCode;
import cz.cuni.mff.stankoti.photo.db.*;
import cz.cuni.mff.stankoti.photo.view.*;
import cz.cuni.mff.stankoti.photo.util.FileSystem;
import cz.cuni.mff.stankoti.photo.util.MetadataInfo;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

/**
  * This is the Command Interpreter - the main processing class of the Controller.
  * <p>
  * It executes all commands of the Photo application and is responsible for communication with the Model and View parts
  * </p>
  */
public class CmdInterpreter {
    /**
      * Model instance with which the Command Interpreter communicates.
      */
    private DB db;
    /**
      * View instance with which the Command Interpreter communicates.
      */
    private View view;
    /**
      * Status code of the last executed command.
      */
    private StatusCode statusCode;
    /**
      * A flag indicating whether an exit from the application has been requested.
      */
    private boolean quitSignal;
    /**
      * CLI instance used by the Command Interpreter for handling user input.
      */
    private CLI cli;

    /**
      * Creates a new Command interpreter instance.
      * It will communicate with the specified database and view instances.
      *
      * @param db the database instance
      * @param view the view instance
      */
    public CmdInterpreter(DB db, View view) {
        this.db = db;
        this.view = view;
        statusCode = StatusCode.NO_ERROR;
        quitSignal = false;
        cli = null;
    }

    /**
      * Gets the last Command Interpreter operation status code.
      *
      * @return the last Command Interpreter operation status code
      */
    public StatusCode getStatusCode() {
        return statusCode;
    }

    /**
      * Sets a Command Interpreter operation status code.
      *
      * @param statusCode the Command Interpreter operation status code
      */
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    /**
      * Gets the quit signal status ('quit signal' = request to exit Photo application).
      *
      * @return true if the quit signal is set, false otherwise
      */
    public boolean getQuitSignal() {
        return quitSignal;
    }

    /**
      * Sets the quit signal status.
      *
      * @param quitSignal the new quit signal status
      */
    public void setQuitSignal(boolean quitSignal) {
        this.quitSignal = quitSignal;
    }

    /**
      * Sets the Command Line Interface (CLI) to be used in communication with the user.
      *
      * @param cli the command line interface instance
      */
    public void setCLI(CLI cli) {
        this.cli = cli;
    }

    /**
      * Entry point for command processing. Executes the specified command.
      *
      * @param cmd the command to execute
      */
    public void executeCommand(Command cmd) {
        setStatusCode(StatusCode.NO_ERROR);

        String command = cmd.command.toUpperCase();

        switch (command) {
            case "" -> {} // do nothing
            case "H", "HELP" -> help();
            case "AB", "ABOUT" -> about();
            case "E", "X", "EXIT" -> exit();
            case "SAVE" -> save(cmd.args);
            case "A", "ADD" -> add(cmd.args);
            case "AK" -> addKeyword(cmd.args);
            case "R", "REMOVE" -> remove(cmd.args);
            case "RK" -> removeKeyword(cmd.args);
            case "L", "LIST" -> list(cmd.args);
            case "LK" -> listKeywords(cmd.args);
            case "LD", "LF" -> listDirectories(cmd.args);
            case "D", "DETAILS" -> details(cmd.args);
            case "DUP", "DD", "DUPLICATES" -> duplicates(cmd.args);
            case "S", "SCAN" -> scan(cmd.args);

            default -> {
                setStatusCode(StatusCode.UNKNOWN_COMMAND);
                view.printStatus(getStatusCode());
            }
        }
    }

    /**
      * HELP command entry point. 
      * Displays the help information (short description) for each command.
      */
    private void help() {
        view.print("List of available commands:");
        view.print("- HELP (H)");
        view.print("  Display page with list of commands.");
        view.print("- ABOUT (AB)");
        view.print("  Display information about program.");
        view.print("- EXIT (E, X)");
        view.print("  Exiting the program.");
        view.print("  If there are unsaved changes, the program will display a control question.");
        view.print("- SAVE [<db-filename>]");
        view.print("  Saving the current memory state to a local file.");
        view.print("  Default name for this file: photo_db.pdb");
        view.print("  New filename can be specified as parameter.");
        view.print("  The name of the file can also be specified as a parameter when starting the program.");
        view.print("- ADD (A)");
        view.print("    - ADD <folder> or <filename>");
        view.print("      Adds all images from the specified <folder> or");
        view.print("      only the one specified by <filename> to the in-memory database.");
        view.print("    - ADD KEYWORD <keyword> <folder> or <filename>");
        view.print("      All images from the specified folder <folder> or");
        view.print("      only the one specified by <filename> get the keyword specified by <keyword>.");
        view.print("- AK");
        view.print("  Short form for ADD KEYWORD command. For details, see ADD command.");
        view.print("- REMOVE (R)");
        view.print("    - REMOVE <folder> or <filename>");
        view.print("      Removes all images from the specified <folder> (including the folder) or");
        view.print("      only the one specified by <filename> from the in-memory database.");
        view.print("    - REMOVE KEYWORD <keyword> <folder> or <filename>");
        view.print("      All images belonging to the folder <folder> or");
        view.print("      only the one specified by <filename> will have the specified <keyword> removed from them.");
        view.print("- RK");
        view.print("  Short form for REMOVE KEYWORD command. For details, see REMOVE command.");
        view.print("- LIST (L)");
        view.print("    - LIST <keyword> or <folder> or <file>");
        view.print("      Lists all images that have the specified keyword or belong to the specified folder.");
        view.print("    - LIST KEYWORDS (LIST KEYS)");
        view.print("      Lists all existing keywords in the database.");
        view.print("    - LIST DIRECTORIES (LIST DIRS, LIST FOLDERS)");
        view.print("      Lists all existing directories (folders) in the database.");
        view.print("    - LIST");
        view.print("      Displays database statistics.");
        view.print("- LK");
        view.print("  Short form for LIST KEYWORDS command. For details, see LIST command.");
        view.print("- LD (LF)");
        view.print("  Short form for LIST DIRECTORIES command. For details, see LIST command.");
        view.print("- DETAILS (D)");
        view.print("  DETAILS <keyword> or <folder> or <file>");
        view.print("  Lists all images that have the given keyword or belong to the given folder or");
        view.print("  given file and displays detailed information about them.");
        view.print("- DUPLICATES (DUP, DD)");
        view.print("  DUPLICATES <keyword> or <folder> or <file>");
        view.print("  Finds duplicates in a set of images determined by a given parameter (comparing files byte by byte).");
        view.print("- SCAN (S)");
        view.print("  SCAN <keyword> or <folder> or <file>");
        view.print("  Compares the set of images determined by the given parameter with the current state on the disk.");
    }

    /**
      * ABOUT command entry point. 
      * Displays information about the program.
      */
    private void about() {
        view.fullProgramInfo();
    }

    /**
      * EXIT command entry point. 
      * Sets the request (quit signal) for exiting the program.
      * If there are unsaved changes, prompts the user to save them.
      */
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

    /**
      * SAVE command entry point. 
      * Saves the current state of the database to a file.
      * A new filename can be specified using the parameter (Save As function).
      *
      * @param args optional argument specifying the new filename
      */
    private void save(String[] args) {
        if (args.length > 1) {
            setStatusCode(StatusCode.INVALID_NUMBER_OF_ARGUMENTS);
            view.printStatus(getStatusCode());
            return;
        }

        if (args.length == 1) {
            String newDbFilename = args[0];
            if (!db.getDbFilename().equals(newDbFilename)) {
                db.setDbFilename(newDbFilename);
            }
        }
        
        save();
    }

    /**
      * Saves the current state of the database to the current DB file.
      */
    private void save() {
        if (db.isChanged()) {
            db.WriteDB();

            switch (db.getStatusCode()) {
                case StatusCode.NO_ERROR -> view.print("Changes saved successfully (DB filename: '" + db.getDbFilename() + "').");
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

    /**
      * ADD command entry point. 
      * Adds a specified file or all files from the specified directory to the database, or 
      * adds a specified keyword to a specified database file or all database files from the specified directory.
      *
      * @param args the arguments of the command (see 'HELP' command for details)
      */
    private void add(String[] args) {
        if (args.length >= 1 && (args[0].toUpperCase().equals("KEYWORD") || 
                                 args[0].toUpperCase().equals("KEY"))) {
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

    /**
      * Adds a specified file to the database.
      *
      * @param filename the name of the file
      * @param fullPath whether the full path of the file is provided
      */
    private void addFile(String filename, boolean fullPath) {
        String filenameOnly = filename;
        if (fullPath) {
            filenameOnly = FileSystem.extractFilename(filename);
        }
        view.print("Processing file '" + filenameOnly + "'... ", false );

        DBFile file = FileSystem.getFileInformation(filename);

        switch (FileSystem.getStatusCode()) {
            case StatusCode.NO_ERROR -> {
                if (db.addFile(file) == 0) {
                    view.print("Added.");
                } else {
                    view.print("Updated.");
                }
            }

            case StatusCode.FILE_SYSTEM_ERROR -> {
                    setStatusCode(StatusCode.FILE_SYSTEM_ERROR);
                    view.print("ERROR! (Error reading file)... Skipped.");
                 }

            case StatusCode.FILE_SYSTEM_NOT_FILE -> {
                    view.print("WARNING! (Not a file)... Skipped.");
                }

            case StatusCode.FILE_SYSTEM_NOT_IMAGE -> {
                    view.print("WARNING! (Not an image)... Skipped.");
                }
                
            default -> { assert false : "Unknown FileSystem error code"; }
        }
    }

    /**
      * Adds all files in a directory to the database.
      *
      * @param directory the path of the directory
      */
    private void addDirectory(String directory) {
        view.print("Processing directory '" + directory + "'... ", false );
        List<String> listOfFiles = FileSystem.filesInDirectory(directory);
        if (listOfFiles.size() == 0 || FileSystem.getStatusCode() == StatusCode.FILE_SYSTEM_ERROR) {
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

    /**
      * AK command (ADD KEYWORD short form) entry point. 
      * Adds a specified keyword to a specified database file or all database files from the specified directory.
      *
      * @param args the arguments of the command (see 'HELP' command for details)
      */
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

    /**
      * Adds a keyword to a specified file.
      *
      * @param keyword the keyword to add
      * @param fileID the ID of the file object
      */
    private void addKeywordToFile(String keyword, int fileID) {
        DBFile file = db.getFile(fileID);
        view.print("Processing file '" + file.getFilename() + "." + file.getExtension() + "'... ", false );
        db.addKeyword(keyword, fileID);
        view.print("Ok (fileID = " + fileID + ").");
    }

    /**
      * REMOVE command entry point. 
      * Removes a specified file or all files from the specified directory from the database, or 
      * removes a specified keyword from a specified database file or all database files from the specified directory.
      *
      * @param args the arguments of the command (see 'HELP' command for details)
      */
    private void remove(String[] args) {
        if (args.length >= 1 && (args[0].toUpperCase().equals("KEYWORD") || 
                                 args[0].toUpperCase().equals("KEY"))) {
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

    /**
      * Removes a specified file from the database.
      *
      * @param fileID the ID of the file object to remove
      */
    private void removeFile(int fileID) {
        DBFile file = db.getFile(fileID);
        view.print("Processing file '" + file.getFilename() + "." + file.getExtension() + "'... ", false );
        db.removeFile(fileID);
        view.print("Removed.");
    }

    /**
      * RK command (REMOVE KEYWORD short form) entry point. 
      * Removes a specified keyword from a specified database file or all database files from the specified directory.
      *
      * @param args the arguments of the command (see 'HELP' command for details)
      */
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

    /**
      * Removes a keyword from a specified file.
      *
      * @param keyword the keyword to remove
      * @param fileID the ID of the file object
      */
    private void removeKeywordFromFile(String keyword, int fileID) {
        DBFile file = db.getFile(fileID);
        view.print("Processing file '" + file.getFilename() + "." + file.getExtension() + "'... ", false );
        db.removeKeyword(keyword, fileID);
        view.print("Ok (fileID = " + fileID + ").");
    }

    /**
      * LIST command entry point.
      * Based on the provided arguments, lists all files with specified keyword or in specified directory, or
      * list all existing directories or keywords, or
      * display current database statistics.
      *
      * @param args the arguments of the command (see 'HELP' command for details)
      */
    private void list(String[] args) {
        if (args.length >= 1 && (args[0].toUpperCase().equals("KEYWORDS") || 
                                 args[0].toUpperCase().equals("KEYS"))) {
            listKeywords(Arrays.copyOfRange(args, 1, args.length));
            return;
        } 

        if (args.length >= 1 && (args[0].toUpperCase().equals("DIRECTORIES") || 
                                 args[0].toUpperCase().equals("DIRS") || 
                                 args[0].toUpperCase().equals("FOLDERS"))) {
            listDirectories(Arrays.copyOfRange(args, 1, args.length));
            return;
        } 

        list(args, false);
    }

    /**
      * Based on the provided arguments, lists all files with specified keyword or in specified directory, or
      * specified file only, or display current database statistics.
      *
      * @param args file or folder or keyword or, if empty, show db statistics
      * @param allDetails if true, print additional details
      */
    private void list(String[] args, Boolean allDetails) {
        if (args.length == 0) {
            view.printDBStatistics(db.getDBStatistics());
            return;
        } else if (args.length > 1) {
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

    /**
      * Lists information about a specific file based on the details level.
      *
      * @param fileID the ID of the file object
      * @param detailsLevel the level of details to print ('F' - file info, 'D' - directory info, 'A' - all information)
      */
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
            view.print(formattedOutput, false);
            if (file.getKeywords().contains("CHANGED")) {
                view.print( " (CHANGED)");
            } else if (file.getKeywords().contains("DELETED")) {
                view.print( " (DELETED)");
            } else {
                view.print("");
            }

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

    /**
      * Formats a date-time string into a more readable format.
      *
      * @param dateTime the date-time string, in the format: 'yyyymmdd hh24miss'
      * @return the formatted date-time string, in the format: 'dd.mm.yyyy hh24:mi:ss'
      */
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

    /**
      * LK command (LIST KEYWORD short form) entry point. 
      * Lists all keywords in the database.
      *
      * @param args the arguments of the command (see 'HELP' command for details)
      */
    private void listKeywords(String[] args) {
        if (args.length > 0) {
            setStatusCode(StatusCode.INVALID_NUMBER_OF_ARGUMENTS);
            view.printStatus(getStatusCode());
            return;
        } 

        view.print("List of keywords in the database:");
        for (String keyword : db.getKeywords()) {
            view.print("   " + keyword);
        }
    }

    /**
      * LD command (LIST DIRECTORIES short form) entry point. 
      * Lists all directories in the database.
      *
      * @param args the arguments of the command (see 'HELP' command for details)
      */
    private void listDirectories(String[] args) {
        if (args.length > 0) {
            setStatusCode(StatusCode.INVALID_NUMBER_OF_ARGUMENTS);
            view.printStatus(getStatusCode());
            return;
        } 

        view.print("List of directories in the database:");
        for (String dir : db.getDirectories()) {
            view.print("   " + dir);
        }
    }

    /**
      * DETAILS command entry point. 
      * Based on the provided arguments, shows details of all files with specified keyword or in specified directory, or
      * specified file only.     
      *
      * @param args the arguments of the command (see 'HELP' command for details)
      */
    private void details(String[] args) {
        list(args, true);
    }

    /**
      * DUPLICATES command entry point. 
      * Based on the provided arguments, finds and marks duplicates of all files with specified keyword or 
      * in specified directory, or specified file only.
      *
      * @param args the arguments of the command (see 'HELP' command for details)
      */
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

    /**
      * Finds and marks in the database duplicates of specified file.
      *
      * @param fileID the ID of the file object whose duplicates it is looking for
      * @param allDuplicatesFound the map containing all found duplicates
      */
    private void findDuplicates(int fileID, Map<Integer, Integer> allDuplicatesFound) {
        DBFile file = db.getFile(fileID);
        view.print(file.getFullpath() + "... ", false );

        // allDuplicatesFound contains all previously found duplicates,
        // so, this allows to avoid processing already processed field IDs
        Integer numOfDuplicates = allDuplicatesFound.get(fileID); // get number of duplicates of fileID
        if (numOfDuplicates == null) { // if fileID has not yet been processed
            Map<Integer, Integer> newDuplicatesFound = db.processDuplicates(fileID); // finds and marks duplicates of fileID
            allDuplicatesFound.putAll(newDuplicatesFound); // add found duplicates info to allDuplicatesFound
            numOfDuplicates = allDuplicatesFound.get(fileID); // get number of duplicates of fileID
        }
        if (numOfDuplicates != null) {
            view.print(numOfDuplicates + " duplicate(s)" );
        } else {
            view.print("no duplicates." );
        }
    }

    /**
      * SCAN command entry point.
      * Based on the provided arguments, compares information of all files with the specified keyword,
      * in the specified directory, or the specified file only, with the current file information from the disk.
      * Changed files are marked as CHANGED or DELETED.
      *
      * @param args the arguments of the command (see 'HELP' command for details)
      */
    private void scan(String[] args) {
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
            for (Integer fileId : new ArrayList<>(fileIDs)) {
                scanFile(fileId);
            }
        } else {
            setStatusCode(StatusCode.DB_FILE_DIR_KEYWORD_DOES_NOT_EXIST);
            view.printStatus(getStatusCode());
        } 
    }

    /**
      * Compares the database information of the specified file with the current file information from the disk.
      * The changed file is marked as CHANGED or DELETED.
      *
      * @param fileID the ID of the file object to be compared
      */
    private void scanFile(int fileID) {
        DBFile dbFileInfo = db.getFile(fileID);
        view.print(dbFileInfo.getFullpath() + "... ", false );

        DBFile currentFileInfo = FileSystem.getFileInformation(dbFileInfo.getFullpath());

        switch (FileSystem.getStatusCode()) {
            case StatusCode.NO_ERROR -> {
                if (fileChanged(dbFileInfo, currentFileInfo)) {
                    db.addKeyword("CHANGED", fileID);
                    db.removeKeyword("DELETED", fileID);
                    view.print("CHANGED.");
                } else {
                    db.removeKeyword("CHANGED", fileID);
                    db.removeKeyword("DELETED", fileID);
                    view.print("ok.");
                }
            }

            case StatusCode.FILE_SYSTEM_ERROR -> {
                    setStatusCode(StatusCode.FILE_SYSTEM_ERROR);
                    view.print("ERROR! (Error reading file)... Skipped.");
                 }

            case StatusCode.FILE_SYSTEM_NOT_FILE -> {
                    db.addKeyword("DELETED", fileID);
                    db.removeKeyword("CHANGED", fileID);
                    view.print("DELETED.");
                }

            case StatusCode.FILE_SYSTEM_NOT_IMAGE -> {
                    db.addKeyword("CHANGED", fileID);
                    db.removeKeyword("DELETED", fileID);
                    view.print("CHANGED.");
                }
                
            default -> { assert false : "Unknown FileSystem error code"; }
        }
    }

    /**
      * Checks if a file has changed by comparing information in the database with 
      * the current file state on the disk.
      *
      * @param dbFileInfo the file object containing information from the database
      * @param currentFileInfo the file object containing current information from the disk
      * @return true if the file has changed, false otherwise
      */
    Boolean fileChanged(DBFile dbFileInfo, DBFile currentFileInfo) {
        Set<MetadataInfo> dbMetadata = dbFileInfo.getMetadata();
        Set<MetadataInfo> currentMetadata = currentFileInfo.getMetadata();

        if (!dbFileInfo.getTimestamp().equals(currentFileInfo.getTimestamp()) ||
            dbFileInfo.getSize() != currentFileInfo.getSize() ||
            dbFileInfo.getChecksum() != currentFileInfo.getChecksum() || 
            dbMetadata.size() != currentMetadata.size()) {
            return true;
        }
        
        for (MetadataInfo dbMetadataInfo : dbMetadata) {
            boolean found = false;
            for (MetadataInfo currentMetadataInfo : currentMetadata) {
                if (dbMetadataInfo.getDirectory().equals(currentMetadataInfo.getDirectory()) &&
                    dbMetadataInfo.getTag().equals(currentMetadataInfo.getTag()) &&
                    dbMetadataInfo.getDescription().equals(currentMetadataInfo.getDescription())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return true;
            }
        }
        
        return false;
    }
}
