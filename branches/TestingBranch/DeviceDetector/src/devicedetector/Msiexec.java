/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicedetector;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author loganludington
 */
public class Msiexec {
    
    //Operation varibles
    public enum Operation {
        INSTALL,
        REPAIR,
        UNINSTALL
    }
    private Operation op = Operation.INSTALL;
    
    //Repair Options 
    public enum RepairOptions {
        ONLY_IF_MISSING, OLDER_VERSION, OLDER_OR_EQUAL_VERSION, DIFFERENT_VERSION,
        CHECKSUM_MISMATCH, ALL_FILES
    }
    private RepairOptions ro;
    
    //Option Varibles
    private boolean quite = true;
    
    //Logging Options
    public enum LoggingOption {
        STATUS_MESSAGES, NONFATAL_WARNINGS, ALL_ERROR_MESSAGES, STARTUP_OF_ACTIONS,
        ACTION_SPECIFIC_RECORDS, USER_REQUESTS, INITIAL_USER_INTERFACE_PARAMS, 
        OUT_OF_MEMORY, TERMINAL_PROPS, VERBOSE, APPEND, FLUSH, ALL
    }
    private Set<LoggingOption> loggingOptions = new HashSet<>();
    private boolean logging =false;
    private File logFile;
    
    //MSI String Location
    private final String msiFile;

    public static void main(String[] args) {
        Msiexec mi = new Msiexec("testing");
        mi.setLoggingOptions(LoggingOption.VERBOSE, LoggingOption.ALL_ERROR_MESSAGES);
        System.out.println(mi.getLoggingOptions());
    }
    
    public Msiexec(String msiFile) {
        if (new File(msiFile).exists())
            this.msiFile= msiFile;
        else
            this.msiFile = null;
    }

    public Operation getOp() {
        return op;
    }

    public void setOp(Operation op) {
        this.op = op;
    }

    public RepairOptions getRo() {
        return ro;
    }

    public void setRo(RepairOptions ro) {
        this.ro = ro;
    }

    public boolean isQuite() {
        return quite;
    }

    public void setQuite(boolean quite) {
        this.quite = quite;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    public Set<LoggingOption> getLoggingOptions() {
        return loggingOptions;
    }

    public String getMsiFile() {
        return msiFile;
    }
    
    

    public void setLoggingOptions(LoggingOption... loggingOptions) {
        this.loggingOptions.addAll(Arrays.asList(loggingOptions));
    }
    
    public void clearLoggingOptions() {
        this.loggingOptions.clear();
    }
    
    public boolean isValid() {
        if (op == null)
            return false;
        if (msiFile == null)
            return false;
        return true;
    }
    
}
