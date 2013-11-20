package CASUAL.archiving.libpit;

import java.util.Arrays;

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
/**
 * PitEntry provides a method of organizing PIT entries and storing data
 * Original Files may be found here:
 * https://github.com/Benjamin-Dobell/libpit--Java-/tree/master/libpit/src/au/com/glassechidna/libpit
 * modified by:
 *
 * @author adam
 */
public class PitEntry {

    /**
     * maximum byte length of part_name
     */
    public static final int PARTITION_NAME_MAX_LENGTH = 32;

    /**
     * maximum byte length of file_name
     */
    public static final int FILENAME_MAX_LENGTH = 32;

    /**
     * maximum byte length of fota_name
     */
    public static final int FOTA_NAME_MAX_LENGTH = 32;

    private int bin_type;
    private int dev_type;
    private int part_id;
    private int attributes;
    private int fileSystem;
    private int block_start;
    private int block_count;
    private int file_offset;
    private int file_size;

    /**
     * Partition name.
     */
    public char[] part_name = new char[32];

    /**
     * File name.
     */
    public char[] file_name = new char[32];

    /**
     * Firmware Over The Air name.
     */
    public char[] fota_name = new char[32];

    /**
     * Constructor for PitEntry sets default values
     */
    public PitEntry() {

        dev_type = 0;
        block_start = 0;
        part_id = 0;
        attributes = 0;
        file_offset = 0;
        file_size = 0;
        block_count = 0;
        fileSystem = 0;
        bin_type = 0;
    }

    /**
     * matches this entries parameters against another to detect equivalence.
     *
     * @param otherPitEntry
     * @return
     */
    public boolean matches(PitEntry otherPitEntry) {
        if (dev_type == otherPitEntry.dev_type && block_start == otherPitEntry.block_start && part_id == otherPitEntry.part_id
                && attributes == otherPitEntry.attributes && file_offset == otherPitEntry.file_offset && file_size == otherPitEntry.file_size
                && block_count == otherPitEntry.block_count && fileSystem == otherPitEntry.fileSystem && bin_type == otherPitEntry.bin_type
                && getPartitionName().equals(otherPitEntry.getPartitionName()) && getFilename().equals(otherPitEntry.getFilename())) {
            return (true);
        } else {
            return (false);
        }
    }

    /**
     * binary type
     *
     * @return
     */
    public int getBinType() {
        return bin_type;
    }

    /**
     * binary type
     *
     * @param binType
     */
    public void setBinType(int binType) {
        this.bin_type = binType;
    }

    /**
     * Device Type differs per-device. generally 0=emmc.
     *
     * @return
     */
    public int getDevType() {
        return dev_type;
    }

    /**
     * Device Type differs per-device. generally 0=emmc.
     *
     * @param devType
     */
    public void setDevType(int devType) {
        this.dev_type = devType;
    }

    /**
     * Partition ID is a number which identifies the partition
     *
     * @return
     */
    public int getPartID() {
        return (part_id);
    }

    /**
     * Partition ID is a number which identifies the partition
     *
     * @param partitionIdentifier
     */
    public void setPartID(int partitionIdentifier) {
        this.part_id = partitionIdentifier;
    }

    /**
     * Partition Attributes
     *
     * @return
     */
    public int getAttributes() {
        return (attributes);
    }

    /**
     * Partition Attributes
     *
     * @param partitionFlags
     */
    public void setAttributes(int partitionFlags) {
        this.attributes = partitionFlags;
    }

    /**
     * rfs=0 raw=1 ext4=2
     *
     * @return filesystem type
     */
    public int getFilesystem() {
        return fileSystem;
    }

    /**
     * sets filesystem type rfs=0 raw=1 ext4=2
     *
     * @param filesystem
     */
    public void setFilesystem(int filesystem) {
        this.fileSystem = filesystem;
    }

    /**
     * starting block on EMMC in 512b blocks
     *
     * @return starting block
     */
    public int getBlockStart() {
        return (block_start);
    }

    /**
     * starting block on EMMC in 512b blocks
     *
     * @param blockStart
     */
    public void setBlockStart(int blockStart) {
        this.block_start = blockStart;
    }

    /**
     * number of 512b blocks in partition
     *
     * @return
     */
    public int getBlockCount() {
        return (block_count);
    }

    /**
     * number of 512b blocks in partition
     *
     * @param partitionBlockCount
     */
    public void setBlockCount(int partitionBlockCount) {
        this.block_count = partitionBlockCount;
    }

    /**
     * number of blocks to offset in partition before beginning write
     *
     * @return
     */
    public int getFileOffset() {
        return (file_offset);
    }

    /**
     * number of blocks to offset in partition before beginning write
     *
     * @param fileOffset
     */
    public void setFileOffset(int fileOffset) {
        this.file_offset = fileOffset;
    }

    /**
     * size of file in bytes
     *
     * @return
     */
    public int getFileSize() {
        return (file_size);
    }

    /**
     * size of file in bytes
     *
     * @param partitionBlockSize
     */
    public void setFileSize(int partitionBlockSize) {
        this.file_size = partitionBlockSize;
    }

    /**
     * Proper name of partition used to reference flash location
     *
     * @return
     */
    public byte[] getPartitionNameBytes() {
        return convertCharArrayToByteArray(part_name);
    }

    /**
     * Proper name of partition used to reference flash location
     *
     * @return
     */
    public String getPartitionName() {
        String partitionName = "";
        for (int i = 0; i < part_name.length; i++) {
            if (part_name[i] != 0) {
                partitionName = partitionName + part_name[i];
            }
        }
        return (partitionName);
    }

    /**
     * Proper name of partition used to reference flash location
     *
     * @param partitionName
     */
    public void setPartitionName(byte[] partitionName) {
        part_name = convertByteArrayToCharArray(partitionName);
    }

    /**
     * Proper name of partition used to reference flash location
     *
     * @param partitionName
     */
    public void setPartitionName(String partitionName) {
        if (partitionName.length() < part_name.length) { // "Less than" due to null byte.
            part_name = Arrays.copyOf(partitionName.toCharArray(), part_name.length);
        } else {
            partitionName = partitionName.substring(0, part_name.length - 1);
            part_name = Arrays.copyOf(partitionName.toCharArray(), part_name.length);
        }
    }

    /**
     * Name of file when transferred from device
     *
     * @return
     */
    public byte[] getFileNameBytes() {
        return convertCharArrayToByteArray(file_name);
    }

    /**
     * Name of file when transferred from device
     *
     * @return
     */
    public String getFilename() {
        String filename = "";
        for (int i = 0; i < file_name.length; i++) {
            if (file_name[i] != 0) {
                filename = filename + file_name[i];
            }
        }
        return (filename);
    }

    /**
     * Name of file when transferred from device
     *
     * @param filename
     */
    public void setFilename(byte[] filename) {
        file_name = convertByteArrayToCharArray(filename);
    }

    /**
     * Name of file when transferred from device
     *
     * @param filename
     */
    public void setFilename(String filename) {
        if (filename.length() < file_name.length) { // "Less than" due to null byte.
            file_name = Arrays.copyOf(filename.toCharArray(), file_name.length);
        } else {
            filename = filename.substring(0, file_name.length - 1);
            file_name = Arrays.copyOf(filename.toCharArray(), file_name.length);
        }
    }

    /**
     * Name of file when receiving an OTA update
     *
     * @return
     */
    public byte[] getFotaNameBytes() {
        return convertCharArrayToByteArray(fota_name);
    }

    /**
     * Name of file when receiving an OTA update
     *
     * @return
     */
    public String getFotaName() {
        String fotaname = "";
        for (int i = 0; i < fota_name.length; i++) {
            if (fota_name[i] != 0) {
                fotaname = fotaname + fota_name[i];
            }
        }
        return fotaname;
    }

    /**
     * Name of file when receiving an OTA update
     *
     * @param fotaName
     */
    public void setFotaName(byte[] fotaName) {
        fota_name = convertByteArrayToCharArray(fotaName);
    }

    /**
     * Name of file when receiving an OTA update
     *
     * @param fotaName
     */
    public void setFotaName(String fotaName) {
        if (fotaName.length() < file_name.length) { // "Less than" due to null byte.
            fota_name = Arrays.copyOf(fotaName.toCharArray(), fota_name.length);
        } else {
            fotaName = fotaName.substring(0, file_name.length - 1);
            fota_name = Arrays.copyOf(fotaName.toCharArray(), fota_name.length);
        }
    }

    @Override
    public String toString() {
        /*
         --- Entry #26 ---
         Binary Type: 0 (AP)
         Device Type: 2 (MMC)
         Identifier: 73
         Attributes: 5 (Read/Write)
         Update Attributes: 1 (FOTA)
         Partition Block Size/Offset: 30777311
         Partition Block Count: 33
         File Offset (Obsolete): 0
         File Size (Obsolete): 0
         Partition Name: SGPT
         Flash Filename: sgpt.img
         FOTA Filename: 
         */
        StringBuilder sb = new StringBuilder();
        String n = System.getProperty("line.separator");

        sb.append("Binary Type: ").append(this.bin_type).append(n);
        sb.append("Device Type: ").append(this.dev_type).append(n);
        sb.append("Identifier: ").append(this.part_id).append(n);
        sb.append("Filesystem Type: ").append(this.fileSystem).append(n);
        sb.append("Attributes: ").append(this.attributes).append(n);
        sb.append("Partition Block Size/Offset: ").append(this.file_offset).append(n);
        sb.append("Partition Block Count: ").append(this.block_count).append(n);
        sb.append("File Offset (Obsolete):").append(this.file_offset).append(n);
        sb.append("File Size (Obsolete): ").append(this.file_size).append(n);
        sb.append("Partition Name: ").append(this.getPartitionName()).append(n);
        sb.append("Flash Filename: ").append(this.getFilename()).append(n);
        sb.append("FOTA Filename: ").append(this.getFotaName()).append(n);
        sb.append(n).append(n);
        return sb.toString();
    }

    /**
     * converts a byte array to an equivalent char array
     *
     * @param byteArray
     * @return
     */
    public char[] convertByteArrayToCharArray(byte[] byteArray) {
        char[] retval = new char[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            retval[i] = (char) byteArray[i];
        }
        return retval;
    }

    /**
     * converts a char array to an equivalent byte array
     *
     * @param charArray
     * @return
     */
    public byte[] convertCharArrayToByteArray(char[] charArray) {
        byte[] retval = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            retval[i] = (byte) charArray[i];
        }
        return retval;
    }

}
