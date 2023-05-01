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
public class ErroLexicoException extends Exception {
 
    private static final long serialVersionUID = -2346384470483785588L;
 
    public ErroLexicoException() {
        super("Erro léxico!");
    }
 
    public ErroLexicoException(String message) {
        super(message);
    }
 
    public ErroLexicoException(Throwable t) {
        super(t);
    }
}

