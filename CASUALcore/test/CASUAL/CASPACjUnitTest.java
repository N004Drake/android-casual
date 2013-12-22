/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import CASUAL.communicationstools.adb.ADBTools;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CASPACjUnitTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testTest(){
            
            }
    //@Test
    public void testCASPACOperations() {
        //TODO: testing is disabled on CASPAC because it is hanging. 
    
        if (!new ADBTools().isConnected()) {
            return;
        }
        CASUAL.CASUALMain.shutdown(0);
        CASUAL.Statics.GUI.setDummyGUI(true);
        String[] casualParams = new String[]{"--CASPAC", "../../CASPAC/testpak.zip"};
        String[] badValues = new String[]{"ERROR"};
        String[] goodValues = new String[]{"echo [PASS]"};
        CASUALTest ct = new CASUALTest(casualParams, goodValues, badValues);
        assertEquals(true, ct.checkTestPoints());
        CASUAL.CASUALMain.shutdown(0);

        System.out.println("TESTING SECOND ROUND");
        System.out.println("TESTING SECOND ROUND");
        System.out.println("TESTING SECOND ROUND");
        System.out.println("TESTING SECOND ROUND");
        CASUAL.Statics.GUI.setDummyGUI(true);
        casualParams = new String[]{"--CASPAC", "../../CASPAC/testpak.zip"};
        badValues = new String[]{"ERROR"};
        goodValues = new String[]{"echo [PASS]", "[PASS] IFNOTCONTAINS"};
        assertEquals(true, new CASUAL.CASUALTest(casualParams, goodValues, badValues).checkTestPoints());
    }
              
}
