/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.archiving;

import CASUAL.archiving.Odin;
import CASUAL.archiving.OdinFile;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class OdinTest {

    public OdinTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        odinFile = new OdinFile("../test/CASUAL/archiving/resources/CF-Auto-Root-gd1-gd1xx-ekgc100.tar.md5");
        filesList = odinFile.extractOdinContents(CASUAL.Statics.getTempFolder());
        pitFile = new File("../test/CASUAL/archiving/resources/ekgc100part.pit");
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
    static OdinFile odinFile;
    static File[] filesList;
    static File pitFile;

    /**
     * Test of getHeimdallCommand method, of class HeimdallFormatter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetHeimdallCommand() throws Exception {
        System.out.println("getFlashFilesCommand");
        File[] fileList = filesList;

        Odin instance = new Odin("heimdall");
        String[] expResult = new String[]{"heimdall","flash", "--RECOVERY", "", "--CACHE",""};

        String[] result = instance.getFlashFilesCommand(pitFile, fileList);
        assertEquals (expResult.length,result.length );
        assertEquals (expResult[0],result[0] );
        assertEquals (expResult[1],result[1] );
        assertEquals (expResult[2],result[2] );
        assertEquals (expResult[4],result[4] );
    }

}
