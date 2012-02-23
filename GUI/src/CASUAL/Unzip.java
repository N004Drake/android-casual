package CASUAL;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 *
 * @author adam Licensed under the "I Found This On Stack Overflow License"
 */
public class Unzip {

    public void unzipFiles() {
    }

    public void unzip(String zipFile) throws ZipException, IOException {
        System.out.println(zipFile);
        int BUFFER = 2048;
        File file = new File(zipFile);
        ZipFile zip = new ZipFile(file);
        String newPath = zipFile.substring(0, zipFile.length() - 4) + "/";
        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();
        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();
            // create the parent directory structure if needed
            destinationParent.mkdirs();
            System.out.println();
            if (!entry.isDirectory()) {
                //if (Static)
                BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];
                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(newPath + entry);
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        BUFFER);
                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            } else if (entry.isDirectory()) {
                System.out.println(newPath + entry.getName());
                new File(newPath + entry.getName()).mkdirs();
            }
            if (currentEntry.endsWith(".zip")) {
                // found a zip file, try to open
                unzip(destFile.getAbsolutePath());
            }
        }
    }


    public void UnZipResource(String ZipResource, String OutputFolder) throws FileNotFoundException, IOException {
            InputStream ZStream=getClass().getResourceAsStream(ZipResource);
            ZipInputStream zin = new ZipInputStream(ZStream);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                System.out.println("Unzipping " + ze.getName());
                File EntryFile =new File(OutputFolder+System.getProperty("file.separator")+ze.getName());
                File EntryFolder=new File(EntryFile.getParent());
                if (!EntryFolder.exists()){
                    EntryFile.mkdirs();
              
                }
                FileOutputStream fout = new FileOutputStream(OutputFolder+System.getProperty("file.separator")+ze.getName());
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                zin.closeEntry();
                fout.close();
            }
            zin.close();
    }
}
