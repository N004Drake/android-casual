/*
 * Copyright (C) 2013 adamoutler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package CASUAL.CommunicationsTools.Fastboot;

import CASUAL.Statics;
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
public class FastbootToolsTest {
    
    public FastbootToolsTest() {
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
     * Test of getBinaryLocation method, of class FastbootTools.
     */
    @Test
    public void testGetBinaryLocation() {
        System.out.println("getBinaryLocation");
        FastbootTools instance = new FastbootTools();
        String expResult = "";
        String result = instance.getBinaryLocation();
        assert(result.contains(Statics.getTempFolder()));
        assert(result.contains("fastboot"));
    }



    /**
     * Test of reset method, of class FastbootTools.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        FastbootTools instance = new FastbootTools();
        instance.reset();
    }



    /**
     * Test of numberOfDevicesConnected method, of class FastbootTools.
     */
    @Test
    public void testNumberOfDevicesConnected() {
        System.out.println("numberOfDevicesConnected");
        FastbootTools instance = new FastbootTools();
        instance.numberOfDevicesConnected();
    }



    /**
     * Test of deployBinary method, of class FastbootTools.
     */
    @Test
    public void testDeployBinary() {
        System.out.println("deployBinary");
        String tempFolder = Statics.getTempFolder();
        FastbootTools instance = new FastbootTools();
        String expResult = "fastboot";
        String result = instance.deployBinary(tempFolder);
        assert(result.contains(expResult));
        assert(result.contains(tempFolder));

    }

    /**
     * Test of restartConnection method, of class FastbootTools.
     */
    @Test
    public void testRestartConnection() {
        System.out.println("restartConnection");
        FastbootTools instance = new FastbootTools();
        instance.restartConnection();
    }
    
    @Test
    public void testIsConnected(){
        System.out.println("test isConnected()");
        FastbootTools instance = new FastbootTools();
        Boolean result=instance.isConnected();
        System.out.println("Is Connected: "+result);
    }
}
