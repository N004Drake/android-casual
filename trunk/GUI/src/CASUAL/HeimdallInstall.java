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

import javax.swing.Timer;



/**
 *
 * @author adam
 */
public class HeimdallInstall {

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

    public static boolean checkAndDeployHeimdall() {
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
                Statics.heimdallResource = Statics.heimdallMac;
                new HeimdallInstall().installHeimdallMac();
                Statics.heimdallDeployed = "heimdall";
                if (Statics.checkAndDeployHeimdall()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private void installHeimdallMac() {
        if (Statics.isMac()) {
            CASUALUpdates update = new CASUALUpdates();
            String installScript = Statics.TempFolder + "installer.sh";
            update.downloadFileFromInternet(Statics.heimdallMacURL, installScript, "Downloading Heimdall Installation file");
            new FileOperations().setExecutableBit(installScript);

            String title="Exiting CASUAL";
            String message="CASUAL will now launch Heimdall Installer.\n  Hit cancel if asked to set up any network interfaces.\n"
                    + "You must install Heimdall in order to continue";
            new CASUALInteraction().showErrorDialog(message, title);
            shell.elevateSimpleCommand(new String[]{installScript});
            
            
            new CASUALInteraction().showErrorDialog("In order to continue, you must unplug the device and\n"
                    + "then it back in.  Use a GOOD port, in the back, not\n"
                    + "in the front.  Use a good cable too.", "Unplug it and then plug it back in");


        }
    }

    public void installWindowsVCRedist() {
        //download 
        CASUALUpdates updater = new CASUALUpdates();
        new Log().Level1Interaction("Installing Visual C++ redistributable package\n You will need to click next in order to install.");
        String installVCResults = "CritERROR!!!";
        
        
        String exec="";
        try {
            exec = new CASUALUpdates().CASUALRepoDownload("https://android-casual.googlecode.com/svn/trunk/repo/vcredist.properties");
        } catch( Exception ex ){
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
        log.level0Error("Installing drivers");
        new Log().level0Error("Driver Problems suck. Lemme make it easy.\n"
                + "1. Check that your device is download mode and connected up.\n"
                + "2. Select the one that says ---Gadget Serial--- in the main window\n"
                + "3. Click ---install driver---.\n"
                + "4. Close out zadig and use CASUAL."
                + "Note: the USB port which you install this driver will be converted\n"
                + "to use Heimdall instead of Odin for download mode.  It only affects\n"
                + "ONE usb port.");
       
              
        //TODO: verify if driver is in the resources at /CASUAL/resources/heimdall/ before downloading else deploy and execute
        String exec="";
        try {
            exec = new CASUALUpdates().CASUALRepoDownload("https://android-casual.googlecode.com/svn/trunk/repo/driver.properties");
        } catch( Exception ex ){
            
        }
        //verify MD5

        log.level2Information(new Shell().sendShellCommand(new String[]{"cmd.exe","/C",exec}));
        

    }

    public void displayWindowsPermissionsMessageAndExit() {
        if (Statics.isWindows()) {
            new CASUALInteraction().showErrorDialog(""
                    + "Administrative permissions are required to continue.\n"
                    + "Please log in as a System Administrator  and rerun the command or use the console: \n"
                    + "runas /user:Administrator java -jar " + getClass().getProtectionDomain().getCodeSource().getLocation().getPath().toString(), //Display Message
                    "Permissions Error");
            //WindowsProblem.start();
        }
        System.exit(0);
    }

    void runWinHeimdallInstallationProcedure() {
        installWindowsVCRedist();
        installWindowsDrivers();
        new Log().level0Error("done.");
        
    }

    public boolean checkHeimdallVersion() {
        String heimdallCommand;
        if (Statics.heimdallDeployed.equals("")) {
            heimdallCommand = "heimdall";
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
