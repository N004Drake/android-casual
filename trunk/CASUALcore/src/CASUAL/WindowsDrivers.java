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
    private Shell shell = new Shell();
    private final String[] windowsDriverBlanket;
    protected static volatile boolean driverExtracted = false;
    protected static volatile boolean driverRemoveOnDone = false;
    private final String pathToCADI;
    private String[] pastInstalls;

    public WindowsDrivers() {
        log.level4Debug("WindowsDrivers() Initializing");
        this.windowsDriverBlanket = new String[]{"04E8", "0B05", "0BB4", "22B8", "054C", "2080", "18D1"};
        this.pathToCADI = Statics.TempFolder + "CADI" + Statics.Slash + "libusbk" + Statics.Slash;
        this.pastInstalls = new String[99];
        driverRemoveOnDone = new CASUALInteraction("CADI", "CASUAL will now install a generic USB driver.\n\nThis will allow communications between CASUAL and your device\n\nWould you like CASUAL to remove this generic driver when the operation has completed?").showYesNoOption();
    }
    
    public static void main(String[] args) {
        new WindowsDrivers().installDriverBlanket();
        new WindowsDrivers().removeDriver();
    }
    
    public void installDriverBlanket() {
         for(int x = 0; windowsDriverBlanket.length > x; x++) installDriver(windowsDriverBlanket[x]);
    }

    private boolean driverExtract() {
        new FileOperations().makeFolder(Statics.TempFolder + "CADI/");
        try {
            log.level4Debug("driverExtract() Unzipping CADI");
            Unzip.unZipResource("/CASUAL/resources/heimdall/CADI.zip", Statics.TempFolder + "CADI" + Statics.Slash);
        } catch (FileNotFoundException ex) {
            log.errorHandler(ex);
            return false;
        } catch (IOException ex) {
            log.errorHandler(ex);
            return false;
        }
        return true;
    }

    private void driverCleanup() {
        if (!driverExtracted) return;
        FileOperations fO = new FileOperations();
        log.level4Debug("driverCleanup() Emptying folder: " + pathToCADI + "xp" + Statics.Slash);
        fO.deleteStringArrayOfFiles(fO.listFolderFilesCannonically(pathToCADI + "xp" + Statics.Slash));
        log.level4Debug("driverCleanup() Emptying folder: " + pathToCADI);
        fO.deleteStringArrayOfFiles(fO.listFolderFilesCannonically(pathToCADI));
        log.level4Debug("driverCleanup() Removing folder: " + pathToCADI + "xp");
        fO.deleteFile(pathToCADI + "xp");
        log.level4Debug("driverCleanup() Removing folder: " + pathToCADI);
        fO.deleteFile(pathToCADI);
        log.level4Debug("driverCleanup() Removing folder: " + Statics.TempFolder + "CADI");
        fO.deleteFile(Statics.TempFolder + "CADI");
        log.level4Debug("driverCleanup() Cleanup complete");
    }

    private void installDriver(String VID) {
        if (VID.equals("")) return;
        if (!driverExtracted) driverExtracted = driverExtract();
        String[] dList = getDeviceList(VID);
        if(dList[0] == null) return;
        for (int x = 0; x < dList.length && dList[x] != null; x++) {
            for(int y = 0; pastInstalls.length > y && pastInstalls[y] != null; y++) if(pastInstalls[y].equals(dList[x])) break;
            if (Statics.OSName().contains("Windows XP")) {
                devconCommand("update " + pathToCADI + "xp" + Statics.Slash + "cadixp.inf " + "\"" + dList[x] + "\"");
            } else {
                devconCommand("update " + pathToCADI + "cadiV78.inf " + "\"" + dList[x] + "\"");
            }
        }
    }

    public void removeDriver() {
        if (!driverExtracted) driverExtracted = driverExtract();
        log.level4Debug("removeDriver() Removing driver package");
        String[] infString = getOemInfName();
        for(int x = 0; infString.length > x && infString[x] != null; x++)devconCommand("-f dp_delete " + infString[x]);
        log.level4Debug("removeDriver() Cleaning up temporary folder");
        driverCleanup();
        devconCommand("rescan");
    }

    private String[] getDeviceList(String VID) {
        if (VID.equals("")) return null;
        int x = 0;
        Pattern pattern = regexPatternHwid();
        log.level4Debug("getDeviceList() Getting device list for: \"*USB\\VID_" + VID + "*\"");
        Matcher matcher = pattern.matcher(devconCommand("find *USB\\VID_" + VID + "*"));
        String[] dList = new String[9];
        while (matcher.find()) {
            dList[x] = StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", ""));
            x++;
        }
        return dList;
    }

    private String[] getOemInfName() {
        Pattern pattern = regexPatternInf();
        String[] oemBuffer = new String[9];
        int x = 0;
        log.level4Debug("getOemInfName() Enumerating installed driver packages");
        String outputBuffer = devconCommand("dp_enum");
        Matcher matcher = pattern.matcher(outputBuffer);
        while(matcher.find()){ oemBuffer[x] = matcher.group(0);}
        return oemBuffer;
    }

    private String devconCommand(String Cmd) {
        String exec = pathToCADI + (Statics.is64bitSystem() ? "devcon_x64.exe " : "devcon_x86.exe ") + Cmd;
        log.level4Debug("devconCommand() " + shell.arrayToString(new String[]{exec}));
        String outPut;
        outPut = new Shell().liveShellCommand(new String[]{"cmd.exe", "/C", "\"" + exec + "\""}, true);
        log.level4Debug(outPut);
        return outPut;
    }

    private Pattern regexPatternHwid() {
        Pattern pattern = Pattern.compile("USB.?VID_[0-9a-fA-F]{4}&PID_[0-9a-fA-F]{4}(?=.*:)");
        return pattern;
    }

    private Pattern regexPatternInf() {
        Pattern pattern = Pattern.compile("[o|Oe|Em|M]{3}[0-9]{1,4}\\.inf(?=\\s*Provider:\\slibusbK\\s*Class:\\s*libusbK USB Devices)");
        return pattern;
    }
}