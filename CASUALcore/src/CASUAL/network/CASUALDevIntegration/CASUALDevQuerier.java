/*CASUALDevQuerier queries the file-system at casual-dev.com for items pertaining to the provided build.prop 
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
 *  along with this program.  If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package CASUAL.network.CASUALDevIntegration;

import CASUAL.misc.MandatoryThread;
import CASUAL.misc.StringOperations;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * queries the file-system at casual-dev.com for items pertaining to the
 * provided build.prop
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALDevQuerier {

    static String BUILDPROP;
    static ArrayList<MandatoryThread> searches = new ArrayList<MandatoryThread>();

    /**
     * Takes the build.prop and instantiates CasualDevQuerier
     *
     * @param deviceBuildProp from an android device.
     * @param additionalProps additional properties to blacklist
     */
    public CASUALDevQuerier(String deviceBuildProp, String[] additionalProps) {
        //clean out the buildprop.  / is a folder here. 
        BUILDPROP = deviceBuildProp.replace("/", "");
        for (String prop : additionalProps) {
            BUILDPROP = prop + BUILDPROP;
        }
    }

    public CASUALPackage[] getPackages(){
        List<CASUALPackage> cp=getPackagesList();
        return cp.toArray(new CASUALPackage[cp.size()]);
    }
    public List<CASUALPackage> getPackagesList(){
        ArrayList<CASUALPackage> cp=new ArrayList<CASUALPackage>();
        String[] list=recursiveFolderSearch();
        for (String pack : list){
            CASUALPackage packageMeta=new CASUALPackage(pack);
            if (packageMeta.isValid()){
                cp.add(packageMeta);
            }
        }
        System.out.println("Found "+cp.size()+ " valid CASPACs");
        return cp;
        
    }
    
    /**
     * Performs a recursive search of builds.casual-dev.com
     *
     * @return an array of URLs in string format representing available files.
     */
    public String[] recursiveFolderSearch() {

        Set<String> availableURLs = new TreeSet<String>();
        //get initial filelist
        searchFolder("files/", availableURLs);
        //dispatchThreads( searchFolder("files/").folders);

        int threadsComplete = 0;
        while (threadsComplete < searches.size()) {
            threadsComplete = 0;
            for (MandatoryThread search : getSearches()) {
                if (!search.isComplete()) {

                    System.out.println("Waiting for " + search.getName());
                    search.waitFor();
                    System.out.println("Done Waiting " + search.getName());
                } else {
                    threadsComplete++;
                }
            }

        }
        System.out.println(availableURLs);
        availableURLs.remove("http://builds.casual-dev.com/");
        availableURLs.remove("http://builds.casual-dev.com/null");
        return availableURLs.toArray(new String[availableURLs.size()]);
    }

    /**
     * adds a thread to the searches list
     *
     * @param t thread to be added
     */
    synchronized static void addSearch(MandatoryThread t) {
        searches.add(t);
    }

    /**
     * gets an array of MandatoryThreads
     *
     * @return array of MandatoryThreads
     */
    MandatoryThread[] getSearches() {
        return searches.toArray(new MandatoryThread[searches.size()]);
    }

    /**
     * dispatches new threads to take care of each folder
     *
     * @param availableFolders folders to check
     * @param availableURLs reference to URLs
     */
    private void dispatchThreads(final String[] availableFolders, final Set<String> availableURLs) {
        for (final String folder : availableFolders) {
            if (folder == null || folder.equals("null") || folder.isEmpty()) {
                return;
            }
            MandatoryThread t = new MandatoryThread(new Runnable() {
                @Override
                public void run() {
                    searchFolder(folder, availableURLs);
                }
            });
            addSearch(t);
            t.start();
            t.setName(folder);
        }
    }

    /**
     * Searches a folder if the folder is not in the blacklist
     *
     * @param folder folder to perform work on
     * @param availableURLs reference to URL list
     */
    private void searchFolder(String folder, Set<String> availableURLs) {
        String[] worklist = new String[]{};
        if (!isBlacklisted(folder)) {
            try {
                worklist = folderList(folder, availableURLs);
                for (String name : worklist) {
                    System.out.println("Folder: " + name);
                }
            } catch (IOException ex) {
                Logger.getLogger(CASUALDevQuerier.class.getName()).log(Level.SEVERE, null, ex);
            }
            dispatchThreads(worklist, availableURLs);
        }
    }

    /**
     * downloads the folder b.prop if available and checks it for whitelist then
     * checks for blacklist.
     *
     * @param folder online folder to check
     * @return true if folder is blacklisted
     */
    private static boolean isBlacklisted(String folder) {
        boolean blacklisted;
        Properties buildprop = new Properties();
        try {
            URI uri = new URI("http", "builds.casual-dev.com", "/" + folder + "b.prop", null);
            URL url = new URL(uri.toASCIIString());
            System.out.println("Searching " + folder);
            buildprop.load(url.openStream());
        } catch (IOException ex) {
            //no b.prop to read so we won't blacklist
            return false;
        } catch (URISyntaxException ex) {
            Logger.getLogger(CASUALDevQuerier.class.getName()).log(Level.SEVERE, null, ex);
        }
        //set the blacklist if there is a whitelist
        blacklisted = !buildprop.getProperty("w[0]", "").isEmpty();

        //parse whitelist in properties file
        String checkValue;
        int i = -1;
        while (!(checkValue = buildprop.getProperty("w[" + ++i + "]", "")).isEmpty()) {
            //unset blacklist if value is detected
            if (BUILDPROP.contains(checkValue)) {
                blacklisted = false;
                break;
            }

        }
        //parse blacklist in properties file
        i = -1;
        while (!(checkValue = buildprop.getProperty("b[" + ++i + "]", "")).isEmpty()) {
            if (BUILDPROP.contains(checkValue)) {
                //if its in there break, no need to parse other values
                blacklisted = true;
                break;
            }
        }
        System.out.println(folder + (blacklisted ? " does not apply to this device." : " is Whitelisted."));
        return blacklisted;
    }

    /**
     * Performs the listing on the folder. This is performed after blacklist
     * Gets folders and files. Adds files to the availableURLs list
     *
     * @param remoteFolder folder to do work on
     * @param availableURLs reference to the master URL list
     * @return new work items to be addressed.
     * @throws MalformedURLException {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    private String[] folderList(String remoteFolder, Set<String> availableURLs) throws MalformedURLException, IOException {
        try {

            //Open the URL with folder search query.
            System.out.println("remoteFolder:" + remoteFolder);
            URI uri = new URI("http", "builds.casual-dev.com", "/" + "query.php", "folder=" + remoteFolder, null);
            URL url = new URL(uri.toASCIIString());
            //Read both expected arrays and close the input
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            //Read file and folder
            String folderLine = in.readLine();
            String fileLine = in.readLine();
            System.out.println(folderLine + "\n" + fileLine);
            in.close();
            //parse sun.proprietary.NativeArray to java array (as returned by javascript)
            folderLine = StringOperations.replaceLast(folderLine, "]", "");
            String[] folderList = folderLine.replaceFirst("\\[", "").replace("\\", "").replace("\"", "").split(",");
            fileLine = StringOperations.replaceLast(fileLine, "]", "");
            String[] fileList = fileLine.replaceFirst("\\[", "").replace("\\", "").replace("\"", "").split(",");
            //clean and add String URL references to availableFiles
            for (String file : fileList) {
                if (!file.endsWith("b.prop") && (!file.endsWith(".properties"))) {
                    file = "http://builds.casual-dev.com/" + file.replace("\\/", "/");
                    availableURLs.add(file);
                    System.out.println("Available File: " + file);
                }
            }

            return folderList;
        } catch (URISyntaxException ex) {
            Logger.getLogger(CASUALDevQuerier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new String[]{};
    }

}
