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

    static int BUFFER = 4096;
    ZipFile zip;
    public Enumeration zipFileEntries;

    /**
     * unzip unzips files
     * @param f file to be unzip
     * @throws ZipException
     * @throws IOException
     */
    public Unzip(File f) throws ZipException, IOException {
        this.zip = new ZipFile(f);
        try {
            this.zipFileEntries = zip.entries();
        } catch (Exception e) {
            new Log().errorHandler(e);
        }
    }

    /**
     * unzip unzips files
     * @param f file to unzip
     * @throws ZipException
     * @throws IOException
     */
    public Unzip(String f) throws ZipException, IOException {
        this.zip = new ZipFile(new File(f));
        try {
            this.zipFileEntries = zip.entries();
        } catch (Exception e) {
            new Log().errorHandler(e);
        }
    }

    /**
     * unzips recursively
     * @param outputFolder to folder
     * @throws ZipException
     * @throws IOException
     */
    public void recursiveUnzipFile(String outputFolder) throws ZipException, IOException {

        String newPath = outputFolder + System.getProperty("file.separator");
        new File(newPath).mkdir();
        Enumeration zfEntries = zip.entries();
        // Process each entry
        while (zfEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zfEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            //destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();
            // create the parent directory structure if needed
            destinationParent.mkdirs();
            if (!entry.isDirectory()) {
                new Log().level3Verbose("unzipping " + entry.toString());
                writeFromZipToFile(zip, entry, newPath);
            } else if (entry.isDirectory()) {
                new Log().level4Debug(newPath + entry.getName());
                new File(newPath + entry.getName()).mkdirs();
            }
            if (currentEntry.endsWith(".zip")) {
                // found a zip file, try to open
                unzipFileToFolder(outputFolder + System.getProperty("file.separator") + destFile.getAbsolutePath() + System.getProperty("file.separator"));
            }
        }
    }

    /**
     * unzips a file to a folder
     * @param outputFolder folder to be unzipped to
     * @throws ZipException
     * @throws IOException
     */
    public void unzipFile(String outputFolder) throws ZipException, IOException {
        unzipFileToFolder(outputFolder);
    }

    private void unzipFileToFolder(String outputFolder) throws ZipException, IOException {
        new Log().level4Debug("Unzipping " + zip.toString());

        String newPath = outputFolder + System.getProperty("file.separator");
        new File(newPath).mkdir();
        zipFileEntries = zip.entries();
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
                new Log().level3Verbose("unzipping " + entry.toString());
                writeFromZipToFile(zip, entry, newPath);
            } else if (entry.isDirectory()) {
                new Log().level4Debug(newPath + entry.getName());
                new File(newPath + entry.getName()).mkdirs();
            }
        }
    }

    /**
     * unzips a resource
     * @param zipResource resource to be unzipped
     * @param outputFolder folder to unzip to
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void unZipResource(String zipResource, String outputFolder) throws FileNotFoundException, IOException {
        InputStream ZStream = new CASUALApp().getClass().getResourceAsStream(zipResource);
        unZipInputStream(ZStream, outputFolder);
    }
    
    /**
     * unzips a stream
     * @param ZStream input stream to unzip
     * @param outputFolder output folder to unzip to
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void unZipInputStream(InputStream ZStream, String outputFolder) throws FileNotFoundException, IOException {

        ZipInputStream ZipInput;
        ZipInput = new ZipInputStream(ZStream);
        ZipEntry ZipEntryInstance;
        while ((ZipEntryInstance = ZipInput.getNextEntry()) != null) {
            new Log().level3Verbose("Unzipping " + ZipEntryInstance.getName());
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
        new Log().level3Verbose("Unzip Complete");
    }

    /**
     * closes the zip file
     */
    public void close() {
        try {
            zip.close();
        } catch (IOException ex) {
            new Log().errorHandler(ex);
        }
    }

    /**
     * deploys a file from a zip
     * @param entry entry file to deploy
     * @param outputFolder folder to be deployed to
     * @return
     * @throws ZipException
     * @throws IOException
     */
    public String deployFileFromZip(Object entry, String outputFolder) throws ZipException, IOException {

        ZipEntry zipEntry = new ZipEntry((ZipEntry) entry);
        writeFromZipToFile(zip, zipEntry, outputFolder);
        zip.close();
        return outputFolder + entry.toString();
    }

    /**
     * gets a stream of a specified file from a zip
     * @param zipFile file to stream from
     * @param entry entry to stream
     * @return stream of file
     * @throws ZipException
     * @throws IOException
     */
    public static BufferedInputStream streamFileFromZip(File zipFile, Object entry) throws ZipException, IOException {
        ZipFile zip = new ZipFile(zipFile);
        return new BufferedInputStream(zip.getInputStream((ZipEntry) entry));
    }

    private void writeFromZipToFile(ZipFile zip, ZipEntry entry, String filePathToWrite) throws IOException, FileNotFoundException {
        //if (Static)
        BufferedInputStream is;
        is = new BufferedInputStream(zip.getInputStream(entry));
        int currentByte;
        // establish buffer for writing file
        byte data[] = new byte[BUFFER];
        // write the current file to disk
        FileOutputStream fos = new FileOutputStream(new File(filePathToWrite + entry));
        BufferedOutputStream dest;
        dest = new BufferedOutputStream(fos, BUFFER);
        // read and write until last byte is encountered
        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
            dest.write(data, 0, currentByte);
        }
        dest.flush();
        dest.close();
        is.close();
    }
    /**
     * gets a stream of a specified file from a zip
     * @param zipFile file to stream from
     * @param entry entry to stream
     * @return stream of file
     * @throws ZipException
     * @throws IOException
     */
    public BufferedInputStream streamFileFromZip( Object entry) throws ZipException, IOException {
        return new BufferedInputStream(zip.getInputStream((ZipEntry) entry));
    }
    
    public String getEntryName(Object entry){
        ZipEntry zipEntry = (ZipEntry)entry;
        String name=zipEntry.getName();
        return name;
        
    }
    
    
    
    
    
    
    
    
    
    
    
}
