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

import cascade2.fileOps.CASPACFileSelection;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;

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

    private TextInputControl[] textControls ;
    private static CASCADEUIController uiController;

    public CASCADEUIController getInstance() {
        return uiController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        uiController = this;
        textControls = new TextInputControl[]{minRev, scriptRevision, uniqueID, supportURL, devName, donateTo, donateLink, applicationTitle, startButtonText, bannerText, scriptDescription, pathToCaspac, caspacOutputFolder, caspacOutputFolder, scriptingArea};
        zipFiles.setOnDragDropped((DragEvent event) -> {
            System.out.println("Drop detected");
            final Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                System.out.println(dragboard.getUrl());
                
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
    private void selectCaspac(){
        pathToCaspac.setText(new CASPACFileSelection().showFileChooser(CASCADE2.getStage(), pathToCaspac.getText() ));
    }
    
    @FXML
    private void chooseFolder(){
        caspacOutputFolder.setText(new CASPACFileSelection().showFolderChooser(CASCADE2.getStage(), caspacOutputFolder.getText()));
    }
    
    private void setTextAreaBlank(TextInputControl[] fields) {
        for (TextInputControl field : fields) {
             System.out.println(field);
            field.setText("");
        }
    }
    
    @FXML
    private void reloadClicked(){
        
    }
    
    
    @FXML
    private void saveClicked(){
        
    }
    
    @FXML
    private void saveCASUALClicked(){
        
    }

    @FXML private void runCASUALClicked(){
        
    }
    

}
