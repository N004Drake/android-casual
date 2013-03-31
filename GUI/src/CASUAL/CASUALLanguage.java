/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import static CASUAL.CASUALScriptParser.ScriptContinue;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 *
 * @author adam
 */
public class CASUALLanguage {

    String ScriptName;
    private String ScriptTempFolder;

    public CASUALLanguage(String ScriptName, String ScriptTempFolder) {
        this.ScriptName = ScriptName;
        this.ScriptTempFolder = ScriptTempFolder;
    }
    Log log = new Log();
    static String GOTO = "";
    int CurrentLine = 1;

    public void beginScriptingHandler(DataInputStream dataIn) {
        String strLine = "";
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dataIn));

            bReader.mark(1);
            while (((strLine = bReader.readLine()) != null) && (ScriptContinue)) {
                CurrentLine++;
                if (Statics.useGUI) {
                    Statics.ProgressBar.setValue(CurrentLine);
                }
                if (!GOTO.equals("")) {

                    bReader.reset();
                    while (!strLine.startsWith(GOTO)) {
                        strLine = bReader.readLine();
                    }
                    GOTO = "";
                }

                commandHandler(strLine);
            }
            //Close the input stream
            dataIn.close();
            log.level0Error("done");
        } catch (Exception e) {//Catch exception if any
            log.errorHandler(e);
            log.errorHandler(new RuntimeException("CASUAL scripting error\n   " + strLine, e));
            log.level0Error("CASUAL experienced an error while parsing command:\n" + strLine + "\nplease report the above exception.");
        }

    }

    public String commandHandler(String line) {
        log.level4Debug("new command: " + line);//log line
        line = StringOperations.removeLeadingSpaces(line);// prepare line for parser


        /*OPERATING SYSTEM COMMANDS
         * $WINDOWS/$LINUX/$MAC
         * checks if the operating system is Windows, Linux Or Mac
         * if it is, it will execute the commands
         * Command may include $HALT and any other command like $ECHO
         */
        if (line.startsWith("$LINUXMAC")) {
            if (!Statics.isLinux() && !Statics.isMac()) {
                return "";
            } else {
                String removeCommand = "$LINUXMAC";
                line = removeCommandAndContinue(removeCommand, line);
                log.progress("Linux Or Mac Detected: ");
                log.level4Debug("OS IS LINUX or MAC! remaining commands:" + line);

            }
        }
        if (line.startsWith("$LINUXWINDOWS")) {
            if (Statics.isLinux() || Statics.isWindows()) {
                String removeCommand = "$LINUXWINDOWS";
                line = removeCommandAndContinue(removeCommand, line);
                log.progress("Windows or Linux Detected: ");
                log.level4Debug("OS IS WINDOWS OR LINUX! remaining commands:" + line);

            } else {
                return "";
            }
        }
        if (line.startsWith("$WINDOWSMAC")) {
            if (Statics.isLinux()) {
                String removeCommand = "$LINUXMAC";
                line = removeCommandAndContinue(removeCommand, line);
                log.progress("Mac or Windows Detected: ");
                log.level4Debug("OS IS Windows or Mac! remaining commands:" + line);

            } else {
                return "";
            }
        }
        if (line.startsWith("$LINUX")) {
            if (Statics.isLinux()) {
                String removeCommand = "$LINUX";
                line = removeCommandAndContinue(removeCommand, line);
                log.progress("Linux Detected: ");
                log.level4Debug("OS IS LINUX! remaining commands:" + line);

            } else {
                return "";
            }
        }
        if (line.startsWith("$WINDOWS")) {
            if (Statics.isWindows()) {
                log.progress("Windows Detected: ");
                String removeCommand = "$WINDOWS";
                line = removeCommandAndContinue(removeCommand, line);
                log.level4Debug("OS IS WINDOWS! remaining commands:" + line);
            } else {
                return "";
            }
        }
        if (line.startsWith("$MAC")) {
            if (Statics.isMac()) {
                log.progress("Mac Detected: ");
                String removeCommand = "$MAC";
                line = removeCommandAndContinue(removeCommand, line);
                log.level4Debug("OS IS MAC! remaining commands:" + line);
            } else {
                return "";
            }
        }

        /*
         * CONTROL COMMANDS
         */
//$RECALL, Last Acceptable CASUAL Value from meta, $ANY OTHER COMMAND. Will automatically halt.
   /* This is for future use not implemented yet.    if (line.startsWith("$RECALL")){
         if (Statics.SVNRevisionRequired!=0){
         Log.level3("RECALL CHECK PARSING");
         line=StringOperations.removeLeadingSpaces(line.replace("$RECALL",""));
         String splitline[] = line.split(" ");
         int recallValue=Integer.parseInt(splitline[0]);
         if (recallValue > Statics.SVNRevisionRequired){
         Log.level1("This CASUAL has been recalled on your platform");
         Log.level1("I am now attempting to bring you to the support website");
         Log.level1(Statics.supportWebsiteFromWeb);
         if (splitline.length>1){
         line="$HALT"+line.replaceFirst(Integer.toString(recallValue), "");
         new LinkLauncher().launchLink(Statics.supportWebsiteFromWeb);
         }
                    
         }
         }
            
         } else {
         return;
         }
         */
        if (line.startsWith("$HALT")) {
            ScriptContinue = false;

//$HALT $ANY OTHER COMMAND will execute any commands after the $HALT command and stop the script.
            line = line.replace("$HALT", "");
            log.level4Debug("HALT RECEIVED");
            line = StringOperations.removeLeadingSpaces(line);
            log.level4Debug("Finishing remaining commands:" + line);

        }

        if (line.startsWith("$GOTO")) {
            line = line.replace("$GOTO", "");
            GOTO = StringOperations.removeLeadingAndTrailingSpaces(line);
            return "";
        }

//$ON will trigger on an event
        //PARAM1 = Textual input event
        //PARAM2 = Command to execute
        //,= separator
        // example $ON File Not Found, $HALT
        // example $ON Permission Denied, su -c !!
        if (line.startsWith("$ON")) {
            line = line.replace("$ON", "");
            line = StringOperations.removeLeadingSpaces(line);
            String Event[] = line.split(",");
            try {
                Statics.ActionEvents.add(Event[0]);
                log.level4Debug("***NEW EVENT ADDED***");
                log.level4Debug("ON EVENT: " + Event[0]);
                Statics.ReactionEvents.add(Event[1]);
                log.level4Debug("PERFORM ACTION: " + Event[1]);
            } catch (Exception e) {
                log.errorHandler(e);

            }
            return "";

        }

        // $CLEARON will remove all actions/reactions
        if (line.startsWith("$CLEARON")) {
            Statics.ActionEvents = new ArrayList();
            Statics.ReactionEvents = new ArrayList();
            log.level4Debug("***$CLEARON RECEIVED. CLEARING ALL LOGGING EVENTS.***");
            return "";
        }

//# is a comment Disregard commented lines
        if (line.startsWith("#")) {
            log.level4Debug("Ignoring commented line" + line);
            return "";
        }

        //Disregard blank lines
        if (line.equals("")) {
            return "";
        }
        log.level4Debug("SCRIPT COMMAND:" + line);

        /*
         * $IFCONTAINS takes:
         * $IFCONTAINS A test string 
         * $INCOMMAND a command to be sent to ADB
         * $DO a CASUAL scripted command to be excuted if test string is found in the command sent to ADB
         */
        // $IFNOTCONTAINS foo $INCOMMAND shell "echo foo" $DO $ECHO foo is in foo so we will see this.
        // $IFNOTCONTAINS foo $INCOMMAND shell "echo bar" $DO $ECHO foo is not in bar so we will not see this.
        // $IFNOTCONTAINS value $INCOMMAND command $DO $ANY CASUAL COMMAND
        if (line.startsWith("$IFCONTAINS ")) {
            line = StringOperations.removeLeadingSpaces(line.replaceFirst("$IFCONTAINS ", ""));
            doIfContainsReturnResults(line, true);
            return "";
        }
        /*
         * $IFNOTCONTAINS takes:
         * $IFNOTCONTAINS A test string 
         * $INCOMMAND a command to be sent to ADB
         * $DO a CASUAL scripted command to be excuted if test is not found in the command sent to ADB
         */
        // $IFNOTCONTAINS foo $INCOMMAND shell "echo foo" $DO $ECHO foo is in foo so we will not see this.
        // $IFNOTCONTAINS foo $INCOMMAND shell "echo bar" $DO $ECHO foo is not in bar so we will see this.
        // $IFNOTCONTAINS value $INCOMMAND command $DO $ANY CASUAL COMMAND
        if (line.startsWith("$IFNOTCONTAINS ")) {
            line = StringOperations.removeLeadingSpaces(line.replaceFirst("$IFCONTAINS ", ""));
            doIfContainsReturnResults(line, false);
            return "";
        }
        /*
         * Environmental variables
         */

//$SLASH will replace with "\" for windows or "/" for linux and mac
        if (line.contains("$SLASH")) {
            line = line.replace("$SLASH", Statics.Slash);
            log.level4Debug("Expanded $SLASH: " + line);
        }
//$ZIPFILE is a reference to the Script's .zip file
        if (line.contains("$ZIPFILE")) {
            line = line.replace("$ZIPFILE", ScriptTempFolder);
            log.level4Debug("Expanded $ZIPFILE: " + line);
        }

        if ((line.contains("\\n")) && ((line.startsWith("$USERNOTIFICATION") || line.startsWith("$USERNOTIFICATION")) || line.startsWith("$USERCANCELOPTION"))) {
            line = line.replace("\\n", "\n");
        }
//$HOMEFOLDER will reference the user's home folder on the system        
        if (line.contains("$HOMEFOLDER")) {
            line = line.replace("$HOMEFOLDER", Statics.CASUALHome);
            log.level4Debug("Expanded $HOMEFOLDER" + line);
        }



        /*
         * GENERAL PURPOSE COMMANDS
         */
//$ECHO command will display text in the main window
        if (line.startsWith("$ECHO")) {
            log.level4Debug("Received ECHO command" + line);
            line = line.replace("$ECHO", "");
            line = StringOperations.removeLeadingSpaces(line);
            log.Level1Interaction(line);
            return "";
//$LISTDIR will a folder on the host machine  Useful with $ON COMMAND
        } else if (line.startsWith("$LISTDIR")) {
            line = line.replace("$LISTDIR", "");
            line = StringOperations.removeLeadingSpaces(line);
            File[] files = new File(line).listFiles();
            for (int i = 0; i <= files.length; i++) {
                try {
                    commandHandler("shell \"echo " + files[i].getCanonicalPath() + "\"");
                } catch (IOException ex) {
                    log.errorHandler(ex);
                }
            }
// $MAKEDIR will make a folder
        } else if (line.startsWith("$MAKEDIR")) {
            line = line.replaceFirst("$MAKEDIR", "");
            line = StringOperations.removeLeadingSpaces(line);
            log.level4Debug("Creating Folder: " + line);
            new File(line).mkdirs();
            return "";


//$USERNOTIFICATION will stop processing and force the user to 
            // press OK to continueNotification 
        } else if (line.startsWith("$USERNOTIFICATION")) {
            if (CASUALPackageData.useSound) {
                AudioHandler.playSound("/CASUAL/resources/sounds/Notification.wav");
            }
            line = line.replace("$USERNOTIFICATION", "");
            line = StringOperations.removeLeadingSpaces(line);
            if (line.contains(",")) {
                String[] Message = line.split(",");
                log.level4Debug("Displaying Notification--" + Message[1]);
                JOptionPane.showMessageDialog(Statics.GUI,
                        Message[1],
                        Message[0],
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(Statics.GUI,
                        line,
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            return "";

// $USERCANCELOPTION will give the user the option to halt the script
            //USE: $USERCANCELOPTION Message
            //USE: $USERCANCELOPTION Title, Message
        } else if (line.startsWith("$USERCANCELOPTION")) {
            if (CASUALPackageData.useSound) {
                //CASUALAudioSystem CAS = new CASUALAudioSystem();
                AudioHandler.playSound("/CASUAL/resources/sounds/RequestToContinue.wav");
            }
            line = StringOperations.removeLeadingSpaces(line.replace("$USERCANCELOPTION", ""));
            if (line.contains(",")) {
                String[] Message = line.split(",");
                Object[] Options = {"Stop",
                    "Continue"};
                int n = JOptionPane.showOptionDialog(
                        Statics.GUI,
                        Message[1],
                        Message[0],
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Options,
                        Options[1]);
                if (n == JOptionPane.YES_OPTION) {
                    log.level0Error(ScriptName + " canceled at user request");
                    ScriptContinue = false;
                    return "";
                }
            } else {
                int n = JOptionPane.showConfirmDialog(
                        Statics.GUI,
                        line,
                        "Do you wish to continue?",
                        JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    log.level0Error(ScriptName + " canceled at user request");
                    ScriptContinue = false;
                    return "";
                }
            }




//$ACTIONREQUIRED Message            

        } else if (line.startsWith("$ACTIONREQUIRED")) {
            if (CASUALPackageData.useSound) {
                //CASUALAudioSystem CAS = new CASUALAudioSystem();
                AudioHandler.playSound("/CASUAL/resources/sounds/UserActionIsRequired.wav");
            }
            line = StringOperations.removeLeadingSpaces(line.replace("$ACTIONREQUIRED", ""));
            line = line.replaceAll("\\n", "\n");


            Object[] Options = {"I didn't do it",
                "I did it"};
            int n = JOptionPane.showOptionDialog(
                    null,
                    new StringBuilder(line),
                    "Dont click through this!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    Options,
                    Options[1]);
            if (n == JOptionPane.YES_OPTION) {
                log.level0Error(ScriptName + " Halted.  Perform sthe required actions to continue.");
                ScriptContinue = false;
                return "";
            }

//$USERINPUTBOX will accept a String to be injected into ADB
            //Any text will be injected into the $USERINPUT variable    
            //USE: $USERINPUTBOX Title, Message, command $USERINPUT
        } else if (line.startsWith("$USERINPUTBOX")) {
            AudioHandler.playSound("/CASUAL/resources/sounds/InputRequested.wav");
            line = line.replace("\\n", "\n");
            String[] Message = line.replace("$USERINPUTBOX", "").split(",");
            String InputBoxText = JOptionPane.showInputDialog(null, Message[1], Message[0], JOptionPane.QUESTION_MESSAGE);
            InputBoxText = returnSafeCharacters(InputBoxText);


            log.level4Debug(InputBoxText);
            doShellCommand(Message[2], "$USERINPUT", InputBoxText);
            return "";
//$DOWNLOAD from, to, friendly download name,  Optional standard LINUX MD5 command ouptut.
        } else if (line.startsWith("$DOWNLOAD")) {
            line = line.replaceFirst("$DOWNLOAD", "");
            line = StringOperations.removeLeadingSpaces(line);
            String[] downloadCommand = line.split(",");
            FileOperations fo = new FileOperations();
            log.level4Debug("Downloading " + downloadCommand[2]);
            log.level4Debug("From " + downloadCommand[0]);
            log.level4Debug("to " + downloadCommand[1]);
            if (!fo.verifyExists(Statics.TempFolder + "download" + Statics.Slash)) {
                fo.makeFolder(Statics.TempFolder + "download" + Statics.Slash);
            }
            if (downloadCommand.length == 2) {
                new CASUALUpdates().downloadFileFromInternet(downloadCommand[0], downloadCommand[1], downloadCommand[2]);
                return "";
            } else if (downloadCommand.length == 3) {
                new CASUALUpdates().downloadFileFromInternet(downloadCommand[0], downloadCommand[1], downloadCommand[2]);
                if (!new MD5sum().compareMD5StringsFromLinuxFormatToFilenames(new String[]{downloadCommand[3]}, new String[]{downloadCommand[1]})) {
                    new CASUALScriptParser().executeOneShotCommand("$HALT HALTING Downloaded md5sum did not check out");
                }
                return "";
            } else {
                log.level0Error("Invalid download command");
                return "Invalid Download Command";
            }

//$EXECUTE will blindly execute commands into the shell.  Usefull only with $LINUX $WINDOWS or $MAC commands.
        } else if (line.startsWith("$EXECUTE")) {
            line = StringOperations.removeLeadingSpaces(line.replace("$EXECUTE", ""));
            ArrayList command = new ShellTools().parseCommandLine(line);
            String[] commandArray = Arrays.copyOf(command.toArray(), command.size(), String[].class);
            return new Shell().sendShellCommand(commandArray);









            /*
             * SUPPORTED SHELLS
             */
// if Heimdall, Send to Heimdall shell command
        } else if (line.startsWith("$HEIMDALL")) {
            line = line.replace("$HEIMDALL", "");
            line = StringOperations.removeLeadingSpaces(line);

            if (Statics.checkAndDeployHeimdall()) {
                new HeimdallTools().doHeimdallWaitForDevice();
                /* if (Statics.isLinux()) {   //Is this needed?
                 doElevatedHeimdallShellCommand(line);
                 }*/
                return new HeimdallTools().doHeimdallShellCommand(line);
            } else {
                return new CASUALScriptParser().executeOneShotCommand("$HALT $ECHO You must install Heimdall!");
            }
// if Fastboot, Send to fastboot shell command
        } else if (line.startsWith("$FASTBOOT")) {
            line = line.replace("$FASTBOOT", "");
            line = StringOperations.removeLeadingSpaces(line);
            Statics.checkAndDeployFastboot();
            if (Statics.isLinux()) {
                if (CASUALPackageData.useSound) {
                    AudioHandler.playSound("/CASUAL/resources/sounds/PermissionEscillation.wav");
                }
                String returnValue = new FastbootTools().doElevatedFastbootShellCommand(line.replaceAll("\"", "\\\""));
                if (!returnValue.contentEquals("\n")) {
                    return returnValue;
                }
            }
            return new FastbootTools().doFastbootShellCommand(line);

            // if Fastboot, Send to fastboot shell command
        } else if (line.startsWith("$ADB")) {
            line = line.replace("$ADB", "");
            line = StringOperations.removeLeadingSpaces(line);
            String retVal = doShellCommand(line, null, null);
            log.level4Debug("return from ADB:" + retVal);
            return retVal;
// if no prefix, then send command directly to ADB.
        } else {
            String retVal = doShellCommand(line, null, null);
            log.level4Debug("return from ADB:" + retVal);
            return retVal;
        }
        //final line output for debugging purposes
        log.level4Debug("COMMAND processed - " + Statics.AdbDeployed + " " + line);
        return "";
    }
//END OF SCRIPT PARSER

    private String removeCommandAndContinue(String remove, String line) {
        line = line.replace(remove, "");
        log.level4Debug("Removed " + remove);
        line = StringOperations.removeLeadingSpaces(line);
        return line;
    }

    private String returnSafeCharacters(String Str) {
        Str = Str.replace("\\", "\\\\");
        Str = Str.replace("\"", "\\\"");
        Str = Str.replace("\'", "\\\'");

        return Str;
    }

    //split the string from $IFCONTAINS "string string" $INCOMMAND "$ADB command to execute" $DO "CASUAL COMMAND"
    private void doIfContainsReturnResults(String line, boolean ifContains) {
        if (line.startsWith("$IFCONTAINS")) {
            line = StringOperations.removeLeadingSpaces(line.replaceFirst("\\$IFCONTAINS", ""));
        } else if (line.startsWith("$IFNOTCONTAINS")) {
            line = StringOperations.removeLeadingSpaces(line.replaceFirst("\\$IFNOTCONTAINS", ""));
        }
        String[] checkValueSplit = line.split("\\$INCOMMAND");
        String checkValue = StringOperations.removeLeadingAndTrailingSpaces(checkValueSplit[0].replace("\\$INCOMMAND", line)); //value to check
        String[] commandSplit = checkValueSplit[1].split("\\$DO");
        String command = StringOperations.removeLeadingAndTrailingSpaces(commandSplit[0]);//command to check
        String casualCommand = StringOperations.removeLeadingAndTrailingSpaces(commandSplit[1]);// command to execute if true
        if (command.startsWith("$ADB")) {
            command = command.replaceFirst("\\$ADB", "");
        }
        log.level4Debug("checking for results to be " + ifContains);
        log.level4Debug("requesting " + command);
        String returnValue = new CASUALScriptParser().executeOneShotCommand(command);
        log.level4Debug("got " + returnValue);
        if ((returnValue.contains(checkValue) == ifContains)) {
            new CASUALScriptParser().executeOneShotCommand(StringOperations.removeLeadingAndTrailingSpaces(casualCommand));
        }
    }

    private void sleepForOneSecond() {
        try {
            Thread.sleep(1000);
            log.progress(".");
        } catch (InterruptedException ex) {
            log.errorHandler(ex);
        }
    }

    private String doShellCommandWithReturnIgnoreError(String Line, String ReplaceThis, String WithThis) {
        return executeShellCommand(Line, ReplaceThis, WithThis, false);
    }

    private String doShellCommand(String Line, String ReplaceThis, String WithThis) {
        return executeShellCommand(Line, ReplaceThis, WithThis, true);
    }

    private String doShellCommandWithReturn(String Line, String ReplaceThis, String WithThis) {
        return executeShellCommand(Line, ReplaceThis, WithThis, true);
    }

    /*
     * doShellCommand is the point where the shell is activated ReplaceThis
     * WithThis allows for a last-minute insertion of commands by default
     * ReplaceThis should be null.
     */
    private String executeShellCommand(String Line, String ReplaceThis, String WithThis, boolean parseError) {
        Line = StringOperations.removeLeadingSpaces(Line);

        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList();
        ShellCommand.add(Statics.AdbDeployed);
        ShellCommand.addAll(new ShellTools().parseCommandLine(Line));
        String StringCommand[] = (StringOperations.convertArrayListToStringArray(ShellCommand));
        if (ReplaceThis != null) {
            for (int i = 0; i < StringCommand.length; i++) {
                StringCommand[i] = StringCommand[i].replace(ReplaceThis, WithThis);
            }
        }
        log.level4Debug("sending");
        if (parseError) {
            return Shell.sendShellCommand(StringCommand);
        } else {
            return Shell.sendShellCommandIgnoreError(StringCommand);
        }

    }
}
