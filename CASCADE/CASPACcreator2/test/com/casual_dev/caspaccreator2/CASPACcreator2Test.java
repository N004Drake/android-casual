/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casual_dev.caspaccreator2;

import com.casual_dev.caspaccreator2.CASPACcreator2;
import com.casual_dev.caspaccreator2.exception.MissingParameterException;
import java.io.IOException;
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
public class CASPACcreator2Test {
    
    public CASPACcreator2Test() {
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
     * Test of main method, of class CASPACcreator2.
     */
    @Test
    public void testLoadAndSave() {
        System.out.println("main");
        String[] args = new String[]{"--caspac=../../CASPAC/testpak.zip", "--output=/tmp/new.zip","--scriptfiles=file 1","--scriptfiles=file 2"};
        CASPACcreator2.main(args);
        
        // TODO review the generated test code and remove the default call to fail.
    }
    
    @Test(expected=MissingParameterException.class)
    public void testColdLoadWithErrorOutput() throws IOException, MissingParameterException {
        System.out.println("main");
        String[] args = new String[]{
        };
        CASPACcreator2 cc = new CASPACcreator2(args);
        cc.createNewCaspac();
        fail("Script not set, not detected");
    }
        
    @Test(expected=MissingParameterException.class)
    public void testColdLoadWithErrorScriptName() throws IOException, MissingParameterException {
        System.out.println("main");
        String[] args = new String[]{
            "--output=/tmp/new.zip",
        };
        CASPACcreator2 cc = new CASPACcreator2(args);
        cc.createNewCaspac();
        fail("ScriptName not set, not detected");
    }    
    
    @Test(expected=MissingParameterException.class)
    public void testColdLoadWithErrordescription() throws IOException, MissingParameterException {
        System.out.println("main");
        String[] args = new String[]{
            "--output=/tmp/new.zip",
            "--scriptname=newscript"
        };
        CASPACcreator2 cc = new CASPACcreator2(args);
        cc.createNewCaspac();
        fail("Descript not set, not detected");
    }
      @Test(expected=MissingParameterException.class)
    public void testColdLoadWithErrorScriptCode() throws IOException, MissingParameterException {
        System.out.println("main");
        String[] args = new String[]{
            "--output=/tmp/new.zip",
            "--scriptname=newscript",
            "--scriptdescription=\"a cool script thingy\nwoot!\""
        };
        CASPACcreator2 cc = new CASPACcreator2(args);
        cc.createNewCaspac();
        fail("script code not set, not detected");
    }    
    
    @Test
    public void testMinimalLoad() throws IOException, MissingParameterException {
        System.out.println("main");
        String[] args = new String[]{
            "--output=/tmp/new.zip",
            "--scriptname=newscript",
            "--scriptdescription=\"a cool script thingy\nwoot!\"",
            "--scriptcode=#testing scriptinjection\nmoretesting\"'"
        };
        CASPACcreator2 cc = new CASPACcreator2(args);
       try {
           cc.createNewCaspac();
       } catch (Exception ex){
           fail("Exception detected");
       }

    }
    
    
}
