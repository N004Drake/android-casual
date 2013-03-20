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

/**
 *
 * @author adam
 */
public class CASUALPackageData {
    public static boolean PackageDataHasBeenSet=false;
    public static boolean ScriptsHaveBeenRecognized=false;
    public static String CASUALSVNRevision;
    public static String CASUALBuildNumber;
    public static String buildProperties;
    public static String ButtonText;
    public static String Title;
    public static String BannerText;
    public static String BannerPic;
    public static boolean usePictureForBanner;
    public static String DeveloperName;
    public static String DonateButtonName;
    public static boolean UseSound;
    public static String DeveloperDonateLink;
    public static String DontateButtonText;
    public static String DonationLink;

    CASUALPackageData(String build) {
       if ((build==null) || (build.equals(""))){
           buildProperties=Statics.BUILDPROPERTIES;
       } else{
           buildProperties=build;
       }
       setProperties();
    }

    CASUALPackageData() {
        buildProperties=Statics.BUILDPROPERTIES;
        setProperties();
    }

    private void setProperties(){
        CASUALSVNRevision= java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision");
        CASUALBuildNumber= java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.buildnumber");
        usePictureForBanner = java.util.ResourceBundle.getBundle(buildProperties).getString("Window.UsePictureForBanner").contains("rue");
        DeveloperDonateLink = java.util.ResourceBundle.getBundle(buildProperties).getString("Developer.DonateLink");
        UseSound = java.util.ResourceBundle.getBundle(buildProperties).getString("Audio.Enabled").contains("rue");
        DonateButtonName = java.util.ResourceBundle.getBundle(buildProperties).getString("Developer.DonateToButtonText");
        DeveloperName = java.util.ResourceBundle.getBundle(buildProperties).getString("Developer.Name");
        buildProperties = Statics.BUILDPROPERTIES;
        ButtonText = java.util.ResourceBundle.getBundle(buildProperties).getString("Window.ExecuteButtonText");
        Title = java.util.ResourceBundle.getBundle(buildProperties).getString("Window.Title") + " - "+ java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.title") +java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision");
        BannerText = java.util.ResourceBundle.getBundle(buildProperties).getString("Window.BannerText");
        BannerPic = java.util.ResourceBundle.getBundle(buildProperties).getString("Window.BannerPic");
        DonateButtonName = java.util.ResourceBundle.getBundle(buildProperties).getString("Developer.DonateToButtonText");
        DonationLink = java.util.ResourceBundle.getBundle(buildProperties).getString("Developer.DonateLink");
        PackageDataHasBeenSet=true;
    }
    public void scan(){
        
    }
  
}
