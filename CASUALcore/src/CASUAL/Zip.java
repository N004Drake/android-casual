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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author adam
 */
public class Zip {
    private String outputZip=null;
    private final String slash = System.getProperty("file.separator");
    private Log log = new Log();
    private String TempFolder = Statics.TempFolder;


    
    /**
     *instantiates the zip class
     * @param zip file to be worked with
     */
    public Zip(File zip){
        try {
            this.outputZip=zip.getCanonicalPath();
        } catch (IOException ex) {
            new Log().errorHandler(ex);
        }
    }
    
    /**
     * instantiates the zip class
     * @param outputFile file to be worked with
     */
    public Zip(String outputFile) {
        //First well seperate the string and store them into the name of the zip
        //file being created and the location of where the zip file is going to
        //be stored
        outputZip = outputFile;
        if (!outputZip.endsWith(".zip"))
        {
            log.Level1Interaction("Adding Zip to file name");
            outputZip = outputZip + ".zip";
        }
        log.level4Debug("A new zip file has been initiated at:\n\t"+
                outputZip);
        //Create the temp dir
    }
    
    
    public String getTempFolder() {
        return TempFolder;
    }

    public void addToTempFolderLoc(String TempFolder) {
        this.TempFolder = this.TempFolder + slash + TempFolder;
        if (!(new File(this.TempFolder).exists()))
            new File(this.TempFolder).mkdirs();
    }
    
    /**
     *adds files to zip
     * @param zipFile specified zip file
     * @param fileToAdd file to be added
     * @throws IOException
     */
    public static void addFilesToExistingZip(String zipFile, String fileToAdd) throws IOException {
        File zip = new File(zipFile);
        File file = new File(fileToAdd);
        addFilesToExistingZip(zip, new File[]{file});
    }

    /**
     * adds files to existing zip file
     * @param zipFile zip file
     * @param filesToBeZipped file to be added
     * @throws IOException
     */
    public static void addFilesToExistingZip(String zipFile, String[] filesToBeZipped) throws IOException {
        File[] fileList = new File[filesToBeZipped.length];
        int i = 0;
        for (String file : filesToBeZipped) {
            fileList[i] = new File(file);
        }
        File zip = new File(zipFile);
        addFilesToExistingZip(zip, fileList);
    }

    /**
     *
     * adds files to existing zip file
     * @param zipFile zip file
     * @param fileToAdd file to be added
     * @throws IOException  
     */
    public static void addFilesToExistingZip(File zipFile, File fileToAdd) throws IOException {
        addFilesToExistingZip(zipFile, new File[]{fileToAdd});
    }

    /**
     * adds files to existing zip file
     * @param zipFile zip file
     * @param fileToAdd file to be added
     * @throws IOException
     */
    public static void addFilesToExistingZip(File zipFile, String fileToAdd) throws IOException {
        File f = new File(fileToAdd);
        File[] zipAdd = new File[]{f};
        addFilesToExistingZip(zipFile, zipAdd);
    }
    /**
     * adds files to existing zip file
     * @param zipFile zip file
     * @param files files to be zipped
     * @throws IOException
     */
    public static void addFilesToExistingZip(File zipFile, File[] files) throws IOException {
        byte[] buf = new byte[4096];


        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();
        //try rename
        boolean renameOk = zipFile.renameTo(tempFile);
        boolean copyOk = false;
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
            copyOk = true;
        }
        if (!renameOk && !copyOk) {
            throw new RuntimeException("could not rename or copy the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
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
    /**
     * adds files to existing zip file
     * @param newZip zip file
     * @param toBeZipped file to be added
     * @throws Exception 
     */
    public static void addFilesToNewZip(String newZip, String toBeZipped) throws Exception {
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
    
    private void addFileToZip(File file) throws IOException
    {
        
        if (!file.exists())
        {
            log.level0Error("File: " + file.toString() + " not found while adding to zip");
            return;
        }
        
        //First we need to create the file (empty) in the temp directory if its
        //not there all ready
        File fileToAdd = new File(TempFolder + slash + file.getName());
        if (!fileToAdd.exists())
            fileToAdd.createNewFile();

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
            if(source != null) {
                source.close();
            }
            if(dest != null) {
                dest.close();
            }
        }
        
    }
    
    //SHOULD ONLY BE CALLED FROM addDirectory
     private void addFileToZip(File file, File destFolder) throws IOException
    {
        
        if (!file.exists())
        {
            log.level0Error("File: " + file.toString() + " not found while adding to zip.");
            return;
        }
        
        //First we need to create the file (empty) in the temp directory if its
        //not there all ready
        File fileToAdd = new File(destFolder.toString() + slash + file.getName());
        if (!fileToAdd.exists())
            fileToAdd.createNewFile();

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
            if(source != null) {
                source.close();
            }
            if(dest != null) {
                dest.close();
            }
        }
        
    }
     
     
    /**
     *
     * @param file
     * @throws IOException
     */
    public void addToZip(File file) throws IOException
    {
        if (!file.exists())
        {
            log.level0Error("File: " + file.toString() + " not found while adding to zip.");
            return;
        }
        if (file.isFile())
            addFileToZip(file);
        
        if (file.isDirectory())
            addDirectoryToZip(file,null);
            
        
    }

    private void addDirectoryToZip(File folder, File parent) throws IOException {
        File dirToAdd = null;
        if (parent == null)
            dirToAdd = new File(TempFolder+ slash + folder.getName() );
        else
            dirToAdd = new File(parent.toString()+ slash + folder.getName());
        if (!dirToAdd.exists())
            dirToAdd.mkdir();
        for ( File c : folder.listFiles())
        {
            if (c.isDirectory())
                    addDirectoryToZip(c,dirToAdd);
            else
                addFileToZip(c,dirToAdd);
        }
        
    }
    
    
    
    /**
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    
    public void execute(String file) throws FileNotFoundException, IOException {
        zipDir(file.toString(), outputZip, "");
    }
    //TODO: Make the files work if directories are empty.
    public void execute() throws FileNotFoundException, IOException {
        zipDir(TempFolder, outputZip, "");
    }
    
 


    /**
     * Zip up a directory
     * 
     * @param directory
     * @param zipName
     * @param path 
     * @throws IOException
     */
    public static void zipDir(String directory, String zipName, String path) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipName))) {
            zipDir(directory, zos, path);
        }
    }

    /**
     * Zip up a directory path
     * @param directory
     * @param zos
     * @param path
     * @throws IOException
     */
    public static void zipDir(String directory, ZipOutputStream zos, String path) throws IOException {
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
}
