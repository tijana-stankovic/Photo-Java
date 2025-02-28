package cz.cuni.mff.stankoti.photo.view;

public class View {
    public void printPrompt() {
        String prompt = "> ";
        System.out.print(prompt);
    }

    public void fullProgramInfo() {
        String version = "0.1";
        String projectName = "Photo Organizer";
        String course = "Programming in Java Language - NPRG013 - student project";
        String author = "Tijana Stankovic";
        String email = "tijana.stankovic@gmail.com";
        String university = "Charles University, Faculty of Mathematics and Physics";

        System.out.println();
        System.out.println(projectName + " [v " + version + "]");
        System.out.println(course);
        System.out.println("(c) " + author + ", " + email);
        System.out.println(university);
        System.out.println();
    }
}
