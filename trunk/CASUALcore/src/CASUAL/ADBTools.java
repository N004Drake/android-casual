/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.awt.HeadlessException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public class ADBTools {
    Log log=new Log();
    public static String[] devicesCmd(){
        return new String[]{Statics.adbDeployed, "devices"}; 
    };
    public static String[] startServer(){
        return new String[] {Statics.adbDeployed, "start-server"};
    }
    public static String[] killServer(){
        return new String[]{Statics.adbDeployed, "kill-server"};
    }
    
    public void restartADBserverSlowly() {
        try {
            log.level3Verbose("@restartingADBSlowly");
            Shell shell = new Shell();
            shell.timeoutShellCommand(killServer(),500);
            sleepForMillis(1000);
            shell.timeoutShellCommand(devicesCmd(), 3000);
        } catch (TimeoutException ex) {
            //Do nothing  This is windows being a PITA and not returning. 
        }
    }

    public void elevateADBserver() {
        log.level3Verbose("@restartingADB");
        Shell shell = new Shell();
        shell.silentShellCommand(killServer());
        shell.elevateSimpleCommand(devicesCmd());
    }

    public void killADBserver() {
        log.level3Verbose("Restarting ADB after system update");
        Shell shell = new Shell();

        shell.silentShellCommand(killServer());
    }
    


    public void checkADBerrorMessages(String DeviceList) throws HeadlessException {
        
        if ((Statics.isLinux()) && (DeviceList.contains("something about UDEV rules"))) { //Don't know how to handle this yet
            //handle add udevrule
        }

        //handle libusb -3
        if ((Statics.isLinux()) && (DeviceList.contains("ERROR-3"))) { //Don't know how to handle this yet
            Shell shell = new Shell();
            log.level0Error("@permissionsElevationRequired");
            shell.silentShellCommand(new String[]{Statics.adbDeployed, "kill-server"});
            shell.elevateSimpleCommandWithMessage(devicesCmd(), "Device permissions problem detected");
        }

        if (DeviceList.contains("ELFCLASS64") && DeviceList.contains("wrong ELF")) {
            new CASUALInteraction("@interactionELFCLASS64Error").showInformationMessage();

        }

        //TODO: implement this as an error handler for ADB. in a centralized manner. 
        if (DeviceList.contains("????????????") || DeviceList.contains("**************") || DeviceList.contains("error: cannot connect to daemon")) {
            log.level4Debug("Restarting ADB slowly");
            restartADBserverSlowly();
            DeviceList = new Shell().silentShellCommand(devicesCmd()).replace("List of devices attached \n", "").replace("\n", "").replace("\t", "");
            if (!Statics.isWindows() && DeviceList.contains("????????????") || DeviceList.contains("**************") || DeviceList.contains("error: cannot connect to daemon")) {
                log.level4Debug("Permissions problem detected. Requesting CASUAL permissions escillation.");
                killADBserver();
                elevateADBserver();
            }
        }
    }
    private void sleepForMillis(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            //no need to handle this
        }
    }
}
