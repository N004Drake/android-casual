/*HeimdallTools provides tools for use with Heimdall
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;

/**
 *
 * @author adam
 */
public class HeimdallTools {

    Log log = new Log();

    public void doHeimdallWaitForDevice() {
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.add("detect");
        String stringCommand[] = (StringOperations.convertArrayListToStringArray(shellCommand));
        log.progress("Waiting for Downoad Mode device.");
        String shellReturn = "";
        Timer connectionTimer = new Timer(60000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new CASUALInteraction().showTimeoutDialog(60, null, "It would appear that the connected device is not recognized.\n"
                        + "The device should be in download mode.. Is it?.\n\n"
                        + "If it's download mode, use a different USB port.\n"
                        + "Don't use a USB hub.  Also, the USB ports behind\n"
                        + "the computer are better than the front.\n",
                        "I don't see the device", CASUALInteraction.OK_OPTION, 2, new String[]{"I did it"}, 0);
            }
        });
        connectionTimer.start();
        //Start timer  wait(90000) and recommend changing USB ports
        while (!shellReturn.contains("Device detected")) {
            log.progress(".");
            sleepForOneSecond();
            shellReturn = Shell.silentShellCommand(stringCommand);
        }
        connectionTimer.stop();
        log.level3Verbose("detected!");
    }

    public String doElevatedHeimdallShellCommand(String line) {
        line = StringOperations.removeLeadingSpaces(line);
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.addAll(new ShellTools().parseCommandLine(line));
        log.level3Verbose("Performing elevated Heimdall command" + line);
        String stringCommand2[] = StringOperations.convertArrayListToStringArray(shellCommand);
        String returnval = Shell.elevateSimpleCommandWithMessage(stringCommand2, "CASUAL uses root to work around Heimdall permissions.  Hit cancel if you have setup your UDEV rules.");
        return returnval;
    }

    public String doHeimdallShellCommand(String line) {
        line = StringOperations.removeLeadingSpaces(line);
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.addAll(new ShellTools().parseCommandLine(line));
        String stringCommand2[] = StringOperations.convertArrayListToStringArray(shellCommand);
        Statics.ExectingHeimdallCommand = true;
        log.level3Verbose("Performing standard Heimdall command" + line);
        String returnRead = Shell.liveShellCommand(stringCommand2, true);
        if (returnRead.contains("libusb error: -3") && Statics.isLinux()) {
            log.level0Error("#A permissions error was detected.  Elevating permissions.");
            this.doElevatedHeimdallShellCommand(line);
        }

        Statics.ExectingHeimdallCommand = false;
        return returnRead;
    }

    private void sleepForOneSecond() {
        try {
            Thread.sleep(1000);
            log.progress(".");
        } catch (InterruptedException ex) {
            log.errorHandler(ex);
        }
    }
}
