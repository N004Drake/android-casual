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
package CASUAL;

import java.awt.HeadlessException;

/**
 *
 * @author adam
 */
public class ADBTools {

    Log log = new Log();

    /**
     * method to get the wait-for-device command for ADB use
     *
     * @return path_to_adb, wait-for-device
     */
    private static String[] getWaitForDeviceCmd() {
        return new String[]{getADBCommand(), "wait-for-device"};
    }

    /**
     * method to get the devices command for ADB use
     *
     * @return path_to_adb, devices
     */
    private static String[] getDevicesCmd() {
        return new String[]{getADBCommand(), "devices"};
    }

    /**
     * value to start the server
     *
     * @return value from adb
     */
    private static String[] getStartServerCmd() {
        return new String[]{getADBCommand(), "start-server"};
    }

    /**
     * return the value to kill the ADB server
     *
     * @return value from ADB command
     */
    private static String[] getKillServerCmd() {
        return new String[]{getADBCommand(), "kill-server"};
    }

    /**
     * returns the location of ADB
     *
     * @return
     */
    public static String getADBCommand() {
        FileOperations fo = new FileOperations();
        if (!fo.verifyExists(Statics.adbDeployed)) {
            new ADBInstall().deployADB();
        }
        return Statics.adbDeployed;
    }

    /**
     * executes the adb wait-for-device commmand. will not do anything until
     * device is detected.
     *
     * @return value from adb wait-for-device
     */
    public static String waitForDevice() {
        Shell shell = new Shell();
        String retval = shell.silentShellCommand(getWaitForDeviceCmd());
        return retval;
    }

    /**
     * executes the getDevices command
     *
     * @return value from adb getDevices
     */
    public static String getDevices() {
        Shell shell = new Shell();
        String retval = shell.silentTimeoutShellCommand(getDevicesCmd(), 5000);
        return retval;
    }

    /**
     * executes the start server command
     *
     * @return value from adb start server
     */
    public static String startServer() {
        Shell shell = new Shell();
        String retval = shell.timeoutShellCommand(getStartServerCmd(), 5000);
        return retval;
    }

    /**
     * kills and restarts the adb server max duration of 7 seconds. Thread will
     * be abandoned if time is exceeded
     */
    public static void restartADBserver() {
        new Log().level3Verbose("@restartingADBSlowly");
        Shell shell = new Shell();
        shell.timeoutShellCommand(getKillServerCmd(), 1000);
        String retval=shell.timeoutShellCommand(getDevicesCmd(), 6000);
        new ADBTools().checkADBerrorMessages(retval);
    }

    /**
     * starts an elevated ADB server
     */
    public static void elevateADBserver() {
        new Log().level3Verbose("@restartingADB");
        Shell shell = new Shell();
        shell.silentShellCommand(getKillServerCmd());
        shell.elevateSimpleCommand(getDevicesCmd());
    }

    /**
     *
     */
    public static void killADBserver() {
        Shell shell = new Shell();
        shell.silentShellCommand(getKillServerCmd());
    }

    /**
     *
     * @param DeviceList
     * @throws HeadlessException
     * @return false if error
     */
    public boolean checkADBerrorMessages(String DeviceList) throws HeadlessException {

        //handle libusb -3
        if (OSTools.isLinux() && DeviceList.contains("ERROR-3")) { //Don't know how to handle this yet
            adbMonitor(false);
            Shell shell = new Shell();
            log.level0Error("@permissionsElevationRequired");
            shell.silentShellCommand(getKillServerCmd());
            shell.elevateSimpleCommandWithMessage(getDevicesCmd(), "Device permissions problem detected");
            adbMonitor(true);
            return false;
        }

        if (DeviceList.contains("ELFCLASS64") && DeviceList.contains("wrong ELF")) {
            adbMonitor(false);
            new CASUALMessageObject("@interactionELFCLASS64Error").showInformationMessage();
            adbMonitor(true);
            return false;
        }

        if ( DeviceList.contains("unauthorized") || DeviceList.contains("Please check the confirmation dialog on your device." ) ){
            adbMonitor(false);
            new CASUALMessageObject("@interactionPairingRequired").showActionRequiredDialog();
            adbMonitor(true);
            return false;
        }

        if (DeviceList.contains("offline")){
            adbMonitor(false);
            new CASUALMessageObject("@interactionOfflineNotification").showActionRequiredDialog();
            adbMonitor(true);
            return false;
        }
        if (DeviceList.contains("????????????") || DeviceList.contains("**************") || DeviceList.contains("error: cannot connect to daemon")) {
            adbMonitor(false);
            log.level4Debug("Restarting ADB slowly");
            restartADBserver();
            DeviceList = new Shell().silentShellCommand(getDevicesCmd()).replace("List of devices attached \n", "").replace("\n", "").replace("\t", "");
            if (!OSTools.isWindows() && DeviceList.contains("????????????") || DeviceList.contains("**************") || DeviceList.contains("error: cannot connect to daemon")) {
                log.level4Debug("Permissions problem detected. Requesting CASUAL permissions escillation.");
                killADBserver();
                elevateADBserver();
            }
            adbMonitor(true);
        }
        return true;
    }
    
    public static boolean isConnected(){
        return new Shell().timeoutShellCommand(new String[]{ADBTools.getADBCommand(),"devices"},4000).contains("   device");
    }
    public void adbMonitor(boolean start){
        if (start){
            CASUALConnectionStatusMonitor.DeviceCheck.start();
        } else {
            CASUALConnectionStatusMonitor.DeviceCheck.stop();    
        }
        
    }
}
