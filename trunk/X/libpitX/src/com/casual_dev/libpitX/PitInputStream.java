package com.casual_dev.libpitX;
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
import java.io.InputStream;

/**
 * PitInputStream provides tools used for writing a pit file Original Files may
 * be found here:
 * https://github.com/Benjamin-Dobell/libpit--Java-/tree/master/libpit/src/au/com/glassechidna/libpit
 * modified by:
 *
 * @author adam
 */
public class PitInputStream {

    private final InputStream inputStream;

    /**
     * Constructs a PitInputStream
     *
     * @see InputStream
     * @param inputStream PIT as InputStream
     */
    public PitInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * reads an int (four bytes) from the input stream
     *
     * @return integer value from four bytes
     * @see InputStream
     * @throws IOException
     */
    public int readInt() throws IOException {
        return (inputStream.read() | (inputStream.read() << 8) | (inputStream.read() << 16)
                | (inputStream.read() << 24));
    }

    /**
     * reads a short (two bytes) from the inputstream
     *
     * @return short value from two bytes
     * @see InputStream
     * @throws IOException
     */
    public short readShort() throws IOException {
        return ((short) (inputStream.read() | (inputStream.read() << 8)));
    }

    /**
     * reads parameterized bytes from the InputStream
     *
     * @param buffer byte buffer
     * @param offset bytes to discard
     * @param length number of bytes to read
     * @return value requested from stream, specified by buffer, offset and length
     * @see InputStream
     * @throws IOException
     */
    public int read(byte[] buffer, int offset, int length) throws IOException {
        return (inputStream.read(buffer, offset, length));
    }

    /**
     * reads a byte from the InputStream
     *
     * @see InputStream
     * @return one byte
     */
    public int read() {
        try {
            return inputStream.read();
        } catch (IOException ex) {
            return -1; //if this happens the whole thing blew up or we are at the end
        }
    }
}
