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

import java.io.IOException;

/**
 *
 * @author adam
 */
public class HeimdallInstall {

    public boolean deployHeimdallForWindows() {
        FileOperations fo = new FileOperations();
        Statics.heimdallResource = Statics.heimdallWin2;
        fo.copyFromResourceToFile(Statics.heimdallResource, Statics.TempFolder + "libusb-1.0.dll");
        Statics.heimdallResource = Statics.heimdallWin;
        Statics.heimdallDeployed = Statics.TempFolder + "heimdall.exe";
        fo.copyFromResourceToFile(Statics.heimdallResource, Statics.heimdallDeployed);
        String x = new Shell().silentShellCommand(new String[]{Statics.heimdallDeployed, "version"}); //try without msredist
        if (!x.equals("")) {
            Statics.isHeimdallDeployed = true;
            return true;
        }
        fo.copyFromResourceToFile(Statics.msvcp110dll, Statics.TempFolder + "msvcp110.dll");
        fo.copyFromResourceToFile(Statics.msvcr110dll, Statics.TempFolder + "msvcr110.dll");
        if (!(new Shell().silentShellCommand(new String[]{Statics.heimdallDeployed, "version"}).equals(""))) { //try with redist files
            Statics.isHeimdallDeployed = true;
            return true;
        }
        new HeimdallInstall().installWindowsVCRedist();
        x = new Shell().silentShellCommand(new String[]{Statics.heimdallDeployed, "version"}); //deploy full
        if (x.contains("")) {
            return false;
        } else {
            return true;
        }
    }

    private boolean installLinuxHeimdall() {

        FileOperations fo = new FileOperations();
        Statics.checkLinuxArch();
//Linux64
        if (Statics.arch.contains("x86_64")) {
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
        } else if (Statics.arch.contains("i686")) {
            Statics.heimdallResource = Statics.heimdallLinuxi386;
            fo.copyFromResourceToFile(Statics.heimdallResource, Statics.heimdallStaging);
            fo.setExecutableBit(Statics.heimdallStaging);
            shell.elevateSimpleCommandWithMessage(new String[]{"dpkg", "-i", Statics.heimdallStaging}, "Install Heimdall");
            Statics.heimdallDeployed = "heimdall";
            if (Statics.checkAndDeployHeimdall()) {
                return true;
            } else {
                Statics.heimdallDeployed = "";
                return false;
            }
        } else {
            new Log().level0Error("This system has been detected as not compatible with Heimdall automatic-installer. If this is not correct, contact AdamOutler from XDA. You must compile and install heimdall from source to continue.  Heimdall's source is available from https://github.com/Benjamin-Dobell/Heimdall .  ");
            return false;
        }

//Windows
    }
    FileOperations FileOperations = new FileOperations();
    Log log = new Log();
    Shell shell = new Shell();

    public static boolean installHeimdall() {
        //if ( installedHeimdallVersion.length==2 && REGEX FOR STRING NUMBERS ONLY){ isHeimdallDeployed=true;
        if (Statics.isHeimdallDeployed) { //if heimdall is installed, return true
            return true;
        } else { //attempt to correct the issue
            if (Statics.isLinux()) {
                return new HeimdallInstall().installLinuxHeimdall();
            } else if (Statics.isWindows()) {
                if (new HeimdallInstall().checkHeimdallVersion()) {
                    return true;
                } else {
                    new HeimdallInstall().installWindowsVCRedist();
                    if (Statics.checkAndDeployHeimdall()) {
                        return true;
                    }
                    Statics.heimdallDeployed = "";
                    return false;
                }

                //Mac          
            } else if (Statics.isMac()) {

                Statics.heimdallDeployed = HeimdallTools.getHeimdallCommand();
                String retval = new Shell().silentShellCommand(new String[]{(Statics.heimdallDeployed)});
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
        if (Statics.isMac()) {
            String exec = "";
            try {
                exec = new CASUALUpdates().CASUALRepoDownload("https://android-casual.googlecode.com/svn/trunk/repo/heimdall.properties");
            } catch (IOException | InterruptedException ex) {
                log.errorHandler(ex);
            }
            new Shell().liveShellCommand(new String[]{"open", "-W", exec}, true);
            new CASUALInteraction().showErrorDialog("In order to continue, you must unplug the device and\n"
                    + "then it back in.  Use a GOOD port, in the back, not\n"
                    + "in the front.  Use a good cable too.", "Unplug it and then plug it back in");
        }
    }

    public void installWindowsVCRedist() {
        new Log().Level1Interaction("Installing Visual C++ redistributable package\n You will need to click next in order to install.");
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

    public void installWindowsDrivers() {
        //install drivers
        //CASUALJFrameWindowsDriverInstall HID = new CASUALJFrameWindowsDriverInstall();
        //HID.setVisible(true);
        
        log.level0Error("\nDrivers are Required Launching CADI.\nCASUAL Automated Driver Installer by jrloper.\nInstalling Drivers now"); //Add Newline
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
            log.level0Error("There was a problem while accessing the online repository.");
        }
        //verify MD5

        log.level2Information(new Shell().sendShellCommand(new String[]{"cmd.exe", "/C", "\"" +exec + "\"" }));

    }

    public void displayWindowsPermissionsMessageAndExit() {
        if (Statics.isWindows()) {
            new CASUALInteraction().showErrorDialog(""
                    + "Administrative permissions are required to continue.\n"
                    + "Please log in as a System Administrator  and rerun the command or use the console: \n"
                    + "runas /user:Administrator java -jar " + getClass().getProtectionDomain().getCodeSource().getLocation().getPath().toString(), //Display Message
                    "Permissions Error");
        }
        CASUALApp.shutdown(0);
    }

    void runWinHeimdallInstallationProcedure() {
        installWindowsVCRedist();
        installWindowsDrivers();
        new Log().level0Error("done.");

    }

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
