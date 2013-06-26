package CASUAL;

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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    final String[] valuesWeDontWantToSee;
    boolean[] badChecks;
    final String[] valuesWeWantToSee;
    private boolean[] goodChecks;
    /* constructor sets up logging and parameters */
    public CASUALTest(){        
        valuesWeWantToSee=new String[]{""};
        goodChecks=new boolean[valuesWeWantToSee.length];
        Arrays.fill(goodChecks,Boolean.FALSE);
        valuesWeDontWantToSee=new String[]{};
        badChecks=new boolean[valuesWeDontWantToSee.length];
        Arrays.fill(badChecks,Boolean.FALSE);
        try {
            fromAppPipedInputStream = new PipedInputStream(BUFFER);
            fromAppPipedOutputStream = new PipedOutputStream(fromAppPipedInputStream);
            readFromCASUAL = new BufferedReader(new InputStreamReader(fromAppPipedInputStream));
            CASUAL.Log.out = new PrintStream(fromAppPipedOutputStream);

            toAppPipedInputStream = new PipedInputStream(BUFFER);
            writeToCASUAL = new PipedOutputStream(toAppPipedInputStream);
            CASUAL.CASUALInteraction.in = new BufferedReader(new InputStreamReader(toAppPipedInputStream));


        } catch (IOException ex) {
            new CASUAL.Log().errorHandler(ex);
        }


    }

    
    /**
     * Launches CASUAL and monitors output
     * @param CASUALLaunchCommand list of parameters to run
     * @param valuesToCheckDuringRun desirable values from CASUAL
     * @param valuesWeDontWantToSee  undesirable values reported from CASUAL
     */
    public CASUALTest( String[] CASUALLaunchCommand,String[] valuesToCheckDuringRun, String[] valuesWeDontWantToSee) {
        valuesWeWantToSee=valuesToCheckDuringRun;
        goodChecks=new boolean[valuesWeWantToSee.length];
        Arrays.fill(goodChecks,Boolean.FALSE);
        this.valuesWeDontWantToSee=valuesWeDontWantToSee;
        badChecks=new boolean[valuesWeDontWantToSee.length];
        Arrays.fill(badChecks,Boolean.FALSE);
        try {
            fromAppPipedInputStream = new PipedInputStream(BUFFER);
            fromAppPipedOutputStream = new PipedOutputStream(fromAppPipedInputStream);
            readFromCASUAL = new BufferedReader(new InputStreamReader(fromAppPipedInputStream));
            CASUAL.Log.out = new PrintStream(fromAppPipedOutputStream);

            toAppPipedInputStream = new PipedInputStream(BUFFER);
            writeToCASUAL = new PipedOutputStream(toAppPipedInputStream);
            CASUAL.CASUALInteraction.in = new BufferedReader(new InputStreamReader(toAppPipedInputStream));
        

        } catch (IOException ex) {
            new CASUAL.Log().errorHandler(ex);
        }




        args = CASUALLaunchCommand;
    }
    StringBuilder sb = new StringBuilder();

    private void instantiateCASUAL() {
        //Runnable launchCASUAL=new CASUALTest();
        Thread launch = new Thread(launchCASUAL);
        launch.setName("CASUALMain");
        launch.start();
        Thread read = new Thread(readReactToCASUAL);
        read.setName("Reading and reacting to CASUAL");
        read.setDaemon(true);
        read.start();
        try {
            launch.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(CASUALTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Runnable readReactToCASUAL = new Runnable() {
        @Override
        public void run() {
            //output from CASUAL is logged to a file in the temp folder

            while (!shutdown) {
                try {
                    String line;
                    while ((line = readFromCASUAL.readLine()) != null) {
                        doCasualOuputHandling(line);
                        if (line.contains("[DEBUG]Shutting Down")) break;
                    }
                    if (line.contains("[DEBUG]Shutting Down")) break;
                    //nothing came out of CASUAL's Log, so lets sleep.
                    doQuarterSecondSleep();
                    
                } catch (Exception ex) {
                    //no need to report this. its fine
                }
            }


        }

        private void doCasualOuputHandling(String line) {
            try {
                System.out.println(line);
                validateLine(line);

                if (line.contains("ERROR")) {
                    System.out.println(line); //error
                } else if (line.contains("[INPUT][ANY]")) {
                    System.out.println(line);//get textual input  

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
                } else if (line.contains("[RETURN]")) {
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
                
                } else if (line.contains("[INTERACTION][CANCELOPTION][Q or RETURN]")) {
                    //general error 
                    System.out.println(line);
                    writeToCASUAL.write(13);
                } else if (line.contains("[INTERACTION][ACTIONREQUIRED][Q or RETURN]")) {
                    //general error 
                    System.out.println(line);
                    writeToCASUAL.write(13);
                }
            } catch (IOException ex) {
                new CASUAL.Log().errorHandler(ex);

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
            System.out.println("launching");
            CASUAL.CASUALApp.beginCASUAL(args);
            shutdown = true;
            System.out.println("shutting down");
            CASUAL.CASUALApp.shutdown(0);
            shutdown=false;
        }
    };
    
        private void setTestPoints(String[] readStrings){
        
    }
    private void validateLine(String line){
        for (int i=0; i<valuesWeWantToSee.length;i++){
            if (line.contains(valuesWeWantToSee[i])){
               goodChecks[i]=true;
            }
        }
        for (int i=0; i<valuesWeDontWantToSee.length;i++){
            if (! line.contains(valuesWeDontWantToSee[i])){
                badChecks[i]=true;
            }
        }
    }
    
    /**
     * Instantiates CASUAL and checks values
     * @return true if no desired values were seen and all desired values were seen
     */
    public boolean checkTestPoints(){
        instantiateCASUAL();
        if (goodChecks !=null && goodChecks.length>0){
            for (boolean check : goodChecks){
                if (!check) return false;
            }
        }
        if (badChecks !=null && badChecks.length>0){
            for (boolean check : badChecks){
                if (!check) return false;
            }
        }
        return true;
    }
    


    
    
}
