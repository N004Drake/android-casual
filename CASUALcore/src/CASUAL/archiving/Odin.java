/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.archiving;

import CASUAL.Statics;
import CASUAL.archiving.CorruptOdinFileException;
import CASUAL.archiving.OdinFile;
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
 *
 * @author adamoutler
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

    public boolean autoReboot = false;
    public boolean repartition = false;
    public boolean flashPit = false;
    public File pit = null;
    public boolean flashBootloader = false;
    public File bootloader = null;
    public boolean flashPda = false;
    public File pda = null;
    public boolean flashPhone = false;
    public File phone = null;
    public boolean flashCsc = false;
    public File csc = null;

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

    public String[] getFlashFilesCommand(File pitFile, File[] filesToFlash) throws FileNotFoundException {
        ArrayList<String> heimdallCommand = getHeimdallCommand();
        heimdallCommand.add("flash");
        getPartitionFilenameList(pitFile, filesToFlash, heimdallCommand);
        return heimdallCommand.toArray(new String[heimdallCommand.size()]);
    }

    public String[] addFlashFiles(String[] ExistingCommand, File pitFile, File[] filesToFlash) throws FileNotFoundException {
        ArrayList<String> heimdallCommand = new ArrayList<String>(Arrays.asList(ExistingCommand));
        getPartitionFilenameList(pitFile, filesToFlash, heimdallCommand);
        return heimdallCommand.toArray(new String[heimdallCommand.size()]);
    }

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
        for (File f : fileList) {
            //we wouldn't be using this if the files dont exist
            if (!f.exists()) {
                throw new FileNotFoundException();
            }
            heimdallCommand.add("--" + new PitData(pitFile).findEntryByFilename(f.getName()).getPartitionName());
            heimdallCommand.add(f.getAbsolutePath());
        }
        return heimdallCommand;
    }

}
