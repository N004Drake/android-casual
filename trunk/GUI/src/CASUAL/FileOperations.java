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

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public class FileOperations {

    Log Log = new Log();
    Shell shellCommand = new Shell();

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
            InputStream resourceAsStream = getClass().getResourceAsStream(Resource);
            try {
                if (resourceAsStream.available() >= 1) {
                    File Destination = new File(toFile);
                    writeInputStreamToFile(resourceAsStream, Destination);
                    if (Destination.length() >= 1) {
                        return true;
                    } else {

                        Log.level0Error("Failed to write file");
                        return false;
                    }

                } else {
                    Log.level0Error("Critical Error copying " + Resource);
                }
            } catch (NullPointerException e) {
                return false;
            }




        } catch (IOException ex) {
            Logger.getLogger(FileOperations.class.getName()).log(Level.SEVERE, null, ex);
            Log.level0Error("Critical Error copying " + Resource);
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
            Log.level2Information("Cleaning up folder:" + path.toString());

            for (File file : c) {
                if (file.isDirectory()) {
                    Log.level4Debug("Deleting " + file.toString());
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
    public boolean verifyReadPermissionsRecursive(String path) {
        File Check = new File(path);
        File[] c = Check.listFiles();
        if (Check.exists()) {
            Log.level2Information("Verifying permissions in folder:" + path.toString());
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
        if (Check.exists()) {
            Log.level2Information("Searching for file in folder:" + PathToSearch.toString());
            for (File file : c) {
                String x = file.getName();
                if (file.isDirectory()) {
                    Log.level4Debug("Searching " + file.toString());
                    File[] subdir = file.listFiles();
                    for (File sub : subdir) {
                        if (sub.isDirectory()) {
                            String FoundFile = findRecursive(sub.toString(), FileName);
                            if (FoundFile.toString().endsWith(FileName)) {
                                return FoundFile;
                            }
                        } else {
                            if (sub.toString().equals(FileName)) {
                                return sub.toString();
                            }
                        }
                    }
                } else if (file.getName().equals(FileName)) {
                    return file.getAbsoluteFile().toString();
                }
            }
        }
        return null;
    }

    /**
     * verifies file/folder exists returns a boolean value if the file exists
     *
     * @param folder
     * @return true if exists
     */
    public boolean verifyExists(String file) {
        return new File(file).exists();
    }

    /**
     * makes a folder, works recursively
     *
     * @param Folder
     * @return true if folder was created
     */
    public boolean makeFolder(String Folder) {
        Boolean CreatedFolder;
        File folder = new File(Folder);

        if (folder.exists()) {
            return false;
        } else {
            CreatedFolder = folder.mkdirs();
        }
        if (CreatedFolder) {
            //Log.level3Verbose("Created Folder:" + Folder);
        } else {

            CreatedFolder = false;
            Log.level0Error("Could not create temp folder in " + Folder);
        }

        return CreatedFolder;
    }

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
        Log.level4Debug("Write Finished");
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
        Log.level4Debug("File overwrite Finished");
    }

    private boolean writeInputStreamToFile(InputStream is, File file) {
        Log.level4Debug("Attempting to write " + file.getPath());
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
                Log.level0Error("ERROR: FILE READ WAS 0 LENGTH");
                return false;
            }
            is.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.print(e);
            System.err.println("Error Writing/Reading Streams.");
            return false;
        }
        if ((file.exists()) && (file.length() >= 4)) {
            Log.level4Debug("File verified.");
            return true;
        } else {
            Log.level0Error(file.getAbsolutePath() + " Was a failed attempt to write- does not exist.");
            Log.level2Information("false");
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
                Log.level4Debug("Deleted " + FileName);
            } else {
                Deleted = false;
                Log.level0Error("ERROR DELETING FILE:" + FileName);
            }
        } else {
            Deleted = true;
        }
        return Deleted;
    }

    /**
     * copies a file from a source to a destination
     *
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public void copyFile(File sourceFile, File destFile) throws IOException {

        Log.level4Debug("Copying " + sourceFile.getPath() + " to " + destFile.getPath());
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
        Log.level4Debug("Detected current folder: " + CurrentDir);
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
        boolean Result = (FileFolder.length() >= 1);
        Log.level4Debug("Verifying " + Folder + " .  Result=" + Result);
        Log.level4Debug("Result=" + Result);
        return (Result);
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
        Log.level4Debug("Setting executable " + Exe + ". Result=" + Result);
        return Result;
    }

    /**
     * takes a string resource name returns result if it exists
     * @param Res
     * @return true if resource exists
     */
    public boolean verifyResource(String res) {
        if ((getClass().getResource(res))==null) {
            return false;
        } else {
            return true;
        }
    }
    
    private String setDest(String FileName) {
        return Statics.TempFolder + FileName;
    }

    private String setRes(String FileName) {
        return Statics.ScriptLocation + FileName;
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
            Log.level0Error("Could not find resource named:" + Resource);
            Logger.getLogger(FileOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Log.level3(text.toString());
        return text.toString();
    }

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

    /*
     * reads file contents returns string
     */
    String readFile(String FileOnDisk) {
        String EntireFile = "";
        try {
            String Line;
            BufferedReader BROriginal = new BufferedReader(new FileReader(FileOnDisk));
            while ((Line = BROriginal.readLine()) != null) {
                //Log.level3(Line);  
                EntireFile = EntireFile + "\n" + Line;
            }
        } catch (FileNotFoundException ex) {
            Log.level0Error("File Not Found Error: " + FileOnDisk);

        } catch (IOException ex) {
            Log.level0Error("Permission Error: " + FileOnDisk);
        }
        EntireFile = EntireFile.replaceFirst("\n", "");
        return EntireFile;
    }
}
