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
 * @author LogansALIEN
 */
public class DiffTextFiles {

    public String diffInputStreamVersusFile(InputStream TestIStream, String OriginalFile) throws IOException {
        String Difference = "";
        BufferedReader TestStream = new BufferedReader(new InputStreamReader(TestIStream));
        File Original = new File(OriginalFile);
        String TestStreamLine;
        String OriginalFileLine;
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
        return Difference;

    }

    public String diffTextFiles(String File1, String File2) {
        String DifferenceFromFile1 = "";
        try {
            BufferedReader Reader1 = new BufferedReader(new FileReader(File1));
            try {

                String Line;
                String Line2;


                while ((Line = Reader1.readLine()) != null) {
                    boolean LineExists = false;
                    BufferedReader Reader2 = new BufferedReader(new FileReader(File1));
                    try {

                        while ((Line2 = Reader2.readLine()) != null) {
                            if (Line2.equals(Line)) {
                                LineExists = true;
                            }
                        }
                        if (!LineExists) {
                            DifferenceFromFile1 = DifferenceFromFile1 + "\n" + Line;
                        }
                    } finally {
                        Reader2.close();
                    }
                }
            } finally {
                Reader1.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DifferenceFromFile1;
    }
    //Takes in the Diff from the constructor and writes it to the file that is 
    //iniFile.

    public void appendDiffToFile(String NameOfFileToBeModified, String Diff) {
        String currentString;
        FileOutputStream FileOut = null;
        File FileToModify=new File(NameOfFileToBeModified);
        try {
            FileReader FR=new FileReader(FileToModify);
            BufferedReader OriginalFileBuffer = new BufferedReader(FR);
            try {
                FileOut = new FileOutputStream(NameOfFileToBeModified + "_new");
                while ((currentString = OriginalFileBuffer.readLine()) != null) {
                    new PrintStream(FileOut).println(currentString);
                }
                new PrintStream(FileOut).println(Diff);
            } finally {
                FileOut.close();
                OriginalFileBuffer.close();
                File OutputFile = FileToModify;
                OutputFile.delete();
                OutputFile = new File(NameOfFileToBeModified + "_new");
                OutputFile.renameTo(new File(NameOfFileToBeModified).getAbsoluteFile());
            }
        } catch (IOException ex) {
            Logger.getLogger(DiffTextFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

}
