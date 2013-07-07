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

    /**
     * location to CASUAL online repository
     */
    public final String CASUALRepo = "http://android-casual.googlecode.com/svn/trunk/CASUALcore/src";

    /*
     * checks for updates returns: 0=no updates found 1=random error 2=Script
     * Update Required 3=CASUAL update required- cannot continue. 4=download
     * failed
     */
    Log Log = new Log();

    /**
     * performs an update to CASUAL script
     * @param script script to check
     * @param localInformation local meta file
     * @return integer value to be processed as return
     * 0 no update required
     * 1 error during update
     * 2 update occured
     * 3 SVN version cannot handle new script
     * 4 bad download
     * 
     * @throws MalformedURLException
     * @throws IOException
     */
    public int checkOfficialRepo(String script, CASPACData localInformation) throws MalformedURLException, IOException {
        //compareMD5StringsFromLinuxFormatToFilenames(String[] LinuxFormat, String[] MD5Filenames){

        CASPACData webInformation;
        try {
            webInformation = new CASPACData(getWebData(CASUALRepo + script + ".meta"));
        } catch (URISyntaxException ex) {
            return 1;

        } catch (IOException ex) {
            Log.level4Debug(script + " not found in repository.");
            return 1;
        }
        Log.level4Debug("***WEB VERSION***\nIDString:" + webInformation.uniqueIdentifier + "\nSVNRevision:" + webInformation.minSVNRevision + "\nScriptRevision:" + webInformation.scriptRevision + "\nsupportURL:" + webInformation.supportURL + "updateMessage" + webInformation.updateMessage);
        Statics.webInformation = webInformation;
        try {
            if (localInformation.uniqueIdentifier.equals(webInformation.uniqueIdentifier)) {
                if (!webInformation.isOurSVNHighEnoughToRunThisScript(Integer.parseInt(localInformation.minSVNRevision))) {
                    //update SVN
                    Log.level0Error("@casualIsOutOfDate");
                    Log.level0Error(webInformation.supportURL);
                    return 3;
                }
                if (checkVersionInformation(webInformation.scriptRevision, localInformation.scriptRevision)) {
                    
                    Log.level2Information("@scriptIsOutOfDate");
                    //ugly code dealing with /SCRIPTS/ folder on computer.
                    new FileOperations().makeFolder(Statics.TempFolder + "SCRIPTS" + Statics.Slash);
                    int status = downloadUpdates(script, webInformation);
                    if (status == 0) {
                        Log.level2Information("@md5sVerified");
                        return 2;
                    } else {
                        return 4;
                    }
                }
            }
            return 0;
        } catch (NullPointerException ex) {
            Log.level0Error("@metaDataMalformed");
            return 0;
        }
    }
    /*
     * used to check CASUAL Version Information returns true if update is
     * required
     */

    private boolean checkVersionInformation(String webVersion, String localVersion) {
        int wv = Integer.parseInt(webVersion);
        int lv = Integer.parseInt(localVersion);
        if (wv != 0) {
            if (wv > lv) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    /**
     * downloads a file
     * @param URL web location to download
     * @param outputFile the local file to output
     * @param friendlyName name displayed to user
     * @return true if downloaded
     */
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

    /**
     *
     * downloads a file
     * @param url web location to download
     * @param outputFile the local file to output
     * @param friendlyName name displayed to user
     * @return true if downloaded
     */
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

    /**
     * displays data from a split meta file
     * @param CASUALString meta information to be displayed
     * 0-id
     * 1-revsion
     * 2-minimum svn
     * 3-support URL
     * 4-update message
     */
    public void displayCASUALString(String[] CASUALString) {
        //SVN Revision, Script Revision, Script Identification, support URL, message to user
        Log.level4Debug("Identification: " + CASUALString[0]);
        Log.level4Debug("ScriptRevision: " + CASUALString[1]);
        Log.level4Debug("CASUALRevision: " + CASUALString[2]);
        Log.level4Debug("URL: " + CASUALString[3]);
        Log.level4Debug("Server Message: " + CASUALString[4]);
    }

    /**
     * converts a string to a URL
     * @param stringURL raw URL in string format
     * @return URL formatted properly
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public URL stringToFormattedURL(String stringURL) throws MalformedURLException, URISyntaxException {
        URL url = new URL(stringURL);
        url = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef()).toURL();
        return url;
    }

    private String getWebData(String script) throws MalformedURLException, IOException, URISyntaxException {
        URL url = stringToFormattedURL(script);
        String webData;
        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream())) {
            ByteBuffer buf = ByteBuffer.allocateDirect(10);
            webData = "";
            int numRead = 0;
            while (numRead >= 0) {
                buf.rewind();
                numRead = rbc.read(buf);
                buf.rewind();
                for (int i = 0; i < numRead; i++) {
                    byte b = buf.get();
                    webData = webData + new String(new byte[]{b});
                }
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
    private int downloadUpdates(String scriptname, CASPACData webInformation) {
        URL url;
        try {
            url = stringToFormattedURL(CASUALRepo + scriptname);
        } catch (MalformedURLException ex) {
            Log.level4Debug("malformedURL exception while CASUALUpdates.downloadUpdates() " + CASUALRepo + scriptname);
            return 1;
        } catch (URISyntaxException ex) {
            Log.level4Debug("URISyntaxException exception while CASUALUpdates.downloadUpdates() " + CASUALRepo + scriptname);
            return 1;
        }
        Log.level0Error("@downloadingUpdates");
        String[] md5lines = StringOperations.convertArrayListToStringArray(webInformation.md5s);

        try {
            ArrayList<String> list = new ArrayList<>();
            String localfile = Statics.TempFolder + scriptname;
            String ext;
            for (int n = 0; n < md5lines.length; n++) {
                //get download extension from md5sum;

                try {
                    String[] md5 = md5lines[n].split("  ");
                    if (md5.length == 2) {
                        String fileName = md5[1];
                        ext = "." + fileName.split("\\.")[1];
                        if (downloadFileFromInternet(new URL(url + ext), localfile + ext, scriptname + ext)) {
                            list.add(Statics.TempFolder + scriptname + ext);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    Log.level0Error("@invalidMD5String");
                    continue;
                } catch (NullPointerException ex) {
                    Log.level0Error("@invalidMD5String");
                    continue;
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

    /**
     * downloads proper file if available in repository
     * @param propertiesFileInCASUALOnlineRepo requested file to be downloaded
     * ie -"heimdall" will be translated to web url:heimdallWin32.zip, downloaded
     * and the path to the downloaded file is returned. 
     * @return file downloaded for system/arch
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     */
    public String CASUALRepoDownload(String propertiesFileInCASUALOnlineRepo) throws FileNotFoundException, IOException, InterruptedException {
        arch = Statics.is64bitSystem() ? "64" : "32";
        system = Statics.isWindows() ? "win" : system;
        system = Statics.isLinux() ? "linux" : system;
        system = Statics.isMac() ? "mac" : system;
        Log.level3Verbose("Found " + system + " " + arch + "computer");
        String basename = new File(propertiesFileInCASUALOnlineRepo).getName();
        //download location, md5, and version information
        downloadFileFromInternet(propertiesFileInCASUALOnlineRepo, Statics.TempFolder + basename, "locating files");
        Log.level3Verbose("downloaded" + propertiesFileInCASUALOnlineRepo);
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
            Log.level3Verbose("based on information, we need to download: " + downloadURL);

            String downloadBasename = downloadURL.substring(downloadURL.lastIndexOf('/') + 1, downloadURL.length());
            String availableVersion = prop.getProperty(system + arch + filenumber + "Version");
            String downloadedFile = Statics.TempFolder + downloadBasename;
            //download update based on information available.

            downloadFileFromInternet(downloadURL, downloadedFile, downloadBasename + " ver" + availableVersion);

            //get expected MD5
            String expectedMD5 = new MD5sum().getMD5fromLinuxMD5String(prop.getProperty(system + arch + "md5"));
            //verify  we have an MD5
            //if MD5 does not match
            if (expectedMD5.length() >= 31 && !new MD5sum().compareFileToMD5(new File(downloadedFile), expectedMD5)) {
                //show message and exit
                new CASUALInteraction("@interactionBadDownload").showErrorDialog();
                CASUALApp.shutdown(0);
            }
            counter++;
            filenumber = "-" + Integer.toString(counter);
        }
        String downloadURL = prop.getProperty(system + arch);
        String downloadBasename = new File(downloadURL).getName();
        return Statics.TempFolder + downloadBasename;

    }
}
