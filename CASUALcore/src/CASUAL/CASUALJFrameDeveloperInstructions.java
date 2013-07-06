/*CASUALDeveloperInstructions provides a quick reference manual and is usually out of date. 
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
package CASUAL;

import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author adam
 */
public class CASUALJFrameDeveloperInstructions extends javax.swing.JFrame {

    /**
     * Creates new form CASUALDeveloperInstructions
     */
    public CASUALJFrameDeveloperInstructions() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        jTextArea14 = new javax.swing.JTextArea();
        jScrollPane15 = new javax.swing.JScrollPane();
        jTextArea15 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jTextArea12 = new javax.swing.JTextArea();
        jScrollPane16 = new javax.swing.JScrollPane();
        jTextArea16 = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
        jScrollPane18 = new javax.swing.JScrollPane();
        jTextArea18 = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextArea8 = new javax.swing.JTextArea();
        jScrollPane19 = new javax.swing.JScrollPane();
        jTextArea19 = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTextArea10 = new javax.swing.JTextArea();
        jScrollPane20 = new javax.swing.JScrollPane();
        jTextArea20 = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTextArea11 = new javax.swing.JTextArea();
        jScrollPane21 = new javax.swing.JScrollPane();
        jTextArea21 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Developer Instructions");

        jScrollPane14.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Description"));

        jTextArea14.setColumns(20);
        jTextArea14.setLineWrap(true);
        jTextArea14.setRows(5);
        jTextArea14.setText("The -Overview.txt file gives an overview of what this CASUAL is intended to do.  Place your credits, intended device, and initial startup conditions here.");
        jTextArea14.setWrapStyleWord(true);
        jTextArea14.setBorder(null);
        jScrollPane14.setViewportView(jTextArea14);

        jScrollPane15.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Example"));

        jTextArea15.setColumns(20);
        jTextArea15.setRows(5);
        jTextArea15.setText("This application is intended for the :\nSony Ericsson Hero Incredible X S G1 Z 4G+ \nPut your device into develompent mode Settings>Applications>Developer Options....\nThis application does stuff and things.  Select the script you wish to perform. \n");
        jScrollPane15.setViewportView(jTextArea15);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        jTabbedPane1.addTab("-Overview.txt", jPanel1);

        jScrollPane12.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Description"));

        jTextArea12.setColumns(20);
        jTextArea12.setLineWrap(true);
        jTextArea12.setRows(5);
        jTextArea12.setText("The build.properties file will set various project properties such as the title and banner.  When the Window.UsePictureForBanner is set to \"true\", Window.BannerPic will be used for the main banner. Developer.* properties are used to give credit to the developer who created the CASUAL scripts and donation purposes.");
        jTextArea12.setWrapStyleWord(true);
        jTextArea12.setBorder(null);
        jScrollPane12.setViewportView(jTextArea12);

        jScrollPane16.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Example"));

        jTextArea16.setColumns(20);
        jTextArea16.setRows(5);
        jTextArea16.setText("#Developer Name\nDeveloper.Name=Adam Outler\n#Donation link button title\nDeveloper.DonateToButtonText=Adam Outler\n#Link for donate button\nDeveloper.DonateLink=http://my-Link.com\n#This is the window title\nWindow.Title=My CASUAL\n#If true, BannerPic will be used for the main window banner decoration\nWindow.UsePictureForBanner=true\n#The main window banner\nWindow.BannerPic=-logo.png\n#If UsePictureForBanner is false this text will be displayed in large format\nWindow.BannerText=Big Banner Text\n#text for main button\nWindow.ExecuteButtonText=Do It\n#\"true\" or \"True\" to enable\nAudio.Enabled=true \n#Enable Connection/Disconnection control locks\nApplication.AlwaysEnableControls=false;");
        jScrollPane16.setViewportView(jTextArea16);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
                    .addComponent(jScrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("-build.properties", jPanel2);

        jScrollPane6.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Description"));

        jTextArea6.setColumns(20);
        jTextArea6.setLineWrap(true);
        jTextArea6.setRows(5);
        jTextArea6.setText("A Script.scr may be named any name and include spaces.  This will be the name the user sees.  The Script.scr is the master file.  Script.txt supports it with a description and Script.zip   supports it with files.  The Script.scr controls how the selected program operates. Below you will find an example containing all Script.scr commands.");
        jTextArea6.setWrapStyleWord(true);
        jTextArea6.setBorder(null);
        jScrollPane6.setViewportView(jTextArea6);

        jScrollPane18.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Example"));

        jTextArea18.setColumns(20);
        jTextArea18.setRows(5);
        jTextArea18.setText("#This is the CASUAL Test Script.  It tests all functions of CASUAL\n$ECHO Testing ALL CASUAL commands. Connect your device\n$ADB push $ZIPFILEwafaf /asfd\n#$ADB wait-for-device\n$LINUXMAC $ECHO This is a Linux or Mac Computer.\n$LINUXWINDOWS $ECHO This is a Linux or Windows Computer.\n$WINDOWSMAC $ECHO This is a Windows or Mac Computer.\n$LINUX $GOTO #for LiN\n$WINDOWS $GOTO #for MaC\n$MAC $GOTO #for WiN\n\n#for LiNuX\n$ECHO [PASS] LINUX This is a Linux computer.\n$GOTO #THIS IS A COMMEN\n$HALT $ECHO GOTO command failed \n\n\n#for MaC\n$ECHO [PASS] MAC This is a Mac Computer.\n$GOTO #THIS IS A COMMEN\n$HALT $ECHO GOTO command failed \n\n\n#for WiNdOwS\n$ECHO [PASS] WINDOWS This is a Windows Computer.\n$GOTO #THIS IS A COMMEN\n$HALT $ECHO GOTO command failed \n\n\n#THIS IS A COMMENT and it's a target for $GOTO\n$ECHO Testing On\n$ON woot, $GOTO #PASS ON command test\n$ADB shell \"echo woot\"\n$HALT $ECHO GOTO command failed \n\n$HALT $ECHO $ON Failure!\n$GOTO #Done with ON\n\n\n#PASS ON command test\n$ECHO \n#Done with ON\n$ECHO [PASS] done with ON testing.\n\n\n#this is just in case I want to use the word woot again while writing this.\n$CLEARON\n\n$IFCONTAINS foo $INCOMMAND $ADB shell \"echo woot `pwd`\" $DO $HALT $ECHO Failure\n$IFCONTAINS foo $INCOMMAND $ADB shell \"echo foo\" $DO $ECHO [PASS] IFCONTAINS\n$IFNOTCONTAINS woot $INCOMMAND $ADB shell \"echo woot `pwd`\" $DO $HALT $ECHO Failure\n$IFNOTCONTAINS foo $INCOMMAND $ADB shell \"echo woot `pwd`\" $DO $ECHO [PASS] IFNOTCONTAINS\n\n$ECHO This is ZIPFILE reference $ZIPFILE.\n$ADB shell \"echo $ZIPFILE\"\n\n$ECHO This is SLASH reference $SLASH.\n\n$ECHO This is HOMEFOLDER reference.\n$ADB shell \"echo [PASS] $HOMEFOLDER\"\n\n$ECHO Making a folder in HOMEFOLDER called foo.\n$MAKEDIR $HOMEFOLDERfoo\n\n#$ECHO Downloading a file into foo\n#do we need this? $DOWNLOAD android-casual.googlecode.com/svn-history/r348/trunk/GUI/src/CASUAL/AudioHandler.java, $HOMEFOLDERfoo,  a file from CASUAL rep/\n#Same for $EXECUTE\n#$ECHO attempting to execute 'ls' command\n#$EXECUTE ls\n\n\n$ECHO This is a LISTDIR on HOMEFOLDER and you should see something in there.\n$LISTDIR /\n$ECHO USERNOTIFICATION\n$USERNOTIFICATION notification, This is a \\n notification and tests new lines too.\n$ECHO USERCANCELOPTION\n$USERCANCELOPTION want to quit?, If you want to quit\\n hit stop.\n$ECHO ACTIONREQUIRED\n$ACTIONREQUIRED you have to do something dummy,  In order to continue you have do\\n this!\n$ECHO USERINPUTBOX\n$USERINPUTBOX type something, I will repeat what\\nyou type., $ECHO [PASS] $USERINPUT\n$ECHO Waiting for device\nwait-for-device\n$ECHO Rebooting into Download Mode\n$ADB reboot download\n$ECHO You must have a SAMSUNG connected!\n$ECHO Rebooting into ADB Mode mode\n$HEIMDALL close-pc-screen\n$ECHO Waiting for device to automatically reboot\n\n$ECHO Connect a nexus device now.\nwait-for-device\n$ECHO Rebooting into bootloader\n$ADB reboot bootloader\n$FASTBOOT reboot\n$ECHO rebooting into android\n\n\n$HALT $ECHO $HALT [PASS] If your device rebooted twice, ALL TESTS PASSED!!!!!!!!!!!!. ");
        jScrollPane18.setViewportView(jTextArea18);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
                    .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        jTabbedPane1.addTab("Script.scr", jPanel4);

        jScrollPane8.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Description"));

        jTextArea8.setColumns(20);
        jTextArea8.setLineWrap(true);
        jTextArea8.setRows(5);
        jTextArea8.setText("The script.txt file describes the currently selected item from the dropdown box");
        jTextArea8.setWrapStyleWord(true);
        jTextArea8.setBorder(null);
        jScrollPane8.setViewportView(jTextArea8);

        jScrollPane19.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Example"));

        jTextArea19.setColumns(20);
        jTextArea19.setRows(5);
        jTextArea19.setText("This gives the user a helpful message before they click the button. ie...\n  \n Ensure you are rooted or this will fail. Credits to Developer.");
        jScrollPane19.setViewportView(jTextArea19);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane19, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane19, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Script.txt", jPanel5);

        jScrollPane10.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Description"));

        jTextArea10.setColumns(20);
        jTextArea10.setLineWrap(true);
        jTextArea10.setRows(5);
        jTextArea10.setText("The Script.zip should be used to store files to be pushed to a device for Script.scr.  A Script.zip may be referenced by the Script.scr with the command $ZIPFILE.  \n ");
        jTextArea10.setWrapStyleWord(true);
        jTextArea10.setBorder(null);
        jScrollPane10.setViewportView(jTextArea10);

        jScrollPane20.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Example"));

        jTextArea20.setColumns(20);
        jTextArea20.setRows(5);
        jTextArea20.setText("EG.  $ZIPFILE  refers to the root of the zip file. \nEG.  $ZIPFILEMyFolder$SLASH refers to the folder called \"MyFolder\" in the zipfile.");
        jScrollPane20.setViewportView(jTextArea20);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
                    .addComponent(jScrollPane20, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane20, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Script.zip", jPanel6);

        jScrollPane11.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Description"));

        jTextArea11.setColumns(20);
        jTextArea11.setLineWrap(true);
        jTextArea11.setRows(5);
        jTextArea11.setText("A CASUAL meta is used for killswitch and auto-update. this must be present upon deployment of CASUAL in order to make use of it.  Meta consists of the following:\nCASUAL.MinSVN must be higher than CASUAL's SVN or killswitch will be active and user will be brought to the SupportURL page.\nScript.ID is used to identify the proper script in the online update repo.\nScript.Revision must be equal to or higher than the web version or update will occur automatically.\nScript.UpdateMessage/Script.KillSwitchMessage will be displayed to the user when update or killswitch occurs \nScript.MD5sum Instructs CASUAL of the filename to download and perform an MD5.  MD5s are updated automatically in the development IDE after initial addition.  MD5s are optional and irrelevant if not present.");
        jTextArea11.setWrapStyleWord(true);
        jTextArea11.setBorder(null);
        jScrollPane11.setViewportView(jTextArea11);

        jScrollPane21.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Example"));

        jTextArea21.setColumns(20);
        jTextArea21.setRows(5);
        jTextArea21.setText("CASUAL.minSVN=600\nScript.Revision=50\nScript.ID=My Unique ID For This Script\nScript.SupportURL=http://xda-developers.com\nScript.UpdateMessage=Update: I incremented teh revision\nScript.KillSwitchMessage=this has been killed because I say so\nScript.MD5[0]=96e59ab98dc651b6d4b41e4dbca25fe9  TestScript.scr\nScript.MD5[1]=414ac6d725a4048c591f1aeefdc48591  TestScript.zip\nScript.MD5[2]=45f8b252c6872be9ba3c3f462559ec87  TestScript.txt\n");
        jScrollPane21.setViewportView(jTextArea21);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
                    .addComponent(jScrollPane21, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane21, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Script.meta", jPanel7);

        jLabel1.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel1.setText("Building your own CASUAL application");

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("A CASUAL App is composed of a build.prop, an Overview.txt , and a adb_usb.ini. Each CASUAL app contains one or more CASUAL scripts.  A script  consists of a script(.scr), a script description(.txt), and a script resources file(.zip).  All files are located in the /SCRIPTS/ folder inside the jar.   A Jar file can be edited by renaming it to a .zip and using any compression tool.");
        jTextArea1.setWrapStyleWord(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jTextArea1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextArea1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * starts the developer instructions frame.
     */
    public static void main() {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException ex) {
                    new Log().errorHandler(ex);
                } catch (InstantiationException ex) {
                    new Log().errorHandler(ex);
                } catch (IllegalAccessException ex) {
                    new Log().errorHandler(ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    new Log().errorHandler(ex);
                }
                break;
            }
        }

        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CASUALJFrameDeveloperInstructions().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea10;
    private javax.swing.JTextArea jTextArea11;
    private javax.swing.JTextArea jTextArea12;
    private javax.swing.JTextArea jTextArea14;
    private javax.swing.JTextArea jTextArea15;
    private javax.swing.JTextArea jTextArea16;
    private javax.swing.JTextArea jTextArea18;
    private javax.swing.JTextArea jTextArea19;
    private javax.swing.JTextArea jTextArea20;
    private javax.swing.JTextArea jTextArea21;
    private javax.swing.JTextArea jTextArea6;
    private javax.swing.JTextArea jTextArea8;
    // End of variables declaration//GEN-END:variables
}
