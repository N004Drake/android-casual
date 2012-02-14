/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author LogansALIEN
 */
public class diffTextFiles {


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
private StringBuilder string1, string2;
}
