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
        System.out.println("encrypt");
        String input="../../CASPAC/testpak.zip";
        String output = "../../CASPAC/testpak.enc.zip";
        String key = "hi";
        CipherHandler instance = new CipherHandler(new File(input));
        String result = instance.encrypt(output, key);
        System.out.println("Your key is:"+result+"\nDecrypting...");
        CipherHandler instance2 = new CipherHandler(new File(output));
        String result2=instance2.decrypt(input+".zip", result);
        System.out.println(result2);
        assertEquals("../../CASPAC/testpak.zip.zip",result2);
        // TODO review the generated test code and remove the default call to fail.
       //fail("The test case is a prototype.");
    }
    
   
 
}