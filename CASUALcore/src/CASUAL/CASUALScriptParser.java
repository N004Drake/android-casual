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

import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
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

    /**
     * If true, script will continue. False to shutdown.
     */
    public static boolean ScriptContinue = true;
    Log log = new Log();
    int LinesInScript = 0;
    String ScriptTempFolder = "";
    String ScriptName = "";



    /**
     * executes a CASUAL script from a file
     * @param File CASUAL.scr file
     * @param script script name
     * @param multiThreaded false executes on main thread
     */
    public void loadFileAndExecute(Caspac caspac, boolean multiThreaded) {
        Statics.setStatus("Loading from file");
        executeSelectedScript(caspac, multiThreaded);
    }

    /**
     * executes a CASUAL script from a file Reports to Log
     * @param script path to file
     */
    private DataInputStream getDataStreamFromFile(Caspac caspac) {

        try {
            log.level4Debug("Selected file" + caspac.activeScript.name);

            ScriptName = caspac.activeScript.name;
            ScriptTempFolder = caspac.activeScript.tempDir;
            LinesInScript = new CountLines().countISLines(caspac.activeScript.getScriptContents());
            log.level4Debug("Lines in Script " + LinesInScript);
            return new DataInputStream(caspac.activeScript.getScriptContents());

        } catch (FileNotFoundException ex) {
            log.errorHandler(ex);
            return null;

        } catch (IOException ex) {
            Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    /**
     * provides a way to insert a line of CASUAL script.
     * @param Line line to execute
     * @return from CASUAL language
     */
    public String executeOneShotCommand(String Line) {
        Statics.setStatus("Executing");
        String x = new CASUALLanguage(this.ScriptName, this.ScriptTempFolder).commandHandler(Line);
        return x;
    }

    /*
     * Script Handler contains all script commands and will execute commands
     */
    DataInputStream scriptInput;

    public void executeSelectedScript(final Caspac caspac, boolean startThreaded) {
        Statics.scriptRunLock = true;
        Statics.ReactionEvents = new ArrayList<>();
        Statics.ActionEvents = new ArrayList<>();
        ScriptContinue = true;
        scriptInput = new DataInputStream(StringOperations.convertStringToStream(caspac.activeScript.scriptContents));
        log.level4Debug("Executing Scripted Datastream" + scriptInput.toString());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //int updateStatus;
                log.level4Debug("CASUAL has initiated a multithreaded execution environment");
                String TestString = "";
                if (checkForUpdates(TestString)) {
                    return;
                }

                if (Statics.useGUI) {
                    Statics.GUI.setProgressBarMax(LinesInScript);
                }
                log.level4Debug("Reading datastream" + scriptInput);
                new CASUALLanguage(caspac.activeScript.name, caspac.activeScript.tempDir).beginScriptingHandler(scriptInput);

                if (Statics.useGUI) {
                    //return to normal.
                    CASUALConnectionStatusMonitor.DeviceCheck.start();
                } else {
                    //just in case something started the device monitor
                    CASUALConnectionStatusMonitor.DeviceCheck.stop();
                }
                try {
                    scriptInput.close();
                } catch (IOException ex) {
                    Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
                }
                Statics.scriptRunLock = false;
                Statics.setStatus("done");
                log.level2Information("@scriptComplete");

            }



            private boolean checkForUpdates(String testString) {
                Statics.setStatus("Checking for Updates");
                int updateStatus;
                if (caspac.activeScript.extractionMethod!=0) {
                    try {
                        //String[] IDStrings = CASUALIDString.split("\n");
                        //This is where we hold the local information to be compared to the update
                        CASPACData localInformation = new CASPACData(testString);

                        updateStatus = new CASUALUpdates().checkOfficialRepo(caspac.activeScript.tempDir, localInformation);
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
                                //TODO: switch input stream to file
                                break;
                            //CASUAL must be update    
                            case 3:
                                log.level0Error(Statics.webInformation.updateMessage);
                                log.level0Error("@killSwitchMessage");
                                new CASUALInteraction("@interactionKillSwitchMessage\n"+ Statics.webInformation.updateMessage).showTimeoutDialog(60, null, CASUALInteraction.ERROR_MESSAGE, CASUALInteraction.ERROR_MESSAGE, new String[]{"Take me to the Support Site"}, 0);
                                new LinkLauncher(Statics.webInformation.supportURL).launch();
                                CASUALApp.shutdown(0);
                                return true;
                            //download error
                            case 4:
                                log.level0Error("@problemDownloading");
                                //HALT script
                                return true;
                            case 5:
                                log.level0Error("@problemDownlaodingFile");
                                new CASUALInteraction("@interactionDownloadFileFailure").showTimeoutDialog(60, null, CASUALInteraction.ERROR_MESSAGE, CASUALInteraction.ERROR_MESSAGE, new String[]{"OK"}, "ok");
                                try {
                                    JavaSystem.restart(new String[]{""});
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                //HALT script
                                return true;
                            default: //unknown error do nothing
                                log.level0Error("@executeScriptError");
                                break;
                        }
                    } catch (MalformedURLException ex) {
                        log.level0Error("@couldNotFindScript"); 
                        log.level0Error(caspac.activeScript.name);
                        log.level0Error("@reportThisError");
                        log.errorHandler(ex);
                    } catch (IOException ex) {
                        log.level0Error("@ioExceptionInExecuteScript");
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

    void executeFirstScriptInCASPAC(Caspac CASPAC) {
        String scriptName= CASPAC.getScriptNames()[0];
        Script s=CASPAC.getScriptByName(scriptName);
        log.level2Information(s.discription);
        int CASUALSVN=Integer.parseInt( java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision"));
        int scriptSVN=Integer.parseInt(s.metaData.minSVNversion);
        if (CASUALSVN<scriptSVN){
           new Log().level0Error("@improperCASUALversion");
           return;
        }
        CASPAC.waitForUnzipComplete();
        try {
            ByteArrayInputStream scriptStream = new ByteArrayInputStream(s.scriptContents.getBytes("UTF-8"));
            DataInputStream dis=new DataInputStream(scriptStream);
            new CASUALLanguage(s.name, s.tempDir).beginScriptingHandler(dis);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
