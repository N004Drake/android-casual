/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class WindowsDriversTest {

    public WindowsDriversTest() {
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
     * Test of main method, of class WindowsDrivers.
     */
 
    /**
     * Test of installDriverBlanket method, of class WindowsDrivers.
     */
    @Test
    public void testInstallDriverBlanket() {
        CASUAL.Statics.guiReady = true;
        if (OSTools.isWindows()) {
            System.out.println("installDriverBlanket");
            WindowsDrivers instance = new WindowsDrivers(1);
            setContinue();
            instance.installDriverBlanket(null);
        }
    }

    /**
     * Test of removeDriver method, of class WindowsDrivers.
     */
    @Test
    public void testRemoveDriver() {
        if (OSTools.isWindows()) {
            System.out.println("uninstallCADI");
            WindowsDrivers instance = new WindowsDrivers(1);
            instance.uninstallCADI();
        }

    }

    public void setContinue() {
        String string = "\n";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        CASUAL.Statics.in = new BufferedReader(new InputStreamReader(stringStream));
    }

    public void setQuit() {
        String string = "q";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        CASUAL.Statics.in = new BufferedReader(new InputStreamReader(stringStream));
    }
}