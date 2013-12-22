/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CasualInstrumentation;

import CASUAL.CASUALConnectionStatusMonitor;
import CASUAL.misc.MandatoryThread;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;


/**
 *
 * @author adamoutler
 */
public class CASUALInstrumentationFXMLController implements Initializable {
    
    @FXML
    TextArea monitorStatus;
    
    @FXML 
    ListView<String> messages;
    
    @FXML
    ListView<MandatoryThread> running;
    @FXML
    Button startAdbButton;
    
    @FXML 
    Button startFastboot;
    @FXML
    TextArea ta;
  /*  @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    */


    public void updateStatus(String status){
       ta.appendText("\n"+status);
    }
    
    

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CASUALInstrumentation.doc=this;
    }    
    
    @FXML public void startADB(){
             new CASUALConnectionStatusMonitor().start(new CASUAL.communicationstools.adb.ADBTools());
    }
    @FXML public void startFastboot(){
             new CASUALConnectionStatusMonitor().start(new CASUAL.communicationstools.fastboot.FastbootTools());
    }
    @FXML public void startHeimdall(){
             new CASUALConnectionStatusMonitor().start(new CASUAL.communicationstools.heimdall.HeimdallTools());
    }
    
    @FXML public void resetConnection(){
        CASUALConnectionStatusMonitor.stop();
    }
}
