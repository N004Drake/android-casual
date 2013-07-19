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

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author adam
 */
public class Zip {

    private final File outputZip;
    private final String slash = System.getProperty("file.separator");
    private Log log = new Log();
    private String TempFolder = Statics.TempFolder;
    byte[] BUFFER = new byte[4096];

    /**
     * instantiates the zip class
     *
     * @param zip file to be worked with
     */
    public Zip(File zip) throws IOException {
        this.outputZip = zip;
    }

    public String getTempFolder() {
        return TempFolder;
    }

    public void addToTempFolderLoc(String TempFolder) {
        this.TempFolder = this.TempFolder + slash + TempFolder;
        if (!(new File(this.TempFolder).exists())) {
            new File(this.TempFolder).mkdirs();
        }
    }

    /**
     * adds files to zip
     *
     * @param zipFile specified zip file
     * @param fileToAdd file to be added
     * @throws IOException
     */
    public void addFilesToExistingZip(String fileToAdd) throws IOException {
        File file = new File(fileToAdd);
        addFilesToExistingZip(new File[]{file});
    }

    /**
     * adds files to existing zip file
     *
     * @param zipFile zip file
     * @param filesToBeZipped file to be added
     * @throws IOException
     */
    public void addFilesToExistingZip(String[] filesToBeZipped) throws IOException {
        File[] fileList = new File[filesToBeZipped.length];
        int i = 0;
        for (String file : filesToBeZipped) {
            fileList[i] = new File(file);
        }

        addFilesToExistingZip(fileList);
    }

    /**
     *
     * adds files to existing zip file
     *
     * @param zipFile zip file
     * @param fileToAdd file to be added
     * @throws IOException
     */
    public void addFilesToExistingZip(File fileToAdd) throws IOException {
        addFilesToExistingZip(new File[]{fileToAdd});
    }

    /**
     * adds files to existing zip file
     *
     * @param zipFile zip file
     * @param fileToAdd file to be added
     * @throws IOException
     */
    public void addFilesToExistingZip(File zipFile, String fileToAdd) throws IOException {
        File f = new File(fileToAdd);
        File[] zipAdd = new File[]{f};
        addFilesToExistingZip(zipAdd);
    }

    /**
     * adds files to existing zip file
     *
     * @param zipFile zip file
     * @param files files to be zipped
     * @throws IOException
     */
    public void addFilesToExistingZip(File[] files) throws IOException {
        // get a temp file
        File tempFile = File.createTempFile(outputZip.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();
        getTemporaryOutputZip(tempFile, BUFFER);
        ZipOutputStream out;
        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        out = prepareZipFileForMoreEntries(zin);
        // Compress the files into the zip
        for (int i = 0; i < files.length; i++) {
            InputStream in = new FileInputStream(files[i]);
            writeEntryToZipFile(out, files[i].getName(), in);

        }
        // Complete the ZIP file
        out.close();
        tempFile.delete();
    }
    
    public void streamEntryToExistingZip(InputStream in, String name) throws IOException{
        File tempFile = File.createTempFile(outputZip.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();
        getTemporaryOutputZip(tempFile, BUFFER);
        ZipOutputStream out;
        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        out = prepareZipFileForMoreEntries(zin);
        // Compress the files into the zip
        writeEntryToZipFile(out, name, in);
        // Complete the ZIP file
        out.close();
        tempFile.delete();
        
        
    }
    
    
    
    public void streamEntryToExistingZip(Map<String, InputStream> nameStream) throws IOException{
        File tempFile = File.createTempFile(outputZip.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();
        getTemporaryOutputZip(tempFile, BUFFER);
        ZipOutputStream out;
        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        out = prepareZipFileForMoreEntries(zin);
           
           for (Map.Entry<String, InputStream> entry : nameStream.entrySet()){
           // Compress the files into the zip
            writeEntryToZipFile(out, entry.getKey(),entry.getValue());
           }
        // Complete the ZIP file
        out.close();
        tempFile.delete();
        
        
    }
    /**
     * adds files to existing zip file
     *
     * @param newZip zip file
     * @param toBeZipped file to be added
     * @throws Exception
     */
    public void addFolderFilesToNewZip(String newZip, String toBeZipped) throws Exception {
        File directory = new File(toBeZipped);
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(newZip);
        Closeable res = out;
        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                for (File kid : directory.listFiles()) {
                    String name = base.relativize(kid.toURI()).getPath();
                    if (kid.isDirectory()) {
                        queue.push(kid);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    } else {
                        zout.putNextEntry(new ZipEntry(name));
                        copy(kid, zout);
                        zout.closeEntry();
                    }
                }
            }
        } finally {
            res.close();
        }
    }

    private static void copy(File in, File out) throws FileNotFoundException, IOException {
        copy(in, new FileOutputStream(out));
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }

    }

    private static void copy(File file, OutputStream out) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            copy(in, out);
        }
    }

    private static void copy(InputStream in, File file) throws IOException {
        try (OutputStream out = new FileOutputStream(file)) {
            copy(in, out);
        }
    }

    private void addFileToZipDir(File file) throws IOException {

        if (!file.exists()) {
            log.level0Error("File: " + file.toString() + " not found while adding to zip");
            return;
        }

        //First we need to create the file (empty) in the temp directory if its
        //not there all ready
        File fileToAdd = new File(TempFolder + slash + file.getName());
        if (!fileToAdd.exists()) {
            fileToAdd.createNewFile();
        }

        //Create two file channels (effectivly Filestreamers with pointers)
        //Well take the source and read it into the file.
        FileChannel source = null;
        FileChannel dest = null;


        //Now stream from one channel to the other, and close once files are
        //filled
        try {
            source = new FileInputStream(file).getChannel();
            dest = new FileOutputStream(fileToAdd).getChannel();
            dest.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (dest != null) {
                dest.close();
            }
        }

    }

    //SHOULD ONLY BE CALLED FROM addDirectory
    private void addFileToZipDir(File file, File destFolder) throws IOException {
        if (!file.exists()) {
            log.level0Error("File: " + file.toString() + " not found while adding to zip.");
            return;
        }

        //First we need to create the file (empty) in the temp directory if its
        //not there all ready
        File fileToAdd = new File(destFolder.toString() + slash + file.getName());
        if (!fileToAdd.exists()) {
            fileToAdd.createNewFile();
        }

        copy(fileToAdd, destFolder);

    }

    /**
     *
     * @param file
     * @throws IOException
     */
    public void addFileToZipDIr(File file) throws IOException {
        if (!file.exists()) {
            log.level0Error("File: " + file.toString() + " not found while adding to zip.");
            return;
        }
        if (file.isFile()) {
            addFileToZipDir(file);
        }

        if (file.isDirectory()) {
            addDirectoryToZipDir(file, null);
        }


    }

    private void addDirectoryToZipDir(File folder, File parent) throws IOException {
        File dirToAdd = null;
        if (parent == null) {
            dirToAdd = new File(TempFolder + slash + folder.getName());
        } else {
            dirToAdd = new File(parent.toString() + slash + folder.getName());
        }
        if (!dirToAdd.exists()) {
            dirToAdd.mkdir();
        }
        for (File c : folder.listFiles()) {
            if (c.isDirectory()) {
                addDirectoryToZipDir(c, dirToAdd);
            } else {
                addFileToZipDir(c, dirToAdd);
            }
        }

    }

    /**
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void compressZipDir(String file) throws FileNotFoundException, IOException {
        zipDir(file.toString(), "");
    }
    //TODO: Make the files work if directories are empty.

    public void compressZipDir() throws FileNotFoundException, IOException {
        zipDir(TempFolder, "");
    }

    /**
     * Zip up a directory
     *
     * @param directory
     * @param zipName
     * @param path
     * @throws IOException
     */
    public void zipDir(String directory, String path) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputZip))) {
            zipDir(directory, zos, path);
        }
    }

    /**
     * Zip up a directory path
     *
     * @param directory
     * @param zos
     * @param path
     * @throws IOException
     */
    public void zipDir(String directory, ZipOutputStream zos, String path) throws IOException {
        File zipDir = new File(directory);
        // get a listing of the directory content
        String[] dirList = zipDir.list();
        byte[] readBuffer = new byte[2156];
        int bytesIn = 0;
        // loop through dirList, and zip the files
        for (int i = 0; i < dirList.length; i++) {
            File f = new File(zipDir, dirList[i]);
            if (f.isDirectory()) {
                String filePath = f.getPath();
                zipDir(filePath, zos, path + f.getName() + "/");
                continue;
            }
            FileInputStream fis = new FileInputStream(f);
            try {
                ZipEntry anEntry = new ZipEntry(path + f.getName());
                zos.putNextEntry(anEntry);
                bytesIn = fis.read(readBuffer);
                while (bytesIn != -1) {
                    zos.write(readBuffer, 0, bytesIn);
                    bytesIn = fis.read(readBuffer);
                }
            } finally {
                fis.close();
            }
        }
    }

    private void getTemporaryOutputZip(File tempFile, byte[] buf) throws IOException, RuntimeException {
        //try rename
        boolean renameOk = outputZip.renameTo(tempFile);
        boolean copyOk = false;
        //if rename fails, make copy

        if (!renameOk) {
            tempFile.delete();
            tempFile.createNewFile();
            FileOutputStream out;
            try (FileInputStream in = new FileInputStream(outputZip)) {
                out = new FileOutputStream(tempFile);
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            out.close();
            copyOk = true;
        }
        if (!renameOk && !copyOk) {
            throw new IOException("could not rename or copy the file " + outputZip.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }
    }

    private ZipOutputStream prepareZipFileForMoreEntries(ZipInputStream zin) throws FileNotFoundException, IOException {
        ZipOutputStream out;
        out = new ZipOutputStream(new FileOutputStream(outputZip));
        //ZipEntry entry = zin.getNextEntry();
        ZipEntry entry;
        while ((entry = zin.getNextEntry()) != null) {
            String name = entry.getName();
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(name));
            // Transfer bytes from the ZIP file to the output file
            int len;
            while ((len = zin.read(BUFFER)) > 0) {
                out.write(BUFFER, 0, len);
            }
        }
        return out;
    }

    private void writeEntryToZipFile(ZipOutputStream out, String file, InputStream in) throws IOException {
        out.putNextEntry(new ZipEntry(file));
        // Transfer bytes from the file to the ZIP file
        int len;
        while ((len = in.read(BUFFER)) > 0) {
            out.write(BUFFER, 0, len);
        }
        // Complete the entry
        out.closeEntry();
    }
}
