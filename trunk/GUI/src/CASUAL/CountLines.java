/*CountLines counts lines
 *Copyright (C) 2013  Adam Outler
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package CASUAL;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 *
 * @author adam
 */
public class CountLines {

    public int countFileLines(String Filename) {
        InputStream IS = null;
        int Lines = 0;
        try {
            IS = new BufferedInputStream(new FileInputStream(Filename));

            Lines = countISLines(IS);


        } catch (FileNotFoundException ex) {
            new Log().errorHandler(ex);
        } catch (IOException ex) {
            new Log().errorHandler(ex);
        } finally {
            try {
                IS.close();
            } catch (IOException ex) {
                new Log().errorHandler(ex);
            }

        }
        return Lines;

    }

    public int countResourceLines(String ResourceName) {
        InputStream IS = getClass().getResourceAsStream(Statics.ScriptLocation + ResourceName + ".scr");
        int Lines = 0;
        try {
            Lines = countISLines(IS);
        } catch (IOException ex) {
            new Log().errorHandler(ex);
        } finally {
            try {
                IS.close();
            } catch (IOException ex) {
                new Log().errorHandler(ex);
            }
        }
        return Lines;
    }

    private int countISLines(InputStream IS) throws IOException {
        int count = 0;
        try {
            byte[] c = new byte[1024];
            int ReadChars;
            while ((ReadChars = IS.read(c)) != -1) {
                for (int i = 0; i < ReadChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
        } finally {
            IS.close();
        }

        return count + 1;

    }
}
