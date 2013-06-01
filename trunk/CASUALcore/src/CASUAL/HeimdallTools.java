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

    static int cyclicErrors=0;
    Log log = new Log();

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
                new CASUALInteraction().showTimeoutDialog(60, null, "It would appear that the connected device is not recognized.\n"
                        + "The device should be in download mode.. Is it?.\n\n"
                        + "If it's download mode, use a different USB port.\n"
                        + "Don't use a USB hub.  Also, the USB ports behind\n"
                        + "the computer are better than the front.\n",
                        "I don't see the device", CASUALInteraction.OK_OPTION, 2, new String[]{"I did it"}, 0);
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
        log.level3Verbose("detected!");
    }

    public String doElevatedHeimdallShellCommand(String line) {
        line = StringOperations.removeLeadingSpaces(line);
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList<>();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.addAll(new ShellTools().parseCommandLine(line));
        log.level3Verbose("Performing elevated Heimdall command" + line);
        String stringCommand2[] = StringOperations.convertArrayListToStringArray(shellCommand);
        String returnval = Shell.elevateSimpleCommandWithMessage(stringCommand2, "CASUAL uses root to work around Heimdall permissions.  Hit cancel if you have setup your UDEV rules.");
        String result = new HeimdallTools().didHeimdallError(returnval);
        if (!result.equals("")) {
            if(result.contains("Script halted")) {
                log.level0Error("[Heimdall Error Report] Detected:\n" + result + "\n[/Heimdall Error Report]\n\n");
                CASUALScriptParser cLang = new CASUALScriptParser();
                cLang.executeOneShotCommand("$HALT $SENDLOG");
                return returnval;
            } else if (result.contains("Attempting to continue")) {
                result = result.replace("Attempting to continue", "Script Halted");
                log.level0Error("[Heimdall Error Report] Detected:\n" + result + "\n[/Heimdall Error Report]\n\n");
                CASUALScriptParser cLang = new CASUALScriptParser();
                cLang.executeOneShotCommand("$HALT $SENDLOG");
                return returnval;
            }
        } else if (result.equals("")) {
            log.level2Information("\n[Heimdall Success]\n\n");
        }
        return returnval;
    }

    public String doHeimdallShellCommand(String line) {
        line = StringOperations.removeLeadingSpaces(line);
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList<>();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.addAll(new ShellTools().parseCommandLine(line));
        String stringCommand2[] = StringOperations.convertArrayListToStringArray(shellCommand);
        log.level3Verbose("Performing standard Heimdall command" + line);
        String returnRead = Shell.liveShellCommand(stringCommand2, true);
        String result = new HeimdallTools().didHeimdallError(returnRead);
        if (!result.equals("")) {
            if(result.contains("Script halted")) {
                log.level0Error("\n[Heimdall Error Report] Detected:\n" + result + "\n[/Heimdall Error Report]\n\n");
                CASUALScriptParser cLang = new CASUALScriptParser();
                cLang.executeOneShotCommand("$HALT $SENDLOG");
                return returnRead;
            }
            else if (result.contains("; Stopping")){
                return returnRead;
            }
            log.level2Information("\n[Heimdall Error Report] Detected:\n" + result + "\n[/Heimdall Error Report]\n\n"); //not an error, generally requires permissions
        } else if (result.contains("")) {
            log.level2Information("\n[Heimdall Success]\n\n");
        }
        if (result.contains("Attempting to continue")) {
            cyclicErrors++;
            if(Statics.isLinux() || Statics.isMac()) {
                log.level2Information("A permissions problem was detected.  Elevating permissions.");
                this.doElevatedHeimdallShellCommand(line);
            } else if (Statics.isWindows()) {
                if (cyclicErrors<5){
                    this.doHeimdallShellCommand(line);
                } else {
                    log.level0Error("Maximum retries exceeded. Shutting down CASUAL Parser.");
                    //TODO: uninstall drivers, reinstall with CADI and try once more.
                    new CASUALScriptParser().executeOneShotCommand("$HALT $ECHO cyclic error.");
                }
            }
            cyclicErrors=0;
        }
        return returnRead;
    }
    
     /**
     * .
     * 
     * @param String CASUAL log output
     * @param String previously executed Heimdall command array
     * 
     * @author  Jeremy Loper    jrloper@gmail.com
     */
    public String didHeimdallError(String stdErrLog) {

        for(int x = 0; x != 60; x++) {
            if(stdErrLog.contains(errFail[x])) {
                return "Heimdall uncontinuable error; Script halted"; 
            }
        }
        
        for(int x = 0; x != 3; x++) {
            if(stdErrLog.contains(errNotFail[x])) { 
                return "Heimdall continuable error; Attempting to continue"; } 
        }
        
        if(stdErrLog.contains("Failed to detect compatible download-mode device")) {
            if(new CASUALInteraction().showUserCancelOption("Heimdall is unable to detect your phone in Odin/Download Mode\n" 
                                                            + "Recheck your cable connections, click Continue when ready") == 0) {
                return "Heimdall uncontinuable error; Script halted";
            }
            return "Heimdall continuable error; Attempting to continue";
        }
        
        if(stdErrLog.contains(" failed!")) {
            if(stdErrLog.contains("Claiming interface failed!")) {
                return "Heimdall failed to claim interface; Script halted"; 
            }
            
            if(stdErrLog.contains("Setting up interface failed!")) {
                return "Heimdall failed to setup an interface; Script halted"; 
            }
            
            if(stdErrLog.contains("Protocol initialisation failed!")) {
                CASUALScriptParser cLang = new CASUALScriptParser();
                cLang.executeOneShotCommand("$HALT $ECHO A random error occurred while attempting initial communications with the device.\nYou will need disconnect USB and pull your battery out to restart your device.\nDo the same for CASUAL.");
                return "Heimdall failed to initialize protocol; Stopping"; 
            }
            
            if(stdErrLog.contains("upload failed!")) {
                return "Heimdall failed to upload; Script halted"; 
            }
        }
        
        if(stdErrLog.contains("Flash aborted!")) {
            return "Heimdall aborted flash; Script halted"; 
        }
        
        if (stdErrLog.contains("libusb error")) {
            int startIndex = stdErrLog.lastIndexOf("libusb error");
            if(stdErrLog.charAt(startIndex + 1) == ':') {
                startIndex =+ 3;
            }
            while(stdErrLog.charAt(startIndex) != '\n'){
                if(stdErrLog.charAt(startIndex) == '-') {
                    switch(stdErrLog.charAt(startIndex + 1)){
                        case '1': {
                            switch(stdErrLog.charAt(startIndex + 2)) {
                                case '0': {// -10
                                    return "'LIBUSB_ERROR_INTERRUPTED' Error not handled; Script halted";
                                }
                                case '1': {// -11
                                    return "'LIBUSB_ERROR_NO_MEM' Error not handled; Script halted";
                                }
                                case '2': {// -12 
                                    if (Statics.isWindows()) {
                                        new HeimdallInstall().installWindowsDrivers();
                                    }
                                    return "'LIBUSB_ERROR_NOT_SUPPORTED'; Attempting to continue";
                                }
                                default: {// -1
                                    return "'LIBUSB_ERROR_IO' Error not Handled; Script halted";
                                }
                            }
                        }
                        case '2': {// -2
                            return "'LIBUSB_ERROR_INVALID_PARAM' Error not handled; Script halted";
                        }
                        case '3': {// -3
                            return "'LIBUSB_ERROR_ACCESS' Error not handled; Attempting to continue";
                        }
                        case '4': {// -4
                            return "'LIBUSB_ERROR_NO_DEVICE' Error not handled; Script halted";
                        }
                        case '5': {// -5
                            return "'LIBUSB_ERROR_NOT_FOUND' Error not handled; Script halted";
                        }
                        case '6': {// -6
                            return "'LIBUSB_ERROR_BUSY' Error not handled; Script halted";
                        }
                        case '7': {// -7
                            return "'LIBUSB_ERROR_TIMEOUT'; Attempting to continue";
                        }
                        case '8': {// -8
                            return "'LIBUSB_ERROR_OVERFLOW' Error not handled; Script halted";
                        }
                        case '9': {
                            if(stdErrLog.charAt(startIndex + 2) == 9) {// -99
                                return "'LIBUSB_ERROR_OTHER' Error not handled; Script halted";
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

    public static String getHeimdallCommand() {
        if (Statics.isMac()) {
            Shell shell = new Shell();
            String check = shell.silentShellCommand(new String[]{"which", "heimdall"});
            if (check.equals("")) {
                String cmd = "/usr/bin/heimdall";
                check = shell.silentShellCommand(new String[]{cmd});
                if (check.equals("CritError!!!")) {
                    cmd = "/bin/heimdall";
                    check = shell.silentShellCommand(new String[]{cmd});
                    if (check.equals("CritError!!!")) {
                        cmd = "/usr/local/bin/heimdall";
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
            return "";
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
    
    protected static String[] errFail = {   "Failed to end phone file transfer sequence!",//FAIL
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
                                            "Failed to receive PIT file part ",//FAIL
                                            "Failed to send request to end PIT file transfer!",//FAIL
                                            "Failed to receive end PIT file transfer verification!",//FAIL
                                            "Failed to download PIT file!",//FAIL
                                            "Failed to send end session packet!",//FAIL
                                            "Failed to receive session end confirmation!",//FAIL
                                            "Failed to send reboot device packet!",//FAIL
                                            "Failed to receive reboot confirmation!",//FAIL
                                            "Failed to begin session!",//FAIL
                                            "Failed to send file part size packet!",//FAIL
                                            "Failed to send data: ",//FAIL
                                            "Failed to send data!",//FAIL
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
                                            "Partition name for "};//FAIL
    
    protected static String[] errNotFail = {    "Failed to receive file part response!",//NO FAIL
                                                "Failed to unpack received packet.",//NO FAIL
                                                "Unexpected handshake response!",//NO FAIL
                                                "Failed to receive handshake response."};//NO FAIL
}