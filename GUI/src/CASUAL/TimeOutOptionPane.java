/*TimeOutOptionPane provides an option pane with timeout
 *Copyright (C) Gio Gilligan, Mar 14, 2007
 * http://www.jguru.com/faq/view.jsp?EID=266182
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
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author adam
 */
public class TimeOutOptionPane extends JOptionPane {

    public TimeOutOptionPane() {
        super();
    }
    static int PRESET_TIME = 335;
    /*
     * int showTimeoutDialog = timeOutOptionPane.showTimeoutDialog( 5, //timeout
     * null, //parentComponent "My Message", //Display Message "My Title",
     * //DisplayTitle TimeOutOptionPane.YES_OPTION, // Options buttons
     * TimeOutOptionPane.INFORMATION_MESSAGE, //Icon new String[]{"blah", "hey",
     * "yo"}, // option buttons "yo"); //seconds before auto "yo"
     *     
     *
     */

    public int timeoutDialog(final int PRESET_TIME, Component parentComponent, Object message, final String title, int optionType,
            int messageType, Object[] options, final Object initialValue) {
        JOptionPane pane = new JOptionPane(message, messageType, optionType, null, options, initialValue);

        pane.setInitialValue(initialValue);

        final JDialog dialog = pane.createDialog(parentComponent, title);

        pane.selectInitialValue();
        new Thread() {
            @Override
            public void run() {

                for (int i = PRESET_TIME; i >= 0; i--) {
                    doSleep();
                    if (dialog.isVisible() && i < 300) {
                        dialog.setTitle(title + "  (" + i + " seconds before auto \"" + initialValue + "\")");
                    }
                }
                if (dialog.isVisible()) {
                    dialog.setVisible(false);
                }

            }

            void doSleep() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    new Log().errorHandler(ex);

                }
            }
        }.start();
        dialog.setVisible(true);

        Object selectedValue = pane.getValue();
        if (selectedValue.equals("uninitializedValue")) {
            selectedValue = initialValue;
        }
        if (selectedValue == null) {
            return CLOSED_OPTION;
        }
        if (options == null) {
            if (selectedValue instanceof Integer) {
                return ((Integer) selectedValue).intValue();
            }
            return CLOSED_OPTION;
        }
        for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
            if (options[counter].equals(selectedValue)) {
                return counter;
            }
        }
        return CLOSED_OPTION;
    }
}
