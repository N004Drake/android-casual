/*iCASUALGUI provides an interface for a main GUI in CASUAL
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

import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import java.awt.image.BufferedImage;

/**
 *
 * @author adam
 */
public interface iCASUALGUI {

    void dispose();
    /**
     * the start button was pressed.
     */
    void StartButtonActionPerformed();

    /**
     * gets the selected combobox item.
     *
     * @return selected item in combobox
     */
    String comboBoxGetSelectedItem();

    /**
     * adds an item to the combo box
     *
     * @param item item to add
     */
    void comboBoxScriptSelectorAddNewItem(String item);

    /**
     * sets controls status
     *
     * @param status commanded value
     * @return true if enabled false if not
     */
    boolean enableControls(boolean status);

    /**
     * gets the control status
     *
     * @return true if enabled
     */
    boolean getControlStatus();

    void setCASPAC(Caspac caspac);

    void setInformationScrollBorderText(String title);

    /**
     * sets the progress bar value.
     *
     * @param value value for progress bar
     */
    void setProgressBar(int value);

    /**
     * sets max value for progress bar
     *
     * @param value maximum
     */
    void setProgressBarMax(int value);

    void setScript(Script s);

    /**
     * sets "do it!" button text
     *
     * @param text text for main execution button
     */
    void setStartButtonText(String text);

    /**
     * changes the label icon
     *
     * @param Icon resource to be displayed
     * @param Text text if icon is missing
     */
    void setStatusLabelIcon(String Icon, String Text);

    /**
     * sets the message label text
     *
     * @param text label text
     */
    void setStatusMessageLabel(String text);

    /**
     * sets window banner image
     *
     * @param icon image to display
     * @param text text if image cannot be displayed
     */
    /**
     *
     * @param icon
     * @param text
     */
    void setWindowBannerImage(BufferedImage icon, String text);

    /**
     * sets the main window banner text if an image is not used
     *
     * @param text text to display as banner
     */
    void setWindowBannerText(String text);

    public void setVisible(boolean b);
    
}
