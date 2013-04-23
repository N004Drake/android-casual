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

/**
 * The main class of the application.
 */
public class CASUALApp {

    /**
     *
     */
    final public static String defaultPackage = "TestScript"; //note this will be used for IDE only.
    final private static boolean useOverrideArgs = true; // this will use overrideArguments.
    final private static boolean useTestFramework = true; // this will begin an automated test without notifications
    
    final private static String[] overrideArguments = new String[]{"--caspac", "SCRIPTS/testpak.zip"};

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

       
        checkEarlyArgs(args);
        new CASUALMain().startup(args);
    }
    String[] arguments;

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
         if (useOverrideArgs) { //overrides command line input
            args = overrideArguments;
        }
        if (useTestFramework){ //automates CASUAL to test for errors
            CASUALTest.args=args;
            //Statics.GUI=new CASUALJFrameMain();
            Statics.useGUI=false;
            try {
            new CASUALTest(args).instantiateCASUAL();
            return;
            } catch (Exception e){
                new Log().errorHandler(e);
                //CASUALApp.shutdown(0);
            } 
            //CASUALApp.shutdown(0);
        }
        beginCASUAL(args);
    }

    private static void checkEarlyArgs(String args[]) {

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
            if (args[i].contains("--caspac") || args[i].contains("-c")) {
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
    
    public static void shutdown(int i){
        Log.out.flush();
        System.exit(i);
    }
    
}
