/*Odin provides a set of tools to make CASUAL operate using Odin parameters. 
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
package CASUAL.Heimdall.Odin;

import CASUAL.Heimdall.HeimdallTools;
import CASUAL.Statics;
import CASUAL.Heimdall.Odin.CorruptOdinFileException;
import CASUAL.Heimdall.Odin.OdinFile;
import CASUAL.archiving.libpit.PitData;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.ArchiveException;

/**
 * provides a set of tools to make CASUAL operate using Odin parameters.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class Odin {

    final String heimdallBinaryLocation;

    /**
     * Public constructor creates pit stream from pit file
     *
     * @param heimdallLocation location to Heimdall binary
     */
    public Odin(String heimdallLocation) {
        this.heimdallBinaryLocation = heimdallLocation;
    }

    /**
     * True if device should be rebooted upon completion.
     */
    public boolean autoReboot = false;

    /**
     * true if the device should be repartitioned.
     */
    public boolean repartition = false;

    /**
     * True if the new pit file should be flashed.
     */
    public boolean flashPit = false;

    /**
     * Pit File to use/flash.
     */
    public File pit = null;

    /**
     * True if bootloader should be flashed.
     */
    public boolean flashBootloader = false;

    /**
     * OdinFile commanded to be flashed first.
     */
    public File bootloader = null;

    /**
     * True if PDA should be flashed.
     */
    public boolean flashPda = false;

    /**
     * OdinFile commanded to be flashed second.
     */
    public File pda = null;

    /**
     * True if Phone should be flashed.
     */
    public boolean flashPhone = false;

    /**
     * OdinFile commanded to be flashed second.
     */
    public File phone = null;

    /**
     * True if CSC should be flashed. 
     */
    public boolean flashCsc = false;

    /**
     * CSC file to be flashed. 
     */
    public File csc = null;

    /**
     * Resets the data to default values. 
     */
    public void reset() {
        autoReboot = false;
        repartition = false;
        flashPit = false;
        pit = null;
        flashBootloader = false;
        bootloader = null;
        flashPda = false;
        pda = null;
        flashPhone = false;
        phone = null;
        flashCsc = false;
        csc = null;

    }

    /**
     * Gets the Heimdall command for Odin compatability. 
     * @return string[] command to flash odin from parameters above. 
     * @throws FileNotFoundException
     * @throws CorruptOdinFileException
     */
    public String[] getOdinCommand() throws FileNotFoundException, FileNotFoundException, CorruptOdinFileException {
        ArrayList<String> odinCommand = new ArrayList<String>();
        odinCommand.add(heimdallBinaryLocation);

        if (pit == null) {
            //TODO: do command to get PIT file from the device then prepare it for flash again here

        }

        if (pit != null) {
            if (repartition) {
                odinCommand.add("--repartion");
            }

            //we will always flash with PIT file even if it is not provided, we should have obtained it.
            odinCommand.add("--PIT");
            odinCommand.add(pit.getAbsolutePath());

            if (this.flashBootloader) {
                try {
                    getPartitionFilenameList(pit, new OdinFile(bootloader).extractOdinContents(Statics.getTempFolder()), odinCommand);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CorruptOdinFileException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ArchiveException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (this.flashPda) {
                try {
                    getPartitionFilenameList(pit, new OdinFile(pda).extractOdinContents(Statics.getTempFolder()), odinCommand);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CorruptOdinFileException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ArchiveException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (this.flashPhone) {
                try {
                    getPartitionFilenameList(pit, new OdinFile(phone).extractOdinContents(Statics.getTempFolder()), odinCommand);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CorruptOdinFileException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ArchiveException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (this.flashCsc) {
                try {
                    getPartitionFilenameList(pit, new OdinFile(csc).extractOdinContents(Statics.getTempFolder()), odinCommand);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CorruptOdinFileException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ArchiveException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Odin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else {
            return null; //failure
        }

        return odinCommand.toArray(new String[odinCommand.size()]);
    }

    /**
     * gets the command to flash the specified files. 
     * @param pitFile PitFile to locate the filesToFlash locations
     * @param filesToFlash Files to be flashed. 
     * @return Processed command. 
     * @throws FileNotFoundException
     */
    public String[] getFlashFilesCommand(File pitFile, File[] filesToFlash) throws FileNotFoundException {
        ArrayList<String> heimdallCommand = getHeimdallCommand();
        heimdallCommand.add("flash");
        getPartitionFilenameList(pitFile, filesToFlash, heimdallCommand);
        return heimdallCommand.toArray(new String[heimdallCommand.size()]);
    }

    /**
     * Adds additional comands to existing command. 
     * @param ExistingCommand existing Heimdall command. 
     * @param pitFile PIT file to be used as a reference. 
     * @param filesToFlash Files which are to be flashed. 
     * @return flash command. 
     * @throws FileNotFoundException
     */
    public String[] addFlashFiles(String[] ExistingCommand, File pitFile, File[] filesToFlash) throws FileNotFoundException {
        ArrayList<String> heimdallCommand = new ArrayList<String>(Arrays.asList(ExistingCommand));
        getPartitionFilenameList(pitFile, filesToFlash, heimdallCommand);
        return heimdallCommand.toArray(new String[heimdallCommand.size()]);
    }

    /**
     * adds the repartition command. 
     * @param ExistingCommand  existing Heimdall command. 
     * @param pitFile Pit File to flash. 
     * @return Heimdall command array. 
     * @throws FileNotFoundException
     */
    public String[] addRepartitionCommand(String[] ExistingCommand, File pitFile) throws FileNotFoundException {
        if (!pitFile.exists()) {
            throw new FileNotFoundException();
        }
        ArrayList<String> heimdallCommand = new ArrayList<String>(Arrays.asList(ExistingCommand));
        heimdallCommand.add("--PIT");
        heimdallCommand.add("--repartition");
        heimdallCommand.add(pitFile.getAbsolutePath());
        return heimdallCommand.toArray(new String[heimdallCommand.size()]);
    }

    private ArrayList<String> getHeimdallCommand() {
        ArrayList<String> heimdallCommand = new ArrayList<String>();
        heimdallCommand.add(heimdallBinaryLocation);
        return heimdallCommand;
    }

    private ArrayList<String> getPartitionFilenameList(File pitFile, File[] fileList, ArrayList<String> heimdallCommand) throws FileNotFoundException {
        PitData pittable=new PitData(pitFile);
        for (File f : fileList) {
            //we wouldn't be using this if the files dont exist
            if (!f.exists()) {
                throw new FileNotFoundException();
            }
            heimdallCommand.add("--" + pittable.findEntryByFilename(f.getName()).getOdinFlashablePartitionName());
            heimdallCommand.add(f.getAbsolutePath());
        }
        return heimdallCommand;
    }

    public File getPitNoReboot(){
        String[] cmd=new String[]{HeimdallTools.getHeimdallCommand()};
        
        return new File(".");
            
        }
    }
    

