/*
 * Copyright (C) 2013 adamoutler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package GUI.CommandLine;

import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import CASUAL.CASUALMessageObject;
import CASUAL.Log;
import CASUAL.Statics;
import CASUAL.iCASUALUI;
import static CASUAL.iCASUALUI.INTERACTION_ACTION_REUIRED;
import static CASUAL.iCASUALUI.INTERACTION_COMMAND_NOTIFICATION;
import static CASUAL.iCASUALUI.INTERACTION_INPUT_DIALOG;
import static CASUAL.iCASUALUI.INTERACTION_SHOW_ERROR;
import static CASUAL.iCASUALUI.INTERACTION_SHOW_INFORMATION;
import static CASUAL.iCASUALUI.INTERACTION_SHOW_YES_NO;
import static CASUAL.iCASUALUI.INTERACTION_TIME_OUT;
import static CASUAL.iCASUALUI.INTERACTION_USER_CANCEL_OPTION;
import static CASUAL.iCASUALUI.INTERACTION_USER_NOTIFICATION;
import GUI.development.TimeOutOptionPane;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.YES_OPTION;

/**
 *
 * @author adamoutler
 */
public class CommandLineUI implements iCASUALUI {

    private int progressMax=0;
    private void msg(String msg){
        Log.level3Verbose("[UI]"+msg);
    }
    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReady(boolean ready) {
        msg("ready requested");
    }

    @Override
    public boolean isDummyGUI() {
        
        msg("is dummy gui requested");
        return false;
    }

    @Override
    public void setDummyGUI(boolean dummy) {
        
        
        msg("set dummy gui requested");
        
    }

   @Override
    public String displayMessage(CASUALMessageObject messageObject) {
        int messageType = messageObject.messageType;
        String title = messageObject.title;
        String messageText = messageObject.messageText;
        String retval = "";
        switch (messageType) {
            case INTERACTION_TIME_OUT:
                return showTimeOutInteraction(messageObject, messageText, title);
            case INTERACTION_ACTION_REUIRED:
                return showActionRequiredInteraction(messageText, title);
            //break;// unreachable

            case INTERACTION_USER_CANCEL_OPTION:
                return showUserCancelOptionInteraction(title, messageText);               //break; unreachable

            case INTERACTION_USER_NOTIFICATION:
                showUserNotificationInteraction(title, messageText);
                break;

            case INTERACTION_SHOW_INFORMATION:
                showInformationInteraction(messageText, title);
                break;

            case INTERACTION_SHOW_ERROR:
                showErrorInteraction(messageText, title);
                break;

            case INTERACTION_SHOW_YES_NO:
                return showYesNoInteraction(title, messageText);
            //break; unreachable

            case INTERACTION_INPUT_DIALOG:
                return showInputDialog(title, messageText);
            //break; unreachable

            case INTERACTION_COMMAND_NOTIFICATION:
                showUserNotificationInteraction(title, messageText);
                return messageText;
        }
        return retval;
    }

    /**
     * grabs input from Statics.in (usually stdin).
     * @return string value containing user input truncated by enter key.
     */
    public String getCommandLineInput() {
        try {
            Log.out.flush();
            String s = Statics.in.readLine();
            if (s == null) {
                while (s == null) {
                    s = Statics.in.readLine();
                }
            }
            return s;
        } catch (IOException ex) {
            Log.errorHandler(ex);
            return "";
        }
    }

    private void waitForStandardInputBeforeContinuing() {
        getCommandLineInput();
    }

    private String showTimeOutInteraction(CASUALMessageObject messageObject, String messageText, String title) {

            Log.Level1Interaction("[STANDARDMESSAGE]" + title + "\n" + messageText + "\n[RESPONSEEXPECTED]");
            String s = getCommandLineInput();
            if (s == null || s.equals("")) {
                return "0";
            }
            return "1";
    }

    private String showActionRequiredInteraction(String messageText, String title) throws HeadlessException {
        String retval;
        int n = 9999;

            while (n != 0 && n != 1) {
                Log.Level1Interaction("[ACTIONREQUIRED][Q or RETURN]" + title + "\n" + messageText + "\npress Q to quit" + "\n[RESPONSEEXPECTED]");
                retval = getCommandLineInput();
                if (!retval.equals("q") && !retval.equals("Q") && !retval.equals("")) {
                    n = new CASUALMessageObject(messageText).showActionRequiredDialog();
                } else if (retval.equals("Q") || retval.equals("q")) {
                    n = 1;
                } else {
                    n = 0;
                }
            }
        return Integer.toString(n);
        //break;// unreachable
    }

    private String showUserCancelOptionInteraction(String title, String messageText) throws HeadlessException {
        int cancelReturn;

            Log.Level1Interaction("[CANCELOPTION][Q or RETURN]" + title + "\n" + messageText + "\npress Q to quit" + "\n[RESPONSEEXPECTED]");
            String s = this.getCommandLineInput();
            if (s.equals("q") || s.equals("Q")) {
                cancelReturn = 1;
            } else {
                cancelReturn = 0;
            }
        return Integer.toString(cancelReturn);
        //break; unreachable
    }

    private void showUserNotificationInteraction(String title, String messageText) throws HeadlessException {

            Log.Level1Interaction("[NOTIFICATION][RETURN]" + title + "\n" + messageText + "  Press any key to continue." + "\n[RESPONSEEXPECTED]");
            waitForStandardInputBeforeContinuing();
    }

    private void showInformationInteraction(String messageText, String title) throws HeadlessException {

            Log.Level1Interaction("[INFOMESSAGE][RETURN]" + title + "\n" + messageText + "  Press any key to continue." + "\n[RESPONSEEXPECTED]");
            waitForStandardInputBeforeContinuing();
    }

    private void showErrorInteraction(String messageText, String title) throws HeadlessException {
            Log.Level1Interaction("[ERRORMESSAGE][RETURN]" + title + "\n" + messageText + "  Press any key to continue." + "\n[RESPONSEEXPECTED]");
            waitForStandardInputBeforeContinuing();

    }

    private String showYesNoInteraction(String title, String messageText) throws HeadlessException {
        Log.level4Debug("Displaying Yes/No Dialog: " + title + " message: " + messageText + "\n[RESPONSEEXPECTED]");

            //display the messageText
            Log.Level1Interaction("[YESNOOPTION][RETURN or n]" + title + "\n" + messageText + "\npress N for no" + "\n[RESPONSEEXPECTED]");
            String s = this.getCommandLineInput();
            if (s.equals("n") || s.equals("N")) {
                return "false";
            } else {
                return "true";
            }
     
    }

    private String showInputDialog(String title, String messageText) throws HeadlessException {
        Log.level4Debug("Requesting User Input.. Title:" + title + " -message:" + messageText + "\n[RESPONSEEXPECTED]");
        messageText = "<html>" + messageText.replace("\\n", "\n");
        
            Log.Level1Interaction("[INPUT][ANY]" + title + messageText + "\n input:");
            return getCommandLineInput();
      
        //break; unreachable
    }

    @Override
    public void dispose() {
       msg("Dispose Commanded");
    }

    @Override
    public void StartButtonActionPerformed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String comboBoxGetSelectedItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void comboBoxScriptSelectorAddNewItem(String item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean setControlStatus(boolean status) {
        msg("control status requested:"+status);
        return status;
    }

    @Override
    public boolean getControlStatus() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCASPAC(Caspac caspac) {
        msg("Setting caspac"+caspac);
        Statics.CASPAC=caspac;
    }

    @Override
    public void setInformationScrollBorderText(String title) {

        msg("boarder title change requested:"+title);
    }

    @Override
    public void setProgressBar(int value) {
        msg("Progress percent:"+value);
        
    }

    @Override
    public void setProgressBarMax(int value) {
       msg("Progress bar max"+value);
       progressMax=value;
    }
    
    @Override
    public void setScript(Script s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStartButtonText(String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStatusLabelIcon(String Icon, String text) {
        msg("Status Label "+text);
    }

    @Override
    public void setStatusMessageLabel(String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setWindowBannerImage(BufferedImage icon, String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setWindowBannerText(String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deviceConnected(String mode) {
       msg ("Device connected");
      
    }

    @Override
    public void deviceDisconnected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deviceMultipleConnected(int numberOfDevicesConnected) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void notificationPermissionsRequired() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void notificationCASUALSound() {
        msg("casualSound Requested");
    }

    @Override
    public void notificationInputRequested() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void notificationGeneral() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void notificationRequestToContinue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void notificationUserActionIsRequired() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setThisAsGUI(){
        Statics.GUI=this;
    }

    @Override
    public void setBlocksUnzipped(int blocks) {
        msg("Progress percent:"+blocks);

    }
}
