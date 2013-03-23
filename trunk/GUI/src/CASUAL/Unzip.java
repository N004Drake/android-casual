/*UnZip unzips files
 *Copyright (C) 2013  Adam Outler
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

import java.io.IOException;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
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
    Log log = new Log();
    int BUFFER = 4096;


    public void unzipFile(String zipFile, String outputFolder) throws ZipException, IOException {
        log.level3(zipFile);

        File file = new File(zipFile);
        ZipFile zip = new ZipFile(file);

        String newPath = outputFolder + System.getProperty("file.separator");
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
            if (!entry.isDirectory()) {
                writeFromZipToFile(zip, entry, newPath);
            } else if (entry.isDirectory()) {
                log.level3(newPath + entry.getName());
                new File(newPath + entry.getName()).mkdirs();
            }
            if (currentEntry.endsWith(".zip")) {
                // found a zip file, try to open
                unzipFile(destFile.getAbsolutePath(), outputFolder + System.getProperty("file.separator") + destFile.getAbsolutePath() + System.getProperty("file.separator"));
            }
        }
    }

    public void UnZipResource(String zipResource, String outputFolder) throws FileNotFoundException, IOException {
        InputStream ZStream = getClass().getResourceAsStream(zipResource);
        ZipInputStream ZipInput;
        ZipInput = new ZipInputStream(ZStream);
        ZipEntry ZipEntryInstance;
        while ((ZipEntryInstance = ZipInput.getNextEntry()) != null) {
            log.level3("Unzipping " + ZipEntryInstance.getName());
            File EntryFile = new File(outputFolder + System.getProperty("file.separator") + ZipEntryInstance.getName());
            if (ZipEntryInstance.isDirectory()) {
                EntryFile.mkdirs();
                continue;
            }
            File EntryFolder = new File(EntryFile.getParent());
            if (!EntryFolder.exists()) {
                EntryFolder.mkdirs();
            }
            int currentByte;
            // establish buffer for writing file
            byte data[] = new byte[BUFFER];
            String currentEntry = ZipEntryInstance.getName();
            File DestFile = new File(outputFolder + System.getProperty("file.separator"), currentEntry);
            FileOutputStream FileOut = new FileOutputStream(DestFile);
            BufferedInputStream BufferedInputStream = new BufferedInputStream(ZipInput);
            BufferedOutputStream Destination;
            Destination = new BufferedOutputStream(FileOut);
            while ((currentByte = BufferedInputStream.read(data, 0, BUFFER)) != -1) {
                Destination.write(data, 0, currentByte);
            }
            Destination.flush();
            Destination.close();
        }
        ZipInput.close();
    }
    
    
    
    public Enumeration getZipFileEntries(File f) throws ZipException, IOException {
        ZipFile zip = new ZipFile(f);
        Enumeration zipFileEntries = zip.entries();
        return zipFileEntries;
    }
    public String deployFileFromZip(File zipFile, Object entry, String outputFolder ) throws ZipException, IOException {
        ZipFile zip = new ZipFile(zipFile);
        ZipEntry zipEntry=new ZipEntry((ZipEntry) entry);
        writeFromZipToFile(zip, zipEntry,outputFolder);
        return outputFolder+entry.toString();
    }
    public BufferedInputStream streamFileFromZip(File zipFile, Object entry) throws ZipException, IOException {
        ZipFile zip = new ZipFile(zipFile);
        return new BufferedInputStream(zip.getInputStream((ZipEntry) entry));
    }
    private void writeFromZipToFile(ZipFile zip, ZipEntry entry, String newPath) throws IOException, FileNotFoundException {
        //if (Static)
        BufferedInputStream is;
        is = new BufferedInputStream(zip.getInputStream(entry));
        int currentByte;
        // establish buffer for writing file
        byte data[] = new byte[BUFFER];
        // write the current file to disk
        FileOutputStream fos = new FileOutputStream(new File(newPath + entry));
        BufferedOutputStream dest;
        dest = new BufferedOutputStream(fos,BUFFER);
        // read and write until last byte is encountered
        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
            dest.write(data, 0, currentByte);
        }
        dest.flush();
        dest.close();
        is.close();
    }
    
}
