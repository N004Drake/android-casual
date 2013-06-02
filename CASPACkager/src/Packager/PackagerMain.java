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
import static Packager.PackagerMain.userOutputDir;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

/**
 * *************************************************************************
 * PackagerMain
 *
 * @author Jeremy Loper jrloper@gmail.com
 * ************************************************************************
 */
public class PackagerMain {

    public PackagerMain() {
        //NADA
    }
    private static boolean useOverrideArgs = false;
    private static String[] overrideArgs = {"--fullauto" ,"/home/adam/code/android-casual/trunk/CASPAC/", "--type" ,"nightly"};
    protected static String userOutputDir = "";
    final private static String defaultOutputDir = Statics.CASUALHome + "PACKAGES" + Statics.Slash;
    private static String caspacWithPath = "";
    private static String caspacNoPath = "";
    private static Log log = new Log();
    static String appendToName = "";
    static String processFolder = "";
    private static FileOperations fileOperations = new FileOperations();
    static boolean hasProcessedFolder=false;
    ;
    
    public static void main(String[] args) {
        if (useOverrideArgs) {
            args = overrideArgs;
        }
        processCommandline(args);
        if (args.length > 0 && args[0].length() != 0) {
            if (doCASPACWork()) {
                if (doCASUALWork()) {
                    if (doCASPACCASUALMerge()) {
                        doCleanUp();
                    }
                }
            }
        }
    }

    /**
     * *************************************************************************
     * processCommandline
     *
     * @param args String array of arguments passed at runtime
     * ************************************************************************
     */
    private static void processCommandline(String[] args) {


        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("--type") || args[i].contains("-t")) {
                appendToName = "-" + args[++i];
            } else if (args[i].contains("--CASPAC") || args[i].contains("-c")) {
                caspacWithPath = args[++i];
            } else if (args[i].contains("--output") || args[i].contains("-o")) {
                userOutputDir = StringOperations.removeLeadingAndTrailingSpaces(args[++i]);
            } else if (args[i].contains("--fullauto") || args[i].contains("-f")) {
                processFolder = StringOperations.removeLeadingAndTrailingSpaces(args[++i]);
            }
        }


        //exit if nothing to process        
        if (caspacWithPath.equals("") && processFolder.equals("")) {
            log.level0Error("You failed to specify any processing items.");
            showMessageAndExit();
        }
        
        //if we are using userOutputDir
        if (!userOutputDir.equals("")) {
            // verify there is a slash at the end of userOutputDir
            if (!userOutputDir.endsWith(Statics.Slash)) {
                userOutputDir = userOutputDir + Statics.Slash;
            }
            // set output dir to the same as the file    
        } 
        
         //if we are using processFolder   
        if (!processFolder.equals("") && !hasProcessedFolder) {
            hasProcessedFolder=true;
            useOverrideArgs = false; //turn off override in case of IDE mode
            if (!processFolder.endsWith(Statics.Slash)) {
                processFolder = processFolder + Statics.Slash;
            }
            //list the files in the folder
            String[] filesToProcess = fileOperations.listFolderFilesCannonically(processFolder);
            
            //parse each file and set up args
            for (String file : filesToProcess) {
                ArrayList argslist=new ArrayList();
                
                //set up append list
                if (!appendToName.equals("")){
                    argslist.add("--type");
                    argslist.add(appendToName);
                }
                
                //do checks to verify its a good file
                if ((file != null) && (!new File(file).isDirectory()) && (!file.endsWith(Statics.Slash))) {
                    argslist.add("--CASPAC");
                    argslist.add(file);
                    
                    //execute the packager
                    PackagerMain.main(StringOperations.convertArrayListToStringArray(argslist));
                }
            }
            log.level2Information("done");
            System.exit(0);
        }
        if (userOutputDir.equals("")){
            try {
                userOutputDir = caspacWithPath;
                userOutputDir = new File(userOutputDir).getCanonicalPath().toString();
                userOutputDir = userOutputDir.substring(0, userOutputDir.lastIndexOf(Statics.Slash)) + Statics.Slash + "CASUAL" + Statics.Slash;
                if (!fileOperations.verifyExists(userOutputDir)) {
                    fileOperations.makeFolder(userOutputDir);
                }
                log.level2Information("Set output folder to default: " + userOutputDir);
            } catch (IOException ex) {
                Logger.getLogger(PackagerMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //if we are using caspacWithPath
        if (!caspacWithPath.equals("")) {
            //verify it is a known extension and extract the name
            if (caspacWithPath.endsWith(".zip") || caspacWithPath.endsWith(".CASPAC")) {
                //log.level4Debug("[processCommandline()]File type is known based on extension");
                caspacNoPath = caspacNoPath = new File(caspacWithPath).getName();
                caspacNoPath = caspacNoPath.substring(0, caspacNoPath.lastIndexOf("."));
            } else {
                log.level0Error("[processCommandline()]File type is unknown based on extention");
                showMessageAndExit();
            }
        }

       

    }

    private static void showMessageAndExit() {
        letCASUALLog(true);
        log.level2Information(
                "CASUAL's CASPAC deployment PACKAGER              ");
        log.level2Information(
                "                                                 ");
        log.level2Information(
                "java -jar PACKAGER.jar [OPTIONS]                 ");
        log.level2Information(
                "                                                 ");
        log.level2Information(
                "--fullauto  /path_to/CASPACs                     ");
        log.level2Information(
                "java -jar --fullauto /home/caspacs/              ");
        log.level2Information(
                "  - or -                                         ");
        log.level2Information(
                "java -jar --CASPAC /home/caspacs/myCASPAC.zip    ");
        log.level2Information(
                "                                                 ");
        log.level2Information(
                "--output /directory/ (optional)                  ");
        log.level2Information(
                "default (user.home)/.CASUAL/PACKAGES             ");
        log.level2Information(
                "--type  \"stringToAppendToFileName\"  (optional) ");
        log.level2Information(
                "default nothing                                  ");
        System.exit(1);
    }

    /**
     * *************************************************************************
     * doCASPACWork
     *
     * @return Success = True, Failure = False
     * ************************************************************************
     */
    private static boolean doCASPACWork() {
        if (!fileOperations.makeFolder(Statics.TempFolder + "CASPAC")) {
            return false;
        }
        //log.level4Debug("[doCASUALWork()]Created folder " + Statics.TempFolder + "CASPAC");

        try {
            Unzip.unzipFile(caspacWithPath, Statics.TempFolder + "CASPAC");
        } catch (ZipException ex) {
            letCASUALLog(true);
            log.errorHandler(ex);
            return false;
        } catch (IOException ex) {
            letCASUALLog(true);
            log.errorHandler(ex);
            return false;
        }

        //log.level4Debug("[doCASPACWork()]CASPAC unzipped to " + Statics.TempFolder + "CASPAC");
        return true;
    }

    /**
     * *************************************************************************
     * doCASUALWork
     *
     * @return Success = True, Failure = False
     * ************************************************************************
     */
    private static boolean doCASUALWork() {
        if (!fileOperations.makeFolder(Statics.TempFolder + "CASUAL")) {
            return false;
        }
        //log.level4Debug("[doCASUALWork()]Created folder " + Statics.TempFolder + "CASUAL");
        fileOperations.copyFromResourceToFile("/Packager/resources/CASUAL.jar", Statics.TempFolder + "CASUAL.zip");

        try {
            letCASUALLog(false);
            Unzip.unzipFile(Statics.TempFolder + "CASUAL.zip", Statics.TempFolder + "CASUAL" + Statics.Slash);
            letCASUALLog(true);
        } catch (FileNotFoundException ex) {
            letCASUALLog(true);
            log.errorHandler(ex);
            return false;
        } catch (IOException ex) {
            letCASUALLog(true);
            log.errorHandler(ex);
            return false;
        }



        String folderToDelete = Statics.TempFolder + "CASUAL" + Statics.Slash + "SCRIPTS" + Statics.Slash;
        //log.level4Debug("[doCASUALWork()]Unzipping CASUAL.jar from PACKAGER.jar's resources");
        //log.level4Debug("[doCASUALWork()]Getting /SCRIPTS folder contents list");

        if (fileOperations.deleteStringArrayOfFiles(fileOperations.listFolderFilesCannonically(folderToDelete))) {
            fileOperations.makeFolder(Statics.TempFolder + "CASUAL" + Statics.Slash + "SCRIPTS" + Statics.Slash);
            return true;
        }

        //log.level4Debug("[doCASUALWork()]Folder is empty");
        //log.level4Debug("[doCASUALWork()]Operation Complete");
        return false;
    }

    /**
     * *************************************************************************
     * doCASPACCASUALMerge merges the extracted CASPAC and extracted CASUAL.jar
     * into a new caspac-CASUAL.jar
     *
     * @return Success = True, Failure = False
     * ************************************************************************
     */
    private static boolean doCASPACCASUALMerge() {
        String[] files = fileOperations.listFolderFiles(Statics.TempFolder + "CASPAC" + Statics.Slash);
        int x = 0;

        if (files[x] == null) {
            log.level0Error("[doCASPACCASUALMerge()]CASPAC contained no files.");
            return false;
        }

        try {
            letCASUALLog(false);
            while (files[x] != null) {
                fileOperations.moveFile(Statics.TempFolder + "CASPAC" + Statics.Slash + files[x], Statics.TempFolder + "CASUAL" + Statics.Slash + "SCRIPTS" + Statics.Slash + files[x]);
                x++;
            }
            letCASUALLog(true);
            log.level4Debug("[doCASPACCASUALMerge()]File bases merged");
            new Zip().addFilesToNewZip(Statics.TempFolder + caspacNoPath + "-CASUAL.jar", Statics.TempFolder + "CASUAL" + Statics.Slash);
            fileOperations.makeFolder(defaultOutputDir);
            String output = caspacNoPath + "-CASUAL-R" + CASUAL.CASPACData.getSVNRevision() + "b" + appendToName + ".jar";

            if (userOutputDir.equals("")) {
                output = defaultOutputDir + output;
                fileOperations.moveFile(Statics.TempFolder + caspacNoPath + "-CASUAL.jar", defaultOutputDir);
            } else {
                output = userOutputDir + output;
                fileOperations.moveFile(Statics.TempFolder + caspacNoPath + "-CASUAL.jar", output);
            }
            fileOperations.setExecutableBit(output);
            log.Level1Interaction("CREATED NEW FILE");
            log.Level1Interaction(output);

        } catch (Exception ex) {
            log.errorHandler(ex);
            return false;
        }
        //log.level4Debug("[doCASPACCASUALMerge()] " + caspacNoPath + "-CASUAL.jar created");

        return true;
    }

    /**
     * *************************************************************************
     * doCleanUp
     * ************************************************************************
     */
    private static void doCleanUp() {
        letCASUALLog(false);
        fileOperations.recursiveDelete(Statics.TempFolder + "CASUAL");
        fileOperations.recursiveDelete(Statics.TempFolder + "CASPAC");
        letCASUALLog(true);
    }
    static PrintStream x=CASUAL.Log.out;
    private static void letCASUALLog(boolean log){
        if (log){
            CASUAL.Log.out=x;
        } else {
            String x=Statics.TempFolder+"shutup";
            try {
                CASUAL.Log.out=new PrintStream(new File(x));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PackagerMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
