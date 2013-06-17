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
import javax.swing.text.BadLocationException;

/**
 *
 * @author adam Logging levels: Logging levels are set in Statics
 *
 * Level0: silent, keep this for future critical tasks Level1: Information for
 * user to see Level2: Information for developers to see Level3: verbose
 * information, use for conversions and random tasks
 *
 */
public class Log {

    public static PrintStream out = new PrintStream(System.out);

    public Log() {
    }

    private void sendToGUI(String data) {
        if (Statics.useGUI && !"".equals(data)) {
            if (!"\n".equals(data)) {
                try {
                    try {
                        Statics.ProgressDoc.insertString(Statics.ProgressDoc.getLength(), data + "\n", null);
                        try {
                            Statics.ProgressPane.setCaretPosition(Statics.ProgressDoc.getLength());
                        } catch (java.lang.IllegalArgumentException x) {
                        }
                    } catch (BadLocationException ex) {
                    }


                } catch (NullPointerException e) {

                    Statics.PreProgress = Statics.PreProgress + "\n" + data;
                    if (Statics.PreProgress.startsWith("\n")) {
                        Statics.PreProgress = Statics.PreProgress.replaceFirst("\n", "");
                    }
                }

            }
        }
    }

    /**
     * level 0 is used for errors.. basically silent. Use level 1 for for most
     * tasks
     *
     * @param data is data to be written to log
     */
    public void level0Error(String data) {
        writeOutToLog("[ERROR]" + data);
        if (Statics.GUIVerboseLevel >= 0) {
            sendToGUI(data);

        }
        if (Statics.CommandLineVerboseLevel >= 0) {
            out.println("[ERROR]" + data);

        }
    }

    /**
     * level 1 is used for interactive tasks.
     *
     * @param data is data to be written to log
     */
    public void Level1Interaction(String data) {
        writeOutToLog("[INTERACTION]" + data);
        if (Statics.GUIVerboseLevel >= 1) {
            sendToGUI(data);
        }
        if (Statics.CommandLineVerboseLevel >= 1) {
            out.println("[INTERACTION]" + data);

        }

    }

    /**
     * level 2 if for debugging data
     *
     * @param data is data to be written to log
     */
    // level 2 is for info-type data
    public void level2Information(String data) {
        writeOutToLog("[INFO]" + data);
        if (Statics.GUIVerboseLevel >= 2) {
            sendToGUI(data);
        }
        if (Statics.CommandLineVerboseLevel >= 2) {
            out.println("[INFO]" + data);
        }
    }

    /**
     * level 3 is for verbose data
     *
     * @param data is data to be written to log
     */
    public void level3Verbose(String data) {
        writeOutToLog("[VERBOSE]" + data);
        if (Statics.GUIVerboseLevel >= 3) {
            sendToGUI(data);
        }
        if (Statics.CommandLineVerboseLevel >= 3) {
            out.println("[VERBOSE]" + data);
        }
    }

    public void level4Debug(String data) {
        writeOutToLog("[DEBUG]" + data);

        if (Statics.GUIVerboseLevel >= 4) {
            sendToGUI(data);
        }
        if (Statics.CommandLineVerboseLevel >= 4) {
            out.println("[DEBUG]" + data);
        }
    }

    public void writeToLogFile(String data) {
        writeOutToLog(data);
    }

    private void writeOutToLog(String data) {
        FileWriter WriteFile;
        try {
            WriteFile = new FileWriter(Statics.TempFolder + "log.txt", true);
        } catch (IOException ex) {
            out.println("Attempted to write to log but could not.");
            return;
        }
        try (PrintWriter output = new PrintWriter(WriteFile)) {
            output.write(data + "\n");

            Statics.OutFile = output;
            if (Statics.OutFile != null) {
                Statics.LogCreated = true;
            }
        }
    }
    private static String progressBuffer = "";
    int lastNewLine = 100;

    public void progress(String data) {
        progressBuffer = progressBuffer + data;
        if (Statics.useGUI && Statics.ProgressDoc != null) {
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
                new Log().errorHandler(ex);
            } catch (NullPointerException e) {
                level0Error(data + e.toString());
            }
            if (data.contains("\n")) {
                writeToLogFile(progressBuffer.replace("\n", ""));
                progressBuffer = "";
                lastNewLine = Statics.ProgressPane.getCaretPosition() - 1;
            }
        } else {
            out.print(data);
        }

    }

    public void LiveUpdate(String data) {
        out.println(data);
        if (Statics.useGUI) {
            try {
                Statics.ProgressDoc.insertString(Statics.ProgressDoc.getLength(), data, null);
                Statics.ProgressPane.setCaretPosition(Statics.ProgressDoc.getLength());
            } catch (BadLocationException ex) {
                new Log().errorHandler(ex);
            }
        }

    }

    /*
     * begins a new line
     */
    public void beginLine() {
        out.println();
        if (Statics.useGUI) {
            progress("\n");
        }
    }

    /*
     * replaces a line of text
     */
    void replaceLine(String data, int position, int length) {
        if (Statics.useGUI) {
            try {
                Statics.ProgressDoc.remove(position, length);
                Statics.ProgressDoc.insertString(position, data, null);
                Statics.ProgressPane.setCaretPosition(Statics.ProgressDoc.getLength());
            } catch (BadLocationException ex) {
                new Log().errorHandler(ex);
            }
        }
    }

    /**
     *
     * @param e is any Throwable.
     */
    public void errorHandler(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        level0Error("[CRITICAL]" + e.getLocalizedMessage() + "\n" + e.getMessage() + "\n" + e.toString() + "\n" + "\n" + writer.toString());
        level0Error("A critical error was encoutered.  Please copy the log from About>Show Log and report this issue ");
    }

    void initialize() {
        out = new PrintStream(System.out);
    }
}
