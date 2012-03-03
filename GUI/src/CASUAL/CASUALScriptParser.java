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
import java.util.ArrayList;

/**
 *
 * @author adam
 */
public class CASUALScriptParser {

    boolean ScriptContinue = true;
    Log Log = new Log();
    int LinesInScript = 0;
    int CurrentLine;
    String ScriptTempFolder = "";
    String ScriptName = "";
    /*
     * Executes a selected script as a resource reports to Log class.
     */

    public void executeSelectedScriptResource(String Script) {
        Log.level3("Selected resource" + Script);
        ScriptName = Script;
        CountLines CountLines = new CountLines();
        LinesInScript = CountLines.countResourceLines(Script);
        Log.level3("Lines in Script " + LinesInScript);
        ScriptTempFolder = Statics.TempFolder + Script + Statics.Slash;

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
        ScriptName = Script;
        ScriptTempFolder = Statics.TempFolder + (new File(Script).getName()) + Statics.Slash;
        LinesInScript = CountLines.countFileLines(Script + ".scr");
        Log.level3("Lines in SCript " + LinesInScript);
        DataInputStream DIS;
        try {
            FileInputStream FileAsStream;
            FileAsStream = new FileInputStream(Script + ".scr");
            DIS = new DataInputStream(FileAsStream);
            executeSelectedScript(DIS);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Script Handler contains all script commands and will execute commands
     */
    private void commandHandler(String Line) {

        //Remove leading spaces
        Line = removeLeadingSpaces(Line);


        //Disregard commented lines
        if (Line.startsWith("#")) {
            Log.level3("Ignoring commented line" + Line);
            return;
        }

        //Disregard blank lines
        if (Line.equals("")) {
            return;
        }



        //replace $SLASH with "\" for windows or "/" for linux and mac
        if (Line.contains("$SLASH")) {
            Line = Line.replace("$SLASH", Statics.Slash);
        }

        //reference to the Script's .zip file
        if (Line.contains("$ZIPFILE")) {
            Line = Line.replace("$ZIPFILE", ScriptTempFolder);
        }
        
        if ((Line.contains("\\n")) && ((Line.startsWith("$USERNOTIFICATION") || Line.startsWith("$USERNOTIFICATION")) || Line.startsWith("$USERCANCELOPTION"))){
            Line=Line.replace("\\n", "\n");
        }
            

        //Disregard commented lines
        if (Line.startsWith("#")) {
            Log.level3("Ignoring commented line" + Line);
            return;
        }
        //$ECHO command will display text in the main window
        if (Line.startsWith("$ECHO")) {
            Line = Line.replace("$ECHO", "");
            Line = removeLeadingSpaces(Line);
            Log.level1(Line);
            return;
        } else if (Line.startsWith("$USERNOTIFICATION")) {
            if (Statics.UseSound.contains("true")) {
                //CASUALAudioSystem CAS = new CASUALAudioSystem();
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/Notification.wav");
            }
            Line = Line.replace("$USERNOTIFICATION", "");
            Line = removeLeadingSpaces(Line);
            if (Line.contains(",")) {
                String[] Message = Line.split(",");
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
        } else if (Line.startsWith("$USERCANCELOPTION")) {
            if (Statics.UseSound.contains("true")) {
                //CASUALAudioSystem CAS = new CASUALAudioSystem();
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/RequestToContinue.wav");
            }
            Line = Line.replace("$USERCANCELOPTION", "");
            if (Line.contains(",")) {
                String[] Message = Line.split(",");
                Object[] Options = {"Stop",
                    "Continue"};
                int n = JOptionPane.showOptionDialog(
                        Statics.GUI,
                        Message[1],
                        Message[0],
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Options,
                        Options[1]);
                if (n == JOptionPane.YES_OPTION) {
                    Log.level0(ScriptName + " canceled at user request");
                    ScriptContinue = false;
                    return;
                }
            } else {
                int n = JOptionPane.showConfirmDialog(
                        Statics.GUI,
                        Line,
                        "Do you wish to continue?",
                        JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.NO_OPTION) {
                    Log.level0(ScriptName + " canceled at user request");
                    ScriptContinue = false;
                    return;
                }
            }
        } else {
            Shell Shell = new Shell();
            ArrayList ShellCommand=new ArrayList();
            ShellCommand.add(Statics.AdbDeployed);
            ShellCommand.addAll(this.parseCommandLine(Line));
            String StringCommand[]= (convertArrayListToStringArray(ShellCommand));
            Shell.liveShellCommand(StringCommand);
        }
        //final line output for debugging purposes
        Log.level3("COMMAND TEST" + Statics.AdbDeployed + " " + Line);
    }

    private void executeSelectedScript(DataInputStream DIS) {
        CurrentLine = 1;
        Statics.ProgressBar.setMaximum(LinesInScript);
        Log.level3("Reading datastream" + DIS);
        doRead(DIS);
    }
    
    private String[] convertArrayListToStringArray(ArrayList List){
        String[] StringArray=new String[List.size()] ;
        for (int i=0; i <= List.size()-1; i++){
            StringArray[i] = List.get(i).toString();
        }
        return StringArray;
    }

    private void doRead(DataInputStream dataIn) {
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dataIn));
            String strLine;

            while (((strLine = bReader.readLine()) != null) && (ScriptContinue)) {
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

    private ArrayList parseCommandLine(String Line) {
        ArrayList List = new ArrayList();
        Boolean SingleQuoteOn = false;
        Boolean DoubleQuoteOn = false;
        String Word ="";
        char LastChar=0;
        char[] TestChars = {
            "\'".toCharArray()[0], //'
            "\"".toCharArray()[0], //"
            " ".toCharArray()[0],  // 
            "\\".toCharArray()[0], //\
            
        };
        char[] CharLine = Line.toCharArray();
        for (int I = 0; I < CharLine.length; I++) {
            //If we are not double quoted, act on singe quotes
            if (!DoubleQuoteOn && CharLine[I] == TestChars[0]&& LastChar != TestChars[3]) {
                //If we are single quoted and we see the last ' character;
                if (SingleQuoteOn){
                    SingleQuoteOn=false;  
                //start single quote
                } else if (! SingleQuoteOn){
                    SingleQuoteOn=true;
                } 
            //if we are not single quoted, act on double quotes
            } else if (!SingleQuoteOn && CharLine[I] == TestChars[1]&& LastChar != TestChars[3]) {
                //if we are doulbe quoted already and see the last character;
                if (DoubleQuoteOn) {
                    //turn doublequote off
                    DoubleQuoteOn=false;
                //start doublequote
                } else {
                    DoubleQuoteOn=true;
                }
            //if space is detected and not single or double quoted
            }else if (!SingleQuoteOn && !DoubleQuoteOn && CharLine[I] == TestChars[2] && LastChar != TestChars[3]) {
                List.add(Word);
                Word="";
            //Otherwise add it to the string
            } else {
                Word=Word + String.valueOf(CharLine[I]);
            }
            //Annotate last char for literal character checks "\".
            LastChar=CharLine[I];
        }
        //add the last word to the list if it's not blank.
        if (!Word.equals("")){ 
            List.add(Word);
        }
        return List;
    }
}
