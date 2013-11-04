/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adamoutler
 */
public class CasualDevCounter {

    Thread t = new Thread();

    public void incrementCounter(final String name) {

        t = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                try {
                    url = new URL("http://counter.casual-dev.com/?" + name);
                    url.openStream();  // throws an IOException
                    url.getFile();

                } catch (MalformedURLException ex) {
                    Logger.getLogger(CasualDevCounter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CasualDevCounter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
    }
    public void waitFor(){
        try {
            t.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(CasualDevCounter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
