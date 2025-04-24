package cz.cuni.mff.stankoti.photo.controller;

import cz.cuni.mff.stankoti.photo.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
  * Command Line Interface (CLI) class with methods for handling user input.
  */
public class CLI implements AutoCloseable { // implements AutoCloseable to ensure resources are properly closed
    private BufferedReader input;

    /**
      * Creates a new CLI instance and initializes the input reader.
      */
    public CLI() {
        input = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
      * Reads a command from the user input.
      * Parses the input line into a command and its arguments.
      * Allows multi-word arguments to be entered within quotes.
      *
      * @return a Command object containing the command and its arguments
      */
    public Command readCommand() {
        ArrayList<String> argList = new ArrayList<>();
    
        try {
            // read data from standard input
            String line = input.readLine();
            if (line != null) {
                StringBuilder currentWord = new StringBuilder();
                boolean insideQuotes = false;

                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);

                    if (c == '"') {
                        if (!insideQuotes) {
                            insideQuotes = true;
                        } else {
                            insideQuotes = false;
                            // closing quote, add the quoted parameter
                            argList.add(currentWord.toString());
                            currentWord.setLength(0);
                        }
                    } else if (Character.isWhitespace(c) && !insideQuotes) {
                        if (currentWord.length() > 0) {
                            argList.add(currentWord.toString());
                            currentWord.setLength(0);
                        }
                    } else {
                        currentWord.append(c);
                    }
                }

                // if there is no closing quote, add the last word
                if (currentWord.length() > 0) {
                    argList.add(currentWord.toString());
                }
            }
        } catch (IOException e) {
            System.err.println("IOException occurred");
        }

        String cmd;
        String[] cmdArgs;
        if (argList.size() > 0) {
            cmd = argList.get(0);
            cmdArgs = new String[argList.size() - 1];
            for (int i = 1; i < argList.size(); i++) {
                cmdArgs[i - 1] = argList.get(i);
            }
        } else {
            cmd = "";
            cmdArgs = new String[0];
        }

        Command command = new Command(cmd, cmdArgs);
        return command;
    }
    
    /**
      * Asks the user a yes/no question and returns the response.
      * Optionally includes a cancel option.
      *
      * @param view the View object instance (which is used to display message)
      * @param message the message (question) to display
      * @param cancel whether to include a cancel option
      * @return 'Y' for yes, 'N' for no, 'C' for cancel (if cancel is true)
      */
    public char askYesNo(View view, String message, boolean cancel) {
        String prompt = cancel ? " (Yes/No/Cancel)" : " (Yes/No)";
        String response;

        while (true) {
            view.print(message + prompt + ": ", false);
            try {
                response = input.readLine().trim().toLowerCase();
            } catch (IOException e) {
                System.err.println("IOException occurred while reading input");
                continue;
            }

            if (response.equals("y") || response.equals("yes")) {
                return 'Y';
            } else if (response.equals("n") || response.equals("no")) {
                return 'N';
            } else if (cancel && (response.equals("c") || response.equals("cancel"))) {
                return 'C';
            } else {
                view.print("Invalid response. Please enter 'Yes', 'No'" + (cancel ? ", or 'Cancel'" : "") + ".");
            }
        }
    }

    /**
      * Closes the input reader.
      * Ensures resources are properly released.
      */
    @Override
    public void close() {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            System.err.println("IOException occurred while closing BufferedReader");
        }
    }    
}
