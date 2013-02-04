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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public Log() {
    }

    private void consoleOut(String data) {
        System.out.println(data);
        if (!"".equals(data)) {
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
    public void level0(String data) {
        if (Statics.ConsoleLevel >= 0) {
            consoleOut(data);

        }
        if (Statics.LogLevel >= 0) {
            debugOut(data);

        }
    }

    /**
     * level 0 is used for normal user data..
     *
     * @param data is data to be written to log
     */
    public void level1(String data) {
        if (Statics.ConsoleLevel >= 1) {
            consoleOut(data);
        }
        if (Statics.LogLevel >= 1) {
            debugOut(data);

        }

    }

    /**
     * level 2 if for debugging data
     *
     * @param data is data to be written to log
     */
    // level 2 is for debugging data
    public void level2(String data) {
        if (Statics.ConsoleLevel >= 2) {
            consoleOut(data);
        }
        if (Statics.LogLevel >= 2) {
            debugOut(data);

        }
    }

    /**
     * level 3 is for verbose data
     *
     * @param data is data to be written to log
     */
    public void level3(String data) {
        if (Statics.ConsoleLevel >= 3) {
            consoleOut(data);
        }
        if (Statics.LogLevel >= 3) {
            debugOut(data);

        }
    }

    public void writeToLogFile(String data) {
        debugOut(data);
    }

    private void debugOut(String data) {
        System.out.println(data);

        FileWriter WriteFile = null;
        try {
            WriteFile = new FileWriter(Statics.TempFolder + "log.txt", true);
        } catch (IOException ex) {
        }
        try (PrintWriter out = new PrintWriter(WriteFile)) {
            Statics.OutFile = out;
            if (Statics.OutFile != null) {
                Statics.LogCreated = true;
            }
            out.print(data + "\n");
        }
    }

    public void progress(String data) {
        try {
            try {
                Statics.ProgressDoc.insertString(Statics.ProgressDoc.getLength(), data, null);
                Statics.ProgressPane.setCaretPosition(Statics.ProgressDoc.getLength());
            } catch (BadLocationException ex) {
                Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (NullPointerException e) {
            level0(data + e.toString());
        }

    }

    public void LiveUpdate(String data) {
        System.out.println(data);
        try {
            Statics.ProgressDoc.insertString(Statics.ProgressDoc.getLength(), data, null);
            Statics.ProgressPane.setCaretPosition(Statics.ProgressDoc.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void beginLine() {
        progress("\n");
    }

    void replaceLine(String data, int position, int length) {
        try {
            Statics.ProgressDoc.remove(position, length);
            Statics.ProgressDoc.insertString(position, data, null);
            Statics.ProgressPane.setCaretPosition(Statics.ProgressDoc.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * @param e is any throwable
     */
    public void errorHandler(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        level0(e.getLocalizedMessage() + "\n" + e.getMessage() + "\n" + e.toString() + "\n" + "\n" + writer.toString());
        level0("A critical error was encoutered.  Please copy the log from About>Show Log and report this issue ");
    }
}
