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
import java.util.ArrayList;

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

    CASPACData(BufferedInputStream BIS) {
        String packdata = "";
        try {
            while (BIS.available() > 0) {
                packdata = packdata + (char) BIS.read();
            }
        } catch (IOException ex) {
            new Log().errorHandler(ex);

        }
        setValues(packdata);
    }

    CASPACData(String string) {
        setValues(string);
    }

    private void setValues(String script) {
        String[] scriptIdentificationString = script.split("\n");

        for (int n = 0; n < scriptIdentificationString.length; n++) { //parse each line

            String[] splitID = scriptIdentificationString[n].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            splitID[0] = StringOperations.removeLeadingSpaces(splitID[0]);
            if (scriptIdentificationString[n].startsWith("ID")) {
                String id = StringOperations.removeLeadingSpaces(splitID[0].replaceFirst("ID", ""));
                uniqueIdentifier = id;
            } else if (scriptIdentificationString[n].startsWith("R")) { //script revision
                scriptRevision = splitID[1].replaceAll(" ", "");
            } else if (scriptIdentificationString[n].startsWith("CASUAL") || splitID[0].startsWith("SVN")) { // CASUAL revision 
                minSVNRevision = splitID[1].replaceAll(" ", "");
                this.isOurSVNHighEnoughToRunThisScript(Integer.parseInt(minSVNRevision));
            } else if (scriptIdentificationString[n].startsWith("URL")) { //Support URL
                String URL = StringOperations.removeLeadingSpaces(scriptIdentificationString[n].replaceFirst("URL", ""));
                supportURL = URL;
            } else if (scriptIdentificationString[n].startsWith("Message")) { // Update message expling why it was updated
                String message = StringOperations.removeLeadingSpaces(scriptIdentificationString[n].replaceFirst("Message", ""));
                updateMessage = message;
            } else { //add to array and assume its an MD5 to be parsed later
                md5s.add(StringOperations.removeLeadingSpaces(scriptIdentificationString[n]));

            }

        }
    }
    
    //TODO: remove CASUAL Webversion versus LocalVersion checks from CASPACHandler. 
    //    private void exitIfSVNRevisionIsNotHighEnough() {
    // this is no longer needed
    
    /**
     * isOurSVNHighEnoughToRunThisScript 
     * @param thisVerison
     * @return True if SVN is greater than required by script. 
     */
    public boolean isOurSVNHighEnoughToRunThisScript(int scriptVersion){
       
        int mySVNVersion = Integer.parseInt(java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision"));
        new Log().level3Verbose("Checking my revision:" +mySVNVersion +" against Script:"+scriptVersion +" to verify we are compatible to run.");
        if (mySVNVersion < scriptVersion){
            new Log().level0Error("Improper version detected CASUAL cannot continue\n CASUAL Revison " + mySVNVersion + " is not new enough to run\nthis script which requires Revision "+scriptVersion );
            new CASUALInteraction().showActionRequiredDialog("CASUAL cannot continue and must be updated.\n");
            // System.exit(0);
            return false;
        } else {
            new Log().level3Verbose("Revision check passed.");
            return true;
        }

    }
    public static String getSVNRevision(){
        return java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision");
    }
}
