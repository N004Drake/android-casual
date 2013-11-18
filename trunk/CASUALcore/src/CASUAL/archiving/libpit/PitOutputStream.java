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

import java.io.IOException;
import java.io.OutputStream;

/**
 * PitOutputStream provides a set of tools designed to assist with reading PIT
 * files
 *
 * Original Files may be found here:
 * https://github.com/Benjamin-Dobell/libpit--Java-/tree/master/libpit/src/au/com/glassechidna/libpit
 * modified by:
 *
 * @author adam
 */
public class PitOutputStream {

    private final OutputStream outputStream;
    private final byte[] writeBuffer = new byte[4];

    public PitOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeInt(int value) throws IOException {
        writeBuffer[0] = (byte) (value & 0xFF);
        writeBuffer[1] = (byte) ((value >> 8) & 0xFF);
        writeBuffer[2] = (byte) ((value >> 16) & 0xFF);
        writeBuffer[3] = (byte) (value >> 24);

        outputStream.write(writeBuffer);
    }

    public void writeShort(short value) throws IOException {
        writeBuffer[0] = (byte) (value & 0xFF);
        writeBuffer[1] = (byte) (value >> 8);

        outputStream.write(writeBuffer, 0, 2);
    }

    public void write(byte[] buffer, int offset, int length) throws IOException {
        outputStream.write(buffer, offset, length);
    }
}
