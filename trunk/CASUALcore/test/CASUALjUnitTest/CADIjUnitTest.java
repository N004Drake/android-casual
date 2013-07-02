/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUALjUnitTest;

import CASUAL.ShellTest;
import java.util.Arrays;
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
public class CADIjUnitTest {
    
    public CADIjUnitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        CASUAL.CASUALApp.shutdown(0);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Test
    public void testCadi() {
        if (CASUAL.Statics.isWindows()){
            //set good/bad returns and command to execute
            String[] goodReturns=new String[]{"CADI","jrloper"};
            String[] badReturns=new String[]{"error while","failed to"};
            String[] commandToRun=new String[]{"CASUAL/resources/heimdall/CADI.exe","--remove"};
            //execute the command
            assertEquals(true,new ShellTest(goodReturns,badReturns,commandToRun).runTest());

            //reboot into download
            CASUAL.CASUALApp.beginCASUAL(new String[]{"-e","$ADB reboot download"});

            //run full CASUAL and test results
            goodReturns=new String[]{"CADI","jrloper"};
            badReturns=new String[]{"error while","failed to"};
            commandToRun=new String[]{"-e","$HEIMDALL close-pc-screen"};
            assertEquals(true, new CASUAL.CASUALTest(commandToRun, goodReturns,badReturns).checkTestPoints());
        }
        
    }
}