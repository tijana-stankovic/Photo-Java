package cz.cuni.mff.stankoti.photo.controller;

public class CmdInterpreter {
    private int errorCode;
    private boolean quitFlag;

    public CmdInterpreter() {
        errorCode = 0;
        quitFlag = false;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean getQuitFlag() {
        return quitFlag;
    }

    public void setQuitFlag(boolean quitFlag) {
        this.quitFlag = quitFlag;
    }

    public boolean executeCommand(Command cmd) {
        setErrorCode(0);

        String command = cmd.command.toUpperCase();

        switch (command) {
            case "EXIT" -> Exit();

            default -> {
                System.out.println(cmd.command);
                for (String arg : cmd.args) {
                    System.out.println(arg);
                }    
            }
        }

        boolean error = false;
        if (getErrorCode() != 0)
            error = true;
        
        return error;
    }

    private void Exit() {
        setQuitFlag(true);
    }
}
