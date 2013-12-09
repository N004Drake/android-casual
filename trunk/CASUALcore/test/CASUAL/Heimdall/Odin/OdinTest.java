/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.Heimdall.Odin;

import CASUAL.Heimdall.Odin.Odin;
import CASUAL.Heimdall.Odin.OdinFile;
import CASUAL.Statics;
import java.io.File;
import java.io.FileNotFoundException;
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
        pitFile = new File("../test/CASUAL/archiving/resources/ekgc100part.pit");

        filesList = odinFile.extractOdinContents(CASUAL.Statics.getTempFolder());
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

    /**
     * Test of reset method, of class Odin.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        Odin instance = new Odin("heimdall");
        instance.flashBootloader=true;
        instance.reset();
        assertEquals(instance.flashBootloader,false);

    }

    /**
     * Test of getHeimdallCommandWithOdinParameters method, of class Odin.
     */
    @Test
    public void testGetHeimdallCommandWithOdinParameters() throws Exception {
        System.out.println("getHeimdallCommandWithOdinParameters");
        Odin instance = new Odin("heimdall");
        String[] expResult = null;
        String[] result = instance.getHeimdallCommandWithOdinParameters();
        assertArrayEquals(expResult, result);

    }

    /**
     * Test of getFlashFilesCommand method, of class Odin.
     */
    @Test
    public void testGetFlashFilesCommand() throws Exception {
        System.out.println("getFlashFilesCommand");
        
        File[] filesToFlash = odinFile.extractOdinContents(Statics.getTempFolder());
        Odin instance = new Odin("heimdall");
        String[] expResult = new String[]{"heimdall","flash","--RECOVERY","","--CACHE"};
        String[] result = instance.getFlashFilesCommand(pitFile, filesToFlash);
        assertEquals(expResult[0],result[0]);
        assertEquals(expResult[1],result[1]);
        assertEquals(expResult[2],result[2]);
        assertEquals(expResult[4],result[4]);
        
    }

    /**
     * Test of addFlashFiles method, of class Odin.
     */
    @Test
    public void testAddFlashFiles() throws Exception {
        System.out.println("addFlashFiles");
        String[] ExistingCommand = new String[]{"flash"};
        
        File[] filesToFlash = odinFile.extractOdinContents(Statics.getTempFolder());
        Odin instance = new Odin("heimdall");
        String expResult = "--RECOVERY";
        String[] result = instance.addFlashFiles(ExistingCommand, pitFile, filesToFlash);
        assertEquals(expResult, result[1]);

    }

    /**
     * Test of addRepartitionCommand method, of class Odin.
     */
    @Test
    public void testAddRepartitionCommand() throws Exception {
        System.out.println("addRepartitionCommand");
        String[] ExistingCommand = new String[]{"heimdall","flash"};

        Odin instance = new Odin("heimdall");
        String[] expResult = null;
        String[] result = instance.addRepartitionCommand(ExistingCommand, pitFile);
        //TODO implement testing on this method.  

    }

    /**
     * Test of getPitFile method, of class Odin.
     */
    @Test
    public void testGetPitFile() {
        System.out.println("getPitFile");
        Odin instance = new Odin("heimdall");
        try {
          File result = instance.getPitFile();
        } catch (FileNotFoundException ex){

            //todo implement testing here
        }
    }
    
  

}
