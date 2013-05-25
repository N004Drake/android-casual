/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class JUnitTest  {
    
    public JUnitTest() {
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
    
    @Test
    public void testFileOperationsFailures(){
        
        assertEquals(true, new CASUAL.FileOperations().copyFromResourceToFile(CASUAL.Statics.ADBini, CASUAL.Statics.TempFolder+"new"));
        assertEquals(false, new CASUAL.FileOperations().copyFromResourceToFile(null, null));
        assertEquals(true, new CASUAL.FileOperations().makeFolder(CASUAL.Statics.TempFolder+"new"+CASUAL.Statics.Slash));
        assertEquals(false, new CASUAL.FileOperations().makeFolder(null));
        assertEquals(true, new CASUAL.FileOperations().verifyExists(CASUAL.Statics.TempFolder+"new"+CASUAL.Statics.Slash));
        assertEquals(false, new CASUAL.FileOperations().verifyExists(CASUAL.Statics.TempFolder+"asfdadfasfd"+CASUAL.Statics.Slash));
        if (CASUAL.Statics.isWindows()){
            assertEquals(true, new CASUAL.HeimdallInstall().deployHeimdallForWindows());
            assertEquals(true, new CASUAL.HeimdallInstall().installWindowsDrivers());
        }
        
        
        
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}