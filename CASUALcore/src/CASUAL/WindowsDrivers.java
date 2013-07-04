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
 *
 * @author Jeremy Loper jrloper@gmail.com
 * *************************************************************************
 */
public class WindowsDrivers {

    private Log log = new Log();
    private final String pathToCADI;
    private final String[] windowsDriverBlanket;
    protected static volatile boolean driverExtracted = false;
    protected static volatile int driverRemoveOnDone=0;//0=unset 1=do not remove 2=remove on exit
    
    public WindowsDrivers() {
        log.level4Debug("WindowsDrivers() Initializing");
        this.windowsDriverBlanket = new String[]{"04E8", "0B05", "0BB4", "22B8", "054C", "2080", "18D1"};
        
        this.pathToCADI = Statics.TempFolder + "CADI" + Statics.Slash + "libusbk" + Statics.Slash;
        if (driverRemoveOnDone==0){ //so it only asks once
            //set value as 2 if true and 1 if false
            driverRemoveOnDone = new CASUALInteraction("@interactionInstallingCADI").showYesNoOption()?2:1;
        }
    }

    public static void main(String[] args) {
        WindowsDrivers wd = new WindowsDrivers();
        wd.installDriverBlanket();
        wd.removeDriver();
     }
    
    public void installDriverBlanket() {
        for (int x = 0; windowsDriverBlanket.length > x; x++) {
            installDriver(windowsDriverBlanket[x]);
        }
    }

    private void driverExtract() throws FileNotFoundException, IOException {
        if(new FileOperations().makeFolder(Statics.TempFolder + "CADI/")) {
            log.level4Debug("driverExtract() Unzipping CADI");
            Unzip.unZipResource("/CASUAL/resources/heimdall/CADI.zip", Statics.TempFolder + "CADI" + Statics.Slash);
        }
    }

    private void installDriver(String VID) {
        log.level3Verbose("Installing driver for VID:" + VID);
        if (!VID.equals("")) {
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
                    if (Statics.OSName().contains("Windows XP") && !installedPreviously) {
                        if (devconCommand("update " + pathToCADI + "xp" + Statics.Slash + "cadixp.inf " + "\"" + dList[x] + "\"") == null) {
                            log.level0Error("devconCommand() returned null!");
                        }
                    } else if (!installedPreviously){
                        if(devconCommand("update " + pathToCADI + "cadiV78.inf " + "\"" + dList[x] + "\"") == null) {
                            log.level0Error("devconCommand() returned null!");
                        }
                    }
                    pastInstalls[x] = dList[x];
                } 
            }else {
                log.level0Error("getDeviceList() returned null!");
            }
        } else {
            log.level0Error("installDriver() no VID specified!");
        }
    }
    
    public void removeDriver() {
        log.level2Information("removeDriver() Initializing");
        String[] infString = getOemInfName();
        if (infString != null) {
            log.level2Information("removeDriver() Forcing removal of driver package");
            for (int x = 0; infString.length > x && infString[x] != null; x++) {
                if (devconCommand("-f dp_delete " + infString[x]) == null) {
                    log.level0Error("devconCommand() returned null!");
                }
            }
        } else {
            log.level0Error("getOemInfName() returned null!");
        }

        log.level2Information("removeDriver() Removing orphaned device installations");
        for (int y = 0; windowsDriverBlanket.length > y; y++) {
            String[] orphanedDevices = getOrphanedDevices(windowsDriverBlanket[y]);
            if (orphanedDevices != null) {
                for (int x = 0; x < orphanedDevices.length && orphanedDevices[x] != null; x++) {
                    if (devconCommand("remove " + "\"" + orphanedDevices[x] + "\"") == null) {
                        log.level0Error("devconCommand() returned null!");
                    }
                }
            } else {
                log.level0Error("getOrphanedDevices() returned null!");
            }
        }
        
        log.level2Information("removeDriver() Windows will now scan for hardware changes");
        if (devconCommand("rescan") == null) {
            log.level0Error("devconCommand() returned null!");
        }
    }

    private String[] getDeviceList(String VID) {
        if (!VID.equals("")) {
            String outputBuffer = devconCommand("find *USB\\VID_" + VID + "*");
            if (outputBuffer != null) {
                Pattern pattern = getRegExPattern("Matching devices");
                if (pattern != null) {
                    Matcher matcher = pattern.matcher(outputBuffer);
                    int stringLength = Integer.parseInt((matcher.find() ? matcher.group(0).toString() : "0"));
                    String[] dList = new String[stringLength];
                    pattern = getRegExPattern("install");
                    
                    if (pattern != null) {
                        matcher = pattern.matcher(outputBuffer);
                        for (int x = 0; matcher.find(); x++) {
                            dList[x] = StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", ""));
                        }
                        return dList;
                    } else {
                        log.level0Error("getRegExPattern() returned null!");
                        return null;
                    }
                } else {
                    log.level0Error("getRegExPattern() returned null!");
                    return null;
                }
            } else {
                log.level0Error("devconCommand() returned null!");
                return null;
            }
        } else {
            log.level0Error("getDeviceList() no VID specified");
            return null;
        }
    }

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
                        log.level0Error("getRegExPattern() returned null!");
                        return null;
                    }
                } else {
                    log.level0Error("devconCommand() returned null!");
                    return null;
                }
            } else {
                log.level0Error("getRegExPattern() returned null!");
                return null;
            }
        } else {
            log.level0Error("getOrphanedDevices() no VID specified");
            return null;
        }
    }

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
                log.level0Error("devconCommand() returned null!");
                return null;
            }
        } else {
            log.level0Error("getRegExPattern() returned null!");
            return null;
        }
    }

    private String devconCommand(String Cmd) {
        if (!Cmd.equals("")) {
            if (!driverExtracted) {
                try {
                    driverExtract();
                } catch (FileNotFoundException ex) {
                    log.errorHandler(ex);
                    return null;
                } catch (IOException ex) {
                    log.errorHandler(ex);
                    return null;
                }
                driverExtracted = true;
            }
            String exec = pathToCADI + (Statics.is64bitSystem() ? "devcon_x64.exe " : "devcon_x86.exe ") + Cmd;
            String retval=new Shell().silentShellCommand(new String[]{"cmd.exe", "/C", "\"" + exec + "\""});
            log.level2Information(retval);
            return retval;
        } else {
            log.level0Error("devconCommand() no command specified");
            return null;
        } 
    }

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