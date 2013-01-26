/*CASUALDeployADB deploys ADB for CASUAL 
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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author adam
 */

class CASUALDeployADB implements Runnable {

    FileOperations FileOperations = new FileOperations();
    Log Log = new Log();

    public void runAction() {
        (new Thread(new CASUALDeployADB())).start();
    }

    public void run() {
        DiffTextFiles DTF = new DiffTextFiles();


        if (Statics.isLinux()) {
            Log.level3("Found Linux Computer");
            //add our lines to the current adbini
            DTF.appendDiffToFile(Statics.FilesystemAdbIniLocationLinuxMac, DTF.diffResourceVersusFile(Statics.ADBini, Statics.FilesystemAdbIniLocationLinuxMac));
            Statics.AdbDeployed = Statics.TempFolder + "adb";
            FileOperations.copyFromResourceToFile(Statics.LinuxADB, Statics.AdbDeployed);
            FileOperations.setExecutableBit(Statics.AdbDeployed);
        } else if (Statics.isMac()) {
            Log.level3("Found Mac Computer");
            //add our lines to the current adbini

            //TODO: figure out why this crashes adb on Mac.
            String addToADBUSB = DTF.diffResourceVersusFile(Statics.ADBini, Statics.FilesystemAdbIniLocationLinuxMac);
            DTF.appendDiffToFile(Statics.FilesystemAdbIniLocationLinuxMac, addToADBUSB);


            Statics.AdbDeployed = Statics.TempFolder + "adb";
            FileOperations.copyFromResourceToFile(Statics.MacADB, Statics.AdbDeployed);
            FileOperations.setExecutableBit(Statics.AdbDeployed);
        } else if (Statics.isWindows()) {
            //TODO: add \ after home.android folder
            //java.io.FileNotFoundException: C:\Users\adam.android\adb_usb.ini (The system can
            //not find the path specified)
            Log.level3("Found Windows Computer");
            DTF.appendDiffToFile(Statics.FilesystemAdbIniLocationWindows, DTF.diffResourceVersusFile(Statics.ADBini, Statics.FilesystemAdbIniLocationWindows));
            FileOperations.copyFromResourceToFile(Statics.WinPermissionElevatorResource, Statics.WinElevatorInTempFolder);
            Statics.AdbDeployed = Statics.TempFolder + "adb.exe";
            FileOperations.copyFromResourceToFile(Statics.WinADB, Statics.AdbDeployed);
            FileOperations.copyFromResourceToFile(Statics.WinADB2, Statics.TempFolder + "AdbWinApi.dll");
            FileOperations.copyFromResourceToFile(Statics.WinADB3, Statics.TempFolder + "AdbWinUsbApi.dll");
            Statics.LiveSendCommand.add(Statics.AdbDeployed);
            Statics.LiveSendCommand.add("get-state");
            new Shell().silentBackgroundShellCommand();
            try {
                Log.level3("sleeping for Windows ADB start");
                Thread.sleep(500);
            } catch (InterruptedException ex) {
               
            }
        } else {
            Log.level0("Your system is not supported");
        }
        FileOperations.copyFromResourceToFile(Statics.ADBini, Statics.TempFolder + "adb_usb.ini");

        Shell Shell = new Shell();

        String[] killCmd = {Statics.AdbDeployed, "kill-server"};
        String[] devicesCmd = {Statics.AdbDeployed, "devices"};

        Statics.LiveSendCommand.add(Statics.AdbDeployed);
        Statics.LiveSendCommand.add("get-state");
        new Shell().silentBackgroundShellCommand();
        String DeviceList = Shell.sendShellCommand(devicesCmd);
        if (DeviceList.contains("ELFCLASS64") && DeviceList.contains("wrong ELF")) {
            JOptionPane.showMessageDialog(Statics.GUI,
                    "Could not execute ADB. 'Wrong ELF class' error\n"
                    + "This can be resolved by installation of ia32-libs"
                    + "eg.. sudo apt-get install ia32-libs\n"
                    + "ie.. sudo YourPackageManger install ia32-libs", "ELFCLASS64 error!",
                    JOptionPane.INFORMATION_MESSAGE);
        }


        Log.level3("Device List:" + DeviceList);
        if ( (!Statics.isWindows()) && (DeviceList.contains("????????????") || DeviceList.contains("error: cannot connect to daemon")) ) {
            Log.level1("killing server and requesting elevated permissions");
            Shell.sendShellCommand(killCmd);
            TimeOutOptionPane TimeOutOptionPane = new TimeOutOptionPane();
            String[] ok = {"ok"};
            CASUALAudioSystem.playSound("/CASUAL/resources/sounds/PermissionEscillation.wav");
            TimeOutOptionPane.showTimeoutDialog(60, null, "It would appear that this computer\n"
                    + "is not set up properly to communicate\n"
                    + "with the device.  As a work-around we\n"
                    + "will attempt to elevate permissions \n"
                    + "to access the device properly.", "Insufficient Permissions", TimeOutOptionPane.OK_OPTION, 2, ok, 0);
            DeviceList = Shell.elevateSimpleCommand(devicesCmd);
            if (!DeviceList.contains("????????????")) {
                Log.level2(DeviceList);
                Log.level1("Permissions elevation sucessful.");
            } else {
                Log.level0("Unrecognized device detected");

            }


        }



    }
}