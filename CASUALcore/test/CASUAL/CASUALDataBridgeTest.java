/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.File;
import java.io.InputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class CASUALDataBridgeTest {
    
    public CASUALDataBridgeTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    Shell shell = new Shell();
    @Test
    public void testSendString() {
        System.out.println("sendString");
        String send = "wooasdfadfasajslfdkajsdlkfjaslkfdjalksjdf;laksjfdlkasjfdlkasjfdlkahsfdkjhaskdjfahskjfdhasjkfhdaslkfdasfdasfdtwootaslakadfasdfasfasfdasfdas\r\nfdasfdasfsdfgsdfgsdfgsdfgsdfgdasfdasfd sldkajskl\r\n\r\n";
        CASUALDataBridge instance = new CASUALDataBridge();
        instance.sendString(send,"sdcard/woot");
        String result=shell.sendShellCommand(new String[]{ADBTools.getADBCommand(),"shell","cat /sdcard/woot;"});
        System.out.println(result);
        assert (result.contains(send));
        
        // TODO review the generated test code and remove the default call to fail.
        
    }

 /*
    @Test
    public void testSendFile() throws Exception {
        System.out.println("sendFile");
        File f = new File("../../CASPAC/testpak.zip");
        String remoteFileName = "/sdcard/testpak";
        CASUALDataBridge instance = new CASUALDataBridge();
        instance.sendFile(f, remoteFileName);
        shell.sendShellCommand(new String[]{ADBTools.getADBCommand(),"pull","/sdcard/testpak","test"});
        String originalmd5=new CASUAL.crypto.MD5sum().getLinuxMD5Sum(f);
        File test=new File("test");
        String testmd5=new CASUAL.crypto.MD5sum().getLinuxMD5Sum(test);
        System.out.println(originalmd5);
        System.out.println("test");
        System.out.println(testmd5);
        
        assert (testmd5.equals(originalmd5));
        test.delete();
        
  
    }*/
}