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
 *  along with this program.  If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package CASUAL;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Logs stuff and things
 *
 * @author Adam Outler adamoutler@gmail.com
 *
 */
public class Log {

    /**
     * output device
     */
    public static PrintStream out = new PrintStream(System.out);

    private static void sendToGUI(String data) {
        if (CASUALSessionData.getInstance().GUI==null){
            CASUALSessionData.getInstance().PreProgress = CASUALSessionData.getInstance().PreProgress + "\n" + data;
        } else if (!data.equals("\n")||!data.isEmpty()) {
            CASUALSessionData.getInstance().GUI.sendString(data + "\n");
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
        if (CASUALSessionData.getInstance().outputGUIVerbosity >= 0) {
            sendToGUI(data);

        }
        if (CASUALSessionData.getInstance().outputLogVerbosity >= 0) {
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
        if (CASUALSessionData.getInstance().outputGUIVerbosity >= 1) {
            sendToGUI(data);
        }
        if (CASUALSessionData.getInstance().outputLogVerbosity >= 1) {
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
        if (CASUALSessionData.getInstance().outputGUIVerbosity >= 2) {
            sendToGUI(data);
        }
        if (CASUALSessionData.getInstance().outputLogVerbosity >= 2) {
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
        if (CASUALSessionData.getInstance().outputGUIVerbosity >= 3) {
            sendToGUI(data);
        }
        if (CASUALSessionData.getInstance().outputLogVerbosity >= 3) {
            out.println("[VERBOSE]" + data);
        }
    }

    /**
     *
     * @param data is data to be written to log
     */
    public static void level4Debug(String data) {
        writeOutToLog("[DEBUG]" + data);

        if (CASUALSessionData.getInstance().outputGUIVerbosity >= 4) {
            sendToGUI(data);
        }
        if (CASUALSessionData.getInstance().outputLogVerbosity >= 4) {
            out.println("[DEBUG]" + data);
        }
    }
    
    public static void insertChars(String data){
        writeOutToLog(data);
        out.print(data);
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
            WriteFile = new FileWriter(CASUALSessionData.getInstance().getTempFolder() + "Log.txt", true);
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
       if (CASUALSessionData.getInstance().GUI==null){
           System.out.print(data);
       } else  {
           CASUALSessionData.getInstance().GUI.sendProgress(data);
       }
       
    }
    
    /**
     *
     * @param data data to be written to screen in real time
     */
    public  static void LiveUpdate(String data) {
        out.print(data);
        if (CASUALSessionData.getInstance().GUI!=null) {
            CASUALSessionData.getInstance().GUI.sendProgress(data);
        }

    }

    /**
     * begins a new line
     */
    public  static void beginLine() {
        out.println();
        if (CASUALSessionData.getInstance().isGUIIsAvailable()) {
            progress("\n");
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
