/*CASUALTools is a miscellanious helper class
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
 */
package CASUAL;

import CASUAL.CommunicationsTools.ADB.ADBTools;
import CASUAL.misc.LinkedProperties;
import CASUAL.caspac.Caspac;
import CASUAL.crypto.MD5sum;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a set of tools used in CASUAL 
 * @author Adam Outler
 */
public class CASUALTools {
    //final public String defaultPackage="ATT GS3 Root";

    /**
     * true if this is running on the flat filesystem. False if in a jar.
     */
    final public static boolean IDEMode = new CASUALTools().getIDEMode();

    Log log = new Log();

    /**
     * listScripts scans the CASUAL for scripts -results are stored in
     * Statics.scriptNames -In IDE mode an MD5 refresh is triggered
     *
     * @throws IOException
     */
    /*
     public void listScripts() throws IOException {
     CodeSource Src = CASUAL.CASUALApp.class.getProtectionDomain().getCodeSource();
     int Count = 0;
     ArrayList<String> list = new ArrayList<>();
     if (Src != null) {
     URL jar = Src.getLocation();
     try (ZipInputStream Zip = new ZipInputStream(jar.openStream())) {
     ZipEntry ZEntry;
     log.level4Debug("Picking Jar File:" + jar.getFile());
     while ((ZEntry = Zip.getNextEntry()) != null) {

     String EntryName = ZEntry.getName();
     if (EntryName.endsWith(".scr")) {

     list.add(EntryName);
     }
     }
     log.level4Debug("Found " + list.size() + " CASUAL scripts");
     Statics.scriptNames = new String[list.size()];
     for (int n = 0; n < list.size(); n++) {
     String EntryName = ((String) list.get(n)).replaceFirst("SCRIPTS/", "").replace(".scr", "");
     log.level4Debug("Found script: " + EntryName);
     Statics.scriptNames[n] = EntryName;
     Count++;
     }

     if (Count == 0) {
     Thread update = new Thread(updateMD5s);
     update.setName("Updating MD5s");
     update.start();
     log.level3Verbose("IDE Mode: Using " + CASUALApp.defaultPackage + ".scr ONLY!");
     //Statics.scriptLocations = new String[]{""};
     Statics.scriptNames = new String[]{CASUALApp.defaultPackage};
     }
     }
     }
     }*/
    /**
     * md5sumTestScript Refreshes the MD5s on the scripts in the /SCRIPTS folder
     */
    private void md5sumTestScripts() {
        Statics.setStatus("Setting MD5s");
        log.level4Debug("\nIDE Mode: Scanning and updating MD5s.\nWe are in " + System.getProperty("user.dir"));
        incrementBuildNumber();

        if (getIDEMode()) { //if we are in development mode
            //Set up scripts path
            String scriptsPath = System.getProperty("user.dir") + Statics.Slash + "SCRIPTS" + Statics.Slash;
            final File folder = new File(scriptsPath);
            if (folder.isDirectory()) {
                for (final File fileEntry : folder.listFiles()) {
                    if (fileEntry.toString().endsWith(".meta")) {
                        InputStream in = null;
                        try {
                            //load each meta file into a properties file
                            new Log().level3Verbose("Verifying meta: " + fileEntry.toString());
                            LinkedProperties prop = new LinkedProperties();
                            in = new FileInputStream(fileEntry);
                            prop.load(in);
                            in.close();
                            //Identify and store the new MD5s
                            String md5;
                            int pos = 0;
                            boolean md5Changed = false;
                            while ((md5 = prop.getProperty("Script.MD5[" + pos + "]")) != null) {
                                String entry = "Script.MD5[" + pos + "]";
                                String[] md5File = md5.split("  ");
                                String newMD5 = new MD5sum().md5sum(scriptsPath + md5File[1]);
                                if (!md5.contains(newMD5)) {
                                    md5Changed = true;
                                    log.level4Debug("Old MD5: " + md5);
                                    log.level4Debug("New MD5: " + prop.getProperty(entry));
                                }
                                prop.setProperty(entry, newMD5 + "  " + md5File[1]);
                                pos++;
                            }

                            if (md5Changed) {
                                new Log().level4Debug("MD5s for " + fileEntry + " changed. Updating...");
                                FileOutputStream fos = new FileOutputStream(fileEntry);
                                prop.store(fos, null);
                                fos.close();

                            }
                        } catch (FileNotFoundException ex) {
                            new Log().errorHandler(ex);
                        } catch (IOException ex) {
                            new Log().errorHandler(ex);
                        } finally {
                            try {
                                if (in != null) {
                                    in.close();
                                }
                            } catch (IOException ex) {
                                new Log().errorHandler(ex);
                            }
                        }
                    }
                }
            }
        }
    }
    //CASUALZipPrep
    /**
     * thread used for preparing zip file. this should never be interrupted.
     */
    public static Thread zipPrep;

    /**
     * prepares the script for execution by setting up environment
     *
     * @param scriptName
     */
    /**
     * tells if CASUAL is running in Development or Execution mode
     *
     * @return true if in IDE mode
     */
    private boolean getIDEMode() {
        String className = getClass().getName().replace('.', '/');
        String classJar = getClass().getResource("/" + className + ".class").toString();
        String path = new File(".").getAbsolutePath();
        boolean isSource = path.contains("src") && path.contains("CASUALcore");
        return classJar.startsWith("file:") && isSource;
    }
    /**
     * Starts a new ADB instance
     */
    public Runnable launchADB = new Runnable() {
        @Override
        public void run() {
            new ADBTools().startServer();
        }
    };
    /**
     * deploys ADB to Statics.ADBDeployed.
     */
    public Runnable adbDeployment = new Runnable() {
        @Override
        public void run() {
            new ADBTools().getBinaryLocation();
            new Log().level3Verbose("ADB Server Started!!!");
        }
    };
    /**
     * Starts the GUI, should be done last and only if needed.
     */
    public Runnable GUI = new Runnable() {
        @Override
        public void run() {
            try {
                setGUIAPI();
                Statics.GUI.setVisible(true);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    /**
     * provides a runnable object for updating MD5s
     */
    public static Runnable updateMD5s = new Runnable() {
        @Override
        public void run() {
            new CASUALTools().md5sumTestScripts();
        }
    };

    //This is only used in IDE mode for development
    /**
     * rewrites MD5s in the provided CASPAC. note: This is only used in IDE mode
     * for development
     *
     * @param CASPAC file to be checked and have MD5s rewritten.
     */
    public static void rewriteMD5OnCASPAC(File CASPAC) {

        Caspac caspac;
        try {
            caspac = new Caspac(CASPAC, Statics.getTempFolder(), 0);
            caspac.load();
            caspac.write();
            System.exit(0);

        } catch (IOException ex) {
            new Log().errorHandler(ex);
        }
    }

    //This is only used in IDE mode for development
    private void incrementBuildNumber() throws NumberFormatException {
        Properties prop = new Properties();
        try {
            if (new File(System.getProperty("user.dir") + "/CASUAL/resources/CASUALApp.properties").exists()) {
                prop.load(new FileInputStream(System.getProperty("user.dir") + "/CASUAL/resources/CASUALApp.properties"));
                int x = Integer.parseInt(prop.getProperty("Application.buildnumber").replace(",", ""));
                x++;
                prop.setProperty("Application.buildnumber", Integer.toString(x));
                prop.setProperty("Application.buildnumber", Integer.toString(x));

                prop.store(new FileOutputStream(System.getProperty("user.dir") + "/CASUAL/resources/CASUALApp.properties"), "Application.buildnumber=" + x);
            }
        } catch (IOException ex) {
            new Log().errorHandler(ex);
        }
    }

    /**
     * sleeps for 1000ms.
     */
    public static void sleepForOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            new Log().errorHandler(ex);
        }
    }

    /**
     * sleeps for 100ms.
     */
    public static void sleepForOneTenthOfASecond() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            new Log().errorHandler(ex);
        }
    }

    /**
     * gets the stored Subversion revision from the last build
     *
     * @return string representation of subversion revision
     */
    public static int getSVNVersion() {
        return Integer.parseInt(java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision"));
    }

  

    private static void setiCASUALinteraction(Class<?> cls) throws InstantiationException, IllegalAccessException {
        iCASUALUI clsInstance;
        if (!java.awt.GraphicsEnvironment.isHeadless()) {
            clsInstance = (CASUAL.iCASUALUI) cls.newInstance();
            CASUAL.Statics.GUI = clsInstance;
        }

    }

    /**
     * sets the GUI API based on property in CASUAL/resources/CASUALApp.
     * The GUI API can be specified by modification of Application.GUI. The API 
 only requires that you specify a class which implements the 
 iCASUALUI class.
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public static void setGUIAPI() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String messageAPI = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.GUI");
        try {
            Class<?> cls = Class.forName(messageAPI);
            setiCASUALGUI(cls);
        } catch (ClassNotFoundException ex) {
            Class<?> cls = Class.forName("GUI.development.CASUALGUIMain");
            setiCASUALGUI(cls);
        } catch (InstantiationException ex) {
            Class<?> cls = Class.forName("GUI.development.CASUALGUIMain");
            setiCASUALGUI(cls);
        } catch (IllegalAccessException ex) {
            Class<?> cls = Class.forName("GUI.development.CASUALGUIMain");
            setiCASUALGUI(cls);
        }
    }

    private static void setiCASUALGUI(Class<?> cls) throws InstantiationException, IllegalAccessException {
        iCASUALUI clsInstance;
        clsInstance = (CASUAL.iCASUALUI) cls.newInstance();
        CASUAL.Statics.GUI = clsInstance;
    }

    /**
     * compares User ID from id -u on the device to the specified User ID.
     * @param expectedUID  User ID specified.
     * @return True if actua UID matches expected
     */
    public static boolean uidMatches(String expectedUID) {
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "shell", "id -u"};
        String retval = new Shell().silentShellCommand(cmd);
        return retval.contains(expectedUID);
    }

    /**
     * Checks the device to get the command required for root access.  This
     * accounts for both adb root and rooted devices.  
     * @return command used to get root, will be blank if unrooted. 
     */
    public static String rootAccessCommand() {
        if (uidMatches("uid=0(")) {
            return "";
        }
        if (uidMatches("2000")) {
            String retval = new Shell().silentShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "su -c 'id -u'"});
            if (retval.contains("uid=0(")) {
                return "su -c ";
            } else {
                return "";
            }
        } else {
            new CASUALMessageObject("@couldNotObtainRootOnDevice").showErrorDialog();
            return "";
        }
    }

}
