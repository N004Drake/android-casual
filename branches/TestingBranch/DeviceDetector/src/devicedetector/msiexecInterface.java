/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicedetector;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Hashtable;

/**
 *
 * @author loganludington
 */
public class msiexecInterface {
    
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
    public boolean quite = true;
    
    //Logging Options
    public enum LoggingOption {
        STATUS_MESSAGES, NONFATAL_WARNINGS, ALL_ERROR_MESSAGES, STARTUP_OF_ACTIONS,
        ACTION_SPECIFIC_RECORDS, USER_REQUESTS, INITIAL_USER_INTERFACE_PARAMS, 
        OUT_OF_MEMORY, TERMINAL_PROPS, VERBOSE, APPEND, FLUSH, ALL
    }
    private EnumMap<LoggingOption,String> loggingOptions = new EnumMap<>(LoggingOption.class);
    private boolean logging =false;
    public File logFile;
    
    //Error Variables
    private boolean valid = false;
    private ArrayList<String> errorList = new ArrayList<>();
    
    //MSI String Location
    private final String msiFile;

    public static void main(String[] args) {
        msiexecInterface mi = new msiexecInterface("testing");
        mi.setLoggingOptions(LoggingOption.VERBOSE, LoggingOption.ALL_ERROR_MESSAGES);
        System.out.println(mi.getLoggingOptions());
    }
    
    public msiexecInterface(String msiFile) {
        this.msiFile= msiFile;
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

    public EnumMap<LoggingOption,String> getLoggingOptions() {
        return loggingOptions;
    }

    public void setLoggingOptions(LoggingOption... loggingOptions) {
        for (LoggingOption op : loggingOptions)
            this.loggingOptions.put(op, "true");
    }
    
    

    
}
