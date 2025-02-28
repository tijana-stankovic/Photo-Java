package cz.cuni.mff.stankoti.photo.controller;

import cz.cuni.mff.stankoti.photo.db.*;
import cz.cuni.mff.stankoti.photo.view.*;

public class Controller {
    private CmdInterpreter interpreter;
    private DB db;
    private View view;

    public Controller() {
        interpreter = new CmdInterpreter();
        db = new DB();
        view = new View();
    }

    public void run() {
        view.fullProgramInfo();

        try (CLI cli = new CLI()) {
            boolean quit = false;
            while (!quit) {
                view.printPrompt();
                Command cmd = cli.readCommand();
                interpreter.executeCommand(cmd);
                quit = interpreter.getQuitFlag();
            }
        }
    }
}
