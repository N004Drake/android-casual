/*CASPACWebOrganizer provides a way to extract meta and properties from caspacs. 
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
 *  along with this program.  If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.casual_dev.caspacweborganizer;

import CASUAL.FileOperations;
import CASUAL.Log;

import CASUAL.archiving.Unzip;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

/**
 * extracts meta and properties for use on builds.casual-dev.com
 *
 * @author adamoutler
 */
public class CASPACWebOrganizer {

    static File folder;
    ArrayList<String> candidates = new ArrayList<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("You must specify a folder to be formatted");
            quit(1);
        }

        folder = new File(args[0]);
        if (!folder.isDirectory()) {
            System.out.println("You must select a folder to process");
            quit(2);
        }
        new CASPACWebOrganizer().organize();
    }

    public static void quit(int x) {
        System.exit(x);
    }

    public void organize() {

        //get list of files
        String[] files = getListOfFiles();
        buildListOfPossibleCASPACs(files);
        for (String candidate : candidates.toArray(new String[candidates.size()])) {
          
            try {
                Unzip unzip = new Unzip(candidate);
                ZipEntry entry = validateCaspac(unzip);
                if (null != entry) {
                    System.out.println("Validated candidate " + candidate );
                    deployEntries(unzip, entry, candidate);
                }
            } catch (IOException ex) {
                System.out.println("organize() Permissions/read error while handling "+candidate);
            }
        }

    }

    private ZipEntry validateCaspac(Unzip unzip) {
        ZipEntry entry=null;
        while (unzip.zipFileEntries.hasMoreElements() && null==entry) {
            ZipEntry x = unzip.zipFileEntries.nextElement();
            if (x.getName().toLowerCase().endsWith(".properties")) {
                entry=x;
            }
        }
        return entry;
    }

    void buildListOfPossibleCASPACs(String[] files) {
        for (String file : files) {
            if (null == file) {
                break;
            }
            if (file.toLowerCase().endsWith(".zip") || file.toLowerCase().endsWith(".caspac")) {
                System.out.println(file + " possible caspac ");
                candidates.add(file);
            } else {
            }
        }
    }

     String[] getListOfFiles() {
        try {
            ArrayList<String> al=new FileOperations().listRecursive(folder.getCanonicalPath());
            return al.toArray(new String[al.size()]);
        } catch (IOException ex) {
            System.out.println("insufficient permissions for file");
            quit(5);
        }
        return null;
    }

    private String getCaspacNameWithoutExtension(String caspac) {
        //https://builds.casual-dev.com/files/all/testpak
        caspac = caspac.substring(0, caspac.lastIndexOf("."));
        Log.level4Debug("CASPAC meta location:" + caspac);
        return caspac;
    }

    private String getFolderNameOfFile(String caspac) {
        //https://builds.casual-dev.com/files/all/testpak
        return new File(caspac).getParent();
    }

    private void deployEntries(Unzip unzip, ZipEntry entry, String caspacName) {

            
            Path build = FileSystems.getDefault().getPath(getCaspacNameWithoutExtension(caspacName) + ".properties");
            System.out.println("Deploying:"+ entry.getName() + " to:" +build.toString());
            build.toFile().delete();
        try {
            Files.copy( unzip.streamFileFromZip(entry), build);
        } catch (IOException ex) {
            Logger.getLogger(CASPACWebOrganizer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("deployEntries() Permissions error:" + entry.getName() + " to "+ build.toString());

            quit(6);
        }



    }
}
