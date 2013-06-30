/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASPACcreator.CASPACcreatorGUI;

import CASUAL.Zip;
import java.io.File;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

/**
 *
 * @author loganludington
 */
public class mainWindowController {
    
    private DefaultListModel fileList = new DefaultListModel();
    private Zip zip;
    private JFrame mw;
    private boolean force = false;
    private boolean ignore=false;
    private String outputFile=null;

    public mainWindowController() {
    }
    
    public mainWindowController(JFrame mw) {
        this.mw = mw;
    }
    
    public void init()
    {
        mw = new mainWindow(this);
        mw.setVisible(true);
    }
 
    public void addFileToZip(File[] files){
        for (File f: files)
            if (f.exists())
                fileList.addElement(f.toString());
    }
    
    public void addFileToZip(File files){
        if (files.exists())
            fileList.addElement(files.toString());
    }
    
    public void removeFiles(int[] indexList)
    {
        for (int i: indexList)
            fileList.remove(i);
    }
    
    public ListModel getListModel()
    {
        return fileList;
            
    }
    
    public void zipFiles(String output) throws IOException
    {
        if (!output.endsWith(".zip"))
        {
            JOptionPane.showMessageDialog(mw,
            "Error: " + output + " is not a valid zipfile.\n"
                    + "Please make sure that your file ends in .zip",
            "Zip File Not Valid",
            JOptionPane.ERROR_MESSAGE);
            return;
        }
        else if (new File(output).exists() && !(force) )
        {
            int returnVal = JOptionPane.showConfirmDialog(mw, "Warning: "+ output + " already exist.\n"
                + "Would you like to overwrite the file?", "Overwite existing zip?", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (returnVal != JOptionPane.YES_OPTION)
                return;  
        }
        
        zip = new Zip(output);
        
        for (Object o : fileList.toArray())
        {
            File file = new File(o.toString());
            System.out.println(file.exists());
            zip.addToZip(file);
        }
        zip.execute();
        
        JOptionPane.showMessageDialog(mw,
            "SUCCESS: " + output + " was created.\n"
                    ,
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
        
        System.exit(0);
    }
    
    public void setForce(boolean force) {
        this.force = force;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }
    
    
    
    
    
}
