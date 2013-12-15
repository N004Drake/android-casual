/*CASUALAudioSystem provides audio output for CASUAL. 
 *Copyright (C) 2013  Adam Outler
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package CASUAL;

import java.io.BufferedInputStream;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;

/**
 * CASUALAudioSystem handles Sounds
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class AudioHandler {

    /**
     * True if sound is to be used.
     */
    public static volatile boolean useSound = false;

    /**
     * playSound plays sounds
     *
     * @param url path to sound
     */
    public static synchronized void playSound(final String url) {
        Thread t = new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing
            @Override
            public void run() {
                if (useSound) {
                    AudioInputStream is;
                    try {
                        byte[] buffer = new byte[4096];
                        is = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(url)));
                        AudioFormat Format = is.getFormat();
                        SourceDataLine line;
                        line = AudioSystem.getSourceDataLine(Format);
                        line.open(Format);
                        line.start();
                        while (is.available() > 0) {
                            int Len = is.read(buffer);
                            line.write(buffer, 0, Len);
                        }
                        line.drain();
                        line.close();
                        is.close();
                        //Don't worry about autio exceptions.  Just turn off audio
                    } catch (IllegalArgumentException ex) {
                        useSound = false;
                    } catch (UnsupportedAudioFileException ex) {
                        useSound = false;
                    } catch (IOException ex) {
                        useSound = false;
                    } catch (LineUnavailableException ex) {
                        useSound = false;
                    }
                }
            }
        });
        t.setName("Audio");
        t.start();
    }

    /**
     * plays multiple sounds
     *
     * @param urls array of paths to sound
     */
    public static synchronized void playMultipleInputStreams(final String[] urls) {
        Thread t;
        t = new Thread(new Runnable() {
// the wrapper thread is unnecessary, unless it blocks on the Clip finishing
            @Override
            public void run() {
                if (useSound) {

                    for (String url : urls) {

                        try {
                            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(url));

                            AudioFormat format = audioIn.getFormat();
                            DataLine.Info info = new DataLine.Info(Clip.class, format);
                            Clip clip = (Clip) AudioSystem.getLine(info);

                            clip.open(audioIn);
                            clip.start();
                            sleepTillEndOfClip(clip);
                            clip.drain();
                        } catch (IOException error) {
                            new Log().level3Verbose("File Not Found");
                        } catch (UnsupportedAudioFileException ex) {
                            Logger.getLogger(AudioHandler.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (LineUnavailableException ex) {
                            Logger.getLogger(AudioHandler.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (java.lang.IllegalArgumentException ex) {
                            Logger.getLogger(AudioHandler.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AudioHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }

            }

            private void sleepTillEndOfClip(Clip clip) throws InterruptedException {
                Thread.sleep(clip.getMicrosecondLength()/5000);
            }
        });
        t.setName("AudioStream");
        
        t.start();

    }
}
