/*
 * Copyright (c) 2011 Adam Outler
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package CASUAL;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JTextArea;



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
    public static int ConsoleLevel=4; //userdata is output to console
    /*increase or decrease the logging level*/
    public static int LogLevel=4; //all logs are output to file
    
    /*
     * miscellanious variables
     */ 
    static Shell shellCommand;
    static Log Log = new Log();    
    public static ArrayList LiveSendCommand;
    public static PrintWriter OutFile;
    public static boolean LogCreated=false; //used by log class
    /*
     *Form data for Heimdall One-Click View 
     */
    public static String ScriptLocation="/SCRIPTS/";
    public static boolean TargetScriptIsResource=false;
    public static CASUALJFrame GUI;
    public static JTextArea ProgressArea; //used by log to update Progress
    public static String PreProgress;
    
    public static String Slash=System.getProperty("file.separator");

    
    

            
    
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
   
 

    /*
     * Cross-Platform data storage
     */    
    public static String AdbDeployed;
    public static String DeploymentBinaries[];
    public static String OSName=System.getProperty("os.name");
    public static String  OSType=""; //used for logging
    public static String  Arch="";
    public static String SelectedScriptFolder;
    public static String WinElevatorInTempFolder=TempFolder+"Elevate.exe";
    final public static String CASUALSCRIPT="/SCRIPTS/";
    final public static String WinADB="/CASUAL/resources/ADB/adb.exe";
    final public static String LinuxADB="/CASUAL/resources/ADB/adblinux";
    final public static String MacADB="/CASUAL/resources/ADB/adbmac";
    final public static String WinADB2="/CASUAL/resources/ADB/AdbWinApi.dll";
    final public static String WinADB3="/CASUAL/resources/ADB/AdbWinUsbApi.dll";
    //Windows permissions elevator
    final public static String WinPermissionElevatorResource="/CASUAL/resources/ADB/Elevate.exe";
    final public static String ADBini=CASUALSCRIPT+"adb_usb.ini";
    final public static String FilesystemAdbIniLocationLinuxMac=System.getProperty("user.home")+Slash+".android/adb_usb.ini";
    //TODO: this does not expand
    final public static String FilesystemAdbIniLocationWindows=System.getProperty("user.home")+".android\\adb_usb.ini";
   

    /*Project properties*/
    public static String DeveloperName;
    public static String DeveloperDonateLink;
    public static String DonateButtonText;
    
 
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
            Statics.DeploymentBinaries[0]=WinADB;
            Statics.OSType="Windows";
            Statics.Slash="\\";
        } else if (isMac()) {
            OSType="Mac";
            Statics.DeploymentBinaries[0]=MacADB;
            Statics.Slash="/";
        }else if(isLinux()){
            OSType="Linux";
            Statics.Slash="/";
            Statics.DeploymentBinaries[0]=LinuxADB;
        }
    }
}
    

