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
    private static Log log = new CASUAL.Log();
    
    /***************************************************************************
     * 
     **************************************************************************/
    private static FileOperations caspacIO = new FileOperations();
    
    /***************************************************************************
     * @param args the command line arguments
     **************************************************************************/
    public int main(String[] args) {
        String caspac = processCommandline(args);
        if(doCASPACWork(caspac)) {
            if(doCASUALWork()) {
                if(doCASPACCASUALMerge(caspac)){
                    doCleanUp();
                    return 0;
                }
                return 1;
            }
            return 2;
        }
        return 3;
    }
    
    /***************************************************************************
     * 
     * @param args
     * @return 
     **************************************************************************/
    private static String processCommandline(String[] args) {
        if(args.length > 0 && args[0] != null) {
            if(args[0].contains(".zip") || args[0].contains(".jar") || args[0].contains(".caspac")) {
                log.level4Debug("[processCommandline()]File type is known based on extention");
                return StringOperations.removeLeadingAndTrailingSpaces(args[0]);
            }
            log.level0Error("[processCommandline()]File type is unknown based on extention");
            return null;
        }
        log.level0Error("[processCommandline()]No file specified");
        return null;
    }
    
    /***************************************************************************
     * 
     * @param caspac
     * @return 
     **************************************************************************/
    private static boolean doCASPACWork(String caspac) {
        try {
            if(caspac == null) {
                log.level0Error("[doCASPACWork()]No file or unknown file type specified.");
                return false;
            }
            new Unzip().unzipFile(caspac, Statics.TempFolder + "CASPAC");
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
     * @return 
     **************************************************************************/
    private static boolean doCASUALWork() {
        try {
            if(!caspacIO.makeFolder(Statics.TempFolder + "CASUAL")) return false;
            log.level4Debug("[doCASUALWork()]Created folder " + Statics.TempFolder + "CASUAL");
            new Unzip().unZipResource("/resouce/CASUAL.jar", Statics.TempFolder + "CASUAL" );
            log.level4Debug("[doCASUALWork()]Unzipping CASUAL.jar from PACKAGER.jar's resources");
            String[] cleanUp = caspacIO.listFolderFiles(Statics.TempFolder + "CASUAL" + Statics.Slash + "SCRIPTS", true);
            log.level4Debug("[doCASUALWork()]Getting /SCRIPTS folder contents list");
            if(cleanUp.length > 0)  {
                log.level4Debug("[doCASUALWork()]Folder is not empty, deleting files");
                for(int x = 0; cleanUp.length > x; x++) {
                    if(!caspacIO.deleteFile(cleanUp[x])) return false;
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
     * 
     * @return 
     **************************************************************************/
    private static boolean doCASPACCASUALMerge(String caspac) {
        String[] files = caspacIO.listFolderFiles(Statics.TempFolder + "CASPAC", true);
        if(files.length == 0) {
            log.level0Error("[doCASPACCASUALMerge()]CASPAC contained no files.");
            return false;
        }
        for(int x = 0; files.length > x; x++) {
            try {
                caspacIO.moveFile(files[x], Statics.TempFolder + "CASPAC" + Statics.Slash);
            } catch (IOException ex) {
                log.errorHandler(ex);
            }
        }
        log.level4Debug("[doCASPACCASUALMerge()]File bases merged");
        String temp = caspac.replace(".jar", "");
        if(temp == null) {
            temp = caspac.replace(".zip", "");
            if(temp == null) {
                caspac = caspac.replace(".caspac", "");
            }
        }
        try {
            Zip.addFilesToExistingZip(temp + "-CASUAL.jar", files);
            log.level4Debug("[doCASPACCASUALMerge()] " + temp + "-CASUAL.jar created");
        } catch (IOException ex) {
            log.errorHandler(ex);
        }
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

