/*CASUALLanguage is where the CASUALLanguage is interperated
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

import CASUAL.caspac.Caspac;
import CASUAL.crypto.MD5sum;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author adam
 */
public class CASUALLanguage {

    String ScriptName;
    private String ScriptTempFolder;
    final String CASUALHOME = System.getProperty("user.home") + System.getProperty("file.separator") + ".CASUAL" + System.getProperty("file.separator");
    final Caspac CASPAC;

    /**
     * instantiates CASUALLanguage with script
     *
     * @param ScriptName name of script
     * @param ScriptTempFolder temp folder to use for script
     */
    public CASUALLanguage(Caspac caspac, String ScriptName, String ScriptTempFolder) {
        this.ScriptName = ScriptName;
        this.ScriptTempFolder = ScriptTempFolder;
        this.CASPAC = caspac;
    }
    Log log = new Log();
    static String GOTO = "";
    int CurrentLine = 1;

    public CASUALLanguage(String ScriptName, String ScriptTempFolder) {
        this.ScriptName = ScriptName;
        this.ScriptTempFolder = ScriptTempFolder;
        this.CASPAC = null;
    }

    /**
     * starts the scripting handler spooler and handles flow control
     *
     * @param dataIn CASUALScript .scr file
     */
    public void beginScriptingHandler(DataInputStream dataIn) {
        String strLine = "";
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dataIn));

            bReader.mark(1);
            while ((strLine = bReader.readLine()) != null) {
                if (Statics.CASPAC.getActiveScript().scriptContinue == false) {
                    return;
                }
                CurrentLine++;
                if (Statics.useGUI) {
                    Statics.GUI.setProgressBar(CurrentLine);
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
            if (WindowsDrivers.removeDriverOnCompletion == 2) {//2 for remove driver 1 for do not remove
                log.level2Information("Removing generic USB driver as requested");
                new WindowsDrivers(2).uninstallCADI();
            }
            log.level2Information("@done");
        } catch (Exception e) {//Catch exception if any
            log.level0Error("@problemParsingScript");
            log.level0Error(strLine);
            log.errorHandler(new RuntimeException("CASUAL scripting error\n   " + strLine, e));
            log.level0Error("@problemParsingScript");
            log.level0Error(strLine);
        }

    }

    /**
     * Process a line of CASUAL script.
     *
     * @param line CASUAL line to process
     * @return value returned from CASUAL command
     */
    public String commandHandler(String line) {
        line = StringOperations.removeLeadingSpaces(line);// prepare line for parser
        if (line.equals("")) {
            //log.level4Debug("received blank line");
            return "";
        }
        /*OPERATING SYSTEM COMMANDS
         * $WINDOWS/$LINUX/$MAC
         * checks if the operating system is Windows, Linux Or Mac
         * if it is, it will execute the commands
         * Command may include $HALT and any other command like $ECHO
         */
        if (line.startsWith("$LINUXMAC")) {
            if (OSTools.isLinux() || OSTools.isMac()) {
                String removeCommand = "$LINUXMAC";
                line = processIdentifiedCommand(removeCommand, line);
                log.progress("Linux Or Mac Detected: ");
                log.level4Debug("OS IS LINUX or MAC! remaining commands:" + line);
            } else {
                return "";
            }
        }
        if (line.startsWith("$LINUXWINDOWS")) {
            if (OSTools.isLinux() || OSTools.isWindows()) {
                String removeCommand = "$LINUXWINDOWS";
                line = processIdentifiedCommand(removeCommand, line);
                log.progress("Windows or Linux Detected: ");
                log.level4Debug("OS IS WINDOWS OR LINUX! remaining commands:" + line);
            } else {
                return "";
            }
        }
        if (line.startsWith("$WINDOWSMAC")) {
            if (OSTools.isWindows() || OSTools.isMac()) {
                String removeCommand = "$WINDOWSMAC";
                line = processIdentifiedCommand(removeCommand, line);
                log.progress("Mac or Windows Detected: ");
                log.level4Debug("OS IS Windows or Mac! remaining commands:" + line);
            } else {
                return "";
            }
        }
        if (line.startsWith("$LINUX")) {
            if (OSTools.isLinux()) {
                String removeCommand = "$LINUX";
                line = processIdentifiedCommand(removeCommand, line);
                log.progress("Linux Detected: ");
                log.level4Debug("OS IS LINUX! remaining commands:" + line);
            } else {
                return "";
            }
        }
        if (line.startsWith("$WINDOWS")) {
            if (OSTools.isWindows()) {
                log.progress("Windows Detected: ");
                String removeCommand = "$WINDOWS";
                line = processIdentifiedCommand(removeCommand, line);
                log.level4Debug("OS IS WINDOWS! remaining commands:" + line);
            } else {
                return "";
            }
        }
        if (line.startsWith("$MAC")) {
            if (OSTools.isMac()) {
                log.progress("Mac Detected: ");
                String removeCommand = "$MAC";
                line = processIdentifiedCommand(removeCommand, line);
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
            Statics.CASPAC.getActiveScript().scriptContinue = false;

            //$HALT $ANY OTHER COMMAND will execute any commands after the $HALT command and stop the script.
            line = line.replace("$HALT", "");
            log.level4Debug("HALT RECEIVED");
            line = StringOperations.removeLeadingSpaces(line);
            log.level4Debug("Finishing remaining commands:" + line);

        }

//Sends the log to the pastebin account and informs user.
        //usage $SENDLOG
        if (line.startsWith("$SENDLOG")) {
            line = line.replace("$SENDLOG", "");
            line = StringOperations.removeLeadingSpaces(line);
            if (StringOperations.removeLeadingAndTrailingSpaces(line).equals("")) {
                log.level4Debug("Sendlog Command Issued!\nNo remaining commands");
            } else {
                log.level4Debug("Sendlog Command Issued!\nFinishing remaining commands:" + line);
            }
            try {
                new Pastebin().doPosting();
            } catch (IOException | URISyntaxException ex) {
                Log logThis = new Log();
                logThis.errorHandler(ex);
            }
            return "";
        }

//GOTO #commented line
        //will go to any line starting with the argumments specified
        //comments work best because they are otherwise inoperative
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
            Statics.ActionEvents = new ArrayList<>();
            Statics.ReactionEvents = new ArrayList<>();
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
            return doIfContainsReturnResults(line, true);
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
            return doIfContainsReturnResults(line, false);
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

            if (!verifyZIPFILEReferencesExist(line)) {
                return "";
            }

            line = line.replace("$ZIPFILE", ScriptTempFolder);
            log.level4Debug("Expanded $ZIPFILE: " + line);
        }

        if (line.contains("\\n") && (line.startsWith("$USERNOTIFICATION") || line.startsWith("$USERNOTIFICATION") || line.startsWith("$USERCANCELOPTION"))) {
            line = line.replace("\\n", "\n");
        }
//$HOMEFOLDER will reference the user's home folder on the system        
        if (line.contains("$HOMEFOLDER")) {
            if (!new FileOperations().verifyExists(CASUALHOME)) {
                new FileOperations().makeFolder(CASUALHOME);
            }
            line = line.replace("$HOMEFOLDER", CASUALHOME);
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
            log.level2Information(line);
            return line;
//$LISTDIR will a folder on the host machine  Useful with $ON COMMAND
        } else if (line.startsWith("$LISTDIR")) {
            line = line.replace("$LISTDIR", "");
            line = StringOperations.removeLeadingSpaces(line);
            File[] files = new File(line).listFiles();
            if (files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    try {
                        commandHandler("shell \"echo " + files[i].getCanonicalPath() + "\"");
                    } catch (IOException ex) {
                        log.errorHandler(ex);
                    }
                }
            }
// $MAKEDIR will make a folder
        } else if (line.startsWith("$MAKEDIR")) {
            line = line.replace("$MAKEDIR", "");
            line = StringOperations.removeLeadingSpaces(line);
            log.level4Debug("Creating Folder: " + line);
            new File(line).mkdirs();
            return "";


//$USERNOTIFICATION will stop processing and force the user to 
            // press OK to continueNotification 
        } else if (line.startsWith("$USERNOTIFICATION")) {
            AudioHandler.playSound("/CASUAL/resources/sounds/Notification.wav");
            line = line.replace("$USERNOTIFICATION", "");
            new CASUALInteraction(line.replaceFirst(",", ">>>")).showUserNotification();
            return "";

// $USERCANCELOPTION will give the user the option to halt the script
            //USE: $USERCANCELOPTION Message
            //USE: $USERCANCELOPTION Title, Message
        } else if (line.startsWith("$USERCANCELOPTION")) {
            //CASUALAudioSystem CAS = new CASUALAudioSystem();
            AudioHandler.playSound("/CASUAL/resources/sounds/RequestToContinue.wav");
            int n;
            line = StringOperations.removeLeadingSpaces(line.replace("$USERCANCELOPTION", ""));
            n = new CASUALInteraction(line.replaceFirst(",", ">>>")).showUserCancelOption();
            if (n == 1) {
                log.level0Error(ScriptName);
                log.level0Error("@canceledAtUserRequest");
                Statics.CASPAC.getActiveScript().scriptContinue = false;
                return "";
            }
            return "";



//$ACTIONREQUIRED Message            

        } else if (line.startsWith("$ACTIONREQUIRED")) {
            AudioHandler.playSound("/CASUAL/resources/sounds/UserActionIsRequired.wav");
            line = StringOperations.removeLeadingSpaces(line.replace("$ACTIONREQUIRED", ""));
            int n = new CASUALInteraction(line.replaceFirst(",", ">>>")).showActionRequiredDialog();
            if (n == 1) {
                log.level0Error(ScriptName);
                log.level0Error("@haltedPerformActions");
                Statics.CASPAC.getActiveScript().scriptContinue = false;
                return "";
            }
            return "";

//$USERINPUTBOX will accept a String to be injected into ADB
            //Any text will be injected into the $USERINPUT variable    
            //USE: $USERINPUTBOX Title, Message, command $USERINPUT
        } else if (line.startsWith("$USERINPUTBOX")) {
            AudioHandler.playSound("/CASUAL/resources/sounds/InputRequested.wav");
            //line = line.replace("\\n", "\n");
            String[] Message = line.replace("$USERINPUTBOX", "").split(",", 3);
            String inputBoxText = new CASUALInteraction(Message[0] + ">>>" + Message[1]).inputDialog();
            if (inputBoxText == null) {
                inputBoxText = "";
            }
            inputBoxText = returnSafeCharacters(inputBoxText);

            log.level4Debug(inputBoxText);

            String command = Message[2].replace("$USERINPUT", inputBoxText);
            new CASUALScriptParser().executeOneShotCommand(command);

            return "";
//$DOWNLOAD from, to, friendly download name,  Optional standard LINUX MD5 command ouptut.


            /*
             * BROKEN
             * [ERROR]no protocol: $DOWNLOAD android-casual.googlecode.com/svn-history/r348/trunk/GUI/src/CASUAL/AudioHandler.java
             no protocol: $DOWNLOAD android-casual.googlecode.com/svn-history/r348/trunk/GUI/src/CASUAL/AudioHandler.java
             java.net.MalformedURLException: no protocol: $DOWNLOAD android-casual.googlecode.com/svn-history/r348/trunk/GUI/src/CASUAL/AudioHandler.java

             java.net.MalformedURLException: no protocol: $DOWNLOAD android-casual.googlecode.com/svn-history/r348/trunk/GUI/src/CASUAL/AudioHandler.java
             at java.net.URL.<init>(URL.java:585)
             at java.net.URL.<init>(URL.java:482)
             at java.net.URL.<init>(URL.java:431)
             at CASUAL.CASUALUpdates.stringToFormattedURL(CASUALUpdates.java:176)
             at CASUAL.CASUALUpdates.downloadFileFromInternet(CASUALUpdates.java:120)
             at CASUAL.CASUALLanguage.commandHandler(CASUALLanguage.java:439)
             at CASUAL.CASUALLanguage.beginScriptingHandler(CASUALLanguage.java:54)
             at CASUAL.CASUALScriptParser$1.run(CASUALScriptParser.java:124)
             at java.lang.Thread.run(Thread.java:722)

             [ERROR]A critical error was encoutered.  Please copy the log from About>Show Log and report this issue 
             [ERROR]3
             3
             java.lang.ArrayIndexOutOfBoundsException: 3

             java.lang.ArrayIndexOutOfBoundsException: 3
             at CASUAL.CASUALLanguage.commandHandler(CASUALLa
             */
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
            log.level4Debug("Received Command: " + line);
            log.level4Debug("CASUALLanguage- verifying Heimdall deployment.");
            HeimdallInstall heimdallInstall = new HeimdallInstall();
            if (heimdallInstall.checkAndDeployHeimdall()) {
                new HeimdallTools("").doHeimdallWaitForDevice();
                /* if (Statics.isLinux()) {   //Is this needed?
                 doElevatedHeimdallShellCommand(line);
                 }*/
                log.level2Information("@executingHeimdall");
                return new HeimdallTools(line).doHeimdallShellCommand();
            } else {
                return new CASUALScriptParser().executeOneShotCommand("$HALT $ECHO You must install Heimdall!");
            }
// if Fastboot, Send to fastboot shell command
        } else if (line.startsWith("$FASTBOOT")) {
            line = line.replace("$FASTBOOT", "");
            line = StringOperations.removeLeadingSpaces(line);
            log.level4Debug("received fastbot command.");
            log.level4Debug("deploying fastboot.");
            FastbootTools.checkAndDeployFastboot();
            log.level2Information("@waitingForDownloadModeDevice");
            if (OSTools.isLinux()) {
                log.level2Information("@linuxPermissionsElevation");
                AudioHandler.playSound("/CASUAL/resources/sounds/PermissionEscillation.wav");
                //TODO: make something like Shell.timeoutShellCommand but it must also test for values
             /*
                 * Shell.timeOutCommand(String[] cmd, String[] values, int timeDelay)
                 * timeDelayClass{
                 * int timer=0
                 * new secondTimer(String testValues {"< waiting for device >"}, int timeDelay){
                 *    if Arrays.asList(values).contains(line)
                 *    timer++;
                 * }
                 * }
                 *   do Threaded shell command and pass line into time delay class
                 * while  timeDelayClass.timer < timeDelay && thread.isAlive()
                 *  do nothing
                 * if timer>timedelay return whatever is in que to be returned
                 */
                String returnValue = new FastbootTools().doElevatedFastbootShellCommand(line.replaceAll("\"", "\\\""));
                if (!returnValue.contentEquals("\n")) {
                    return returnValue;
                }
            } else {
                return new FastbootTools().doFastbootShellCommand(line);
            }

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
        log.level4Debug("COMMAND processed - " + ADBTools.getADBCommand() + " " + line);
        return "";
    }
//END OF SCRIPT PARSER

    private String processIdentifiedCommand(String identified, String line) {
        line = line.replace(identified, "");
        log.level4Debug("Processing " + identified);
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
    private String doIfContainsReturnResults(String line, boolean ifContains) {
        if (line.startsWith("$IFCONTAINS")) {
            line = StringOperations.removeLeadingSpaces(line.replaceFirst("\\$IFCONTAINS", ""));
        } else if (line.startsWith("$IFNOTCONTAINS")) {
            line = StringOperations.removeLeadingSpaces(line.replaceFirst("\\$IFNOTCONTAINS", ""));
        }
        String[] checkValueSplit = line.split("\\$INCOMMAND", 2);
        String checkValue = StringOperations.removeLeadingAndTrailingSpaces(checkValueSplit[0].replace("\\$INCOMMAND", line)); //value to check
        String[] commandSplit = checkValueSplit[1].split("\\$DO", 2);
        String command = StringOperations.removeLeadingAndTrailingSpaces(commandSplit[0]);//command to check
        String casualCommand = StringOperations.removeLeadingAndTrailingSpaces(commandSplit[1]);// command to execute if true
        if (command.startsWith("$ADB")) {
            command = command.replaceFirst("\\$ADB", "");
        }
        log.level4Debug("checking for results to be " + ifContains);
        log.level4Debug("requesting " + command);
        String returnValue = new CASUALScriptParser().executeOneShotCommand(command);
        log.level4Debug("got " + returnValue);
        String retValue = "";
        if (returnValue.contains(checkValue) == ifContains) {
            retValue = retValue + new CASUALScriptParser().executeOneShotCommand(StringOperations.removeLeadingAndTrailingSpaces(casualCommand));
        }
        return retValue;
    }

    private String doShellCommand(String Line, String ReplaceThis, String WithThis) {
        return executeADBCommand(Line, ReplaceThis, WithThis, true);
    }


    /*
     * doShellCommand is the point where the shell is activated ReplaceThis
     * WithThis allows for a last-minute insertion of commands by default
     * ReplaceThis should be null.
     */
    private String executeADBCommand(String Line, String ReplaceThis, String WithThis, boolean parseError) {
        Line = StringOperations.removeLeadingSpaces(Line);

        if (Line.startsWith("wait-for")) {
            log.level2Information("@waitingForDeviceToBeDetected");
        }

        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList<>();
        ShellCommand.add(ADBTools.getADBCommand());
        ShellCommand.addAll(new ShellTools().parseCommandLine(Line));
        String StringCommand[] = StringOperations.convertArrayListToStringArray(ShellCommand);
        if (ReplaceThis != null) {
            for (int i = 0; i < StringCommand.length; i++) {
                StringCommand[i] = StringCommand[i].replace(ReplaceThis, WithThis);
            }
        }
        log.level4Debug("sending");
        if (parseError) {
            return Shell.liveShellCommand(StringCommand, true);
        } else {
            return Shell.sendShellCommandIgnoreError(StringCommand);
        }

    }

    private boolean verifyFileExists(String testFileString) {
        if (new FileOperations().verifyExists(testFileString)) {
            //exists
            log.level3Verbose("verified " + testFileString + " exists");
        } else {
            return false;
        }
        return true;
    }

    private void fileNotFound() {
        int n = new CASUALInteraction("@interactionMissingFileVirusScanner").showUserCancelOption();
        if (n == 1) {
            log.level0Error(ScriptName);
            log.level0Error("@canceledDueToMissingFiles");
            Statics.CASPAC.getActiveScript().scriptContinue = false;
        }
    }

    private boolean verifyZIPFILEReferencesExist(String line) {
        //break commandline into an array of arguments
        //verify zipfile reference exists
        //allow echo of zipfile
        if (!line.startsWith("$ECHO")) {
            String[] lineArray = line.split(" ");
            //loop through line, locate positions of $ZIPFILE and test
            int pos = 0;
            while (pos < lineArray.length) {
                if (lineArray[pos].contains("$ZIPFILE")) {
                    String zipRef = lineArray[pos].replace("$ZIPFILE", ScriptTempFolder).replace("\"", "");
                    //if $ZIPFILE is a folder reference verify and continue
                    if (lineArray[pos].endsWith("$ZIPFILE\"") || lineArray[pos].equals("$ZIPFILE")) {
                        if (!verifyFileExists(zipRef)) {
                            fileNotFound();
                            return false;
                        }
                        pos++;
                        continue;
                    } else {
                        //$ZIPFILE is not a folder reference
                        //if we are at the last arg
                        if (verifyFileExists(zipRef)) {
                            pos++;
                            continue; //zipfile at the end of the line
                        }

                        boolean fileFound = false;
                        //test to end of string or until next ZIPFILE reference
                        String instanceString = zipRef;
                        for (int instancePos = pos + 1; instancePos < lineArray.length; instancePos++) {
                            if (lineArray[instancePos].contains("$ZIPFILE")) {
                                break;//new zipfile ref without previous found.
                            }
                            instanceString = instanceString + " " + lineArray[instancePos];
                            if (verifyFileExists(instanceString)) {
                                fileFound = true;
                                pos++;
                                break;
                            }
                        }
                        if (!fileFound) {
                            fileNotFound();
                        }
                    }
                }
                pos++;
            }
        }
        return true;
    }
}
