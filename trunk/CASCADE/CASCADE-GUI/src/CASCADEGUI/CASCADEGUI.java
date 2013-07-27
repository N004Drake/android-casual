/*CASCADEGUI is CASUAL's Automated Scripting Action Development Environment GUI
 *Copyright (C) 2013  Adam Outler & Logan Ludington
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
/**
 *
 * @author loganludington
 */
package CASCADEGUI;
//Dependencies must be built or there will be errors.. Build the project first.
import CASUAL.crypto.AES128Handler;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.Statics;
import CASUAL.StringOperations;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import Packager.PackagerMain;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import java.awt.datatransfer.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class CASCADEGUI extends javax.swing.JFrame {

    /**
     * Creates new form mainWindow
     */
    private Log log = new Log();
    //Stores the list of current include files. NOTE: only for current script
    private DefaultListModel<File> fileList = new DefaultListModel<>();
    /*Stores the list of the current scripts
     * Will only store in memory untill caspacme is pressed.
     */
    private DefaultListModel<Script> scriptList = new DefaultListModel<>();
    //Used to keep track of currently selected script
    //int currentScriptIndex = -1;
    DefaultListModel listModel = new DefaultListModel();
    private boolean dropEventEnable = false;
    private String slash = System.getProperty("file.separator");
    private Caspac cp;
    BufferedImage logo;

    /**
     * initializes window
     */
    public CASCADEGUI() {
        initComponents();
        this.setLocationRelativeTo(null); //Centers Container to Screen
        //this.resourcesForScriptList.setDropTarget(dt);
        jList1.setModel(listModel);
        listModel.addElement("Drop files here and click to remove.");
        this.jList1.setDropTarget(dropTargetForFileList);
        this.caspacOutputFile.setDropTarget(caspacDropTarget);
        disableAll();
        scriptList.addListDataListener(new scriptListener());
    }
    DropTarget dropTargetForFileList = new DropTarget() {
        @Override
        public synchronized void drop(DropTargetDropEvent event) {
            if (!dropEventEnable) {
                return;
            }
            // Accept copy drops
            event.acceptDrop(DnDConstants.ACTION_COPY);

            // Get the transfer which can provide the dropped item data
            Transferable transferable = event.getTransferable();

            // Get the data formats of the dropped item
            DataFlavor[] flavors = transferable.getTransferDataFlavors();
            // Loop through the flavors
            for (DataFlavor flavor : flavors) {
                // If the item is a file
                if (flavor.isFlavorJavaFileListType()) {
                    List<File> files;
                    try {  //get a list of the files and add them
                        if (!listModel.isEmpty() && listModel.get(0).equals("Drop files here and click to remove.")) {
                            listModel.remove(0);
                        }
                        files = (List<File>) transferable.getTransferData(flavor);
                        for (File f : files) {
                            String file = f.getCanonicalPath();
                            scriptList.getElementAt(scriptListJList.getSelectedIndex()).individualFiles.add(f);
                            listModel.addElement(file);
                        }

                    } catch (IOException | UnsupportedFlavorException ex) {
                        Logger.getLogger(CASCADEGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            Point point = event.getLocation();
            System.out.println(point + "drop event");
            // handle drop inside current table
        }
    };
    DropTarget caspacDropTarget = new DropTarget() {
        @Override
        public synchronized void drop(DropTargetDropEvent event) {
            //Accept copy drops
            event.acceptDrop(DnDConstants.ACTION_COPY);
            // Get the transfer which can provide the dropped item data
            Transferable transferable = event.getTransferable();
            // Get the data formats of the dropped item
            DataFlavor[] flavors = transferable.getTransferDataFlavors();
            // Loop through the flavors
            for (DataFlavor flavor : flavors) {
                // If the item is a file
                if (flavor.isFlavorJavaFileListType()) {
                    List<File> files;
                    try {  //get a list of the files and add them
                        files = (List<File>) transferable.getTransferData(flavor);
                        File firstFile = files.get(0);
                        if (firstFile.isDirectory()) {
                            String newFile = firstFile.getCanonicalPath();
                            newFile = newFile + Statics.Slash + "newCaspac.zip";
                            caspacOutputFile.setText(newFile);
                        } else if (firstFile.isFile() && firstFile.exists()) {
                            caspacOutputFile.setText(files.get(0).getCanonicalPath());
                            loadCaspacActionPerformed(null);
                        }


                    } catch (IOException | UnsupportedFlavorException ex) {
                        Logger.getLogger(CASCADEGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            Point point = event.getLocation();
            System.out.println(point + "drop event");
            // handle drop inside current table
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BannerPicOrText = new javax.swing.ButtonGroup();
        popupMenuForZipManagement = new javax.swing.JPopupMenu();
        removeZipFile = new javax.swing.JMenuItem();
        mainPanel = new javax.swing.JPanel();
        outputFIle = new javax.swing.JPanel();
        caspacOutputFile = new javax.swing.JTextField();
        makeCASPAC = new javax.swing.JButton();
        loadButton = new javax.swing.JButton();
        caspacOutputBrowseButton = new javax.swing.JButton();
        useEncryption = new javax.swing.JCheckBox();
        CASPACkagerPanel = new javax.swing.JPanel();
        casualOutputFile = new javax.swing.JTextField();
        makeItCasualButton = new javax.swing.JButton();
        casualOutputBrowseButton = new javax.swing.JButton();
        typeCheckBox = new javax.swing.JCheckBox();
        typeTextBox = new javax.swing.JTextField();
        workArea = new javax.swing.JTabbedPane();
        scriptGroup = new javax.swing.JTabbedPane();
        scriptOverview = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        scriptNameTitleJLabel = new javax.swing.JLabel();
        minSVNversionTitleJLabel = new javax.swing.JLabel();
        scriptRevisionTitleJLabel = new javax.swing.JLabel();
        uniqueIDTitleJLabel = new javax.swing.JLabel();
        supportURLTitleJLabel = new javax.swing.JLabel();
        updateMessageTitleJLabel = new javax.swing.JLabel();
        killswitchMessageTitleJLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        scriptDescriptionJText = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        scriptNameJLabel = new javax.swing.JLabel();
        minSVNversion = new javax.swing.JTextField();
        scriptRevision = new javax.swing.JTextField();
        uniqueID = new javax.swing.JTextField();
        supportURL = new javax.swing.JTextField();
        updateMessage = new javax.swing.JTextField();
        killswitchMessage = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        scriptListJList = new javax.swing.JList();
        deleteScriptButton = new javax.swing.JButton();
        addScriptButton = new javax.swing.JButton();
        editScriptNameButton = new javax.swing.JButton();
        script = new javax.swing.JPanel();
        scriptText = new javax.swing.JScrollPane();
        scriptWorkArea = new javax.swing.JTextArea();
        overviewScrollPane = new javax.swing.JScrollPane();
        overviewWorkArea = new javax.swing.JTextArea();
        buildPropertiesPanel = new javax.swing.JPanel();
        donationPanel = new javax.swing.JPanel();
        dontateTextPanel = new javax.swing.JPanel();
        donateText = new javax.swing.JTextField();
        dontateLinkPanel = new javax.swing.JPanel();
        donateLink = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        alwaysEnableControls = new javax.swing.JCheckBox();
        audioEnabled = new javax.swing.JCheckBox();
        useBannerText = new javax.swing.JRadioButton();
        useBannerPic = new javax.swing.JRadioButton();
        windowTitlePanel = new javax.swing.JPanel();
        windowText = new javax.swing.JTextField();
        buttonTextPanel = new javax.swing.JPanel();
        buttonText = new javax.swing.JTextField();
        bannerTextPanel = new javax.swing.JPanel();
        bannerText = new javax.swing.JTextField();
        bannerPicPanel = new javax.swing.JPanel();
        bannerPic = new javax.swing.JTextField();
        browseLogo = new javax.swing.JButton();
        developerNamePanel = new javax.swing.JPanel();
        developerName = new javax.swing.JTextField();
        logoLabel = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("CASCADEGUI/resources/Bundle"); // NOI18N
        popupMenuForZipManagement.setLabel(bundle.getString("CASCADEGUI.popupMenuForZipManagement.label")); // NOI18N
        popupMenuForZipManagement.setName("popupMenuForZipManagement"); // NOI18N

        removeZipFile.setText(bundle.getString("CASCADEGUI.removeZipFile.text")); // NOI18N
        removeZipFile.setName("removeZipFile"); // NOI18N
        removeZipFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeZipFileActionPerformed(evt);
            }
        });
        popupMenuForZipManagement.add(removeZipFile);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.mainPanel.border.title"))); // NOI18N
        mainPanel.setName("mainPanel"); // NOI18N

        outputFIle.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.outputFIle.border.title"))); // NOI18N
        outputFIle.setName("outputFIle"); // NOI18N

        caspacOutputFile.setText(System.getProperty("user.dir"));
        caspacOutputFile.setName("caspacOutputFile"); // NOI18N

        makeCASPAC.setText(bundle.getString("CASCADEGUI.makeCASPAC.text")); // NOI18N
        makeCASPAC.setEnabled(false);
        makeCASPAC.setName("makeCASPAC"); // NOI18N
        makeCASPAC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeCASPACActionPerformed(evt);
            }
        });

        loadButton.setText(bundle.getString("CASCADEGUI.loadButton.text")); // NOI18N
        loadButton.setName("loadButton"); // NOI18N
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadCaspacActionPerformed(evt);
            }
        });

        caspacOutputBrowseButton.setIcon(UIManager.getIcon("Tree.openIcon"));
        caspacOutputBrowseButton.setText(bundle.getString("CASCADEGUI.caspacOutputBrowseButton.text")); // NOI18N
        caspacOutputBrowseButton.setName("caspacOutputBrowseButton"); // NOI18N
        caspacOutputBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                caspacOutputBrowseButtonActionPerformed(evt);
            }
        });

        useEncryption.setText(bundle.getString("CASCADEGUI.useEncryption.text")); // NOI18N
        useEncryption.setName("useEncryption"); // NOI18N

        javax.swing.GroupLayout outputFIleLayout = new javax.swing.GroupLayout(outputFIle);
        outputFIle.setLayout(outputFIleLayout);
        outputFIleLayout.setHorizontalGroup(
            outputFIleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputFIleLayout.createSequentialGroup()
                .addComponent(loadButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(caspacOutputFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(caspacOutputBrowseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(makeCASPAC, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useEncryption)
                .addGap(12, 12, 12))
        );
        outputFIleLayout.setVerticalGroup(
            outputFIleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputFIleLayout.createSequentialGroup()
                .addGroup(outputFIleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(makeCASPAC, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addGroup(outputFIleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(loadButton)
                        .addComponent(caspacOutputFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(useEncryption)
                    .addComponent(caspacOutputBrowseButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        CASPACkagerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.CASPACkagerPanel.border.title"))); // NOI18N
        CASPACkagerPanel.setToolTipText(bundle.getString("CASCADEGUI.CASPACkagerPanel.toolTipText")); // NOI18N
        CASPACkagerPanel.setEnabled(false);
        CASPACkagerPanel.setName("CASPACkagerPanel"); // NOI18N
        CASPACkagerPanel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                CASPACkagerPanelPropertyChange(evt);
            }
        });

        casualOutputFile.setText(System.getProperty("user.dir"));
        casualOutputFile.setEnabled(CASPACkagerPanel.isEnabled());
        casualOutputFile.setName("casualOutputFile"); // NOI18N

        makeItCasualButton.setText(bundle.getString("CASCADEGUI.makeItCasualButton.text")); // NOI18N
        makeItCasualButton.setEnabled(false);
        makeItCasualButton.setName("makeItCasualButton"); // NOI18N
        makeItCasualButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeItCasualButtonActionPerformed(evt);
            }
        });

        casualOutputBrowseButton.setIcon(UIManager.getIcon("Tree.openIcon"));
        casualOutputBrowseButton.setText(bundle.getString("CASCADEGUI.casualOutputBrowseButton.text")); // NOI18N
        casualOutputBrowseButton.setName("casualOutputBrowseButton"); // NOI18N
        casualOutputBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                casualOutputBrowseButtonActionPerformed(evt);
            }
        });

        typeCheckBox.setText(bundle.getString("CASCADEGUI.typeCheckBox.text")); // NOI18N
        typeCheckBox.setEnabled(false);
        typeCheckBox.setName("typeCheckBox"); // NOI18N
        typeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeCheckBoxActionPerformed(evt);
            }
        });

        typeTextBox.setText(bundle.getString("CASCADEGUI.typeTextBox.text")); // NOI18N
        typeTextBox.setEnabled(false);
        typeTextBox.setName("typeTextBox"); // NOI18N

        javax.swing.GroupLayout CASPACkagerPanelLayout = new javax.swing.GroupLayout(CASPACkagerPanel);
        CASPACkagerPanel.setLayout(CASPACkagerPanelLayout);
        CASPACkagerPanelLayout.setHorizontalGroup(
            CASPACkagerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CASPACkagerPanelLayout.createSequentialGroup()
                .addComponent(casualOutputFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(casualOutputBrowseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typeTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typeCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(makeItCasualButton))
        );
        CASPACkagerPanelLayout.setVerticalGroup(
            CASPACkagerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CASPACkagerPanelLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(CASPACkagerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(casualOutputFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(casualOutputBrowseButton)
                    .addComponent(typeTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeCheckBox)
                    .addComponent(makeItCasualButton))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        workArea.setName(""); // NOI18N

        scriptGroup.setName("scriptGroup"); // NOI18N

        scriptOverview.setName("scriptOverview"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        scriptNameTitleJLabel.setText(bundle.getString("CASCADEGUI.scriptNameTitleJLabel.text")); // NOI18N
        scriptNameTitleJLabel.setName("scriptNameTitleJLabel"); // NOI18N

        minSVNversionTitleJLabel.setText(bundle.getString("CASCADEGUI.minSVNversionTitleJLabel.text")); // NOI18N
        minSVNversionTitleJLabel.setName("minSVNversionTitleJLabel"); // NOI18N

        scriptRevisionTitleJLabel.setText(bundle.getString("CASCADEGUI.scriptRevisionTitleJLabel.text")); // NOI18N
        scriptRevisionTitleJLabel.setMaximumSize(null);
        scriptRevisionTitleJLabel.setMinimumSize(null);
        scriptRevisionTitleJLabel.setName("scriptRevisionTitleJLabel"); // NOI18N

        uniqueIDTitleJLabel.setText(bundle.getString("CASCADEGUI.uniqueIDTitleJLabel.text")); // NOI18N
        uniqueIDTitleJLabel.setName("uniqueIDTitleJLabel"); // NOI18N

        supportURLTitleJLabel.setText(bundle.getString("CASCADEGUI.supportURLTitleJLabel.text")); // NOI18N
        supportURLTitleJLabel.setName("supportURLTitleJLabel"); // NOI18N

        updateMessageTitleJLabel.setText(bundle.getString("CASCADEGUI.updateMessageTitleJLabel.text")); // NOI18N
        updateMessageTitleJLabel.setName("updateMessageTitleJLabel"); // NOI18N

        killswitchMessageTitleJLabel.setText(bundle.getString("CASCADEGUI.killswitchMessageTitleJLabel.text")); // NOI18N
        killswitchMessageTitleJLabel.setName("killswitchMessageTitleJLabel"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        scriptDescriptionJText.setColumns(20);
        scriptDescriptionJText.setLineWrap(true);
        scriptDescriptionJText.setRows(5);
        scriptDescriptionJText.setName("scriptDescriptionJText"); // NOI18N
        scriptDescriptionJText.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                scriptDescriptionJTextCaretUpdate(evt);
            }
        });
        jScrollPane4.setViewportView(scriptDescriptionJText);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.jPanel5.border.title"))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Drop files here" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setName("jList1"); // NOI18N
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jList1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
        );

        scriptNameJLabel.setName("scriptNameJLabel"); // NOI18N

        minSVNversion.setText(bundle.getString("CASCADEGUI.minSVNversion.text")); // NOI18N
        minSVNversion.setName("minSVNversion"); // NOI18N
        minSVNversion.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                minSVNversionCaretUpdate(evt);
            }
        });

        scriptRevision.setText(bundle.getString("CASCADEGUI.scriptRevision.text")); // NOI18N
        scriptRevision.setName("scriptRevision"); // NOI18N
        scriptRevision.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                scriptRevisionCaretUpdate(evt);
            }
        });

        uniqueID.setText(bundle.getString("CASCADEGUI.uniqueID.text")); // NOI18N
        uniqueID.setName("uniqueID"); // NOI18N
        uniqueID.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                uniqueIDCaretUpdate(evt);
            }
        });

        supportURL.setText(bundle.getString("CASCADEGUI.supportURL.text")); // NOI18N
        supportURL.setName("supportURL"); // NOI18N
        supportURL.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                supportURLCaretUpdate(evt);
            }
        });

        updateMessage.setText(bundle.getString("CASCADEGUI.updateMessage.text")); // NOI18N
        updateMessage.setName("updateMessage"); // NOI18N
        updateMessage.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                updateMessageCaretUpdate(evt);
            }
        });

        killswitchMessage.setText(bundle.getString("CASCADEGUI.killswitchMessage.text")); // NOI18N
        killswitchMessage.setName("killswitchMessage"); // NOI18N
        killswitchMessage.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                killswitchMessageCaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(supportURLTitleJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(supportURL))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(scriptNameTitleJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scriptNameJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(minSVNversionTitleJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(minSVNversion))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(scriptRevisionTitleJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scriptRevision))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(uniqueIDTitleJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uniqueID))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(updateMessageTitleJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updateMessage))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(killswitchMessageTitleJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(killswitchMessage)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scriptNameTitleJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scriptNameJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minSVNversionTitleJLabel)
                    .addComponent(minSVNversion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scriptRevisionTitleJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scriptRevision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uniqueIDTitleJLabel)
                    .addComponent(uniqueID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(supportURLTitleJLabel)
                    .addComponent(supportURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateMessageTitleJLabel)
                    .addComponent(updateMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(killswitchMessageTitleJLabel)
                    .addComponent(killswitchMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        scriptListJList.setModel(scriptList);
        scriptListJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scriptListJList.setName("scriptListJList"); // NOI18N
        scriptListJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                scriptListJListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(scriptListJList);

        deleteScriptButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/CASCADEGUI/resources/recyclebin.png"))); // NOI18N
        deleteScriptButton.setName("deleteScriptButton"); // NOI18N
        deleteScriptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteScriptButtonActionPerformed(evt);
            }
        });

        addScriptButton.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        addScriptButton.setText(bundle.getString("CASCADEGUI.addScriptButton.text")); // NOI18N
        addScriptButton.setName("addScriptButton"); // NOI18N
        addScriptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addScriptButtonActionPerformed(evt);
            }
        });

        editScriptNameButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/CASCADEGUI/resources/pencil-icon.png"))); // NOI18N
        editScriptNameButton.setName("editScriptNameButton"); // NOI18N
        editScriptNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editScriptNameButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout scriptOverviewLayout = new javax.swing.GroupLayout(scriptOverview);
        scriptOverview.setLayout(scriptOverviewLayout);
        scriptOverviewLayout.setHorizontalGroup(
            scriptOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scriptOverviewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scriptOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                    .addGroup(scriptOverviewLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(editScriptNameButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteScriptButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addScriptButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        scriptOverviewLayout.setVerticalGroup(
            scriptOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scriptOverviewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scriptOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(scriptOverviewLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(scriptOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(addScriptButton)
                            .addComponent(deleteScriptButton, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editScriptNameButton, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        scriptGroup.addTab(bundle.getString("CASCADEGUI.scriptOverview.TabConstraints.tabTitle"), scriptOverview); // NOI18N

        script.setName("script"); // NOI18N

        scriptText.setName("scriptText"); // NOI18N

        scriptWorkArea.setColumns(20);
        scriptWorkArea.setRows(5);
        scriptWorkArea.setName("scriptWorkArea"); // NOI18N
        scriptWorkArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                scriptWorkAreaCaretUpdate(evt);
            }
        });
        scriptText.setViewportView(scriptWorkArea);

        javax.swing.GroupLayout scriptLayout = new javax.swing.GroupLayout(script);
        script.setLayout(scriptLayout);
        scriptLayout.setHorizontalGroup(
            scriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scriptText, javax.swing.GroupLayout.DEFAULT_SIZE, 1117, Short.MAX_VALUE)
        );
        scriptLayout.setVerticalGroup(
            scriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scriptText, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
        );

        scriptGroup.addTab(bundle.getString("CASCADEGUI.script.TabConstraints.tabTitle"), script); // NOI18N

        workArea.addTab(bundle.getString("CASCADEGUI.scriptGroup.TabConstraints.tabTitle"), scriptGroup); // NOI18N

        overviewScrollPane.setName("overviewScrollPane"); // NOI18N

        overviewWorkArea.setColumns(20);
        overviewWorkArea.setLineWrap(true);
        overviewWorkArea.setRows(5);
        overviewWorkArea.setWrapStyleWord(true);
        overviewWorkArea.setName("overviewWorkArea"); // NOI18N
        overviewWorkArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                overviewWorkAreaCaretUpdate(evt);
            }
        });
        overviewScrollPane.setViewportView(overviewWorkArea);

        workArea.addTab(bundle.getString("CASCADEGUI.overviewScrollPane.TabConstraints.tabTitle"), overviewScrollPane); // NOI18N

        buildPropertiesPanel.setName("buildPropertiesPanel"); // NOI18N

        donationPanel.setName("donationPanel"); // NOI18N

        dontateTextPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.dontateTextPanel.border.title"))); // NOI18N
        dontateTextPanel.setName("dontateTextPanel"); // NOI18N

        donateText.setText(System.getProperty("user.name"));
        donateText.setBorder(null);
        donateText.setName("donateText"); // NOI18N
        donateText.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                donateTextCaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout dontateTextPanelLayout = new javax.swing.GroupLayout(dontateTextPanel);
        dontateTextPanel.setLayout(dontateTextPanelLayout);
        dontateTextPanelLayout.setHorizontalGroup(
            dontateTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dontateTextPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(donateText, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        dontateTextPanelLayout.setVerticalGroup(
            dontateTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dontateTextPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(donateText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dontateLinkPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.dontateLinkPanel.border.title"))); // NOI18N
        dontateLinkPanel.setName("dontateLinkPanel"); // NOI18N

        donateLink.setText(bundle.getString("CASCADEGUI.donateLink.text")); // NOI18N
        donateLink.setBorder(null);
        donateLink.setName("donateLink"); // NOI18N

        javax.swing.GroupLayout dontateLinkPanelLayout = new javax.swing.GroupLayout(dontateLinkPanel);
        dontateLinkPanel.setLayout(dontateLinkPanelLayout);
        dontateLinkPanelLayout.setHorizontalGroup(
            dontateLinkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dontateLinkPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(donateLink, javax.swing.GroupLayout.DEFAULT_SIZE, 799, Short.MAX_VALUE)
                .addContainerGap())
        );
        dontateLinkPanelLayout.setVerticalGroup(
            dontateLinkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dontateLinkPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(donateLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout donationPanelLayout = new javax.swing.GroupLayout(donationPanel);
        donationPanel.setLayout(donationPanelLayout);
        donationPanelLayout.setHorizontalGroup(
            donationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(donationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dontateTextPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dontateLinkPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        donationPanelLayout.setVerticalGroup(
            donationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(donationPanelLayout.createSequentialGroup()
                .addGroup(donationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(dontateTextPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dontateLinkPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jPanel4.setName("jPanel4"); // NOI18N

        alwaysEnableControls.setText(bundle.getString("CASCADEGUI.alwaysEnableControls.text")); // NOI18N
        alwaysEnableControls.setName("alwaysEnableControls"); // NOI18N
        alwaysEnableControls.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                alwaysEnableControlsStateChanged(evt);
            }
        });

        audioEnabled.setSelected(true);
        audioEnabled.setText(bundle.getString("CASCADEGUI.audioEnabled.text")); // NOI18N
        audioEnabled.setName("audioEnabled"); // NOI18N
        audioEnabled.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                audioEnabledStateChanged(evt);
            }
        });

        BannerPicOrText.add(useBannerText);
        useBannerText.setSelected(true);
        useBannerText.setText(bundle.getString("CASCADEGUI.useBannerText.text")); // NOI18N
        useBannerText.setName("useBannerText"); // NOI18N
        useBannerText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useBannerTextActionPerformed(evt);
            }
        });

        BannerPicOrText.add(useBannerPic);
        useBannerPic.setText(bundle.getString("CASCADEGUI.useBannerPic.text")); // NOI18N
        useBannerPic.setName("useBannerPic"); // NOI18N
        useBannerPic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useBannerPicActionPerformed(evt);
            }
        });

        windowTitlePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.windowTitlePanel.border.title"))); // NOI18N
        windowTitlePanel.setName("windowTitlePanel"); // NOI18N

        windowText.setText(bundle.getString("CASCADEGUI.windowText.text")); // NOI18N
        windowText.setBorder(null);
        windowText.setName("windowText"); // NOI18N
        windowText.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                windowTextCaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout windowTitlePanelLayout = new javax.swing.GroupLayout(windowTitlePanel);
        windowTitlePanel.setLayout(windowTitlePanelLayout);
        windowTitlePanelLayout.setHorizontalGroup(
            windowTitlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(windowTitlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(windowText, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        windowTitlePanelLayout.setVerticalGroup(
            windowTitlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(windowTitlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(windowText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        buttonTextPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.buttonTextPanel.border.title"))); // NOI18N
        buttonTextPanel.setName("buttonTextPanel"); // NOI18N

        buttonText.setText(bundle.getString("CASCADEGUI.buttonText.text")); // NOI18N
        buttonText.setBorder(null);
        buttonText.setName("buttonText"); // NOI18N
        buttonText.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buttonTextCaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout buttonTextPanelLayout = new javax.swing.GroupLayout(buttonTextPanel);
        buttonTextPanel.setLayout(buttonTextPanelLayout);
        buttonTextPanelLayout.setHorizontalGroup(
            buttonTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonTextPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonText, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        buttonTextPanelLayout.setVerticalGroup(
            buttonTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonTextPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        bannerTextPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.bannerTextPanel.border.title"))); // NOI18N
        bannerTextPanel.setName("bannerTextPanel"); // NOI18N

        bannerText.setText(bundle.getString("CASCADEGUI.bannerText.text")); // NOI18N
        bannerText.setBorder(null);
        bannerText.setName("bannerText"); // NOI18N
        bannerText.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                bannerTextCaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout bannerTextPanelLayout = new javax.swing.GroupLayout(bannerTextPanel);
        bannerTextPanel.setLayout(bannerTextPanelLayout);
        bannerTextPanelLayout.setHorizontalGroup(
            bannerTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bannerTextPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bannerText, javax.swing.GroupLayout.DEFAULT_SIZE, 863, Short.MAX_VALUE)
                .addContainerGap())
        );
        bannerTextPanelLayout.setVerticalGroup(
            bannerTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bannerTextPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bannerText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        bannerPicPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.bannerPicPanel.border.title"))); // NOI18N
        bannerPicPanel.setName("bannerPicPanel"); // NOI18N

        bannerPic.setBorder(null);
        bannerPic.setEnabled(false);
        bannerPic.setName("bannerPic"); // NOI18N
        bannerPic.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                bannerPicCaretUpdate(evt);
            }
        });

        browseLogo.setIcon(UIManager.getIcon("Tree.openIcon"));
        browseLogo.setText(bundle.getString("CASCADEGUI.browseLogo.text")); // NOI18N
        browseLogo.setName("browseLogo"); // NOI18N
        browseLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseLogoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bannerPicPanelLayout = new javax.swing.GroupLayout(bannerPicPanel);
        bannerPicPanel.setLayout(bannerPicPanelLayout);
        bannerPicPanelLayout.setHorizontalGroup(
            bannerPicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bannerPicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bannerPic, javax.swing.GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browseLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bannerPicPanelLayout.setVerticalGroup(
            bannerPicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bannerPicPanelLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(bannerPicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(bannerPic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseLogo)))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(windowTitlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonTextPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(alwaysEnableControls)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(audioEnabled))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(useBannerPic)
                            .addComponent(useBannerText))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bannerTextPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bannerPicPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(windowTitlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonTextPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(alwaysEnableControls)
                            .addComponent(audioEnabled))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(useBannerText)
                    .addComponent(bannerTextPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(useBannerPic)
                    .addComponent(bannerPicPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56))
        );

        developerNamePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.developerNamePanel.border.title"))); // NOI18N
        developerNamePanel.setName("developerNamePanel"); // NOI18N

        developerName.setText(System.getProperty("user.name"));
        developerName.setBorder(null);
        developerName.setName("developerName"); // NOI18N
        developerName.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                developerNameCaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout developerNamePanelLayout = new javax.swing.GroupLayout(developerNamePanel);
        developerNamePanel.setLayout(developerNamePanelLayout);
        developerNamePanelLayout.setHorizontalGroup(
            developerNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(developerNamePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(developerName)
                .addContainerGap())
        );
        developerNamePanelLayout.setVerticalGroup(
            developerNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(developerNamePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(developerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        logoLabel.setText(bundle.getString("CASCADEGUI.logoLabel.text")); // NOI18N
        logoLabel.setName("logoLabel"); // NOI18N

        javax.swing.GroupLayout buildPropertiesPanelLayout = new javax.swing.GroupLayout(buildPropertiesPanel);
        buildPropertiesPanel.setLayout(buildPropertiesPanelLayout);
        buildPropertiesPanelLayout.setHorizontalGroup(
            buildPropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buildPropertiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buildPropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(donationPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(developerNamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(buildPropertiesPanelLayout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 805, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(214, Short.MAX_VALUE))
        );
        buildPropertiesPanelLayout.setVerticalGroup(
            buildPropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buildPropertiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(developerNamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(donationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addContainerGap())
        );

        workArea.addTab(bundle.getString("CASCADEGUI.buildPropertiesPanel.TabConstraints.tabTitle"), buildPropertiesPanel); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(workArea, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(outputFIle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(CASPACkagerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(workArea)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputFIle, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CASPACkagerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /*
     * Listener set up to listen for the CASPACme button
     */
    private void makeCASPACActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeCASPACActionPerformed
        try {
            //Create new file In memory with name from the JTextField
            File file = new File(this.caspacOutputFile.getText());


            if (!file.toString().endsWith(".zip")) {
                JOptionPane.showMessageDialog(this, "The file: \n"
                        + this.caspacOutputFile.getText() + "\n is not a valid zip file.\n"
                        + "Please make sure that the file ends in a .zip", "File output error",
                        JOptionPane.ERROR_MESSAGE);
                log.level0Error("Output zip file not valid: \n \t" + this.caspacOutputFile.getText());
                return;
            }

            //File overwrite check
            if (file.exists()) {
                log.level2Information("File exist prompting for overwrite");
                int i = JOptionPane.showConfirmDialog(this, "Warning:" + this.caspacOutputFile.getText()
                        + " already exists are you sure you wish to continue.\n Any "
                        + "previous files will be overridden.", "Overwrite existing file?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (i == JOptionPane.NO_OPTION) {
                    log.level0Error("File override declined");
                    return;
                } else {
                    file.delete();
                }
            }

            cp = buildCASPAC();
            log.level2Information("Attempting CASCPAC write");
            try {
                cp.write();
            } catch (IOException ex) {
                log.errorHandler(ex);
            }
            log.level2Information("CASPAC write successfull!!!");
            if (useEncryption.isSelected()) {
                File temp = new File(cp.CASPAC.getAbsolutePath() + ".tmp");
                new FileOperations().copyFile(file, temp);
                new AES128Handler(temp).encrypt(file.getAbsolutePath(), getPassword());
            }

            enableCasual();
        } catch (IOException ex) {
            Logger.getLogger(CASCADEGUI.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_makeCASPACActionPerformed

    private char[] getPassword() {
        char[] password = new char[]{};
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter a password:");
        JPasswordField pass = new JPasswordField(10);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "The title",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[1]);
        if (option == 0) // pressing OK button
        {
            password = pass.getPassword();
            System.out.println("Your password is: " + new String(password));
        }
        return password;
    }

    /*
     * Listener set up to listen for the + button to add script
     */
    private void addScriptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addScriptButtonActionPerformed
        // Prompt for script name
        String s = JOptionPane.showInputDialog(this, "What would you like to name the script:\n",
                "Script Name", JOptionPane.QUESTION_MESSAGE);

        //If there is a name in prompt

        if (checkScriptNameExists(s)) {
            return;
        }
        if (!(s.isEmpty())) {

            scriptList.addElement(new Script(s, Statics.TempFolder + s));

            //Set that script as current script
            this.scriptListJList.setSelectedIndex(scriptList.getSize() - 1);

            //Rerender all of the Info to current script
            loadScript();
            this.scriptListJList.setSelectedIndex(this.scriptListJList.getLastVisibleIndex());
            disableCasual();
        }

    }//GEN-LAST:event_addScriptButtonActionPerformed

    /*
     * Listens for a new selection on the list and then loads that script info
     * into the current displayed information.
     */
    private void scriptListJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_scriptListJListValueChanged
        //this.scriptListJList.getSelectedIndex() = this.scriptListJList.getSelectedIndex();
        //System.out.println(this.scriptListJList.getSelectedIndex());
        if (this.scriptListJList.getSelectedIndex() == -1) {
            this.editScriptNameButton.setEnabled(false);
            this.deleteScriptButton.setEnabled(false);
        } else {
            this.editScriptNameButton.setEnabled(true);
            this.deleteScriptButton.setEnabled(true);
        }
        loadScript();
    }//GEN-LAST:event_scriptListJListValueChanged

    /*
     * Listens for edit icon to be pressed
     */
    private void editScriptNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editScriptNameButtonActionPerformed
        //Prompts for the new string name.
        String scriptName = JOptionPane.showInputDialog(this, "Please enter the new name for the script:\n"
                + "Leave empty to keep the existing name",
                "Script Name", JOptionPane.QUESTION_MESSAGE);

        //Only changes name if input string is not blank
        if (checkScriptNameExists(scriptName)) {
            return;
        }
        if (!(scriptName.isEmpty())) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex());
            loadScript();
            disableCasual();
        }

    }//GEN-LAST:event_editScriptNameButtonActionPerformed

    /*
     * Listener for script delete button
     */
    private void deleteScriptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteScriptButtonActionPerformed
        int i = JOptionPane.showConfirmDialog(this, "Are you sure you wish to delete this script?\n"
                + "THIS IS IRREVERSABLE", "Confirm Deletion:", JOptionPane.WARNING_MESSAGE);
        if (i == JOptionPane.YES_OPTION) {
            if (this.scriptListJList.getSelectedIndex() != -1) {
                scriptList.removeElementAt(this.scriptListJList.getSelectedIndex());
                clearAll();
            }
        }
        disableCasual();
    }//GEN-LAST:event_deleteScriptButtonActionPerformed

    /*
     * Listener to dynamically save the minSVNversion
     */
    private void minSVNversionCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_minSVNversionCaretUpdate
        if (!scriptList.isEmpty() && this.scriptListJList.getSelectedIndex() != -1) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.minSVNversion = (minSVNversion.getText());
        }
        disableCasual();
    }//GEN-LAST:event_minSVNversionCaretUpdate

    /*
     * Listener to dynamically save the scriptRevision
     */
    private void scriptRevisionCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_scriptRevisionCaretUpdate
        if (!scriptList.isEmpty() && this.scriptListJList.getSelectedIndex() != -1) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.scriptRevision = (scriptRevision.getText());
        }
        disableCasual();
    }//GEN-LAST:event_scriptRevisionCaretUpdate

    /*
     * Listener to dynamically save the uniqueId
     */
    private void uniqueIDCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_uniqueIDCaretUpdate
        if (!scriptList.isEmpty() && this.scriptListJList.getSelectedIndex() != -1) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.uniqueIdentifier = uniqueID.getText();
        }
        disableCasual();
    }//GEN-LAST:event_uniqueIDCaretUpdate

    /*
     * Listener to dynamically save the supportURL
     */
    private void supportURLCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_supportURLCaretUpdate
        if (!scriptList.isEmpty() && this.scriptListJList.getSelectedIndex() != -1) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.supportURL = supportURL.getText();
        }
        disableCasual();
    }//GEN-LAST:event_supportURLCaretUpdate

    /*
     * Listener to dynamically save the updateMessage
     */
    private void updateMessageCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_updateMessageCaretUpdate
        if (!scriptList.isEmpty() && this.scriptListJList.getSelectedIndex() != -1) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.updateMessage = updateMessage.getText();
        }
        disableCasual();
    }//GEN-LAST:event_updateMessageCaretUpdate

    /*
     * Listener to dynamically save the killswitchMessage
     */
    private void killswitchMessageCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_killswitchMessageCaretUpdate
        if (!scriptList.isEmpty() && this.scriptListJList.getSelectedIndex() != -1) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.killSwitchMessage = killswitchMessage.getText();
        }
        disableCasual();
    }//GEN-LAST:event_killswitchMessageCaretUpdate

    /*
     * Listener to dynamically save the script
     */
    private void scriptWorkAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_scriptWorkAreaCaretUpdate
        if (!scriptList.isEmpty() && this.scriptListJList.getSelectedIndex() != -1) {

            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).scriptContents = (this.scriptWorkArea.getText());
        }
        disableCasual();
    }//GEN-LAST:event_scriptWorkAreaCaretUpdate

    private void removeZipFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeZipFileActionPerformed
        this.popupMenuForZipManagement.setVisible(false);

        jList1.remove(this.jList1.getSelectedIndex());

    }//GEN-LAST:event_removeZipFileActionPerformed

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        int X = (int) this.jList1.getBounds().getMaxX();

        java.awt.Point point = evt.getPoint();
        int index = jList1.locationToIndex(point);
        if (jList1.isSelectedIndex(index)) {
            listModel.remove(index);
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).individualFiles.remove(index);
            disableCasual();
        }

    }//GEN-LAST:event_jList1MouseClicked

    private void browseLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseLogoActionPerformed
        final JFileChooser fc = new JFileChooser();
        int returnValue = fc.showOpenDialog(this);
        if (returnValue == JFileChooser.OPEN_DIALOG) {
            if (fc.getSelectedFile().toString().contains(".png")) {
                this.bannerPic.setText(fc.getSelectedFile().toString());
                try {
                    logo = ImageIO.read(ImageIO.createImageInputStream(new FileInputStream(fc.getSelectedFile())));
                    logoLabel.setIcon(new ImageIcon(logo));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CASCADEGUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CASCADEGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(this, "File must be a PNG "
                        + "image.", "Incorrect Format", JOptionPane.ERROR_MESSAGE);
            }
        }
        disableCasual();
    }//GEN-LAST:event_browseLogoActionPerformed

    private void loadCaspacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadCaspacActionPerformed
        
        File file = new File(this.caspacOutputFile.getText());
        
        String filename=file.getAbsolutePath();
        if (!file.toString().endsWith(".zip")) {
            JOptionPane.showMessageDialog(this, "The file: \n"
                    + filename + "\n is not a valid zip file.\n"
                    + "Please make sure that the file ends in a .zip", "File read error",
                    JOptionPane.ERROR_MESSAGE);
            log.level0Error("Input zip file not valid: \n \t" + filename);
            return;
        }
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "The file: \n"
                    + filename + "\n is not a valid zip file.\n"
                    + "Ensure the file exists", "File Not Found",
                    JOptionPane.ERROR_MESSAGE);
            log.level0Error("Could not find file at: \n \t" + filename);
            return;
        }
        try {
            if (AES128Handler.getCASPACHeaderLength(file)>20){
                cp = new Caspac(file, Statics.TempFolder, 0,getPassword());
            } else {
                cp = new Caspac(file, Statics.TempFolder, 0);
            }
        } catch (IOException ex) {
            Logger.getLogger(CASCADEGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CASCADEGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            scriptList.clear();
            log.level4Debug("Initiating CASPAC load.");
            cp.load();
            cp.waitForUnzipComplete();
        } catch (ZipException ex) {
            log.errorHandler(ex);
        } catch (IOException ex) {
            log.errorHandler(ex);
        }
        Caspac tempcas;
        tempcas = cp;
        for (Script s : tempcas.scripts) {
            scriptList.addElement(s);

            //Set that script as current script
        }

        this.scriptListJList.setSelectedIndex(scriptList.getSize() - 1);

        //Rerender all of the Info to current script
        loadScript();
        updateBuildAndOverview(cp);
        logoLabel.setIcon(new ImageIcon(logo));
        this.scriptListJList.setSelectedIndex(this.scriptListJList.getLastVisibleIndex());
        enableCasual();



    }//GEN-LAST:event_loadCaspacActionPerformed

    private void caspacOutputBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_caspacOutputBrowseButtonActionPerformed
        JFileChooser jc;
        if (new File(this.caspacOutputFile.getText()).exists()) {
            jc = new JFileChooser(this.caspacOutputFile.getText());
        } else {
            jc = new JFileChooser();
        }
        int returnVal = jc.showSaveDialog(this);
        if (returnVal == JFileChooser.OPEN_DIALOG) {
            if (!jc.getSelectedFile().toString().endsWith(".zip")) {
                this.caspacOutputFile.setText(jc.getSelectedFile().toString() + ".zip");
            } else {
                this.caspacOutputFile.setText(jc.getSelectedFile().toString());
            }
            disableCasual();
        }
    }//GEN-LAST:event_caspacOutputBrowseButtonActionPerformed

    private void casualOutputBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_casualOutputBrowseButtonActionPerformed
        JFileChooser jc;
        if (new File(this.casualOutputFile.getText()).exists()) {
            jc = new JFileChooser(this.casualOutputFile.getText());
        } else {
            jc = new JFileChooser();
        }
        jc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = jc.showOpenDialog(this);
        if (returnVal == JFileChooser.OPEN_DIALOG) {
            if (!jc.getSelectedFile().toString().endsWith(".jar")) {
                this.casualOutputFile.setText(jc.getSelectedFile().toString() + ".jar");
            } else {
                this.casualOutputFile.setText(jc.getSelectedFile().toString());
            }
        }
    }//GEN-LAST:event_casualOutputBrowseButtonActionPerformed

    private void makeItCasualButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeItCasualButtonActionPerformed
        File caspacin = new File(this.caspacOutputFile.getText());
        File casualout = new File(this.casualOutputFile.getText());
        if (!caspacin.toString().endsWith(".zip")) {
            JOptionPane.showMessageDialog(this, "The file: \n"
                    + this.caspacOutputFile.getText() + "\n is not a valid CASPAC file.\n"
                    + "Please make sure that the file ends in a .zip, and is a valid CASPAC", "File read error",
                    JOptionPane.ERROR_MESSAGE);
            log.level0Error("Input CASPAC file not valid: \n \t" + this.caspacOutputFile.getText());
            return;
        }
        if (!caspacin.exists()) {
            JOptionPane.showMessageDialog(this, "The file: \n"
                    + this.caspacOutputFile.getText() + "\n is not a valid CASPAC file.\n"
                    + "Ensure the CASPAC exists.", "File Not Found",
                    JOptionPane.ERROR_MESSAGE);
            log.level0Error("Input CASPAC file not found: \n \t" + this.caspacOutputFile.getText());
            return;
        }

        if (casualout.isFile()) {
            JOptionPane.showMessageDialog(this, "ERROR:" + this.caspacOutputFile.getText()
                    + " is a file, and must be a folder to place the pregenerated"
                    + "named file into.\n Please select a valid ouput folder and try again.", "Output should be directory",
                    JOptionPane.ERROR);


        }


        String[] args = argBuilder();
        PackagerMain.main(args);
    }//GEN-LAST:event_makeItCasualButtonActionPerformed

    private void typeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeCheckBoxActionPerformed
        if (this.typeCheckBox.isSelected()) {
            this.typeTextBox.setEnabled(true);
            this.CASPACkagerPanel.setEnabled(true);
        } else {
            this.typeTextBox.setEnabled(false);
        }
    }//GEN-LAST:event_typeCheckBoxActionPerformed

    private void CASPACkagerPanelPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_CASPACkagerPanelPropertyChange
        if (this.CASPACkagerPanel.isEnabled()) {
            enableCasualComponents();
        } else {
            disableCasualComponents();
        }
    }//GEN-LAST:event_CASPACkagerPanelPropertyChange

    private void scriptDescriptionJTextCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_scriptDescriptionJTextCaretUpdate
        if (!scriptList.isEmpty() && this.scriptListJList.getSelectedIndex() != -1) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).discription = this.scriptDescriptionJText.getText();
        }
        disableCasual();
    }//GEN-LAST:event_scriptDescriptionJTextCaretUpdate

    private void overviewWorkAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_overviewWorkAreaCaretUpdate
        disableCasual();
    }//GEN-LAST:event_overviewWorkAreaCaretUpdate

    private void developerNameCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_developerNameCaretUpdate
        disableCasual();
    }//GEN-LAST:event_developerNameCaretUpdate

    private void donateTextCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_donateTextCaretUpdate
        disableCasual();
    }//GEN-LAST:event_donateTextCaretUpdate

    private void windowTextCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_windowTextCaretUpdate
        disableCasual();
    }//GEN-LAST:event_windowTextCaretUpdate

    private void buttonTextCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buttonTextCaretUpdate
        disableCasual();
    }//GEN-LAST:event_buttonTextCaretUpdate

    private void alwaysEnableControlsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_alwaysEnableControlsStateChanged
        disableCasual();
    }//GEN-LAST:event_alwaysEnableControlsStateChanged

    private void audioEnabledStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_audioEnabledStateChanged
        disableCasual();
    }//GEN-LAST:event_audioEnabledStateChanged

    private void useBannerPicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useBannerPicActionPerformed
        disableCasual();
    }//GEN-LAST:event_useBannerPicActionPerformed

    private void useBannerTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useBannerTextActionPerformed
        disableCasual();
    }//GEN-LAST:event_useBannerTextActionPerformed

    private void bannerTextCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_bannerTextCaretUpdate
        disableCasual();
    }//GEN-LAST:event_bannerTextCaretUpdate

    private void bannerPicCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_bannerPicCaretUpdate
        disableCasual();
    }//GEN-LAST:event_bannerPicCaretUpdate

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("System".equals(info.getName()) || info.getName().toLowerCase().contains(System.getProperty("os.name").toLowerCase().subSequence(0, 3))) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CASCADEGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CASCADEGUI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup BannerPicOrText;
    private javax.swing.JPanel CASPACkagerPanel;
    private javax.swing.JButton addScriptButton;
    private javax.swing.JCheckBox alwaysEnableControls;
    private javax.swing.JCheckBox audioEnabled;
    private javax.swing.JTextField bannerPic;
    private javax.swing.JPanel bannerPicPanel;
    private javax.swing.JTextField bannerText;
    private javax.swing.JPanel bannerTextPanel;
    private javax.swing.JButton browseLogo;
    private javax.swing.JPanel buildPropertiesPanel;
    private javax.swing.JTextField buttonText;
    private javax.swing.JPanel buttonTextPanel;
    private javax.swing.JButton caspacOutputBrowseButton;
    private javax.swing.JTextField caspacOutputFile;
    private javax.swing.JButton casualOutputBrowseButton;
    private javax.swing.JTextField casualOutputFile;
    private javax.swing.JButton deleteScriptButton;
    private javax.swing.JTextField developerName;
    private javax.swing.JPanel developerNamePanel;
    private javax.swing.JTextField donateLink;
    private javax.swing.JTextField donateText;
    private javax.swing.JPanel donationPanel;
    private javax.swing.JPanel dontateLinkPanel;
    private javax.swing.JPanel dontateTextPanel;
    private javax.swing.JButton editScriptNameButton;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField killswitchMessage;
    private javax.swing.JLabel killswitchMessageTitleJLabel;
    private javax.swing.JButton loadButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton makeCASPAC;
    private javax.swing.JButton makeItCasualButton;
    private javax.swing.JTextField minSVNversion;
    private javax.swing.JLabel minSVNversionTitleJLabel;
    private javax.swing.JPanel outputFIle;
    private javax.swing.JScrollPane overviewScrollPane;
    private javax.swing.JTextArea overviewWorkArea;
    private javax.swing.JPopupMenu popupMenuForZipManagement;
    private javax.swing.JMenuItem removeZipFile;
    private javax.swing.JPanel script;
    private javax.swing.JTextArea scriptDescriptionJText;
    private javax.swing.JTabbedPane scriptGroup;
    private javax.swing.JList scriptListJList;
    private javax.swing.JLabel scriptNameJLabel;
    private javax.swing.JLabel scriptNameTitleJLabel;
    private javax.swing.JPanel scriptOverview;
    private javax.swing.JTextField scriptRevision;
    private javax.swing.JLabel scriptRevisionTitleJLabel;
    private javax.swing.JScrollPane scriptText;
    private javax.swing.JTextArea scriptWorkArea;
    private javax.swing.JTextField supportURL;
    private javax.swing.JLabel supportURLTitleJLabel;
    private javax.swing.JCheckBox typeCheckBox;
    private javax.swing.JTextField typeTextBox;
    private javax.swing.JTextField uniqueID;
    private javax.swing.JLabel uniqueIDTitleJLabel;
    private javax.swing.JTextField updateMessage;
    private javax.swing.JLabel updateMessageTitleJLabel;
    private javax.swing.JRadioButton useBannerPic;
    private javax.swing.JRadioButton useBannerText;
    private javax.swing.JCheckBox useEncryption;
    private javax.swing.JTextField windowText;
    private javax.swing.JPanel windowTitlePanel;
    private javax.swing.JTabbedPane workArea;
    // End of variables declaration//GEN-END:variables


    /*
     * Called to remove files from fileList
     */
    private void removeFiles(int[] indexList) {

        /*
         * The order of operations is to remove the first ones one the list first
         * problem is if you remove 5 before 6 then 6 will no longer exist as 
         * it is 5 so therefore list must be sorted.
         */
        List<Integer> list = new ArrayList<>();


        for (int i : indexList) {
            list.add(i);
        }
        //Sorts from least to greatest
        Collections.sort(list);

        //Reverses sort
        Collections.reverse(list);

        for (int i : list) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).individualFiles.remove(i);
            fileList.remove(i);
        }

    }

    private Properties buildMaker() {
        Properties buildProp = new Properties();
        buildProp.setProperty("Audio.Enabled", this.audioEnabled.isSelected() ? "True" : "False");
        buildProp.setProperty("Application.AlwaysEnableControls", this.alwaysEnableControls.isSelected() ? "True" : "False");
        buildProp.setProperty("Window.UsePictureForBanner", this.useBannerPic.isSelected() ? "True" : "False");
        buildProp.setProperty("Developer.DonateToButtonText", this.donateText.getText());
        buildProp.setProperty("Developer.DonateLink", this.donateLink.getText());
        buildProp.setProperty("Developer.Name", this.developerName.getText());
        buildProp.setProperty("Window.ExecuteButtonText", this.buttonText.getText());
        buildProp.setProperty("Window.BannerText", this.bannerText.getText());
        buildProp.setProperty("Window.BannerPic", this.bannerPic.getText());
        buildProp.setProperty("Window.Title", this.windowText.getText());
        return buildProp;
    }

    private Caspac buildCASPAC() throws IOException {
        log.level2Information("Creating CASPAC file");
        Caspac casp = new Caspac(new File(this.caspacOutputFile.getText()), Statics.TempFolder, 0);
        log.level2Information("Setting CASPAC build");
        casp.setBuild(buildMaker());
        casp.logo = logo;
        log.level2Information("Adding scripts from memory to CASPAC");
        for (int j = 0; j < scriptList.getSize(); j++) {
            casp.scripts.add(scriptList.get(j));
        }
        log.level2Information("Setting Overview");
        casp.overview = this.overviewWorkArea.getText();
        return casp;
    }

    /*
     * SAVES ALL ELEMENTS OF SCRIPT
     * then clears all fields then
     * LOADS ALL ELEMENTS OF NEW SCRIPT
     */
    private void loadScript() {
        if (scriptList.isEmpty()) {
            return;
        }
        if (this.scriptListJList.getSelectedIndex() == -1) {
            return;
        }
        this.scriptNameJLabel.setText(scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).name);
        if (scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).scriptContents.isEmpty()) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).scriptContents = "#Enter CASUAL commands here";
        }

        this.scriptWorkArea.setText(scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).scriptContents);


        if (scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).discription.isEmpty()) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).discription = "Describe your script here";
        }
        this.scriptDescriptionJText.setText(scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).discription);


        if (scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.killSwitchMessage.isEmpty()) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.killSwitchMessage = "CASUAL cannot continue. The SVN version is too low";
        }
        this.killswitchMessage.setText(scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.killSwitchMessage);


        if (scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.minSVNversion.isEmpty()) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.minSVNversion = (CASUAL.CASPACData.getSVNRevision());
        }
        this.minSVNversion.setText(scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.minSVNversion);


        if (scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.scriptRevision.isEmpty()) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.scriptRevision = "0";
        }
        this.scriptRevision.setText(scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.scriptRevision);


        if (scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.supportURL.isEmpty()) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.supportURL = "http://xda-developers.com";
        }
        this.supportURL.setText(scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.supportURL);


        if (scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.uniqueIdentifier.isEmpty()) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.uniqueIdentifier = ("Unique Update ID " + StringOperations.generateRandomHexString(8));
        }
        this.uniqueID.setText(scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.uniqueIdentifier);

        if (scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.updateMessage.isEmpty()) {
            scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.updateMessage = ("Inital Release.");
        }
        this.updateMessage.setText(scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).metaData.updateMessage);

        listModel.removeAllElements();
        for (File f : scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).individualFiles) {
            listModel.addElement(f);
        }

    }

    private void enableCasual() {
        this.CASPACkagerPanel.setEnabled(true);
    }

    private void disableCasual() {
        this.CASPACkagerPanel.setEnabled(false);
    }

    private void enableCasualComponents() {
        this.casualOutputFile.setEnabled(true);
        this.casualOutputBrowseButton.setEnabled(true);
        this.typeCheckBox.setEnabled(true);
        this.makeItCasualButton.setEnabled(true);
    }

    private void disableCasualComponents() {
        this.casualOutputFile.setEnabled(false);
        this.casualOutputBrowseButton.setEnabled(false);
        this.typeCheckBox.setEnabled(false);
        this.makeItCasualButton.setEnabled(false);
    }
    /*
     * Disables those items that can not be used if there is no loaded script
     * or if there are no scripts
     */

    private void disableAll() {
        this.scriptNameJLabel.setEnabled(false);
        this.scriptDescriptionJText.setEnabled(false);
        this.killswitchMessage.setEnabled(false);
        this.minSVNversion.setEnabled(false);
        this.scriptRevision.setEnabled(false);
        this.supportURL.setEnabled(false);
        this.uniqueID.setEnabled(false);
        this.updateMessage.setEnabled(false);
        this.scriptWorkArea.setEnabled(false);
        this.editScriptNameButton.setEnabled(false);
        this.deleteScriptButton.setEnabled(false);
        this.jList1.setEnabled(false);
        this.dropEventEnable = false;
    }

    /*
     * Enables those items that can  be used if there is a loaded script
     */
    private void enableAll() {
        this.scriptNameJLabel.setEnabled(true);
        this.scriptDescriptionJText.setEnabled(true);
        this.killswitchMessage.setEnabled(true);
        this.minSVNversion.setEnabled(true);
        this.scriptRevision.setEnabled(true);
        this.supportURL.setEnabled(true);
        this.uniqueID.setEnabled(true);
        this.updateMessage.setEnabled(true);
        this.scriptWorkArea.setEnabled(true);
        this.editScriptNameButton.setEnabled(true);
        this.deleteScriptButton.setEnabled(true);
        this.jList1.setEnabled(true);
        this.dropEventEnable = true;

    }

    private void clearAll() {
        this.scriptNameJLabel.setText("");
        this.scriptDescriptionJText.setText("");
        this.killswitchMessage.setText("");
        this.minSVNversion.setText("");
        this.scriptRevision.setText("");
        this.supportURL.setText("");
        this.uniqueID.setText("");
        this.updateMessage.setText("");
        this.scriptWorkArea.setText("");
        this.editScriptNameButton.setText("");
        this.deleteScriptButton.setText("");
    }

    private String[] argBuilder() {
        String[] args;
        String CASPACIn = this.caspacOutputFile.getText();
        String CASUALOut = this.casualOutputFile.getText();
        if (this.typeCheckBox.isSelected()) {
            args = new String[]{"--CASPAC", CASPACIn, "--output", CASUALOut, "--type", this.typeTextBox.getText()};
        } else {
            args = new String[]{"--CASPAC", CASPACIn, "--output", CASUALOut};
        }
        return args;


    }

    private boolean checkScriptNameExists(String testName) {
        for (int i = 0; i < scriptList.getSize(); i++) {
            if (scriptList.get(i).name.equals(testName)) {
                log.level0Error("The script \"" + testName + "\" already exists");
                JOptionPane.showMessageDialog(this, "The script \"" + testName + "\" already exists",
                        "Script Alreay Exists", JOptionPane.ERROR_MESSAGE);
                return true;
            }
        }
        return false;
    }

    private void updateBuildAndOverview(Caspac cp) {
        this.developerName.setText(cp.build.developerName);
        this.donateLink.setText(cp.build.donateLink);
        this.donateText.setText(cp.build.developerDonateButtonText);
        this.windowText.setText(cp.build.windowTitle);
        this.buttonText.setText(cp.build.executeButtonText);
        this.audioEnabled.setSelected(cp.build.audioEnabled);
        this.alwaysEnableControls.setSelected(cp.build.alwaysEnableControls);
        this.useBannerPic.setSelected(cp.build.usePictureForBanner);
        this.bannerPic.setText(cp.build.bannerPic);
        this.bannerText.setText(cp.build.bannerText);
        this.overviewWorkArea.setText(cp.overview);
        this.logo = cp.logo;
    }

    /*
     * Listener that enables and disables script elements based on the existance
     * of scripts in list. Whenever a script is added or deleted it will check 
     * to see if the list is empty. If its empty then it disables all to prevent
     * from null pointer exeptions.
     */
    private class scriptListener implements ListDataListener {

        @Override
        public void contentsChanged(ListDataEvent evt) {
        }

        @Override
        public void intervalRemoved(ListDataEvent evt) {
            if (scriptList.isEmpty()) {
                disableAll();
                clearAll();
                makeCASPAC.setEnabled(false);
            }
        }

        @Override
        public void intervalAdded(ListDataEvent evt) {
            if (!scriptList.isEmpty()) {
                enableAll();
                makeCASPAC.setEnabled(true);
            }
        }
    }
}
