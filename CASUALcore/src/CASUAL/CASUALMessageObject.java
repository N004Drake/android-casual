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
import java.util.Arrays;

/**
 * CASUALInteraction is the place where all User Interactions are initiated.
 * @author AdamOutler
 */
public class CASUALMessageObject {

    /**
     * The message sent into the CASUALMessageObject. Original message includes
     * Title and Message delimited by the first comma or >>>.
     */
    public String originalMessage = ""; //for use with translations

    /**
     * Return values expected (for use as a guide only). The expected returns
     * for all are 0 for acknowledged, ok, yes or any other positive. 1 is for
     * cancel or no.
     */
    public String expectedReturn = "";

    /**
     * The title of the message.
     */
    public String title;

    /**
     * The text of the message.
     */
    public String messageText;

    /**
     * Type of message commanded by this MessageObject
     * iCASUALInteraction.INTERACTION_TIME_OUT=0;
     * iCASUALInteraction.INTERACTION_USER_CANCEL_OPTION=1;
     * iCASUALInteraction.INTERACTION_ACTION_REUIRED=2;
     * iCASUALInteraction.INTERACTION_USER_NOTIFICATION=3;
     * iCASUALInteraction.INTERACTION_SHOW_INFORMATION=4;
     * iCASUALInteraction.INTERACTION_SHOW_ERROR=5;
     * iCASUALInteraction.INTERACTION_SHOW_YES_NO=6;
     * iCASUALInteraction.INTERACTION_INPUT_DIALOG=7;
     * iCASUALInteraction.INTERACTION_COMMAND_NOTIFICATION=8;
     */
    public int messageType;

    /**
     * Used by jOptionPane for TimeOutMessages only. Made available for use
     * under other APIs so that it may be changed if needed.
     *
     * @see JOptionPane.OptionType
     */
    public int timeoutOptionType;

    /**
     * Used by TimeOutMessages only. Specifies the default value for timeout
     * upon timeout.
     */
    public Object timeoutInitialValue;

    /**
     * Used by TimeOutMessages only. Specifies the options for the
     * TimeOutMessage
     */
    public Object[] timeoutOptions;

    /**
     * Used by TimeOutMessages only. Specifies the amount of time the message
     * should be displayed before timing out and returning the default value
     */
    public int timeoutPresetTime;

    /**
     * Used by jOptionPane for TimeOutMessages only. Made available for use
     * under other APIs so that it may be changed if needed.
     *
     * @see JOptionPane.MESSAGETYPE
     */
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
                messageText = translation;
            }
        } else {
            if (messageInput.contains(">>>")) {
                originalMessage = messageInput;
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
     * @return value chosen 0 for first, 1 for second...
     */
    public int showTimeoutDialog(final int PRESET_TIME, Component parentComponent, int optionType, int timeOutMessageType, Object[] options, final Object initialValue) {
        this.timeoutOptionType = optionType;
        this.timeoutMessageType = timeOutMessageType;
        this.timeoutOptions = options;
        this.timeoutInitialValue = initialValue;
        this.timeoutPresetTime = PRESET_TIME;
        this.messageType = iCASUALInteraction.INTERACTION_TIME_OUT;
        expectedReturn = "(String)int from " + Arrays.asList(options).toString();
        return Integer.parseInt(Statics.interaction.displayMessage(this));
    }

    /**
     * shows an input dialog
     *
     * @return value from user input
     * @throws HeadlessException
     */
    public String inputDialog() throws HeadlessException {
        this.messageType = iCASUALInteraction.INTERACTION_INPUT_DIALOG;
        expectedReturn = "Any String";
        return Statics.interaction.displayMessage(this);
    }

    /**
     * shows action required dialog
     *
     * @return 1 if user didn't do it, or 0 if user did it.
     * @throws HeadlessException
     */
    public int showActionRequiredDialog() throws HeadlessException {
        this.messageType = iCASUALInteraction.INTERACTION_ACTION_REUIRED;
        expectedReturn = "String 0-continue, 1-stop";
        return Integer.parseInt(Statics.interaction.displayMessage(this));
    }

    /**
     * displays user cancel option
     *
     * @return 1 if cancel was requested
     */
    public int showUserCancelOption() {
        this.messageType = iCASUALInteraction.INTERACTION_USER_CANCEL_OPTION;
        expectedReturn = "String 0-continue, 1-stop";
        return Integer.parseInt(Statics.interaction.displayMessage(this));
    }

    /**
     * displays command notification
     *
     * @throws HeadlessException
     */
    public void showCommandNotification() throws HeadlessException {
        this.messageType = iCASUALInteraction.INTERACTION_COMMAND_NOTIFICATION;
        expectedReturn = "Empty";
        Statics.interaction.displayMessage(this);
    }

    /**
     * displays user notification
     *
     * @throws HeadlessException
     */
    public void showUserNotification() throws HeadlessException {
        this.messageType = iCASUALInteraction.INTERACTION_USER_NOTIFICATION;
        expectedReturn = "Empty";
        Statics.interaction.displayMessage(this);
    }

    /**
     * displays information message
     *
     * @throws HeadlessException
     */
    public void showInformationMessage() throws HeadlessException {
        this.messageType = iCASUALInteraction.INTERACTION_SHOW_INFORMATION;
        expectedReturn = "Empty";
        new Log().level3Verbose("showing information message object");
        Statics.interaction.displayMessage(this);
        new Log().level3Verbose("Done with message object");
    }

    /**
     * displays error message
     *
     * @throws HeadlessException
     */
    public void showErrorDialog() throws HeadlessException {
        this.messageType = iCASUALInteraction.INTERACTION_SHOW_ERROR;
        expectedReturn = "Empty";
        Statics.interaction.displayMessage(this);

    }

    /**
     * displays a Yes/No dialog
     *
     * @return true if yes, false if no
     */
    public boolean showYesNoOption() {
        this.messageType = iCASUALInteraction.INTERACTION_SHOW_ERROR;
        Boolean retval = Statics.interaction.displayMessage(this).equals("true");
        expectedReturn = "String 0-yes, 1-no";
        return retval;
    }
}
