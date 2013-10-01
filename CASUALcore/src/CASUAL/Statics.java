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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

/**
 *
 * @author adam
 *
 * Statics is used for any type of static variable It is the Static Class for
 * information to be used everywhere in the program.
 */
public class Statics {

    public static boolean GUIIsAvailable = false; //used to tell if the GUI is up yet.
    //public static boolean useGUI = false; //used by CASPAC mode to use terminal only
    public static boolean dumbTerminalGUI = false; //used by CASPAC mode
    private static String currentStatus = "working";
    public static boolean debugMode = false;
    public static Caspac CASPAC;

    public Statics() {
    }

    /*
     * increase or decrease the logging level
     */
    public static int GUIVerboseLevel = 2; //userdata is output to console
    public static int CommandLineVerboseLevel = 4; //all logs are output to file
    public static iCASUALInteraction interaction;
    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    /*
     * miscellanious variables
     */
    static Log log = new Log();
    public static ArrayList<String> LiveSendCommand = new ArrayList<String>();
    public static PrintWriter OutFile; //used by log class
    public static boolean LogCreated = false; //used by Log class
    public static ArrayList<String> runnableMD5list = new ArrayList<String>();
    //Form data
    //public static boolean TargetScriptIsResource = true;  //true if resource, false if file
    public static iCASUALGUI GUI; //Static reference to GUI input/output device
    public static JTextPane ProgressPane = new JTextPane(); //used by log to update Progress
    final public static String Slash = System.getProperty("file.separator"); //file separator for system \ or /
    /**
     * ProgressDoc provides a static reference to the program output
     */
    public static String PreProgress = "";  //place to log data before GUI comes up
    public static StyledDocument ProgressDoc; //anything in here is displayed to GUI. this is main output device.
    //Folders
    public static String ScriptLocation = "/SCRIPTS/"; //location to scripts
    public static String CASUALHome = System.getProperty("user.home") + System.getProperty("file.separator") + ".CASUAL" + System.getProperty("file.separator");
    private static String TempFolder;

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
        fo.makeFolder(TempFolder);
        return TempFolder;
    }

    public String getTempFolderInstance() {
        return TempFolder;
    }
    //Cross-Platform data storage
    public static String adbDeployed; //location of ADB after deployment
    public static String SelectedScriptFolder;//Used for script locations on disk
    public static String WinElevatorInTempFolder = TempFolder + "Elevate.exe"; //location of elevate.exe after deployed

    //ADB
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
    final public static String LinuxARMv6ADB = "/CASUAL/resources/ADB/adb-linuxARMv6";
    final public static String MacADB = "/CASUAL/resources/ADB/adb-mac";
    final public static String WinADB = "/CASUAL/resources/ADB/adb.exe";
    final public static String WinADB2 = "/CASUAL/resources/ADB/AdbWinApi.dll";
    final public static String WinADB3 = "/CASUAL/resources/ADB/AdbWinUsbApi.dll";
    //Heimdall
    final public static String heimdallVersion = "132";  //primary version string
    final public static String heimdallLinuxi386 = "/CASUAL/resources/heimdall/heimdall_i386.deb";
    final public static String heimdallLinuxamd64 = "/CASUAL/resources/heimdall/heimdall_amd64.deb";
    final public static String heimdallLinuxARMv6 = "/CASUAL/resources/heimdall/heimdall_armv6.deb";
    final public static String heimdallMac = "/CASUAL/resources/heimdall/heimdall-mac.dmg";
    final public static String heimdallWin = "/CASUAL/resources/heimdall/heimdall.exe";
    final public static String heimdallWin2 = "/CASUAL/resources/heimdall/libusb-1.0.dll";
    final public static String msvcp110dll = "/CASUAL/resources/heimdall/msvcp110.dll";
    final public static String msvcr110dll = "/CASUAL/resources/heimdall/msvcr110.dll";
    final public static String WinDriverResource = "/CASUAL/resources/heimdall/CADI.exe";  //original CADI
    final public static String WinDriverResource1 = "/CASUAL/resources/heimdall/CADI.zip";  //devcon CADI
    final public static String WinDriverResource2 = "/CASUAL/resources/heimdall/xp/CADI.exe";  //xp original CADI
    final public static String WinDriverResource3 = "/CASUAL/resources/heimdall/xp/CADI.zip";  //xp devcon CADI
    //Fastboot
    final public static String fastbootLinux64 = "/CASUAL/resources/fastboot/fastboot-linux64";
    final public static String fastbootLinux32 = "/CASUAL/resources/fastboot/fastboot-linux32";
    final public static String fastbootLinuxARMv6 = "/CASUAL/resources/fastboot/fastboot-linuxARMv6";
    final public static String fastbootWindows = "/CASUAL/resources/fastboot/fastboot-win.exe";
    final public static String fastbootMac = "/CASUAL/resources/fastboot/fastboot-mac";
    //Busybox
    final public static String busyboxARM = "/CASUAL/resources/ADB/busybox/busybox-armv4tl";
    final public static String busyboxX86 = "/CASUAL/resources/ADB/busybox/busybox-i686";
    
    //Windows permissions elevator
    final public static String WinPermissionElevatorResource = "/CASUAL/resources/ADB/Elevate.exe";
    final public static String ADBini = "/CASUAL/resources/ADB/adb_usb.ini";
    final public static String FilesystemAdbIniLocationLinuxMac = System.getProperty("user.home") + Slash + ".android" + Slash + "adb_usb.ini";
    final public static String FilesystemAdbIniLocationWindows = System.getProperty("user.home") + Slash + ".android" + Slash + "adb_usb.ini";
    //Windows Visual C++ redist --not always required
    final public static String WinVCRedis32tInRepo = "https://android-casual.googlecode.com/svn/trunk/repo/vcredist_x86.exe"; //Win vcredist in repo
    //CADI location by Jeremy Loper
    final public static String WinDriverInRepo = "https://android-casual.googlecode.com/svn/trunk/repo/CADI.exe"; //windriver in repo
    
    /*
     * Project properties
     */
    public static boolean scriptRunLock = false;
    public static boolean lockGUIformPrep = true;
    public static boolean lockGUIunzip = false;
    /*
     * Determines if Linux, Mac or Windows
     */
    //Check for windows
    //script data
    public static ArrayList<String> ActionEvents = new ArrayList<String>(); //Action events for $ON command. set by script
    public static ArrayList<String> ReactionEvents = new ArrayList<String>(); //Reactions for $ON command. . set by script
    //fastboot
    //static boolean isFastbootDeployed = false;  // if fastboot has been deployed
    public static String fastbootResource = ""; //location to fastboot set from final values above
    public static String fastbootDeployed = TempFolder + "fastboot"; //deployed fastboot
    //heimdall  
    static boolean isHeimdallDeployed = false; //if fastboot has been deployed
    static String heimdallResource = ""; //location to heimdall set from final values above
    static String heimdallStaging = TempFolder + "heimdallStage";//location for heimdall files while deploying on Linux
    static String heimdallDeployed = ""; //location of heimdall once deployed
    static String[] resourceHeimdallVersion;//get resource version[] from "/CASUAL/resources/heimdall/HeimdallVersion".replace("v","").split(.) ;
    static String[] installedHeimdallVersion; //attempt to get from running heimdall blindly, then .replace("v","").split(.) 

    public static void initializeStatics() {
        GUIIsAvailable = false;
        dumbTerminalGUI = false;
        setStatus("working");
        GUIVerboseLevel = 2;
        CommandLineVerboseLevel = 4;
        LiveSendCommand = new ArrayList<String>();
        OutFile = null;
        LogCreated = false;
        ProgressPane = new JTextPane();
        PreProgress = "";
        ProgressDoc = null;
        adbDeployed = null;
        SelectedScriptFolder = "";
        WinElevatorInTempFolder = TempFolder + "Elevate.exe";
        scriptRunLock = false;
        lockGUIformPrep = true;
        lockGUIunzip = false;
        runnableMD5list = new ArrayList<String>();
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
        if (CASPAC != null) {
            if (CASPAC.scripts != null) {
                Statics.CASPAC.getActiveScript().scriptContinue = false;
            }
        }
    }

    public static void setStatus(String status) {
        new Log().level4Debug(status);
        currentStatus = status;
        if (GUIIsAvailable) {
            GUI.setInformationScrollBorderText(status);
        }
    }

    public static String getStatus() {
        return currentStatus;
    }

    public static String setTempFolder(String folder) {
        TempFolder = folder;
        WinElevatorInTempFolder = TempFolder + "Elevate.exe";
        fastbootDeployed = TempFolder + "fastboot";
        heimdallStaging = TempFolder + "heimdallStage";
        heimdallDeployed = "";
        return TempFolder;
    }
}
