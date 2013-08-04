/*BooleanOperations contains accellerators for booleans
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
package CASUAL.misc;

/**
 *
 * @author adam
 */
public class BooleanOperations {

    public static boolean containsTrue(boolean[] array) {
        for (boolean b : array) {
            if (b) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsFalse(boolean[] array) {
        for (boolean b : array) {
            if (!b) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAllTrue(boolean[] array) {
        for (boolean b : array) {
            if (!b) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsAllFalse(boolean[] array) {
        for (boolean b : array) {
            if (b) {
                return false;
            }
        }
        return true;
    }
}
