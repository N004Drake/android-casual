/*Zip zips files
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
 * 
 * parts of this file are from http://stackoverflow.com/questions/3048669/how-can-i-add-entries-to-an-existing-zip-file-in-java
 * 
 */
package CASUAL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author adam
 */
public class Zip {

    public static void addFilesToExistingZip(String zipFile, String fileToAdd) throws IOException {
        File zip = new File(zipFile);
        File file = new File(fileToAdd);
        addFilesToExistingZip(zip, new File[]{file});
    }

    public static void addFilesToExistingZip(String zipFile, String[] filesToBeZipped) throws IOException {
        File[] fileList = new File[filesToBeZipped.length];
        int i = 0;
        for (String file : filesToBeZipped) {
            fileList[i] = new File(file);
        }
        File zip = new File(zipFile);
        addFilesToExistingZip(zip, fileList);
    }

    public static void addFilesToExistingZip(File zipFile, File fileToAdd) throws IOException {
        addFilesToExistingZip(zipFile, new File[]{fileToAdd});
    }

    public static void addFilesToExistingZip(File zipFile, String fileToAdd) throws IOException {
        File f = new File(fileToAdd);
        File[] zipAdd = new File[]{f};
        addFilesToExistingZip(zipFile, zipAdd);
    }

    public static void addFilesToExistingZip(File zipFile, File[] files) throws IOException {
        byte[] buf = new byte[4096];

 
        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();
        //try rename
        boolean renameOk = zipFile.renameTo(tempFile);
        boolean copyOk=false;
        //if rename fails, make copy
        
        if (!renameOk) {
           tempFile.delete();
           tempFile.createNewFile();
            FileOutputStream out;
            try (FileInputStream in = new FileInputStream(zipFile)) {
                out = new FileOutputStream(tempFile);
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            out.close();
            copyOk=true;
        }
        if (!renameOk) {
            if (!copyOk){
            throw new RuntimeException("could not rename or copy the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
            }
        }
        ZipOutputStream out;
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile))) {
            out = new ZipOutputStream(new FileOutputStream(zipFile));
            ZipEntry entry = zin.getNextEntry();
            while (entry != null) {
                String name = entry.getName();
                boolean notInFiles = true;
                for (File f : files) {
                    if (f.getName().equals(name)) {
                        notInFiles = false;
                        break;
                    }
                }
                if (notInFiles) {
                    // Add ZIP entry to output stream.
                    out.putNextEntry(new ZipEntry(name));
                    // Transfer bytes from the ZIP file to the output file
                    int len;
                    while ((len = zin.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
                entry = zin.getNextEntry();
            }
        }
        // Compress the files
        for (int i = 0; i < files.length; i++) {
            try (InputStream in = new FileInputStream(files[i])) {
                out.putNextEntry(new ZipEntry(files[i].getName()));
                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                // Complete the entry
                out.closeEntry();
            }
        }
        // Complete the ZIP file
        out.close();
        tempFile.delete();
    }
}
