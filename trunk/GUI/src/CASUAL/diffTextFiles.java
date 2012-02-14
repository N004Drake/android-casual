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
    public diffTextFiles(String file1,String file2)
    {
        try{
            BufferedReader reader1 = new BufferedReader(new FileReader(file1));
            try {
               String line = null;
               while ((line=reader1.readLine())!=null){
                   string1.append(line);
                   string1.append(System.getProperty("line.separator"));
               }
            }
            finally{
            reader1.close();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        try{
            BufferedReader reader2 = new BufferedReader(new FileReader(file1));
            try {
               String line = null;
               while ((line=reader2.readLine())!=null){
                   string2.append(line);
                   string2.append(System.getProperty("line.separator"));
               }
            }
            finally{
            reader2.close();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
    private StringBuilder string1,string2;
    
    
}
