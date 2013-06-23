/*CASUALPackageData provides an object for CASUAL to load information from. 
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author adam
 */
public class CASUALapplicationData {

    public static boolean packageDataHasBeenSet = false;
    public static boolean scriptsHaveBeenRecognized = false;
    public static String CASUALSVNRevision = "0";
    public static String CASUALBuildNumber = "0";
    public static String buildProperties = "";
    public static String buttonText = "Do It!";
    public static String title = "";
    public static String bannerText = "CASUAL";
    public static String bannerPic = "";
    public static boolean usePictureForBanner = false;
    public static String developerName = "";
    public static String donateButtonName = "";
    public static boolean useSound = false;
    public static String developerDonateLink = "";
    public static String donationLink = "";
    public static String CASUALFileName = "";
    public static boolean AlwaysEnableControls = true;
    public static String meta;

    CASUALapplicationData() {
        buildProperties = Statics.BUILDPROPERTIES;
        try {
            usePictureForBanner = java.util.ResourceBundle.getBundle(buildProperties).getString("Window.UsePictureForBanner").contains("rue");
            developerDonateLink = java.util.ResourceBundle.getBundle(buildProperties).getString("Developer.DonateLink");
            useSound = java.util.ResourceBundle.getBundle(buildProperties).getString("Audio.Enabled").contains("rue");
            donateButtonName = java.util.ResourceBundle.getBundle(buildProperties).getString("Developer.DonateToButtonText");
            developerName = java.util.ResourceBundle.getBundle(buildProperties).getString("Developer.Name");
            buttonText = java.util.ResourceBundle.getBundle(buildProperties).getString("Window.ExecuteButtonText");
            title = java.util.ResourceBundle.getBundle(buildProperties).getString("Window.Title") + " - " + java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.title") + java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision");
            bannerText = java.util.ResourceBundle.getBundle(buildProperties).getString("Window.BannerText");
            bannerPic = java.util.ResourceBundle.getBundle(buildProperties).getString("Window.BannerPic");
            donateButtonName = java.util.ResourceBundle.getBundle(buildProperties).getString("Developer.DonateToButtonText");
            donationLink = java.util.ResourceBundle.getBundle(buildProperties).getString("Developer.DonateLink");
            AlwaysEnableControls = java.util.ResourceBundle.getBundle(buildProperties).getString("Application.AlwaysEnableControls").contains("rue");
            packageDataHasBeenSet = true;
        } catch (java.util.MissingResourceException ex) {
            //nothing to do.  bundle is missing. use defaults
        }
    }

    CASUALapplicationData(BufferedInputStream in) throws IOException {

        Properties properties = new Properties();
        properties.load(in);
        //properties.list(System.out);
        in.close();
        buildProperties = null;
        usePictureForBanner = properties.getProperty("Window.UsePictureForBanner").contains("rue");
        developerDonateLink = properties.getProperty("Developer.DonateLink");
        useSound = properties.getProperty("Audio.Enabled").contains("rue");
        donateButtonName = properties.getProperty("Developer.DonateToButtonText");
        developerName = properties.getProperty("Developer.Name");
        buttonText = properties.getProperty("Window.ExecuteButtonText");
        title = properties.getProperty("Window.Title") + " - " + java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.title") + java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision");
        bannerText = properties.getProperty("Window.BannerText");
        bannerPic = properties.getProperty("Window.BannerPic");
        donateButtonName = properties.getProperty("Developer.DonateToButtonText");
        donationLink = properties.getProperty("Developer.DonateLink");
        packageDataHasBeenSet = true;

        new Log().level3Verbose("-----CASUAL PACKAGE-----");
        new Log().level3Verbose("" + title + "\n by:" + developerName + "");
        if (!donationLink.equals("")) {
            new Log().level3Verbose(" Donate:" + donationLink);
        }
    }

    public void initialize() {
        //handled in constructor, this is here to provide an access method.
    }
}
