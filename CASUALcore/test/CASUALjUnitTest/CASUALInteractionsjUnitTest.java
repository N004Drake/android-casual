/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUALjUnitTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class CASUALInteractionsjUnitTest {

    public CASUALInteractionsjUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        CASUAL.CASUALApp.shutdown(0);
    }

    @AfterClass
    public static void tearDownClass() {
        CASUAL.CASUALApp.shutdown(0);
    }

    @Test
    public void testCASUALInteractions() {
        String title = "Testing Title";
        String message = "Testing Message";
        String string = "aaa";
        //InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());

        CASUAL.Statics.useGUI = false;
        CASUAL.CASUALInteraction ci = new CASUAL.CASUALInteraction(title, message);
        setContinue();
        assertEquals("", ci.inputDialog());
        setQuit();
        assertEquals("q", ci.inputDialog());

        setQuit();
        assertEquals(0, ci.showActionRequiredDialog());
        setContinue();
        assertEquals(1, ci.showActionRequiredDialog());
        setContinue();
        ci.showErrorDialog();
        setContinue();
        ci.showInformationMessage();
        setQuit();
        assertEquals(0, ci.showUserCancelOption());
        setContinue();
        assertEquals(1, ci.showUserCancelOption());
        setContinue();
        ci.showUserNotification();
        CASUAL.Statics.useGUI = true;
        int x = new CASUAL.CASUALInteraction("testing", "Do you want to perform the full array of GUI tests?\ntest").showTimeoutDialog(10, null, 1, 1, new String[]{"ok", "cancel"}, "cancel");
        if (x == 0) {
            ci = new CASUAL.CASUALInteraction("Text Input", "Press\n1");
            assertEquals("1", ci.inputDialog());
            ci = new CASUAL.CASUALInteraction("Action Required", "Select\nI didn't do it!");
            assertEquals(0, ci.showActionRequiredDialog());
            ci = new CASUAL.CASUALInteraction("Action Required", "Select\nI did it!");
            assertEquals(1, ci.showActionRequiredDialog());
            ci = new CASUAL.CASUALInteraction("Cancel Option", "hit\nStop!");
            assertEquals(0, ci.showUserCancelOption());
            ci = new CASUAL.CASUALInteraction("Cancel Option ", "hit\nContinue!");
            assertEquals(1, ci.showUserCancelOption());
            ci = new CASUAL.CASUALInteraction("Error Dialog", "hit\nOK!");
            ci.showErrorDialog();
            ci = new CASUAL.CASUALInteraction("Information Dialog", "hit\nOK!");
            ci.showInformationMessage();
            ci = new CASUAL.CASUALInteraction("Notification Dialog", "hit OK!");
            ci.showUserNotification();
        }
    }

    private void setContinue() {
        String string = "\n";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        CASUAL.CASUALInteraction.in = new BufferedReader(new InputStreamReader(stringStream));
    }

    private void setQuit() {
        String string = "q";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        CASUAL.CASUALInteraction.in = new BufferedReader(new InputStreamReader(stringStream));
    }
}