/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASCADEGUI;

import javax.swing.UnsupportedLookAndFeelException;

/**
 * Provides a launcher for CASCADE
 * @author adam
 */
public class main {

    /**
     *  Launches CASCADEGUI. 
     * @param args no arguments taken
     */
    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            new CASUAL.Log().errorHandler(ex);
        } catch (InstantiationException ex) {
            new CASUAL.Log().errorHandler(ex);
        } catch (IllegalAccessException ex) {
            new CASUAL.Log().errorHandler(ex);
        } catch (UnsupportedLookAndFeelException ex) {
            new CASUAL.Log().errorHandler(ex);
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                CASCADEGUI cg = new CASCADEGUI();
            }
        };
        Thread t = new Thread(r);
        t.setName("GUI");
        t.start();

    }
}
