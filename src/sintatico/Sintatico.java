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
import java.io.UnsupportedEncodingException;
import javax.swing.filechooser.FileFilter;
import java.io.FileNotFoundException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.JTextArea;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;

/**
 * Sintatico - Primeira versao do sintatico
 *
 * @author Turma de projeto de compiladores 1/2023
 *
 *
 * gramatica:
 *
 * <G> ::= 'PROGRAM {' <LISTA> <CMDS> '}'
 * <LISTA> ::= 'VAR' <VARS> ';'
 * <VARS> ::= <VAR> , <VARS>
 * <VARS> ::= <VAR>
 * <VAR> ::= <ID>
 * <CMDS> ::= <CMD> ; <CMDS>
 * <CMDS> ::= <CMD>
 * <CMD> ::= <CMD_IF>
 * <CMD> ::= <CMD_WHILE>
 * <CMD> ::= <CMD_FOR>
 * <CMD> ::= <CMD_ASSIGNMENT>
 * <CMD> ::= <CMD_READ>
 * <CMD> ::= <CMD_WRITE>
 * <CMD_IF> ::= 'IF (' <CONDICAO> ') {' <CMDS> '}'
 * <CMD_IF> ::= 'IF (' <CONDICAO> ') {' <CMDS> '} ELSE {' <CMDS> '}'
 * <CMD_WHILE> ::= 'WHILE(' <CONDICAO> ') {' <CMDS> '}'
 * <CMD_PARA> ::= 'FOR(' <VAR> '=' <E> 'TO' <E> ') {' <CMDS> '}'
 * <CMD_ASSIGNMENT> ::= <VAR> '=' <E>
 * <CMD_READ> ::= 'READ' '(' <VAR> ')'
 * <CMD_WRITE> ::= 'WRITE' '(' <E> ')'
 * <CONDICAO> ::= <E> '>' <E>
 * <CONDICAO> ::= <E> '>=' <E>
 * <CONDICAO> ::= <E> '!=' <E>
 * <CONDICAO> ::= <E> '<=' <E> <
 * CONDICAO> ::= <E> '<' <E>
 * <CONDICAO> ::= <E> '==' <E>
 * <E> ::= <E> + <T>
 * <E> ::= <E> - <T>
 * <E> ::= <T>
 * <T> ::= <T> * <F>
 * <T> ::= <T> / <F>
 * <T> ::= <T> % <F>
 * <T> ::= <F>
 * <F> ::= -<X>
 * <F> ::= <X> ** <F>
 * <F> ::= <X>
 * <X> ::= '(' <E> ')'
 * <X> ::= [0-9]+('.'[0-9]+)
 * <X> ::= <VAR>
 * <ID> ::= [A-Z]+([A-Z]_[0-9]*)
 *
 */
public class Sintatico {

    // Lista de tokens	
    static final int T_PROGRAM = 1;
    static final int T_END = 2;
    static final int T_VAR = 3;
    static final int T_COMMA = 4;
    static final int T_SEMICOLON = 5;
    static final int T_IF = 6;
    static final int T_ELSE = 7;
    static final int T_END_IF = 8;
    static final int T_WHILE = 9;
    static final int T_END_WHILE = 10;
    static final int T_FOR = 11;
    static final int T_ASSIGNMENT = 12;
    static final int T_TO = 13;
    static final int T_END_FOR = 14;
    static final int T_READ = 15;
    static final int T_OPEN_PAR = 16;
    static final int T_CLOSE_PAR = 17;
    static final int T_WRITE = 18;
    static final int T_GREATEST = 19;
    static final int T_LOWEST = 20;
    static final int T_GREATER_OR_EQUAL = 21;
    static final int T_LESS_OR_EQUAL = 22;
    static final int T_EQUAL = 23;
    static final int T_DIFFERENT = 24;
    static final int T_PLUS = 25;
    static final int T_MINUS = 26;
    static final int T_TIMES = 27;
    static final int T_DIVISION = 28;
    static final int T_REMAINDER = 29;
    static final int T_POWER = 30;
    static final int T_NUMBER = 31;
    static final int T_ID = 32;
    static final int T_OPEN_BRACKET = 33;
    static final int T_CLOSE_BRACKET = 34;
    static final int T_SWITCH = 35;
    static final int T_CASE = 36;
    static final int T_BREAK = 37;
    static final int T_DEFAULT = 38;
    static final int T_COLON = 39;
    static final int T_FOREACH = 40;
    static final int T_ERROR_LEX = 98;
    static final int T_NULL = 99;

    static final int END_FILE = 26;

    static final int E_NO_ERRORS = 0;
    static final int E_ERROR_LEXICO = 1;
    static final int E_ERROR_SINTATICO = 2;

    // Variaveis que surgem no Lexico
    static File arqFonte;
    static BufferedReader rdFonte;
    static File arqDestino;
    static char lookAhead;
    static int token;
    static String lexema;
    static int ponteiro;
    static String linhaFonte;
    static int linhaAtual;
    static int colunaAtual;
    static String mensagemDeErro;
    static StringBuffer tokensIdentificados = new StringBuffer();

    // Variaveis adicionadas para o sintatico
    static StringBuffer regrasReconhecidas = new StringBuffer();
    static int estadoCompilacao;

    public static void main(String s[]) throws ErroLexicoException {
        try {
            abreArquivo();
            abreDestino();
            linhaAtual = 0;
            colunaAtual = 0;
            ponteiro = 0;
            linhaFonte = "";
            token = T_NULL;
            mensagemDeErro = "";
            tokensIdentificados.append("Tokens reconhecidos: \n\n");
            regrasReconhecidas.append("\n\nRegras reconhecidas: \n\n");
            estadoCompilacao = E_NO_ERRORS;

            // posiciono no primeiro token
            movelookAhead();
            buscaProximoToken();

            analiseSintatica();

            exibeSaida();

            gravaSaida(arqDestino);

            fechaFonte();

        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, "Arquivo nao existe!", "FileNotFoundException!", JOptionPane.ERROR_MESSAGE);
        } catch (UnsupportedEncodingException uee) {
            JOptionPane.showMessageDialog(null, "Erro desconhecido", "UnsupportedEncodingException!", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Erro de io: " + ioe.getMessage(), "IOException!", JOptionPane.ERROR_MESSAGE);
        } catch (ErroLexicoException ele) {
            JOptionPane.showMessageDialog(null, ele.getMessage(), "Erro Lexico Exception!", JOptionPane.ERROR_MESSAGE);
        } catch (ErroSintaticoException ese) {
            JOptionPane.showMessageDialog(null, ese.getMessage(), "Erro Sint�tico Exception!", JOptionPane.ERROR_MESSAGE);
        } finally {
            System.out.println("Execucao terminada!");
        }
    }

    static void analiseSintatica() throws IOException, ErroLexicoException, ErroSintaticoException {

        g();

        if (estadoCompilacao == E_ERROR_LEXICO) {
            JOptionPane.showMessageDialog(null, mensagemDeErro, "Erro Lexico!", JOptionPane.ERROR_MESSAGE);
        } else if (estadoCompilacao == E_ERROR_SINTATICO) {
            JOptionPane.showMessageDialog(null, mensagemDeErro, "Erro Sintatico!", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Analise Sintatica terminada sem erros", "Analise Sintatica terminada!", JOptionPane.INFORMATION_MESSAGE);
            acumulaRegraSintaticaReconhecida("<G>");
        }
    }

    // <G> ::= 'PROGRAM {' <LISTA> <CMDS> '}'
    private static void g() throws IOException, ErroLexicoException, ErroSintaticoException {
        if (token == T_PROGRAM) {
            buscaProximoToken();

            if (token == T_OPEN_BRACKET) {
                buscaProximoToken();
                lista();

                if (token == T_CLOSE_BRACKET) {
                    buscaProximoToken();
                    acumulaRegraSintaticaReconhecida("<G> ::= 'PROGRAM {' <LISTA> <CMDS> '}'");
                } else {
                    while(token != T_END) {
                        cmds();
                    }
                    if (token == T_CLOSE_BRACKET) {
                        buscaProximoToken();
                        acumulaRegraSintaticaReconhecida("<G> ::= 'PROGRAM {' <LISTA> <CMDS> '}'");
                    }
                }
            } else {
                registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n('{') esperado, mas encontrei: " + lexema);
            }
        } else {
            registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n('program') esperado, mas encontrei: " + lexema);
        }
    }

    // <LISTA> ::= 'VARS' <VARS>
    private static void lista() throws IOException, ErroLexicoException, ErroSintaticoException {
        if (token == T_VAR) {
            buscaProximoToken();
            vars();
            if (token == T_SEMICOLON) {
                buscaProximoToken();
                acumulaRegraSintaticaReconhecida("<LISTA> ::= 'VAR' <VARS>");
            } else {
                registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n';' esperado, mas encontrei: " + lexema);
            }
        } else {
            registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n('var') esperado, mas encontrei: " + lexema);
        }
    }

    // <VARS> ::= <VAR> , <VARS> | <VAR> 
    private static void vars() throws IOException, ErroLexicoException, ErroSintaticoException {
        var();
        while (token == T_COMMA) {
            buscaProximoToken();
            var();
        }
        acumulaRegraSintaticaReconhecida("<VARS> ::= <VAR> , <VARS> | <VAR>");
    }

    // <VAR> ::= <ID> 
    private static void var() throws IOException, ErroLexicoException, ErroSintaticoException {
        id();
        acumulaRegraSintaticaReconhecida("<VAR> ::= <ID>");
    }

    // <ID> ::= [A-Z]+([A-Z]_[0-9])*
    private static void id() throws IOException, ErroLexicoException, ErroSintaticoException {
        if (token == T_ID) {
            buscaProximoToken();
            acumulaRegraSintaticaReconhecida("<ID> ::= [A-Z]+([A-Z]_[0-9])*");
        } else {
            registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nEsperava um identificador. Encontrei: " + lexema);
        }
    }

    // <CMDS> ::= <CMD> ; <CMDS> | <CMD>
    private static void cmds() throws IOException, ErroLexicoException, ErroSintaticoException {
        cmd();
        while (token == T_SEMICOLON) {
            buscaProximoToken();
            cmd();
        }
        acumulaRegraSintaticaReconhecida("<CMDS> ::= <CMD> ; <CMDS> | <CMD>");
    }

    // <CMD> ::= <CMD_IF>
    // <CMD> ::= <CMD_WHILE>
    // <CMD> ::= <CMD_FOR>
    // <CMD> ::= <CMD_ASSINGMENT>
    // <CMD> ::= <CMD_READ>
    // <CMD> ::= <CMD_WRITE>
    private static void cmd() throws IOException, ErroLexicoException, ErroSintaticoException {
        switch (token) {
            case T_IF:
                cmd_if();
                break;
            case T_WHILE:
                cmd_while();
                break;
            case T_FOR:
                cmd_for();
                break;
            case T_ID:
                cmd_assignment();
                break;
            case T_READ:
                cmd_read();
                break;
            case T_WRITE:
                cmd_write();
                break;
            case T_SWITCH:
                cmd_switch();
                break;
            case T_CLOSE_BRACKET:
                break;
            default:
                registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\nComando nao identificado va aprender a programar pois encontrei: " + lexema);
        }
        acumulaRegraSintaticaReconhecida("<CMD> ::= <CMD_IF>|<CMD_WHILE>|<CMD_FOR>|<CMD_ASSIGNMENT>|<CMD_READ>|<CMD_WRITE>");
    }

    // <CMD_IF> ::= 'IF(' <CONDICAO>') {' <CMDS> '}' 
    // <CMD_IF> ::= 'IF(' <CONDICAO>') {' <CMDS> '} ELSE {' <CMDS> '}' 
    private static void cmd_if() throws IOException, ErroLexicoException, ErroSintaticoException {
        if (token == T_IF) {
            buscaProximoToken();
            if (token == T_OPEN_PAR) {
                buscaProximoToken();
                condicao();
                if (token == T_CLOSE_PAR) {
                    buscaProximoToken();
                    if (token == T_OPEN_BRACKET) {
                        buscaProximoToken();
                        cmds();
                        if (token == T_CLOSE_BRACKET) {
                            buscaProximoToken();
                            if (token == T_ELSE) {
                                buscaProximoToken();
                                if (token == T_OPEN_BRACKET) {
                                    buscaProximoToken();
                                    cmds();
                                    if (token == T_CLOSE_BRACKET) {
                                        buscaProximoToken();
                                        
                                    } else {
                                        registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'}' esperado mas encontrei: " + lexema);
                                    }
                                }
                            }
                            acumulaRegraSintaticaReconhecida("<CMD_IF> ::= 'IF(' <CONDICAO>') {' <CMDS> '} ELSE {' <CMDS> '}' ");
                        } else {
                            registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'}' esperado mas encontrei: " + lexema);
                        }

                    } else {
                        registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'{' esperado mas encontrei: " + lexema);
                    }
                } else {
                    registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n')' esperado mas encontrei: " + lexema);
                }
            } else {
                registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'(' esperado mas encontrei: " + lexema);
            }
        }
        //acumulaRegraSintaticaReconhecida("<CMD_IF> ::= 'IF(' <CONDICAO> ') {' <CMDS> '}' | '} ELSE' {' <CMDS> '}'");
    }

    // <CMD_WHILE> ::= 'WHILE(' <CONDICAO> ') {' <CMDS> '}'
    private static void cmd_while() throws IOException, ErroLexicoException, ErroSintaticoException {
        if (token == T_WHILE) {
            buscaProximoToken();

            if (token == T_OPEN_PAR) {
                buscaProximoToken();
                condicao();

                if (token == T_CLOSE_PAR) {
                    buscaProximoToken();
                    if (token == T_OPEN_BRACKET) {
                        buscaProximoToken();
                        cmds();

                        if (token == T_CLOSE_BRACKET) {
                            buscaProximoToken();
                            acumulaRegraSintaticaReconhecida("<CMD_WHILE> ::= 'WHILE(' <CONDICAO> ') {' <CMDS> '}'");
                        } else {
                            registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'}' esperado mas encontrei: " + lexema);
                        }
                    } else {
                        registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'{' esperado mas encontrei: " + lexema);
                    }
                } else {
                    registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n')' esperado mas encontrei: " + lexema);
                }
            } else {
                registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'(' esperado mas encontrei: " + lexema);
            }
        } else {
            registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'enquanto' esperado mas encontrei: " + lexema);
        }
    }

    // <CMD_FOR> ::= 'FOR(' <VAR> '=' <E> 'TO' <E> ') {' <CMDS> '}' 
    private static void cmd_for() throws IOException, ErroLexicoException, ErroSintaticoException {
        if (token == T_FOR) {
            buscaProximoToken();
            if (token == T_OPEN_PAR) {
                buscaProximoToken();
                var();
                if (token == T_ASSIGNMENT) {
                    buscaProximoToken();
                    e();
                    if (token == T_TO) {
                        buscaProximoToken();
                        e();

                        if (token == T_OPEN_BRACKET) {
                            buscaProximoToken();
                            cmds();

                            if (token == T_CLOSE_BRACKET) {
                                buscaProximoToken();
                                acumulaRegraSintaticaReconhecida("<CMD_FOR> ::= 'FOR(' <VAR> '=' <E> 'TO' <E> ') {' <CMDS> '}'");
                            } else {
                                registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'}' esperado mas encontrei: " + lexema);
                            }
                        } else {
                            registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'{' esperado mas encontrei: " + lexema);
                        }
                    } else {
                        registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'TO' esperado mas encontrei: " + lexema);
                    }
                } else {
                    registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'=' esperado mas encontrei: " + lexema);
                }
            } else {
                registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'(' esperado mas encontrei: " + lexema);
            }
        } else {
            registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'FOR' esperado mas encontrei: " + lexema);
        }
    }
    
    // <CMD_WHILE> ::= 'WHILE(' <CONDICAO> ') {' <CMDS> '}'
    private static void cmd_case() throws IOException, ErroLexicoException, ErroSintaticoException {
        if (token == T_CASE) {
            buscaProximoToken();
            e();

            if (token == T_COLON) {
                buscaProximoToken();
                cmds();

                if (token == T_BREAK || token == T_CASE || token == T_DEFAULT) {
                    if (token == T_BREAK || token == T_CASE) {
                        cmd_case();
                    }
                    
                    if (token == T_DEFAULT) {
                        buscaProximoToken();
                        
                        if (token == T_COLON) {
                            buscaProximoToken();
                            cmds();
                        } else {
                    registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n':' esperado mas encontrei: " + lexema);
                }

                    } else {
                    registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'default' esperado mas encontrei: " + lexema);
                }

                } else {
                    registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'break' ou 'case' esperado mas encontrei: " + lexema);
                }
            } else {
                registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n':' esperado mas encontrei: " + lexema);
            }
            
        } else {
            registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'case' esperado mas encontrei: " + lexema);
        }
        
    }
    
    private static void cmd_switch() throws IOException, ErroLexicoException, ErroSintaticoException {
        if (token == T_SWITCH) {
            buscaProximoToken();

            if (token == T_OPEN_PAR) {
                buscaProximoToken();
                e();

                if (token == T_CLOSE_PAR) {
                    buscaProximoToken();
                    if (token == T_OPEN_BRACKET) {
                        buscaProximoToken();
                        cmd_case();
                        
                        buscaProximoToken();
                        
                        if (token == T_CLOSE_BRACKET) {
                            buscaProximoToken();
                            acumulaRegraSintaticaReconhecida("<CMD_SWITCH> ::= 'SWITCH(' <E> '){ ' <CMDS_CASE> 'default:' <CMDS> '}'");
                        } else {
                            registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'}' esperado mas encontrei: " + lexema);
                        }
                    } else {
                        registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'{' esperado mas encontrei: " + lexema);
                    }
                } else {
                    registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n')' esperado mas encontrei: " + lexema);
                }
            } else {
                registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'(' esperado mas encontrei: " + lexema);
            }
        } else {
            registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'enquanto' esperado mas encontrei: " + lexema);
        }
    }

    // <CMD_ASSIGNMENT> ::= <VAR> '=' <E>
    private static void cmd_assignment() throws IOException, ErroLexicoException, ErroSintaticoException {
        var();
        if (token == T_ASSIGNMENT) {
            buscaProximoToken();
            e();
            acumulaRegraSintaticaReconhecida("<CMD_ASSIGNMENT> ::= <VAR> '=' <E>");
        } else {
            registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'=' esperado mas encontrei: " + lexema);
        }
    }

    // <CMD_READ> ::= 'READ' '(' <VAR> ')' 
    private static void cmd_read() throws IOException, ErroLexicoException, ErroSintaticoException {
        if (token == T_READ) {
            buscaProximoToken();
            if (token == T_OPEN_PAR) {
                buscaProximoToken();
                var();
                if (token == T_CLOSE_PAR) {
                    buscaProximoToken();
                    acumulaRegraSintaticaReconhecida("<CMD_READ> ::= 'READ' '(' <VAR> ')'");
                } else {
                    registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n')' esperado mas encontrei: " + lexema);
                }
            } else {
                registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'(' esperado mas encontrei: " + lexema);
            }
        } else {
            registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'READ' esperado mas encontrei: " + lexema);
        }
    }

    // <CMD_WRITE> ::= 'WRITE' '(' <E> ')'
    private static void cmd_write() throws IOException, ErroLexicoException, ErroSintaticoException {
        if (token == T_WRITE) {
            buscaProximoToken();
            if (token == T_OPEN_PAR) {
                buscaProximoToken();
                e();
                if (token == T_CLOSE_PAR) {
                    buscaProximoToken();
                    acumulaRegraSintaticaReconhecida("<CMD_WRITE> ::= 'WRITE' '(' <E> ')'");
                } else {
                    registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n')' esperado mas encontrei: " + lexema);
                }
            } else {
                registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'(' esperado mas encontrei: " + lexema);
            }
        } else {
            registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'WRITE' esperado mas encontrei: " + lexema);
        }
    }

    // <CONDICAO> ::= <E> '>' <E> 
    // <CONDICAO> ::= <E> '>=' <E> 
    // <CONDICAO> ::= <E> '!=' <E> 
    // <CONDICAO> ::= <E> '<=' <E> 
    // <CONDICAO> ::= <E> '<' <E> 
    // <CONDICAO> ::= <E> '==' <E>
    private static void condicao() throws ErroLexicoException, IOException, ErroSintaticoException {
        e();
        switch (token) {
            case T_GREATEST:
                buscaProximoToken();
                e();
                break;
            case T_LOWEST:
                buscaProximoToken();
                e();
                break;
            case T_GREATER_OR_EQUAL:
                buscaProximoToken();
                e();
                break;
            case T_LESS_OR_EQUAL:
                buscaProximoToken();
                e();
                break;
            case T_EQUAL:
                buscaProximoToken();
                e();
                break;
            case T_DIFFERENT:
                buscaProximoToken();
                e();
                break;
            default:
                registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nEsperava um operador logico. Encontrei: " + lexema);
        }
        acumulaRegraSintaticaReconhecida("<CONDICAO> ::= <E> ('>'|'>='|'!='|'<='|'<'|'==') <E> ");
    }

    // <E> ::= <E> + <T>
    // <E> ::= <E> - <T>
    // <E> ::= <T>
    private static void e() throws IOException, ErroLexicoException, ErroSintaticoException {
        t();
        while ((token == T_PLUS) || (token == T_MINUS)) {
            buscaProximoToken();
            t();
        }
        acumulaRegraSintaticaReconhecida("<E> ::= <E> + <T>|<E> - <T>|<T> ");
    }

    // <T> ::= <T> * <F>
    // <T> ::= <T> / <F>
    // <T> ::= <T> % <F>
    // <T> ::= <F>
    private static void t() throws IOException, ErroLexicoException, ErroSintaticoException {
        f();
        while ((token == T_TIMES) || (token == T_DIVISION) || (token == T_REMAINDER)) {
            buscaProximoToken();
            f();
        }
        acumulaRegraSintaticaReconhecida("<T> ::= <T> * <F>|<T> / <F>|<T> % <F>|<F>");
    }

    // <F> ::= -<F>
    // <F> ::= <X> ** <F>
    // <F> ::= <X>     
    private static void f() throws IOException, ErroLexicoException, ErroSintaticoException {
        if (token == T_MINUS) {
            buscaProximoToken();
            f();
        } else {
            x();
            while (token == T_POWER) {
                buscaProximoToken();
                x();
            }
        }
        acumulaRegraSintaticaReconhecida("<F> ::= -<F>|<X> ** <F>|<X> ");

    }

    // <X> ::= '(' <E> ')'
    // <X> ::= [0-9]+('.'[0-9]+)
    // <X> ::= <VAR>
    private static void x() throws IOException, ErroLexicoException, ErroSintaticoException {
        switch (token) {
            case T_ID:
                buscaProximoToken();
                acumulaRegraSintaticaReconhecida("<X> ::= <VAR>");
                break;
            case T_NUMBER:
                buscaProximoToken();
                acumulaRegraSintaticaReconhecida("<X> ::= [0-9]+('.'[0-9]+)");
                break;
            case T_OPEN_PAR: {
                buscaProximoToken();
                e();
                if (token == T_CLOSE_PAR) {
                    buscaProximoToken();
                    acumulaRegraSintaticaReconhecida("<X> ::= '(' <E> ')'");
                } else {
                    registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n')' esperado mas encontrei: " + lexema);
                }
            }
            break;
            default:
                registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\nFator invalido: encontrei: " + lexema);
        }
    }

    static void fechaFonte() throws IOException {
        rdFonte.close();
    }

    static void movelookAhead() throws IOException {

        if ((ponteiro + 1) > linhaFonte.length()) {

            linhaAtual++;
            ponteiro = 0;

            if ((linhaFonte = rdFonte.readLine()) == null) {
                lookAhead = END_FILE;
            } else {

                StringBuffer sbLinhaFonte = new StringBuffer(linhaFonte);
                sbLinhaFonte.append('\13').append('\10');
                linhaFonte = sbLinhaFonte.toString();

                lookAhead = linhaFonte.charAt(ponteiro);
            }
        } else {
            lookAhead = linhaFonte.charAt(ponteiro);
        }

        // Se comentar esse if, eu terei uma linguagem 
        // que diferencia minusculas de maiusculas
        if ((lookAhead >= 'a')
                && (lookAhead <= 'z')) {
            lookAhead = (char) (lookAhead - 'a' + 'A');
        }

        ponteiro++;
        colunaAtual = ponteiro + 1;
    }

    static void buscaProximoToken() throws IOException, ErroLexicoException {
        //int i, j;

        StringBuffer sbLexema = new StringBuffer("");

        // Salto espaçoes enters e tabs até o inicio do proximo token
        while ((lookAhead == 9)
                || (lookAhead == '\n')
                || (lookAhead == 8)
                || (lookAhead == 11)
                || (lookAhead == 12)
                || (lookAhead == '\r')
                || (lookAhead == 32)) {
            movelookAhead();
        }

        /*--------------------------------------------------------------*
     * Caso o primeiro caracter seja alfabetico, procuro capturar a *
     * sequencia de caracteres que se segue a ele e classifica-la   *
     *--------------------------------------------------------------*/
        if ((lookAhead >= 'A') && (lookAhead <= 'Z')) {
            sbLexema.append(lookAhead);
            movelookAhead();

            while (((lookAhead >= 'A') && (lookAhead <= 'Z'))
                    || ((lookAhead >= '0') && (lookAhead <= '9')) || (lookAhead == '_')) {
                sbLexema.append(lookAhead);
                movelookAhead();
            }

            lexema = sbLexema.toString();

            /* Classifico o meu token como palavra reservada ou id */
            if (lexema.equals("PROGRAM")) {
                token = T_PROGRAM;
            } else if (lexema.equals("END")) {
                token = T_END;
            } else if (lexema.equals("VAR")) {
                token = T_VAR;
            } else if (lexema.equals("IF")) {
                token = T_IF;
            } else if (lexema.equals("ELSE")) {
                token = T_ELSE;
            } else if (lexema.equals("END IF")) {
                token = T_END_IF;
            } else if (lexema.equals("WHILE")) {
                token = T_WHILE;
            } else if (lexema.equals("END WHILE")) {
                token = T_END_WHILE;
            } else if (lexema.equals("FOR")) {
                token = T_FOR;
            } else if (lexema.equals("TO")) {
                token = T_TO;
            } else if (lexema.equals("END FOR")) {
                token = T_END_FOR;
            } else if (lexema.equals("READ")) {
                token = T_READ;
            } else if (lexema.equals("WRITE")) {
                token = T_WRITE;
            } else if (lexema.equals("SWITCH")) {
                token = T_SWITCH; 
            } else if (lexema.equals("CASE")) {
                token = T_CASE; 
            } else if (lexema.equals(":")) {
                token = T_COLON;  
            } else if (lexema.equals("DEFAULT")) {
                token = T_DEFAULT; 
            } else if (lexema.equals("BREAK")) {
                token = T_BREAK; 
            } else {
                token = T_ID;
            }
        } else if ((lookAhead >= '0') && (lookAhead <= '9')) {
            sbLexema.append(lookAhead);
            movelookAhead();
            while ((lookAhead >= '0') && (lookAhead <= '9')) {
                sbLexema.append(lookAhead);
                movelookAhead();
            }
            token = T_NUMBER;
        } else if (lookAhead == '(') {
            sbLexema.append(lookAhead);
            token = T_OPEN_PAR;
            movelookAhead();
        } else if (lookAhead == ')') {
            sbLexema.append(lookAhead);
            token = T_CLOSE_PAR;
            movelookAhead();
        } else if (lookAhead == '{') {
            sbLexema.append(lookAhead);
            token = T_OPEN_BRACKET;
            movelookAhead();
        } else if (lookAhead == '}') {
            sbLexema.append(lookAhead);
            token = T_CLOSE_BRACKET;
            movelookAhead();
        } else if (lookAhead == ';') {
            sbLexema.append(lookAhead);
            token = T_SEMICOLON;
            movelookAhead();
        } else if (lookAhead == ',') {
            sbLexema.append(lookAhead);
            token = T_COMMA;
            movelookAhead();
        } else if (lookAhead == '+') {
            sbLexema.append(lookAhead);
            token = T_PLUS;
            movelookAhead();
        } else if (lookAhead == '-') {
            sbLexema.append(lookAhead);
            token = T_MINUS;
            movelookAhead();
        } else if (lookAhead == '*') {
            sbLexema.append(lookAhead);
            movelookAhead();
            if (lookAhead == '*') {
                sbLexema.append(lookAhead);
                movelookAhead();
                token = T_POWER;
            } else {
                token = T_TIMES;
            }
        } else if (lookAhead == '/') {
            sbLexema.append(lookAhead);
            token = T_DIVISION;
            movelookAhead();
        } else if (lookAhead == '%') {
            sbLexema.append(lookAhead);
            token = T_REMAINDER;
            movelookAhead();
        } else if (lookAhead == '=') {
            sbLexema.append(lookAhead);
            token = T_ASSIGNMENT;
            movelookAhead();
        } else if (lookAhead == '!') {
            sbLexema.append(lookAhead);
            movelookAhead();
            if (lookAhead == '=') {
                sbLexema.append(lookAhead);
                movelookAhead();
                token = T_DIFFERENT;
            }
        } else if (lookAhead == ':') {
            sbLexema.append(lookAhead);
            movelookAhead();
            token = T_COLON;
        } else if (lookAhead == '<') {
            sbLexema.append(lookAhead);
            movelookAhead();
            if (lookAhead == '=') {
                sbLexema.append(lookAhead);
                movelookAhead();
                token = T_LESS_OR_EQUAL;
            } else {
                token = T_LOWEST;
            }
        } else if (lookAhead == '>') {
            sbLexema.append(lookAhead);
            movelookAhead();
            if (lookAhead == '=') {
                sbLexema.append(lookAhead);
                movelookAhead();
                token = T_GREATER_OR_EQUAL;
            } else {
                token = T_GREATEST;
            }
        } else if (lookAhead == END_FILE) {
            token = T_CLOSE_BRACKET;
        } else {
            token = T_ERROR_LEX;
            sbLexema.append(lookAhead);
        }

        lexema = sbLexema.toString();

        mostraToken();

        if (token == T_ERROR_LEX) {
            mensagemDeErro = "Erro Léxico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\nToken desconhecido: " + lexema;
            throw new ErroLexicoException(mensagemDeErro);
        }
    }

    static void mostraToken() {

        StringBuffer tokenLexema = new StringBuffer("");

        switch (token) {
            case T_PROGRAM:
                tokenLexema.append("T_PROGRAM");
                break;
            case T_END:
                tokenLexema.append("T_END");
                break;
            case T_VAR:
                tokenLexema.append("T_VAR");
                break;
            case T_COMMA:
                tokenLexema.append("T_COMMA");
                break;
            case T_SEMICOLON:
                tokenLexema.append("T_SEMICOLON");
                break;
            case T_IF:
                tokenLexema.append("T_IF");
                break;
            case T_ELSE:
                tokenLexema.append("T_ELSE");
                break;
            case T_END_IF:
                tokenLexema.append("T_END_IF");
                break;
            case T_WHILE:
                tokenLexema.append("T_WHILE");
                break;
            case T_END_WHILE:
                tokenLexema.append("T_END_WHILE");
                break;
            case T_FOR:
                tokenLexema.append("T_FOR");
                break;
            case T_ASSIGNMENT:
                tokenLexema.append("T_ASSIGNMENT");
                break;
            case T_TO:
                tokenLexema.append("T_TO");
                break;
            case T_END_FOR:
                tokenLexema.append("T_END_FOR");
                break;
            case T_READ:
                tokenLexema.append("T_READ");
                break;
            case T_OPEN_PAR:
                tokenLexema.append("T_OPEN_PAR");
                break;
            case T_CLOSE_PAR:
                tokenLexema.append("T_CLOSE_PAR");
                break;
            case T_OPEN_BRACKET:
                tokenLexema.append("T_OPEN_BRACKET");
                break;
            case T_CLOSE_BRACKET:
                tokenLexema.append("T_CLOSE_BRACKET");
                break;
            case T_WRITE:
                tokenLexema.append("T_WRITE");
                break;
            case T_GREATEST:
                tokenLexema.append("T_GREATEST");
                break;
            case T_LOWEST:
                tokenLexema.append("T_LOWEST");
                break;
            case T_GREATER_OR_EQUAL:
                tokenLexema.append("T_GREATER_OR_EQUAL");
                break;
            case T_LESS_OR_EQUAL:
                tokenLexema.append("T_LESS_OR_EQUAL");
                break;
            case T_EQUAL:
                tokenLexema.append("T_EQUAL");
                break;
            case T_DIFFERENT:
                tokenLexema.append("T_DIFFERENT");
                break;
            case T_PLUS:
                tokenLexema.append("T_PLUS");
                break;
            case T_MINUS:
                tokenLexema.append("T_MINUS");
                break;
            case T_TIMES:
                tokenLexema.append("T_TIMES");
                break;
            case T_DIVISION:
                tokenLexema.append("T_DIVISION");
                break;
            case T_REMAINDER:
                tokenLexema.append("T_REMAINDER");
                break;
            case T_POWER:
                tokenLexema.append("T_POWER");
                break;
            case T_NUMBER:
                tokenLexema.append("T_NUMBER");
                break;
            case T_SWITCH:
                tokenLexema.append("T_SWITCH");
                break;
            case T_CASE:
                tokenLexema.append("T_CASE");
                break;
            case T_DEFAULT:
                tokenLexema.append("T_DEFAULT");
                break;
            case T_COLON:
                tokenLexema.append("T_COLON");
                break;
            case T_BREAK:
                tokenLexema.append("T_BREAK");
                break;
            case T_ID:
                tokenLexema.append("T_ID");
                break;
            case T_ERROR_LEX:
                tokenLexema.append("T_ERROR_LEX");
                break;
            case T_NULL:
                tokenLexema.append("T_NULL");
                break;
            default:
                tokenLexema.append("N/A");
                break;
        }
        System.out.println(tokenLexema.toString() + " ( " + lexema + " )");
        acumulaToken(tokenLexema.toString() + " ( " + lexema + " )");
        tokenLexema.append(lexema);
    }

    private static void abreArquivo() {

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FiltroSab filtro = new FiltroSab();

        fileChooser.addChoosableFileFilter(filtro);
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        arqFonte = fileChooser.getSelectedFile();
        abreFonte(arqFonte);

    }

    private static boolean abreFonte(File fileName) {

        if (arqFonte == null || fileName.getName().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Nome de Arquivo Invalido", "Nome de Arquivo Invalido", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            linhaAtual = 1;
            try {
                FileReader fr = new FileReader(arqFonte);
                rdFonte = new BufferedReader(fr);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private static void abreDestino() {

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FiltroSab filtro = new FiltroSab();

        fileChooser.addChoosableFileFilter(filtro);
        int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        arqDestino = fileChooser.getSelectedFile();
    }

    private static boolean gravaSaida(File fileName) {

        if (arqDestino == null || fileName.getName().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Nome de Arquivo Invalido", "Nome de Arquivo Invalido", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            FileWriter fw;
            try {
                System.out.println(arqDestino.toString());
                System.out.println(tokensIdentificados.toString());
                fw = new FileWriter(arqDestino);
                BufferedWriter bfw = new BufferedWriter(fw);
                bfw.write(tokensIdentificados.toString());
                bfw.write(regrasReconhecidas.toString());
                bfw.close();
                JOptionPane.showMessageDialog(null, "Arquivo Salvo: " + arqDestino, "Salvando Arquivo", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Erro de Entrada/Sa�da", JOptionPane.ERROR_MESSAGE);
            }
            return true;
        }
    }

    public static void exibeTokens() {

        JTextArea texto = new JTextArea();
        texto.append(tokensIdentificados.toString());
        JOptionPane.showMessageDialog(null, texto, "Tokens Identificados (token/lexema)", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void acumulaRegraSintaticaReconhecida(String regra) {

        regrasReconhecidas.append(regra);
        regrasReconhecidas.append("\n");

    }

    public static void acumulaToken(String tokenIdentificado) {

        tokensIdentificados.append(tokenIdentificado);
        tokensIdentificados.append("\n");

    }

    public static void exibeSaida() {

        JTextArea texto = new JTextArea();
        texto.append(tokensIdentificados.toString());
        JOptionPane.showMessageDialog(null, texto, "Analise Lexica", JOptionPane.INFORMATION_MESSAGE);

        texto.setText(regrasReconhecidas.toString());
        texto.append("\n\nStatus da Compilacao:\n\n");
        texto.append(mensagemDeErro);

        JOptionPane.showMessageDialog(null, texto, "Resumo da Compilacao", JOptionPane.INFORMATION_MESSAGE);
    }

    static void registraErroSintatico(String msg) throws ErroSintaticoException {
        if (estadoCompilacao == E_NO_ERRORS) {
            estadoCompilacao = E_ERROR_SINTATICO;
            mensagemDeErro = msg;
        }
        throw new ErroSintaticoException(msg);
    }

}

/**
 * Classe Interna para criacao de filtro de selecao
 */
class FiltroSab extends FileFilter {

    public boolean accept(File arg0) {
        if (arg0 != null) {
            if (arg0.isDirectory()) {
                return true;
            }
            if (getExtensao(arg0) != null) {
                if (getExtensao(arg0).equalsIgnoreCase("grm")) {
                    return true;
                }
            };
        }
        return false;
    }

    /**
     * Retorna quais extensoes poderao ser escolhidas
     */
    public String getDescription() {
        return "*.grm";
    }

    /**
     * Retorna a parte com a extensao de um arquivo
     */
    public String getExtensao(File arq) {
        if (arq != null) {
            String filename = arq.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1) {
                return filename.substring(i + 1).toLowerCase();
            };
        }
        return null;
    }
}
