/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.*;

/**
 *
 * @author Adam
 */
public class PersistantShell {

    public BufferedInputStream OUTPUT;
    public BufferedInputStream ERROR;
    public BufferedOutputStream INPUT;

    PersistantShell() {
        INPUT = new BufferedOutputStream(p.getOutputStream());
        OUTPUT = new BufferedInputStream(p.getInputStream());
        ERROR = new BufferedInputStream(p.getErrorStream());

    }
    public Process p;
}
