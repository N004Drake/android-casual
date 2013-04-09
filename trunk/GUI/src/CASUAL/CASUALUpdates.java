/*CASUALUpdates provides a way to check and update CASUAL
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

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public class CASUALUpdates {

    /*
     * checks for updates returns: 0=no updates found 1=random error 2=Script
     * Update Required 3=CASUAL update required- cannot continue. 4=download
     * failed
     */
    Log Log = new Log();

    public int checkOfficialRepo(String script, String localIdentificationString) throws MalformedURLException, IOException {
        //compareMD5StringsFromLinuxFormatToFilenames(String[] LinuxFormat, String[] MD5Filenames){

        CASPACData webInformation;
        try {
            webInformation = new CASPACData(getWebData(Statics.CASUALRepo + script + ".meta"));
        } catch (URISyntaxException ex) {
            return 1;

        } catch (IOException ex) {
            Log.level4Debug(script + " not found in repository.");
            return 1;
        }
        //This is where we hold the local information to be compared to the update
        CASPACData localInformation = new CASPACData(localIdentificationString);

        Log.level4Debug("***WEB VERSION***\nIDString:" + webInformation.uniqueIdentifier + "\nSVNRevision:" + webInformation.minSVNRevision + "\nScriptRevision:" + webInformation.scriptRevision + "\nsupportURL:" + webInformation.supportURL + "updateMessage" + webInformation.updateMessage);


        if (localInformation.uniqueIdentifier.equals(webInformation.uniqueIdentifier)) {
            if (checkVersionInformation(webInformation.minSVNRevision, localInformation.minSVNRevision)) {
                //update SVN
                Log.level0Error("ERROR. CASUAL is out-of-date. This version of CASUAL cannot procede further. See " + webInformation.supportURL + " for more information. ");
                //Log.level0(webInformation[4]);
                return 3;
            }
            if (checkVersionInformation(webInformation.scriptRevision, localInformation.scriptRevision)) {
                Log.level0Error("Current Version " + localInformation.scriptRevision + " requires update to version " + webInformation.scriptRevision);
                Log.level0Error("Script is out of date. See " + webInformation.supportURL + " for more information.  Updating.");
                Log.level0Error(webInformation.updateMessage);
                //ugly code dealing with /SCRIPTS/ folder on computer.
                new FileOperations().makeFolder(Statics.TempFolder + "SCRIPTS" + Statics.Slash);
                int status = downloadUpdates(script, webInformation, Statics.TempFolder);
                if (status == 0) {
                    Log.level0Error("... Update Sucessful! MD5s verified!");
                    return 2;
                } else {
                    return 4;
                }
            }
        }
        return 0;
    }
    /*
     * used to check CASUAL Version Information returns true if update is
     * required
     */

    private boolean checkVersionInformation(String webVersion, String localVersion) {
        int wv = Integer.parseInt(webVersion);
        int lv = Integer.parseInt(localVersion);
        if ((wv != 0)) {
            if (wv > lv) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public boolean downloadFileFromInternet(String URL, String outputFile, String friendlyName) {
        try {
            downloadFileFromInternet(stringToFormattedURL(URL), outputFile, friendlyName);
        } catch (MalformedURLException ex) {
            Log.errorHandler(ex);
        } catch (URISyntaxException ex) {
            Log.errorHandler(ex);
        }
        return true;
    }

    public boolean downloadFileFromInternet(URL url, String outputFile, String friendlyName) {
        Log.level4Debug("Downloading " + url);
        Log.level4Debug("To: " + outputFile);
        InputStream input;
        try {

            input = url.openStream();
            byte[] buffer = new byte[4096];
            File f = new File(outputFile);
            OutputStream output = new FileOutputStream(f);
            int bytes = 0;
            Log.progress(friendlyName.replace("/SCRIPTS/", ""));
            int lastlength = 0;
            int kilobytes;
            int offset = Statics.ProgressDoc.getLength();
            try {
                int bytesRead;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                    bytes = bytes + bytesRead;
                    kilobytes = bytes / 1024;
                    Log.replaceLine(("..." + Integer.toString(kilobytes)) + "kb ", offset, lastlength);
                    lastlength = 6 + Integer.toString(kilobytes).length();

                }
            } finally {
                output.flush();
                output.close();

            }
        } catch (Exception ex) {
            Log.level4Debug("Error Downloading " + ex.getMessage());
            return false;
        }
        return true;
    }

    public void displayCASUALString(String[] CASUALString) {
        //SVN Revision, Script Revision, Script Identification, support URL, message to user
        Log.level4Debug("Identification: " + CASUALString[0]);
        Log.level4Debug("ScriptRevision: " + CASUALString[1]);
        Log.level4Debug("CASUALRevision: " + CASUALString[2]);
        Log.level4Debug("URL: " + CASUALString[3]);
        Log.level4Debug("Server Message: " + CASUALString[4]);
    }

    public URL stringToFormattedURL(String stringURL) throws MalformedURLException, URISyntaxException {
        URL url = new URL(stringURL);
        url = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef()).toURL();
        return url;
    }

    private String getWebData(String script) throws MalformedURLException, IOException, URISyntaxException {
        URL url = stringToFormattedURL(script);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        ByteBuffer buf = ByteBuffer.allocateDirect(10);
        String webData = "";
        int numRead = 0;
        while (numRead >= 0) {
            buf.rewind();
            numRead = rbc.read(buf);
            buf.rewind();
            for (int i = 0; i < numRead; i++) {
                byte b = buf.get();
                webData = webData + (new String(new byte[]{b}));
            }
        }
        return webData;
    }

    /*
     * Takes CASUAL ID String Returns: SVN Revision, Script Revision, Script
     * Identification, support URL, message to user
     * returns 0-update applied
     * 1- update not available
     * 2- update error
     */
    private int downloadUpdates(String scriptname, CASPACData webInformation, String localPath) {
        URL url;
        try {
            url = stringToFormattedURL(Statics.CASUALRepo + scriptname);
        } catch (MalformedURLException ex) {
            Log.level4Debug("malformedURL exception while CASUALUpdates.downloadUpdates() " + Statics.CASUALRepo + scriptname);
            return 1;
        } catch (URISyntaxException ex) {
            Log.level4Debug("URISyntaxException exception while CASUALUpdates.downloadUpdates() " + Statics.CASUALRepo + scriptname);
            return 1;
        }
        Log.level0Error("Downloading Updates");
        String[] md5lines = StringOperations.convertArrayListToStringArray(webInformation.md5s);

        try {
            ArrayList<String> list = new ArrayList<>();
            String localfile = Statics.TempFolder + scriptname;
            String ext;
            for (int n = 0; n < md5lines.length; n++) {
                //get download extension from md5sum;

                try {

                    String[] md5 = md5lines[n].split("  ");
                    String fileName = md5[1];
                    ext = "." + fileName.split("\\.")[1];
                    if (downloadFileFromInternet(new URL(url + ext), localfile + ext, scriptname + ext)) {
                        list.add(Statics.TempFolder + scriptname + ext);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    continue; //TODO should this be handled better?
                } catch (NullPointerException ex) {
                    continue; //TODO shoudl this be handled better?
                }


            }

            String[] files = list.toArray(new String[list.size()]);
            if (new MD5sum().compareMD5StringsFromLinuxFormatToFilenames(md5lines, files)) {
                return 0;//success
            } else {
                return 2;//failure
            }


        } catch (MalformedURLException ex) {
            Logger.getLogger(CASUALUpdates.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1;//no update available
    }
    /*
     * String Properties File
     * Returns location of first downloaded file
     */
    static String arch = "";
    static String system = "";

    public String CASUALRepoDownload(String propertiesFileInCASUALOnlineRepo) throws FileNotFoundException, IOException, InterruptedException {
        arch = Statics.is64bitSystem() ? "64" : "32";
        system = Statics.isWindows() ? "win" : system;
        system = Statics.isLinux() ? "linux" : system;
        system = Statics.isMac() ? "mac" : system;
        String basename = new File(propertiesFileInCASUALOnlineRepo).getName();
        //download location, md5, and version information
        downloadFileFromInternet(propertiesFileInCASUALOnlineRepo, Statics.TempFolder + basename, "locating files");
        //Set properties file
        Properties prop = new Properties();
        prop.load(new FileInputStream(Statics.TempFolder + basename));
        // get information from properties file
        int counter = 1;
        String filenumber = "";

        /*
         * This loop uses the filenumber as a blank the first time through
         * after that filenumber turns to "2", so it will look for 
         * eg. "win32" property then "win32-2" property
         * 
         * It will download the applicable files in the properties file. then
         * MD5sum against the value in the properties file.
         */
        while (prop.getProperty(system + arch + filenumber) != null) {
            String downloadURL = prop.getProperty(system + arch + filenumber);


            String downloadBasename = downloadURL.substring(downloadURL.lastIndexOf('/') + 1, downloadURL.length());
            String availableVersion = prop.getProperty(system + arch + filenumber + "version");
            String downloadedFile = Statics.TempFolder + downloadBasename;
            //download update based on information available.

            downloadFileFromInternet(downloadURL, downloadedFile, downloadBasename + " ver" + availableVersion);

            //get expected MD5
            String expectedMD5 = new MD5sum().getMD5fromLinuxMD5String(prop.getProperty(system + arch + "md5"));
            //verify  we have an MD5
            if (expectedMD5.length() >= 31) {
                //if MD5 does not match
                if (!new MD5sum().compareFileToMD5(new File(downloadedFile), expectedMD5)) {
                    //show message and exit
                    new CASUALInteraction().showErrorDialog("During update a bad file was downlaoded.\n"
                            + "Please check your internet connection and try again.\n"
                            + "If the problem persists, report it.\n"
                            + "CASUAL will now exit.  Please try again.", "ERROR!");
                    System.exit(0);
                }
            }
            counter++;
            filenumber = "-" + Integer.toString(counter);
        }
        String downloadURL = prop.getProperty(system + arch);
        String downloadBasename = new File(downloadURL).getName();
        return Statics.TempFolder + downloadBasename;

    }
    Runnable gatherInfo = new Runnable() {
        @Override
        public void run() {
            arch = Statics.is64bitSystem() ? "64" : "32";
            system = Statics.isWindows() ? "win" : system;
            system = Statics.isLinux() ? "linux" : system;
            system = Statics.isMac() ? "mac" : system;

        }
    };
}
