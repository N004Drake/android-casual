/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package casthezipper;
import casthezipper.resources.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 *
 * @author loganludington
 */
public class CASthezipper {

    private static final String slash = System.getProperty("file.separator");
    private static final String wd = System.getProperty("user.dir");
    private static boolean force = false;
    private static boolean ignore = false;
    private static String outputfile = null;
    private static List<File> inputfiles = new ArrayList();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
       argProcessor(args);
       Zip zip = new Zip(outputfile);
       for (File f : inputfiles)
           zip.addToZip(f);
       zip.execute();
       System.out.println("Successfully created zip file at: \n" + outputfile);
    }
    
    private static void argProcessor(String[] args){
        List<String> listArgs = new ArrayList();
                listArgs.addAll(Arrays.asList(args));
        while("-".equals(listArgs.get(0).substring(0,1)))
        {
            flagProcessor(listArgs.get(0));
            listArgs.remove(0);
        }
        if(args.length<2)
            usage("Error: Requires 2 or more arguments.");
        for (int i=0;i<listArgs.size();i++)
        {
            if (!listArgs.get(i).substring(0, 1).equals(slash))
                listArgs.set(i, wd +slash+ listArgs.get(i));
        }
        outputfile = listArgs.get(0);
        listArgs.remove(0);
        if(new File(outputfile).exists() && !(force))
            usage("Error: Output file exist, please use the -f option to overwrite");
        if(!("zip".equals(outputfile.substring(outputfile.lastIndexOf(".")+1,outputfile.length()))))
            usage("Error: Output file must have a .zip extension.");
        String missingFiles = "";
        for(String s : listArgs)
        {
            if(!(new File(s).exists()))
                missingFiles = missingFiles + s + "\n";
            else
                inputfiles.add(new File(s));
        }
        if (ignore)
        {
            System.out.println("Warning: The following files are missing: \n"+ 
                    missingFiles + "\n" + "However the packaging will continue.");
        }
        else if (!("".equals(missingFiles)))
        {
            usage("The following files are missing: \n"+ missingFiles);
        }
        
        
    }
    
    public static void usage(String error){
        if (error != null)
            System.out.println(error);
        System.out.println("Usage: java -jar CASthezipper.jar  [-fi]  output_zipfile inputfile1 ... inputfileN");
        System.out.println("       -f: Will overwrite existing output_zipfile");
        System.out.println("       -i: Will ignore nonexisting input files");
        System.exit(1);
    }

    private static void flagProcessor(String get) {
        
        if (get.split("").length<2)
            usage("Error: - is not a valid flag.");
        String flagString = get.substring(1,get.length()).trim();
        
        List<String> flagList = new ArrayList();
        String [] flags = flagString.split("");
        flagList.addAll(Arrays.asList(flags));
        for(int i=1;i<flagList.size();i++)
        {
            if ("f".equals(flagList.get(i)))
                force = true;
            
            
            else if ("i".equals(flagList.get(i)))
                ignore = true;
            
            else
                usage("Error: -"+ flagList.get(i) + " is not a valid flag.");
        }
        
    }
}
