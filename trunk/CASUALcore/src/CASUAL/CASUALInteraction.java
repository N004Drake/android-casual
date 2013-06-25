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
public class CASUALInteraction extends JOptionPane {
   
    String title;
    String messageText;    
    String originalMessage="";
    
    public CASUALInteraction(String messageInput) {
        if (messageInput.startsWith("@")){
            String translation=Translations.get(messageInput);
            if (translation.contains(">>>")){
                originalMessage=messageInput;
                String[] s = translation.split(">>>",2);
                //messageText=s[1].replace("\n","\\n");
                title=s[0];
                messageText=s[1];
            }  else {
                title=null;
                message=translation;
            }
        } else {
             if (messageInput.contains(">>>")){
                String[] s = messageInput.split(">>>",2);
                //messageText=s[1].replace("\n","\\n");
                title=s[0];
                messageText=s[1];
            }  else {
                this.title = null;
                this.messageText = messageInput;
            }
        }
    }

    public CASUALInteraction(String title, String messageInput) {
        this.title = title;
        this.messageText = messageInput;
    }


    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public int showTimeoutDialog(final int PRESET_TIME, Component parentComponent, int optionType, int messageType, Object[] options, final Object initialValue) {
        if (!originalMessage.equals("")) logPretranslated("[STANDARDMESSAGE]"+originalMessage);
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            return new TimeOutOptionPane().timeoutDialog(PRESET_TIME, parentComponent, messageText, title, optionType, messageType, options, initialValue);
        } else {
            new Log().Level1Interaction("[STANDARDMESSAGE]" + title + "\n" + messageText);
            String s = getCommandLineInput();
            if (s==null||s.equals("")){
                return 0;
            }
            return 1;
        }
    }
    static String cmdlineinput = "";

    public String getCommandLineInput() {
        try {
            Log.out.flush();
            String s = in.readLine();
            if (s==null){
                while (s==null){
                    s=in.readLine();
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

    public String inputDialog() throws HeadlessException {
        if (!originalMessage.equals("")) logPretranslated("[INPUT][ANY]"+originalMessage);
        new Log().level4Debug("Requesting User Input.. Title:" + title + " -message:" + messageText);
        messageText="<html>"+messageText.replace("\\n","\n");
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            if (title == null) {
                
                return (String)JOptionPane.showInputDialog(Statics.GUI, messageText, "Input Required", JOptionPane.QUESTION_MESSAGE);
            } else {
                return (String)JOptionPane.showInputDialog(Statics.GUI, messageText, title, JOptionPane.QUESTION_MESSAGE);
            }
        } else {
            new Log().Level1Interaction("[INPUT][ANY]"+ title +  messageText +  "\n input:");
            return getCommandLineInput();
        }
    }

    public int showActionRequiredDialog() throws HeadlessException {
        if (!originalMessage.equals("")) logPretranslated("[USERTASK][Q or RETURN][CRITICAL]"+originalMessage);
        new Log().level4Debug("Displaying Action Is Required Dialog:" + messageText);
        int n = 9999;
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            Object[] Options = {"I didn't do it", "I did it"};
            messageText = "<html>" + messageText.replace("\\n", "<BR>");

            n = JOptionPane.showOptionDialog(
                    Statics.GUI,
                    messageText,
                    "Dont click through this!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    Options,
                    Options[1]);
        } else {
            while (n != 0 && n != 1) {
                new Log().Level1Interaction("[ACTIONREQUIRED][Q or RETURN]" + title + "\n" + messageText + "\npress Q to quit");
                String retval = getCommandLineInput();
                if (!retval.equals("q") && !retval.equals("Q") && !retval.equals("")) {
                    n = new CASUALInteraction(messageText).showActionRequiredDialog();
                } else if (retval.equals("Q") || retval.equals("q")) {
                    n = 0;
                } else {
                    n = 1;
                }
            }
        }
        return n;
    }

    public int showUserCancelOption() {
        if (!originalMessage.equals("")) logPretranslated("[CANCELOPTION][Q or RETURN]"+originalMessage);
        new Log().level4Debug("Displaying User Cancel Option Dialog title: " + title + " message: " + messageText);

        int n;
        Object[] Options = {"Stop", "Continue"};
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            if (title == null) {
                n = JOptionPane.showOptionDialog(
                        Statics.GUI,
                        messageText,
                        "Do you wish to continue?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Options,
                        Options[1]);
            } else {
                n = JOptionPane.showOptionDialog(
                        Statics.GUI,
                        messageText,
                        title,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Options,
                        Options[1]);
            }
        } else {
            new Log().Level1Interaction("[CANCELOPTION][Q or RETURN]" + title + "\n" + messageText + "\npress Q to quit");
            String s = this.getCommandLineInput();
            if (s.equals("q") || s.equals("Q")) {
                return 0;
            }
            return 1;
        }
        return n;
    }

    public void showUserNotification() throws HeadlessException {
        if (!originalMessage.equals("")) logPretranslated("[NOTIFICATION][RETURN]"+originalMessage);
        new Log().level4Debug("Showing User Notification Dialog -Title:" + title + " -message:" + messageText);

        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            if (title != null) {
                JOptionPane.showMessageDialog(Statics.GUI,
                        messageText,
                        title,
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(Statics.GUI,
                        messageText,
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            new Log().Level1Interaction("[NOTIFICATION][RETURN]" + title + "\n" + messageText + "  Press any key to continue.");
            waitForStandardInputBeforeContinuing();
        }
    }

    public void showInformationMessage() throws HeadlessException {
        if (!originalMessage.equals("")) logPretranslated("[INFOMESSAGE][RETURN]" +originalMessage);
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            JOptionPane.showMessageDialog(Statics.GUI,
                    messageText, title,
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            new Log().Level1Interaction("[INFOMESSAGE][RETURN]" + title + "\n" + messageText + "  Press any key to continue.");
            waitForStandardInputBeforeContinuing();
        }
    }

    public void showErrorDialog() throws HeadlessException {
        if (!originalMessage.equals("")) logPretranslated("[ERRORMESSAGE][RETURN]"+originalMessage);
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            JOptionPane.showMessageDialog(Statics.GUI, messageText, title, JOptionPane.ERROR_MESSAGE);
        } else {
            new Log().Level1Interaction("[ERRORMESSAGE][RETURN]" + title + "\n" + messageText + "  Press any key to continue.");
            waitForStandardInputBeforeContinuing();
        }
    }

    public boolean showYesNoOption() {
        if (!originalMessage.equals("")) logPretranslated("[YESNOOPTION][RETURN or n]" +originalMessage);
        new Log().level4Debug("Displaying Yes/No Dialog: " + title + " message: " + messageText);
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            if (title == null) {
                title = "Yes or No";
            }
            boolean retval = (JOptionPane.showConfirmDialog(
                    Statics.GUI,
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
            new Log().Level1Interaction("[YESNOOPTION][RETURN or n]" + title + "\n" + messageText + "\npress N for no");
            String s = this.getCommandLineInput();
            if (s.equals("n") || s.equals("N")) {
                return false;
            }
            return true;
        }
    }
    
    private void logPretranslated(String messageText){
        new Log().level4Debug(messageText);
    }
}
