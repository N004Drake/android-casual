/*
 * Copyright (C) 2013 adamoutler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package CASUAL;

import CASUAL.CommunicationsTools.Fastboot.FastbootTools;
import CASUAL.CommunicationsTools.ADB.ADBTools;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adamoutler
 */
public class ShellTest {

    final String ex = "List of devices attached \n\n";
    final String exp = "\nList of devices attached \n\n";

    public ShellTest() {
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
     * Test of elevateSimpleCommandWithMessage method, of class Shell.
     */
    @Test
    public void testElevateSimpleCommandWithMessage() {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        System.out.println("elevateSimpleCommandWithMessage");
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String message = "adb wait-for-device";
        String result = instance.elevateSimpleCommandWithMessage(cmd, message);
        assert (result.contains(ex));

    }

    /**
     * Test of elevateSimpleCommand method, of class Shell.
     */
    @Test
    public void testElevateSimpleCommand() {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        System.out.println("elevateSimpleCommand");
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String result = instance.elevateSimpleCommand(cmd);
        assertEquals(ex, result);

    }

    /**
     * Test of sendShellCommand method, of class Shell.
     */
    @Test
    public void testSendShellCommand() {
        System.out.println("sendShellCommand");
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String result = instance.sendShellCommand(cmd);
        assert (result.contains(exp));
    }

    /**
     * Test of sendShellCommandIgnoreError method, of class Shell.
     */
    @Test
    public void testSendShellCommandIgnoreError() {
        System.out.println("sendShellCommandIgnoreError");
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String result = instance.sendShellCommand(cmd);
        assert (result.contains(exp));
        cmd = new String[]{new ADBTools().getBinaryLocation()};
        String expResult = "\nAndroid Debug Bridge version 1.0.31\n\n";
        result = instance.sendShellCommand(cmd);
        assertEquals(expResult, result);

    }

    /**
     * Test of silentShellCommand method, of class Shell.
     */
    @Test
    public void testSilentShellCommand() {
        System.out.println("silentShellCommand");
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String result = instance.sendShellCommand(cmd);
        assert (result.contains(exp));
    }

    /**
     * Test of liveShellCommand method, of class Shell.
     */
    @Test
    public void testLiveShellCommand() {
        System.out.println("liveShellCommand");
        String[] params = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        boolean display = false;
        Shell instance = new Shell();
        String result = instance.liveShellCommand(params, display);
        assertEquals(ex, result);

    }

   

    /**
     * Test of timeoutShellCommand method, of class Shell.
     */
    @Test
    public void testTimeoutShellCommand() {
        System.out.println("timeoutShellCommand");
        int timeout = 4000;
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String result = instance.timeoutShellCommand(cmd, timeout);
        assertEquals(ex, result);
        timeout = 0;
        result = instance.timeoutShellCommand(cmd, timeout);
        String expResult = "Timeout!!! ";
        assertEquals(expResult, result);

    }

    /**
     * Test of silentTimeoutShellCommand method, of class Shell.
     */
    @Test
    public void testSilentTimeoutShellCommand() {
        System.out.println("silentTimeoutShellCommand");
        int timeout = 6000;
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String result = instance.silentTimeoutShellCommand(cmd, timeout);
        assertEquals(ex, result);
        timeout = 0;
        result = instance.silentTimeoutShellCommand(cmd, timeout);
        String expResult = "Timeout!!! ";
        assertEquals(expResult, result);

    }

    /**
     * Test of timeoutValueCheckingShellCommand method, of class Shell.
     * @throws java.io.IOException
     */
    @Test
    public void testTimeoutValueCheckingShellCommand() throws IOException {
        System.out.println("timeoutValueCheckingShellCommand");
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        String[] startTimerOnThisInLine = new String[]{"devices","attached"};
        String expectedResult="List of devices attached \n\n";
        int timeout = 4000;
        Shell instance = new Shell();
        String result = instance.timeoutValueCheckingShellCommand(cmd, startTimerOnThisInLine, timeout);
        assertEquals(expectedResult, result);
        
        //verify the command "fastboot flash" times out after 3 seconds
        cmd = new String[]{new FastbootTools().getBinaryLocation(), "flash"};
        //instantiate a final static variable for use in the timer
        class check{
            boolean timerElapsed=false;
        }
        final check c=new check();
        //instantiate a timer
        Timer t = new Timer();
        startTimerOnThisInLine = new String[]{"waiting"};
        expectedResult="Timeout!!! < waiting for device >\n";
        t.schedule(new TimerTask() {
           @Override
            public void run() {
                c.timerElapsed=true;
            }
        }, timeout);
        result = instance.timeoutValueCheckingShellCommand(cmd, startTimerOnThisInLine, timeout);
        assert(c.timerElapsed);
        assertEquals(expectedResult,result);
        //reset test timer for checking non-timeout
        c.timerElapsed=false;
        cmd = new String[]{new FastbootTools().getBinaryLocation(), "devices"};
        expectedResult="";
        t.schedule(new TimerTask() {
           @Override
            public void run() {
                c.timerElapsed=true;
            }
        }, timeout);
        
        result = instance.timeoutValueCheckingShellCommand(cmd, startTimerOnThisInLine, timeout);
        assert (!c.timerElapsed);
        assertEquals(expectedResult,result);
        
    }

}
