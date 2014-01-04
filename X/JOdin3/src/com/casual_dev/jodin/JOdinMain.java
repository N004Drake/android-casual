/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template fileToBeDownloaded, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casual_dev.jodin;


//NOTE: Runtime Error == Java is out of date. 
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author adam
 */
public class JOdinMain extends Application {

    public static Stage stage;
    public static Map<String,String> paramList;
    static String fileToBeDownloaded;
    @Override
    public void start(final Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("JOdin.fxml"));
        JOdinMain.stage = stage;
        Scene scene = new Scene(root);
        fileToBeDownloaded = this.getParameters().getNamed().get("file");
         //app.getParameters().getNamed("zip");
        stage.setScene(scene);
        stage.show();

        stage.setTitle("JODIN3      powered by Heimdall and CASUAL");
        stage.setResizable(false);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stage.close();

                CASUAL.CASUALMain.shutdown(0);
            }
        });
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
