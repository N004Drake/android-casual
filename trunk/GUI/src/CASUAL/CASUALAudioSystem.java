/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * CASUALAudioSystem handles Sounds
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALAudioSystem {

    /*
     * playSound plays sounds
     */
    public static synchronized void playSound(final String URL) {
        new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing, see comments

            public void run() {
                if (Statics.UseSound.equals("true")||Statics.UseSound.equals("True")){
                try {
                    byte[] buffer = new byte[4096];
                    AudioInputStream IS = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(URL));
                    AudioFormat Format = IS.getFormat();
                    SourceDataLine Line = AudioSystem.getSourceDataLine(Format);
                    Line.open(Format);
                    Line.start();
                    while (IS.available() > 0) {
                        int Len = IS.read(buffer);
                        Line.write(buffer, 0, Len);
                    }
                    Line.close();
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
            }
        }).start();
    }
    /*
     * plays multiple sounds
     */

    public static synchronized void playMultipleInputStreams(final String[] URLs) {
        new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing, see comments

            public void run() {
                if (Statics.UseSound.equals("true")||Statics.UseSound.equals("True")){
                byte[] buffer = new byte[4096];
                for (String URL : URLs) {
                    try {
                        AudioInputStream IS = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(URL));
                        AudioFormat Format = IS.getFormat();
                        SourceDataLine Line = AudioSystem.getSourceDataLine(Format);
                        Line.open(Format);
                        Line.start();
                        Line.drain();
                        while (IS.available() > 0) {
                            int Len = IS.read(buffer);
                            Line.write(buffer, 0, Len);
                        }
                        //Line.drain(); //**[DEIT]** wait for the buffer to empty before closing the line
                        Line.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            }
        }).start();
    }
}
