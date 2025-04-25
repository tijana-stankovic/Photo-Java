package cz.cuni.mff.stankoti.photo.controller;

/**
  * A class providing internal supporting structure for a user command.
  */
public class Command {
    /**
      * The command itself.
      */
    public String command;
    /**
      * The command arguments.
      */
    public String[] args;

    /**
      * Creates a new Command instance with the specified command and arguments.
      *
      * @param command the command
      * @param args the command arguments
      */
    public Command(String command, String[] args) {
        this.command = command;
        this.args = args;
    }
}
