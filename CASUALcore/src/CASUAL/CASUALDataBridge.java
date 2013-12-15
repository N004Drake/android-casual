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

import CASUAL.CommunicationsTools.ADB.ADBTools;
import CASUAL.misc.StringOperations;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 * CASUALDataBridge is a unique way to flash information to a device. as opposed
 * to other methods, which require writing a string or a file, CASUALDataBridge
 * also handles Block and Character devices. This allows "flashing" and
 * "pulling" of entire partitions on a device without first transferring a file
 * to the SDCard. The technique used is to deploy a server, and monitor its
 * operations. A second thread is started which sends or receives information to
 * or from the device via TCP over USB. The end result is a verifiable and
 * error-checked method of data transfer from a computer to the Android device
 * from any file/block/char device available.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALDataBridge {

    Shell shell = new Shell();
    Log log = new Log();
    ADBTools adb = new ADBTools();
    final static String port = "27825";
    final static String ip = "127.0.0.1";
    final static Integer WATCHDOGINTERVAL = 2000;
    private static boolean deviceReadyForReceive = false;
    static String deviceSideMessage = "";
    static boolean shutdownBecauseOfError = false;

    /**
     * used externally to command shutdown. If shutdown is commanded, all
     * operations must halt as soon as possible and return.
     */
    public static boolean commandedShutdown = false;
    static long bytes = 0;
    static long lastbytes = -1;
    static String status = "";
    final String USBDISCONNECTED = "USB Disconnected";
    final String DEVICEDISCONNECTED = "error: device not found";
    final String PERMISSIONERROR = "/system/bin/sh: can't open";

    CASUALDataBridge() {
    }

    /**
     * gets a file from the device.
     *
     * @param remoteFileName path to remote file
     * @param f local file to write
     * @return string path to local file
     * @throws IOException
     * @throws InterruptedException
     */
    public synchronized String getFile(String remoteFileName, File f) throws IOException, InterruptedException {
        status = "received ";

        FileOutputStream fos = new FileOutputStream(f);
        //begin write

        getStream(fos, remoteFileName);

        if (shutdownBecauseOfError) {
            return deviceSideMessage;
        }
        return f.getCanonicalPath();

    }

    /**
     * Sends a string to a block/char/file on device
     *
     * @param send string to send
     * @param remoteFileName remote block/char/file on deviec
     * @return number of bytes sent
     * @throws InterruptedException
     * @throws SocketException
     * @throws IOException
     */
    public synchronized long sendString(String send, String remoteFileName) throws InterruptedException, SocketException, IOException {
        //make a duplicate of the array, with the \n and 0x3 key to end the file transfer
        send = send + "\n" + 0x04;
        ByteArrayInputStream bis = new ByteArrayInputStream(send.getBytes());
        long retval = sendStream(bis, remoteFileName);

        return retval;
    }

    /**
     * Sends a file to the device.
     *
     * @param f local file to send
     * @param remoteFileName path to remote file on device
     * @return number of bytes sent
     * @throws FileNotFoundException
     * @throws Exception
     */
    public synchronized long sendFile(File f, String remoteFileName) throws FileNotFoundException, Exception {
        log.level2Information("sending " + f.getName() + " to device. size=" + f.length());
        FileInputStream fis = new FileInputStream(f);
        long retval = sendStream(fis, remoteFileName);
        try {
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!checkErrors()) {
            retval = 0;
        }
        if (f.length() != retval) {
            retval = 0;
        }
        return retval;

    }

    /**
     * Sends an inputstream to the device.
     *
     * @param input stream to be written to remote file
     * @param remoteFileName name of remote file to be written
     * @return number of bytes sent.
     * @throws InterruptedException
     * @throws SocketException
     * @throws IOException
     */
    public synchronized long sendStream(final InputStream input, final String remoteFileName) throws InterruptedException, SocketException, IOException {
        resetCASUALConnection();

        //start device-side receiver thread
        Thread t = new DeviceSide().startDeviceSideServer(remoteFileName, true);

        //Open the socket
        final Socket socket = setupPort();
        //grab the stream
        OutputStream os = socket.getOutputStream();
        //do the work
        copyStreamToDevice(input, os, true);
        //begin write
        shutdownCommunications(socket, t);
        if (shutdownBecauseOfError) {
            return 0;
        }
        return bytes;
    }

    private long getStream(OutputStream output, String remoteFileName) throws IOException, InterruptedException {
        resetCASUALConnection();

        //start device-side sender
        Thread t = new DeviceSide().startDeviceSideServer(remoteFileName, false);
        //open the socket
        final Socket socket = setupPort();

        //begin write;
        copyStreamFromDevice(socket, output);

        shutdownCommunications(socket, t);
        return bytes;
    }

    private long copyStreamToDevice(InputStream input, OutputStream output, boolean toDevice) {

        try {
            //make a buffer to work with and setup start time
            long startTime = System.currentTimeMillis();
            byte[] buf = new byte[16384];
            timeoutWatchdog.start();
            BufferedOutputStream bos = new BufferedOutputStream(output);
            BufferedInputStream bis = new BufferedInputStream(input);
            int buflen = buf.length - 1;
            //pump in 4096 byte chunks at a time. from input to output
            while (bis.available() >= buflen && !commandedShutdown) {
                bis.read(buf);
                bos.write(buf);
                bytes = bytes + buf.length;
            }

            //send final bits less than 4096
            if (bis.available() > 0) {
                buf = new byte[input.available()];
                bis.read(buf);
                bytes = bytes + buf.length;
                bos.write(buf);
            }
            timeoutWatchdog.stop();
            bos.flush();

            Statics.setStatus("Sent:" + bytes + "bytes");

            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000.000;
            double kb = bytes / duration / 1000;
            log.level2Information("Sent:" + bytes / 1000 + "kb in " + duration + "s at " + kb + " KB/s");

        } catch (IOException ex) {
            timeoutWatchdog.stop();
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }

        return bytes;
    }

    private long copyStreamFromDevice(final Socket socket, OutputStream output) {
        try {
            Statics.setStatus("Data transfer initiated, please wait");
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            long startTime = System.currentTimeMillis();
            byte[] buf;
            timeoutWatchdog.start();
            while (deviceReadyForReceive && !commandedShutdown) {

                while ((buf = new byte[bis.available()]).length > 0) {
                    bytes = bytes + buf.length;
                    bis.read(buf);
                    output.write(buf);

                }
            }
            timeoutWatchdog.stop();
            output.flush();
            Statics.setStatus("Sent:" + bytes + "bytes");
            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000.000;
            double kb = bytes / duration / 1000;
            String message;

            log.level2Information("Sent:" + bytes / 1000 + "kb in " + duration + "s at " + kb + " KB/s");
        } catch (IOException ex) {
            timeoutWatchdog.stop();
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }

        return bytes;
    }

    private void resetCASUALConnection() {
        deviceReadyForReceive = false;
        shutdownBecauseOfError = false;
        deviceSideMessage = "";
        bytes = 0;
        lastbytes = -1;
        log.level4Debug("closing existing stream");
        log.level4Debug("restarting server");
        new ADBTools().restartConnection();
        shell.silentShellCommand(new String[]{adb.getBinaryLocation(), "forward", "--remove-all"});
        try {
            Socket socket = new Socket(ip, Integer.parseInt(port));
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (ConnectException ex) {
        } catch (IOException ex) {
        }
        log.level4Debug("establishing ports");
        shell.silentShellCommand(new String[]{adb.getBinaryLocation(), "shell", "killall busybox"});
        shell.silentShellCommand(new String[]{adb.getBinaryLocation(), "forward", "tcp:" + port, "tcp:" + port});
        log.level3Verbose("ports established");
    }

    private void waitForReadySignal() {
        //open the port for write
        while (!deviceReadyForReceive && !commandedShutdown) {
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
        log.level4Debug("Flushing port");
        deviceReadyForReceive = false;
        socket.getOutputStream().flush();
        log.level4Debug("closing ports");
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();

        log.level4Debug("waiting for device-side server to shutdown");
        t.join();
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            //do nothing 
        }
    }
    /**
     * timeoutWatchdog checks every WATCHDOGINTERVAL millis to verify bytes have
     * increased. If bytes have not increased, it throws an error and shuts
     * things down. This is used to detect a broken connection.
     */
    public static Timer timeoutWatchdog = new Timer(WATCHDOGINTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (lastbytes == bytes) {
                try {
                    new Log().level0Error("Failed to send file.  Timeout. Bytes:" + bytes + " Message:" + deviceSideMessage);
                    deviceReadyForReceive = false;
                    shutdownBecauseOfError = true;
                    timeoutWatchdog.stop();
                    throw new TimeoutException();
                } catch (TimeoutException ex) {
                    Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                lastbytes = bytes;
                Statics.setStatus(status + " " + bytes);
            }

        }
    });

    private boolean checkErrors() {
        return !deviceSideMessage.equals(USBDISCONNECTED) && !deviceSideMessage.equals(DEVICEDISCONNECTED) && !deviceSideMessage.startsWith(PERMISSIONERROR);
    }

    String integralGetFile(String remoteFile, File f) {

        String retval = "";
        try {
            retval = getFile(remoteFile, f);
        } catch (IOException ex) {
            log.errorHandler(ex);
        } catch (InterruptedException ex) {
            log.errorHandler(ex);
        }
        while (shutdownBecauseOfError && !commandedShutdown) {
            deviceReadyForReceive = false;
            deviceSideMessage = "";
            shutdownBecauseOfError = false;
            bytes = 0;
            lastbytes = -1;
            status = "";
            new CASUALMessageObject("@interactionFailedToReceive").showInformationMessage();
            try {
                return getFile(remoteFile, f);
            } catch (IOException ex) {
                log.errorHandler(ex);
            } catch (InterruptedException ex) {
                log.errorHandler(ex);
            }

        }
        return retval;
    }

    /**
     * This is a placeholder class to represent the device-server portion of the
     * DataBridge.
     */
    class DeviceSide {

        private Thread startDeviceSideServer(String remoteFileName, boolean forWrite) {
            Thread t = new Thread(new DeviceSide().openLinkForReadOrWrite(remoteFileName, forWrite));
            t.setName("Device Write Server");
            t.start();
            waitForReadySignal();
            return t;
        }

        /**
         * This returns a runnable server object ready to deploy on any device.
         *
         * @param remoteFileName filename on the device
         * @param forWrite true if writing, false if reading
         * @return server object ready to be started
         */
        private Runnable openLinkForReadOrWrite(final String remoteFileName, final boolean forWrite) {
            return new Runnable() {
                @Override
                public void run() {
                    try {
                        String[] cmd;
                        //deploy and get busybox location
                        String busybox = BusyboxTools.getBusyboxLocation();
                        String donestring = "operation complete";
                        //the command executed on the device should end with a keyword.  in this case the keyword is "done" which shows us it has exited properly.
                        //this command is used if forWrite is true (flash)--  basically netcat>desired file
                        String sendcommand = busybox + " stty raw;" + busybox + " nc -l " + ip + ":" + port + ">'" + remoteFileName + "';echo " + donestring;
                        //this command is used if forWrite is false (pull)--  basically netcat<desired file with a sync at the end
                        String receiveCommand = busybox + " stty raw;" + busybox + " nc -l " + ip + ":" + port + " <'" + remoteFileName + "';sync;echo " + donestring;

                        //build the command to send or receive with root or without. 
                        if (forWrite) {
                            if (!CASUALTools.rootAccessCommand().equals("")) {
                                cmd = new String[]{adb.getBinaryLocation(), "shell", sendcommand};
                            } else {
                                cmd = new String[]{adb.getBinaryLocation(), "shell", CASUALTools.rootAccessCommand() + " \"" + sendcommand + ";\""};
                            }
                        } else {
                            if (CASUALTools.rootAccessCommand().equals("")) {
                                cmd = new String[]{adb.getBinaryLocation(), "shell", receiveCommand};
                            } else {
                                cmd = new String[]{adb.getBinaryLocation(), "shell", CASUALTools.rootAccessCommand() + " \'" + receiveCommand + "\'"};
                            }
                        }

                        //launch the process
                        ProcessBuilder p = new ProcessBuilder(cmd);
                        p.redirectErrorStream(true);
                        Process proc = p.start();

                        //read a byte from the inputstream from the process so it does not halt. 
                        InputStream is = proc.getInputStream();
                        is.read(new byte[]{});

                        //wait for the connection to be ready then send the device ready signal
                        is = waitForDeviceSideConnection(is);

                        //device is ready for transfer
                        Statics.setStatus("device ready");
                        deviceReadyForReceive = true;
                        proc.waitFor();

                        //transfer is complete because host closed connection and device-side process exited
                        Statics.setStatus("device-side server closed");
                        deviceSideMessage = StringOperations.convertStreamToString(is);

                        //check for errors.  if any errors were present they would have come before the donestring
                        if (!deviceSideMessage.startsWith(donestring)) {
                            if (deviceSideMessage.equals("")) {
                                deviceSideMessage = USBDISCONNECTED;
                            }
                            shutdownBecauseOfError = true;
                            deviceReadyForReceive = false;
                            log.level0Error("Failed to send file. Bytes:" + bytes + " Message:" + deviceSideMessage);
                            throw new RuntimeException("Server exited improperly- received");
                        } else {
                            log.level4Debug("device reported sucessful shutdown");
                        }

                        //signal that the device is done before this thread dies.
                        deviceReadyForReceive = false;

                    } catch (InterruptedException ex) {
                        Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                InputStream waitForDeviceSideConnection(InputStream is) throws IOException {
                    String[] cmd;
                    cmd = new String[]{adb.getBinaryLocation(), "shell", "/data/local/tmp/busybox netstat -tul"};
                    boolean ready = false;
                    String received = "";
                    Statics.setStatus("monitoring ports on device");
                    while (!ready && !commandedShutdown) {
                        //monitor server status and detect errors
                        while (is.available() > 0) {
                            received = received + (char) is.read();
                            log.level4Debug(received);
                            if (received.contains("read-only file system")
                                    || received.contains("cannot open")
                                    || received.contains("No such file or directory")
                                    || received.contains(DEVICEDISCONNECTED)
                                    || received.contains(USBDISCONNECTED)
                                    || received.contains(PERMISSIONERROR)
                                    || received.contains("error: more than one device and emulator")) {
                                shutdownServer(received);
                            }

                        }
                        String returnval = shell.silentShellCommand(cmd);
                        if (returnval.contains(":" + port)) {
                            ready = true;
                        }
                    }
                    return is;
                }

                private void shutdownServer(String message) {
                    shutdownBecauseOfError = true;
                    deviceSideMessage = message;
                    deviceReadyForReceive = true;
                    log.level0Error(message);
                }
            };
        }
    }
}
