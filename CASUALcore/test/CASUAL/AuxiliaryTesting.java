package CASUAL;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class AuxiliaryTesting {


    @BeforeClass
    public static void setUpClass() {
        CASUALApp.shutdown(0);
    }

    @AfterClass
    public static void tearDownClass() {
    }
    @Test
    public void testCasualAuxilliaryFunctions() {

        //run CASUAL to set environmental values
        CASUALApp.main(new String[]{"-e", "$ADB devices"});
        //Testing ADB reboot download
        CASUAL.Statics.GUIIsAvailable = true;
        if (new CASUAL.CASUALMessageObject("Testing Heimdall", "Connect an ODIN capable device in ADB mode").showUserCancelOption() == 0) {
            CASUAL.Statics.GUIIsAvailable = false;
            String returnval = new CASUAL.CASUALScriptParser().executeOneShotCommand("$ADB reboot download");
            assert returnval.equals("") || returnval.equals("\n ");
            CASUALApp.shutdown(0);
        }

        //Testing Heimdall close-pc-screen
        CASUAL.Statics.GUIIsAvailable= true;
        if (new CASUAL.CASUALMessageObject("Testing Heimdall", "Connect a device in ODIN mode").showUserCancelOption() == 0) {
            CASUAL.Statics.GUIIsAvailable = false;
            setContinue();
            setContinue();
            setContinue();
            setContinue();
            new CASUAL.CASUALScriptParser().executeOneShotCommand( "$HEIMDALL close-pc-screen");
            CASUALApp.shutdown(0);
        }

        //testing ADB reboot bootloader
        CASUAL.Statics.GUIIsAvailable = true;
        if (new CASUAL.CASUALMessageObject("Testing Fastboot", "Connect a FASTBOOT capable device in ADB mode").showUserCancelOption() ==0 ) {
            CASUAL.Statics.GUIIsAvailable = false;
            CASUALApp.main(new String[]{"-e", "$ADB reboot bootloader"});
            CASUALApp.shutdown(0);
        }

        //testing Fastboot reboot
        CASUAL.Statics.GUIIsAvailable = true;
        if (new CASUAL.CASUALMessageObject("Testing Fastboot", "Connect a device in FASTBOOT mode").showUserCancelOption() == 0) {
            CASUAL.Statics.GUIIsAvailable = false;
            String returnval = new CASUAL.CASUALScriptParser().executeOneShotCommand("$FASTBOOT reboot");
            CASUALApp.shutdown(0);
            assert returnval.contains("rebooting...");
        }

        CASUAL.Statics.GUIIsAvailable = true;
        if (new CASUAL.CASUALMessageObject("Overall Test", "Connect a device in ADB mode").showUserCancelOption() == 0) {
            CASUAL.Statics.GUIIsAvailable = false;
            CASUAL.Statics.dumbTerminalGUI = true;
            String[] casualParams = new String[]{"--execute", "$ECHO hi"};
            String[] badValues = new String[]{"holy mother of god, i just saw a dog."};
            String[] goodValues = new String[]{"hi"};
            assertEquals(true, new CASUAL.CASUALTest(casualParams, goodValues, badValues).checkTestPoints());
            CASUALApp.shutdown(0);
        }

    }
    public void setContinue() {
        String string = "\n";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        CASUAL.Statics.in = new BufferedReader(new InputStreamReader(stringStream));
    }
}