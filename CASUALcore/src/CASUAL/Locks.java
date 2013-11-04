package CASUAL;

import CASUAL.misc.MandatoryThread;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author adamoutler
 */
public class Locks {

    public static Thread startGUI = new Thread(new Runnable() {

        @Override
        public void run() {
            if (CASUALMain.useGUI || Statics.dumbTerminalGUI) {
                startGUI = new Thread(new CASUALTools().GUI);
                startGUI.setName("CASUAL GUI");
                Statics.setStatus("launching GUI");
                startGUI.start();
            }
        }

    });
    
    static MandatoryThread caspacPrepLock=new MandatoryThread();
    public static MandatoryThread scriptRunLock=new MandatoryThread();
    public static boolean lockGUIformPrep = true;
    public static boolean lockGUIunzip = true;
    public static boolean caspacScriptPrepLock = true;

}
