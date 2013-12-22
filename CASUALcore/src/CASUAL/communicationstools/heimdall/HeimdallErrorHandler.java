/*
 * Copyright (C) 2013 adamoutler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package CASUAL.communicationstools.heimdall;

import CASUAL.CASUALMessageObject;
import CASUAL.CASUALScriptParser;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.communicationstools.heimdall.HeimdallTools.CommandDisposition;

/**
 *
 * @author adamoutler
 */
public class HeimdallErrorHandler {
    static final String[] errFail = {"Failed to end phone file transfer sequence!", "Failed to end modem file transfer sequence!", "Failed to confirm end of file transfer sequence!", "Failed to request dump!", "Failed to receive dump size!", "Failed to request dump part ", "Failed to receive dump part ", "Failed to send request to end dump transfer!", "Failed to receive end dump transfer verification!", "Failed to initialise file transfer!", "Failed to begin file transfer sequence!", "Failed to confirm beginning of file transfer sequence!", "Failed to send file part packet!", "Failed to request device info packet!", "Failed to initialise PIT file transfer!", "Failed to confirm transfer initialisation!", "Failed to send PIT file part information!", "Failed to confirm sending of PIT file part information!", "Failed to send file part packet!", "Failed to receive PIT file part response!", "Failed to send end PIT file transfer packet!", "Failed to confirm end of PIT file transfer!", "Failed to request receival of PIT file!", "Failed to receive PIT file size!", "Failed to request PIT file part ", "Failed to receive PIT file part ", "Failed to send request to end PIT file transfer!", "Failed to receive end PIT file transfer verification!", "Failed to download PIT file!", "Failed to send end session packet!", "Failed to receive session end confirmation!", "Failed to send reboot device packet!", "Failed to receive reboot confirmation!", "Failed to begin session!", "Failed to send file part size packet!", "Failed to complete sending of data: ", "Failed to complete sending of data!", "Failed to unpack device's PIT file!", "Failed to retrieve device description", "Failed to retrieve config descriptor", "Failed to find correct interface configuration", "Failed to read PIT file.", "Failed to open output file ", "Failed to write PIT data to output file.", "Failed to open file ", "Failed to send total bytes device info packet!", "Failed to receive device info response!", "Expected file part index: ", "Expected file part index: ", "No partition with identifier ", "Could not identify the PIT partition within the specified PIT file.", "Unexpected file part size response!", "Unexpected device info response!", "Attempted to send file to unknown destination!", "The modem file does not have an identifier!", "Incorrect packet size received - expected size = ", "does not exist in the specified PIT.", "Partition name for ", "Failed to send data: ", "Failed to send data!", "Failed to receive file part response!", "Failed to unpack received packet.", "Unexpected handshake response!", "Failed to receive handshake response."};
    static final String[] epicFailures = {"ERROR: No partition with identifier"};

    static final String[] nonErrors={"ERROR: Failed to detect compatible download-mode device."};
    
    
        HeimdallTools.CommandDisposition doErrorCheck(String[] command, String result){

            for (String value:nonErrors){
                if (result.startsWith(value)){
                    return CommandDisposition.NOACTIONREQUIRED;
                }
            }

            
            if (result.contains("Script halted")) {
                return HeimdallTools.CommandDisposition.HALTSCRIPT;
            } else if (result.equals("")) {
                return HeimdallTools.CommandDisposition.NOACTIONREQUIRED;
            } else if (result.contains("; Attempting to continue")) {
                return HeimdallTools.CommandDisposition.RUNAGAIN;
            } else if (result.contains("//TODO IMPLEMENT THIS")){
                return HeimdallTools.CommandDisposition.ELEVATIONREQUIRED;
                
            } else if (result.contains("//TODO IMPLEMENT THIS")){
                return HeimdallTools.CommandDisposition.INSTALLDRIVERS;
                
            }



            return HeimdallTools.CommandDisposition.NOACTIONREQUIRED;
        }
    private void doErrorReport(String[] command, String result, HeimdallTools heimdallTools) {
        Log.level0Error("@heimdallErrorReport");
        Log.level0Error(displayArray(command));
        Log.level0Error("@heimdallErrorReport");
        Log.level0Error(result);
        Log.level0Error("@heimdallErrorReport");
        CASUALScriptParser cLang = new CASUALScriptParser();
        cLang.executeOneShotCommand("$HALT $SENDLOG");
    }

    /**
     * checks if Heimdall threw an error
     *
     * @param returnValue CASUAL log output
     * @return containing halted if cannot continue or continue if it can
     *
     * @author Jeremy Loper jrloper@gmail.com
     */
    private String checkHeimdallErrorStatus(String returnValue, HeimdallTools heimdallTools) {
        if (returnValue.startsWith("Usage:")) {
            return "invalid command; Attempting to continue for test purposes";
        }
        for (String code : HeimdallErrorHandler.epicFailures) {
            if (returnValue.contains(code)) {
                return "Heimdall epic uncontinuable error; Script halted";
            }
        }
        for (String code : HeimdallErrorHandler.errFail) {
            if (returnValue.contains(code)) {
                if (heimdallTools.heimdallRetries <= 3) {
                    new CASUALMessageObject("@interactionRestartDownloadMode").showActionRequiredDialog();
                    return "Heimdall continuable error; Attempting to continue";
                } else {
                    return "Heimdall uncontinuable error; Script halted";
                }
            }
        }
        if (returnValue.contains("Failed to detect compatible download-mode device")) {
            if (new CASUALMessageObject("@interactionUnableToDetectDownloadMode").showUserCancelOption() == 0) {
                return "Heimdall uncontinuable error; Script halted";
            }
            return "Heimdall continuable error; Attempting to continue";
        }
        if (returnValue.contains(" failed!")) {
            if (returnValue.contains("Claiming interface failed!")) {
                new CASUALMessageObject(null, "@interactionRestartDownloadMode").showActionRequiredDialog();
                return "Heimdall failed to claim interface; Attempting to continue";
            }
            if (returnValue.contains("Setting up interface failed!")) {
                return "Heimdall failed to setup an interface; Attempting to continue";
            }
            if (returnValue.contains("Protocol initialisation failed!")) {
                CASUALScriptParser cLang = new CASUALScriptParser();
                cLang.executeOneShotCommand("$HALT $ECHO A random error occurred while attempting initial communications with the device.\nYou will need disconnect USB and pull your battery out to restart your device.\nDo the same for CASUAL.");
                return "Heimdall failed to initialize protocol; Attempting to continue";
            }
            if (returnValue.contains("upload failed!")) {
                return "Heimdall failed to upload; Attempting to continue";
            }
        }
        if (returnValue.contains("Flash aborted!")) {
            return "Heimdall aborted flash; Attempting to continue";
        }
        if (returnValue.contains("libusb error")) {
            int startIndex = returnValue.lastIndexOf("libusb error");
            if (returnValue.charAt(startIndex + 1) == ':') {
                startIndex = +3;
            }
            while (returnValue.charAt(startIndex) != '\n') {
                if (returnValue.charAt(startIndex) == '-') {
                    String libusbError="";
                    examineLibusbError(returnValue, startIndex);
                }
                startIndex++;
            }
        }
        return "";
    }

    private String examineLibusbError(String returnValue, int startIndex) {
        String libusbError;
        switch (returnValue.charAt(startIndex + 1)) {
            case '1':
                switch (returnValue.charAt(startIndex + 2)) {
                    case '0': // -10
                        libusbError= "'LIBUSB_ERROR_INTERRUPTED' Error not handled; Attempting to continue";
                        break;
                    case '1':// -11
                        libusbError= "'LIBUSB_ERROR_NO_MEM' Error not handled; Attempting to continue";
                        break;
                    case '2':// -12
                        if (OSTools.isWindows()) {
                            new HeimdallInstall().installWindowsDrivers();
                        }
                        libusbError= "'LIBUSB_ERROR_NOT_SUPPORTED'; Attempting to continue";
                        break;
                    default:// -1
                        libusbError= "'LIBUSB_ERROR_IO' Error not Handled; Attempting to continue";
                        break;
                }
                break;
            case '2':// -2
                libusbError= "'LIBUSB_ERROR_INVALID_PARAM' Error not handled; Attempting to continue";
                break;
            case '3':// -3
                libusbError= "'LIBUSB_ERROR_ACCESS' Error not handled; Attempting to continue";
                break;
            case '4':// -4
                libusbError= "'LIBUSB_ERROR_NO_DEVICE' Error not handled; Attempting to continue";
                break;
            case '5':// -5
                libusbError= "'LIBUSB_ERROR_NOT_FOUND' Error not handled; Attempting to continue";
                break;
            case '6':// -6
                libusbError= "'LIBUSB_ERROR_BUSY' Error not handled; Attempting to continue";
                break;
            case '7':// -7
                libusbError= "'LIBUSB_ERROR_TIMEOUT'; Attempting to continue";
                break;
            case '8':// -8
                libusbError= "'LIBUSB_ERROR_OVERFLOW' Error not handled; Attempting to continue";
                break;
            case '9':
                if (returnValue.charAt(startIndex + 2) == 9) {// -99
                    libusbError= "'LIBUSB_ERROR_OTHER' Error not handled; Attempting to continue";
                } else {//-9   //TODO jrloper examine this for correctness
                    libusbError= "'LIBUSB_ERROR_PIPE'; Attempting to continue";
                }
                break;
            default:
                libusbError= "'LIBUSB_ERROR_OTHER' Error not handled; Script halted";
        }
        return libusbError;
    }

    

    String displayArray(String[] command) {
        StringBuilder sb = new StringBuilder();
        for (String cmd : command) {
            sb.append("\"").append(cmd).append("\" ");
        }
        return sb.toString();
    }
    
}
