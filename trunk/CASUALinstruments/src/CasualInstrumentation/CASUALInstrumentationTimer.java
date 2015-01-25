/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CasualInstrumentation;

import CASUAL.CASUALConnectionStatusMonitor;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

/**
 *
 * @author adamoutler
 */
public class CASUALInstrumentationTimer {

    Timer timer;
    Toolkit toolkit;

    public CASUALInstrumentationTimer() {

    }

    class StatusUpdate extends TimerTask {

        @Override
        public void run() {
            final String status = CASUALConnectionStatusMonitor.getStatus();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    CASUALInstrumentation.doc.monitorStatus.setText(status);
                }
            });
        }
    }

    public void start() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                toolkit = Toolkit.getDefaultToolkit();
                timer = new Timer();
                timer.schedule(new StatusUpdate(),
                        0, //initial delay
                        2 * 1000);  //subsequent rate
            }
        });
        t.start();
    }

}
