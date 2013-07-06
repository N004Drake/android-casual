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
import java.util.List;
import java.util.Map;

/**
 *
 * @author loganludington
 */
public class Script {
    private static String slash = System.getProperty("file.separator");
    private String name = "";
    private String script = "";
    public List<File> includeFiles = new ArrayList();
    public meta metaData = new meta();
    private String discription="";

    public Script(String name) {
        this.name=name;
    }

    
    
    public Script(String name, String script, String discription, List<File> includeFiles) {
        this.discription = discription;
        this.name = name;
        this.script = script;
        this.includeFiles = includeFiles;
    }

    public Script(String name, String script, String discription, 
            List<File> includeFiles, Map<String,String> metaMap) {
        this.discription = discription;
        this.name = name;
        this.script = script;
        this.includeFiles = includeFiles;
        this.metaData = new meta(metaMap);
    }

    public Script(String name, String script, String discription) {
        this.name = name;
        this.script = script;
        this.discription = discription;
    }
    
    public Script(){
        
    }
    
    public boolean verifyScript()
    {
        boolean testingBool = true;
        testingBool = !(name.isEmpty()) && testingBool;
        testingBool = !(script.isEmpty()) && testingBool;
        testingBool = !(includeFiles.isEmpty()) && testingBool;
        testingBool = !(discription.isEmpty())  && testingBool;
        testingBool = metaData.verifyMeta() && testingBool;
        return testingBool;
    }
    
    public void writeScript(File file) throws IOException
    {
        if (!(verifyScript()))
            return;
        if (!(file.isDirectory()))
            file.mkdir();
        if (!(new File(file.toString() + slash + name + ".scr")).exists())
            new FileOperations().writeToFile(script, file.toString() + slash + name + ".scr");
        if (!(new File(file.toString() + slash + name + ".txt")).exists())
            new FileOperations().writeToFile(discription, file.toString() + slash + name + ".txt");
        if (!(new File(Statics.TempFolder + slash + name + ".zip").exists()))
        {
            Zip includeZip = new Zip(new File(Statics.TempFolder + slash + name + ".zip")) ;
            for (File f : includeFiles)
                includeZip.addToZip(f);
            includeZip.execute();
        }
        if (!(new File(file.toString() + slash + name + ".meta").exists()))
            new FileOperations().writeToFile(metaStringBuilder(), file.toString() + slash + name + ".meta");
    }
    
    private String metaStringBuilder()
    {
        String meta = "";
        meta = meta + "CASUAL.minSVN=" + metaData.getMinSVNversion()+ "\n";
        meta = meta + "Script.Revision=" + metaData.getScriptRevsion() + "\n";
        meta = meta + "Script.ID=" + metaData.getUniqueID() + "\n";
        meta = meta + "Script.SupportURL=" + metaData.getSupportURL() + "\n";
        meta = meta + "Script.UpdateMessage=" + metaData.getUpdateMessage() + "\n";
        meta = meta + "Script.KillSwitchMessage=" + metaData.getKillSwitchMessage() + "\n";
        return meta;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public meta getMetaData() {
        return metaData;
    }

    public void setMetaData(meta metaData) {
        this.metaData = metaData;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }
    
    
    
    public static void main(String args[]) {
        String test = "";
        System.out.println(test.isEmpty() && false);
    }

    @Override
    public String toString() {
        return name;
    }
    
    
    
    public class meta{
        private String minSVNversion = "";
        private String scriptRevsion = "";
        private String uniqueID = "";
        private String supportURL = "";
        private String updateMessage = "";
        private String killSwitchMessage = "";

        public meta() {
        }

        public meta(Map <String,String> metaMap) {
            if (metaMap.containsKey("minSVNversion"))
                minSVNversion = metaMap.get("minSVNversion");
            if (metaMap.containsKey("scriptRevsion"))
                scriptRevsion = metaMap.get("scriptRevsion");
            if (metaMap.containsKey("uniqueID"))
                uniqueID = metaMap.get("uniqueID");
            if (metaMap.containsKey("supportURL"))
                supportURL = metaMap.get("supportURL");
            if (metaMap.containsKey("updateMessage"))
                updateMessage = metaMap.get("updateMessage");
            if (metaMap.containsKey("killSwitchMessage"))
                killSwitchMessage = metaMap.get("killSwitchMessage");
        }
        
        public boolean verifyMeta (){
            boolean testingBool = true;
            testingBool = !(minSVNversion.isEmpty()) && testingBool;
            testingBool = !(scriptRevsion.isEmpty()) && testingBool;
            testingBool = !(uniqueID.isEmpty()) && testingBool;
            testingBool = !(supportURL.isEmpty()) && testingBool;
            testingBool = !(updateMessage.isEmpty()) && testingBool;
            testingBool = !(killSwitchMessage.isEmpty()) && testingBool;
            return testingBool;
            
        }
        
        public String getMinSVNversion() {
            return minSVNversion;
        }

        public void setMinSVNversion(String minSVNversion) {
            this.minSVNversion = minSVNversion;
        }

        public String getScriptRevsion() {
            return scriptRevsion;
        }

        public void setScriptRevsion(String scriptRevsion) {
            this.scriptRevsion = scriptRevsion;
        }

        public String getUniqueID() {
            return uniqueID;
        }

        public void setUniqueID(String uniqueID) {
            this.uniqueID = uniqueID;
        }

        public String getSupportURL() {
            return supportURL;
        }

        public void setSupportURL(String supportURL) {
            this.supportURL = supportURL;
        }

        public String getUpdateMessage() {
            return updateMessage;
        }

        public void setUpdateMessage(String updateMessage) {
            this.updateMessage = updateMessage;
        }

        public String getKillSwitchMessage() {
            return killSwitchMessage;
        }

        public void setKillSwitchMessage(String killSwitchMessage) {
            this.killSwitchMessage = killSwitchMessage;
        }
        
    }
    
}
