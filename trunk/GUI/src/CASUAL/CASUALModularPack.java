/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipException;

/**
 *
 * @author adam
 */

public class CASUALModularPack {

        void loadCASUALPackFileForCommandLineOnly(String pack) {
    Unzip unzip = new Unzip();
        
        try {
            File f=new File(pack);
            if ( ! f.exists()){
                new Log().level0("File Not Found "+pack); 
                System.exit(1);
            } else {
                System.out.println("-----CASPAC MODE-----\nCASPAC: "+f.getAbsolutePath());
            }
            
            Enumeration zippedFiles =unzip.getZipFileEntries(f);
            while (zippedFiles.hasMoreElements()){
                Object entry = zippedFiles.nextElement();
                if (entry.toString().equals("-build.properties")){
                    new CASUALPackageData().setPropertiesFromInputStream(unzip.streamFileFromZip(f, entry));
                } else if (entry.toString().equals("-Overview.txt")){
                    System.out.print("\n"+new FileOperations().readTextFromStream(unzip.streamFileFromZip(f, entry))+"\n");
                } else if (entry.toString().equals("-adb_usb.ini")){
                    new FileOperations().writeStreamToFile(unzip.streamFileFromZip(f, entry), Statics.TempFolder+"adb_usb.ini");
                } else if (entry.toString().endsWith("zip")){
                    //uncompress to scripts folder
                    
                    //TODO: get zipstream and parse zipstream instead of uncompressing twice. 
                    Statics.ScriptLocation=Statics.TempFolder+ StringOperations.replaceLast(entry.toString(),".zip", "")+Statics.Slash;
                    new FileOperations().makeFolder(Statics.ScriptLocation);
                    new FileOperations().writeStreamToFile(unzip.streamFileFromZip(f, entry),Statics.ScriptLocation+entry.toString());
                    System.out.println("writing " +Statics.ScriptLocation +entry.toString());
                    unzip.unzipFile(Statics.ScriptLocation+entry.toString(), Statics.ScriptLocation);
                } else {
                    
                   //note script name
                }
                
                
                
            }
            System.exit(0);
        } catch (ZipException ex) {
            new Log().errorHandler(ex);
            new Log().level0("Zip File is corrupt. cannot continue.");
            System.exit(1);
        } catch (IOException ex) {
            new Log().errorHandler(ex);
            new Log().level0("There was a problem reading the file.");
            System.exit(1);        
        }
        
        System.out.println();
    }
        
        
        
}
