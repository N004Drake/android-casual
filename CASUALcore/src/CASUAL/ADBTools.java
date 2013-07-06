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
    Log log=new Log();

    
    /**
     * method to get the devices command for ADB use
     * @return path_to_adb, devices
     */
    public static String[] devicesCmd(){
        return new String[]{Statics.adbDeployed, "devices"}; 
    };
    /**
     *starts adb server
     * @return value from adb
     */
    public static String[] startServer(){
        return new String[] {Statics.adbDeployed, "start-server"};
    }
    /**
     *kills the ADB server
     * @return value from ADB command
     */
    public static String[] killServer(){
        return new String[]{Statics.adbDeployed, "kill-server"};
    }
    
    /**
     *kills and restarts the adb server max duration of 4.5 seconds
     */
    public void restartADBserverSlowly() {
        log.level3Verbose("@restartingADBSlowly");
        Shell shell = new Shell();
        shell.timeoutShellCommand(killServer(),500);
        sleepForMillis(1000);
        shell.timeoutShellCommand(devicesCmd(), 3000);

    }

    /**
     * starts an elevated ADB server
     */
    public void elevateADBserver() {
        log.level3Verbose("@restartingADB");
        Shell shell = new Shell();
        shell.silentShellCommand(killServer());
        shell.elevateSimpleCommand(devicesCmd());
    }

    /**
     *
     */
    public void killADBserver() {
        log.level3Verbose("Restarting ADB after system update");
        Shell shell = new Shell();

        shell.silentShellCommand(killServer());
    }
    


    /**
     *
     * @param DeviceList
     * @throws HeadlessException
     */
    public void checkADBerrorMessages(String DeviceList) throws HeadlessException {
        
        if ((Statics.isLinux()) && (DeviceList.contains("something about UDEV rules"))) { //Don't know how to handle this yet
            //handle add udevrule
        }

        //handle libusb -3
        if ((Statics.isLinux()) && (DeviceList.contains("ERROR-3"))) { //Don't know how to handle this yet
            Shell shell = new Shell();
            log.level0Error("@permissionsElevationRequired");
            shell.silentShellCommand(new String[]{Statics.adbDeployed, "kill-server"});
            shell.elevateSimpleCommandWithMessage(devicesCmd(), "Device permissions problem detected");
        }

        if (DeviceList.contains("ELFCLASS64") && DeviceList.contains("wrong ELF")) {
            new CASUALInteraction("@interactionELFCLASS64Error").showInformationMessage();

        }

        //TODO: implement this as an error handler for ADB. in a centralized manner. 
        if (DeviceList.contains("????????????") || DeviceList.contains("**************") || DeviceList.contains("error: cannot connect to daemon")) {
            log.level4Debug("Restarting ADB slowly");
            restartADBserverSlowly();
            DeviceList = new Shell().silentShellCommand(devicesCmd()).replace("List of devices attached \n", "").replace("\n", "").replace("\t", "");
            if (!Statics.isWindows() && DeviceList.contains("????????????") || DeviceList.contains("**************") || DeviceList.contains("error: cannot connect to daemon")) {
                log.level4Debug("Permissions problem detected. Requesting CASUAL permissions escillation.");
                killADBserver();
                elevateADBserver();
            }
        }
    }
    private void sleepForMillis(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            //no need to handle this
        }
    }
}
