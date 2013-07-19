/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.caspac;

import CASUAL.Statics;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class CaspacTest {
    
    public CaspacTest() {
        try {
            this.instance = new Caspac(new File("../../../CASPAC/testpak.zip"), Statics.TempFolder,0);
        } catch (IOException ex) {
            Logger.getLogger(CaspacTest.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    Caspac instance;
    /**
     * Test of addScript method, of class Caspac.
     */
    @Test
    public void testAddScript() {
        System.out.println("addScript");

        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of removeScript method, of class Caspac.
     */
    @Test
    public void testRemoveScript() {
        System.out.println("removeScript");
        Script script;
        Caspac instance = null;
        // TODO review the generated test code and remove the default call to fail.
    }

 
}