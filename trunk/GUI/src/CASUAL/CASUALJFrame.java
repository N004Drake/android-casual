/*
 * Copyright (c) 2012 Adam Outler
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package CASUAL;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.*;
import org.jdesktop.application.Application;

/**
 *
 * @author adam
 */
public class CASUALJFrame extends javax.swing.JFrame {

    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    String NonResourceFileName;
    Log Log = new Log();
    FileOperations FileOperations = new FileOperations();

    /**
     * Creates new form CASUALJFrame2
     */
    public CASUALJFrame() {
        initComponents();        
        Statics.GUI=this;
        Statics.ProgressArea = this.ProgressArea;
        Statics.ProgressBar=this.ProgressBar;
        ProgressArea.setText(Statics.PreProgress);
        populateFields();
        org.jdesktop.application.ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(CASUALJFrame.class);


        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int count = 0; count < busyIcons.length; count++) {
            busyIcons[count] = resourceMap.getIcon("StatusBar.busyIcons[" + count + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                StatusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });

        idleIcon = resourceMap.getIcon("/" + "StatusBar.idleIcon");
        StatusAnimationLabel.setIcon(idleIcon);


        // connecting action tasks to status bar via TaskMonitor




        Statics.ProgressArea = this.ProgressArea;
        Log.level1(FileOperations.readTextFromResource(Statics.ScriptLocation + "Overview.txt"));

        Log.level2("Deploying ADB");
        deployADB();

        Log.level3("Searching for scripts");
        prepareScripts();
        System.out.println(Statics.GUI);


    }

    /*
     * Timer for adb devices
     */
    Shell Shell=new Shell();
    public final static int ONE_SECOND = 500;
    Timer DeviceCheck = new Timer(ONE_SECOND, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            String DeviceCommand[]={Statics.AdbDeployed,"devices"};
            String DeviceList=Shell.silentShellCommand(DeviceCommand).replace("List of devices attached \n","").replace("\n","");
            Statics.DeviceTracker=DeviceList.split("device");
            
            //TODO: Remove for test
            for (int i=0; i < Statics.DeviceTracker.length; i++){
                Statics.DeviceTracker[i]=Statics.DeviceTracker[i].replace("\n","").replace("	device","");
                Log.level0("Device"+i+" "+Statics.DeviceTracker[i]);
                
            }
            if (DeviceList.contains("????????????")) {
                String cmd[]={Statics.AdbDeployed,"kill-server"};
                Log.level1("killing server and requesting elevated permissions");
                Shell.sendShellCommand(cmd);
                TimeOutOptionPane TimeOutOptionPane = new TimeOutOptionPane();
                String[] ok = {"ok"};
                TimeOutOptionPane.showTimeoutDialog(60, null, "It would appear that this computer\n"
                        + "is not set up properly to communicate\n"
                        + "with the device.  As a work-around we\n"
                        + "will attempt to elevate permissions \n"
                        + "to access the device properly.", "Insufficient Permissions", TimeOutOptionPane.OK_OPTION, 2, ok, 0);
                DeviceList = Shell.elevateSimpleCommand(DeviceCommand);
                if (!DeviceList.contains("????????????")) {
                    Log.level1(DeviceList);
                } else {
                    Log.level0("Unrecognized device detected");
                }

            }    
            Statics.GUI.setStatusMessageLabel(DeviceList);
        }    
    });
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        FileChooser1 = new javax.swing.JFileChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        ProgressArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        ProgressBar = new javax.swing.JProgressBar();
        StatusAnimationLabel = new javax.swing.JLabel();
        StatusMessageLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        MenuItemOpenScript = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        MenuItemShowDeveloperPane = new javax.swing.JMenuItem();
        MenuItemShowAboutBox = new javax.swing.JMenuItem();

        FileChooser1.setDialogTitle("Select a CASUAL \"scr\" file");
        FileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileChooser1ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.title") +java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision"));

        ProgressArea.setColumns(20);
        ProgressArea.setLineWrap(true);
        ProgressArea.setRows(5);
        ProgressArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(ProgressArea);

        jLabel1.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        jLabel1.setText("NARZ or picture of some sort");

        jComboBox1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                jComboBox1PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
        });
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

        jButton2.setText("Donate");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        StatusAnimationLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/CASUAL/resources/icons/idle-icon.png"))); // NOI18N

        StatusMessageLabel.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        StatusMessageLabel.setText("Ready");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSeparator1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StatusMessageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StatusAnimationLabel)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(StatusMessageLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(StatusAnimationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(24, 24, 24))
        );

        jMenu1.setText("File");

        MenuItemOpenScript.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_MASK));
        MenuItemOpenScript.setText("Open CASUAL script");
        MenuItemOpenScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemOpenScriptActionPerformed(evt);
            }
        });
        jMenu1.add(MenuItemOpenScript);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("About");

        MenuItemShowDeveloperPane.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        MenuItemShowDeveloperPane.setText("Developing A Script");
        MenuItemShowDeveloperPane.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemShowDeveloperPaneActionPerformed(evt);
            }
        });
        jMenu2.add(MenuItemShowDeveloperPane);

        MenuItemShowAboutBox.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        MenuItemShowAboutBox.setText("About");
        MenuItemShowAboutBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemShowAboutBoxActionPerformed(evt);
            }
        });
        jMenu2.add(MenuItemShowAboutBox);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.busyIconTimer.start();
        enableControls(false);
        if (Statics.TargetScriptIsResource) {
            ScriptParser ScriptParser = new ScriptParser();
            ScriptParser.executeSelectedScriptResource(jComboBox1.getSelectedItem().toString());
        } else {
            ScriptParser ScriptParser = new ScriptParser();
            ScriptParser.executeSelectedScriptFile(NonResourceFileName);
        }
        this.busyIconTimer.stop();
        enableControls(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void MenuItemShowDeveloperPaneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemShowDeveloperPaneActionPerformed
        CASUALDeveloperInstructions CDI = new CASUALDeveloperInstructions();
        CDI.setVisible(true);
    }//GEN-LAST:event_MenuItemShowDeveloperPaneActionPerformed

    private void MenuItemOpenScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemOpenScriptActionPerformed
        Statics.TargetScriptIsResource = false;
        String FileName;
        int returnVal = FileChooser1.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                FileName = FileChooser1.getSelectedFile().getCanonicalPath();
                NonResourceFileName = this.getFilenameWithoutExtension(FileName);
                Log.level1("Description for " + NonResourceFileName);
                Log.level1(FileOperations.readFile(NonResourceFileName + ".txt"));
                this.jComboBox1.setSelectedItem("woot");
                Statics.SelectedScriptFolder = Statics.TempFolder + new File(NonResourceFileName).getName();
                Log.level0("Delete this debug line in MenuItemOpenScriptActionPerformed()");
            } catch (IOException ex) {
                Logger.getLogger(CASUALJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.out.println("File access cancelled by user.");
        }

    }//GEN-LAST:event_MenuItemOpenScriptActionPerformed

    private void MenuItemShowAboutBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemShowAboutBoxActionPerformed
        Statics.TargetScriptIsResource = false;
        CASUALAboutBox CAB = new CASUALAboutBox();
        CAB.setVisible(true);
    }//GEN-LAST:event_MenuItemShowAboutBoxActionPerformed

    private void FileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileChooser1ActionPerformed
        /*
         * Log.level1("Description for " +
         * jComboBox1.getSelectedItem().toString());
         * Log.level1(FileOperations.readTextFromResource(Statics.ScriptLocation+jComboBox1.getSelectedItem().toString()+".txt"));
         * Statics.SelectedScriptFolder=Statics.TempFolder+jComboBox1.getSelectedItem().toString();
         */
    }//GEN-LAST:event_FileChooser1ActionPerformed

    private void jComboBox1PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jComboBox1PopupMenuWillBecomeInvisible
        Statics.TargetScriptIsResource = true;
        Log.level1("Description for " + jComboBox1.getSelectedItem().toString());
        Log.level1(FileOperations.readTextFromResource(Statics.ScriptLocation + jComboBox1.getSelectedItem().toString() + ".txt"));
        Statics.SelectedScriptFolder = Statics.TempFolder + jComboBox1.getSelectedItem().toString();        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1PopupMenuWillBecomeInvisible

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        Statics.TargetScriptIsResource = true;
        Log.level1("Description for " + jComboBox1.getSelectedItem().toString());
        Log.level1(FileOperations.readTextFromResource(Statics.ScriptLocation + jComboBox1.getSelectedItem().toString() + ".txt"));
        Statics.SelectedScriptFolder = Statics.TempFolder + jComboBox1.getSelectedItem().toString();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        TimeOutOptionPane timeOutOptionPane = new TimeOutOptionPane();
        int DResult = timeOutOptionPane.showTimeoutDialog(
                60, //timeout
                null, //parentComponent
                "This application was developed by " + Statics.DeveloperName + " using CASUAL framework.\n"
                + "Donations give developers a tangeble reason to continue quality software development\n",
                "Donate to the developers", //DisplayTitle
                TimeOutOptionPane.OK_OPTION, // Options buttons
                TimeOutOptionPane.INFORMATION_MESSAGE, //Icon
                new String[]{"Donate To CASUAL", "Donate To " + Statics.DonateButtonText}, // option buttons
                "No"); //Default{
        if (DResult == 0) {
            launchLink("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=ZYM99W5RHRY3Y");
        } else if (DResult == 1) {
            launchLink(Statics.DeveloperDonateLink);

        }

    }//GEN-LAST:event_jButton2ActionPerformed
    private static void launchLink(String Link) {
        LinkLauncher LinkLauncher = new LinkLauncher();
        LinkLauncher.launchLink(Link);
    }
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser FileChooser1;
    private javax.swing.JMenuItem MenuItemOpenScript;
    private javax.swing.JMenuItem MenuItemShowAboutBox;
    private javax.swing.JMenuItem MenuItemShowDeveloperPane;
    private javax.swing.JTextArea ProgressArea;
    private javax.swing.JProgressBar ProgressBar;
    private javax.swing.JLabel StatusAnimationLabel;
    private javax.swing.JLabel StatusMessageLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables

    private void deployADB() {
       RunableDeployADB RunableDeployADB = new RunableDeployADB();
       RunableDeployADB.run();
    }

    private void prepareScripts() {
        try {
            listScripts();
        } catch (IOException ex) {
            Log.level0("ListScripts() could not find any entries");
            Logger.getLogger(CASUALJFrame.class.getName()).log(Level.SEVERE, null, ex);

        }


    }

    private String getFilenameWithoutExtension(String FileName) {

        if (FileName.endsWith(".scr")) {
            FileName = FileName.replace(".scr", "");
        }
        return FileName;

    }
    private boolean DeviceTimerState=false;
    public void startStopTimer(boolean StateCommanded){
        if (StateCommanded && !DeviceTimerState){
            DeviceCheck.start();
        } else if (!StateCommanded && DeviceTimerState){
            DeviceCheck.stop();
        }
    }
    public void setStatusMessageLabel(String text){
        this.StatusMessageLabel.setText(text);
    }
    private void listScripts() throws IOException {
        CodeSource Src = CASUAL.CASUALApp.class.getProtectionDomain().getCodeSource();
        int Count = 0;

        if (Src != null) {
            URL jar = Src.getLocation();
            ZipInputStream Zip = new ZipInputStream(jar.openStream());
            ZipEntry ZEntry;
            while ((ZEntry = Zip.getNextEntry()) != null) {
                String EntryName = ZEntry.getName();
                if (EntryName.endsWith(".scr")) {
                    Log.level3("Found: " + EntryName.replace(".scr", ""));
                    jComboBox1.addItem(EntryName.replace(".scr", ""));
                    Count++;
                }
            }
            if (Count == 0) {
                Log.level0("No Scripts found. Using Test Script.");
                jComboBox1.addItem("Test Script");
                Unzip Unzip=new Unzip();
                Unzip.UnZipResource("/SCRIPTS/Test Script.zip",Statics.TempFolder);
            }

        }


    }

    public void enableControls(boolean status) {
        jButton1.setEnabled(status);
        jComboBox1.setEnabled(status);
        Log.level3("Controls Enabled status: " + status);
    }

    protected ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private void populateFields() {

        try {
            this.jButton1.setText(java.util.ResourceBundle.getBundle("SCRIPTS/build").getString("Window.ExecuteButtonText"));
            this.setTitle(java.util.ResourceBundle.getBundle("SCRIPTS/build").getString("Window.Title") + " - " + this.getTitle());
            if (java.util.ResourceBundle.getBundle("SCRIPTS/build").getString("Window.UsePictureForBanner").equals("true")) {
                jLabel1.setText("");
                jLabel1.setIcon(createImageIcon("/SCRIPTS/" + java.util.ResourceBundle.getBundle("SCRIPTS/build").getString("Window.BannerPic"), java.util.ResourceBundle.getBundle("SCRIPTS/build").getString("Window.BannerText")));


            } else {
                this.jLabel1.setText(java.util.ResourceBundle.getBundle("SCRIPTS/build").getString("Window.BannerText"));
            }
            Statics.DeveloperName = java.util.ResourceBundle.getBundle("SCRIPTS/build").getString("Developer.Name");
            Statics.DonateButtonText = java.util.ResourceBundle.getBundle("SCRIPTS/build").getString("Developer.DonateToButtonText");
            Statics.DeveloperDonateLink = java.util.ResourceBundle.getBundle("SCRIPTS/build").getString("Developer.DonateLink");
        } catch (MissingResourceException ex) {
            Log.level0("Could not find build.prop");
            System.out.print(ex);
        }
    }
}

class RunableDeployADB implements Runnable{
    static public final String newline = "\n";
    FileOperations FileOperations=new FileOperations();
    Log Log=new Log();    
    public static void runAction(String Action) {
        (new Thread(new RunableDeployADB())).start();
    }   
    public void run() {
        DiffTextFiles DTF = new DiffTextFiles();


        if (Statics.isLinux()) {
            Log.level3("Found Linux Computer");
            //add our lines to the current adbini
            DTF.appendDiffToFile(Statics.FilesystemAdbIniLocationLinuxMac, DTF.diffResourceVersusFile(Statics.ADBini, Statics.FilesystemAdbIniLocationLinuxMac));
            Statics.AdbDeployed = Statics.TempFolder + "adb";
            FileOperations.copyFromResourceToFile(Statics.LinuxADB, Statics.AdbDeployed);
            FileOperations.setExecutableBit(Statics.AdbDeployed);
        } else if (Statics.isMac()) {
            Log.level3("Found Mac Computer");
            //add our lines to the current adbini
            DTF.appendDiffToFile(Statics.FilesystemAdbIniLocationLinuxMac, DTF.diffResourceVersusFile(Statics.ADBini, Statics.FilesystemAdbIniLocationLinuxMac));
            Statics.AdbDeployed = Statics.TempFolder + "adb";
            FileOperations.copyFromResourceToFile(Statics.MacADB, Statics.AdbDeployed);
            FileOperations.setExecutableBit(Statics.AdbDeployed);
        } else if (Statics.isWindows()) {
            Log.level3("Found Windows Computer");
            DTF.appendDiffToFile(Statics.FilesystemAdbIniLocationWindows, DTF.diffResourceVersusFile(Statics.ADBini, Statics.FilesystemAdbIniLocationWindows));
            FileOperations.copyFromResourceToFile(Statics.WinPermissionElevatorResource, Statics.WinElevatorInTempFolder);
            Statics.AdbDeployed = Statics.TempFolder + "adb.exe";
            FileOperations.copyFromResourceToFile(Statics.WinADB, Statics.AdbDeployed);
            FileOperations.copyFromResourceToFile(Statics.WinADB2, Statics.TempFolder + "AdbWinApi.dll");
            FileOperations.copyFromResourceToFile(Statics.WinADB3, Statics.TempFolder + "AdbWinUsbApi.dll");
        } else {
            Log.level0("Your system is not supported");
        }
        FileOperations.copyFromResourceToFile(Statics.ADBini, Statics.TempFolder + "adb_usb.ini");

        Shell Shell = new Shell();
        
        String[] cmd = {Statics.AdbDeployed, "kill-server"};
        String[] cmd2 = {Statics.AdbDeployed, "devices"};
        String DeviceList = Shell.sendShellCommand(cmd2);
        if (DeviceList.contains("ELFCLASS64")&& DeviceList.contains("wrong ELF")){
                JOptionPane.showMessageDialog(Statics.GUI,
                     "Could not execute ADB. 'Wrong ELF class' error\n"
                        + "This can be resolved by installation of ia32-libs"
                        + "eg.. sudo apt-get install ia32-libs\n"
                        + "ie.. sudo YourPackageManger install ia32-libs"
                        ,"ELFCLASS64 error!",
                     JOptionPane.INFORMATION_MESSAGE);   
        }


        Log.level1("Device List:" + DeviceList);
        if (DeviceList.contains("????????????")) {
            Log.level1("killing server and requesting elevated permissions");
            Shell.sendShellCommand(cmd);
            TimeOutOptionPane TimeOutOptionPane = new TimeOutOptionPane();
            String[] ok = {"ok"};
            TimeOutOptionPane.showTimeoutDialog(60, null, "It would appear that this computer\n"
                    + "is not set up properly to communicate\n"
                    + "with the device.  As a work-around we\n"
                    + "will attempt to elevate permissions \n"
                    + "to access the device properly.", "Insufficient Permissions", TimeOutOptionPane.OK_OPTION, 2, ok, 0);
            DeviceList = Shell.elevateSimpleCommand(cmd2);
            if (!DeviceList.contains("????????????")) {
                Log.level1(DeviceList);
            } else {
                Log.level0("Unrecognized device detected");

            }


        }
        
        Statics.GUI.startStopTimer(true);

    }

    
    
    
}