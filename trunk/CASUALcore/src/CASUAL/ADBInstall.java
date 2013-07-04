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



/**
 *
 * @author adam
 */
class ADBInstall {

    FileOperations FileOperations = new FileOperations();
    Log Log = new Log();

    public void deployADB() {

        int adbTimeout=0;
        if (Statics.isLinux()) {
            Log.level4Debug("Found Linux Computer");
            Statics.adbDeployed = Statics.TempFolder + "adb";
            FileOperations.copyFromResourceToFile(Statics.LinuxADB(), Statics.adbDeployed);
        } else if (Statics.isMac()) {
            Log.level4Debug("Found Mac Computer");
            Statics.adbDeployed = Statics.TempFolder + "adb";
            FileOperations.copyFromResourceToFile(Statics.MacADB, Statics.adbDeployed);
        } else if (Statics.isWindows()) {
            Log.level4Debug("@Found Windows Computer");
            FileOperations.copyFromResourceToFile(Statics.WinPermissionElevatorResource, Statics.WinElevatorInTempFolder);
            Statics.adbDeployed = Statics.TempFolder + "adb.exe";
            FileOperations.copyFromResourceToFile(Statics.WinADB, Statics.adbDeployed);
            FileOperations.copyFromResourceToFile(Statics.WinADB2, Statics.TempFolder + "AdbWinApi.dll");
            FileOperations.copyFromResourceToFile(Statics.WinADB3, Statics.TempFolder + "AdbWinUsbApi.dll");
            adbTimeout=3000;
        } else {
            new CASUALInteraction("@interactionsystemNotNativelySupported").showInformationMessage();
            Statics.adbDeployed = "adb";
        }

        updateADBini();
        FileOperations.setExecutableBit(Statics.adbDeployed); //for *nix.
        String DeviceList;
        DeviceList = new Shell().timeoutShellCommand(ADBTools.devicesCmd(), adbTimeout);
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
