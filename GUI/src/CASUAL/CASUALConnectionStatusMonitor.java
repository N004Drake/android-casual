/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
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
                stateSwitcher(Statics.DeviceTracker.length);
            //No devices detected
            } else if ( Statics.DeviceTracker[0].isEmpty()){
                stateSwitcher(0);
                
            //One device detected
            } else if (! Statics.DeviceTracker[0].isEmpty()){
                stateSwitcher(1);
            }

            //Check and handle abnormalities
            //insufficient permissions
            if (DeviceList.contains("????????????")) {
                DeviceCheck.stop();
                Log.level0("Insufficient permissions on server detected.");
                String cmd[]={Statics.AdbDeployed,"kill-server"}; //kill the server
                Log.level1("killing server and requesting elevated permissions.");
                Shell.sendShellCommand(cmd); //send the command
                //notify user that permissions will be requested and what they are used for
                TimeOutOptionPane TimeOutOptionPane = new TimeOutOptionPane();
                String[] ok = {"ok"};
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/PermissionEscillation.wav");
                TimeOutOptionPane.showTimeoutDialog(60, null, "It would appear that this computer\n"
                        + "is not set up properly to communicate\n"
                        + "with the device.  As a work-around we\n"
                        + "will attempt to elevate permissions \n"
                        + "to access the device properly.", "Insufficient Permissions", TimeOutOptionPane.OK_OPTION, 2, ok, 0);
                DeviceList = Shell.elevateSimpleCommand(DeviceCommand);
                // if permissions elevation was sucessful
                if (!DeviceList.contains("????????????")) {
                    Log.level1(DeviceList);
                    DeviceCheck.start();
                //devices still not properly recognized.  Log it.
                } else {
                    Log.level0("Unrecognized device detected");
                    Log.level0("");
                    Log.level0("Application halted. Please restart the application.");
                    Log.level0("If you continue to experience problems, please report this issue ");
                }

            }    
        }    
    });
     

            


private void stateSwitcher(int State){
    if (LastState!=State){
        Log.level3("State Change Detected, The new state is: "+State);
        switch (State){
            case 0:
                Log.level3("State Disconnected");
                Statics.GUI.enableControls(false);
                Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceDisconnected.png", "Device Not Detected");
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/Disconnected.wav");
                break;
            case 1:
                Log.level3("State Connected");
                Statics.GUI.enableControls(true);
                Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceConnected.png", "Device Connected");
                Statics.GUI.setStatusMessageLabel("Target Acquired");
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/Connected-SystemReady.wav");
                break;
            default:
                
                if (State == 2){
                    Log.level0("Multiple devices detected. Remove " + (State-1) + " device to continue.");
                } else {
                    Log.level0("Remove " + (State-1) + " devices to continue.");
                }
                        
                Log.level3("State Multiple Devices Number of devices" + State);
                Statics.GUI.enableControls(false);
                Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/TooManyDevices.png", "Target Acquired");
                String[] URLs = {"/CASUAL/resources/sounds/"+String.valueOf(State)+".wav","/CASUAL/resources/sounds/DevicesDetected.wav"};
                CASUALAudioSystem.playMultipleInputStreams(URLs);
                break;
            
        }
        LastState=State;
    }
}
    

}

