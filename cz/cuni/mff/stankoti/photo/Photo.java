package cz.cuni.mff.stankoti.photo;

import cz.cuni.mff.stankoti.photo.controller.*;

/**
  * Represents the Photo application entry point.
  * <p>
  * This class provides the main() methods, which is the starting point of the application.
  */
public class Photo {
    /**
      * Default constructor.
      * (defined to prevent Javadoc warning)
      */
    public Photo() {}

    /**
      * The starting point of the application.
      * <p>
      * This method is called when the program starts. 
      * It initializes the controller part of the application and passes control to the controller.
      * <br>
      * Only one command line parameter is supported:
      * 'db-file-name', which is the name of the file containing the photo database.
      *
      * @param args the command-line arguments passed to the program
      */
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
