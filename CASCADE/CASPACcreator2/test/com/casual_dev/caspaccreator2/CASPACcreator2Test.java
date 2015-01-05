/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casual_dev.caspaccreator2;

import com.casual_dev.caspaccreator2.CASPACcreator2;
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
    
}
