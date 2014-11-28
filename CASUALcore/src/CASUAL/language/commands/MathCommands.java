/*
 * Copyright (C) 2014 adamoutler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package CASUAL.language.commands;

import CASUAL.language.Command;
import CASUAL.misc.math.CASUALMathOperationException;
import CASUAL.misc.math.StringMath;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adamoutler
 */
public class MathCommands {
    public static boolean doMath(Command math) throws CASUALMathOperationException{
        if (math.get().startsWith("$MATH ")){
            String cmd=math.get().replaceFirst("\\$MATH","").trim();
            try {
               math.setReturn(true, new StringMath().performRoundedMathOperation(cmd));
               return true;
            } catch (CASUALMathOperationException ex) {
               math.setReturn(false, "Math Error");
               throw ex;
            }
            
        }
        return false;
    }
}
