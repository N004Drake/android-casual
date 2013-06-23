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

    public static String[] DeviceTracker; //used as static reference by casualConnectionStatusMonitor
    Log Log = new Log();
    Shell Shell = new Shell();
    AudioHandler CAS = new AudioHandler();
    private static int LastState = 0;  //last state detected
    private static int timerInterval = 1000;
    private static int cycles = 0; //number of cycles
    private static boolean hasConnected = false; //device was detected since startup

    CASUALConnectionStatusMonitor() {
        this.r = new Runnable() {
            @Override
            public void run() {

                //setup initial state
                if ((Statics.GUIIsAvailable) && (Statics.lockGUIformPrep || Statics.lockGUIunzip)) {
                    Statics.GUI.enableControls(false);
                    Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceDisconnected.png", "Device Not Detected");
                    LastState = 0;
                    return;
                }
                try {
                    String DeviceList = getConnectedDevices();
                    CASUALConnectionStatusMonitor.DeviceTracker = DeviceList.split("device");


                    //Multiple devices detected
                    if (CASUALConnectionStatusMonitor.DeviceTracker.length > 1 && (!DeviceList.contains("offline"))) {
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
                        stateSwitcher(1);


                        //Check and handle abnormalities
                        // pairing problem with 4.2+
                        if (DeviceList.contains("offline")) {
                            Statics.casualConnectionStatusMonitor.DeviceCheck.stop();
                            sleepForFourSeconds(); //give the device a chance to come online
                            DeviceList = getConnectedDevices();
                            if (DeviceList.contains("offline")) {
                                String[] ok = {"All set and done!"};
                                new CASUALInteraction("@interactionOfflineNotification").showTimeoutDialog(120, null, CASUALInteraction.OK_OPTION, 2, ok, 0);
                                Log.level0Error("@disconnectAndReconnect");
                                DeviceList = Shell.sendShellCommand(new String[]{Statics.adbDeployed, "wait-for-device"});
                                Statics.casualConnectionStatusMonitor.DeviceCheck.start();
                            }
                        }
                        //insufficient permissions

                        if (DeviceList.contains("????????????") && (!Statics.isWindows())) {
                            Statics.casualConnectionStatusMonitor.DeviceCheck.stop();
                            Log.level4Debug("@sleepingfor4Seconds");
                            sleepForFourSeconds();
                            DeviceList = getConnectedDevices();
                            CASUALConnectionStatusMonitor.DeviceTracker = DeviceList.split("device");

                            //Linux and mac only.
                            if (DeviceList.contains("????????????")) {
                                String cmd[] = {Statics.adbDeployed, "kill-server"}; //kill the server
                                Log.level2Information("@permissionsElevationRequired");
                                Shell.sendShellCommand(cmd); //send the command
                                //notify user that permissions will be requested and what they are used for
                                String[] ok = {"ok"};
                                AudioHandler.playSound("/CASUAL/resources/sounds/PermissionEscillation.wav");
                                new CASUALInteraction("@interactionInsufficientPermissionsWorkaround").showTimeoutDialog(60, null, CASUALInteraction.OK_OPTION, 2, ok, 0);
                                String[] getDevicesCommand = new String[]{Statics.adbDeployed, "devices"};
                                DeviceList = Shell.elevateSimpleCommand(getDevicesCommand);
                                // if permissions elevation was sucessful
                                if (!DeviceList.contains("????????????")) {
                                    Log.level4Debug(DeviceList);
                                    Log.level2Information("Permissions problem corrected");
                                    stateSwitcher(1);
                                    //devices still not properly recognized.  Log it.
                                } else {
                                    Log.level0Error("@unrecognizedDeviceDetected");
                                }
                            }
                            Statics.casualConnectionStatusMonitor.DeviceCheck.start();
                        }

                    }
                } catch (NullPointerException E) {
                    //unreported because there's no reason to report there are no devices.
                }
            }

            private void sleepForFourSeconds() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException ex) {
                    Log.errorHandler(ex);
                }
            }
        };
        //GETDEVICECOMMAND = new String[]{Statics.adbDeployed, "devices"};
    }
    /*
     * Starts and stops the ADB timer
     * reference with Statics.casualConnectionStatusMonitor.DeviceCheck ONLY;
     */
    public Timer DeviceCheck = new Timer(timerInterval, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            Thread t = new Thread(r);
            t.setName("Device Monitor");
            t.start();
        }
    });
    Runnable r;

    private void stateSwitcher(int State) {
        boolean stateSwitchWasSucessful; //try again later.
        if (LastState != State) {
            Log.level4Debug("State Change Detected, The new state is: " + State);
            switch (State) {
                case 0:
                    Log.level4Debug("@stateDisconnected");
                    stateSwitchWasSucessful = Statics.GUI.enableControls(false);
                    Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceDisconnected.png", "Device Not Detected");
                    AudioHandler.playSound("/CASUAL/resources/sounds/Disconnected.wav");
                    break;
                case 1:
                    Log.level4Debug("@stateConnected");
                    stateSwitchWasSucessful = Statics.GUI.enableControls(true);
                    Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceConnected.png", "Device Connected");
                    Statics.GUI.setStatusMessageLabel("Target Acquired");
                    AudioHandler.playSound("/CASUAL/resources/sounds/Connected-SystemReady.wav");
                    break;
                default:

                    if (State == 2) {
                        Log.level0Error("@stateMultipleDevices");
                        Log.level0Error("Remove " + (State - 1) + " device to continue.");
                    }

                    Log.level4Debug("State Multiple Devices Number of devices" + State);
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
            if (Statics.isWindows()) {
                new CASUALInteraction("@interactionWindowsDeviceNotDetected").showTimeoutDialog(60, null, CASUALInteraction.OK_OPTION, CASUALInteraction.INFORMATION_MESSAGE, new String[]{"OK"}, "OK");
            } else if (Statics.isLinux()) {
                new CASUALInteraction("@interactionLinuxDeviceNotDetected").showTimeoutDialog(60, null, CASUALInteraction.OK_OPTION, CASUALInteraction.INFORMATION_MESSAGE, new String[]{"OK"}, "OK");
            } else if (Statics.isMac()) {
                new CASUALInteraction("@interactionMacDeviceNotDetected").showTimeoutDialog(60, null, CASUALInteraction.OK_OPTION, CASUALInteraction.INFORMATION_MESSAGE, new String[]{"OK"}, "OK");
            }
            hasConnected = true;
        }

    }

    private String getConnectedDevices() {
        String[] getDevicesCommand = new String[]{Statics.adbDeployed, "devices"};
        String devices = Shell.silentShellCommand(getDevicesCommand).replace("List of devices attached \n", "").replace("\n", "").replace("\t", "");
        return devices;

    }
}
