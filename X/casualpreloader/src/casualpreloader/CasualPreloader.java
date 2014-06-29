package casualpreloader;
/*CasualPreloader is used for loading CASUAL related items. 
 *Copyright (C) 2013  Adam Outler
 *Heavily Modified from Oracle JavaFX Ensemble Demo. 
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
 *  along with this program.  If not, see &lt;http://www.gnu.org/licenses/&gt;.
 * 
 * 
 */
import com.sun.javaws.progress.Progress;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
 import javafx.application.Preloader;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
/**
 * Modified from Oracle JavaFX Ensemble Demo.
 * This is a splash
 */
public class CasualPreloader extends Preloader{
    //variables for storing initial position of the stage at the beginning of drag
    Stage stage;
    ProgressBar bar;
    Text text;
    private void init(Stage primaryStage) {
                
                //create stage which has set stage style transparent
                 stage = new Stage(StageStyle.TRANSPARENT);
                //create root node of scene, i.e. group
                Group rootGroup = new Group();
                //create scene with set width, height and color
                Scene scene = new Scene(rootGroup, 300, 300, Color.TRANSPARENT);
                //set scene to stage
                stage.setScene(scene);
                //center stage on screen
                stage.centerOnScreen();
                //show the stage
                stage.show();
                ProgressIndicator pi = new ProgressIndicator();
                pi.setProgress(-.1);
                pi.setMinWidth(240);
                pi.setMinHeight(240);
                pi.setStyle("-fx-progress-color: darkgray");
                pi.setOpacity(.1);
                bar=new ProgressBar();
                bar.setStyle("-fx-accent: green;");
                bar.setScaleY(2);
                // CREATE SIMPLE TEXT NODE
                text = new Text("CASUAL"); //20, 110,
                text.setFill(Color.WHITESMOKE);
                text.setEffect(new Lighting());
                text.setBoundsType(TextBoundsType.VISUAL);
                text.setFont(Font.font(Font.getDefault().getFamily(), 30));
 
                // USE A LAYOUT VBOX FOR EASIER POSITIONING OF THE VISUAL NODES ON SCENE
                VBox vBox = new VBox();
                vBox.setSpacing(10);
                vBox.setPadding(new Insets(90, 0, 0, 60));
                vBox.setAlignment(Pos.TOP_CENTER);
                vBox.getChildren().addAll(text,bar);
                
                //add all nodes to main root group
                rootGroup.getChildren().addAll(vBox,pi);
    }
 
    @Override 
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        //primaryStage.show();
     
    }
    @Override
    public void handleStateChangeNotification(StateChangeNotification scn) {
        if (scn.getType() == StateChangeNotification.Type.BEFORE_START) {
             FadeTransition ft = new FadeTransition(
                    Duration.millis(10000), stage.getScene().getRoot());
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            final Stage s = stage;
            EventHandler<ActionEvent> eh = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    s.hide();
                    s.close();
                }
            };
            ft.setOnFinished(eh);
            ft.play();
                
        }
    }
    
    boolean finalnotification=false;
    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        bar.setProgress(pn.getProgress());
        if (pn.getProgress()>.98 && finalnotification==false){
            finalnotification=true;
            FadeTransition ft = new FadeTransition(
                    Duration.millis(1000),bar);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            final Stage s = stage;
            EventHandler<ActionEvent> eh = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    text.setText(text.getText()+"\npreparing");
                    ScaleTransition st = new ScaleTransition(Duration.millis(1000),text);
                    st.setFromX(1);
                    st.setToX(1.5);
                    st.setFromY(1);
                    st.setToY(1.5);
                    st.play();
                }
            };
            ft.setOnFinished(eh);
            ft.play();
        }
    }   
    public static void main(String[] args) { launch(args); }
}