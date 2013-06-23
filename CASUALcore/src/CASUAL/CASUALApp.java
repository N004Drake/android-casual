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

import java.awt.Window;
import java.io.File;
import java.net.MalformedURLException;

/**
 * The main class of the application.
 */
public class CASUALApp {
//TODO: convert android-casual to Maven so it works better cross-platform

    /**
     *
     */
        final public static String defaultPackage = "TestScript"; //note this will be used for IDE only.
    final private static boolean useOverrideArgs = false; // this will use overrideArguments.
    final private static String[] overrideArguments = new String[]{"-e", "\"$HEIMDALL close-pc-screen\""};

   
    static String[] arguments;

    /**
     * At startup create and show the main frame of the application.
     */
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     *
     * @param root
     */
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * Main method launching the application.
     *
     * @param args
     */
    public static void main(String[] args) {
        arguments=args;
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
      * @param args 
      */
     public static void beginCASUAL(String[] args) {
        CASUALapplicationData.CASUALFileName = new File(new CASUALApp().getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).toString();
        CASUALapplicationData.CASUALSVNRevision = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision");
        CASUALapplicationData.CASUALBuildNumber = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.buildnumber");
        new Log().level2Information("We are running " + System.getProperty("os.name") + "\nCreating Temp Folder in:" + Statics.TempFolder
                + "CASUAL Cross-platform Android Scripting and Unified Auxiliary Loader\nRevision:" + CASUALapplicationData.CASUALSVNRevision + " build:" + CASUALapplicationData.CASUALBuildNumber + "\n"
                + "    CASUAL  Copyright (C) 2013  Adam Outler\n"
                + "    This program comes with ABSOLUTELY NO WARRANTY.  This is free software,\n"
                + "    and you are welcome to redistribute it, under certain conditions; run\n"
                + "    '" + CASUALapplicationData.CASUALFileName + " --license'\n"
                + "    for details. http://android-casual.googlecode.com for source.");


        checkModeSwitchArgs(args);
        new CASUALMain().startup(args);
    }
    
    /**
     * checkModeSwitchArgs is a primary switch before any real
     * actions happen.  Here we check for switches that will either
     * change the mode of CASUAL or display something quick and exit.
     * @param args 
     */
    private static void checkModeSwitchArgs(String args[]) {

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
                return;
            }
            if (args[i].contains("--caspac") || args[i].contains("-c") || args[i].contains("--CASPAC")|| args[i].contains("-CASPAC")) {
                i++;
                new CASPACHandler().loadCASUALPack(args[i]);
                new Log().level2Information("CASPAC completed.");
                CASUALApp.shutdown(0);
            }
            if (args[i].contains("--gui") || args[i].contains("-g")) {
                Statics.useGUI = true;
            }
        }
    }

    public static void shutdown(int i) {
        new Log().level4Debug("Shutting Down");
        Log.out.flush();
        Window windows[] = Window.getWindows();
        if (windows != null) {
            for (Window window : windows) {
                window.dispose();
            }
        }
        new Shell().silentShellCommand(new String[]{Statics.adbDeployed, "kill-server"});
        //if (!CASUALTools.IDEMode){
            try {
                new Pastebin().pasteAnonymousLog();
            } catch (MalformedURLException ex) {
                new Log().errorHandler(ex);
            }
        //}
        Statics.initializeStatics();
    }
}
