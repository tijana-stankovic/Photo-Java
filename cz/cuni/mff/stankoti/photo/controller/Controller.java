package cz.cuni.mff.stankoti.photo.controller;

import cz.cuni.mff.stankoti.photo.status.StatusCode;
import cz.cuni.mff.stankoti.photo.db.*;
import cz.cuni.mff.stankoti.photo.view.*;

public class Controller {
    private View view;
    private DB db;
    private CmdInterpreter interpreter;

    public Controller(String[] args) {
        view = new View();
        view.fullProgramInfo();

        String dbFilename = getFilename(args);
        db = new DB(dbFilename);
        switch (db.getStatusCode()) {
            case StatusCode.NO_ERROR -> {} // do nothing
            case StatusCode.DB_FILE_DOES_NOT_EXIST,
                 StatusCode.DB_FILE_INCOMPATIBLE_FORMAT,
                 StatusCode.DB_FILE_READ_ERROR -> view.printStatus(db.getStatusCode());
            default -> view.printStatus(StatusCode.UNEXPECTED_STATUS);
        }

        interpreter = new CmdInterpreter(db, view);
    }

    private String getFilename(String[] args) {
        String fileName;

        if (args.length == 0) {
            fileName = DB.DEFAULT_DB_FILENAME;
            view.print("The default name of the db file will be used: " + fileName);
        } else {
            fileName = args[0];
            if (!fileName.contains(".")) {
                fileName += ".pdb";
            }
            view.print("The db filename: " + fileName);
        }

        return fileName;
    }

    public void run() {
        view.print("");
        try (CLI cli = new CLI()) {
            interpreter.setCLI(cli);
            boolean quit = false;
            while (!quit) {
                view.printPrompt();
                Command cmd = cli.readCommand();
                interpreter.executeCommand(cmd);
                quit = interpreter.getQuitSignal();
            }
        }
    }
}
