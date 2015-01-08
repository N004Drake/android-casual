/*UI Controller for CASCADE
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
package cascade2;

import CASPACkager.PackagerMain;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.Statics;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import CASUAL.crypto.AES128Handler;
import cascade2.drag_event.DragEventHandler;
import cascade2.fileOps.CASPACFileSelection;
import com.casual_dev.caspaccreator2.CASPACcreator2;
import com.casual_dev.caspaccreator2.exception.MissingParameterException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.DialogResources;

/**
 *
 * @author adamoutler
 */
public class CASCADEUIController implements Initializable {

    //Scripting and Overview 
    @FXML
    Accordion scripting;

    //Overview Tab
    @FXML
    Button newScript;
    @FXML
    TextField scriptName;
    @FXML
    TextField devName;
    @FXML
    TextField scriptRevision;
    @FXML
    TextField supportURL;
    @FXML
    TextField donateTo;
    @FXML
    TextField donateLink;
    @FXML
    TextField applicationTitle;
    @FXML
    TextArea scriptDescription;
    @FXML
    TextField startButtonText;
    @FXML
    TextField bannerText;
    @FXML
    ToggleButton enableControls;

    //Scripting panel
    @FXML
    TextArea scriptingArea;
    @FXML
    ListView zipFiles;
    @FXML
    TreeView commandAssistant;

    //CASPAC FIle area
    @FXML
    TitledPane caspacFile;
    @FXML
    Button selectCaspac;
    @FXML
    TextField pathToCaspac;
    Button reloadCASPAC;
    @FXML
    Button saveCASPAC;
    @FXML
    CheckBox encrypt;

    //CASPACOutputArea 
    @FXML
    TitledPane caspacOutput;
    @FXML
    TextField caspacOutputFolder;
    @FXML
    Button editScriptName;
    @FXML
    CheckBox useTag;
    @FXML
    Button chooseFolder;
    @FXML
    TextField tagAppend;
    @FXML
    Button saveCASUAL;
    @FXML
    Button runCASUAL;

    @FXML
    TitledPane overview;
    private TextInputControl[] textControls;
    private static CASCADEUIController uiController;

    public CASCADEUIController getInstance() {
        return uiController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new Thread(() -> {
            Statics.GUI = new GUI.testing.automatic();
        }).start();
        Platform.runLater(() -> {
            uiController = CASCADEUIController.this;
            textControls = new TextInputControl[]{ scriptRevision,  supportURL, devName, donateTo, donateLink, applicationTitle, startButtonText, bannerText, scriptDescription, pathToCaspac, caspacOutputFolder, caspacOutputFolder, scriptingArea};

            overview.setExpanded(true);
        });
    }

    private void insertTextIntoScriptingAreaAtCursor(String replacement) {
        final IndexRange selection = scriptingArea.getSelection();
        if (selection.getLength() == 0) {
            scriptingArea.insertText(selection.getStart(), replacement);
        } else {
            scriptingArea.replaceText(selection.getStart(), selection.getEnd(), replacement);
        }
    }

    public void disableControls(boolean e) {
        scripting.setDisable(e);
        caspacFile.setDisable(e);
        caspacOutput.setDisable(e);
    }

    public void enableScripting() {
        scripting.setDisable(false);
    }

    public void enableCaspac() {
        caspacFile.setDisable(false);
    }

    public void enableCaspacOutput() {
        caspacOutput.setDisable(false);
    }

    @FXML
    private void newButtonClicked() {
        setTextAreaBlank(textControls);
        enableControls.setSelected(false);
        caspacOutput.setDisable(true);

    }

    @FXML
    private void selectCaspac() {
        pathToCaspac.setText(new CASPACFileSelection().showFileChooser(CASCADE2.getStage(), pathToCaspac.getText()));
        if (!pathToCaspac.getText().isEmpty()) {
            reloadClicked();
        }
    }

    @FXML
    private void chooseFolder() {
        caspacOutputFolder.setText(new CASPACFileSelection().showFolderChooser(CASCADE2.getStage(), caspacOutputFolder.getText()));
    }

    private void setTextAreaBlank(TextInputControl[] fields) {
        for (TextInputControl field : fields) {
            field.setText("");
        }
    }

    @FXML
    private void reloadClicked() {
        try {
            Caspac cp = new Caspac(new File(pathToCaspac.getText()), Statics.getTempFolder(), 0);
            new Thread(() -> {
                setIDEInfoFromCASPAC(cp);
            }).start();

        } catch (IOException ex) {
            Logger.getLogger(CASCADEUIController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setIDEInfoFromCASPAC(final Caspac cp) {
        try {
            cp.load();
            cp.waitForUnzip();

        } catch (IOException ex) {
            Logger.getLogger(CASCADEUIController.class
                    .getName()).log(Level.SEVERE, null, ex);

            return;
        }
        Platform.runLater(() -> {
            Script script = cp.getScripts().get(0);
            this.scriptRevision.setText(script.getMetaData().getScriptRevision());
            this.supportURL.setText(script.getMetaData().getSupportURL());
            this.scriptName.setText(script.getName());
            this.scriptDescription.setText(script.getDiscription());
            this.scriptingArea.setText(script.getScriptContentsString());
            this.bannerText.setText(cp.getOverview());
            devName.setText(cp.getBuild().getDeveloperName());
            donateLink.setText(cp.getBuild().getDonateLink());
            donateTo.setText(cp.getBuild().getDeveloperDonateButtonText());
            applicationTitle.setText(cp.getBuild().getWindowTitle());
            startButtonText.setText(cp.getBuild().getExecuteButtonText());
            enableControls.setSelected(cp.getBuild().isAlwaysEnableControls());
            zipFiles.getItems().addAll(script.getIndividualFiles());

            /*
             listModel.removeAllElements();
             for (File f : scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).individualFiles) {
             String file = f.toString();
             listModel.addElement(file.replace(file.substring(0, file.lastIndexOf(Statics.slash) + 1), "$ZIPFILE"));
             //listModel.addElement(f);
             }*/
        });

    }

    @FXML
    private File saveClicked() throws IOException {
        this.disableControls(true);
        List<String> argsArray = new ArrayList<>();
        argsArray.add("--output=" +this.pathToCaspac.getText());
        argsArray.add("--scriptname=" + scriptName.getText());
        argsArray.add("--scriptdescription=" + this.scriptDescription.getText());
        argsArray.add("--scriptcode=" + this.scriptingArea.getText());
        zipFiles.getItems().stream().forEach((file) -> {
            argsArray.add(((File) file).getAbsolutePath());
        });
        argsArray.add("--overview=" + this.scriptDescription.getText());
        argsArray.add("--devname=" + this.devName.getText());
        argsArray.add("--enablecontrols=" + this.enableControls.isSelected());
        argsArray.add("--bannertext=" + this.bannerText.getText());
        argsArray.add("--donatebuttontext=" + this.donateTo.getText());
        argsArray.add("--donatelink=" + this.donateLink.getText());
        argsArray.add("--startbutton=" + this.startButtonText.getText());
        argsArray.add("--windowtitle=" + this.applicationTitle.getText());
        argsArray.add("--scriptrevision=" + this.scriptRevision.getText());
        argsArray.add("--supporturl=" + this.supportURL.getText());

        CASPACcreator2 cpc = new CASPACcreator2(argsArray.toArray(new String[argsArray.size()]));
        Caspac caspac=null;
        try {
            caspac = cpc.createNewCaspac();
        } catch (MissingParameterException ex) {
            Logger.getLogger(CASCADEUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
         
         if (this.encrypt.isSelected()){
               File temp = new File(caspac.getCASPAC().getAbsolutePath() + ".tmp");
                if (new AES128Handler(caspac.getCASPAC()).encrypt(temp.getAbsolutePath(), getPassword())){
                    caspac.getCASPAC().delete();
                    new FileOperations().moveFile(temp, caspac.getCASPAC());
              } 
         }
        disableControls(false);
         return caspac.getCASPAC();
    }

    private char[] getPassword(){
        Dialog dialog=new Dialog();
        dialog.setTitle("CASPAC Encryption");
        dialog.setHeaderText("Enter your password");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY);
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        dialog.getDialogPane().setContent(password);
        
        dialog.showAndWait();


        
         return password.getText().toCharArray();
    }
    
    @FXML
    private File saveCASUALClicked() throws IOException {
        File caspacFile=saveClicked();
       File casualFile=PackagerMain.doPackaging(new String[]{"--CASPAC",caspacFile.getAbsolutePath(),"--output", this.caspacOutputFolder.getText()});
              return casualFile;
    }

    @FXML
    private void runCASUALClicked() throws IOException {
          File casual=saveCASUALClicked();
            String exe = "";
                if (OSTools.isWindows()) {
                    exe = ".exe";
                }
                final String executable = exe;
                final String outputFile = casual.getAbsolutePath();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {

                        //CASUAL.JavaSystem.restart(new String[]{outputFile+Statics.slash+file});
                        ProcessBuilder pb;
                        if (OSTools.isWindows()) {
                            System.out.println("executing " + "cmd.exe /c start  " + System.getProperty("java.home") + Statics.slash + "bin" + Statics.slash + "java" + executable + " -jar " + outputFile);
                            new CASUAL.Shell().liveShellCommand(new String[]{"cmd.exe", "/C", "\"" + outputFile + "\""}, true);
                        } else {
                            System.out.println("Executing" + System.getProperty("java.home") + Statics.slash + "bin" + Statics.slash + "java" + executable + " -jar " + outputFile);

                            new CASUAL.Shell().liveShellCommand(new String[]{System.getProperty("java.home") + Statics.slash + "bin" + Statics.slash + "java" + executable, "-jar", outputFile}, true);

                        }
                        // pb.directory(new File(new File( "." ).getCanonicalPath()));
                        //log.level3Verbose("Launching CASUAL \""+pb.command().get(0)+" "+pb.command().get(1)+" "+pb.command().get(2));
                        //Process p = pb.start();

                        //new CASUAL.Shell().sendShellCommand(new String[]{System.getProperty("java.home") + Statics.slash + "bin" + Statics.slash + "java" + executable,"-jar",outputFile+Statics.slash+file});
                    }
                };
                Thread t = new Thread(r);

                t.start();
    }

    @FXML
    private void zipFileResourcesClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {

                String replacement = "\"$ZIPFILE" + new File(zipFiles.getSelectionModel().getSelectedItem().toString()).getName() + "\"";
                insertTextIntoScriptingAreaAtCursor(replacement);

            }
        }
        if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
            zipFiles.getItems().remove(zipFiles.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    protected void zipFileDragOver(DragEvent event) {
        new DragEventHandler().setzipFileEventList(event);
        event.consume();
    }

    @FXML
    protected void zipFileDragExited(DragEvent event) {
        new DragEventHandler().markTimeOfDrop();

    }

    @FXML
    protected void zipFileMouseEnter() {

        List<File> fileList = new DragEventHandler().ifTimerInRangeSetFileList();
        fileList.stream().forEach((f) -> {
            if (f.isFile() && f.exists() && !zipFiles.getItems().contains(f.getAbsolutePath())) {
                zipFiles.getItems().add(f.getAbsolutePath());
            } else {
                Log.level4Debug("Invalid drop event detected:" + f);
            }
        });
    }
}
