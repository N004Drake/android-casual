/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author adam
 */
public class CASPAC {

    final public File caspac;
    public String overview;
    public Object build;
    public ArrayList scripts = new ArrayList();
    public String TempFolder;

    CASPAC(File caspac) {
        this.caspac = caspac;
        TempFolder = Statics.TempFolder + "CASPAC" + caspac.getName();

        if (new FileOperations().verifyFileExists(caspac.toString())){
            //TODO: expand into TempFolder
        } else {
            //TODO: create new file for CASPAC
        }

        //get build.properties
        this.build = new Build(new File("buildprop"));
        //TODO: read from overview
        overview = "";
        //TODO:get files.endsWith("scr") and loop through on scr file names
            {
                Script s = new Script("file name");
                scripts.add(new Object[]{s.name, s.meta, s.scr, s.txt, s.zipContents});
            }
    }

    public void write() {
        //todo write out CASPAC to File(caspac);        
    }
}

class Build {

    final File build;
    Build(File build) {
        this.build=build;
        if (new FileOperations().verifyExists(build.toString())){
            //TODO: get properties and put them into variables
        }
    }
    public String developerName = "";
    public String developerDonateButtonText = "";
    public String donateLink = "";
    public String windowTitle = "";
    public boolean usePictureForBanner = false;
    public String bannerPic = "-logo.png";
    public String executeButtonText = "Do It";
    public String AudioEnabled = "false";
    public boolean EnableControls = false;
    public void write(){
        //TODO write properties to build file
    }
}

class Script {

    String txt = "";
    String meta = "";
    String scr = "";
    ArrayList zipContents = new ArrayList();
    String name="";
    Script(String script) {
        //remove extension and get string
        String scriptWithoutExtension = "test";
        name=scriptWithoutExtension;
        scr = new FileOperations().readFile(scriptWithoutExtension + ".scr");
        meta = new FileOperations().readFile(scriptWithoutExtension + ".meta");
        txt = new FileOperations().readFile(scriptWithoutExtension + ".txt");
        //TODO: check if these files exist and create new files if needed
        Zip zip = new Zip(new File(scriptWithoutExtension + ".zip"));
    }
    public void write(){
        
    }

    class Zip {

        final File zipFile;

        Zip(File f) {
            //TODO: test if file exists and make a new one
            zipFile = f;
        }

        public ArrayList getContents() {
            //TODO: unzip "zip" to new folder in temp
            //add contents to ArrayList
            ArrayList folderContents = new ArrayList();
            return folderContents;
        }
    }
}