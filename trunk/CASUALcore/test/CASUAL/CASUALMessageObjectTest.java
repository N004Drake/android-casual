/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CASUALMessageObjectTest {

    public CASUALMessageObjectTest() {
        if (! java.awt.GraphicsEnvironment.isHeadless() ){
            CASUAL.Statics.GUI=new GUI.development.CASUALGUIMain();
        }
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testCASUALInteractions() {
        if ( java.awt.GraphicsEnvironment.isHeadless()){
            return;
        }
        String title = "Testing Title";
        String message = "Testing Message";
        //InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());

        CASUAL.Statics.GUI.setReady(false);
        CASUAL.CASUALMessageObject ci = new CASUAL.CASUALMessageObject(title, message);
        setContinue();
        assertEquals("", ci.inputDialog());
        setQuit();
        assertEquals("q", ci.inputDialog());
         
        setQuit();
        assertEquals(1, ci.showActionRequiredDialog());
        setContinue();
        assertEquals(0, ci.showActionRequiredDialog());
        setContinue();
        ci.showErrorDialog();
        setContinue();
        ci.showInformationMessage();
        setQuit();
        assertEquals(1, ci.showUserCancelOption());
        setContinue();
        assertEquals(0, ci.showUserCancelOption());
        setContinue();
        ci.showUserNotification();
        CASUAL.Statics.GUI.setReady(true);
        if (!java.awt.GraphicsEnvironment.isHeadless()){
            int x = new CASUAL.CASUALMessageObject("testing", "Do you want to perform the full array of GUI tests?\ntest").showTimeoutDialog(10, null, 1, 1, new String[]{"ok", "cancel"}, "cancel");
            CASUAL.Statics.GUI.setReady(true);
            if (x == 0) {
                ci = new CASUAL.CASUALMessageObject("Text Input", "Press\n1");
                assertEquals("1", ci.inputDialog());
                ci = new CASUAL.CASUALMessageObject("Action Required", "Select\nI didn't do it!");
                assertEquals(1, ci.showActionRequiredDialog());
                ci = new CASUAL.CASUALMessageObject("Action Required", "Select\nI did it!");
                assertEquals(0, ci.showActionRequiredDialog());
                ci = new CASUAL.CASUALMessageObject("Cancel Option", "hit\nStop!");
                assertEquals(1, ci.showUserCancelOption());
                ci = new CASUAL.CASUALMessageObject("Cancel Option ", "hit\nContinue!");
                assertEquals(0, ci.showUserCancelOption());
                ci = new CASUAL.CASUALMessageObject("Error Dialog", "hit\nOK!");
                ci.showErrorDialog();
                ci = new CASUAL.CASUALMessageObject("Information Dialog", "hit\nOK!");
                ci.showInformationMessage();
                ci = new CASUAL.CASUALMessageObject("Notification Dialog", "In the next window, double-click the Disconnected logo to enable the controls");
                ci.showUserNotification();
                
            }
        }
    }

    
    public void testGUIResponsiveness(){
        
    }
    private void setContinue() {
        String string = "\n";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        CASUAL.Statics.in = new BufferedReader(new InputStreamReader(stringStream));
    }

    private void setQuit() {
        String string = "q";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        CASUAL.Statics.in = new BufferedReader(new InputStreamReader(stringStream));
    }
}