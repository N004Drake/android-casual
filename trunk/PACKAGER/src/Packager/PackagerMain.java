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

import CASUAL.Statics;
import CASUAL.Log;
import CASUAL.StringOperations;
import CASUAL.FileOperations;
import CASUAL.Unzip;
import CASUAL.Zip;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipException;

/***************************************************************************
 *                                                                                                                                                                                                                                                                                                               
 * @author Jeremy Loper     jrloper@gmail.com
 **************************************************************************/
public class PackagerMain {

     /*************************************************************************
     * default constructor
     **************************************************************************/
    public PackagerMain(){
        //NADA
    }
    
    /***************************************************************************
     * 
     **************************************************************************/
    private static String caspacWithPath = "";
    
    /***************************************************************************
     * 
     **************************************************************************/
    private static String caspacNoPath = "";
    
    /***************************************************************************
     * 
     **************************************************************************/
    private static Log log = new Log();;
    
    /***************************************************************************
     * 
     **************************************************************************/
    private static FileOperations caspacIO = new FileOperations();;
    
    /***************************************************************************
     * @param args the command line arguments
     **************************************************************************/
    public static void main(String[] args) {
        args = new String[] {"C:\\Users\\Jeremy\\Desktop\\CASUAL\\CASPACS\\backatchaverizon.zip"};
        processCommandline(args);
        if(doCASPACWork()) {
            if(doCASUALWork()) {
                if(doCASPACCASUALMerge()){
                    doCleanUp();
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
                //log.level4Debug("[processCommandline()]Removing file extension");
                if(caspacNoPath.endsWith(".zip")) {
                    caspacNoPath = caspacNoPath.replace(".zip", "");
                } else if(caspacNoPath.endsWith(".jar")) {
                    caspacNoPath = caspacNoPath.replace(".jar", "");
                } else if(caspacNoPath.endsWith(".caspac")) {
                    caspacNoPath = caspacNoPath.replace(".caspac", "");
                }
                //log.level4Debug("[processCommandline()]Removing file path");
                caspacNoPath = caspacNoPath.substring(caspacNoPath.lastIndexOf(Statics.Slash) + 1, caspacNoPath.length() - 1);
            }
            //log.level0Error("[processCommandline()]File type is unknown based on extention");
            return;
        }
        //log.level0Error("[processCommandline()]No file specified");
    }
    
    /***************************************************************************
     * 
     * @return Success = True, Failure = False 
     **************************************************************************/
    private static boolean doCASPACWork() {
        try {
            if(!caspacIO.makeFolder(Statics.TempFolder + "CASPAC")) return false;
            log.level4Debug("[doCASUALWork()]Created folder " + Statics.TempFolder + "CASPAC");
            new Unzip().unzipFile(caspacWithPath, Statics.TempFolder + "CASPAC");
            log.level4Debug("[doCASPACWork()]CASPAC unzipped to " + Statics.TempFolder + "CASPAC");
            return true;
        } catch (ZipException ex) {
            log.errorHandler(ex);
        } catch (IOException ex) {
            log.errorHandler(ex);
        }
        return false;
    }
    
    /***************************************************************************
     * 
     * @return Success = True, Failure = False
     **************************************************************************/
    private static boolean doCASUALWork() {
        try {
            if(!caspacIO.makeFolder(Statics.TempFolder + "CASUAL")) return false;
            log.level4Debug("[doCASUALWork()]Created folder " + Statics.TempFolder + "CASUAL");
            caspacIO.copyFromResourceToFile("/Packager/resources/CASUAL.jar", Statics.TempFolder + "CASUAL.zip");
            new Unzip().unzipFile(Statics.TempFolder + "CASUAL.zip", Statics.TempFolder + "CASUAL" + Statics.Slash);
            log.level4Debug("[doCASUALWork()]Unzipping CASUAL.jar from PACKAGER.jar's resources");
            String[] cleanUp = caspacIO.listFolderFiles(Statics.TempFolder + "CASUAL" + Statics.Slash + "SCRIPTS" + Statics.Slash);
            log.level4Debug("[doCASUALWork()]Getting /SCRIPTS folder contents list");
            int x = 0;
            if(cleanUp[x] != null)  {
                log.level4Debug("[doCASUALWork()]Folder is not empty, deleting files");
                while(cleanUp[x] != null) {
                    if(!caspacIO.deleteFile(cleanUp[x])) return false;
                    x++;
                }
                log.level4Debug("[doCASUALWork()]Folder is empty");
                log.level4Debug("[doCASUALWork()]Operation Complete");
                return true;
            }
            log.level4Debug("[doCASUALWork()]Folder is empty");
            log.level4Debug("[doCASUALWork()]Operation Complete");
            return true;
        } catch (FileNotFoundException ex) {
            log.errorHandler(ex);
        } catch (IOException ex) {
            log.errorHandler(ex);
        }
        return false;
    }
    
    /***************************************************************************
     * doCASPACCASUALMerge merges the extracted CASPAC and extracted CASUAL.jar
     * into a new caspac-CASUAL.jar
     * @return Success = True, Failure = False
     **************************************************************************/
    private static boolean doCASPACCASUALMerge() {
        String[] files = caspacIO.listFolderFiles(Statics.TempFolder + "CASPAC" + Statics.Slash);
        int x = 0;
        if(files[x] == null) {
            log.level0Error("[doCASPACCASUALMerge()]CASPAC contained no files.");
            return false;
        }
        while(files[x] != null) {
            try {
                caspacIO.moveFile(Statics.TempFolder + "CASPAC" + Statics.Slash + files[x], Statics.TempFolder + "CASUAL" + Statics.Slash + files[x]);
            } catch (IOException ex) {
                log.errorHandler(ex);
            }
            x++;
        }
        log.level4Debug("[doCASPACCASUALMerge()]File bases merged");
        //try {
            //Zip.addFilesToExistingZip(Statics.TempFolder + caspacNoPath + "-CASUAL.jar", files);
            //caspacIO.moveFile(Statics.TempFolder + caspacNoPath + "-CASUAL.jar", getProperty()); //TODO Create new CASUAL.jar, move it somewhere, done.
            //log.level4Debug("[doCASPACCASUALMerge()] " + caspacNoPath + "-CASUAL.jar created");
        //} catch (IOException ex) {
            //log.errorHandler(ex);
        //}
        return true;
    }
    
    /***************************************************************************
     * 
     **************************************************************************/
    private static void doCleanUp(){
        caspacIO.recursiveDelete(Statics.TempFolder + "CASUAL");
        caspacIO.recursiveDelete(Statics.TempFolder + "CASPAC");  
    }
}

