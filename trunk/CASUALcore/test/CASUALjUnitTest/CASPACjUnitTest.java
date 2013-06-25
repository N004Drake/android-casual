/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUALjUnitTest;


import CASUAL.CASUALTest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;


/**
 *
 * @author adam
 */
public class CASPACjUnitTest {
    @BeforeClass
    public static void setUpClass() {
        CASUAL.CASUALApp.shutdown(0);
    }
    
    @AfterClass
    public static void tearDownClass() {
        CASUAL.CASUALApp.shutdown(0);
    }
    
 @Test
    public void testCASPACOperations() {
        CASUAL.Statics.useGUI = true;
        int x = new CASUAL.CASUALInteraction("testing", "Do you want to test CASPAC functionality?\ntest").showTimeoutDialog(10, null, 1, 1, new String[]{"ok", "cancel"}, "cancel");
        if (x == 0) {
            


            Thread t=new Thread(new CASUAL.CASUALTest().readReactToCASUAL);
            t.setDaemon(true);
            t.start();
            CASUAL.Statics.useGUI = false;
            CASUAL.CASUALApp.beginCASUAL(new String[]{"--CASPAC", "../../CASPAC/testpak.zip"});
            CASUAL.CASUALApp.shutdown(0);
            try {
                Thread.sleep(10000); //must sleep because a process may be writing to the CASPAC
            } catch (InterruptedException ex) {
                Logger.getLogger(CASPACjUnitTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            t=new Thread(new CASUAL.CASUALTest().readReactToCASUAL);
            t.setDaemon(true);
            t.start();
            CASUAL.Statics.useGUI = false;
            CASUAL.CASUALApp.beginCASUAL(new String[]{"--CASPAC", "../../CASPAC/testpak.zip"});
            CASUALTest.shutdown=true;
            CASUAL.CASUALApp.shutdown(0);
            t.stop();
            CASUALTest.shutdown=false;            

            //TODO: verify results
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


