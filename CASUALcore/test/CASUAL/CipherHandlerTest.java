/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.File;
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
        
        for (int i=0; i<10; i++){
            System.out.println("encrypt");
            String input="../../CASPAC/testpak.zip";
            String output = "../../CASPAC/testpak.enc.zip";
            String key = "testatesttestatestatestatest";
            MD5sum md5=new MD5sum();
            File f=new File(input);
            String originalMD5=md5.md5sum(f);
            CipherHandler instance = new CipherHandler(f);
            String result = instance.encrypt(output, key);
            System.out.println("Your key is:"+result+"\nDecrypting...");
            CipherHandler instance2 = new CipherHandler(new File(output));
            String result2=instance2.decrypt(input+".zip", result);
            System.out.println(result2);
            String newMD5=md5.md5sum(new File(input+".zip"));
            assertEquals(originalMD5,newMD5);
        }
        
    }
    
   
 
}