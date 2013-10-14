/*CASUALDataBridge provides a method of direct transfer to/from block/character/file objects on the device. 
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public class CASUALDataBridge {

    Shell shell = new Shell();
    //port 27825
    //BusyboxTools.deploy
    //adb forward tcp:27825 tcp:27825
    // to send data
    //adb shell "busyboxLocation nc -l 127.0.0.1:27825"
    //adb shell "/data/local/tmp/busybox nc -l 127.0.0.1:27825
    //    pipe data into| nc 127.0.0.1 27825
    //String[] args=new String[]{"-p","27827","127.0.0.1"};
    //NetCat.main(args)
    //adb shell "/data/local/tmp/busybox nc -l 127.0.0.1:27827>file out"
    //to retrieve data
    // adb shell "stty raw; dd if=/sdcard/Flash\ twrp\ recovery.zip">./woot
    //processor watch for nc: Address already in use on STDERR and use different port
    static String port = "27825";
    static String ip = "127.0.0.1";

    private void resetCASUALConnection() {

        Log log = new Log();
        Socket socket = new Socket();
        log.level4Debug("closing existing stream");
        try {
            socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 3000);
            socket.getOutputStream().write(0xFFFF);
            socket.getOutputStream().flush();
            socket.close();

        } catch (ConnectException ex) {
        } catch (IOException ex) {
        }
        shell.silentShellCommand(new String[]{ADBTools.getADBCommand(), "forward", "--remove-all"});
        log.level4Debug("restarting server");
        shell.silentShellCommand(new String[]{ADBTools.getADBCommand(), "stop-server"});
        shell.timeoutShellCommand(new String[]{ADBTools.getADBCommand(), "start-server"}, 4000);
        
        log.level4Debug("establishing streams");
        shell.silentShellCommand(new String[]{ADBTools.getADBCommand(), "forward", "tcp:" + port, "tcp:" + port});
        log.level3Verbose("streams established");
    }

    public void getFile(File f, String remoteFileName) throws IOException {
        try {
            resetCASUALConnection();
            //setup receiving side
            Thread t = startListener(remoteFileName, false);
            waitForReadySignal();

            Socket socket = setupPort();
            //grab the stream

            InputStream is = socket.getInputStream();
            FileOutputStream fos = new FileOutputStream(f);
            //begin write
            while (is.available() < 1) {
                sleep(10);
            }
            copyStreamFromDevice(is, fos);
            fos.flush();

            shutdownCommunications(socket, t);
        } catch (InterruptedException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    public int sendString(String send, String remoteFileName) throws InterruptedException, SocketException, IOException {
        //make a duplicate of the array, with the \n and 0x3 key to end the file transfer
        send = send + "\n"+0x04;

        

        ByteArrayInputStream bis = new ByteArrayInputStream(send.getBytes());

        int bytesSent = sendStream(bis, remoteFileName);
        resetCASUALConnection();
        return bytesSent;
    }

    public int sendFile(File f, String remoteFileName) throws FileNotFoundException, Exception {
        FileInputStream fis = new FileInputStream(f);
        int bytesSent = sendStream(fis, remoteFileName);
        try {
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bytesSent;

    }

    public int sendStream(final InputStream input, final String remoteFileName) throws InterruptedException, SocketException, IOException {
        resetCASUALConnection();
        Thread t = startListener(remoteFileName, true);
        waitForReadySignal();
        final Socket socket = setupPort();
        //grab the stream
        OutputStream os = socket.getOutputStream();
        //begin write
        int bytes = copyStreamToDevice(input, os);
        shutdownCommunications(socket, t);
        return bytes;
    }

    private int copyStreamFromDevice(InputStream input, OutputStream output) {
        int sent = 0;
        try {
            byte[] buf;
            while (deviceReady) {
                while ((buf = new byte[input.available()]).length > 0) {
                    input.read(buf);
                    output.write(buf);
                    sent = sent + buf.length;
                    Statics.setStatus("bytes: " + sent);
                }
            }
            output.flush();
            new Log().level3Verbose("sent " + sent + " bytes");
        } catch (IOException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sent;
    }

    private int copyStreamToDevice(InputStream input, OutputStream output) {
        int sent = 0;
        try {
            long startTime = System.currentTimeMillis() / 1000;
            byte[] buf;
            while ((buf = new byte[input.available()]).length > 0) {
                input.read(buf);
                output.write(buf);
                sent = sent + buf.length;
                Statics.setStatus("bytes: " + sent);
            }
            output.flush();
            long endTime = System.currentTimeMillis() / 1000;
            double duration = (endTime - startTime);
            new Log().level3Verbose("sent " + sent + " bytes in " + duration + " seconds");
        } catch (IOException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sent;
    }
    private static boolean deviceReady = false;

    private Runnable openDeviceSideLinkForSend(final String remoteFileName, final boolean forWrite) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    String[] cmd;
                    if (forWrite) {
                        if (CASUALTools.rootAccessCommand().equals("")){
                            cmd = new String[]{ADBTools.getADBCommand(), "shell", BusyboxTools.getBusyboxLocation() + " stty raw;" + BusyboxTools.getBusyboxLocation() + " nc -l " + ip + ":" + port + ">\"" + remoteFileName + "\";sync;"};                            
                        } else {
                            cmd = new String[]{ADBTools.getADBCommand(), "shell", BusyboxTools.getBusyboxLocation() + " stty raw;" + CASUALTools.rootAccessCommand() +" \""+BusyboxTools.getBusyboxLocation() + " nc -l " + ip + ":" + port + ">'" + remoteFileName + "';sync;\""};
                        }
                    } else {
                        if (CASUALTools.rootAccessCommand().equals("")){
                            cmd = new String[]{ADBTools.getADBCommand(), "shell", BusyboxTools.getBusyboxLocation() + " stty raw;" + BusyboxTools.getBusyboxLocation() + " nc -l " + ip + ":" + port + " <\"" + remoteFileName + "\";sync;"};
                        } else {
                            cmd = new String[]{ADBTools.getADBCommand(), "shell", BusyboxTools.getBusyboxLocation() + " stty raw;"+ CASUALTools.rootAccessCommand()+" \"" + BusyboxTools.getBusyboxLocation() + " nc -l " + ip + ":" + port + " <'" + remoteFileName + "';sync;"+"\""};
                        }
                    }
                    ProcessBuilder p = new ProcessBuilder(cmd);
                    p.redirectError();
                    Process proc = p.start();


                    //wait for the connection to be ready then send the device ready signal
                    waitForDeviceSideConnection();
                    proc.getInputStream().read(new byte[]{});
                    deviceReady = true;
                    proc.waitFor();
                    deviceReady = false;
                    System.out.println("Receiving end done!");


                } catch (InterruptedException ex) {
                    Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            private void waitForDeviceSideConnection() {
                String[] cmd;
                cmd = new String[]{ADBTools.getADBCommand(), "shell", "/data/local/tmp/busybox netstat -tul"};
                boolean ready = false;
                while (!ready) {
                    String returnval = shell.silentShellCommand(cmd);
                    if (returnval.contains(":" + port)) {
                        ready = true;
                    }
                }
            }
        };
    }

    private Thread startListener(String remoteFileName, boolean forWrite) {
        //start the listener
        Thread t = new Thread(openDeviceSideLinkForSend(remoteFileName, forWrite));
        t.start();
        return t;
    }

    private void waitForReadySignal() {
        //open the port for write
        while (!deviceReady) {
            //wait for deviceReady signal
            sleep(100);
        }
    }

    private Socket setupPort() throws SocketException, IOException, NumberFormatException {
        int p = Integer.parseInt(port);
        final Socket socket = new Socket(ip, p);
        socket.setTrafficClass(0x04);
        return socket;
    }

    private void shutdownCommunications(Socket socket, Thread t) throws InterruptedException, IOException {
        socket.getOutputStream().flush();
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
        deviceReady = false;
        t.join();
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            //do nothing 
        }
    }
}
