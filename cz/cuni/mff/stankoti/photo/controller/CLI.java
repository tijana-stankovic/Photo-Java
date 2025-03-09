package cz.cuni.mff.stankoti.photo.controller;

import cz.cuni.mff.stankoti.photo.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CLI implements AutoCloseable {
    private BufferedReader input;

    public CLI() {
        input = new BufferedReader(new InputStreamReader(System.in));
    }

    public Command readCommand() {
        ArrayList<String> argList = new ArrayList<>();
    
        try {
            // Read data from standard input
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
                            // Closing quote, add the quoted parameter
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

                // If there is no closing quote, add the last word
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
