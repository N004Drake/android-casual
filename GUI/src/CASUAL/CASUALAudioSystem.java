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
import javax.sound.sampled.*;

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
                if (Statics.UseSound.contains("true") || Statics.UseSound.contains("True")) {
                    try {
                        byte[] buffer = new byte[4096];
                        AudioInputStream IS = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(URL)));
                        AudioFormat Format = IS.getFormat();
                        SourceDataLine Line = AudioSystem.getSourceDataLine(Format);
                        Line.open(Format);
                        Line.start();
                        while (IS.available() > 0) {
                            int Len = IS.read(buffer);
                            Line.write(buffer, 0, Len);
                        }
                        Line.drain();
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
                if (Statics.UseSound.contains("true") || Statics.UseSound.contains("True")) {
                    byte[] buffer = new byte[4096];
                    int URLEndPosition = URLs.length - 1;
                    int CurrentURL = 0;
                    SourceDataLine Line = null;
                    for (String URL : URLs) {
                        try {
                            AudioInputStream IS = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(URL)));
                            AudioFormat Format = IS.getFormat();
                            Line = AudioSystem.getSourceDataLine(Format);

                            Line.open(Format);
                            Line.start();
                            Line.drain();
                            while (IS.available() > 0) {
                                int Len = IS.read(buffer);
                                Line.write(buffer, 0, Len);
                            }
                            if (CurrentURL == URLEndPosition) {
                                Line.drain(); // wait for the buffer to empty before closing the line
                            }
                        } catch (Exception e) {
                            new Log().level3(e.getMessage());
                        }
                        CurrentURL = CurrentURL++;
                    }
                    Line.close();

                }
            }
        }).start();
    }
}
