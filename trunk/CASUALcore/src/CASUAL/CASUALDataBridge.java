/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.IOUtils;

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

    public void resetForwards() {
        try {
            Log log = new Log();
            log.level3Verbose("removing streams");
            log.level3Verbose(shell.timeoutShellCommand(new String[]{ADBTools.getADBCommand(), "forward", "--remove-all"}, 4000));
            Thread.sleep(500);
            log.level3Verbose("establishing streams");
            log.level3Verbose(shell.timeoutShellCommand(new String[]{ADBTools.getADBCommand(), "forward", "tcp:" + port, "tcp:" + port}, 4000));
            Thread.sleep(500);
            log.level3Verbose("streams established");
        } catch (InterruptedException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendString(String send, String remoteFileName) {
        //make a duplicate of the array, with the \n and 0x3 key to end the file transfer
        send = send + "\n";
        byte[] tempArray = new byte[send.length() + 2];
        System.arraycopy(send.getBytes(), 0, tempArray, 0, send.length());
        tempArray[tempArray.length - 2] = 0x04;
        tempArray[tempArray.length - 1] =  "\n".getBytes()[0];
        
        //"\u001a\r\n"
        ByteArrayInputStream bis = new ByteArrayInputStream(tempArray);

        sendStream(bis, remoteFileName);
resetForwards();
    }

    public void sendFile(File f, String remoteFileName) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(f);
        byte[] endbytes = new byte[2];
        endbytes[1] = (byte) ("\n".toCharArray()[0]);
        endbytes[0] = 0x04;
        ByteArrayInputStream bis = new ByteArrayInputStream(endbytes);
        SequenceInputStream sis = new SequenceInputStream(fis, bis);

        sendStream(fis, remoteFileName);
        resetForwards();

        try {
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendStream(final InputStream input, final String remoteFileName) {
        try {
            //reset the streams
            resetForwards();

            //open the port for write
            int p = Integer.parseInt(port);
            final Socket socket = new Socket("127.0.0.1", p);
            socket.setTcpNoDelay(true);
            socket.setTrafficClass(0x04);

            //start the listener
            Thread t = new Thread(openDeviceSideLinkForSend(remoteFileName));
            t.start();
            //wait for the response
            while(!deviceReady){
                //wait for deviceReady signal
                Thread.sleep(100);
            }

            //class for device communications
            class Comm {

                boolean stillWriting = true;
            }
            final Comm comm = new Comm();

            //start reading from the port
            Thread reader = new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream input = null;
                    try {
                        String x = "";
                        Thread.sleep(100);

                        BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        while (comm.stillWriting) { //note maybe should be outputshutdown
                            if (br.ready()){
                                x=x+br.readLine();
                            } else {
                                Thread.sleep(10);
                            }
                               
                        }
                        while (br.ready()){
                            x=br.readLine();
                        }
                        new Log().level4Debug(x);
                    } catch (IOException ex) {
                        Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });
            //start writing to the port
            Thread writer = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OutputStream out=socket.getOutputStream();
                        byte[] buf=new byte[4096];
                        
                        while ((buf=new byte[input.available()]).length>1){
                            input.read(buf);
                            out.write(buf);
                        }
                        comm.stillWriting=false;
                        
                    } catch (IOException ex) {
                        Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            while (!deviceReady){
                Thread.sleep(10);
            }
            System.out.println("preparing reader/writer");
            reader.setName("Databridge Reader");
            reader.start();
            writer.start();
         
            

            writer.setName("Databridge writer");

            
            writer.join();
            reader.join();
           /* if (!socket.isOutputShutdown()){
                try {
                    socket.getOutputStream().write("\u001a\r\n".getBytes());
                    socket.getOutputStream().write(13);
                    socket.getOutputStream().write(0xA);
                } catch (java.net.SocketException ex){

                }
            }*/
            System.out.println("bytes written shutting stream down");

            deviceReady=false;
            socket.close();
            
                        
            t.join();
            System.out.println("All threads done");
        } catch (UnknownHostException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }

   


    }

    
    private void copyStream(InputStream source, OutputStream dest){
        try {
            dest.write(IOUtils.readFully(source, 0, true));
        } catch (IOException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }
    
    private static boolean deviceReady=false;
    private Runnable openDeviceSideLinkForSend(final String remoteFileName) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    String[] cmd = new String[]{ADBTools.getADBCommand(), "shell",BusyboxTools.getBusyboxLocation() + " nc -l 127.0.0.1:" + port+ ">\""+remoteFileName+"\";sync; exit"};
                    ProcessBuilder p=new ProcessBuilder(cmd);
                    p.redirectError();
                    Process proc=p.start();
                    Thread.sleep(2000);

                    try {
                    if (proc.exitValue()>=0){
                        System.out.println("((((((((((((SYSTEM FAILURE)))))))))))))");
                        //TODO: process could not be started shutdown the operation
                        return;
                        
                    }
                    }catch (IllegalThreadStateException ex){
                        deviceReady=true;
                        System.out.println("Receiving end ready!");
                        while(deviceReady){
                            Thread.sleep(1000);
                        }
                        proc.destroy();
                        System.out.println("Receiving end done!");

                    }
                    BufferedReader br= new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    while (br.ready()) new Log().level0Error(br.readLine());
                    
                    

                } catch (InterruptedException ex) {
                    Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        };
    }
}
