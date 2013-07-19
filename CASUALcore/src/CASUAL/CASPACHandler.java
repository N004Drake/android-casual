/*CASUALModularPack provides a way to launch CASPAC format. 
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

import CASUAL.caspac.Caspac;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

/**
 *
 * @author adam
 */
public class CASPACHandler {

    String activeScript = "";
    Thread script;
    Thread zip;
    Caspac CASPAC;
    String meta = "";
    Thread adbLaunch = new Thread(new CASUALTools().launchADB);

    /**
     * Launches a CASPAC
     *
     * @param pack
     */
    public void loadCASUALPack(String pack) {
        Thread adb = new Thread(new CASUALTools().adbDeployment);
        adb.setName("ADB Deployment");
        adb.start();
        File zipFile = new File(pack);
        if (!zipFile.exists()) { //verify this is a valid caspac
            new Log().level0Error("@fileNotFound");
            new Log().level0Error(pack);
            CASUALApp.shutdown(1);
        }
        new Log().level3Verbose("-----CASPAC MODE-----\nCASPAC: " + zipFile.getAbsolutePath());
        try {
            //begin unziping and analyzing CASPAC
            CASPAC = new Caspac(zipFile,Statics.TempFolder,0);
        } catch (IOException ex) {
            Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //get ADB ready
        try {
            adb.join();
            adbLaunch.setDaemon(true);
            adbLaunch.setName("Starting ADB server");
            adbLaunch.start();
        } catch (InterruptedException ex) {
            new Log().errorHandler(new Exception("CASPACHandler.loadCASUALPack interrupted" + ex));
        }



        if (Statics.useGUI) {
            startDumbTerminalGUI();
        }
        try {
            adbLaunch.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
        }        

        //Launch script
        Thread t = new Thread(activateFirstScript);
        t.setName("CASUAL Script");
        t.start();
        //do communications here
        try {
            t.join();
        } catch (InterruptedException ex) {
            new Log().errorHandler(new Exception("CASPACHandler.loadCASUALPack interrupted" + ex));
        }

    }
    Runnable activateFirstScript = new Runnable() {
        @Override
        public void run() {
            try {
                CASPAC.loadFirstScriptFromCASPAC();
            } catch (ZipException ex) {
                Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            new CASUALScriptParser().executeFirstScriptInCASPAC(CASPAC);

            CASUALApp.shutdown(0);
        }
    };

    
    /**
     *
     * @param pack CASPAC file
     * @param CASUALMeta Metadata from CASPAC
     * @return list of MD5s
     * @throws IOException
     */
    public ArrayList getMD5sfromCASPAC(String pack, String CASUALMeta) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        Unzip unzip = new Unzip(new File(pack));
        File f = new File(pack);
        MD5sum md5sum = new MD5sum();
        while (unzip.zipFileEntries.hasMoreElements()) {
            Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
            if (entry.toString().endsWith(".meta")) {
                new FileOperations().overwriteFile(StringOperations.convertStreamToString(Unzip.streamFileFromZip(f, entry)), CASUALMeta);
            } else {
                list.add(md5sum.convertMD5andFiletoLinuxMD5Sum(md5sum.md5sum(Unzip.streamFileFromZip(f, entry)), entry.toString()));
            }

        }

        return list;
    }

    /**
     *
     * @param zippedFiles Enumeration containing the names of all files in the
     * caspac
     * @return name of meta file. null if none found
     */
    public String getMetaName(Enumeration zippedFiles) {
        while (zippedFiles.hasMoreElements()) {
            Object file = zippedFiles.nextElement();
            if (file.toString().endsWith(".meta")) {
                return file.toString();
            }
        }
        return null;
    }


 
    private void startDumbTerminalGUI() {
        CASUALapplicationData.scriptsHaveBeenRecognized = true;
        Statics.TargetScriptIsResource = false;
        Statics.dumbTerminalGUI = true;
        Statics.GUI = new CASUALJFrameMain();
        Statics.GUI.setVisible(true);
    }
}
