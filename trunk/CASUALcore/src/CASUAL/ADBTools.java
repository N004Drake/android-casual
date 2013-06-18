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
        return new String[]{Statics.AdbDeployed, "devices"}; 
    };
    public static String[] startServer(){
        return new String[] {Statics.AdbDeployed, "start-server"};
    }
    public static String[] killServer(){
        return new String[]{Statics.AdbDeployed, "kill-server"};
    }
    
    public void restartADBserverSlowly() {
        try {
            log.level3Verbose("Restarting ADB slowly");
            Shell shell = new Shell();
            shell.timeOutShellCommand(killServer(),500);
            sleepForMillis(1000);
            shell.timeOutShellCommand(devicesCmd(), 3000);
        } catch (TimeoutException ex) {
            //Do nothing  This is windows being a PITA and not returning. 
        }
    }

    public void elevateADBserver() {
        log.level3Verbose("Restarting ADB");
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
            log.level0Error("Permissions problem detected. Killing and requesting permissions escillation.");
            shell.silentShellCommand(new String[]{Statics.AdbDeployed, "kill-server"});
            shell.elevateSimpleCommandWithMessage(devicesCmd(), "Device permissions problem detected");
        }

        if (DeviceList.contains("ELFCLASS64") && DeviceList.contains("wrong ELF")) {
            new CASUALInteraction("ELFCLASS64 error!", "Could not execute ADB. 'Wrong ELF class' error\n"
                    + "This can be resolved by installation of ia32-libs"
                    + "eg.. sudo apt-get install ia32-libs\n"
                    + "ie.. sudo YourPackageManger install ia32-libs").showInformationMessage();

        }

        //TODO: implement this as an error handler for ADB. in a centralized manner. 
        log.level4Debug("Device List:" + DeviceList);
        if (DeviceList.contains("????????????") || DeviceList.contains("**************") || DeviceList.contains("error: cannot connect to daemon")) {
            log.level4Debug(" Device list: " + DeviceList + " Restarting server slowly");
            restartADBserverSlowly();
            DeviceList = new Shell().silentShellCommand(devicesCmd()).replace("List of devices attached \n", "").replace("\n", "").replace("\t", "");
            if (!Statics.isWindows() && DeviceList.contains("????????????") || DeviceList.contains("**************") || DeviceList.contains("error: cannot connect to daemon")) {
                log.level4Debug(" Device list: " + DeviceList + " \n Elevated Permissions Required to properly start ADB server");
                killADBserver();
                elevateADBserver();
            }
        }
    }
    private void sleepForMillis(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(ADBInstall.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
