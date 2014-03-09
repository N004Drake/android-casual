/*Statics is where the static variables from CASUAL reside
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

//import java.awt.Color;
import CASUAL.caspac.Caspac;
import CASUAL.communicationstools.adb.ADBTools;
import CASUAL.communicationstools.adb.busybox.CASUALDataBridge;
import CASUAL.communicationstools.fastboot.FastbootTools;
import CASUAL.communicationstools.heimdall.HeimdallTools;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Statics is used for any type of static variable It is the Static Class for
 * information to be used everywhere in the program.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class Statics {

    /**
     * CASUAL does not look to GUI to execute. Execution will start
     * autonomously. Terminal is used for input and output. GUI is a display.
     */
   // public static boolean dumbTerminalGUI = false; //used by CASPAC mode
    private static String currentStatus = "working";

    /**
     * true if debugMode. Do not send logs in debug mode. We create too many
     * errors, thanks.
     */
    public static boolean debugMode = false;

    /**
     * reference to CASPAC used by this CASUAL
     */
    public static Caspac CASPAC;

    /**
     * true when GUI is ready.
     */
    //public static boolean guiReady = false;

    /**
     * @return the GUIIsAvailable
     */
    public static boolean isGUIIsAvailable() {
        if (GUI != null) {
            return GUI.isReady() && !java.awt.GraphicsEnvironment.isHeadless();
        }
        return false;
    }

    /**
     * increase or decrease the logging level. 0 is error only, 4 is debug
     */
    public static int outputGUIVerbosity = 2; //userdata is output to console

    /**
     * increase or decrease the log file output. 0 is error only, 4 is debug
     */
    public static int outputLogVerbosity = 4; //all logs are output to file

    /**
     * static reference to interactions object for CASUALMessageObject.
    /**
     * static reference to interactions object for CASUALMessageObject.
     */


    /**
     * Input Device for CASUAL. Generally System.in (STDIN) but may be
     * reassigned to any inputstream.
     */
    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
    /**
     * static reference for class implementing interface for CASUAL's GUI.
     */
    public static iCASUALUI GUI; //Static reference to GUI input/output device



    /**
     * slash provides a universal reference to the / on linux/mac and a \ on Windows. 
     */
    final public static String slash = System.getProperty("file.separator"); //file separator for system \ or /
    /**
     * ProgressDoc provides a static reference to the program output.
     */
    public static String PreProgress = "";  //place to log data before GUI comes up




    /**
     * Default home folder for CASUAL. Use for permanent storage of data only.
     * Located in the users home folder, in a folder called ".CASUAL".
     */
    public static String CASUALHome = System.getProperty("user.home") + System.getProperty("file.separator") + ".CASUAL" + System.getProperty("file.separator");
    private static File TempFolder;

    /**
     * Creates and returns the temp folder if required.
     *
     * @return temp folder string location.
     */
    public static String getTempFolder() {
        FileOperations fo = new FileOperations();
        if (TempFolder == null) {
            String user = System.getProperty("user.name");  //username
            String tf = System.getProperty("java.io.tmpdir"); //tempfolder
            tf = tf.endsWith(slash) ? tf : tf + slash;  //make sure temp folder has a slash
            SimpleDateFormat sdf = new SimpleDateFormat("-yyyy-MM-dd-HH.mm.ss");
            TempFolder = new File(tf + "CASUAL" + user + sdf.format(new Date()).toString() + slash); //set /temp/usernameRandom/
            setTempFolder(TempFolder.toString());
            fo.makeFolder(TempFolder.toString());
        }

        if (!TempFolder.exists()) {
            TempFolder.mkdirs();
        }
        return TempFolder.toString()+slash;
    }

    /**
     * gets the temp folder for CASUAL;
     *
     * @return the temp folder.
     */
    public String getTempFolderInstance() {
        return TempFolder.toString()+slash;
    }
    //Cross-Platform data storage


 
    /**
     * Windows Visual C++ redistributable downloadable file. This is not used as
     * we include the proper dependencies in CASUAL. Windows Visual C++ redist 
     * not always required.

     */
    final public static String WinVCRedis32tInRepo = "https://android-casual.googlecode.com/svn/trunk/repo/vcredist_x86.exe"; //Win vcredist in repo


    //TODO: determine feasability of moving this to CASPAC.Script. 
    /**
     * ActionEvents for the $ON command are set up by script. Trigger Reaction
     * events.
     */
    public static ArrayList<String> ActionEvents = new ArrayList<String>(); //Action events for $ON command. set by script

    /**
     * ReactionEvents are triggered by ActionEvents and created by $ON command.
     */
    public static ArrayList<String> ReactionEvents = new ArrayList<String>(); //Reactions for $ON command. . set by script

    /**
     * Resets all variables in CASUAL to provide, basically, a warm reboot.
     */
    public static void initializeStatics() {
        CASUALDataBridge.commandedShutdown = true;
        setStatus("working");
        outputGUIVerbosity = 2;
        outputLogVerbosity = 4;

        PreProgress = "";
        CASUALStartupTasks.scriptRunLock = new CASUAL.misc.MandatoryThread();
        CASUALStartupTasks.lockGUIunzip = false;
        ActionEvents = new ArrayList<String>();
        ReactionEvents = new ArrayList<String>();
        new ADBTools().reset();
        new HeimdallTools().reset();
        new FastbootTools().reset(); 
        CASUALLanguage.GOTO = "";
        try {
            Statics.CASPAC.getActiveScript().scriptContinue = false;
        } catch (NullPointerException ex) {
            //do nothing at all 
        }
    }

    /**
     * sets the current operation status.
     *
     * @param status status to be displayed to user.
     */
    public static void setStatus(final String status) {
        Log.level4Debug(status);
        currentStatus = status;

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (GUI!=null) {
                        GUI.setInformationScrollBorderText(status);
                    }
                    
                    CASUAL.instrumentation.Instrumentation.updateStatus(status);
                    
                }
            });
            t.setName("Updating GUI");
            t.start();

    }

    /**
     * Gets the current status for display.
     *
     * @return current status.
     */
    public static String getStatus() {
        return currentStatus;
    }

    /**
     * Sets the temp folder. Generally this is auto-assigned by getTempFolder,
     * but it can be manually assigned.
     *
     * @param folder dir to make temp folder.
     * @return path to new temp folder.
     */
    public static String setTempFolder(String folder) {
        TempFolder = new File(folder);
        new FastbootTools().reset();
        new ADBTools().reset();
        return TempFolder.toString()+slash;
    }
}
