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
import CASUAL.Log;
import CASUAL.language.CASUALLanguageException;
import CASUAL.language.Command;
import CASUAL.misc.StringOperations;
import java.util.HashMap;

/**
 *
 * @author adamoutler
 */
public class Variables {
    private static final HashMap<String,String> variables=new HashMap<String,String>();
    public static void parseVariablesInCommandString(Command c) throws CASUALLanguageException{
        //multiple varialbes may be present, keep parsing until line no longer begins with "var=val".
        if (c.get().split(" ")[0].contains("=")){
            String[] replacement=c.get().split("=",2);
            replacement[1]=StringOperations.replaceLast(replacement[1], CASUALScriptParser.NEWLINE, "");
            String returnValue=replacement[1];
            try {
                returnValue=new CASUALScriptParser().executeOneShotCommand(replacement[1]);
            } catch (Exception ex){
                System.out.println("variable is not a command");
                //throw new CASUALLanguageException("Problem while setting variable:"+replacement[0]);
            }
            if (returnValue.equals("")){  // didn't return a value, errored
                           variables.put(replacement[0], replacement[1]);
            } else {  //value received, replace it.
               variables.put(replacement[0], returnValue);
               c.set(returnValue);
               
            }
            
            c.set(c.get().replaceFirst(c.get().split(" ")[0], returnValue));
             Log.level4Debug("new variable added"+varDump());

        }
        for (String k:variables.keySet()){
            if (c.get().contains(k)){
                 c.set(c.get().replaceAll(k, variables.get(k)));
            }
        }
    }
    
    public static void reset(){
        variables.clear();
    }
    
    @Override
    public String toString(){
       return varDump();
    }
    public static String varDump(){
        StringBuilder sb = new StringBuilder();
        sb.append("---Variable Dump ---\n");
       for (String key: variables.keySet()){
           sb.append("var:").append(key).append(" == val:").append(variables.get(key)).append("\n");
       }
       return sb.toString();
    }
    
    
}
