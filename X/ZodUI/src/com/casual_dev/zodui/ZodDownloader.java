/*Handles initial downloading of CASUAL Components
 *Copyright (C) 2014 CASUAL-Dev or Adam Outler
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
 *  along with this program.  If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.casual_dev.zodui;

import CASUAL.Statics;
import static CASUAL.Statics.getTempFolder;
import CASUAL.caspac.Caspac;
import CASUAL.network.CASUALUpdates;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;

/**
 * Handles initial downloading of CASUAL Components
 * @author adamoutler
 */

public class ZodDownloader {

    static AtomicBoolean downloadingCASPAC = new AtomicBoolean(true);
    static int expectedKB = 0;
    static String title = "";
    URL url;
    CASUALUpdates cu = new CASUALUpdates();
    final static private Object downloadLock = new Object();

    /**
     *
     * @param url
     * @param title
     */
    public ZodDownloader(URL url, String title) {
        this.url = url;
        ZodDownloader.title = title;
        expectedKB = cu.tryGetFileSize(url) / 1_024;
    }

    /**
     *
     * @param ui  User Interface to be updated.
     */
    public void downloadCaspac(CASUALZodMainUI ui) {
        synchronized (downloadLock) {
            ZodDownloader.downloadingCASPAC.set(true);
        }

        new Thread(() -> {
            boolean result = cu.downloadFileFromInternet(url, getTempFolder() + "caspac.caspac", title);
            if (result) {
                CASUALZodMainUI.content.setSubtitle("CASPAC Download Complete " + expectedKB  + "kb");
            }
            ZodDownloader.downloadingCASPAC.set(false);
            try {
                Statics.GUI.sendProgress("Downloaded, examining CASPAC");
                CASUALZodMainUI.content.setMainTitle("Examining Contents");
                Statics.CASPAC = new Caspac(new File(getTempFolder() + "caspac.caspac"), getTempFolder(), 0);
                ui.createNewZod(CASUALZodMainUI.content);
                CASUALZodMainUI.content.setMainTitle("Loading Script");
                Statics.CASPAC.loadFirstScriptFromCASPAC();
                ui.createNewZod(CASUALZodMainUI.content);
                Statics.CASPAC.setActiveScript(Statics.CASPAC.getScriptByName(Statics.CASPAC.getScriptNames()[0]));
                Statics.GUI.setCASPAC(Statics.CASPAC);
                CASUALZodMainUI.content.setMainTitle("Ready - Click Start");
                ui.createNewZod(CASUALZodMainUI.content);
                Statics.GUI.sendProgress("ready");
            } catch (IOException ex) {
                getLogger(ZodDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }
            synchronized (downloadLock) {
                ZodDownloader.downloadLock.notify();
            }
        }).start();
    }

    /**
     *
     * @throws InterruptedException
     */
    public static void waitForDownload() throws InterruptedException {
        synchronized (downloadLock) {
            if (downloadingCASPAC.get()) {
                downloadLock.wait();
            }
        }
    }

    /**
     *
     * @return
     */
    public static int getExpectedBytes() {
        return expectedKB;
    }

    /**
     *
     * @return
     */
    public static String getTitle() {
        return title;
    }
    private static final Logger LOG = Logger.getLogger(ZodDownloader.class.getName());
}
