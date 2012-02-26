/*
 * Copyright (c) 2012 Adam Outler
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package CASUAL;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author adam
 */
public class ScriptParser {
    boolean ScriptContinue=true;
    Log Log = new Log();
    int LinesInScript = 0;
    int CurrentLine;
    String ScriptTempFolder="";
    String ScriptName="";
    /*
     * Executes a selected script as a resource reports to Log class.
     */
    public void executeSelectedScriptResource(String Script) {
        Log.level3("Selected resource" + Script);
        ScriptName=Script;
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
        ScriptName=Script;
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

    /*
     * Script Handler contains all script commands and
     * will execute commands
     */
    private void commandHandler(String Line) {

        //Remove leading spaces
        Line = removeLeadingSpaces(Line);
        
        
        //Disregard commented lines
        if (Line.startsWith("#")){
            Log.level3("Ignoring commented line"+Line);
            return;
        }
        
        //Disregard blank lines
        if (Line.equals("")) return;
        

        
        //replace $SLASH with "\" for windows or "/" for linux and mac
        if (Line.contains("$SLASH")){
            Line=Line.replace("$SLASH", Statics.Slash);
        }
        
        //reference to the Script's .zip file
        if (Line.contains("$ZIPFILE")){
            Line=Line.replace("$ZIPFILE", ScriptTempFolder);
        }
        
        
        //Disregard commented lines
        if (Line.startsWith("#")){
            Log.level3("Ignoring commented line"+Line);
            return;
        }
        //$ECHO command will display text in the main window
        if (Line.startsWith("$ECHO")){
            Line=Line.replace("$ECHO","");
            Line=removeLeadingSpaces(Line);
            Log.level1(Line);
            return;
        }
        // $USERNOTIFICATION will launch a textbox and stop all commands
        if (Line.startsWith("$USERNOTIFICATION")){
            if (Statics.UseSound.contains("true")){
                //CASUALAudioSystem CAS = new CASUALAudioSystem();
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/Notification.wav");
            }
            Line=Line.replace("$USERNOTIFICATION","");
            Line=removeLeadingSpaces(Line);
            if (Line.contains(",")){
                String[] Message=Line.split(",");
                JOptionPane.showMessageDialog(Statics.GUI,
                     Message[1],
                     Message[0],
                     JOptionPane.INFORMATION_MESSAGE);                
            } else {
                JOptionPane.showMessageDialog(Statics.GUI,
                     Line,
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
            
            //TODO: this
          if (Line.startsWith("$USERCANCELOPTION")){
            if (Statics.UseSound.contains("true")){
                //CASUALAudioSystem CAS = new CASUALAudioSystem();
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/RequestToContinue.wav");
            }
              Line=Line.replace("$USERCANCELOPTION","");
                if (Line.contains(",")){
                    String[] Message=Line.split(",");
                    int n = JOptionPane.showConfirmDialog(
                        Statics.GUI,
                        Message[1],
                        Message[0],
                        JOptionPane.YES_NO_OPTION);  
                    if (n==JOptionPane.NO_OPTION) {
                        Log.level0(ScriptName+ " canceled at user request");
                        ScriptContinue=false;
                        return;
                    }
                } else {
                    int n = JOptionPane.showConfirmDialog(
                        Statics.GUI,
                        Line,
                        "Do you wish to continue?",
                        JOptionPane.YES_NO_OPTION);
                    if (n==JOptionPane.NO_OPTION) {
                        Log.level0(ScriptName+ " canceled at user request");
                        ScriptContinue=false;
                        return;
                    }
                }
           }

     
        
        
        //final line output for debugging purposes
        Log.level3("COMMAND TEST"+Statics.AdbDeployed+" "+Line);
    }
    


    
    private void executeSelectedScript(DataInputStream DIS) {
        CurrentLine = 1;
        Statics.ProgressBar.setMaximum(LinesInScript);
        Log.level3("Reading datastream" + DIS);
        doRead(DIS);
    }

    private void doRead(DataInputStream dataIn) {
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dataIn));
            String strLine;

            while (((strLine = bReader.readLine()) != null)&&(ScriptContinue)) {
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
