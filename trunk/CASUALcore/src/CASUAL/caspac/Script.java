/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.caspac;

import CASUAL.FileOperations;
import CASUAL.Zip;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author loganludington
 */
public class Script {
    private static String slash = System.getProperty("file.separator");
    private String name = "";
    private String script = "";
    public List<File> includeFiles = new ArrayList<>();
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
        String scriptPath=file.toString() + slash + name ;
        if (!(file.isDirectory())){
            file.mkdir();
        }
        if (!(new File(scriptPath + ".scr")).exists()){
            new FileOperations().writeToFile(script, scriptPath+ ".scr");
        }
        if (!(new File(scriptPath + ".txt")).exists()){
            new FileOperations().writeToFile(discription, scriptPath + ".txt");
        }
        if (!includeFiles.isEmpty()){
            Zip includeZip = new Zip(scriptPath + ".zip") ;
            includeZip.addToTempFolderLoc(name + ".script");
            for (File f : includeFiles){
                includeZip.addToZip(f);
            }
            includeZip.execute();
        }
        if (!(new File(scriptPath + ".meta").exists())){
            metaData.write(scriptPath + ".meta");
        }
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
        public String minSVNversion = "";
        public String scriptRevision = "";
        public String uniqueIdentifier = "";
        public String supportURL = "";
        public String updateMessage = "";
        public String killSwitchMessage = "";
        public Properties metaProp;
        public meta() {
            metaProp=new Properties();
        }

        public meta(Properties prop){
            metaProp=prop;
            setVariablesFromProperties(prop);
        }
        
        public meta(InputStream prop) throws IOException{
            metaProp.load(prop);
            setVariablesFromProperties(metaProp);
        }
        
        public meta(Map <String,String> metaMap) {
            if (metaMap.containsKey("minSVNversion"))
                minSVNversion = metaMap.get("minSVNversion");
            if (metaMap.containsKey("scriptRevsion"))
                scriptRevision = metaMap.get("scriptRevsion");
            if (metaMap.containsKey("uniqueID"))
                uniqueIdentifier = metaMap.get("uniqueID");
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
            testingBool = !(scriptRevision.isEmpty()) && testingBool;
            testingBool = !(uniqueIdentifier.isEmpty()) && testingBool;
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
            return scriptRevision;
        }

        public void setScriptRevsion(String scriptRevsion) {
            this.scriptRevision = scriptRevsion;
        }

        public String getUniqueID() {
            return uniqueIdentifier;
        }

        public void setUniqueID(String uniqueID) {
            this.uniqueIdentifier = uniqueID;
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

        
             /**
         * writes meta properties to a file
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
         * writes meta properties to a file
         * @param output file to write
         * @return true if file was written
         * @throws FileNotFoundException
         * @throws IOException 
         */
        public boolean write (File output) throws FileNotFoundException, IOException{
            setPropsFromVariables();
            FileOutputStream fos=new FileOutputStream(output);
            metaProp.store(fos, "This properties file was generated by CASUAL");
            return new FileOperations().verifyExists(output.toString());
        }
        
        
        private void setPropsFromVariables(){
            metaProp.setProperty("CASUAL.minSVN", script);
            metaProp.setProperty("Script.Revision", script);
            metaProp.setProperty("Script.ID", script);
            metaProp.setProperty("Script.SupportURL", script);
            metaProp.setProperty("Script.UpdateMessage", script);
            metaProp.setProperty("Script.KillSwitchMessage", script);
        }
        
        private void setVariablesFromProperties(Properties prop) {
            minSVNversion = prop.getProperty("CASUAL.minSVN");
            scriptRevision = prop.getProperty("Script.Revision");
            uniqueIdentifier = prop.getProperty("Script.ID");
            supportURL = prop.getProperty("Script.SupportURL");
            updateMessage = prop.getProperty("Script.UpdateMessage");
            killSwitchMessage = prop.getProperty("Script.KillSwitchMessage");
        }
        
    }
    
}
