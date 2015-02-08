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
         if (!new File(initial).isDirectory()) initial=new File(initial).getParent();
        chooser.setInitialDirectory(ifInitialEmptyUseHome(initial));
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");
        FileChooser.ExtensionFilter cpFilter = new FileChooser.ExtensionFilter("CASPAC files (*.CASPAC)", "*.CASPAC","*.caspac");
       
        chooser.getExtensionFilters().addAll(allFilter,cpFilter);
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            return file.getAbsolutePath();
        }
        return "";
    }
    
    public String showFolderChooser(Stage stage, String initial){
         DirectoryChooser chooser = new DirectoryChooser();
         chooser.setTitle("Select Folder");

         
        chooser.setInitialDirectory( ifInitialEmptyUseHome(initial));      
        File dir = chooser.showDialog(stage);

        if (dir != null) {
            return dir.getAbsolutePath();
        }
        return "";
    }
    
    private File ifInitialEmptyUseHome(String initial){
        if (initial==null||initial.isEmpty()){
             initial=System.getProperty("user.home");
         }
        return new File(initial);
    }
}
