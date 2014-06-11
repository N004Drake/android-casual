/*CasualDevCounter provides integration with CASUAL-Dev's counter system.
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
 *  along with this program.  If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package CASUAL.network.CASUALDevIntegration;

import CASUAL.misc.MandatoryThread;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * provides integration with CASUAL-Dev's counter system.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CasualDevCounter {

    /**
     * provides an object which can be polled to determine if the
     * incrementalCounter has finished
     */
    public MandatoryThread t = new MandatoryThread();

    public static void doIncrementCounter( String name ){
        new CasualDevCounter().incrementCounter(name);
    }
    /**
     * Increments a counter at CASUAL-Dev.com
     *
     * @param name the counter reference to increment
     */
    public void incrementCounter(final String name) {
        t = new MandatoryThread(new Runnable() {
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
        t.setName("counter.casual-dev.com");
        t.start();
    }

    /**
     * waits for the thread to complete. Used for testing purposes for
     * rapid-fire.
     */
    public void waitFor() {
        t.waitFor();
    }
}
