
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author adamoutler
 */
public class ReplaceLineInFile {

    /**
     * replaces entire lines in files
     *
     * @param args 1. file or folder to scan 2. old line contents to be replaced
     * 3. new line contents to be replaced
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                return;
            }
            File folder = new File(args[0]);
            System.out.println("Checking " + folder.getCanonicalPath() + " for matches");
            if (folder.isDirectory()) {
                File[] list=folder.listFiles();
                for (File f : list) {
                    scanFile(f, args);
                }
            } else if (folder.isFile()) {
                scanFile(folder, args);
            } else {
                System.out.println("File Not Found");
            }
        } catch (IOException ex) {
            Logger.getLogger(ReplaceLineInFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void scanFile(File file, String[] args) throws IOException {
        System.out.println("Scanning " + file.getCanonicalPath());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(file + "temporary"));
        String line = "";
        while ((line = reader.readLine()) != null) {
            if (line.equals(args[1])) {
                line = args[2];
            }
            writer.write(line+"\n");
        }
        writer.close();
        reader.close();
        file.delete();
        new File(file + "temporary").renameTo(file);

    }
}
