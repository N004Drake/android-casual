package CASUAL;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class TranslationsjUnitTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }
    //mixed valid and invalid translation strings early, middle, late values.
    final static String line = "@permissionsElevationRequired @interactionOfflineNotification @ppermissionsElevationRequiredermissionsElevationRequired @permissionsElevatisfdasf  test test test   @heimdallWasSucessful test test  ";
    //early in translation file
    final static String line2 = "@interactionOfflineNotification";
    //late in translation file
    final static String line3 = "@NotForYourDevice";
    //middle of translation file
    final static String line4 = "@md5sVerified";

    @Test
    public void translation1() {
        for (int i = 0; i < 100; i++) {
            Translations.get(line);
            Translations.get(line2);
            Translations.get(line3);
            Translations.get(line4);
        }
    }
}