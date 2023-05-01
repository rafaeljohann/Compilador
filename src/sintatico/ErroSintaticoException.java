/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sintatico;

/**
 *
 * @author Rafael
 */
public class ErroSintaticoException extends Exception {
 
    private static final long serialVersionUID = -2346384470483785588L;
 
    public ErroSintaticoException() {
        super("Erro sintático!");
    }
 
    public ErroSintaticoException(String message) {
        super(message);
    }
 
    public ErroSintaticoException(Throwable t) {
        super(t);
    }
}
