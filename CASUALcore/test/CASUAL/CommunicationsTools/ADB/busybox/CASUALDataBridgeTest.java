/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.CommunicationsTools.ADB.busybox;

import CASUAL.Shell;
import CASUAL.communicationstools.adb.ADBTools;
import CASUAL.communicationstools.adb.busybox.CASUALDataBridge;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CASUALDataBridgeTest {

 
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    Shell shell = new Shell();

 
    @Test
    public void testSendString() {
        try {
            if (!new ADBTools().isConnected()) {
                return;
            }
            shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm /sdcard/woot"});
            System.out.println("sendString");
            String send = "wooaoas";
            CASUALDataBridge instance = new CASUALDataBridge();
            instance.sendString(send, "/sdcard/woot");
            String result = shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "cat /sdcard/woot;"});
            System.out.println(result);
            shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm /sdcard/woot"});
            assert (result.contains(send));
        } catch (UnknownHostException ex) {
            Logger.getLogger(CASUALDataBridgeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(CASUALDataBridgeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(CASUALDataBridgeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CASUALDataBridgeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testSendFile() throws Exception {
        if (!new ADBTools().isConnected()) {
            return;
        }
        System.out.println("sendFile");
        File f = new File("../../CASPAC/testpak.zip");
        String remoteFileName = "/sdcard/testpak";
        CASUALDataBridge instance = new CASUALDataBridge();
        long retval=instance.sendFile(f, remoteFileName);
        assert (retval==f.length());
        shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "pull", "/sdcard/testpak", "test"});
        String originalmd5 = new CASUAL.crypto.MD5sum().getLinuxMD5Sum(f).split(" ")[0];
        File test = new File("test");
        String testmd5 = new CASUAL.crypto.MD5sum().getLinuxMD5Sum(test).split(" ")[0];
        System.out.println("original md5:"+originalmd5);
        System.out.println("received md5:"+testmd5);
        test.delete();
        shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm /sdcard/testpak"});
        assert (testmd5.equals(originalmd5));
    }

  
    @Test
    public void testGetFile() throws Exception {
        if (!new ADBTools().isConnected()) return;
        System.out.println("getFile");
        File original = new File("../../CASPAC/testpak.zip");
        String remoteFileName = "/sdcard/testpak.zip";
        File test = new File("./test");
        test.delete();
        shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "push", original.getAbsolutePath(), remoteFileName});
        CASUALDataBridge instance = new CASUALDataBridge();
        instance.getFile(remoteFileName,test);
        String originalmd5 = new CASUAL.crypto.MD5sum().getLinuxMD5Sum(original).split(" ")[0];
        String testmd5 = new CASUAL.crypto.MD5sum().getLinuxMD5Sum(test).split(" ")[0];
        System.out.println(test.getAbsolutePath());

        shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm " + remoteFileName});
        test.delete();
        assert (testmd5.equals(originalmd5));
    }

 
    @Test
    public void testSendStream() throws Exception {
        if (!new ADBTools().isConnected()) return;
        System.out.println("sendStream");
        String expResult = "omfg \n cool! 123456789";
        InputStream input = (InputStream) new ByteArrayInputStream(expResult.getBytes());
        String remoteFileName = "/sdcard/sendstreamtest";
        CASUALDataBridge instance = new CASUALDataBridge();

        long test =instance.sendStream(input, remoteFileName);
        String result = shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "cat " + remoteFileName});
        shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm " + remoteFileName});
        assert (test == expResult.length());
        assertEquals(expResult.replace("\n", ""), result.replace("\n", ""));
    }
}
