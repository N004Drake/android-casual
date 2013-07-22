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
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * CASUALAudioSystem handles Sounds
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class AudioHandler {
    public static boolean useSound=false;

    /**
     *playSound plays sounds
     * @param URL path to sound
     */
    public static synchronized void playSound(final String URL) {
        Thread t = new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing
            @Override
            public void run() {
                if (useSound) {
                    AudioInputStream IS;
                    try {
                        byte[] buffer = new byte[4096];
                        IS = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(URL)));
                        AudioFormat Format = IS.getFormat();
                        SourceDataLine line;
                        line = AudioSystem.getSourceDataLine(Format);
                        line.open(Format);
                        line.start();
                        while (IS.available() > 0) {
                            int Len = IS.read(buffer);
                            line.write(buffer, 0, Len);
                        }
                        line.drain();
                        line.close();
                        IS.close();
                        //Don't worry about autio exceptions.  Just turn off audio
                    } catch (            IllegalArgumentException | UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
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
     * @param URLs array of paths to sound
     */
    public static synchronized void playMultipleInputStreams(final String[] URLs) {
        Thread t = new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing
            @Override
            public void run() {
                if (useSound) {
                    byte[] buffer = new byte[4096];
                    int URLEndPosition = URLs.length - 1;
                    int CurrentURL = 0;
                    SourceDataLine line;

                    for (String URL : URLs) {
                        try {
                            AudioInputStream IS;
                            IS = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(URL)));
                            AudioFormat Format = IS.getFormat();
                            line = AudioSystem.getSourceDataLine(Format);
                            line.open(Format);
                            line.start();
                            line.drain();
                            while (IS.available() > 0) {
                                int Len = IS.read(buffer);
                                line.write(buffer, 0, Len);
                            }
                            if (CurrentURL == URLEndPosition) {
                                line.drain(); // wait for the buffer to empty before closing the line
                                line.close();
                                IS.close();
                            }
                            //Don't worry about autio exceptions.  Just turn off audio
                        } catch (IllegalArgumentException | UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                            useSound = false;
                        }
                    }
                }
            }
        });
        t.setName("AudioStream");
        t.start();
    }
}
