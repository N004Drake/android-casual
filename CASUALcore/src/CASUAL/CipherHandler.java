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

    CipherHandler(File targetFile) {
        this.targetFile = targetFile;
    }

    /**
     * encrypts a file to the output file
     *
     * @param output string location of file output
     * @param key password
     * @return modified version of password with random digits at the end, null
     * if error
     */
    public String encrypt(String output, String key) {
        new Log().level2Information("Encrypting " + targetFile.getName());

        try {
            //key is infalated by 16 random characters A-Z,a-z,0-9
            //16 digits are used for ivSpec
            byte[] randomness = randomCharGen(key, 16).getBytes();
            new Log().level2Information("Key parsed.  Encrypting...");
            InputStream fis = new FileInputStream(targetFile);
            List<InputStream> streams = Arrays.asList(
                    new ByteArrayInputStream(randomness),
                    fis);
            new Log().level2Information("obtaining key...");
            InputStream is = new SequenceInputStream(Collections.enumeration(streams));
            doIt(is, randomness, output, key, Cipher.ENCRYPT_MODE);
        } catch (NoSuchAlgorithmException | FileNotFoundException ex) {
            Logger.getLogger(CipherHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        //key is returned.
        return key;
    }

    /**
     * decrypts a file
     *
     * @param output string name of file to output
     * @param key password issued by encrytper
     * @return name of file written, null if error
     */
    public String decrypt(String output, String key) {
        new Log().level2Information("Decrytping " + targetFile.getName());
        //filename is returned
        new Log().level2Information("parsing key...");
        new Log().level2Information("Key parsed.  Encrypting...");
        FileInputStream fis = null;
        byte[] IV = new byte[0];
        try {
            fis = new FileInputStream(targetFile);
            IV = new byte[16];
            fis.read(IV);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CipherHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CipherHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return doIt(fis, IV, output, key, Cipher.DECRYPT_MODE);
    }

    private String doIt(InputStream fis, byte[] iv, String output, String key, int mode) {

        Cipher c;
        try {

            SecretKeySpec skey = new SecretKeySpec(oneWayHash(key), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(mode, skey, ivspec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            new Log().errorHandler(ex);
            return null;
        }
        try {
            CipherInputStream cis = new CipherInputStream(fis, c);
            new FileOperations().writeStreamToFile(new BufferedInputStream(cis), output);
            new Log().level2Information("Encryption operation complete.");

            return output;

        } catch (IOException ex) {
            new Log().errorHandler(ex);
            return null;
        }
    }

    private String randomCharGen(String key, int numberOfChars) throws NoSuchAlgorithmException {
        String alphabet = "!@#$%^&*()_+,./:0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int len = alphabet.length();
        System.out.println("Generating randomness");
        SecureRandom random = new SecureRandom(SecureRandom.getSeed(key.length()));
        byte bytes[] = new byte[numberOfChars];
        random.nextBytes(bytes);
        String retval = "";
        for (int i = 0; i < numberOfChars; i++) {
            retval = retval + alphabet.charAt(random.nextInt(len));
            random = new SecureRandom();
        }
        return retval;
    }

    /**
     * provides a one-way hash on a password
     *
     * @param input your password
     * @return PBKDF2 with HMAC SHA1 password
     */
    public byte[] oneWayHash(String input) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keyspec = new PBEKeySpec("password".toCharArray(), "00000000".getBytes(), 1000, 256);
            Key key = factory.generateSecret(keyspec);
            return key.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(CipherHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}