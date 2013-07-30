/*MD256sum provides several methods for md256 standards
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
package CASUAL.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author adam
 */
/**
 * attempts to replicates linux's sha256sum..  there appears to be a problem with
 * *Linux* when tested against test vectors from this page: http://www.nsrl.nist.gov/testdata/
 * I will need to review all data and figure out how to implement this later
 * 
 * This will need to be examined further. 
 * adam@adam-desktop:~/code/android-casual/trunk/CASUALcore$ sha256sum build.xml
 * b34f8085b81991bfa95b872fb69fb25ec8041e220a0184093dbc9dee10edac48 build.xml
 *
 * ad5f9292c7bd44068b5465b48b38bf18c98b4d133e80307957e5f5c372a36f7d logo.xcf
 * @author adam
 */
public class SHA256sum {

    final ByteArrayInputStream toBeSHA256;
    final protected static String LINUXSPACER = "  ";

    public SHA256sum(String s) throws IOException {
        ByteArrayInputStream bas = new ByteArrayInputStream(s.getBytes());
        toBeSHA256 = bas;
        toBeSHA256.mark(0);
    }

    public SHA256sum(InputStream is) throws IOException {

        byte[] buff = new byte[8120];
        int bytesRead;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        while ((bytesRead = is.read(buff)) != -1) {
            bao.write(buff, 0, bytesRead);
        }
        ByteArrayInputStream bin = new ByteArrayInputStream(bao.toByteArray());
        toBeSHA256 = bin;
        toBeSHA256.mark(0);
    }

    public SHA256sum(File f) throws FileNotFoundException, IOException {

        RandomAccessFile ra;
        ra = new RandomAccessFile(f, "rw");
        byte[] b = new byte[(int) f.length()];
        ra.read(b);
        ByteArrayInputStream bas = new ByteArrayInputStream(b);
        toBeSHA256 = bas;
        toBeSHA256.mark(0);
    }
    public String getLinuxSum(String filename){
        if (filename.isEmpty()) {
            filename="-";
        }
        try {
            String sha=getSha256();
            return sha+LINUXSPACER+filename;
        } catch (IOException ex) {
            return null;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
        
        
        
    }
    

    public static String getLinuxSum(File file) {
        String name = file.getName();
        String sum;

        try {
            sum = new SHA256sum(file).getSha256();
            String linuxSHA256;
            linuxSHA256 = formatLinuxOutputSHA256Sum(sum, name);
            return linuxSHA256;
        } catch (IOException | NoSuchAlgorithmException ex) {
            return "";
        }
    }

    public static String getName(String sha256sum) {
        if (sha256sum.contains(LINUXSPACER)) {
            String[] split = sha256sum.split(LINUXSPACER);
            return split[1];
        }
        return "";
    }

    public static String getSum(String sha256sum) {
        if (sha256sum.contains(LINUXSPACER)) {
            String[] split = sha256sum.split(LINUXSPACER);
            return split[0];
        }
        return "";
    }

    /**
     * does the SHA256
     *
     * @return hex string representation of the input
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public String getSha256() throws IOException, NoSuchAlgorithmException {
      toBeSHA256.reset();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[8192];
            int read;
            while ((read = toBeSHA256.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            while (output.length() != 64) {
                output = "0" + output;
            }
            return output;
        } catch (NoSuchAlgorithmException ex) {
            return "ERROR0NoSuchAlgorythemException0";

        } catch (IOException ex) {
            return "ERROR00IOException00000000000000";
        }
    }

    public static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for(byte b : bytes) {
        sb.append(String.format("%02x", b));
    }
    return sb.toString();
    }

    public static String formatLinuxOutputSHA256Sum(String sum, String name) {
        String linuxSHA256;
        linuxSHA256 = sum + LINUXSPACER + name;
        return linuxSHA256;
    }
}
