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
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public class CASUALUpdates {

    /*
     * checks for updates returns: 0=no updates found 1=random error
     * 2=Script Update Required 3=CASUAL update required- cannot
     * continue. 4=download failed 
     */
    Log Log = new Log();

    public int checkOfficialRepo(String script, String localIdentificationString) throws MalformedURLException, IOException {

        String webData = "";
        try {
            webData = getWebData(Statics.CASUALRepo + script + ".scr");
        } catch (URISyntaxException ex) {
            return 1;

        } catch (IOException ex) {
            return 1;
        }
        String[] localInformation = parseIDString(localIdentificationString);

        System.out.println("***WEB VERSION***");
        String[] webInformation = parseIDString(webData.split("\n", 2)[0]);

        displayCASUALString(webInformation);
        System.out.println();

        if (localInformation[0].equals(webInformation[0])) {
            if (checkVersionInformation(webInformation[2], localInformation[2])) {
                //update SVN
                Log.level0("ERROR. CASUAL is out-of-date. This version of CASUAL cannot procede further. See " + webInformation[3] + " for more information. ");
                Log.level0(webInformation[4]);
                return 3;
            }
            if (checkVersionInformation(webInformation[1], localInformation[1])) {
                try {
                    Log.level0("Script is out of date. See " + webInformation[3] + " for more information.  Updating.");
                    Log.level0(webInformation[4]);

                    URL url = this.stringToFormattedURL(Statics.CASUALRepo + script);
                    String scriptname = script.replaceFirst("/SCRIPTS/", Statics.Slash+"SCRIPTS"+Statics.Slash);
                    new FileOperations().makeFolder(Statics.TempFolder+Statics.Slash+"SCRIPTS"+Statics.Slash);
                    
                    //TODO set /tmp/newfile as a real file
                    downloadFileFromInternet(new URL(url + ".zip"), Statics.TempFolder+scriptname+".zip" ,  scriptname + ".zip");
                    downloadFileFromInternet(new URL(url + ".scr"), Statics.TempFolder+scriptname+".scr",  scriptname + ".scr");
                    downloadFileFromInternet(new URL(url + ".txt"), Statics.TempFolder+scriptname+".txt",  scriptname + ".txt");
                    return 2;
                } catch (URISyntaxException ex) {
                    Logger.getLogger(CASUALUpdates.class.getName()).log(Level.SEVERE, null, ex);
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

    public boolean downloadFileFromInternet(URL url, String outputFile, String friendlyName) {
        InputStream input;
        try {

            input = url.openStream();
            byte[] buffer = new byte[1024];
            File f = new File(outputFile);
            OutputStream output = new FileOutputStream(f);
            int bytes = 0;
            Log.level1("Downloading update to " + friendlyName.replace("/SCRIPTS/", ""+":  "));
            Log.level1("");
            int lastlength = 0;
            int kilobytes=0;
            int offset = Statics.ProgressDoc.getLength() - 1;
            try {
                int bytesRead = 0;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                    bytes = bytes + buffer.length;
                    kilobytes++;
                    Log.replaceLine(" "+Integer.toString(kilobytes)+"kb", offset, lastlength);
                    lastlength = 3 + Integer.toString(kilobytes).length();

                }
            } finally {

                output.flush();
                output.close();

            }
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
        return true;
    }

    public void displayCASUALString(String[] CASUALString) {
        //SVN Revision, Script Revision, Script Identification, support URL, message to user
        System.out.println("CASUALRevision: " + CASUALString[0]);
        System.out.println("ScriptRevision: " + CASUALString[1]);
        System.out.println("Identification: " + CASUALString[2]);
        System.out.println("URL: " + CASUALString[3]);
        System.out.println("Server Message: " + CASUALString[4]);
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
     */
    private String[] parseIDString(String scriptIdentificationString) {
        scriptIdentificationString = scriptIdentificationString.replaceAll(" ", "").replaceAll("#", "");
        String[] commaSplit = scriptIdentificationString.split(",");
        String[] SVNScriptRevision = {"", "", "", "", ""};
        for (int n = 0; n < commaSplit.length; n++) {
            String[] splitID = commaSplit[n].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)", 4);
            if (splitID[0].startsWith("ID")) {
                String id = splitID[0].replaceFirst("ID", "");
                SVNScriptRevision[0] = id;
            }
            if (splitID[0].startsWith("R")) {
                SVNScriptRevision[1] = splitID[1];
            }

            if (splitID[0].startsWith("CASUAL") || splitID[0].startsWith("SVN")) {
                SVNScriptRevision[2] = splitID[1];
            }
            if (splitID[0].startsWith("URL")) {
                String URL = splitID[0].replaceFirst("URL", "");
                SVNScriptRevision[3] = URL;
            }
            if (splitID[0].startsWith("Message")) {
                String message = splitID[0].replaceFirst("Message", "");
                SVNScriptRevision[4] = message;
            }
        }
        return SVNScriptRevision;
    }
}
