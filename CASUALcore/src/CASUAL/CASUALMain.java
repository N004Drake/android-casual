/*CASUALMain provides a place for the main thread to break out into different modes.
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


import CASUAL.caspac.Caspac;
import java.io.IOException;
import java.security.CodeSource;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

/**
 *
 * @author adam
 */
public final class CASUALMain {
    String[] args;
    
    /**
     * startup is where CASUAL starts its normal routines for both 
     * @param cmd 
     */
    public void startup(String[] cmd) {
        args = cmd;
        //Build Caspac
        
        CodeSource Src = CASUAL.CASUALApp.class.getProtectionDomain().getCodeSource();
        Thread startGUI = null;
        if (Statics.useGUI = true) {
            startGUI = new Thread(new CASUALTools().GUI);
            startGUI.setName("CASUAL GUI");
            Statics.setStatus("launching GUI");
            startGUI.start();
            CASUALConnectionStatusMonitor.DeviceCheck.start();
        }
        
        new FileOperations().makeFolder(Statics.TempFolder);
        Thread adb = new Thread(new CASUALTools().adbDeployment);
        adb.setName("ADB Deployment");
        adb.start(); //start ADB deployment
        Statics.lockGUIformPrep = true;
        Thread prepCASPAC = new Thread(setupCASUALCASPAC);
        prepCASPAC.setName("Preparing Scripts");
        prepCASPAC.start(); //scan self for embedded scripts
        
        
        try {

            prepCASPAC.join();
            Caspac CASPAC=Statics.CASPAC;
            try {
                CASPAC.loadSelectedScript(CASPAC.scripts.get(0));
            } catch (ZipException ex) {
                new Log().errorHandler(ex);
            } catch (IOException ex) {
                new Log().errorHandler(ex);
            }
            AudioHandler.playSound("/CASUAL/resources/sounds/CASUAL.wav");
            if (args.length != 0 && !Statics.useGUI) {
                Statics.setStatus("waiting for ADB");
                adb.join(); //wait for adb deployment
                CASUALConnectionStatusMonitor.DeviceCheck.start();
                Statics.setStatus("Preparing scripts");
                prepCASPAC.join(); //wait for embedded scripts scan
                doConsoleStartup();  //use command line args
            } else {
                if (startGUI!=null) startGUI.join();
                    Statics.GUI.setCASPAC(CASPAC);
            }
        } catch (InterruptedException ex) {
            new Log().errorHandler(ex);
        }
    }

    private void doConsoleStartup() {
        for (int i = 0; i < args.length; i++) {
            Statics.setStatus("parsing");
            if (args[i].contains("--execute") || args[i].contains("-e")) {
                i++;
                CASUALConnectionStatusMonitor.DeviceCheck.stop();
                CASUALScriptParser csp = new CASUALScriptParser();
                csp.executeOneShotCommand(args[i]);
                Statics.setStatus("Complete");
                new Log().level2Information("@scriptComplete");
            } else {
                Statics.setStatus("Invalid command");
                new Log().level0Error("@unrecognizedCommand");
            }

        }
        //   CASUALApp.shutdown(0);
    }

    private void doGUIStartup() {
        Thread startGUI = new Thread(new CASUALTools().GUI);
        startGUI.setName("CASUAL GUI");
        Statics.setStatus("launching GUI");
        startGUI.start();
    }
     /**
     * Scans /SCRIPTS/ Folder to locate scripts.
     */
    public Runnable setupCASUALCASPAC = new Runnable() {
        @Override
        public void run() {
            //Build a CASPAC from the SCRIPTS folder
            CodeSource src = CASUAL.CASUALApp.class.getProtectionDomain().getCodeSource();
            Caspac cp;
            try {
                cp=new Caspac(src,Statics.TempFolder,1);
                
                //cp.load();
                Statics.CASPAC=cp;
            } catch (ZipException ex) {
                Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
}