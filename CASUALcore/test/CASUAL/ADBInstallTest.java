/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class ADBInstallTest {
    
    public ADBInstallTest() throws IOException {
        Statics.initializeStatics();
        System.out.println(new File(".").getCanonicalPath());

    }

    /**
     * Test of deployADB method, of class ADBInstall.
     */
    @Test
    public void testDeployADB() {
        System.out.println("deployADB");
        ADBInstall instance = new ADBInstall();
        instance.deployADB();
        assert (new File(Statics.adbDeployed).exists());
        assert (new Shell().sendShellCommand(new String[]{Statics.adbDeployed,"devices"}).contains("device"));
    }
}