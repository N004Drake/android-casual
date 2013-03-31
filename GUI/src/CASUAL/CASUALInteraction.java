/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.awt.Component;
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
}
