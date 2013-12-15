/*BusyboxTools deploys and gives an on-device reference to busybox.
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

import CASUAL.CommunicationsTools.ADB.ADBTools;

/**
 * BusyboxTools deploys and gives an on-device reference to busybox.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class BusyboxTools {

    final String busyboxLocation = "/data/local/tmp/busybox";
    ADBTools adb = new ADBTools();
    Shell shell = new Shell();
    

    private String getDeviceArch() {
        if (Statics.CASPAC != null) {
            if (!Statics.CASPAC.getActiveScript().deviceArch.equals("")) {
                return Statics.CASPAC.getActiveScript().deviceArch;
            }
        }
        String cpuinfo = shell.silentShellCommand(new String[]{adb.getBinaryLocation(), "shell", "cat /proc/cpuinfo"});
        String[] lines = cpuinfo.split("\n");
        for (String line : lines) {
            if (line.contains("Processor") && line.contains("ARM")) {
                return "ARM";

            }
        }
        return "X86";
    }

    private boolean busyboxIsInstalled() {

        String temp = shell.silentShellCommand(new String[]{adb.getBinaryLocation(), "shell", "chmod 777 " + busyboxLocation + ";ls " + busyboxLocation});

        return !temp.contains("No such") && !temp.contains("found");
    }

    /**
     * Deploys busybox to device and returns the location of busybox on device.
     *
     * @return string path to busybox on device
     */
    public static String getBusyboxLocation() {
        BusyboxTools bbtools = new BusyboxTools();
        if (bbtools.busyboxIsInstalled()) {
            return bbtools.busyboxLocation;
        } else {
            new Log().level4Debug("deploying busybox");
            return bbtools.deployBusybox();
        }
    }

    private String deployBusybox() {
        FileOperations fo = new FileOperations();
        String busyboxOnHost = Statics.getTempFolder() + "busybox";
        String[] installCmd = {adb.getBinaryLocation(), "push", busyboxOnHost, busyboxLocation};
        String busyboxResource;
        if (getDeviceArch().equals("ARM")) {
            busyboxResource = Statics.busyboxARM;
        } else {
            busyboxResource = Statics.busyboxX86;
        }
        fo.copyFromResourceToFile(busyboxResource, busyboxOnHost);
        new Shell().silentShellCommand(installCmd);
        String check = new Shell().sendShellCommand(new String[]{adb.getBinaryLocation(), "shell", "chmod 777 /data/local/tmp/busybox;ls /data/local/tmp"});
        if (check.contains("busybox")) {
            return this.busyboxLocation;
        } else {
            return null;
        }

    }
}
