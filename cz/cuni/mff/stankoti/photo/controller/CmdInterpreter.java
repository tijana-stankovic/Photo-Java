package cz.cuni.mff.stankoti.photo.controller;

import cz.cuni.mff.stankoti.photo.status.StatusCode;
import cz.cuni.mff.stankoti.photo.db.*;
import cz.cuni.mff.stankoti.photo.view.*;
import cz.cuni.mff.stankoti.photo.util.FileSystem;

import java.util.Arrays;
import java.util.List;

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
            file.setID(db.nextFileID());
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

        view.print("Add keyword...");
        System.out.println(args.length);    
    }

    private void remove(String[] args) {
        view.print("Remove...");
    }

    private void removeKeyword(String[] args) {
        view.print("Remove keyword...");
    }

    private void list(String[] args) {
        view.print("List...");
    }

    private void details(String[] args) {
        view.print("Details...");
    }

    private void duplicates(String[] args) {
        view.print("Duplicates...");
    }

    private void scan(String[] args) {
        view.print("Scan...");
    }
}
