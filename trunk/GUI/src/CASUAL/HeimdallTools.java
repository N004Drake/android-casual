/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        Timer connectionTimer = new Timer(90000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new CASUALUserInteraction().showTimeoutDialog(60, null, "It would appear that the connected device is not recognized.\n"
                        + "The device should be in download mode.. Is it?.\n\n"
                        + "If it's download mode, use a different USB port.\n"
                        + "Don't use a USB hub.  Also, the USB ports behind\n"
                        + "the computer are better than the front.\n",
                        "I don't see the device", CASUALUserInteraction.OK_OPTION, 2, new String[]{"I did it"}, 0);
            }
        });
        connectionTimer.start();
        //Start timer  wait(90000) and recommend changing USB ports
        while (!shellReturn.contains("Device detected")) {
            shellReturn = Shell.silentShellCommand(stringCommand);
        }
        connectionTimer.stop();
        log.Level1Interaction("detected!");
    }

    public String doElevatedHeimdallShellCommand(String Line) {
        Line = StringOperations.removeLeadingSpaces(Line);
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.addAll(new ShellTools().parseCommandLine(Line));
        String stringCommand2[] = StringOperations.convertArrayListToStringArray(shellCommand);
        String returnval = Shell.elevateSimpleCommandWithMessage(stringCommand2, "CASUAL uses root to work around Heimdall permissions.  Hit cancel if you have setup your UDEV rules.");
        return returnval;
    }

    public String doHeimdallShellCommand(String Line) {
        Line = StringOperations.removeLeadingSpaces(Line);
        Shell Shell = new Shell();
        ArrayList<String> shellCommand = new ArrayList();
        shellCommand.add(Statics.heimdallDeployed);
        shellCommand.addAll(new ShellTools().parseCommandLine(Line));
        String stringCommand2[] = StringOperations.convertArrayListToStringArray(shellCommand);
        Statics.ExectingHeimdallCommand = true;
        String returnRead = Shell.liveShellCommand(stringCommand2);
        if (returnRead.contains("libusb error: -3") && Statics.isLinux()) {
            log.level0Error("#A permissions error was detected.  Elevating permissions.");
            this.doElevatedHeimdallShellCommand(Line);
        }
        Statics.ExectingHeimdallCommand = false;
        return returnRead;
    }
}
