/*CASUALInteraction is the user interface class
 *Copyright (C) 2013  Adam Outler
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
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package CASUAL;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.YES_OPTION;

/**
 *
 * @author adam
 */
public class CASUALMessageObject extends JOptionPane implements iCASUALInteraction {

    public String title;
    public String messageText;
    String originalMessage = "";
    public int messageType;

    //for timeout option pane
    public int timeoutOptionType;
    public Object timeoutInitialValue;
    public Object[] timeoutOptions;
    public int timeoutPresetTime;
    public int timeoutMessageType;
    /**
     * instantiates an interaction
     *
     * @param messageInput can be title,message or title>>>message, or just
     * message and title will be automatically chosen
     */
    public CASUALMessageObject(String messageInput) {
        if (messageInput.startsWith("@")) {
            String translation = Translations.get(messageInput);
            if (translation.contains(">>>")) {
                originalMessage = messageInput;
                String[] s = translation.split(">>>", 2);
                //messageText=s[1].replace("\n","\\n");
                title = s[0];
                messageText = s[1];
            } else {
                title = null;
                message = translation;
            }
        } else {
            if (messageInput.contains(">>>")) {
                String[] s = messageInput.split(">>>", 2);
                //messageText=s[1].replace("\n","\\n");
                title = s[0];
                messageText = s[1];
            } else {
                this.title = null;
                this.messageText = messageInput;
            }
        }
    }

    /**
     * instantiates a CASUALInteraction
     *
     * @param title title to display on interaction
     * @param messageInput message to display on interaction
     */
    public CASUALMessageObject(String title, String messageInput) {
        this.title = title;
        this.messageText = messageInput;
    }
    /**
     * CASUALInteraction input device
     */


    /**
     * shows a TimeOutDialog
     *
     * @param PRESET_TIME time to show message
     * @param parentComponent where to hover over
     * @param optionType jOptionPane.OPTION_
     * @param timeOutMessageType jOptionPane.MESSAGETYPE
     * @param options array of options
     * @param initialValue value to choose if none other are chosen
     * @return
     */
    public int showTimeoutDialog(final int PRESET_TIME, Component parentComponent, int optionType, int timeOutMessageType, Object[] options, final Object initialValue) {
        this.timeoutOptionType=optionType;
        this.timeoutMessageType=timeOutMessageType;
        this.timeoutOptions=options;
        this.timeoutInitialValue=initialValue;
        this.timeoutPresetTime=PRESET_TIME;
        this.messageType=iCASUALInteraction.INTERACTION_TIME_OUT;
        return Integer.parseInt(Statics.interaction.displayMessage(this));
    }



    

   
    /**
     * shows an input dialog
     *
     * @return value from user input
     * @throws HeadlessException
     */
    public String inputDialog() throws HeadlessException {
        this.messageType=iCASUALInteraction.INTERACTION_INPUT_DIALOG;
        return Statics.interaction.displayMessage(this);
    }

    /**
     * shows action required dialog
     *
     * @return 1 if user didn't do it, or 0 if user did it.
     * @throws HeadlessException
     */
    public int showActionRequiredDialog() throws HeadlessException {
        this.messageType=iCASUALInteraction.INTERACTION_ACTION_REUIRED;
        return Integer.parseInt(Statics.interaction.displayMessage(this));
    }

    /**
     * displays user cancel option
     *
     * @return 1 if cancel was requested
     */
    public int showUserCancelOption() {
        this.messageType=iCASUALInteraction.INTERACTION_USER_CANCEL_OPTION;
        return Integer.parseInt(Statics.interaction.displayMessage(this));
    }

    /**
     * displays user notification
     *
     * @throws HeadlessException
     */
    public void showUserNotification() throws HeadlessException {
        this.messageType=iCASUALInteraction.INTERACTION_USER_NOTIFICATION;
        Statics.interaction.displayMessage(this);
        return;
    }

    /**
     * displays information message
     *
     * @throws HeadlessException
     */
    public void showInformationMessage() throws HeadlessException {
        this.messageType=iCASUALInteraction.INTERACTION_SHOW_INFORMATION;
        Statics.interaction.displayMessage(this);
        return;
    }

    /**
     * displays error message
     *
     * @throws HeadlessException
     */
    public void showErrorDialog() throws HeadlessException {
        this.messageType=iCASUALInteraction.INTERACTION_SHOW_ERROR;
        Statics.interaction.displayMessage(this);
        return;
        
       
    }

    /**
     * displays a Yes/No dialog
     *
     * @return true if yes, false if no
     */
    public boolean showYesNoOption() {
        this.messageType=iCASUALInteraction.INTERACTION_SHOW_ERROR;
        Boolean retval=Statics.interaction.displayMessage(this).equals("true");
        return retval;
    }

   
    @Override
    public String displayMessage(CASUALMessageObject messageObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
