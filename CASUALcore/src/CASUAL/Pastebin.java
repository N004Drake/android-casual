/*Pastebin provides automated pastebin submisson
 * 
 *  Copyright (C) 2013  Adam Outler
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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Jeremy Loper jrloper@gmail.com
 *
 * Modified From: https://code.google.com/p/pastebin-click/ Originally released
 * under the MIT license (http://www.opensource.org/licenses/mit-license.php)
 */
public class Pastebin {

    //Pastebin User DEV API Key
    final private static String devKey = "027c63663a6023d774b5392f380e5923";

    /**
     * Automatically prompts the user for their XDA username and submits a
     * pasting to Pastebin
     */
    public static void doPosting() throws IOException, URISyntaxException {

        String xdaUsername = new CASUALInteraction(new String[]{"An Error Has Occured!", "This is an automated prompt to sumbit a CASUAL log to Pastebin.\n\nPlease enter your XDA-Developers username and click 'Ok', click 'Cancel' to cancel"}).inputDialog();
        if (xdaUsername != null) {//CANCEL_OPTION will rerturn a null String
            API paste = new API(devKey);
            Log log = new Log();
            String user = "CASUAL-Automated";
            String passwd = "2J2y7SK172p46m1";
            String token = "";
            String format = "text";
            if (!(user.equals("")) && !(passwd.equals(""))) {
                String lResult = paste.login(user, passwd);
                if (lResult.equals("false")) {
                    log.level4Debug("Pastebin Login Failed");
                } else {
                    paste.setToken(lResult);
                    log.level4Debug("Pastebin Login Successful");
                }
                String pasteData = new FileOperations().readFile(Statics.TempFolder + "log.txt");

                String output = paste.makePaste(pasteData, "CASUAL r" + CASUALapplicationData.CASUALSVNRevision + "-" + xdaUsername, format);
                if (output.substring(0, 4).equals("http")) {
                    new LinkLauncher(output).launch();
                    StringSelection stringSelection = new StringSelection(output);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                    new CASUALInteraction("Thank You!","Pastebin URL Copied to Clipboard\n\nPlease Submit it in the appropriate forum thread").showInformationMessage();
                    log.level4Debug(output);
                } else {
                    log.level4Debug(output);
                }
            }
        }
    }

    /**
     * This is the API for Pastebin
     *
     */
    static class API {

        private Log log = new Log();
        private String token; //used for instance
        private String devkey; //used for our program
        private String loginURL = "http://www.pastebin.com/api/api_login.php";
        private String pasteURL = "http://www.pastebin.com/api/api_post.php";
        private API(String devkey) {
            this.devkey = devkey;
        }
        private String checkResponse(String response) {
            if (response.substring(0, 15).equals("Bad API request")) {
                if (response.substring(17)!= null){
                    return response.substring(17);
                }
            }
            return "";
        }
        public String login(String username, String password) throws UnsupportedEncodingException {
            String api_user_name = URLEncoder.encode(username, "UTF-8");
            String api_user_password = URLEncoder.encode(password, "UTF-8");
            String data = "api_dev_key=" + this.devkey + "&api_user_name=" + api_user_name + "&api_user_password=" + api_user_password;
            String response = this.page(this.loginURL, data);
            String check = this.checkResponse(response);
            if (!check.equals("")) {
                return "false";
            }

            this.token = response;
            return response;
        }


        public String makePaste(String code, String name, String format) throws UnsupportedEncodingException {
            String content = URLEncoder.encode(code, "UTF-8");
            String title = URLEncoder.encode(name, "UTF-8");
            String data = "api_option=paste&api_user_key=" + this.token + "&api_paste_private=0&api_paste_name=" + title + "&api_paste_expire_date=N&api_paste_format=" + format + "&api_dev_key=" + this.devkey + "&api_paste_code=" + content;
            String response = this.page(this.pasteURL, data);
            String check = this.checkResponse(response);
            if (!check.equals("")) {
                return check;
            }
            return response;
        }


        public String page(String uri, String urlParameters) {
            URL url;
            HttpURLConnection connection = null;
            try {
                // Create connection
                url = new URL(uri);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                    wr.writeBytes(urlParameters);
                    wr.flush();
                }
                // Get Response
                InputStream is = connection.getInputStream();
                StringBuilder response;
                try (BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    response = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                    }
                }
                return response.toString();

            } catch (Exception e) {
                log.level0Error(e.getMessage());
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
