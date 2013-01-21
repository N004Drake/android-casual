/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

/**
 *
 * @author adam
 */
public class CASUALIDString {
    public String[] md5sums;
    public String[] metaData;
    
    public String[] getMetaData(){
        return metaData;
    }
    public String[] getmd5sums(){
        return md5sums;
    }
    public void setMetaDataFromIDString(String[] scriptIdentificationString) {
        md5sums=new String[scriptIdentificationString.length-5];
        metaData = new String[scriptIdentificationString.length];
        int md5Counter=0;
        for (int n=0; n<scriptIdentificationString.length;n++){ //parse each line
           
            String[] splitID = scriptIdentificationString[n].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            splitID[0]=StringOperations.removeLeadingSpaces(splitID[0]); 
            if (scriptIdentificationString[n].startsWith("ID")) {
                String id = StringOperations.removeLeadingSpaces(splitID[0].replaceFirst("ID", ""));
                metaData[0] = id;
            } else if (scriptIdentificationString[n].startsWith("R")) { //script revision
                metaData[1] = splitID[1].replaceAll(" ", "");
            } else if (scriptIdentificationString[n].startsWith("CASUAL") || splitID[0].startsWith("SVN")) { // CASUAL revision 
                metaData[2] = splitID[1].replaceAll(" ", "");
            } else if (scriptIdentificationString[n].startsWith("URL")) { //Support URL
                String URL = StringOperations.removeLeadingSpaces(scriptIdentificationString[n].replaceFirst("URL", ""));
                metaData[3] = URL;
            } else if (scriptIdentificationString[n].startsWith("Message")) { // Update message expling why it was updated
                String message = StringOperations.removeLeadingSpaces(scriptIdentificationString[n].replaceFirst("Message", ""));
                metaData[4] = message;
            } else { //add to array and assume its an MD5 to be parsed later
                md5sums[md5Counter]=StringOperations.removeLeadingSpaces(scriptIdentificationString[n]);
                md5Counter++;
            }
      
        }
    }
}
