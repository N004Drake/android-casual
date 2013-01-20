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

/*
 * Inspired by R.J. Lorimer http://www.javalobby.org/java/forums/t84420.html
 */
public class MD5sum {

    public static boolean compareFileToMD5(File f, String MD5) throws NoSuchAlgorithmException, FileNotFoundException {
        if (md5sum(f).equals(MD5)) {
            return true;
        } else {
            return false;
        }
    }

    public static String md5sum(File f) throws NoSuchAlgorithmException, FileNotFoundException {
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
}
