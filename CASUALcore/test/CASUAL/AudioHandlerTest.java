/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class AudioHandlerTest {
    
    public AudioHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of playSound method, of class AudioHandler.
     */
    @Test
    public void testPlaySound() throws InterruptedException {
        AudioHandler.useSound=true;
        System.out.println("playSound");
        String URL = "/CASUAL/resources/sounds/CASUAL.wav";
        AudioHandler.playSound(URL);
        Thread.sleep(1000);
    }

    /**
     * Test of playMultipleInputStreams method, of class AudioHandler.
     */
    @Test
    public void testPlayMultipleInputStreams() throws InterruptedException {
        AudioHandler.useSound=true;
        System.out.println("playMultipleInputStreams");
        String[] URLs = {"/CASUAL/resources/sounds/2.wav","/CASUAL/resources/sounds/3.wav","/CASUAL/resources/sounds/4.wav","/CASUAL/resources/sounds/5.wav","/CASUAL/resources/sounds/6.wav","/CASUAL/resources/sounds/7.wav","/CASUAL/resources/sounds/8.wav","/CASUAL/resources/sounds/9OrMore.wav","/CASUAL/resources/sounds/DevicesDetected.wav","/CASUAL/resources/sounds/Disconnected.wav","/CASUAL/resources/sounds/InputRequested.wav","/CASUAL/resources/sounds/Notification.wav","/CASUAL/resources/sounds/PermissionEscillation.wav","/CASUAL/resources/sounds/RequestToContinue.wav"};
        AudioHandler.playMultipleInputStreams(URLs);
        Thread.sleep(15000);

    }
}