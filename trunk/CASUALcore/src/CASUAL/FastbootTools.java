/*FastbootTools is a set of tools for use with fastboot
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

import CASUAL.misc.StringOperations;
import java.util.ArrayList;

/**
 *
 * @author adam
 */
public class FastbootTools {

    /**
     * deploys and verifies fastboot
     */
    public static void checkAndDeployFastboot() {
        if (! new FileOperations().verifyExists(Statics.fastbootDeployed)) {
            if (OSTools.isLinux()) {
                Statics.fastbootResource = getFastbootLinuxResource();
            }
            if (OSTools.isWindows()) {
                new CASUALMessageObject("@interactionInstallFastbootDrivers").showInformationMessage();
                Statics.fastbootDeployed=Statics.fastbootDeployed+".exe";
                Statics.fastbootResource = Statics.fastbootWindows;
            }
            if (OSTools.isMac()) {
                Statics.fastbootResource = Statics.fastbootMac;
            }
            Statics.log.level2Information("@deployingFastboot");
            Statics.log.level3Verbose("Deploying Fastboot from " + Statics.fastbootResource + " to " + Statics.fastbootDeployed);
            new FileOperations().copyFromResourceToFile(Statics.fastbootResource, Statics.fastbootDeployed);
            if (OSTools.isLinux() || OSTools.isMac()) {
                new FileOperations().setExecutableBit(Statics.fastbootDeployed);
            }
            Statics.log.level2Information("@fastbootDeployed");
        }
    }

    /**
     * gets the resource for Fastboot
     *
     * @return path to resource
     */
    public static String getFastbootLinuxResource() {
        String arch=OSTools.checkLinuxArch();
        
        if (arch.equals("x86_64")) {
            new Log().level3Verbose("found x86-64 bit arch");
            return Statics.fastbootLinux64;
        }
        if (arch.equals("ARMv6")) {
            new Log().level3Verbose("found ARMv6 arch");
            return Statics.fastbootLinuxARMv6;
        }
        new Log().level3Verbose("found x86-32 bit arch");
        return Statics.fastbootLinux32;
    }

    /**
     * executes fastboot
     *
     * @param line params for fastboot
     * @return value from fastboot command
     */
    public String doFastbootShellCommand(String line) {
        line = StringOperations.removeLeadingSpaces(line);
        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList<>();
        ShellCommand.add(Statics.fastbootDeployed);
        ShellCommand.addAll(new ShellTools().parseCommandLine(line));
        String StringCommand[] = StringOperations.convertArrayListToStringArray(ShellCommand);
        new Log().level3Verbose("Performing standard fastboot command" + line);
        return Shell.liveShellCommand(StringCommand, true);
    }

    /**
     * performs elevated fastboot command
     *
     * @param line params for fastboot
     * @return value from fastboot command
     */
    public String doElevatedFastbootShellCommand(String line) {
        line = StringOperations.removeLeadingSpaces(line);
        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList<>();
        ShellCommand.add(Statics.fastbootDeployed);
        ShellCommand.addAll(new ShellTools().parseCommandLine(line));
        String StringCommand[] = StringOperations.convertArrayListToStringArray(ShellCommand);
        new Log().level3Verbose("Performing elevated Fastboot command" + line);
        String returnval = Shell.elevateSimpleCommandWithMessage(StringCommand, "CASUAL uses root to work around fastboot permissions.  Hit cancel if you have setup your UDEV rules.");
        return returnval;
    }
}
