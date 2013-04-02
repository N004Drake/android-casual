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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    CASPACData cpm;

    void loadCASUALPackFileForCommandLineOnly(String pack) {
        Unzip unzip = new Unzip();
        boolean gotScript = false; //CASPAC will contain only one script.
        try {
            Thread adb = new Thread(new CASUALTools().adbDeployment);
            adb.start();
            File f = new File(pack);
            if (!f.exists()) {
                new Log().level0Error("File Not Found " + pack);
                System.exit(1);
            } else {
                System.out.println("-----CASPAC MODE-----\nCASPAC: " + f.getAbsolutePath());
            }
            //begin unziping and analyzing CASPAC
            Enumeration zippedFiles = unzip.getZipFileEntries(f);
            rewriteMD5OnCASPAC(pack);
            while (zippedFiles.hasMoreElements()) {
                Object entry = zippedFiles.nextElement(); //get the object and begin examination
                handleCASPACFiles(entry, unzip, f, gotScript);
            }

            //get ADB ready
            try {

                adb.join();
                zip.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                int scriptSVNRevision = (Integer.parseInt(java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision")));
                int minSVNRevision = Integer.parseInt(cpm.minSVNRevision);
                //verify it's an integer
                if (scriptSVNRevision > 0 && minSVNRevision > 0) {
                    //do the check
                    if (scriptSVNRevision < minSVNRevision) {
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
                    boolean md5Matches = false;
                    for (Object expectedMD5 : cpm.md5s.toArray()) {
                        if (expectedMD5.toString().equals(md5.toString())) {
                            md5Matches = true;
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

            }
            new CASUALScriptParser().executeOneShotCommand("$ADB wait-for-device");
            //Launch script
            Thread t = new Thread(activateScript);
            t.start();
            //do communications here
            try {
                t.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
            }



        } catch (ZipException ex) {
            new Log().errorHandler(ex);
            new Log().level0Error("Zip File is corrupt. cannot continue.");
            System.exit(1);
        } catch (IOException ex) {
            new Log().errorHandler(ex);
            new Log().level0Error("There was a problem reading the file.");
            System.exit(1);
        }

        System.out.println();
    }
    Runnable activateScript = new Runnable() {
        @Override
        public void run() {
            new CASUALScriptParser().loadFileAndExecute(activeScript, activeScript, false);
        }
    };

    private void handleCASPACFiles(Object entry, Unzip unzip, File f, boolean gotScript) throws IOException {
        if (entry.toString().equals("-build.properties")) {

            new CASUALPackageData().setPropertiesFromInputStream(unzip.streamFileFromZip(f, entry));
        } else if (entry.toString().equals("-Overview.txt")) {
            if (Statics.useGUI) { //only display overview if using GUI.
                System.out.print("\n" + new FileOperations().readTextFromStream(unzip.streamFileFromZip(f, entry)) + "\n");
            }
        } else if (entry.toString().endsWith(".meta")) {
            cpm = new CASPACData(unzip.streamFileFromZip(f, entry));
        } else if (entry.toString().endsWith(".scr") && !gotScript) {
            String scriptBasename = StringOperations.replaceLast(entry.toString(), ".scr", "");
            Statics.ScriptLocation = Statics.TempFolder + scriptBasename + Statics.Slash;
            activeScript = Statics.ScriptLocation + scriptBasename;
            new FileOperations().makeFolder(Statics.ScriptLocation);
            new FileOperations().writeStreamToFile(unzip.streamFileFromZip(f, entry), Statics.ScriptLocation + entry);
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

    private ArrayList getMD5sfromCASPAC(Enumeration zippedFiles, String pack, String CASUALMeta) throws IOException {
        ArrayList list = new ArrayList();
        Unzip unzip = new Unzip();
        File f = new File(pack);
        MD5sum md5sum = new MD5sum();
        while (zippedFiles.hasMoreElements()) {
            Object entry = zippedFiles.nextElement(); //get the object and begin examination
            if (entry.toString().endsWith(".meta")) {
                new FileOperations().overwriteFile(StringOperations.convertStreamToString(unzip.streamFileFromZip(f, entry)), CASUALMeta);
            } else {
                list.add(md5sum.makeMD5String(md5sum.md5sum(unzip.streamFileFromZip(f, entry)), entry.toString()));
            }

            // System.out.print("\n" + new FileOperations().readTextFromStream(unzip.streamFileFromZip(f, entry)) + "\n");
            //DONE with extraction and preparation of zip
        }
        return list;
    }

    private void rewriteMD5OnCASPAC(String pack) {
        System.out.println("Writing new CASUAL Package Data!");
        ArrayList list = new ArrayList<String>();
        File f = new File(pack);
        String CASUALMeta = Statics.TempFolder + "info.meta";
        try {
            list = getMD5sfromCASPAC(new Unzip().getZipFileEntries(f), pack, CASUALMeta);
        } catch (ZipException ex) {
            Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileInputStream buildfile = null;

        try {
            String line = "";
            String output = "";
            buildfile = new FileInputStream(CASUALMeta);
            MD5sum md5sum = new MD5sum();
            while (buildfile.available() > 0) {
                line = line + (char) buildfile.read();
                if (line.contains("\n")) { //read a line
                    if (md5sum.lineContainsMD5(line)) { //test if it contains an md5
                        System.out.println("MD5" + line);
                        String mdstring = md5sum.pickNewMD5fromArrayList(list, line);
                        String filetocheck = mdstring.split("  ")[1];
                        Enumeration entries = new Unzip().getZipFileEntries(f);
                        while (entries.hasMoreElements()) {
                            Object e = entries.nextElement();
                            System.out.println(e.toString());
                            if (filetocheck.contains(e.toString())) {
                                String newMD5 = new MD5sum().md5sum(new Unzip().streamFileFromZip(f, e));
                                output=output+new MD5sum().makeMD5String(newMD5, e.toString()+"\n");
                                
                            }

                        }
                    } else {
                        output = output + line;
                    }
                    line = "";
                }
            }
            //         System.out.println(output);
            new FileOperations().overwriteFile(output, CASUALMeta);
            System.out.println(output);

        } catch (IOException ex) {
            Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
        }


        /*        try {
         Zip.addFilesToExistingZip(pack, StringOperations.convertArrayListToStringArray(list));
         } catch (IOException ex) {
         Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
         }
         */



    }
}
