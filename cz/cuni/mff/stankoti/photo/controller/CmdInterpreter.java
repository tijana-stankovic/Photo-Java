package cz.cuni.mff.stankoti.photo.controller;

import cz.cuni.mff.stankoti.photo.status.StatusCode;
import cz.cuni.mff.stankoti.photo.db.*;
import cz.cuni.mff.stankoti.photo.view.*;

import java.util.Arrays;
import java.util.HashSet;

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

    public void print(String str) {
        view.print(str);
    }

    public void executeCommand(Command cmd) {
        setStatusCode(StatusCode.NO_ERROR);

        String command = cmd.command.toUpperCase();

        switch (command) {
            case "H", "HELP" -> help(cmd.args);
            case "AB", "ABOUT" -> about();
            case "E", "X", "EXIT" -> exit();
            case "SAVE" -> save(cmd.args);
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
        print("List of available commands:");
        print("- HELP (H)");
        print("  Display page with list of commands.");
        print("- ABOUT (AB)");
        print("  Display information about program.");
        print("- EXIT (E, X)");
        print("  Exiting the program.");
        print("  If there are unsaved changes, the program will display a control question.");
        print("- SAVE");
        print("  Saving the current memory state to a local file.");
        print("  Default name for this file: photo_db.pdb");
        print("  The name of the file can be specified as a parameter when starting the program.");
        print("- ADD (A)");
        print("  ...");
        print("- AK");
        print("  ...");
        print("- REMOVE (R)");
        print("  ...");
        print("- RK");
        print("  ...");
        print("- LIST (L)");
        print("  ...");
        print("- DETAILS (D)");
        print("  ...");
        print("- DUPLICATES (DUP, DD)");
        print("  ...");
        print("- SCAN (S)");
        print("  ...");
    }

    private void about() {
        print("About...");
    }

    private void exit() {
        if (db.isChanged()) {
            assert cli != null : "Interpreter CLI is not initialized!";

            Character response = cli.askYesNo(view, "There are unsaved changes. Do you want to save them?", true);
            switch (response) {
                case 'Y' -> {
                    db.WriteDB();
                    setQuitSignal(true);
                }
                case 'N' -> setQuitSignal(true);
                case 'C' -> { }
                default -> { }
            }
        } else {
            setQuitSignal(true);
        }
    }

    private void save(String[] args) {
        if (db.isChanged()) {
            db.WriteDB();
            view.print("Changes saved successfully.");
        } else {
            view.print("There are no changes to save.");
        }
    }

    private void add(String[] args) {
        print("Add...");
        int fileID = db.nextFileID();
        if (db.addFile(new File(fileID, 
                    "/path/to/file1", 
                    "file1.txt", 
                    ".txt", 
                    "2025-02-21", 
                    1024, 
                    123456789L, 
                    new HashSet<>(Arrays.asList("example1", "document1")), 
                    new HashSet<>(Arrays.asList("metadata1-1", "metadata1-2")))) == 0) {
            print("New file is added.");
        } else {
            print("File is updated.");
        }

        fileID = db.nextFileID();
        if (db.addFile(new File(fileID,
                    "/path/to/file2", 
                    "file2.txt", 
                    ".txt", 
                    "2025-02-22", 
                    1025, 
                    123456790L, 
                    new HashSet<>(Arrays.asList("example2", "document2")), 
                    new HashSet<>(Arrays.asList("metadata2-1", "metadata2-2")))) == 0) {
                    print("New file is added.");
        } else {
            print("File is updated.");
        }
    }

    private void addKeyword(String[] args) {
        print("Add keyword...");
    }

    private void remove(String[] args) {
        print("Remove...");
    }

    private void removeKeyword(String[] args) {
        print("Remove keyword...");
    }

    private void list(String[] args) {
        print("List...");
    }

    private void details(String[] args) {
        print("Details...");
    }

    private void duplicates(String[] args) {
        print("Duplicates...");
    }

    private void scan(String[] args) {
        print("Scan...");
    }
}
