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
    private static int LastState=0;
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
                stateSwitcher(2);
            } else if ( Statics.DeviceTracker[0].isEmpty()){
                stateSwitcher(0);
            } else if (! Statics.DeviceTracker[0].isEmpty()){
                stateSwitcher(1);
                Statics.GUI.setStatusMessageLabel("Target Acquired");



            }

            
            if (DeviceList.contains("????????????")) {
                Log.level0("Insufficient permissions on server detected.");
                String cmd[]={Statics.AdbDeployed,"kill-server"};
                Log.level1("killing server and requesting elevated permissions.");
                Shell.sendShellCommand(cmd);
                TimeOutOptionPane TimeOutOptionPane = new TimeOutOptionPane();
                String[] ok = {"ok"};
                playSound("/CASUAL/resources/sounds/PermissionEscillation.wav");
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
     
private static synchronized void playSound(final String url) {
    new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing, see comments
      public void run() {
        try {
          Clip clip = AudioSystem.getClip();
          AudioStream AS = new AudioStream( getClass().getResourceAsStream(url));
          AudioPlayer.player.start(AS);
          
        } catch (Exception e) {
          System.err.println(e.getMessage());
        }
      }
    }).start();
  }

private static void stateSwitcher(int State){
    if (LastState!=State){
        switch (State){
            case 0:
                Statics.GUI.setStatusMessageLabel("Device Not Detected");
                break;
            case 1:
                Statics.GUI.setStatusMessageLabel("Target Acquired");
                playSound ("/CASUAL/resources/sounds/Connected-SystemReady.wav");
                break;
            case 2:
                Statics.GUI.setStatusMessageLabel("Multiple Devices");
                break;
            case 3:
                Statics.GUI.setStatusMessageLabel("WTF did you do???");
        }
        LastState=State;
    }
}
    

}

