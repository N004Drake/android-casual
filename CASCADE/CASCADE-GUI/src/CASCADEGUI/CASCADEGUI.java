/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASCADEGUI;

import CASUAL.caspac.Caspac;
import CASUAL.Zip;
import CASUAL.caspac.Script;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.xml.bind.DatatypeConverter;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.datatransfer.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author loganludington
 */
public class CASCADEGUI extends javax.swing.JFrame {

    private static final String trashIcon ="89504e470d0a1a0a0000000d4948445200000010000000100804000000b5fa37ea000000097048597300000b1300000b1301009a9c18000003186943435050686f746f73686f70204943432070726f66696c65000078da6360609ee0e8e2e4ca24c0c050505452e41ee418191119a5c07e9e818d819981818181812131b9b8c03120c087818181212f3f2f9501153032307cbbc6c0c8c0c0c07059d7d1c5c9958134c09a5c5054c2c0c070808181c12825b538998181e10b0303437a794941090303630c03038348527641090303630103038348764890330303630b0303134f496a450903030383737e416551667a468982a1a5a5a582634a7e52aa42706571496a6eb182675e727e51417e5162496a0a030303d40e060606065e97fc1205f7c4cc3c05230355062a8388c82805080b113e083104482e2d2a83072503830083028301830343004322433dc30286a30c6f18c5195d184b195730de6312630a629ac07481599839927921f31b164b960e965bac7aacadacf7d82cd9a6b17d630f67dfcda1c4d1c5f1853391f3029723d7166e4dee053c523c5379857827f109f34de397e15f2ca023b043d055f08a50aad00fe15e111591bda2e1a25fc426891b895f91a89094933c26952f2d2d7d42a64c565df6965c9fbc8bfc1f85ad8a854a7a4a6f95d7aa14a89aa8fe543ba8dea511aaa9a4f941eb80f6249d545d2b3d41bd57fa470c1618d61ac518db9ac89b329bbe34bb60bed36289e504ab3aeb5c9b38db403b577b6b0763471d2735672517055779370577650f754f5d2f136f1b1f77df60bf04fffc80fac089414b8377855c0c7d19ce142117691515115d113333764fdc8304b644dda4b0e4869435a937d339322c3233b3e6665fcc65cfb3cfaf28d854f8ae58bb24ab7455d99b0afdca92aa5d358cb55e7553eb1f36ea35d5349f6d956b2b6c3fda29dd55d47dba57b5afb1ffee449b49b327ff9d1a3fedf00c8d99fdb3becf49987b7abef982a58b4416b72ef9b62c73f9bd9521ab4eaf7159bb6fbde5866d9b4c366fd96ab26dfb0eab9dfb77bbee39bb2f6cff838339877e1e693f267e7cc549eb53e7ce249ffd757ed245ed4b47af245efd777dce4d9b5b77efd4df53be7fe261de63b127fb9f65be107979f075fe5bf977173e347d32fdfceaeb82efe13f057e9dfad3facff1ff7f000d000f34fa96f15d000000206348524d00007a25000080830000f9ff000080e9000075300000ea6000003a980000176f925fc546000000f04944415478da8cd1ab4a04601005e0efdf5d2fec066f08168b884f2098c46235992c16ad169fc06031984c3e80c505ab60315a4410149382c9455804415c51770cff5e14f1326518ce99cb3993c2ef51ca29e5346b59b2e73897819427a4255b1a1ed58561032a36ed467409fb46dd49b909837a6cc761973061cdb81b434a6a26d5edb88cbc2704b3c28107278ebcaa0a0b196b13e6dd9b726dd5a29a31b7163356d03eb847c5bbb25ea1aca8a5bff04972122dbd49c79ec21f3efd9f10684a9a026fdf57f4e359494353f2a2a4afd31a8269e15cc3852b4fce84b98cb59d64dd8a53238aeecca8daf8f2ac9fe363006b5d542ebf959c0a0000000049454e44ae426082";
    private static final String pencilIcon ="89504e470d0a1a0a0000000d4948445200000015000000100806000000f9da342500000424694343504943432050726f66696c65000038118555df6fdb54143e896f52a4163f205847878ac5af55535bb91b1aadc6064993a5ed4a16a5e9d82a24e43a3789a91b07dbe9b6aa4f7b813706fc0140d9030f483c210d06627bd9f6c0b44953872aaa49487be8c40f2126ed0555e1bb76622753c45cf5facb39df39e73be75edb443d5f69b59a19558896abae9dcf249593a716949e4d8ad2b3d44b03d4abe94e2d91cbcd122ec115f7ceebe11d8a08cbed91eefe4ef623bf7a8bdcd189224fc06e151d7d19f80c51ccd46bb64b14bf07fbf869b706dcf31cf0d3360402ab02977d9c1278d1c7273d4e213f098ed02aeb15ad08bc063cbcd8662fb7615f0318c893e1556e1bba226691b3ad926172cfe12f8f71b731ff0f2e9b75f4ec5d8358fb9ca5b963b80f89de2bf654be893fd7b5f41cf04bb05fafb949617f05f88ffad27c02781f51f4a9927d74dee7475f5fad14de06de057bd170a70b4dfb6a75317b1c18b1d1f525eb98c82338d7756712b3a41780ef56f8b4d863e891a8c85369e061e0c14a7daa995f9a7156e684ddcbb35a99cc02238f64bfa7cde48007803fb4adbca805cdd23a3733a216f24b576b6eaea941daae9a59510bfd32993b5e8fc8296dbb95c2941fcb0eba76a119cb164ac6d1e9267fad667a6711dad805bb9e17da909fddd2ec74061879d83fbc3a2fe6061cdb5dd45262b6a3c047e84444234e162d62d5a94a3ba4509e3294c4bd46363c2532c88485c3cb6131224fd2126cdd79398fe3c7848cb217bd2da251a53bc7af70bfc9b1583f53d901fc1f62b3ec301b6713a4b037d89bec084bc13ac10e050a726d3a152ad37d28f5f3bc4f7554163a4e50f2fc407d288851ced9ef1afacd8ffe869ab04b2bf4234fd031345bed13200713687537d23ea76b6b3fec0e3cca06bbf8ceedbe6b6b74fc71f38ddd8b6dc736b06ec6b6c2f8d8afb12dfc6d52023b607a8a96a1caf076c20978231d3d5c01d3250deb6fe059d0da52dab1a3a5eaf981d02326c13fc83eccd2b9e1d0aafea2fea96ea85fa817d4df3b6a84193ba6247d2a7d2bfd287d277d2ffd4c8a7459ba22fd245d95be912e0539bbefbd9f25d87baf5f6113dd8a5d68d56b9f3527534eca7be417e594fcbcfcb23c1bb014b95f1e93a7e4bdf0ec09f6cd0cfd1dbd18740ab36a4db57b2df10418340f25069d06d7c654c584ab741659db9f93a65236c8c6d8f423a7765c9ce5968a783a9e8a274889ef8b4fc4c7e23302b79ebcf85ef826b0a603f5fe9313303a3ae06d2c4c25c833e29d3a715645f40a749bd0cd5d7e06df0ea249ab76d636ca1557d9afaaaf29097ccab8325dd5478715cd3415cfe5283677b8bdc28ba324be83228ee841defbbe4576dd0c6dee5b4487ffc23beb56685ba8137ded10f5bf1ada86f04e7ce633a28b07f5babde2e7a348e40691533ab0dffb1de94be2dd74b7d17880f755cf27443b1f371affae371a3b5f22ff16d165f33f590071786c1b2b13000000097048597300000b1300000b1301009a9c18000000f9494441543811ad93410ac23010454df516ee055782d0a24772e97dbc882b75a5e04574e705b4be1f9232d8266dc1c027d3c99f9769d2babaae27ff1ec5bf81e28d863ae7a6285b371bd3a9801cd75b35c48eb8f3ecb23bda0d2390798d1602323beb69626d96939a42853c8c2dbaa04378f6f9dffa2c3014ea9505acd00d9dd032e474042d462b614d14db0ecf3c1f519503fa350bb131c5be0be632c0d4e126007df7d66fe3ce4e298e1dce89af41e510a03ca9db8fb7ba03f8427bcc772e5b9bf94f8a5c72c457ec340059b1f004f4206ebed14eb34926a140b4e6af7d0c50ec24d42f86df11f6c734d21b66a1bdd50943eaa212f661e92f82eb276a58ce86470000000049454e44ae426082";
    //private mainWindowController mwc = null;
    /**
     * Creates new form mainWindow
     */
    private DefaultListModel<File> fileList = new DefaultListModel<>();
    private DefaultListModel<Script> scriptList = new DefaultListModel<>();
    private Zip zipFile;
    private Caspac cp = new Caspac();
    int currentScriptIndex = -1;
    
    /**
     *initializes window
     */
    public CASCADEGUI() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.resourcesForScriptList.setDropTarget(dt);
        this.list1.setDropTarget(dt);
    }
    
    DropTarget dt=new DropTarget(){
        
            @Override
            public synchronized void drop(DropTargetDropEvent event) {
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
                        List<File> files = null;
                        try {  //get a list of the files and add them
                            files = (List<File>) transferable.getTransferData(flavor);
                            for (File f : files) {
                                String file=f.getCanonicalPath();
                                resourcesForScriptList.add(file);
                                list1.add(file);
                            }
                        } catch (UnsupportedFlavorException ex) {
                            Logger.getLogger(CASCADEGUI.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(CASCADEGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
   
                    //event.dropComplete(true);


                Point point = event.getLocation();
                System.out.println(point+"drop event");
                // handle drop inside current table
            }
        };


    public CASCADEGUI(mainWindowController mwc) {
        initComponents();
        //this.mwc = mwc;
        this.setLocationRelativeTo(null);
        //this.jList1.setModel(this.mwc.getListModel());
        this.outputFile.setText(mwc.getOutputFile());
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BannerPicOrText = new javax.swing.ButtonGroup();
        mainPanel = new javax.swing.JPanel();
        outputFIle = new javax.swing.JPanel();
        outputFile = new javax.swing.JTextField();
        makeCASPAC = new javax.swing.JButton();
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
        jScrollPane2 = new javax.swing.JScrollPane();
        scriptDiscriptionJText = new javax.swing.JTextPane();
        jPanel5 = new javax.swing.JPanel();
        list1 = new java.awt.List();
        scriptNameJLabel = new javax.swing.JLabel();
        minSVNversionJLabel = new javax.swing.JLabel();
        scriptRevisionJLabel = new javax.swing.JLabel();
        uniqueIDJLabel = new javax.swing.JLabel();
        supportURLJLabel = new javax.swing.JLabel();
        updateMessageJLabel = new javax.swing.JLabel();
        killSwitchJLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        scriptListJList = new javax.swing.JList();
        deleteScriptButton = new javax.swing.JButton();
        addScriptButton = new javax.swing.JButton();
        editScriptNameButton = new javax.swing.JButton();
        script = new javax.swing.JPanel();
        scriptText = new javax.swing.JScrollPane();
        scriptWorkArea = new javax.swing.JTextArea();
        txtfile = new javax.swing.JPanel();
        descriptionScrollpane = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();
        zip = new javax.swing.JPanel();
        resourcesForScriptList = new java.awt.List();
        remove = new javax.swing.JButton();
        add = new javax.swing.JButton();
        meta = new javax.swing.JPanel();
        minSVNversion = new javax.swing.JTextField();
        minSVNversionLabel = new javax.swing.JLabel();
        scriptRevisionLabel = new javax.swing.JLabel();
        uniqueIDLabel = new javax.swing.JLabel();
        supportURLLabel = new javax.swing.JLabel();
        updateMessageLabel = new javax.swing.JLabel();
        killswitchMessageLabel = new javax.swing.JLabel();
        scriptRevision = new javax.swing.JTextField();
        uniqueID = new javax.swing.JTextField();
        supportURL = new javax.swing.JTextField();
        updateMessage = new javax.swing.JTextField();
        killswitchMessage = new javax.swing.JTextField();
        overviewScrollPane = new javax.swing.JScrollPane();
        overviewWorkArea = new javax.swing.JTextArea();
        buildPropertiesPanel = new javax.swing.JPanel();
        developerName = new javax.swing.JTextField();
        donationPanel = new javax.swing.JPanel();
        donateLink = new javax.swing.JTextField();
        donateText = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        windowText = new javax.swing.JTextField();
        buttonText = new javax.swing.JTextField();
        alwaysEnableControls = new javax.swing.JCheckBox();
        audioEnabled = new javax.swing.JCheckBox();
        useBannerText = new javax.swing.JRadioButton();
        useBannerPic = new javax.swing.JRadioButton();
        bannerPic = new javax.swing.JTextField();
        bannerText = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("CASCADEGUI/resources/Bundle"); // NOI18N
        mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.mainPanel.border.title"))); // NOI18N
        mainPanel.setName("mainPanel"); // NOI18N

        outputFIle.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.outputFIle.border.title"))); // NOI18N
        outputFIle.setName("outputFIle"); // NOI18N

        outputFile.setText(System.getProperty("user.dir"));
        outputFile.setName("outputFile"); // NOI18N
        outputFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputFileActionPerformed(evt);
            }
        });

        makeCASPAC.setText(bundle.getString("CASCADEGUI.makeCASPAC.text")); // NOI18N
        makeCASPAC.setName("makeCASPAC"); // NOI18N
        makeCASPAC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeCASPACActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout outputFIleLayout = new javax.swing.GroupLayout(outputFIle);
        outputFIle.setLayout(outputFIleLayout);
        outputFIleLayout.setHorizontalGroup(
            outputFIleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputFIleLayout.createSequentialGroup()
                .addComponent(outputFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(makeCASPAC, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        outputFIleLayout.setVerticalGroup(
            outputFIleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(makeCASPAC)
            .addComponent(outputFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        workArea.setName(""); // NOI18N

        scriptGroup.setName("scriptGroup"); // NOI18N
        scriptGroup.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                scriptGroupStateChanged(evt);
            }
        });

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

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        scriptDiscriptionJText.setEnabled(false);
        scriptDiscriptionJText.setName("scriptDiscriptionJText"); // NOI18N
        jScrollPane2.setViewportView(scriptDiscriptionJText);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CASCADEGUI.jPanel5.border.title"))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N

        list1.setName("list1"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(list1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(list1, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addContainerGap())
        );

        scriptNameJLabel.setName("scriptNameJLabel"); // NOI18N

        minSVNversionJLabel.setName("minSVNversionJLabel"); // NOI18N

        scriptRevisionJLabel.setName("scriptRevisionJLabel"); // NOI18N

        uniqueIDJLabel.setName("uniqueIDJLabel"); // NOI18N

        supportURLJLabel.setName("supportURLJLabel"); // NOI18N

        updateMessageJLabel.setName("updateMessageJLabel"); // NOI18N

        killSwitchJLabel.setName("killSwitchJLabel"); // NOI18N

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
                        .addComponent(supportURLJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(minSVNversionTitleJLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(minSVNversionJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(scriptRevisionTitleJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scriptRevisionJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(uniqueIDTitleJLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uniqueIDJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(updateMessageTitleJLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(updateMessageJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(scriptNameTitleJLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scriptNameJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 6, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(killswitchMessageTitleJLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(killSwitchJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())))
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(minSVNversionJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(minSVNversionTitleJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scriptRevisionTitleJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scriptRevisionJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(uniqueIDTitleJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(uniqueIDJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(supportURLTitleJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(supportURLJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(updateMessageTitleJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(updateMessageJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(killswitchMessageTitleJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(killSwitchJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        deleteScriptButton.setIcon(new ImageIcon(DatatypeConverter.parseHexBinary(trashIcon)));
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

        editScriptNameButton.setIcon(new ImageIcon(DatatypeConverter.parseHexBinary(pencilIcon)));
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(scriptOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(deleteScriptButton)
                            .addComponent(addScriptButton)
                            .addComponent(editScriptNameButton)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        scriptGroup.addTab(bundle.getString("CASCADEGUI.scriptOverview.TabConstraints.tabTitle"), scriptOverview); // NOI18N

        script.setName("script"); // NOI18N

        scriptText.setName("scriptText"); // NOI18N

        scriptWorkArea.setColumns(20);
        scriptWorkArea.setRows(5);
        scriptWorkArea.setName("scriptWorkArea"); // NOI18N
        scriptText.setViewportView(scriptWorkArea);

        javax.swing.GroupLayout scriptLayout = new javax.swing.GroupLayout(script);
        script.setLayout(scriptLayout);
        scriptLayout.setHorizontalGroup(
            scriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scriptText, javax.swing.GroupLayout.DEFAULT_SIZE, 1101, Short.MAX_VALUE)
        );
        scriptLayout.setVerticalGroup(
            scriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scriptText, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
        );

        scriptGroup.addTab(bundle.getString("CASCADEGUI.script.TabConstraints.tabTitle"), script); // NOI18N

        txtfile.setName("txtfile"); // NOI18N

        descriptionScrollpane.setName("descriptionScrollpane"); // NOI18N

        description.setColumns(20);
        description.setLineWrap(true);
        description.setRows(5);
        description.setWrapStyleWord(true);
        description.setName("description"); // NOI18N
        descriptionScrollpane.setViewportView(description);

        javax.swing.GroupLayout txtfileLayout = new javax.swing.GroupLayout(txtfile);
        txtfile.setLayout(txtfileLayout);
        txtfileLayout.setHorizontalGroup(
            txtfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(descriptionScrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 1101, Short.MAX_VALUE)
        );
        txtfileLayout.setVerticalGroup(
            txtfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(descriptionScrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
        );

        scriptGroup.addTab(bundle.getString("CASCADEGUI.txtfile.TabConstraints.tabTitle"), txtfile); // NOI18N

        zip.setName("zip"); // NOI18N

        resourcesForScriptList.setName("resourcesForScriptList"); // NOI18N

        remove.setIcon(new ImageIcon(DatatypeConverter.parseHexBinary(trashIcon)));
        remove.setName("remove"); // NOI18N
        remove.setPreferredSize(new java.awt.Dimension(35, 30));
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });

        add.setText(bundle.getString("CASCADEGUI.add.text")); // NOI18N
        add.setName("add"); // NOI18N
        add.setPreferredSize(new java.awt.Dimension(35, 30));
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout zipLayout = new javax.swing.GroupLayout(zip);
        zip.setLayout(zipLayout);
        zipLayout.setHorizontalGroup(
            zipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(zipLayout.createSequentialGroup()
                .addGap(0, 1018, Short.MAX_VALUE)
                .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(remove, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(zipLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resourcesForScriptList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        zipLayout.setVerticalGroup(
            zipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(zipLayout.createSequentialGroup()
                .addComponent(resourcesForScriptList, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(zipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(remove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        scriptGroup.addTab(bundle.getString("CASCADEGUI.zip.TabConstraints.tabTitle"), zip); // NOI18N

        meta.setName("meta"); // NOI18N

        minSVNversion.setName("minSVNversion"); // NOI18N
        minSVNversion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minSVNversionActionPerformed(evt);
            }
        });

        minSVNversionLabel.setText(bundle.getString("CASCADEGUI.minSVNversionLabel.text")); // NOI18N
        minSVNversionLabel.setName("minSVNversionLabel"); // NOI18N

        scriptRevisionLabel.setText(bundle.getString("CASCADEGUI.scriptRevisionLabel.text")); // NOI18N
        scriptRevisionLabel.setMaximumSize(null);
        scriptRevisionLabel.setMinimumSize(null);
        scriptRevisionLabel.setName("scriptRevisionLabel"); // NOI18N

        uniqueIDLabel.setText(bundle.getString("CASCADEGUI.uniqueIDLabel.text")); // NOI18N
        uniqueIDLabel.setName("uniqueIDLabel"); // NOI18N

        supportURLLabel.setText(bundle.getString("CASCADEGUI.supportURLLabel.text")); // NOI18N
        supportURLLabel.setName("supportURLLabel"); // NOI18N

        updateMessageLabel.setText(bundle.getString("CASCADEGUI.updateMessageLabel.text")); // NOI18N
        updateMessageLabel.setName("updateMessageLabel"); // NOI18N

        killswitchMessageLabel.setText(bundle.getString("CASCADEGUI.killswitchMessageLabel.text")); // NOI18N
        killswitchMessageLabel.setName("killswitchMessageLabel"); // NOI18N

        scriptRevision.setMinimumSize(new java.awt.Dimension(50, 16));
        scriptRevision.setName("scriptRevision"); // NOI18N
        scriptRevision.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scriptRevisionActionPerformed(evt);
            }
        });

        uniqueID.setMinimumSize(new java.awt.Dimension(50, 16));
        uniqueID.setName("uniqueID"); // NOI18N

        supportURL.setName("supportURL"); // NOI18N

        updateMessage.setName("updateMessage"); // NOI18N

        killswitchMessage.setName("killswitchMessage"); // NOI18N

        javax.swing.GroupLayout metaLayout = new javax.swing.GroupLayout(meta);
        meta.setLayout(metaLayout);
        metaLayout.setHorizontalGroup(
            metaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(metaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(metaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(metaLayout.createSequentialGroup()
                        .addComponent(killswitchMessageLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(killswitchMessage))
                    .addGroup(metaLayout.createSequentialGroup()
                        .addComponent(scriptRevisionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scriptRevision, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                        .addGap(696, 696, 696))
                    .addGroup(metaLayout.createSequentialGroup()
                        .addComponent(supportURLLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(supportURL)
                        .addGap(166, 166, 166))
                    .addGroup(metaLayout.createSequentialGroup()
                        .addComponent(updateMessageLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updateMessage))
                    .addGroup(metaLayout.createSequentialGroup()
                        .addGroup(metaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(metaLayout.createSequentialGroup()
                                .addComponent(minSVNversionLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(minSVNversion, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(metaLayout.createSequentialGroup()
                                .addComponent(uniqueIDLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uniqueID, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        metaLayout.setVerticalGroup(
            metaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(metaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(metaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minSVNversionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minSVNversion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(metaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scriptRevision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scriptRevisionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(metaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uniqueID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uniqueIDLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(metaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(supportURLLabel)
                    .addComponent(supportURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(metaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateMessageLabel)
                    .addComponent(updateMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(metaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(killswitchMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(killswitchMessageLabel))
                .addGap(338, 338, 338))
        );

        scriptGroup.addTab(bundle.getString("CASCADEGUI.meta.TabConstraints.tabTitle"), meta); // NOI18N

        workArea.addTab(bundle.getString("CASCADEGUI.scriptGroup.TabConstraints.tabTitle"), scriptGroup); // NOI18N

        overviewScrollPane.setName("overviewScrollPane"); // NOI18N

        overviewWorkArea.setColumns(20);
        overviewWorkArea.setLineWrap(true);
        overviewWorkArea.setRows(5);
        overviewWorkArea.setWrapStyleWord(true);
        overviewWorkArea.setName("overviewWorkArea"); // NOI18N
        overviewScrollPane.setViewportView(overviewWorkArea);

        workArea.addTab(bundle.getString("CASCADEGUI.overviewScrollPane.TabConstraints.tabTitle"), overviewScrollPane); // NOI18N

        buildPropertiesPanel.setName("buildPropertiesPanel"); // NOI18N

        developerName.setBackground(new java.awt.Color(240, 240, 240));
        developerName.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("CASCADEGUI.buttonText.border.title"))); // NOI18N
        developerName.setName("developerName"); // NOI18N

        donationPanel.setName("donationPanel"); // NOI18N

        donateLink.setBackground(new java.awt.Color(240, 240, 240));
        donateLink.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("CASCADEGUI.buttonText.border.title"))); // NOI18N
        donateLink.setName("donateLink"); // NOI18N

        donateText.setBackground(new java.awt.Color(240, 240, 240));
        donateText.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("CASCADEGUI.buttonText.border.title"))); // NOI18N
        donateText.setName("donateText"); // NOI18N

        javax.swing.GroupLayout donationPanelLayout = new javax.swing.GroupLayout(donationPanel);
        donationPanel.setLayout(donationPanelLayout);
        donationPanelLayout.setHorizontalGroup(
            donationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(donationPanelLayout.createSequentialGroup()
                .addComponent(donateText, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(donateLink))
        );
        donationPanelLayout.setVerticalGroup(
            donationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(donationPanelLayout.createSequentialGroup()
                .addComponent(donateLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 2, Short.MAX_VALUE))
            .addComponent(donateText)
        );

        jPanel4.setName("jPanel4"); // NOI18N

        windowText.setBackground(new java.awt.Color(240, 240, 240));
        windowText.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("CASCADEGUI.buttonText.border.title"))); // NOI18N
        windowText.setName("windowText"); // NOI18N
        windowText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                windowTextActionPerformed(evt);
            }
        });

        buttonText.setBackground(new java.awt.Color(240, 240, 240));
        buttonText.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("CASCADEGUI.buttonText.border.title"))); // NOI18N
        buttonText.setName("buttonText"); // NOI18N

        alwaysEnableControls.setText(bundle.getString("CASCADEGUI.alwaysEnableControls.text")); // NOI18N
        alwaysEnableControls.setName("alwaysEnableControls"); // NOI18N

        audioEnabled.setSelected(true);
        audioEnabled.setText(bundle.getString("CASCADEGUI.audioEnabled.text")); // NOI18N
        audioEnabled.setName("audioEnabled"); // NOI18N

        BannerPicOrText.add(useBannerText);
        useBannerText.setSelected(true);
        useBannerText.setText(bundle.getString("CASCADEGUI.useBannerText.text")); // NOI18N
        useBannerText.setName("useBannerText"); // NOI18N

        BannerPicOrText.add(useBannerPic);
        useBannerPic.setText(bundle.getString("CASCADEGUI.useBannerPic.text")); // NOI18N
        useBannerPic.setName("useBannerPic"); // NOI18N

        bannerPic.setBackground(new java.awt.Color(240, 240, 240));
        bannerPic.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("CASCADEGUI.buttonText.border.title"))); // NOI18N
        bannerPic.setName("bannerPic"); // NOI18N

        bannerText.setBackground(new java.awt.Color(240, 240, 240));
        bannerText.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("CASCADEGUI.buttonText.border.title"))); // NOI18N
        bannerText.setName("bannerText"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(windowText, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonText, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(alwaysEnableControls)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(audioEnabled))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(useBannerPic)
                            .addComponent(useBannerText))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bannerPic, javax.swing.GroupLayout.DEFAULT_SIZE, 947, Short.MAX_VALUE)
                            .addComponent(bannerText))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(windowText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(buttonText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(alwaysEnableControls)
                        .addComponent(audioEnabled)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bannerText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(useBannerText))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bannerPic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(useBannerPic))
                .addGap(63, 63, 63))
        );

        javax.swing.GroupLayout buildPropertiesPanelLayout = new javax.swing.GroupLayout(buildPropertiesPanel);
        buildPropertiesPanel.setLayout(buildPropertiesPanelLayout);
        buildPropertiesPanelLayout.setHorizontalGroup(
            buildPropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buildPropertiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buildPropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(developerName)
                    .addComponent(donationPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        buildPropertiesPanelLayout.setVerticalGroup(
            buildPropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buildPropertiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(developerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(donationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(289, Short.MAX_VALUE))
        );

        workArea.addTab(bundle.getString("CASCADEGUI.buildPropertiesPanel.TabConstraints.tabTitle"), buildPropertiesPanel); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(workArea, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(outputFIle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(workArea)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(outputFIle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void makeCASPACActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeCASPACActionPerformed
        File file = new File(this.outputFile.getText());
        if (file.exists())
        {
            int i = JOptionPane.showConfirmDialog(this, "Warning:" + this.outputFile.getText()+
                    " already exists are you sure you wish to continue.\n Any "
                    + "previous files will be overridden.", "Overwrite existing file?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (i == JOptionPane.NO_OPTION) {
                
                //TODO make it do someting
            }
        }
    }//GEN-LAST:event_makeCASPACActionPerformed

    private void outputFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_outputFileActionPerformed

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        // TODO add your handling code here:
        removeFiles(resourcesForScriptList.getSelectedIndexes());
    }//GEN-LAST:event_removeActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            addFileToZip(fc.getSelectedFiles());
    }//GEN-LAST:event_addActionPerformed

    private void scriptRevisionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scriptRevisionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_scriptRevisionActionPerformed

    private void windowTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_windowTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_windowTextActionPerformed

    private void minSVNversionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minSVNversionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minSVNversionActionPerformed

    private void addScriptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addScriptButtonActionPerformed
        // TODO add your handling code here:
        String s = JOptionPane.showInputDialog(this, "What would you like to name the script:\n",
                "Script Name", JOptionPane.QUESTION_MESSAGE);
        if (!(s.isEmpty()))
        {
            scriptList.addElement(new Script(s));
            cp.addScript(new Script(s));
            currentScriptIndex=scriptList.getSize()-1;
            loadScript();
        }
        
    }//GEN-LAST:event_addScriptButtonActionPerformed

    private void scriptListJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_scriptListJListValueChanged
        // TODO add your handling code here:
        currentScriptIndex = this.scriptListJList.getSelectedIndex();
        loadScript();
    }//GEN-LAST:event_scriptListJListValueChanged

    private void scriptGroupStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_scriptGroupStateChanged
        // TODO add your handling code here:
        if (currentScriptIndex != -1)
        {
            scriptList.getElementAt(currentScriptIndex).setDiscription(this.description.getText());
            scriptList.getElementAt(currentScriptIndex).setScript(this.scriptWorkArea.getText());
            scriptList.getElementAt(currentScriptIndex).metaData.setKillSwitchMessage(this.killswitchMessage.getText());
            scriptList.getElementAt(currentScriptIndex).metaData.setMinSVNversion(this.minSVNversion.getText());
            scriptList.getElementAt(currentScriptIndex).metaData.setScriptRevsion(this.scriptRevision.getText());
            scriptList.getElementAt(currentScriptIndex).metaData.setSupportURL(this.supportURL.getText());
            scriptList.getElementAt(currentScriptIndex).metaData.setUniqueID(this.uniqueID.getText());
            scriptList.getElementAt(currentScriptIndex).metaData.setUpdateMessage(this.updateMessage.getText());
            loadScript();
        }
    }//GEN-LAST:event_scriptGroupStateChanged

    private void editScriptNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editScriptNameButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editScriptNameButtonActionPerformed

    private void deleteScriptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteScriptButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteScriptButtonActionPerformed

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
                if ("System".equals(info.getName())||info.getName().toLowerCase().contains(System.getProperty("os.name").toLowerCase().subSequence(0, 3))) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CASCADEGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CASCADEGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CASCADEGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
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
    private javax.swing.JButton add;
    private javax.swing.JButton addScriptButton;
    private javax.swing.JCheckBox alwaysEnableControls;
    private javax.swing.JCheckBox audioEnabled;
    private javax.swing.JTextField bannerPic;
    private javax.swing.JTextField bannerText;
    private javax.swing.JPanel buildPropertiesPanel;
    private javax.swing.JTextField buttonText;
    private javax.swing.JButton deleteScriptButton;
    private javax.swing.JTextArea description;
    private javax.swing.JScrollPane descriptionScrollpane;
    private javax.swing.JTextField developerName;
    private javax.swing.JTextField donateLink;
    private javax.swing.JTextField donateText;
    private javax.swing.JPanel donationPanel;
    private javax.swing.JButton editScriptNameButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel killSwitchJLabel;
    private javax.swing.JTextField killswitchMessage;
    private javax.swing.JLabel killswitchMessageLabel;
    private javax.swing.JLabel killswitchMessageTitleJLabel;
    private java.awt.List list1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton makeCASPAC;
    private javax.swing.JPanel meta;
    private javax.swing.JTextField minSVNversion;
    private javax.swing.JLabel minSVNversionJLabel;
    private javax.swing.JLabel minSVNversionLabel;
    private javax.swing.JLabel minSVNversionTitleJLabel;
    private javax.swing.JPanel outputFIle;
    private javax.swing.JTextField outputFile;
    private javax.swing.JScrollPane overviewScrollPane;
    private javax.swing.JTextArea overviewWorkArea;
    private javax.swing.JButton remove;
    private java.awt.List resourcesForScriptList;
    private javax.swing.JPanel script;
    private javax.swing.JTextPane scriptDiscriptionJText;
    private javax.swing.JTabbedPane scriptGroup;
    private javax.swing.JList scriptListJList;
    private javax.swing.JLabel scriptNameJLabel;
    private javax.swing.JLabel scriptNameTitleJLabel;
    private javax.swing.JPanel scriptOverview;
    private javax.swing.JTextField scriptRevision;
    private javax.swing.JLabel scriptRevisionJLabel;
    private javax.swing.JLabel scriptRevisionLabel;
    private javax.swing.JLabel scriptRevisionTitleJLabel;
    private javax.swing.JScrollPane scriptText;
    private javax.swing.JTextArea scriptWorkArea;
    private javax.swing.JTextField supportURL;
    private javax.swing.JLabel supportURLJLabel;
    private javax.swing.JLabel supportURLLabel;
    private javax.swing.JLabel supportURLTitleJLabel;
    private javax.swing.JPanel txtfile;
    private javax.swing.JTextField uniqueID;
    private javax.swing.JLabel uniqueIDJLabel;
    private javax.swing.JLabel uniqueIDLabel;
    private javax.swing.JLabel uniqueIDTitleJLabel;
    private javax.swing.JTextField updateMessage;
    private javax.swing.JLabel updateMessageJLabel;
    private javax.swing.JLabel updateMessageLabel;
    private javax.swing.JLabel updateMessageTitleJLabel;
    private javax.swing.JRadioButton useBannerPic;
    private javax.swing.JRadioButton useBannerText;
    private javax.swing.JTextField windowText;
    private javax.swing.JTabbedPane workArea;
    private javax.swing.JPanel zip;
    // End of variables declaration//GEN-END:variables

    /**
     *adds an array of files to list of files
     * @param files
     */
    public void addFileToZip(File[] files){
        for (File f: files)
            if (f.exists())
            {
                scriptList.getElementAt(currentScriptIndex).includeFiles.add(f);
                fileList.addElement(f);
            }
    }
    
    /**
     *adds a single file to list of files
     * @param files
     */
    public void addFileToZip(File files){
        if (files.exists())
        {
            scriptList.getElementAt(currentScriptIndex).includeFiles.add(files);
            fileList.addElement(files);
        }
    }

    /**
     * removes files from list
     * @param indexList
     */
    public void removeFiles(int[] indexList)
    {
        List<Integer> list = new ArrayList<>();
        

        for (int i:indexList )
        {
            list.add(i);
        }
        
        Collections.sort(list);
        Collections.reverse(list);
        
        for (int i : list)
        {
            scriptList.getElementAt(currentScriptIndex).includeFiles.remove(i);
            fileList.remove(i);
        }
        
    }

    private Map<String, String> buildMaker() {
        Map<String, String> buildMap = new HashMap<>();
        if (!this.developerName.getText().isEmpty())
            buildMap.put("developerName", this.developerName.getText());
        if (!this.donateText.getText().isEmpty())
            buildMap.put("developerDonateButtonText", this.donateText.getText());
        if (!this.donateLink.getText().isEmpty())
            buildMap.put("donateLink", this.donateLink.getText());
        if (!this.windowText.getText().isEmpty())
            buildMap.put("windowTitle", this.windowText.getText());
        buildMap.put("usePictureForBanner", Boolean.toString(this.useBannerPic.isSelected()));
        if (!this.bannerPic.getText().isEmpty())
            buildMap.put("bannerPic", this.bannerPic.getText());
        buildMap.put("AudioEnabled", Boolean.toString(this.audioEnabled.isSelected()));
        buildMap.put("EnableControls", Boolean.toString(this.alwaysEnableControls.isSelected()));
        return buildMap;
    }
    
    private void loadScript()
    {
        this.scriptWorkArea.setText(scriptList.getElementAt(currentScriptIndex).getScript());
        this.description.setText(scriptList.getElementAt(currentScriptIndex).getDiscription());
        this.killswitchMessage.setText(scriptList.getElementAt(currentScriptIndex).metaData.getKillSwitchMessage());
        this.minSVNversion.setText(scriptList.getElementAt(currentScriptIndex).metaData.getMinSVNversion());
        this.scriptRevision.setText(scriptList.getElementAt(currentScriptIndex).metaData.getScriptRevsion());
        this.supportURL.setText(scriptList.getElementAt(currentScriptIndex).metaData.getSupportURL());
        this.uniqueID.setText(scriptList.getElementAt(currentScriptIndex).metaData.getUniqueID());
        this.updateMessage.setText(scriptList.getElementAt(currentScriptIndex).metaData.getUpdateMessage());
        fileList.removeAllElements();
        for (File f: scriptList.getElementAt(currentScriptIndex).includeFiles)
            fileList.addElement(f);

        this.scriptNameJLabel.setText(scriptList.getElementAt(currentScriptIndex).getName());
        this.scriptDiscriptionJText.setText(scriptList.getElementAt(currentScriptIndex).getDiscription());
        this.killSwitchJLabel.setText(scriptList.getElementAt(currentScriptIndex).metaData.getKillSwitchMessage());
        this.minSVNversionJLabel.setText(scriptList.getElementAt(currentScriptIndex).metaData.getMinSVNversion());
        this.scriptRevisionJLabel.setText(scriptList.getElementAt(currentScriptIndex).metaData.getScriptRevsion());
        this.supportURLJLabel.setText(scriptList.getElementAt(currentScriptIndex).metaData.getSupportURL());
        this.uniqueIDJLabel.setText(scriptList.getElementAt(currentScriptIndex).metaData.getUniqueID());
        this.updateMessageJLabel.setText(scriptList.getElementAt(currentScriptIndex).metaData.getUpdateMessage());
    }
}
