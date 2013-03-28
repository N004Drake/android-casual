/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public class CASUALModularPackData {

    public String minSVNRevision;
    public String scriptRevision;
    public String uniqueIdentifier;
    public String supportURL;
    public String updateMessage;
    public ArrayList md5s=new ArrayList();

    CASUALModularPackData(BufferedInputStream BIS) {
        String packdata = "";
        try {
            while (BIS.available() > 0) {
                packdata=packdata+(char)BIS.read();
            }
        } catch (IOException ex) {
            Logger.getLogger(CASUALModularPackData.class.getName()).log(Level.SEVERE, null, ex);
        }
        setValues(packdata);
    }

    CASUALModularPackData(String string) {
        setValues(string);
    }
    
    private void setValues(String script){
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
    
}
