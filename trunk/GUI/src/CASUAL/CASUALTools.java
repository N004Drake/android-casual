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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author adam
 */
public class CASUALTools {
    final public String defaultPackage="ATT GS3 Root";
    
    Log log = new Log();
    private boolean DeviceTimerState = false;



    public void startStopADBDeviceCheckTimer(boolean StateCommanded) {
        if (StateCommanded && !DeviceTimerState) {
            Statics.DeviceMonitor.DeviceCheck.start();
        } else if (!StateCommanded && DeviceTimerState) {
            Statics.DeviceMonitor.DeviceCheck.start();
        }
    }

    public void listScripts() throws IOException {
        CodeSource Src = CASUAL.CASUALApp.class.getProtectionDomain().getCodeSource();
        int Count = 0;
        ArrayList<String> list = new ArrayList();
        if (Src != null) {
            URL jar = Src.getLocation();
            ZipInputStream Zip = new ZipInputStream(jar.openStream());
            ZipEntry ZEntry;
            log.level3("Picking Jar File:" + jar.getFile());
            while ((ZEntry = Zip.getNextEntry()) != null) {

                String EntryName = ZEntry.getName();
                if (EntryName.endsWith(".scr")) {
                    list.add(EntryName);
                }
            }
            //Statics.scriptLocations = new String[list.size()];
            Statics.scriptNames = new String[list.size()];
            for (int n = 0; n < list.size(); n++) {
                String EntryName = ((String) list.get(n)).replaceFirst("SCRIPTS/", "").replace(".scr", "");
                log.level3("Found: " + EntryName);
                Statics.scriptNames[n] = EntryName;
                Count++;
            }

            if (Count == 0) {
                new CASUALTools().md5sumTestScripts();
                log.level0("IDE Mode: Using "+CASUALApp.defaultPackage+".scr ONLY!");
                //Statics.scriptLocations = new String[]{""};
                Statics.scriptNames = new String[]{"WindowsLinux Galaxy Note 2 Jailbreak"};
            }
            CASUALPackageData.ScriptsHaveBeenRecognized=true;
        }
    }

    public void md5sumTestScripts() {
        log.level3("\nIDE Mode: Scanning and updating MD5s.\nWe are in " + System.getProperty("user.dir"));

        String scriptsPath = System.getProperty("user.dir") + Statics.Slash + "src" + Statics.Slash + "SCRIPTS" + Statics.Slash;
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
                    if (line.matches("(\\S{32,})(\\s\\s)(.*\\..*)")) {
                        System.out.println(line);
                        String[] md5File = line.split("  ");
                        log.level3("Old MD5: " + md5File[0]);
                        String newMD5 = new MD5sum().md5sum(scriptsPath + md5File[1]);
                        log.level3("New MD5 " + newMD5);
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

    public void prepareCurrentScript(String scriptName) {
        Statics.SelectedScriptFolder = Statics.TempFolder + scriptName;
        //set the ZipResource
        final String ZipResource = Statics.TargetScriptIsResource ? (Statics.ScriptLocation + scriptName + ".zip") : (scriptName + ".zip");


        Thread t;
        t = new Thread() {
            @Override
            public void run() {
                try {
                    Statics.GUI.enableControls(false);
                } catch (NullPointerException ex) {
                    log.level3("attempted to lock controls but controls are not availble yet");
                }
                Statics.lockGUIunzip = true;
                if (getClass().getResource(ZipResource) != null) {
                    log.level3("Extracting archive....");

                    Unzip Unzip = new Unzip();
                    try {
                        Unzip.UnZipResource(ZipResource.toString(), Statics.SelectedScriptFolder);
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

        log.level3("Exiting comboBoxUpdate()");
    }

}
