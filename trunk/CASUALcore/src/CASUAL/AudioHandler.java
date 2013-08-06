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
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * CASUALAudioSystem handles Sounds
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class AudioHandler {

    public static volatile boolean useSound = false;

    /**
     * playSound plays sounds
     *
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
                    } catch (IllegalArgumentException | UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
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
     * @param URLs array of paths to sound
     */
    public static synchronized void playMultipleInputStreams(final String[] URLs) {
        Thread t; 
        t = new Thread(new Runnable() { 
// the wrapper thread is unnecessary, unless it blocks on the Clip finishing
     @Override
     public void run() {
         if (useSound) {
             try {
                 long length=150000000;
                 
                 AudioInputStream ex=AudioSystem.getAudioInputStream(getClass().getResourceAsStream(URLs[0]));
                 AudioFormat format=ex.getFormat();
                 
                 List<AudioInputStream> list =new ArrayList<>();
                 for (String URL : URLs) {
                     AudioInputStream x=AudioSystem.getAudioInputStream(getClass().getResourceAsStream(URL));
                     length=length+x.getFrameLength();
                     list.add(x);
                         
                 }
                 AudioInputStream appendedFiles = new AudioInputStream(new SequenceInputStream(Collections.enumeration(list)),format,length);

                 Clip clip = AudioSystem.getClip();
                 clip.open(appendedFiles);
                 clip.start();

             } catch (LineUnavailableException ex) {
                 Logger.getLogger(AudioHandler.class.getName()).log(Level.SEVERE, null, ex);
             } catch (IOException ex) {
                 Logger.getLogger(AudioHandler.class.getName()).log(Level.SEVERE, null, ex);
             } catch (UnsupportedAudioFileException ex) {
                 Logger.getLogger(AudioHandler.class.getName()).log(Level.SEVERE, null, ex);
             }
         }
     }
 });
        t.setName("AudioStream");
        t.start();
        
    }
}
