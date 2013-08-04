/*
 * Copyright (c) 2011 Adam Outler
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package CASUAL;

import CASUAL.network.CASUALUpdates;
import java.io.IOException;

/**
 *
 * @author adam
 */
public class HeimdallInstall {

    final String[] WindowsDriverBlanket = {"18D1", "04E8", "0B05", "0BB4", "22B8", "054C", "2080"};
    /**
     * Vendor ID detected
     */
    public String VID = "";
    /**
     * Device ID detected
     */
    public String PID = "";

    /**
     * deploys heimdal
     *
     * @return true if deployed
     */
    public boolean deployHeimdallForWindows() {
        FileOperations fo = new FileOperations();
        Statics.heimdallResource = Statics.heimdallWin2;
        fo.copyFromResourceToFile(Statics.heimdallResource, Statics.TempFolder + "libusb-1.0.dll");
        Statics.heimdallResource = Statics.heimdallWin;
        Statics.heimdallDeployed = Statics.TempFolder + "heimdall.exe";
        fo.copyFromResourceToFile(Statics.heimdallResource, Statics.heimdallDeployed);
        fo.copyFromResourceToFile(Statics.msvcp110dll, Statics.TempFolder + "msvcp110.dll");
        fo.copyFromResourceToFile(Statics.msvcr110dll, Statics.TempFolder + "msvcr110.dll");

        log.level4Debug("deployHeimdallForWindows- verifying Heimdall deployment");
        if (checkHeimdall()) { //try with redist files
            Statics.isHeimdallDeployed = true;
            log.level4Debug("heimdall install sucessful");
            return true;
        } else {
            log.level2Information("@additionalFilesAreRequired");
            new HeimdallInstall().installWindowsVCRedist();
        }

        log.level4Debug("Verifying Heimdall deployment after Visual C++ Redistributable installation");
        if (checkHeimdall()) {
            log.level0Error("@heimdallCouldNotBeDeployed");
            return false;
        } else {
            log.level4Debug("heimdall install sucessful");
            return true;
        }
    }

    /**
     * checks if heimdall is deployed
     *
     * @return true if heidmall version returns anything
     */
    public boolean checkHeimdall() {
        boolean retval = !new Shell().silentShellCommand(new String[]{HeimdallTools.getHeimdallCommand(), "version"}).equals("");
        return retval;
    }

    private boolean installLinuxHeimdall() {
        
        FileOperations fo = new FileOperations();
        String arch=OSTools.checkLinuxArch();
//Linux64
        if (arch.contains("x86_64")) {
            Statics.heimdallResource = Statics.heimdallLinuxamd64;
            fo.copyFromResourceToFile(Statics.heimdallResource, Statics.heimdallStaging);
            fo.setExecutableBit(Statics.heimdallStaging);
            shell.elevateSimpleCommandWithMessage(new String[]{"dpkg", "-i", Statics.heimdallStaging}, "Permissions escillation required to install Heimdall");
            Statics.heimdallDeployed = "heimdall";
            if (new HeimdallInstall().checkHeimdallVersion()) {
                return true;
            } else {
                Statics.heimdallDeployed = "";
                return false;
            }
//Linux32
        } else if (arch.contains("i686")) {
            Statics.heimdallResource = Statics.heimdallLinuxi386;
            fo.copyFromResourceToFile(Statics.heimdallResource, Statics.heimdallStaging);
            fo.setExecutableBit(Statics.heimdallStaging);
            shell.elevateSimpleCommandWithMessage(new String[]{"dpkg", "-i", Statics.heimdallStaging}, "Install Heimdall");
            Statics.heimdallDeployed = "heimdall";
            if (checkAndDeployHeimdall()) {
                return true;
            } else {
                Statics.heimdallDeployed = "";
                return false;
            }
        } else {
            new Log().level0Error("@incompatibleWithHeimdal");
            return false;
        }

//Windows
    }
    FileOperations FileOperations = new FileOperations();
    Log log = new Log();
    Shell shell = new Shell();

    /**
     * installs heimdall
     *
     * @return true if heimdall was detected
     */
    public boolean installHeimdall() {
        //if ( installedHeimdallVersion.length==2 && REGEX FOR STRING NUMBERS ONLY){ isHeimdallDeployed=true;
        if (Statics.isHeimdallDeployed) { //if heimdall is installed, return true
            return true;
        } else { //attempt to correct the issue
            if (OSTools.isLinux()) {
                return new HeimdallInstall().installLinuxHeimdall();
            } else if (OSTools.isWindows()) {
                if (new HeimdallInstall().checkHeimdallVersion()) {
                    return true;
                } else {
                    new HeimdallInstall().installWindowsVCRedist();
                    if (checkAndDeployHeimdall()) {
                        return true;
                    }
                    Statics.heimdallDeployed = "";
                    return false;
                }

                //Mac          
            } else if (OSTools.isMac()) {

                Statics.heimdallDeployed = HeimdallTools.getHeimdallCommand();
                String retval = new Shell().silentShellCommand(new String[]{HeimdallTools.getHeimdallCommand()});
                if (retval.contains("CritError!!!")) {
                    new HeimdallInstall().installHeimdallMac();
                }
                if (new HeimdallInstall().checkHeimdallVersion()) {
                    return true;
                } else {
                    Statics.heimdallDeployed = "";
                    return false;
                }
            }
        }
        return true;
    }

    private void installHeimdallMac() {
        if (OSTools.isMac()) {
            String exec = "";
            try {
                exec = new CASUALUpdates().CASUALRepoDownload("https://android-casual.googlecode.com/svn/trunk/repo/heimdall.properties");
            } catch (IOException | InterruptedException ex) {
                log.errorHandler(ex);
            }
            new Shell().liveShellCommand(new String[]{"open", "-W", exec}, true);
            new CASUALMessageObject("@interactionUnplugItAndPlugItBackIn").showErrorDialog();
        }
    }

    /**
     * @deprecated installs Windows Visual C++ redistributable
     */
    public void installWindowsVCRedist() {
        new Log().level2Information("@installingVisualCPP");
        String installVCResults = "CritERROR!!!";
        String exec = "";
        try {
            exec = new CASUALUpdates().CASUALRepoDownload("https://android-casual.googlecode.com/svn/trunk/repo/vcredist.properties");
        } catch (IOException | InterruptedException ex) {
            log.errorHandler(ex);
        }
        new Shell().liveShellCommand(new String[]{exec}, true);
        if (installVCResults.contains("CritERROR!!!")) {
            displayWindowsPermissionsMessageAndExit();
        }
    }

    /**
     * Installs windows drivers
     *
     * @return always returns true
     * @WTF always returns true?
     */
    public boolean installWindowsDrivers() {
        //install drivers
        //CASUALJFrameWindowsDriverInstall HID = new CASUALJFrameWindowsDriverInstall();
        //HID.setVisible(true);
        /*
         * @WTF
         */
        new WindowsDrivers(0).installDriverBlanket(null);
        return true;
        /*log.level2Information("@installingCADI"); //Add Newline
         new Log().level3Verbose("Driver Problems suck. Lemme make it easy.\n"
         + "We're going to install drivers now.  Lets do it.\n"
         + "THIS PROCESS CAN TAKE UP TO 5 MINTUES.\nDURING THIS TIME YOU WILL NOT SEE ANYTHING.\nBE PATIENT!");

         String exec = "";
         try {
         if (new FileOperations().verifyResource(Statics.WinDriverResource)) {
         exec = Statics.TempFolder + "CADI.exe";
         new FileOperations().copyFromResourceToFile(Statics.WinDriverResource, exec);
         } else {
         exec = new CASUALUpdates().CASUALRepoDownload("https://android-casual.googlecode.com/svn/trunk/repo/driver.properties");
         }
         } catch (IOException | InterruptedException ex) {
         log.level0Error("@problemWithOnlineRepo");
         }
         //verify MD5
         String driverreturn = new Shell().sendShellCommand(new String[]{"cmd.exe", "/C", "\"" + exec + "\""});*/
        /*
         * 
         * TODO: Here we need to parse return from CADI
         * 
         * Access is denied is likely a non-priviliged account otherwise access would be granted.
         * 
         * 
         * UNSUPPORTED DEVICE for Galaxy S1  Loops forever   Why is GS1 not supported?
         [DEBUG]deployHeimdallForWindows- verifying Heimdall deployment
         [DEBUG]heimdall install sucessful
         Waiting for Downoad Mode device...[VERBOSE]detected!
         [INFO]Executing Heimdall command.
         [VERBOSE]Performing standard Heimdall commandclose-pc-screen
         [DEBUG]###executing real-time command: C:\Users\adam\AppData\Local\Temp\adamTEMPCASUAL6EDFD949\heimdall.exe###
         Heimdall v1.4.0

         Copyright (c) 2010-2013, Benjamin Dobell, Glass Echidna
         http://www.glassechidna.com.au/

         This software is provided free of charge. Copying and redistribution is
         encouraged.

         If you appreciate this software and you would like to support future
         development please consider donating:
         http://www.glassechidna.com.au/donate/

         Initialising connection...
         Detecting device...
         ERROR: Failed to access device. libusb error: -12
         [ERROR]
         Drivers are Required Launching CADI.
         CASUAL Automated Driver Installer by jrloper.
         Installing Drivers now
         [VERBOSE]Driver Problems suck. Lemme make it easy.
         We're going to install drivers now.  Lets do it.
         THIS PROCESS CAN TAKE UP TO 5 MINTUES.
         DURING THIS TIME YOU WILL NOT SEE ANYTHING.
         BE PATIENT!
         [DEBUG]Attempting to write C:\Users\adam\AppData\Local\Temp\adamTEMPCASUAL6EDFD949\CADI.exe
         [DEBUG]File verified.
         [DEBUG]###executing: cmd.exe###
         [INFO]

         [INFO]
         [Heimdall Error Report] Detected:
         'LIBUSB_ERROR_NOT_SUPPORTED'; Attempting to continue
         [/Heimdall Error Report]


         [VERBOSE]Performing standard Heimdall commandclose-pc-screen
         [DEBUG]###executing real-time command: C:\Users\adam\AppData\Local\Temp\adamTEMPCASUAL6EDFD949\heimdall.exe###
         Heimdall v1.4.0
         ....
         * ...
         * ...

         [ERROR]Maximum retries exceeded. Shutting down Parser.
         [DEBUG]HALT RECEIVED    * 
         * 
         */
        /*log.level2Information(driverreturn);
         if (driverreturn.contains("CritError")) {
         return false;
         } else {
         return true;
         }*/
    }

    /**
     * displays a message to the user that Windows permissions were not
     * obtainable
     */
    public void displayWindowsPermissionsMessageAndExit() {
        if (OSTools.isWindows()) {
            new CASUALMessageObject("@interactionwindowsRunAsMessage" + getClass().getProtectionDomain().getCodeSource().getLocation().getPath().toString()).showErrorDialog();
        }
        CASUALApp.shutdown(0);
    }

    void runWinHeimdallInstallationProcedure() {
        installWindowsVCRedist();
        installWindowsDrivers();
    }

    /**
     * checks and deploys heimdall
     *
     * @return true if deployed
     */
    public boolean checkAndDeployHeimdall() {

        //deploys heimdall for Windows, launches checks for all other OS's. 

        if (Statics.isHeimdallDeployed) {
            return true;
        } else {
            if (OSTools.isWindows()) {
                return new HeimdallInstall().deployHeimdallForWindows();
            } else {

                if (new HeimdallInstall().checkHeimdallVersion()) {
                    return true;
                } else { //shell returned error
                    if (installHeimdall()) {
                        return true;
                    }
                    return false;
                }

            }
        }
    }

    /**
     * checks the heimdall version against version expected from Statics
     *
     * @return true if version is good
     */
    public boolean checkHeimdallVersion() {
        String heimdallCommand;
        if (Statics.heimdallDeployed.equals("")) {
            heimdallCommand = HeimdallTools.getHeimdallCommand();
        } else {
            heimdallCommand = Statics.heimdallDeployed;
        }
        String[] command = {heimdallCommand, "version"};
        String Version = new Shell().silentShellCommand(command);
        if (!Version.contains("CritError!!!")) {
            Version = Version.replaceAll("\n", "").replaceAll("v", "");
            if (Version.contains(" ")) {
                Version = Version.split(" ")[0];
            }
            Version = Version.replaceAll("\\.", "");
            if (Version.length() == 2) {
                Version = Version + 0;
            }
        } else {
            return false;
        }
        char[] digits = Version.toCharArray();
        int commandLineVersion = Integer.parseInt(new String(digits));
        int resourceVersion = Integer.parseInt(Statics.heimdallVersion);

        if (commandLineVersion >= resourceVersion) {
            Statics.heimdallDeployed = heimdallCommand;
            Statics.isHeimdallDeployed = true;
            return true;
        } else {
            return false;
        }
    }
}
