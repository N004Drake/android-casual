/*WindowsDrivers.java
 * **************************************************************************
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
 ***************************************************************************/
package CASUAL;

/**
 * TODOs:
 * 
 * class-wide:
 * Javadoc all methods
 * 
 * method installDriver():
 * Documentation needed for x/y/pastInstalls
 * Possible to install WinUSB in same manner if this has been run before?
 * 
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * **************************************************************************
 * @author Jeremy Loper jrloper@gmail.com
 * @author Adam Outler  adamoutler@gmail.com
 * *************************************************************************
 */
public class WindowsDrivers {

    private Log log = new Log();
    private final String pathToCADI;
    private final String[] windowsDriverBlanket;
    private String currentVID;
    protected static volatile boolean driverExtracted = false;
    protected static volatile int driverRemoveOnDone = 0; //0=unset 1=do not remove 2=remove on exit
    
    /**
     * WindowsDrivers
     * @param promptInit
     */
    public WindowsDrivers(int promptInit) {
        driverRemoveOnDone = promptInit;
        log.level4Debug("WindowsDrivers() Initializing");
        this.windowsDriverBlanket = new String[]{"04E8", "0B05", "0BB4", "22B8", "054C", "2080", "18D1"};
        this.pathToCADI = Statics.TempFolder + "CADI" + Statics.Slash;
        if (driverRemoveOnDone == 0){ //so it only asks once
            driverRemoveOnDone = (new CASUALInteraction("@interactionInstallingCADI").showYesNoOption() ? 2 : 1); //set value as 2 if true and 1 if false
        }
    }

    /**
     * <code>main</code> execution entrypoint.
     * In this class, used for testing purposes only
     * 
     * @param args 
     */
    public static void main(String[] args) {
        WindowsDrivers wd = new WindowsDrivers(0);
        wd.installDriverBlanket(null);
        wd.removeDriver();
     }
    
    /**
     * <code>installDriverBlanket</code>
     */
    public void installDriverBlanket(String[] additionalVIDs) {
        for (int x = 0; windowsDriverBlanket.length > x; x++) {
            installDriver(windowsDriverBlanket[x]);
        }
        if(additionalVIDs != null) {
            for (int x = 0; additionalVIDs.length > x; x++) {
                installDriver(additionalVIDs[x]);
            }
        }
    }

    /**
     * driverExtract extracts the contents of CADI.zip from CASUAL's resources
     * @param   pathToExtract           the desired destination folders full path.
     * 
     * @throws  FileNotFoundException
     * @throws  IOException 
     */
    private void driverExtract(String pathToExtract) throws FileNotFoundException, IOException {
        if(Statics.OSName().contains("Windows XP")){
            if(new FileOperations().makeFolder(pathToCADI)) {
            log.level4Debug("driverExtract() Unzipping CADI for xp");
            Unzip.unZipResource("/CASUAL/resources/heimdall/xp/CADI.zip", pathToExtract);
            }
        } else if(new FileOperations().makeFolder(pathToCADI)) {
            log.level4Debug("driverExtract() Unzipping CADI");
            Unzip.unZipResource("/CASUAL/resources/heimdall/CADI.zip", pathToExtract);
        }
    }

    /**
     * installDriver
     * 
     * @param VID
     * @return 
     */
    public boolean installDriver(String VID) {
        if (VID.equals("")) {
            log.level0Error("installDriver() no VID specified!");
            return false;
        }
        log.level3Verbose("Installing driver for VID:" + VID);
        boolean installedPreviously = false;
        String[] dList = getDeviceList(VID);
        if (dList != null) {
            String[] pastInstalls = new String[dList.length];
            for (int x = 0; x < dList.length && dList[x] != null; x++) {
                for (int y = 0; pastInstalls.length > y && pastInstalls[y] != null; y++) { 
                    if (pastInstalls[y].equals(dList[x])) {
                        installedPreviously = true;
                    }
                }
                if (!installedPreviously){
                    String retVal = devconCommand("update " + pathToCADI + "cadi.inf " + "\"" + dList[x] + "\"");
                    if (retVal == null) {
                        log.level0Error("installDriver() devcon returned null!");
                        return false;
                    } else if (!retVal.contains("Drivers installed successfully") || retVal.contains(" failed")) {
                        log.level0Error("installDriver() failed for "+ "\"" + dList[x] + "\"!");
                        return launchOldFaithful();
                    }
                }
                pastInstalls[x] = dList[x];
            } 
        }else {
            log.level0Error("installDriver() no target devices for VID: " + VID);
            return false;
        }
        return true;
    }
    
    /**
     * installDriver
     * 
     * @param VID
     * @return 
     */
    private boolean launchOldFaithful() {
        String exec = "";
        try {
            if(Statics.OSName().contains("Windows XP")) {
                if (new FileOperations().verifyResource(Statics.WinDriverResource2)) {
                    exec = Statics.TempFolder + "CADI.exe";
                    new FileOperations().copyFromResourceToFile(Statics.WinDriverResource2, exec);
                }
            } else {
                if (new FileOperations().verifyResource(Statics.WinDriverResource)) {
                    exec = Statics.TempFolder + "CADI.exe";
                    new FileOperations().copyFromResourceToFile(Statics.WinDriverResource, exec);
                } else {
                    exec = new CASUALUpdates().CASUALRepoDownload("https://android-casual.googlecode.com/svn/trunk/repo/driver.properties");
                }
            }
        } catch (IOException | InterruptedException ex) {
            log.level0Error("@problemWithOnlineRepo");
        }
        //verify MD5
        String driverreturn = new Shell().sendShellCommand(new String[]{"cmd.exe", "/C", "\"" + exec + "\""});
        log.level2Information(driverreturn);
        if (driverreturn.contains("CritError")) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * removeDriver
     * 
     */
    public void removeDriver() {
        log.level2Information("removeDriver() Initializing");
        log.level2Information("removeDriver() Scanning for CADI driver package(s)");
        String[] infString = getOemInfName();
        if (infString != null) {
            for (int x = 0; infString.length > x && infString[x] != null; x++) {
                log.level2Information("removeDriver() Forcing removal of driver package: " + infString[x]);
                if (devconCommand("-f dp_delete " + infString[x]) == null) {
                    log.level0Error("removeDriver() devcon returned null!");
                }
            }
        } else {
            log.level0Error("removeDriver() getOemInfName() returned null!");
        }

        log.level2Information("removeDriver() Scanning for orphaned devices");
        for (int y = 0; windowsDriverBlanket.length > y; y++) {
            String[] orphanedDevices = getOrphanedDevices(windowsDriverBlanket[y]);
            if (orphanedDevices != null) {
                for (int x = 0; x < orphanedDevices.length && orphanedDevices[x] != null; x++) {
                    log.level2Information("removeDriver() Removing orphaned device installation");
                    if (devconCommand("remove " + "\"" + orphanedDevices[x] + "\"") == null) {
                        log.level0Error("removeDriver() devcon returned null!");
                    }
                }
            } else {
                log.level0Error("removeDriver() getOrphanedDevices() returned null!");
            }
        }
        
        log.level2Information("removeDriver() Windows will now scan for hardware changes");
        if (devconCommand("rescan") == null) {
            log.level0Error("removeDriver() devcon returned null!");
        }
    }

    /**
     * getDeviceList
     * 
     * @param   VID     a String containing a four character USB vendor ID code in hexadecimal
     * @return 
     */
    private String[] getDeviceList(String VID) {
        if (!VID.equals("")) {
            String outputBuffer = devconCommand("find *USB\\VID_" + VID + "*");
            if (outputBuffer != null) {
                Pattern pattern = getRegExPattern("Matching devices");
                if (pattern != null) {
                    Matcher matcher = pattern.matcher(outputBuffer);
                    String[] dList = new String[Integer.parseInt((matcher.find() ? matcher.group(0).toString() : "0"))];
                    pattern = getRegExPattern("install");
                    
                    if (pattern != null) {
                        matcher = pattern.matcher(outputBuffer);
                        for (int x = 0; matcher.find(); x++) {
                            dList[x] = StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", ""));
                        }
                        return dList;
                    } else {
                        log.level0Error("getDeviceList() getRegExPattern() returned null!");
                        return null;
                    }
                } else {
                    log.level0Error("getDeviceList() getRegExPattern() returned null!");
                    return null;
                }
            } else {
                log.level0Error("getDeviceList() devcon returned null!");
                return null;
            }
        } else {
            log.level0Error("getDeviceList() no VID specified");
            return null;
        }
    }

    /**
     * getOrphanedDevices
     * 
     * @param   VID     a String containing a four character USB vendor ID code in hexadecimal
     * @return 
     */
    private String[] getOrphanedDevices(String VID) {
        if (!VID.equals("")) {
            Pattern pattern = getRegExPattern("Matching devices");
            if (pattern != null) {
                String outputBuffer = devconCommand("findall *USB\\VID_" + VID + "*");
                if (outputBuffer != null) {
                    Matcher matcher = pattern.matcher(outputBuffer);
                    String[] dList = new String[Integer.parseInt((matcher.find() ? matcher.group(0) : "0"))];
                    pattern = getRegExPattern("orphans");
                    if (pattern != null) {
                        matcher = pattern.matcher(outputBuffer);
                        for (int x = 0; matcher.find(); x++) {
                            dList[x] = StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", ""));
                        }
                        return dList;
                    } else {
                        log.level0Error("getOrphanedDevices() getRegExPattern() returned null!");
                        return null;
                    }
                } else {
                    log.level0Error("getOrphanedDevices() devcon returned null!");
                    return null;
                }
            } else {
                log.level0Error("getOrphanedDevices() getRegExPattern() returned null!");
                return null;
            }
        } else {
            log.level0Error("getOrphanedDevices() no VID specified");
            return null;
        }
    }

    /**
     * getOemInfName parses output from devconCommand
     * via regex to extract the name of the *.inf file from Windows
     * driver store. Extraction of the file name is determined by setup classes 
     * & provider names.
     * 
     * @return      a String Array of *.inf files matching the search criteria.
     */
    private String[] getOemInfName() {
        log.level2Information("getOemInfName() Enumerating installed driver packages");
        Pattern pattern = getRegExPattern("inf");
        if (pattern != null) {
            String[] oemBuffer = new String[9];
            String outputBuffer = devconCommand("dp_enum");
            if (outputBuffer != null) {
                Matcher matcher = pattern.matcher(outputBuffer);
                for (int x = 0; matcher.find(); x++) {
                    oemBuffer[x] = matcher.group(0);
                }
                return oemBuffer;
            } else {
                log.level0Error("getOemInfName() devcon returned null!");
                return null;
            }
        } else {
            log.level0Error("getOemInfName() getRegExPattern() returned null!");
            return null;
        }
    }

    /**
     * devconCommand executes Devcon (x86 or x64 depending upon
     * detected Windows architecture) using the callers arguments.
     * 
     * @param   args    a String of arguments (space delimited) to be passed to Devcon 
     * @return          the console output of the executed command. 
     *                  Error messages and null may also be returned 
     *                  should the command fail to execute or the method was called
     *                  improperly.
     */
    private String devconCommand(String args) {
        if (!args.equals("")) {
            if (!driverExtracted) {
                try {
                    driverExtract(pathToCADI);
                } catch (FileNotFoundException ex) {
                    log.errorHandler(ex);
                    return null;
                } catch (IOException ex) {
                    log.errorHandler(ex);
                    return null;
                }
                driverExtracted = true;
            }
            String exec = pathToCADI + (Statics.is64bitSystem() ? "devcon_x64.exe " : "devcon_x86.exe ") + args;
            String retval;
            retval = new Shell().timeoutShellCommand(new String[]{"cmd.exe", "/C", "\"" + exec + "\""},90000); //1000 milliseconds — one second
            log.level2Information(retval);
            return retval;
        } else {
            log.level0Error("devconCommand() no command specified");
            return null;
        } 
    }
    
    /**
     * getRegExPattern returns a Pattern Object of the requested REGEX pattern.
     * 
     * @param `whatPattern  a predefined String name for a REGEX pattern.
     * @return              a compiled REGEX Pattern if requested pattern exists, otherwise null.
     */
    private Pattern getRegExPattern(String whatPattern) {
        if (!whatPattern.equals("")) {
            switch (whatPattern) {
                case "orphans":
                    return Pattern.compile("USB.?VID_[0-9a-fA-F]{4}&PID_[0-9a-fA-F]{4}(?=.*:\\s[CASUAL's|Samsung]+\\s[Android\\sDevice])");
                case "inf":
                    return Pattern.compile("[o|Oe|Em|M]{3}[0-9]{1,4}\\.inf(?=\\s*Provider:\\slibusbK\\s*Class:\\s*libusbK USB Devices)");
                case "install":
                    return Pattern.compile("USB.?VID_[0-9a-fA-F]{4}&PID_[0-9a-fA-F]{4}(?=.*:)");
                case "Matching devices":
                    return Pattern.compile("(?<=\\s)[0-9]{1,3}?(?=[\\smatching\\sdevice\\(s\\)\\sfound])");
                default:
                    log.level0Error("getRegExPattern() no known pattern requested");
                    return null;
            }
        } else {
            log.level0Error("getRegExPattern() no pattern requested");
            return null;
        }
    }
}