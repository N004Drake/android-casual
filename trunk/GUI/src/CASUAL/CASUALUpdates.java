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

    public int checkOfficialRepo(String script, String localIdentificationString, String idStringFile) throws MalformedURLException, IOException {
        //compareMD5StringsFromLinuxFormatToFilenames(String[] LinuxFormat, String[] MD5Filenames){

        String webData;
        try {
            webData = getWebData(Statics.CASUALRepo + script + ".meta");
        } catch (URISyntaxException ex) {
            return 1;

        } catch (IOException ex) {
            Log.level3(script + " not found in repository.");
            return 1;
        }
        //This is where we hold the local information to be compared to the update
        CASUALIDString localInformation = new CASUALIDString();
        if (Statics.localInformation == null) {
            localInformation.setMetaDataFromIDString(localIdentificationString.split("\n"));
        } else {
            localInformation = Statics.localInformation;
        }

        //This is where we hold web information to be checked for an update every run.
        CASUALIDString webInformation = new CASUALIDString();
        webInformation.setMetaDataFromIDString(webData.split("\n"));
        Log.level3("***WEB VERSION***" + webInformation.metaData);
        Statics.SVNRevisionRequired = Integer.parseInt(webInformation.metaData[2]);
        Statics.updateMessageFromWeb = webInformation.getMetaData()[4];
        Statics.supportWebsiteFromWeb = webInformation.metaData[3];
        displayCASUALString(webInformation.metaData);

        if (localInformation.metaData[0].equals(webInformation.metaData[0])) {
            if (checkVersionInformation(webInformation.metaData[2], localInformation.metaData[2])) {
                //update SVN
                Log.level0("ERROR. CASUAL is out-of-date. This version of CASUAL cannot procede further. See " + webInformation.metaData[3] + " for more information. ");
                //Log.level0(webInformation[4]);
                return 3;
            }
            if (checkVersionInformation(webInformation.metaData[1], localInformation.metaData[1])) {
                Log.level0("Current Version " + localInformation.metaData[1] + " requires update to version " + webInformation.metaData[1]);
                Log.level0("Script is out of date. See " + webInformation.metaData[3] + " for more information.  Updating.");
                Log.level0(webInformation.metaData[4]);
                //ugly code dealing with /SCRIPTS/ folder on computer.
                new FileOperations().makeFolder(Statics.TempFolder + "SCRIPTS" + Statics.Slash);
                int status = downloadUpdates(script, webInformation, Statics.TempFolder);
                if (status == 0) {
                    Log.level0("... Update Sucessful! MD5s verified!");
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
        } catch (MalformedURLException | URISyntaxException ex) {
            Log.errorHandler(ex);
        }
        return true;
    }

    public boolean downloadFileFromInternet(URL url, String outputFile, String friendlyName) {
        Log.level3("Downloading " + url);
        Log.level3("To: " + outputFile);
        InputStream input;
        try {

            input = url.openStream();
            byte[] buffer = new byte[4096];
            File f = new File(outputFile);
            OutputStream output = new FileOutputStream(f);
            int bytes = 0;
            Log.progress(friendlyName.replace("/SCRIPTS/", ""));
            int lastlength = 0;
            int kilobytes = 0;
            int offset = Statics.ProgressDoc.getLength();
            try {
                int bytesRead;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                    bytes = bytes + buffer.length;
                    kilobytes = kilobytes + 4;
                    Log.replaceLine(("..." + Integer.toString(kilobytes)) + "kb ", offset, lastlength);
                    lastlength = 6 + Integer.toString(kilobytes).length();

                }
            } finally {
                output.flush();
                output.close();

            }
        } catch (Exception ex) {
            Log.level3("Error Downloading " + ex.getMessage());
            return false;
        }
        return true;
    }

    public void displayCASUALString(String[] CASUALString) {
        //SVN Revision, Script Revision, Script Identification, support URL, message to user
        Log.level3("Identification: " + CASUALString[0]);
        Log.level3("ScriptRevision: " + CASUALString[1]);
        Log.level3("CASUALRevision: " + CASUALString[2]);
        Log.level3("URL: " + CASUALString[3]);
        Log.level3("Server Message: " + CASUALString[4]);
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
    private int downloadUpdates(String scriptname, CASUALIDString webInformation, String localPath) {
        URL url;
        try {
            url = stringToFormattedURL(Statics.CASUALRepo + scriptname);
        } catch (MalformedURLException ex) {
            Log.level3("malformedURL exception while CASUALUpdates.downloadUpdates() " + Statics.CASUALRepo + scriptname);
            return 1;
        } catch (URISyntaxException ex) {
            Log.level3("URISyntaxException exception while CASUALUpdates.downloadUpdates() " + Statics.CASUALRepo + scriptname);
            return 1;
        }
        Log.level0("Downloading Updates");

        try {
            ArrayList<String> list = new ArrayList();
            String localfile = Statics.TempFolder + scriptname;
            String ext;
            for (int n = 0; n < webInformation.md5sums.length; n++) {
                //get download extension from md5sum;

                try {
                    String[] md5 = webInformation.md5sums[n].split("  ");
                    String fileName = md5[1];
                    ext = "." + fileName.split("\\.")[1];
                    if (downloadFileFromInternet(new URL(url + ext), localfile + ext, scriptname + ext)) {
                        list.add(Statics.TempFolder + scriptname + ext);
                    }
                } catch ( ArrayIndexOutOfBoundsException | NullPointerException ex) {
                    continue; //TODO should this be handled better?
                }


            }

            String[] files = list.toArray(new String[list.size()]);
            if (new MD5sum().compareMD5StringsFromLinuxFormatToFilenames(webInformation.md5sums, files)) {
                return 0;//success
            } else {
                return 2;//failure
            }


        } catch (MalformedURLException ex) {
            Logger.getLogger(CASUALUpdates.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1;//no update available
    }
}
