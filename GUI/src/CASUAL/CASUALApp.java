/*
 * NARSApp.java
 */
package CASUAL;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class CASUALApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        
        FileOperations FileOperations= new FileOperations();
        Statics Statics=new Statics();
        
        FileOperations.makeFolder(Statics.TempFolder);
        
        //Statics.GUI = new CasualJFrame();
        Statics.GUI= new CASUALJFrame();
        System.out.println(Statics.GUI.toString());
        Statics.GUI.setVisible(true);
        show(Statics.GUI); 
        Statics.GUI.setVisible(true);
        System.out.println(Statics.GUI.toString());
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     *
     * @return the instance of NARSApp
     */
    public static CASUALApp getApplication() {
        return Application.getInstance(CASUALApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(CASUALApp.class, args);
    }
}
