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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

/**
 *
 * @author adam
 */
public class CASUALModularPack {

    String activeScript = "";
    Thread script;
    Thread zip;
    CASUALModularPackData cpm;

    void loadCASUALPackFileForCommandLineOnly(String pack) {
        Unzip unzip = new Unzip();
        boolean gotScript = false; //CASPAC will contain only one script.
        try {
            Thread adb = new Thread(new CASUALTools().adbDeployment);
            adb.start();
            File f = new File(pack);
            if (!f.exists()) {
                new Log().level0("File Not Found " + pack);
                System.exit(1);
            } else {
                System.out.println("-----CASPAC MODE-----\nCASPAC: " + f.getAbsolutePath());
            }
            //begin unziping and analyzing CASPAC
            Enumeration zippedFiles = unzip.getZipFileEntries(f);
            while (zippedFiles.hasMoreElements()) {
                Object entry = zippedFiles.nextElement(); //get the object and begin examination
                if (entry.toString().equals("-build.properties")) {

                    new CASUALPackageData().setPropertiesFromInputStream(unzip.streamFileFromZip(f, entry));
                } else if (entry.toString().equals("-Overview.txt")) { //TODO: is this needed or should it be used for notes in CASPAC?
                    System.out.print("\n" + new FileOperations().readTextFromStream(unzip.streamFileFromZip(f, entry)) + "\n");
                } else if (entry.toString().endsWith(".meta")) {
                    cpm = new CASUALModularPackData(unzip.streamFileFromZip(f, entry));
                } else if (entry.toString().endsWith(".scr") && !gotScript) {
                    String scriptBasename = StringOperations.replaceLast(entry.toString(), ".scr", "");
                    Statics.ScriptLocation = Statics.TempFolder + scriptBasename + Statics.Slash;
                    activeScript = Statics.ScriptLocation + scriptBasename + ".scr";
                    new FileOperations().makeFolder(Statics.ScriptLocation);
                    new FileOperations().writeStreamToFile(unzip.streamFileFromZip(f, entry), activeScript);
                    script = new Thread(new MD5sumRunnable(activeScript, entry.toString()));
                    gotScript = true;
                } else if (entry.toString().endsWith(".zip")) {
                    Statics.ScriptLocation = StringOperations.replaceLast(Statics.TempFolder + entry.toString(), ".zip", "") + Statics.Slash;
                    new FileOperations().makeFolder(Statics.ScriptLocation);
                    unzip.unZipInputStream(unzip.streamFileFromZip(f, entry), Statics.ScriptLocation);
                    zip = new Thread(new MD5sumRunnable(unzip.streamFileFromZip(f, entry), entry.toString()));

                } else if (entry.toString().endsWith(".txt")) {
                    System.out.print("\n" + new FileOperations().readTextFromStream(unzip.streamFileFromZip(f, entry)) + "\n");
                }
                //DONE with extraction and preparation of zip
            }

            //get ADB ready
            try {
                script.join();
                adb.join();
                zip.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CASUALModularPack.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
            int scriptSVNRevision=(Integer.parseInt(java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision")));
            int minSVNRevision=Integer.parseInt(cpm.minSVNRevision);
            //verify it's an integer
            if (scriptSVNRevision>0 && minSVNRevision>0){
                //do the check
                if (scriptSVNRevision<minSVNRevision){
                    System.out.println("FAILURE!  CASUAL MUST BE UPDATED TO RUN THIS!");
                    System.exit(1);
                } else {
                    System.out.println("Application Revision Check Passed");
                }
                
            }
            } catch (NumberFormatException E) {
                System.out.println("WARNING: could not parse SVN revision.. Continuing");
            
            }
            //TODO: go online check ID and revision (should we use these for CASPAC or should it implement a file naming)?
            //TODO: possible: PackageNameR2.CASPAC.zip
            
            if (new CASUALTools().getIDEMode()) {
                //we are in IDE mode
                for (Object md5 : Statics.runnableMD5list.toArray()) {
                    System.out.println(md5.toString());
                    boolean md5Matches=false;
                    for (Object expectedMD5 : cpm.md5s.toArray()){
                       if (expectedMD5.toString().equals(md5.toString())){
                           md5Matches=true;
                       }
                    }
                    if (!md5Matches) {
                        System.out.println("ERROR: Package is corrupt. Cannot continue.");
                        System.exit(0);
                    }
                }
                System.out.println("File Integrity Verification Check passed.");
            } else {
                //we are in normal mode
                
                for (Object md5 : Statics.runnableMD5list.toArray()) {
                    System.out.println(md5.toString());
                   //TODO: verify MD5s from CASPAC   
                    
                }

      
                new CASUALScriptParser().executeOneShotCommand("$ADB wait-for-device");
                //Launch script
                Thread t = new Thread(activateScript);
                //t.start();
                //do communications here
                try {
                    t.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CASUALModularPack.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }  catch (ZipException ex) {
            new Log().errorHandler(ex);
            new Log().level0("Zip File is corrupt. cannot continue.");
            System.exit(1);
        } catch (IOException ex) {
            new Log().errorHandler(ex);
            new Log().level0("There was a problem reading the file.");
            System.exit(1);
        }

        System.out.println();
    }
    Runnable activateScript = new Runnable() {
        @Override
        public void run() {
            new CASUALScriptParser().executeSelectedScriptFile(activeScript, activeScript, false);
        }
    };
}
