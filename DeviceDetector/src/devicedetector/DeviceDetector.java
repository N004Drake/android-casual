/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicedetector;

import CASUAL.FileOperations;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author loganludington
 */
public class DeviceDetector {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DeviceDetector DD = new DeviceDetector();
        DD.run(args);
        
    }
    public void run(String[] args) {
        System.out.println(CASUAL.ADBTools.getADBCommand());
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {  
            @Override
            public void run() {
                    FileOperations FO = new CASUAL.FileOperations();
                    FO.recursiveDelete(CASUAL.Statics.getTempFolder());
                }  
        }));
        try {
            Process p = Runtime.getRuntime().exec("ls -la");
            BufferedReader read = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
        } catch (IOException ex) {
            Logger.getLogger(DeviceDetector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
