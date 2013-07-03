/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUALjUnitTest;

import CASUAL.WindowsDrivers;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CADIjUnitTest {

    public CADIjUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        CASUAL.CASUALApp.shutdown(0);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testCadi() {
        if (CASUAL.Statics.isWindows()) {
            setContinue();
            new WindowsDrivers().installDriverBlanket();
            //new WindowsDrivers().removeDriver();
            //TODO: set good/bad returns and command to assert
            //execute the command
        }

    }

    public void setContinue() {
        String string = "\n";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        CASUAL.CASUALInteraction.in = new BufferedReader(new InputStreamReader(stringStream));
    }

    public void setQuit() {
        String string = "q";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        CASUAL.CASUALInteraction.in = new BufferedReader(new InputStreamReader(stringStream));
    }
}