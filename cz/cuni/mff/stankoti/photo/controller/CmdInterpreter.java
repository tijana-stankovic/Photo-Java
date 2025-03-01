package cz.cuni.mff.stankoti.photo.controller;

import java.util.ArrayList;
import java.util.Arrays;

import cz.cuni.mff.stankoti.photo.errors.*;
import cz.cuni.mff.stankoti.photo.db.*;

public class CmdInterpreter {
    private ErrorCode errorCode;
    private boolean quitFlag;
    private ArrayList<String> commandOutput;

    public CmdInterpreter() {
        errorCode = ErrorCode.NO_ERROR;
        quitFlag = false;
        clearCommandOutput();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public boolean getQuitFlag() {
        return quitFlag;
    }

    public void setQuitFlag(boolean quitFlag) {
        this.quitFlag = quitFlag;
    }

    public void clearCommandOutput() {
        commandOutput = new ArrayList<>();
    }

    public ArrayList<String> getCommandOutput() {
        return commandOutput;
    }

    public void print(String str) {
        commandOutput.add(str);
    }

    public boolean executeCommand(Command cmd) {
        setErrorCode(ErrorCode.NO_ERROR);
        clearCommandOutput();

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

            default -> setErrorCode(ErrorCode.UNKNOWN_COMMAND);
        }

        boolean success = true;
        if (getErrorCode() != ErrorCode.NO_ERROR)
            success = false;
        
        return success;
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
        setQuitFlag(true);
    }

    private void save(String[] args) {
        print("Save...");


        ArrayList<File> files = new ArrayList<>();
        files.add(new File(1, 
                    "/path/to/file1", 
                    "file1.txt", 
                    ".txt", 
                    "2025-02-21", 
                    1024, 
                    123456789L, 
                    Arrays.asList("example1", "document1"), 
                    Arrays.asList("metadata1-1", "metadata1-2")));
        files.add(new File(2, 
                    "/path/to/file2", 
                    "file2.txt", 
                    ".txt", 
                    "2025-02-22", 
                    1025, 
                    123456790L, 
                    Arrays.asList("example2", "document2"), 
                    Arrays.asList("metadata2-1", "metadata2-2")));
        DB.WriteDB(files, "test.db");
        files = new ArrayList<>();
        files = DB.ReadDB("test.db");
    }

    private void add(String[] args) {
        print("Add...");
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
