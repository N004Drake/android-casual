/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.archiving.libpit;

/**
 *
 * @author adamoutler
 */
class PitFormatException extends Exception {

    public PitFormatException() {
        System.out.println("Invalid PIT file format was detected");
    }

    public PitFormatException(String message) {
        System.out.println("Invalid PIT file format was detected- " + message);

    }
}
