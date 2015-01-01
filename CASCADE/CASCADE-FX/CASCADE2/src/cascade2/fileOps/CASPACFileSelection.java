/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cascade2.fileOps;

import java.io.File;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author adamoutler
 */
public class CASPACFileSelection {

    public String showFileChooser(Stage stage, String initial) {
        FileChooser chooser = new FileChooser();
         chooser.setTitle("Select CASPAC file");
        chooser.setInitialDirectory(ifInitialEmptySetToPeriod(initial));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CASPAC files (*.CASPAC)", "*.caspac");
        chooser.getExtensionFilters().add(extFilter);
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            return file.getAbsolutePath();
        }
        return "";
    }
    
    public String showFolderChooser(Stage stage, String initial){
         DirectoryChooser chooser = new DirectoryChooser();
         chooser.setTitle("Select Folder");

         
        chooser.setInitialDirectory( ifInitialEmptySetToPeriod(initial));      
        File dir = chooser.showDialog(stage);

        if (dir != null) {
            return dir.getAbsolutePath();
        }
        return "";
    }
    
    private File ifInitialEmptySetToPeriod(String initial){
        if (initial==null||initial.isEmpty()){
             initial=".";
         }
        return new File(initial);
    }
}
