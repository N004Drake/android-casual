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



/**
 * The main class of the application.
 */
public class CASUALApp {
    final public static String defaultPackage="ATT GS3 Root";
    final private static boolean useOverrideArgs=false;
    final private static String[] overrideArguments=new String[]{"--execute", "$HEIMDALL print-pit --no-reboot"};
    
    String[] arguments;
    /**
     * At startup create and show the main frame of the application.
     */

    void startup(String[] args) {
       new CASUALMain(args).startup();
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
        
        if (useOverrideArgs){
            args=overrideArguments;
        }
        new CASUALApp().startup(args);
    }
}
