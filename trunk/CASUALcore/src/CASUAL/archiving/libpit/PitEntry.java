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
    private int device_type;
    private int part_id;
    private int part_type;
    private int filesystem;
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

        device_type = 0;
        block_start = 0;
        part_id = 0;
        part_type = 0;
        file_offset = 0;
        file_size = 0;
        block_count = 0;
        filesystem = 0;
        bin_type = 0;
    }

    /**
     * matches this entries parameters against another to detect equivalence.
     *
     * @param otherPitEntry entry to match against
     * @return true if match
     */
    public boolean matches(PitEntry otherPitEntry) {
        return this.toString().equals(otherPitEntry.toString());
    }

    /**
     * binary type
     *
     * @return type of binary
     */
    public int getBinType() {
        return bin_type;
    }

    /**
     * binary type
     *
     * @param binType unsigned integer
     */
    public void setBinType(int binType) {
        this.bin_type = binType;
    }

    /**
     * Device Type differs per-device. generally 0=emmc.
     *
     * @return device type
     */
    public int getDevType() {
        return device_type;
    }

    /**
     * Device Type differs per-device. generally 0=emmc.
     *
     * @param devType unsigned integer
     */
    public void setDeviceType(int devType) {
        this.device_type = devType;
    }

    /**
     * Partition ID is a number which identifies the partition
     *
     * @return partition identifier
     */
    public int getPartID() {
        return (part_id);
    }

    /**
     * Partition ID is a number which identifies the partition
     *
     * @param partitionIdentifier unsigned integer
     */
    public void setPartID(int partitionIdentifier) {
        this.part_id = partitionIdentifier;
    }

    /**
     * Partition Attributes
     *
     * @return attributes field in PIT
     */
    public int getAttributes() {
        return (part_type);
    }

    /**
     * Partition Attributes
     *
     * @param partitionFlags unsigned integer
     */
    public void setPartitionType(int partitionFlags) {
        this.part_type = partitionFlags;
    }

    /**
     * rfs=0 raw=1 ext4=2
     *
     * @return filesystem type
     */
    public int getFilesystem() {
        return filesystem;
    }

    /**
     * sets filesystem type rfs=0 raw=1 ext4=2
     *
     * @param filesystem unsigned integer
     */
    public void setFilesystem(int filesystem) {
        this.filesystem = filesystem;
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
     * @param blockStart unsigned integer
     */
    public void setBlockStart(int blockStart) {
        this.block_start = blockStart;
    }

    /**
     * number of 512b blocks in partition
     *
     * @return block count
     */
    public int getBlockCount() {
        return (block_count);
    }

    /**
     * number of 512b blocks in partition
     *
     * @param partitionBlockCount unsigned integer
     */
    public void setBlockCount(int partitionBlockCount) {
        this.block_count = partitionBlockCount;
    }

    /**
     * number of blocks to offset in partition before beginning write
     *
     * @return block offset
     */
    public int getFileOffset() {
        return (file_offset);
    }

    /**
     * number of blocks to offset in partition before beginning write
     *
     * @param fileOffset unsigned integer
     */
    public void setFileOffset(int fileOffset) {
        this.file_offset = fileOffset;
    }

    /**
     * size of file in bytes
     *
     * @return partition size in bytes
     */
    public int getFileSize() {
        return (file_size);
    }

    /**
     * size of file in bytes
     *
     * @param partitionBlockSize unsigned integer
     */
    public void setFileSize(int partitionBlockSize) {
        this.file_size = partitionBlockSize;
    }

    /**
     * Proper name of partition used to reference flash location
     *
     * @return byte representation of partition name
     */
    public byte[] getPartitionNameBytes() {
        return convertCharArrayToByteArray(part_name);
    }

    /**
     * Proper name of partition used to reference flash location
     *
     * @return partition name
     */
    public String getPartitionName() {
        String partitionName = "";
        return new String(part_name).trim();
    }

    /**
     * Proper name of partition used to reference flash location
     *
     * @param partitionName unsigned integer
     */
    public void setPartitionName(byte[] partitionName) {
        part_name = convertByteArrayToCharArray(partitionName);
    }

    /**
     * Proper name of partition used to reference flash location
     *
     * @param partitionName unsigned integer
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
     * @return byte representation of filename
     */
    public byte[] getFileNameBytes() {
        return convertCharArrayToByteArray(file_name);
    }

    /**
     * Name of file when transferred from device
     *
     * @return file name
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
     * @return byte representation of FOTA name
     */
    public byte[] getFotaNameBytes() {
        return convertCharArrayToByteArray(fota_name);
    }

    /**
     * Name of file when receiving an OTA update
     *
     * @return FOTA name
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

    public String getPartitionTypeFriendlyName() {
        switch (this.part_type) {
            case 1:
                return "Bct";
            case 2:
                return "Bootloader";
            case 4:
                return "Data";
            case 5:
                return "Data";
            case 6:
                return "MBR";
            case 7:
                return "EBR";
            case 8:
                return "GP1";
            case 9:
                return "GPT";
            default:
                return "unknown";
        }
    }

    public String getFilesystemTypeFriendlyName() {
        switch (this.filesystem) {
            case 0:
                return "raw";
            case 1:
                return "Basic";
            case 2:
                return "Enhanced";
            case 3:
                return "EXT2";
            case 4:
                return "YAFFS2";
            case 5:
                return "EXT4";
            default:
                return "unknown";
        }
    }

    public String getHardwareTypeFriendlyName() {
        switch (this.device_type) {
            case 1:
                return "NAND";
            case 2:
                return "EMMC";
            case 3:
                return "SPI";
            case 4:
                return "IDE";
            case 5:
                return "NAND_X16";
            default:
                return "unknwon";

        }
    }

    //http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
    /**
     * convert block count into human-readable form.
     *
     * @param si use SI units (KB=1000B) or binary (KiB=1024B)
     * @return human readable bytes from block count
     */
    public String getBlockCountFriendly(boolean si) {
        long bytes = (long) block_count * 512;
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);

    }

    @Override
    public String toString() {
        /*  original entry from Heimdall
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
        sb.append("Partition ID: ").append(this.part_id).append(n);
        sb.append("Partition Name: ").append(this.getPartitionName()).append(n);
        sb.append("Flash Filename: ").append(this.getFilename()).append(n);
        sb.append("Block Count: ").append(this.block_count).append(" (").append(getBlockCountFriendly(true)).append(")").append(n);
        sb.append("Block Start: ").append(this.block_start).append(n);
        sb.append("Last Block: ").append(getPartitionEndBlock()).append(n);
        sb.append("Filesystem Type: ").append(this.filesystem).append(" (").append(this.getFilesystemTypeFriendlyName()).append(")").append(n);
        sb.append("Partition Type: ").append(this.part_type).append(" (").append(this.getPartitionTypeFriendlyName()).append(")").append(n);
        sb.append("Device Type: ").append(this.device_type).append(" (").append(this.getHardwareTypeFriendlyName()).append(")").append(n);
        sb.append("Binary Type: ").append(this.bin_type).append(n);
        sb.append("FOTA Filename: ").append(this.getFotaName()).append(n);
        sb.append(n).append(n);
        return sb.toString();
    }

    /**
     * calculated value for partition start + partition size -1 to account for
     * first block's usage.
     *
     * @return last block used by partition
     */
    private int getPartitionEndBlock() {
        return this.block_start + this.block_count - 1;
    }

    /**
     * converts a byte array to an equivalent char array
     *
     * @param byteArray
     * @return byte representation of char array
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
     * @return byte representation of char array
     */
    public byte[] convertCharArrayToByteArray(char[] charArray) {
        byte[] retval = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            retval[i] = (byte) charArray[i];
        }
        return retval;
    }

}
