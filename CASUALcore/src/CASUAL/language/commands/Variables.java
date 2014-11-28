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

import CASUAL.CASUALScriptParser;
import CASUAL.language.CASUALLanguageException;
import CASUAL.language.Command;
import CASUAL.misc.StringOperations;
import java.util.HashMap;

/**
 *
 * @author adamoutler
 */
public class Variables {
    private HashMap<String,String> variables=new HashMap();
    public void parseVarialbesInString(Command c) throws CASUALLanguageException{
        //multiple varialbes may be present, keep parsing until line no longer begins with "var=val".
        while (c.get().split(" ")[0].contains("=")){
            String[] replacement=c.get().split(" ")[0].split("=");
            replacement[1]=StringOperations.replaceLast(replacement[1], CASUALScriptParser.NEWLINE, "");
            String returnValue;
            try {
                returnValue=new CASUALScriptParser().executeOneShotCommand(replacement[1]);
            } catch (Exception ex){
                throw new CASUALLanguageException("Problem while setting variable:"+replacement[0]);
            }
            if (returnValue.equals("")){
            
               variables.put(replacement[0], replacement[1]);
            } else {
               variables.put(replacement[0], returnValue);
            }
            
            c.set(c.get().replaceFirst(c.get().split(" ")[0], returnValue));
        }
        
        
        for (String k:variables.keySet()){
            c.set(c.get().replaceAll(k, variables.get(k)));
        }
        
    }
}
