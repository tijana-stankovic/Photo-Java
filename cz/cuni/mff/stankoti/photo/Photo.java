package cz.cuni.mff.stankoti.photo;

import cz.cuni.mff.stankoti.photo.controller.*;

// Java naming conventions:
// Classes/Interfaces: PascalCase
// Methods/Variables: camelCase
// Constants: UPPERCASE_WITH_UNDERSCORES
// Packages: lowercase

public class Photo {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.run();
    }
}
