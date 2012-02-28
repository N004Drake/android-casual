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
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 *
 * @author adam 
 */
public class Unzip {

    public void unzipFiles() {
    }

    public void unzipFile(String zipFile, String OutputFolder) throws ZipException, IOException {
        System.out.println(zipFile);
        int BUFFER = 2048;
        File file = new File(zipFile);
        ZipFile zip = new ZipFile(file);
        
        String newPath = OutputFolder+System.getProperty("file.separator");
        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();
        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            //destFile = new File(newPath, destFile.getName());
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
                unzipFile(destFile.getAbsolutePath(),OutputFolder+System.getProperty("file.separator")+destFile.getAbsolutePath()+System.getProperty("file.separator"));
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
                if (ze.isDirectory()){
                    EntryFile.mkdirs();
                    continue;
                } 
                File EntryFolder=new File(EntryFile.getParent());
                if (!EntryFolder.exists()){
                    EntryFolder.mkdirs();
              
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
