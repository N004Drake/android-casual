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

/**
 *
 * @author adam
 */
public final class CASUALMain {

    String[] args;

    public void startup(String[] cmd) {
        args = cmd;
        new FileOperations().makeFolder(Statics.TempFolder);
        Thread adb = new Thread(new CASUALTools().adbDeployment);
        adb.start(); //start ADB deployment
        Statics.lockGUIformPrep = true;
        Thread scriptPrep = new Thread(new CASUALTools().prepScripts);
        scriptPrep.start(); //scan self for embedded scripts
        Thread pData = new Thread(new CASUALTools().setCASUALPackageDataFromScriptsFolder);
        pData.start(); // scan self and set package properties
        Thread cSound = new Thread(new CASUALTools().casualSound);
        try {
            pData.join(); //wait for properties
            cSound.start();  //do startup sound
            
        if (args.length != 0 && !Statics.useGUI) {
            adb.join(); //wait for adb deployment
            Statics.casualConnectionStatusMonitor.DeviceCheck.start();
            scriptPrep.join(); //wait for embedded scripts scan
            doConsoleStartup();  //use command line args
        } else {
            Statics.useGUI = true;
            doGUIStartup(); //bring up GUI and wait for user to click start
            adb.join(); //wait for adb deployment
            scriptPrep.join(); //wait for embedded scripts scan
            Statics.casualConnectionStatusMonitor.DeviceCheck.start();
        }
        } catch (InterruptedException ex) {
            new Log().errorHandler(ex);
        }
    }

    private void doConsoleStartup() {
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("--execute") || args[i].contains("-e")) {
                i++;
                Statics.casualConnectionStatusMonitor.DeviceCheck.stop();
                new CASUALScriptParser().executeOneShotCommand(args[i]);
            } else {
                new Log().level0Error("Unrecogized command");
            }

        }
        //   CASUALApp.shutdown(0);
    }

    private void doGUIStartup() {
        Thread startGUI = new Thread(new CASUALTools().GUI);
        startGUI.start();
    }
}