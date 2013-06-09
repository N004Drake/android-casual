/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class JUnitTest  {
    
    public JUnitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        //launch CASUAL to set values
        CASUAL.CASUALApp.main(new String[]{"-e","$ADB devices"});
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
    
    @Test
    public void testFileOperationsFailures(){
        
        assertEquals(true, new CASUAL.FileOperations().copyFromResourceToFile(CASUAL.Statics.ADBini, CASUAL.Statics.TempFolder+"new"));
        assertEquals(false, new CASUAL.FileOperations().copyFromResourceToFile(null, null));
        assertEquals(true, new CASUAL.FileOperations().makeFolder(CASUAL.Statics.TempFolder+"new"+CASUAL.Statics.Slash));
        assertEquals(false, new CASUAL.FileOperations().makeFolder(null));
        assertEquals(true, new CASUAL.FileOperations().verifyExists(CASUAL.Statics.TempFolder+"new"+CASUAL.Statics.Slash));
        assertEquals(false, new CASUAL.FileOperations().verifyExists(CASUAL.Statics.TempFolder+"asfdadfasfd"+CASUAL.Statics.Slash));
        if (CASUAL.Statics.isWindows()){
            assertEquals(true, new CASUAL.HeimdallInstall().deployHeimdallForWindows());
            assertEquals(true, new CASUAL.HeimdallInstall().installWindowsDrivers());
        }
    }
    
    private void setContinue(){
        String string = "\n";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        CASUAL.CASUALInteraction.in=new BufferedReader(new InputStreamReader(stringStream));
    }
    private void setQuit(){
        String string = "q";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        CASUAL.CASUALInteraction.in=new BufferedReader(new InputStreamReader(stringStream));
    }
    
    @Test 
    public void testCASUALLanguage(){
        String x= new CASUAL.CASUALScriptParser().executeOneShotCommand("$IFNOTCONTAINS d2cafdan $INCOMMAND shell \"cat /system/build.prop\" $DO $IFNOTCONTAINS d2asdfgtt $INCOMMAND shell \"cat /system/build.prop\" $DO $ECHO hi");
        assert x.contains("hi");
    }   
    
    
    
    @Test
    public void testCASUALInteractions(){
        String title="Testing Title";
        String message="Testing Message";
        String string = "aaa";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        
        CASUAL.Statics.useGUI=false;
        CASUAL.CASUALInteraction ci= new CASUAL.CASUALInteraction(title, message);
        setContinue();
        assertEquals("",ci.inputDialog());  
        setQuit();
        assertEquals("q",ci.inputDialog());  
        
        setQuit();
        assertEquals(0,ci.showActionRequiredDialog());
        setContinue();
        assertEquals(1,ci.showActionRequiredDialog());
        ci.showErrorDialog();
        ci.showInformationMessage();
        setQuit();
        assertEquals(0,ci.showUserCancelOption());
        setContinue();
        assertEquals(1,ci.showUserCancelOption());
        ci.showUserNotification();
        CASUAL.Statics.useGUI=true;
        int x= new CASUAL.CASUALInteraction("testing","Do you want to perform the full array of GUI tests?\ntest").showTimeoutDialog(10, null, 1, 1, new String[]{"ok","cancel"}, "cancel");
        if (x==0){
            ci= new CASUAL.CASUALInteraction("Text Input", "Press\n1");
            assertEquals("1",ci.inputDialog());            
            ci= new CASUAL.CASUALInteraction("Action Required", "Select\nI didn't do it!");
            assertEquals(0,ci.showActionRequiredDialog());
            ci= new CASUAL.CASUALInteraction("Action Required", "Select\nI did it!");
            assertEquals(1,ci.showActionRequiredDialog());
            ci= new CASUAL.CASUALInteraction("Cancel Option", "hit\nStop!");
            assertEquals(0,ci.showUserCancelOption());
            ci= new CASUAL.CASUALInteraction("Cancel Option ", "hit\nContinue!");
            assertEquals(1,ci.showUserCancelOption());
            ci= new CASUAL.CASUALInteraction("Error Dialog", "hit\nOK!");
            ci.showErrorDialog();
            ci= new CASUAL.CASUALInteraction("Information Dialog", "hit\nOK!");
            ci.showInformationMessage();
            ci= new CASUAL.CASUALInteraction("Notification Dialog", "hit OK!");
            ci.showUserNotification(); 
        }
    }
    
    @Test
    public void testCasualCore(){
        int choice= new CASUAL.CASUALInteraction("testing","Do you want to perform the full array of CASUAL tests?\ntest").showTimeoutDialog(10, null, 1, 1, new String[]{"ok","cancel"}, "cancel");
        if (choice==0){
            
            boolean result;
            String returnval;

            
            //run CASUAL to set environmental values
            CASUAL.CASUALApp.main(new String[]{"-e","$ADB devices"});
            //Testing ADB reboot download
            CASUAL.Statics.useGUI=true;
            if (new CASUAL.CASUALInteraction("Testing Heimdall","Connect an ODIN capable device in ADB mode").showUserCancelOption()==1){
                returnval=new CASUAL.CASUALScriptParser().executeOneShotCommand("$ADB reboot download");
                assert returnval.equals("") || returnval.equals("\n ");
            }
            
            //Testing Heimdall close-pc-screen
            CASUAL.Statics.useGUI=true;
            if (new CASUAL.CASUALInteraction("Testing Heimdall","Connect a device in ODIN mode").showUserCancelOption()==1) {
                returnval=new CASUAL.CASUALScriptParser().executeOneShotCommand("$HEIMDALL close-pc-screen");
                assert returnval.contains("Attempt complete");
            }
            
            //testing ADB reboot bootloader
            CASUAL.Statics.useGUI=true;
            if (new CASUAL.CASUALInteraction("Testing Fastboot","Connect a FASTBOOT capable device in ADB mode").showUserCancelOption()==1){
                returnval=new CASUAL.CASUALScriptParser().executeOneShotCommand("$ADB reboot bootloader");
                assert returnval.equals("") || returnval.equals("\n");
            }
            
            //testing Fastboot reboot
            CASUAL.Statics.useGUI=true;
            if (new CASUAL.CASUALInteraction("Testing Fastboot","Connect a device in FASTBOOT mode").showUserCancelOption()==1){
                returnval=new CASUAL.CASUALScriptParser().executeOneShotCommand("$FASTBOOT reboot");
                assert returnval.contains("rebooting...");
            }
   
            CASUAL.Statics.useGUI=true;
            if (new CASUAL.CASUALInteraction("Overall Test","Connect a device in ADB mode").showUserCancelOption()==1)
                         CASUAL.CASUALApp.main(new String[]{"-e","$ADB reboot download"});
        }
    }

    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}