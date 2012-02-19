/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public class ScriptParser {

    Log Log = new Log();
    int LinesInScript = 0;
    int CurrentLine;

    public void executeSelectedScriptResource(String Script) {
        Log.level3("Selected resource" + Script);
        LinesInScript = countResourceLines(Script);
        Log.level3("Lines in Script " + LinesInScript);

        InputStream ResourceAsStream = getClass().getResourceAsStream(Statics.ScriptLocation + Script + ".scr");
        DataInputStream DIS = new DataInputStream(ResourceAsStream);
        executeSelectedScript(DIS);
    }

    public int countFileLines(String Filename) {
        InputStream IS = null;
        int Lines = 0;
        try {
            IS = new BufferedInputStream(new FileInputStream(Filename));

            Lines = countISLines(IS);


        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                IS.close();
            } catch (IOException ex) {
                Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return Lines;

    }

    public int countResourceLines(String ResourceName) {
        InputStream IS = getClass().getResourceAsStream(Statics.ScriptLocation + ResourceName + ".scr");
        int Lines = 0;
        try {
            Lines = countISLines(IS);
        } catch (IOException ex) {
            Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                IS.close();
            } catch (IOException ex) {
                Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Lines;
    }

    private int countISLines(InputStream IS) throws IOException {
        int count = 0;
        try {
            byte[] c = new byte[1024];
            int ReadChars = 0;
            while ((ReadChars = IS.read(c)) != -1) {
                for (int i = 0; i < ReadChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
        } finally {
            IS.close();
        }

        return count + 1;

    }

    public void executeSelectedScriptFile(String Script) {

        try {
            Log.level3("Selected file" + Script);
            LinesInScript = countFileLines(Script + ".scr");
            Log.level3("Lines in SCript " + LinesInScript);
            FileInputStream ResourceAsStream = new FileInputStream(Script + ".scr");
            DataInputStream DIS = new DataInputStream(ResourceAsStream);
            executeSelectedScript(DIS);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void executeSelectedScript(DataInputStream DIS) {
        CurrentLine=0;
        Statics.ProgressBar.setMaximum(LinesInScript);
        Log.level3("Reading datastream" + DIS);
        Statics.GUI.enableControls(false);
        doRead(DIS);
        Statics.GUI.enableControls(true);
    }

    private void doRead(DataInputStream dataIn) {
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dataIn));
            String strLine;

            while ((strLine = bReader.readLine()) != null) {
                CurrentLine++;
                Statics.ProgressBar.setValue(CurrentLine);
                
                
                parseScript(strLine);
            }
            //Close the input stream
            dataIn.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }

    private void parseScript(String Line) {
        //Log original line at a high level to be ignored for production 
        Log.level3("Original:" + Line);
        //Remove leading spaces
        Line = removeLeadingSpaces(Line);
        //Disregard commented lines
        if (Line.startsWith("#")) {
            Log.level3("Ignoring commented line");
        }



    }

    private String removeLeadingSpaces(String Line) {
        while (Line.startsWith(" ")) {
            Log.level3("Removing leading space.");
            Line = Line.replaceFirst(" ", "");
        }
        return Line;
    }
}
