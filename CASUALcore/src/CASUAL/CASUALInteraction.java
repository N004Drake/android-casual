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

/**
 *
 * @author adam
 */
public class CASUALInteraction extends JOptionPane {
    
    String message;
    String title;


    public CASUALInteraction(String message){
        this.title=null;
        this.message=message;
    }
    public CASUALInteraction(String title, String message){
        this.title=title;
        this.message=message;
    }
    public CASUALInteraction(String[] messageArray){
        if (messageArray.length>1){
            title=messageArray[0];
            message=messageArray[1];
        } else {
            title=null;
            message=messageArray[0];
        }
    }
    
    
    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
    public int showTimeoutDialog(final int PRESET_TIME, Component parentComponent, int optionType, int messageType, Object[] options, final Object initialValue) {
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            return new TimeOutOptionPane().timeoutDialog(PRESET_TIME, parentComponent, message, title, optionType, messageType, options, initialValue);
        } else {
            new Log().Level1Interaction("[STANDARDMESSAGE]" + title + "\n" + message);
            String s = getCommandLineInput();
            return 1;
        }
    }
    static String cmdlineinput = "";
    
    public String getCommandLineInput() {
        try {
            Log.out.flush();
            String s = in.readLine();
            return s;
        } catch (IOException ex) {
            new Log().errorHandler(ex);
            return "";
        }
    }

    private int getCommandLineInputNumber() {
        new Log().level4Debug("getting command line input");
        String x;
        int retval;
        try {
            x = getCommandLineInput();
            retval = Integer.parseInt(x);
            if (retval < 10) {
                return retval;
            } else {
                retval = getCommandLineInputNumber();
                return retval;
            }
        } catch (NumberFormatException ex) {
            return 9999;
        }
    }
    
    private void waitForStandardInputBeforeContinuing() {
        getCommandLineInput();
    }
    
    public String inputDialog() throws HeadlessException {
        new Log().level4Debug("Requesting User Input.. Title:"+ title + " -message:" +message);
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            if (title ==null){
                return JOptionPane.showInputDialog(Statics.GUI, message, "Input Required", JOptionPane.QUESTION_MESSAGE);
            } else {
                return JOptionPane.showInputDialog(Statics.GUI, message, title, JOptionPane.QUESTION_MESSAGE);
            }
        } else {
            if (title ==null){
                new Log().Level1Interaction("[INPUT][ANY]" + message + "\n input:");
                return getCommandLineInput();                    
            } else {
                new Log().Level1Interaction("[INPUT][ANY]" + message + title + "\n input:");
                return getCommandLineInput();    
            }
        }
    }
    
    public int showActionRequiredDialog() throws HeadlessException {
        new Log().level4Debug("Displaying Action Is Required Dialog:" +message);
        int n = 9999;
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            Object[] Options = {"I didn't do it", "I did it"};
            message = "<html>" + message.replace("\\n", "<BR>");

            n = JOptionPane.showOptionDialog(
                    Statics.GUI,
                    message,
                    "Dont click through this!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    Options,
                    Options[1]);
        } else {
            new Log().Level1Interaction("[USERTASK][Q or RETURN][CRITICAL]" + message + "<BR> press  to quit");
            while (n != 0 && n != 1) {
                String retval = getCommandLineInput();
                if (!retval.equals("q") && !retval.equals("Q") && !retval.equals("")) {
                    n = new CASUALInteraction(message).showActionRequiredDialog();
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
        new Log().level4Debug("Displaying User Cancel Option Dialog title: "+title + " message: "+message);

        int n;
        Object[] Options = {"Stop", "Continue"};
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            if (title==null) {
                n = JOptionPane.showOptionDialog(
                        Statics.GUI,
                        message,
                        "Do you wish to continue?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Options,
                        Options[1]);
            } else {
                n = JOptionPane.showOptionDialog( 
                        Statics.GUI,
                        message,
                        title,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Options,
                        Options[1]);
            }
        } else {
            if (title==null){
                new Log().Level1Interaction("[CANCELOPTION][Q or RETURN]" + message + "\npress Q to quit");
            } else {
                new Log().Level1Interaction("[CANCELOPTION][Q or RETURN]" + title + "\n" + message + "\npress Q to quit");
            }
            String s = this.getCommandLineInput();
            if (s.equals("q") || s.equals("Q")) {
                return 0;
            }
            return 1;
        }
        return n;
    }
    
    public void showUserNotification() throws HeadlessException {
        new Log().level4Debug("Showing User Notification Dialog -Title:"+title+" -message:"+message);

        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            if (title!=null) {
                JOptionPane.showMessageDialog(Statics.GUI,
                        title,
                        message,
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(Statics.GUI,
                        message,
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            if (title !=null){
                new Log().Level1Interaction("[NOTIFICATION][RETURN]" + title + "\n" + message + "  Press any key to continue.");
            } else {
                new Log().Level1Interaction("[NOTIFICATION][RETURN]" + message + "  Press any key to continue.");
            }
            waitForStandardInputBeforeContinuing();
        }
    }
    
    public void showInformationMessage() throws HeadlessException {
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            JOptionPane.showMessageDialog(Statics.GUI,
                    message, title,
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            new Log().Level1Interaction("[INFOMESSAGE][RETURN]" + title + "\n" + message + "  Press any key to continue.");
            waitForStandardInputBeforeContinuing();
        }
    }
    
    public void showErrorDialog() throws HeadlessException {
        if (Statics.useGUI && !Statics.dumbTerminalGUI) {
            JOptionPane.showMessageDialog(Statics.GUI, message, title, JOptionPane.ERROR_MESSAGE);
        } else {
            new Log().Level1Interaction("[ERRORMESSAGE][RETURN]" + title + "\n" + message + "  Press any key to continue.");
            waitForStandardInputBeforeContinuing();
        }
    }
}
