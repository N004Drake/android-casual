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
public class diffTextFiles {

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

    private void writeDiffToIni(String diff) {
        InputStream ResourceAsStream = getClass().getResourceAsStream(Statics.ADBini);
        try {
            BufferedReader d = new BufferedReader(new InputStreamReader(ResourceAsStream));
            try {
                while ((currentString = d.readLine()) != null) {
                    FileOutputStream fout = new FileOutputStream(Statics.ADBini + "_new");
                    new PrintStream(fout).println(currentString);
                }
                new PrintStream(fout).println(diff);
            } finally {
                fout.close();
                d.close();
                File file = new File(Statics.ADBini);
                file.delete();
                file = new File(Statics.ADBini + "_new");
                file.renameTo(new File(Statics.ADBini).getAbsoluteFile());
            }
        } catch (IOException ex) {
            Logger.getLogger(diffTextFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private StringBuilder string1, string2;
    private String currentString;
    private BufferedReader d;
    private FileOutputStream fout;
}
