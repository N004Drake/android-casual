/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Timer;
import sun.audio.*;
/**
 *
 * @author Adam Outler adamoutler@gmail.com
 */


public class CASUALConnectionStatusMonitor {
    CASUALAudioSystem CAS=new CASUALAudioSystem();
    private static int LastState=0;
    Log Log=new Log();
      Shell Shell=new Shell();
     public final static int ONE_SECOND = 1000;
     Timer DeviceCheck = new Timer(ONE_SECOND, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            
            //execute adb devices and filter
            String DeviceCommand[]={Statics.AdbDeployed,"devices"};
            String DeviceList=Shell.silentShellCommand(DeviceCommand).replace("List of devices attached \n","").replace("\n","").replace("\t","");
            Statics.DeviceTracker=DeviceList.split("device");
            
            //Multiple devices detected
            if (Statics.DeviceTracker.length>1){
                Log.level0("Multiple devices detected");
                stateSwitcher(Statics.DeviceTracker.length);
            //No devices detected
            } else if ( Statics.DeviceTracker[0].isEmpty()){
                stateSwitcher(0);
            //One device detected
            } else if (! Statics.DeviceTracker[0].isEmpty()){
                stateSwitcher(1);
                Statics.GUI.setStatusMessageLabel("Target Acquired");



            }

            //Check and handle abnormalities
            //insufficient permissions
            if (DeviceList.contains("????????????")) {
                Log.level0("Insufficient permissions on server detected.");
                String cmd[]={Statics.AdbDeployed,"kill-server"}; //kill the server
                Log.level1("killing server and requesting elevated permissions.");
                Shell.sendShellCommand(cmd); //send the command
                //notify user that permissions will be requested and what they are used for
                TimeOutOptionPane TimeOutOptionPane = new TimeOutOptionPane();
                String[] ok = {"ok"};
                CAS.playSound("/CASUAL/resources/sounds/PermissionEscillation.wav");
                TimeOutOptionPane.showTimeoutDialog(60, null, "It would appear that this computer\n"
                        + "is not set up properly to communicate\n"
                        + "with the device.  As a work-around we\n"
                        + "will attempt to elevate permissions \n"
                        + "to access the device properly.", "Insufficient Permissions", TimeOutOptionPane.OK_OPTION, 2, ok, 0);
                DeviceList = Shell.elevateSimpleCommand(DeviceCommand);
                // if permissions elevation was sucessful
                if (!DeviceList.contains("????????????")) {
                    Log.level1(DeviceList);
                //devices still not properly recognized.  Log it.
                } else {
                    Log.level0("Unrecognized device detected");
                }

            }    
        }    
    });
     

            


private void stateSwitcher(int State){
    if (LastState!=State){
        switch (State){
            case 0:
                Statics.GUI.setStatusMessageLabel("Device Not Detected");
                CAS.playSound("/CASUAL/resources/sounds/disconnected.wav");
                break;
            case 1:
                Statics.GUI.setStatusMessageLabel("Target Acquired");
                CAS.playSound("/CASUAL/resources/sounds/Connected-SystemReady.wav");
                break;
            default:
                String[] URLs = {"/CASUAL/resources/sounds/"+String.valueOf(State)+".wav","/CASUAL/resources/sounds/devicesdetected.wav"};
                CAS.playMultipleInputStreams(URLs);

                Statics.GUI.setStatusMessageLabel("Multiple Devices");
                
                break;
            
        }
        LastState=State;
    }
}
    

}

