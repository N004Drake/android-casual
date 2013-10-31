/*CASUALJFrame provides UI for CASUAL. 
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
package GUI.development;

import CASUAL.iCASUALGUI;
import CASUAL.ADBTools;
import CASUAL.AudioHandler;
import CASUAL.CASUALApp;
import CASUAL.CASUALConnectionStatusMonitor;
import CASUAL.CASUALMessageObject;
import CASUAL.misc.CASUALScrFilter;
import CASUAL.CASUALScriptParser;
import CASUAL.FileOperations;
import CASUAL.network.LinkLauncher;
import CASUAL.Log;
import CASUAL.Statics;
import CASUAL.Unzip;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 *
 * @author adam
 */
public final class CASUALJFrameMain extends javax.swing.JFrame implements iCASUALGUI {

    Caspac caspac;
    String nonResourceFileName;
    Log log = new Log();
    FileOperations fileOperations = new FileOperations();
    private String ComboBoxValue = "";

    /**
     * Creates new form CASUALJFrame2
     */
    public CASUALJFrameMain() {

        initComponents();

        //set up place to log to for GUI
        ProgressArea.setContentType("text/html");
        Statics.ProgressPane = CASUALJFrameMain.ProgressArea;
        Statics.ProgressPane.setContentType("text/html");
        Statics.ProgressDoc = Statics.ProgressPane.getStyledDocument();
        ProgressArea.setText(Statics.PreProgress + ProgressArea.getText());

        Statics.lockGUIformPrep = false;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {

                CASUALApp.shutdown(0);
            }
        });

    }

    /*
     * Timer for adb devices
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        FileChooser1 = new javax.swing.JFileChooser();
        windowBanner = new javax.swing.JLabel();
        comboBoxScriptSelector = new javax.swing.JComboBox();
        startButton = new javax.swing.JButton();
        DonateButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        StatusLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        informationScrollPanel = new javax.swing.JScrollPane();
        ProgressArea = new javax.swing.JTextPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        MenuItemOpenScript = new javax.swing.JMenuItem();
        MenuItemExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        MenuItemShowDeveloperPane = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        MenuItemShowAboutBox = new javax.swing.JMenuItem();

        FileChooser1.setDialogTitle("Select a CASUAL \"scr\" file");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        windowBanner.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        windowBanner.setText("loading.. please wait");

        comboBoxScriptSelector.setEnabled(false);
        comboBoxScriptSelector.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                comboBoxScriptSelectorPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        comboBoxScriptSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxScriptSelectorActionPerformed(evt);
            }
        });

        startButton.setText("Do It!");
        startButton.setEnabled(false);
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                startButtonMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButtonMouseExited(evt);
            }
        });
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        DonateButton.setText("Donate");
        DonateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DonateButtonActionPerformed(evt);
            }
        });

        StatusLabel.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        StatusLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/development/resources/images/DeviceDisconnected.png"))); // NOI18N
        StatusLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                StatusLabelMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                StatusLabelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(StatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(StatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        informationScrollPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Important Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Ubuntu", 1, 10))); // NOI18N

        ProgressArea.setText("<html>");
        ProgressArea.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        informationScrollPanel.setViewportView(ProgressArea);

        jMenu1.setText("File");

        MenuItemOpenScript.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_MASK));
        MenuItemOpenScript.setText("Open CASUAL script");
        MenuItemOpenScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemOpenScriptActionPerformed(evt);
            }
        });
        jMenu1.add(MenuItemOpenScript);

        MenuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        MenuItemExit.setText("Exit");
        MenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemExitActionPerformed(evt);
            }
        });
        jMenu1.add(MenuItemExit);

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

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Show Log");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(windowBanner, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(comboBoxScriptSelector, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(DonateButton))
                            .addComponent(informationScrollPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(windowBanner, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(informationScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxScriptSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startButton)
                    .addComponent(DonateButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * the start button was pressed.
     */
    @Override
    public void StartButtonActionPerformed() {
        log.level4Debug("StartButtonActionPerformed() Script Activated");
        log.level4Debug("Script known as " + this.comboBoxScriptSelector.getSelectedItem().toString() + " is running");

        ADBTools.adbMonitor(false);
        enableControls(false);
        String script = comboBoxScriptSelector.getSelectedItem().toString();

        //execute
        if (Statics.CASPAC.getActiveScript().extractionMethod != 2) { //not on filesystem
            log.level4Debug("Loading internal resource: " + script);
            Statics.CASPAC.getActiveScript().scriptContinue = true;
            new CASUALScriptParser().executeSelectedScript(caspac, true);
        }

    }

    /**
     * sets the progress bar value.
     *
     * @param value value for progress bar
     */
    @Override
    public void setProgressBar(int value) {
        progressBar.setValue(value);
        this.repaint();
    }

    /**
     * sets max value for progress bar
     *
     * @param value maximum
     */
    @Override
    public void setProgressBarMax(int value) {
        progressBar.setMaximum(value);
    }

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        this.StartButtonActionPerformed();
    }//GEN-LAST:event_startButtonActionPerformed

    private void MenuItemShowDeveloperPaneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemShowDeveloperPaneActionPerformed
        CASUALJFrameDeveloperInstructions CDI = new CASUALJFrameDeveloperInstructions();
        CDI.setVisible(true);
    }//GEN-LAST:event_MenuItemShowDeveloperPaneActionPerformed

    private void MenuItemOpenScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemOpenScriptActionPerformed

        String FileName;
        FileChooser1.setDialogTitle("Select a CASUAL \"scr\" file");
        FileChooser1.setFileFilter(new CASUALScrFilter());
        int returnVal = FileChooser1.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                ADBTools.adbMonitor(false);
                this.enableControls(false);
                FileName = FileChooser1.getSelectedFile().getCanonicalPath();
                nonResourceFileName = this.getFilenameWithoutExtension(FileName);
                log.level2Information("Description for " + nonResourceFileName);
                try {
                    log.level2Information(fileOperations.readFile(nonResourceFileName + ".txt"));
                } catch (Exception e) {
                    log.level2Information("@textResourceNotFound");
                }
                this.comboBoxScriptSelector.setSelectedItem(nonResourceFileName);
                Statics.SelectedScriptFolder = Statics.getTempFolder() + new File(nonResourceFileName).getName() + Statics.Slash;
                if (new FileOperations().verifyFileExists(nonResourceFileName.toString() + ".zip")) {
                    new Unzip(nonResourceFileName.toString() + ".zip").unzipFile(Statics.SelectedScriptFolder);
                }
                Statics.ScriptLocation = Statics.SelectedScriptFolder;
                comboBoxScriptSelector.setEditable(true);
                ComboBoxValue = getFilenameWithoutExtension(FileName);
                comboBoxScriptSelector.setSelectedItem(ComboBoxValue);
                comboBoxScriptSelector.setEditable(false);
                ADBTools.adbMonitor(true);
            } catch (IOException ex) {
                log.errorHandler(ex);
            }

        }
    }//GEN-LAST:event_MenuItemOpenScriptActionPerformed

    private void MenuItemShowAboutBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemShowAboutBoxActionPerformed
        CASUALJFrameAboutBox CAB = new CASUALJFrameAboutBox();
        CAB.setVisible(true);
    }//GEN-LAST:event_MenuItemShowAboutBoxActionPerformed

    private void MenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemExitActionPerformed
        CASUALApp.shutdown(0);
    }//GEN-LAST:event_MenuItemExitActionPerformed

    private void DonateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DonateButtonActionPerformed
        this.setInformationScrollBorderText("Donate");
        int DResult = new CASUALMessageObject("Donate to the developers", "This application was developed by " + caspac.build.developerName + " using CASUAL framework.\n"
                + "Donations give developers a tangeble reason to continue quality software development\n").showTimeoutDialog(
                        60, //timeout
                        null, //parentComponent
                        //DisplayTitle
                        javax.swing.JOptionPane.OK_OPTION, // Options buttons
                        javax.swing.JOptionPane.INFORMATION_MESSAGE, //Icon
                        new String[]{"Donate To CASUAL", "Donate To " + caspac.build.developerName}, // option buttons
                        "No"); //Default{
        if (DResult == 0) {
            launchLink("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=ZYM99W5RHRY3Y");
        } else if (DResult == 1) {
            launchLink(caspac.build.donateLink);
        }
    }//GEN-LAST:event_DonateButtonActionPerformed

    /**
     * gets the selected combobox item.
     *
     * @return selected item in combobox
     */
    @Override
    public String comboBoxGetSelectedItem() {
        return (String) comboBoxScriptSelector.getSelectedItem();
    }

    /**
     * adds an item to the combo box
     *
     * @param item item to add
     */
    @Override
    public void comboBoxScriptSelectorAddNewItem(String item) {
        comboBoxScriptSelector.addItem(item);
    }

    private void comboBoxScriptSelectorPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_comboBoxScriptSelectorPopupMenuWillBecomeInvisible
        ADBTools.adbMonitor(false);
        this.enableControls(false);
        Statics.lockGUIunzip = true;
        String selectedScript = comboBoxScriptSelector.getSelectedItem().toString();
        log.level4Debug("hiding script selector TargetScript: " + selectedScript);
        caspac.setActiveScript(caspac.getScriptByName(selectedScript));
        log.level2Information(caspac.getActiveScript().discription);
        caspac.waitForUnzipComplete();

        Statics.lockGUIunzip = false;
        ADBTools.adbMonitor(true);


    }//GEN-LAST:event_comboBoxScriptSelectorPopupMenuWillBecomeInvisible

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        CASUALJFrameLog CASUALLogJFrame = new CASUALJFrameLog();
        CASUALLogJFrame.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        ADBTools.adbMonitor(false);
        ADBTools.killADBserver();
    }//GEN-LAST:event_formWindowClosing
    boolean buttonEnableStage = false;
    private void startButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startButtonMouseClicked
    }//GEN-LAST:event_startButtonMouseClicked

    private void startButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startButtonMouseExited
    }//GEN-LAST:event_startButtonMouseExited

    private void StatusLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_StatusLabelMouseClicked
        if (buttonEnableStage) {
            log.level4Debug("Control system override active.  User has manually enabled controls");
            startButton.setEnabled(buttonEnableStage);
            this.comboBoxScriptSelector.setEnabled(buttonEnableStage);
            this.startButton.setText(java.util.ResourceBundle.getBundle("SCRIPTS/-build").getString("Window.ExecuteButtonText"));
            buttonEnableStage = false;

        }
        if (!startButton.isEnabled() && !Statics.lockGUIformPrep) {
            log.level4Debug("Control system override clicked");
            startButton.setText("Click again to enable all controls");
            buttonEnableStage = true;

        }
    }//GEN-LAST:event_StatusLabelMouseClicked

    private void StatusLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_StatusLabelMouseExited
        try {
            this.startButton.setText(java.util.ResourceBundle.getBundle("SCRIPTS/-build").getString("Window.ExecuteButtonText"));
        } catch (java.util.MissingResourceException ex) {
            try {
                this.startButton.setText(java.util.ResourceBundle.getBundle("SCRIPTS\\-build").getString("Window.ExecuteButtonText"));
            } catch (java.util.MissingResourceException er) {
                this.startButton.setText(Statics.CASPAC.build.executeButtonText);
            }
        }
        buttonEnableStage = false;
    }//GEN-LAST:event_StatusLabelMouseExited

    private void comboBoxScriptSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxScriptSelectorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboBoxScriptSelectorActionPerformed

    private static void launchLink(String Link) {
        new LinkLauncher(Link).launch();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DonateButton;
    private javax.swing.JFileChooser FileChooser1;
    private javax.swing.JMenuItem MenuItemExit;
    private javax.swing.JMenuItem MenuItemOpenScript;
    private javax.swing.JMenuItem MenuItemShowAboutBox;
    private javax.swing.JMenuItem MenuItemShowDeveloperPane;
    public static javax.swing.JTextPane ProgressArea;
    private javax.swing.JLabel StatusLabel;
    private javax.swing.JComboBox comboBoxScriptSelector;
    private javax.swing.JScrollPane informationScrollPanel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton startButton;
    private javax.swing.JLabel windowBanner;
    // End of variables declaration//GEN-END:variables

    /**
     * changes the label icon
     *
     * @param Icon resource to be displayed
     * @param Text text if icon is missing
     */
    @Override
    public void setStatusLabelIcon(String Icon, String Text) {
        StatusLabel.setIcon(createImageIcon(Icon, Text));
    }

    /**
     * takes a resource and turns it into an ImageIcon
     *
     * @param path pat to resource
     * @param description icon description
     * @return an icon
     */
    protected ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private String getFilenameWithoutExtension(String FileName) {

        if (FileName.endsWith(".scr")) {
            FileName = FileName.replace(".scr", "");
        }
        return FileName;

    }

    /**
     * sets the message label text
     *
     * @param text label text
     */
    @Override
    public void setStatusMessageLabel(String text) {
        this.StatusLabel.setText(text);
    }

    /**
     * gets the control status
     *
     * @return true if enabled
     */
    @Override
    public boolean getControlStatus() {
        return startButton.isEnabled() && comboBoxScriptSelector.isEnabled();
    }

    /**
     * sets controls status
     *
     * @param status commanded value
     * @return true if enabled false if not
     */
    @Override
    public boolean enableControls(boolean status) {
        //LockOnADBDisconnect tells CASUAL to disregard ADB status.
        if (caspac != null) {
            boolean bypassLock = caspac.build.alwaysEnableControls;
            if (bypassLock) {
                status = true; //if LockOnADBDisconnect is false then just enable controls
                startButton.setEnabled(status);
                comboBoxScriptSelector.setEnabled(status);
                return true;
            }
        }
        if (!Statics.lockGUIformPrep) {
            if (!Statics.lockGUIunzip) {
                if (!Statics.scriptRunLock) {
                    startButton.setEnabled(status);
                    comboBoxScriptSelector.setEnabled(status);
                    log.level4Debug("Controls Enabled status: " + status);
                } else {
                    log.level4Debug("Control Change requested but script is running");
                }
            } else {
                log.level4Debug("Control Change requested but unzip has not yet finished");
            }
        } else {
            log.level4Debug("Control Change requested but GUI is not ready is set.");
        }
        return checkGUIStatus(status) ? true : false;
    }

    private boolean checkGUIStatus(boolean expectedStatus) {
        if (Statics.isGUIIsAvailable()) {
            if (expectedStatus == Statics.GUI.getControlStatus()) {
                return true; //expected true = actually true;
            } else {
                return false;
            }
        } else if (Statics.isGUIIsAvailable()) {  //if gui is not available yet
            return false;
        }
        return true; //gui is not used for this CASUAL.

    }

    /**
     * sets the main window banner text if an image is not used
     *
     * @param text text to display as banner
     */
    @Override
    public void setWindowBannerText(String text) {
        windowBanner.setText(text);
    }

    /**
     * sets "do it!" button text
     *
     * @param text text for main execution button
     */
    @Override
    public void setStartButtonText(String text) {
        startButton.setText(text);
    }

    /**
     * sets window banner image
     *
     * @param icon image to display
     * @param text text if image cannot be displayed
     */
    /**
     *
     * @param icon
     * @param text
     */
    @Override
    public void setWindowBannerImage(BufferedImage icon, String text) {
        windowBanner.setIcon(new ImageIcon(icon, text));

    }

    /**
     * window is closing
     *
     * @param e closing event
     */
    public void windowCosing(WindowEvent e) {
        CASUALApp.shutdown(0);
    }

    @Override
    public void setScript(Script s) {
    }

    @Override
    public void setCASPAC(Caspac caspac) {
        this.setInformationScrollBorderText("Important Information");
        this.caspac = caspac;
        Statics.guiReady = true;

        log.level2Information(caspac.overview);
        /* if (caspac.build.usePictureForBanner) {
         //setup banner with CASPAC.logo
         }*/
        if (caspac.build.alwaysEnableControls) {
            enableControls(true);
        }
        if (caspac.scripts.size() > 0) {
            for (Script s : caspac.scripts) {
                boolean addScript = true;
                for (int i = 0; i < comboBoxScriptSelector.getItemCount(); i++) {
                    if (comboBoxScriptSelector.getItemAt(i).equals(s.name)) {
                        addScript = false;
                    }
                }
                if (addScript) {
                    this.comboBoxScriptSelector.addItem(s.name);
                }

                log.level4Debug("adding " + s.name + " to UI");
            }
            this.comboBoxScriptSelector.setSelectedItem(caspac.getActiveScript().name);
            log.level2Information(caspac.getScriptByName(this.comboBoxGetSelectedItem()).discription);
        }
        if (comboBoxScriptSelector.getItemCount() < 1) {
            // comboBoxScriptSelector.setVisible(false);
        }
        this.startButton.setText(caspac.build.executeButtonText);
        setWindowBannerText("");
        if (caspac.logo != null && caspac.logo.getMinX() < 40) {
            setWindowBannerImage(caspac.logo, caspac.build.bannerText);
        } else {
            setWindowBannerText(caspac.build.bannerText);
        }
    }
    long time = System.currentTimeMillis();

    @Override
    public void setInformationScrollBorderText(String title) {
        Border b = informationScrollPanel.getBorder();
        ((TitledBorder) b).setTitle(title);
        if (System.currentTimeMillis() > time + 100) {
            repaint();
            time = System.currentTimeMillis();
        }
    }

    public void setVisibile(boolean v) {
        this.setVisible(v);
    }

    @Override
    public void deviceConnected(String mode) {
        setStatusLabelIcon("/GUI/development/resources/images/DeviceConnected.png", "Device Connected");
        setStatusMessageLabel("Target Acquired");
        AudioHandler.playSound("/GUI/development/resources/sounds/Connected-SystemReady.wav");

    }

    @Override
    public void deviceDisconnected() {
        setStatusLabelIcon("/GUI/development/resources/images/DeviceDisconnected.png", "Device Not Detected");
        AudioHandler.playSound("/GUI/development/resources/sounds/Disconnected.wav");
    }

    @Override
    public void deviceMultipleConnected(int numberOfDevicesConnected) {
        setStatusLabelIcon("/GUI/development/resources/images/TooManyDevices.png", "Target Acquired");
        String[] URLs = {"/GUI/development/resources/sounds/" + String.valueOf(numberOfDevicesConnected) + ".wav", "/GUI/development/resources/sounds/DevicesDetected.wav"};
        AudioHandler.playMultipleInputStreams(URLs);
    }

    @Override
    public void notificationPermissionsRequired() {
        AudioHandler.playSound("/GUI/development/resources/sounds/PermissionEscillation.wav");
    }

    @Override
    public void notificationCASUALSound() {
        AudioHandler.playSound("/GUI/development/resources/sounds/CASUAL.wav");
    }

    @Override
    public void notificationInputRequested() {
        AudioHandler.playSound("/GUI/development/resources/sounds/InputRequested.wav");
    }

    @Override
    public void notificationGeneral() {
        AudioHandler.playSound("/GUI/development/resources/sounds/Notification.wav");

    }

    @Override
    public void notificationRequestToContinue() {
        AudioHandler.playSound("/GUI/development/resources/sounds/RequestToContinue.wav");
    }

    @Override
    public void notificationUserActionIsRequired() {
        AudioHandler.playSound("/GUI/development/resources/sounds/UserActionIsRequired.wav");
    }
}
