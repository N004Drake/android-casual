/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

/**
 *
 * @author adam
 */
public class CASUALModularPack {

    String activeScript = "";
    Thread script;
    Thread zip;

    void loadCASUALPackFileForCommandLineOnly(String pack) {
        Unzip unzip = new Unzip();
        try {
            Thread adb = new Thread(new CASUALTools().adbDeployment);
            adb.start();
            File f = new File(pack);
            if (!f.exists()) {
                new Log().level0("File Not Found " + pack);
                System.exit(1);
            } else {
                System.out.println("-----CASPAC MODE-----\nCASPAC: " + f.getAbsolutePath());
            }
            //begin unziping and analyzing CASPAC
            Enumeration zippedFiles = unzip.getZipFileEntries(f);
            while (zippedFiles.hasMoreElements()) {
                Object entry = zippedFiles.nextElement(); //get the object and begin examination
                if (entry.toString().equals("-build.properties")) {
                    
                    new CASUALPackageData().setPropertiesFromInputStream(unzip.streamFileFromZip(f, entry));
                } else if (entry.toString().equals("-Overview.txt")) {
                    System.out.print("\n" + new FileOperations().readTextFromStream(unzip.streamFileFromZip(f, entry)) + "\n");
                    //TODO: allow specification of which script here and take no action unless specified script is called. 
                } else if (entry.toString().endsWith(".scr")) {
                    String scriptBasename = StringOperations.replaceLast(entry.toString(), ".scr", "");
                    Statics.ScriptLocation = Statics.TempFolder + scriptBasename + Statics.Slash;
                    activeScript = Statics.ScriptLocation + scriptBasename+".scr";
                    new FileOperations().makeFolder(Statics.ScriptLocation);
                    new FileOperations().writeStreamToFile(unzip.streamFileFromZip(f, entry), activeScript);
                    script=new Thread(new MD5sumRunnable(activeScript,entry.toString()));
                } else if (entry.toString().endsWith(".zip")) {
                    Statics.ScriptLocation =StringOperations.replaceLast(Statics.TempFolder+entry.toString(), ".zip", "") + Statics.Slash;
                    new FileOperations().makeFolder(Statics.ScriptLocation);
                    unzip.unZipInputStream(unzip.streamFileFromZip(f, entry), Statics.ScriptLocation);
                    zip=new Thread(new MD5sumRunnable(unzip.streamFileFromZip(f, entry),entry.toString()));
                    
                } else if (entry.toString().endsWith(".txt")) {
                    System.out.print("\n" + new FileOperations().readTextFromStream(unzip.streamFileFromZip(f, entry)) + "\n");
                }
                //DONE with extraction and preparation of zip
            }

            //get ADB ready
            try {
                script.join();
                adb.join();
                zip.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CASUALModularPack.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            //TODO: verify MD5 in JAR mode and update MD5 in IDE mode
            for (Object md5 :  Statics.runnableMD5list.toArray()){
                System.out.println(md5.toString());
            }
            
            
            
            new CASUALScriptParser().executeOneShotCommand("$ADB wait-for-device");
            //Launch script
            Thread t = new Thread(activateScript);
            //t.start();
            //do communications here
            try {
                t.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CASUALModularPack.class.getName()).log(Level.SEVERE, null, ex);
            }



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
    Runnable activateScript = new Runnable() {
        @Override
        public void run() {
            new CASUALScriptParser().executeSelectedScriptFile(activeScript, activeScript, false);
        }
    };


}
