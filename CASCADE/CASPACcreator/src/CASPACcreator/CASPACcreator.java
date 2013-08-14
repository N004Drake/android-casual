/*CASPACcreator creates a CASPAC using command-line args.
 *Copyright (C) 2013  Logan Ludington
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
package CASPACcreator;

import CASUAL.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import zip.Zip;

/**
 *
 * @author loganludington
 * @author Adam Outler
 */
public class CASPACcreator {

    /**
     * force overwrite
     */
    public boolean force = false;
    /**
     * ignore missing files
     */
    public boolean ignore = false;

    private String outputfile = null;
    List<File> inputfiles = new ArrayList<>();  //temp holding for collecting files.
    boolean shutdown = false;
    Log log = new Log();

    private void doWork(String[] args) {
        argProcessor(args);
        if (!shutdown) {
            try {
                Zip zip = new Zip(new File(outputfile));
                zip.addFilesToExistingZip(inputfiles.toArray(new File[inputfiles.size()]));
            } catch (IOException ex) {
                Logger.getLogger(CASPACcreator.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        CASPACcreator creator = new CASPACcreator();
        creator.doWork(args);
    }

    private void argProcessor(String[] args) {

        //parse args
        for (int i = 0; i < args.length; i++) {

             if (args[i].contains("--force") || args[i].contains("-f")) {
                force = true;
                log.level3Verbose("force overwrite of output file.");
            } else if (args[i].contains("--ignore")) {
                ignore = true;
                log.level3Verbose("ignoring invalid files.");
            } else if (args[i].contains("--output") || args[i].contains("-o")) {
                outputfile = args[++i];
                log.level3Verbose("output file " + args[i]);
            } else if (args[i].startsWith("-")) {
                usage("Error: -" + args[i] + " is not a valid flag.");
            } else {
                    inputfiles.add(new File(args[i]));
            }

        }
        
        //input validation
        if (outputfile == null) {
            usage("Error: You must specify an output file");
        }
        if (inputfiles.size() < 1) {
            usage("Error: Requires 1 or more files.");
        }
        if ((new File(outputfile).exists() && !(force))) {
            usage("Error: Output file exist, please use the -f option to overwrite");
        }

        String missingFiles = "";
        for (File f : inputfiles) {
            if (!(f.exists())) {
                missingFiles = missingFiles + f.getAbsolutePath() + "\n";
            }
        }
        if (ignore) {
            log.level0Error("Warning: The following files are missing: \n"
                    + missingFiles + "\n" + "However the packaging will continue.");
        } else if (!("".equals(missingFiles))) {
            usage("The following missing files or invalid arguments: \n" + missingFiles);
        }


    }

    private void usage(String error) {
        log.level0Error(error);
        log.level2Information("Usage: java -jar CASthezipper.jar  [-fi]  --output output_zipfile inputfile1 ... inputfileN");
        log.level2Information("       -f: Will overwrite existing output_zipfile");
        log.level2Information("       -i: Will ignore nonexisting input files");
        shutdown = true;
    }
}
