/*WindowsDrivers.java
 * **************************************************************************
 *Copyright (C) 2013  Jeremy Loper
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
 ***************************************************************************/
package CASUAL.communicationstools.heimdall.drivers;

import CASUAL.CASUALMessageObject;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.Shell;
import CASUAL.Statics;
import CASUAL.archiving.Unzip;
import CASUAL.misc.StringOperations;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * **************************************************************************
 * WindowsDrivers a.k.a. CADI(v2) or (CASUALS Automated Driver Installer) is a
 * CASUALcore dependant class which attempts to automate CASUAL process on
 * Windows (XP - Win8) A generic driver is required for USB IO via CASUAL. This
 * driver must temporarily take the place of the default OEM driver of targeted
 * device (which must be currently connected). While many OEMs use WinUSB (or
 * compatible alternative) as a device interface, CASUAL is not able communicate
 * with the target because of proprietary (undocumented) driver service API.
 * However once the generic driver is installed CASUAL using reverse engineered
 * open-source tools such as Heimdall - http://goo.gl/bqeulW is able to interact
 * with the target device directly.
 *
 * This class is heavily dependant upon REGEX and a modified version of Devcon
 * (MS-LPL). CADI uses libusbK, which is a generic WinUSB compatible driver for
 * libusbx communication via Heimdall. Two sets of drivers are used (each
 * containing an x86/x64 variant), one built with WDK 7.1 (allowing for XP
 * support) the other built with WDK 8.0 (for Windows 8 support). All driver
 * components are built & digitally signed by Jeremy Loper.
 *
 * WARNING: Modifications to this class can result in system-wide crash of
 * Windows. (I know, I've seen it :-D ) So plan out all modifications prior, and
 * always ensure a null value is never passed to Devcon.
 *
 * @author Jeremy Loper jrloper@gmail.com
 * @author Adam Outler adamoutler@gmail.com
 * ************************************************************************
 */
public class WindowsDrivers {

    /**
     * CADI Windows Driver for XP.
     */
    private final static String cadiXpDrivers = "/CASUAL/communicationstools/heimdall/drivers/resources/xp/CADI.zip";

    /**
     * CADI Windows Driver for Windows Vista and higher.
     */
    private final static String cadiDrivers = "/CASUAL/communicationstools/heimdall/drivers/resources/CADI.zip";

    /**
     * pathToCADI contains the full path to the root folder of where driver
     * package(s) are (or will be). This Member is populated on Class Object
     * creation.
     */
    public final String pathToCADI;

    /**
     * windowsDriverBlanket is a static Array of targeted USB VID (VendorID
     * numbers) in hexadecimal form. IDs are stored as strings because Java
     * doesn't have a native storage class for hexadecimal (base 16) without
     * conversion to decimal (base 10) This Member is populated on Class Object
     * creation.
     */
    public final String[] windowsDriverBlanket;

    /**
     * driverExtracted this static member is toggled true upon a successful
     * driver package decompression.
     *
     */
    public static volatile boolean driverExtracted = false;

    /**
     * removeDriverOnCompletion is a primarily user set variable, relating to
     * driver package uninstallation. Should driver be removed on script
     * completion? 0 - Unset (will prompt user) 1 - Do not remove driver on
     * completion 2 - Remove driver on script completion This Member is
     * populated on Class Object creation.
     */
    public static volatile int removeDriverOnCompletion;

    /**
     * WindowsDrivers instantiates the windows driver class.
     *
     * @param promptInit initializes removeDriverOnCompletion member and
     * subsequent prompting action. 0 - Unset (will prompt user) (default) 1 -
     * Do not remove driver on completion 2 - Remove driver on script completion
     */
    public WindowsDrivers(int promptInit) {
        removeDriverOnCompletion = promptInit;
        Log.level4Debug("WindowsDrivers() Initializing");
        this.windowsDriverBlanket = new String[]{"04E8", "0B05", "0BB4", "22B8", "054C", "2080", "18D1"};
        this.pathToCADI = Statics.getTempFolder() + "CADI" + Statics.slash;
        if (removeDriverOnCompletion == 0) { //so it only asks once
            removeDriverOnCompletion = new CASUALMessageObject("@interactionInstallingCADI").showYesNoOption() ? 2 : 1; //set value as 2 if true and 1 if false
        }
    }
    
    
    public boolean installKnownDrivers(String[] additionalVIDs){
        String[] deviceList=getDeviceList(true,true);
        int devicesInstalled=getCASUALDriverCount();
        
        //get list of devices to be instaled
        ArrayList<String> qualifiedDevices=new ArrayList<String>();
        for (String device:deviceList){
            addDeviceToInstallationQueueIfInList(qualifiedDevices, device);
        }
        //get vidUID list
        ArrayList<String[]> uidVid=new ArrayList<String[]>();
        parseUidVidFromQualifiedDevices(qualifiedDevices, uidVid);
        
        //install each driver
        for (String[] uv:uidVid){
         
             //"USB\VID_04E8&PID_6860"
             String usbvidString="USB\\VID_"+uv[0]+"&PID_"+uv[1];

             //TODO: install driver
         
         
        }
        return true;
    }

    private ArrayList<String[]> parseUidVidFromQualifiedDevices(ArrayList<String> qualifiedDevices, ArrayList<String[]> uidVid) {
        for (String device:qualifiedDevices){
            if (!device.startsWith("USB\\VID_")){
                continue;
            }
            device=device.replace("USB\\VID_", "");
            String vid=device.substring(0,4);
            if (!device.startsWith(vid+"&PID_")){
                continue;
            }
            device=device.replace(vid+"&PID_","");
            String uid=device.substring(0,4);
            uidVid.add(new String[]{vid,uid});
        }
        return uidVid;
    }

    private ArrayList addDeviceToInstallationQueueIfInList(ArrayList installqueue,String device){
        boolean inList=false;
        for (String vid:windowsDriverBlanket){
            if (device.startsWith("USB\\VID_"+vid)){
                installqueue.add(device);
            }
        }
        return installqueue;
    }
    
    
    /**
     * installDriverBlanket parses VID String Array windowsDriverBlanket and
     * calls installDriver() method for each.
     *
     * @param additionalVIDs optional String Array of additional VIDs to be
     * scanned for should normally always be null.
     *
     * @return a boolean sum of result. Value greater than 0 == success
     */
    public boolean installDriverBlanket(String[] additionalVIDs) {
        int resultSum = 0;
        for (int x = 0; windowsDriverBlanket.length > x; x++) {
            resultSum += (installDriver(windowsDriverBlanket[x]) ? 1 : 0);
        }
        if (additionalVIDs != null) {
            for (int x = 0; additionalVIDs.length > x; x++) {
                resultSum += (installDriver(additionalVIDs[x]) ? 1 : 0);
            }
        }
        return resultSum > 0;
    }

    /**
     * driverExtract extracts the contents of CADI.zip from CASUAL's resources
     *
     * @param pathToExtract the desired destination folders full path.
     *
     * @throws FileNotFoundException
     * @throws IOException
     *
     * @return true if successful, false otherwise
     */
    public boolean driverExtract(String pathToExtract) throws FileNotFoundException, IOException {
        if (OSTools.OSName().contains("Windows XP")) {
            if (new FileOperations().makeFolder(pathToCADI)) {
                Log.level4Debug("driverExtract() Unzipping CADI for xp");
                Unzip.unZipResource(cadiXpDrivers, pathToExtract);
                return true;
            }
            return false;
        } else if (new FileOperations().makeFolder(pathToCADI)) {
            Log.level4Debug("driverExtract() Unzipping CADI");
            Unzip.unZipResource(cadiDrivers, pathToExtract);
            return true;
        }
        return false;
    }

    /**
     * installDriver parses devcon output for connected devices matching the VID
     * parameter, and attempts to install LibusbK device driver (cadi.inf) via
     * devcon on devcon failure (only for Samsung) libwdi will attempt to
     * install the driver.
     *
     * @param VID target VID to scan & install drivers for.
     * @return true if driver is installed
     */
    public boolean installDriver(String VID) {
        if (VID.equals("")) {
            Log.level0Error("installDriver() no VID specified!");
            return false;
        }
        Log.level3Verbose("Installing driver for VID:" + VID);
        boolean installedPreviously = false;    //flags true if a previously installed HWID is found, to prevent redundant calls to devcon.
        String[] dList = getDeviceList(VID);    //get device HWID list for current VID
        if (dList == null) {
            Log.level0Error("installDriver() no target devices for VID: " + VID);
            return false;
        }
        String[] pastInstalls = new String[dList.length];   //pastInstalls stores a history of previously installed HWIDs for comparison
        for (int x = 0; x < dList.length && dList[x] != null; x++) {    //parse device HWID listing
            if (dList[x]==null){
                continue;
            }
            for (int y = 0; pastInstalls.length > y && pastInstalls[y] != null; y++) { //parse previously installed HWID
                if (pastInstalls[y].equals(dList[x])) { //checks for previously installed HWID (index y) against current device HWID (index x)
                    installedPreviously = true; //flags a redundant HWID
                }
            }
            if (!installedPreviously) { //checks if current HWID is redundant
                String retVal = devconCommand("update " + pathToCADI + "cadi.inf " + "\"" + dList[x] + "\"");
                if (retVal == null) {
                    Log.level0Error("installDriver() devcon returned null!");
                    return false;
                } else if (!retVal.contains("Drivers installed successfully") || retVal.contains(" failed")) {
                    Log.level0Error("installDriver() failed for " + "\"" + dList[x] + "\"!");
                }
                pastInstalls[x] = dList[x]; //add installed HWID to redundancy list
            }
        }
        return true;
    }

    /**
     * uninstallCADI attempts to remove any existing or previous remnants of
     * CADIv1 or CADIv2
     *
     * @return a boolean sum of result. Value greater than 0 == success
     */
    public boolean uninstallCADI() {
        int resultSum = 0;
        Log.level2Information("uninstallCADI() Initializing");
        Log.level2Information("uninstallCADI() Scanning for CADI driver package(s)");
        if (deleteOemInf()) {
            resultSum++;
        }

        Log.level2Information("uninstallCADI() Scanning for orphaned devices");
        for (int x = 0; windowsDriverBlanket.length > x; x++) {
            if (removeOrphanedDevices(windowsDriverBlanket[x])) {
                resultSum++;
            }
        }

        Log.level2Information("removeDriver() Windows will now scan for hardware changes");
        if (devconCommand("rescan") == null) {
            Log.level0Error("removeDriver() devcon returned null!");
        }
        return resultSum > 0;
    }

    /**
     * getDeviceList parses devcon output for connected USB devices of the
     * specified VID; Any matching devices are stored for return in a String
     * Array.
     *
     * @param VID a String containing a four character USB vendor ID code in
     * hexadecimal
     * @return is a String Array of matching connected devices, null otherwise
     */
    public String[] getDeviceList(String VID) {
        if (VID.equals("")) {
            Log.level0Error("getDeviceList() no VID specified");
            return null;
        } 
        String rawDeviceList = devconCommand("find *USB\\VID_" + VID + "*");
        if (rawDeviceList == null) {
            Log.level0Error("getDeviceList() devcon returned null!");
            return null;
        }
        Pattern pattern = regexPattern(PatternChoice.MATCHINGDEVICES);
        if (pattern == null) {
            Log.level0Error("getDeviceList() getRegExPattern() returned null!");
            return null;
        }
        Matcher matcher = pattern.matcher(rawDeviceList);
        
        //TODO: jrloper dlist is never used. 
        String[] dList = new String[Integer.parseInt(matcher.find() ? matcher.group(0).toString() : "0")];
        pattern = regexPattern(PatternChoice.ALLDEVICES);
        if (pattern == null) {
            Log.level0Error("getDeviceList() getRegExPattern() returned null!");
            return null;
        }

        dList = new String[Integer.parseInt(matcher.find() ? matcher.group(0).toString() : "0")];
        pattern = regexPattern(PatternChoice.ALLDEVICES);
        matcher = pattern.matcher(rawDeviceList);
        ArrayList<String> al=new ArrayList<String>();
        while (matcher.find()) {
            String replacedQuote = StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", ""));
            al.add(replacedQuote);
        }
        String[] retval=al.toArray(new String[al.size()]);
        if (retval.length==0){
            retval=null;
        }
        return retval;

    }

    /**
     * getDeviceList parses devcon output for devices specified Any matching
     * devices are stored for return in a String Array.
     *
     * @param onlyConnected boolean for presently connected devices only
     * @param onlyUSB boolean for USB devices only
     * @return is a String Array of matching devices, null otherwise
     */
    public String[] getDeviceList(boolean onlyConnected, boolean onlyUSB) {
        String rawDeviceList;
        if (onlyConnected && onlyUSB) { //All present USB devices
             rawDeviceList = devconCommand("find USB*");
        } else if (onlyConnected && !onlyUSB){
             rawDeviceList = devconCommand("find *");
        } else if (!onlyConnected && onlyUSB){
            rawDeviceList = devconCommand("findall USB*");
        } else {
            rawDeviceList = devconCommand("findall*");
        }

        if (rawDeviceList == null) {
            Log.level0Error("getDeviceList() devcon returned null!");
            return null;
        }
        Pattern pattern = regexPattern(PatternChoice.MATCHINGDEVICES);
        if (pattern == null) {
            Log.level0Error("getDeviceList() getRegExPattern() returned null!");
            return null;
        }
        pattern = regexPattern(PatternChoice.ALLDEVICES);
        Matcher matcher = pattern.matcher(rawDeviceList);
        ArrayList<String> al=new ArrayList<String>();
        while (matcher.find()) {
            String replacedQuote = StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", ""));
            al.add(replacedQuote);
        }
        String[] retval=al.toArray(new String[al.size()]);
        if (retval.length==0){
            retval=null;
        }
        return retval;

    }

    /**
     * getCASUALDriverCount parses devcon output for all CASUAL driver
     * installations and returns an integer count
     *
     * @return integer count of CASUAL driver installs
     */
    public int getCASUALDriverCount() {
        int devCount = 0;
        String outputBuffer = devconCommand("findall USB*");
        if (outputBuffer == null) {
            Log.level0Error("removeOrphanedDevices() devcon returned null!");
            return 0;
        }
        Pattern pattern = regexPattern(PatternChoice.CASUALDRIVER);
        if (pattern == null) {
            Log.level0Error("removeOrphanedDevices() getRegExPattern() returned null!");
            return 0;
        }
        Matcher matcher = pattern.matcher(outputBuffer);
        while (matcher.find()) {
            devCount++;
        }
        return devCount;
    }

    /**
     * removeOrphanedDevices parses devcon output of any current or previously
     * installed USB device drivers for the specified VID. Any matching device
     * drivers are uninstalled
     *
     * @param VID a String containing a four character USB vendor ID code in
     * hexadecimal
     * @return a String Array of devcon output from attempted uninstalls of
     * drivers
     */
    public boolean removeOrphanedDevices(String VID) {
        int i = 0;
        int resultSum = 0;
        String result;
        if (VID.equals("")) {
            Log.level0Error("removeOrphanedDevices() no VID specified");
            return false;
        } 
        Pattern pattern = regexPattern(PatternChoice.MATCHINGDEVICES);
        if (pattern == null) {
            Log.level0Error("removeOrphanedDevices() getRegExPattern() returned null!");
            return false;
        } 
        String outputBuffer = devconCommand("findall *USB\\VID_" + VID + "*");
        if (outputBuffer == null) {
            Log.level0Error("removeOrphanedDevices() devcon returned null!");
            return false;
        } 
        pattern = regexPattern(PatternChoice.ORPHANS);
        if (pattern == null) {
            Log.level0Error("removeOrphanedDevices() getRegExPattern() returned null!");
            return false;
        } 
        Matcher matcher = pattern.matcher(outputBuffer);
        while (matcher.find()) {
            Log.level2Information("removeOrphanedDevices() Removing orphaned device " + "\"@" + StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", "")) + "\"");
            result = devconCommand("remove " + "\"@" + StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", "")) + "\"");
            if (result.equals("")) {
            } else if (result.contains("device(s) are ready to be removed. To remove the devices, reboot the system.")) {
                resultSum++;
            } else {
                Log.level0Error("removeOrphanedDevices() devcon returned null!");
            }
            i++;
        }



        return resultSum > 0;
    }

    /**
     * deleteOemInf parses output from devconCommand via regex to extract the
     * name of the *.inf file from Windows driver store. Extraction of the file
     * name is determined by setup classes & provider names.
     *
     * @return a String Array of *.inf files matching the search criteria.
     */
    public boolean deleteOemInf() {
        Log.level2Information("deleteOemInf() Enumerating installed driver packages");
        int resultSum = 0;
        Pattern pattern = regexPattern(PatternChoice.INF);
        String outputBuffer = devconCommand("dp_enum");
        if (outputBuffer == null) {
            Log.level0Error("deleteOemInf() devcon returned null!");
            return false;
        } 

        Matcher matcher = pattern.matcher(outputBuffer);
        while (matcher.find()) {
            Log.level2Information("removeDriver() Forcing removal of driver package" + matcher.group(0));
            String result = devconCommand("-f dp_delete " + matcher.group(0));
            if (result == null || result.contains("Driver package")) {
                Log.level0Error("removeDriver() devcon returned null!");
            }
            resultSum++;
        }


        return resultSum > 0;
    }

    /**
     * devconCommand executes Devcon (x86 or x64 depending upon detected Windows
     * architecture) using the callers arguments.
     *
     * @param args a String of arguments (space delimited) to be passed to
     * Devcon
     * @return the console output of the executed command. Error messages and
     * null may also be returned should the command fail to execute or the
     * method was called improperly.
     */
    public String devconCommand(String args) {
        if (!args.equals("")) {
            if (!driverExtracted) {
                try {
                    driverExtract(pathToCADI);
                } catch (FileNotFoundException ex) {
                    Log.errorHandler(ex);
                    return null;
                } catch (IOException ex) {
                    Log.errorHandler(ex);
                    return null;
                }
                driverExtracted = true;
            }
            String exec = pathToCADI + (OSTools.is64bitSystem() ? "driver_x64.exe " : "driver_x86.exe ") + args;
            String retval;
            retval = new Shell().timeoutShellCommand(new String[]{"cmd.exe", "/C", "\"" + exec + "\""}, 90000); //1000 milliseconds — one second
            Log.level2Information(retval);
            return retval;
        } else {
            Log.level0Error("devconCommand() no command specified");
            return null;
        }
    }

    /**
     * regexPattern returns a Pattern Object of the requested REGEX pattern.
     *
     * @param whatPattern a predefined String name for a REGEX pattern.
     * @return a compiled REGEX Pattern if requested pattern exists, otherwise
     * null.
     */
    public Pattern regexPattern(PatternChoice whatPattern) {
        switch (whatPattern) {
            case ORPHANS:
                return Pattern.compile("USB.?VID_[0-9a-fA-F]{4}&PID_[0-9a-fA-F]{4}.*(?=:\\s[CASUAL's|Samsung]+\\s[Android\\sDevice])");
            case CASUALDRIVER:
                return Pattern.compile("USB.?VID_[0-9a-fA-F]{4}&PID_[0-9a-fA-F]{4}.*(?=:\\s[CASUAL's|Samsung]+\\s[Android\\sDevice])");
            case INF:
                return Pattern.compile("[o|Oe|Em|M]{3}[0-9]{1,4}\\.inf(?=\\s*Provider:\\slibusbK\\s*Class:\\s*libusbK USB Devices)");
            case INSTALL:
                return Pattern.compile("USB.?VID_[0-9a-fA-F]{4}&PID_[0-9a-fA-F]{4}(?=.*:)");
            case MATCHINGDEVICES:
                return Pattern.compile("(?<=\\s)[0-9]{1,3}?(?=[\\smatching\\sdevice\\(s\\)\\sfound])");
            case ALLDEVICES:
                return Pattern.compile("\\S+(?=\\s*:\\s)");
            default:
                Log.level0Error("getRegExPattern() no known pattern requested");
                return null;
        }
    }

    public enum PatternChoice {

        ORPHANS, CASUALDRIVER, INF, INSTALL, MATCHINGDEVICES, ALLDEVICES
    }

}
