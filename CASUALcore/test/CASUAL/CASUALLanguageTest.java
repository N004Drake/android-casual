/*
 * To change ttestings license header, choose License Headers in Project Properties.
 * To change ttestings template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CASUAL;

import CASUAL.communicationstools.adb.ADBTools;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class CASUALLanguageTest {
    
    public CASUALLanguageTest() {
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
    CASUALScriptParser csp = new CASUALScriptParser();
    
    /**
     * Test of commandHandler method, of class CASUALLanguage.
     */
    @Test
    public void testOS() {
        
        if (OSTools.isLinux()){
            System.out.println("Testing Linux commands");
            assertEquals("testing",csp.executeOneShotCommand("$LINUX $ECHO testing"));
            assertEquals("testing",csp.executeOneShotCommand("$LINUXMAC $ECHO testing"));
            assertEquals("testing",csp.executeOneShotCommand("$LINUXWINDOWS $ECHO testing"));
            assertEquals("",csp.executeOneShotCommand("$WINDOWS $ECHO testing"));
            assertEquals("",csp.executeOneShotCommand("$WINDOWSMAC $ECHO testing"));
            assertEquals("",csp.executeOneShotCommand("$MAC $ECHO testing"));

        }
        if (OSTools.isMac()){
            System.out.println("Testing Mac commands");
            assertEquals("testing",csp.executeOneShotCommand("$WINDOWSMAC $ECHO testing"));
            assertEquals("testing",csp.executeOneShotCommand("$LINUXMAC $ECHO testing"));
            assertEquals("testing",csp.executeOneShotCommand("$MAC $ECHO testing"));            
            assertEquals("",csp.executeOneShotCommand("$WINDOWS $ECHO testing"));
            assertEquals("",csp.executeOneShotCommand("$LINUXMAC $ECHO testing"));
            assertEquals("",csp.executeOneShotCommand("$LINUX $ECHO testing"));
        }
        if (OSTools.isWindows()){
            System.out.println("Testing Windows commands");
            assertEquals("testing",csp.executeOneShotCommand("$WINDOWS $ECHO testing"));
            assertEquals("testing",csp.executeOneShotCommand("$WINDOWSMAC $ECHO testing"));
            assertEquals("testing",csp.executeOneShotCommand("$LINUXWINDOWS $ECHO testing"));
            assertEquals("",csp.executeOneShotCommand("$LINUX $ECHO testing"));
            assertEquals("",csp.executeOneShotCommand("$LINUXMAC $ECHO testing"));
            assertEquals("",csp.executeOneShotCommand("$MAC $ECHO testing"));        }
 
    }
    @Test
    public void testEcho() {
        System.out.println("$ECHO");
        String expResult = "testing";
        String result = csp.executeOneShotCommand("$ECHO testing");
        assertEquals(expResult, result);
    }
    @Test
    public void testHalt() {
        System.out.println("$HALT");
        String expResult = "testing\ntesting\n";
        String haltResult="";
        String result = csp.executeOneShotCommand("$HALT $ECHO testing;;; $ECHO testing");
        System.out.println(result);
        String result2 = csp.executeOneShotCommand("$HALT");
        assertEquals(expResult, result);
        assertEquals(haltResult,result2);
    }
    @Test
    public void testOnClearOn() {
        System.out.println("$ON");
        ArrayList<String> action=new ArrayList<String>();
        ArrayList<String> reaction=new ArrayList<String>();
        csp.executeOneShotCommand("$ON action,$ECHO hi");
        action.add("action");
        reaction.add("$ECHO hi");
        assertEquals(reaction.get(0),Statics.ReactionEvents.get(0));
        assertEquals(action.get(0), Statics.ActionEvents.get(0));
        System.out.println("$CLEARON");
        csp.executeOneShotCommand("$CLEARON");
        assert(Statics.ReactionEvents.isEmpty());
        assert(Statics.ActionEvents.isEmpty());
    }

    @Test
    public void testcomment() {
        System.out.println("Comment");
        String expResult = "";
        String result = csp.executeOneShotCommand("#$ECHO testing");
        assertEquals(expResult, result);
    }
    
    @Test
    public void testBlankLines() {
        System.out.println("Testing blank lines");
        String expResult = "";
        String result = csp.executeOneShotCommand("");
        assertEquals(expResult, result);
    }
    @Test
    public void testIfContains() {
        System.out.println("$IFCONTAINS true");
        String expResult = "testing";
        String result = csp.executeOneShotCommand("$IFCONTAINS woot $INCOMMAND $ECHO woot $DO $ECHO testing");
        assertEquals(expResult, result);
        System.out.println("$IFCONTAINS false");
        result = csp.executeOneShotCommand("$IFCONTAINS toow $INCOMMAND $ECHO woot $DO $ECHO testing");
        assertEquals("", result);
    }
    @Test
    public void testIfNotContains() {
        System.out.println("$IFNOTCONTAINS true");
        String expResult = "testing";
        String result = csp.executeOneShotCommand("$IFNOTCONTAINS toow $INCOMMAND $ECHO woot $DO $ECHO testing");
        assertEquals(expResult, result);
        System.out.println("$IFNOTCONTAINS false");
        result = csp.executeOneShotCommand("$IFNOTCONTAINS woot $INCOMMAND $ECHO woot $DO $ECHO testing");
        assertEquals("", result);       
    }
    @Test
    public void testSleep() {
        System.out.println("Testing blank lines");
        long time=System.currentTimeMillis();
        csp.executeOneShotCommand("$SLEEP 1");
        assert(System.currentTimeMillis()>=time+1000);
    }    
    @Test
    public void testSleepMillis() {
        System.out.println("Testing blank lines");
        long time=System.currentTimeMillis();
        csp.executeOneShotCommand("$SLEEPMILLIS 1000");
        assert(System.currentTimeMillis()>=time+1000);
    }    
    
    @Test
    public void testBusybox() {
        System.out.println("$BUSYBOX");
        //this will fail if no device is connected.
        if (new ADBTools().isConnected()){
            String expResult = "/data/local/tmp/busybox";
            String result = csp.executeOneShotCommand("$ECHO $BUSYBOX");
            assertEquals(expResult, result);
        }
    }
    @Test
    public void testSlash() {
        System.out.println("$SLASH");
        String expResult = System.getProperty("file.separator"); 
        expResult=expResult+expResult; //get two in there just to verify for literal purposes
        String result = csp.executeOneShotCommand("$ECHO $SLASH$SLASH");
        assertEquals(expResult, result);
    }
     @Test
    public void testZipfile() {
        System.out.println("$ZIPFILE");
        String expResult = Statics.getTempFolder();
        String result = csp.executeOneShotCommand("$ECHO $ZIPFILE");
        System.out.println(result);
        assert(result.contains(expResult));
    }   
    @Test
    public void testListDir() throws IOException {
        System.out.println("$LISTDIR");
        String expResult = "test.txt"; 
        File f=new File(expResult);
        f.createNewFile();
        String result = csp.executeOneShotCommand("$LISTDIR .");
        f.delete();
        System.out.println("$LISTDIR result:\n"+result);
        String[] retvalsplit=result.split("\n");
        boolean test=false;
        for (String res:retvalsplit){
            if(res.endsWith(expResult)){
                test=true;
            }
        }
        assert(test);
    }
    @Test
    public void testMAKEREMOVEDIR() throws IOException {
        System.out.println("$MAKEDIR/$REMOVEDIR");
        String expResult = "testfolder"; 
        File f=new File(expResult);
        f.createNewFile();
        String result = csp.executeOneShotCommand("$MAKEDIR "+expResult);
        assert(result.contains(expResult));

        result = csp.executeOneShotCommand("$LISTDIR .");
        
        String[] retvalsplit=result.split("\n");
        boolean test=false;
        for (String res:retvalsplit){
            if(res.endsWith(expResult)){
                test=true;
            }
        }
        assert(test);
        System.out.println("$REMOVEDIR");
        assert(new File(expResult).exists());
        assert(csp.executeOneShotCommand("$REMOVEDIR "+expResult).contains(expResult));
        assert(! new File(expResult).exists());
    }
    @Test
    public void testDownload() {
        System.out.println("$Download");
        String expResult = Statics.getTempFolder();
        String result = csp.executeOneShotCommand("$DOWNLOAD https://android-casual.googlecode.com/svn/trunk/README , $ZIPFILEreadme, CASUAL SVN readme file");

        String sha256sum=CASUAL.crypto.SHA256sum.getLinuxSum(new File(result));
        assertEquals (sha256sum, "b2db2359cb7ea18bec6189b26e06775abf253f36ffb00402a9cf4faa1a2b6982  readme");

        new File(result).delete();
        assert(result.contains(expResult));
    }   
    
    @Test
    public void testADB(){
        System.out.println("adb test");
        String expResult = Statics.getTempFolder();
        String result = csp.executeOneShotCommand("$ADB devices");
        assert result.contains("List of devices attached");
        result = csp.executeOneShotCommand("adb devices");
        assert result.contains("List of devices attached");
        System.out.println("adb language test completed");  
    }
        @Test
    public void testFastboot(){
        System.out.println("fastboot test");
        String expResult = Statics.getTempFolder();
        String result = csp.executeOneShotCommand("$FASTBOOT --help");
        assert result.contains("unrecognized option '--help'");
        result = csp.executeOneShotCommand("fastboot --help");
        assert result.contains("unrecognized option '--help'");
        System.out.println("fastboot language test completed");  
    }
    @Test
    public void testHeimdall(){
        System.out.println("heimdall test");
        String expResult = Statics.getTempFolder();
        String result = csp.executeOneShotCommand("$HEIMDALL detect");
        assert result.contains("download");
        result = csp.executeOneShotCommand("heimdall detect");
        assert result.contains("download");
        System.out.println("heimdall language test completed");  
    }
}
