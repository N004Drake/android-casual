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

    public static final int DATA_SIZE = 132;
    public static final int PARTITION_NAME_MAX_LENGTH = 32;
    public static final int FILENAME_MAX_LENGTH = 32;
    public static final int FOTA_NAME_MAX_LENGTH = 32;

    public static final int PARTITION_TYPE_RFS = 0;
    public static final int PARTITION_TYPE_BLANK = 1;
    public static final int PARTITION_TYPE_EXT4 = 2;

    public static final int PARTITION_FLAG_WRITE = 1 << 1;

    private int bin_type;
    private int dev_type;
    private int part_id;
    private int attributes;
    private int fileSystem;
    private int block_start;
    private int block_count;
    private int file_offset;
    private int file_size;
    private char[] part_name = new char[32];
    private char[] file_name = new char[32];
    private char[] fota_name = new char[32];

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

    public int getBinType() {
        return bin_type;
    }

    public void setBinType(int unknown3) {
        this.bin_type = unknown3;
    }

    public int getDevType() {
        return dev_type;
    }

    public void setDevType(int bintype) {
        this.dev_type = bintype;
    }

    public int getPartID() {
        return (part_id);
    }

    public void setPartID(int partitionIdentifier) {
        this.part_id = partitionIdentifier;
    }

    public int getAttributes() {
        return (attributes);
    }

    public void setAttributes(int partitionFlags) {
        this.attributes = partitionFlags;
    }

    public int getFilesystem() {
        return fileSystem;
    }

    public void setFilesystem(int filesystem) {
        this.fileSystem = filesystem;
    }

    public int getBlockStart() {
        return (block_start);
    }

    public void setBlockStart(int blockStart) {
        this.block_start = blockStart;
    }

    public int getBlockCount() {
        return (block_count);
    }

    public void setBlockCount(int partitionBlockCount) {
        this.block_count = partitionBlockCount;
    }

    public int getFileOffset() {
        return (file_offset);
    }

    public void setFileOffset(int unknown1) {
        this.file_offset = unknown1;
    }

    public int getFileSize() {
        return (file_size);
    }

    public void getFileSize(int partitionBlockSize) {
        this.file_size = partitionBlockSize;
    }

    public String getPartitionName() {
        String partitionName = "";
        for (int i = 0; i < part_name.length; i++) {
            if (part_name[i] != 0) {
                partitionName = partitionName + part_name[i];
            }
        }
        return (partitionName);
    }

    public void setPartitionName(String partitionName) {
        if (partitionName.length() < part_name.length) { // "Less than" due to null byte.
            part_name = Arrays.copyOf(partitionName.toCharArray(), part_name.length);
        } else {
            partitionName = partitionName.substring(0, part_name.length - 1);
            part_name = Arrays.copyOf(partitionName.toCharArray(), part_name.length);
        }
    }

    public String getFilename() {
        String filename = "";
        for (int i = 0; i < file_name.length; i++) {
            if (file_name[i] != 0) {
                filename = filename + file_name[i];
            }
        }
        return (filename);
    }

    public void setFilename(String filename) {
        if (filename.length() < file_name.length) { // "Less than" due to null byte.
            file_name = Arrays.copyOf(filename.toCharArray(), file_name.length);
        } else {
            filename = filename.substring(0, file_name.length - 1);
            file_name = Arrays.copyOf(filename.toCharArray(), file_name.length);
        }
    }

    public String getFotaName() {
        String fotaname = "";
        for (int i = 0; i < fota_name.length; i++) {
            if (fota_name[i] != 0) {
                fotaname = fotaname + fota_name[i];
            }
        }
        return ("");
    }

    public void setFotaName(String fotaName) {
        if (fotaName.length() < file_name.length) { // "Less than" due to null byte.
            fota_name = Arrays.copyOf(fotaName.toCharArray(), fota_name.length);
        } else {
            fotaName = fotaName.substring(0, file_name.length - 1);
            fota_name = Arrays.copyOf(fotaName.toCharArray(), fota_name.length);
        }
    }

}
