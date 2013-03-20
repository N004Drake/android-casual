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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public final class CASUALMain {
    String[] args;

    CASUALMain(String[] cmd) {
        args = cmd;
    }

    public void startup() {
        new FileOperations().makeFolder(Statics.TempFolder);
        Thread adb = new Thread(adbDeployment);
        adb.start(); //start ADB deployment
        Statics.lockGUIformPrep = true;
        Thread scriptPrep = new Thread(prepScripts);
        scriptPrep.start(); //scan self for embedded scripts
        Thread pData = new Thread(packageData);
        pData.start(); // scan self and set package properties
        Thread cSound = new Thread (casualSound);
        try {
            pData.join(); //wait for properties
            cSound.start();  //do startup sound
            scriptPrep.join(); //wait for embedded scripts scan
            adb.join(); //wait for adb deployment
        } catch (InterruptedException ex) {
            Logger.getLogger(CASUALMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        new CASUALTools().startStopADBDeviceCheckTimer(true); //start device scanning


        if (args.length == 0) {
            Statics.UseGUI=true;
            doGUIStartup(); //bring up GUI and wait for user to click start
        } else {
            doConsoleStartup();  //use command line args
        }
    }

    private void doConsoleStartup() {
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("--execute")) {
                i++;
                new CASUALScriptParser().executeOneShotCommand(args[i]);
            } else {
                System.out.println("Unrecogized command");
            }
        }
        System.exit(0);
    }

    private void doGUIStartup() {
        Thread startGUI = new Thread(GUI);
        startGUI.start();
    }

    
    
    Runnable adbDeployment = new Runnable() {
        @Override
        public void run() {
            new CASUALDeployADB().runAction();
        }
    };
    Runnable packageData = new Runnable() {
        @Override
        public void run() {
            CASUALPackageData casualPackageData = new CASUALPackageData();
            casualPackageData.scan();
        }
    };
    Runnable casualSound = new Runnable() {
        @Override
        public void run() {
            if (CASUALPackageData.UseSound) {
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/CASUAL.wav");
            }
        }
    };
    Runnable GUI = new Runnable() {
        @Override
        public void run() {
            Statics.GUI = new CASUALJFrame();
            Statics.GUI.setVisible(true);
        }
    };
    Runnable prepScripts = new Runnable() {
        @Override
        public void run() {
            try {
                new CASUALTools().listScripts();
            } catch (IOException ex) {
                Logger.getLogger(CASUALMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
}
