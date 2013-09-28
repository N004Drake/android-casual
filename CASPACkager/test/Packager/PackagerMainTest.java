/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Packager;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class PackagerMainTest {
    
    public PackagerMainTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = new String[]{"--CASPAC" , "../CASPAC/fastbootRecoveryTest.zip", "--type" ,"CASPACkager Test Build", "--output", "../CASPAC/CASUAL", "--replaceReference","--Recovery--","MY Recovery","--replaceReference","--DeviceFriendlyName--"," YOUR device", "--replaceFile" ,"recovry.img", "../repo/driver.properties"};
        PackagerMain.main(args);
        
        
    }


}