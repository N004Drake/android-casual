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
import static org.junit.Assert.*;

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
        Statics.useGUI=true;
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class WindowsDrivers.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        setContinue();
        WindowsDrivers.main(args);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of installDriverBlanket method, of class WindowsDrivers.
     */
    @Test
    public void testInstallDriverBlanket() {
        System.out.println("installDriverBlanket");
        WindowsDrivers instance = new WindowsDrivers();
        setContinue();
        instance.installDriverBlanket();
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of removeDriver method, of class WindowsDrivers.
     */
    @Test
    public void testRemoveDriver() {
        System.out.println("removeDriver");
        WindowsDrivers instance = new WindowsDrivers();
        setContinue();
        instance.removeDriver();
        // TODO review the generated test code and remove the default call to fail.
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