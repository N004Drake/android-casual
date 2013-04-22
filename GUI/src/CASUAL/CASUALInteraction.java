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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
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
            new Log().Level1Interaction("[STANDARDMESSAGE]" + title + "\n" + message);
            String s = getCommandLineInput();
            return 1;

        }
    }

    public String getCommandLineInput() {
        try {
            String input;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            return in.readLine();
                
                
        } catch (IOException ex) {
            return "";
        }

    }
    //TODO: this is broken

    private int getCommandLineInputNumber() {
        String x;
        int retval;
        try {
        x=getCommandLineInput();
        retval=Integer.parseInt(x);
        if (retval<10){
           return retval;
       } else {
           retval=getCommandLineInputNumber();
           return retval;
       }
       } catch (NumberFormatException ex){
         return 9999;
       }

    }

    private void waitForStandardInputBeforeContinuing() {
        getCommandLineInput();
    }

    public String inputDialog(String[] Message) throws HeadlessException {
        if (Statics.useGUI) {
            return JOptionPane.showInputDialog(null, Message[1], Message[0], JOptionPane.QUESTION_MESSAGE);
        } else {
            new Log().Level1Interaction("[INPUT][ANY]"+ Message[0] + Message[1]+"\n input:");
            return getCommandLineInput();
        }
            
         
    }

    public int showActionRequiredDialog(String instructionalMessage) throws HeadlessException {
        int n=9999;
        if (Statics.useGUI) {

            Object[] Options = {"I didn't do it", "I did it"};
            instructionalMessage = "<html>" + instructionalMessage.replace("\n", "<BR>") + "</html>";
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
            new Log().Level1Interaction("[USERTASK][Q or RETURN][CRITICAL]" + instructionalMessage +"<BR> press  to quit");
            while (n != 0 && n != 1){
                String retval =getCommandLineInput();
                if (!retval.equals("q") && !retval.equals("Q")&& !retval.equals("")){
                    n=showActionRequiredDialog(instructionalMessage);
                } else if (retval.equals("Q") || retval.equals("q")){
                    n=0;
                } else {
                    n=1;
                }
                
            }
              
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
            new Log().Level1Interaction("[CANCELOPTION][Q or RETURN]" + Message[0] + "\n" + Message[1]+"\npress Q to quit");
            String s=this.getCommandLineInput();
            if (s.equals("q")||s.equals("Q")){
                return 0;
            }
            return 1;
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
            new Log().Level1Interaction("[NOTIFICATION][RETURN]" + Message[0] + "\n" + Message[1] + "  Press any key to continue.");
            waitForStandardInputBeforeContinuing();
        }
    }

    public void showInformationMessage(String message, String title) throws HeadlessException {
        if (Statics.useGUI) {
            JOptionPane.showMessageDialog(null,
                    message, title,
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            new Log().Level1Interaction("[INFOMESSAGE][RETURN]" + title + "\n" + message + "  Press any key to continue.");
            waitForStandardInputBeforeContinuing();
        }
    }

    public void showErrorDialog(String message, String title) throws HeadlessException {
        if (Statics.useGUI) {

            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

        } else {
            new Log().Level1Interaction("[ERRORMESSAGE][RETURN]" + title + "\n" + message + "  Press any key to continue.");
            waitForStandardInputBeforeContinuing();
        }
    }
}
