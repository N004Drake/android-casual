/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cascade2.drag_event;

import CASUAL.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;

/**
 *
 * @author adamoutler
 */
public class DragEventHandler {
    
    static long time = 0;
    static List<File> fileListForDropEvent;

    @FXML

   public void setzipFileEventList(DragEvent event) {
        if (null == event) {
            return;
        }
        fileListForDropEvent = event.getDragboard().getFiles();
        Log.level4Debug("Dropped files released to list"+fileListForDropEvent);
    }
    
    public void markTimeOfDrop(){
          time = System.currentTimeMillis() + 100; 
    }
    
    public List<File>  ifTimerInRangeSetFileList(){
         if (time >= System.currentTimeMillis()) {
             return fileListForDropEvent;   
        }
    return new ArrayList<>();
    }
    
}