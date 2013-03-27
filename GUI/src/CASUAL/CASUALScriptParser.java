/*CASUALScriptParser handles all script operations and language usage in CASUAL.
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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author adam
 */
public class CASUALScriptParser {

    static boolean ScriptContinue = true;
    Log log = new Log();
    int LinesInScript = 0;
    int CurrentLine;
    String ScriptTempFolder = "";
    String ScriptName = "";

    /*
     * Executes a selected script as a resource reports to Log class.
     */
    public void executeSelectedScriptResource(final String script, boolean multiThreaded) {
        log.level3("Selected resource" + script);
        ScriptName = script;
        CountLines CountLines = new CountLines();
        LinesInScript = CountLines.countResourceLines(script);
        log.level3("Lines in Script " + LinesInScript);
        ScriptTempFolder = Statics.TempFolder + script + Statics.Slash;


        DataInputStream RAS = new DataInputStream(getClass().getResourceAsStream(Statics.ScriptLocation + script + ".scr"));
        executeSelectedScript(RAS, script, multiThreaded);
    }


    /*
     * executes a CASUAL script from a file Reports to Log
     *
     */
    public DataInputStream getDataStreamFromFile(String script) {
        log.level3("Selected file" + script);

        ScriptName = script;
        ScriptTempFolder = Statics.TempFolder + (new File(script).getName()) + Statics.Slash;
        LinesInScript = new CountLines().countFileLines(script + ".scr");
        log.level3("Lines in Script " + LinesInScript);

        try {
            return new DataInputStream(new FileInputStream(script + ".scr"));

        } catch (FileNotFoundException ex) {
            log.errorHandler(ex);
            return null;
        }

    }

    public void executeSelectedScriptFile(String File, String script, boolean multiThreaded) {
        DataInputStream DIS = getDataStreamFromFile(File);
        executeSelectedScript(DIS, script, multiThreaded);
    }

    /*
     * executeOneShotCommand provides a way to insert a script line.
     *
     */
    public String executeOneShotCommand(String Line) {
        //$LINE is a reference to the last line received in the shell            
        if (Line.contains("$LINE")) {
            Line = Line.replace("$LINE", Statics.LastLineReceived);
            log.level3("Executing Reaction - $LINE: " + Line);
        }
        return commandHandler(Line);
    }

    /*
     * Script Handler contains all script commands and will execute commands
     */
    private String commandHandler(String line) {
        log.level3("new command: " + line);//log line
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
                log.level3("OS IS LINUX or MAC! remaining commands:" + line);

            }
        }
        if (line.startsWith("$LINUXWINDOWS")) {
            if (Statics.isLinux() || Statics.isWindows()) {
                String removeCommand = "$LINUXWINDOWS";
                line = removeCommandAndContinue(removeCommand, line);
                log.progress("Windows or Linux Detected: ");
                log.level3("OS IS WINDOWS OR LINUX! remaining commands:" + line);

            } else {
                return "";
            }
        }
        if (line.startsWith("$WINDOWSMAC")) {
            if (Statics.isLinux()) {
                String removeCommand = "$LINUXMAC";
                line = removeCommandAndContinue(removeCommand, line);
                log.progress("Mac or Windows Detected: ");
                log.level3("OS IS Windows or Mac! remaining commands:" + line);

            } else {
                return "";
            }
        }
        if (line.startsWith("$LINUX")) {
            if (Statics.isLinux()) {
                String removeCommand = "$LINUX";
                line = removeCommandAndContinue(removeCommand, line);
                log.progress("Linux Detected: ");
                log.level3("OS IS LINUX! remaining commands:" + line);

            } else {
                return "";
            }
        }
        if (line.startsWith("$WINDOWS")) {
            if (Statics.isWindows()) {
                log.progress("Windows Detected: ");
                String removeCommand = "$WINDOWS";
                line = removeCommandAndContinue(removeCommand, line);
                log.level3("OS IS WINDOWS! remaining commands:" + line);
            } else {
                return "";
            }
        }
        if (line.startsWith("$MAC")) {
            if (Statics.isMac()) {
                log.progress("Mac Detected: ");
                String removeCommand = "$MAC";
                line = removeCommandAndContinue(removeCommand, line);
                log.level3("OS IS MAC! remaining commands:" + line);
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
            log.level3("HALT RECEIVED");
            line = StringOperations.removeLeadingSpaces(line);
            log.level3("Finishing remaining commands:" + line);

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
                log.level3("***NEW EVENT ADDED***");
                log.level3("ON EVENT: " + Event[0]);
                Statics.ReactionEvents.add(Event[1]);
                log.level3("PERFORM ACTION: " + Event[1]);
            } catch (Exception e) {
                log.errorHandler(e);

            }
            return "";

        }

        // $CLEARON will remove all actions/reactions
        if (line.startsWith("$CLEARON")) {
            Statics.ActionEvents = new ArrayList();
            Statics.ReactionEvents = new ArrayList();
            log.level3("***$CLEARON RECEIVED. CLEARING ALL LOGGING EVENTS.***");
            return "";
        }

//# is a comment Disregard commented lines
        if (line.startsWith("#")) {
            log.level3("Ignoring commented line" + line);
            return "";
        }

        //Disregard blank lines
        if (line.equals("")) {
            return "";
        }
        log.level3("SCRIPT COMMAND:" + line);

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
            log.level3("Expanded $SLASH: " + line);
        }
//$ZIPFILE is a reference to the Script's .zip file
        if (line.contains("$ZIPFILE")) {
            line = line.replace("$ZIPFILE", ScriptTempFolder);
            log.level3("Expanded $ZIPFILE: " + line);
        }

        if ((line.contains("\\n")) && ((line.startsWith("$USERNOTIFICATION") || line.startsWith("$USERNOTIFICATION")) || line.startsWith("$USERCANCELOPTION"))) {
            line = line.replace("\\n", "\n");
        }
//$HOMEFOLDER will reference the user's home folder on the system        
        if (line.contains("$HOMEFOLDER")) {
            line = line.replace("$HOMEFOLDER", Statics.CASUALHome);
            log.level3("Expanded $HOMEFOLDER" + line);
        }



        /*
         * GENERAL PURPOSE COMMANDS
         */
//$ECHO command will display text in the main window
        if (line.startsWith("$ECHO")) {
            log.level3("Received ECHO command" + line);
            line = line.replace("$ECHO", "");
            line = StringOperations.removeLeadingSpaces(line);
            log.level1(line);
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
            log.level3("Creating Folder: " + line);
            new File(line).mkdirs();
            return "";


//$USERNOTIFICATION will stop processing and force the user to 
            // press OK to continueNotification 
        } else if (line.startsWith("$USERNOTIFICATION")) {
            if (CASUALPackageData.useSound) {
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/Notification.wav");
            }
            line = line.replace("$USERNOTIFICATION", "");
            line = StringOperations.removeLeadingSpaces(line);
            if (line.contains(",")) {
                String[] Message = line.split(",");
                log.level3("Displaying Notification--" + Message[1]);
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
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/RequestToContinue.wav");
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
                    log.level0(ScriptName + " canceled at user request");
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
                    log.level0(ScriptName + " canceled at user request");
                    ScriptContinue = false;
                    return "";
                }
            }




//$ACTIONREQUIRED Message            

        } else if (line.startsWith("$ACTIONREQUIRED")) {
            if (CASUALPackageData.useSound) {
                //CASUALAudioSystem CAS = new CASUALAudioSystem();
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/UserActionIsRequired.wav");
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
                log.level0(ScriptName + " Halted.  Perform sthe required actions to continue.");
                ScriptContinue = false;
                return "";
            }

//$USERINPUTBOX will accept a String to be injected into ADB
            //Any text will be injected into the $USERINPUT variable    
            //USE: $USERINPUTBOX Title, Message, command $USERINPUT
        } else if (line.startsWith("$USERINPUTBOX")) {
            CASUALAudioSystem.playSound("/CASUAL/resources/sounds/InputRequested.wav");
            line = line.replace("\\n", "\n");
            String[] Message = line.replace("$USERINPUTBOX", "").split(",");
            String InputBoxText = JOptionPane.showInputDialog(null, Message[1], Message[0], JOptionPane.QUESTION_MESSAGE);
            InputBoxText = returnSafeCharacters(InputBoxText);


            log.level3(InputBoxText);
            doShellCommand(Message[2], "$USERINPUT", InputBoxText);
            return "";
//$DOWNLOAD from, to, friendly download name,  Optional standard LINUX MD5 command ouptut.
        } else if (line.startsWith("$DOWNLOAD")) {
            line = line.replaceFirst("$DOWNLOAD", "");
            line = StringOperations.removeLeadingSpaces(line);
            String[] downloadCommand = line.split(",");
            FileOperations fo = new FileOperations();
            log.level3("Downloading " + downloadCommand[2]);
            log.level3("From " + downloadCommand[0]);
            log.level3("to " + downloadCommand[1]);
            if (!fo.verifyExists(Statics.TempFolder + "download" + Statics.Slash)) {
                fo.makeFolder(Statics.TempFolder + "download" + Statics.Slash);
            }
            if (downloadCommand.length == 2) {
                new CASUALUpdates().downloadFileFromInternet(downloadCommand[0], downloadCommand[1], downloadCommand[2]);
                return "";
            } else if (downloadCommand.length == 3) {
                new CASUALUpdates().downloadFileFromInternet(downloadCommand[0], downloadCommand[1], downloadCommand[2]);
                if (!new MD5sum().compareMD5StringsFromLinuxFormatToFilenames(new String[]{downloadCommand[3]}, new String[]{downloadCommand[1]})) {
                    this.executeOneShotCommand("$HALT HALTING Downloaded md5sum did not check out");
                }
                return "";
            } else {
                log.level0("Invalid download command");
                return "Invalid Download Command";
            }

//$EXECUTE will blindly execute commands into the shell.  Usefull only with $LINUX $WINDOWS or $MAC commands.
        } else if (line.startsWith("$EXECUTE")) {
            line = StringOperations.removeLeadingSpaces(line.replace("$EXECUTE", ""));
            ArrayList command = parseCommandLine(line);
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
                this.doHeimdallWaitForDevice();
                /* if (Statics.isLinux()) {   //Is this needed?
                 doElevatedHeimdallShellCommand(line);
                 }*/
                return doHeimdallShellCommand(line);
            } else {
                return executeOneShotCommand("$HALT $ECHO You must install Heimdall!");
            }
// if Fastboot, Send to fastboot shell command
        } else if (line.startsWith("$FASTBOOT")) {
            line = line.replace("$FASTBOOT", "");
            line = StringOperations.removeLeadingSpaces(line);
            Statics.checkAndDeployFastboot();
            if (Statics.isLinux()) {
                if (CASUALPackageData.useSound) {
                    CASUALAudioSystem.playSound("/CASUAL/resources/sounds/PermissionEscillation.wav");
                }
                String returnValue= doElevatedFastbootShellCommand(line.replaceAll("\"", "\\\""));
                if (!returnValue.contentEquals("\n")) {
                    return returnValue;
                }
            }
                return doFastbootShellCommand(line);
 
            // if Fastboot, Send to fastboot shell command
        } else if (line.startsWith("$ADB")) {
            line = line.replace("$ADB", "");
            line = StringOperations.removeLeadingSpaces(line);
            String retVal= doShellCommand(line, null, null);
            log.level3("return from ADB:" + retVal);
            return retVal;
// if no prefix, then send command directly to ADB.
        } else {
            String retVal= doShellCommand(line, null, null);
            log.level3("return from ADB:" + retVal);
            return retVal;
        }
        //final line output for debugging purposes
        log.level3("COMMAND processed - " + Statics.AdbDeployed + " " + line);
        return "";
    }
//END OF SCRIPT PARSER

    private String removeCommandAndContinue(String remove, String line) {
        line = line.replace(remove, "");
        log.level3("Removed " + remove);
        line = StringOperations.removeLeadingSpaces(line);
        return line;
    }
    DataInputStream DATAIN;

    private void executeSelectedScript(DataInputStream DIS, final String script, boolean startThreaded) {
        Statics.ReactionEvents = new ArrayList();
        Statics.ActionEvents = new ArrayList();
        ScriptContinue = true;
        DATAIN = DIS;
        log.level3("Executing Scripted Datastream" + DIS.toString());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                int updateStatus;
                log.level3("CASUAL has initiated a multithreaded execution environment");
                String idStringFile = "";
                String TestString = "";
                try {
                    idStringFile = StringOperations.removeLeadingSpaces(StringOperations.convertStreamToString(getClass().getResourceAsStream(Statics.ScriptLocation + script + ".meta")));
                    TestString = StringOperations.removeLeadingSpaces(idStringFile);
                } catch (NullPointerException ex) {
                    log.level3("NO METADATA FOUND\nNO METADATA FOUND\n");
                }
                if ((TestString != null) && (Statics.getScriptLocationOnDisk(script).equals(""))) {
                    try {

                        //String[] IDStrings = CASUALIDString.split("\n");
                        updateStatus = new CASUALUpdates().checkOfficialRepo(Statics.ScriptLocation + script, TestString, idStringFile);

                        /*
                         * checks for updates returns: 0=no updates found
                         * 1=random error 2=Script Update Required 3=CASUAL
                         * update required- cannot continue. 4=download
                         * failed *
                         */
                        switch (updateStatus) {
                            //no updates found
                            case 0: //do nothing
                                break;
                            //random error with URL formatting
                            case 1: //do nothing
                                break;
                            //script update performed
                            case 2:
                                Statics.setScriptLocationOnDisk(script, Statics.TempFolder + "SCRIPTS" + Statics.Slash + script);
                                updateDataStream(Statics.getScriptLocationOnDisk(script));//switch input stream to file
                                break;
                            //CASUAL must be update    
                            case 3:
                                log.level0(Statics.updateMessageFromWeb);
                                log.level0("CASUAL has been kill-switched due to critical updates.  Please read the above message");
                                new TimeOutOptionPane().showTimeoutDialog(60, null, "CASUAL Cannot continue due to kill-switch activation.\n" + Statics.updateMessageFromWeb + "\n CASUAL will now take you to the supporting webpage.", "CRITICAL ERROR!", TimeOutOptionPane.ERROR_MESSAGE, TimeOutOptionPane.ERROR_MESSAGE, new String[]{"Take me to the Support Site"}, 0);
                                new LinkLauncher().launchLink(Statics.supportWebsiteFromWeb);
                                System.exit(0);
                                return;
                            //download error
                            case 4:
                                log.level0("There was a problem downloading the script.  Please check your internet connection and try again.");
                                //HALT script
                                return;
                            case 5:
                                log.level0("Problem downloading file from internet, please try again");
                                log.level0("Problem downloading file from internet, please try again");
                                new TimeOutOptionPane().showTimeoutDialog(60, null, "Download Failure.  CASUAL will now restart.", "CRITICAL ERROR!", TimeOutOptionPane.ERROR_MESSAGE, TimeOutOptionPane.ERROR_MESSAGE, new String[]{"OK"}, "ok");
                                try {
                                    JavaSystem.restart(new String[]{""});
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
                                }


                                //TODO stop and reset script to stock... possibly delete temp folder and restart CASUAL
                                //HALT script
                                return;
                            default: //unknown error do nothing
                                log.level0("CASUALScriptParser().executeSelectedScript: CASUAL has encountered an unknown error. Please report this.");
                                break;
                        }

                    } catch (MalformedURLException ex) {
                        log.level0("Could not find the script while trying to executeSelectedScript in CASUALScriptParser! " + script + " Please report this.");
                        log.errorHandler(ex);
                    } catch (IOException ex) {
                        log.level0("IOException occoured while trying to executeSelectedScript in CASUALScriptParser! It's likely a bad download.");
                        log.errorHandler(ex);
                    }
                }

                CurrentLine = 1;
                if (Statics.useGUI) { 
                    Statics.ProgressBar.setMaximum(LinesInScript);
                };
                log.level3("Reading datastream" + DATAIN);
                doRead(DATAIN);
                if (Statics.useGUI) Statics.GUI.enableControls(true);
                Statics.DeviceMonitor.DeviceCheck.start();
                try {
                    DATAIN.close();
                } catch (IOException ex) {
                    Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            private void updateDataStream(String script) {
                DATAIN = getDataStreamFromFile(script);
            }
        };
        if (startThreaded){
            Thread ExecuteScript = new Thread(r);
            ExecuteScript.start();
        } else {
            r.run();
        }
    }


    static String GOTO = "";

    private void doRead(DataInputStream dataIn) {
        String strLine = "";
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dataIn));

            bReader.mark(1);
            while (((strLine = bReader.readLine()) != null) && (ScriptContinue)) {
                CurrentLine++;
                if (Statics.useGUI) Statics.ProgressBar.setValue(CurrentLine);
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
            log.level0("done");
        } catch (Exception e) {//Catch exception if any
            log.errorHandler(e);
            log.errorHandler(new RuntimeException("CASUAL scripting error\n   " + strLine, e));
            log.level0("CASUAL experienced an error while parsing command:\n" + strLine + "\nplease report the above exception.");
        }

    }

    private ArrayList<String> parseCommandLine(String Line) {
        ArrayList<String> List = new ArrayList();
        Boolean SingleQuoteOn = false;
        Boolean DoubleQuoteOn = false;
        String Word = "";
        char LastChar = 0;
        char[] TestChars = {
            "\'".toCharArray()[0], //'
            "\"".toCharArray()[0], //"
            " ".toCharArray()[0], // 
            "\\".toCharArray()[0], //\
        };
        char[] CharLine = Line.toCharArray();
        for (int I = 0; I < CharLine.length; I++) {
            //If we are not double quoted, act on singe quotes
            if (!DoubleQuoteOn && CharLine[I] == TestChars[0] && LastChar != TestChars[3]) {
                //If we are single quoted and we see the last ' character;
                if (SingleQuoteOn) {
                    SingleQuoteOn = false;
                    //start single quote
                } else if (!SingleQuoteOn) {
                    SingleQuoteOn = true;
                }
                //if we are not single quoted, act on double quotes
            } else if (!SingleQuoteOn && CharLine[I] == TestChars[1] && LastChar != TestChars[3]) {
                //if we are doulbe quoted already and see the last character;
                if (DoubleQuoteOn) {
                    //turn doublequote off
                    DoubleQuoteOn = false;
                    //start doublequote
                } else {
                    DoubleQuoteOn = true;
                }
                //if space is detected and not single or double quoted
            } else if (!SingleQuoteOn && !DoubleQuoteOn && CharLine[I] == TestChars[2] && LastChar != TestChars[3]) {
                List.add(Word);
                Word = "";
                //Otherwise add it to the string
            } else {
                Word = Word + String.valueOf(CharLine[I]);
            }
            //Annotate last char for literal character checks "\".
            LastChar = CharLine[I];
        }
        //add the last word to the list if it's not blank.
        if (!Word.equals("")) {
            List.add(Word);
        }
        return List;
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
        ShellCommand.addAll(parseCommandLine(Line));
        String StringCommand[] = (StringOperations.convertArrayListToStringArray(ShellCommand));
        if (ReplaceThis != null) {
            for (int i = 0; i < StringCommand.length; i++) {
                StringCommand[i] = StringCommand[i].replace(ReplaceThis, WithThis);
            }
        }
        log.level3("sending");
        if (parseError){
            return Shell.sendShellCommand(StringCommand);
        } else {
            return Shell.sendShellCommandIgnoreError(StringCommand);
        }

    }

    private String doFastbootShellCommand(String Line) {
        Line = StringOperations.removeLeadingSpaces(Line);

        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList();
        ShellCommand.add(Statics.fastbootDeployed);
        ShellCommand.addAll(this.parseCommandLine(Line));
        String StringCommand[] = (StringOperations.convertArrayListToStringArray(ShellCommand));
        return Shell.liveShellCommand(StringCommand);
    }

    private String doElevatedFastbootShellCommand(String Line) {
        Line = StringOperations.removeLeadingSpaces(Line);

        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList();
        ShellCommand.add(Statics.fastbootDeployed);
        ShellCommand.addAll(this.parseCommandLine(Line));
        String StringCommand[] = (StringOperations.convertArrayListToStringArray(ShellCommand));
        String returnval = Shell.elevateSimpleCommandWithMessage(StringCommand, "CASUAL uses root to work around fastboot permissions.  Hit cancel if you have setup your UDEV rules.");
        return returnval;
    }

    private void doHeimdallWaitForDevice() {
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.add("detect");
        String stringCommand[] = (StringOperations.convertArrayListToStringArray(shellCommand));
        log.progress("Waiting for Downoad Mode device.");
        String shellReturn="";
        Timer connectionTimer = new Timer(90000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new TimeOutOptionPane().showTimeoutDialog(60, null, "It would appear that the connected device is not recognized.\n"
                        + "The device should be in download mode.. Is it?.\n\n"
                        + "If it's download mode, use a different USB port.\n"
                        + "Don't use a USB hub.  Also, the USB ports behind\n"
                        + "the computer are better than the front.\n",
                        "I don't see the device", TimeOutOptionPane.OK_OPTION, 2, new String[]{"I did it"} , 0);
            }
        });
        connectionTimer.start();
        //Start timer  wait(90000) and recommend changing USB ports
        while (! shellReturn.contains("Device detected")) {
            shellReturn=Shell.silentShellCommand(stringCommand);
        }
        connectionTimer.stop();
        log.level0("detected!");
    }

    private void sleepForOneSecond(){
            try {
                Thread.sleep(1000);
                log.progress(".");
            } catch (InterruptedException ex) {
                log.errorHandler(ex);
            }
    }
    private String doHeimdallShellCommand(String Line) {
        Line = StringOperations.removeLeadingSpaces(Line);
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.addAll(this.parseCommandLine(Line));
        String stringCommand2[] = StringOperations.convertArrayListToStringArray(shellCommand);
        Statics.ExectingHeimdallCommand = true;
        String returnRead=Shell.liveShellCommand(stringCommand2);
        if (returnRead.contains("libusb error: -3") && Statics.isLinux()){
             log.level0("#A permissions error was detected.  Elevating permissions.");
             this.doElevatedHeimdallShellCommand(Line);
        }
        Statics.ExectingHeimdallCommand = false;
        return returnRead;
    }


    private String doElevatedHeimdallShellCommand(String Line) {
        Line = StringOperations.removeLeadingSpaces(Line);
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.addAll(this.parseCommandLine(Line));
        String stringCommand2[] = StringOperations.convertArrayListToStringArray(shellCommand);
        String returnval = Shell.elevateSimpleCommandWithMessage(stringCommand2, "CASUAL uses root to work around Heimdall permissions.  Hit cancel if you have setup your UDEV rules.");
        return returnval;
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
        log.level3("checking for results to be " + ifContains);
        log.level3("requesting " + command);
        String returnValue = executeOneShotCommand(command);
        log.level3("got " + returnValue);
        if ((returnValue.contains(checkValue) == ifContains)) {
            this.executeOneShotCommand(StringOperations.removeLeadingAndTrailingSpaces(casualCommand));
        }
    }
}
