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
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JProgressBar;
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
    public static final String BUILDPROPERTIES="SCRIPTS/-build";
    public static boolean GUIIsAvailable=false;
    public static boolean UseGUI=false;

    public Statics() {
    }

    /*
     * increase or decrease the logging level
     */
    public static int ConsoleLevel = 1; //userdata is output to console
    public static int LogLevel = 4; //all logs are output to file

    /*
     * miscellanious variables
     */
    static Shell shellCommand;
    static Log Log = new Log();
    public static ArrayList<String> LiveSendCommand = new ArrayList();
    public static PrintWriter OutFile; //used by log class
    public static boolean LogCreated = false; //used by log class
    public static String[] DeviceTracker;
    public static String LastLineReceived;
    //web information
    final public static String CASUALRepo = "http://android-casual.googlecode.com/svn/trunk/GUI/src";
    public static String updateMessageFromWeb;
    public static String supportWebsiteFromWeb;
    public static CASUALIDString localInformation = null;
    final public static String WinVCRedis32tInRepo = "https://android-casual.googlecode.com/svn/trunk/repo/vcredist_x86.exe";
    final public static String WinVCRedis64tInRepo = "https://android-casual.googlecode.com/svn/trunk/repo/vcredist_x64.exe";
    final public static String WinVCRedist2010InRepo = "https://android-casual.googlecode.com/svn/trunk/repo/vcredist_x862010.exe";
    final public static String WinVCRedis32tInRepoMD5 = "a8d5962623206751bdd4416d140ae7c5  vcredist_x86.exe";
    final public static String WinVCRedis64tInRepoMD5 = "ba2c17a20b2b1d8a30f96d53e2632a68  vcredist_x64.exe";
    final public static String WinVCRedis201064tInRepoMD5 = "b88228d5fef4b6dc019d69d4471f23ec  vcredist_x862010.exe";
    final public static String WinDriverInRepo = "https://android-casual.googlecode.com/svn/trunk/repo/zadig.exe";
    final public static String WinDriverIniInRepo = "https://android-casual.googlecode.com/svn/trunk/repo/zadig.ini";
    final public static String WinDriverInRepoMD5 = "e0476fe60b539ff057371994dd4e8e30  zadig.exe";
    static String heimdallMacURL = "https://android-casual.googlecode.com/svn/trunk/repo/Heimdall_1.4.1_compressed.dmg.sh";
    //Form data
    public static boolean TargetScriptIsResource = true;
    public static CASUALJFrame GUI;
    public static JTextPane ProgressPane=new JTextPane(); //used by log to update Progress
    public static StyledDocument ProgressDoc=ProgressPane.getStyledDocument();
    public static String PreProgress = "";
    public static JProgressBar ProgressBar;
    final public static String Slash = System.getProperty("file.separator");
    public static CASUALConnectionStatusMonitor DeviceMonitor = new CASUALConnectionStatusMonitor();

    public static void initDocument() {
        ProgressPane.setContentType("text/html");
        ProgressDoc = ProgressPane.getStyledDocument();
    }
    //Folders
    public static String ScriptLocation = "/SCRIPTS/";
    private static String TempF = null;
    //TempFolder is the folder used for file operations
    final public static String TempFolder = getTempFolder();

    private static String getTempFolder() {

        if (TempF == null) {
            TempF = System.getProperty("java.io.tmpdir");
            if (!TempF.endsWith(Slash)) {
                TempF = TempF + Slash;
            }
            String UserName = System.getenv("USERNAME");
            if (UserName == null) {
                TempF = TempF + "TempCASUAL";
            } else {
                TempF = TempF + UserName + "TEMPCASUAL";
            }
            String Randomness = "";
            String Characters = "123456789ABCDEF";
            Random RandomNumberGenerator = new Random();
            for (int i = 0; i < 8; i++) {
                Randomness = Randomness + Characters.charAt(RandomNumberGenerator.nextInt(Characters.length()));
            }
            TempF = TempF + Randomness;
            if (!TempF.endsWith(Slash)) {
                TempF = TempF + Slash;
            }
        }
        return TempF;
    }
    public static String CASUALHome = System.getProperty("user.home") + System.getProperty("file.separator") + ".CASUAL" + System.getProperty("file.separator");
    //Cross-Platform data storage
    public static String AdbDeployed;
    public static String OSName = System.getProperty("os.name");
    public static String SelectedScriptFolder;
    public static String WinElevatorInTempFolder = TempFolder + "Elevate.exe";
    final public static String CASUALSCRIPT = "/SCRIPTS/";
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
    final public static String Linux32ADB = "/CASUAL/resources/ADB/adb-linux32";
    final public static String Linux64ADB = "/CASUAL/resources/ADB/adb-linux64";
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
    //Fastboot

    public static String fastbootLinux() {
        if (Statics.arch.equals("x86_64")) {
            return fastbootLinux64;
        }
        if (Statics.arch.equals("ARMv6")) {
            return fastbootLinuxARMv6;
        }
        return fastbootLinux32;
    }
    final private static String fastbootLinux64 = "/CASUAL/resources/fastboot/fastboot-linux64";
    final private static String fastbootLinux32 = "/CASUAL/resources/fastboot/fastboot-linux32";
    final private static String fastbootLinuxARMv6 = "/CASUAL/resources/fastboot/fastboot-linuxARMv6";
    final private static String fastbootWindows = "/CASUAL/resources/fastboot/fastboot-win.exe";
    final private static String fastbootMac = "/CASUAL/resources/fastboot/fastboot-mac";
    //Windows permissions elevator
    final public static String WinPermissionElevatorResource = "/CASUAL/resources/ADB/Elevate.exe";
    final public static String ADBini = CASUALSCRIPT + "-adb_usb.ini";
    final public static String FilesystemAdbIniLocationLinuxMac = System.getProperty("user.home") + Slash + ".android" + Slash + "adb_usb.ini";
    final public static String FilesystemAdbIniLocationWindows = System.getProperty("user.home") + Slash + ".android" + Slash + "adb_usb.ini";
    /*
     * Project properties
     */

    public static boolean lockGUIformPrep = true;
    public static boolean lockGUIunzip = false;
    public static boolean lockGUIdeviceConnectionStatus = false;
    public static int SVNRevisionRequired = 0;

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
        if (os.indexOf("nux") >= 0){
            if (arch.equals("")) {
                checkLinuxArch();
            }
            return true;
        }
        
        return false;
    }

    public static void displayWindowsXPDiscontinued() {
        int dResult = new TimeOutOptionPane().showTimeoutDialog(
                60, //timeout
                null, //parentComponent
                "Windows XP is a 12 year old Operating system.\n"
                + "CASUAL is not able to handle problems caused by this.\n"
                + "Certain critial operations may not work. Please run as\n"
                + "an Administrator if you wish to continue.... However,\n"
                + "I recommend letting this window timeout, quit CASUAL and upgrade.\n",
                "Your Operating System is Not Supported", //DisplayTitle
                TimeOutOptionPane.OK_OPTION, // Options buttons
                TimeOutOptionPane.INFORMATION_MESSAGE, //Icon
                new String[]{"Continue At Your Own Risk!!"}, // option buttons
                "Quit"); //Default{
        if (dResult != 0) {
            System.exit(1);
        }
    }
    //restart app
    //script data
    public static ArrayList<String> ActionEvents = new ArrayList();
    public static ArrayList<String> ReactionEvents = new ArrayList();
    public static String[] scriptNames = {""};
    public static String[] scriptLocations = {""};

    public static String getScriptLocationOnDisk(String name) {
        for (int n = 0; n < scriptNames.length; n++) {
            if (name.equals(scriptNames[n])) {
                Log.level3("Script " + name + " returned #" + n + scriptNames[n]);

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
                Log.level3("Associated Script " + name + " with #" + n + scriptNames[n]);

                scriptLocations[n] = location;
            }
        }

    }
    //fastboot
    static boolean isFastbootDeployed = false;
    public static String fastbootResource = "";
    public static String fastbootDeployed = TempFolder + "fastboot";

    public static void checkAndDeployFastboot() {
        if (!isFastbootDeployed) {
            if (isLinux()) {
                fastbootResource = fastbootLinux();
            }
            if (isWindows()) {
                fastbootResource = fastbootWindows;
            }
            if (isMac()) {
                fastbootResource = fastbootMac;
            }
            Log.level2("Deploying Fastboot from " + fastbootResource + " to " + fastbootDeployed);
            new FileOperations().copyFromResourceToFile(fastbootResource, fastbootDeployed);
            if (isLinux() || isMac()) {
                new FileOperations().setExecutableBit(fastbootDeployed);
            }
            isFastbootDeployed = true;
        }
    }
    //heimdall 
    static boolean isHeimdallDeployed = false;
    static boolean ExectingHeimdallCommand = false;
    static String heimdallResource = "";
    static String arch = "";
    //public static String heimdallResource2 = "";
    static String heimdallStaging = TempFolder + "heimdallStage";
    static String heimdallDeployed = "";
    static String[] resourceHeimdallVersion;//get resource version[] from "/CASUAL/resources/heimdall/HeimdallVersion".replace("v","").split(.) ;
    static String[] installedHeimdallVersion; //attempt to get from running heimdall blindly, then .replace("v","").split(.) 

    public static boolean checkAndDeployHeimdall() {
//handling for Windows

        if (isHeimdallDeployed) {
            return true;
        } else {
            if (Statics.isWindows()) {
                FileOperations fo = new FileOperations();  //Windows must deploy heimdall every startup.
                Statics.heimdallResource = Statics.heimdallWin2;
                fo.copyFromResourceToFile(Statics.heimdallResource, Statics.TempFolder + "libusb-1.0.dll");
                Statics.heimdallResource = Statics.heimdallWin;
                Statics.heimdallDeployed = Statics.TempFolder + "heimdall.exe";

                fo.copyFromResourceToFile(Statics.heimdallResource, Statics.heimdallDeployed);
                String x = new Shell().silentShellCommand(new String[]{Statics.heimdallDeployed, "version"});
                if (!x.equals("")) {
                    Statics.isHeimdallDeployed = true;
                    return true;
                } else {
                    new HeimdallInstall().installWindowsVCRedist();
                    x = new Shell().silentShellCommand(new String[]{Statics.heimdallDeployed, "version"});
                    if (x.contains("CritError!!!")) {
                        return false;
                    } else {
                        return true;
                    }

                }
//handling for Linux/mac
            } else {
                if ((isHeimdallDeployed) || (new HeimdallInstall().checkHeimdallVersion())) {
                    return true;
                } else { //shell returned error
                    if (HeimdallInstall.checkAndDeployHeimdall()) {
                        return true;
                    }
                    return false;
                }

            }
        }
    }

    public static boolean isWindows64Arch() {
        return (System.getenv("ProgramFiles(x86)") != null);
    }

    public static void checkLinuxArch() {
        Shell shell = new Shell();
        String[] Command = {"dpkg", "--help"};
        String dpkgResults = shell.sendShellCommand(Command);
        if (dpkgResults.contains("aptitude") || dpkgResults.contains("debian") || dpkgResults.contains("deb")) {
            String[] CommandArch = {"arch"};
            String rawArch = shell.sendShellCommand(CommandArch);
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
}
