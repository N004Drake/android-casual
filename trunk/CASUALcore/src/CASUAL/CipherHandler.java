/*CipherHandler provides a way to encrypt and decrypt given a password
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author adam inspired by
 * http://stackoverflow.com/questions/8674018/pbkdf2-with-bouncycastle-in-java
 * inspired by
 * https://www.cigital.com/justice-league-blog/2009/08/14/proper-use-of-javas-securerandom/
 * inspired by
 * http://stackoverflow.com/questions/1220751/how-to-choose-an-aes-encryption-mode-cbc-ecb-ctr-ocb-cfb
 * severely beaten several times by Pulser
 */
public class CipherHandler {

    final File targetFile;
    Log log = new Log();

    /*these variables are used for generating a header 
     *"EncryptedCASPAC-CASUAL-Revision3999" where
     * 3 represents then number of digits in the revision
     */
    final static private String revision = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision");
    final static private String casualID = "EncryptedCASPAC-CASUAL-Revision";
    private static String header = casualID + revision.length() + revision;

    public CipherHandler(File targetFile) {
        this.targetFile = targetFile;
    }

    /**
     * encrypts a file to the output file. Appends CASPAC Header
     *
     * @param output string location of file output
     * @param key password
     * @return true if encryption was sucessful
     */
    public boolean encrypt(String output, char[] key) {
        new Log().level2Information("Encrypting " + targetFile.getName());

        try {
            //key is infalated by 16 random characters A-Z,a-z,0-9
            //16 digits are used for ivSpec
            byte[] randomness = secureRandomCharGen(key, 16);
            new Log().level2Information("Key parsed.  Encrypting...");
            InputStream fis = new FileInputStream(targetFile);
            List<InputStream> streams = Arrays.asList(
                    new ByteArrayInputStream(randomness),
                    fis);
            new Log().level2Information("obtaining key...");
            InputStream is = new SequenceInputStream(Collections.enumeration(streams));
            writeCipherFile(is, randomness, output, key, Cipher.ENCRYPT_MODE);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IOException ex) {
            return false;
        }
        //key is returned.
        return true;
    }

    /**
     * decrypts a file
     *
     * @param output string name of file to output
     * @param key password issued by encrytper
     * @return name of file written, null if error
     */
    public String decrypt(String output, char[] key) throws Exception {
        try {
            FileInputStream fis = new FileInputStream(targetFile);
            int headersize = getCASPACHeaderLength(targetFile);
            if (headersize < 10) {
                throw new Exception("Invalid CASPAC Format");
            }

            fis.read(new byte[headersize]);
            byte[] IV = new byte[16];
            fis.read(IV);
            return writeCipherFile(fis, IV, output, key, Cipher.DECRYPT_MODE);
        } catch (IOException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException ex) {
            return null;
        }
    }

    private InputStream appendStream(InputStream appendToFront, InputStream is) {
        List<InputStream> streams = Arrays.asList(
                appendToFront, is);
        InputStream newis = new SequenceInputStream(Collections.enumeration(streams));
        return newis;
    }

    private String writeCipherFile(InputStream fis, byte[] iv, String output, char[] key, int mode) throws NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, FileNotFoundException, IOException {
        byte[] bkey = oneWayHash(key);
        char[] newKey = new char[bkey.length];
        for (int i = 0; i < bkey.length; i++) {
            newKey[i] = (char) bkey[i];
        }
        Cipher c = getCipher(newKey, iv, mode);
        CipherInputStream cis = new CipherInputStream(fis, c);

        if (mode == Cipher.ENCRYPT_MODE) {
            InputStream headerbytes = new ByteArrayInputStream(header.getBytes());
            InputStream doOutput = appendStream(headerbytes, (InputStream) cis);
            new FileOperations().writeStreamToFile(new BufferedInputStream(doOutput), output);
        } else {
            new FileOperations().writeStreamToFile(new BufferedInputStream(cis), output);
        }
        return output;
    }

    private byte[] secureRandomCharGen(char[] key, int numberOfChars) throws NoSuchAlgorithmException {
        String alphabet = "!@#$%^&*()_+,./:0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int len = alphabet.length();
        log.level4Debug("Generating randomness");
        SecureRandom random = new SecureRandom(SecureRandom.getSeed(key.length));
        byte bytes[] = new byte[numberOfChars];
        random.nextBytes(bytes);  //burn some bits
        String retval = "";
        for (int i = 0; i < numberOfChars - 1; i++) {
            bytes[i] = (byte) alphabet.charAt(random.nextInt(len));
            retval = retval + alphabet.charAt(random.nextInt(len));

        }
        return bytes;
    }

    /**
     * provides a one-way hash on a password
     *
     * @param input your password
     * @return PBKDF2 with HMAC SHA1 password
     */
    public byte[] oneWayHash(char[] input) {
        try {

            int maxSecurity = Cipher.getMaxAllowedKeyLength("AES");
            log.level4Debug("The maximum security allowed on this system is AES " + maxSecurity);
            if (maxSecurity > 128) {
                maxSecurity = 128;
            }
            log.level4Debug("For the sake of compatibility with US Import/Export laws we are using AES " + maxSecurity);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keyspec = new PBEKeySpec(input, "--salt--".getBytes(), 100000, maxSecurity);
            Key key = factory.generateSecret(keyspec);
            return key.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(CipherHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Cipher getCipher(char[] key, byte[] iv, int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        SecretKeySpec skey = new SecretKeySpec(oneWayHash(key), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(mode, skey, ivspec);
        return c;
    }

    /**
     * will return the length of the CASPAC Header
     *
     * @param f
     * @return 0 if failed, will be between >18 if valid.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static int getCASPACHeaderLength(File f) throws FileNotFoundException, IOException {
        CipherHandler c = new CipherHandler(f);
        FileInputStream fis = new FileInputStream(f);
        byte[] chartest = new byte[casualID.length()];
        byte[] headert = casualID.getBytes();
        fis.read(chartest);
        //read length of revision
        if (Arrays.equals(chartest, headert)) {
            char charRevisionLength = (char) fis.read();
            int revisionLength = Integer.parseInt(String.valueOf(charRevisionLength));
            return chartest.length + 1 + revisionLength;
        }
        return 0;
    }
}