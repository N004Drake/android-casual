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

import CASPACcreator.CASPACcreator;
import CASUAL.Statics;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import cascade2.fileOps.CASPACFileSelection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

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
    TextField minRev;
    @FXML
    TextField uniqueID;
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
            textControls = new TextInputControl[]{minRev, scriptRevision, uniqueID, supportURL, devName, donateTo, donateLink, applicationTitle, startButtonText, bannerText, scriptDescription, pathToCaspac, caspacOutputFolder, caspacOutputFolder, scriptingArea};
            zipFiles.setOnDragDropped((DragEvent event) -> {
                System.out.println("Drop detected");
                final Dragboard dragboard = event.getDragboard();
                if (dragboard.hasFiles()) {
                    System.out.println(dragboard.getUrl());

                }

            });
            overview.setExpanded(true);
        });

        zipFiles.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {

                    int cursorPosition = scriptingArea.getCaretPosition();
                    String s = scriptingArea.getText();
                    String pre = s.substring(0, cursorPosition);
                    String post = s.substring(cursorPosition);
                    scriptingArea.setText(pre + "$ZIPFILE" + new File(zipFiles.getSelectionModel().getSelectedItem().toString()).getName() + post);
                    scriptingArea.positionCaret(cursorPosition);

                }
            }
        });
    }


public void disableAll() {
        scripting.setDisable(true);
        caspacFile.setDisable(true);
        caspacOutput.setDisable(true);
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

    @FXML
        private void chooseFolder() {
        caspacOutputFolder.setText(new CASPACFileSelection().showFolderChooser(CASCADE2.getStage(), caspacOutputFolder.getText()));
    }

    private void setTextAreaBlank(TextInputControl[] fields) {
        for (TextInputControl field : fields) {
            System.out.println(field);
            field.setText("");
        }
    }

    @FXML
        private void reloadClicked() {

    }

    private void setIDEInfoFromCASPAC(final Caspac cp) {
        try {
            cp.load();
        

} catch (IOException ex) {
            Logger.getLogger(CASCADEUIController.class  

    .getName()).log(Level.SEVERE, null, ex);

return;
        }
        Platform.runLater(() -> {
            Script script = cp.scripts.get(0);
            this.minRev.setText(script.metaData.minSVNversion);
            this.scriptRevision.setText(script.metaData.scriptRevision);
            this.supportURL.setText(script.metaData.supportURL);
            this.uniqueID.setText(script.metaData.uniqueIdentifier);
            this.scriptName.setText(script.name);
            this.scriptDescription.setText(script.discription);
            this.scriptingArea.setText(script.scriptContents);
            this.bannerText.setText(cp.overview);
            devName.setText(cp.build.developerName);
            donateLink.setText(cp.build.donateLink);
            donateTo.setText(cp.build.developerDonateButtonText);
            applicationTitle.setText(cp.build.windowTitle);
            startButtonText.setText(cp.build.executeButtonText);
            enableControls.setSelected(cp.build.alwaysEnableControls);
            zipFiles.getItems().addAll(script.individualFiles);

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
        private void saveClicked() {
        CASPACcreator cpc = new CASPACcreator();

    }

    @FXML
        private void saveCASUALClicked() {

    }

    @FXML
        private void runCASUALClicked() {

    }

    @FXML
        private void zipFileResourcesClicked() {

    }

}
