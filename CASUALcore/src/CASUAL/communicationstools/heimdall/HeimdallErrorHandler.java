/*Handles errors from heimdall. 
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

/**
 * Used for detection and reaction to errors in heimdall. 
 * @author Adam Outler adamoutler@gmail.com
 */
public class HeimdallErrorHandler {
    static final String[] errFail = {"Failed to end phone file transfer sequence!", "Failed to end modem file transfer sequence!", "Failed to confirm end of file transfer sequence!", "Failed to request dump!", "Failed to receive dump size!", "Failed to request dump part ", "Failed to receive dump part ", "Failed to send request to end dump transfer!", "Failed to receive end dump transfer verification!", "Failed to initialise file transfer!", "Failed to begin file transfer sequence!", "Failed to confirm beginning of file transfer sequence!", "Failed to send file part packet!", "Failed to request device info packet!", "Failed to initialise PIT file transfer!", "Failed to confirm transfer initialisation!", "Failed to send PIT file part information!", "Failed to confirm sending of PIT file part information!", "Failed to send file part packet!", "Failed to receive PIT file part response!", "Failed to send end PIT file transfer packet!", "Failed to confirm end of PIT file transfer!", "Failed to request receival of PIT file!", "Failed to receive PIT file size!", "Failed to request PIT file part ", "Failed to receive PIT file part ", "Failed to send request to end PIT file transfer!", "Failed to receive end PIT file transfer verification!", "Failed to download PIT file!", "Failed to send end session packet!", "Failed to receive session end confirmation!", "Failed to send reboot device packet!", "Failed to receive reboot confirmation!", "Failed to begin session!", "Failed to send file part size packet!", "Failed to complete sending of data: ", "Failed to complete sending of data!", "Failed to unpack device's PIT file!", "Failed to retrieve device description", "Failed to retrieve config descriptor", "Failed to find correct interface configuration", "Failed to read PIT file.", "Failed to open output file ", "Failed to write PIT data to output file.", "Failed to open file ", "Failed to send total bytes device info packet!", "Failed to receive device info response!", "Expected file part index: ", "Expected file part index: ", "No partition with identifier ", "Could not identify the PIT partition within the specified PIT file.", "Unexpected file part size response!", "Unexpected device info response!", "Attempted to send file to unknown destination!", "The modem file does not have an identifier!", "Incorrect packet size received - expected size = ", "does not exist in the specified PIT.", "Partition name for ", "Failed to send data: ", "Failed to send data!", "Failed to receive file part response!", "Failed to unpack received packet.", "Unexpected handshake response!", "Failed to receive handshake response."};
    static final String[] epicFailures = {"ERROR: No partition with identifier"};
    static final String[] nonErrors={"ERROR: Failed to detect compatible download-mode device."};
    
    
    public HeimdallTools.CommandDisposition doErrorCheck(String[] command, String result){

        int heimdallError = errorCheckHeimdallOutput(result);
        if (heimdallError == 0) {
            return HeimdallTools.CommandDisposition.HALTSCRIPT;
        } else if (heimdallError == 1) {
            return HeimdallTools.CommandDisposition.NOACTIONREQUIRED;
        } else if (heimdallError == 2) {
            return HeimdallTools.CommandDisposition.RUNAGAIN;
        } else if (heimdallError == 3){
            return HeimdallTools.CommandDisposition.ELEVATIONREQUIRED;
        } else if (heimdallError == 4){
            return HeimdallTools.CommandDisposition.INSTALLDRIVERS;
        } else return HeimdallTools.CommandDisposition.NOACTIONREQUIRED;
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
     * errorCheckHeimdallOutput parses console log output of Heimdall, checking 
     * for key error strings
     *
     * @param heimdallOutput CASUAL (console) log output of a Heimdall execution
     * @return is an integer representation of a CommandDisposition        
     *      0 HALTSCRIPT
     *      1 NOACTIONREQUIRED
     *      2 RUNAGAIN
     *      3 ELEVATIONREQUIRED
     *      4 INSTALLDRIVERS
     *
     * @author Jeremy Loper jrloper@gmail.com
     */
    private int errorCheckHeimdallOutput(String heimdallOutput) {
        if (heimdallOutput.startsWith("Usage:"))  return 1;
        
        for (String code : HeimdallErrorHandler.epicFailures) if (heimdallOutput.contains(code)) return 0;
        
        for (String code : HeimdallErrorHandler.errFail) if (heimdallOutput.contains(code)) return 0;
        
        if (heimdallOutput.contains("Failed to detect compatible download-mode device")) {
            return 0;
        }
        
        if (heimdallOutput.contains(" failed!")) {
            if (heimdallOutput.contains("Claiming interface failed!")) {
                new CASUALMessageObject(null, "@interactionRestartDownloadMode").showActionRequiredDialog();
                return 2;
            }
            
            if (heimdallOutput.contains("Setting up interface failed!")) return 2;
            
            if (heimdallOutput.contains("Protocol initialisation failed!")) {
                CASUALScriptParser cLang = new CASUALScriptParser();
                cLang.executeOneShotCommand("$HALT $ECHO A random error occurred while attempting initial communications with the device.\nYou will need disconnect USB and pull your battery out to restart your device.\nDo the same for CASUAL.");
                return 2;
            }
            if (heimdallOutput.contains("upload failed!")) return 2;
        }
        if (heimdallOutput.contains("Flash aborted!")) return 2;
        
        if (heimdallOutput.contains("libusb error")) {
            int startIndex = heimdallOutput.lastIndexOf("libusb error");
            if (heimdallOutput.charAt(startIndex + 1) == ':') startIndex = +3;
            while (heimdallOutput.charAt(startIndex) != '\n') {
                if (heimdallOutput.charAt(startIndex) == '-') {
                    String retVal = examineLibusbError(heimdallOutput, startIndex);
                    if(retVal.contains("LIBUSB_ERROR_NOT_SUPPORTED") && OSTools.isWindows()) return 3;//Install driver
                    else if(retVal.contains("LIBUSB_ERROR_ACCESS") && OSTools.isLinux()) return 4;//Elevate Heimdall Command
                    else if(retVal.contains("LIBUSB_ERROR_OTHER")) return 1;//Other libUSB Error, Halt
                    else return 2;//Hit me baby, one more time
                }
                startIndex++;
            }
        }
        return 0;
    }

    /**
     * examineLibusbError parses console log output of Heimdall, checking 
     * for key error strings
     * 
     * @param heimdallOutput CASUAL (console) log output of a Heimdall execution
     * @param startIndex Integer representing a String index position of a libUSB
     *                   error number
     * @return is a String representation of the libUSB error   
     */
    private String examineLibusbError(String heimdallOutput, int startIndex) {
        switch (heimdallOutput.charAt(startIndex + 1)) {
            case '1':
                switch (heimdallOutput.charAt(startIndex + 2)) {
                    case '0': return "LIBUSB_ERROR_INTERRUPTED";// -10
                    case '1': return "LIBUSB_ERROR_NO_MEM";// -11
                    case '2': return "LIBUSB_ERROR_NOT_SUPPORTED";// -12
                    default:  return "LIBUSB_ERROR_IO";// -1
                }
            case '2': return "LIBUSB_ERROR_INVALID_PARAM";// -2
            case '3': return "LIBUSB_ERROR_ACCESS";// -3
            case '4': return "LIBUSB_ERROR_NO_DEVICE";// -4
            case '5': return "LIBUSB_ERROR_NOT_FOUND";// -5
            case '6': return "LIBUSB_ERROR_BUSY";// -6
            case '7': return "LIBUSB_ERROR_TIMEOUT";// -7
            case '8': return "LIBUSB_ERROR_OVERFLOW";// -8
            case '9': if (heimdallOutput.charAt(startIndex + 2) == 9){
                          return "LIBUSB_ERROR_OTHER";
                      }// -99
                      else return "LIBUSB_ERROR_PIPE";//-9
            default:  return "LIBUSB_ERROR_OTHER";//??
        }
    }

    String displayArray(String[] command) {
        StringBuilder sb = new StringBuilder();
        for (String cmd : command) {
            sb.append("\"").append(cmd).append("\" ");
        }
        return sb.toString();
    }
    
}
