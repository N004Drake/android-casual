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

import CASUAL.communicationstools.AbstractDeviceCommunicationsProtocol;

/**
 * CASUALConnectionStatus provides ADB connection status monitoring for CASUAL
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALConnectionStatusMonitor {

    private static int LastState = 0;  //last state detected
    private static CASUAL.communicationstools.AbstractDeviceCommunicationsProtocol monitor;
    private static CASUAL.communicationstools.AbstractDeviceCommunicationsProtocol monitorLastState;

    /**
     * number of sucessive times ADB has halted. If ADB pauses for more than 4
     * seconds, it is considered locked up. If ADB locks up 10 times, monitoring
     * is stopped.
     */
    final static int TIMERINTERVAL = 1000;
    static boolean paused = false;

    /**
     * stops monitoring and nulls the monitor out. Stores the monitor to be
     * resumed at a later time. Monitor may be started again by using the
     * start(new monitor) or resumeAfterStop to continue the monitoring.
     */
    public static void stop() {
        monitorLastState = monitor;
        monitor = null;
        paused = true;
    }

    public static void resumeAfterStop() {
        paused = false;
        if (monitorLastState == null) {
            Log.level3Verbose("A call to resume monitor occurred, but monitor was not reset first.  No action is occuring");
        } else {
            new CASUALConnectionStatusMonitor().start(monitorLastState);
        }
    }

    /**
     * Static method to access toString().
     *
     * @return value of toString()
     */
    public static String getStatus() {
        return new CASUALConnectionStatusMonitor().toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String n = "\n";
        sb.append("Status:");
        if (monitor == null) {
            sb.append("offline").append(n).append("Mode:not monitoring").append(n);
        } else {
            sb.append("online").append(n).append(monitor.toString());
        }
        return sb.toString();
    }

    /**
     * Starts and stops the ADB timer reference with
     * Statics.casualConnectionStatusMonitor.DeviceCheck ONLY;
     *
     * @param mode sets the monitoring mode
     */
    public void start(AbstractDeviceCommunicationsProtocol mode) {
        stop();
        paused=false;
        stateSwitcher(0);
        monitor = mode;
        Log.level3Verbose("Starting: " + mode);
        //lock controls if not available yet.
        if (Statics.isGUIIsAvailable() && (CASUALStartupTasks.lockGUIformPrep || CASUALStartupTasks.lockGUIunzip)) {
            Statics.GUI.setControlStatus(false);
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
                    if (paused) {
                        continue;
                    }
                    doDeviceCheck();
                }
            }

        });
        t.setName("Connection Status");
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
            stateSwitcher(1);

        }

    }

    void stateSwitcher(int state) {
        if (LastState != state) {
            Log.level4Debug("State Change Detected, The new state is: " + state);
            switch (state) {
                case 0:
                    Log.level4Debug("@stateDisconnected");
                    Statics.setStatus("Device Removed");
                    Statics.GUI.deviceDisconnected();
                    Statics.GUI.setControlStatus(false);

                    break;
                case 1:
                    Statics.setStatus("Device Connected");
                    Log.level4Debug("@stateConnected");
                    Statics.GUI.deviceConnected("ADB");
                    Statics.GUI.setControlStatus(true);
                    break;
                default:
                    Statics.setStatus("Multiple Devices Detected");
                    if (state == 2) {
                        Log.level0Error("@stateMultipleDevices");
                        Log.level0Error("Remove " + (state - 1) + " device to continue.");
                    }

                    Statics.GUI.setControlStatus(false);
                    Log.level4Debug("State Multiple Devices Number of devices" + state);
                    Statics.GUI.deviceMultipleConnected(state);
                    break;

            }
            LastState = state;
        }
    }

    private void sleepForOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Log.errorHandler(ex);
        }
    }

}
