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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;

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
            new Log().level0("This system has been detected as not compatible with Heimdall automatic-installer. If this is not correct, contact AdamOutler from XDA. You must compile and install heimdall from source to continue.  Heimdall's source is available from https://github.com/Benjamin-Dobell/Heimdall .  ");
            return false;
        }

//Windows
    }
    FileOperations FileOperations = new FileOperations();
    Log log = new Log();
    Shell shell = new Shell();

    public static boolean checkAndDeployHeimdall() {
        //if ( installedHeimdallVersion.length==2 && REGEX FOR STRING NUMBERS ONLY){ isHeimdallDeployed=true;

        if ((Statics.isHeimdallDeployed)) { //if heimdall is installed, return true

            return true;
        } else { //attempt to correct the issue

            if (Statics.isLinux()) {

                return new HeimdallInstall().installLinuxHeimdall();
            } else if (Statics.isWindows()) {
                //TODO check if drivers and runtime is installed
                //otherwise download and Shell().elevate

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
            Statics.heimdallStaging = Statics.TempFolder + "Heimdall.dmg";
            new CASUALUpdates().downloadFileFromInternet(Statics.heimdallMacURL, Statics.heimdallStaging, "Downloading Heimdall");
            String[] mount = {"hdiutil", "mount", Statics.heimdallStaging};
            String[] lineSplit = shell.silentShellCommand(mount).split("\n");
            String folder = "";
            for (String lines : lineSplit) {
                String[] line = lines.split("	");
                for (String item : line) {
                    if (item.contains("eimdall")) {
                        folder = item;
                        log.progress("Mounted " + folder);
                    }
                }
            }
            String[] getFolderContents = {"ls", "-1", folder};


            //TODO This is inoperative on mac.  I don't know why but it should work
            String[] folderContents = shell.silentShellCommand(getFolderContents).split("\\n");
            String file = "";
            for (String item : folderContents) {
                if (item.contains("mpkg")) {
                    file = item;
                    JOptionPane.showMessageDialog(null, "Heimdall One-Click will now launch Heimdall Installer\n"
                            + "You must install Heimdall in order to continue", "Exiting Heimdall One-Click", JOptionPane.ERROR_MESSAGE);
                }
            }

            String[] openMpkg = {"open", folder + "/" + file};
            String x = shell.sendShellCommand(openMpkg);
            System.err.println(x);
            System.exit(0);

        }
    }

    public void installWindowsVCRedist() {
        //download 
        CASUALUpdates updater = new CASUALUpdates();
        new Log().level0("Installing Visual C++ redistributable package\n You will need to click next in order to install.");
        String installVCResults = "CritERROR!!!";
        try {
                updater.downloadFileFromInternet(updater.stringToFormattedURL(Statics.WinVCRedis32tInRepo), Statics.TempFolder + "vcredist_32.exe", "Visual Studio Redistributable");
                new MD5sum().compareMD5StringsFromLinuxFormatToFilenames(new String[]{Statics. WinVCRedis32tInRepo}, new String[]{Statics.TempFolder + "vcredist_32.exe"});
                installVCResults = shell.elevateSimpleCommand(new String[]{Statics.TempFolder + "vcredist_32.exe"});
            //Will need upating in the future
            //This downloads, MD5's and Installs Visual C++ for Win32/64
            /*if (Statics.isWindows64Arch()){
                updater.downloadFileFromInternet(updater.stringToFormattedURL(Statics.WinVCRedis64tInRepo), Statics.TempFolder + "vcredist_x64.exe", "Visual Studio Redistributable");
                new MD5sum().compareMD5StringsFromLinuxFormatToFilenames(new String[]{Statics. WinVCRedis32tInRepoMD5}, new String[]{Statics.TempFolder + "vcredist_x86.exe"});
                installVCResults = shell.elevateSimpleCommand(new String[]{Statics.TempFolder + "vcredist_x86.exe"});
            } else{
                updater.downloadFileFromInternet(updater.stringToFormattedURL(Statics.WinVCRedis32tInRepo), Statics.TempFolder + "vcredist_x86.exe", "Visual Studio Redistributable");
                new MD5sum().compareMD5StringsFromLinuxFormatToFilenames(new String[]{Statics. WinVCRedis64tInRepoMD5}, new String[]{Statics.TempFolder + "vcredist_x86.exe"});
                installVCResults = shell.elevateSimpleCommand(new String[]{Statics.TempFolder + "vcredist_x64.exe"});
             }*/        
        } catch (MalformedURLException | URISyntaxException ex) {
            log.errorHandler(ex);
        }
        if (installVCResults.contains("CritERROR!!!")) {
            displayWindowsPermissionsMessageAndExit();
        }
    }

    public void installWindowsDrivers() {
        //install drivers
        HeimdallInstallDriversForWindowsPicture.main(null);
        log.level0("Installing drivers");
        new Log().level0("Driver Problems suck. Lemme make it easy.\n"
                + "1. Check that your device is download mode and connected up.\n"
                + "2. Select the one that says ---Gadget Serial--- in the main window\n"
                + "3. Click ---install driver---.\n" 
                + "4. Close out zadig and use CASUAL."
                + "Note: the USB port which you install this driver will be converted\n"
                + "to use Heimdall instead of Odin for download mode.  It only affects\n"
                + "ONE usb port.");
        //download 
        CASUALUpdates updater = new CASUALUpdates();
        try {
            updater.downloadFileFromInternet(updater.stringToFormattedURL(Statics.WinDriverInRepo), Statics.TempFolder + "zadig.exe", "Open-Source Heimdall Drivers");
            updater.downloadFileFromInternet(updater.stringToFormattedURL(Statics.WinDriverIniInRepo),"zadig.ini", "Open-Source Heimdall Drivers config");

        } catch (MalformedURLException | URISyntaxException ex) {
            log.errorHandler(ex);
        }
        //verify MD5 new String{"b88228d5fef4b6dc019d69d4471f23ec  vcredist_x86.exe"}
        new MD5sum().compareMD5StringsFromLinuxFormatToFilenames(new String[]{Statics.WinDriverInRepoMD5}, new String[]{Statics.TempFolder + "zadig.exe"});
        //execute
        String InstallZadigResults = shell.elevateSimpleCommand(new String[]{Statics.TempFolder + "zadig.exe"});
        if (InstallZadigResults.contains("CritERROR!!!")) {
            displayWindowsPermissionsMessageAndExit();
        }

    }

    public void displayWindowsPermissionsMessageAndExit() {
        if (Statics.isWindows()) {
            JOptionPane.showMessageDialog(null, ""
                    + "Administrative permissions are required to continue.\n"
                    + "Please log in as a System Administrator  and rerun the command or use the console: \n"
                    + "runas /user:Administrator java -jar " + getClass().getProtectionDomain().getCodeSource().getLocation().getPath().toString(), //Display Message
                    "Permissions Error", JOptionPane.ERROR_MESSAGE);
            //WindowsProblem.start();
        }
        System.exit(0);
    }

    void runWinHeimdallInstallationProcedure() {
        installWindowsDrivers();
        new Log().level0("done.");
        installWindowsVCRedist();
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
