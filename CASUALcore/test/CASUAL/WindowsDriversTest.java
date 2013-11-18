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

package CASUAL;

import java.util.regex.Pattern;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assume.*;
import org.junit.BeforeClass;

/**
 *
 * @author Jeremy
 */
public class WindowsDriversTest {
    
    public static WindowsDrivers instance = null;
    
    public WindowsDriversTest() {
        assumeTrue(OSTools.isWindows());
    }
    
    @BeforeClass
    public static void setUp() {
        instance = new WindowsDrivers(1);
    }
    
    @AfterClass
    public static void tearDownClass() {
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
     */
    @Test
    public void testDriverExtract() throws Exception {
        System.out.println("driverExtract");
        String pathToExtract = instance.pathToCADI;
        assert(instance.driverExtract(pathToExtract));
    }

    /**
     * Test of getDeviceList method, of class WindowsDrivers.
     */
    @Test
    public void testGetDeviceList() {
        System.out.println("getDeviceList");
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
        assert(!instance.deleteOemInf());
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

    /**
     * Test of getRegExPattern method, of class WindowsDrivers.
     */
    @Test
    public void testGetRegExPattern() {
        System.out.println("getRegExPattern");
        String whatPattern = "install";
        assert(instance.getRegExPattern(whatPattern) != null);
    }
}
