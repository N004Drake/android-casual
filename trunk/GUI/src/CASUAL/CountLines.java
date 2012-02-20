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
public class CountLines {
     public int countFileLines(String Filename) {
        InputStream IS = null;
        int Lines = 0;
        try {
            IS = new BufferedInputStream(new FileInputStream(Filename));

            Lines = countISLines(IS);


        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                IS.close();
            } catch (IOException ex) {
                Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return Lines;

    }

    public int countResourceLines(String ResourceName) {
        InputStream IS = getClass().getResourceAsStream(Statics.ScriptLocation + ResourceName + ".scr");
        int Lines = 0;
        try {
            Lines = countISLines(IS);
        } catch (IOException ex) {
            Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                IS.close();
            } catch (IOException ex) {
                Logger.getLogger(ScriptParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Lines;
    }

    private int countISLines(InputStream IS) throws IOException {
        int count = 0;
        try {
            byte[] c = new byte[1024];
            int ReadChars = 0;
            while ((ReadChars = IS.read(c)) != -1) {
                for (int i = 0; i < ReadChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
        } finally {
            IS.close();
        }

        return count + 1;

    }
}
