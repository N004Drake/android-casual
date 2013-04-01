/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStreamReader;
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
            String s = getCommandLineInput(title + " -- " + message);

            return 0;

        }
    }

    private String getCommandLineInput(String message) {
        System.out.println(message);
        try {
            char x = 0;
            new Log().Level1Interaction(message);
            while (x<1){
                x = (char) new InputStreamReader(System.in).read();
            }
             return String.valueOf(x);
        } catch (IOException e) {
            return "0";
        }
    }
    public String inputDialog(String[] Message) throws HeadlessException {
        String InputBoxText = JOptionPane.showInputDialog(null, Message[1], Message[0], JOptionPane.QUESTION_MESSAGE);
        return InputBoxText;
    }

    public int showActionRequiredDialog(String line) throws HeadlessException {
        Object[] Options = {"I didn't do it", "I did it"};
        line = "<html>" + line.replace("\\n", "<BR>") + "</html>";
        int n = JOptionPane.showOptionDialog(
                null,
                line,
                "Dont click through this!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                Options,
                Options[1]);
        return n;
    }

    public int showUserCancelOption(String line) throws HeadlessException {
        int n;
        if (line.contains(",")) {
            String[] Message = line.split(",");
            Object[] Options = {"Stop", "Continue"};
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
                    line,
                    "Do you wish to continue?",
                    JOptionPane.YES_NO_OPTION);
        }
        return n;
    }

    public void showUserNotification(String line) throws HeadlessException {
        line = StringOperations.removeLeadingSpaces(line);
        if (line.contains(",")) {
            String[] Message = line.split(",");
            JOptionPane.showMessageDialog(Statics.GUI,
                    Message[1],
                    Message[0],
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(Statics.GUI,
                    line,
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public void wrongElfClassMessage() throws HeadlessException {
        JOptionPane.showMessageDialog(Statics.GUI,
                "Could not execute ADB. 'Wrong ELF class' error\n"
                + "This can be resolved by installation of ia32-libs"
                + "eg.. sudo apt-get install ia32-libs\n"
                + "ie.. sudo YourPackageManger install ia32-libs", "ELFCLASS64 error!",
                JOptionPane.INFORMATION_MESSAGE);
    }
    public void showErrorDialog(String message, String title) throws HeadlessException {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
