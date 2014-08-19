/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casual_dev.zodui.Downloader;

import CASUAL.Log;
import CASUAL.misc.MandatoryThread;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 *
 * @author adamoutler
 */
public class ScriptMeta {

    final String noExtensionURL;
    Properties properties=new Properties();
    MandatoryThread t =new MandatoryThread(() -> {
            populateProperties();
        });;
    //https://builds.casual-dev.com/files/all/testpak.zip

    ScriptMeta(URL caspacLocationOnWeb) {
        noExtensionURL = getUrlWithoutExtension(caspacLocationOnWeb.toString());
    }

    ScriptMeta(String caspacLocationOnWeb) {
        //https://builds.casual-dev.com/files/all/testpak.zip
        System.out.println(caspacLocationOnWeb);
        noExtensionURL = getUrlWithoutExtension(caspacLocationOnWeb);
    }
    private String getUrlWithoutExtension(String url) {
        //https://builds.casual-dev.com/files/all/testpak
        url = url.substring(0, url.lastIndexOf("."));
        Log.level4Debug("CASPAC meta location:" + url);
        return url;
    }

    public void getPropsInBackground() {
        if (!t.isAlive() && !t.isComplete()){
            t.setName("Get Properties");
            t.start();
        }
    }

    public Properties getProperties() {
        if (!t.isComplete()) {
            if (!t.isAlive()) {
                getPropsInBackground();
            }
            t.waitFor();
        }
        return properties;
    }

    private Properties populateProperties() {
        try {
            getDataFromWeb(properties, "meta");
        } catch (IOException ex) {
            Log.level4Debug("Warning:Couldn't get " + properties + "meta");
        }
        try {
            getDataFromWeb(properties, "properties");
        } catch (IOException ex) {
            Log.level4Debug("Warning:Couldn't get " + properties + "properties");
        }
        return properties;
    }

    private Properties getDataFromWeb(Properties props, String type) throws MalformedURLException, IOException {
        InputStream input = new URL(noExtensionURL + "." + type).openStream();
        props.load(input);

        return props;
    }

}
