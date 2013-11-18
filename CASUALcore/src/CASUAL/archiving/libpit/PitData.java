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

    public static final int FILE_IDENTIFIER = 0x12349876;
    private int entryCount; // 0x04
    char[] fileType = new char[8];
    char[] pitName = new char[12];

    // Entries start at 0x1C
    private final ArrayList<PitEntry> entries = new ArrayList<PitEntry>();

    public PitData() {
    }

    public PitData(PitInputStream pis) throws PitFormatException {
        unpack(pis);
    }

    public PitData(File pit) throws FileNotFoundException, PitFormatException {
        this(new PitInputStream(new FileInputStream(pit)));

    }

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
                entry.getFileSize(pitInputStream.readInt());

                //read partition name
                pitInputStream.read(buffer, 0, PitEntry.PARTITION_NAME_MAX_LENGTH);
                entry.setPartitionName(new String(buffer, 0, PitEntry.PARTITION_NAME_MAX_LENGTH));

                //read filename
                pitInputStream.read(buffer, 0, PitEntry.FILENAME_MAX_LENGTH);
                entry.setFilename(new String(buffer, 0, PitEntry.FILENAME_MAX_LENGTH));

                //read fota name
                pitInputStream.read(buffer, 0, PitEntry.FOTA_NAME_MAX_LENGTH);
                entry.setFotaName(new String(buffer, 0, PitEntry.FOTA_NAME_MAX_LENGTH));

            }

            return (true);
        } catch (IOException e) {
            return (false);
        }
    }

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
                dataOutputStream.write(get32CharArray(entry.getPartitionName()));
                dataOutputStream.write(get32CharArray(entry.getFilename()));
                dataOutputStream.write(get32CharArray(entry.getFotaName()));

            }

            return (true);
        } catch (IOException e) {
            return (false);
        }
    }

    byte[] get32CharArray(String value) {
        char[] a = value.toCharArray();
        byte[] byteArray = new byte[32];
        Arrays.fill(byteArray, (byte) 0);
        for (int i = 0; i < value.length(); i++) {
            byteArray[i] = (byte) (a[i]);
        }
        return byteArray;
    }

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

    public void clear() {
        entryCount = 0;
        fileType = new char[8];
        pitName = new char[12];
        entries.clear();
    }

    public PitEntry getEntry(int index) {
        return (entries.get(index));
    }

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

    public PitEntry findEntry(int partitionIdentifier) {
        for (int i = 0; i < entries.size(); i++) {
            PitEntry entry = entries.get(i);

            if (entry.getPartID() == partitionIdentifier) {
                return (entry);
            }
        }

        return (null);
    }

    public void addEntry(PitEntry entry) {
        entries.add(entryCount++, entry);
    }

    public int getEntryCount() {
        return (entryCount);
    }

    public char[] getFileType() {
        return fileType;
    }

    public char[] getPhone() {
        return pitName;
    }
}
