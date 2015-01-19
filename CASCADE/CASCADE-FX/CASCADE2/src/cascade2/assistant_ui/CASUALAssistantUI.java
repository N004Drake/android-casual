/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cascade2.assistant_ui;

import CASUAL.Log;
import cascade2.CASCADE2;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.ToolTipManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author adamoutler
 */
public class CASUALAssistantUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CASUAL Commands");

        StackPane root = new StackPane();
        VBox vbox = new VBox();
        TextInputControl ta = new TextArea();
        TreeView<Label> tree = getCasualLanguageTreeView(ta);
        vbox.getChildren().addAll(tree, ta);

        primaryStage.setScene(new Scene(vbox, 300, 250));
        primaryStage.show();
    }

    public TreeView<Label> getCasualLanguageTreeView(TextInputControl ctl) {
  

        for (int i=0; i<3; i++){
        try {
            TreeItem<Label> rootItem = new TreeItem<>(new Label("Commands-hover for description"));
            rootItem.setExpanded(true);
            TreeView<Label> tree = new TreeView<>(rootItem);
            tree.setEditable(false);
            Document casualCommandsAndVariables = Jsoup.connect("http://casual-dev.com/casual-commands-and-variables/").get();
            Elements sections = casualCommandsAndVariables.select("section");

            sections.stream().map((e) -> {
                System.out.println(">>>>>>>SECTION:    " + e.attr("data-name"));
                return e;
            }).map((Element e) -> {
                TreeItem<Label>  section = new TreeItem<>(new Label(e.attr("data-name")));
                System.out.println(e.getElementsByTag("p").get(1).text());
                Elements cmds = e.getElementsByTag("article");
                cmds.stream().forEach((cmd) -> {
                    //get strings and then remove so whatever is left is the tooltip.
                    Elements commandName = cmd.getElementsByTag("li");
                    Elements commandCode = cmd.getElementsByTag("pre");
                    String name = commandName.text();
                    String code = commandCode.text();
                    commandName.remove();
                    commandCode.remove();
                    String tooltip = cmd.text();

                    //create a label to apply the tooltip.
                    Label label = new Label(name);
                    Platform.runLater(()->{
                            setTooltip(tooltip, label, code, ctl);
                    });
                    TreeItem<Label> l=new TreeItem<>(label);
                    section.getChildren().add(l);
                    section.setExpanded(false);
                    System.out.println("--" + name);
                    System.out.println("----" + tooltip);
                    System.out.println("####" + code);
                });
                return section;
            }).forEach((section) -> {
                rootItem.getChildren().add(section);
            });
        return tree;
        } catch (IOException ex) {
           Log.level4Debug("Could not connect to server in a timely manner.  retrying.");
        }
           
        }
        return new TreeView<Label>();
    }

    private final int defaultDismissTimeout = ToolTipManager.sharedInstance().getDismissDelay();
    private void setTooltip(String tooltip, Label label, String code, TextInputControl ctl) {
        Tooltip tip=new Tooltip(tooltip);
        tip.setPrefWidth(300);
        tip.setWrapText(true);
        label.setOnMouseMoved((MouseEvent mouseEvent) -> {
            tip.show( CASCADE2.getScene().getWindow());
        });
        label.setOnMouseExited((MouseEvent mouseEvent) -> {
            tip.hide();
        });
        sendCodeToTextOnMouseClicked(label, code, ctl);
        
    }

    private void sendCodeToTextOnMouseClicked(Label label, String codeText, TextInputControl ctl) {
        label.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    ctl.replaceSelection(codeText);
                }
            }
        });
                
    }

}
