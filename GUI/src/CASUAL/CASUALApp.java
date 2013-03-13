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

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class CASUALApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        Statics.lockGUIformPrep = true;
        System.out.println("CASUAL Cross-platform ADB Scripting Universal Android Loader\nRevision:" + java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision") + " Build:" + java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.buildnumber"));

        Statics Statics = new Statics();
        Statics.UseSound = java.util.ResourceBundle.getBundle("SCRIPTS/-build").getString("Audio.Enabled");
        CASUALAudioSystem.playSound("/CASUAL/resources/sounds/CASUAL.wav");

        new FileOperations().makeFolder(Statics.TempFolder);
        Statics.GUI = new CASUALJFrame();
        Statics.GUI.setVisible(true);
        show(Statics.GUI);
        Statics.GUI.startStopTimer(true);
        Statics.GUI.setVisible(true);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     *
     * @return the instance of NARSApp
     */
    public static CASUALApp getApplication() {
        return Application.getInstance(CASUALApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {

        launch(CASUALApp.class, args);
    }
}
