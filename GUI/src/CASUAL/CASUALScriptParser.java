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

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.util.ArrayList;

import java.net.MalformedURLException;

/**
 *
 * @author adam
 */
public class CASUALScriptParser {

    static boolean ScriptContinue = true;
    Log Log = new Log();
    int LinesInScript = 0;
    int CurrentLine;
    String ScriptTempFolder = "";
    String ScriptName = "";

    /*
     * Executes a selected script as a resource reports to Log class.
     */
    public void executeSelectedScriptResource(final String script) {
        Log.level3("Selected resource" + script);
        ScriptName = script;
        CountLines CountLines = new CountLines();
        LinesInScript = CountLines.countResourceLines(script);
        Log.level3("Lines in Script " + LinesInScript);
        ScriptTempFolder = Statics.TempFolder + script + Statics.Slash;


        DataInputStream RAS = new DataInputStream(getClass().getResourceAsStream(Statics.ScriptLocation + script + ".scr"));
        executeSelectedScript(RAS, script);
    }


    /*
     * executes a CASUAL script from a file Reports to Log
     *
     */
    public DataInputStream getDataStreamFromFile(String script) {
        Log.level3("Selected file" + script);

        ScriptName = script;
        ScriptTempFolder = Statics.TempFolder + (new File(script).getName()) + Statics.Slash;
        LinesInScript = new CountLines().countFileLines(script + ".scr");
        Log.level3("Lines in Script " + LinesInScript);

        try {
            return new DataInputStream(new FileInputStream(script + ".scr"));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
            Log.level0("An unknown error has happened while trying to getDataStreamFromFile! " + script + "  Please report this");
            return null;
        }

    }

    public void executeSelectedScriptFile(String File, String script) {
        executeSelectedScript(getDataStreamFromFile(File), script);

    }

    /*
     * executeOneShotCommand provides a way to insert a script line.
     *
     */
    public void executeOneShotCommand(String Line) {
        //$LINE is a reference to the last line received in the shell            
        if (Line.contains("$LINE")) {
            Line = Line.replace("$LINE", Statics.LastLineReceived);
            Log.level3("Executing Reaction - $LINE: " + Line);
        }
        commandHandler(Line);
    }

    /*
     * Script Handler contains all script commands and will execute commands
     */
    private void commandHandler(String line) {

        
       /*
        * $WINDOWS/$LINUX/$MAC
        * checks if the operating system is Windows, Linux Or Mac
        * if it is, it will execute the commands
        * Command may include $HALT and any other command like $ECHO
        */
       if (line.startsWith("$LINUX")) {
           if (Statics.isLinux()){
                String removeCommand="$LINUX";
                line = removeCommandAndContinue(removeCommand, line);
                Log.progress("Linux Detected: ");
                Log.level3("OS IS LINUX! remaining commands:" + line);
                
           } else {
               return;
           }
       }
       if (line.startsWith("$WINDOWS")) {
           if (Statics.isWindows()){
                Log.progress("Windows Detected: ");
                String removeCommand="$WINDOWS";
                line = removeCommandAndContinue(removeCommand, line);
                Log.level3("OS IS WINDOWS! remaining commands:" + line);
           } else {
               return;
           }
       }   
       if (line.startsWith("$MAC")) {
           if (Statics.isMac()){
                Log.progress("Mac Detected: ");
                String removeCommand="$MAC";
                line = removeCommandAndContinue(removeCommand, line);
                Log.level3("OS IS MAC! remaining commands:" + line);
           } else {
               return;
           }
       }        
        Log.level3("");
        //Remove leading spaces
        line = StringOperations.removeLeadingSpaces(line);
//$HALT $ANY OTHER COMMAND will execute any commands after the $HALT command and stop the script.
        if (line.startsWith("$HALT")) {
            ScriptContinue = false;

            line = line.replace("$HALT", "");
            Log.level3("HALT RECEIVED");
            line = StringOperations.removeLeadingSpaces(line);
            Log.level3("Finishing remaining commands:" + line);


            //TODO: add end LED notification here


        }
//TODO: add "reboot" with adb reboot then send shell command "sleep5" then wait-for-device to account for windows retardedness

//# is a comment Disregard commented lines
        if (line.startsWith("#")) {
            Log.level3("Ignoring commented line" + line);
            return;
        }

        //Disregard blank lines
        if (line.equals("")) {
            return;
        }
        Log.level3("SCRIPT COMMAND:" + line);

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
                Log.level3("***NEW EVENT ADDED***");
                Log.level3("ON EVENT: " + Event[0]);
                Statics.ReactionEvents.add(Event[1]);
                Log.level3("PERFORM ACTION: " + Event[1]);
            } catch (Exception e) {
                Logger.getLogger(CASUALJFrame.class.getName()).log(Level.SEVERE, null, e);

            }
            return;

        }

//$SLASH will replace with "\" for windows or "/" for linux and mac
        if (line.contains("$SLASH")) {
            line = line.replace("$SLASH", Statics.Slash);
            Log.level3("Expanded $SLASH: " + line);
        }

//$ZIPFILE is a reference to the Script's .zip file
        if (line.contains("$ZIPFILE")) {
            line = line.replace("$ZIPFILE", ScriptTempFolder);
            Log.level3("Expanded $ZIPFILE: " + line);
        }

        if ((line.contains("\\n")) && ((line.startsWith("$USERNOTIFICATION") || line.startsWith("$USERNOTIFICATION")) || line.startsWith("$USERCANCELOPTION"))) {
            line = line.replace("\\n", "\n");
        }
//$HOMEFOLDER will reference the user's home folder on the system        
        if (line.contains("$HOMEFOLDER")) {
            line = line.replace("$HOMEFOLDER", Statics.CASUALHome);
            Log.level3("Expanded $HOMEFOLDER" + line);
        }
//$ECHO command will display text in the main window
        if (line.startsWith("$ECHO")) {
            Log.level3("Received ECHO command" + line);
            line = line.replace("$ECHO", "");
            line = StringOperations.removeLeadingSpaces(line);
            Log.level1(line);
            return;

// $LISTDIR will a folder on the host machine
        } else if (line.startsWith("$LISTDIR")) {
            line = line.replace("$LISTDIR", "");
            line = StringOperations.removeLeadingSpaces(line);
            File[] files = new File(line).listFiles();
            for (int i = 0; i <= files.length; i++) {
                try {
                    commandHandler("shell \"echo " + files[i].getCanonicalPath() + "\"");
                } catch (IOException ex) {
                    Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return;

// $MAKEDIR will make a folder
        } else if (line.startsWith("$MAKEDIR")) {
            line = line.replaceFirst("$MAKEDIR", "");
            line = StringOperations.removeLeadingSpaces(line);
            Log.level3("Creating Folder: " + line);
            new File(line).mkdirs();
            return;

// $CLEARON will remove all actions/reactions
        } else if (line.startsWith("$CLEARON")) {
            Statics.ActionEvents = new ArrayList<String>();
            Statics.ReactionEvents = new ArrayList<String>();
            Log.level3("***$CLEARON RECEIVED. CLEARING ALL LOGGING EVENTS.***");


//$USERNOTIFICATION will stop processing and force the user to 
            // press OK to continueNotification 
        } else if (line.startsWith("$USERNOTIFICATION")) {
            if (Statics.UseSound.contains("true")) {
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/Notification.wav");
            }
            line = line.replace("$USERNOTIFICATION", "");
            line = StringOperations.removeLeadingSpaces(line);
            if (line.contains(",")) {
                String[] Message = line.split(",");
                Log.level3("Displaying Notification--" + Message[1]);
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
            return;

// $USERCANCELOPTION will give the user the option to halt the script
            //USE: $USERCANCELOPTION Message
            //USE: $USERCANCELOPTION Title, Message
        } else if (line.startsWith("$USERCANCELOPTION")) {
            if (Statics.UseSound.contains("true")) {
                //CASUALAudioSystem CAS = new CASUALAudioSystem();
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/RequestToContinue.wav");
            }
            line = line.replace("$USERCANCELOPTION", "");
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
                    Log.level0(ScriptName + " canceled at user request");
                    ScriptContinue = false;
                    return;
                }
            } else {
                int n = JOptionPane.showConfirmDialog(
                        Statics.GUI,
                        line,
                        "Do you wish to continue?",
                        JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    Log.level0(ScriptName + " canceled at user request");
                    ScriptContinue = false;
                    return;
                }
            }
//$USERINPUTBOX will accept a String to be injected into ADB
            //Any text will be injected into the $USERINPUT variable    
            //USE: $USERINPUTBOX Title, Message, command $USERINPUT
        } else if (line.startsWith("$USERINPUTBOX")) {
            CASUALAudioSystem.playSound("/CASUAL/resources/sounds/InputRequested.wav");
            line.replace("\\n", "\n");
            String[] Message = line.replace("$USERINPUTBOX", "").split(",");
            String InputBoxText = JOptionPane.showInputDialog(null, Message[1], Message[0], JOptionPane.QUESTION_MESSAGE);
            InputBoxText = returnSafeCharacters(InputBoxText);


            Log.level3(InputBoxText);
            doShellCommand(Message[2], "$USERINPUT", InputBoxText);
            return;
            
// if Heimdall, Send to Heimdall shell command
        } else if (line.startsWith("$HEIMDALL")){
            line = line.replace("$HEIMDALL", "");
            line = StringOperations.removeLeadingSpaces(line);
            
            if (Statics.checkAndDeployHeimdall()){
                this.doHeimdallWaitForDevice();
                if (Statics.isLinux()) {
                    doElevatedHeimdallShellCommand(line);
                }
            doHeimdallShellCommand(line);
            } else {
                this.executeOneShotCommand("$HALT $ECHO You must install Heimdall!");
            }
// if Fastboot, Send to fastboot shell command
        } else if (line.startsWith("$FASTBOOT")) {
            line = line.replace("$FASTBOOT", "");
            line = StringOperations.removeLeadingSpaces(line);
            Statics.checkAndDeployFastboot();
            if (Statics.isLinux()) {
                if (Statics.UseSound.contains("true")) {
                    CASUALAudioSystem.playSound("/CASUAL/resources/sounds/PermissionEscillation.wav");
                }
                doElevatedFastbootShellCommand(line);
            }
            doFastbootShellCommand(line);

            // if Fastboot, Send to fastboot shell command
        } else if (line.startsWith("$ADB")) {
            line = line.replace("$ADB", "");
            line = StringOperations.removeLeadingSpaces(line);
            doShellCommand(line, null, null);
// if no prefix, then send command directly to ADB.
        } else {
            doShellCommand(line, null, null);
        }
        //final line output for debugging purposes
        Log.level3("COMMAND processed - " + Statics.AdbDeployed + " " + line);
    }

    private String removeCommandAndContinue(String remove, String line) {
        ScriptContinue = false;
        line = line.replace(remove, "");
        Log.level3("Removed " + remove );
        line = StringOperations.removeLeadingSpaces(line);
        return line;
    }
    DataInputStream DATAIN;

    private void executeSelectedScript(DataInputStream DIS, final String script) {
        Statics.ReactionEvents = new ArrayList<String>();
        Statics.ActionEvents = new ArrayList<String>();
        ScriptContinue = true;
        DATAIN = DIS;
        Log.level3("Executing Scripted Datastream" + DIS.toString());
        Runnable r = new Runnable() {

            public void run() {
                int updateStatus;
                Log.level3("CASUAL has initiated a multithreaded execution environment");
                String idStringFile = StringOperations.removeLeadingSpaces(StringOperations.convertStreamToString(getClass().getResourceAsStream(Statics.ScriptLocation + script + ".meta")));
                    String TestString = StringOperations.removeLeadingSpaces(idStringFile);
                    if ((TestString!=null) && (Statics.getScriptLocationOnDisk(script).equals(""))) {
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
                                    Log.level0(Statics.updateMessageFromWeb);
                                    Log.level0("CASUAL has been kill-switched due to critical updates.  Please read the above message");
                                    new TimeOutOptionPane().showTimeoutDialog(60, null, "CASUAL Cannot continue due to kill-switch activation.\n" + Statics.updateMessageFromWeb + "\n CASUAL will now take you to the supporting webpage.", "CRITICAL ERROR!", TimeOutOptionPane.ERROR_MESSAGE, TimeOutOptionPane.ERROR_MESSAGE, new String[]{"Take me to the Support Site"}, 0);
                                    new LinkLauncher().launchLink(Statics.supportWebsiteFromWeb);
                                    System.exit(0);
                                    return;
                                //download error
                                case 4:
                                    Log.level0("There was a problem downloading the script.  Please check your internet connection and try again.");
                                    //HALT script
                                    return;
                                case 5:
                                    Log.level0("Problem downloading file from internet, please try again");
                                    Log.level0("Problem downloading file from internet, please try again");


                                    //TODO stop and reset script to stock... possibly delete temp folder and restart CASUAL
                                    //HALT script
                                    return;
                                default: //unknown error do nothing
                                    break;
                            }

                        } catch (MalformedURLException ex) {
                            Log.level0("Could not find the script while trying to executeSelectedScript in CASUALScriptParser! " + script + " Please report this.");
                            //Script not in repository
                        } catch (IOException ex) {
                            Log.level0("IOException occoured while trying to executeSelectedScript in CASUALScriptParser! It's likely a bad download.");

                        }
                    }
               
                CurrentLine = 1;
                Statics.ProgressBar.setMaximum(LinesInScript);
                Log.level3("Reading datastream" + DATAIN);
                doRead(DATAIN);
                Statics.GUI.enableControls(true);
                Statics.DeviceMonitor.DeviceCheck.start();

            }

            private void updateDataStream(String script) {
                DATAIN = getDataStreamFromFile(script);
            }
        };
        Thread ExecuteScript = new Thread(r);
        ExecuteScript.start();
    }

    private String[] convertArrayListToStringArray(ArrayList List) {
        String[] StringArray = new String[List.size()];
        for (int i = 0; i <= List.size() - 1; i++) {
            StringArray[i] = List.get(i).toString();
        }
        return StringArray;
    }

    private void doRead(DataInputStream dataIn) {
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dataIn));
            String strLine;

            while (((strLine = bReader.readLine()) != null) && (ScriptContinue)) {
                CurrentLine++;
                Statics.ProgressBar.setValue(CurrentLine);


                commandHandler(strLine);
            }
            //Close the input stream
            dataIn.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }

    private ArrayList<String> parseCommandLine(String Line) {
        ArrayList<String> List = new ArrayList<String>();
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

    /*
     * doShellCommand is the point where the shell is activated ReplaceThis
     * WithThis allows for a last-minute insertion of commands by default
     * ReplaceThis should be null.
     */
    private void doShellCommand(String Line, String ReplaceThis, String WithThis) {
        Line = StringOperations.removeLeadingSpaces(Line);

        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList<String>();
        ShellCommand.add(Statics.AdbDeployed);
        ShellCommand.addAll(parseCommandLine(Line));
        String StringCommand[] = (convertArrayListToStringArray(ShellCommand));
        if (ReplaceThis != null) {
            for (int i = 0; i < StringCommand.length; i++) {
                StringCommand[i] = StringCommand[i].replace(ReplaceThis, WithThis);
            }
        }
        Shell.liveShellCommand(StringCommand);
    }

    private void doFastbootShellCommand(String Line) {
        Line = StringOperations.removeLeadingSpaces(Line);

        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList<String>();
        ShellCommand.add(Statics.fastbootDeployed);
        ShellCommand.addAll(this.parseCommandLine(Line));
        String StringCommand[] = (convertArrayListToStringArray(ShellCommand));
        Shell.liveShellCommand(StringCommand);
    }

    private void doElevatedFastbootShellCommand(String Line) {
        Line = StringOperations.removeLeadingSpaces(Line);

        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList<String>();
        ShellCommand.add(Statics.fastbootDeployed);
        ShellCommand.addAll(this.parseCommandLine(Line));
        String StringCommand[] = (convertArrayListToStringArray(ShellCommand));
        Shell.elevateSimpleCommandWithMessage(StringCommand, "CASUAL uses root to work around fastboot permissions.  Hit cancel if you have setup your UDEV rules.");
    } 
    
    
    
    
    
    
    
    
    
    
    
    private void doHeimdallWaitForDevice(){
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList<String>();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.add("detect");
        String stringCommand[] = (convertArrayListToStringArray(shellCommand));
        while (! Shell.silentShellCommand(stringCommand).contains("Device detected")){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private void doHeimdallShellCommand(String Line) {
        Line = StringOperations.removeLeadingSpaces(Line);
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList<String>();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.addAll(this.parseCommandLine(Line));
        String stringCommand2[] = convertArrayListToStringArray(shellCommand);
        Shell.liveShellCommand(stringCommand2);
    }
    private void doElevatedHeimdallShellCommand(String Line) {
        Line = StringOperations.removeLeadingSpaces(Line);
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList<String>();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.addAll(this.parseCommandLine(Line));
        String stringCommand2[] = convertArrayListToStringArray(shellCommand);
        Shell.elevateSimpleCommandWithMessage(stringCommand2, "CASUAL uses root to work around Heimdall permissions.  Hit cancel if you have setup your UDEV rules.");
    }

    
    
    
    
    
    
    
    
    
    
    
    private String returnSafeCharacters(String Str) {
        Str = Str.replace("\\", "\\\\");
        Str = Str.replace("\"", "\\\"");
        Str = Str.replace("\'", "\\\'");

        return Str;
    }
}
