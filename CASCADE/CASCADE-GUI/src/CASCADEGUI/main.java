/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASCADEGUI;

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CASCADEGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        Runnable r=new Runnable(){
             public void run(){
                 CASCADEGUI cg=new CASCADEGUI();
                 cg.setVisible(true);
             }
        };
        Thread t=new Thread(r);
        t.setName("GUI");
        t.start();
        
    }
}
