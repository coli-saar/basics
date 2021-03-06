/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.shell;

/**
 * Exception that a @CallableFromShell method can throw to signal to the
 * shell that it should shut down.
 * 
 * @author koller
 */
public class ShutdownShellException extends Exception {

    public ShutdownShellException(Throwable thrwbl) {
        super(thrwbl);
    }

    public ShutdownShellException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public ShutdownShellException(String string) {
        super(string);
    }

    public ShutdownShellException() {
    }
    
    
}
