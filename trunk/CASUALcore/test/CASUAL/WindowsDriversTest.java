/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.*;
import static org.junit.Assert.fail;

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
        //new WindowsDrivers(2).uninstallCADI();
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

    /**
     * Test of main method, of class WindowsDrivers.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        WindowsDrivers.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of installDriver method, of class WindowsDrivers.
     */
    @Test
    public void testInstallDriver() {
        System.out.println("installDriver");
        String VID = "";
        WindowsDrivers instance = null;
        boolean expResult = false;
        boolean result = instance.installDriver(VID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of uninstallCADI method, of class WindowsDrivers.
     */
    @Test
    public void testUninstallCADI() {
        System.out.println("uninstallCADI");
        WindowsDrivers instance = null;
        instance.uninstallCADI();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    
    //The following are utilities for this class
    
    //returns true if reboot was sucessful. fails if error.
    public boolean rebootDownload(){
        ADBTools.restartADBserver();
        String[] cmd=new String[]{ADBTools.getADBCommand(),"reboot","download"};
        String s=new Shell().sendShellCommand(cmd);
        assert s.trim().equals("");
        return s.equals("");
        
        
    }
    
    //returns true if close-pc-screen does not error.  fails if error
    public boolean doHeimdallCheck(){
        String[] cmd=new String[]{HeimdallTools.getHeimdallCommand(),"close-pc-screen"};
        String retval=new Shell().sendShellCommand(cmd);
        assert (!retval.contains("ERROR"));
        return !retval.contains("ERROR");
    }
    
    /*Returns true if Samsung device and Windows host 
     runs at the start of each method
      if(!isValidSetup)return;
    */
    public boolean isValidSetup(){
        if (! OSTools.isWindows()){
            //not playing if it's not windows. 
            return false;
        }
        
        String[] cmd = {ADBTools.getADBCommand(), "shell", "cat /system/build.prop"};
        Properties p= new Properties();
        try {
            //load device build.prop into properties file
            p.load(CASUAL.misc.StringOperations.convertStringToStream(new Shell().timeoutShellCommand(cmd,5000)));
            //pull ro.product.manfacturer
            String mfg=p.getProperty("ro.product.manufacturer","");
            //test that it contains Samsung, SAMSUNG or samsung.
            return mfg.contains("amsung")|| mfg.equals("AMSUNG");
        } catch (IOException ex) {
            return false;
        }
    }

}