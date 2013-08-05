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
import java.util.zip.ZipException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CaspacTest {
    
    
    public CaspacTest() {
        try {
            
            this.instance = new Caspac(new File("../../CASPAC/testpak.zip"), Statics.getTempFolder(),0);
            System.out.println(instance.CASPAC.getCanonicalPath());
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
     * Test of removeScript method, of class Caspac.
     */
    @Test
    public void testRemoveScript() {
        try {
            System.out.println("removeScript");
            instance.load();
            Script s=new Script(instance.scripts.get(0));
            instance.getScriptByName("foobar");
            instance.removeScript(instance.getScriptByName("foobar"));
            assert(!instance.scripts.contains("foobar"));
        } catch (ZipException ex) {
            Logger.getLogger(CaspacTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CaspacTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testSetActiveScript() {
        System.out.println("setActiveScript");
        Script s=instance.getScriptByName("foobar");
        instance.setActiveScript(s);
        assert(instance.getActiveScript().name.equals("foobar"));
        
        // TODO review the generated test code and remove the default call to fail.
        
    }

    @Test
    public void testGetActiveScript() {
        System.out.println("getActiveScript");
        
        for (int i=0; i<10; i++){
            instance.getScriptByName("script"+Integer.toString(i));
        }
        instance.setActiveScript(instance.scripts.get(instance.scripts.size()-1));
        Script result = instance.getActiveScript();
        assert(result.name.equals(instance.scripts.get(instance.scripts.size()-1).name));
    }

    @Test
    public void testWrite() throws Exception {
        System.out.println("write");
        //instance.CASPAC=new File("./cool");
        
        //TODO: how do we change the output folder?  the file is final.
        
        //instance.write();
        
    }

    @Test
    public void testSetBuild() {
        System.out.println("setBuild");
        Properties p=instance.build.buildProp;
        p.setProperty("Developer.DonateLink", "OMFG");
        instance.setBuild(p);
        assert (instance.build.donateLink.equals("OMFG"));
    }

}