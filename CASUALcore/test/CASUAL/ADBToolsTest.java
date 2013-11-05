/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class ADBToolsTest {


    public ADBToolsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getADBCommand method, of class ADBTools.
     */
    @Test
    public void testGetADBCommand() {
        System.out.println("getADBCommand");
        String expResult = "adb";
        String result = ADBTools.getADBCommand();
        assert(result.contains(expResult));
        assert(result.contains(Statics.getTempFolder()));
    }

    /**
     * Test of waitForDevice method, of class ADBTools.
     */
    @Test
    public void testWaitForDevice(){
        if (!ADBTools.isConnected()) return;
        Statics.guiReady=true;
        Statics.interaction=new GUI.development.CASUALShowJFrameMessageObject();
        int x=new CASUALMessageObject("Disconnect>>>Disconnect Your Device, wait 5 seconds and then reconnect.\n").showTimeoutDialog(10, null, 1, 1, new Object[]{"ok","cancel"}, "ok");
        if (x==0){
            System.out.println("waitForDevice");
            String expResult = "";
            String result = ADBTools.waitForDevice();
            assert(!result.contains("CRIT"));
        }
    }

    /**
     * Test of getDevices method, of class ADBTools.
     */
    @Test
    public void testGetDevices() {
        System.out.println("getDevices");
        String expResult = "List of devices attached";
        String result = ADBTools.getDevices();
        assert(result.contains(expResult));
    }

    /**
     * Test of startServer method, of class ADBTools.
     */
    @Test
    public void testStartServer() {
        System.out.println("startServer");
        ADBTools.startServer();
        
        
    }

    /**
     * Test of restartADBserver method, of class ADBTools.
     */
    @Test
    public void testRestartADBserver() {
        System.out.println("restartADBserver");
        ADBTools.restartADBserver();
    }

    /**
     * Test of elevateADBserver method, of class ADBTools.
     */
    @Test
    public void testElevateADBserver() {
        if (!ADBTools.isConnected()) return;
        System.out.println("elevateADBserver");
        if (!OSTools.isWindows()) ADBTools.elevateADBserver();

    }

    /**
     * Test of killADBserver method, of class ADBTools.
     */
    @Test
    public void testKillADBserver() {
        System.out.println("killADBserver");
        ADBTools.killADBserver();
    }

    /**
     * Test of checkADBerrorMessages method, of class ADBTools.
     */
    @Test
    public void testCheckADBerrorMessages() {
        System.out.println("checkADBerrorMessages");
        String DeviceList = "List of devices";
        ADBTools instance = new ADBTools();
        instance.checkADBerrorMessages(DeviceList);

    }
}