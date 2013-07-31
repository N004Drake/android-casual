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
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
//define <output and input> to this abstract class
public class Shell {

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
        if (OSTools.isLinux()) {
            //TODO: elevate shell and make static reference to it to have commands passed in
            //      elevate "sh" and pass scripts into it to be executed
            //      ensure monitoring so that we stop blocking after a certain keyword... like um.. "HOLY-GUACAMOLI-SPELLINGERROR"
            //      If elevated shell exists, use it
            //         else create elevated shell
            //      This solves fastboot issues of having multiple password entries to perform several tasks

            boolean useGKSU = true;
            String[] testGKSudo = {"which", "gksudo"};
            String testReturn = Shell.silentShellCommand(testGKSudo);
            if (testReturn.contains("CritERROR!!!") || testReturn.equals("\n") || testReturn.isEmpty()) {
                useGKSU = false;
                String[] testPKexec = {"which", "pkexec"};
                testReturn = Shell.silentShellCommand(testPKexec);
                if (testReturn.contains("CritERROR!!!") || testReturn.equals("\n") || testReturn.isEmpty()) {
                    new CASUALInteraction("@interactionPermissionNotFound").showTimeoutDialog(60, null, CASUALInteraction.OK_OPTION, CASUALInteraction.ERROR_MESSAGE, null, null);
                }
            }


            String ScriptFile = Statics.TempFolder + "ElevateScript.sh";
            FileOperations.deleteFile(ScriptFile);
            try {
                FileOperations.writeToFile("#!/bin/sh\n" + Command, ScriptFile);
            } catch (IOException ex) {
                log.errorHandler(ex);
            }
            FileOperations.setExecutableBit(ScriptFile);
            log.level4Debug("###Elevating Command: " + Command + " ###");
            Result = "";
            if (useGKSU) {
                if (message == null) {
                    Result = Shell.liveShellCommand(new String[]{"gksudo", "-k", "-D", "CASUAL", ScriptFile}, true);
                } else {
                    Result = Shell.liveShellCommand(new String[]{"gksudo", "--message", message, "-k", "-D", "CASUAL", ScriptFile}, true);
                }
            } else {
                int i = 0;
                //give the user 3 retries for password
                while (Result.equals("") || Result.contains("Error executing command as another user")) {
                    Result = Shell.liveShellCommand(new String[]{"pkexec", ScriptFile}, true);
                    i++;
                    if (Result.contains("Error executing command as another user:") && i >= 3) {
                        log.level2Information("@permissionsElevationProblem");
                        Result = Shell.liveShellCommand(new String[]{ScriptFile}, true);
                        break;
                    }
                }
            }

        } else if (OSTools.isMac()) {
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
        } else if (!OSTools.OSName().equals("Windows XP")) {
            newCmd = new String[cmd.length + 2];
            newCmd[0] = Statics.WinElevatorInTempFolder;
            newCmd[1] = "-wait";

            //check if a virus scanner trashed CASUAL's Elevate.exe file. 
            if (!new FileOperations().verifyExists(Statics.WinElevatorInTempFolder)) {
                new CASUALInteraction("@interactionCASUALCorrupt").showUserCancelOption();
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
            try {
                process.waitFor();
            } catch (InterruptedException ex) {
                log.errorHandler(ex);
            }
            //log.level3Verbose(STDOUT.readLine());
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
            log.level0Error("@problemWhileExecutingCommand " + arrayToString(cmd) + "\nreturnval:" + AllText);
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
            log.level0Error("@problemWhileExecutingCommand " + arrayToString(cmd) + "returnval:" + AllText);
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

                if (!Statics.ActionEvents.isEmpty() && LineRead.contains("\n") || LineRead.contains("\r")) {
                    for (int i = 0; i <= Statics.ActionEvents.size() - 1; i++) {
                        if (Statics.ActionEvents != null && LineRead.contains((String) Statics.ActionEvents.get(i))) {
                            new CASUALScriptParser().executeOneShotCommand((String) Statics.ReactionEvents.get(i));
                        }
                    }
                    LineRead = "";

                }
            }
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
                String LogData = "";
                try {
                    String[] params = Statics.LiveSendCommand.toArray(new String[Statics.LiveSendCommand.size()]);
                    log.level4Debug("###executing real-time background command: " + params[0] + "###");
                    Process process = new ProcessBuilder(params).start();
                    BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader STDERR = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String LineRead = null;
                    String CharRead;

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
                    log.level0Error("@problemWhileExecutingCommand " + StringOperations.convertArrayListToStringArray(Statics.LiveSendCommand) + "\nreturnval:" + LogData);
                }
            }
        };
        Thread t = new Thread(r);
        t.setName("liveBackgroundShellCommand");
        t.start();
    }

    public void silentBackgroundShellCommand() {
        Thread t = new Thread(shell);
        t.setName("Silent Background Shell Command");
        t.start();

    }
    Runnable shell = new Runnable() {
        @Override
        public void run() {
            try {
                String[] params = Statics.LiveSendCommand.toArray(new String[Statics.LiveSendCommand.size()]);
                ProcessBuilder pb = new ProcessBuilder(params);
                pb.redirectErrorStream(true);
                Process process = pb.start();
                try {
                    process.waitFor();
                } catch (InterruptedException ex) {
                    log.errorHandler(ex);
                }
            } catch (IOException ex) {
                log.level0Error("@problemWhileExecutingCommand ");
                Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    /**
     * timeoutShellCommand is a multi-threaded method and reports to the
     * TimeOutString class. The value contained within the TimeOutString class
     * is reported after the timeout elapses if the task locks up.
     *
     * @param cmd cmd to be executed
     * @param timeout in millis
     * @return any text from the command
     * @throws TimeoutException
     */
    public String timeoutShellCommand(final String[] cmd, int timeout) {
        //final object for runnable to write out to.
        final TimeoutString tos = new TimeoutString();

        //Runnable executes in the background
        Runnable runCommand = new Runnable() {
            @Override
            public void run() {
                log.level4Debug("\n###executing: " + cmd[0] + "###");
                try {
                    String line;
                    ProcessBuilder p = new ProcessBuilder(cmd);
                    p.redirectError();
                    Process process = p.start();
                    BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    while ((line = STDOUT.readLine()) != null) {
                        tos.AllText = tos.AllText + line + "\n";
                    }
                    //log.level0(cmd[0]+"\":"+AllText);
                } catch (Exception ex) {
                    log.level0Error("@problemWhileExecutingCommand " + arrayToString(cmd) + " " + tos.AllText);
                }
            }
        };
        //t executes the runnable on a different thread
        Thread t = new Thread(runCommand);
        t.setDaemon(true);
        t.setName("TimeOutShell " + cmd[0] + timeout + "ms abandon time");
        t.start();

        //set up timeout with calendar time in millis
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MILLISECOND, timeout);
        log.level4Debug("Executing timeoutShellCommand");
        log.level4Debug("The current time is: " + Calendar.getInstance().getTimeInMillis());
        log.level4Debug("The thread will end at: " + endTime.getTimeInMillis());
        //loop while not timeout and halt if thread dies. 
        while (Calendar.getInstance().getTimeInMillis() < endTime.getTimeInMillis()) {
            if (!t.isAlive()) {
                break;
            }
        }
        if (Calendar.getInstance().getTimeInMillis() >= endTime.getTimeInMillis()) {
            log.level3Verbose("TimeOut on " + cmd[0] + " after " + timeout + "ms. Returning what was received.");
            return "Timeout!!! " + tos.AllText;
        }
        //return values logged from TimeoutString class above
        return tos.AllText;

    }

    /**
     * timeoutShellCommand is a multi-threaded method and reports to the
     * TimeOutString class. The value contained within the TimeOutString class
     * is reported after the timeout elapses if the task locks up.
     *
     * @param cmd cmd to be executed
     * @param timeout in millis
     * @return any text from the command
     * @throws TimeoutException
     */
    public String silentTimeoutShellCommand(final String[] cmd, int timeout) {
        //final object for runnable to write out to.
        final TimeoutString tos = new TimeoutString();

        //Runnable executes in the background
        Runnable runCommand = new Runnable() {
            @Override
            public void run() {
                try {
                    String line;
                    ProcessBuilder p = new ProcessBuilder(cmd);
                    p.redirectError();
                    Process process = p.start();
                    BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    while ((line = STDOUT.readLine()) != null) {
                        tos.AllText = tos.AllText + line + "\n";
                    }
                    //log.level0(cmd[0]+"\":"+AllText);
                } catch (Exception ex) {
                    log.level0Error("@problemWhileExecutingCommand " + arrayToString(cmd) + " " + tos.AllText);
                }
            }
        };
        //t executes the runnable on a different thread
        Thread t = new Thread(runCommand);
        t.setDaemon(true);
        t.setName("SilentTimeOutShell " + cmd[0] + timeout + "ms abandon time");
        t.start();

        //set up timeout with calendar time in millis
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MILLISECOND, timeout);
        //loop while not timeout and halt if thread dies. 
        while (Calendar.getInstance().getTimeInMillis() < endTime.getTimeInMillis()) {
            if (!t.isAlive()) {
                break;
            }
        }
        if (Calendar.getInstance().getTimeInMillis() >= endTime.getTimeInMillis()) {
            log.level3Verbose("TimeOut on " + cmd[0] + " after " + timeout + "ms. Returning what was received.");
        }
        //return values logged from TimeoutString class above
        return tos.AllText;

    }

    /**
     * holds variables temporarily. this class is here to be made final for
     * passing objects
     */
    class TimeoutString {

        public String AllText = "";
    };
}