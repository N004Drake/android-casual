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
 *  along with this program.  If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package CASUAL;

import CASUAL.language.CASUALLanguage;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import CASUAL.instrumentation.Track;
import CASUAL.misc.CountLines;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Parses and prepares CASUAL Script for CASUAL Language interperater.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALScriptParser {

    static Caspac oneShotCaspac;

    /**
     * If true, script will continue. False to shutdown.
     */
    public int LinesInScript = 0;
    String ScriptTempFolder = "";
    String ScriptName = "";
    static String scriptReturnValue="";
    public final static String NEWLINE=";;;";

    /**
     * executes a CASUAL script from a file
     *
     * @param caspac Caspac used for the script
     * @param multiThreaded false executes on main thread
     */
    public void loadFileAndExecute(Caspac caspac, boolean multiThreaded) {
        Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.CASUALExecuting);
        CASUALSessionData.getInstance().setStatus("Loading from file");
        executeSelectedScript(caspac, multiThreaded ,CASUALSessionData.getInstance());
    }

    /**
     * executes a CASUAL script from a file Reports to Log
     *
     * @param script path to file
     */
    private DataInputStream getDataStreamFromFile(Caspac caspac) {

        try {
            Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.CASUALExecuting);
            Log.level4Debug("Selected file" + caspac.getActiveScript().getName());

            ScriptName = caspac.getActiveScript().getName();
            ScriptTempFolder = caspac.getActiveScript().getTempDir();
            LinesInScript = new CountLines().countISLines(caspac.getActiveScript().getScriptContents());
            Log.level4Debug("Lines in Script " + LinesInScript);
            return new DataInputStream(caspac.getActiveScript().getScriptContents());

        } catch (FileNotFoundException ex) {
            Log.errorHandler(ex);
            return null;

        } catch (IOException ex) {
            Log.errorHandler(ex);
            return null;
        }

    }

    /**
     * provides a way to insert a line of CASUAL script.
     *
     * @param Line line to execute
     * @return from CASUAL language
     */
    public String executeOneShotCommand(String Line) throws Exception {
        Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.CASUALExecuting);
        CASUALSessionData.getInstance().setStatus("Executing");
        String retvalue = "";
        if (CASUALSessionData.getInstance().CASPAC == null) {
            ScriptName = "oneShot";
            ScriptTempFolder = CASUALSessionData.getInstance().getTempFolder();
        }
            if (Line.contains(NEWLINE)) {
                String[] lineArray = Line.split(NEWLINE);
                for (String linesplit : lineArray) {
                    retvalue = retvalue + new CASUALLanguage(ScriptName, ScriptTempFolder).commandHandler(linesplit) + "\n";
                }
            } else {
                retvalue = new CASUALLanguage(ScriptName, ScriptTempFolder).commandHandler(Line);

            }

        return retvalue;
    }

    /*
     * Script Handler contains all script commands and will execute commands
     */
    public DataInputStream scriptInput;

    /**
     * executes the Active Script in the provided CASPAC
     *
     * @param caspac CASPAC to have script executed
     * @param startThreaded true if it is to be started on a new thread.
     */
    public void executeSelectedScript(final Caspac caspac, boolean startThreaded , final CASUALSessionData data) {
        Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.CASUALExecuting);
        data.ReactionEvents = new ArrayList<String>();
        data.ActionEvents = new ArrayList<String>();
        data.CASPAC.getActiveScript().setScriptContinue(true);
        scriptInput = new DataInputStream(caspac.getActiveScript().getScriptContents());
        Log.level4Debug("Executing Scripted Datastream" + scriptInput.toString());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //int updateStatus;
                Log.level4Debug("CASUAL has initiated a multithreaded execution environment");

                if (data.isGUIIsAvailable()) {
                    data.GUI.setProgressBarMax(LinesInScript);
                }
                Log.level4Debug("Reading datastream" + scriptInput);
                new CASUALLanguage(caspac, caspac.getActiveScript().getTempDir()).beginScriptingHandler(scriptInput);

                if (data.isGUIIsAvailable()) {
                    //return to normal.
                    CASUALConnectionStatusMonitor.resumeAfterStop();
                } else {
                    //just in case something started the device monitor
                    CASUALConnectionStatusMonitor.stop();
                }
                try {
                    scriptInput.close();
                } catch (IOException ex) {
                    Log.errorHandler(ex);
                }
                data.CASPAC.getActiveScript().setDeviceArch("");
                data.setStatus("done");
                Log.level2Information("@scriptComplete");
                data.GUI.setReady(true);

            }
        };
        if (startThreaded) {
            CASUALStartupTasks.scriptRunLock = new CASUAL.misc.MandatoryThread(r);
            CASUALSessionData.getInstance().setStatus("Executing");
            CASUALStartupTasks.scriptRunLock.setName("CASUAL Script Executor");
            CASUALStartupTasks.scriptRunLock.start();
        } else {
            r.run();

        }
    }

    void executeActiveScript(Caspac CASPAC) {
        Log.level3Verbose("Exection of active script in CASPAC Commensing");
        Script s = CASPAC.getActiveScript();
        CASUALSessionData.getInstance().CASPAC.getActiveScript().setScriptContinue(true);

        Log.level2Information(s.getDiscription());
        int CASUALSVN = Integer.parseInt(java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision"));
        int scriptSVN = Integer.parseInt(s.getMetaData().getMinSVNversion());
        if (CASUALSVN < scriptSVN) {
            Log.level0Error("@improperCASUALversion");
            return;
        }

            DataInputStream dis = new DataInputStream(s.getScriptContents());
            CASPAC.setActiveScript(s);
            new CASUALLanguage(CASPAC, s.getTempDir()).beginScriptingHandler(dis);
        
    }

    void executeFirstScriptInCASPAC(Caspac CASPAC) {
        String scriptName = CASPAC.getScriptNames()[0];
        Script s = CASPAC.getScriptByName(scriptName);
        CASPAC.setActiveScript(s);
        executeActiveScript(CASPAC);

    }
    public static void setReturnValue(String value){
        scriptReturnValue=value;
    }
    public static String getReturnValue(){
        return scriptReturnValue;
    }
}
