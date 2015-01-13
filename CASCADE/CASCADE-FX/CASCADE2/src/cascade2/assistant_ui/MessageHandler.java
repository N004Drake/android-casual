/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cascade2.assistant_ui;

import CASUAL.CASUALMessageObject;
import CASUAL.Log;
import CASUAL.Statics;
import cascade2.CASCADE2;
import cascade2.CASCADEUIController;
import java.util.Optional;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Skin;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author adamoutler
 */
public class MessageHandler extends Application {

    final private static String title = "CASCADE";

    public String sendMessage(CASUALMessageObject mo) {
        String retval = displayMessage(mo);
        return retval;

    }

    public static void main(String args[]) {
        launch(args);

    }

    synchronized public static String displayMessage(CASUALMessageObject messageObject) {
        Log.Level1Interaction(messageObject.toString());

        String[] message = new String[]{messageObject.title, messageObject.messageText};
        String returnValue = "";
        switch (messageObject.category) {
            case ACTIONREQUIRED:
                return displaySurface(messageObject, new String[]{"I did it", "I didn' do it"}, AlertType.CONFIRMATION);

            case COMMANDNOTIFICATION:
                return displaySurface(messageObject, new String[]{"OK"}, AlertType.INFORMATION);

            case TEXTINPUT:
                return displaySurface(messageObject, new String[]{"OK", "Cancel"}, AlertType.CONFIRMATION);

            case SHOWERROR:
                return displaySurface(messageObject, new String[]{"OK"}, AlertType.ERROR);

            case SHOWINFORMATION:
                return displaySurface(messageObject, new String[]{"OK"}, AlertType.INFORMATION);

            case SHOWYESNO:
                return displaySurface(messageObject, new String[]{"Yes", "no"}, AlertType.CONFIRMATION);

            case TIMEOUT:
                return displaySurface(messageObject, new String[]{"Ok"}, AlertType.NONE);

            case USERCANCELOPTION:
                return displaySurface(messageObject, new String[]{"OK", "Cancel"}, AlertType.WARNING);

            case USERNOTIFICATION:
                return displaySurface(messageObject, new String[]{"OK"}, AlertType.INFORMATION);

        }

        return returnValue;

    }
    private static Text textHolder = new Text();
    private static double oldHeight = 0;

    static SimpleIntegerProperty count = new SimpleIntegerProperty(40);

    @FXML
    private static String displaySurface(CASUALMessageObject cmo, final String[] buttonText, AlertType type) {
        Alert dialogBox = new Alert(type);
       dialogBox.setTitle(title);
        ButtonType[] buttonTypes = convertButtonTextToButtonTypes(buttonText);
       dialogBox.getButtonTypes().setAll(buttonTypes);
        dialogBox.setHeaderText(cmo.title);
        dialogBox.setContentText(cmo.messageText);

        TextArea userMessage = new TextArea(cmo.messageText);

        userMessage.setText(cmo.messageText);
        userMessage.setWrapText(true);
        userMessage.setStyle(" -fx-background-color: transparent;");
        userMessage.setEditable(false);
        final Text text=new Text(cmo.messageText);
        userMessage.setPrefWidth(600);
        text.setWrappingWidth(600);
        userMessage.setPrefHeight(text.getLayoutBounds().getHeight()+50);
        userMessage.skinProperty().addListener(new ChangeListener<Skin<?>>() {

            @Override
            public void changed(
                    ObservableValue<? extends Skin<?>> ov, Skin<?> t, Skin<?> t1) {
                if (t1 != null && t1.getNode() instanceof Region) {
                    Region r = (Region) t1.getNode();
                    r.setBackground(Background.EMPTY);

                    r.getChildrenUnmodifiable().stream().
                            filter(n -> n instanceof Region).
                            map(n -> (Region) n).
                            forEach(n -> n.setBackground(Background.EMPTY));

                    r.getChildrenUnmodifiable().stream().
                            filter(n -> n instanceof Control).
                            map(n -> (Control) n).
                            forEach(c -> c.skinProperty().addListener(this)); // *
                }
            }
        });


       
        
        dialogBox.getDialogPane().setContent(userMessage);
        userMessage.autosize();
        dialogBox.getDialogPane().autosize();

        if (cmo.category.equals(cmo.category.TEXTINPUT)) {
            TextField userInput = new TextField();
            userMessage.setPrefHeight(userMessage.getPrefHeight()+40);
            dialogBox.getDialogPane().setContent(new VBox(userMessage, userInput));
            return showTextInputDialog(dialogBox, userInput);
        } else {
            Optional clickResult = showDialogWithCustomButtons(dialogBox);
            String result = null;
            result = getButtonNumberPressedFromOptional(buttonTypes, clickResult, result);
            return result;
        }
    }

    private static Optional showDialogWithCustomButtons(Alert dialogBox) {

        return dialogBox.showAndWait();
    }

    private static String showTextInputDialog(Alert dialogBox, TextField userInput) {
        ButtonType[] buttonText = new ButtonType[]{new ButtonType("Submit")};

        if (null != CASCADE2.getScene()) {
            dialogBox.initOwner(CASCADE2.getScene().getWindow());
        }

//        dialogBox.getDialogPane().setPrefWidth(400);
        dialogBox.getDialogPane().getButtonTypes().setAll(buttonText);
        Optional o = dialogBox.showAndWait();
        return userInput.getText();
    }

    private static String getButtonNumberPressedFromOptional(ButtonType[] buttonTypes, Optional clickResult, String result) {
        for (int i = 0; i < buttonTypes.length; i++) {
            if (buttonTypes[i].equals(clickResult.get())) {
                System.out.println(((ButtonType) clickResult.get()).getButtonData());
                result = Integer.toString(i);
            }
        }
        return result;
    }

    private static ButtonType[] convertButtonTextToButtonTypes(final String[] buttonText) {
        ButtonType[] buttonTypes = new ButtonType[buttonText.length];
        for (int i = 0; i < buttonTypes.length; i++) {
            buttonTypes[i] = new ButtonType(buttonText[i]);
        }
        return buttonTypes;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        CASUALMessageObject cmo = new CASUALMessageObject("This is a title", "Here is a message.  This message is intended to give lots of information about what is supposed to happen here.  If it gets too long we neeed to handle that. "+"Here is a message.  This message is intended to give lots of information about what is supposed to happen here.  If it gets too long we neeed to handle that. "+"Here is a message.  This message is intended to give lots of information about what is supposed to happen here.  If it gets too long we neeed to handle that. "+"Here is a message.  This message is intended to give lots of information about what is supposed to happen here.  If it gets too long we neeed to handle that. ");
        Statics.GUI = new CASCADEUIController();
        try {
            /* cmo.title = "show yes no option";
             System.out.println(cmo.showYesNoOption());
             cmo.title = "action Required";
             System.out.println(cmo.showActionRequiredDialog());
             cmo.title = "error";
             cmo.showErrorDialog();
             cmo.title = "information message";
             cmo.showInformationMessage();
             cmo.title = "user cancel";
             System.out.println(cmo.showUserCancelOption());
             cmo.title = "user notification";
             cmo.showUserNotification();*/
            cmo.title = "show yes no";
            System.out.println(cmo.showYesNoOption());
            cmo.title = "return string";
            System.out.println(cmo.inputDialog());

        } catch (NullPointerException ex) {
            Log.errorHandler(ex);
        }

    }

}
