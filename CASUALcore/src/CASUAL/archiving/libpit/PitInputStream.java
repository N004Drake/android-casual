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
import java.io.InputStream;

public class PitInputStream
{
        private InputStream inputStream;
        
        public PitInputStream(InputStream inputStream)
        {
                this.inputStream = inputStream;
        }

        public int readInt() throws IOException
        {                
                return (inputStream.read() | (inputStream.read() << 8) | (inputStream.read() << 16)
                        | (inputStream.read() << 24));
        }

        public short readShort() throws IOException
        {
                return ((short)(inputStream.read() | (inputStream.read() << 8)));
        }
        
        public int read(byte[] buffer, int offset, int length) throws IOException
        {
                return (inputStream.read(buffer, offset, length));
        }
}