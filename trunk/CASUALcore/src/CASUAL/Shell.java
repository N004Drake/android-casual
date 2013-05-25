/*Shell provides a set of shell tools. 
 *Copyright (C) 2013  Adam Outler
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package CASUAL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
//define <output and input> to this abstract class
public class Shell implements Runnable {

    //for internal access
    public Shell() {
    }
    //for external access
    Log log = new Log();
    //Send a command to the shell

    public String elevateSimpleCommandWithMessage(String[] cmd, String message) {
        return elevateSimpleCommands(cmd, message);
    }

    public String elevateSimpleCommand(String[] cmd) {
        return elevateSimpleCommands(cmd, null);

    }

    private String elevateSimpleCommands(String[] cmd, String message) {
        FileOperations FileOperations = new FileOperations();
        Shell Shell = new Shell();
        String Result = "";


        String Command = "";
        for (int i = 0; i < cmd.length; i++) {
            Command = Command + "\"" + cmd[i] + "\" ";
        }

        String[] newCmd;
        if (Statics.isLinux()) {
            //TODO: better testing for GKSU
            //TODO: possibly switch to pkexec
            String[] TestGKSudo = {"which", "gksudo"};
            String TestReturn = Shell.sendShellCommand(TestGKSudo);
            if ((TestReturn.contains("CritERROR!!!") || (TestReturn.equals("")))) {
                CASUALInteraction TO = new CASUALInteraction();
                TO.showTimeoutDialog(60, null, "Please install package 'gksudo'", "GKSUDO NOT FOUND", CASUALInteraction.OK_OPTION, CASUALInteraction.ERROR_MESSAGE, null, null);
            }


            String ScriptFile = Statics.TempFolder + "ElevateScript.sh";
            FileOperations.deleteFile(ScriptFile);
            try {
                FileOperations.writeToFile(Command, ScriptFile);
            } catch (IOException ex) {
                log.errorHandler(ex);
            }
            FileOperations.setExecutableBit(ScriptFile);
            log.level4Debug("###Elevating Command: " + Command + " ###");
            if (message == null) {
                Result = Shell.liveShellCommand(new String[]{"gksudo", "-k", "-D", "CASUAL", ScriptFile}, true);
            } else {
                Result = Shell.liveShellCommand(new String[]{"gksudo", "--message", message, "-k", "-D", "CASUAL", ScriptFile}, true);
            }

        } else if (Statics.isMac()) {
            String ScriptFile = Statics.TempFolder + "ElevateScript.sh";
            try {
                FileOperations.writeToFile(""
                        + "#!/bin/sh \n"
                        + "export bar=" + Command + " ;\n"
                        + "for i in \"$@\"; do export bar=\"$bar '${i}'\";done;\n"
                        + "osascript -e \'do shell script \"$bar\" with administrator privileges\'", ScriptFile);
                log.level3Verbose(ScriptFile);
            } catch (IOException ex) {
                log.errorHandler(ex);
            }
            FileOperations.setExecutableBit(ScriptFile);
            String[] MacCommand = {ScriptFile};
            Result = liveShellCommand(MacCommand, true);
        } else if (!Statics.OSName.equals("Windows XP")) {
            newCmd = new String[cmd.length + 2];
            newCmd[0] = Statics.WinElevatorInTempFolder;
            newCmd[1] = "-wait";
            
            //check if a virus scanner trashed CASUAL's Elevate.exe file. 
            if (! new FileOperations().verifyExists(Statics.WinElevatorInTempFolder)){
                new CASUALInteraction().showUserCancelOption("It has been detected that CASUAL's\nconsistancy has been compromised.\nThis is likely the work of a virus\nscanner.  It is recommended to disable\nvirus scanners and redownload CASUAL.");
            }
            
            for (int i = 2; i < cmd.length + 2; i++) {
                newCmd[i] = cmd[i - 2] + " ";
            }

            Result = liveShellCommand(newCmd, true);

        }

        return Result;
    }

    public String sendShellCommand(String[] cmd) {
        log.level4Debug("###executing: " + cmd[0] + "###");
        String AllText = "";
        try {
            String line;
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader STDERR = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            /*try {
             process.waitFor();
             } catch (InterruptedException ex) {
             log.errorHandler(ex);
             }*/
            log.level3Verbose(STDOUT.readLine());
            int y = 0;
            while ((line = STDOUT.readLine()) != null) {
                if (y == 0) {
                    AllText = AllText + "\n" + line + "\n"; //Sloppy Fix, ensures first line of STDOUT is written to a newline
                } else {
                    AllText = AllText + line + "\n";
                }
                y++;
            }
            y = 0;
            while ((line = STDERR.readLine()) != null && !line.equals("")) {
                if (y == 0) {
                    AllText = AllText + "\n" + line + "\n"; //Sloppy Fix, ensures first line of STDERR is written to a newline
                } else {
                    AllText = AllText + line + "\n";
                }
                y++;
            }
            //log.level0(cmd[0]+"\":"+AllText);
            return AllText + "\n";
        } catch (Exception ex) {
            log.level0Error("Problem while executing" + arrayToString(cmd)
                    + " in Shell.sendShellCommand() Received " + AllText);
            return "CritERROR!!!";
        }

    }

    public String sendShellCommandIgnoreError(String[] cmd) {
        log.level4Debug("\n###executing: " + cmd[0] + "###");
        String AllText = "";
        try {
            String line;
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = STDOUT.readLine()) != null) {
                AllText = AllText + line + "\n";
            }
            //log.level0(cmd[0]+"\":"+AllText);
            return AllText + "\n";
        } catch (Exception ex) {
            log.level0Error("Problem while executing" + arrayToString(cmd)
                    + " in Shell.sendShellCommand() Received " + AllText);
            return "CritERROR!!!";
        }

    }

    public String silentShellCommand(String[] cmd) {
        String AllText = "";
        try {
            String line;
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            try {
                process.waitFor();
            } catch (InterruptedException ex) {
                log.errorHandler(ex);
            }
            while ((line = STDOUT.readLine()) != null) {

                AllText = AllText + "\n" + line;

            }
            return AllText;
        } catch (IOException ex) {
            return "CritError!!!";
        }

    }

    public String arrayToString(String[] stringarray) {
        String str = " ";
        for (int i = 0; i < stringarray.length; i++) {
            str = str + " " + stringarray[i];
        }
        log.level4Debug("arrayToString " + stringarray + " expanded to: " + str);
        return str;
    }

    private boolean testForException(Process process) {

        if (process.exitValue() >= 0) {

            return false;
        } else {
            return true;
        }


    }

    public String liveShellCommand(String[] params, boolean display) {
        String LogRead = "";
        try {
            ProcessBuilder p = new ProcessBuilder(params);
            p.redirectErrorStream(true);
            Process process = p.start();
            log.level4Debug("###executing real-time command: " + params[0] + "###");
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String LineRead = "";
            String CharRead;

            int c;
            while ((c = STDOUT.read()) > -1) {

                CharRead = Character.toString((char) c);
                LineRead = LineRead + CharRead;
                LogRead = LogRead + CharRead;
                if (display) {
                    log.progress(CharRead);
                }

                if (!Statics.ActionEvents.isEmpty() && ((LineRead.contains("\n") || LineRead.contains("\r")))) {
                    for (int i = 0; i <= Statics.ActionEvents.size() - 1; i++) {
                        if (Statics.ActionEvents != null && LineRead.contains((String) Statics.ActionEvents.get(i))) {
                            new CASUALScriptParser().executeOneShotCommand((String) Statics.ReactionEvents.get(i));
                        }
                    }
                    LineRead = "";

                }
            }

            //log.level4Debug(LogRead);

        } catch (RuntimeException | IOException ex) {
            log.errorHandler(ex);
            return LogRead;
        }
        return LogRead;
    }

    public void liveBackgroundShellCommand() {


        Runnable r = new Runnable() {
            @Override
            public void run() {
                boolean LinkLaunched = false;
                try {
                    String[] params = Statics.LiveSendCommand.toArray(new String[Statics.LiveSendCommand.size()]);
                    log.level4Debug("###executing real-time background command: " + params[0] + "###");
                    Process process = new ProcessBuilder(params).start();
                    BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader STDERR = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String LineRead = null;
                    String CharRead;
                    String LogData = "";
                    boolean ResetLine = false;
                    int c;
                    while ((c = STDOUT.read()) > -1) {
                        if (ResetLine) {
                            log.beginLine();
                            ResetLine = !ResetLine;
                        }
                        CharRead = Character.toString((char) c);
                        LineRead = LineRead + CharRead;
                        log.progress(CharRead);
                        LogData = LogData + CharRead.toString();
                    }
                    while ((LineRead = STDERR.readLine()) != null) {
                        log.progress(LineRead);
                    }
                    new Log().level2Information(LogData);

                } catch (IOException ex) {
                    String[] ArrayList = Statics.LiveSendCommand.toArray(new String[Statics.LiveSendCommand.size()]);
                    log.level2Information("Problem while executing" + ArrayList
                            + " in Shell.liveShellCommand()");
                    Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    public void silentBackgroundShellCommand() {


        Runnable r = new Runnable() {
            @Override
            public void run() {
                boolean LinkLaunched = false;
                try {
                    String[] params = Statics.LiveSendCommand.toArray(new String[Statics.LiveSendCommand.size()]);
                    ProcessBuilder pb = new ProcessBuilder(params);
                    pb.redirectErrorStream(true);
                    Process process = pb.start();
                    BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String LineRead = null;
                    String CharRead;
                    String LogData = "";
                    boolean ResetLine = false;
                    int c;
                    try {
                        process.waitFor();
                    } catch (InterruptedException ex) {
                        log.errorHandler(ex);
                    }
                    while ((c = STDOUT.read()) > -1) {
                        if (ResetLine) {
                            log.beginLine();
                            ResetLine = !ResetLine;
                        }
                        CharRead = Character.toString((char) c);
                        LineRead = LineRead + CharRead;
                        //log.level3(CharRead);
                        LogData = LogData + CharRead.toString();
                    }

                    new Log().level4Debug(LogData);

                } catch (IOException ex) {
                    String[] ArrayList = Statics.LiveSendCommand.toArray(new String[Statics.LiveSendCommand.size()]);
                    log.level0Error("Problem while executing" + ArrayList
                            + " in Shell.liveShellCommand()");
                    Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}