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
import java.io.File;
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


    String password;
    File caspacLocation;
    boolean exitWhenDone=false;
    boolean execute=false;
    boolean useGUI=false;
    private void doArgsCheck(String[] args){
        for (int i=0; i<args.length; i++){
            if (args[i].contains("--password") || args[i].contains("-p")) {
                password = args[++i];
            }
            if (args[i].contains("--caspac") || args[i].contains("-c") || args[i].contains("--CASPAC") || args[i].contains("-CASPAC")) {
               if (new File(args[++i]).exists()){
                   caspacLocation=new File(args[i]);
                   new Log().level4Debug("Setting CASPAC location to "+caspacLocation.getAbsolutePath());
               } else {
                   new Log().level0Error("@fileNotFound");
                   return;
               }
               
            }
            if (args[i].contains("--gui") || args[i].contains("-g")) {
                useGUI = true;
            }
            if (args[i].contains("--nosound") || args[i].contains("-n")) {
                AudioHandler.useSound=false;
            }
            if (args[i].contains("--execute") || args[i].contains("-e")) {
               execute=true;
               i++;
            }

        }
        
        
        
    }
    /**
     * startup is where CASUAL starts its normal routines for both
     *
     * @param cmd
     */
    public void startup(String[] args) {
        //make the temp folder
        if (Statics.getTempFolder()==null)Statics.setTempFolder(Statics.getTempFolder());
        
        new FileOperations().makeFolder(Statics.getTempFolder());

        //parse args
        if (args.length>0){
            doArgsCheck(args);
        }  else {
            useGUI=true;
        }
        //prepare the CASPAC
        Thread prepCASPAC = prepareCaspac();

        //start the GUI if required
        Thread startGUI = null;
        startGUI = startGUI(startGUI);//starts the GUI if required
        
        //deploy ADB
        Thread adb = startADB();
        Statics.lockGUIformPrep = true;
                         
        try {
            prepCASPAC.join();
            //if not a single commmand, then load up the active script
            if (!execute ){
                
                Statics.CASPAC.setActiveScript(Statics.CASPAC.scripts.get(0));
                Statics.CASPAC.getActiveScript().scriptContinue = true;
            }
            if (args.length != 0 && !useGUI) {
                //Using command line mode
                Statics.setStatus("waiting for ADB");
                adb.join(); //wait for adb deployment
                
                //start the device monitor
                CASUALConnectionStatusMonitor.DeviceCheck.start();
                
                //wait for complete;
                
                if (execute ){
                    doConsoleStartup(args);
                } else {
                    CASUALConnectionStatusMonitor.DeviceCheck.stop();
                    Statics.CASPAC.waitForUnzipComplete();
                    
                    new CASUALScriptParser().executeFirstScriptInCASPAC(Statics.CASPAC);
                    CASUALApp.shutdown(0);
                }  //use command line args
            } else {
                AudioHandler.playSound("/CASUAL/resources/sounds/CASUAL.wav");
                //using GUI mode
                if (startGUI != null) {
                    startGUI.join();
                }
                Statics.GUI.setCASPAC(Statics.CASPAC);
            }
        } catch (InterruptedException ex) {
            new Log().errorHandler(ex);

        }
    }

    private void doConsoleStartup(String[] args) {
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
                Statics.setStatus("Invalid CASUAL Startup argument "+args[i]);
                new Log().level0Error("@unrecognizedCommand");
            }

        }
        //   CASUALApp.shutdown(0);
    }
    /**
     * Scans /SCRIPTS/ Folder to locate scripts.
     */
    public Runnable setupCASUALCASPAC = new Runnable() {
        @Override
        public void run() {
            
            if (caspacLocation!=null){
                try {
                    Caspac cp;
                    if (caspacLocation !=null && caspacLocation.exists()&& !password.isEmpty()){
                        cp=new Caspac(caspacLocation,Statics.getTempFolder(),0,password.toCharArray());
                        password="";
                    } else {
                        cp=new Caspac(caspacLocation,Statics.getTempFolder(),0);
                    
                    }
                    cp.loadFirstScriptFromCASPAC();
                    Statics.CASPAC=cp;
                } catch (IOException ex) {
                    new Log().errorHandler(ex);
                } catch (Exception ex) {
                    Logger.getLogger(CASUALMain.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } else if (!execute) {   //execute is for single commands
                //Build a CASPAC from the SCRIPTS folder
                CodeSource src = CASUAL.CASUALApp.class.getProtectionDomain().getCodeSource();
                Caspac cp;
                try {
                    new Log().level4Debug("codesource: " + src.toString());
                    cp = new Caspac(src, Statics.getTempFolder(), 1);

                    //cp.load();
                    Statics.CASPAC = cp;
                } catch (ZipException ex) {
                    Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };

    public Thread startGUI(Thread startGUI) {
        if (useGUI|| Statics.dumbTerminalGUI) {
            startGUI = new Thread(new CASUALTools().GUI);
            startGUI.setName("CASUAL GUI");
            Statics.setStatus("launching GUI");
            startGUI.start();
            CASUALConnectionStatusMonitor.DeviceCheck.start();
        }
        return startGUI;
    }

    public Thread startADB() {
        Thread adb = new Thread(new CASUALTools().adbDeployment);
        adb.setName("ADB Deployment");
        adb.start(); //start ADB deployment
        return adb;
    }

    public Thread prepareCaspac() {
        Thread prepCASPAC = new Thread(setupCASUALCASPAC);
        prepCASPAC.setName("Preparing Scripts");
        prepCASPAC.start(); //scan self for embedded scripts
        return prepCASPAC;
    }
}