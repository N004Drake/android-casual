/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CASUAL.caspac;

import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.Statics;
import CASUAL.Unzip;
import CASUAL.Zip;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipException;


/**
 *
 * @author adam
 */
public class Caspac {
    public File logo;
    private static String slash = System.getProperty("file.separator");
    public final File CASPAC;
    public String overview;
    public Build build;
    public ArrayList<Script> scripts = new ArrayList<>();
    public String TempFolder;
    public Log log = new Log();
    public ArrayList<File> unzipQueue = new ArrayList<>();


    public Caspac(File caspac) {
        this.CASPAC = caspac;
        System.out.println(Statics.TempFolder);
        log.level4Debug("Creating folder for CASPAC at\n\t"
                + Statics.TempFolder + "CASPAC" + caspac.getName());
        TempFolder = Statics.TempFolder + "CASPAC" + caspac.getName();
        
        if (!(new File(TempFolder).exists()))
            new File(TempFolder).mkdir();
    }
    
    
    public void addScript(Script script) {
        if (!(scripts.contains(script)))
        {
            scripts.add(script);
            log.level4Debug("Adding Script: "+ script.getName());
        }
}
    public void removeScript (Script script) {
        if (scripts.contains(script))
        {
            scripts.remove(script);
            log.level4Debug("Removing Script: "+ script.getName());
        }
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public File getCaspac() {
        return CASPAC;
    }


    public File getLogo() {
        return logo;
    }

    public void setLogo(File logo) {
        this.logo = logo;
    }
    
    
    
    
    public void write() throws IOException {
        for (Script s : scripts)
        {
            log.level4Debug("Writing Script: "+ s.getName());
            s.writeScript(new File(TempFolder));
        }
        if (!(new File(TempFolder.toString() + slash + "-Overview.txt")).exists())
        {
            log.level4Debug("Writing Overview to: \n\t"+ TempFolder.toString() + slash + "-Overview.txt");
            new FileOperations().writeToFile(overview, TempFolder.toString() + slash + "-Overview.txt");
        }
        if (!(new File(TempFolder.toString() + slash + "-build.properties")).exists())
        {
            log.level4Debug("Writing build.properties to: \n\t"+ TempFolder.toString() + slash + "-build.properties");
            build.write(TempFolder.toString() + slash + "-build.properties");

        }
        if (!(new File(TempFolder.toString() + slash + build.bannerPic).exists()) && !(build.bannerPic.isEmpty()))
        {
            log.level4Debug("Writing Overview to: \n\t"+ TempFolder.toString() + slash +
                    build.bannerPic.substring(build.bannerPic.lastIndexOf(slash)+1,
                    build.bannerPic.length()));
            new FileOperations().copyFile(new File(build.bannerPic), new File(TempFolder.toString() + slash +
                    build.bannerPic.substring(build.bannerPic.lastIndexOf(slash)+1,
                    build.bannerPic.length())));
        }
        log.level4Debug("Creating new Zip at: \n\t"+CASPAC.toString());
        Zip zip = new Zip(CASPAC);  
        log.level4Debug("Placeing the following files in the caspac Zip");
        for(File f : new File(TempFolder.toString()).listFiles())
        {
            zip.addToZip(f);
            log.level4Debug(f.toString());
        }
        zip.execute(TempFolder);
    }
    
    public void setBuild(Properties prop){
        build=new Build(prop);
    }

    public void load() throws ZipException, IOException{
        Script dummy;
        int i;
         Unzip unzip = new Unzip(CASPAC);
         while (unzip.zipFileEntries.hasMoreElements()) {
            Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
            handleCASPACFiles(entry, unzip);
         }
         for (File f : unzipQueue)
         {
             unzip = new Unzip(f.toString());
             new File(f.toString().replace(".zip", "")).mkdir();
             unzip.unzipFile(f.toString().replace(".zip", ""));
             dummy = getScriptInstanceByFilename(f.getName());
             i = scripts.indexOf(dummy);
            dummy.includeFiles.addAll(Arrays.asList(new File(f.toString().replace(".zip", "")).listFiles()));
            scripts.set(i, dummy);
         }
    }
    private void handleCASPACFiles(Object entry, Unzip pack) throws IOException {

        //get the filename from the entry
        String filename=pack.getEntryName(entry);
        
        ///Start parsing the files
        if (filename.equals("-build.properties")) {
            build = new Build(pack.streamFileFromZip(entry));
            build.loadPropsToVariables();
        } else if (filename.toString().equals("-Overview.txt")) {
            overview=new FileOperations().readTextFromStream(pack.streamFileFromZip(entry));
        } else if (filename.toString().endsWith(".meta")) {

            Script script=getScriptInstanceByFilename(filename.toString());
            int i;
            if (!scripts.contains(script))
                scripts.add(script);
            i = scripts.indexOf(script);

            script.metaData.load(pack.streamFileFromZip(entry));
            int md5ArrayPosition = 0;
            String md5;
            List<String> md5s=new ArrayList<>();
            while ((md5 = script.metaData.metaProp.getProperty("Script.MD5[" + md5ArrayPosition + "]")) != null) {
                script.metaData.md5s.add(md5);
                md5ArrayPosition++;
            }
            scripts.set(i, script);
             //TODO: add this to the proper script  script.getName().set...
        } else if (filename.toString().endsWith(".scr")) {
            Script script=getScriptInstanceByFilename(filename.toString());
            int i;
            if (!scripts.contains(script))
                scripts.add(script);
            i = scripts.indexOf(script);
            
            String scriptText=new FileOperations().readTextFromStream(pack.streamFileFromZip(entry));
            script.setScript(scriptText);
            scripts.set(i, script);
        } else if (filename.toString().endsWith(".zip")) {
            Script script=getScriptInstanceByFilename(filename.toString());
            int i;
            if (!scripts.contains(script))
                scripts.add(script);
            i = scripts.indexOf(script);
            new FileOperations().writeStreamToFile(pack.streamFileFromZip(entry), TempFolder + slash + 
                    filename);
            unzipQueue.add(new File(TempFolder + slash + filename));
            //for (File f:new File(TempFolder + slash + "IncludeExplode"+slash).listFiles())
            //    script.includeFiles.add(f);
            
            scripts.set(i, script);
        } else if (filename.toString().endsWith(".txt")) {
             Script script=getScriptInstanceByFilename(filename.toString());
            int i;
            if (!scripts.contains(script))
                scripts.add(script);
            i = scripts.indexOf(script);
            String description=new FileOperations().readTextFromStream(pack.streamFileFromZip(entry));
            script.setDiscription(description);
            scripts.set(i, script);

        }
    }
    

    private Script getScriptInstanceByFilename(String fileName) {
        for (Script s: scripts)
            if(s.getName().equals(fileName.substring(0, fileName.lastIndexOf("."))))
                return s;
        return new Script(fileName.substring(0, fileName.lastIndexOf("."))); //TODO make an iterator to find a script by file name for loading; 
    }
    
    private ArrayList<String> getScriptNames()
    {
        ArrayList<String> scriptNames = new ArrayList<>();
        for (Script s : scripts)
            scriptNames.add(slash);
        return scriptNames;
    }
    
    
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
        public Properties buildProp= new Properties();
        
        public Build(InputStream prop) throws IOException{
            buildProp.load(prop);
        }
        
        /**
         * loads and sets properties file
         * @param prop build.properties file
         */
        public Build(Properties prop){
            this.buildProp = prop;
        }
        
        /**
         * writes build properties to a file
         * @param output file to write
         * @return true if file was written
         * @throws FileNotFoundException 
         * @throws IOException 
         */
        public boolean write(String output) throws FileNotFoundException, IOException{
            File f= new File(output);
            return write(f);
        }
        
        /**
         * writes build properties to a file
         * @param output file to write
         * @return true if file was written
         * @throws FileNotFoundException
         * @throws IOException 
         */
        public boolean write (File output) throws FileNotFoundException, IOException{
            setPropsFromVariables();
            FileOutputStream fos=new FileOutputStream(output);
            buildProp.store(fos, "This properties file was generated by CASUAL");
            return new FileOperations().verifyExists(output.toString());
        }
        
        /**
         * loads build properties from a map
         * @param buildMap key,value pairs
         */
       

        //empty build
        public Build(){
        }

        
        
        private void setPropsFromVariables(){
            buildProp.setProperty("Audio.Enabled", AudioEnabled?"True":"False");
            buildProp.setProperty("Application.AlwaysEnableControls", AudioEnabled?"True":"False");
            buildProp.setProperty("Developer.DonateToButtonText", developerDonateButtonText);
            buildProp.setProperty("Developer.Name", developerName );
            buildProp.setProperty("Window.ExecuteButtonText", executeButtonText);
            buildProp.setProperty("Window.BannerText", bannerText);
            buildProp.setProperty("Window.BannerPic", bannerPic);
            buildProp.setProperty("Window.Title", windowTitle);
            
        }
        
        /**
         * sets properties to values stored in build.properties file.
         */
        private void loadPropsToVariables() {
            if (buildProp.containsKey("Audio.Enabled"))
                AudioEnabled = buildProp.getProperty("Audio.Enabled").contains("rue");
            developerDonateButtonText = buildProp.getProperty("Developer.DonateToButtonText");
            developerName = buildProp.getProperty("Developer.Name");
            donateLink = buildProp.getProperty("Developer.DonateLink");
            executeButtonText = buildProp.getProperty("Window.ExecuteButtonText");
            bannerText = buildProp.getProperty("Window.BannerText");
            bannerPic = TempFolder + slash + buildProp.getProperty("Window.BannerPic");
            if (buildProp.contains("Application.AlwaysEnableControls"))            
                alwaysEnableControls = buildProp.getProperty("Application.AlwaysEnableControls").contains("rue");
            windowTitle = buildProp.getProperty("Window.Title");
            
        }
    }
}

