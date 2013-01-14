/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 *
 * @author adam
 */
public class CASUALUpdates {
    Log Log=new Log();
    public void checkOfficialRepo(String script,String scriptIdentificationString, int CASUALSVNRev) throws MalformedURLException, IOException {
        
        scriptIdentificationString="#SVN160 , R1, ID TEH ROXOR, URLhttp://www.google.com,MessageOMFGIROXORTEHBIG11111";
        String[] id= ParseIDString(scriptIdentificationString);
        String webData = getWebData();
        String[] RemoteIDString=webData.split("\n",2);
        System.out.println(RemoteIDString[0]);
        //System.out.println(webData);
        System.out.println(id[0]+id[1]+id[2]);
        System.out.println();
    }

    private String getWebData() throws MalformedURLException, IOException {
        URL website = new URL("https://android-casual.googlecode.com/svn/trunk/GUI/src/SCRIPTS/FlymeTestRoot.scr");
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        ByteBuffer buf = ByteBuffer.allocateDirect(10);
        String webData="";
        int numRead = 0;
        while (numRead >= 0) {
            buf.rewind();

            numRead = rbc.read(buf);

            buf.rewind();

            for (int i = 0; i < numRead; i++) {
                byte b = buf.get();
                webData=webData+(new String(new byte[] {b}));

            }
        }
        return webData;
    }

    /*Takes CASUAL ID String
     * Returns: SVN Revision, Script Revision, Script Identification
     */
    private String[] ParseIDString(String scriptIdentificationString) {
        scriptIdentificationString=scriptIdentificationString.replaceAll(" ","").replaceAll("#","");
        String[] commaSplit=scriptIdentificationString.split(",");
        String[] SVNScriptRevision={"","",""};
        for (int n=0; n<commaSplit.length; n++){
              String[] splitID = commaSplit[n].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)",2);    
                  if (splitID[0].startsWith("SVN")){
                      SVNScriptRevision[0]=splitID[1];
                      System.out.println("SVNRevision: "+splitID[1]);
                                        }
                  if (splitID[0].startsWith("R")){
                      System.out.println("ScriptRevision: "+splitID[1]);  
                      SVNScriptRevision[1]=splitID[1];
                  } 
                  if (splitID[0].startsWith("ID")){
                      String id=splitID[0].replaceFirst("ID","");
                      System.out.println("Identification: " + id);  
                      SVNScriptRevision[2]=id;
                  } 
              }
        return SVNScriptRevision;
    }
}
