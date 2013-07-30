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

import CASUAL.AudioHandler;
import CASUAL.BooleanOperations;
import CASUAL.CASUALApp;
import CASUAL.CASUALTools;
import CASUAL.CASUALUpdates;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.Statics;
import CASUAL.StringOperations;
import CASUAL.Unzip;
import CASUAL.Zip;
import CASUAL.crypto.AES128Handler;
import CASUAL.crypto.MD5sum;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author adam
 */
public final class Caspac {
/*TODO: update this.build.bannerPic=tempbannerpic;
 * at the end of every load operation involving loading
 * build.properties files.  This is important because
 * the logo.png location is stored on the filesystem,
 * not in the caspac.  Once caspac is loaded, if build.properties 
 * is loaded last, it will be an invalid file reference.
 */

    
    /* Loads a CASPAC
     * Types 0 CASPAC
     * Type 1 CASUAL
     * Type 2 Filesystem
     */
    public final int type;
    //public File logo;
    public BufferedImage logo;
    public final File CASPAC;
    public final CodeSource CASPACsrc;
    public String overview="";
    public Build build;
    public ArrayList<Script> scripts = new ArrayList<>();
    public final String TempFolder;
    public Log log = new Log();
    FileOperations fo = new FileOperations();
    private ArrayList<Thread> unzipThreads = new ArrayList<>();
    public static boolean useSound=true;
    //For CASUAL mode
    private Script activeScript;
    public boolean caspacShouldBeDeletedAfterExtraction=false;
    
    /*
     * Constructor for Caspac
     */
    
    public Caspac(File caspac, String tempDir, int type) throws IOException {
        this.CASPAC = caspac;
        this.CASPACsrc=null;
        this.TempFolder = tempDir;
        this.type = type;
        loadCASPACcontrolFilesFromCASPAC();
    }
    
    /*
     * secure constructor for Caspac 
     * always call waitForUnzipComplete in order to delete file and maintain security
     */
    public Caspac(File caspac, String tempDir, int type, char[] securityKey) throws IOException, Exception {
        AES128Handler ch=new AES128Handler(caspac);
        ch.decrypt(tempDir+caspac.getName(), securityKey);
        this.CASPAC = new File(tempDir+caspac.getName());
        this.CASPACsrc=null;
        this.TempFolder = tempDir;
        this.type = type;
        caspacShouldBeDeletedAfterExtraction=true;
        loadCASPACcontrolFilesFromCASPAC();
        this.build.bannerPic=tempbannerpic;
    }
    
    /*
     * Constructor for CASUAL
     */
    public Caspac(CodeSource src, String tempDir, int type) throws IOException {
        this.CASPACsrc = src;
        URL jar = src.getLocation();
        this.CASPAC = new File(tempDir + jar.getFile().toString());
        this.TempFolder = tempDir;
        this.type = type;
        CodeSource Src = CASUAL.CASUALApp.class.getProtectionDomain().getCodeSource();
        FileOperations fileOps = new FileOperations();
        int Count = 0;
        ArrayList<String> list = new ArrayList<>();
        if (CASUALTools.IDEMode){
            updateMD5s();
            //Statics.scriptLocations = new String[]{""};
            setupIDEModeScriptForCASUAL(CASUALApp.defaultPackage);
        } else {

            try (ZipInputStream zip = new ZipInputStream(jar.openStream())) {
                ZipEntry ZEntry;
                log.level4Debug("Picking Jar File:" + jar.getFile() +" ..scanning.");
                while ((ZEntry = zip.getNextEntry()) != null) {
                    String entry = ZEntry.getName();
                    getScriptByFilename(entry).actualMD5s.add(new MD5sum().getLinuxMD5Sum(StringOperations.convertStringToStream(fo.readTextFromResource("/"+entry)), entry.replace("SCRIPTS/","")));
              
                    if (entry.startsWith("SCRIPTS/")){ //part of CASPAC
                        log.level4Debug("parsing "+entry);
                        handleCASPACJarFiles(entry);
                      }
                }
            }
        }
    }


    public void setActiveScript(Script s){
        if (type == 1) {  //CASUAL checks for updates
            try {
                activeScript=s;
                loadActiveScript();
                //update script

            } catch (MalformedURLException ex) {
                log.level0Error("@problemDownloading");
            } catch (IOException ex) {
                log.level0Error("@problemDownloading");
            }

        } else {
            activeScript=s;
        }
    }
    public Script getActiveScript(){
        return this.activeScript;
    }

    /**
     * removes a script
     *
     * @param script Script reference
     */
    public void removeScript(Script script) {
        if (scripts.contains(script)) {
            scripts.remove(script);
            log.level4Debug("Removing Script: " + script.name);
        }
    }

    /**
     * writes a CASPAC
     *
     * @throws IOException
     */
    public void write() throws IOException {
        Map<String, InputStream> nameStream=new HashMap<>();
        if (!CASPAC.exists()){
            CASPAC.createNewFile();
        }
        Zip zip = new Zip(CASPAC);
        //write Properties File
        nameStream.put("-build.properties",build.getBuildPropInputStream());
        nameStream.put("-Overview.txt", StringOperations.convertStringToStream(overview));
        
        if (logo!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(logo, "png", baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            nameStream.put("-logo.png", is);
        }
        for (Script s : scripts) {
            //individualFiles.toArray();
            nameStream.putAll(s.getScriptAsMapForCASPAC());
        }
        

        log.level4Debug("Placeing the following files in the caspac Zip");
        zip.streamEntryToExistingZip(nameStream);
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
        log.level4Debug("\n\n\nStarting CASPAC unzip on "+CASPAC.getAbsolutePath());
        String scriptName="";
        Unzip unzip = new Unzip(CASPAC);
        while (unzip.zipFileEntries.hasMoreElements()) {
            Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
            String filename = unzip.getEntryName(entry);
            boolean isScript=!handleCASPACInformationFiles(filename, unzip, entry);
            if ((isScript && scriptName.isEmpty()) || (isScript && scriptName.equals(entry) )){ //ugly
                handleCASPACScriptFiles(filename, unzip, entry);
                this.activeScript=this.getScriptByFilename(filename);
            }
        }
        performUnzipOnQueue();
    }
    
    public void loadActiveScript() throws IOException{
         log.level4Debug("\n\n\nStarting CASPAC unzip.");
        String scriptName=activeScript.name;

        if (type==0){
            Unzip unzip = new Unzip(CASPAC);
            while (unzip.zipFileEntries.hasMoreElements()) {
                Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
                String filename = unzip.getEntryName(entry);
                if (entry.toString().startsWith(scriptName)){
                    handleCASPACScriptFiles(filename, unzip, entry);
                }
            }
    
        } else if (type==1){
             try {
                 activeScript=updateIfRequired(activeScript);
             } catch (     MalformedURLException | URISyntaxException ex) {
                 Logger.getLogger(Caspac.class.getName()).log(Level.SEVERE, null, ex);
             }
            //no need to update again because it is being updated
            replaceScriptByName(activeScript);

            unzipThreads=new ArrayList<>();
            this.unzipThreads.add(new Thread(activeScript.getExtractionRunnable()));
            performUnzipOnQueue();
        }
        
        
        
        
    }

    

    /**
     * loads a CASPAC.zip file
     *
     * @throws ZipException
     * @throws IOException
     */
    public void load() throws ZipException, IOException {

        if (type ==1){
            Script s=this.scripts.get(0);
            InputStream in = getClass().getClassLoader()
                                .getResourceAsStream(s.scriptZipFile.toString());
            Unzip.unZipInputStream(in, s.tempDir);
            this.activeScript=s;
            
            return;
        } 
        //Type 0
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
        log.level4Debug("Currently waiting for Threads:"+ Integer.toString(isUnzipping.length));
        while (BooleanOperations.containsTrue(isUnzipping)){
            CASUALTools.sleepForOneTenthOfASecond();
            for (int i = 0; i<isUnzipping.length; i++){
                if (!unzipThreads.get(i).isAlive()){
                    isUnzipping[i]=false;
                }
            }
        }
        if (this.caspacShouldBeDeletedAfterExtraction){
            this.CASPAC.delete();
        }
        if (Statics.useGUI){
            Statics.GUI.setCASPAC(this);
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
            handleCASPACScriptFiles(filename, pack, entry);
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
            if (s.name.equals(fileName.substring(0, fileName.lastIndexOf(".")))) {
                return s;
            }
        }
        Script script = new Script(fileName.substring(0, fileName.lastIndexOf(".")), this.TempFolder+fileName+Statics.Slash,this.type);
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
        
       try{ String scriptName=fileName.substring(0, fileName.lastIndexOf("."));
        for (Script s : scripts) {
            if (s.name.equals(scriptName)) {
                return s;
            }
        }
        Script s=new Script(scriptName,this.TempFolder+scriptName+Statics.Slash,this.type);
        this.scripts.add(s);
        return this.scripts.get(scripts.size()-1);
       } catch (ArrayIndexOutOfBoundsException ex){
           return null;
       }
    }

    
    /**
     * returns all script names
     *
     * @return list of script names
     */
    public String[] getScriptNames() { //TODO: examine this to figure out why we are iterating "scripts" and adding a slash while getting names
        ArrayList<String> scriptNames = new ArrayList<>();
        for (Script s : scripts) {
            scriptNames.add(s.name);
        }
        return StringOperations.convertArrayListToStringArray(scriptNames);
    }
    
    /**
     * gets script by name
     * @param name name of script to be pulled
     * @return Script object or null
     */
    public Script getScriptByName(String name){
        for (Script s : scripts) {
            if (s.name.equals(name)){
                return s;
            }
        }
        Script s=new Script(name,this.TempFolder+Statics.Slash+name+Statics.Slash,this.type);
        this.scripts.add(s);
        return this.scripts.get(scripts.size()-1);
    }


    private void performUnzipOnQueue() {
            for (Thread t :this.unzipThreads){
                t.start();
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
        logo=ImageIO.read(ImageIO.createImageInputStream(pack.streamFileFromZip(entry)));
        if (filename.isEmpty()) {
            filename=this.TempFolder+"-logo.png";
        }
        if (build!=null){
            build.bannerPic = filename;
        } else {
            tempbannerpic=filename;
        }
        
    }
    private String tempbannerpic;

    private boolean handleCASPACInformationFiles(String filename, Unzip pack, Object entry) throws IOException {
        boolean isAControlFile=false;
        if (filename.equals("-build.properties")) {
            setBuildPropInformation(pack, entry);
            isAControlFile=true;
        } else if (filename.endsWith(".png")) {
            if (filename.equals("")) {
                filename=this.TempFolder+"-logo.png";
            }
            extractCASPACBanner(pack, entry, filename);
            isAControlFile=true;
        } else if (filename.toString().equals("-Overview.txt")) {
            overview=StringOperations.convertStreamToString(pack.streamFileFromZip(entry));
            isAControlFile=true;
        }
        return isAControlFile;
    }

    private void handleCASPACScriptFiles(String filename, Unzip pack, Object entry) throws IOException {
        MD5sum md5sum =new MD5sum();
        if (filename.toString().endsWith(".meta")) {

            Script script = getScriptInstanceByFilename(filename.toString());
            log.level4Debug("Found METADATA for " + script.name + ".");
            int i;
            if (!scripts.contains(script)) {
                log.level4Debug(script.name + " not found in CASPAC adding"
                        + " script to CASPAC.");
                scripts.add(script);
            }
            i = scripts.indexOf(script);

            script.metaData.load(pack.streamFileFromZip(entry));
            log.level4Debug("Added METADATA to " + script.name + ".");
            int md5ArrayPosition = 0;
            String md5;
            while ((md5 = script.metaData.metaProp.getProperty("Script.MD5[" + md5ArrayPosition + "]")) != null) {
                script.metaData.md5s.add(md5);
                md5ArrayPosition++;
            }
            scripts.set(i, script);
        } else if (filename.toString().endsWith(".scr")) {
            Script script = getScriptInstanceByFilename(filename.toString());
            script.scriptContents= fo.readTextFromStream(pack.streamFileFromZip(entry));
            log.level4Debug("Added Script for " + script.name + ".");
            script.actualMD5s.add(md5sum.getLinuxMD5Sum(StringOperations.convertStringToStream(script.scriptContents), filename));
           
        } else if (filename.toString().endsWith(".zip")) {
            Script script = getScriptInstanceByFilename(filename.toString());
            script.scriptZipFile=entry;
            script.zipfile=pack;
            this.unzipThreads.add(new Thread(script.getExtractionRunnable()));
            log.level4Debug("Added .zip to " + script.name + ". It will be unziped at end of unpacking.");
            
        } else if (filename.toString().endsWith(".txt")) {
            Script script = getScriptInstanceByFilename(filename.toString());
            String description = fo.readTextFromStream(pack.streamFileFromZip(entry));
            script.discription=description;
            log.level4Debug("Added Description to " + script.name + ".");
            script.actualMD5s.add(md5sum.getLinuxMD5Sum(StringOperations.convertStringToStream(script.scriptContents), filename));
        }
    }

    private void setBuild(InputStream in) throws IOException {
        Properties prop=new Properties();
        prop.load(in);
        this.setBuild(prop);
    

}

    private void handleCASPACJarFiles(String entry) throws IOException {
        FileOperations fo=new FileOperations();
        
        if (entry.equals("SCRIPTS/-Overview.txt")) {
            entry="/"+entry;
            System.out.println("Attempting to read resource:" +entry);
            this.overview = fo.readTextFromResource(entry);
            System.out.println("overview "+overview);
        } else if (entry.equals("SCRIPTS/-build.properties")) {
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(entry);
            setBuild(in);
        } else if (entry.equals("SCRIPTS/-logo.png")) {
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(entry);
            logo = ImageIO.read(ImageIO.createImageInputStream(in));
            build.bannerPic = entry;
        } else if (entry.endsWith("SCRIPTS/.txt")) {
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(entry);
            this.getScriptByFilename(entry).discription = fo.readTextFromResource(entry);
        } else if (entry.endsWith(".scr")) {
            this.getScriptByFilename(entry).scriptContents = fo.readTextFromResource("/"+entry);
        } else if (entry.endsWith(".meta")) {
            System.out.println("loading meta "+entry);
                        InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(entry);
            Properties prop = new Properties();
            prop.load(in);
            this.getScriptByFilename(entry).metaData.load(prop);
        }  else if (entry.endsWith(".zip")) {
            log.level3Verbose("found zip at "+entry);
            this.getScriptByFilename(entry).scriptZipFile=entry;
        }
        new MD5sum().getLinuxMD5Sum(StringOperations.convertStringToStream(fo.readTextFromResource("/"+entry)), entry);
    }

    private void setupIDEModeScriptForCASUAL(String defaultPackage) {
        Script script=this.getScriptByName(defaultPackage);
        FileOperations fo = new FileOperations();
        
        String caspacPath="SCRIPTS/";
        try {
            File f=new File(".");
            caspacPath = f.getCanonicalPath()+"/SCRIPTS/";
        } catch (IOException ex) {
            Logger.getLogger(Caspac.class.getName()).log(Level.SEVERE, null, ex);
        }
        String scriptPath=caspacPath+defaultPackage;
        try {
            this.setBuild(new BufferedInputStream(new FileInputStream(caspacPath+"-build.properties")));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Caspac.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Caspac.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.overview=fo.readFile(caspacPath+"-Overview.txt");
        String logof=caspacPath+"-logo.png";
        
        try {
         
            InputStream in = new FileInputStream(logof);
            logo = ImageIO.read(ImageIO.createImageInputStream(in));
        } catch (IOException ex) {
            //no logo for this CASPAC
        }
            build.bannerPic = logof;
        
        log.level4Debug("IDE MODE PATH="+scriptPath);
        getScriptByName(defaultPackage).scriptContents=fo.readFile(scriptPath+".scr");
        getScriptByName(defaultPackage).discription=fo.readFile(scriptPath+".txt");
        try {
            getScriptByName(defaultPackage).metaData.load(new BufferedInputStream(new FileInputStream(new File(scriptPath+".meta"))));
        } catch (FileNotFoundException ex) {
            //no meta, its not requried
        }
        getScriptByName(defaultPackage).scriptZipFile=(scriptPath+".zip");
    }

    private void loadCASPACcontrolFilesFromCASPAC() throws IOException {
        //if the CASPAC exists lets try to grab the non-script files 
        if (fo.verifyExists(CASPAC.getAbsolutePath()) && CASPAC.canRead()) {
            try {
                Unzip unzip = new Unzip(CASPAC);
                Enumeration cpEnumeration = unzip.zipFileEntries;
                if (cpEnumeration.hasMoreElements()) {
                    while (cpEnumeration.hasMoreElements()) {
                        Object o = cpEnumeration.nextElement();
                        if (unzip.getEntryName(o).contains("-Overview.txt")) {
                            this.overview = fo.readTextFromStream(unzip.streamFileFromZip(o));
                        }
                        if (unzip.getEntryName(o).contains("-build.properties")) {
                            this.build = new Build(unzip.streamFileFromZip(o));
                        }
                        if (unzip.getEntryName(o).contains("-logo.png")) {
                            extractCASPACBanner(unzip, o, overview);
                        }
                    }
                }

            } catch (ZipException ex) {
            }



        }
    }

    private void updateMD5s() {
        Thread update;
        update = new Thread(CASUALTools.updateMD5s);
        update.setName("Updating MD5s");
        update.start(); //ugly  move this to somewhere else
        log.level3Verbose("IDE Mode: Using " + CASUALApp.defaultPackage + ".scr ONLY!");
    }

    
        
    /**
     * checks for updates.  
     * @return true if script can continue.  false if halt is recommended.
     */
    public Script updateIfRequired(Script s) throws MalformedURLException, URISyntaxException, IOException{
        if (s.metaData.minSVNversion.isEmpty()) {
            return s;
        }
        int mySVNVersion=Integer.parseInt(java.util.ResourceBundle
                .getBundle("CASUAL/resources/CASUALApp")
                .getString("Application.revision"));
        int myScriptVersion=Integer.parseInt(s.metaData.scriptRevision);
        String myScriptName=s.name;
        CASUALUpdates ci=new CASUALUpdates();
        Properties updatedprop=new Properties();
        
        Script updatedScript=new Script(s);
        new File(s.tempDir).mkdirs();
        updatedprop.load((InputStream)ci.downloadMetaFromRepoForScript(s));
        updatedScript.metaData.load(updatedprop);
        int updatedSVNVersion=Integer.parseInt(updatedScript.metaData.minSVNversion);
        int updatedScriptVersion=Integer.parseInt(updatedScript.metaData.scriptRevision);
        
        if (mySVNVersion<updatedSVNVersion){
            updatedScript.scriptContents="";
            log.level2Information("\n"+updatedScript.metaData.killSwitchMessage);
            return updatedScript;
        } else if (myScriptVersion< updatedScriptVersion){
            log.level2Information("@scriptIsOutOfDate");
            log.level2Information("\n"+updatedScript.metaData.updateMessage);
            updatedScript=ci.updateScript(updatedScript,this.TempFolder);
            return updatedScript;
        } else {
            log.level2Information("@noUpdateRequired");
            return s;
        }

    }
 
    /**
     * replaces a script in list array
     * @param s script to replace
     */
    public int replaceScriptByName(Script s) {
        String name=s.name;
        for (int i=0; i<this.scripts.size();i++){
            if (scripts.get(i).name.equals(name)){
                scripts.set(i, s);
                return i;
            }
        }
        return -1;
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
    public boolean audioEnabled = Caspac.useSound;
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

    public InputStream getBuildPropInputStream() throws IOException {
        setPropsFromVariables();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        buildProp.store(output, "This properties file was generated by CASUAL");
        return new ByteArrayInputStream(output.toByteArray());

    }

    /**
     * loads build properties from a map
     *
     * @param buildMap key,value pairs
     */
    private void setPropsFromVariables() {
        buildProp.setProperty("Window.UsePictureForBanner", usePictureForBanner ? "True" : "False");
        buildProp.setProperty("Audio.Enabled", audioEnabled ? "True" : "False");
        buildProp.setProperty("Application.AlwaysEnableControls", alwaysEnableControls ? "True" : "False");
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
            audioEnabled = buildProp.getProperty("Audio.Enabled", "").contains("rue");
            AudioHandler.useSound=audioEnabled;
        }
        usePictureForBanner = buildProp.getProperty("Window.UsePictureForBanner", "").contains("rue");
        
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
