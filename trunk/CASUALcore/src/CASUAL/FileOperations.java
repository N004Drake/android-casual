/*FileOperations provides a group of tools which relate to files.
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

import CASUAL.misc.StringOperations;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public class FileOperations {

    Log log = new Log();
    Shell shellCommand = new Shell();

    /**
     * performs file operations
     */
    public FileOperations() {
    }

    /**
     * copies a resource to a file
     *
     * @param Resource
     * @param toFile
     * @return true if complete
     */
    public boolean copyFromResourceToFile(String Resource, String toFile) {
        try {
            try (InputStream resourceAsStream = getClass().getResourceAsStream(Resource)) {
                if (resourceAsStream.available() >= 1) {
                    File Destination = new File(toFile);
                    writeInputStreamToFile(resourceAsStream, Destination);
                    if (Destination.length() >= 1) {
                        resourceAsStream.close();
                        return true;
                    } else {
                        resourceAsStream.close();
                        log.level0Error("@failedToWriteFile");
                        return false;
                    }
                } else {
                    resourceAsStream.close();
                    log.level0Error("@criticalErrorWhileCopying " + Resource);
                }
            } catch (NullPointerException e) {
                return false;
            }




        } catch (IOException ex) {
            Logger.getLogger(FileOperations.class.getName()).log(Level.SEVERE, null, ex);
            log.level0Error("@criticalErrorWhileCopying " + Resource);
            return false;
        }
        return false;
    }

    /**
     * recursively deletes a String path
     *
     * @param path
     */
    public void recursiveDelete(String path) {
        recursiveDelete(new File(path));
    }

    /**
     * recursively deletes a file path
     *
     * @param path
     */
    public void recursiveDelete(File path) {
        File[] c = path.listFiles();
        if (path.exists()) {
            log.level4Debug("Removing folder and contents:" + path.toString());

            for (File file : c) {
                if (file.isDirectory()) {
                    recursiveDelete(file);
                    file.delete();
                } else {
                    file.delete();
                }
            }
            path.delete();
        }
    }

    /**
     * verify ability to write to every file in a path
     *
     * @param path
     * @return true if permission to write
     */
    public boolean verifyWritePermissionsRecursive(String path) {
        File Check = new File(path);
        File[] c = Check.listFiles();
        if (Check.exists()) {
            log.level4Debug("Verifying permissions in folder:" + path.toString());
            for (File file : c) {
                if (!file.canWrite()) {
                    return false;
                }
            }
        }
        return true;
    }
    

    /**
     * takes a path and a name returns qualified path to file
     *
     * @param PathToSearch
     * @param FileName
     * @return absolute path to folder
     */
    public String findRecursive(String PathToSearch, String FileName) {
        File Check = new File(PathToSearch);
        File[] c = Check.listFiles();
        String s="";
        if (Check.exists()) {
            log.level3Verbose("Searching for file in folder:" + PathToSearch.toString());
            for (File file : c) {
                if (file.isDirectory()) {
                    return findRecursive(file.getAbsolutePath(),FileName);
                } else if (file.getName().equals(FileName)) {
                    try {
                        return file.getCanonicalPath();
                    } catch (IOException ex) {
                        Logger.getLogger(FileOperations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            }
        }
        return s;
    }

    /**
     * takes a path and a name returns qualified path to file
     *
     * @param PathToSearch
     * @return absolute path to folder
     */
    public ArrayList listFoldersTwoDeep(String PathToSearch) {
        ArrayList<String[]> al = new ArrayList<>();
        File rootFolder = new File(PathToSearch);
        File[] c = rootFolder.listFiles();
        if (rootFolder.exists()) {
            for (File file : c) {
                if (file.isDirectory()) {
                    File[] subdir1 = file.listFiles();
                    for (File subdir2 : subdir1) {
                        if (subdir2.isDirectory()) {
                            al.add(new String[]{file.getPath(), subdir2.toString()});
                        }
                    }

                }
            }
        }
        return al;
    }

    /**
     * verifies file/folder exists returns a boolean value if the file exists
     *
     * @param file
     * @return true if exists
     */
    public boolean verifyExists(String file) {
        if (file != null && !file.isEmpty()) {
            File f = new File(file);
            if (!f.exists() && !f.isDirectory() && !f.isFile()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * makes a folder, works recursively
     *
     * @param Folder
     * @return true if folder was created
     */
    public boolean makeFolder(String Folder) {
        if (Folder == null) {
            return false;
        }
        File folder = new File(Folder);
        if (folder.exists()) {
            return true;
        } else {
            folder.mkdirs();
            if (folder.exists()){
                return true;
            } else {
                log.level0Error("@couldNotCreateFolder " + Folder);
                return false;
            }
        }
    }

    /**
     * writes a stream to a destination file
     *
     * @param stream Stream to be written
     * @param destination output file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void writeStreamToFile(BufferedInputStream stream, String destination) throws FileNotFoundException, IOException {
        int currentByte;
        int buffer = 4096;
        byte data[] = new byte[buffer];
        File f = new File(destination);
        if (!verifyExists(f.getParent())) {
            makeFolder(f.getParent());
        }
        FileOutputStream fos = new FileOutputStream(f);
        BufferedOutputStream dest;
        dest = new BufferedOutputStream(fos, buffer);
        while ((currentByte = stream.read(data, 0, buffer)) != -1) {
            dest.write(data, 0, currentByte);
        }
        dest.flush();
        dest.close();
    }

    /**
     * takes a string and a filename, writes to the file
     *
     * @param Text
     * @param File
     * @throws IOException
     */
    public void writeToFile(String Text, String File) throws IOException {
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(File, true));
        bw.write(Text);
        bw.close();
        log.level4Debug("Write Finished");
    }

    /**
     * takes a string and a filename, overwrites to the file
     *
     * @param Text
     * @param File
     * @throws IOException
     */
    public void overwriteFile(String Text, String File) throws IOException {
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(File, false));
        bw.write(Text);
        bw.close();
        log.level4Debug("File overwrite Finished");
    }

    private boolean writeInputStreamToFile(InputStream is, File file) {
        log.level4Debug("Attempting to write " + file.getPath());
        try {
            BufferedOutputStream out;
            out = new BufferedOutputStream(new FileOutputStream(file));
            int currentByte;
            // establish buffer for writing file
            int BUFFER = 4096;
            byte data[] = new byte[BUFFER];
            if (is.available() > 0) {
                // while stream does not return -1, fill data buffer and write.
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, currentByte);
                }
            } else {
                return false;
            }
            is.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            return false;
        }
        if (file.exists() && file.length() >= 4) {
            log.level4Debug("File verified.");
            return true;
        } else {
            log.level0Error("@failedToWriteFile");
            return false;
        }
    }

    /**
     * takes a string filename returns a boolean if the file was deleted
     *
     * @param FileName
     * @return true if file was deleted
     */
    public Boolean deleteFile(String FileName) {
        Boolean Deleted;
        File file = new File(FileName);
        if (file.exists()) {
            if (file.delete()) {
                Deleted = true;
                log.level4Debug("Deleted " + FileName);
            } else {
                Deleted = false;
                log.level0Error("@couldNotDeleteFile" + FileName);
            }
        } else {
            Deleted = true;
        }
        return Deleted;
    }

    /**
     * deletes files
     *
     * @param cleanUp files to be deleted
     * @return true if all files were deleted false and halts on error
     */
    public boolean deleteStringArrayOfFiles(String[] cleanUp) {
        for (String s:cleanUp){
            if (s!=null){
                new File(s).delete();
            } else {
                continue;
            }
            if (this.verifyExists(s)){
                return false;
            }
       } //all files were deleted
       return true;   
    }
    /**
     * copies a file from a source to a destination
     *
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public void copyFile(File sourceFile, File destFile) throws IOException {

        log.level4Debug("Copying " + sourceFile.getPath() + " to " + destFile.getPath());
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
        }
        if (destination != null) {
            destination.close();

        }


    }

    /**
     * returns the name of the current folder
     *
     * @return current folder
     */
    public String currentDir() {
        String CurrentDir = new File(".").getAbsolutePath();
        log.level4Debug("Detected current folder: " + CurrentDir);
        if (CurrentDir.endsWith(".")) {
            CurrentDir = CurrentDir.substring(0, CurrentDir.length() - 1);
        }
        return CurrentDir;
    }

    /**
     * copies a file from a string path to a string path returns a boolean if
     * completed
     *
     * @param FromFile
     * @param ToFile
     * @return true if completed
     */
    public boolean copyFile(String FromFile, String ToFile) {
        File OriginalFile = new File(FromFile);
        File DestinationFile = new File(ToFile);
        try {
            copyFile(OriginalFile, DestinationFile);
            return true;
        } catch (IOException ex) {
            return false;
        }

    }

    /**
     * take a string filename returns a boolean if file exists
     *
     * @param Folder
     * @return true if file exists
     */
    public boolean verifyFileExists(String Folder) {
        File FileFolder = new File(Folder);
        boolean Result = FileFolder.length() >= 1;
        log.level4Debug("Verifying " + Folder + " .  Result=" + Result);
        log.level4Debug("Result=" + Result);
        return Result;
    }

    /**
     * takes a filename sets executable returns result
     *
     * @param Executable
     * @return true if executable bit was set
     */
    public boolean setExecutableBit(String Executable) {
        File Exe = new File(Executable);
        boolean Result = Exe.setExecutable(true);
        log.level4Debug("Setting executable " + Exe + ". Result=" + Result);
        return Result;
    }

    /**
     * takes a string resource name returns result if it exists
     *
     * @param res resource to verify
     * @return true if resource exists
     */
    public boolean verifyResource(String res) {
        if (getClass().getResource(res) == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * takes a resource name returns a string of file contents
     *
     * @param Resource
     * @return string contents of resource
     */
    public String readTextFromResource(String Resource) {
        InputStream resourceAsStream = getClass().getResourceAsStream(Resource);
        StringBuilder text = new StringBuilder();
        try {
            InputStreamReader in;
            in = new InputStreamReader(resourceAsStream, "UTF-8");
            int read;
            while ((read = in.read()) != -1) {
                char C = Character.valueOf((char) read);
                text.append(C);
            }
            in.close();
        } catch (NullPointerException ex) {
            log.level0Error("@resourceNotFound:" + Resource);
        } catch (IOException ex) {
            log.level0Error("@resourceNotFound:" + Resource);
        }
        //Log.level3(text.toString());
        return text.toString();
    }

    /**
     * reads text from stream
     *
     * @param in stream to read
     * @return text output
     */
    public String readTextFromStream(BufferedInputStream in) {
        StringBuilder text = new StringBuilder();
        try {
            int read;
            while ((read = in.read()) != -1) {
                char C = Character.valueOf((char) read);
                text.append(C);
            }
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(FileOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Log.level3(text.toString());
        return text.toString();
    }

    /**
     * reads file contents returns string
     *
     * @param FileOnDisk file to read
     * @return string representation of file
     */
    public String readFile(String FileOnDisk) {
        String EntireFile = "";
        try {
            String Line;
            try (BufferedReader br = new BufferedReader(new FileReader(FileOnDisk))) {
                while ((Line = br.readLine()) != null) {
                    //Log.level3(Line);  
                    EntireFile = EntireFile + "\n" + Line;
                }
            }
        } catch (IOException ex) {
            log.level2Information("@fileNotFound " + FileOnDisk);
        }
        EntireFile = EntireFile.replaceFirst("\n", "");
        return EntireFile;
    }

    /**
     * lists files in a folder
     *
     * @param folder folder to list
     * @return array of filenames
     */
    public String[] listFolderFiles(String folder) {
        File dir = new File(folder);
        if (!dir.isDirectory()) {
            new Log().level0Error("@fileNotAFolder");
            return null;
        }
        ArrayList<String> files = new ArrayList<>();
        File[] list = dir.listFiles();
        for (int x = 0; list.length > x; x++) {
            files.add(list[x].getName());
        }
        return StringOperations.convertArrayListToStringArray(files);
    }

    /**
     * lists files with full qualifiers
     *
     * @param folder folder to list
     * @return array of files
     */
    public String[] listFolderFilesCannonically(String folder) {
        File dir = new File(folder);
        if (!dir.isDirectory()) {
            new Log().level0Error("\"@fileNotAFolder");
            return null;
        }
        String[] childOf = new String[1024];
        File[] list = dir.listFiles();
        for (int x = 0; list.length > x; x++) {
            try {
                childOf[x] = list[x].getCanonicalFile().toString();
            } catch (IOException ex) {
                Logger.getLogger(FileOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return childOf;
    }

    /**
     *
     * @param sourceFile from locaton
     * @param destFile to location
     * @return true if moved
     * @throws IOException
     */
    public boolean moveFile(File sourceFile, File destFile) throws IOException {
        FileOperations fO = new FileOperations();
        if (fO.copyFile(sourceFile.toString(), destFile.toString())) {
            if (fO.deleteFile(sourceFile.toString())) {
                log.level4Debug("[moveFile()]File moved successfully");
                return true;
            } else {
                log.level4Debug("[moveFile()]File copied, unable to remove source");
                return false;
            }
        } else {
            log.level4Debug("[moveFile()]Unable to copy from source");
            return false;
        }
    }

    /**
     * moves a file
     *
     * @param sourceFile from location
     * @param destFile to location
     * @return true if moved
     * @throws IOException
     */
    public boolean moveFile(String sourceFile, String destFile) throws IOException {
        FileOperations fo = new FileOperations();
        if (!fo.verifyExists(sourceFile)) {
            log.level4Debug("[moveFile()] Source doesn't exist");
            return false;
        }
        if (fo.verifyExists(destFile)) {
            fo.deleteFile(destFile);
        }
        if (fo.copyFile(sourceFile, destFile)) {
            if (fo.deleteFile(sourceFile)) {
                log.level4Debug("[moveFile()]File moved successfully");
                return true;
            } else {
                log.level4Debug("[moveFile()]File copied, unable to remove source");
                return false;
            }
        } else {
            log.level4Debug("[moveFile()]Unable to copy source to destination");
            return false;
        }
    }
}
