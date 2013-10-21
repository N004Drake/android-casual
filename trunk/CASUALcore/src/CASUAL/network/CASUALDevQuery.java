/*CASUALDevQuery queries the file-system at casual-dev.com for items pertaining to the provided build.prop 
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
package CASUAL.network;

import CASUAL.misc.StringOperations;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author adam
 */
public class CASUALDevQuery {

    final String BUILDPROP;
    public List<String> availableURLs;


    /**
     * Takes the build.prop and instantiates CasualDevQuery
     * @param BuildProp from an android device.
     */
    public CASUALDevQuery(String BuildProp) {
        //clean out the buildprop.  / is a folder here. 
        this.BUILDPROP = BuildProp.replace("/","");
        
        //Lists cannot be instantiated
        //so we must make an object which can return one. 
        String[] temp = new String[]{};
        availableURLs = new LinkedList<String>(Arrays.asList(temp));
    }

    /**
     * goes out to CASUALDev and determines what is available based on your build.prop
     * @return List containing http references to packages available.
     * @throws MalformedURLException  should never occur
     * @throws IOException possible if internet is disconnected
     */
    public List<String> getData() throws MalformedURLException, IOException {
        //get the initial list from the server
        List<String> folders = folderList("./files/");
        //search each folder recursively
        recursiveSearch(folders);
        for (String file:availableURLs)
            new CASUAL.Log().level4Debug("found:"+file);
        return availableURLs;
    }

    @SuppressWarnings("unchecked")
    private List<String> folderList(String remoteFolder) throws MalformedURLException, IOException {
        
        //remove leading slash so it is compatible with the search format.
        if (remoteFolder.startsWith("/")) {
            remoteFolder = remoteFolder.replaceFirst("/", "");
        }

        //Open the URL with folder search query.
        System.out.println("remoteFolder:" + remoteFolder);
        URL url = new URL("http://builds.casual-dev.com/query.php?folder=" + remoteFolder);

        //Read both expected arrays and close the input
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String folderLine = in.readLine();
        String fileLine = in.readLine();
        System.out.println(folderLine + "\n" + fileLine);
        in.close();

        
        try {
            //process with javascript to create a Java object from JSON
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("JavaScript");
            //create Lists from javascript output
            
            List<String> folderList = (List<String>)engine.eval(folderLine);
            List<String> fileList = (List<String>) engine.eval(fileLine);
            //clean and add String URL references to availableFiles
            if (fileList != null) {
                for (String file : fileList) {
                    file = "http://builds.casual-dev.com/" + file.replaceAll("\\/", "/");
                    availableURLs.add(file);
                }
            }
            //clean the list data from PHP/JSONification 
            if (folderList != null) {
                for (String dir : folderList) {
                    dir = dir.replaceAll("\\/", "/");
                }
            }
            return folderList;

        } catch (ScriptException ex) {
            Logger.getLogger(CASUALDevQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //return list of folders received
        return null;
    }
    private static <String> Object createList(List<String> argumentList) {
        List<String> otherList = new ArrayList<String>();
        //do something here
        return otherList;
    }
    private void recursiveSearch(List<String> folders) throws MalformedURLException, IOException {
        if (folders != null) {
            //go through each folder
            for (String instanceFolder : folders) {

                //isolate the value to search in the build.prop
                //eg /ro.product.manufacturer=OPPO/ro.product.model=X909/
                //we only one the value between the last two "/"s
                String search = StringOperations.replaceLast(instanceFolder, "/", "");
                if (search.contains("/")) {
                    search = search.substring(search.lastIndexOf("/") + 1, search.length());
                }
                //if the folder is found in the build.prop search the folder.
                if (search != null && (BUILDPROP.contains(search + "\n") || search.equals("all"))) {
                    System.out.println("Identified Build Prop Entry:" + search);
                    recursiveSearch(folderList(instanceFolder));
                }
            }
        }
    }
}
