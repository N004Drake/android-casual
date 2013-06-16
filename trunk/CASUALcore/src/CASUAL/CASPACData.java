/*CASPACData contains a datstructure for CASPAC format
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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public class CASPACData {

    public String minSVNRevision="0";
    public String scriptRevision="0";
    public String uniqueIdentifier="0";
    public String supportURL="";
    public String updateMessage="";
    public ArrayList<String> md5s = new ArrayList<>();

    Properties prop;
    CASPACData(String propString) {
        prop= load(propString);
        setValues();
    }

    private void setValues() {
        minSVNRevision = prop.getProperty("CASUAL.minSVN");
        scriptRevision = prop.getProperty("Script.Revision");
        uniqueIdentifier = prop.getProperty("Script.ID");
        supportURL = prop.getProperty("Script.SupportURL");
        updateMessage = prop.getProperty("Script.UpdateMessage");
        int md5ArrayPosition=0;
        System.out.print("Script.MD5["+md5ArrayPosition+"]");
        String md5;
        while ((md5=prop.getProperty("Script.MD5["+md5ArrayPosition+"]"))!=null){
            md5s.add(md5);
            md5ArrayPosition++;
        }
    }

    /**Gets the SVN revision
     *
     * @return SVN representation of the SVN revision
     */
    public static String getSVNRevision(){
        return java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision");
    }
    
    /** Checks to verify that SVN revision is high enough to run this script
     * isOurSVNHighEnoughToRunThisScript 
     * @param scriptVersion is the MINSVN required from the script attempting to load
     * @return True if SVN is greater than required by script. 
     */
    public boolean isOurSVNHighEnoughToRunThisScript(int scriptVersion){
        int mySVNVersion = Integer.parseInt(getSVNRevision());
        new Log().level3Verbose("Checking my revision:" +mySVNVersion +" against Script:"+scriptVersion +" to verify we are compatible to run.");
        if (mySVNVersion < scriptVersion){
            new Log().level0Error("Improper version detected CASUAL cannot continue\n CASUAL Revison " + mySVNVersion + " is not new enough to run\nthis script which requires Revision "+scriptVersion );
            return false;
        } else {
            new Log().level3Verbose("Revision check passed.");
            return true;
        }

    }

    /** loads properties from a string 
     * @param propertiesString string containing properties
     * @return Properties object
     */
    private Properties load(String propertiesString) {
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(propertiesString));
        } catch (IOException ex) {
            Logger.getLogger(CASPACData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return properties;
    }
}
