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
     Log Log=new Log();
      Shell Shell=new Shell();
     public final static int ONE_SECOND = 1000;
     Timer DeviceCheck = new Timer(ONE_SECOND, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            String DeviceCommand[]={Statics.AdbDeployed,"devices"};
            String DeviceList=Shell.silentShellCommand(DeviceCommand).replace("List of devices attached \n","").replace("\n","").replace("\t","");
            Statics.DeviceTracker=DeviceList.split("device");
            
            //TODO: Remove for test

            if (Statics.DeviceTracker.length>1){
                Log.level0("Multiple devices detected");
                Statics.GUI.setStatusMessageLabel("Multiple Devices");
            } else if ( Statics.DeviceTracker[0].isEmpty()){
                Statics.GUI.setStatusMessageLabel("Device Not Detected");
            } else if (! Statics.DeviceTracker[0].isEmpty()){
                Statics.GUI.setStatusMessageLabel("Target Acquired");


            }

            
            if (DeviceList.contains("????????????")) {
                Log.level0("Insufficient permissions on server detected.");
                String cmd[]={Statics.AdbDeployed,"kill-server"};
                Log.level1("killing server and requesting elevated permissions.");
                Shell.sendShellCommand(cmd);
                TimeOutOptionPane TimeOutOptionPane = new TimeOutOptionPane();
                String[] ok = {"ok"};
                TimeOutOptionPane.showTimeoutDialog(60, null, "It would appear that this computer\n"
                        + "is not set up properly to communicate\n"
                        + "with the device.  As a work-around we\n"
                        + "will attempt to elevate permissions \n"
                        + "to access the device properly.", "Insufficient Permissions", TimeOutOptionPane.OK_OPTION, 2, ok, 0);
                DeviceList = Shell.elevateSimpleCommand(DeviceCommand);
                if (!DeviceList.contains("????????????")) {
                    Log.level1(DeviceList);
                } else {
                    Log.level0("Unrecognized device detected");
                }

            }    
        }    
    });
}
