package cz.cuni.mff.stankoti.photo.controller;

import cz.cuni.mff.stankoti.photo.status.StatusCode;
import cz.cuni.mff.stankoti.photo.db.*;
import cz.cuni.mff.stankoti.photo.view.*;

/**
  * The top-level class of the Controller.
  * <p>
  * It initializes the other application and Controller parts and executes the main application loop.
  * </p>
  */
public class Controller {
    private View view;
    private DB db;
    private CmdInterpreter interpreter;

    /**
      * Creates a new Controller instance.
      * Initializes the view, database, and command interpreter.
      * Displays the full program information and database statistics.
      *
      * @param args can contain the name of the external file from which the database data is read
      */
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

        view.print("");
        view.printDBStatistics(db.getDBStatistics());

        interpreter = new CmdInterpreter(db, view);
    }

    /**
      * Determines the database filename based on the provided arguments.
      * If no arguments are provided, uses the default database filename.
      *
      * @param args can contain the name of the external file
      * @return the database filename
      */
    private String getFilename(String[] args) {
        String fileName;

        if (args.length == 0) {
            fileName = DB.DEFAULT_DB_FILENAME;
            view.print("The default name of the DB file will be used: " + fileName);
        } else {
            fileName = args[0];
            if (!fileName.contains(".")) {
                fileName += ".pdb";
            }
            view.print("The DB filename: " + fileName);
        }

        return fileName;
    }

    /**
      * Runs the main application loop.
      * Initializes the command-line interface and processes user commands until the quit signal is received.
      */
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
