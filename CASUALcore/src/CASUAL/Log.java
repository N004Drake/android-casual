/*Log provides logging tools 
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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;

/**
 * Logs things
 *
 * @author AdamOutler Logging levels: Logging levels are set in Statics

 Level0: silent, keep this for future critical tasks Level1: Information for
 user to see Level2: Information for developers to see Level3: verbose
 information, use for conversions and random tasks
 *
 */
public class Log {

    /**
     * output device
     */
    public static PrintStream out = new PrintStream(System.out);

    private static void sendToGUI(String data) {
        if (Statics.GUI!=null&& (Statics.isGUIIsAvailable() || Statics.GUI.isDummyGUI()) && !"".equals(data) && !"\n".equals(data)) {
            try {
                Statics.ProgressDoc.insertString(Statics.ProgressDoc.getLength(), data + "\n", null);
                Statics.ProgressPane.setCaretPosition(Statics.ProgressDoc.getLength());
            } catch (NullPointerException e) {
                Statics.PreProgress = Statics.PreProgress + "\n" + data;
                if (Statics.PreProgress.startsWith("\n")) {
                    Statics.PreProgress = Statics.PreProgress.replaceFirst("\n", "");
                }
            } catch (BadLocationException ex) {
                //nothing to do, GUI is not running
            }
        }
    }

    /**
     * removes all information from the progress document. (GUI log)
     */
    public static void clearGUI() {
        try {
            Statics.ProgressDoc.remove(0, Statics.ProgressDoc.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * level 0 is used for errors.. basically silent. Use level 1 for for most
     * tasks
     *
     * @param data is data to be written to log
     */
    public static void level0Error(String data) {
        if (data.startsWith("@")) {
            data = Translations.get(data);
        }
        writeOutToLog("[ERROR]" + data);
        if (Statics.outputGUIVerbosity >= 0) {
            sendToGUI(data);

        }
        if (Statics.outputLogVerbosity >= 0) {
            out.println("[ERROR]" + data);

        }
    }

    /**
     * level 1 is used for interactive tasks.
     *
     * @param data is data to be written to log
     */
    public static void Level1Interaction(String data) {
        if (data.startsWith("@")) {
            data = Translations.get(data);
        }
        writeOutToLog("[INTERACTION]" + data);
        if (Statics.outputGUIVerbosity >= 1) {
            sendToGUI(data);
        }
        if (Statics.outputLogVerbosity >= 1) {
            out.println("[INTERACTION]" + data);

        }

    }

    /**
     * level 2 if for debugging data
     *
     * @param data is data to be written to log
     */
    // level 2 is for info-type data
    public static void level2Information(String data) {
        if (data.startsWith("@")) {
            data = Translations.get(data);
        }
        writeOutToLog("[INFO]" + data);
        if (Statics.outputGUIVerbosity >= 2) {
            sendToGUI(data);
        }
        if (Statics.outputLogVerbosity >= 2) {
            out.println("[INFO]" + data);
        }
    }

    /**
     * level 3 is for verbose data
     *
     * @param data is data to be written to log
     */
    public static void level3Verbose(String data) {
        writeOutToLog("[VERBOSE]" + data);
        if (Statics.outputGUIVerbosity >= 3) {
            sendToGUI(data);
        }
        if (Statics.outputLogVerbosity >= 3) {
            out.println("[VERBOSE]" + data);
        }
    }

    /**
     *
     * @param data is data to be written to log
     */
    public static void level4Debug(String data) {
        writeOutToLog("[DEBUG]" + data);

        if (Statics.outputGUIVerbosity >= 4) {
            sendToGUI(data);
        }
        if (Statics.outputLogVerbosity >= 4) {
            out.println("[DEBUG]" + data);
        }
    }

    /**
     *
     * @param data to be written to log file
     */
    public static void writeToLogFile(String data) {
        writeOutToLog(data);
    }

    private static synchronized void writeOutToLog(String data) {
        FileWriter WriteFile;
        try {
            WriteFile = new FileWriter(Statics.getTempFolder() + "Log.txt", true);
            PrintWriter output = new PrintWriter(WriteFile);
            output.write(data + "\n");
            WriteFile.close();
            output.close();
        } catch (IOException ex) {
            out.println("Attempted to write to log but could not.");
        }

    }
    private static String progressBuffer = "";
    static int lastNewLine = 100;

    /**
     *
     * @param data data to be written to progress on screen
     */
    public  static void progress(String data) {
        progressBuffer = progressBuffer + data;
        if (Statics.isGUIIsAvailable() && Statics.ProgressDoc != null) {
            try {

                if (data.contains("\b")) {
                    writeToLogFile(progressBuffer);
                    progressBuffer = "";
                    Statics.ProgressDoc.remove(lastNewLine, Statics.ProgressDoc.getLength() - lastNewLine);
                    //lastNewLine=Statics.ProgressDoc.getLength();

                }
                Statics.ProgressDoc.insertString(Statics.ProgressDoc.getLength(), data, null);
                Statics.ProgressPane.setCaretPosition(Statics.ProgressDoc.getLength());

            } catch (BadLocationException ex) {
                Log.errorHandler(ex);
            } catch (NullPointerException e) {
                level0Error(data + e.toString());
            }
            if (data.contains("\n")) {
                writeToLogFile(progressBuffer.replace("\n", ""));
                progressBuffer = "";
                //TODO implement StringBuilder and use that for Log. 
                //lastNewLine = Statics.GUI.ProgressPane.getCaretPosition() - 1;
            }
        } else {
            out.print(data);
        }

    }

    /**
     *
     * @param data data to be written to screen in real time
     */
    public  static void LiveUpdate(String data) {
        out.println(data);
        if (Statics.isGUIIsAvailable()) {
            try {
                Statics.ProgressDoc.insertString(Statics.ProgressDoc.getLength(), data, null);
                Statics.ProgressPane.setCaretPosition(Statics.ProgressDoc.getLength());
            } catch (BadLocationException ex) {
                Log.errorHandler(ex);
            }
        }

    }

    /**
     * begins a new line
     */
    public  static void beginLine() {
        out.println();
        if (Statics.isGUIIsAvailable()) {
            progress("\n");
        }
    }

    /**
     * inserts a line of text to a location. Deletes data at position and lenth
     * specified allowing for a replacement effect. If length 0 is specified the
     * data will be only inserted.
     *
     * @param data Data to be inserted
     * @param position position to start
     * @param length length of text to remove.
     */
    public  static void replaceLine(String data, int position, int length) {
        if (Statics.isGUIIsAvailable()) {
            try {
                Statics.ProgressDoc.remove(position, length);
                Statics.ProgressDoc.insertString(position, data, null);
                Statics.ProgressPane.setCaretPosition(Statics.ProgressDoc.getLength());
            } catch (BadLocationException ex) {
            }
        }
    }

    /**
     *
     * @param e is any Throwable.
     */
    public  static void errorHandler(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        level0Error("[CRITICAL]" + e.getLocalizedMessage() + "\n" + e.getMessage() + "\n" + e.toString() + "\n" + "\n" + writer.toString());
        level0Error("@criticalError");
    }

    static void initialize() {
        out = new PrintStream(System.out);
    }
}
