/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class CipherHandlerTest {
    
    public CipherHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of encrypt method, of class CipherHandler.
     */
    @Test
    public void testEncrypt() {
        String input="../../CASPAC/testpak.zip";
        String output = "../../CASPAC/testpak.enc.zip";
        String finalOutput="../../CASPAC/testpak.zip";
        File inputFile=new File(input);
        CipherHandler instance = new CipherHandler(inputFile);
        for (int i=0; i<10; i++){
            System.out.println("encrypt");
            String key = "testatesttestatestatestatest"+i;
            MD5sum md5=new MD5sum();
            String originalMD5=md5.md5sum(inputFile);
            //encrypt file
            assertEquals(true,instance.encrypt(output, key.toCharArray()));
            //decrypt file
            File encryptedFile=new File(output);
            CipherHandler instance2 = new CipherHandler(encryptedFile);
            System.out.println("Encrypted MD5:"+md5.getLinuxMD5Sum(encryptedFile));
            String result2="";
            try {
                result2 = instance2.decrypt(finalOutput, key.toCharArray());
            } catch (Exception ex) {
                Logger.getLogger(CipherHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(result2);
            String newMD5=md5.md5sum(new File(finalOutput));
            assertEquals(originalMD5,newMD5);
        }
        
    }
    
   
 
}