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

import CASUAL.GUI.TimeOutOptionPane;
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
public class CASUALInteraction extends JOptionPane {

    String title;
    String messageText;
    String originalMessage = "";

    /**
     * instantiates an interaction
     *
     * @param messageInput can be title,message or title>>>message, or just
     * message and title will be automatically chosen
     */
    public CASUALInteraction(String messageInput) {
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
    public CASUALInteraction(String title, String messageInput) {
        this.title = title;
        this.messageText = messageInput;
    }
    /**
     * CASUALInteraction input device
     */
    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    /**
     * shows a TimeOutDialog
     *
     * @param PRESET_TIME time to show message
     * @param parentComponent where to hover over
     * @param optionType jOptionPane.OPTION_
     * @param messageType jOptionPane.MESSAGETYPE
     * @param options array of options
     * @param initialValue value to choose if none other are chosen
     * @return
     */
    public int showTimeoutDialog(final int PRESET_TIME, Component parentComponent, int optionType, int messageType, Object[] options, final Object initialValue) {
        if (!originalMessage.equals("")) {
            sendLog("[STANDARDMESSAGE]" + originalMessage + "\n[RESPONSEEXPECTED]");
        }
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            return new TimeOutOptionPane().timeoutDialog(PRESET_TIME, parentComponent, messageText, title, optionType, messageType, options, initialValue);
        } else {
            new Log().Level1Interaction("[STANDARDMESSAGE]" + title + "\n" + messageText + "\n[RESPONSEEXPECTED]");
            String s = getCommandLineInput();
            if (s == null || s.equals("")) {
                return 0;
            }
            return 1;
        }
    }
    static String cmdlineinput = "";

    /**
     * gets command line input from interaction input device
     *
     * @return commandline input
     */
    public String getCommandLineInput() {
        try {
            Log.out.flush();
            String s = in.readLine();
            if (s == null) {
                while (s == null) {
                    s = in.readLine();
                }
            }
            return s;
        } catch (IOException ex) {
            new Log().errorHandler(ex);
            return "";
        }
    }

    private void waitForStandardInputBeforeContinuing() {
        getCommandLineInput();
    }

    /**
     * shows an input dialog
     *
     * @return value from user input
     * @throws HeadlessException
     */
    public String inputDialog() throws HeadlessException {
        if (!originalMessage.equals("")) {
            sendLog("[INPUT][ANY]" + originalMessage);
        }
        new Log().level4Debug("Requesting User Input.. Title:" + title + " -message:" + messageText + "\n[RESPONSEEXPECTED]");
        messageText = "<html>" + messageText.replace("\\n", "\n");
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            if (title == null) {

                return (String) JOptionPane.showInputDialog((Component) Statics.GUI, messageText, "Input Required", JOptionPane.QUESTION_MESSAGE);
            } else {
                return (String) JOptionPane.showInputDialog((Component) Statics.GUI, messageText, title, JOptionPane.QUESTION_MESSAGE);
            }
        } else {
            new Log().Level1Interaction("[INPUT][ANY]" + title + messageText + "\n input:");
            return getCommandLineInput();
        }
    }

    /**
     * shows action required dialog
     *
     * @return 0 if user didn't do it, or 1 if user did it.
     * @throws HeadlessException
     */
    public int showActionRequiredDialog() throws HeadlessException {
        if (!originalMessage.equals("")) {
            sendLog("[USERTASK][Q or RETURN][CRITICAL]" + originalMessage + "\n[RESPONSEEXPECTED]");
        }
        new Log().level4Debug("Displaying Action Is Required Dialog:" + messageText);
        int n = 9999;
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            Object[] Options = {"I did it", "I didn't do it"};
            messageText = "<html>" + messageText.replace("\\n", "<BR>");

            n = JOptionPane.showOptionDialog(
                    (Component) Statics.GUI,
                    messageText,
                    "Dont click through this!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    Options,
                    Options[1]);
        } else {
            while (n != 0 && n != 1) {
                new Log().Level1Interaction("[ACTIONREQUIRED][Q or RETURN]" + title + "\n" + messageText + "\npress Q to quit" + "\n[RESPONSEEXPECTED]");
                String retval = getCommandLineInput();
                if (!retval.equals("q") && !retval.equals("Q") && !retval.equals("")) {
                    n = new CASUALInteraction(messageText).showActionRequiredDialog();
                } else if (retval.equals("Q") || retval.equals("q")) {
                    n = 1;
                } else {
                    n = 0;
                }
            }
        }
        return n;
    }

    /**
     * displays user cancel option
     *
     * @return 0 if cancel was requested
     */
    public int showUserCancelOption() {
        if (!originalMessage.equals("")) {
            sendLog("[CANCELOPTION][Q or RETURN]" + originalMessage + "\n[RESPONSEEXPECTED]");
        }
        new Log().level4Debug("Displaying User Cancel Option Dialog title: " + title + " message: " + messageText + "\n[RESPONSEEXPECTED]");

        int n;
        Object[] Options = {"Continue", "Stop"};
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            if (title == null) {
                n = JOptionPane.showOptionDialog(
                        (Component)Statics.GUI,
                        messageText,
                        "Do you wish to continue?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Options,
                        Options[1]);
            } else {
                n = JOptionPane.showOptionDialog(
                        (Component)Statics.GUI,
                        messageText,
                        title,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Options,
                        Options[1]);
            }
        } else {
            new Log().Level1Interaction("[CANCELOPTION][Q or RETURN]" + title + "\n" + messageText + "\npress Q to quit" + "\n[RESPONSEEXPECTED]");
            String s = this.getCommandLineInput();
            if (s.equals("q") || s.equals("Q")) {
                return 1;
            }
            return 0;
        }
        return n;
    }

    /**
     * displays user notification
     *
     * @throws HeadlessException
     */
    public void showUserNotification() throws HeadlessException {
        if (!originalMessage.equals("")) {
            sendLog("[NOTIFICATION][RETURN]" + originalMessage + "\n[RESPONSEEXPECTED]");
        }
        new Log().level4Debug("Showing User Notification Dialog -Title:" + title + " -message:" + messageText);

        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            if (title != null) {
                JOptionPane.showMessageDialog((Component) Statics.GUI,
                        messageText,
                        title,
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog((Component) Statics.GUI,
                        messageText,
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            new Log().Level1Interaction("[NOTIFICATION][RETURN]" + title + "\n" + messageText + "  Press any key to continue." + "\n[RESPONSEEXPECTED]");
            waitForStandardInputBeforeContinuing();
        }
    }

    /**
     * displays information message
     *
     * @throws HeadlessException
     */
    public void showInformationMessage() throws HeadlessException {
        if (!originalMessage.equals("")) {
            sendLog("[INFOMESSAGE][RETURN]" + originalMessage + "\n[RESPONSEEXPECTED]");
        }
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            JOptionPane.showMessageDialog((Component) Statics.GUI,
                    messageText, title,
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            new Log().Level1Interaction("[INFOMESSAGE][RETURN]" + title + "\n" + messageText + "  Press any key to continue." + "\n[RESPONSEEXPECTED]");
            waitForStandardInputBeforeContinuing();
        }
    }

    /**
     * displays error message
     *
     * @throws HeadlessException
     */
    public void showErrorDialog() throws HeadlessException {
        if (!originalMessage.equals("")) {
            sendLog("[ERRORMESSAGE][RETURN]" + originalMessage + "\n[RESPONSEEXPECTED]");
        }
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            JOptionPane.showMessageDialog((Component) Statics.GUI, messageText, title, JOptionPane.ERROR_MESSAGE);
        } else {
            new Log().Level1Interaction("[ERRORMESSAGE][RETURN]" + title + "\n" + messageText + "  Press any key to continue." + "\n[RESPONSEEXPECTED]");
            waitForStandardInputBeforeContinuing();
        }
    }

    /**
     * displays a Yes/No dialog
     *
     * @return true if yes, false if no
     */
    public boolean showYesNoOption() {
        if (!originalMessage.equals("")) {
            sendLog("[YESNOOPTION][RETURN or n]" + originalMessage + "\n[RESPONSEEXPECTED]");
        }
        new Log().level4Debug("Displaying Yes/No Dialog: " + title + " message: " + messageText + "\n[RESPONSEEXPECTED]");
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            if (title == null) {
                title = "Yes or No";
            }
            boolean retval = (JOptionPane.showConfirmDialog(
                    (Component) Statics.GUI,
                    messageText,
                    title,
                    JOptionPane.YES_NO_OPTION) == YES_OPTION) ? true : false;
            return retval;
        } else {
            if (title == null) {
                title = "";
            } else {
                title = title + "\n";
            }
            //display the messageText
            new Log().Level1Interaction("[YESNOOPTION][RETURN or n]" + title + "\n" + messageText + "\npress N for no" + "\n[RESPONSEEXPECTED]");
            String s = this.getCommandLineInput();
            if (s.equals("n") || s.equals("N")) {
                return false;
            }
            return true;
        }
    }

    private void sendLog(String messageText) {
        new Log().level4Debug(messageText);
    }
}
