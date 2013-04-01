/*MD5sumRunnable provides a way to do an MD5Sum as a separate process
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

/**
 *
 * @author adam
 */
public class MD5sumRunnable implements Runnable {

    private final String fileToMD5;
    private final InputStream sumstream;
    private final String filename;

    MD5sumRunnable(final InputStream sumstream, String filename) {
        this.sumstream = sumstream;
        this.fileToMD5 = "";
        this.filename = filename;
        getMD5();

    }

    MD5sumRunnable(final String fileToMD5, String filename) {
        this.fileToMD5 = fileToMD5;
        this.sumstream = null;
        this.filename = filename;
        getMD5();
    }

    private void getMD5() {
        MD5sum md5 = new MD5sum();
        if (fileToMD5.equals("")) {
            Statics.runnableMD5list.add(md5.makeMD5String(md5.md5sum(sumstream), filename));
        } else {
            Statics.runnableMD5list.add(md5.makeMD5String(md5.md5sum(fileToMD5), filename));
        }
    }

    @Override
    public void run() {
    }
}
