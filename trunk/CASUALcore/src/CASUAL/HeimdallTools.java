/*HeimdallTools provides tools for use with Heimdall
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;

/**
 *
 * @author adam
 */
public class HeimdallTools {

    Log log = new Log();
    int permissionEscillationAttempt = 0;
    int heimdallRetries = 0;
    String line;

    HeimdallTools(String line) {
        this.line = line;
    }

    /**
     * do nothing until a heimdall device is detected
     */
    public void doHeimdallWaitForDevice() {
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList<>();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.add("detect");
        String stringCommand[] = (StringOperations.convertArrayListToStringArray(shellCommand));
        log.progress("Waiting for Downoad Mode device.");
        String shellReturn = "";
        Timer connectionTimer = new Timer(60000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new CASUALInteraction("@interactionDownloadModeNotDetected").showTimeoutDialog(60, null, CASUALInteraction.OK_OPTION, 2, new String[]{"I did it"}, 0);
            }
        });
        connectionTimer.start();
        //Start timer  wait(90000) and recommend changing USB ports
        while (!shellReturn.contains("Device detected")) {
            log.progress(".");
            sleepForOneSecond();
            shellReturn = Shell.silentShellCommand(stringCommand);
        }
        connectionTimer.stop();
        if (Statics.isWindows()){
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
            }
        }
        log.level3Verbose("detected!");
    }

    /**
     * performs an elevated heimdall command
     * @return result from heimdall
     */
    public String doElevatedHeimdallShellCommand() {
        ++heimdallRetries;
        line = StringOperations.removeLeadingSpaces(line);
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList<>();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.addAll(new ShellTools().parseCommandLine(line));
        log.level3Verbose("Performing elevated Heimdall command" + line);
        String stringCommand2[] = StringOperations.convertArrayListToStringArray(shellCommand);
        String returnval = Shell.elevateSimpleCommandWithMessage(stringCommand2, "CASUAL uses root to work around Heimdall permissions.  Hit cancel if you have setup your UDEV rules.");
        String result = didHeimdallError(returnval);
        if (!result.equals("")) {
            if (result.contains("Script halted")) {
                log.level0Error("@heimdallEncounteredAnError");
                log.level0Error( result );
                log.level0Error("@heimdallEncounteredAnError");
                CASUALScriptParser cLang = new CASUALScriptParser();
                if (!Statics.debugMode) cLang.executeOneShotCommand("$HALT $SENDLOG");
                return returnval;
            } else if (result.contains("Attempting to continue")) {
                log.level2Information("@permissionsElevationRequired");
                returnval = doElevatedHeimdallShellCommand();
                return returnval;
            }
        } else {
            log.level3Verbose("\n[Heimdall Success]\n\n");
        }
        return returnval;
    }

    /**
     * performs a heimdall command
     * @return value from heimdall command
     */
    public String doHeimdallShellCommand() {
        line = StringOperations.removeLeadingSpaces(line);
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList<>();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.addAll(new ShellTools().parseCommandLine(line));
        String stringCommand2[] = StringOperations.convertArrayListToStringArray(shellCommand);
        log.level3Verbose("Performing standard Heimdall command" + line);
        String returnRead = Shell.liveShellCommand(stringCommand2, true);
        String result = didHeimdallError(returnRead);
        if (!result.equals("")) {
            if (result.contains("Script halted")) {
                
                log.level0Error("@heimdallErrorReport");
                log.level0Error(line);
                log.level0Error("@heimdallErrorReport");
                log.level0Error( result);
                log.level0Error("@heimdallErrorReport");
                CASUALScriptParser cLang = new CASUALScriptParser();
                cLang.executeOneShotCommand("$HALT $SENDLOG");
                return returnRead;
            } else if (result.contains("; Stopping")) {
                return returnRead;
            }
            log.level2Information(result);
        } else if (result.contains("")) {
            log.level2Information("@heimdallWasSucessful");
        }
        if (result.contains("Attempting to continue")) {
            permissionEscillationAttempt++;
            if (Statics.isLinux()) {
                log.level2Information("@permissionsElevationRequired");
                returnRead = returnRead + doElevatedHeimdallShellCommand();
            } else if (Statics.isWindows() || Statics.isMac()) {
                if (permissionEscillationAttempt < 5) {
                    returnRead = returnRead + doHeimdallShellCommand();
                } else {
                    log.level0Error("@maximumRetries");
                    //TODO: uninstall drivers, reinstall with CADI and try once more.
                    new CASUALScriptParser().executeOneShotCommand("$HALT $ECHO cyclic error.");
                }
            }
            permissionEscillationAttempt = 0;
        }
        return returnRead;
    }

    /**
     * checks if Heimdall threw an error
     * @param stdErrLog CASUAL log output
     * @return containing halted if cannot continue or continue if it can
     *
     * @author Jeremy Loper jrloper@gmail.com
     */
    public String didHeimdallError(String stdErrLog) {

        for (String code : epicFailures) {
            if (stdErrLog.contains(code)) {
                return "Heimdall uncontinuable error; Script halted";
            }
        }

        //Critical Failure, stop
        for (String code : errFail) { //halt
            if (stdErrLog.contains(code)) {
                if (heimdallRetries <= 3) {  //only loop thrice
                    new CASUALInteraction("@interactionRestartDownloadMode").showActionRequiredDialog();
                    return "Heimdall continuable error; Attempting to continue";
                } else {
                    return "Heimdall uncontinuable error; Script halted";
                }
            }

        }




        if (stdErrLog.contains("Failed to detect compatible download-mode device")) {
            if (new CASUALInteraction("@interactionUnableToDetectDownloadMode").showUserCancelOption() == 0) {
                return "Heimdall uncontinuable error; Script halted";
            }
            return "Heimdall continuable error; Attempting to continue";
        }

        if (stdErrLog.contains(" failed!")) {
            if (stdErrLog.contains("Claiming interface failed!")) {
                new CASUALInteraction(null, "@interactionRestartDownloadMode").showActionRequiredDialog();
                return "Heimdall failed to claim interface; Attempting to continue";
            }

            if (stdErrLog.contains("Setting up interface failed!")) {
                return "Heimdall failed to setup an interface; Attempting to continue";
            }

            if (stdErrLog.contains("Protocol initialisation failed!")) {
                CASUALScriptParser cLang = new CASUALScriptParser();
                cLang.executeOneShotCommand("$HALT $ECHO A random error occurred while attempting initial communications with the device.\nYou will need disconnect USB and pull your battery out to restart your device.\nDo the same for CASUAL.");
                return "Heimdall failed to initialize protocol; Attempting to continue";
            }

            if (stdErrLog.contains("upload failed!")) {
                return "Heimdall failed to upload; Attempting to continue";
            }
        }

        if (stdErrLog.contains("Flash aborted!")) {
            return "Heimdall aborted flash; Attempting to continue";
        }

        if (stdErrLog.contains("libusb error")) {
            int startIndex = stdErrLog.lastIndexOf("libusb error");
            if (stdErrLog.charAt(startIndex + 1) == ':') {
                startIndex = +3;
            }
            while (stdErrLog.charAt(startIndex) != '\n') {
                if (stdErrLog.charAt(startIndex) == '-') {
                    switch (stdErrLog.charAt(startIndex + 1)) {
                        case '1': {
                            switch (stdErrLog.charAt(startIndex + 2)) {
                                case '0': {// -10
                                    return "'LIBUSB_ERROR_INTERRUPTED' Error not handled; Attempting to continue";
                                }
                                case '1': {// -11
                                    return "'LIBUSB_ERROR_NO_MEM' Error not handled; Attempting to continue";
                                }
                                case '2': {// -12 
                                    if (Statics.isWindows()) {
                                        new HeimdallInstall().installWindowsDrivers();
                                    }
                                    return "'LIBUSB_ERROR_NOT_SUPPORTED'; Attempting to continue";
                                }
                                default: {// -1
                                    return "'LIBUSB_ERROR_IO' Error not Handled; Attempting to continue";
                                }
                            }
                        }
                        case '2': {// -2
                            return "'LIBUSB_ERROR_INVALID_PARAM' Error not handled; Attempting to continue";
                        }
                        case '3': {// -3
                            return "'LIBUSB_ERROR_ACCESS' Error not handled; Attempting to continue";
                        }
                        case '4': {// -4
                            return "'LIBUSB_ERROR_NO_DEVICE' Error not handled; Attempting to continue";
                        }
                        case '5': {// -5
                            return "'LIBUSB_ERROR_NOT_FOUND' Error not handled; Attempting to continue";
                        }
                        case '6': {// -6
                            return "'LIBUSB_ERROR_BUSY' Error not handled; Attempting to continue";
                        }
                        case '7': {// -7
                            return "'LIBUSB_ERROR_TIMEOUT'; Attempting to continue";
                        }
                        case '8': {// -8
                            return "'LIBUSB_ERROR_OVERFLOW' Error not handled; Attempting to continue";
                        }
                        case '9': {
                            if (stdErrLog.charAt(startIndex + 2) == 9) {// -99
                                return "'LIBUSB_ERROR_OTHER' Error not handled; Attempting to continue";
                            } else {//-9
                                return "'LIBUSB_ERROR_PIPE'; Attempting to continue";
                            }
                        }
                        default: {
                            return "'LIBUSB_ERROR_OTHER' Error not handled; Script halted";
                        }
                    }
                }
                startIndex++;
            }
        }
        return "";
    }

    /**
     * gets the command to run heimdall
     * @return string path to heimdall
     */
    public static String getHeimdallCommand() {
        if (Statics.isMac()) {
            Shell shell = new Shell();
            String cmd = "/usr/local/bin/heimdall";
            String check = shell.silentShellCommand(new String[]{cmd});
            if (check.equals("")) {
                cmd = "/usr/bin/heimdall";
                check = shell.silentShellCommand(new String[]{cmd});
                if (check.equals("CritError!!!")) {
                    cmd = "/bin/heimdall";
                    check = shell.silentShellCommand(new String[]{cmd});
                    if (check.equals("CritError!!!")) {
                        cmd = "heimdall";
                        check = shell.silentShellCommand(new String[]{cmd});
                        if (check.equals("CritError!!!")) {
                            return "";
                        }
                        return cmd;
                    }
                    return cmd;
                }
                return cmd;
            }
            return cmd;
        } else {
            if (Statics.heimdallDeployed.equals("")) {
                return "heimdall";
            } else {
                return Statics.heimdallDeployed;
            }

        }
    }

    private void sleepForOneSecond() {
        try {
            Thread.sleep(1000);
            log.progress(".");
        } catch (InterruptedException ex) {
            log.errorHandler(ex);
        }
    }
    final static String[] errFail = {"Failed to end phone file transfer sequence!",//FAIL
        "Failed to end modem file transfer sequence!",//FAIL
        "Failed to confirm end of file transfer sequence!",//FAIL
        "Failed to request dump!",//FAIL
        "Failed to receive dump size!",//FAIL
        "Failed to request dump part ",//FAIL
        "Failed to receive dump part ",//FAIL
        "Failed to send request to end dump transfer!",//FAIL
        "Failed to receive end dump transfer verification!",//FAIL
        "Failed to initialise file transfer!",//FAIL
        "Failed to begin file transfer sequence!",//FAIL
        "Failed to confirm beginning of file transfer sequence!",//FAIL
        "Failed to send file part packet!",//FAIL
        "Failed to request device info packet!",//FAIL
        "Failed to initialise PIT file transfer!",//FAIL
        "Failed to confirm transfer initialisation!",//FAIL
        "Failed to send PIT file part information!",//FAIL
        "Failed to confirm sending of PIT file part information!",//FAIL
        "Failed to send file part packet!",//FAIL
        "Failed to receive PIT file part response!",//FAIL
        "Failed to send end PIT file transfer packet!",//FAIL
        "Failed to confirm end of PIT file transfer!",//FAIL
        "Failed to request receival of PIT file!",//FAIL
        "Failed to receive PIT file size!",//FAIL
        "Failed to request PIT file part ",//FAIL
        "Failed to receive PIT file part ",//TODO: FAIL  BAD USB CABLE
        "Failed to send request to end PIT file transfer!",//FAIL
        "Failed to receive end PIT file transfer verification!",//FAIL
        "Failed to download PIT file!",//TODO: FAIL  BAD USB CABLE
        "Failed to send end session packet!",//FAIL
        "Failed to receive session end confirmation!",//FAIL
        "Failed to send reboot device packet!",//FAIL
        "Failed to receive reboot confirmation!",//FAIL
        "Failed to begin session!",//FAIL
        "Failed to send file part size packet!",//FAIL
        "Failed to complete sending of data: ",//FAIL
        "Failed to complete sending of data!",//FAIL
        "Failed to unpack device's PIT file!",//FAIL
        "Failed to retrieve device description",//FAIL
        "Failed to retrieve config descriptor",//FAIL
        "Failed to find correct interface configuration",//FAIL
        "Failed to read PIT file.",//FAIL
        "Failed to open output file ",//FAIL
        "Failed to write PIT data to output file.",//FAIL
        "Failed to open file ",//FAIL
        "Failed to send total bytes device info packet!",//FAIL
        "Failed to receive device info response!",//FAIL
        "Expected file part index: ",//FAIL
        "Expected file part index: ",//FAIL
        "No partition with identifier ",//FAIL
        "Could not identify the PIT partition within the specified PIT file.",//FAIL
        "Unexpected file part size response!",//FAIL
        "Unexpected device info response!",//FAIL
        "Attempted to send file to unknown destination!",//FAIL
        "The modem file does not have an identifier!",//FAIL			
        "Incorrect packet size received - expected size = ",//FAIL
        "does not exist in the specified PIT.",//FAIL
        "Partition name for ",
        "Failed to send data: ",//FAIL
        "Failed to send data!",
        "Failed to receive file part response!",//NO FAIL
        "Failed to unpack received packet.",//NO FAIL
        "Unexpected handshake response!",//NO FAIL
        "Failed to receive handshake response."
    };
    final static String[] epicFailures = {"ERROR: No partition with identifier"
    };
}