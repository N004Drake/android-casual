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

    Log Log=new Log();
    
    
    public void executeSelectedScript(String Script){
        Statics.GUI.enableControls(false);
        Log.level2("Selected "+ Script + " " );
        //TODO open as datastream and pass to DoRead
        FileInputStream fis = null; 
        InputStreamReader in = null;
        String TextFile="";
        InputStream resourceAsStream = getClass().getResourceAsStream(Statics.ScriptLocation+Script+".scr");
        DataInputStream DIN = new DataInputStream(resourceAsStream);
        Log.level3("Reading "+ Statics.ScriptLocation+Script+".scr");
        doRead(DIN);
       

        
        //doRead(Script);
        Statics.GUI.enableControls(true);
    }
    
    private void doRead(DataInputStream dataIn) {
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dataIn));
            String strLine;
            while ((strLine = bReader.readLine()) != null) {
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
        Log.level3("Original:"+Line);
        //Remove leading spaces
        Line=removeLeadingSpaces(Line);
        //Disregard commented lines
        if (Line.startsWith("#")){
            Log.level3("Ignoring commented line");
        }
        
        
        
    }
    
    private String removeLeadingSpaces(String Line){
        while (Line.startsWith(" ")){
            Log.level3("Removing leading space.");
            Line=Line.replaceFirst(" ", "");
        }
        return Line;
    }
    
}
