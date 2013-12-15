/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import CASUAL.CommunicationsTools.ADB.ADBTools;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class BusyboxToolsTest {

    public BusyboxToolsTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    String busybox = "/data/local/tmp/busybox";

    @Test
    public void testGetBusyboxLocation() {
        System.out.println("getBusyboxLocation");
        new Shell().sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm " + busybox});
        String expResult = "/data/local/tmp/busybox";
        String result = BusyboxTools.getBusyboxLocation();
        assertEquals(expResult, result);
    }

    @Test
    public void testBusyboxCASUALCommand() {
        if (!new ADBTools().isConnected()) {
            return;
        }
        System.out.println("testBusyboxCASUALCommand");
        new Shell().sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm " + busybox});
        String result = new Shell().sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "ls " + busybox});
        assert result.contains("busybox");
        result = new CASUALScriptParser().executeOneShotCommand("$ADB shell $BUSYBOX mount");

        assert result.contains("rootfs on /");
    }
}
