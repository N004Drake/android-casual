package CASUALjUnitTest;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import CASUAL.CASUALApp;
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
public class JUnitTest {

    public JUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        CASUAL.CASUALApp.shutdown(0);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    @Test
    public void testFileOperationsFailures() {

        assertEquals(true, new CASUAL.FileOperations().copyFromResourceToFile(CASUAL.Statics.ADBini, CASUAL.Statics.TempFolder + "new"));
        assertEquals(false, new CASUAL.FileOperations().copyFromResourceToFile(null, null));
        assertEquals(true, new CASUAL.FileOperations().makeFolder(CASUAL.Statics.TempFolder + "new" + CASUAL.Statics.Slash));
        assertEquals(false, new CASUAL.FileOperations().makeFolder(null));
        assertEquals(true, new CASUAL.FileOperations().verifyExists(CASUAL.Statics.TempFolder + "new" + CASUAL.Statics.Slash));
        assertEquals(false, new CASUAL.FileOperations().verifyExists(CASUAL.Statics.TempFolder + "asfdadfasfd" + CASUAL.Statics.Slash));
    }

   


 
    @Test
    public void testCasualCore() {
        CASUAL.Statics.useGUI = true;
        int choice = new CASUAL.CASUALInteraction("testing", "Do you want to perform the full array of CASUAL tests?\ntest").showTimeoutDialog(10, null, 1, 1, new String[]{"ok", "cancel"}, "cancel");
        if (choice == 0) {
            CASUAL.Statics.useGUI = false; 

            //run CASUAL to set environmental values
            CASUAL.CASUALApp.main(new String[]{"-e", "$ADB devices"});
            //Testing ADB reboot download
            CASUAL.Statics.useGUI = true;
            if (new CASUAL.CASUALInteraction("Testing Heimdall", "Connect an ODIN capable device in ADB mode").showUserCancelOption() == 1) {
                String returnval = new CASUAL.CASUALScriptParser().executeOneShotCommand("$ADB reboot download");
                assert returnval.equals("") || returnval.equals("\n ");
                CASUALApp.shutdown(0);
            }

            //Testing Heimdall close-pc-screen
            CASUAL.Statics.useGUI = true;
            if (new CASUAL.CASUALInteraction("Testing Heimdall", "Connect a device in ODIN mode").showUserCancelOption() == 1) {
                CASUAL.CASUALApp.main(new String[]{"-e", "$HEIMDALL close-pc-screen"});
                CASUALApp.shutdown(0);
            }

            //testing ADB reboot bootloader
            CASUAL.Statics.useGUI = true;
            if (new CASUAL.CASUALInteraction("Testing Fastboot", "Connect a FASTBOOT capable device in ADB mode").showUserCancelOption() == 1) {
                CASUAL.CASUALApp.main(new String[]{"-e", "$ADB reboot bootloader"});
                CASUALApp.shutdown(0);
            }

            //testing Fastboot reboot
            CASUAL.Statics.useGUI = true;
            if (new CASUAL.CASUALInteraction("Testing Fastboot", "Connect a device in FASTBOOT mode").showUserCancelOption() == 1) {
                String returnval = new CASUAL.CASUALScriptParser().executeOneShotCommand("$FASTBOOT reboot");
                CASUALApp.shutdown(0);
                assert returnval.contains("rebooting...");
            }

            CASUAL.Statics.useGUI = true;
            if (new CASUAL.CASUALInteraction("Overall Test", "Connect a device in ADB mode").showUserCancelOption() == 1) {
                CASUAL.Statics.useGUI = false;
                CASUAL.Statics.dumbTerminalGUI=true;
                String[] casualParams=new String[]{"--execute", "$ECHO hi"};
                String[] badValues=new String[]{"holy mother of god, i just saw a dog."};
                String[] goodValues=new String[]{"hi"};
                assertEquals(true, new CASUAL.CASUALTest(casualParams, goodValues,badValues).checkTestPoints());
                CASUALApp.shutdown(0);
            }

        }
    }
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    private void setTestPoints(String[] readStrings){
        
    }
    private boolean checkTestPoints(){
        if (checkPassed !=null && checkPassed.length>0){
            for (boolean check : checkPassed){
                if (!check) return false;
            }
        }
        return true;
    }
    
    static String[] testpoints;
    static Boolean[] checkPassed;

    
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