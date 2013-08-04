/*iCASUALInteraction provides an interface for CASUAL to interact with the user
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



/**
 *
 * @author adam
 */
public interface iCASUALInteraction {
    final int INTERACTION_TIME_OUT = 0;
    final int INTERACTION_ACTION_REUIRED = 1;
    final int INTERACTION_USER_CANCEL_OPTION = 2;
    final int INTERACTION_USER_NOTIFICATION = 3;
    final int INTERACTION_SHOW_INFORMATION = 4;
    final int INTERACTION_SHOW_ERROR = 5;
    final int INTERACTION_SHOW_YES_NO = 6;
    final int INTERACTION_INPUT_DIALOG = 7;
    /**
     * Takes a message object and displays to user
     * @param messageObject defined by CASUAL
     * @return string value which must be interpereted
     */
    String displayMessage(CASUALMessageObject messageObject);
    
}
