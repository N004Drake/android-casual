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
package com.casual_dev.zodui.Downloader;

import CASUAL.CASUALSessionData;
import CASUAL.caspac.Caspac;
import CASUAL.misc.MandatoryThread;
import CASUAL.network.CASUALUpdates;
import com.casual_dev.zodui.CASUALZodMainUI;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
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
        ScriptMeta sm=new ScriptMeta(url.toString());
        this.expectedKB = cu.tryGetFileSize(url) / 1_024;
        processRemoteCASPAC();
    }

    /**
     * downloads a CASPAC and updates UI
     * @param ui  User Interface to be updated.
     */
    public void downloadCaspac(CASUALZodMainUI ui){
        this.ui=ui;
        ScriptMeta sm=new ScriptMeta(url);
        sm.getPropsInBackground();
        downloadThread.start();
        Properties p=sm.getProperties();
        ui.updateFrontPageProperties(p);
        
        System.out.println(p);
    }

    /**
     * processes the caspac
     */
    public void processRemoteCASPAC() {

        downloadThread=new MandatoryThread(() -> {
            boolean result = cu.downloadFileFromInternet(url, CASUALSessionData.getInstance().getTempFolder() + "caspac.caspac", title);
            if (!result) {
                return;
                
            } 
            CASUALZodMainUI.content.setSubtitle("CASPAC Download Complete " + expectedKB  + "kb");
            downloadedFile=CASUALSessionData.getInstance().getTempFolder() + "caspac.caspac";
            try {
                processDownloadedCASPAC();
            } catch (IOException ex) {
                getLogger(ZodDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
    }

    private void processDownloadedCASPAC() throws IOException {
        //TODO: Ugly, move out of downloader and into something else.
        CASUALSessionData.getInstance().GUI.sendProgress("Downloaded, examining CASPAC");
        CASUALZodMainUI.content.setMainTitle("Examining Contents");
        CASUALSessionData.getInstance().CASPAC = new Caspac(new File(CASUALSessionData.getInstance().getTempFolder() + "caspac.caspac"), CASUALSessionData.getInstance().getTempFolder(), 0);
        ui.createNewZod(CASUALZodMainUI.content);
        CASUALZodMainUI.content.setMainTitle("Loading Script");
        CASUALSessionData.getInstance().CASPAC.loadFirstScriptFromCASPAC();
        ui.createNewZod(CASUALZodMainUI.content);
        CASUALSessionData.getInstance().CASPAC.setActiveScript(CASUALSessionData.getInstance().CASPAC.getScriptByName(CASUALSessionData.getInstance().CASPAC.getScriptNames()[0]));
        CASUALSessionData.getInstance().GUI.setCASPAC(CASUALSessionData.getInstance().CASPAC);
        CASUALZodMainUI.content.setMainTitle("Ready");
        ui.createNewZod(CASUALZodMainUI.content);
        CASUALSessionData.getInstance().GUI.sendProgress("ready");
        CASUALZodMainUI.CASUALready.set(true);
        ui.setReady(true);
        ui.sendString(CASUALSessionData.getInstance().CASPAC.getActiveScript().getName());
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

    public static boolean isDownloading(){
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
