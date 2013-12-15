/*interface DeviceConnection provides a unified manner of accessing tools which access devices.
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
package CASUAL.CommunicationsTools;

import CASUAL.Shell;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adamoutler
 */
public abstract class AbstractDeviceCommunicationsProtocol {

    
    String[] windowsLocation;
    String[] linux32Location;
    String[] linux64Location;
    String[] linuxArmv6Location;
    String[] macLocation;
    
    /*
     * binaryLocation refers to the location of the binary to be operated by
     * this class eg.. /tmp/Adam2123/adb.exe or C:\Users\Adam\local...
     * This must be overridden and made static for the interface. 
     */
    //static String binaryLocation;

    /**
     * reset is used to clear the binary location from outside the package and
     * stop the service if required. This is useful for when the temp folder is
     * changed or when the system is shutting down. This should also trigger the
     * getBinaryLocation() to create a new binary upon the next call. This is a
     * method to destroy the private static location of the binary in memory.
     */
    abstract public void reset();

    /**
     * Deploys the binary and returns its location. This method should check the
     * binaryLocation, and if the called location is null, it should deploy the
     * binary using the deployBinary(TempFolder) method. This is the primary
     * method used by this class.
     *
     * @return location to binary being called.
     */
    abstract public String getBinaryLocation();

    /**
     * returns true if 1 device is connected. Will return false if more than one
     * or less than one is connected. This method should use the
     * numberOfDevicesConnected() method to get the number of devices connected
     * and determine if it is a single device.
     *
     * @return true if connected.
     */
    public boolean isConnected() {
        return numberOfDevicesConnected() == 1;
    }

    /**
     * Waits for isConnected() to return true. If the device is not connected,
     * this method will continue blocking. The purpose of this method is to halt
     * progress until a device is connected and usable. waitForDevice() may use
     * any tools it can to determine the ready status of the device.
     *
     */
    public void waitForDevice(){
        while (!isConnected()){
           sleep200ms();
        }
    }
    
    /**
     * sleeps for 200 ms and then returns
     */
    private void sleep200ms(){
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(AbstractDeviceCommunicationsProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * returns and integer representing the number of devices connected. This
     * may or may not be possible depending upon the tool used. This method will
     * use the getBinaryLocation method.
     *
     * @return results from the wait command.
     */
    abstract public int numberOfDevicesConnected();

    /**
     * Examines the return values of other commands and determines if action
     * should be taken, then takes it. This method should be called frequently
     * so as to ensure that errors are caught and corrected quickly. The catches
     * may be as simple as notifying the operator to plug in the device or
     * automatically installing drivers should the return value detect that it
     * is required.
     *
     * @param returnValue string to check for errors
     * @return true if no error
     */
    abstract public boolean checkErrorMessage(String[] commandRun, String returnValue);

    /**
     * This method is used by checkErrorMessage to install drivers when
     * required. There should never be a reason to call this independently.
     *
     * @return true if drivers were installed. false indicates a problem.
     */
    abstract public boolean installDriver();

    /**
     * Called by the getBinaryLocation method to deploy the binary used by the
     * application. This method is called when there is no known location for
     * the binary. It is in charge of determining which platform's binary to
     * deploy and deploying associated resources.
     *
     * @param tempFolder Location to deploy binary.
     * @return location to binary.
     */
    abstract public String deployBinary(String tempFolder);

    /**
     * Restarts the connection to the device. This may be a simple call or a
     * complex one. This call is intended to fix problems detected by
     * checkErrorMessage. Depending on the situation, it may be beneficial to
     * keep a counter and try various troubleshooting steps for various
     * operating systems here.
     */
    abstract public void restartConnection();

    /**
     * provides a safe method to run the binary with parameters. this method
     * should execute a Shell.timeOutShell command and allow for a method of
     * calling the binary with a timeout so as to never allow the method to
     * hang.
     *
     * @param parameters parameters used to operate the binary. Eg.. adb DETECT,
     * or heimdall FLASH. The binary is to be specified in the run() and only
     * the parameters are supplied.
     * @param timeout time in ms before timeout will occur and the command will
     * return;
     * @param silent true will cause this method to keep information out of the
     * logs so as not to clutter with every-second-pings of a device or the
     * such.
     * @return value from command. if result begins with "Timeout!!! " the
     * command has exceeded the set timeout value.
     */
    public String run(String[] parameters, int timeout, boolean silent) {
        Shell shell = new Shell();
        //expand array by one
        String[] runcmd = new String[parameters.length + 1];
        runcmd[0] = getBinaryLocation(); //insert binary as [0]
        for (int i = 1; i < runcmd.length; i++) {
            runcmd[i] = parameters[i - 1]; //insert the rest of the parameters
        }
        if (silent) {
            return shell.silentTimeoutShellCommand(runcmd, timeout);
        } else {
            return shell.timeoutShellCommand(runcmd, timeout);
        }
    }

}
