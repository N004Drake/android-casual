/*StringOperations provides string tools 
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

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class StringOperations {

    public static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length(), string.length());
        } else {
            return string;
        }
    }

    public static String removeLeadingSpaces(String Line) {
        while (Line.startsWith(" ")) {
            Line = Line.replaceFirst(" ", "");
        }
        return Line;
    }

    public static String removeLeadingAndTrailingSpaces(String line) {
        while (line.startsWith(" ")) {
            line = line.replaceFirst(" ", "");
        }
        while (line.endsWith(" ")) {
            StringBuilder b = new StringBuilder(line);
            b.replace(line.lastIndexOf(" "), line.lastIndexOf(" ") + 1, "");
            line = b.toString();
        }
        return line;
    }

    public static String removeTrailingSpaces(String line) {
        while (line.endsWith(" ")) {
            StringBuilder b = new StringBuilder(line);
            b.replace(line.lastIndexOf(" "), line.lastIndexOf(" ") + 1, "");
            line = b.toString();
        }
        return line;
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String[] convertArrayListToStringArray(ArrayList List) {
        String[] StringArray = new String[List.size()];
        for (int i = 0; i <= List.size() - 1; i++) {
            StringArray[i] = List.get(i).toString();
        }
        return StringArray;
    }

    /**
     * Returns an array of Strings from a source String.
     *
     * @param String contains comma delimited collection of strings each
     * surrounded by quotations.
     *
     * @author Jeremy Loper jrloper@gmail.com
     */
    public static String[] convertStringToArray(String inputString) {
        StringOperations.removeLeadingAndTrailingSpaces(inputString);
        String[] outputArray = {};
        int currentQuotePosition = 0;
        int lastQuotePosition = 0;

        for (int i = 0; i <= inputString.length(); i++, currentQuotePosition = inputString.indexOf("\",", currentQuotePosition)) {
            if (inputString.length() != currentQuotePosition) {
                outputArray[i] = inputString.substring(lastQuotePosition, (currentQuotePosition - 1));
                lastQuotePosition = currentQuotePosition++;
            } else {
                outputArray[i] = inputString.substring(lastQuotePosition, currentQuotePosition);
                break;
            }
        }
        return outputArray;
    }
    public static String generateRandomHexString(int len) {
        final char[] chars = new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        String random = "";
        for (int i = 0; i < len; i++) {
            random = random + chars[new Random().nextInt(chars.length)];
        }
        return random;
    }
}
