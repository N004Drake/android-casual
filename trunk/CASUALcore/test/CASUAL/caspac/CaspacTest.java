/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.caspac;

import CASUAL.Statics;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CaspacTest {
    Caspac test;
    public CaspacTest() {
        Statics.GUI=new GUI.testing.automatic();
        try {
            test=new Caspac(new File("../../CASPAC/QualityControl/echoTest.zip"),Statics.getTempFolder(),0);
            test.loadFirstScriptFromCASPAC();
            this.instance = new Caspac(new File("../../CASPAC/testpak.zip"), Statics.getTempFolder(), 0);
            System.out.println(instance.CASPAC.getCanonicalPath());
        } catch (IOException ex) {
            Logger.getLogger(CaspacTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    Caspac instance;

    /**
     * Test of removeScript method, of class Caspac.
     */
    @Test
    public void testRemoveScript() {
        try {
            System.out.println("removeScript");
            instance.load();
            Script s = new Script(instance.scripts.get(0));
            instance.getScriptByName("foobar");
            instance.removeScript(instance.getScriptByName("foobar"));
            assert (!instance.scripts.contains("foobar"));
        } catch (ZipException ex) {
            Logger.getLogger(CaspacTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CaspacTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testSetActiveScript() {
        System.out.println("setActiveScript");
        Script s = instance.getScriptByName("foobar");
        instance.setActiveScript(s);
        assert (instance.getActiveScript().name.equals("foobar"));

    }

    @Test
    public void testGetActiveScript() {
        System.out.println("getActiveScript");

        for (int i = 0; i < 10; i++) {
            instance.getScriptByName("script" + Integer.toString(i));
        }
        instance.setActiveScript(instance.scripts.get(instance.scripts.size() - 1));
        Script result = instance.getActiveScript();
        assert (result.name.equals(instance.scripts.get(instance.scripts.size() - 1).name));
    }


    @Test
    public void testSetBuild() {
        System.out.println("setBuild");
        Properties p = test.build.buildProp;
        p.setProperty("Developer.DonateLink", "OMFG");
        instance.setBuild(p);
        assert (instance.build.donateLink.equals("OMFG"));
    }
    
    @Test
    public void testEchoTestVerification() throws Exception {
        System.out.println("write");
        assert test.type==0;
        assert test.logo==null;
        assert test.CASPACsrc==null;
        assert test.overview.equals("test");
        assert test.build.developerName.equals("test");
        assert test.build.donateLink.equals("test");
        assert test.build.windowTitle.equals("test");
        assert test.build.bannerPic.equals("");
        assert test.build.bannerText.equals("test");
        assert test.build.executeButtonText.equals("test");
        assert test.build.audioEnabled==true;
        assert test.build.usePictureForBanner==false;
        assert test.build.alwaysEnableControls==false;
        assert test.TempFolder.equals(Statics.getTempFolder());
        assert test.build.developerDonateButtonText.equals("test");
        assert test.build.developerDonateButtonText.equals("test");
        assert test.CASPAC.getCanonicalFile().equals(new File("../../CASPAC/QualityControl/echoTest.zip").getCanonicalFile());
        assert test.scripts.get(0).extractionMethod==0;
        assert test.scripts.get(0).name.equals("echoTest");
        String x=test.scripts.get(0).tempDir;
        assert test.scripts.get(0).tempDir.contains(Statics.getTempFolder()+test.scripts.get(0).name);
        assert test.scripts.get(0).scriptContents.equals("$ECHO test");
        assert test.scripts.get(0).individualFiles.size() ==0;
        assert test.scripts.get(0).metaData.minSVNversion.equals("0");
        assert test.scripts.get(0).metaData.scriptRevision.equals("0");
        assert test.scripts.get(0).metaData.uniqueIdentifier.equals("test");
        assert test.scripts.get(0).metaData.supportURL.equals("test");
        assert test.scripts.get(0).metaData.updateMessage.equals("test");
        assert test.scripts.get(0).metaData.killSwitchMessage.equals("test");
        assert test.scripts.get(0).metaData.md5s.contains("c9aa2a1d8bce6a47bc7599d62c475658  echoTest.scr");
        assert test.scripts.get(0).metaData.md5s.contains("58eba1c6a6b700f8b42b143f82942176  echoTest.txt");
        assert test.scripts.get(0).metaData.md5s.contains("76cdb2bad9582d23c1f6f4d868218d6c  echoTest.zip");
        assert test.scripts.get(0).discription.equals("Describe your script here");
        assert test.scripts.get(0).scriptContinue==false;
    }
}