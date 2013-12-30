/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import CASUAL.communicationstools.adb.ADBTools;
import org.junit.AfterClass;
import static org.junit.Assume.assumeTrue;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * @author adam
 */
public class CASUALLanguagejUnitTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testCASUALLanguage() {
        CASUAL.CASUALMain.main(new String[]{"-e", "$ECHO hi"});
        String x = new CASUAL.CASUALScriptParser().executeOneShotCommand("$IFNOTCONTAINS d2cafdan $INCOMMAND shell \"cat /system/build.prop\" $DO $IFNOTCONTAINS d2asdfgtt $INCOMMAND shell \"cat /system/build.prop\" $DO $ECHO hi");
        assert x.contains("hi");
    }

}