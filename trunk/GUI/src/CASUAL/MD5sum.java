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

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Inspired by R.J. Lorimer http://www.javalobby.org/java/forums/t84420.html
 */
public class MD5sum {
    private String[][] baselineMD5=null;
    private String[][] downloadedMD5=null;
    
    public boolean compareFileToMD5(File f, String MD5) throws NoSuchAlgorithmException, FileNotFoundException {
        if (md5sum(f).equals(MD5)) {
            return true;
        } else {
            return false;
        }
    }

    public String md5sum(File f) throws NoSuchAlgorithmException, FileNotFoundException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        InputStream is = new FileInputStream(f);
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
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
            }
        }
    }
    public boolean compareMD5StringsFromLinuxFormatToFilenames(String[] LinuxFormat, String[] MD5Filenames){
        String[][] FilenamesAndMD5=splitFilenamesAndMD5(LinuxFormat);
        boolean[] matches=new boolean[MD5Filenames.length];
        for (int n=0; n<MD5Filenames.length; n++){ //loop through files
            matches[n]=false; //set match as false by default
            try {
                String md5=md5sum(new File(MD5Filenames[n]));// get MD5 for current file
                for (int nn=0; nn<FilenamesAndMD5[0].length; nn++){ //find MD5 in lookup table
                    if (md5.equals(FilenamesAndMD5[n][1])) { //if md5 is found while looping through lookup table set match true
                        matches[n]=true;
                    } else if (md5.length()!=16){ //or if it is not an actual MD5 set as true;
                        matches[n]=true;
                    }
                    
                }
            } catch (NoSuchAlgorithmException ex) {
                System.out.println("NoSuchAlgorythem Exception while parsing " +MD5Filenames[n] + " in compareMD5StringsFromLinuxFormatToFilenames");
            } catch (FileNotFoundException ex) {
                System.out.println("FileNotFound Exception while parsing " +MD5Filenames[n]+ " in compareMD5StringsFromLinuxFormatToFilenames");
            }
            
        }
        for (int n=0; n<matches.length; n++){ //loop through all values
            if (matches[n]==false) return false; //if all values don't match, return false
        }
        
        return true;
    }
    private String[][] splitFilenamesAndMD5(String[] idStrings) {
        final int ROWS=2; 
        int COLUMNS=idStrings.length;
        final String[][] NameMD5=new String[ROWS][COLUMNS];
        for (int n=0; n<COLUMNS; n++){
               String[] splitID=idStrings[n].split("  ");
               if (splitID.length==2){
                       if ((splitID[0]!=null) && (splitID[1]!=null)){
                           NameMD5[n][0]="splitID[0]";
                           NameMD5[n][1]="splitID[1]";
                           //this is a valid MD5 split
                       } else {
                           //spoof empty string
                           NameMD5[n][0]="";
                           NameMD5[n][1]="";
                       }
        
               } else {
               //spoof empty string;
                    NameMD5[n][0]="";
                    NameMD5[n][1]="";
               }
        }
        return NameMD5;        
    }
}
