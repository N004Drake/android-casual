/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CASUALLanguagejUnitTest {

    @Before
    public void setUp() {
        Statics.GUI = new GUI.testing.automatic();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testCASUALLanguage() {
        CASUAL.CASUALMain.main(new String[]{"-e", "$ECHO hi"});
        Statics.GUI = new GUI.testing.automatic();

        String x = new CASUAL.CASUALScriptParser().executeOneShotCommand("$IFNOTCONTAINS d2cafdan $INCOMMAND adb shell \"cat /system/build.prop\" $DO $IFNOTCONTAINS d2asdfgtt $INCOMMAND $ADB shell \"cat /system/build.prop\" $DO $ECHO hi");
        assert x.contains("hi");
    }

}
