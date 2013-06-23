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
import java.io.PrintWriter;
import java.security.Timestamp;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
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

    public static final String BUILDPROPERTIES = "SCRIPTS/-build";
    public static boolean GUIIsAvailable = false; //used to tell if the GUI is up yet.
    public static boolean useGUI = false; //used by CASPAC mode to use terminal only
    public static boolean dumbTerminalGUI = false; //used by CASPAC mode
    public static String currentStatus="working";


    public Statics() {
    }

    /*
     * increase or decrease the logging level
     */
    public static int GUIVerboseLevel = 2; //userdata is output to console
    public static int CommandLineVerboseLevel = 4; //all logs are output to file
  
    /*
     * miscellanious variables
     */    
    static Log Log = new Log();
    public static ArrayList<String> LiveSendCommand = new ArrayList<>();
    public static PrintWriter OutFile; //used by log class
    public static boolean LogCreated = false; //used by Log class
    public static CASUALConnectionStatusMonitor casualConnectionStatusMonitor = new CASUALConnectionStatusMonitor();
    public static CASPACData localInformation;
    public static CASPACData webInformation;
    public static ArrayList<String> runnableMD5list = new ArrayList<>();

    //Form data
    public static boolean TargetScriptIsResource = true;  //true if resource, false if file
    public static CASUALJFrameMain GUI; //Static reference to GUI input/output device
    public static JTextPane ProgressPane = new JTextPane(); //used by log to update Progress
    final public static String Slash = System.getProperty("file.separator"); //file separator for system \ or /
    /**
     * ProgressDoc provides a static reference to the program output
     */
    public static String PreProgress = "";  //place to log data before GUI comes up
    public static StyledDocument ProgressDoc; //anything in here is displayed to GUI. this is main output device.

    //Folders
    public static String ScriptLocation = "/SCRIPTS/"; //location to scripts
    private static String TempF = null; //TempF is the actual tempfolder, it's served by getTempFolder
    public static String CASUALHome = System.getProperty("user.home") + System.getProperty("file.separator") + ".CASUAL" + System.getProperty("file.separator");
    
    //TODO: figure out a better way to not use static non-final variable during initialization. as reported by Netbeans
    final public static String TempFolder = (TempF==null)?getTempFolder():TempF;
    private static String getTempFolder() {
        if (TempF == null) {
            String user = System.getProperty("user.name");  //username
            String tf = System.getProperty("java.io.tmpdir"); //tempfolder
            tf=tf.endsWith(Slash)?tf:tf+Slash;  //make sure temp folder has a slash
            SimpleDateFormat sdf = new SimpleDateFormat("-yyyy-MM-dd-HH.mm.ss");
            TempF= tf +"CASUAL"+user+sdf.format(new Date()).toString()+ Slash; //set /temp/usernameRandom/
            new FileOperations().makeFolder(TempF);
        }
        return TempF;
    }
    public static String setTempFolder(String folder){
        TempF=folder;
        return TempF;
    }
    //Cross-Platform data storage
    public static String adbDeployed; //location of ADB after deployment
    public static String SelectedScriptFolder;//Used for script locations on disk
    public static String WinElevatorInTempFolder = TempFolder + "Elevate.exe"; //location of elevate.exe after deployed

    //ADB
    public static String LinuxADB() {
        if (Statics.arch.equals("x86_64")) {
            return Linux64ADB;
        }
        if (Statics.arch.equals("ARMv6")) {
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
    final public static String WinDriverResource = "/CASUAL/resources/heimdall/CADI.exe";  //win driver in CASUAL
    //final public static String WinDriverResource = "/CASUAL/resources/heimdall/CADI.zip";  //win driver in CASUAL
    //Fastboot
    final public static String fastbootLinux64 = "/CASUAL/resources/fastboot/fastboot-linux64";
    final public static String fastbootLinux32 = "/CASUAL/resources/fastboot/fastboot-linux32";
    final public static String fastbootLinuxARMv6 = "/CASUAL/resources/fastboot/fastboot-linuxARMv6";
    final public static String fastbootWindows = "/CASUAL/resources/fastboot/fastboot-win.exe";
    final public static String fastbootMac = "/CASUAL/resources/fastboot/fastboot-mac";
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

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("XP")) {
            displayWindowsXPDiscontinued();
        }
        return (os.indexOf("win") >= 0);
    }
    //Check for Mac

    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("mac") >= 0);
    }
    //Check for Linux

    public static boolean isLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("nux") >= 0) {
            if (arch.equals("")) {
                checkLinuxArch();
            }
            return true;
        }

        return false;
    }

    public static void displayWindowsXPDiscontinued() {
        int dResult = new CASUALInteraction("Your Operating System is Not Fully Supported", "Windows XP is a " + (Calendar.getInstance().get(Calendar.YEAR) - 2001) + " year old Operating system.\n"
                + "CASUAL is not able to handle problems caused by this.\n"
                + "Certain critial operations may not work. Please run as\n"
                + "an Administrator if you wish to continue.... However,\n"
                + "I recommend letting this window timeout, quit CASUAL and upgrade.\n"
                 ).showTimeoutDialog(
                60, //timeout
                null, //parentComponent
                CASUALInteraction.OK_OPTION, // Options buttons
                CASUALInteraction.INFORMATION_MESSAGE, //Icon
                new String[]{"Continue At Your Own Risk!!"}, // option buttons
                "Quit"); //Default{
        if (dResult != 0) {
            CASUALApp.shutdown(1);
        }
    }

    //script data
    public static ArrayList<String> ActionEvents = new ArrayList<>(); //Action events for $ON command. set by script
    public static ArrayList<String> ReactionEvents = new ArrayList<>(); //Reactions for $ON command. . set by script
    public static String[] scriptNames = {""};//list of all scripts in package. set on runtime
    public static String[] scriptLocations = {""}; //All scripts in package. set on runtime

    public static String getScriptLocationOnDisk(String name) {
        if (Statics.TargetScriptIsResource) return "";
        for (int n = 0; n < scriptNames.length; n++) {
            if (name.equals(scriptNames[n])) {
                Log.level4Debug("Script " + name + " returned #" + n + scriptNames[n]);

                if (scriptLocations[n] != null) {
                    return scriptLocations[n];
                }
                return "";
            }
        }
        return "";
    }

    public static void setScriptLocationOnDisk(String name, String location) {
        for (int n = 0; n < scriptNames.length; n++) {
            if (name.equals(scriptNames[n])) {
                Log.level4Debug("Associated Script " + name + " with #" + n + scriptNames[n]);
                scriptLocations[n] = location;
            }
        }

    }
    
    //fastboot
    static boolean isFastbootDeployed = false;  // if fastboot has been deployed
    public static String fastbootResource = ""; //location to fastboot set from final values above
    public static String fastbootDeployed = TempFolder + "fastboot"; //deployed fastboot
    //heimdall  
    static boolean isHeimdallDeployed = false; //if fastboot has been deployed
    static String heimdallResource = ""; //location to heimdall set from final values above
    static String heimdallStaging = TempFolder + "heimdallStage";//location for heimdall files while deploying on Linux
    static String heimdallDeployed = ""; //location of heimdall once deployed
    static String[] resourceHeimdallVersion;//get resource version[] from "/CASUAL/resources/heimdall/HeimdallVersion".replace("v","").split(.) ;
    static String[] installedHeimdallVersion; //attempt to get from running heimdall blindly, then .replace("v","").split(.) 

    public static boolean checkAndDeployHeimdall() {

        //deploys heimdall for Windows, launches checks for all other OS's. 
        
        if (isHeimdallDeployed) {
            return true;
        } else {
            if (Statics.isWindows()) {
                return new HeimdallInstall().deployHeimdallForWindows();
            } else {
                
                if (new HeimdallInstall().checkHeimdallVersion()) {
                    return true;
                } else { //shell returned error
                    if (HeimdallInstall.installHeimdall()) {
                        return true;
                    }
                    return false;
                }

            }
        }
    }

    public static boolean is64bitSystem() {
        if (isWindows()) {
            return isWindows64Arch();
        } else {
            return isMacLinux64Arch();
        }
    }

    private static boolean isWindows64Arch() {
        return (System.getenv("ProgramFiles(x86)") != null);
    }

    private static boolean isMacLinux64Arch() {
        String[] CommandArch = {"arch"};
        return new Shell().silentShellCommand(CommandArch).contains("64");
    }

    static String arch = ""; //system archetecture.
    public static void checkLinuxArch() {
        Shell shell = new Shell();
        String[] Command = {"dpkg", "--help"};
        String dpkgResults = shell.silentShellCommand(Command);
        if (dpkgResults.contains("aptitude") || dpkgResults.contains("debian") || dpkgResults.contains("deb")) {
            String[] CommandArch = {"arch"};
            String rawArch = shell.silentShellCommand(CommandArch);
            if (rawArch.contains("armv6")) {
                Statics.heimdallResource = heimdallLinuxARMv6;
                Statics.arch = "armv6";
            } else if (rawArch.contains("i686")) {
                Statics.heimdallResource = Statics.heimdallLinuxi386;
                Statics.arch = "i686";
            } else if (rawArch.contains("x86_64")) {
                Statics.heimdallResource = Statics.heimdallLinuxamd64;

                Statics.arch = "x86_64";
            } else {
                Statics.arch = "Linux";
            }
        } else {
            Statics.arch = "Linux";
        }
    }
    public static String OSName(){//windows, linux, mac....
        return System.getProperty("os.name");
    } 
    public static void initializeStatics(){
        GUIIsAvailable = false;
        useGUI = false;
        dumbTerminalGUI = false;
        currentStatus="working";
        GUIVerboseLevel = 2;
        CommandLineVerboseLevel = 4;

        LiveSendCommand = new ArrayList<>();
        OutFile=null;
        LogCreated = false;
        casualConnectionStatusMonitor = new CASUALConnectionStatusMonitor();
        localInformation=null;
        webInformation=null;
        TargetScriptIsResource = true;  
       
        ProgressPane = new JTextPane(); 
        PreProgress = "";
        ProgressDoc=null;
        TempF = null;
        adbDeployed=null;
        SelectedScriptFolder="";
        WinElevatorInTempFolder = TempFolder + "Elevate.exe";
        scriptRunLock = false;
        lockGUIformPrep = true;
        lockGUIunzip = false;
        runnableMD5list = new ArrayList<>();
        ActionEvents = new ArrayList<>();
        ReactionEvents = new ArrayList<>();
        scriptNames = new String[]{""};
        scriptLocations = new String[]{""};
        isFastbootDeployed = false;  // if fastboot has been deployed
        fastbootResource = ""; //location to fastboot set from final values above
        fastbootDeployed = TempFolder + "fastboot"; //deployed fastboot
        isHeimdallDeployed = false; //if fastboot has been deployed
        heimdallResource = ""; //location to heimdall set from final values above
        heimdallStaging = TempFolder + "heimdallStage";//location for heimdall files while deploying on Linux
        heimdallDeployed = ""; //location of heimdall once deployed
        resourceHeimdallVersion=null;//get resource version[] from "/CASUAL/resources/heimdall/HeimdallVersion".replace("v","").split(.) ;
        installedHeimdallVersion=null; //attempt to get from running heimdall blindly, then .replace("v","").split(.) 
        arch = "";
        CASUALLanguage.GOTO = "";
        CASUALScriptParser.ScriptContinue = true;
        CASUALTools.IDEMode = false;
        CASUALapplicationData.packageDataHasBeenSet = false;
        CASUALapplicationData.scriptsHaveBeenRecognized = false;
        CASUALapplicationData.CASUALSVNRevision="0";
        CASUALapplicationData.CASUALBuildNumber="0";
        CASUALapplicationData.buildProperties="";
        CASUALapplicationData.buttonText="Do It!";
        CASUALapplicationData.title="";
        CASUALapplicationData.bannerText="CASUAL";
        CASUALapplicationData.bannerPic="";
        CASUALapplicationData.usePictureForBanner=false;
        CASUALapplicationData.developerName="";
        CASUALapplicationData.donateButtonName="";
        CASUALapplicationData.useSound=false;
        CASUALapplicationData.developerDonateLink="";
        CASUALapplicationData.DontateButtonText="";
        CASUALapplicationData.donationLink="";
        CASUALapplicationData.CASUALFileName="";
        CASUALapplicationData.AlwaysEnableControls=true;
        CASUALapplicationData.meta=null;
    }
    
    public static void setStatus(String status){
        Statics.currentStatus=status;
    }
 
}
