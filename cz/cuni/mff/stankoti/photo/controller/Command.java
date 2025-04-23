package cz.cuni.mff.stankoti.photo.controller;

/**
  * A class providing internal supporting structure for a user command.
  */
public class Command {
    public String command;
    public String[] args;

    public Command(String command, String[] args) {
        this.command = command;
        this.args = args;
    }
}
