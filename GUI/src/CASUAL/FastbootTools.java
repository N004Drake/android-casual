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

import java.util.ArrayList;

/**
 *
 * @author adam
 */
public class FastbootTools {

    public String doFastbootShellCommand(String line) {
        line = StringOperations.removeLeadingSpaces(line);

        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList();
        ShellCommand.add(Statics.fastbootDeployed);
        ShellCommand.addAll(new ShellTools().parseCommandLine(line));
        String StringCommand[] = (StringOperations.convertArrayListToStringArray(ShellCommand));
        new Log().level3Verbose("Performing elevated Fastboot command" + line);
        return Shell.liveShellCommand(StringCommand,true);
    }

    public String doElevatedFastbootShellCommand(String line) {
        line = StringOperations.removeLeadingSpaces(line);
        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList();
        ShellCommand.add(Statics.fastbootDeployed);
        ShellCommand.addAll(new ShellTools().parseCommandLine(line));
        String StringCommand[] = (StringOperations.convertArrayListToStringArray(ShellCommand));
        new Log().level3Verbose("Performing elevated Fastboot command" + line);
        String returnval = Shell.elevateSimpleCommandWithMessage(StringCommand, "CASUAL uses root to work around fastboot permissions.  Hit cancel if you have setup your UDEV rules.");
        return returnval;
    }
}
