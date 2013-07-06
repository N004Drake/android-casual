/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CASUAL.caspac;

import CASUAL.FileOperations;
import CASUAL.Statics;
import CASUAL.Zip;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


/**
 *
 * @author adam
 */
public class Caspac {
    public File logo;
    private static String slash = System.getProperty("file.separator");
    public File caspac;
    public String overview;
    public Build build;
    public ArrayList<Script> scripts = new ArrayList<>();
    public String TempFolder;

    public Caspac() {
        TempFolder = Statics.TempFolder + "CASPAC";
        
        if (!(new File(TempFolder).exists()))
            new File(TempFolder).mkdir();
    }
    
    

    public Caspac(File caspac) {
        this.caspac = caspac;
        TempFolder = Statics.TempFolder + "CASPAC" + caspac.getName();
        
        if (!(new File(TempFolder).exists()))
            new File(TempFolder).mkdir();
    }
    
    public Caspac(File caspac , File logo) {
        this.caspac = caspac;
        TempFolder = Statics.TempFolder + "CASPAC" + caspac.getName();
        if (logo.exists())
            this.logo = logo;
        
        if (!(new File(TempFolder).exists()))
            new File(TempFolder).mkdir();
    }
    
    public void addScript(Script script) {
        if (!(scripts.contains(script)))
            scripts.add(script);
}
    public void removeScript (Script script) {
        if (scripts.contains(script))
            scripts.remove(script);
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public File getCaspac() {
        return caspac;
    }

    public void setCaspac(File caspac) {
        this.caspac = caspac;
    }
    
    
    
    public void write() throws IOException {
        for (Script s : scripts)
            s.writeScript(new File(TempFolder));
        if (!(new File(TempFolder.toString() + slash + "-Overview.txt")).exists())
            new FileOperations().writeToFile(overview, TempFolder.toString() + slash + "-Overview.txt");
        if (!(new File(TempFolder.toString() + slash + "-build.properties")).exists())
            new FileOperations().writeToFile(build.buildFile(), TempFolder.toString() + slash + "-build.properties");
        if (!(new File(TempFolder.toString() + slash + logo.getName()).exists()))
            new FileOperations().copyFile(logo, new File(TempFolder.toString() + slash + logo.getName()));
        Zip zip = new Zip(caspac);   
        for(File f : new File(TempFolder.toString()).listFiles())
            zip.addToZip(f);
    }
    
    public void setBuild(Map <String,String> buildMap)
    {
        build = new Build(buildMap);
    }


    public class Build {    
        public String developerName = "";
        public String developerDonateButtonText = "";
        public String donateLink = "";
        public String windowTitle = "";
        public boolean usePictureForBanner = false;
        public String bannerPic = "-logo.png";
        public String bannerText = "";
        public String executeButtonText = "Do It";
        public String AudioEnabled = "false";
        public boolean EnableControls = false;

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
                AudioEnabled = buildMap.get("AudioEnabled");
            if (buildMap.containsKey("EnableControls"))
                EnableControls = Boolean.getBoolean(buildMap.get("EnableControls"));
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
                    "Application.AlwaysEnableControls=" + EnableControls + "\n";
            return buildString;
        }
    }
}

