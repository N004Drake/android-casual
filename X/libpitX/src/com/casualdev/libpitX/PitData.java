package CASUAL.archiving.libpit;
/*Copyright (c) 2010-2011 Benjamin Dobell, Glass Echidna
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Pitdata provides a way to work with the header information of the PIT file
 * Original Files may be found here:
 * https://github.com/Benjamin-Dobell/libpit--Java-/tree/master/libpit/src/au/com/glassechidna/libpit
 * modified by:
 *
 * @author adam
 */
public class PitData {

    /**
     * Magic Number to identify an Odin File
     */
    public static final int FILE_IDENTIFIER = 0x12349876;
    private int entryCount; // 0x04
    char[] fileType = new char[8];
    char[] pitName = new char[12];

    // Entries start at 0x1C
    private final ArrayList<PitEntry> entries = new ArrayList<PitEntry>();
    ByteArrayOutputStream signature = new ByteArrayOutputStream();

    /**
     * Constructor for new PIT file
     */
    public PitData() {
    }

    /**
     * Constructor to grab PIT file from an InputStream
     *
     * @param PitStream inputStream containing a PIT file only
     */
    public PitData(PitInputStream PitStream) {
        unpack(PitStream);
    }

    /**
     * Constructor to grab PIT file from a File
     *
     * @param pit PIT file
     * @throws FileNotFoundException
     */
    public PitData(File pit) throws FileNotFoundException {
        this(new PitInputStream(new FileInputStream(pit)));
    }

    /**
     * unpacks a PIT into the PitData and its PitEntry classes
     *
     * @param pitInputStream InputStream containing only a Pit FIle
     * @return true if unpack was performed
     */
    public final boolean unpack(PitInputStream pitInputStream) {
        try {
            int pitID = pitInputStream.readInt();
            if (pitID != FILE_IDENTIFIER) {
                return (false);
            }

            entries.clear();

            entryCount = pitInputStream.readInt();

            entries.ensureCapacity(entryCount);

            //read 8 bytes of filetype
            for (int i = 0; i < 8; i++) {
                fileType[i] = (char) pitInputStream.read();
            }

            //read 8 bytes of phone name
            for (int i = 0; i < 12; i++) {
                pitName[i] = (char) pitInputStream.read();
            }

            byte[] buffer = new byte[PitEntry.FILENAME_MAX_LENGTH];

            for (int i = 0; i < entryCount; i++) {
                PitEntry entry = new PitEntry();
                entries.add(entry);

                entry.setBinType(pitInputStream.readInt());
                entry.setDevType(pitInputStream.readInt());
                entry.setPartID(pitInputStream.readInt());
                entry.setAttributes(pitInputStream.readInt());
                entry.setFilesystem(pitInputStream.readInt());
                entry.setBlockStart(pitInputStream.readInt());
                entry.setBlockCount(pitInputStream.readInt());
                entry.setFileOffset(pitInputStream.readInt());
                entry.setFileSize(pitInputStream.readInt());

                //read partition name
                pitInputStream.read(buffer, 0, PitEntry.PARTITION_NAME_MAX_LENGTH);
                entry.setPartitionName(buffer);

                //read filename
                pitInputStream.read(buffer, 0, PitEntry.FILENAME_MAX_LENGTH);
                entry.setFilename(buffer);

                //read fota name
                pitInputStream.read(buffer, 0, PitEntry.FOTA_NAME_MAX_LENGTH);
                entry.setPartitionName(buffer);

            }

            int byteRead;
            while ((byteRead = pitInputStream.read()) != -1) {
                signature.write(byteRead);
            }
            return (true);
        } catch (IOException e) {
            return (false);
        }
    }

    /**
     * Packs current object into a PIT file
     * @param dataOutputStream dataoutputstream to write to
     * @return true if sucessful
     */
    public boolean pack(DataOutputStream dataOutputStream) {
        try {

            dataOutputStream.writeInt(Integer.reverseBytes(FILE_IDENTIFIER));

            dataOutputStream.writeInt(Integer.reverseBytes(entryCount));
            for (int i = 0; i < fileType.length; i++) {
                dataOutputStream.write((int) fileType[i]);
            }
            for (int i = 0; i < pitName.length; i++) {
                dataOutputStream.write((int) pitName[i]);
            }

            for (int i = 0; i < entryCount; i++) {
                PitEntry entry = entries.get(i);
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getBinType()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getDevType()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getPartID()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getAttributes()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getFilesystem()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getBlockStart()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getBlockCount()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getFileOffset()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getFileSize()));
                dataOutputStream.write(entry.getPartitionNameBytes());
                dataOutputStream.write(entry.getFileNameBytes());
                dataOutputStream.write(entry.getFotaNameBytes());
            }
            dataOutputStream.write(signature.toByteArray());

            return (true);
        } catch (IOException e) {
            return (false);
        }
    }

    /**
     * tests for a match on a PitData
     * @param otherPitData second pit data to be ested
     * @return true if match
     */
    public boolean matches(PitData otherPitData) {

        if (entryCount == otherPitData.entryCount && Arrays.equals(pitName, otherPitData.pitName) && Arrays.equals(fileType, otherPitData.fileType)) {
            for (int i = 0; i < entryCount; i++) {
                if (!entries.get(i).matches(otherPitData.entries.get(i))) {
                    return (false);
                }
            }

            return (true);
        } else {
            return (false);
        }
    }

    /**
     *
     */
    public void clear() {
        entryCount = 0;
        fileType = new char[8];
        pitName = new char[12];
        entries.clear();
    }

    /**
     * Gets a PitEntry by index
     * @param index index of entry
     * @return PitEntry at index
     */
    public PitEntry getEntry(int index) {
        return (entries.get(index));
    }

    /**
     * gets a PitEntry by Partition name 
     * @param partitionName partition name to be matched
     * @return PitEntry matched by name
     */
    public PitEntry findEntry(String partitionName) {
        for (int i = 0; i < entries.size(); i++) {
            PitEntry entry = entries.get(i);
            String s = entry.getPartitionName();

            if (entry.getPartitionName().equals(partitionName)) {
                return (entry);
            }
        }

        return (null);
    }

    /**
     * gets a PitEntry by filename
     * @param filename filename in pit entry
     * @return PitEntry matched on filename
     */
    public PitEntry findEntryByFilename(String filename) {
        for (int i = 0; i < entries.size(); i++) {
            PitEntry entry = entries.get(i);
            String nameCheck = "";
            for (char c : entry.file_name) {
                if (c == 0) {  //character signifying the end of the name and the beginning of modifier "md5"
                    break;
                } else {
                    nameCheck = nameCheck + c;
                }
            }

            if (filename.equals(nameCheck)) {
                return (entry);
            }
        }
        return (null);
    }

    /**
     * Gets a PitEntry based on partition ID
     * @param partitionIdentifier identifier to match
     * @return PitEntry matched on PartitionIdentifier
     */
    public PitEntry findEntry(int partitionIdentifier) {
        for (int i = 0; i < entries.size(); i++) {
            PitEntry entry = entries.get(i);

            if (entry.getPartID() == partitionIdentifier) {
                return (entry);
            }
        }

        return (null);
    }

    /**
     * Removes a PitEntry from the list of PitEntries
     * @param entry entry to be removed
     */
    public void removeEntry(PitEntry entry){
        entries.remove(entry);
    }
    
    /**
     * Adds a PitEntry to the list of entries
     * @param entry entry to be added
     */
    public void addEntry(PitEntry entry) {
        entries.add(entryCount++, entry);
    }

    /**
     * gets the number of entries
     * @return entry count
     */
    public int getEntryCount() {
        return (entryCount);
    }

    /**
     * returns the file type
     * @return file type
     */
    public char[] getFileType() {
        return fileType;
    }

    /**
     * gets the name of the intended platform
     * @return platform name
     */
    public char[] getPhone() {
        return pitName;
    }

    @Override
    public String toString() {
        String n = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append(n);
        sb.append("PIT Name: ").append(this.pitName).append(n);
        sb.append("Entry Count: ").append(this.entryCount).append(n);
        sb.append("File Type: ").append(this.fileType).append(n);
        sb.append(n);
        sb.append(n);
        for (int i = 0; i < this.entries.size(); i++) {
            sb.append("--- Entry #").append(i).append(" ---").append(n);
            sb.append(entries.get(i).toString());
        }
        return sb.toString();
    }

}
