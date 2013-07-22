/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;
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
 * @author adam
 * inspired by https://www.cigital.com/justice-league-blog/2009/08/14/proper-use-of-javas-securerandom/
 * inspired by http://stackoverflow.com/questions/1220751/how-to-choose-an-aes-encryption-mode-cbc-ecb-ctr-ocb-cfb
 * inspired by http://stackoverflow.com/questions/5531455/how-to-encode-some-string-with-sha256-in-java
 * inspired by Pulser
 */
public class CipherHandler {
    final File targetFile;
    CipherHandler(File targetFile){
        this.targetFile=targetFile;
    }
    
    
    public String encrypt(String output, String key){
        new Log().level2Information("Encrypting "+targetFile.getName());

        try {
            //key is infalated by 16 random characters A-Z,a-z,0-9
            //16 digits are used for ivSpec
            key=key+randomCharGen(key, 16);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CipherHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        new Log().level2Information("obtaining key...");
        doIt(output,key,Cipher.ENCRYPT_MODE);
        //key is returned.
        return key;
    }
    public String decrypt(String output, String key){
        new Log().level2Information("Decrytping "+targetFile.getName());
        //filename is returned
        new Log().level2Information("parsing key...");
        return doIt(output,key,Cipher.DECRYPT_MODE);
    }
    private String doIt(String output, String key,int mode){
        
        Cipher c;
        try {
            
            String ivspecString = key.substring(key.length() - 16);
            //key=key.substring(0, 16);
            byte iv[]=ivspecString.getBytes();
            SecretKeySpec skey=new SecretKeySpec(oneWayHash(key),"AES");
            IvParameterSpec ivspec=new IvParameterSpec(iv);
            c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(mode, skey, ivspec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            new Log().errorHandler(ex);
            return null;
        }
        try {
            new Log().level2Information("Key parsed.  Encrypting...");
            InputStream fis=new FileInputStream(targetFile);
            CipherInputStream cis = new CipherInputStream(fis,c);
            new FileOperations().writeStreamToFile(new BufferedInputStream(cis), output);
            new Log().level2Information("Encryption operation complete.");

            return output;
            
        } catch (IOException ex) {
            new Log().errorHandler(ex);
            return null;
        }
    }    
    
    private String randomCharGen(String key, int numberOfChars) throws NoSuchAlgorithmException{
        String alphabet = "!@#$%^&*()_+,./:0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int len = alphabet.length();
        System.out.println("Generating randomness");
        SecureRandom random = new SecureRandom(SecureRandom.getSeed(key.length()));
        byte bytes[] = new byte[numberOfChars];
        random.nextBytes(bytes);
        String retval="";
            for (int i = 0; i < numberOfChars; i++) {
                retval=retval+alphabet.charAt(random.nextInt(len));
                random=new SecureRandom();
            }
        return retval;
    }
    
    public byte[] oneWayHash(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            return hash;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CipherHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CipherHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}