/*Script provides a way to readn and write Script information for a Caspac
 *Copyright (C) 2013  Adam Outler & Logan Ludington
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
package CASUAL.caspac;

import CASUAL.CASUALMessageObject;
import CASUAL.CASUALTools;
import CASUAL.FileOperations;
import CASUAL.Locks;
import CASUAL.Log;
import CASUAL.Statics;
import CASUAL.misc.StringOperations;
import CASUAL.Unzip;
import CASUAL.Zip;
import CASUAL.crypto.MD5sum;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipException;

/**
 *
 * @author loganludington
 */
public class Script {
    /*
     * extractionMethod = 
     * 0 for CASPAC  (File, zipFile/zipFile)
     * 1 for CASUAL  (Resource, /SCRIPTS/zipFile
     * 2 for Filesystem (File, zipFile)
     */

    final int CASPAC = 0;
    final int CASUAL = 1;
    final int FILE = 2;
    final public int extractionMethod;
    public Object scriptZipFile; //zipFile Entry, Resource or File on disk
    public Unzip zipfile; //CASPAC only.
    final public String name;
    final public String tempDir;
    public String scriptContents = "";
    public List<File> individualFiles = new ArrayList<File>();
    public meta metaData = new meta();
    public String discription = "";
    public boolean scriptContinue = false;
    private static String slash = System.getProperty("file.separator");
    public String deviceArch = "";
    Map<? extends String, ? extends InputStream> getAllAsStringAndInputStream;
    public List<String> actualMD5s = new ArrayList<String>();

    public Script(Script s) {
        new Log().level4Debug("Setting up script " + s.name + " from preexisting script");
        this.name = s.name;
        this.tempDir = s.tempDir;
        this.extractionMethod = 2;
        this.metaData = s.metaData;
        this.individualFiles = s.individualFiles;
        this.zipfile = s.zipfile;
        this.discription = s.discription;
        this.scriptContinue = s.scriptContinue;
        this.getAllAsStringAndInputStream = s.getAllAsStringAndInputStream;
    }

    public Script(String name, String tempDir) {
        new Log().level4Debug("Setting up script " + name + " with name and tempdir");
        this.name = name;
        this.tempDir = tempDir;
        this.extractionMethod = 0;
    }

    public Script(String name, String tempDir, int type) {
        new Log().level4Debug("Setting up script " + name + " with name, tempdir and type");
        this.name = name;
        this.tempDir = tempDir;
        this.extractionMethod = type;
    }

    public Script(String name, String script, String discription, List<File> includeFiles, String tempDir) {
        new Log().level4Debug("Setting up script " + name + " with name, script, description, included files and tempdir");
        this.discription = discription;
        this.name = name;
        this.scriptContents = script;
        this.individualFiles = includeFiles;
        this.tempDir = tempDir;
        extractionMethod = 0;
    }

    public Script(String name, String script, String discription,
            List<File> includeFiles, Properties prop, String tempDir, int type) {
        new Log().level4Debug("Setting up script " + name + " with name, script, description, included files, propeties, type and tempdir");
        this.discription = discription;
        this.name = name;
        this.scriptContents = script;
        this.individualFiles = includeFiles;
        this.metaData = new meta(prop);
        this.tempDir = tempDir;
        this.extractionMethod = type;
    }

    public Script(String name, String script, String discription,
            List<File> includeFiles, Properties prop, String tempDir) {
        new Log().level4Debug("Setting up script " + name + " with name, script, description includedFiles, properties, and tempdir");
        this.discription = discription;
        this.name = name;
        this.scriptContents = script;
        this.individualFiles = includeFiles;
        this.metaData = new meta(prop);
        this.tempDir = tempDir;
        extractionMethod = 0;
    }

    public Script(String name, String script, String discription, String tempDir) {
        new Log().level4Debug("Setting up script " + name + " with name, script, description and tempdir");
        this.name = name;
        this.scriptContents = script;
        this.discription = discription;
        this.tempDir = tempDir;
        extractionMethod = 0;
    }

    public boolean verifyScript() {
        boolean testingBool = true;
        testingBool = !(name.isEmpty()) && testingBool;
        testingBool = !(scriptContents.isEmpty()) && testingBool;
        testingBool = !(individualFiles.isEmpty()) && testingBool;
        testingBool = !(discription.isEmpty()) && testingBool;
        testingBool = metaData.verifyMeta() && testingBool;
        return testingBool;
    }

    public DataInputStream getScriptContents() {
        InputStream is = StringOperations.convertStringToStream(scriptContents);
        return new DataInputStream(is);
    }

    @Override
    public String toString() {
        return name;
    }

    private void addMD5ToMeta(String linuxMD5, int md5Position) {
        new Log().level3Verbose("evaluated MD5 to " + linuxMD5);
        metaData.metaProp.setProperty("Script.MD5[" + md5Position + "]", linuxMD5);
    }

    private void addMD5ToMeta(MD5sum md5sum, String filePath, int md5Position) {
        String linuxMD5 = md5sum.getLinuxMD5Sum(new File(filePath));
        new Log().level3Verbose("evaluated MD5 to " + linuxMD5);
        metaData.metaProp.setProperty("Script.MD5[" + md5Position + "]", linuxMD5);
    }

    /*
     * extracts includedFiles from zip
     */
    public Runnable getExtractionRunnable() {
        final Log log = new Log();
        if (this.extractionMethod == CASPAC) {  //This is a CASPAC
            final Unzip myCASPAC = this.zipfile;
            final Object entry = this.scriptZipFile;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    log.level4Debug("Examining CASPAC mode package contents");
                    BufferedInputStream bis = null;
                    try {
                        new Log().level4Debug("Unzipping CASPAC member " + name);
                        bis = myCASPAC.streamFileFromZip(entry);
                        actualMD5s.add(new MD5sum().getLinuxMD5Sum(bis, entry.toString()));
                        bis = myCASPAC.streamFileFromZip(entry);
                        Unzip.unZipInputStream(bis, tempDir);
                        bis.close();
                        log.level4Debug("Extracted entry " + myCASPAC.getEntryName(entry) + "to " + tempDir);

                    } catch (ZipException ex) {
                        new Log().errorHandler(ex);
                    } catch (IOException ex) {
                        new Log().errorHandler(ex);
                    } finally {
                        try {
                            if (bis != null) {
                                bis.close();
                            }
                        } catch (IOException ex) {
                            new Log().errorHandler(ex);
                        }
                    }
                    File[] files = new File(tempDir).listFiles();
                    if (files != null) {
                        individualFiles.addAll(Arrays.asList(files));
                        for (String md5 : metaData.md5s) {
                            if (!Arrays.asList(actualMD5s.toArray(new String[]{})).contains(md5)) {
                                new Log().level4Debug("Could not find " + md5 + " in list " + StringOperations.arrayToString(actualMD5s.toArray(new String[]{})));
                                new CASUALMessageObject("@interactionPackageCorrupt").showErrorDialog();
                                if (!Caspac.debug) {
                                    scriptContents = "";
                                }
                            }
                        }
                    }
                }
            };
            Locks.caspacScriptPrepLock=false;
            return r;
        }
        if (this.extractionMethod == CASUAL) {  //This is a CASUAL
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    if (scriptZipFile != null && !scriptZipFile.toString().isEmpty()) {
                        if (CASUALTools.IDEMode) {
                            try {
                                log.level4Debug("Examining IDE mode script contents" + scriptZipFile.toString());
                                actualMD5s.add(new MD5sum().getLinuxMD5Sum(new File((String) scriptZipFile)));
                                Unzip unzip = new Unzip(new File((String) scriptZipFile));
                                unzip.unzipFile(tempDir);
                            } catch (ZipException ex) {
                                new Log().errorHandler(ex);
                            } catch (IOException ex) {
                                new Log().errorHandler(ex);
                            }
                        } else {
                            try {
                                log.level4Debug("Examining CASUAL mode script contents:" + scriptZipFile.toString());
                                actualMD5s.add(new MD5sum().getLinuxMD5Sum(getClass().getResourceAsStream("/" + scriptZipFile.toString()), scriptZipFile.toString()));
                                new Log().level4Debug("unzip of " + scriptZipFile.toString() + " is beginning.");
                                Unzip.unZipResource("/" + scriptZipFile.toString(), tempDir);
                            } catch (FileNotFoundException ex) {
                                new Log().errorHandler(ex);
                            } catch (IOException ex) {
                                new Log().errorHandler(ex);
                            }
                            new Log().level4Debug("unzip of " + name + " is complete.");
                        }
                    } else {
                        new Log().level3Verbose("script Zipfile was null");
                    }
                    /*
                     * CASUAL do not receive MD5s
                     */
                }
            };
            Locks.caspacScriptPrepLock=false;
            return r;
        }
        if (this.extractionMethod == FILE) { //This is running on the filesystem
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    log.level4Debug("Examining updated script contents on filesystem");
                    actualMD5s.add(new MD5sum().getLinuxMD5Sum(new File(scriptZipFile.toString())));
                    String ziplocation = scriptZipFile.toString();
                    try {
                        Unzip unzip = new Unzip(ziplocation);
                        log.level4Debug("Unzipping from " + ziplocation + " to " + tempDir);
                        unzip.unzipFile(tempDir);
                    } catch (ZipException ex) {
                        new Log().errorHandler(ex);
                    } catch (IOException ex) {
                        new Log().errorHandler(ex);
                    }
                    log.level4Debug("examining MD5s");
                    for (String md5 : metaData.md5s) {
                        if (!(Arrays.asList(actualMD5s.toArray()).contains(md5))) {
                            log.level4Debug("Md5 mismatch!!  Expected:" + md5);

                            if (!Caspac.debug) {
                                scriptContents = "";
                            }
                        }
                    }
                    if (!scriptContents.equals("")) {
                        new Log().level4Debug("Update sucessful.  MD5s matched server.");
                    } else {
                        new CASUALMessageObject("@interactionPackageCorrupt").showErrorDialog();
                    }
                }
            };
            Locks.caspacScriptPrepLock=false;
            return r;
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
            }
        };

        Locks.caspacScriptPrepLock=false;
        return r;

    }

    Map<String, InputStream> getScriptAsMapForCASPAC() {
        CASUAL.Log log = new CASUAL.Log();
        CASUAL.crypto.MD5sum md5sum = new CASUAL.crypto.MD5sum();
        Map<String, InputStream> scriptEntries = new HashMap<String, InputStream>();
        ArrayList<String> tempMD5s = new ArrayList<String>();

        //get md5 and stream for script
        tempMD5s.add(md5sum.getLinuxMD5Sum(StringOperations.convertStringToStream(scriptContents), name + ".scr"));
        scriptEntries.put(name + ".scr", StringOperations.convertStringToStream(scriptContents));

        //get md5 and stream for txt
        tempMD5s.add(md5sum.getLinuxMD5Sum(StringOperations.convertStringToStream(this.discription), name + ".txt"));
        scriptEntries.put(name + ".txt", StringOperations.convertStringToStream(this.discription));

        //get md5 and stream for zip
        //go to folder above and create stream
        File masterTempDir = new File(tempDir).getParentFile();
        File instanceZip = new File(masterTempDir + Statics.Slash + name + ".zip");
        if (!instanceZip.exists()) {
            try {
                instanceZip.createNewFile();
            } catch (IOException ex) {
                new Log().errorHandler(ex);
            }
        }
        new Log().level3Verbose("set script $ZIPFILE to " + instanceZip.getAbsolutePath());
        try {
            Zip zip;

            zip = new Zip(instanceZip);
            zip.addFilesToExistingZip(individualFiles.toArray(new File[individualFiles.size()]));



            log.level3Verbose("Adding zip:" + instanceZip.getAbsolutePath());

            tempMD5s.add(new CASUAL.crypto.MD5sum().getLinuxMD5Sum(instanceZip));
            scriptEntries.put(name + ".zip", new FileInputStream(instanceZip.getAbsoluteFile()));

        } catch (IOException ex) {
            new Log().errorHandler(ex);
        }



        //update MD5s and update meta
        for (int i = 0; i < tempMD5s.size(); i++) {
            this.addMD5ToMeta(tempMD5s.get(i), i);
        }
        this.actualMD5s = tempMD5s;
        //get meta
        scriptEntries.put(name + ".meta", this.metaData.getMetaInputStream());

        return scriptEntries;

    }

    public void performUnzipAfterScriptZipfileUpdate() throws ZipException, IOException {
        this.getExtractionRunnable().run();
    }

    public class meta {

        public String minSVNversion = "";
        public String scriptRevision = "";
        public String uniqueIdentifier = "";
        public String supportURL = "";
        public String updateMessage = "";
        public String killSwitchMessage = "";
        public Properties metaProp;
        public List<String> md5s = new ArrayList<String>();

        public meta() {
            metaProp = new Properties();
        }

        public meta(Properties prop) {
            metaProp = prop;
            setVariablesFromProperties(prop);
        }

        public meta(InputStream prop) throws IOException {
            metaProp.load(prop);
            setVariablesFromProperties(metaProp);
        }

        /**
         * verifies metadata is not empty
         *
         * @return true if filled in
         */
        public boolean verifyMeta() {
            boolean testingBool = true;
            testingBool = !(minSVNversion.isEmpty()) && testingBool;
            testingBool = !(scriptRevision.isEmpty()) && testingBool;
            testingBool = !(uniqueIdentifier.isEmpty()) && testingBool;
            testingBool = !(supportURL.isEmpty()) && testingBool;
            testingBool = !(updateMessage.isEmpty()) && testingBool;
            testingBool = !(killSwitchMessage.isEmpty()) && testingBool;
            return testingBool;

        }

        public InputStream getMetaInputStream() {
            setPropsFromVariables();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                metaProp.store(output, "This properties file was generated by CASUAL");
            } catch (IOException ex) {
                new Log().errorHandler(ex);
            }
            return new ByteArrayInputStream(output.toByteArray());

        }

        /**
         * writes meta properties to a file
         *
         * @param output file to write
         * @return true if file was written
         * @throws FileNotFoundException
         * @throws IOException
         */
        public boolean write(String output) throws FileNotFoundException, IOException {
            File f = new File(output);
            return write(f);
        }

        /**
         * writes meta properties to a file
         *
         * @param output file to write
         * @return true if file was written
         * @throws FileNotFoundException
         * @throws IOException
         */
        public boolean write(File output) throws FileNotFoundException, IOException {
            setPropsFromVariables();
            FileOutputStream fos = new FileOutputStream(output);
            metaProp.store(fos, "This properties file was generated by CASUAL");
            return new FileOperations().verifyExists(output.toString());
        }

        /**
         * sets the properties object from local variables for writing
         */
        public void setPropsFromVariables() {
            metaProp.setProperty("CASUAL.minSVN", minSVNversion);
            metaProp.setProperty("Script.Revision", scriptRevision);
            metaProp.setProperty("Script.ID", uniqueIdentifier);
            metaProp.setProperty("Script.SupportURL", supportURL);
            metaProp.setProperty("Script.UpdateMessage", updateMessage);
            metaProp.setProperty("Script.KillSwitchMessage", killSwitchMessage);
        }

        /**
         * sets the variables from properties object for loading
         *
         * @param prop properties file
         */
        private void setVariablesFromProperties(Properties prop) {
            minSVNversion = prop.getProperty("CASUAL.minSVN", "");
            scriptRevision = prop.getProperty("Script.Revision", "");
            uniqueIdentifier = prop.getProperty("Script.ID", "");
            supportURL = prop.getProperty("Script.SupportURL", "");
            updateMessage = prop.getProperty("Script.UpdateMessage", "");
            killSwitchMessage = prop.getProperty("Script.KillSwitchMessage", "");
            md5s = new ArrayList<String>();
            int i = 0;
            while (!prop.getProperty("Script.MD5[" + i + "]", "").equals("")) {
                md5s.add(prop.getProperty("Script.MD5[" + i + "]"));
                i++;
            }


        }

        public void load(Properties prop) {
            this.metaProp = prop;
            setVariablesFromProperties(metaProp);
        }

        void load(BufferedInputStream streamFileFromZip) {
            try {
                this.metaProp.load(streamFileFromZip);
            } catch (IOException ex) {
                new Log().errorHandler(ex);
            }
            setVariablesFromProperties(metaProp);

        }

        public int minSVNversion() {
            return Integer.parseInt(minSVNversion);
        }
    }
}
