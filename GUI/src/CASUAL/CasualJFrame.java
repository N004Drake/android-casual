package CASUAL;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author adam
 */
public class CasualJFrame extends javax.swing.JPanel {
    Log Log=new Log();
    FileOperations FileOperations=new FileOperations();
    /**
     * Creates new form CasualJFrame
     */
    public CasualJFrame() {
        initComponents();
        Statics.ProgressArea = this.jTextArea1;
        Log.level1(FileOperations.readTextFromResource(Statics.ScriptLocation+"Overview.Instructions.txt"));
         
        Log.level2("Deploying ADB");
        deployADB();
        
        Log.level3("Searching for scripts");
        prepareScripts();
        System.out.println(Statics.GUI);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        jLabel1.setText("NARZ or picture of some sort");

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton1.setText("Do It!");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addGap(8, 8, 8))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ScriptParser ScriptParser=new ScriptParser();
        ScriptParser.executeSelectedScript(jComboBox1.getSelectedItem()+".scr");
        ScriptParser.executeSelectedScript(Statics.AdbDeployed);


    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
         Log.level1(FileOperations.readTextFromResource(Statics.ScriptLocation+jComboBox1.getSelectedItem().toString()+".txt"));

    }//GEN-LAST:event_jComboBox1ActionPerformed
    
    private void prepareScripts(){
        try {
            listScripts();
        } catch (IOException ex) {
            Log.level0("ListScripts() could not find any entries");
            Logger.getLogger(CasualJFrame.class.getName()).log(Level.SEVERE, null, ex);
           
        }
        
        
        
    }
    private void listScripts() throws IOException {
        CodeSource Src = CASUAL.CASUALApp.class.getProtectionDomain().getCodeSource();
        int Count=0;

        if (Src != null) {
            URL jar = Src.getLocation();
            ZipInputStream Zip = new ZipInputStream(jar.openStream());
            ZipEntry ZEntry;
            while ((ZEntry = Zip.getNextEntry()) != null) {
                String EntryName = ZEntry.getName();
                if (EntryName.endsWith(".scr")) {
                    Log.level3("Found: "+EntryName.replace(".scr", ""));
                    jComboBox1.addItem(EntryName.replace(".scr", ""));
                    Count++;
                }
            }
            if (Count == 0 ){
                Log.level0("No Scripts found. Using Test Script.");
                jComboBox1.addItem("Test Script");
            }

        }
      
   
    }
    public void enableControls(boolean status){
        jButton1.setEnabled(status);
        jComboBox1.setEnabled(status);
        Log.level3("Controls Enabled status: " + status);
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

    private void deployADB() {
        if (Statics.isLinux()){
            Statics.AdbDeployed=Statics.TempFolder+"adb";
            Log.level0(Statics.TempFolder);
            FileOperations.copyFromResourceToFile(Statics.LinuxADB, Statics.AdbDeployed);
            FileOperations.setExecutableBit(Statics.AdbDeployed);
        } else if (Statics.isMac()){
            Statics.AdbDeployed=Statics.TempFolder+"adb";
            FileOperations.copyFromResourceToFile(Statics.MacADB, Statics.AdbDeployed);
            FileOperations.setExecutableBit(Statics.AdbDeployed);
        } else if (Statics.isWindows()){
            Statics.AdbDeployed=Statics.TempFolder+"adb.exe";
            FileOperations.copyFromResourceToFile(Statics.WinADB, Statics.AdbDeployed);
            FileOperations.setExecutableBit(Statics.AdbDeployed);
            FileOperations.copyFromResourceToFile(Statics.WinADB2, Statics.TempFolder+"AdbWinApi.dll");
            FileOperations.copyFromResourceToFile(Statics.WinADB3, Statics.TempFolder+"AdbWinUsbApi.dll");
        } else {
            Log.level0("Your system is not supported");
        }
        FileOperations.copyFromResourceToFile(Statics.ADBini, Statics.TempFolder+"adb_usb.ini");
        
        String[] cmd={Statics.AdbDeployed,"kill-server"};
        Log.level0(new Shell().sendShellCommand(cmd));
        String[] cmd2= {Statics.AdbDeployed, "devices"};
        Log.level0(new Shell().elevateSimpleCommand(cmd2));

    }
}
