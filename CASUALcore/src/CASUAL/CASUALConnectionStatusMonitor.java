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

import CASUAL.CommunicationsTools.AbstractDeviceCommunicationsProtocol;

/**
 * CASUALConnectionStatus provides ADB connection status monitoring for CASUAL
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALConnectionStatusMonitor {

    /**
     * Array of devices connnected via ADB. Note: more than one is not
     * supported.
     */
    private static int LastState = 0;  //last state detected
    private static int cycles = 0; //number of cycles
    private static boolean hasConnected = false; //device was detected since startup
    Log log = new Log();

    /**
     * number of sucessive times ADB has halted. If ADB pauses for more than 4
     * seconds, it is considered locked up. If ADB locks up 10 times, monitoring
     * is stopped.
     */
    final static int TIMERINTERVAL = 1000;
    static CASUAL.CommunicationsTools.AbstractDeviceCommunicationsProtocol monitor;

    public static void reset() {
        monitor = null;
    }

    /**
     * Starts and stops the ADB timer reference with
     * Statics.casualConnectionStatusMonitor.DeviceCheck ONLY;
     *
     * @param mode sets the monitoring mode
     */
    public void start(AbstractDeviceCommunicationsProtocol mode) {
        monitor = mode;
        //lock controls if not available yet.
        if (Statics.isGUIIsAvailable() && (Locks.lockGUIformPrep || Locks.lockGUIunzip)) {
            Statics.GUI.enableControls(false);
            Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceDisconnected.png", "Device Not Detected");
            LastState = 0;
        }
        doMonitoring();
    }

    private void doMonitoring() {

        //check device for state changes
        //loop on new thread while the monitor is the same monitor
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                AbstractDeviceCommunicationsProtocol stateMonitor = monitor;
                while (CASUALConnectionStatusMonitor.monitor != null && CASUALConnectionStatusMonitor.monitor.equals(stateMonitor)) {
                    sleepForOneSecond();
                    doDeviceCheck();
                }
            }

        });
        t.start();

    }

    private void doDeviceCheck() {
        int connectedDevices;
        try {
            connectedDevices = monitor.numberOfDevicesConnected();
        } catch (NullPointerException ex) {
            connectedDevices = 0;
        }

        //Multiple devices detected
        if (connectedDevices > 1) {

            stateSwitcher(connectedDevices);
            //No devices detected
        } else if (connectedDevices == 0) {
            stateSwitcher(0);
            //One device detected
        } else if (connectedDevices == 1) {
            hasConnected = true;
            stateSwitcher(1);

        }

    }

    void stateSwitcher(int state) {
        if (LastState != state) {
            log.level4Debug("State Change Detected, The new state is: " + state);
            switch (state) {
                case 0:
                    log.level4Debug("@stateDisconnected");
                    Statics.setStatus("Device Removed");
                    Statics.GUI.deviceDisconnected();
                    Statics.GUI.enableControls(false);

                    break;
                case 1:
                    Statics.setStatus("Device Connected");
                    log.level4Debug("@stateConnected");
                    Statics.GUI.deviceConnected("ADB");
                    Statics.GUI.enableControls(true);
                    break;
                default:
                    Statics.setStatus("Multiple Devices Detected");
                    if (state == 2) {
                        log.level0Error("@stateMultipleDevices");
                        log.level0Error("Remove " + (state - 1) + " device to continue.");
                    }

                    Statics.GUI.enableControls(false);
                    log.level4Debug("State Multiple Devices Number of devices" + state);
                    Statics.GUI.deviceMultipleConnected(state);
                    break;

            }
            LastState = state;
        }
    }

    private void messageUser() {
        cycles++;
        if (cycles == 30 && !hasConnected) {
            if (OSTools.isWindows()) {
                new CASUALMessageObject("@interactionWindowsDeviceNotDetected").showTimeoutDialog(60, null, javax.swing.JOptionPane.OK_OPTION, javax.swing.JOptionPane.INFORMATION_MESSAGE, new String[]{"OK"}, "OK");
            } else if (OSTools.isLinux()) {
                new CASUALMessageObject("@interactionLinuxDeviceNotDetected").showTimeoutDialog(60, null, javax.swing.JOptionPane.OK_OPTION, javax.swing.JOptionPane.INFORMATION_MESSAGE, new String[]{"OK"}, "OK");
            } else if (OSTools.isMac()) {
                new CASUALMessageObject("@interactionMacDeviceNotDetected").showTimeoutDialog(60, null, javax.swing.JOptionPane.OK_OPTION, javax.swing.JOptionPane.INFORMATION_MESSAGE, new String[]{"OK"}, "OK");
            }
            hasConnected = true;
        }

    }

    private void sleepForOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log.errorHandler(ex);
        }
    }

}
