/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.CommunicationsTools.ADB;

import CASUAL.CASUALMessageObject;
import CASUAL.CommunicationsTools.AbstractDeviceCommunicationsProtocol;
import CASUAL.CommunicationsTools.ADB.ADBTools;
import CASUAL.OSTools;
import CASUAL.Statics;
import java.io.File;
import java.io.IOException;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class ADBToolsTest {

    AbstractDeviceCommunicationsProtocol instance;

    public ADBToolsTest() throws IOException {
        Statics.initializeStatics();
        instance = new ADBTools();
        Statics.GUI = new GUI.development.CASUALGUIMain();
        System.out.println(new File(".").getCanonicalPath());
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
        String result = new ADBTools().getBinaryLocation();
        assert (result.contains(expResult));
        assert (result.contains(Statics.getTempFolder()));
        assert (new File(result).exists());
    }

    /**
     * Test of waitForDevice method, of class ADBTools.
     */
    @Test
    public void testWaitForDevice() {
        if (!instance.isConnected()) {
            return;
        }
        
        int x = new CASUALMessageObject("Disconnect>>>Disconnect Your Device, wait 5 seconds and then reconnect.\n").showTimeoutDialog(10, null, 1, 1, new Object[]{"ok", "cancel"}, "ok");
        if (x == 0) {
            System.out.println("waitForDevice");
            String expResult = "";
            instance.waitForDevice();
            
        }
    }

    /**
     * Test of getDevices method, of class ADBTools.
     */
    @Test
    public void testGetDevices() {
        System.out.println("getDevices");
        String result = new ADBTools().getDevices();
    }

    /**
     * Test of startServer method, of class ADBTools.
     */
    @Test
    public void testStartServer() {
        System.out.println("startServer");
        new ADBTools().startServer();

    }

    /**
     * Test of restartADBserver method, of class ADBTools.
     */
    @Test
    public void testRestartADBserver() {
        System.out.println("restartADBserver");
        instance.restartConnection();
    }

    /**
     * Test of elevateADBserver method, of class ADBTools.
     */
    @Test
    public void testElevateADBserver() {
        if (!new ADBTools().isConnected()) {
            return;
        }
        System.out.println("elevateADBserver");
        if (!OSTools.isWindows()) {
            new ADBTools().elevateADBserver();
        }

    }

    /**
     * Test of killADBserver method, of class ADBTools.
     */
    @Test
    public void testKillADBserver() {
        System.out.println("killADBserver");
        new ADBTools().killADBserver();
    }

    /**
     * Test of checkErrorMessage method, of class ADBTools.
     */
    @Test
    public void testCheckADBerrorMessages() {
        System.out.println("checkADBerrorMessages");
        assert (instance.checkErrorMessage(new String[]{}, "woot"));

    }

    @Test
    public void testNumberOfDevicesConnected() {
        System.out.println("numberOfDevicesConnected");
        int expResult = 0;
        int result = new ADBTools().numberOfDevicesConnected();
        System.out.println(result + " devices connected");
    }

    /**
     * Test of isConnected method, of class ADBTools.
     */
    @Test
    public void testIsConnected() {
        System.out.println("isConnected");
        boolean result = new ADBTools().isConnected();

    }

}
