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
import CASUAL.CASUALTools;
import CASUAL.FileOperations;
import CASUAL.CASUALStartupTasks;
import CASUAL.Log;
import CASUAL.Statics;
import CASUAL.misc.StringOperations;
import CASUAL.archiving.Unzip;
import CASUAL.archiving.Zip;
import CASUAL.crypto.AES128Handler;
import CASUAL.crypto.MD5sum;
import CASUAL.misc.MandatoryThread;
import CASUAL.network.CASUALDevIntegration.CasualDevCounter;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

/**
 * handles gathering, reading and writing of CASPACs in a unified manner
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public final class Caspac {

    /**
     * Loads a CASPAC Type 0 CASPAC, Type 1 CASUAL, Type 2 Filesystem.
     */
    public final int type;
    //public File logo;

    /**
     * BufferedImage for logo.png in CASPAC.
     */
    public BufferedImage logo;

    /**
     * CASPAC which is being used.
     */
    public final File CASPAC;

    /**
     * CodeSource if available to CASPAC. Generally used in place of CASPAC
     * file.
     */
    public final CodeSource CASPACsrc;

    /**
     * CASPAC -Overview.txt file contents.
     */
    public String overview = "";
    /**
     * CASPAC -Build.properties file.
     */
    public Build build;

    /**
     * ArrayList of scripts contained in CASPAC.
     */
    public ArrayList<Script> scripts = new ArrayList<Script>();

    /**
     * TempDir used for CASPAC.
     */
    public final String TempFolder;


    private ArrayList<CASUAL.misc.MandatoryThread> unzipThreads = new ArrayList<CASUAL.misc.MandatoryThread>();
    //For CASUAL mode
    private Script activeScript;

    /**
     * deletes an unencrypted CASPAC from disk after extraction occurs.
     */
    public boolean caspacShouldBeDeletedAfterExtraction = false;

    /**
     * If we are debugging a script, we dont want to delete the script contents
     * to prevent further execution on error. This is used for debugging
     * purposes.
     */
    public static boolean debug = false;

    /**
     * Constructor for Caspac
     *
     * @param caspac file containing CASPAC information.
     * @param tempDir temp folder to use
     * @param type Type of CASPAC CASPAC, Type 1 CASUAL, Type 2 Filesystem
     * @throws IOException
     */
    public Caspac(File caspac, String tempDir, int type) throws IOException {
        this.CASPAC = caspac;
        this.CASPACsrc = null;
        this.TempFolder = tempDir;
        this.type = type;
        loadCASPACcontrolFilesFromCASPAC();
    }

    /**
     * secure constructor for Caspac always call startAndWaitForUnzip in order
 to delete file and maintain security
     *
     * @param caspac file containing CASPAC information.
     * @param tempDir temp folder to use
     * @param type Type of CASPAC CASPAC, Type 1 CASUAL, Type 2 Filesystem
     * @param securityKey key to decrypt CASPAC.
     * @throws IOException
     * @throws Exception
     */
    public Caspac(File caspac, String tempDir, int type, char[] securityKey) throws IOException, Exception {
        AES128Handler ch = new AES128Handler(caspac);
        ch.decrypt(tempDir + caspac.getName(), securityKey);
        this.CASPAC = new File(tempDir + caspac.getName());
        this.CASPACsrc = null;
        this.TempFolder = tempDir;
        this.type = type;
        caspacShouldBeDeletedAfterExtraction = true;
        loadCASPACcontrolFilesFromCASPAC();
        if (tempbannerpic != null) {
            this.build.bannerPic = tempbannerpic;
        }
    }

    /*
     * Constructor for CASUAL
     */
    /**
     * Constructor for CASUAL
     *
     * @param src CodeSource reference, used to reference SCRIPTS folder.
     * @param tempDir Temporary folder to use
     * @param type Type of CASPAC CASPAC, Type 1 CASUAL, Type 2 Filesystem
     * (should be 1 generally)
     * @throws IOException
     */
    public Caspac(CodeSource src, String tempDir, int type) throws IOException {
        this.CASPACsrc = src;
        URL jar = src.getLocation();
        this.CASPAC = new File(tempDir + jar.getFile().toString());
        this.TempFolder = tempDir;
        this.type = type;
        if (CASUALTools.IDEMode) {
            updateMD5s();
            //Statics.scriptLocations = new String[]{""};
            setupIDEModeScriptForCASUAL(CASUAL.CASUALMain.defaultPackage);
        } else {
            Log.level4Debug("Opening self as stream for scan");
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            ZipEntry ZEntry;
            Log.level4Debug("Picking Jar File:" + src.toString() + " ..scanning.");
            while ((ZEntry = zip.getNextEntry()) != null) {
                String entry = ZEntry.getName();
                if (entry.startsWith("SCRIPTS/") || entry.startsWith("SCRIPTS\\")) { //part of CASPAC
                    handleCASPACJarFiles(entry);

                }
            }

        }
    }

    /**
     * returns an empty CASPAC.
     *
     * @return empty CASPAC>
     * @throws IOException
     */
    final public static Caspac makeGenericCaspac() throws IOException {
        File f = new File(Statics.getTempFolder() + "newfile");
        Caspac c = new Caspac(f, Statics.getTempFolder(), 2);
        Script s = new Script("oneshot", Statics.getTempFolder());

        return c;
    }

    /**
     * Sets the active script to an instace of a script.
     *
     * @param s script to make active.
     */
    public void setActiveScript(Script s) {
        CasualDevCounter.doIncrementCounter(s.name+s.metaData.uniqueIdentifier);
        CASUALStartupTasks.caspacScriptPrepLock = true;
        if (type == 1) {  //CASUAL checks for updates
            try {
                Log.level3Verbose("Setting script " + s.name + " as active and loading");
                activeScript = s;
                loadActiveScript();
                //update script

            } catch (MalformedURLException ex) {
            } catch (IOException ex) {
            }

        } else {
            activeScript = s;
        }
    }

    /**
     * gets the active script.
     *
     * @return reference to active script.
     */
    public Script getActiveScript() {
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
            Log.level4Debug("Removing Script: " + script.name);
        }
    }

    /**
     * writes a CASPAC
     *
     * @throws IOException
     */
    public void write() throws IOException {
        Map<String, InputStream> nameStream = new HashMap<String, InputStream>();
        if (!CASPAC.exists()) {
            CASPAC.createNewFile();
        }
        Zip zip = new Zip(CASPAC);
        //write Properties File
        nameStream.put("-build.properties", build.getBuildPropInputStream());
        nameStream.put("-Overview.txt", StringOperations.convertStringToStream(overview));

        if (logo != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(logo, "png", baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            nameStream.put("-logo.png", is);
        }
        for (Script s : scripts) {
            //individualFiles.toArray();
            File[] list = new File(s.tempDir).listFiles();
            if (list != null) {
                for (File test : list) {
                    boolean delete = true;
                    for (File f : s.individualFiles) {
                        if (test.getCanonicalFile().equals(f.getCanonicalFile())) {
                            delete = false;
                        }
                    }
                    if (delete) {
                        if (test.toString().contains(s.tempDir)) {
                            test.delete();
                        }

                    }
                }
            }
            nameStream.putAll(s.getScriptAsMapForCASPAC());
        }

        Log.level4Debug("Placeing the following files in the caspac Zip");
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

    /**
     * parses CASPAC and loads the first script seen identified by non-caspac
     * controller files.
     *
     * @throws ZipException
     * @throws IOException
     */
    public void loadFirstScriptFromCASPAC() throws ZipException, IOException {
        Log.level4Debug("Starting loadFirstScriptFromCASPAC unzip on " + CASPAC.getAbsolutePath());
        String scriptName = "";
        Unzip unzip = new Unzip(CASPAC);
        while (unzip.zipFileEntries.hasMoreElements()) {
            Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
            String filename = unzip.getEntryName(entry);

            //detect if file is Overview, Logo, or build.properties, otherwise its a script
            boolean isScript = !Arrays.asList(controlFiles).contains(entry.toString());

            //if it's a script, and we havent set a script, or if it's a script and it matches the script we set
            if (isScript && scriptName.isEmpty() || isScript && scriptName.equals(activeScript.name)) {
                handleCASPACScriptFiles(filename, unzip, entry);
                //entry.toString().subst
                scriptName = entry.toString().substring(0, entry.toString().lastIndexOf("."));
                this.activeScript = this.getScriptByFilename(filename);
            }
        }
        Log.level4Debug("loading CASPAC script");
        performUnzipOnQueue();

    }

    /**
     * Loads the active script after its been set. 
     * @throws IOException
     */
    public void loadActiveScript() throws IOException {
        Log.level4Debug("Starting loadActiveScript CASPAC unzip.");
        String scriptName = activeScript.name;

        if (type == 0) {
            Unzip unzip = new Unzip(CASPAC);
            while (unzip.zipFileEntries.hasMoreElements()) {
                Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
                String filename = unzip.getEntryName(entry);
                if (entry.toString().startsWith(scriptName)) {
                    handleCASPACScriptFiles(filename, unzip, entry);
                }
            }

        } else if (type == 1) {
            Log.level3Verbose("This is a CASUAL jar with resources");
            try {
                activeScript = updateIfRequired(activeScript);
            } catch (URISyntaxException ex) {
                Log.errorHandler(ex);
            }
            Log.level4Debug("returned from checking updates.");
            //no need to update again because it is being updated
            replaceScriptByName(activeScript);

            unzipThreads = new ArrayList<CASUAL.misc.MandatoryThread>();
            CASUAL.misc.MandatoryThread t = new CASUAL.misc.MandatoryThread(activeScript.getExtractionRunnable());
            t.setName("Active Script Preparation");
            this.unzipThreads.add(t);
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

        if (type == 1) {
            Script s = this.scripts.get(0);
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(s.scriptZipFile.toString());
            Unzip.unZipInputStream(in, s.tempDir);
            in.close();
            this.activeScript = s;

            return;
        }
        //Type 0
        Log.level4Debug("Starting commanded Load CASPAC unzip.");
        Unzip unzip = new Unzip(CASPAC);
        while (unzip.zipFileEntries.hasMoreElements()) {
            Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
            handleCASPACFiles(entry, unzip);
        }
        Log.level4Debug("Starting to unzip script zips");
        performUnzipOnQueue();
        Log.level4Debug("CASPAC load completed.");
    }

    /**
     * waits for unzip to complete and executes a runnable. 
     * @param action runnable to execute. 
     */
    public void waitForUnzipAndRun(Runnable action) {
        waitForUnzipAndRun(action, false, null);
    }

    /**
     * waits for unzip to complete and executes a runnable. 
     * @param action runnable to execute. 
     * @param onASeparateThread true if multi-threadded 
     * @param ThreadName name for the alternate thread.
     */
    public void waitForUnzipAndRun(Runnable action, boolean onASeparateThread, String ThreadName) {
        startAndWaitForUnzip();
        if (onASeparateThread) {
            MandatoryThread t = new MandatoryThread(action);
            t.setName(ThreadName);
            t.start();
        } else {
            action.run();
        }
    }

    /**
     * causes the current thread to wait until all unzipThreads have completed.
     * this is the longest part and the last part of completion of the CASPAC prep. 
     */
    public void startAndWaitForUnzip() {
        boolean[] isUnzipping = new boolean[unzipThreads.size()];
        Log.level4Debug("Currently waiting for Threads:" + Integer.toString(isUnzipping.length));
        for (CASUAL.misc.MandatoryThread t : unzipThreads) {
            if (t!=null&&!t.isComplete()){
                t.start();
                t.waitFor();

            }
            Log.level4Debug("Unzip completed!");
        }

        if (this.caspacShouldBeDeletedAfterExtraction) {
            this.CASPAC.delete();
        }
        Log.level4Debug("Unzipping complete.");
    }

    
    public void waitForUnzip(){
        for (CASUAL.misc.MandatoryThread t : unzipThreads) {
            if (t==null){
                continue;
            }
            if (!t.isAlive()&& !t.isComplete()){
                t.start();
            }
            if (!t.isComplete()){
                t.waitFor();
            }
            Log.level4Debug("Unzip completed!");
        }
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
        boolean isScript = !handleCASPACInformationFiles(filename, pack, entry);
        if (isScript) {
            handleCASPACScriptFiles(filename, pack, entry);
        }
    }

    /**
     * script instance which is being referenced. creates a new script if not
     * found
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
        Script script = new Script(fileName.substring(0, fileName.lastIndexOf(".")), this.TempFolder + fileName + Statics.slash, this.type);
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
        Log.level4Debug("Looking up " + fileName);
        String scriptName = "";
        try {
            scriptName = fileName.substring(0, fileName.lastIndexOf("."));
            for (Script s : scripts) {
                if (s.name.equals(scriptName)) {
                    return s;
                }
            }

        } catch (Exception ex) {
        }
        if (!scriptName.isEmpty()) {
            Script s = new Script(scriptName, this.TempFolder + scriptName + Statics.slash, this.type);
            this.scripts.add(s);
            return this.scripts.get(scripts.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * returns all script names
     *
     * @return list of script names
     */
    public String[] getScriptNames() {
        ArrayList<String> scriptNames = new ArrayList<String>();
        for (Script s : scripts) {
            scriptNames.add(s.name);
        }
        return StringOperations.convertArrayListToStringArray(scriptNames);
    }

    /**
     * gets script by name
     *
     * @param name name of script to be pulled
     * @return Script object or null
     */
    public Script getScriptByName(String name) {
        for (Script s : scripts) {
            if (s.name.equals(name)) {
                return s;
            }
        }
        Script s = new Script(name, this.TempFolder + Statics.slash + name + Statics.slash, this.type);
        this.scripts.add(s);
        return this.scripts.get(scripts.size() - 1);
    }

    private void performUnzipOnQueue() {
        Log.level3Verbose("Performing unzip of resources.");
        for (MandatoryThread t : this.unzipThreads) {
            t.start();
        }
    }

    private void setBuildPropInformation(Unzip pack, Object entry) throws IOException {
        Log.level4Debug("Found -build.properties adding information to "
                + "CASPAC");
        build = new Build(pack.streamFileFromZip(entry));
        build.loadPropsToVariables();
    }

    private void extractCASPACBanner(Unzip pack, Object entry, String filename) throws IOException {
        Log.level4Debug("Found logo adding information to "
                + "CASPAC");
        logo = ImageIO.read(ImageIO.createImageInputStream(pack.streamFileFromZip(entry)));
        if (filename.isEmpty()) {
            filename = this.TempFolder + "-logo.png";
        }
        if (build != null) {
            build.bannerPic = filename;
        } else {
            tempbannerpic = filename;
        }

    }
    private String tempbannerpic;

    private boolean handleCASPACInformationFiles(String filename, Unzip pack, Object entry) throws IOException {
        boolean isAControlFile = false;
        if (filename.equals("-build.properties")) {
            setBuildPropInformation(pack, entry);
            isAControlFile = true;
        } else if (filename.endsWith(".png")) {
            if (filename.equals("")) {
                filename = this.TempFolder + "-logo.png";
            }
            extractCASPACBanner(pack, entry, filename);
            isAControlFile = true;
        } else if (filename.toString().equals("-Overview.txt")) {
            overview = StringOperations.convertStreamToString(pack.streamFileFromZip(entry));
            isAControlFile = true;
        }
        return isAControlFile;
    }

    private void handleCASPACScriptFiles(String filename, Unzip pack, Object entry) throws IOException {
        FileOperations fo = new FileOperations();
        MD5sum md5sum = new MD5sum();
        if (filename.toString().endsWith(".meta")) {

            Script script = getScriptInstanceByFilename(filename.toString());
            Log.level4Debug("Found METADATA for " + script.name + ".");
            int i;
            if (!scripts.contains(script)) {
                Log.level4Debug(script.name + " not found in CASPAC adding"
                        + " script to CASPAC.");
                scripts.add(script);
            }
            i = scripts.indexOf(script);

            script.metaData.load(pack.streamFileFromZip(entry));
            Log.level4Debug("Added METADATA to " + script.name + ".");
            int md5ArrayPosition = 0;
            scripts.set(i, script);
        } else if (filename.toString().endsWith(".scr")) {
            Script script = getScriptInstanceByFilename(filename.toString());
            script.scriptContents = fo.readTextFromStream(pack.streamFileFromZip(entry));
            Log.level4Debug("Added Script for " + script.name + ".");
            script.actualMD5s.add(md5sum.getLinuxMD5Sum(StringOperations.convertStringToStream(script.scriptContents), filename));

        } else if (filename.toString().endsWith(".zip")) {
            Script script = getScriptInstanceByFilename(filename.toString());
            script.scriptZipFile = entry;
            script.zipfile = pack;
            CASUAL.misc.MandatoryThread t = new CASUAL.misc.MandatoryThread(script.getExtractionRunnable());
            t.setName("zip File Preparation " + unzipThreads.size());
            this.unzipThreads.add(t);

            Log.level4Debug("Added .zip to " + script.name + ". It will be unziped at end of unpacking.");

        } else if (filename.toString().endsWith(".txt")) {
            Script script = getScriptInstanceByFilename(filename.toString());
            String description = fo.readTextFromStream(pack.streamFileFromZip(entry));
            script.discription = description;
            Log.level4Debug("Added Description to " + script.name + ".");
            script.actualMD5s.add(md5sum.getLinuxMD5Sum(StringOperations.convertStringToStream(script.discription), filename));
        }
    }

    private void setBuild(InputStream in) throws IOException {
        Properties prop = new Properties();
        prop.load(in);
        this.setBuild(prop);
        Log.level4Debug(StringOperations.convertStreamToString(this.build.getBuildPropInputStream()));

    }

    private void handleCASPACJarFiles(String entry) throws IOException {
        FileOperations fo = new FileOperations();
        if (entry.startsWith("/")) {
            entry = entry.replaceFirst("/", "");
        }

        if (entry.equals("SCRIPTS/-Overview.txt") || entry.equals("SCRIPTS\\-Overview.txt")) {

            Log.level4Debug("processing:" + entry);
            this.overview = fo.readTextFromResource(entry);
            System.out.println("overview " + overview);
        } else if (entry.equals("SCRIPTS/-build.properties") || entry.equals("SCRIPTS\\-build.properties")) {
            Log.level4Debug("processing:" + entry);
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(entry);
            setBuild(in);
        } else if (entry.equals("SCRIPTS/-logo.png") || entry.equals("SCRIPTS\\-logo.png")) {
            Log.level4Debug("processing:" + entry);
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(entry);
            logo = ImageIO.read(ImageIO.createImageInputStream(in));
        } else if (entry.endsWith(".txt")) {
            Log.level4Debug("processing:" + entry);
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(entry);
            this.getScriptByFilename(entry).discription = fo.readTextFromResource(entry);
        } else if (entry.endsWith(".scr")) {
            Log.level4Debug("processing:" + entry);
            System.out.println("SCRIPT CONTENTS:" + fo.readTextFromResource(entry));
            this.getScriptByFilename(entry).scriptContents = fo.readTextFromResource(entry);

        } else if (entry.endsWith(".meta")) {
            Log.level4Debug("processing:" + entry);
            System.out.println("loading meta " + entry);
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(entry);
            Properties prop = new Properties();
            prop.load(in);
            this.getScriptByFilename(entry).metaData.load(prop);
        } else if (entry.endsWith(".zip")) {
            Log.level4Debug("processing:" + entry);
            Log.level3Verbose("found zip at " + entry);
            this.getScriptByFilename(entry).scriptZipFile = entry;
        }
        Log.level4Debug("getting MD5 for:" + entry);
        new MD5sum().getLinuxMD5Sum(Caspac.class.getClassLoader().getResourceAsStream(entry), entry);
    }

    private void setupIDEModeScriptForCASUAL(String defaultPackage) {
        FileOperations fo = new FileOperations();
        Script script = this.getScriptByName(defaultPackage);

        String caspacPath = "SCRIPTS/";
        try {
            File f = new File(".");
            caspacPath = f.getCanonicalPath() + "/SCRIPTS/";
        } catch (IOException ex) {
            Log.errorHandler(ex);
        }
        String scriptPath = caspacPath + defaultPackage;
        try {
            this.setBuild(new BufferedInputStream(new FileInputStream(caspacPath + "-build.properties")));
        } catch (FileNotFoundException ex) {
            Log.errorHandler(ex);
        } catch (IOException ex) {
            Log.errorHandler(ex);
        }
        this.overview = fo.readFile(caspacPath + "-Overview.txt");
        String logof = caspacPath + "-logo.png";

        try {

            InputStream in = new FileInputStream(logof);
            logo = ImageIO.read(ImageIO.createImageInputStream(in));
        } catch (IOException ex) {
            //no logo for this CASPAC
        }
        build.bannerPic = logof;

        Log.level4Debug("IDE MODE PATH=" + scriptPath);
        getScriptByName(defaultPackage).scriptContents = fo.readFile(scriptPath + ".scr");
        getScriptByName(defaultPackage).discription = fo.readFile(scriptPath + ".txt");
        try {
            getScriptByName(defaultPackage).metaData.load(new BufferedInputStream(new FileInputStream(new File(scriptPath + ".meta"))));
        } catch (FileNotFoundException ex) {
            //no meta, its not requried
        }
        getScriptByName(defaultPackage).scriptZipFile = (scriptPath + ".zip");
    }
    private String controlFiles[] = {"-Overview.txt", "-build.properties", "-logo.png"};

    private void loadCASPACcontrolFilesFromCASPAC() throws IOException {
        FileOperations fo = new FileOperations();
        //if the CASPAC exists lets try to grab the non-script files 
        if (fo.verifyExists(CASPAC.getAbsolutePath()) && CASPAC.canRead()) {
            try {
                Unzip unzip = new Unzip(CASPAC);
                Enumeration<? extends ZipEntry> cpEnumeration = unzip.zipFileEntries;
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
        MandatoryThread update;
        update = new MandatoryThread(CASUALTools.updateMD5s);
        update.setName("Updating MD5s");
        update.start(); //ugly  move this to somewhere else
        Log.level3Verbose("IDE Mode: Using " + CASUAL.CASUALMain.defaultPackage + ".scr ONLY!");
    }

    /**
     * checks for updates.
     *
     * @param s Script to be checked
     * @return true if script can continue. false if halt is recommended.
     * @throws java.net.MalformedURLException
     * @throws java.net.URISyntaxException
     */
    public Script updateIfRequired(Script s) throws MalformedURLException, URISyntaxException, IOException {
        return s;
        //TODO reenable SCRIPT updates. 
    }
    /*
     
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
     Log.level3Verbose("creating new script instance to compare against online version");
     Script updatedScript=new Script(s);
     new File(s.tempDir).mkdirs();
     Log.level3Verbose("getting updated script version info");
        
     //TODO: downloadMetaFromRepoForScript hangs.  Script will not complte unzip because of this.  Updates are down
     updatedprop.load(ci.downloadMetaFromRepoForScript(s));
     Log.level3Verbose("updating meta");
     updatedScript.metaData.load(updatedprop);
        
     int updatedSVNVersion=Integer.parseInt(updatedScript.metaData.minSVNversion);
     int updatedScriptVersion=Integer.parseInt(updatedScript.metaData.scriptRevision);
     Log.level3Verbose("comparing script information");
     if (mySVNVersion<updatedSVNVersion){
     updatedScript.scriptContents="";
     Log.level2Information("\n"+updatedScript.metaData.killSwitchMessage);
     return updatedScript;
     } else if (myScriptVersion< updatedScriptVersion){
     Log.level2Information("@scriptIsOutOfDate");
     Log.level2Information("\n"+updatedScript.metaData.updateMessage);
     updatedScript=ci.updateScript(updatedScript,this.TempFolder);
     return updatedScript;
     } else {
     Log.level2Information("@noUpdateRequired");
     return s;
     }

     } */
 
    /**
     * replaces a script in list array.
     * @param s script to replace
     * @return location of script in scripts array. 
     */
    public int replaceScriptByName(Script s) {
        String name = s.name;
        for (int i = 0; i < this.scripts.size(); i++) {
            if (scripts.get(i).name.equals(name)) {
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

        /**
         * Name of Script Developer.
         */
        public String developerName = "";

        /**
         * Name to display on donation button. 
         */
        public String developerDonateButtonText = "";

        /**
         * Link to direct users to when donate button is clicked. 
         */
        public String donateLink = "";

        /**
         * Title of UI window. 
         */
        public String windowTitle = "";

        /**
         * True if use picture for banner.  False if use text. 
         */
        public boolean usePictureForBanner = false;

        /**
         * Image to use for banner pic.  Generally CASPAC folder/CodeSource -Logo.png
         */
        public String bannerPic = "";

        /**
         * Text to use for banner. 
         */
        public String bannerText = "";

        /**
         * Text to be displayed on execute button. 
         */
        public String executeButtonText = "Do It";

        /**
         * True if Audio is to be used by application for user enhanced experience. 
         */
        public boolean audioEnabled = AudioHandler.useSound;

        /**
         * True if controls should never be disabled. 
         */
        public boolean alwaysEnableControls = false;

        /**
         * Properties file containing all properties of the -Build.properties file 
         */
        public Properties buildProp = new Properties();

        /**
         * Accepts a -Build.properties file via InputStream. 
         * @param prop Properties file. 
         * @throws IOException
         */
        public Build(InputStream prop) throws IOException {
            Log.level4Debug("Loading build information from inputstream");
            buildProp.load(prop);
            loadPropsToVariables();
            Log.level4Debug(windowTitle + " - " + bannerText + " - " + developerName);

        }

        /**
         * loads and sets properties file
         *
         * @param prop build.properties file
         */
        public Build(Properties prop) {
            Log.level4Debug("Loading build information from prop information");
            this.buildProp = prop;
            loadPropsToVariables();
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
            FileOperations fo = new FileOperations();
            setPropsFromVariables();
            FileOutputStream fos = new FileOutputStream(output);
            buildProp.store(fos, "This properties file was generated by CASUAL");
            return fo.verifyExists(output.toString());
        }

        /**
         * gets the -Build.properties as an InputStream
         * @return InputStream properties file for write-out. 
         * @throws IOException
         */
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
                AudioHandler.useSound = audioEnabled;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String n = System.getProperty("line.separator");
        sb.append("Scripts:").append(this.scripts.size()).append(n);
        sb.append("Working Dir: ").append(this.TempFolder).append(n);
        sb.append(this.build.buildProp.toString());

        return sb.toString();
    }
}
