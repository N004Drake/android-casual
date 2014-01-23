/*
 * Copyright (C) 2013 adam
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

package GUI.development;

import CASUAL.Statics;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import static org.junit.Assume.assumeTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CASUALGUIMainTest {
    
    public CASUALGUIMainTest() {
        assumeTrue(!java.awt.GraphicsEnvironment.isHeadless());
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    
    
    


    @Test
    public void testUserInteraction() {
        if (!java.awt.GraphicsEnvironment.isHeadless()) {
            CASUAL.Statics.GUI = new GUI.development.CASUALGUIMain();
            CASUAL.Statics.GUI.setReady(true);
            int x = new CASUAL.CASUALMessageObject("testing", "In the window which pops up, you have 4 seconds to \nduble-click the 'Disconnected' icon.").showTimeoutDialog(30, null, 1, 1, new String[]{"ok", "cancel"}, "cancel");
            if (x!=0){
                return;
            }
            CASUAL.Statics.GUI.setVisible(true);
            
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CASUALGUIMainTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            assert Statics.GUI.getControlStatus();
        }
    }

    
}
