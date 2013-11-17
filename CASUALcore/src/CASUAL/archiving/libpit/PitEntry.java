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



/** 
 * PitEntry provides a method of organizing PIT entries and storing data
 * Original Files may be found here: 
 * https://github.com/Benjamin-Dobell/libpit--Java-/tree/master/libpit/src/au/com/glassechidna/libpit
 * modified by:
 * @author adam
 */
public class PitEntry
{
        public static final int DATA_SIZE = 132;
        public static final int PARTITION_NAME_MAX_LENGTH = 32;
        public static final int FILENAME_MAX_LENGTH = 64;

        public static final int PARTITION_TYPE_RFS = 0;
        public static final int PARTITION_TYPE_BLANK = 1;
        public static final int PARTITION_TYPE_EXT4 = 2;

        public static final int PARTITION_FLAG_WRITE = 1 << 1;

        private boolean unused;

        private int partitionType;
        private int partitionIdentifier;
        private int partitionFlags;

        private int unknown1;

        private int partitionBlockSize;
        private int partitionBlockCount;

        private int unknown2;
        private int unknown3;

        private String partitionName;
        private String filename;

        public PitEntry()
        {
                unused = false;
                partitionType = 0;
                partitionIdentifier = 0;
                partitionFlags = 0;
                unknown1 = 0;
                partitionBlockSize = 0;
                partitionBlockCount = 0;
                unknown2 = 0;
                unknown3 = 0;
        }


        public boolean matches(PitEntry otherPitEntry)
        {
                if (unused == otherPitEntry.unused && partitionType == otherPitEntry.partitionType && partitionIdentifier == otherPitEntry.partitionIdentifier
                        && partitionFlags == otherPitEntry.partitionFlags && unknown1 == otherPitEntry.unknown1 && partitionBlockSize == otherPitEntry.partitionBlockSize
                        && partitionBlockCount == otherPitEntry.partitionBlockCount && unknown2 == otherPitEntry.unknown2 && unknown3 == otherPitEntry.unknown3
                        && partitionName.equals(otherPitEntry.partitionName) && filename.equals(otherPitEntry.filename))
                {
                        return (true);
                }
                else
                {
                        return (false);
                }
        }

        public boolean getUnused()
        {
                return unused;
        }

        public void setUnused(boolean unused)
        {
                this.unused = unused;
        }

        public int getPartitionType()
        {
                return (partitionType);
        }

        public void setPartitionType(int partitionType)
        {
                this.partitionType = partitionType;
        }

        public int getPartitionIdentifier()
        {
                return (partitionIdentifier);
        }

        public void setPartitionIdentifier(int partitionIdentifier)
        {
                this.partitionIdentifier = partitionIdentifier;
        }

        public int getPartitionFlags()
        {
                return (partitionFlags);
        }

        public void setPartitionFlags(int partitionFlags)
        {
                this.partitionFlags = partitionFlags;
        }

        public int getUnknown1()
        {
                return (unknown1);
        }

        public void setUnknown1(int unknown1)
        {
                this.unknown1 = unknown1;
        }

        public int getPartitionBlockSize()
        {
                return (partitionBlockSize);
        }

        public void setPartitionBlockSize(int partitionBlockSize)
        {
                this.partitionBlockSize = partitionBlockSize;
        }

        public int getPartitionBlockCount()
        {
                return (partitionBlockCount);
        }

        public void setPartitionBlockCount(int partitionBlockCount)
        {
                this.partitionBlockCount = partitionBlockCount;
        }

        public int getUnknown2()
        {
                return unknown2;
        }

        public void setUnknown2(int unknown2)
        {
                this.unknown2 = unknown2;
        }

        public int getUnknown3()
        {
                return unknown3;
        }

        public void setUnknown3(int unknown3)
        {
                this.unknown3 = unknown3;
        }

        public String getPartitionName()
        {
                return (partitionName);
        }

        public void setPartitionName(String partitionName)
        {
                if (partitionName.length() < PARTITION_NAME_MAX_LENGTH) // "Less than" due to null byte.
                        this.partitionName = partitionName;
                else
                        this.partitionName = partitionName.substring(0, PARTITION_NAME_MAX_LENGTH - 1);
        }

        public String getFilename()
        {
                return (filename);
        }

        public void setFilename(String filename)
        {
                if (partitionName.length() < PARTITION_NAME_MAX_LENGTH) // "Less than" due to null byte.
                        this.filename = filename;
                else
                        this.filename = filename.substring(0, FILENAME_MAX_LENGTH - 1);
        }
}