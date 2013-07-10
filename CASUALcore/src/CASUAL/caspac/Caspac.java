/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CASUAL.caspac;

import CASUAL.CASPACData;
import CASUAL.CASUALapplicationData;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.MD5sumRunnable;
import CASUAL.Statics;
import CASUAL.StringOperations;
import CASUAL.Unzip;
import CASUAL.Zip;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
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


    public Caspac(File caspac) {
        this.CASPAC = caspac;
        System.out.println(Statics.TempFolder);
        log.level4Debug("Creating folder for CASPAC at\n\t"
                + Statics.TempFolder + "CASPAC" + caspac.getName());
        TempFolder = Statics.TempFolder + "CASPAC" + caspac.getName();
        
        if (!(new File(TempFolder).exists()))
            new File(TempFolder).mkdir();
    }
    
    public Caspac(File caspac , File logo) {
        this.CASPAC = caspac;
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
            log.level4Debug("Writing Overview to: \n\t"+ 
                    TempFolder.toString() + slash + "-Overview.txt");
            new FileOperations().writeToFile(overview, TempFolder.toString() + slash + "-Overview.txt");
        }
        if (!(new File(TempFolder.toString() + slash + "-build.properties")).exists())
        {
            log.level4Debug("Writing build.properties to: \n\t"+ 
                    TempFolder.toString() + slash + "-build.properties");
            new FileOperations().writeToFile(build.buildFile(), TempFolder.toString() + slash + "-build.properties");
        }
        if (!(new File(TempFolder.toString() + slash + build.bannerPic).exists()) && !(build.bannerPic.isEmpty()))
        {
            log.level4Debug("Writing Overview to: \n\t"+ 
                    TempFolder.toString() + slash +
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
    
    public void setBuild(Map <String,String> buildMap)
    {
        build = new Build(buildMap);
    }

    public void load() throws ZipException, IOException{
         Unzip unzip = new Unzip(CASPAC);
         while (unzip.zipFileEntries.hasMoreElements()) {
            Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
            handleCASPACFiles(entry, unzip);
         }
    }
    private void handleCASPACFiles(Object entry, Unzip pack) throws IOException {

        //get the filename from the entry
        String filename=pack.getEntryName(entry);
        
        ///Start parsing the files
        if (filename.equals("-build.properties")) {
            //load build.prop from memory
            Properties prop = new Properties();
            prop.load(pack.streamFileFromZip(entry));
            this.build = new Build();
            try {
                //build.usePictureForBanner = prop.getProperty("Window.UsePictureForBanner").contains("rue");
                build.AudioEnabled = prop.getProperty("Audio.Enabled").contains("rue");
                build.developerDonateButtonText = prop.getProperty("Developer.DonateToButtonText");
                build.developerName = prop.getProperty("Developer.Name");
                build.executeButtonText = prop.getProperty("Window.ExecuteButtonText");
                //load the CASUALApp resource bundle to get the proper window title. 
                ResourceBundle casualAppData=ResourceBundle.getBundle("CASUAL/resources/CASUALApp");
                build.windowTitle = prop.getProperty("Window.Title") + " - " + casualAppData.getString("Application.title") + casualAppData.getString("Application.revision");
                build.bannerText = prop.getProperty("Window.BannerText");
                build.bannerPic = prop.getProperty("Window.BannerPic");
                build.alwaysEnableControls = prop.getProperty("Application.AlwaysEnableControls").contains("rue");
            } catch (java.util.MissingResourceException ex) {
                //Do nothing, bundle is missing.
            }
        } else if (filename.toString().equals("-Overview.txt")) {
            overview=new FileOperations().readTextFromStream(pack.streamFileFromZip(entry));
        } else if (filename.toString().endsWith(".meta")) {
            if (!getScriptNames().contains(filename.toString().substring(0, filename.toString().lastIndexOf("."))))
            {
                Script script = new Script(filename.toString().substring(0, filename.toString().lastIndexOf(".")));
                scripts.add(script);
                script.setName("testing123");
            }
            else
            {
                Script script=getScriptInstanceByFilename(filename.toString());
            }
            Properties prop = new Properties();
            prop.load(pack.streamFileFromZip(entry)); //TODO: add this to the proper script
             
            String minSVNRevision = prop.getProperty("CASUAL.minSVN");
            String scriptRevision = prop.getProperty("Script.Revision");
            String uniqueIdentifier = prop.getProperty("Script.ID");
            String supportURL = prop.getProperty("Script.SupportURL");
            String updateMessage = prop.getProperty("Script.UpdateMessage");
            int md5ArrayPosition = 0;
            System.out.print("Script.MD5[" + md5ArrayPosition + "]");
            String md5;
            List<String> md5s=new ArrayList<>();
            while ((md5 = prop.getProperty("Script.MD5[" + md5ArrayPosition + "]")) != null) {
                md5s.add(md5);
                md5ArrayPosition++;
            }
             //TODO: add this to the proper script  script.getName().set...
        } else if (filename.toString().endsWith(".scr")) {
            String script=new FileOperations().readTextFromStream(pack.streamFileFromZip(entry));
            //TODO: add this to the proper script  script.getName().set...
        } else if (filename.toString().endsWith(".zip")) {
            pack.deployFileFromZip(entry, TempFolder);
            //TODO: add this to the proper script  script.getName().set...
        } else if (filename.toString().endsWith(".txt")) {
            String script=new FileOperations().readTextFromStream(pack.streamFileFromZip(entry));
            //TODO: add this to the proper script  script.getName().set...
        }
    }
    
    /**
     * loads properties from a string
     *
     * @param propertiesString string containing properties
     * @return Properties object
     */
    private Properties load(String propertiesString) throws IOException {
        Properties properties = new Properties();
        properties.load(new StringReader(propertiesString));
        return properties;
    }    

    private Script getScriptInstanceByFilename(String fileName) {
        for (Script s: scripts)
            if(s.getName().equals(fileName.substring(0, fileName.lastIndexOf("."))))
                return s;
        return new Script(); //TODO make an iterator to find a script by file name for loading; 
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

        public Build(Map<String,String> buildMap)
        {
            if (buildMap.containsKey("developerName"))
                developerName = buildMap.get("developerName");
            if (buildMap.containsKey("developerDonateButtonText"))
                developerDonateButtonText = buildMap.get("developerDonateButtonText");
            if (buildMap.containsKey("donateLink"))
                donateLink = buildMap.get("donateLink");
            if (buildMap.containsKey("windowTitle"))
                windowTitle = buildMap.get("windowTitle");
            if (buildMap.containsKey("usePictureForBanner"))
                usePictureForBanner = Boolean.valueOf(buildMap.get("usePictureForBanner"));
            if (buildMap.containsKey("bannerPic"))
                bannerPic = buildMap.get("bannerPic");
            if (buildMap.containsKey("bannerText"))
                bannerText = buildMap.get("bannerText");
            if (buildMap.containsKey("executeButtonText"))
                executeButtonText = buildMap.get("executeButtonText");
            if (buildMap.containsKey("AudioEnabled"))
                AudioEnabled = buildMap.get("AudioEnabled").contains("rue");
            if (buildMap.containsKey("EnableControls"))
                alwaysEnableControls = Boolean.getBoolean(buildMap.get("EnableControls"));
        }
        
        public Build()
        {
            
        }

        public String buildFile(){
            String buildString = "";
            buildString = buildString + "#Developer Name\n" + "Developer.Name=" + 
                    developerName + "\n";
            buildString = buildString + "#Donation link button title\n" + 
                    "Developer.DonateToButtonText=" + developerDonateButtonText + "\n";
            buildString = buildString + "#Link for donate button\n" + 
                    "Developer.DonateLink=" + donateLink + "\n";
            buildString = buildString + "#This is the window title\n" + 
                    "Window.Title=" + windowTitle +"\n";
            buildString = buildString + "#If true, BannerPic will be used for the main window banner decoration"
                    + "Window.UsePictureForBanner=" + usePictureForBanner + "\n";
            buildString = buildString + "#The main window banner\n" + "Window.BannerPic=" 
                    + bannerPic +"\n";
            buildString = buildString + "#If UsePictureForBanner is false this text will be displayed in large format" +
                    "Window.BannerText=" + bannerText + "\n";
            buildString = buildString + "#text for main button\n" + "Window.ExecuteButtonText=" +
                    executeButtonText + "\n";
            buildString = buildString + "#\"true\" or \"True\" to enable\n" + "Audio.Enabled=" +
                    AudioEnabled + "\n";
            buildString = buildString + "#Enable Connection/Disconnection control locks\n" + 
                    "Application.AlwaysEnableControls=" + alwaysEnableControls + "\n";
            return buildString;
        }
    }
}

