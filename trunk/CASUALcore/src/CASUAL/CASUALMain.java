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
    static Thread scriptPrep;
    
    public void startup(String[] cmd) {
        args = cmd;
        new FileOperations().makeFolder(Statics.TempFolder);
        Thread adb = new Thread(new CASUALTools().adbDeployment);
        adb.setName("ADB Deployment");
        adb.start(); //start ADB deployment
        Statics.lockGUIformPrep = true;
        scriptPrep = new Thread(new CASUALTools().prepScripts);
        scriptPrep.setName("Preparing Scripts");
        scriptPrep.start(); //scan self for embedded scripts
        Thread pData = new Thread(new CASUALTools().setCASUALPackageDataFromScriptsFolder);
        pData.setName("Setting Up CASUAL Package");
        pData.start(); // scan self and set package properties
        Thread cSound = new Thread(new CASUALTools().casualSound);
        cSound.setName("CASUAL Sound");
        try {
            pData.join(); //wait for properties
            cSound.start();  //do startup sound
            
        if (args.length != 0 && !Statics.useGUI) {
            Statics.setStatus("waiting for ADB");
            adb.join(); //wait for adb deployment
            Statics.casualConnectionStatusMonitor.DeviceCheck.start();
            Statics.setStatus("Preparing scripts");
            scriptPrep.join(); //wait for embedded scripts scan
            Statics.setStatus("Warming Up");
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
            Statics.setStatus("parsing");
            if (args[i].contains("--execute") || args[i].contains("-e")) {
                i++;
                Statics.casualConnectionStatusMonitor.DeviceCheck.stop();
                CASUALScriptParser csp=new CASUALScriptParser();
                String s= csp.executeOneShotCommand(args[i]);
                Statics.setStatus("Complete");
                new Log().level2Information("Script Complete");
            } else {
                Statics.setStatus("Invalid commands");
                new Log().level0Error("Unrecogized command");
            }

        }
        //   CASUALApp.shutdown(0);
    }

    private void doGUIStartup() {
        Thread startGUI = new Thread(new CASUALTools().GUI);
        startGUI.setName("GUI");
        Statics.setStatus("launching GUI");
        startGUI.start();
    }
}