/*CASUALConnectionStatus provides connection status monitoring for CASUAL
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

    CASUALAudioSystem CAS = new CASUALAudioSystem();
    private static int LastState = 0;
    Log Log = new Log();
    Shell Shell = new Shell();
    public final static int ONE_SECOND = 1000;
    private static int cycles = 0;
    private static boolean hasConnected = false;
    Timer DeviceCheck = new Timer(ONE_SECOND, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            
           Thread t = new Thread(r);
           t.start();
        }
    });

    Runnable r = new Runnable(){
                @Override
        public void run() {

        
            if (Statics.lockGUIformPrep||Statics.lockGUIunzip) {
                    Statics.GUI.enableControls(false);
                    Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceDisconnected.png", "Device Not Detected");
                    LastState=0;
                    return;
                }
                String DeviceCommand[] = {Statics.AdbDeployed, "devices"};
                try {
                    String DeviceList = Shell.silentShellCommand(DeviceCommand).replace("List of devices attached \n", "").replace("\n", "").replace("\t", "");
                    Statics.DeviceTracker = DeviceList.split("device");

                    if (!hasConnected) {
                        if (cycles > 15) {
                            if (Statics.isWindows()) {
                                new TimeOutOptionPane().showTimeoutDialog(60, null, "I have not detected your device connect.\nIt is possible that you need to install drivers\nPlease search for \"windows driver *your device*\" on google.", "Device not detected", TimeOutOptionPane.OK_OPTION, TimeOutOptionPane.INFORMATION_MESSAGE, new String[]{"OK"}, "OK");
                            } else if (Statics.isLinux()) {
                                new TimeOutOptionPane().showTimeoutDialog(60, null, "I have not detected your device connect.\nIt is possible that you need to install a KO or libusb \nPlease search for \"using adb Linux *your device*\" on google.", "Device not detected", TimeOutOptionPane.OK_OPTION, TimeOutOptionPane.INFORMATION_MESSAGE, new String[]{"OK"}, "OK");
                            } else if (Statics.isMac()) {
                                new TimeOutOptionPane().showTimeoutDialog(60, null, "I have not detected your device connect.\nIt is possible that you need to install a kext\nPlease search for \"kext *your device*\" on google.", "Device not detected", TimeOutOptionPane.OK_OPTION, TimeOutOptionPane.INFORMATION_MESSAGE, new String[]{"OK"}, "OK");
                            }
                            hasConnected = true;
                        }
                        cycles++;
                    }
                    //Multiple devices detected
                    if (Statics.DeviceTracker.length > 1) {
                        stateSwitcher(Statics.DeviceTracker.length);
                        //No devices detected
                    } else if (Statics.DeviceTracker[0].isEmpty()) {
                        //TODO: Execute adb wait-for-device as a blocking call to stop additional processes
                        stateSwitcher(0);

                        //TODO: verify this causes no problems. 
                        Log.level0("Device is not connnected");
                        Statics.DeviceMonitor.DeviceCheck.stop();
                        Shell.sendShellCommand(new String[]{Statics.AdbDeployed, "wait-for-device"});
                        Statics.DeviceMonitor.DeviceCheck.start();
                        //One device detected
                    } else if (!Statics.DeviceTracker[0].isEmpty()) {
                        hasConnected=true;
                        stateSwitcher(1);
                    } else if (Statics.DeviceTracker[0].contains("offline")) {

                    }     

                    //Check and handle abnormalities
                    // pairing problem with 4.2+
                    if (DeviceList.contains("offline")){
                        TimeOutOptionPane TimeOutOptionPane = new TimeOutOptionPane();
                        String[] ok = {"All set and done!"};
                        new TimeOutOptionPane().showTimeoutDialog(60, null, "It would appear that the connected device is not paired properly.\n"
                                + "Please disconnect the device, then reconnect it.\n"
                                + "Next unlock the device and check for a message onscreen.\n"
                                + "Select \"Always allow from this computer\" then press OK.\n", 
                                "Device Not Paired", TimeOutOptionPane.OK_OPTION, 2, ok , 0);
                        DeviceList = Shell.elevateSimpleCommand(DeviceCommand);
                    }
                    //insufficient permissions

                    if (DeviceList.contains("????????????") && (!Statics.isWindows())) {
                        Log.level3("sleeping for 4 seconds.  Device list: " + DeviceList);
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException ex) {
                            Log.errorHandler(ex);
                        }
                        DeviceList = Shell.silentShellCommand(DeviceCommand).replace("List of devices attached \n", "").replace("\n", "").replace("\t", "");
                        Statics.DeviceTracker = DeviceList.split("device");


                        if (DeviceList.contains("????????????")) {
                            DeviceCheck.stop();
                            Log.level0("Insufficient permissions on server detected.");
                            String cmd[] = {Statics.AdbDeployed, "kill-server"}; //kill the server
                            Log.level1("killing server and requesting elevated permissions.");
                            Shell.sendShellCommand(cmd); //send the command
                            //notify user that permissions will be requested and what they are used for
                            TimeOutOptionPane TimeOutOptionPane = new TimeOutOptionPane();
                            String[] ok = {"ok"};
                            CASUALAudioSystem.playSound("/CASUAL/resources/sounds/PermissionEscillation.wav");
                            TimeOutOptionPane.showTimeoutDialog(60, null, "It would appear that this computer\n"
                                    + "is not set up properly to communicate\n"
                                    + "with the device.  As a work-around we\n"
                                    + "will attempt to elevate permissions \n"
                                    + "to access the device properly.", "Insufficient Permissions", TimeOutOptionPane.OK_OPTION, 2, ok, 0);
                            DeviceList = Shell.elevateSimpleCommand(DeviceCommand);
                            // if permissions elevation was sucessful
                            if (!DeviceList.contains("????????????")) {
                                Log.level3(DeviceList);
                                Log.level1("Permissions problem corrected");
                                DeviceCheck.start();
                                //devices still not properly recognized.  Log it.
                            } else {
                                Log.level0("Unrecognized device detected");
                                Log.level0("");
                                Log.level0("Application halted. Please restart the application.");
                                Log.level0("If you continue to experience problems, please report this issue ");
                            }
                        }
                    }
                } catch (java.lang.NullPointerException e) {
                    Log.errorHandler(e);
                }
        }


        
    };
    private void stateSwitcher(int State) {
        if (LastState != State) {
            Log.level3("State Change Detected, The new state is: " + State);
            switch (State) {
                case 0:
                    Log.level3("State Disconnected");
                    Statics.GUI.enableControls(false);
                    Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceDisconnected.png", "Device Not Detected");
                    CASUALAudioSystem.playSound("/CASUAL/resources/sounds/Disconnected.wav");
                    break;
                case 1:
                    Log.level3("State Connected");
                    Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceConnected.png", "Device Connected");
                    Statics.GUI.setStatusMessageLabel("Target Acquired");
                    CASUALAudioSystem.playSound("/CASUAL/resources/sounds/Connected-SystemReady.wav");
                    Statics.GUI.enableControls(true);
                    break;
                default:

                    if (State == 2) {
                        Log.level0("Multiple devices detected. Remove " + (State - 1) + " device to continue.");
                    } else {
                        Log.level0("Remove " + (State - 1) + " devices to continue.");
                    }

                    Log.level3("State Multiple Devices Number of devices" + State);
                    Statics.GUI.enableControls(false);
                    Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/TooManyDevices.png", "Target Acquired");
                    String[] URLs = {"/CASUAL/resources/sounds/" + String.valueOf(State) + ".wav", "/CASUAL/resources/sounds/DevicesDetected.wav"};
                    CASUALAudioSystem.playMultipleInputStreams(URLs);
                    break;

            }
            LastState = State;
        }
    }
}
