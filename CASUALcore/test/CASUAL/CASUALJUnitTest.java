/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import CASUAL.misc.JarClassLoader;
import java.io.File;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class CASUALJUnitTest {
    
    public CASUALJUnitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Test
    public void testCASUAL() throws Exception{
        System.out.println(new File("../dist/CASUALstatic.jar").getCanonicalPath());
        JarClassLoader jarLoader=new JarClassLoader("../dist/CASUALstatic.jar");
        Class c = jarLoader.loadClass("CASUAL.CASUALApp",true);
        Object cmain = c.newInstance();
        CASUALApp ca = (CASUALApp) cmain;
        ca.main(new String[]{});
        
       Thread.sleep(7000);
        
        System.out.println(Statics.getTempFolder());
        
        
    
        
        
        
       
        
        
    }
}