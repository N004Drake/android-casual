/*CASUALTools is a miscellanious helper class
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
 *
 * @author adam
 */
public class CASUALTools {
    //final public String defaultPackage="ATT GS3 Root";

    /**
     *
     */
    public static boolean IDEMode = false;
    Log log = new Log();


    
    /**listScripts scans the CASUAL for scripts
     * -results are stored in Statics.scriptNames
     * -In IDE mode an MD5 refresh is triggered
     * @throws IOException
     */
    public void listScripts() throws IOException {
        CodeSource Src = CASUAL.CASUALApp.class.getProtectionDomain().getCodeSource();
        int Count = 0;
        ArrayList<String> list = new ArrayList<>();
        if (Src != null) {
            URL jar = Src.getLocation();
            try (ZipInputStream Zip = new ZipInputStream(jar.openStream())) {
                ZipEntry ZEntry;
                log.level4Debug("Picking Jar File:" + jar.getFile());
                while ((ZEntry = Zip.getNextEntry()) != null) {

                    String EntryName = ZEntry.getName();
                    if (EntryName.endsWith(".scr")) {

                        list.add(EntryName);
                    }
                }
                log.level4Debug("Found " + list.size() + " CASUAL scripts");
                Statics.scriptNames = new String[list.size()];
                for (int n = 0; n < list.size(); n++) {
                    String EntryName = ((String) list.get(n)).replaceFirst("SCRIPTS/", "").replace(".scr", "");
                    log.level4Debug("Found script: " + EntryName);
                    Statics.scriptNames[n] = EntryName;
                    Count++;
                }

                if (Count == 0) {
                    Thread t = new Thread(updateMD5s);
                    t.setName("Updating MD5s");
                    t.start();
                    log.level0Error("IDE Mode: Using " + CASUALApp.defaultPackage + ".scr ONLY!");
                    //Statics.scriptLocations = new String[]{""};
                    Statics.scriptNames = new String[]{CASUALApp.defaultPackage};
                }
                CASUALapplicationData.scriptsHaveBeenRecognized = true;
            }
        }
    }

    /**md5sumTestScript
     * Refreshes the MD5s on the scripts in the /SCRIPTS folder
     */
    //TODO: fix this for CASPACData upgrade
    private void md5sumTestScripts() {
        Statics.setStatus("Setting MD5s");
        log.level4Debug("\nIDE Mode: Scanning and updating MD5s.\nWe are in " + System.getProperty("user.dir"));
        incrementBuildNumber();
        
        if (getIDEMode()) { //if we are in development mode
            //Set up scripts path
            String scriptsPath = System.getProperty("user.dir") + Statics.Slash + "SCRIPTS" + Statics.Slash;
            final File folder = new File(scriptsPath); 
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.toString().endsWith(".meta")) {
                    InputStream in = null;
                    try {
                        //load each meta file into a properties file
                        new Log().level3Verbose("Updating meta: " +fileEntry.toString());
                        LinkedProperties prop = new LinkedProperties();
                        in = new FileInputStream(fileEntry);
                        prop.load(in);
                        in.close();
                        //Identify and store the new MD5s
                        String md5;
                        int pos=0;
                        while ((md5=prop.getProperty("Script.MD5["+pos+"]"))!=null){
                            String entry="Script.MD5["+pos+"]";
                            String[] md5File = md5.split("  ");
                            log.level4Debug("Old MD5: " + md5File[0]);
                            String newMD5 = new MD5sum().md5sum(scriptsPath + md5File[1]);
                            prop.setProperty(entry, newMD5+ "  "+md5File[1]);
                            log.level4Debug("New Property Update: "+ prop.getProperty(entry));
                            
                            pos++;
                        }
                        FileOutputStream fos= new FileOutputStream(fileEntry);
                        prop.store(fos,null);
                        fos.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null,ex);
                    } catch (IOException ex) {
                        Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    /**
     * prepares the script for execution by setting up environment
     * @param scriptName
     * @return
     */
    public Thread prepareCurrentScript(String scriptName) {
        Statics.SelectedScriptFolder = Statics.TempFolder + Statics.Slash + scriptName;
        //set the ZipResource
        final String ZipResource = Statics.TargetScriptIsResource ? (Statics.ScriptLocation + scriptName + ".zip") : (scriptName + ".zip");
        
        log.level4Debug("Created zipResource at " + ZipResource);

        Thread t;
        t = new Thread() {
            @Override
            public void run() {
                try {
                    Statics.GUI.enableControls(false);
                } catch (NullPointerException ex) {
                    log.level4Debug("attempted to lock controls but controls are not availble yet");
                }
                Statics.lockGUIunzip = true;
                if (!new FileOperations().verifyExists(Statics.SelectedScriptFolder)) {
                    new FileOperations().makeFolder(Statics.SelectedScriptFolder);
                }
                log.level4Debug("Extracting archive....");
                if (getClass().getResource(ZipResource) != null) {
                    log.level4Debug("Target Script Is resource");
                    try {
                        Unzip.unZipResource(ZipResource.toString(), Statics.SelectedScriptFolder);
                    } catch (FileNotFoundException ex) {
                        log.errorHandler(ex);
                    } catch (IOException ex) {
                        log.errorHandler(ex);
                    }
                }
                Statics.lockGUIunzip = false;
            }
        };
        t.setName("Script Preparation");
        t.start();

        log.level4Debug("Exiting comboBoxUpdate()");
        return t;
    }

    /**
     * tells if CASUAL is running in Development or Execution mode 
     * @return true if in IDE mode
     */
    public boolean getIDEMode() {
        String className = this.getClass().getName().replace('.', '/');
        String classJar = this.getClass().getResource("/" + className + ".class").toString();
        if (classJar.startsWith("jar:")) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * deploys ADB to Statics.ADBDeployed.
     */
    public Runnable adbDeployment = new Runnable() {
        @Override
        public void run() {
            new CASUALDeployADB().deployADB();
        }
    };
    /**
     * sets up the static CASUALPackageData for use with /SCRIPTS/folder.
     */
    public Runnable setCASUALPackageDataFromScriptsFolder = new Runnable() {
        @Override
        public void run() {
            CASUALapplicationData ca = new CASUALapplicationData();
            
        }
    };
    /**
     * Plays the CASUAL startup sound.
     */
    public Runnable casualSound = new Runnable() {
        @Override
        public void run() {
            if (CASUALapplicationData.useSound) {
                AudioHandler.playSound("/CASUAL/resources/sounds/CASUAL.wav");
            }
        }
    };
    /**
     * Starts the GUI, should be done last and only if needed.
     */
    public Runnable GUI = new Runnable() {
        @Override
        public void run() {
            Statics.GUI = new CASUALJFrameMain();
            Statics.GUI.setVisible(true);
        }
    };
    /**
     * Scans /SCRIPTS/ Folder to locate scripts.
     */
    public Runnable prepScripts = new Runnable() {
        @Override
        public void run() {
            try {
                new CASUALTools().listScripts();
            } catch (IOException ex) {
                new Log().errorHandler(ex);
            }
        }
    };
    private Runnable updateMD5s = new Runnable() {
        @Override
        public void run() {
            md5sumTestScripts();
        }
    };

    //This is only used in IDE mode for development
    void rewriteMD5OnCASPAC(File CASPAC, CASPACHandler caspacHandler) {
        new Log().level3Verbose("Writing new CASUAL Package Data!");
        incrementBuildNumber();
        ArrayList list;
        Enumeration zippedFiles;
        String CASUALMeta;
        Unzip unzip;
        try {
            unzip = new Unzip(CASPAC);
            zippedFiles = unzip.zipFileEntries;
            CASUALMeta = Statics.TempFolder + caspacHandler.getMetaName(zippedFiles);
            if (CASUALMeta == null) {
                return;
            }
            list = caspacHandler.getMD5sfromCASPAC(CASPAC.toString(), CASUALMeta);
        } catch (ZipException ex) {
            new Log().level3Verbose("Could not read meta");
            return;
        } catch (IOException ex) {
            new Log().level3Verbose("Could not read meta");
            return;
        }
        try {
            BufferedReader buildfile;
            String line;
            String output = "";
            buildfile = new BufferedReader(new FileReader(CASUALMeta));
            MD5sum md5sum = new MD5sum();
            while ((line = buildfile.readLine()) != null) {

                if (md5sum.lineContainsMD5(line)) {
                    unzip = new Unzip(CASPAC);
                    new Log().level3Verbose("MD5" + line);
                    String mdstring = md5sum.pickNewMD5fromArrayList(list, line);
                    String filetocheck = mdstring.split("  ")[1];
                    Enumeration entries = unzip.zipFileEntries;
                    while (entries.hasMoreElements()) {
                        Object e = entries.nextElement();
                        new Log().level3Verbose(e.toString());
                        if (filetocheck.contains(e.toString())) {
                            String newMD5 = new MD5sum().md5sum(Unzip.streamFileFromZip(CASPAC, e));
                            output = output + new MD5sum().makeMD5String(newMD5, e.toString() + "\n");
                        }
                    }
                } else {
                    output = output + line + "\n";
                }
            }
            new FileOperations().overwriteFile(output, CASUALMeta);
            new Log().level3Verbose(output);
        } catch (IOException ex) {
            new Log().errorHandler(ex);
        } finally {
            unzip.closeZip();
        }
        try {

            Zip.addFilesToExistingZip(CASPAC, caspacHandler.meta);
        } catch (IOException ex) {
            new Log().errorHandler(ex);
        }
    }

    //This is only used in IDE mode for development
    private void incrementBuildNumber() throws NumberFormatException {
        Properties prop = new Properties();
        try {
            if (new File(System.getProperty("user.dir") + "/CASUAL/resources/CASUALApp.properties").exists()) {
                prop.load(new FileInputStream(System.getProperty("user.dir") + "/CASUAL/resources/CASUALApp.properties"));
                int x = Integer.parseInt(prop.getProperty("Application.buildnumber").replace(",", ""));
                x++;
                prop.setProperty("Application.buildnumber", Integer.toString(x));
                prop.setProperty("Application.buildnumber", Integer.toString(x));

                prop.store(new FileOutputStream(System.getProperty("user.dir") + "/CASUAL/resources/CASUALApp.properties"), "Application.buildnumber=" + x);
            }
        } catch (IOException ex) {
            new Log().errorHandler(ex);
        }
    }
}
