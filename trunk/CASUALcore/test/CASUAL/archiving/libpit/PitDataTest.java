/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CASUAL.archiving.libpit;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adamoutler
 */
public class PitDataTest {
    
    public PitDataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
            pitFile=new File("../test/CASUAL/network/CASUALDevIntegration/resources/sch-i535-32gb.pit");
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
    static File pitFile;



    /**
     * Test of pack method, of class PitData.
     */
    @Test
    public void testPack(){
        try {
            System.out.println("pack");
            DataOutputStream dataOutputStream = null;
            String testFile=CASUAL.Statics.getTempFolder()+"test.pit";
            PitData instance = new PitData(pitFile);
            instance.pack(new DataOutputStream(new FileOutputStream(testFile)));
            System.out.println("packed " +testFile);
            PitData test=new PitData(new File(testFile));
            assert(test.matches(instance));
            String s=instance.getEntry(0).getFilename();
            assert(s.equals("NON-HLOS.bin"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of matches method, of class PitData.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        testPack();
    }

    /**
     * Test of clear method, of class PitData.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        PitData instance = new PitData();
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        instance.clear();
        assert(instance.getEntryCount()==0);
    }

    /**
     * Test of getEntry method, of class PitData.
     */
    @Test
    public void testGetEntry(){
        System.out.println("getEntry");
        PitData instance = new PitData();
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        String test=instance.getEntry(0).getFilename();
        assert(test.equals("NON-HLOS.bin"));
        test=instance.getEntry(1).getFilename();
        assert(test.equals("sbl1.mbn"));
        test=instance.getEntry(2).getFilename();
        assert(test.equals("sbl2.mbn"));
        test=instance.getEntry(3).getFilename();
        assert(test.equals("sbl3.mbn"));
        test=instance.getEntry(4).getFilename();
        assert(test.equals("aboot.mbn"));
        test=instance.getEntry(5).getFilename();
        assert(test.equals("rpm.mbn"));
        test=instance.getEntry(6).getFilename();
        assert(test.equals("boot.img"));
        test=instance.getEntry(7).getFilename();
        assert(test.equals("tz.mbn"));
        test=instance.getEntry(8).getFilename();
        assert(test.equals(""));
        test=instance.getEntry(9).getFilename();
        assert(test.equals(""));
        test=instance.getEntry(10).getFilename();
        assert(test.equals("efs.img.ext4"));
        test=instance.getEntry(11).getFilename();
        assert(test.equals("nvrebuild1.bin"));
        test=instance.getEntry(12).getFilename();
        assert(test.equals("nvrebuild2.bin"));
        test=instance.getEntry(13).getFilename();
        assert(test.equals("system.img.ext4"));
        test=instance.getEntry(14).getFilename();
        assert(test.equals("userdata.img.ext4"));
        test=instance.getEntry(15).getFilename();
        assert(test.equals("persist.img.ext4"));
        test=instance.getEntry(16).getFilename();
        assert(test.equals("cache.img.ext4"));
        test=instance.getEntry(17).getFilename();
        assert(test.equals("recovery.img"));
        test=instance.getEntry(18).getFilename();
        assert(test.equals(""));
        test=instance.getEntry(19).getFilename();
        assert(test.equals(""));
        test=instance.getEntry(20).getFilename();
        assert(test.equals(""));
        test=instance.getEntry(21).getFilename();
        assert(test.equals(""));
        test=instance.getEntry(22).getFilename();
        assert(test.equals(""));
        test=instance.getEntry(23).getFilename();
        assert(test.equals("pgpt.img"));
        test=instance.getEntry(24).getFilename();
        assert(test.equals("MSM8960.pit"));
        test=instance.getEntry(25).getFilename();
        assert(test.equals("md5.img"));
        test=instance.getEntry(26).getFilename();
        assert(test.equals("sgpt.img"));
    }

    /**
     * Test of findEntry method, of class PitData.
     */
    @Test
    public void testFindEntry_String() {
        System.out.println("findEntry");
        String partitionName = "MD5";
        PitData instance = new PitData();
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        String expResult = "md5.img";
        PitEntry result = instance.findEntry(partitionName);
        assertEquals(expResult, result.getFilename());
    }

    /**
     * Test of findEntry method, of class PitData.
     */
    @Test
    public void testFindEntry_int() {
        try {
            System.out.println("findEntry");
            int partitionIdentifier = 10;
            PitData instance = new PitData(pitFile);
            String expResult = "PARAM";
            PitEntry result = instance.findEntry(partitionIdentifier);
            assertEquals(expResult, result.getPartitionName());
            result = instance.findEntry(6);
            assertEquals("RPM", result.getPartitionName());
            result = instance.findEntry(2);
            assertEquals("SBL1", result.getPartitionName());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of addEntry method, of class PitData.
     */
    @Test
    public void testAddEntry() {
        try {
            System.out.println("addEntry");
            PitEntry entry = null;
            PitData instance = new PitData(pitFile);
            PitEntry expresult = instance.findEntry(10);
            instance.addEntry(expresult);
            PitEntry result=instance.getEntry(27);
            assert (result.getPartitionName().equals(expresult.getPartitionName()));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getEntryCount method, of class PitData.
     */
    @Test
    public void testGetEntryCount() {
        try {
            System.out.println("getEntryCount");
            PitData instance = new PitData(pitFile);
            int expResult = 27;
            int result = instance.getEntryCount();
            assertEquals(expResult, result);
            instance.addEntry(new PitEntry());
            assertEquals(expResult+1, instance.getEntryCount());
            instance.addEntry(new PitEntry());
            assertEquals(expResult+2, instance.getEntryCount());
            instance.addEntry(new PitEntry());
            assertEquals(expResult+3, instance.getEntryCount());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getFileType method, of class PitData.
     */
    @Test
    public void testGetFileType() {
        try {
            System.out.println("getFileType");
            PitData instance = new PitData(pitFile);
            char[] expResult = new char[]{'C','O','M','_','T','A','R','2'};
            char[] result = instance.getFileType();
            assertArrayEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getPhone method, of class PitData.
     */
    @Test
    public void testGetPhone() {
        try {
            System.out.println("getPhone");
            PitData instance = new PitData(pitFile);
            char[] expResult = new char[]{'M','S','M','8','9','6','0',' ',' ',' ',' ',' '};
            char[] result = instance.getPhone();
            assertArrayEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

  
}
