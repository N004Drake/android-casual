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

import CASUAL.misc.StringOperations;
import CASUAL.network.CASUALUpdates;
import CASUAL.network.Pastebin;
import CASUAL.caspac.Caspac;
import CASUAL.crypto.MD5sum;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CASUALLanguage is where the CASUALLanguage is interperated
 *
 * @author Adam Outler
 */
public class CASUALLanguage {

    String ScriptName;
    final private String ScriptTempFolder;
    final String CASUALHOME = System.getProperty("user.home") + System.getProperty("file.separator") + ".CASUAL" + System.getProperty("file.separator");
    final Caspac CASPAC;
    private String deviceBuildPropStorage;

    /**
     * instantiates CASUALLanguage with script
     *
     * @param caspac the CASPAC used for the script
     * @param ScriptName name of script
     * @param ScriptTempFolder temp folder to use for script
     */
    public CASUALLanguage(Caspac caspac, String ScriptName, String ScriptTempFolder) {
        //TODO remove ScriptName as it is only used for logging.  The CASUALLanguage need not know what SCRIPT is executing. 
        this.ScriptName = ScriptName;
        this.ScriptTempFolder = ScriptTempFolder;
        this.CASPAC = caspac;
    }
    Log log = new Log();
    static String GOTO = "";
    int CurrentLine = 1;

    /**
     * Constructor for CASUALLanguage
     *
     * @param ScriptName Name of script to be executed
     * @param ScriptTempFolder Folder in which script is executing.
     */
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
                if (Statics.isGUIIsAvailable()) {
                    Statics.GUI.setProgressBar(CurrentLine);
                }
                if (!GOTO.equals("")) {

                    bReader.reset();
                    while (!strLine.startsWith(GOTO)) {
                        strLine = bReader.readLine();
                    }
                    GOTO = "";
                }
                if (strLine.contains(";;;")) {
                    String[] lineArray = strLine.split(";;;");
                    for (String line : lineArray) {
                        commandHandler(line);
                    }
                } else {
                    commandHandler(strLine);
                }
            }
            //Close the input stream
            dataIn.close();
            if (WindowsDrivers.removeDriverOnCompletion == 2) {//2 for remove driver 1 for do not remove
                log.level2Information("Removing generic USB driver as requested");
                new WindowsDrivers(2).uninstallCADI();
            }
            log.level2Information("@done");
        } catch (Exception e) {
            /*
             *  Java reports this as an overly broad catch.  Thats fine.  this is 
             *  supposed to be broad.  It is the handler for all errors during 
             *  execution of CASUAL script.  Script commands are tested for quality
             *  and errors found here will be syntax or oher scripting errors.
             *
             *  CASUAL will take the blame for the end user and the developer will
             *  see that it was a problem with their script
             */
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
            if (Statics.CASPAC != null) {
                Statics.CASPAC.getActiveScript().scriptContinue = false;
            }
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
            } catch (IOException ex) {
                new Log().errorHandler(ex);
            } catch (URISyntaxException ex) {
                new Log().errorHandler(ex);
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
            Statics.ActionEvents = new ArrayList<String>();
            Statics.ReactionEvents = new ArrayList<String>();
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
        if (line.startsWith("$SLEEP")) {
            log.level3Verbose("detected sleep command: " + line);
            int sleeptime;
            line = line.replace("$SLEEP", "").trim();
            if (line.startsWith("MILLIS")) {
                line = line.replace("MILLIS", "").trim();
                sleeptime = Integer.parseInt(line);
            } else {
                sleeptime = Integer.parseInt(line) * 1000;
            }
            if (!(Integer.parseInt(line) >= 0)) {
                throw new RuntimeException();
            }

            try {
                log.level2Information("sleeping for " + (double) sleeptime / 1000 + " seconds");
                Thread.sleep(sleeptime);
            } catch (InterruptedException ex) {
            }
            return line;

        }
        /*
         * Environmental variables
         */
//$SLASH will replace with "\" for windows or "/" for linux and mac
        if (line.contains("$BUSYBOX")) {
            line = line.replace("$BUSYBOX", BusyboxTools.getBusyboxLocation());
            log.level4Debug("Expanded $BUSYBOX: " + line);
        }

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
            if (OSTools.isLinux() || OSTools.isMac()) {
            } else {
                line = line.replace("/", Statics.Slash);
            }
            File[] files = new File(line).listFiles();
            String retval = "";
            if (files != null && files.length > 0) {
                for (File file : files) {
                    retval = retval + file.getAbsolutePath() + "\n";
                    try {
                        //TODO: create a method which will parse $ON action/reaction events.  This is currently implemented in Shell() but should be moved to ScriptParser or script spooler. 
                        //or parse $ON events from CASUALScriptParser
                        //this method can create a problem if the device is in recovery mode.
                        //the problem could be solved by creating a new method which will
                        //parse $ON action/reaction events which is the purpose for this..
                        commandHandler("shell \"echo " + file.getCanonicalPath() + "\"");
                    } catch (IOException ex) {
                        log.errorHandler(ex);
                    }
                }
            }
            return retval;

// $MAKEDIR will make a folder
        } else if (line.startsWith("$MAKEDIR")) {
            line = line.replace("$MAKEDIR", "");
            line = StringOperations.removeLeadingSpaces(line);
            log.level4Debug("Creating Folder: " + line);
            new File(line).mkdirs();
            return line;
// $REMOVEDIR will make a folder
        } else if (line.startsWith("$REMOVEDIR")) {
            line = line.replace("$REMOVEDIR", "");
            line = StringOperations.removeLeadingSpaces(line);
            log.level4Debug("Creating Folder: " + line);
            new FileOperations().recursiveDelete(line);
            return line;

// Takes a value from a command and returns to text box        
        } else if (line.startsWith("$COMMANDNOTIFICATION")) {
            line = line.replace("$COMMANDNOTIFICATION", "").trim();
            String title = "Return Value";
            String retval = commandHandler(line);
            new CASUALMessageObject(title + ">>>" + retval).showCommandNotification();
            return retval;

//$USERNOTIFICATION will stop processing and force the user to 
            // press OK to continueNotification 
        } else if (line.startsWith("$USERNOTIFICATION")) {
            Statics.GUI.notificationGeneral();
            line = line.replace("$USERNOTIFICATION", "");
            new CASUALMessageObject(line.replaceFirst(",", ">>>")).showUserNotification();
            return "";

// $USERCANCELOPTION will give the user the option to halt the script
            //USE: $USERCANCELOPTION Message
            //USE: $USERCANCELOPTION Title, Message
        } else if (line.startsWith("$USERCANCELOPTION")) {
            //CASUALAudioSystem CAS = new CASUALAudioSystem();
            Statics.GUI.notificationRequestToContinue();
            int n;
            line = StringOperations.removeLeadingSpaces(line.replace("$USERCANCELOPTION", ""));
            n = new CASUALMessageObject(line.replaceFirst(",", ">>>")).showUserCancelOption();
            if (n == 1) {
                log.level0Error(ScriptName);
                log.level0Error("@canceledAtUserRequest");
                Statics.CASPAC.getActiveScript().scriptContinue = false;
                return "";
            }
            return "";

//$ACTIONREQUIRED Message            
        } else if (line.startsWith("$ACTIONREQUIRED")) {
            Statics.GUI.notificationUserActionIsRequired();
            line = StringOperations.removeLeadingSpaces(line.replace("$ACTIONREQUIRED", ""));
            int n = new CASUALMessageObject(line.replaceFirst(",", ">>>")).showActionRequiredDialog();
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
            Statics.GUI.notificationInputRequested();
            //line = line.replace("\\n", "\n");
            String[] Message = line.replace("$USERINPUTBOX", "").split(",", 3);
            String inputBoxText = new CASUALMessageObject(Message[0] + ">>>" + Message[1]).inputDialog();
            if (inputBoxText == null) {
                inputBoxText = "";
            }
            inputBoxText = returnSafeCharacters(inputBoxText);

            log.level4Debug(inputBoxText);

            String command = Message[2].replace("$USERINPUT", inputBoxText);
            this.commandHandler(command);

            return "";
//$DOWNLOAD from, to, friendly download name,  Optional standard LINUX MD5 command ouptut.

        } else if (line.startsWith("$DOWNLOAD")) {
            line = line.replace("$DOWNLOAD", "");
            line = StringOperations.removeLeadingSpaces(line);
            String[] downloadCommand = line.split(",");
            for (int i = 0; i < downloadCommand.length; i++) {
                downloadCommand[i] = downloadCommand[i].trim();
            }
            FileOperations fo = new FileOperations();
            log.level4Debug("Downloading " + downloadCommand[2]);
            log.level4Debug("From " + downloadCommand[0]);
            log.level4Debug("to " + downloadCommand[1]);
            if (!new File(downloadCommand[1]).getParentFile().exists()) {
                new File(downloadCommand[1]).getParentFile().mkdirs();
            }

            if (downloadCommand.length == 3) {
                new CASUALUpdates().downloadFileFromInternet(downloadCommand[0], downloadCommand[1], downloadCommand[2]);
                return downloadCommand[1];
            } else if (downloadCommand.length == 4) {
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
            ArrayList<String> command = new ShellTools().parseCommandLine(line);
            String[] commandArray = Arrays.copyOf(command.toArray(), command.size(), String[].class);
            return new Shell().sendShellCommand(commandArray);

//$BUILDPROP will silently grab the build.prop from the device
        } else if (line.startsWith("$BUILDPROP")) {
            if (deviceBuildPropStorage != null && deviceBuildPropStorage.contains("ro.")) {
                return deviceBuildPropStorage;
            } else {
                String[] cmd = {ADBTools.getADBCommand(), "shell", "cat /system/build.prop"};
                deviceBuildPropStorage = new Shell().timeoutShellCommand(cmd, 5000);
                return deviceBuildPropStorage;
            }
//$FLASH will push a file to the specified block eg $FLASH $ZIPFILEmyFile, /dev/block/mmcblk0p5
        } else if (line.startsWith("$FLASH")) {

            line = line.replace("$FLASH", "").trim();
            if (!line.contains(",")) {
                log.level0Error("Missing Comma in $FLASH command");
                throw new RuntimeException("no comma to split and specify destination");
            }
            String[] split = line.split(",");
            File f = new File(split[0].replace("\"", "").trim());
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(CASUALLanguage.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                long x = new CASUALDataBridge().sendFile(f, split[1].trim());
                if (x != f.length()) {
                    new CASUALMessageObject("@interactionUltimateFlashFailure").showErrorDialog();
                }
                return "Pushed " + x + " bytes";
            } catch (FileNotFoundException ex) {
                new Log().level0Error("@fileNotFound");
                throw new RuntimeException("File not found");
            } catch (Exception ex) {
                new Log().level0Error("@failedToWriteFile");
                throw new RuntimeException("Failed to write file");
            }
//$PULL will push a file to the specified block eg $PULL  /dev/block/mmcblk0p5 , $ZIPFILEmyFile
        } else if (line.startsWith("$PULL")) {

            line = line.replace("$PULL", "").trim();
            if (!line.contains(",")) {
                log.level0Error("Missing Comma in $PULL command");
                throw new RuntimeException("no comma to split and specify destination");
            }
            String[] split = line.split(",");
            File f = new File(split[1].replace("\"", "").trim());

            new File(f.getParent()).mkdirs();
            try {
                f.createNewFile();
            } catch (IOException ex) {
            }
            return new CASUALDataBridge().integralGetFile(split[0].trim(), f);

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
                Statics.GUI.notificationPermissionsRequired();
                //Todo write checks for this
                //String retval=new Shell().timeoutValueCheckingShellCommand(new String[]{line.replaceAll("\"", "\\\"")},new String[]{"< waiting for device >"},30000);
                //if (retval.endsWith("< waiting for device >")){
                String returnValue = new FastbootTools().doElevatedFastbootShellCommand(line.replaceAll("\"", "\\\""));
                if (!returnValue.contentEquals("\n")) {
                    return returnValue;
                }
                //}

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

        //ifnotcontains==false or ifcontains==true
        if (returnValue.contains(checkValue) == ifContains) {
            if (casualCommand.contains("&&&")) {
                String[] lineSplit = casualCommand.split("&&&");
                for (String cmd : lineSplit) {
                    retValue = retValue + new CASUALScriptParser().executeOneShotCommand(StringOperations.removeLeadingAndTrailingSpaces(cmd));

                }
            } else {
                retValue = retValue + new CASUALScriptParser().executeOneShotCommand(StringOperations.removeLeadingAndTrailingSpaces(casualCommand));
            }
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
        ArrayList<String> ShellCommand = new ArrayList<String>();
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
            testFileString = testFileString.replace(",", "");
            if (new FileOperations().verifyExists(testFileString)) {
                //exists
                log.level3Verbose("verified " + testFileString + " exists");
                return true;
            }
            return false;
        }
        return true;
    }

    private void fileNotFound() {
        int n = new CASUALMessageObject("@interactionMissingFileVirusScanner").showUserCancelOption();
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
        if (!line.startsWith("$USERINPUTBOX") && !line.startsWith("$MAKEDIR") && !line.startsWith("$PULL") && !line.startsWith("$DOWNLOAD") && !line.startsWith("$ECHO") && !line.startsWith("$REMOVEDIR") && !line.startsWith("$COMMANDNOTIFICATION") && !line.startsWith("$MAKEDIR") && !line.contains(" shell echo ") && !line.startsWith("$USERNOTIFICATION") && !line.contains(" pull ")) {
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
