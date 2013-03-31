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

    final public static String defaultPackage = "ATT GS3 Root"; //note this will be used for IDE only.
    final private static boolean useOverrideArgs = false; // this will use overrideArguments.
    //final private static String[] overrideArguments=new String[]{"--execute", "$HEIMDALL print-pit --no-reboot"};
    final private static String[] overrideArguments = new String[]{"--caspac", "/home/adam/Desktop/casualpack/testPak.zip"};
    String[] arguments;

    /**
     * At startup create and show the main frame of the application.
     */
    void startup(String[] args) {
        new CASUALMain().startup(args);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     *
     * @return the instance of NARSApp
     */
    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        CASUALPackageData.CASUALFileName = new File(new CASUALApp().getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).toString();
        CASUALPackageData.CASUALSVNRevision = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision");
        CASUALPackageData.CASUALBuildNumber = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.buildnumber");
        if (useOverrideArgs) {
            args = overrideArguments;
        }
        checkEarlyArgs(args);
        new CASUALApp().startup(args);
    }

    private static void checkEarlyArgs(String args[]) {

        for (int i = 0; i < args.length; i++) {
            System.out.println("CASUAL Cross-platform ADB Scripting Universal Android Loader\nRevision:" + CASUALPackageData.CASUALSVNRevision + " build:" + CASUALPackageData.CASUALBuildNumber + "\n"
                    + "    CASUAL  Copyright (C) 2013  Adam Outler\n"
                    + "    This program comes with ABSOLUTELY NO WARRANTY.  This is free software,\n"
                    + "    and you are welcome to redistribute it, under certain conditions; run\n"
                    + "    '" + CASUALPackageData.CASUALFileName + " --license'\n"
                    + "    for details. http://android-casual.googlecode.com for source.");


            if (args[i].equals("--help") || args[i].equals("-v?")) {
                System.out.println("\n"
                        + " Usage: casual.jar [optional parameters]\n"
                        + " without arguments - Launch the GUI\n"
                        + " [--help] shows this message and exits\n"
                        + " [--license] -shows license and exits\n"
                        + " [--execute/-e \"command\"]-executes any CASUAL command and exits. Launch CASUAL GUI to read about commands"
                        + " [--caspac/-c path_to" + Statics.Slash + "CASPACzip] -launches CASUAL with a CASPAC"
                        + " [--gui/-g)] - performs actions with a GUI");

                System.out.println("");
                System.exit(0);
            }
            if (args[i].equals("--license")) {
                System.out.println("\n"
                        + "    This program is free software: you can redistribute it and/or modify\n"
                        + "    it under the terms of the GNU General Public License as published by\n"
                        + "    the Free Software Foundation, either version 3 of the License, or\n"
                        + "    (at your option) any later version.\n"
                        + "\n"
                        + "    This program is distributed in the hope that it will be useful,\n"
                        + "    but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
                        + "    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
                        + "    GNU General Public License for more details.");
                System.exit(0);
            }
            if (args[i].contains("--caspac") || args[i].contains("-c")) {
                i++;
                new CASPACHandler().loadCASUALPackFileForCommandLineOnly(args[i]);
                System.out.println("CASPAC completed.");
                System.exit(0);
            }
            if (args[i].contains("--gui") || args[i].contains("-g")) {
                Statics.useGUI = true;
            }
        }
    }
}
