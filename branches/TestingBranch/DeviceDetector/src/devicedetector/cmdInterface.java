/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicedetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Logan Ludington
 */
public class cmdInterface {
    private InputStreamReader isr = null;
    private InputStream is = null;
    private BufferedReader br = null;
    private String command;
    private Process p = null;
    
    final private String elevationBatchFile = "Set objShell = CreateObject(\"Shell.Application\")\n"
                        + "args = \"%s\" \n"
                        + "objShell.ShellExecute \"%s\", args, \"\", \"runas\"";
    
    public static void main(String[] args) {
        cmdInterface cmd = new cmdInterface("cmd /C reg.exe /Query /? > C:\\Users\\owner\\Downloads\\testing2.log");
        cmd.runAsAdmin();
        cmd.printOuput();
    }
    
    public cmdInterface(String command) {
        this.command = command;
    }
    
    public void run() {
        if (command == null) {
            System.out.println("CmdInterface: A command is required. \n"
                    + "\tPlease create a new cmdInterface and run.\n");
            return;
        }
        try {
            p = Runtime.getRuntime().exec(command);
            try {
                p.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(cmdInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(cmdInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void runAsAdmin() { 
        File outFile = null;
        try {
            //        InputStream is = this.getClass().getResourceAsStream("resources/Elevate.exe");
            //        OutputStream os;
            //        int readBytes;
            //        byte[] buffer = new byte[4096];
            //        File outFile = null;
            //        try {
            //            outFile = File.createTempFile("Elevate", ".exe");
            //            outFile.deleteOnExit();
            //            os = new FileOutputStream(outFile);
            //            while ((readBytes = is.read(buffer)) > 0) {
            //                os.write(buffer, 0 , readBytes);
            //            }
            //            os.close();
            //            is.close();
            //        } catch (FileNotFoundException ex) {
            //            Logger.getLogger(cmdInterface.class.getName()).log(Level.SEVERE, null, ex);
            //        } catch (IOException ex) {
            //            Logger.getLogger(cmdInterface.class.getName()).log(Level.SEVERE, null, ex);
            //        }
            //        if (outFile == null)
            //            command = null;
            //        else
                    outFile = File.createTempFile("sudo", ".vbs");
                    outFile.deleteOnExit();
                    FileWriter fw = new FileWriter(outFile);
                    String[] cmdSplit = command.split(" ", 2);
                    if (cmdSplit.length < 2)
                        fw.write(String.format(elevationBatchFile, "", cmdSplit[0]));
                    else
                        fw.write(String.format(elevationBatchFile, cmdSplit[1], cmdSplit[0]));
                    fw.close();
        } catch (IOException ex) {
            Logger.getLogger(cmdInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (command == null) {
            System.out.println("CmdInterface: A command is required. \n"
                    + "\tPlease create a new cmdInterface and run.\n");
            return;
        }
        if (outFile == null) {
            System.out.println("CmdInterface: Elevation Script could not be written \n"
                    + "\tPlease create a new cmdInterface and run.\n");
            return;
        }
        command = "cscript "+ outFile.toString();
        run();

        
    }

    public InputStreamReader getInputStreamReader() {
        if (is == null)
            getInputStream();
        if (is == null) {
            return null;
        }
        isr = new InputStreamReader(is);
        return isr;
    }

    public InputStream getInputStream() {
        if ( p==null) {
            System.out.println("CmdInterface: Process has not been run. \n"
                    + "\tPlease use the run() method before this one.\n");
            return null;
        }
        is = p.getInputStream();
        return is;
    }

    public BufferedReader getBufferedReader() {
        if (isr==null)
            getInputStreamReader();
        if (isr == null) {
            return null;
        }
        br = new BufferedReader(isr);
        return br;
    }
    
    public void printOuput() {
        if (br == null)
            getBufferedReader();
        if (br == null) {
            return;
        }
        String line;
        try {
            while (( line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(cmdInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public String getOuputString() {
        String outputString = "";
        if (br == null)
            getBufferedReader();
        if (br == null) {
            return null;
        }
        String line;
        try {
            while (( line = br.readLine()) != null) {
                outputString = outputString + line + "\n";
            }
        } catch (IOException ex) {
            Logger.getLogger(cmdInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outputString;
    }
    
}
