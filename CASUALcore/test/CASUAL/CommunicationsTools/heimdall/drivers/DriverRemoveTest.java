/*
 * Copyright (C) 2013 Jeremy
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

package CASUAL.CommunicationsTools.heimdall.drivers;

import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.Statics;
import CASUAL.communicationstools.heimdall.drivers.DriverRemove;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 *
 * @author Jeremy
 */
public class DriverRemoveTest {
    
    public DriverRemoveTest() {
        assumeTrue(OSTools.isWindows()&&  !java.awt.GraphicsEnvironment.isHeadless());
        Statics.GUI = new GUI.testing.automatic();
    }

    /**
     * Test of deleteOemInf method, of class DriverRemove.
     */
    @Test
    public void testDeleteOemInf() {
        Log.level4Debug("Testing DriverRemove.deleteOemInf()");
        DriverRemove instance = new DriverRemove();
        assertFalse(instance.deleteOemInf());
    }

    /**
     * Test of removeOrphanedDevices method, of class DriverRemove.
     */
    @Test
    public void testRemoveOrphanedDevices() {
        Log.level4Debug("Testing DriverRemove.removeOrphanedDevices()");
        DriverRemove instance = new DriverRemove();
        assertFalse(instance.removeOrphanedDevices("USB\\VID_XXXX&PID_XXXX"));
    }
    
}
