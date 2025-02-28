package cz.cuni.mff.stankoti.photo.controller;

import cz.cuni.mff.stankoti.photo.db.*;
import cz.cuni.mff.stankoti.photo.view.*;

public class Controller {
    private DB db;
    private View view;

    public Controller() {
        db = new DB();
        view = new View();
    }

    public void run() {
        view.fullProgramInfo();
    }
}
