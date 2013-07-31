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
 * class-wide: Javadoc launchOldFaithful, getDeviceList, removeOrphanedDevices,
 * uninstallCADI
 *
 * Q:Possible to install WinUSB in same manner if this has been run before?
 * A:Possible, but overly complicated and probably unreliable without parsing
 * all matching *.inf's contents
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * **************************************************************************
 * @author Jeremy Loper jrloper@gmail.com
 * @author Adam Outler adamoutler@gmail.com
 * *************************************************************************
 */
public class WindowsDrivers {

    private Log log = new Log();
    private final String pathToCADI;
    private final String[] windowsDriverBlanket;
    /**
     * true if driver has been prepared.
     */
    protected static volatile boolean driverExtracted = false;
    /**
     * Should driver be removed on script completion? 0 - Unset (will prompt
     * user) 1 - Do not remove driver on completion 2 - Remove driver on script
     * completion
     */
    protected static volatile int removeDriverOnCompletion;

    /**
     * WindowsDrivers instantiates the windows driver class.
     *
     * @param promptInit initializes removeDriverOnCompletion member and
     * subsequent prompting action. 0 - Unset (will prompt user) (default) 1 -
     * Do not remove driver on completion 2 - Remove driver on script completion
     */
    public WindowsDrivers(int promptInit) {
        removeDriverOnCompletion = promptInit;
        log.level4Debug("WindowsDrivers() Initializing");
        this.windowsDriverBlanket = new String[]{"04E8", "0B05", "0BB4", "22B8", "054C", "2080", "18D1"};
        this.pathToCADI = Statics.TempFolder + "CADI" + Statics.Slash;
        if (removeDriverOnCompletion == 0) { //so it only asks once
            removeDriverOnCompletion = new CASUALInteraction("@interactionInstallingCADI").showYesNoOption() ? 2 : 1; //set value as 2 if true and 1 if false
        }
    }

    /**
     * main is the default execution entrypoint, however In this class it is
     * used only for testing purposes and should not be called externally.
     *
     * @param args
     */
    public static void main(String[] args) {
        WindowsDrivers wd = new WindowsDrivers(0);
        wd.installDriverBlanket(null);
        wd.uninstallCADI();
    }

    /**
     * installDriverBlanket parses VID String Array windowsDriverBlanket and
     * calls installDriver() method for each.
     *
     * @param additionalVIDs optional String Array of additional VIDs to be
     * scanned for should normally always be null.
     */
    public void installDriverBlanket(String[] additionalVIDs) {
        for (int x = 0; windowsDriverBlanket.length > x; x++) {
            installDriver(windowsDriverBlanket[x]);
        }
        if (additionalVIDs != null) {
            for (int x = 0; additionalVIDs.length > x; x++) {
                installDriver(additionalVIDs[x]);
            }
        }
    }

    /**
     * driverExtract extracts the contents of CADI.zip from CASUAL's resources
     *
     * @param pathToExtract the desired destination folders full path.
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void driverExtract(String pathToExtract) throws FileNotFoundException, IOException {
        if (OSTools.OSName().contains("Windows XP")) {
            if (new FileOperations().makeFolder(pathToCADI)) {
                log.level4Debug("driverExtract() Unzipping CADI for xp");
                Unzip.unZipResource("/CASUAL/resources/heimdall/xp/CADI.zip", pathToExtract);
            }
        } else if (new FileOperations().makeFolder(pathToCADI)) {
            log.level4Debug("driverExtract() Unzipping CADI");
            Unzip.unZipResource("/CASUAL/resources/heimdall/CADI.zip", pathToExtract);
        }
    }

    /**
     * installDriver parses devcon output for connected devices matching the VID
     * parameter, and attempts to install LibusbK device driver (cadi.inf) via
     * devcon on devcon failure (only for Samsung) libwdi will attempt to
     * install the driver.
     *
     * @param VID target VID to scan & install drivers for.
     * @return
     */
    public boolean installDriver(String VID) {
        if (VID.equals("")) {
            log.level0Error("installDriver() no VID specified!");
            return false;
        }
        log.level3Verbose("Installing driver for VID:" + VID);
        boolean installedPreviously = false;    //flags true if a previously installed HWID is found, to prevent redundant calls to devcon.
        String[] dList = getDeviceList(VID);    //get device HWID list for current VID
        if (dList != null) {
            String[] pastInstalls = new String[dList.length];   //pastInstalls stores a history of previously installed HWIDs for comparison
            for (int x = 0; x < dList.length && dList[x] != null; x++) {    //parse device HWID listing
                for (int y = 0; pastInstalls.length > y && pastInstalls[y] != null; y++) { //parse previously installed HWID
                    if (pastInstalls[y].equals(dList[x])) { //checks for previously installed HWID (index y) against current device HWID (index x)
                        installedPreviously = true; //flags a redundant HWID
                    }
                }
                if (!installedPreviously) { //checks if current HWID is redundant
                    String retVal = devconCommand("update " + pathToCADI + "cadi.inf " + "\"" + dList[x] + "\"");
                    if (retVal == null) {
                        log.level0Error("installDriver() devcon returned null!");
                        return false;
                    } else if (!retVal.contains("Drivers installed successfully") || retVal.contains(" failed")) {
                        log.level0Error("installDriver() failed for " + "\"" + dList[x] + "\"!");
                        if (VID.equals("04E8")) {
                            return launchOldFaithful();
                        } else {
                            return false;
                        }
                    }
                    pastInstalls[x] = dList[x]; //add installed HWID to redundancy list
                }
            }
        } else {
            log.level0Error("installDriver() no target devices for VID: " + VID);
            return false;
        }
        return true;
    }

    /**
     * launchOldFaithful
     *
     * @return
     */
    private boolean launchOldFaithful() {
        String exec = "";
        try {
            if (OSTools.OSName().contains("Windows XP")) {
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
     * uninstallCADI
     *
     */
    public void uninstallCADI() {
        log.level2Information("uninstallCADI() Initializing");
        log.level2Information("uninstallCADI() Scanning for CADI driver package(s)");
        deleteOemInf();

        log.level2Information("uninstallCADI() Scanning for orphaned devices");
        for (int x = 0; windowsDriverBlanket.length > x; x++) {
            removeOrphanedDevices(windowsDriverBlanket[x]);
        }

        log.level2Information("removeDriver() Windows will now scan for hardware changes");
        if (devconCommand("rescan") == null) {
            log.level0Error("removeDriver() devcon returned null!");
        }
    }

    /**
     * getDeviceList
     *
     * @param VID a String containing a four character USB vendor ID code in
     * hexadecimal
     * @return
     */
    private String[] getDeviceList(String VID) {
        if (!VID.equals("")) {
            String outputBuffer = devconCommand("find *USB\\VID_" + VID + "*");
            if (outputBuffer != null) {
                Pattern pattern = getRegExPattern("Matching devices");
                if (pattern != null) {
                    Matcher matcher = pattern.matcher(outputBuffer);
                    String[] dList = new String[Integer.parseInt(matcher.find() ? matcher.group(0).toString() : "0")];
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
     * removeOrphanedDevices
     *
     * @param VID a String containing a four character USB vendor ID code in
     * hexadecimal
     * @return
     */
    private void removeOrphanedDevices(String VID) {
        if (!VID.equals("")) {
            Pattern pattern = getRegExPattern("Matching devices");
            if (pattern != null) {
                String outputBuffer = devconCommand("findall *USB\\VID_" + VID + "*");
                if (outputBuffer != null) {

                    pattern = getRegExPattern("orphans");
                    if (pattern != null) {
                        Matcher matcher = pattern.matcher(outputBuffer);
                        while (matcher.find()) {
                            log.level2Information("removeOrphanedDevices() Removing orphaned device " + "\"@" + StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", "")) + "\"");
                            if (devconCommand("remove " + "\"@" + StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", "")) + "\"") == null) {
                                log.level0Error("removeOrphanedDevices() devcon returned null!");
                            }
                        }
                    } else {
                        log.level0Error("removeOrphanedDevices() getRegExPattern() returned null!");
                    }
                } else {
                    log.level0Error("removeOrphanedDevices() devcon returned null!");
                }
            } else {
                log.level0Error("removeOrphanedDevices() getRegExPattern() returned null!");
            }
        } else {
            log.level0Error("removeOrphanedDevices() no VID specified");
        }
    }

    /**
     * deleteOemInf parses output from devconCommand via regex to extract the
     * name of the *.inf file from Windows driver store. Extraction of the file
     * name is determined by setup classes & provider names.
     *
     * @return a String Array of *.inf files matching the search criteria.
     */
    private void deleteOemInf() {
        log.level2Information("deleteOemInf() Enumerating installed driver packages");
        Pattern pattern = getRegExPattern("inf");
        if (pattern != null) {
            String outputBuffer = devconCommand("dp_enum");
            if (outputBuffer != null) {
                Matcher matcher = pattern.matcher(outputBuffer);
                //TODO: examine this to find out why we are iterating through but only using "0"
                for (int x = 0; matcher.find(); x++) {
                    log.level2Information("removeDriver() Forcing removal of driver package" + matcher.group(0));
                    if (devconCommand("-f dp_delete " + matcher.group(0)) == null) {
                        log.level0Error("removeDriver() devcon returned null!");
                    }
                }
            } else {
                log.level0Error("deleteOemInf() devcon returned null!");
            }
        } else {
            log.level0Error("deleteOemInf() getRegExPattern() returned null!");
        }
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
            String exec = pathToCADI + (OSTools.is64bitSystem() ? "driver_x64.exe " : "driver_x86.exe ") + args;
            String retval;
            retval = new Shell().timeoutShellCommand(new String[]{"cmd.exe", "/C", "\"" + exec + "\""}, 90000); //1000 milliseconds — one second
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
     * @param `whatPattern a predefined String name for a REGEX pattern.
     * @return a compiled REGEX Pattern if requested pattern exists, otherwise
     * null.
     */
    private Pattern getRegExPattern(String whatPattern) {
        if (!whatPattern.equals("")) {
            switch (whatPattern) {
                case "orphans":
                    return Pattern.compile("USB.?VID_[0-9a-fA-F]{4}&PID_[0-9a-fA-F]{4}.*(?=:\\s[CASUAL's|Samsung]+\\s[Android\\sDevice])");
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