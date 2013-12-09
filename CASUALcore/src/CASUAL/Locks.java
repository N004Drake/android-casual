package CASUAL;

import CASUAL.misc.MandatoryThread;

/*Locks provides a place for storage of sequential and static items
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
/**
 * Locks provides a place for storage of sequential and static items
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class Locks {

    /**
     * Thread representing the Graphical User Interface.
     */
    public static Thread startGUI = new Thread();

    /**
     * Lock representing the preparation state of the CASPAC. This is alive when
     * CASPAC is being prepared.
     */
    public static MandatoryThread caspacPrepLock = new MandatoryThread();

    /**
     * Lock representing the script. this is alive when script is running.
     */
    public static MandatoryThread scriptRunLock = new MandatoryThread();

    /**
     * Lock representing the state of ADB starting. This is alive while ADB is
     * being deployed and the server is started.
     */
    public static MandatoryThread startADB = new MandatoryThread();

    /**
     * Lock representing the GUI preparation. This is alive while the form is
     * preparing itself.
     */
    public static boolean lockGUIformPrep = true;

    /**
     * Lock representing the unzip phase of the CASPAC. This is alive during
     * CASPACpreparation unzip and goes dead when information is available for
     * the GUI. This is basically PrepCASAPC, but does not include unzip of
     * resources.
     */
    public static boolean lockGUIunzip = true;

    /**
     * is true while CASPAC is preparing a script.
     *
     */
    public static boolean caspacScriptPrepLock = true;

}
