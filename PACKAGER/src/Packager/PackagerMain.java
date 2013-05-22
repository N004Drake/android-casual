/*PackagerMain
 ***************************************************************************
 *Copyright (C) 2013  Adam Outler
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
 **************************************************************************/

package Packager;

//This project must be built first in order to satisfy the dependencies
import CASUAL.Statics;
import CASUAL.Log;
import CASUAL.StringOperations;
import CASUAL.FileOperations;
import CASUAL.Unzip;
import CASUAL.Zip;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

/***************************************************************************
 * PackagerMain                                                                                                                                                                                                                                                                                                              
 * @author Jeremy Loper     jrloper@gmail.com
 **************************************************************************/
public class PackagerMain {



    public PackagerMain(){
        //NADA
    }

    private static boolean useOverrideArgs=true;
    private static String[] overrideArgs={"../CASPACS/backatchaverizon.zip"};
    
    protected static String userOutputDir = "";
    
    final private static String defaultOutputDir = Statics.CASUALHome + "PACKAGES" + Statics.Slash;
    
    private static String caspacWithPath = null;
    
    private static String caspacNoPath = null;
    
    private static Log log = new Log();;
    
    private static FileOperations fileOperations = new FileOperations();;
    
    public static void main(String[] args) {
        if (useOverrideArgs){
            try {
                String current = new java.io.File( "." ).getCanonicalPath();
                args=overrideArgs;                
            } catch (IOException ex) {
                Logger.getLogger(PackagerMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        processCommandline(args);
        if ( args.length>0 && args[0].length()!=0 ){
            if(doCASPACWork()) {
                if(doCASUALWork()) {
                    if(doCASPACCASUALMerge()){
                        doCleanUp();
                    }
                }
            }
        }
    }
    
    /***************************************************************************
     * processCommandline
     * @param args String array of arguments passed at runtime
     **************************************************************************/
    private static void processCommandline(String[] args) {
        if(args.length > 0) {
            caspacWithPath = args[0];
            caspacWithPath = StringOperations.removeLeadingAndTrailingSpaces(args[0]);
            
            if(caspacWithPath.endsWith(".zip") || caspacWithPath.endsWith(".jar") || caspacWithPath.endsWith(".caspac")) {
                //log.level4Debug("[processCommandline()]File type is known based on extension");
                caspacNoPath = caspacWithPath;
                //log.level4Debug("[processCommandline()]Removing file path");
                caspacNoPath=new File(caspacNoPath).getName();
                caspacNoPath=caspacNoPath.substring(0, caspacNoPath.indexOf("."));
            }            
        log.level0Error("[processCommandline()]File type is unknown based on extention");
        }
        
        if(args.length > 1) {
            if(args[1].endsWith(Statics.Slash)) { 
                userOutputDir = StringOperations.removeLeadingAndTrailingSpaces(args[1]);
            } else {
                userOutputDir = StringOperations.removeLeadingAndTrailingSpaces(args[1]) + Statics.Slash;
            } 
        } else {
            try {
                userOutputDir = caspacWithPath;
                userOutputDir=new File(userOutputDir).getCanonicalPath().toString();
                userOutputDir=userOutputDir.substring(0, userOutputDir.lastIndexOf(Statics.Slash))+Statics.Slash;
                log.level2Information("Set output folder to default: " + userOutputDir);
            } catch (IOException ex) {
                Logger.getLogger(PackagerMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
            
        log.level2Information("CASUAL's CASPAC deployment PACKAGER              ");
        log.level2Information("                                                 ");
        log.level2Information("java -jar PACKAGER.jar [OPTIONS]                 ");
        log.level2Information("                                                 ");
        log.level2Information("input CASPAC file (required)                     ");
        log.level2Information("java -jar PACKAGER.jar CASPAC.zip                ");
        log.level2Information("                                                 ");
        log.level2Information("output directory (optional)                      ");
        log.level2Information("default (user.home)/.CASUAL/PACKAGES             ");
        log.level2Information("java -jar PACKAGER.jar CASPAC.zip C:/OUTPUT/     ");
        //new java.util.Scanner(System.in).nextLine();        
    }
    
    /***************************************************************************
     * doCASPACWork
     * @return Success = True, Failure = False 
     **************************************************************************/
    private static boolean doCASPACWork() {
        if(!fileOperations.makeFolder(Statics.TempFolder + "CASPAC")) return false;
        //log.level4Debug("[doCASUALWork()]Created folder " + Statics.TempFolder + "CASPAC");
        
        try {
            new Unzip().unzipFile(caspacWithPath, Statics.TempFolder + "CASPAC");
        }
        catch (ZipException ex) {
            log.errorHandler(ex);
            return false;
        }
        catch (IOException ex) {
            log.errorHandler(ex);
            return false;
        }
        
        //log.level4Debug("[doCASPACWork()]CASPAC unzipped to " + Statics.TempFolder + "CASPAC");
        return true;
    }
    
    /***************************************************************************
     * doCASUALWork
     * @return Success = True, Failure = False
     **************************************************************************/
    private static boolean doCASUALWork() {
        if(!fileOperations.makeFolder(Statics.TempFolder + "CASUAL")) return false;
        //log.level4Debug("[doCASUALWork()]Created folder " + Statics.TempFolder + "CASUAL");
        fileOperations.copyFromResourceToFile("/Packager/resources/CASUAL.jar", Statics.TempFolder + "CASUAL.zip");
        
        try {    
            new Unzip().unzipFile(Statics.TempFolder + "CASUAL.zip", Statics.TempFolder + "CASUAL" + Statics.Slash);
        } catch (FileNotFoundException ex) {
            log.errorHandler(ex);
            return false;
        } catch (IOException ex) {
            log.errorHandler(ex);
            return false;
        }
        
        //log.level4Debug("[doCASUALWork()]Unzipping CASUAL.jar from PACKAGER.jar's resources");
        
        String folderToDelete=Statics.TempFolder + "CASUAL" + Statics.Slash + "SCRIPTS" + Statics.Slash;
        //log.level4Debug("[doCASUALWork()]Getting /SCRIPTS folder contents list");

        if(fileOperations.deleteStringArrayOfFiles(fileOperations.listFolderFiles(folderToDelete)))  return false;
        
        //log.level4Debug("[doCASUALWork()]Folder is empty");
        //log.level4Debug("[doCASUALWork()]Operation Complete");
        return true;
    }
    
    /***************************************************************************
     * doCASPACCASUALMerge merges the extracted CASPAC and extracted CASUAL.jar
     * into a new caspac-CASUAL.jar
     * @return Success = True, Failure = False
     **************************************************************************/
    private static boolean doCASPACCASUALMerge() {
        String[] files = fileOperations.listFolderFiles(Statics.TempFolder + "CASPAC" + Statics.Slash);
        int x = 0;
        
        if(files[x] == null) {
            log.level0Error("[doCASPACCASUALMerge()]CASPAC contained no files.");
            return false;
        }
        
        while(files[x] != null) {
            try {
                fileOperations.moveFile(Statics.TempFolder + "CASPAC" + Statics.Slash + files[x], Statics.TempFolder + "CASUAL" + Statics.Slash + "SCRIPTS" + Statics.Slash + files[x]);
            } catch (IOException ex) {
                log.errorHandler(ex);
                return false;
            }
            x++;
        }
        
        //log.level4Debug("[doCASPACCASUALMerge()]File bases merged");
        
        try {
            new Zip().addFilesToNewZip(Statics.TempFolder + caspacNoPath + "-CASUAL.jar", Statics.TempFolder + "CASUAL" + Statics.Slash);
        } catch (Exception ex) {
            log.errorHandler(ex);
            return false;
        }
        
        fileOperations.makeFolder(defaultOutputDir);
        String output;
        try {
            if (userOutputDir.equals("")) {
                output=defaultOutputDir + caspacNoPath + "-CASUAL.jar";
                fileOperations.moveFile(Statics.TempFolder + caspacNoPath + "-CASUAL.jar",defaultOutputDir);
            } else {
                output=userOutputDir + caspacNoPath + "-CASUAL.jar";
                fileOperations.moveFile(Statics.TempFolder + caspacNoPath + "-CASUAL.jar",userOutputDir + caspacNoPath + "-CASUAL.jar");
            }
            fileOperations.setExecutableBit(output);
        } catch (IOException ex) {
            log.errorHandler(ex);
            return false;
        }
        
        //log.level4Debug("[doCASPACCASUALMerge()] " + caspacNoPath + "-CASUAL.jar created");
        
        return true;
    }
    
    /***************************************************************************
     * doCleanUp
     **************************************************************************/
    private static void doCleanUp(){
        fileOperations.recursiveDelete(Statics.TempFolder + "CASUAL");
        fileOperations.recursiveDelete(Statics.TempFolder + "CASPAC");  
    }
    
}

