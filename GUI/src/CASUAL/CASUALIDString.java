/*CASUALIDString provides a method of identifying a datastructure for meta
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
public class CASUALIDString {

    public String[] md5sums;
    public String[] metaData;

    public String[] getMetaData() {
        return metaData;
    }

    public String[] getmd5sums() {
        return md5sums;
    }

    public void setMetaDataFromIDString(String[] scriptIdentificationString) {
        md5sums = new String[scriptIdentificationString.length - 5];
        metaData = new String[scriptIdentificationString.length];
        int md5Counter = 0;
        for (int n = 0; n < scriptIdentificationString.length; n++) { //parse each line

            String[] splitID = scriptIdentificationString[n].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            splitID[0] = StringOperations.removeLeadingSpaces(splitID[0]);
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
                md5sums[md5Counter] = StringOperations.removeLeadingSpaces(scriptIdentificationString[n]);
                md5Counter++;
            }

        }
    }
}
