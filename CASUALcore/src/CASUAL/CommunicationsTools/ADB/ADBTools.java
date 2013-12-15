/*ADBInstall deploys ADB for CASUAL 
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
package CASUAL.CommunicationsTools.ADB;

import CASUAL.CASUALConnectionStatusMonitor;
import CASUAL.CASUALMessageObject;
import CASUAL.CASUALTools;
import CASUAL.FileOperations;
import CASUAL.Locks;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.Shell;
import CASUAL.Statics;
import CASUAL.misc.DiffTextFiles;
import java.awt.HeadlessException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a set of tools for using ADB in CASUAL
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class ADBTools extends CASUAL.CommunicationsTools.AbstractDeviceCommunicationsProtocol {

    /**
     * path to ADB after deployment.
     */
    private static String binaryLocation; //location of ADB after deployment

    public ADBTools(){
    
}
    // The following variables represent locations of ADB files
    private static final String[] linux64Location = new String[]{"/CASUAL/CommunicationsTools/ADB/resources/adb-linux64"};
    private static final String[] linux32Location = new String[]{"/CASUAL/CommunicationsTools/ADB/resources/adb-linux32"};
    private static final String[] windowsLocation = new String[]{"/CASUAL/CommunicationsTools/ADB/resources/adb.exe", "/CASUAL/CommunicationsTools/ADB/resources/AdbWinApi.dll", "/CASUAL/CommunicationsTools/ADB/resources/AdbWinUsbApi.dll"};
    private static final String[] macLocation = new String[]{"/CASUAL/CommunicationsTools/ADB/resources/adb-mac"};
    private static final String[] linuxArmv6Location = new String[]{"/CASUAL/CommunicationsTools/ADB/resources/adb-linuxARMv6"};
    private static final String adbIniResource = "/CASUAL/CommunicationsTools/ADB/resources/adb_usb.ini";


    private String getAdbIniLocation(){
        return System.getProperty("user.home") + Statics.Slash + ".android" + Statics.Slash + "adb_usb.ini";
    }
    /**
     * returns the Instance of Linux's ADB binary
     *
     * @return gets the proper name of the ADB binary as a resource.
     */
    private String[] getLinuxADBResource() {
        String arch = OSTools.checkLinuxArch();
        if (arch.equals("x86_64")) {
            return linux64Location;
        }
        if (arch.equals("ARMv6")) {
            return linuxArmv6Location;
        }
        return linux32Location;
    }

    Log log = new Log();
    /**
     *{@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public int numberOfDevicesConnected() {
        String[] devices= getIndividualDevices();
        int connected=0;
        for (String device:devices){
            if (device.trim().endsWith("device")||device.trim().endsWith("recovery")){
                connected++;
            }
        }
        return connected;
    }

        /**
     *{@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String getBinaryLocation() {
        if (binaryLocation == null || !new File(binaryLocation).exists()) {
            deployBinary(Statics.getTempFolder());
        }
        return binaryLocation;
    }

    /**
     * kills and restarts the adb server max duration of 7 seconds. Thread will
     * be abandoned if time is exceeded
     * 
     *@inheritDoc
     */
    @Override
    public void restartConnection() {
        new Log().level3Verbose("@restartingADBSlowly");
        Shell shell = new Shell();
        shell.timeoutShellCommand(getKillServerCmd(), 1000);
        String retval = shell.timeoutShellCommand(getDevicesCmd(), 6000);
        new ADBTools().checkErrorMessage(getDevicesCmd(), retval);
    }

        /**
     *{@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean checkErrorMessage(String[] CommandRun, String returnValue) throws HeadlessException {

        /**
         * This error was received on Linux when permissions elevation was
         * required. daemon not running. starting it now on port 5037 * cannot
         * bind 'local:5037' ADB server didn't ACK failed to start daemon *
         * error: cannot connect to daemon
         */
        if (OSTools.isLinux() && returnValue.contains("ERROR-3")) { //Don't know how to handle this yet
            adbMonitor(false);
            Shell shell = new Shell();
            new Log().level0Error("@permissionsElevationRequired");
            shell.silentShellCommand(getKillServerCmd());
            shell.elevateSimpleCommandWithMessage(getDevicesCmd(), "Device permissions problem detected");
            adbMonitor(true);
            return false;
        }

        if (returnValue.contains("ELFCLASS64") && returnValue.contains("wrong ELF")) {
            adbMonitor(false);
            new CASUALMessageObject("@interactionELFCLASS64Error").showInformationMessage();
            adbMonitor(true);
            return false;
        }

        if (returnValue.contains("List of devices attached ")) {
            if (returnValue.contains("unauthorized") || returnValue.contains("Please check the confirmation dialog on your device.")) {
                adbMonitor(false);
                new CASUALMessageObject("@interactionPairingRequired").showActionRequiredDialog();
                adbMonitor(true);
                return false;
            }

            if (returnValue.contains("offline")) {
                adbMonitor(false);
                String[] ok = {"All set and done!"};
                new CASUALMessageObject("@interactionOfflineNotification").showTimeoutDialog(120, null, javax.swing.JOptionPane.OK_OPTION, 2, ok, 0);
                log.level0Error("@disconnectAndReconnect");
                adbMonitor(true);
                return false;
            }
            if (returnValue.contains("????????????") || returnValue.contains("**************") || returnValue.contains("error: cannot connect to daemon")) {
                log.level0Error("@unrecognizedDeviceDetected");
                adbMonitor(false);
                new Log().level4Debug("Restarting ADB slowly");
                restartConnection();
                returnValue = new Shell().silentShellCommand(getDevicesCmd()).replace("List of devices attached \n", "").replace("\n", "").replace("\t", "");
                if (!OSTools.isWindows() && returnValue.contains("????????????") || returnValue.contains("**************") || returnValue.contains("error: cannot connect to daemon")) {
                    String[] ok = {"ok"};
                    Statics.GUI.notificationPermissionsRequired();
                    new CASUALMessageObject("@interactionInsufficientPermissionsWorkaround").showTimeoutDialog(60, null, javax.swing.JOptionPane.OK_OPTION, 2, ok, 0);
                    killADBserver();
                    elevateADBserver();
                }
                adbMonitor(true);
            }
        }
        return true;
    }

        /**
     *{@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return numberOfDevicesConnected() == 1;
    }
    /**
     *{@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public void reset() {
        this.killADBserver();
        binaryLocation = null;
    }
    /**
     *{@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean installDriver() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *{@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String deployBinary(String TempFolder) {
        //TODO check that deployBinary is deploying proper working Linux64 binary. 
        FileOperations fo = new FileOperations();
        if (binaryLocation != null && new File(binaryLocation).exists()) {
            return binaryLocation;
        } else {
            binaryLocation = Statics.getTempFolder() + "adb";
            String[] resourceLocation;
            if (OSTools.isLinux()) {
                new Log().level4Debug("Found Linux Computer for ADB deployment");
                resourceLocation=this.getLinuxADBResource();
            } else if (OSTools.isMac()) {
                new Log().level4Debug("Found Mac Computer for ADB deployment");
                resourceLocation=macLocation;
            } else if (OSTools.isWindows()) {
                new Log().level4Debug("Found Windows Computer for ADB deployment");
                resourceLocation=windowsLocation;
                fo.copyFromResourceToFile(Statics.WinPermissionElevatorResource, Statics.WinElevatorInTempFolder);
                binaryLocation = binaryLocation+"exe";
                
            } else {
                new CASUALMessageObject("@interactionsystemNotNativelySupported").showInformationMessage();
                resourceLocation=new String[]{};
                binaryLocation = "adb";
            }
            for (String res:resourceLocation){
                fo.copyFromResourceToFile(res, binaryLocation);    
            }
        }
        updateADBini();
        fo.setExecutableBit(binaryLocation);
        String DeviceList;
        DeviceList = new ADBTools().getDevices();
        new ADBTools().checkErrorMessage(getDevicesCmd(), DeviceList);
        return binaryLocation;
    }

    private void updateADBini() {
        FileOperations fo = new FileOperations();
        if (!fo.verifyExists(getAdbIniLocation())) {
            new FileOperations().copyFromResourceToFile(adbIniResource, getAdbIniLocation());
        } else {
            DiffTextFiles DTF = new DiffTextFiles();
            DTF.appendDiffToFile(getAdbIniLocation(), DTF.diffResourceVersusFile(adbIniResource, getAdbIniLocation()));
        }
    }

    /**
     * Turns on or off the adbMonitor.
     *
     * @see CASUAL.CASUALConnectionStatusMonitor
     * @param start true if monitor is to be started.
     */
    public static void adbMonitor(boolean start) {
        if (start) {
            while (Statics.CASPAC == null) {
                CASUALTools.sleepForOneTenthOfASecond();
            }
            try {
                Locks.caspacPrepLock.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(ADBTools.class.getName()).log(Level.SEVERE, null, ex);
            }
            while (Locks.caspacScriptPrepLock) {
                CASUALTools.sleepForOneTenthOfASecond();
            }
            Statics.CASPAC.waitForUnzipComplete();

            new CASUALConnectionStatusMonitor().start(new CASUAL.CommunicationsTools.ADB.ADBTools());
        } else {
            CASUALConnectionStatusMonitor.reset();
        }

    }

    /**
     * executes the getDevices command
     *
     * @return individual devices listed as strings
     */
    public String[] getIndividualDevices() {
        Shell shell = new Shell();
        String devReturn = shell.silentTimeoutShellCommand(getDevicesCmd(), 5000);
        checkErrorMessage(getDevicesCmd(), devReturn);
        if (devReturn.equals("List of devices attached \n\n")) {
            return new String[]{};
        } else {
            //TODO evaluate this and check that line ends with recovery or devices.
            String[] retval;
            try {
                retval = devReturn.split("List of devices attached ")[1].trim().split("\n");
            } catch (ArrayIndexOutOfBoundsException ex) {
                retval = new String[]{};
            }
            return retval;
        }

    }

    /**
     * method to get the wait-for-device command for ADB use
     *
     * @return path_to_adb, wait-for-device
     */
    private String[] getWaitForDeviceCmd() {
        return new String[]{getBinaryLocation(), "wait-for-device"};
    }

    /**
     * method to get the devices command for ADB use
     *
     * @return path_to_adb, devices
     */
    private String[] getDevicesCmd() {
        return new String[]{getBinaryLocation(), "devices"};
    }

    /**
     * value to start the server
     *
     * @return value from adb
     */
    private String[] getStartServerCmd() {
        return new String[]{getBinaryLocation(), "start-server"};
    }

    /**
     * return the value to kill the ADB server
     *
     * @return value from ADB command
     */
    private String[] getKillServerCmd() {
        return new String[]{getBinaryLocation(), "kill-server"};
    }

    /**
     * executes the getDevices command
     *
     * @return value from adb getDevices
     */
    public String getDevices() {
        Shell shell = new Shell();
        String devReturn = shell.silentTimeoutShellCommand(getDevicesCmd(), 5000);
        //TODO implement error checking here and install drivers if needed EXPAND this!
        return devReturn;
    }

    /**
     * executes the start server command
     *
     * @return value from adb start server
     */
    public String startServer() {
        Shell shell = new Shell();
        String retval = shell.timeoutShellCommand(getStartServerCmd(), 5000);
        return retval;
    }

    /**
     * starts an elevated ADB server.
     */
    public void elevateADBserver() {
        new Log().level3Verbose("@restartingADB");
        Shell shell = new Shell();
        shell.silentShellCommand(getKillServerCmd());
        shell.elevateSimpleCommand(getDevicesCmd());
    }

    /**
     * Halts the ADB server.
     */
    public void killADBserver() {
        Shell shell = new Shell();
        shell.silentShellCommand(getKillServerCmd());
    }

}
