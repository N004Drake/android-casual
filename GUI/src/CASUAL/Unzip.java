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

    Log log = new Log();
    int BUFFER = 4096;

    public void unzipFile(String zipFile, String OutputFolder) throws ZipException, IOException {
        log.level3(zipFile);

        File file = new File(zipFile);
        ZipFile zip = new ZipFile(file);

        String newPath = OutputFolder + System.getProperty("file.separator");
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
                try (BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry))) {
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];
                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(newPath + entry);
                    try (BufferedOutputStream dest = new BufferedOutputStream(fos,
                                 BUFFER)) {
                        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                        dest.flush();
                    }
                }
            } else if (entry.isDirectory()) {
                log.level3(newPath + entry.getName());
                new File(newPath + entry.getName()).mkdirs();
            }
            if (currentEntry.endsWith(".zip")) {
                // found a zip file, try to open
                unzipFile(destFile.getAbsolutePath(), OutputFolder + System.getProperty("file.separator") + destFile.getAbsolutePath() + System.getProperty("file.separator"));
            }
        }
    }

    public void UnZipResource(String ZipResource, String OutputFolder) throws FileNotFoundException, IOException {
        InputStream ZStream = getClass().getResourceAsStream(ZipResource);
        try (ZipInputStream ZipInput = new ZipInputStream(ZStream)) {
            ZipEntry ZipEntryInstance;
            while ((ZipEntryInstance = ZipInput.getNextEntry()) != null) {
                log.level3("Unzipping " + ZipEntryInstance.getName());
                File EntryFile = new File(OutputFolder + System.getProperty("file.separator") + ZipEntryInstance.getName());
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
                File DestFile = new File(OutputFolder + System.getProperty("file.separator"), currentEntry);
                FileOutputStream FileOut = new FileOutputStream(DestFile);
                BufferedInputStream BufferedInputStream = new BufferedInputStream(ZipInput);
                try (BufferedOutputStream Destination = new BufferedOutputStream(FileOut)) {
                    while ((currentByte = BufferedInputStream.read(data, 0, BUFFER)) != -1) {
                        Destination.write(data, 0, currentByte);
                    }

                    Destination.flush();
                }

            }
        }
    }
}
