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

    public String doFastbootShellCommand(String Line) {
        Line = StringOperations.removeLeadingSpaces(Line);

        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList();
        ShellCommand.add(Statics.fastbootDeployed);
        ShellCommand.addAll(new ShellTools().parseCommandLine(Line));
        String StringCommand[] = (StringOperations.convertArrayListToStringArray(ShellCommand));
        return Shell.liveShellCommand(StringCommand);
    }

    public String doElevatedFastbootShellCommand(String Line) {
        Line = StringOperations.removeLeadingSpaces(Line);
        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList();
        ShellCommand.add(Statics.fastbootDeployed);
        ShellCommand.addAll(new ShellTools().parseCommandLine(Line));
        String StringCommand[] = (StringOperations.convertArrayListToStringArray(ShellCommand));
        String returnval = Shell.elevateSimpleCommandWithMessage(StringCommand, "CASUAL uses root to work around fastboot permissions.  Hit cancel if you have setup your UDEV rules.");
        return returnval;
    }
}
