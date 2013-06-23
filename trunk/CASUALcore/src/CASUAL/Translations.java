/*Translations provides tools for translating strings for CASUAL
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

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author adam
 */
public class Translations {

    private static ResourceBundle translation;
    public static String language = "en";

    /**
     * Returns translated string for CASUAL
     *
     * @param reference string to be translated
     * @return
     */
    public static String get(String reference) {
        //load translation only if nulled.
        if (translation == null) {
            String lang = Locale.getDefault().getDisplayLanguage();
            try {
                translation = java.util.ResourceBundle.getBundle("CASUAL/resources/Translations/" + lang);
            } catch (Exception e) {
                translation = java.util.ResourceBundle.getBundle("CASUAL/resources/Translations/English");
                new Log().level3Verbose("Language " + lang + " was not found in CASUAL/resources/Translations/" + lang + ".properties.  CASUAL will accept translations.  Defaulting to english. ");

            }
        }
        //get translation
        String[] splitRef = reference.split("( )|(\n)");
        String retVal = "";
        for (String ref : splitRef) {
            if (! ref.equals("") && ref.startsWith("@")){
                try {
                    new Log().level4Debug("[TRANSLATION]" + ref);
                    retVal = reference.replace(ref, translation.getString(ref));
                } catch (java.util.MissingResourceException ex) {
                    new Log().level3Verbose("*****MISSING TRANSLATION VALUE*****");
                }
            } else {
                
            }
        }
        return retVal;
    }

    public static void changeLanguage(String newLanguage) {
        translation = null;
        language = newLanguage;
    }

    public static void translateAnyFromLine(String line) {
        Enumeration allNames = translation.getKeys();
        while (allNames.hasMoreElements()) {
            String toBeReplaced = (String) allNames.nextElement();
            if (line.contains(toBeReplaced)) {
                line = line.replace(toBeReplaced, get(toBeReplaced));
            }
        }
    }
}
