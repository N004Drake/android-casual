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

    Log log = new Log();
    private boolean DeviceTimerState = false;

    public void startStopADBDeviceCheckTimer(boolean StateCommanded) {
        if (StateCommanded && !DeviceTimerState) {
            Statics.casualConnectionStatusMonitor.DeviceCheck.start();
        } else if (!StateCommanded && DeviceTimerState) {
            Statics.casualConnectionStatusMonitor.DeviceCheck.start();
        }
    }

    public void listScripts() throws IOException {
        CodeSource Src = CASUAL.CASUALApp.class.getProtectionDomain().getCodeSource();
        int Count = 0;
        ArrayList<String> list = new ArrayList<>();
        if (Src != null) {
            URL jar = Src.getLocation();
            ZipInputStream Zip = new ZipInputStream(jar.openStream());
            ZipEntry ZEntry;
            log.level4Debug("Picking Jar File:" + jar.getFile());
            while ((ZEntry = Zip.getNextEntry()) != null) {

                String EntryName = ZEntry.getName();
                if (EntryName.endsWith(".scr")) {
                    list.add(EntryName);
                }
            }
            
            Statics.scriptNames = new String[list.size()];
            for (int n = 0; n < list.size(); n++) {
                String EntryName = ((String) list.get(n)).replaceFirst("SCRIPTS/", "").replace(".scr", "");
                log.level4Debug("Found: " + EntryName);
                Statics.scriptNames[n] = EntryName;
                Count++;
            }

            if (Count == 0) {
                Thread t = new Thread(updateMD5s);
                t.start();
                log.level0Error("IDE Mode: Using " + CASUALApp.defaultPackage + ".scr ONLY!");
                //Statics.scriptLocations = new String[]{""};
                Statics.scriptNames = new String[]{CASUALApp.defaultPackage};
            }
            CASUALapplicationData.ScriptsHaveBeenRecognized = true;
            Zip.close();
        }
    }

    public void md5sumTestScripts() {
        log.level4Debug("\nIDE Mode: Scanning and updating MD5s.\nWe are in " + System.getProperty("user.dir"));
        String x=CASUAL.CASUALApp.class.getResource("resources"+Statics.Slash+"CASUALApp.properties").toString();
        if (CASUAL.CASUALApp.class.getResource("resources"+Statics.Slash+"CASUALApp.properties") != null) {
            incrementBuildNumber();

            String scriptsPath = System.getProperty("user.dir") + Statics.Slash + "SCRIPTS" + Statics.Slash;
            final File folder = new File(scriptsPath);
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.toString().endsWith(".meta")) {
                    String meta = fileEntry.toString();
                    System.out.println("Updating meta: " + meta);
                    String fileContents = new FileOperations().readFile(meta);
                    String[] fileLines = fileContents.split("\\n");
                    String writeOut = "";
                    for (int i = 0; i < fileLines.length; i++) {
                        String line = StringOperations.removeLeadingSpaces(fileLines[i]);
                        if (line.matches("(\\S{31,})(\\s\\s)(.*\\..*)")) {
                            System.out.println(line);
                            String[] md5File = line.split("  ");
                            log.level4Debug("Old MD5: " + md5File[0]);
                            String newMD5 = new MD5sum().md5sum(scriptsPath + md5File[1]);
                            log.level4Debug("New MD5 " + newMD5);
                            writeOut = writeOut + newMD5 + "  " + md5File[1] + "\n";
                        } else {
                            writeOut = writeOut + line + "\n";
                        }

                    }
                    try {
                        new FileOperations().overwriteFile(writeOut, meta);
                    } catch (IOException ex) {
                        log.errorHandler(ex);
                    }
                }
            }
        }
    }

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
                Unzip unzip = new Unzip();
                if (getClass().getResource(ZipResource) != null) {
                    log.level4Debug("Target Script Is resource");
                    try {
                        unzip.unZipResource(ZipResource.toString(), Statics.SelectedScriptFolder);
                    } catch (FileNotFoundException ex) {
                        log.errorHandler(ex);
                    } catch (IOException ex) {
                        log.errorHandler(ex);
                    }
                }
                Statics.lockGUIunzip = false;
            }
        };
        t.start();

        log.level4Debug("Exiting comboBoxUpdate()");
        return t;
    }

    public boolean getIDEMode() {
        //TODO: move this to CASUAL tools
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
            CASUALapplicationData casualPackageData = new CASUALapplicationData();
            casualPackageData.setProperties();
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
                Logger.getLogger(CASUALMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    private Runnable updateMD5s = new Runnable() {
        @Override
        public void run() {
            new CASUALTools().md5sumTestScripts();
        }
    };

    void rewriteMD5OnCASPAC(File CASPAC, CASPACHandler caspacHandler) {
        System.out.println("Writing new CASUAL Package Data!");
        ArrayList list;
        Enumeration zippedFiles;
        String CASUALMeta;
        Unzip unzip=new Unzip();
        try {
            unzip = new Unzip(CASPAC);
        } catch (ZipException ex) {
            Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            zippedFiles = unzip.zipFileEntries;
            CASUALMeta = Statics.TempFolder + caspacHandler.getMetaName(zippedFiles);
            if (CASUALMeta == null) {
                return;
            }
            list = caspacHandler.getMD5sfromCASPAC( CASPAC.toString(), CASUALMeta);
        } catch (ZipException ex) {
            System.out.println("Could not read meta");
            return;
        } catch (IOException ex) {
            System.out.println("Could not read meta");
            return;
        }
        try {
            BufferedReader buildfile;
            String line = "";
            String output = "";
            buildfile = new BufferedReader(new FileReader(CASUALMeta));
            MD5sum md5sum = new MD5sum();
            while ((line=buildfile.readLine())!=null) {
                
                    if (md5sum.lineContainsMD5(line)) {
                        unzip = new Unzip(CASPAC);
                        System.out.println("MD5" + line);
                        String mdstring = md5sum.pickNewMD5fromArrayList(list, line);
                        String filetocheck = mdstring.split("  ")[1];
                        Enumeration entries = unzip.zipFileEntries;
                        while (entries.hasMoreElements()) {
                            Object e = entries.nextElement();
                            System.out.println(e.toString());
                            if (filetocheck.contains(e.toString())) {
                                String newMD5 = new MD5sum().md5sum(new Unzip().streamFileFromZip(CASPAC, e));
                                output = output + new MD5sum().makeMD5String(newMD5, e.toString() + "\n");
                            }
                        }
                    } else {
                        output = output + line+"\n";
                    }
                    line = "";
                }
            new FileOperations().overwriteFile(output, CASUALMeta);
            System.out.println(output);
        } catch (IOException ex) {
            Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            unzip.closeZip();
        }
        try {

            Zip.addFilesToExistingZip(CASPAC, caspacHandler.meta);
        } catch (IOException ex) {
            Logger.getLogger(CASPACHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void incrementBuildNumber() throws NumberFormatException {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(System.getProperty("user.dir") + "/CASUAL/resources/CASUALApp.properties"));
            int x = Integer.parseInt(prop.getProperty("Application.buildnumber").replace(",", ""));
            x++;
            prop.setProperty("Application.buildnumber", Integer.toString(x));
            prop.setProperty("Application.buildnumber", Integer.toString(x));

            prop.store(new FileOutputStream(System.getProperty("user.dir") + "/CASUAL/resources/CASUALApp.properties"), "Application.buildnumber=" + x);
        } catch (IOException ex) {
            Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
