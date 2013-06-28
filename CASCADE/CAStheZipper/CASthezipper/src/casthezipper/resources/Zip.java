/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package casthezipper.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author loganludington
 */
public class Zip {
    private String name;
    private String location;
    private final String slash = System.getProperty("file.separator");
    private final String tempDir = System.getProperty("java.io.tmpdir");
    private final File casualTempFolder;

    /**
     *
     * @param destWithFilename
     */
    public Zip(String destWithFilename) {
        //First well seperate the string and store them into the name of the zip
        //file being created and the location of where the zip file is going to
        //be stored
        name = destWithFilename.substring(destWithFilename.lastIndexOf(slash)+1,destWithFilename.length());
        location = destWithFilename.substring(0,destWithFilename.lastIndexOf(slash));
        
        //Next were going to create a temp file that will act as the workspace 
        //for our zip. To do this well create a folder with the params
        // TempDirlocation + CASCADE_Username_MMddyyyy_HHmmss
        DateFormat dateFormat = new SimpleDateFormat("MMddyyyy_HHmmss");
        Date date = new Date();
        String tempEndString = dateFormat.format(date);
        String tempDirString = "CASCADE" + "_" + System.getProperty("user.name")+ "_"+ tempEndString;
        casualTempFolder = new File(tempDir + tempDirString);
        
        //Create the temp dir
        casualTempFolder.mkdir();
        deleteOnShutdown(casualTempFolder);
    }
    
    
    //TODO: look at making this private
    private void addFileToZip(File file) throws IOException
    {
        
        if (!file.exists())
        {
            //TODO: Impliment Logger from casual
            System.out.println("File: " + file.toString() + " not found.");
            return;
        }
        
        //First we need to create the file (empty) in the temp directory if its
        //not there all ready
        File fileToAdd = new File(casualTempFolder.toString() + slash + file.getName());
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
            //TODO: Impliment Logger from casual
            System.out.println("File: " + file.toString() + " not found.");
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
            //TODO: Impliment Logger from casual
            System.out.println("File: " + file.toString() + " not found.");
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
            dirToAdd = new File(casualTempFolder.toString()+ slash + folder.getName() );
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
    
    //TODO: Make the files work if directories are empty.
    public void execute() throws FileNotFoundException, IOException
    {
        
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(location+slash+name));
        zipDir(casualTempFolder.toString(), location + slash + name , "");
        
        
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
        // create a ZipOutputStream to zip the data to
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipName));
        zipDir(directory, zos, path);
        // close the stream
        zos.close();
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


    private void deleteOnShutdown( final File file){
            Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run(){
                if (file.isDirectory()) {                   
                    for (File c : file.listFiles())
                        if(c.isDirectory())
                            recursiveDelete(c);
                        else
                        {
                            c.delete();
                        }
                    }
                file.delete();
                }
            }
    );
    }
    
    private void recursiveDelete(File file){
        if (file.isDirectory()) {                   
                    for (File c : file.listFiles())
                        if(c.isDirectory())
                            recursiveDelete(c);
                        else
                        {
                            c.delete();
                        }
                    }
                file.delete();
    }
}