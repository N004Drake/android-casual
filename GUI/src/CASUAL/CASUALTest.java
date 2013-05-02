/*CASUALTest provides an automation framework for CASUAL
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

/**
 *
 * @author adam
 */
public class CASUALTest {

    static String[] args;  //args to be passed into CASUAL
    public static boolean shutdown = false; //Shudown is commanded
    int BUFFER = 4096;
    PipedOutputStream writeToCASUAL;
    PipedInputStream toAppPipedInputStream;
    BufferedReader readFromCASUAL;
    PipedOutputStream fromAppPipedOutputStream;
    PipedInputStream fromAppPipedInputStream;

    /* constructor sets up logging and parameters */
    CASUALTest(final String[] CASUALLaunchCommand) {

        try {
            fromAppPipedInputStream = new PipedInputStream(BUFFER);
            fromAppPipedOutputStream = new PipedOutputStream(fromAppPipedInputStream);
            readFromCASUAL = new BufferedReader(new InputStreamReader(fromAppPipedInputStream));
            Log.out = new PrintStream(fromAppPipedOutputStream);

            toAppPipedInputStream = new PipedInputStream(BUFFER);
            writeToCASUAL = new PipedOutputStream(toAppPipedInputStream);
            CASUALInteraction.in = new BufferedReader(new InputStreamReader(toAppPipedInputStream));


        } catch (IOException ex) {
            new Log().errorHandler(ex);
        }




        args = CASUALLaunchCommand;
    }
    StringBuilder sb = new StringBuilder();

    public void instantiateCASUAL() {
        //Runnable launchCASUAL=new CASUALTest();
        Thread launch = new Thread(launchCASUAL);
        launch.start();
        Thread read = new Thread(readReactToCASUAL);
        read.start();
    }
    Runnable readReactToCASUAL = new Runnable() {
        @Override
        public void run() {
            //output from CASUAL is logged to a file in the temp folder

            while (!shutdown) {
                try {
                    String line;
                    while ((line = readFromCASUAL.readLine()) != null) {
                        doCasualOuputHandling(line);
                    }
                    //nothing came out of CASUAL's log, so lets sleep.
                    doQuarterSecondSleep();
                } catch (IOException ex) {
                    //no need to report this. its fine

                }
            }


        }

        private void doCasualOuputHandling(String line) {
            try {
                System.out.println(line);

                if (line.contains("ERROR")) {
                } else if (line.contains("[INPUT][ANY]")) {
                    System.out.println(line);//get textual input  

                    System.out.println(line);
                    writeToCASUAL.write(13);
                } else if (line.contains("[USERTASK][Q or RETURN][CRITICAL]")) {
                    System.out.println(line);
                    writeToCASUAL.write(13);


                    //perform an action now or stop
                } else if (line.contains("[CANCELOPTION][Q or RETURN]")) {
                    //are you sure you want to continue?
                    System.out.println(line);
                    writeToCASUAL.write(13);


                } else if (line.contains("[INTERACTION][NOTIFICATION][RETURN]")) {
                    //general notification
                    System.out.println(line);
                    writeToCASUAL.write(13);


                } else if (line.contains("[INFOMESSAGE][RETURN]")) {
                    //general information
                    System.out.println(line);
                    writeToCASUAL.write(13);


                } else if (line.contains("[ERRORMESSAGE][RETURN]")) {
                    //general error 
                    System.out.println(line);
                    writeToCASUAL.write(13);
                }
            } catch (IOException ex) {
                new Log().errorHandler(ex);

            }


        }

        private void doQuarterSecondSleep() {
            try {
                 Thread.sleep(250);
             } catch (InterruptedException ex) {
                 //don't care program will continue on
             }
        }
    };
    Runnable launchCASUAL = new Runnable() {
        @Override
        public void run() {

            CASUAL.CASUALApp.beginCASUAL(args);
            shutdown = true;
            CASUALApp.shutdown(BUFFER);
        }
    };
}
