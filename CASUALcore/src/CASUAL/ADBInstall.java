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

import CASUAL.misc.DiffTextFiles;


/**
 *
 * @author adam
 */
class ADBInstall {

    FileOperations FileOperations = new FileOperations();
    Log Log = new Log();

    public void deployADB() { //This is called by getADBCommand() so it must not use that command
        FileOperations fo=new FileOperations();
        if ( Statics.adbDeployed!=null && fo.verifyExists(Statics.adbDeployed) ){
            return;
        }

        if (OSTools.isLinux()) {
            Log.level4Debug("Found Linux Computer");
            Statics.adbDeployed = Statics.getTempFolder() + "adb";
            fo.copyFromResourceToFile(Statics.LinuxADB(), Statics.adbDeployed);
        } else if (OSTools.isMac()) {
            Log.level4Debug("Found Mac Computer");
            Statics.adbDeployed = Statics.getTempFolder() + "adb";
            fo.copyFromResourceToFile(Statics.MacADB, Statics.adbDeployed);
        } else if (OSTools.isWindows()) {
            Log.level4Debug("Found Windows Computer");
            fo.copyFromResourceToFile(Statics.WinPermissionElevatorResource, Statics.WinElevatorInTempFolder);
            Statics.adbDeployed = Statics.getTempFolder() + "adb.exe";
            fo.copyFromResourceToFile(Statics.WinADB, Statics.adbDeployed);
            fo.copyFromResourceToFile(Statics.WinADB2, Statics.getTempFolder() + "AdbWinApi.dll");
            fo.copyFromResourceToFile(Statics.WinADB3, Statics.getTempFolder() + "AdbWinUsbApi.dll");
        } else {
            new CASUALMessageObject("@interactionsystemNotNativelySupported").showInformationMessage();
            Statics.adbDeployed = "adb";
        }

        updateADBini();
        fo.setExecutableBit(Statics.adbDeployed); //for *nix.
        String DeviceList;
        DeviceList = ADBTools.getDevices();
        new ADBTools().checkADBerrorMessages(DeviceList);
    }

    private void updateADBini() {
        FileOperations fo = new FileOperations();
        String adbIniDeployed = "";
        if (OSTools.isLinux() || OSTools.isMac()) {
            adbIniDeployed = Statics.FilesystemAdbIniLocationLinuxMac;
        }
        if (OSTools.isWindows()) {
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
