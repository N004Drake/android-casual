/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;

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
        Statics.GUI.enableControls(true);
    }
    
    private static void DoRead(DataInputStream dataIn) {
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dataIn));
            String strLine;
            while ((strLine = bReader.readLine()) != null) {
                parseScript();
            }
            //Close the input stream
            dataIn.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        
    }
    private static void parseScript() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
