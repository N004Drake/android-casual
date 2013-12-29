/*WindowsDriversTest.java
 * **************************************************************************
 *Copyright (C) 2013  Adam Outler
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***************************************************************************/

package CASUAL.CommunicationsTools.heimdall.drivers;
import CASUAL.OSTools;
import CASUAL.Statics;

import CASUAL.communicationstools.heimdall.drivers.WindowsDrivers;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.junit.Assume.*;
import org.junit.BeforeClass;

/**
 *
 * @author Jeremy
 */
public class WindowsDriversTest {
    
    public static WindowsDrivers instance = null;
    
    @BeforeClass
    public static void setUp() {
        instance = new WindowsDrivers(1);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    public WindowsDriversTest() {
        assumeTrue(OSTools.isWindows());
        Statics.GUI=new GUI.testing.automatic();
    }
    
    
    /**
     * Test of installKnownDrivers() method of class WindowsDrivers.
     */
    @Test
    public void testinstallKnownDrivers(){
        WindowsDrivers wd=new WindowsDrivers(0);
        wd.installKnownDrivers(new String[]{});
        
    }
    /**
     * Test of getDeviceList(BOOL, BOOL) method, of class WindowsDrivers.
     */
    @Test
    public void testGetDeviceList() {
        System.out.println("getDeviceList(BOOL,BOOL)");
        String[] dList = instance.getDeviceList(false, true);
        assert(dList.length > 1 && dList[0].contains("USB"));
    }

    /**
     * Test of installDriverBlanket method, of class WindowsDrivers.
     */
    @Test
    public void testInstallDriverBlanket() {
        System.out.println("installDriverBlanket");
        String[] additionalVIDs = new String[0];
        assert(instance.installDriverBlanket(additionalVIDs));
    }

    /**
     * Test of getCASUALDriverCount method, of class WindowsDrivers.
     */    
    @Test
    public void testGetCASUALDriverCount() {
        System.out.println("getCASUALDriverCount");
        assert(instance.getCASUALDriverCount() > 0);
    }    
    
    /**
     * Test of installDriver method, of class WindowsDrivers.
     */
    @Test
    public void testInstallDriver() {
        System.out.println("installDriver");
        assert(instance.installDriver("") == false);
    }

    /**
    * Test of uninstallCADI method, of class WindowsDrivers.
    */
    @Test
    public void testUninstallCADI() {
        System.out.println("uninstallCADI");
        assert(instance.uninstallCADI());
    }

    /**
     * Test of driverExtract method, of class WindowsDrivers.
     * @throws java.lang.Exception
     */
    @Test
    public void testDriverExtract() throws Exception {
        System.out.println("driverExtract");
        String pathToExtract = instance.pathToCADI;
        assert(instance.driverExtract(pathToExtract));
    }

    /**
     * Test of getDeviceList(VID) method, of class WindowsDrivers.
     */
    @Test
    public void testGetDeviceList1() {
        System.out.println("getDeviceList(VID)");
        String VID = "";
        assert(instance.getDeviceList(VID) == null);
    }

    /**
     * Test of removeOrphanedDevices method, of class WindowsDrivers.
     */
    @Test
    public void testRemoveOrphanedDevices() {
        System.out.println("removeOrphanedDevices");
        assert(!instance.removeOrphanedDevices(""));
    }

    /**
     * Test of deleteOemInf method, of class WindowsDrivers.
     */
    @Test
    public void testDeleteOemInf() {
        System.out.println("deleteOemInf");
        assertEquals(false,instance.deleteOemInf());
    }

    /**
     * Test of devconCommand method, of class WindowsDrivers.
     */
    @Test
    public void testDevconCommand() {
        System.out.println("devconCommand");
        String result = instance.devconCommand("help");
        assert(result.contains("Device Console Help:"));
    }

}
