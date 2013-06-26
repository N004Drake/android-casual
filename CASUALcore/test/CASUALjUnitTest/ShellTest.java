/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUALjUnitTest;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author adam
 */
public class ShellTest {
    final String[] valuesWeWantToSee;
    final String[] valuesWeDontWantToSee;
    final String[] cmdToExecute;
    public ShellTest(String[] good,String[] bad,String[] execute){
        cmdToExecute=execute;
        valuesWeWantToSee=good;
        valuesWeDontWantToSee=bad;
    }
    
    
    public boolean runTest(){
        //execute the command and get return value
        String cadiRetVal= new CASUAL.Shell().sendShellCommand(cmdToExecute);
        
        //Test that return value contains all values we want to see
        boolean goodChecks=true;
        for (String value:valuesWeWantToSee){
            if (! cadiRetVal.contains(value)){
                goodChecks=false;
            }
        }
        
        
        //Test that return value does not contain any value we dont want to see
        boolean badChecks=true;
        for (String value:valuesWeDontWantToSee){
            if (cadiRetVal.contains(value)){
                badChecks=false;
            }
        }
        
        return (goodChecks && badChecks);  //return true if both check out
        
    }
    
}
