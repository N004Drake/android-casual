/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CASUAL.CASPAC;

import CASUAL.FileOperations;
import CASUAL.Statics;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;


/**
 *
 * @author adam
 */
public class CASPAC {

    final public File caspac;
    public String overview;
    public Build build;
    public ArrayList<script> scripts = new ArrayList();
    public String TempFolder;

    public CASPAC(File caspac) {
        this.caspac = caspac;
        TempFolder = Statics.TempFolder + "CASPAC" + caspac.getName();
        
        if (!(new File(TempFolder).exists()))
            new File(TempFolder).mkdir();
    }
    
    public void addScript(script script) {
        if (!(scripts.contains(script)))
            scripts.add(script);
}
    public void removeScript (script script) {
        if (scripts.contains(script))
            scripts.remove(script);
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
    
    public void write() {
        //todo write out CASPAC to File(caspac);        
    }
    
    public void setBuild(Map <String,String> buildMap)
    {
        build = new Build(buildMap);
    }


class Build {    
    public String developerName = "";
    public String developerDonateButtonText = "";
    public String donateLink = "";
    public String windowTitle = "";
    public boolean usePictureForBanner = false;
    public String bannerPic = "-logo.png";
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
        if (buildMap.containsKey("executeButtonText"))
            executeButtonText = buildMap.get("executeButtonText");
        if (buildMap.containsKey("AudioEnabled"))
            AudioEnabled = buildMap.get("AudioEnabled");
        if (buildMap.containsKey("EnableControls"))
            EnableControls = Boolean.getBoolean(buildMap.get("EnableControls"));
    }
    
    public void write(){
        //TODO write properties to build file
    }
    }
}

