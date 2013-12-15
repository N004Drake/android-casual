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

import CASUAL.CommunicationsTools.ADB.ADBTools;
import CASUAL.caspac.Caspac;
import CASUAL.misc.MandatoryThread;
import CASUAL.network.Pastebin;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.CodeSource;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

/**
 * provides a place for the main thread to break out into different modes.
 * @author AdamOuler
 */
public final class CASUALMain {

    String password = "";
    File caspacLocation;
    boolean exitWhenDone = false;
    boolean execute = false;
    static boolean useGUI = false;
    //TODO: convert android-casual to Maven so it works better cross-platform

    /**
     * the default package used for IDE mode or if no scripts are found
     */
    final public static String defaultPackage = "TestScript"; //note this will be used for IDE only.
    final private static boolean useOverrideArgs = false; // this will use overrideArguments.
    final private static String[] overrideArguments = new String[]{""};
    static String[] arguments;

    /**
     * Main method launching the application.
     *
     * @param args
     */
    public static void main(String[] args) {
        arguments = args;
        //Initialize statics
        Statics.initializeStatics();
        //Override args for test modes
        if (useOverrideArgs) {
            args = overrideArguments;
        }
        beginCASUAL(args);
    }

    /**
     * Begins actual CASUAL modes this can be called as a reset for CASUAL
     * without losing args[] in case of a problem
     *
     * @param args
     */
    public static void beginCASUAL(String[] args) {
        String CASUALFileName = new File(new CASUALMain().getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).toString();
        String CASUALSVNRevision = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision");
        String CASUALBuildNumber = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.buildnumber");
        boolean shutdown = checkModeSwitchArgs(args);
        new Log().level2Information("CASUAL Cross-platform Android Scripting and Unified Auxiliary Loader\nRevision:" + CASUALSVNRevision + " build:" + CASUALBuildNumber + "\n"
                + "    CASUAL  Copyright (C) 2013  Adam Outler\n"
                + "    This program comes with ABSOLUTELY NO WARRANTY.  This is free software,\n"
                + "    and you are welcome to redistribute it, under certain conditions; run\n"
                + "    '" + CASUALFileName + " --license'\n"
                + "    for details. http://android-casual.googlecode.com for source."
                + "Logging:" + Statics.getTempFolder() + "\nSystem: " + System.getProperty("os.name"));

        if (!shutdown) { //shutdown may be commanded by modeswitch
            new CASUALMain().startup(args);
        }
    }

    /**
     * checkModeSwitchArgs is a primary switch before any real actions happen.
     * Here we check for switches that will either change the mode of CASUAL or
     * display something quick and exit.
     *
     * @param args
     * @return true if shutdown is commanded;
     */
    private static boolean checkModeSwitchArgs(String args[]) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("--temp") || args[i].contains("-t")) {
                i++;
                Statics.setTempFolder(args[i]);

            }
            if (args[i].equals("--help") || args[i].equals("-v?")) {
                new Log().level2Information("\n"
                        + " Usage: casual.jar [optional parameters]\n"
                        + " without arguments - Launch the GUI\n"
                        + " [--help] shows this message and exits\n"
                        + " [--license] -shows license and exits\n"
                        + " [--execute/-e \"command\"]-executes any CASUAL command and exits. Launch CASUAL GUI to read about commands"
                        + " [--caspac/-c path_to" + Statics.Slash + "CASPACzip] -launches CASUAL with a CASPAC"
                        + " [--gui/-g)] - performs actions with a GUI\n");

                shutdown(0);
                return true;
            }
            if (args[i].equals("--license")) {
                new Log().level2Information("\n"
                        + "    This program is free software: you can redistribute it and/or modify\n"
                        + "    it under the terms of the GNU General Public License as published by\n"
                        + "    the Free Software Foundation, either version 3 of the License, or\n"
                        + "    (at your option) any later version.\n"
                        + "\n"
                        + "    This program is distributed in the hope that it will be useful,\n"
                        + "    but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
                        + "    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
                        + "    GNU General Public License for more details.");
                shutdown(0);
                return true;
            }

        }
        return false;
    }

    /**
     * shuts down CASUAL
     *
     * @param i code to throw
     */
    public static void shutdown(int i) {
        new Log().level4Debug("Shutting Down");
        AudioHandler.useSound = false;
        Log.out.flush();
        if (Statics.CASPAC != null && Statics.CASPAC.getActiveScript() != null) {
            Statics.CASPAC.getActiveScript().scriptContinue = false;
        }
        CASUALConnectionStatusMonitor.reset();
        

        new ADBTools().shutdown();
        //No logs if Developing, No GUI, or CASPAC.  Only if CASUAL distribution.
        if (!CASUALTools.IDEMode && !Statics.isGUIIsAvailable() && Statics.CASPAC.type != 0) {
            try {
                new Pastebin().pasteAnonymousLog();
            } catch (MalformedURLException ex) {
                new Log().errorHandler(ex);
            }
        }

        if (Statics.GUI != null) {
            Statics.GUI.dispose();
        }

        Statics.initializeStatics();

    }

    private static void doSleep(){
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(CASUALMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void doArgsCheck(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("--password") || args[i].contains("-p")) {
                password = args[++i];
            }
            if (args[i].contains("--caspac") || args[i].contains("-c") || args[i].contains("--CASPAC") || args[i].contains("-CASPAC")) {
                if (new File(args[++i]).exists()) {
                    caspacLocation = new File(args[i]);
                    new Log().level4Debug("Setting CASPAC location to " + caspacLocation.getAbsolutePath());
                } else {
                    new Log().level0Error("@fileNotFound");
                    return;
                }

            }
            if (args[i].contains("--gui") || args[i].contains("-g")) {
                useGUI = true;
            }
            if (args[i].contains("--nosound") || args[i].contains("-n")) {
                AudioHandler.useSound = false;
            }
            if (args[i].contains("--execute") || args[i].contains("-e")) {
                execute = true;
                i++;
            }

        }

    }

    /**
     * startup is where CASUAL starts its normal routines for both
     *
     * @param args command line args
     */
    public void startup(String[] args) {
        //starts the scriptRunLock so that the lock will not be enabled when checked for the first time. 
        Locks.scriptRunLock.start();
        //make the temp folder
        if (Statics.getTempFolder() == null) {
            Statics.setTempFolder(Statics.getTempFolder());
        }

        new FileOperations().makeFolder(Statics.getTempFolder());

        //parse args
        if (args.length > 0) {
            doArgsCheck(args);
        } else {
            useGUI = true;
        }
        //prepare the CASPAC, locate, unzip and ready it.
        prepareCaspac();
        //start the GUI if required

        Locks.startGUI=new Thread(new CASUALTools().GUI);
        Locks.startGUI.setName("CASUAL GUI");
        Locks.startGUI.start();//starts the GUI if required
        
        //deploy ADB
        Locks.startADB = startADB();


        try {
            Locks.caspacPrepLock.waitFor();
            //if not a single commmand, then load up the active script
            if (!execute) {
                if (Statics.CASPAC != null && Statics.CASPAC.scripts != null && Statics.CASPAC.scripts.size() >= 1) {
                    new Log().level4Debug("Finalizing active script up to be run");
                    //TODO set Active Script in CASPAC here
                    Statics.CASPAC.setActiveScript(Statics.CASPAC.scripts.get(0));
                    Statics.CASPAC.getActiveScript().scriptContinue = true;
                }
            }
            if (args.length != 0 && !useGUI) {
                //Using command line mode
                Statics.setStatus("waiting for ADB");
                Locks.startADB.waitFor(); //wait for adb deployment

                //start the device monitor
                //wait for complete;
                if (execute) {
                    doConsoleStartup(args);
                } else {
                    ADBTools.adbMonitor(false);
                    Statics.CASPAC.waitForUnzipComplete();

                    new CASUALScriptParser().executeFirstScriptInCASPAC(Statics.CASPAC);
                    shutdown(0);
                }  //use command line args
            } else {
                //using GUI mode
                if (useGUI||Statics.GUI.isDummyGUI()) {
                    Locks.startGUI.join();
                    Statics.GUI.notificationCASUALSound();
                }
                
                Statics.GUI.setCASPAC(Statics.CASPAC);
                ADBTools.adbMonitor(true);
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
                ADBTools.adbMonitor(false);
                CASUALScriptParser csp = new CASUALScriptParser();
                csp.executeOneShotCommand(args[i]);
                Statics.setStatus("Complete");
                new Log().level2Information("@scriptComplete");
            } else {
                Statics.setStatus("Invalid CASUAL Startup argument " + args[i]);
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

            if (caspacLocation != null && caspacLocation.exists()) {
                try {
                    Caspac cp;
                    if (caspacLocation != null && caspacLocation.exists() && !password.isEmpty()) {
                        cp = new Caspac(caspacLocation, Statics.getTempFolder(), 0, password.toCharArray());
                        password = "";
                    } else if (caspacLocation != null && caspacLocation.exists()) {
                        cp = new Caspac(caspacLocation, Statics.getTempFolder(), 0);

                    } else {
                        new Log().level4Debug("exiting setupCASUALCASPAC().  Nothing to be done");
                        return;
                    }
                    cp.loadFirstScriptFromCASPAC();
                    Statics.CASPAC = cp;
                } catch (IOException ex) {
                    new Log().errorHandler(ex);
                } catch (Exception ex) {
                    new Log().errorHandler(ex);
                }

            } else if (!execute) {   //execute is for single commands
                //Build a CASPAC from the SCRIPTS folder
                CodeSource src = getClass().getProtectionDomain().getCodeSource();
                Caspac cp;
                try {
                    cp = new Caspac(src, Statics.getTempFolder(), 1);

                    //cp.load();
                    Statics.CASPAC = cp;
                } catch (ZipException ex) {
                    new Log().errorHandler(ex);
                } catch (IOException ex) {
                    new Log().errorHandler(ex);
                }
            }
        }
    };

    /**
     * Starts a Thread responsible for deploying and starting ADB.
     * @return a reference to the startADBThread
     */
    public MandatoryThread startADB() {
        MandatoryThread adb = new MandatoryThread(new CASUALTools().adbDeployment);
        adb.setName("ADB Deployment");
        adb.start(); //start ADB deployment
        return adb;
    }

    /**
     * starts preparing the CASPAC provided. 
     */
    public void prepareCaspac() {
        Locks.caspacPrepLock = new MandatoryThread(setupCASUALCASPAC);
        Locks.caspacPrepLock.setName("Preparing Scripts");
        Locks.caspacPrepLock.start(); //scan self for embedded scripts
    }
}
