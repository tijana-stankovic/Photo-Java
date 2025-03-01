package cz.cuni.mff.stankoti.photo.controller;

import cz.cuni.mff.stankoti.photo.db.*;
import cz.cuni.mff.stankoti.photo.view.*;

public class Controller {
    private View view;
    private DB db;
    private CmdInterpreter interpreter;

    public Controller() {
        view = new View();
        view.fullProgramInfo();
        interpreter = new CmdInterpreter(db, view);
    }

    public void run() {
        view.print("");
        try (CLI cli = new CLI()) {
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
