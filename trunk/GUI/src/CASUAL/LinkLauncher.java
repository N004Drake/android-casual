/*LinkLauncher launches URLs on various platforms. 
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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author adam
 */
public class LinkLauncher implements Runnable {

    static String Link;
    /*
     * launches a browser with a link
     */

    public void launchLink(String link) {
        Link = link;
        Runnable runnable = new LinkLauncher();
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    public void run() {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop;
            desktop = Desktop.getDesktop();
            URI uri;
            try {
                uri = new URI(Link);
                desktop.browse(uri);
            } catch (IOException ioe) {
                System.out.println("Attempted to open" + Link + " Failed with IO error");
            } catch (URISyntaxException use) {
                System.out.println("Attempted to open" + Link + " Failed with URI Syntax error");

            }
        } else {
            Shell Shell = new Shell();
            String Cmd[] = {"firefox", Link};
            String LaunchRes = Shell.sendShellCommand(Cmd);
            if (LaunchRes.contains("CritERROR!!!")) {
                String MCmd[] = {"open", Link};
                String MLaunchRes = Shell.sendShellCommand(MCmd);
                if (MLaunchRes.contains("CritERROR!!!")) {
                    String WCmd[] = {"explorer", Link};
                    String WLaunchRes = Shell.sendShellCommand(WCmd);
                    if (WLaunchRes.contains("CritERROR!!!")) {
                    }
                }
            }
        }
    }
}
