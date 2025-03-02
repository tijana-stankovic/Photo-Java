package cz.cuni.mff.stankoti.photo;

import cz.cuni.mff.stankoti.photo.controller.*;

public class Photo {
    public static void main(String[] args) {
        if (args.length <= 1) {
            Controller controller = new Controller(args);
            controller.run();
        } else {
            System.err.println();
            System.err.println("Usage: photo [db-file-name]");
        }
    }
}
