/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.heimdall.odin;

import CASUAL.communicationstools.heimdall.odin.OdinFile;
import CASUAL.Statics;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class OdinFileTest {

    public OdinFileTest() {
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
     * Test of open method, of class OdinDecompressor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testOpen() throws Exception {
        System.out.println("open");
        File F = new File(".");
        OdinFile instance = new OdinFile("../test/CASUAL/network/CASUALDevIntegration/resources/CF-Auto-Root-gd1-gd1xx-ekgc100.tar.md5");
        File[] result = instance.extractOdinContents(Statics.getTempFolder());
        assert (result.length == 2);
        //test successive calls
        result = instance.extractOdinContents(Statics.getTempFolder());
        assert (result.length == 2);

    }

}
