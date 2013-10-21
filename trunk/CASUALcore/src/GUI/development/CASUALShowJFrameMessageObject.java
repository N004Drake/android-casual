/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.development;

import CASUAL.iCASUALInteraction;
import CASUAL.CASUALMessageObject;
import CASUAL.Log;
import CASUAL.Statics;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author adam
 */
public class CASUALShowJFrameMessageObject extends JOptionPane implements iCASUALInteraction {

    @Override
    public String displayMessage(CASUALMessageObject messageObject) {
        messageType = messageObject.messageType;
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
                showUserNotificationInteraction(title,messageText);
                return messageText;
        }
        return retval;
    }

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
            new Log().errorHandler(ex);
            return "";
        }
    }

    private void waitForStandardInputBeforeContinuing() {
        getCommandLineInput();
    }

    private String showTimeOutInteraction(CASUALMessageObject messageObject, String messageText, String title) {
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            return Integer.toString(new TimeOutOptionPane().timeoutDialog(messageObject.timeoutPresetTime, (Component) Statics.GUI, messageText, title, messageObject.timeoutOptionType, messageObject.timeoutMessageType, messageObject.timeoutOptions, messageObject.timeoutInitialValue));
        } else {
            new Log().Level1Interaction("[STANDARDMESSAGE]" + title + "\n" + messageText + "\n[RESPONSEEXPECTED]");
            String s = getCommandLineInput();
            if (s == null || s.equals("")) {
                return "0";
            }
            return "1";
        }
    }

    private String showActionRequiredInteraction(String messageText, String title) throws HeadlessException {
        String retval;
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
                retval = getCommandLineInput();
                if (!retval.equals("q") && !retval.equals("Q") && !retval.equals("")) {
                    n = new CASUALMessageObject(messageText).showActionRequiredDialog();
                } else if (retval.equals("Q") || retval.equals("q")) {
                    n = 1;
                } else {
                    n = 0;
                }
            }
        }
        return Integer.toString(n);
        //break;// unreachable
    }

    private String showUserCancelOptionInteraction(String title, String messageText) throws HeadlessException {
        int cancelReturn;
        Object[] Options = {"Continue", "Stop"};
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            if (title == null) {
                cancelReturn = JOptionPane.showOptionDialog(
                        (Component) Statics.GUI,
                        messageText,
                        "Do you wish to continue?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Options,
                        Options[1]);
            } else {
                cancelReturn = JOptionPane.showOptionDialog(
                        (Component) Statics.GUI,
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
                cancelReturn = 1;
            } else {
                cancelReturn = 0;
            }
        }
        return Integer.toString(cancelReturn);
        //break; unreachable
    }

    private void showUserNotificationInteraction(String title, String messageText) throws HeadlessException {
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

    private void showInformationInteraction(String messageText, String title) throws HeadlessException {
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            JOptionPane.showMessageDialog((Component) Statics.GUI,
                    messageText, title,
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            new Log().Level1Interaction("[INFOMESSAGE][RETURN]" + title + "\n" + messageText + "  Press any key to continue." + "\n[RESPONSEEXPECTED]");
            waitForStandardInputBeforeContinuing();
        }
    }

    private void showErrorInteraction(String messageText, String title) throws HeadlessException {
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
           JOptionPane.showMessageDialog( (Component) Statics.GUI, messageText, title, ERROR_MESSAGE);
        } else {
            new Log().Level1Interaction("[ERRORMESSAGE][RETURN]" + title + "\n" + messageText + "  Press any key to continue." + "\n[RESPONSEEXPECTED]");
            waitForStandardInputBeforeContinuing();
        }
    }

    private String showYesNoInteraction(String title, String messageText) throws HeadlessException {
        new Log().level4Debug("Displaying Yes/No Dialog: " + title + " message: " + messageText + "\n[RESPONSEEXPECTED]");
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            if (title == null) {
                title = "Yes or No";
            }
            boolean val = (JOptionPane.showConfirmDialog(
                    (Component) Statics.GUI,
                    messageText,
                    title,
                    JOptionPane.YES_NO_OPTION) == YES_OPTION) ? true : false;
            return val ? "true" : "false";
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
                return "false";
            } else {
                return "true";
            }
        }
    }

    private String showInputDialog(String title, String messageText) throws HeadlessException {
        new Log().level4Debug("Requesting User Input.. Title:" + title + " -message:" + messageText + "\n[RESPONSEEXPECTED]");
        messageText = "<html>" + messageText.replace("\\n", "\n");
        if (Statics.GUIIsAvailable && !Statics.dumbTerminalGUI) {
            if (title == null) {
                return JOptionPane.showInputDialog((Component) Statics.GUI, messageText, "Input Required", JOptionPane.QUESTION_MESSAGE);
            } else {
                return JOptionPane.showInputDialog((Component) Statics.GUI, messageText, title, JOptionPane.QUESTION_MESSAGE);
            }
        } else {
            new Log().Level1Interaction("[INPUT][ANY]" + title + messageText + "\n input:");
            return getCommandLineInput();
        }
        //break; unreachable
    }
}
