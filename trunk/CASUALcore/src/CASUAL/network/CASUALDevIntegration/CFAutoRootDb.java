/*Pulls file locations from CFAutoRoot 
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
package CASUAL.network.CASUALDevIntegration;

import CASUAL.misc.StringOperations;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adamoutler
 */
public class CFAutoRootDb {

    final Properties BUILDPROP = new Properties();
    final private ArrayList<Device> devicesAvailable;
    CFAutoRootDb(String BuildProp) {
        this.devicesAvailable = new ArrayList<Device>();
        try {
            BUILDPROP.load(new StringReader(BuildProp));
        } catch (IOException ex) {
        }
        grabTable();

    }

    

    public String returnForMyDevice() {
        for (Device d : devicesAvailable) {
            if (BUILDPROP.getProperty("ro.product.manufacturer", "").equals(d.oem)) {
                if (BUILDPROP.getProperty("ro.product.model", "").equals(d.model)) {
                    if (BUILDPROP.getProperty("ro.product.name", "").equals(d.name)) {
                        if (BUILDPROP.getProperty("ro.product.device", "").equals(d.device)) {
                            if (BUILDPROP.getProperty("ro.product.board", "").equals(d.board)) {
                                if (BUILDPROP.getProperty("ro.board.platform", "").equals(d.platform)) {
                                    
                                    System.out.println("located a "+d.oem+ ", model:"+d.model+", device:"+d.device);
                                    System.out.println(d.download);
                                    return d.download+"?retrieve_file=1";
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Found nothing available for your device on http://autoroot.chainfire.eu/");
        return "";
    }

    private void grabTable() {

        try {
            //get url
            URI uri = new URI("http", "autoroot.chainfire.eu", "/" + "", "", null);
            URL url = new URL(uri.toASCIIString());
            //convert to a table
            String page = StringOperations.convertStreamToString(url.openStream());
            page = page.substring(page.indexOf("<table>"), page.length());
            page = page.substring(0, page.indexOf("</table>"))+"\n";
            //convert to a buffered reader
            BufferedReader br = new BufferedReader(new StringReader(page));

            //flush out the table headers
            int trcount = 0;
            while (trcount < 2) {
                if (br.readLine().contains("<tr>")) {
                    trcount++;
                }
            }
            
            //Start the main reading action
            while (br.ready()) {
                //get a line
                String line="";
                while ( !line.endsWith("\n")){
                    line = line+(char)br.read();
                    if (line.endsWith("\uffff")) return;
                }
                //begin parsing tables
                if (line.contains("<tr>")) {
                    //our instance device
                    Device device = new Device();
                    int i = 0;
                    while (!line.contains("</tr>")) {
                        line = br.readLine().replace("<td>", "").replace("</td>", "").trim();
                        switch (i) {
                            case 0:
                                device.oem = line;
                                break;
                            case 1:
                                device.model = line;
                                break;
                            case 2:
                                device.name = line;
                                break;
                            case 3:
                                device.device = line;
                                break;
                            case 4:
                                device.board = line;
                                break;
                            case 5:
                                device.platform = line;
                                break;
                            case 9:
                                device.download = line.replace("\">File</a>", "").replace("<a href=\"", "");
                                break;
                            default:
                                break;
                        }
                        i++;
                    }

                    devicesAvailable.add(device);
                    /*System.out.println("OEM: " + device.oem);
                    System.out.println("model: " + device.model);
                    System.out.println("name: " + device.name);
                    System.out.println("device: " + device.device);
                    System.out.println("board: " + device.board);
                    System.out.println("platform: " + device.platform);
                    System.out.println("download link: " + device.download);*/
                }
            }


        } catch (URISyntaxException ex) {
            Logger.getLogger(CFAutoRootDb.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(CFAutoRootDb.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CFAutoRootDb.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

class Device {

    String oem;
    String model;
    String name;
    String device;
    String board;
    String platform;
    String download;
}
