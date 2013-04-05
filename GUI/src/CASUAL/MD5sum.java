/*MD5sum provides MD5 tools for use in CASUAL
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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/*
 * Inspired by R.J. Lorimer http://www.javalobby.org/java/forums/t84420.html
 */
public class MD5sum {

    public boolean compareFileToMD5(File f, String MD5) {
        if (md5sum(f).equals(MD5)) {
            return true;
        } else {
            return false;
        }
    }

    public String md5sum(InputStream is) {
        return md5sumStream(is);//TODO: return stream
    }

    public String md5sum(File f) {
        InputStream is;
        try {
            is = new FileInputStream(f);
            return md5sumStream(is);
        } catch (FileNotFoundException ex) {
            return "ERROR0FileNotFoundException00000";
        }
    }

    public String md5sumStream(InputStream is) {

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[8192];
            int read;
            try {
                while ((read = is.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
                byte[] md5sum = digest.digest();
                BigInteger bigInt = new BigInteger(1, md5sum);
                String output = bigInt.toString(16);
                return output;

            } finally {
                is.close();
            }

        } catch (NoSuchAlgorithmException ex) {
            return "ERROR0NoSuchAlgorythemException0";

        } catch (IOException ex) {
            return "ERROR00IOException00000000000000";
        }

    }

    public boolean compareMD5StringsFromLinuxFormatToFilenames(String[] LinuxFormat, String[] MD5Filenames) {
        String[][] FilenamesAndMD5 = splitFilenamesAndMD5(LinuxFormat);
        boolean[] matches = new boolean[MD5Filenames.length];
        for (int n = 0; n < MD5Filenames.length; n++) { //loop through files
            matches[n] = false; //set match as false by default
            String md5 = md5sum(new File(MD5Filenames[n]));// get MD5 for current file
            for (int nn = 0; nn < FilenamesAndMD5.length; nn++) { //find MD5 in lookup table
                if (md5.length() != 32) { //if md5 is found while looping through lookup table set match true
                    matches[n] = true;
                } else if (md5.equals(FilenamesAndMD5[nn][0])) { //or if it is not an actual MD5 set as true;
                    matches[n] = true;
                }

            }


        }
        for (int n = 0; n < matches.length; n++) { //loop through all values
            if (matches[n] == false) {
                return false; //if all values don't match, return false
            }
        }

        return true;
    }

    private String[][] splitFilenamesAndMD5(String[] idStrings) {
        final int ROWS = idStrings.length;
        int COLUMNS = 2;
        final String[][] NameMD5 = new String[ROWS][COLUMNS];
        for (int n = 0; n < ROWS; n++) {
            try {
                if (idStrings[n].contains("  ")) {
                    String[] splitID = idStrings[n].split("  ");
                    if (splitID.length == 2) {
                        if ((splitID[0] != null) && (splitID[1] != null)) {
                            NameMD5[n][0] = splitID[0];
                            NameMD5[n][1] = splitID[1];
                            //this is a valid MD5 split
                        } else {
                            //spoof empty string
                            NameMD5[n][0] = "";
                            NameMD5[n][1] = "";
                        }

                    } else {
                        //spoof empty string;
                        NameMD5[n][0] = "";
                        NameMD5[n][1] = "";
                    }
                }
            } catch (NullPointerException e) {
                continue;
            }
        }
        return NameMD5;
    }

    String md5sum(String string) {
        return md5sum(new File(string));

    }

    public String[] splitMD5String(String md5){
        return md5.split("  ");

    }
    public String makeMD5String(String md5, String filename) {
        return md5 + "  " + filename;
    }
    public boolean lineContainsMD5(String testLine){
        
       boolean x= testLine.matches("([0-9a-f]{32}([\\s\\S]*))");
       return x;
    }
    
    public String pickNewMD5fromArrayList(ArrayList list, String OldMD5){
        String[] md5FileSplit=OldMD5.split("  ");
        for (Object item:list.toArray()){
            if (((String)item).endsWith(md5FileSplit[1])){
                return (String)item;
            }

        }
        return OldMD5;
    }
    
}
