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

import java.io.*;
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
                BufferedReader OriginalReader = new BufferedReader(new FileReader(Original));
                OriginalReader.mark(0);

                boolean LineExists = false;
                OriginalReader.reset();
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
                new Log().errorHandler(ex);
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
            String line;
            String line2;
            BufferedReader BROriginal;
            BROriginal = new BufferedReader(new FileReader(Original));
            BROriginal.mark(0);
            while ((line = BRTestDiff.readLine()) != null) {
                BROriginal.reset();
                boolean lineExists = false;
                while ((line2 = BROriginal.readLine()) != null) {
                    if (line2.equals(line)) {
                        lineExists = true;
                    }
                }
                if (!lineExists) {
                    DifferenceFromFile1 = DifferenceFromFile1 + "\n" + line;
                }
            }
        } catch (IOException e) {
            new Log().errorHandler(e);
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
                new Log().errorHandler(ex);
            }
        }
        FileReader FR;
        try {
            FR = new FileReader(FileToModify);
        } catch (FileNotFoundException ex) {
            new Log().errorHandler(ex);
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
                new Log().errorHandler(ex);
            }
        }

    }
}
