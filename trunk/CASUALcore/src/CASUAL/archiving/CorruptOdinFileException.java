/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.archiving;

/**
 *
 * @author adamoutler
 */
public class CorruptOdinFileException extends Exception {
    public CorruptOdinFileException(String error) {
        System.out.println("The odin File Is corrupt.");
        System.out.println(error);
    }
}
