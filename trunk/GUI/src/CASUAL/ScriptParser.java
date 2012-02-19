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
    
    public void executeSelectedScriptResource(String Script){
         Log.level3("Selected resource"+Script);
         InputStream ResourceAsStream = getClass().getResourceAsStream(Statics.ScriptLocation+Script+".scr");
         DataInputStream DIS=new DataInputStream(ResourceAsStream);
         executeSelectedScript(DIS);
    }
    
    public void executeSelectedScriptFile(String Script){
        try {
            Log.level3("Selected file"+Script);
            FileInputStream ResourceAsStream=new FileInputStream(Script+".scr");
            DataInputStream DIS=new DataInputStream(ResourceAsStream);
            executeSelectedScript(DIS);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void executeSelectedScript(DataInputStream DIS){
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
