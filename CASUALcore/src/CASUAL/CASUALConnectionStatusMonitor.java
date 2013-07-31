/*CASUALConnectionStatus provides ADB connection status monitoring for CASUAL
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
import javax.swing.Timer;

/**
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALConnectionStatusMonitor {

    /**
     * array of device serial numbers
     */
    private static String mode = "adb";
    public static String[] DeviceTracker; //used as static reference by casualConnectionStatusMonitor
    private static int LastState = 0;  //last state detected
    private static int cycles = 0; //number of cycles
    private static boolean hasConnected = false; //device was detected since startup
    public static int adbLockedUp = 0;
    final static int TIMERINTERVAL = 1000;

    CASUALConnectionStatusMonitor() {
        adbLockedUp = 0;
    }

    /**
     * Starts and stops the ADB timer reference with
     * Statics.casualConnectionStatusMonitor.DeviceCheck ONLY;
     */
    public static void setMode(String mode) {
        mode = "mode";
    }
    public static Timer DeviceCheck = new Timer(TIMERINTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (mode.equals("qprst")) {
                //TODO: implement fastboot here as well. 
            } else {  //default to adb
                Thread t = new Thread(adbDeviceCheck);
                t.setName("Device Monitor");
                t.start();
            }
        }
    });
    final static Runnable adbDeviceCheck = new Runnable() {
        Log log = new Log();

        @Override
        public void run() {

            //setup initial state
            if (Statics.GUIIsAvailable && (Statics.lockGUIformPrep || Statics.lockGUIunzip)) {
                Statics.GUI.enableControls(false);
                Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceDisconnected.png", "Device Not Detected");
                LastState = 0;
                return;
            }
            String DeviceList = getConnectedDevices();
            CASUALConnectionStatusMonitor.DeviceTracker = DeviceList.split("device");
            try {

                //Multiple devices detected
                if (CASUALConnectionStatusMonitor.DeviceTracker.length > 1 && !DeviceList.contains("offline")) {
                    stateSwitcher(CASUALConnectionStatusMonitor.DeviceTracker.length);
                    //No devices detected
                } else if (CASUALConnectionStatusMonitor.DeviceTracker[0].isEmpty()) {
                    stateSwitcher(0);
                    if (!hasConnected) {
                        messageUser();
                    }
                    //One device detected
                } else if (!CASUALConnectionStatusMonitor.DeviceTracker[0].isEmpty()) {
                    hasConnected = true;
                    //Check and handle abnormalities
                    // pairing problem with 4.2+
                    if (DeviceList.contains("offline")) {
                        DeviceCheck.stop();
                        sleepForFourSeconds(); //give the device a chance to come online
                        DeviceList = getConnectedDevices();
                        if (DeviceList.contains("offline")) {
                            String[] ok = {"All set and done!"};
                            new CASUALInteraction("@interactionOfflineNotification").showTimeoutDialog(120, null, CASUALInteraction.OK_OPTION, 2, ok, 0);
                            log.level0Error("@disconnectAndReconnect");
                            DeviceList = ADBTools.getDevices();
                            DeviceCheck.start();
                        }
                    } else {
                        if (Statics.useGUI && !Statics.GUI.getControlStatus()) {
                            Statics.GUI.enableControls(true);
                        }
                        stateSwitcher(1);
                    }
                    //insufficient permissions

                    if (DeviceList.contains("????????????") && !OSTools.isWindows()) {
                        DeviceCheck.stop();
                        log.level4Debug("@sleepingfor4Seconds");
                        sleepForFourSeconds();
                        DeviceList = getConnectedDevices();
                        CASUALConnectionStatusMonitor.DeviceTracker = DeviceList.split("device");

                        //Linux and mac only.
                        if (DeviceList.contains("????????????")) {
                            log.level2Information("@permissionsElevationRequired");
                            ADBTools.startServer(); //send the command
                            //notify user that permissions will be requested and what they are used for
                            String[] ok = {"ok"};
                            AudioHandler.playSound("/CASUAL/resources/sounds/PermissionEscillation.wav");
                            new CASUALInteraction("@interactionInsufficientPermissionsWorkaround").showTimeoutDialog(60, null, CASUALInteraction.OK_OPTION, 2, ok, 0);

                            DeviceList = ADBTools.getDevices();
                            // if permissions elevation was sucessful
                            if (!DeviceList.contains("????????????")) {
                                log.level4Debug(DeviceList);
                                stateSwitcher(1);
                                //devices still not properly recognized.  log it.
                            } else {
                                log.level0Error("@unrecognizedDeviceDetected");
                            }
                        }
                        DeviceCheck.start();
                    }

                }
            } catch (NullPointerException ex) {
            }


        }

        private void stateSwitcher(int State) {
            boolean stateSwitchWasSucessful; //try again later.
            if (LastState != State) {
                log.level4Debug("State Change Detected, The new state is: " + State);
                switch (State) {
                    case 0:
                        log.level4Debug("@stateDisconnected");
                        Statics.setStatus("Device Removed");
                        stateSwitchWasSucessful = Statics.GUI.enableControls(false);
                        Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceDisconnected.png", "Device Not Detected");
                        AudioHandler.playSound("/CASUAL/resources/sounds/Disconnected.wav");
                        break;
                    case 1:
                        Statics.setStatus("Device Connected");
                        log.level4Debug("@stateConnected");
                        stateSwitchWasSucessful = Statics.GUI.enableControls(true);
                        Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceConnected.png", "Device Connected");
                        Statics.GUI.setStatusMessageLabel("Target Acquired");
                        if (stateSwitchWasSucessful) {
                            AudioHandler.playSound("/CASUAL/resources/sounds/Connected-SystemReady.wav");
                        }
                        break;
                    default:
                        Statics.setStatus("Multiple Devices Detected");
                        if (State == 2) {
                            log.level0Error("@stateMultipleDevices");
                            log.level0Error("Remove " + (State - 1) + " device to continue.");
                        }

                        log.level4Debug("State Multiple Devices Number of devices" + State);
                        stateSwitchWasSucessful = Statics.GUI.enableControls(false);
                        Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/TooManyDevices.png", "Target Acquired");
                        String[] URLs = {"/CASUAL/resources/sounds/" + String.valueOf(State) + ".wav", "/CASUAL/resources/sounds/DevicesDetected.wav"};
                        AudioHandler.playMultipleInputStreams(URLs);
                        break;

                }
                if (stateSwitchWasSucessful) {
                    LastState = State;
                }
            }
        }

        private void messageUser() {
            cycles++;
            if (cycles == 30) {
                if (OSTools.isWindows()) {
                    new CASUALInteraction("@interactionWindowsDeviceNotDetected").showTimeoutDialog(60, null, CASUALInteraction.OK_OPTION, CASUALInteraction.INFORMATION_MESSAGE, new String[]{"OK"}, "OK");
                } else if (OSTools.isLinux()) {
                    new CASUALInteraction("@interactionLinuxDeviceNotDetected").showTimeoutDialog(60, null, CASUALInteraction.OK_OPTION, CASUALInteraction.INFORMATION_MESSAGE, new String[]{"OK"}, "OK");
                } else if (OSTools.isMac()) {
                    new CASUALInteraction("@interactionMacDeviceNotDetected").showTimeoutDialog(60, null, CASUALInteraction.OK_OPTION, CASUALInteraction.INFORMATION_MESSAGE, new String[]{"OK"}, "OK");
                }
                hasConnected = true;
            }

        }

        private String getConnectedDevices() {
            String devices = ADBTools.getDevices().replace("List of devices attached \n", "").replace("\n", "").replace("\t", "");
            if (devices.startsWith("Timeout!!! ")) {
                devices = devices.replace("Timeout!!! ", "");
                CASUALConnectionStatusMonitor.adbLockedUp++;
                if (adbLockedUp == 10) {
                    new CASUALInteraction("@interactionADBLockedUp").showErrorDialog();
                }
            } else {
                adbLockedUp = 0;
            }
            return devices;

        }

        private void sleepForFourSeconds() {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                log.errorHandler(ex);
            }
        }
    };
}
