/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CASUAL.network.CFAutoroot;

/**
 *
 * @author adamoutler
 */
class CFAutorootTableException extends Exception {

    public CFAutorootTableException(String tables_On_autorootchainfireeu_changed) {
        System.out.println(tables_On_autorootchainfireeu_changed);
        System.out.println("Tables have changed on CFAutoRoot.");
    }
    
}
