/*CASUALApp launches CASUAL.
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class of the application.
 */
public class CASUALApp {
//TODO: convert android-casual to Maven so it works better cross-platform
    
    /**
     *the default package used for IDE mode or if no scripts are found
     */
    final public static String defaultPackage = "TestScript"; //note this will be used for IDE only.
    final private static boolean useOverrideArgs = false; // this will use overrideArguments.
    final private static String[] overrideArguments = new String[]{"-e", "\"$HEIMDALL close-pc-screen\""};
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
        String CASUALFileName = new File(new CASUALApp().getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).toString();
        String CASUALSVNRevision = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision");
        String CASUALBuildNumber = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.buildnumber");
        new Log().level2Information("We are running " + System.getProperty("os.name") + "\nCreating Temp Folder in:" + Statics.TempFolder
                + "CASUAL Cross-platform Android Scripting and Unified Auxiliary Loader\nRevision:" +CASUALSVNRevision + " build:" + CASUALBuildNumber + "\n"
                + "    CASUAL  Copyright (C) 2013  Adam Outler\n"
                + "    This program comes with ABSOLUTELY NO WARRANTY.  This is free software,\n"
                + "    and you are welcome to redistribute it, under certain conditions; run\n"
                + "    '" + CASUALFileName + " --license'\n"
                + "    for details. http://android-casual.googlecode.com for source.");


        boolean shutdown = checkModeSwitchArgs(args);
        if ( ! shutdown ) new CASUALMain().startup(args);
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
        String password=null;
        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("--help") || args[i].equals("-v?")) {
                new Log().level2Information("\n"
                        + " Usage: casual.jar [optional parameters]\n"
                        + " without arguments - Launch the GUI\n"
                        + " [--help] shows this message and exits\n"
                        + " [--license] -shows license and exits\n"
                        + " [--execute/-e \"command\"]-executes any CASUAL command and exits. Launch CASUAL GUI to read about commands"
                        + " [--caspac/-c path_to" + Statics.Slash + "CASPACzip] -launches CASUAL with a CASPAC"
                        + " [--gui/-g)] - performs actions with a GUI\n");


                CASUALApp.shutdown(0);
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
                CASUALApp.shutdown(0);
                return true;
            }
            if (args[i].contains("--password")||args[i].contains("-p")){
                i++;
                password=args[i];
            }
            
            if (args[i].contains("--caspac") || args[i].contains("-c") || args[i].contains("--CASPAC") || args[i].contains("-CASPAC")) {
                
                
                i++;
                if (password==null){
                    new CASPACHandler().loadCASUALPack(args[i]);
                } else {
                    new CASPACHandler().loadCASUALPack(args[i],password.toCharArray());    
                }
                new Log().level2Information("CASPAC completed.");
                CASUALApp.shutdown(0);
                return true;
            }
            if (args[i].contains("--gui") || args[i].contains("-g")) {
                Statics.useGUI = true;
            }
            
        }
        return false;
    }

    /**
     * shuts down CASUAL
     * @param i code to throw
     */
    public static void shutdown(int i) {
        new Log().level4Debug("Shutting Down");
        Log.out.flush();
        if (Statics.CASPAC !=null && Statics.CASPAC.getActiveScript()!=null){
            Statics.CASPAC.getActiveScript().scriptContinue=false;
        }
        CASUALConnectionStatusMonitor.DeviceCheck.stop();
        ADBTools.killADBserver();
        if (!CASUALTools.IDEMode && !Statics.useGUI){
            try {

                new Pastebin().pasteAnonymousLog();
            } catch (MalformedURLException ex) {
                new Log().errorHandler(ex);
            }
        }

        try {
            CASUALInteraction.in.close();
            Statics.initializeStatics();
        } catch (IOException ex) {
            Logger.getLogger(CASUALApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
