/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author adam
 */
public class CASUALInteraction extends JOptionPane {

    public int showTimeoutDialog(final int PRESET_TIME, Component parentComponent, Object message, final String title, int optionType, int messageType, Object[] options, final Object initialValue) {
        if (Statics.useGUI) {
            return new TimeOutOptionPane().timeoutDialog(PRESET_TIME, parentComponent, message, title, optionType, messageType, options, initialValue);
        } else {
            new Log().Level1Interaction("[STANDARDMESSAGE]"+title + "\n" + message);
            String s = getCommandLineInput();
            return 0;

        }
    }

    private String getCommandLineInput() {
        System.out.println(message);
        try {
            char x = 0;
            //new Log().Level1Interaction(message);
            while (x < 1) {
                x = (char) new InputStreamReader(System.in).read();
            }
            return String.valueOf(x);
        } catch (IOException e) {
            return "0";
        }
    }
    
    //TODO: this is broken
     private int getCommandLineInputNumber() {
        System.out.println(message);
        try {
            char x = 0;
            //new Log().Level1Interaction(message);
            while (x < 1) {
                x = (char) new InputStreamReader(System.in).read();
            }
            return x;
        } catch (IOException e) {
            return 0;
        }
    }
    
     private void waitForStandardInputBeforeContinuing(){
         int x=0;  
         while (x < 1) {
             try {
                 x = (char) new InputStreamReader(System.in).read();
             } catch (IOException ex) {
                 Logger.getLogger(CASUALInteraction.class.getName()).log(Level.SEVERE, null, ex);
             }
         }
     }

    public String inputDialog(String[] Message) throws HeadlessException {
        String InputBoxText = JOptionPane.showInputDialog(null, Message[1], Message[0], JOptionPane.QUESTION_MESSAGE);
        return InputBoxText;
    }

    public int showActionRequiredDialog(String instructionalMessage) throws HeadlessException {
        int n;
        if (Statics.useGUI) {

            Object[] Options = {"I didn't do it", "I did it"};
            instructionalMessage = "<html>" + instructionalMessage.replace("\\n", "<BR>") + "</html>";
            n = JOptionPane.showOptionDialog(
                    null,
                    instructionalMessage,
                    "Dont click through this!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    Options,
                    Options[1]);
        } else {
            new Log().Level1Interaction("[USERTASK][CRITICAL]" +instructionalMessage);
            n=getCommandLineInputNumber();
        }
        
        return n;
    }

    public int showUserCancelOption(String CASUALStringCommand) throws HeadlessException {
        int n;
        String[] Message = CASUALStringCommand.split(",");
        Object[] Options = {"Stop", "Continue"};
        if (Statics.useGUI) {

            if (CASUALStringCommand.contains(",")) {

                n = JOptionPane.showOptionDialog(
                        null,
                        Message[1],
                        Message[0],
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Options,
                        Options[1]);
            } else {
                n = JOptionPane.showConfirmDialog(
                        Statics.GUI,
                        CASUALStringCommand,
                        "Do you wish to continue?",
                        JOptionPane.YES_NO_OPTION);
            }
        } else {
            new Log().Level1Interaction("[CANCELOPTION]"+ Message[0]+"\n" +Message[1]);
            n=getCommandLineInputNumber();
        }
        return n;
    }

    public void showUserNotification(String CASUALStringCommand) throws HeadlessException {
        CASUALStringCommand = StringOperations.removeLeadingSpaces(CASUALStringCommand);
        String[] Message = CASUALStringCommand.split(",");
        if (Statics.useGUI) {
            if (CASUALStringCommand.contains(",")) {

                JOptionPane.showMessageDialog(null,
                        Message[1],
                        Message[0],
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        CASUALStringCommand,
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            new Log().Level1Interaction("[NOTIFICATION]"+ Message[0]+"\n" +Message[1]+"  Press any key to continue.");
            waitForStandardInputBeforeContinuing();
        }
    }

    public void showInformationMessage(String message, String title) throws HeadlessException {
        if (Statics.useGUI) {
            JOptionPane.showMessageDialog(null,
                    message, title,
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            new Log().Level1Interaction("[INFOMESSAGE]"+ title +"\n"+ message +"  Press any key to continue.");
            waitForStandardInputBeforeContinuing();
        }
    }

    public void showErrorDialog(String message, String title) throws HeadlessException {
        if (Statics.useGUI) {

            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

        } else {
            new Log().Level1Interaction("[ERRORMESSAGE]"+ title +"\n"+ message +"  Press any key to continue.");
            waitForStandardInputBeforeContinuing();
        }
    }
}
