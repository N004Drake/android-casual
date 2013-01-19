/*DiffTextFiles displays a special type of diff between files
 *Copyright (C) 2013  Logan Ludington
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author LogansALIEN
 */
public class DiffTextFiles {

    /*
     * takes a resource and a string outputs difference as a string
     */
    public String diffResourceVersusFile(String TestIStream, String OriginalFile) {

        String Difference = "";
        InputStream ResourceAsStream = getClass().getResourceAsStream(TestIStream);
        BufferedReader TestStream = new BufferedReader(new InputStreamReader(ResourceAsStream));
        File Original = new File(OriginalFile);
        String TestStreamLine = "";
        String OriginalFileLine;
        try {
            while ((TestStreamLine = TestStream.readLine()) != null) {
                boolean LineExists = false;
                BufferedReader OriginalReader = new BufferedReader(new FileReader(Original));
                while ((OriginalFileLine = OriginalReader.readLine()) != null) {
                    if (OriginalFileLine.equals(TestStreamLine)) {
                        LineExists = true;
                    }
                }
                if (!LineExists) {
                    Difference = Difference + "\n" + TestStreamLine;
                }
            }
        } catch (IOException ex) {

            Difference = TestStreamLine + "\n";
            try {
                while ((TestStreamLine = TestStream.readLine()) != null) {
                    Difference = Difference + TestStreamLine + "\n";
                }
            } catch (IOException ex1) {
                Logger.getLogger(DiffTextFiles.class.getName()).log(Level.SEVERE, null, ex1);
            }


        }
        if (Difference.startsWith("\n")) {
            Difference = Difference.replaceFirst("\n", "");
        }
        if (Difference.endsWith("\n")) {
            Difference = StringOperations.replaceLast(Difference, "\n", "");
        }

        return Difference;

    }

    /*
     * takes two files returns the difference between the two
     */
    public String diffTextFiles(String Original, String TestForDiff) {
        String DifferenceFromFile1 = "";
        try {
            BufferedReader BRTestDiff = new BufferedReader(new FileReader(TestForDiff));
            try {

                String Line;
                String Line2;
                while ((Line = BRTestDiff.readLine()) != null) {

                    BufferedReader BROriginal = new BufferedReader(new FileReader(Original));
                    try {
                        boolean LineExists = false;
                        while ((Line2 = BROriginal.readLine()) != null) {
                            if (Line2.equals(Line)) {
                                LineExists = true;
                            }
                        }
                        if (!LineExists) {
                            DifferenceFromFile1 = DifferenceFromFile1 + "\n" + Line;
                        }

                    } finally {
                        BROriginal.close();
                    }
                }
            } finally {
                BRTestDiff.close();
            }
        } catch (IOException e) {
            new Log().level3(e.getMessage());
        }
        return DifferenceFromFile1;
    }
    //Takes in the Diff from the constructor and writes it to the file that is 
    //iniFile.

    /*
     * appends text to a file
     */
    public void appendDiffToFile(String NameOfFileToBeModified, String Diff) {
        if (Diff.equals("")) {
            return;
        }
        String currentString;
        FileOutputStream FileOut;
        File FileToModify = new File(NameOfFileToBeModified);
        if (!FileToModify.exists()) {
            try {
                FileToModify.mkdirs();
                if (FileToModify.isDirectory()) {
                    FileToModify.delete();
                }
                FileToModify.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(DiffTextFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        FileReader FR;
        try {
            FR = new FileReader(FileToModify);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DiffTextFiles.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        BufferedReader OriginalFileBuffer = new BufferedReader(FR);
        try {
            FileOut = new FileOutputStream(NameOfFileToBeModified + "_new");
            while ((currentString = OriginalFileBuffer.readLine()) != null) {
                new PrintStream(FileOut).println(currentString);
            }
            new PrintStream(FileOut).println(Diff);
        } catch (IOException ex) {
            Logger.getLogger(DiffTextFiles.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                OriginalFileBuffer.close();
                File OutputFile = FileToModify;
                OutputFile.delete();
                OutputFile = new File(NameOfFileToBeModified + "_new");
                OutputFile.renameTo(new File(NameOfFileToBeModified).getAbsoluteFile());
            } catch (IOException ex) {
                Logger.getLogger(DiffTextFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
