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

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public class CASUALScriptParser {

    public static boolean ScriptContinue = true;
    Log log = new Log();
    int LinesInScript = 0;
    String ScriptTempFolder = "";
    String ScriptName = "";

    /*
     * Executes a selected script as a resource reports to Log class.
     */
    public void loadResourceAndExecute(final String script, boolean multiThreaded) {
        Statics.setStatus("Loading resources");
        log.level4Debug("Selected resource" + script);
        ScriptName = script;
        CountLines CountLines = new CountLines();
        LinesInScript = CountLines.countResourceLines(script);
        log.level4Debug("Lines in Script " + LinesInScript);
        ScriptTempFolder = Statics.TempFolder + script + Statics.Slash;


        DataInputStream RAS = new DataInputStream(getClass().getResourceAsStream(Statics.ScriptLocation + script + ".scr"));
        executeSelectedScript(RAS, ScriptTempFolder, script, multiThreaded);
    }

    public void loadFileAndExecute(String File, String script, boolean multiThreaded) {
        Statics.setStatus("Loading from file");
        DataInputStream DIS = getDataStreamFromFile(File);
        executeSelectedScript(DIS, ScriptTempFolder, script, multiThreaded);
    }

    /*
     * executes a CASUAL script from a file Reports to Log
     *
     */
    public DataInputStream getDataStreamFromFile(String script) {
        log.level4Debug("Selected file" + script);

        ScriptName = script;
        ScriptTempFolder = Statics.TempFolder + (new File(script).getName()) + Statics.Slash;
        LinesInScript = new CountLines().countFileLines(script + ".scr");
        log.level4Debug("Lines in Script " + LinesInScript);

        try {
            return new DataInputStream(new FileInputStream(script + ".scr"));

        } catch (FileNotFoundException ex) {
            log.errorHandler(ex);
            return null;
        }

    }

    /*
     * executeOneShotCommand provides a way to insert a script line.
     *
     */
    public String executeOneShotCommand(String Line) {
        Statics.setStatus("Executing");
        String x = new CASUALLanguage(this.ScriptName, this.ScriptTempFolder).commandHandler(Line);
        return x;
    }

    /*
     * Script Handler contains all script commands and will execute commands
     */
    DataInputStream DATAIN;

    private void executeSelectedScript(DataInputStream DIS, final String scriptFolder, final String script, boolean startThreaded) {
        Statics.scriptRunLock = true;
        Statics.ReactionEvents = new ArrayList<>();
        Statics.ActionEvents = new ArrayList<>();
        ScriptContinue = true;
        DATAIN = DIS;
        log.level4Debug("Executing Scripted Datastream" + DIS.toString());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //int updateStatus;
                log.level4Debug("CASUAL has initiated a multithreaded execution environment");
                String idStringFile;
                String TestString = "";
                try {
                    idStringFile = StringOperations.removeLeadingSpaces(StringOperations.convertStreamToString(getClass().getResourceAsStream(Statics.ScriptLocation + script + ".meta")));
                    TestString = StringOperations.removeLeadingSpaces(idStringFile);
                } catch (NullPointerException ex) {
                    log.level4Debug("NO METADATA FOUND\nNO METADATA FOUND\n");
                }
                if (checkForUpdates(TestString)) {
                    return;
                }

                if (Statics.useGUI) {
                    Statics.GUI.setProgressBarMax(LinesInScript);
                }
                log.level4Debug("Reading datastream" + DATAIN);
                new CASUALLanguage(script, scriptFolder).beginScriptingHandler(DATAIN);
                if (Statics.useGUI) {
                    Statics.GUI.enableControls(true);
                }
                if (Statics.useGUI) {
                    Statics.casualConnectionStatusMonitor.DeviceCheck.start();
                } else {
                    Statics.casualConnectionStatusMonitor.DeviceCheck.stop();
                }
                try {
                    DATAIN.close();
                } catch (IOException ex) {
                    Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
                }
                Statics.scriptRunLock = false;
                Statics.currentStatus = "Script Complete";
                log.level2Information("Script Complete");

            }

            private void updateDataStream(String script) {
                DATAIN = getDataStreamFromFile(script);
            }

            private boolean checkForUpdates(String testString) {
                Statics.setStatus("Checking for Updates");
                int updateStatus;
                if (testString != null && Statics.getScriptLocationOnDisk(script).equals("")) {
                    try {
                        //String[] IDStrings = CASUALIDString.split("\n");
                        //This is where we hold the local information to be compared to the update
                        CASPACData localInformation = new CASPACData(testString);

                        updateStatus = new CASUALUpdates().checkOfficialRepo(Statics.ScriptLocation + script, localInformation);
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
                                Statics.TargetScriptIsResource = false;
                                Statics.setScriptLocationOnDisk(script, Statics.TempFolder + "SCRIPTS" + Statics.Slash + script);
                                updateDataStream(Statics.getScriptLocationOnDisk(script));//switch input stream to file
                                break;
                            //CASUAL must be update    
                            case 3:
                                log.level0Error(Statics.webInformation.updateMessage);
                                log.level0Error("CASUAL has been kill-switched due to critical updates.  Please read the above message");
                                new CASUALInteraction("CRITICAL ERROR!", "CASUAL Cannot continue due to kill-switch activation.\n" + "\n CASUAL will now take you to the supporting webpage." + Statics.webInformation.updateMessage).showTimeoutDialog(60, null, CASUALInteraction.ERROR_MESSAGE, CASUALInteraction.ERROR_MESSAGE, new String[]{"Take me to the Support Site"}, 0);
                                new LinkLauncher(Statics.webInformation.supportURL).launch();
                                CASUALApp.shutdown(0);
                                return true;
                            //download error
                            case 4:
                                log.level0Error("There was a problem downloading the script.  Please check your internet connection and try again.");
                                //HALT script
                                return true;
                            case 5:
                                log.level0Error("Problem downloading file from internet, please try again");
                                log.level0Error("Problem downloading file from internet, please try again");
                                new CASUALInteraction("CRITICAL ERROR!", "Download Failure.  CASUAL will now restart.").showTimeoutDialog(60, null, CASUALInteraction.ERROR_MESSAGE, CASUALInteraction.ERROR_MESSAGE, new String[]{"OK"}, "ok");
                                try {
                                    JavaSystem.restart(new String[]{""});
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                //HALT script
                                return true;
                            default: //unknown error do nothing
                                log.level0Error("CASUALScriptParser().executeSelectedScript: CASUAL has encountered an unknown error. Please report this.");
                                break;
                        }
                    } catch (MalformedURLException ex) {
                        log.level0Error("Could not find the script while trying to executeSelectedScript in CASUALScriptParser! " + script + " Please report this.");
                        log.errorHandler(ex);
                    } catch (IOException ex) {
                        log.level0Error("IOException occoured while trying to executeSelectedScript in CASUALScriptParser! It's likely a bad download.");
                        log.errorHandler(ex);
                    }
                }
                return false;
            }
        };
        if (startThreaded) {
            Thread ExecuteScript = new Thread(r);
            Statics.setStatus("Executing");
            ExecuteScript.setName("CASUAL Script Executor");
            ExecuteScript.start();
        } else {
            r.run();

        }
    }
}
