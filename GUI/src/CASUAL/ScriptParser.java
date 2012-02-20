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
    String ScriptTempFolder="";
    
    /*
     * Executes a selected script as a resource reports to Log class.
     */
    public void executeSelectedScriptResource(String Script) {
        Log.level3("Selected resource" + Script);
        CountLines CountLines = new CountLines();
        LinesInScript = CountLines.countResourceLines(Script);
        Log.level3("Lines in Script " + LinesInScript);
        ScriptTempFolder=Statics.TempFolder+Script+Statics.Slash;

        InputStream ResourceAsStream = getClass().getResourceAsStream(Statics.ScriptLocation + Script + ".scr");
        DataInputStream DIS = new DataInputStream(ResourceAsStream);
        executeSelectedScript(DIS);
    }

    /*
     * executes a CASUAL script from a file Reports to Log
     *
     */
    public void executeSelectedScriptFile(String Script) {
        Log.level3("Selected file" + Script);
        CountLines CountLines = new CountLines();
        
        ScriptTempFolder=Statics.TempFolder+(new File(Script).getName())+Statics.Slash;
        LinesInScript = CountLines.countFileLines(Script + ".scr");
        Log.level3("Lines in SCript " + LinesInScript);
        DataInputStream DIS;
        try {
            FileInputStream FileAsStream;
            FileAsStream = new FileInputStream(Script + ".scr");
            DIS = new DataInputStream(FileAsStream);
            executeSelectedScript(DIS);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void commandHandler(String Line) {
        //Log original line at a high level to be ignored for production 

        //Remove leading spaces
        Line = removeLeadingSpaces(Line);
        //Disregard blank lines
        if (Line.equals("")) return;
        
        //Disregard commented lines
        if (Line.startsWith("#")){
            Log.level3("Ignoring commented line"+Line);
            return;
        }
        if (Line.contains("$SLASH")){
            Line=Line.replace("$SLASH", Statics.Slash);
        }
        if (Line.contains("$ZIPFILE")){
            Line=Line.replace("$ZIPFILE", ScriptTempFolder);
        }
        if (Line.startsWith("$ECHO ")){
            Line=Line.replace("$ECHO ","");
            Log.level1(Line);
            return;
        }
        if (Line.startsWith("$ECHO")){
            Line=Line.replace("$ECHO","");
            Log.level1(Line);
            return;
        }
        Log.level3("Final:"+Line);
    }
    


    
    private void executeSelectedScript(DataInputStream DIS) {
        CurrentLine = 0;
        Statics.ProgressBar.setMaximum(LinesInScript);
        Log.level3("Reading datastream" + DIS);
        doRead(DIS);
    }

    private void doRead(DataInputStream dataIn) {
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dataIn));
            String strLine;

            while ((strLine = bReader.readLine()) != null) {
                CurrentLine++;
                Statics.ProgressBar.setValue(CurrentLine);


                commandHandler(strLine);
            }
            //Close the input stream
            dataIn.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
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
