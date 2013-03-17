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
public class CASUALMain {
    
    public void doStartup(){
        Statics Statics = new Statics();
        new FileOperations().makeFolder(Statics.TempFolder);
        Statics.lockGUIformPrep = true;
        Thread scriptPrep=new Thread(prepScripts);
        scriptPrep.start();
        Thread adb=new Thread(adbDeployment);
        adb.start();
        Thread pData=new Thread(packageData);
        pData.start();
        Thread startGUI=new Thread(GUI);
        startGUI.start();
        System.out.println("CASUAL Cross-platform ADB Scripting Universal Android Loader\nRevision:" + CASUALPackageData.CASUALSVNRevision + CASUALPackageData.CASUALBuildNumber);
    }
    
    Runnable adbDeployment=new Runnable() {
        @Override
        public void run() {
            new CASUALDeployADB().runAction();
            new CASUALTools().startStopADBDeviceCheckTimer(true); 
        }
    };
    
    Runnable packageData=new Runnable(){
        @Override
        public void run() {
            CASUALPackageData casualPackageData = new CASUALPackageData();
            if (CASUALPackageData.UseSound) CASUALAudioSystem.playSound("/CASUAL/resources/sounds/CASUAL.wav");
        }
    };
    
    Runnable GUI=new Runnable(){
        @Override
        public void run() {
            Statics.GUI = new CASUALJFrame();
            Statics.GUI.setVisible(true);
        }
    };
    Runnable prepScripts=new Runnable(){

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
