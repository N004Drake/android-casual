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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeView;

/**
 *
 * @author adamoutler
 */
public class CASCADEUIController implements Initializable {

    @FXML
    TextField scriptName;
    @FXML
    TextField minRev;
    @FXML
    TextField uniqueID;
    @FXML
    TextField supportURL;
    @FXML
    TextArea  scriptDescription;
   @FXML
    TextField  scriptRevision ;
    @FXML
     TextField devName;
    @FXML
    TextField donateTo;
    @FXML
    TextField donateLink;
    @FXML
    TextField applicationTitle;
    @FXML
    TextField startButtonText;
    @FXML
    TextField bannerText;
    @FXML
    TextField pathToCaspac;
    @FXML
    TextField caspacOutputFolder;
    @FXML
    TextField tagAppend;
    @FXML
    Button editScriptName;
    @FXML
    Button newScript;
    @FXML
    Button reloadCASPAC;
    @FXML
    Button saveCASPAC;
    @FXML
    Button chooseFolder;
    @FXML
    Button saveCASUAL;
   @FXML
    Button runCASUAL;
    @FXML
    ToggleButton enableControls;
    @FXML
    ListView zipFiles;
    @FXML
    TreeView commandAssistant;
    @FXML
    TextArea scriptingArea;
    @FXML
    CheckBox encrypt;
    @FXML 
    CheckBox useTag;
    
    
    
    
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
