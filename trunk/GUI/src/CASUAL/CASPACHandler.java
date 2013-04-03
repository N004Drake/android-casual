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
import java.io.FileInputStream;
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
    CASPACData cd;
    String meta="";

    void loadCASUALPackFileForCommandLineOnly(String pack) {
        Unzip unzip = new Unzip();
        try {
            Thread adb = new Thread(new CASUALTools().adbDeployment);
            adb.start();
            File CASPAC = new File(pack);
            if (!CASPAC.exists()) { //verify this is a valid caspac
                new Log().level0Error("File Not Found " + pack);
                System.exit(1);
            }
            System.out.println("-----CASPAC MODE-----\nCASPAC: " + CASPAC.getAbsolutePath());
            //begin unziping and analyzing CASPAC
            Enumeration zippedFiles = unzip.getZipFileEntries(CASPAC);
            cd=processCASPAC(zippedFiles, CASPAC);

            //get ADB ready
            try {

                adb.join();
                zip.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                int scriptSVNRevision = (Integer.parseInt(java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision")));
                int minSVNRevision = Integer.parseInt(cd.minSVNRevision);
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
               this.rewriteMD5OnCASPAC(CASPAC);
            }
                //we are in IDE mode
                for (Object md5 : Statics.runnableMD5list.toArray()) {
                    
                    System.out.println(md5.toString());
                    boolean md5Matches = false;
                    for (Object expectedMD5 : cd.md5s.toArray()) {
                        if (expectedMD5.toString().equals(md5.toString())) {
                            md5Matches = true;
                        } 
                    }
                    
                    if (!md5Matches) {
                        System.out.println("Expected ");
                        System.out.println("ERROR: Package is corrupt. Cannot continue.");
                        System.exit(0);
                    }
                }
                System.out.println("File Integrity Verification Check passed.");
            
                //we are in normal mode

                for (Object md5 : Statics.runnableMD5list.toArray()) {
                    System.out.println(md5.toString());
                    //TODO: verify MD5s from CASPAC   

                }

            if (Statics.useGUI){
              CASUALPackageData.ScriptsHaveBeenRecognized=true;
              Statics.TargetScriptIsResource=false;
              Statics.dumbTerminalGUI=true;
              Statics.GUI=new CASUALJFrameMain();
              Statics.GUI.setVisible(true);
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

    private CASPACData handleCASPACFiles(Object entry, File f) throws IOException {
        Unzip unzip=new Unzip();
        if (entry.toString().equals("-build.properties")) {
            new CASUALPackageData().setPropertiesFromInputStream(unzip.streamFileFromZip(f, entry));
        } else if (entry.toString().equals("-Overview.txt")) {
            if (Statics.useGUI) { //only display overview if using GUI.
                System.out.print("\n" + new FileOperations().readTextFromStream(unzip.streamFileFromZip(f, entry)) + "\n");
                new FileOperations().writeStreamToFile(unzip.streamFileFromZip(f, entry), Statics.ScriptLocation + entry);

            }
        } else if (entry.toString().endsWith(".meta")) {
            CASUALPackageData.meta=entry.toString();
            meta=Statics.TempFolder+entry.toString();
            unzip.deployFileFromZip(f, entry, Statics.TempFolder);
            return new CASPACData(new FileOperations().readFile(meta));
        } else if (entry.toString().endsWith(".scr")) {
            String scriptBasename = StringOperations.replaceLast(entry.toString(), ".scr", "");
            Statics.ScriptLocation = Statics.TempFolder + scriptBasename + Statics.Slash;
            activeScript = Statics.ScriptLocation + scriptBasename;
            new FileOperations().makeFolder(Statics.ScriptLocation);
            new FileOperations().writeStreamToFile(unzip.streamFileFromZip(f, entry), Statics.ScriptLocation + entry);
            new MD5sumRunnable(unzip.streamFileFromZip(f, entry), entry.toString()).run();
        } else if (entry.toString().endsWith(".zip")) {
            Statics.ScriptLocation = StringOperations.replaceLast(Statics.TempFolder + entry.toString(), ".zip", "") + Statics.Slash;
            new FileOperations().makeFolder(Statics.ScriptLocation);
            unzip.unZipInputStream(unzip.streamFileFromZip(f, entry), Statics.ScriptLocation);
            zip = new Thread(new MD5sumRunnable(unzip.streamFileFromZip(f, entry), entry.toString()));
        } else if (entry.toString().endsWith(".txt")) {
            System.out.print("\n" + new FileOperations().readTextFromStream(unzip.streamFileFromZip(f, entry)) + "\n");
        }
        return null;
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

    private void rewriteMD5OnCASPAC(File CASPAC) {
        System.out.println("Writing new CASUAL Package Data!");
        ArrayList list = new ArrayList<String>();
        Enumeration zippedFiles=null;
        try {
            zippedFiles = new Unzip().getZipFileEntries(CASPAC);
        } catch (ZipException ex) {
            Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        String CASUALMeta=Statics.TempFolder+ getMetaName(zippedFiles);
        
        try {
            list = getMD5sfromCASPAC(new Unzip().getZipFileEntries(CASPAC), CASPAC.toString(), CASUALMeta);
        } catch (ZipException ex) {
            Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileInputStream buildfile;

        try {
            String line = "";
            String output = "";
            buildfile = new FileInputStream(CASUALMeta);
            MD5sum md5sum = new MD5sum();
            while (buildfile.available() > 0) { //check if availabe
                line = line + (char) buildfile.read(); //read into string
                if (line.contains("\n")) { //stop when we have a line 
                    if (md5sum.lineContainsMD5(line)) { //test if it contains an md5
                        System.out.println("MD5" + line);
                        String mdstring = md5sum.pickNewMD5fromArrayList(list, line);
                        String filetocheck = mdstring.split("  ")[1];
                        Enumeration entries = new Unzip().getZipFileEntries(CASPAC);
                        while (entries.hasMoreElements()) {
                            Object e = entries.nextElement();
                            System.out.println(e.toString());
                            if (filetocheck.contains(e.toString())) {
                                String newMD5 = new MD5sum().md5sum(new Unzip().streamFileFromZip(CASPAC, e));
                                output=output+new MD5sum().makeMD5String(newMD5, e.toString()+"\n");
                            }
                        }
                    } else {
                        output = output + line;
                    }
                    line = "";
                }
            }
            new FileOperations().overwriteFile(output, CASUALMeta);
            System.out.println(output);
        } catch (IOException ex) {
            Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
        }


       try {
         Zip.addFilesToExistingZip(CASPAC,meta);
         } catch (IOException ex) {
         Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
         }
       }

    private String getMetaName(Enumeration zippedFiles) {
        while (zippedFiles.hasMoreElements()){
            Object file=zippedFiles.nextElement();
            if (file.toString().endsWith(".meta")){
                return file.toString();
            }
        }
        return "";
    }

    private CASPACData processCASPAC(Enumeration zippedFiles, File f) throws IOException {
        CASPACData returnCASPAC=null;
        while (zippedFiles.hasMoreElements()) {
            Object entry = zippedFiles.nextElement(); //get the object and begin examination
            CASPACData test;
            test=handleCASPACFiles(entry, f);
            if (test!=null){
                cd=test;
            }
        }
        return cd;
    }
}
