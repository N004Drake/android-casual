/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.BeforeClass;

/**
 *
 * @author adam
 */
public class CASPACjUnitTest {

    @BeforeClass
    public static void setUpClass() {
        CASUAL.CASUALApp.shutdown(0);
    }

    @AfterClass
    public static void tearDownClass() {
        CASUAL.CASUALApp.shutdown(0);
    }

    @Test
    public void testCASPACOperations() {
        
        CASUAL.Statics.useGUI = true;
        int x = new CASUAL.CASUALInteraction("testing", "Do you want to test CASPAC functionality?\ntest").showTimeoutDialog(10, null, 1, 1, new String[]{"ok", "cancel"}, "cancel");
        if (x == 0) {


            CASUAL.Statics.useGUI = false;
            CASUALApp.shutdown(0);
            String[] casualParams = new String[]{"--CASPAC", "../../CASPAC/testpak.zip"};
            String[] badValues = new String[]{"ERROR"};
            String[] goodValues = new String[]{"echo [PASS]"};
            CASUALTest ct=new CASUALTest(casualParams, goodValues, badValues);
            assertEquals(true, ct.checkTestPoints());
            CASUALApp.shutdown(0);
            System.out.println("TESTING SECOND ROUND");
            System.out.println("TESTING SECOND ROUND");
            System.out.println("TESTING SECOND ROUND");
            System.out.println("TESTING SECOND ROUND");
            CASUAL.Statics.useGUI = false;
            casualParams = new String[]{"--CASPAC", "../../CASPAC/testpak.zip"};
            badValues = new String[]{"ERROR"};
            goodValues = new String[]{"echo [PASS]", "[PASS] IFNOTCONTAINS"};
            assertEquals(true, new CASUAL.CASUALTest(casualParams, goodValues, badValues).checkTestPoints());
        }
    }
}
