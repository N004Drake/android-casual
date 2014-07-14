/*Provides input and output from the main User Interface
 *Copyright (C) 2014 CASUAL-Dev or Adam Outler
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
 */
package com.casual_dev.zodui;

import CASUAL.CASUALMessageObject;
import CASUAL.CASUALScriptParser;
import CASUAL.Log;
import static CASUAL.Log.level4Debug;
import CASUAL.Statics;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import com.casual_dev.zodui.Log.ZodLog;
import static com.casual_dev.zodui.ZodDownloader.getExpectedBytes;
import static com.casual_dev.zodui.ZodDownloader.getTitle;
import com.casual_dev.zodui.about.AboutController;
import com.casual_dev.zodui.contentpanel.ZodPanelContent;
import com.casual_dev.zodui.contentpanel.ZodPanelController;
import com.casual_dev.zodui.messagepanel.MessagePanelContent;
import com.casual_dev.zodui.messagepanel.MessagePanelController;
import java.io.IOException;
import static java.lang.System.exit;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import static javafx.util.Duration.millis;

/**
 *Provides input and output from the main User Interface 
 * @author adamoutler
 */
public class CASUALZodMainUI
        implements CASUAL.iCASUALUI {

    @FXML
    GridPane grid;
    @FXML
    WebView ad;
    @FXML
    Button startButton;

    BorderPane newPanel;

    Script activeScript;

    /**
     * The panel content currently displayed
     */
    public ZodPanelController panel;
    MessagePanelController message;

    /**
     * true if running in testing mode.
     * //todo: delete this later  This is from debugging.
     */
    public static boolean testmode = false;

    /**
     *Content used in in ZodPanel.
     */
    public static ZodPanelContent content = new ZodPanelContent();
    final Object messageCreationLock = new Object();
    int x = 0;
    //double movement = grid.getRowConstraints().get(0).getMaxHeight()+grid.getRowConstraints().get(1).getMaxHeight();
    boolean clicked = false;
    boolean adOpened = false;
    AtomicBoolean ready = new AtomicBoolean(true);

    /**
     * default constructor which initializes the ad and creates a zod panel.
     */
    public void AnchorController() {
        this.initializeAd();
        this.createNewZod(CASUALZodMainUI.content);
    }

    /**
     * creates a new message for display
     * @param msg a messagePanelContent object is required
     * @return 0, 1, 2 or text depending on what the user pressed.
     */
    public synchronized String createNewMessage(final MessagePanelContent msg) {

        //Start the message panel on a new thread and wait for it to create
        Platform.runLater(() -> {
            AnchorPane b = getNewMessage(msg);
            b.setPrefHeight(panel.getHeight() - 50);
            b.setMaxHeight(panel.getHeight() - 50);

            grid.add(b, 0, 0, 1, 1);
            notifyMessageCreationCompleted();
        });

        try {
            //wait for message to be created and added so we don't run into collision.
            waitForMessageCreation();
        } catch (InterruptedException ex) {
            Log.errorHandler(ex);
        }

        //Get return value from user
        String messageButtonValue = "0";
        try {
            messageButtonValue = message.getReturn();
        } catch (InterruptedException ex) {
            Log.errorHandler(ex);
        }
        //get rid of the pane
        level4Debug("User clicked Button:\"" + messageButtonValue + "\" after " + message.getCompletionTime() + "ms");
        message.disposeMessagePanel(this.grid);
        return messageButtonValue;
    }

    private void notifyMessageCreationCompleted() {
        //synchronize
        synchronized (messageCreationLock) {
            messageCreationLock.notifyAll();
        }
    }

    private void waitForMessageCreation() throws InterruptedException {
        //wait for message to be created and added.
        synchronized (messageCreationLock) {
            messageCreationLock.wait();
        }
    }

    AnchorPane getNewMessage(MessagePanelContent mpc) {
        try {

            URL location = this.getClass().getResource("/com/casual_dev/zodui/messagepanel/MessagePanel.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            Parent root = (Parent) fxmlLoader.load(location.openStream());
            this.message = (MessagePanelController) fxmlLoader.getController();
            message.createMessage(mpc);

            return (AnchorPane) root;
        } catch (IOException ex) {
            Log.errorHandler(ex);
            return null;
        }
    }

    /**
     * Creates a new Zod Panel from Zod Panel Content.
     * @param zpc content used to create new panel.
     */
    public synchronized void createNewZod(ZodPanelContent zpc) {

        /**
         * anonymous inner class to ensure there is no way to access this except here. 
         */
        class creator {
            CASUALZodMainUI ui;
            creator(CASUALZodMainUI ui) {
                this.ui = ui;
            }

            private BorderPane generateZodPanel(ZodPanelContent zpc) {

                try {
                    URL location = this.getClass().getResource("/com/casual_dev/zodui/contentpanel/ZodPanel.fxml");
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(location);
                    fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
                    Parent root = (Parent) fxmlLoader.load(location.openStream());
                    panel = (ZodPanelController) fxmlLoader.getController();
                    panel.setParentObject(ui);
                    panel.setZodPanelContent(zpc);
                    return (BorderPane) root;
                } catch (IOException ex) {
                    Log.errorHandler(ex);
                    return null;
                }
            }
        }
        if (this.panel != null) {
            this.panel.disposeZod(this.grid);
        }
        BorderPane b = new creator(this).generateZodPanel(zpc);
        b.setCacheHint(CacheHint.SPEED);
        b.setCache(true);
        TranslateTransition tt = new TranslateTransition(millis((double) 500.0), (Node) b);
        tt.setFromX(-600.0);
        tt.setToX(0.0);
        Platform.runLater(() -> {

            this.grid.add(b, 0, 0);
            tt.play();
            tt.setOnFinished((ActionEvent actionEvent) -> {
                b.setCache(false);
            });
        });
        Log.level4Debug("new panel created: " + ++x);
        this.panel.setZodPanelContent(zpc);
    }



    @FXML
    private void showLog() {
        new ZodLog().showLog(new Stage());
    }

    private void webViewScaler(boolean expansionRequested) {

    }

    @FXML
    private void webViewClicked() {

    }

    void initializeAd() {
        final String adURL = "https://builds.casual-dev.com/ad.php";
        Platform.runLater(() -> {
            final WebEngine webEngine = CASUALZodMainUI.this.ad.getEngine();
            webEngine.setJavaScriptEnabled(true);
            webEngine.load(adURL);
            webEngine.locationProperty().addListener((ChangeListener<String>) new ChangeListener<String>() {
                boolean isclicked = false;

                @Override
                public void changed(ObservableValue<? extends String> observable, String browserClaimedValue, String actualLink) {
                    System.out.println(browserClaimedValue);
                    if (!actualLink.contains(adURL) && !isclicked) {
                        isclicked = true;
                        CASUAL.network.LinkLauncher ll = new CASUAL.network.LinkLauncher(actualLink);
                        ll.launch();
                        initializeAd();
                    }
                }
            });
        });
    }

    /**
     * returns the current ZodPanelContent
     * @return content
     */
    public static ZodPanelContent getZodPanelContent() {
        return content;
    }

    /**
     * returns a list of nodes representing the children of the panel
     * @return list of nodes
     */
    public ObservableList<Node> getChildren() {
        return this.panel.getChildren();
    }

    @Override
    public boolean isReady() {
        return ready.get();
    }

    @Override
    public void setReady(boolean bln) {
        ready.set(bln);
    }

    @Override
    public boolean isDummyGUI() {
        return false;
    }

    @Override
    public String displayMessage(CASUALMessageObject cslm) {

        String retval=createNewMessage(new MessagePanelContent(cslm));
        return retval;

    }

    @Override
    public void dispose() {
        exit(0);
    }

    @Override
    @FXML
    public void StartButtonActionPerformed() {
        startButton.setDisable(true);
        //execute
        if (Statics.CASPAC.getActiveScript().extractionMethod != 2) { //not on filesystem
            Log.level4Debug("Loading internal resource: " + activeScript);
            Statics.CASPAC.getActiveScript().scriptContinue = true;
            new CASUALScriptParser().executeSelectedScript(activeCASPAC, true);
        }

    }

    @Override
    public boolean setControlStatus(boolean bln) {
        startButton.setDisable(!bln);
        return bln;
    }

    @Override
    public boolean getControlStatus() {
        return !startButton.disableProperty().get();
    }

    Caspac activeCASPAC;

    @Override
    public void setCASPAC(Caspac caspac) {
        activeCASPAC = caspac;
    }

    @Override
    public void setInformationScrollBorderText(String string) {
        ZodPanelContent zpc = new ZodPanelContent(content);
        zpc.setSubtitle(string);
        this.createNewZod(zpc);
    }

    @Override
    public void setProgressBar(int i) {
        ZodPanelContent.setProgress(i);
        panel.updateProgress();
    }

    @Override
    public void setProgressBarMax(int i) {
        ZodPanelContent.setProgressMax(x);
    }

    @Override
    public void setScript(Script script) {
        this.activeScript = script;
        content.setMainTitle(script.name);
        content.setSubtitle(script.discription);
        this.createNewZod(content);
    }

    @Override
    public void setStartButtonText(String string) {
        startButton.setText(string);
    }

    @Override
    public void setStatusLabelIcon(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStatusMessageLabel(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setWindowBannerText(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setVisible(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deviceConnected(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deviceDisconnected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deviceMultipleConnected(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBlocksUnzipped(String string) {
        Log.progress("Unzipping:" + string);
    }

    @Override
    public void sendString(final String string) {
        Platform.runLater(() -> {
            CASUALZodMainUI.content.setStatus(string);
        });
        //todo: send to log
    }

    @Override
    public void sendProgress(final String string) {
        Platform.runLater(() -> {

            //todo catch download here and send to log
            if (ZodDownloader.downloadingCASPAC.get()) {
                try {
                    panel.setStatus("Downloading " + getTitle() + ":" + string + " of " + getExpectedBytes() + "kb");
                } catch (NumberFormatException ex) {
                    panel.setStatus(string);
                }
            }
        });
    }

    @FXML
    private void showAbout() throws Exception {
        new AboutController().show();
    }

}
