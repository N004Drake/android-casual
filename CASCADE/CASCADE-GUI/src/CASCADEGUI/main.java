/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASCADEGUI;

import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author adam
 */
public class main {

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("System".equals(info.getName()) || info.getName().toLowerCase().contains(System.getProperty("os.name").toLowerCase().subSequence(0, 3))) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
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
                cg.setVisible(true);
            }
        };
        Thread t = new Thread(r);
        t.setName("GUI");
        t.start();

    }
}
