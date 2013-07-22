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

import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.MD5sum;
import CASUAL.Statics;
import CASUAL.StringOperations;
import CASUAL.Unzip;
import CASUAL.Zip;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

/**
 *
 * @author loganludington
 */
public class Script{
    /*
     * extractionMethod = 
     * 0 for CASPAC  (File, zipFile/zipFile)
     * 1 for CASUAL  (Resource, /SCRIPTS/zipFile
     * 2 for Filesystem (File, zipFile)
     */
    final public int extractionMethod;
    public Object scriptZipFile; //zipFile Entry, Resource or File on disk
    
    public Unzip zipfile; //CASPAC only.
    final public String name;
    final public String tempDir;
    public String scriptContents = "";
    public List<File> individualFiles = new ArrayList<>();
    public meta metaData = new meta();
    public String discription = "";

    private static String slash = System.getProperty("file.separator");
    Map<? extends String, ? extends InputStream> getAllAsStringAndInputStream;

    
    
    public Script(String name, String tempDir) {
        this.name = name;
        this.tempDir = tempDir;
        this.extractionMethod = 0;
    }
    public Script(String name, String tempDir,int type) {
        this.name = name;
        this.tempDir = tempDir;
        this.extractionMethod = type;
    }
    public Script(String name, String script, String discription, List<File> includeFiles, String tempDir) {
        this.discription = discription;
        this.name = name;
        this.scriptContents = script;
        this.individualFiles = includeFiles;
        this.tempDir = tempDir;
        extractionMethod = 0;
    }

    public Script(String name, String script, String discription,
            List<File> includeFiles, Properties prop, String tempDir, int type) {
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
        this.discription = discription;
        this.name = name;
        this.scriptContents = script;
        this.individualFiles = includeFiles;
        this.metaData = new meta(prop);
        this.tempDir = tempDir;
        extractionMethod = 0;
    }

    public Script(String name, String script, String discription, String tempDir) {
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
        if (this.extractionMethod==0){  //This is a CASPAC
            final Unzip CASPAC=this.zipfile;
            final Object entry = this.scriptZipFile;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    BufferedInputStream bis = null;
                    try {
                        Log log = new Log();
                        new Log().level4Debug("Unzipping " + name);
                        log.level3Verbose("Grabbing Entry "+CASPAC.getEntryName(entry));
                        bis = CASPAC.streamFileFromZip(entry);
                        log.level4Debug("Setup InputStream. Extracting to"+tempDir);
                        Unzip.unZipInputStream(bis, tempDir);
                        log.level4Debug("Extracted entry "+CASPAC.getEntryName(entry) + "to "+tempDir);

                    } catch (ZipException ex) {
                        Logger.getLogger(Script.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Script.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            if (bis!=null)bis.close();
                        } catch (IOException ex) {
                            Logger.getLogger(Script.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    File[] files =new File(tempDir).listFiles();
                    individualFiles.addAll(Arrays.asList(files));
                }
            };
            return r;
        }
        if (this.extractionMethod==1){  //This is a CASUAL
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    
                }
            };
            return r;
        }
        if (this.extractionMethod==2){ //This is running on the filesystem
            Runnable r = new Runnable() {
                @Override
                public void run() {

                }
            };
            return r;
        }
        
        Runnable r = new Runnable() {
            @Override
            public void run() {

            }
        };
        
        
        return r;

    }

    Map<? extends String, ? extends InputStream> getScriptAsMapForCASPAC() {
        CASUAL.Log log = new CASUAL.Log();
        int md5Position = 0;
        CASUAL.MD5sum md5sum = new CASUAL.MD5sum();
        Map<String, InputStream> scriptEntries=new HashMap<>();
        ArrayList<String> tempMD5s=new ArrayList<>();
        
        //get md5 and stream for script
        tempMD5s.add(md5sum.getLinuxMD5Sum(StringOperations.convertStringToStream(scriptContents),name+".scr"));
        scriptEntries.put(name+".scr", StringOperations.convertStringToStream(scriptContents));
        
        //get md5 and stream for txt
        tempMD5s.add(md5sum.getLinuxMD5Sum(StringOperations.convertStringToStream(this.discription),name+".txt"));
        scriptEntries.put(name+".txt", StringOperations.convertStringToStream(this.discription));
        
        //get md5 and stream for zip
        //go to folder above and create stream
        File masterTempDir=new File(tempDir).getParentFile();
        File instanceZip=new File(masterTempDir+Statics.Slash+name+".zip");
        if (! instanceZip.exists()){
            try {
                instanceZip.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Script.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        new Log().level3Verbose("set script $ZIPFILE to "+instanceZip.getAbsolutePath());
        try {
            Zip zip;

            zip = new Zip(instanceZip);
            zip.addFilesToExistingZip(individualFiles.toArray(new File[individualFiles.size()]));
            log.level3Verbose("Adding zip:"+instanceZip.getAbsolutePath());
            scriptEntries.put(name+".zip",new FileInputStream(instanceZip.getAbsoluteFile()));
        } catch (IOException ex) {
            Logger.getLogger(Script.class.getName()).log(Level.SEVERE, null, ex);
        }


        
        //update MD5s and update meta
        for (int i=0; i<tempMD5s.size();i++){
            this.addMD5ToMeta(tempMD5s.get(i), md5Position);
        }
        
        //get meta
        scriptEntries.put(name+".meta",this.metaData.getMetaInputStream());         

        return scriptEntries;
       
    }

    public class meta {

        public String minSVNversion = "";
        public String scriptRevision = "";
        public String uniqueIdentifier = "";
        public String supportURL = "";
        public String updateMessage = "";
        public String killSwitchMessage = "";
        public Properties metaProp;
        List<String> md5s = new ArrayList<>();

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
        public InputStream getMetaInputStream(){
            setPropsFromVariables();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                metaProp.store(output, "This properties file was generated by CASUAL");
            } catch (IOException ex) {
                Logger.getLogger(Script.class.getName()).log(Level.SEVERE, null, ex);
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

        void doMd5Sum() {
            MD5sum md5sum = new MD5sum();
            //TODO: zip resources in this class
            //TODO: get md5sum the script.zip file
            //TODO: get md5sum for the script.scr file

        }

        /**
         * sets the properties object from local variables for writing
         */
        private void setPropsFromVariables() {
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
        }

        public void load(Properties prop){
            this.metaProp=prop;
            setVariablesFromProperties(metaProp);
        }
        void load(BufferedInputStream streamFileFromZip) {
            try {
                this.metaProp.load(streamFileFromZip);
            } catch (IOException ex) {
                Logger.getLogger(Script.class.getName()).log(Level.SEVERE, null, ex);
            }
            setVariablesFromProperties(metaProp);

        }

        public int minSVNversion() {
            return Integer.parseInt(minSVNversion);
        }
    }
}
