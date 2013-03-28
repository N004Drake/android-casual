/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
