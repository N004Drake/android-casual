/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicedetector;

/**
 *
 * @author owner
 */
public class driverInterface {
    private final String PID;
    
    public driverInterface(String PID) {
        this.PID = PID;
    }
    
    public boolean queryInstall() {
    cmdInterface cmd = new cmdInterface("cmd /C reg.exe /Query "
            + "HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
            + PID);
    cmd.run();
    String outString = cmd.getOuputString();
    if(outString.contains("ERROR"))
        return false;
    else
        return true;
}
    
    
}
