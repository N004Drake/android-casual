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
import java.awt.Color;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;



/**
 *
 * @author adam
 * 
 * Statics is used for any type of static variable
 * It is the Static Class for information to be used
 * everywhere in the program.
 */
public class Statics {
    
    
    public Statics(){
    }    

    /*increase or decrease the logging level*/
    public static int ConsoleLevel=1; //userdata is output to console
    /*increase or decrease the logging level*/
    public static int LogLevel=4; //all logs are output to file
    /*
     * miscellanious variables
     */ 
    static Shell shellCommand;
    static Log Log = new Log();    
    public static ArrayList LiveSendCommand=new ArrayList();
    public static PrintWriter OutFile; //used by log class
    public static boolean LogCreated=false; //used by log class
    public static String[] DeviceTracker;
    public static String LastLineReceived;
    public static String updateMessageFromWb;
    public static String supportWebsiteFromWeb;
    /*
     *Form data 
     */
    public static String ScriptLocation="/SCRIPTS/";
    public static boolean TargetScriptIsResource=true;
    public static CASUALJFrame GUI;
    public static JTextPane ProgressPane; //used by log to update Progress
    public static StyledDocument ProgressDoc;
    
    

    
    
    
    
    public static String PreProgress="";
    public static JProgressBar ProgressBar;
    public static String Slash=System.getProperty("file.separator");
    public static CASUALConnectionStatusMonitor DeviceMonitor=new CASUALConnectionStatusMonitor();
    public static String UseSound;
    public static ArrayList ActionEvents = new ArrayList();
    public static ArrayList ReactionEvents = new ArrayList();
    
    
    

    /*
     * Folders
     */
    
    private static String TempF=null;
    //TempFolder is the folder used for file operations
    public static String TempFolder=getTempFolder();
    private static String getTempFolder(){
        
        if (TempF == null){
            TempF = System.getProperty("java.io.tmpdir");
            if (!TempF.endsWith(Slash))TempF=TempF + Slash;
            String UserName=System.getenv("USERNAME");
            if ( UserName == null){
                TempF=TempF+"TempCASUAL";
            } else {
                TempF=TempF+UserName+"TEMPCASUAL";
            }
            String Randomness = "";
            String Characters="123456789ABCDEF";
            Random RandomNumberGenerator=new Random();
            for (int i = 0; i < 8; i++){
                Randomness=Randomness+ Characters.charAt(RandomNumberGenerator.nextInt(Characters.length()));
            }
            TempF=TempF+Randomness;
            if (!TempF.endsWith(Slash))TempF=TempF + Slash;
        }
        return TempF;
    }
    public static String CASUALHome=System.getProperty("user.home")+System.getProperty("file.separator")+".CASUAL"+System.getProperty("file.separator");
 

    /*
     * Cross-Platform data storage
     */    
    public static String AdbDeployed;
    //public static String DeploymentBinaries[];
    public static String OSName=System.getProperty("os.name");
    public static String Arch="";
    public static String SelectedScriptFolder;
    public static String WinElevatorInTempFolder=TempFolder+"Elevate.exe";
    final public static String CASUALSCRIPT="/SCRIPTS/";
    final public static String LinuxADB="/CASUAL/resources/ADB/adblinux";
    final public static String MacADB="/CASUAL/resources/ADB/adbmac";
    final public static String WinADB="/CASUAL/resources/ADB/adb.exe";
    final public static String WinADB2="/CASUAL/resources/ADB/AdbWinApi.dll";
    final public static String WinADB3="/CASUAL/resources/ADB/AdbWinUsbApi.dll";
    //Windows permissions elevator
    final public static String WinPermissionElevatorResource="/CASUAL/resources/ADB/Elevate.exe";
    final public static String ADBini=CASUALSCRIPT+"adb_usb.ini";
    final public static String FilesystemAdbIniLocationLinuxMac=System.getProperty("user.home")+Slash+".android"+Slash+"adb_usb.ini";
    final public static String FilesystemAdbIniLocationWindows=System.getProperty("user.home")+Slash+".android"+Slash+"adb_usb.ini";
    final private static String fastbootWindows="/CASUAL/resources/fastboot/fastbootWin.exe";
    final private static String fastbootLinux="/CASUAL/resources/fastboot/fastbootLinux";
    final private static String fastbootMac="/CASUAL/resources/fastboot/fastbootMac";
    final public static String CASUALRepo="http://android-casual.googlecode.com/svn/trunk/GUI/src";
    
    /*Project properties*/
    public static String DeveloperName;
    public static String DeveloperDonateLink;
    public static String DonateButtonText;
    public static boolean MasterLock=true;
    public static void setMasterLock(boolean status){
        MasterLock=status;
        try {
            GUI.enableControls(MasterLock);
        } catch (NullPointerException e) {
            Log.level3(e.getMessage());
        }
    }

 
    /*
     * Determines if Linux, Mac or Windows
     */
    //Check for windows
    public static boolean isWindows(){
 	String os = System.getProperty("os.name").toLowerCase();
    return (os.indexOf( "win" ) >= 0); }
    //Check for Mac
    public static boolean isMac(){
 	String os = System.getProperty("os.name").toLowerCase();
    return (os.indexOf( "mac" ) >= 0);}
    //Check for Linux
    public static boolean isLinux(){
        String os = System.getProperty("os.name").toLowerCase();
	return (os.indexOf( "nux") >=0);}
    
    
    
  
    /*
     * sets system information, including binary presence, operating system and archetecture
     */
    public void setSystemInfo(){

        if(isWindows()){
            Statics.Slash="\\";
        } else if (isMac()) {
            Statics.Slash="/";
        }else if(isLinux()){
            Statics.Slash="/";
        }
    }
    public static SimpleAttributeSet keyWord = new SimpleAttributeSet();
    public static void initDocument(){
        ProgressPane.setContentType("text/html");
        ProgressDoc=ProgressPane.getStyledDocument();
       
StyleConstants.setForeground(keyWord, Color.RED);
StyleConstants.setBackground(keyWord, Color.YELLOW);
StyleConstants.setBold(keyWord, true);
    }
    
    
     
public static String[] scriptNames={""};
public static String[] scriptLocations={""};
public static String getScriptLocationOnDisk(String name){
    for (int n=0; n<scriptNames.length; n++){
        if (name.equals(scriptNames[n])) {
            Log.level3("Script "+name+" returned #"+n+scriptNames[n]);
            
            if (scriptLocations[n]!=null){
                return scriptLocations[n];
            }
            return "";
        }
    }
    return "";
}
public static void setScriptLocationOnDisk(String name, String location){
    for (int n=0; n<scriptNames.length; n++){
        if (name.equals(scriptNames[n])) {
            Log.level3("Associated Script "+name+" with #"+n+scriptNames[n]);

            scriptLocations[n]=location;
        }
    }
    
}    
static boolean isFastbootDeployed=false;
public static String fastbootResource="";
public static String fastbootDeployed=TempFolder+"fastboot";
public static void checkAndDeployFastboot(){
    if (! isFastbootDeployed){
            if (isLinux()) fastbootResource=fastbootLinux;
            if (isWindows()) fastbootResource=fastbootWindows;
            if (isMac()) fastbootResource=fastbootMac;
            Log.level2("Deploying Fastboot from "+fastbootResource + " to " + fastbootDeployed);
            new FileOperations().copyFromResourceToFile(fastbootResource, fastbootDeployed);    
            if ( isLinux() || isMac() ) new FileOperations().setExecutableBit(fastbootDeployed);
            isFastbootDeployed=true;
        }
    }
  
    
}
    


