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

/**
 *
 * @author adam
 */
class CASUALDeployADB {

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
            Statics.LiveSendCommand.add(Statics.AdbDeployed);
            Statics.LiveSendCommand.add("get-state");
            new Shell().silentBackgroundShellCommand();
            try {
                Log.level4Debug("sleeping for Windows ADB start");
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                //no catch needed for sleep interruption
            }

        } else {
            Log.level0Error("Your system is not supported");
        }
        
        Shell Shell = new Shell();
        killADBserver();
        String[] devicesCmd = {Statics.AdbDeployed, "devices"};
        Statics.LiveSendCommand.add(Statics.AdbDeployed);
        Statics.LiveSendCommand.add("get-state");
        new Shell().silentBackgroundShellCommand();
        String DeviceList = Shell.sendShellCommand(devicesCmd);


        if ((Statics.isLinux()) && (DeviceList.contains("something about UDEV rules"))) { //Don't know how to handle this yet
            //handle add udevrule
        }

        //handle libusb -3
        if ((Statics.isLinux()) && (DeviceList.contains("ERROR-3"))) { //Don't know how to handle this yet
            Shell shell = new Shell();
            Log.level0Error("Permissions problem detected. Killing and requesting permissions escillation.");
            shell.silentShellCommand(new String[]{Statics.AdbDeployed, "kill-server"});
            shell.elevateSimpleCommandWithMessage(devicesCmd, "Device permissions problem detected");
        }

        if (DeviceList.contains("ELFCLASS64") && DeviceList.contains("wrong ELF")) {
            new CASUALInteraction().showInformationMessage("Could not execute ADB. 'Wrong ELF class' error\n"
                    + "This can be resolved by installation of ia32-libs"
                    + "eg.. sudo apt-get install ia32-libs\n"
                    + "ie.. sudo YourPackageManger install ia32-libs", "ELFCLASS64 error!");

        }


        Log.level4Debug("Device List:" + DeviceList);
        if ((!Statics.isWindows()) && ((DeviceList.contains("????????????") || (DeviceList.contains("**************")) || (DeviceList.contains("error: cannot connect to daemon"))))) {
            Log.level4Debug("sleeping for 4 seconds.  Device list: " + DeviceList);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Log.errorHandler(ex);
            }
            DeviceList = Shell.silentShellCommand(devicesCmd).replace("List of devices attached \n", "").replace("\n", "").replace("\t", "");
            Statics.DeviceTracker = DeviceList.split("device");
            if (DeviceList.contains("????????????") || (DeviceList.contains("**************"))) {
            }
        } else {
        }
    }
    
    private void updateADBini(){
        FileOperations fo=new FileOperations();
        String adbIniDeployed="";
        if (Statics.isLinux()||Statics.isMac()) adbIniDeployed=Statics.FilesystemAdbIniLocationLinuxMac;
        if (Statics.isWindows()) adbIniDeployed=Statics.FilesystemAdbIniLocationWindows;
        if (! fo.verifyExists(adbIniDeployed)){  
            //deploy a new copy
            FileOperations.copyFromResourceToFile(Statics.ADBini, adbIniDeployed);
        } else {
            //see what's missing and add new entries
            DiffTextFiles DTF=new DiffTextFiles();
            DTF.appendDiffToFile(adbIniDeployed, DTF.diffResourceVersusFile(Statics.ADBini, adbIniDeployed));
        }
    }
    
    
    private void restartADBserver(){
        Log.level2Information("Restarting ADB after system update");
        Shell shell=new Shell();
        String[] killCmd = {Statics.AdbDeployed, "kill-server"};
        shell.silentShellCommand(killCmd);
        String[] devicesCmd = {Statics.AdbDeployed, "devices"};
        shell.silentShellCommand(devicesCmd);
    }
    private void killADBserver(){
        Log.level2Information("Restarting ADB after system update");
        Shell shell=new Shell();
        String[] killCmd = {Statics.AdbDeployed, "kill-server"};
        shell.silentShellCommand(killCmd);
    }
}
