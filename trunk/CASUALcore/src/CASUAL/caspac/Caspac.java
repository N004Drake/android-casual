/*Caspac handles gathering, reading and writing of CASPACs in a unified manner
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

import CASUAL.BooleanOperations;
import CASUAL.CASUALTools;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.Statics;
import CASUAL.StringOperations;
import CASUAL.Unzip;
import CASUAL.Zip;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import javax.imageio.ImageIO;

/**
 *
 * @author adam
 */
public class Caspac {

    //public File logo;
    public BufferedImage logo;
    public final File CASPAC;
    public String overview;
    public Build build;
    public ArrayList<Script> scripts = new ArrayList<>();
    public final String TempFolder;
    public Log log = new Log();
    private ArrayList<File> unzipQueue = new ArrayList<>();
    FileOperations fo = new FileOperations();
    private ArrayList<Thread> unzipThreads=new ArrayList<>();
    public boolean isAZipFile;

    public Caspac(File caspac, String tempDir) throws IOException {
        this.CASPAC = caspac;
        this.TempFolder = tempDir;
        isAZipFile = false;
        //if the CASPAC exists lets try to grab the non-script files 
        if (fo.verifyExists(CASPAC.getAbsolutePath()) && CASPAC.canRead()) {
            try {
                Unzip unzip = new Unzip(CASPAC);
                Enumeration cpEnumeration = unzip.zipFileEntries;
                if (cpEnumeration.hasMoreElements()) {
                    isAZipFile = true;
                    while (cpEnumeration.hasMoreElements()) {
                        Object o = cpEnumeration.nextElement();
                        if (unzip.getEntryName(o).contains("-Overview.txt")) {
                            this.overview = fo.readTextFromStream(unzip.streamFileFromZip(o));
                        }
                        if (unzip.getEntryName(o).contains("-build.properties")) {
                            this.build = new Build(unzip.streamFileFromZip(o));
                        }
                        if (unzip.getEntryName(o).contains("-logo.png")) {
                            logo = ImageIO.read(unzip.streamFileFromZip(o));
                            //unzip.deployFileFromZip(o, TempFolder);
                            //logo = new File(TempFolder + "-logo.png");
                        }
                    }
                }

            } catch (ZipException ex) {
            }



        }



    }



    /**
     * adds a Script
     *
     * @param script completed Script class
     */
    public void addScript(Script script) {
        if (!(scripts.contains(script))) {
            scripts.add(script);
            log.level4Debug("Adding Script: " + script.getName());
        }
    }

    /**
     * removes a script
     *
     * @param script Script reference
     */
    public void removeScript(Script script) {
        if (scripts.contains(script)) {
            scripts.remove(script);
            log.level4Debug("Removing Script: " + script.getName());
        }
    }

    public File getCaspac() {
        return CASPAC;
    }

    /**
     * writes a CASPAC
     *
     * @throws IOException
     */
    public void write() throws IOException {
        if (!(new File(TempFolder).exists())) {
            new File(TempFolder).mkdir();
        }
        for (Script s : scripts) {
            log.level4Debug("Writing Script: " + s.getName());
            s.writeScript(new File(TempFolder));
        }
        if (!(new File(TempFolder + "-Overview.txt")).exists()) {
            log.level4Debug("Writing Overview to: \n\t" + TempFolder.toString() + "-Overview.txt");
            fo.writeToFile(overview, TempFolder.toString() + "-Overview.txt");
        }
        if (!(new File(TempFolder + "-build.properties")).exists()) {
            log.level4Debug("Writing build.properties to: \n\t" + TempFolder + Statics.Slash + "-build.properties");
            build.write(TempFolder + "-build.properties");

        }
        if (!(new File(TempFolder + Statics.Slash + build.bannerPic).exists()) && !(build.bannerPic.isEmpty())) {
            log.level4Debug("Writing Overview to: \n\t" + TempFolder.toString()
                    + build.bannerPic.substring(build.bannerPic.lastIndexOf(Statics.Slash) + 1,
                    build.bannerPic.length()));
            fo.copyFile(new File(build.bannerPic), new File(TempFolder
                    + build.bannerPic.substring(build.bannerPic.lastIndexOf(Statics.Slash) + 1,
                    build.bannerPic.length())));
        }
        log.level4Debug("Creating new Zip at: \n\t" + CASPAC.toString());
        Zip zip = new Zip(CASPAC);
        log.level4Debug("Placeing the following files in the caspac Zip");
        zip.execute(TempFolder);
        cleanCaspacWrite();
    }

    /**
     * sets build properties
     *
     * @param prop properties file
     */
    public void setBuild(Properties prop) {
        build = new Build(prop);
        build.loadPropsToVariables();
    }
    
    public void loadFirstScriptFromCASPAC() throws ZipException, IOException{
        log.level4Debug("\n\n\nStarting CASPAC unzip.");
        String scriptName="";
        Unzip unzip = new Unzip(CASPAC);
        while (unzip.zipFileEntries.hasMoreElements()) {
            Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
            String filename = unzip.getEntryName(entry);
            boolean isScript=!handleCASPACInformationFiles(filename, unzip, entry);
            if ((isScript && scriptName.isEmpty()) || (isScript && scriptName.equals(entry) )){
                handleScriptFiles(filename, unzip, entry);
            }
            
        }
        log.level4Debug("Starting to unzip script zips");
        

        
        performUnzipOnQueue();
    }
    
    

    /**
     * loads a CASPAC.zip file
     *
     * @throws ZipException
     * @throws IOException
     */
    public void load() throws ZipException, IOException {

        log.level4Debug("\n\n\nStarting CASPAC unzip.");
        Unzip unzip = new Unzip(CASPAC);
        while (unzip.zipFileEntries.hasMoreElements()) {
            Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
            handleCASPACFiles(entry, unzip);
        }
        log.level4Debug("Starting to unzip script zips");
        performUnzipOnQueue();
        log.level4Debug("CASPAC load completed.");
    }

    public void waitForUnzipAndRun(Runnable action){
        waitForUnzipAndRun(action,false, null);
    }
    public void waitForUnzipAndRun(Runnable action, boolean onASeparateThread, String ThreadName){
        waitForUnzipComplete();
        if (onASeparateThread){
            Thread t=new Thread(action);
            t.setName(ThreadName);
            t.start();
        }  else {
            action.run();
        }
        
        
    }
    public void waitForUnzipComplete(){
        boolean[] isUnzipping=new boolean[unzipThreads.size()];
        Arrays.fill(isUnzipping, Boolean.TRUE);
        while (BooleanOperations.containsTrue(isUnzipping)){
            CASUALTools.sleepForOneTenthOfASecond();
            for (int i = 0; i<isUnzipping.length; i++){
                if (!unzipThreads.get(i).isAlive()){
                    isUnzipping[i]=false;
                }
            }
        }
        log.level4Debug("Unzipping complete.");
    }

    /**
     * handles each CASPAC file appropriately
     *
     * @param entry entry from CASPAC
     * @param pack CASPAC file to be processed
     * @throws IOException
     */
    private void handleCASPACFiles(Object entry, Unzip pack) throws IOException {

        //get the filename from the entry
        String filename = pack.getEntryName(entry);
        boolean isScript=!handleCASPACInformationFiles(filename, pack, entry);
        if (isScript){
            handleScriptFiles(filename, pack, entry);
        }
    }

    /**
     * script instance which is being referenced. creates a new script if not found
     *
     * @param fileName filename of script
     * @return script instance of script to be processed
     */
    private Script getScriptInstanceByFilename(String fileName) {
        for (Script s : scripts) {
            if (s.getName().equals(fileName.substring(0, fileName.lastIndexOf(".")))) {
                return s;
            }
        }
        Script script = new Script(fileName.substring(0, fileName.lastIndexOf(".")));
        //Add script 
        scripts.add(script);
        return scripts.get(scripts.indexOf(script));
    }
    /**
     * script instance which is being referenced
     *
     * @param fileName filename of script
     * @return script instance of script to be processed null if not found
     */
    public Script getScriptByFilename(String fileName) {
        for (Script s : scripts) {
            if (s.getName().equals(fileName.substring(0, fileName.lastIndexOf(".")))) {
                return s;
            }
        }
        return null;
    }

    
    /**
     * returns all script names
     *
     * @return list of script names
     */
    public String[] getScriptNames() { //TODO: examine this to figure out why we are iterating "scripts" and adding a slash while getting names
        ArrayList<String> scriptNames = new ArrayList<>();
        for (Script s : scripts) {
            scriptNames.add(s.getName());
        }
        return StringOperations.convertArrayListToStringArray(scripts);
    }
    
    /**
     * gets script by name
     * @param name name of script to be pulled
     * @return Script object or null
     */
    public Script getScriptByName(String name){
        for (Script s : scripts) {
            if (s.getName().equals(name)){
                return s;
            }
        }
        return null;
    }

    /**
     * TODO: should this be endswith(".scr")???
     */
    private void cleanCaspacWrite() {
        for (File f : new File(Statics.TempFolder).listFiles()) {
            if ((f.toString().endsWith(".script") || f.toString().endsWith(".zip")) && f.isDirectory()) {
                fo.recursiveDelete(f);
            }
        }
    }

    private void performUnzipOnQueue() {
        for (final File zipFileName : unzipQueue) {

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        new Log().level4Debug("Unzipping " + zipFileName.getName());
                        Unzip unzip = new Unzip(zipFileName.toString());
                        new File(zipFileName.toString().replace(".zip", "")).mkdir();
                        String tempFolder=zipFileName.toString().replace(".zip", "");
                        unzip.unzipFile(tempFolder);
                        log.level4Debug("Addind zip contents to script Included Files");
                        Script scriptInstance = getScriptInstanceByFilename(zipFileName.getName());
                        int i = scripts.indexOf(scriptInstance);
                        scriptInstance.includeFiles.addAll(Arrays.asList(new File(tempFolder).listFiles()));
                        scriptInstance.TempFolder=tempFolder;
                        log.level4Debug("Added files from " + zipFileName.getName() + " to " + scriptInstance.getName() + ".");
                        scripts.set(i, scriptInstance);
                    } catch (ZipException ex) {
                        Logger.getLogger(Caspac.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Caspac.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            unzipThreads.add(new Thread(r));
            unzipThreads.get(unzipThreads.size()-1).start();
        }
    }

    private void setBuildPropInformation(Unzip pack, Object entry) throws IOException {
        log.level4Debug("Found -build.properties adding information to "
                + "CASPAC");
        build = new Build(pack.streamFileFromZip(entry));
        build.loadPropsToVariables();
    }

    private void extractCASPACBanner(Unzip pack, Object entry, String filename) throws IOException {
        log.level4Debug("Found logo adding information to "
                + "CASPAC");
        fo.writeStreamToFile(pack.streamFileFromZip(entry), TempFolder + filename);
        build.bannerPic = TempFolder + filename;
    }

    private void setOverview(Unzip pack, Object entry) throws IOException {
        log.level4Debug("Found -Overview.txt adding information to "
                + "CASPAC");
        overview = fo.readTextFromStream(pack.streamFileFromZip(entry));
    }

    private boolean handleCASPACInformationFiles(String filename, Unzip pack, Object entry) throws IOException {
        boolean isCASPAC=false;
        if (filename.equals("-build.properties")) {
            setBuildPropInformation(pack, entry);
            isCASPAC=true;
        } else if (filename.endsWith(".png")) {
            extractCASPACBanner(pack, entry, filename);
            isCASPAC=true;
        } else if (filename.toString().equals("-Overview.txt")) {
            setOverview(pack, entry);
            isCASPAC=true;
        }
        return isCASPAC;
    }

    private void handleScriptFiles(String filename, Unzip pack, Object entry) throws IOException {
        boolean isScript=false;
        
        if (filename.toString().endsWith(".meta")) {

            Script script = getScriptInstanceByFilename(filename.toString());
            log.level4Debug("Found METADATA for " + script.getName() + ".");
            int i;
            if (!scripts.contains(script)) {
                log.level4Debug(script.getName() + " not found in CASPAC adding"
                        + " script to CASPAC.");
                scripts.add(script);
            }
            i = scripts.indexOf(script);

            script.metaData.load(pack.streamFileFromZip(entry));
            log.level4Debug("Added METADATA to " + script.getName() + ".");
            int md5ArrayPosition = 0;
            String md5;
            while ((md5 = script.metaData.metaProp.getProperty("Script.MD5[" + md5ArrayPosition + "]")) != null) {
                script.metaData.md5s.add(md5);
                md5ArrayPosition++;
            }
            scripts.set(i, script);
        } else if (filename.toString().endsWith(".scr")) {
            Script script = getScriptInstanceByFilename(filename.toString());
            String scriptText = fo.readTextFromStream(pack.streamFileFromZip(entry));
            script.setScript(scriptText);
            log.level4Debug("Added Script for " + script.getName() + ".");
           
        } else if (filename.toString().endsWith(".zip")) {
            Script script = getScriptInstanceByFilename(filename.toString());
            fo.writeStreamToFile(pack.streamFileFromZip(entry), TempFolder + filename);
            log.level4Debug("Added .zip to " + script.getName() + ". It will be unziped at end of unpacking.");
            unzipQueue.add(new File(TempFolder + filename));
            //for (File f:new File(TempFolder + slash + "IncludeExplode"+slash).listFiles())
            //    script.includeFiles.add(f);

        } else if (filename.toString().endsWith(".txt")) {
            Script script = getScriptInstanceByFilename(filename.toString());
            String description = fo.readTextFromStream(pack.streamFileFromZip(entry));
            script.setDiscription(description);
            log.level4Debug("Added Description to " + script.getName() + ".");
        }
    }

    /**
     * build class is a reference to handle -build.properties information
     */
    public class Build {

        public String developerName = "";
        public String developerDonateButtonText = "";
        public String donateLink = "";
        public String windowTitle = "";
        public boolean usePictureForBanner = false;
        public String bannerPic = "";
        public String bannerText = "";
        public String executeButtonText = "Do It";
        public boolean AudioEnabled = false;
        public boolean alwaysEnableControls = false;
        public Properties buildProp = new Properties();

        public Build(InputStream prop) throws IOException {
            buildProp.load(prop);
        }

        /**
         * loads and sets properties file
         *
         * @param prop build.properties file
         */
        public Build(Properties prop) {
            this.buildProp = prop;
        }

        /**
         * writes build properties to a file
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
         * writes build properties to a file
         *
         * @param output file to write
         * @return true if file was written
         * @throws FileNotFoundException
         * @throws IOException
         */
        public boolean write(File output) throws FileNotFoundException, IOException {
            setPropsFromVariables();
            FileOutputStream fos = new FileOutputStream(output);
            buildProp.store(fos, "This properties file was generated by CASUAL");
            return fo.verifyExists(output.toString());
        }

        /**
         * loads build properties from a map
         *
         * @param buildMap key,value pairs
         */
        private void setPropsFromVariables() {
            buildProp.setProperty("Window.UsePictureForBanner", usePictureForBanner ? "True" : "False");
            buildProp.setProperty("Audio.Enabled", AudioEnabled ? "True" : "False");
            buildProp.setProperty("Application.AlwaysEnableControls", AudioEnabled ? "True" : "False");
            buildProp.setProperty("Developer.DonateLink", donateLink);
            buildProp.setProperty("Developer.DonateToButtonText", developerDonateButtonText);
            buildProp.setProperty("Developer.Name", developerName);
            buildProp.setProperty("Window.ExecuteButtonText", executeButtonText);
            buildProp.setProperty("Window.BannerText", bannerText);
            buildProp.setProperty("Window.BannerPic", bannerPic);
            buildProp.setProperty("Window.Title", windowTitle);

        }

        /**
         * sets properties to values stored in build.properties file.
         */
        private void loadPropsToVariables() {
            if (buildProp.containsKey("Audio.Enabled")) {
                usePictureForBanner = buildProp.getProperty("Window.UsePictureForBanner", "").contains("rue");
            }
            AudioEnabled = buildProp.getProperty("Audio.Enabled", "").contains("rue");
            developerDonateButtonText = buildProp.getProperty("Developer.DonateToButtonText", "");
            developerName = buildProp.getProperty("Developer.Name", "");
            donateLink = buildProp.getProperty("Developer.DonateLink", "");
            donateLink = buildProp.getProperty("Developer.DonateLink", "");
            executeButtonText = buildProp.getProperty("Window.ExecuteButtonText", "");
            bannerText = buildProp.getProperty("Window.BannerText", "");
            alwaysEnableControls = buildProp.getProperty("Application.AlwaysEnableControls", "").contains("rue");
            windowTitle = buildProp.getProperty("Window.Title", "");

        }
    }
}
