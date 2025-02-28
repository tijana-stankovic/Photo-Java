package cz.cuni.mff.stankoti.photo.controller;

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
        String cmd = "";
        ArrayList<String> argList = new ArrayList<>();

        try {
            // Read data from standard input
            String line = input.readLine();
            if (line != null) {
                // Split the line into words using whitespace as the delimiter
                String[] words = line.split("\\s+");
                for (String word : words) {
                    // Skip empty elements
                    if (word.isEmpty()) {
                        continue;
                    }
                    if (cmd.isEmpty()) {
                        cmd = word;
                    } else {
                        argList.add(word);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("IOException occurred");
        }

        String[] cmdArgs = new String[argList.size()];
        for (int i = 0; i < argList.size(); i++) {
            cmdArgs[i] = argList.get(i);
        }
        Command command = new Command(cmd, cmdArgs);
        return command;
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
