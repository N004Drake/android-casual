/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.caspac;

import CASUAL.FileOperations;
import CASUAL.MD5sum;
import CASUAL.Zip;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            List<File> includeFiles, Properties prop) {
        this.discription = discription;
        this.name = name;
        this.script = script;
        this.includeFiles = includeFiles;
        this.metaData = new meta(prop);
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

    public void setMetaData(Properties prop) {
        this.metaData = new meta(prop);
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
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
        List<String> md5s=new ArrayList<>();
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
        
        /**
         * verifies metadata is not empty
         * @return true if filled in
         */
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
        
        void doMd5Sum(){
            MD5sum md5sum = new MD5sum();
            //TODO: zip resources in this class
            //TODO: get md5sum the script.zip file
            //TODO: get md5sum for the script.scr file
            
        }
        
        /**
         * sets the properties object from local variables for writing
         */
        private void setPropsFromVariables(){
            metaProp.setProperty("CASUAL.minSVN", minSVNversion);
            metaProp.setProperty("Script.Revision", scriptRevision);
            metaProp.setProperty("Script.ID", uniqueIdentifier);
            metaProp.setProperty("Script.SupportURL", supportURL);
            metaProp.setProperty("Script.UpdateMessage", updateMessage);
            metaProp.setProperty("Script.KillSwitchMessage", killSwitchMessage);
        }
        
        /**
         * sets the variables from properties object for loading
         * @param prop properties file
         */
        private void setVariablesFromProperties(Properties prop) {
            minSVNversion = prop.getProperty("CASUAL.minSVN","");
            scriptRevision = prop.getProperty("Script.Revision","");
            uniqueIdentifier = prop.getProperty("Script.ID","");
            supportURL = prop.getProperty("Script.SupportURL","");
            updateMessage = prop.getProperty("Script.UpdateMessage","");
            killSwitchMessage = prop.getProperty("Script.KillSwitchMessage","");
        }

        void load(BufferedInputStream streamFileFromZip) {
            try {
                this.metaProp.load(streamFileFromZip);
            } catch (IOException ex) {
                Logger.getLogger(Script.class.getName()).log(Level.SEVERE, null, ex);
            }
            setVariablesFromProperties(metaProp);
            
        }
        
    }
    
}
