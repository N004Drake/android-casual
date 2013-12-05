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
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

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
    public static boolean dumbTerminalGUI = false; //used by CASPAC mode
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
    public static boolean guiReady = false;

    /**
     * @return the GUIIsAvailable
     */
    public static boolean isGUIIsAvailable() {
        if (GUI != null) {
            return guiReady && !java.awt.GraphicsEnvironment.isHeadless();
        }
        return false;
    }

    /**
     * increase or decrease the logging level. 0 is error only, 4 is debug
     */
    public static int guiOutputVerbosity = 2; //userdata is output to console

    /**
     * increase or decrease the log file output. 0 is error only, 4 is debug
     */
    public static int logFIleOutputVerbosity = 4; //all logs are output to file

    /**
     * static reference to interactions object for CASUALMessageObject.
     */
    public static iCASUALInteraction interaction;

    /**
     * Input Device for CASUAL. Generally System.in (STDIN) but may be
     * reassigned to any inputstream.
     */
    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    private static Log log = new Log();
     


    //Form data
    //public static boolean TargetScriptIsResource = true;  //true if resource, false if file


    /**
     * static reference for class implementing interface for CASUAL's GUI.
     */
    public static iCASUALGUI GUI; //Static reference to GUI input/output device

    //TODO: replace this with an interface as JTextPane is not supported on all platforms
    /**
     * the Progress area for CASUAL's user interface.
     */
    public static JTextPane ProgressPane; //used by log to update Progress

    /**
     * Slash provides a universal reference to the / on linux/mac and a \ on Windows. 
     */
    final public static String Slash = System.getProperty("file.separator"); //file separator for system \ or /
    /**
     * ProgressDoc provides a static reference to the program output.
     */
    public static String PreProgress = "";  //place to log data before GUI comes up

    //TODO: replace this with an interface as StyledDocument is not supported on all platforms
    /**
     * progress document used to document progress.
     */
    public static StyledDocument ProgressDoc; //anything in here is displayed to GUI. this is main output device.
    //Folders

    /**
     * default SCRIPTS location for CASUAL.
     */
    public static String ScriptLocation = "/SCRIPTS/"; //location to scripts

    /**
     * Default home folder for CASUAL. Use for permanent storage of data only.
     * Located in the users home folder, in a folder called ".CASUAL".
     */
    public static String CASUALHome = System.getProperty("user.home") + System.getProperty("file.separator") + ".CASUAL" + System.getProperty("file.separator");
    private static String TempFolder;

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
            tf = tf.endsWith(Slash) ? tf : tf + Slash;  //make sure temp folder has a slash
            SimpleDateFormat sdf = new SimpleDateFormat("-yyyy-MM-dd-HH.mm.ss");
            TempFolder = tf + "CASUAL" + user + sdf.format(new Date()).toString() + Slash; //set /temp/usernameRandom/
            setTempFolder(TempFolder);
            fo.makeFolder(TempFolder);
        }

        if (!new File(TempFolder).exists()) {
            new File(TempFolder).mkdirs();
        }
        return TempFolder;
    }

    /**
     * gets the temp folder for CASUAL;
     *
     * @return the temp folder.
     */
    public String getTempFolderInstance() {
        return TempFolder;
    }
    //Cross-Platform data storage

    //TODO remove this as ADBTools().getAdbLocation() should be used istead. 
    /**
     * path to ADB after deployment.
     */
    public static String adbDeployed; //location of ADB after deployment

    //TODO It may be possible to remove this as it is handled by the active script 
    /**
     * Static reference to active script folder.
     */
    public static String SelectedScriptFolder;//Used for script locations on disk

    //TODO: evaluate possibility of removal of elevate.exe
    /**
     * Path to elevate.exe as after deployment. Elevate is deployed
     * automatically on Windows.
     */
    public static String WinElevatorInTempFolder = getTempFolder() + "Elevate.exe"; //location of elevate.exe after deployed

    //TODO: move this to ADBTools(). 
    /**
     * returns the Instance of Linux's ADB binary
     *
     * @return gets the proper name of the ADB binary as a resource. 
     */
    public static String LinuxADB() {
        String arch = OSTools.checkLinuxArch();
        if (arch.equals("x86_64")) {
            return Linux64ADB;
        }
        if (arch.equals("ARMv6")) {
            return LinuxARMv6ADB;
        }
        return Linux32ADB;  //defautlt to 32bit ADB
    }
    final private static String Linux32ADB = "/CASUAL/resources/ADB/adb-linux32";
    final private static String Linux64ADB = "/CASUAL/resources/ADB/adb-linux64";

    /**
     * ADB for ARMv6.
     */
    final public static String LinuxARMv6ADB = "/CASUAL/resources/ADB/adb-linuxARMv6";

    /**
     * ADB for Mac.
     */
    final public static String MacADB = "/CASUAL/resources/ADB/adb-mac";

    /**
     * ADB for Windows.
     */
    final public static String WinADB = "/CASUAL/resources/ADB/adb.exe";

    /**
     * ADB for Windows resource must be in same folder.
     */
    final public static String WinADB2 = "/CASUAL/resources/ADB/AdbWinApi.dll";

    /**
     * Adb for Windows resource must be in same folder.
     */
    final public static String WinADB3 = "/CASUAL/resources/ADB/AdbWinUsbApi.dll";
    //Heimdall

    /**
     * Heimdall version.
     */
    final public static String heimdallVersion = "140";  //primary version string

    /**
     * heimdall for Debian Linux x86.
     */
    final public static String heimdallLinuxi386 = "/CASUAL/resources/heimdall/heimdall_i386.deb";

    /**
     * heimdall for Debian Linux 64-bit.
     */
    final public static String heimdallLinuxamd64 = "/CASUAL/resources/heimdall/heimdall_amd64.deb";

    /**
     * Heimdall for Debian Linux ARMv6.
     */
    final public static String heimdallLinuxARMv6 = "/CASUAL/resources/heimdall/heimdall_armv6.deb";

    /**
     * Heimdall for Mac dmg file.
     */
    final public static String heimdallMac = "/CASUAL/resources/heimdall/heimdall-mac.dmg";

    /**
     * Heimdall for Windows.
     */
    final public static String heimdallWin = "/CASUAL/resources/heimdall/heimdall.exe";

    /**
     * Heimdall for Windows resource must be in same folder as exe.
     */
    final public static String heimdallWin2 = "/CASUAL/resources/heimdall/libusb-1.0.dll";

    /**
     * Heimdall for Windows resource must be in same folder as exe.
     */
    final public static String msvcp110dll = "/CASUAL/resources/heimdall/msvcp110.dll";

    /**
     * Heimdall for Windows resource must be in same folder as exe.
     */
    final public static String msvcr110dll = "/CASUAL/resources/heimdall/msvcr110.dll";

    //TODO jrloper, refactor and make this set of WinDriverResources separately named (not windriverresource2). 
    //TODO jrloper, remove this. 
    /**
     * CADI windows Driver. "Old Faithful"
     */
    final public static String WinDriverResource = "/CASUAL/resources/heimdall/CADI.exe";  //original CADI

    /**
     * CADI Windows Driver.
     */
    final public static String WinDriverResource1 = "/CASUAL/resources/heimdall/CADI.zip";  //devcon CADI
    //TODO jrloper, remove this. 
    /**
     * CADI Windows Driver for XP. "Old Faithful"
     */
    final public static String WinDriverResource2 = "/CASUAL/resources/heimdall/xp/CADI.exe";  //xp original CADI

    /**
     * CADI Windows Driver.
     */
    final public static String WinDriverResource3 = "/CASUAL/resources/heimdall/xp/CADI.zip";  //xp devcon CADI
    //Fastboot

    /**
     * Fastboot for Linux 64-bit.
     */
    final public static String fastbootLinux64 = "/CASUAL/resources/fastboot/fastboot-linux64";

    /**
     * Fastboot for Linux 32bit.
     */
    final public static String fastbootLinux32 = "/CASUAL/resources/fastboot/fastboot-linux32";

    /**
     * Fastboot for Linux ARMv6.
     */
    final public static String fastbootLinuxARMv6 = "/CASUAL/resources/fastboot/fastboot-linuxARMv6";

    /**
     * Fastboot for Windows.
     */
    final public static String fastbootWindows = "/CASUAL/resources/fastboot/fastboot-win.exe";

    /**
     * Fastboot for Mac.
     */
    final public static String fastbootMac = "/CASUAL/resources/fastboot/fastboot-mac";
    //Busybox

    /**
     * Busybox for Linux ARMv4tl is the most compatible with all ARM according
     * to Busybox site. This is intended for the device, not the host.
     */
    final public static String busyboxARM = "/CASUAL/resources/ADB/busybox/busybox-armv4tl";

    /**
     * Busybox for Linux x86. This is intended for the device, not the host.
     */
    final public static String busyboxX86 = "/CASUAL/resources/ADB/busybox/busybox-i686";
    //Windows permissions elevator

    /**
     * Windows Elevate.exe as resource in CASUAL
     */
    final public static String WinPermissionElevatorResource = "/CASUAL/resources/ADB/Elevate.exe";

    /**
     * adb_usb.ini file used for deployment of ADB and specification of
     * otherwise unsupported devices.
     */
    final public static String ADBini = "/CASUAL/resources/ADB/adb_usb.ini";

    //TODO: remove this and use ADBTools().getAdbLocation();
    /**
     * Location of ADB on the filesystem for Linux and Mac.
     */
    final public static String FilesystemAdbIniLocationLinuxMac = System.getProperty("user.home") + Slash + ".android" + Slash + "adb_usb.ini";

    //TODO: remove this and use ADBTools().getAdbLocation();
    /**
     * Location of ADB on the filesystem for Windows.
     */
    final public static String FilesystemAdbIniLocationWindows = System.getProperty("user.home") + Slash + ".android" + Slash + "adb_usb.ini";
    //Windows Visual C++ redist --not always required

    /**
     * Windows Visual C++ redistributable downloadable file. This is not used as
     * we include the proper dependencies in CASUAL.
     */
    final public static String WinVCRedis32tInRepo = "https://android-casual.googlecode.com/svn/trunk/repo/vcredist_x86.exe"; //Win vcredist in repo

    //TODO: jrloper remove this. 
    /**
     * Location of CADI.exe in online repo. (old Faithful) CADI by Jeremy Loper
     */
    final public static String WinDriverInRepo = "https://android-casual.googlecode.com/svn/trunk/repo/CADI.exe"; //windriver in repo

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
     * location to fastboot resource. set from final values above.
     */
    public static String fastbootResource = ""; //

    //TODO: remove this and move it to FastbootTools.getFastbootLocation();
    /**
     * fastboot after deployment to computer disk.
     */
    public static String fastbootDeployed = getTempFolder() + "fastboot"; //deployed fastboot
    //heimdall  
    //TODO: remove all thse and Heimdall Install/HeimdallTools with getters set by, or triggering installation ;
    static boolean isHeimdallDeployed = false; //if fastboot has been deployed
    static String heimdallResource = ""; //location to heimdall set from final values above
    static String heimdallStaging = getTempFolder() + "heimdallStage";//location for heimdall files while deploying on Linux
    static String heimdallDeployed = ""; //location of heimdall once deployed
    static String[] resourceHeimdallVersion;//get resource version[] from "/CASUAL/resources/heimdall/HeimdallVersion".replace("v","").split(.) ;
    static String[] installedHeimdallVersion; //attempt to get from running heimdall blindly, then .replace("v","").split(.) 

    /**
     * Resets all variables in CASUAL to provide, basically, a warm reboot.
     */
    public static void initializeStatics() {
        CASUALDataBridge.commandedShutdown = true;
        guiReady = false;
        dumbTerminalGUI = false;
        setStatus("working");
        guiOutputVerbosity = 2;
        logFIleOutputVerbosity = 4;
        if (!java.awt.GraphicsEnvironment.isHeadless()) {
            ProgressPane = new JTextPane();
        }
        PreProgress = "";
        ProgressDoc = null;
        adbDeployed = null;
        SelectedScriptFolder = "";
        WinElevatorInTempFolder = TempFolder + "Elevate.exe";
        Locks.scriptRunLock = new CASUAL.misc.MandatoryThread();
        Locks.lockGUIunzip = false;
        ActionEvents = new ArrayList<String>();
        ReactionEvents = new ArrayList<String>();
        fastbootResource = ""; //location to fastboot set from final values above
        fastbootDeployed = TempFolder + "fastboot"; //deployed fastboot
        isHeimdallDeployed = false; //if fastboot has been deployed
        heimdallResource = ""; //location to heimdall set from final values above
        heimdallStaging = TempFolder + "heimdallStage";//location for heimdall files while deploying on Linux
        heimdallDeployed = ""; //location of heimdall once deployed
        resourceHeimdallVersion = null;//get resource version[] from "/CASUAL/resources/heimdall/HeimdallVersion".replace("v","").split(.) ;
        installedHeimdallVersion = null; //attempt to get from running heimdall blindly, then .replace("v","").split(.) 
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
        new Log().level4Debug(status);
        currentStatus = status;
        if (isGUIIsAvailable()) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    GUI.setInformationScrollBorderText(status);
                }
            });
            t.setName("Updating GUI");
            t.start();
        }
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
        TempFolder = folder;
        WinElevatorInTempFolder = TempFolder + "Elevate.exe";
        fastbootDeployed = TempFolder + "fastboot";
        heimdallStaging = TempFolder + "heimdallStage";
        heimdallDeployed = "";
        return TempFolder;
    }
}
