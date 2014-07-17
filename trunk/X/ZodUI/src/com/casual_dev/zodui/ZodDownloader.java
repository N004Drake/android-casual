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
import CASUAL.misc.MandatoryThread;
import CASUAL.network.CASUALUpdates;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;

/**
 * Handles initial downloading of CASUAL Components
 * @author adamoutler
 */

public final class ZodDownloader {

    int expectedKB = 0;
    String title = "";
    String downloadedFile;
   CASUALZodMainUI ui;
    URL url;
    CASUALUpdates cu = new CASUALUpdates();
static MandatoryThread downloadThread;
    /**
     * instantiates a download session for ZodDownloader
     * @param url URL to download from
     * @param title  title of download for user to see
     */
    public ZodDownloader(URL url, String title) {
        this.url = url;
        this.title = title;
        this.expectedKB = cu.tryGetFileSize(url) / 1_024;
        processRemoteCASPAC();
    }

    public void downloadCaspac(CASUALZodMainUI ui){
        this.ui=ui;
        downloadThread.start();
    }
    
    /**
     * downloads a CASPAC and updates UI
     * @param ui  User Interface to be updated.
     */
    public void processRemoteCASPAC() {

        downloadThread=new MandatoryThread(() -> {
            boolean result = cu.downloadFileFromInternet(url, getTempFolder() + "caspac.caspac", title);
            if (!result) {
                return;
                
            } 
            CASUALZodMainUI.content.setSubtitle("CASPAC Download Complete " + expectedKB  + "kb");
            downloadedFile=getTempFolder() + "caspac.caspac";
            try {
                processDownloadedCASPAC();
            } catch (IOException ex) {
                getLogger(ZodDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
    }

    private void processDownloadedCASPAC() throws IOException {
        //TODO: Ugly, move out of downloader and into something else.
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
    }

    /**
     * waits for download. will lock if download is not called first.  Will never lock if download was called. 
     * @return string path to file
     * @throws InterruptedException if interrupted
     */
    public String waitForDownload() throws InterruptedException {
        if (! downloadThread.isComplete()){
            downloadThread.waitFor();
        }
        return downloadedFile;
    }

    public  boolean isDownloading(){
        return !downloadThread.isComplete();
    }
    
    /**
     * returns the expected KB that will be downloaded for this ZodDownloader
     * @return expected kb
     */
    public int getExpectedBytes() {
        return expectedKB;
    }

    /**
     *
     * @return
     */
    public  String getTitle() {
        return title;
    }
    

}
