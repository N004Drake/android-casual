/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.crypto;

import CASUAL.crypto.SHA256sum;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class SHA256sumTest {

    public SHA256sumTest() {
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
    String expResult = "ad5f9292c7bd44068b5465b48b38bf18c98b4d133e80307957e5f5c372a36f7d  logo.xcf";
    String expname = "logo.xcf";
    String expsum = "ad5f9292c7bd44068b5465b48b38bf18c98b4d133e80307957e5f5c372a36f7d";
    File file = new File("../logo.xcf");

    /**
     * Test of getLinuxSum method, of class SHA256sum.
     */
    @Test
    public void testGetLinuxSum() {
        System.out.println("getLinuxSum");
        String result = SHA256sum.getLinuxSum(file);
        //TODO get proper result
        //assertEquals(expsum,result);

    }

    /**
     * Test of getName method, of class SHA256sum.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String sha256sum = expResult;

        String result = SHA256sum.getName(sha256sum);
        assertEquals(expname, result);
    }

    @Test
    public void testString() {

        System.out.println("Testing vectors from http://www.nsrl.nist.gov/testdata/");
        String result = "";


        String vector = "BA7816BF8F01CFEA414140DE5DAE2223B00361A396177A9CB410FF61F20015AD".toLowerCase();
        String expectedresult = "BA7816BF8F01CFEA414140DE5DAE2223B00361A396177A9CB410FF61F20015AD".toLowerCase();
        try {
            result = new SHA256sum("abc").getSha256();
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(SHA256sumTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        expectedresult = vector;
        assertEquals(expectedresult, result);
        System.out.println(result);

        vector = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
        expectedresult = "248D6A61D20638B8E5C026930C3E6039A33CE45964FF2167F6ECEDD419DB06C1".toLowerCase();
        try {
            result = new SHA256sum(vector).getSha256();
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(SHA256sumTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(expectedresult, result);
        System.out.println(result);
        System.out.println("Generating 1,000,000 a's");
        vector = "";
        for (int i = 0; i < 1000; i++) {
            //takes 30seconds to generate one-at-a-time, 1 second to generate 1,000 at a time.
            vector = vector + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        }
        expectedresult = "CDC76E5C9914FB9281A1C7E284D73E67F1809A48A497200E046D39CCC7112CD0".toLowerCase();
        try {
            result = new SHA256sum(vector).getSha256();
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(SHA256sumTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(expectedresult, result);
        System.out.println(result);


    }

    /**
     * Test of getSum method, of class SHA256sum.
     */
    @Test
    public void testGetSum() {
        System.out.println("getSum");
        String result = SHA256sum.getSum(expResult);
        assertEquals(expsum, result);
    }

    /**
     * Test of bytesToHex method, of class SHA256sum.
     */
    @Test
    public void testBytesToHex() {
        System.out.println("bytesToHex");
        byte[] bytes = {-47, 1, 16, 84, 2, 101, 110, 83, 111, 109, 101, 32, 78, 70, 67, 32, 68, 97, 116, 97};
        String result = SHA256sum.bytesToHex(bytes);
        assertEquals("D101105402656E536F6D65204E46432044617461".toLowerCase(), result);
    }
}