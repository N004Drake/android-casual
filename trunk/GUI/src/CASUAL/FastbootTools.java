/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
