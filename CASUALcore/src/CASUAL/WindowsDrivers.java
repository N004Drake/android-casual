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

    private static Log log = new Log();
    private static Shell shell = new Shell();
    private static boolean Extracted;
    public static boolean driverRemoveOnDone;

    //converted to method final private String regexPatternHwid = "^USB\\VID_[0-9a-fA-F]{4}&PID_[0-9a-fA-F]{4}.*(?=:)";
    //converted to method final private static String regexPatternInf = "[o|Oe|Em|M]{3}[0-9]*\\.inf(?=.?[Provider:\\slibusbK]?.?[Class:\\sCASUAL's\\sUSB\\sDevices]?)";
    public WindowsDrivers() {
        log.level4Debug("WindowsDrivers() Initialized");
        driverRemoveOnDone = new CASUALInteraction("CADI",
                "CASUAL will now install a generic USB driver."
                + "\n\nThis will allow communications between CASUAL and your device"
                + "\n\nWould you like CASUAL to remove this generic driver when the"
                + " operation has completed?").showYesNoOption();
    }

    /**
     * *************************************************************************
     *
     **************************************************************************
     */
    private static boolean driverExtract() {
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

    /**
     * *************************************************************************
     *
     **************************************************************************
     */
    private static void driverCleanup() {
        FileOperations fO = new FileOperations();
        log.level4Debug("driverCleanup() Emptying folder: " + Statics.TempFolder + "CADI" + Statics.Slash + "xp" + Statics.Slash);
        fO.deleteStringArrayOfFiles(fO.listFolderFilesCannonically(Statics.TempFolder + "CADI" + Statics.Slash + "xp" + Statics.Slash));
        log.level4Debug("driverCleanup() Emptying folder: " + Statics.TempFolder + "CADI" + Statics.Slash);
        fO.deleteStringArrayOfFiles(fO.listFolderFilesCannonically(Statics.TempFolder + "CADI" + Statics.Slash));
        log.level4Debug("driverCleanup() Removing folder: " + Statics.TempFolder + "CADI" + Statics.Slash + "xp");
        fO.deleteFile(Statics.TempFolder + "CADI" + Statics.Slash + "xp");
        log.level4Debug("driverCleanup() Removing folder: " + Statics.TempFolder + "CADI");
        fO.deleteFile(Statics.TempFolder + "CADI");
        log.level4Debug("driverCleanup() Cleanup complete");
    }

    /**
     * *************************************************************************
     *
     **************************************************************************
     */
    public void installDriver(String VID) {
        if (!Extracted) {
            Extracted = driverExtract();
        }
        String[] dList = getDeviceList(VID);
        for (int x = 0; x < dList.length; x++) {
            if (Statics.OSName().contains("Windows XP")) {
                devconCommand("updateni", Statics.TempFolder + "CADI" + Statics.Slash + "xp" + Statics.Slash + "cadixp.inf", dList[x]);
            } else {
                devconCommand("updateni", Statics.TempFolder + "CADI" + Statics.Slash + "cadiV78.inf", dList[x]);
            }
        }
    }

    /**
     * *************************************************************************
     *
     **************************************************************************
     */
    public void installDriver(String VID, String PID) {
        if (!Extracted) {
            Extracted = driverExtract();
        }
        log.level4Debug("removeDriver() Removing installed devices");
        String[] dList = getDeviceList(VID, PID);
        for (int x = 0; x < dList.length; x++) {
            if (Statics.OSName().contains("Windows XP")) {
                devconCommand("updateni", Statics.TempFolder + "CADI" + Statics.Slash + "xp" + Statics.Slash + "cadixp.inf", dList[x]);
            } else {
                devconCommand("updateni", Statics.TempFolder + "CADI" + Statics.Slash + "cadiV78.inf", dList[x]);
            }
        }
    }

    /**
     * *************************************************************************
     *
     **************************************************************************
     */
    public void removeDriver() {
        if (!Extracted) {
            Extracted = driverExtract();
        }
        log.level4Debug("removeDriver() Removing installed devices");
        devconCommand("remove", "\"CASUAL's USB Devices\"", "*");
        log.level4Debug("removeDriver() Removing driver package");
        devconCommand("dp_delete", getOemInfName(), "");
        log.level4Debug("removeDriver() Cleaning up temporary folder");
        driverCleanup();
    }

    /**
     * *************************************************************************
     *
     **************************************************************************
     */
    private String[] getDeviceList(String VID) {
        String[] dList = null;
        Pattern pattern = regexPatternHwid();
        log.level4Debug("getDeviceList() Getting device list for: \"*USB\\VID_" + VID + "&PID_" + "*\"");
        Matcher matcher = pattern.matcher(devconCommand("find", "*USB\\VID_" + VID + "&PID_*", ""));
        int x = 0;
        while (matcher.find()) {
            dList[x] = matcher.group(x);
            x++;
        }
        return dList;
    }

    /**
     * *************************************************************************
     *
     **************************************************************************
     */
    private String[] getDeviceList(String VID, String PID) {
        String[] dList = null;
        Pattern pattern = regexPatternHwid();
        log.level4Debug("getDeviceList() Getting device list for: \"*USB\\VID_" + VID + "&PID_" + PID + "*\"");
        String result = devconCommand("find", "*USB\\VID_" + VID + "&PID_" + PID + "*", "");
        Matcher matcher = pattern.matcher(result);
        int x = 0;
        while (matcher.find()) {
            dList[x] = matcher.group(x);
            x++;
        }
        return dList;
    }

    /**
     * *************************************************************************
     *
     **************************************************************************
     */
    private String getOemInfName() {
        //unused String[] infKeywords = {"Provider: libusbK", "Class: CASUAL's USB Devices"};
        Pattern pattern = regexPatternInf();
        log.level4Debug("getOemInfName() Enumerating installed driver packages");
        Matcher matcher = pattern.matcher(devconCommand("dp_enum", "", ""));
        log.level4Debug("getOemInfName() " + matcher.group(0) + "located");
        return matcher.group(0);
    }

    /**
     * *************************************************************************
     *
     **************************************************************************
     */
    private String devconCommand(String Cmd, String Arg1, String Arg2) {
        String[] exec = {Statics.TempFolder + "CADI" + Statics.Slash + (Statics.is64bitSystem() ? "devcon_x64.exe" : "devcon_x86.exe"), Cmd, Arg1, Arg2};
        log.level4Debug("devconCommand() " + shell.arrayToString(exec));
        return shell.silentShellCommand(exec);
    }

    private Pattern regexPatternHwid() {
        Pattern pattern = Pattern.compile("^USB\\VID_[0-9a-fA-F]{4}&PID_[0-9a-fA-F]{4}.*(?=:)");
        return pattern;
    }

    private Pattern regexPatternInf() {
        Pattern pattern = Pattern.compile("[o|Oe|Em|M]{3}[0-9]*\\.inf(?=.?[Provider:\\slibusbK]?.?[Class:\\sCASUAL's\\sUSB\\sDevices]?)");
        return pattern;
    }
}