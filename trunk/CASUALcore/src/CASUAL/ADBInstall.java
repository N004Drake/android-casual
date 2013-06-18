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

import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author adam
 */
class ADBInstall {

    FileOperations FileOperations = new FileOperations();
    Log Log = new Log();

    public void deployADB() {

        if (Statics.isLinux()) {
            Log.level4Debug("Found Linux Computer");
            updateADBini();
            Statics.AdbDeployed = Statics.TempFolder + "adb";
            FileOperations.copyFromResourceToFile(Statics.LinuxADB(), Statics.AdbDeployed);
            FileOperations.setExecutableBit(Statics.AdbDeployed);
        } else if (Statics.isMac()) {
            Log.level4Debug("Found Mac Computer");
            updateADBini();
            Statics.AdbDeployed = Statics.TempFolder + "adb";
            FileOperations.copyFromResourceToFile(Statics.MacADB, Statics.AdbDeployed);
            FileOperations.setExecutableBit(Statics.AdbDeployed);
        } else if (Statics.isWindows()) {
            Log.level4Debug("Found Windows Computer");
            updateADBini();
            FileOperations.copyFromResourceToFile(Statics.WinPermissionElevatorResource, Statics.WinElevatorInTempFolder);
            Statics.AdbDeployed = Statics.TempFolder + "adb.exe";
            FileOperations.copyFromResourceToFile(Statics.WinADB, Statics.AdbDeployed);
            FileOperations.copyFromResourceToFile(Statics.WinADB2, Statics.TempFolder + "AdbWinApi.dll");
            FileOperations.copyFromResourceToFile(Statics.WinADB3, Statics.TempFolder + "AdbWinUsbApi.dll");
        } else {
            new CASUALInteraction("Warning","Your system is not natively supported,\nplease install ADB manually.\nAttempting to continue").showInformationMessage();
            Statics.AdbDeployed = "adb";
        }

        
        String DeviceList;
        try {
            DeviceList = new Shell().timeOutShellCommand(ADBTools.devicesCmd(), 3000);
        } catch (TimeoutException ex) {
            DeviceList="";
        }
        new ADBTools().checkADBerrorMessages(DeviceList);
    }

    private void updateADBini() {
        FileOperations fo = new FileOperations();
        String adbIniDeployed = "";
        if (Statics.isLinux() || Statics.isMac()) {
            adbIniDeployed = Statics.FilesystemAdbIniLocationLinuxMac;
        }
        if (Statics.isWindows()) {
            adbIniDeployed = Statics.FilesystemAdbIniLocationWindows;
        }
        if (!fo.verifyExists(adbIniDeployed)) {
            //deploy a new copy
            FileOperations.copyFromResourceToFile(Statics.ADBini, adbIniDeployed);
        } else {
            //see what's missing and add new entries
            DiffTextFiles DTF = new DiffTextFiles();
            DTF.appendDiffToFile(adbIniDeployed, DTF.diffResourceVersusFile(Statics.ADBini, adbIniDeployed));
        }
    }
   
    
}
