package semantico;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

/**
 * Semantico - Primeira versao do semantico
 * 
 * @author Rafael Johann
 * @author Turma de projeto de compiladores 1/2023
 *

gramatica:

 * <G> ::= 'PROGRAM' <LISTA> <CMDS> 'END;'
 * <LISTA> ::= 'VAR' <VARS> ';'
 * <VARS> ::= <VAR> , <VARS>
 * <VARS> ::= <VAR>
 * <VAR> ::= <ID>
 * <CMDS> ::= <CMD> ; <CMDS>
 * <CMDS> ::= <CMD>
 * <CMD> ::= <CMD_IF>
 * <CMD> ::= <CMD_WHILE>
 * <CMD> ::= <CMD_FOR>
 * <CMD> ::= <CMD_SWITCH>
 * <CMD> ::= <CMD_FOREACH>
 * <CMD> ::= <CMD_ASSIGNMENT>
 * <CMD> ::= <CMD_READ>
 * <CMD> ::= <CMD_WRITE>
 * <CMD> ::= <CMD_CASE>
 * <CMD_IF> ::= 'IF (' <CONDICAO> ') {' <CMDS> '}'
 * <CMD_IF> ::= 'IF (' <CONDICAO> ') {' <CMDS> '} ELSE {' <CMDS> '}'
 * <CMD_WHILE> ::= 'WHILE(' <CONDICAO> ') {' <CMDS> '}'
 * <CMD_FOR> ::= 'FOR(' <CMD_ONLY_ONE_ASSIGNMENT> 'TO' <E> ') {' <CMDS> '}'
 * <CMD_FOREACH> ::= 'FOREACH(' <VAR> <E> 'IN' <E> ') {' <CMDS> '}'
 * <CMD_CASE_OR_DEFAULT> ::= ''CASE'|'DEFAULT' '<E>':'|':'' <CMDS> 'BREAK;'
 * <CMD_SWITCH> ::= 'SWITCH('<E>') {' <CMD_CASE_OR_DEFAULT> '}'
 * <CMD_ONLY_ONE_ASSIGNMENT> ::= 'VAR' <CMD_ASSIGNMENT>
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

*/

public class Semantico {

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
  static final int T_IN = 41;
  static final int T_END_PROGRAM = 90;
  static final int T_ERROR_LEX = 98;
  static final int T_NULL = 99;

  static final int END_FILE = 226;

  static final int E_NO_ERRORS = 0;
  static final int E_ERROR_LEXICO = 1;
  static final int E_ERROR_SINTATICO = 2;
  static final int E_ERROR_SEMANTICO = 3;

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

  // Variaveis adicionadas para o semantico
  static String ultimoLexema; // criada para poder usar no codigo
  // guardar o lexema anterior
  static StringBuffer codigoJava = new StringBuffer();
  static int nivelIdentacao = 0; // para saber quantos espaços eu dou
  static String exp_0;
  static String exp_1;
  static String exp_2;
  static String exp_alvo;
  static NodoPilhaSemantica nodo;
  static NodoPilhaSemantica nodo_0;
  static NodoPilhaSemantica nodo_1;
  static NodoPilhaSemantica nodo_2;
  static PilhaSemantica pilhaSemantica = new PilhaSemantica();
  static HashMap<String, Integer> tabelaSimbolos = new HashMap<String, Integer>();

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
      JOptionPane.showMessageDialog(
        null,
        "Arquivo nao existe!",
        "FileNotFoundException!",
        JOptionPane.ERROR_MESSAGE
      );
    } catch (UnsupportedEncodingException uee) {
      JOptionPane.showMessageDialog(
        null,
        "Erro desconhecido",
        "UnsupportedEncodingException!",
        JOptionPane.ERROR_MESSAGE
      );
    } catch (IOException ioe) {
      JOptionPane.showMessageDialog(
        null,
        "Erro de io: " + ioe.getMessage(),
        "IOException!",
        JOptionPane.ERROR_MESSAGE
      );
    } catch (ErroLexicoException ele) {
      JOptionPane.showMessageDialog(
        null,
        ele.getMessage(),
        "Erro Lexico Exception!",
        JOptionPane.ERROR_MESSAGE
      );
    } catch (ErroSintaticoException ese) {
      JOptionPane.showMessageDialog(
        null,
        ese.getMessage(),
        "Erro Sintatico Exception!",
        JOptionPane.ERROR_MESSAGE
      );
    } catch (ErroSemanticoException esme) {
      JOptionPane.showMessageDialog(
        null,
        esme.getMessage(),
        "Erro Semantico Exception!",
        JOptionPane.ERROR_MESSAGE
      );
    } finally {
      System.out.println("Execucao terminada!");
    }
  }

  static void analiseSintatica()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    g();

    if (estadoCompilacao == E_ERROR_LEXICO) {
      JOptionPane.showMessageDialog(
        null,
        mensagemDeErro,
        "Erro Lexico!",
        JOptionPane.ERROR_MESSAGE
      );
    } else if (estadoCompilacao == E_ERROR_SINTATICO) {
      JOptionPane.showMessageDialog(
        null,
        mensagemDeErro,
        "Erro Sintatico!",
        JOptionPane.ERROR_MESSAGE
      );
    } else if (estadoCompilacao == E_ERROR_SEMANTICO) {
      JOptionPane.showMessageDialog(
        null,
        mensagemDeErro,
        "Erro Sintatico!",
        JOptionPane.ERROR_MESSAGE
      );
    } else {
      JOptionPane.showMessageDialog(
        null,
        "Analise Sintatica terminada sem erros",
        "Analise Sintatica terminada!",
        JOptionPane.INFORMATION_MESSAGE
      );
      acumulaRegraSintaticaReconhecida("<G>");
    }
  }

  // <G> ::= 'PROGRAM' <LISTA> <CMDS> 'END;'
  private static void g()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_PROGRAM) {
      buscaProximoToken();
      regraSemantica(0);
      lista();
      while (token != T_END) {
        cmds();
      }

      buscaProximoToken();
      regraSemantica(1);
      if (token == T_SEMICOLON) {
        acumulaRegraSintaticaReconhecida(
          "<G> ::= 'PROGRAM' <LISTA> <CMDS> 'END;'"
        );
      } else {
        registraErroSintatico(
          "Erro Sintatico. Linha: " +
          linhaAtual +
          "\nColuna: " +
          colunaAtual +
          "\nErro: <" +
          linhaFonte +
          ">\n';' esperado, mas encontrei: " +
          lexema
        );
      }
    } else {
      registraErroSintatico(
        "Erro Sintatico. Linha: " +
        linhaAtual +
        "\nColuna: " +
        colunaAtual +
        "\nErro: <" +
        linhaFonte +
        ">\n('program') esperado, mas encontrei: " +
        lexema
      );
    }
  }

  // <LISTA> ::= 'VARS' <VARS>
  private static void lista()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_VAR) {
      buscaProximoToken();
      regraSemantica(43);
      vars();
      if (token == T_SEMICOLON) {
        buscaProximoToken();
        regraSemantica(45);
        regraSemantica(16);
        regraSemantica(16);
        regraSemantica(16);
        regraSemantica(16);

        acumulaRegraSintaticaReconhecida("<LISTA> ::= 'VAR' <VARS>");
      } else {
        registraErroSintatico(
          "Erro Sintatico. Linha: " +
          linhaAtual +
          "\nColuna: " +
          colunaAtual +
          "\nErro: <" +
          linhaFonte +
          ">\n';' esperado, mas encontrei: " +
          lexema
        );
      }
    } else {
      registraErroSintatico(
        "Erro Sintatico. Linha: " +
        linhaAtual +
        "\nColuna: " +
        colunaAtual +
        "\nErro: <" +
        linhaFonte +
        ">\n('var') esperado, mas encontrei: " +
        lexema
      );
    }
  }

  private static void cmd_only_one_assignment()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_VAR) {
      buscaProximoToken();
      cmd_assignment_sem_verificacao_var();
      if (token == T_SEMICOLON) {
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida("<CMD> ::= 'VAR' <CMD_ASSIGNMENT>");
      } else {
        registraErroSintatico(
          "Erro Sintatico. Linha: " +
          linhaAtual +
          "\nColuna: " +
          colunaAtual +
          "\nErro: <" +
          linhaFonte +
          ">\n';' esperado, mas encontrei: " +
          lexema
        );
      }
    } else {
      registraErroSintatico(
        "Erro Sintatico. Linha: " +
        linhaAtual +
        "\nColuna: " +
        colunaAtual +
        "\nErro: <" +
        linhaFonte +
        ">\n('var') esperado, mas encontrei: " +
        lexema
      );
    }
  }

  // <VARS> ::= <VAR> , <VARS> | <VAR>
  private static void vars()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    int i = 0;
    var();
    while (token == T_COMMA) {
      if (i > 0) {
        regraSemantica(44);
      }

      buscaProximoToken();
      var();
      regraSemantica(4);
      regraSemantica(42);
      i++;
    }
    acumulaRegraSintaticaReconhecida("<VARS> ::= <VAR> , <VARS> | <VAR>");
  }

  // <VAR> ::= <ID>
  private static void var()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    id();
    regraSemantica(2);
    acumulaRegraSintaticaReconhecida("<VAR> ::= <ID>");
  }

  // <VARIAVEL> ::= <ID>
  private static void variable()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    id();
    regraSemantica(4);
    acumulaRegraSintaticaReconhecida("<VARIABLE> ::= <ID>");
  }

  private static void variableSemVerificacaoExists()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    id();
    regraSemantica(41);
    acumulaRegraSintaticaReconhecida("<VARIABLE> ::= <ID>");
  }

  // <ID> ::= [A-Z]+([A-Z]_[0-9])*
  private static void id()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_ID) {
      buscaProximoToken();
      acumulaRegraSintaticaReconhecida("<ID> ::= [A-Z]+([A-Z]_[0-9])*");
    } else {
      registraErroSintatico(
        "Erro Sintatico. Linha: " +
        linhaAtual +
        "\nColuna: " +
        colunaAtual +
        "\nErro: <" +
        linhaFonte +
        ">\nEsperava um identificador. Encontrei: " +
        lexema
      );
    }
  }

  // <CMDS> ::= <CMD> ; <CMDS> | <CMD>
  private static void cmds()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
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
  // <CMD> ::= <CMD_SWITCH>
  // <CMD> ::= <CMD_FOREACH>
  private static void cmd()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
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
      case T_FOREACH:
        cmd_foreach();
        break;
      case T_CLOSE_BRACKET:
        break;
      case T_BREAK:
        break;
      case T_END:
        break;
      default:
        registraErroSintatico(
          "Erro Sintatico na linha: " +
          linhaAtual +
          "\nReconhecido ao atingir a coluna: " +
          colunaAtual +
          "\nLinha do Erro: <" +
          linhaFonte +
          ">\nComando nao identificado va aprender a programar pois encontrei: " +
          lexema
        );
    }
    acumulaRegraSintaticaReconhecida(
      "<CMD> ::= <CMD_IF>|<CMD_WHILE>|<CMD_FOR>|<CMD_ASSIGNMENT>|<CMD_READ>|<CMD_WRITE>|CMD_SWITCH|<CMD_FOREACH>"
    );
  }

  // <CMD_IF> ::= 'IF(' <CONDICAO>') {' <CMDS> '}'
  // <CMD_IF> ::= 'IF(' <CONDICAO>') {' <CMDS> '} ELSE {' <CMDS> '}'
  private static void cmd_if()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_IF) {
      buscaProximoToken();
      if (token == T_OPEN_PAR) {
        buscaProximoToken();
        condicao();
        regraSemantica(17);
        if (token == T_CLOSE_PAR) {
          buscaProximoToken();
          if (token == T_OPEN_BRACKET) {
            buscaProximoToken();
            cmds();
            regraSemantica(16);
            if (token == T_CLOSE_BRACKET) {
              buscaProximoToken();
              if (token == T_ELSE) {
                buscaProximoToken();
                if (token == T_OPEN_BRACKET) {
                  buscaProximoToken();
                  regraSemantica(18);
                  cmds();
                  regraSemantica(16);
                  if (token == T_CLOSE_BRACKET) {
                    buscaProximoToken();
                    regraSemantica(40);
                  } else {
                    registraErroSintatico(
                      "Erro Sintatico na linha: " +
                      linhaAtual +
                      "\nReconhecido ao atingir a coluna: " +
                      colunaAtual +
                      "\nLinha do Erro: <" +
                      linhaFonte +
                      ">\n'}' esperado mas encontrei: " +
                      lexema
                    );
                  }
                } else {
                  registraErroSintatico(
                    "Erro Sintatico na linha: " +
                    linhaAtual +
                    "\nReconhecido ao atingir a coluna: " +
                    colunaAtual +
                    "\nLinha do Erro: <" +
                    linhaFonte +
                    ">\n'{' esperado mas encontrei: " +
                    lexema
                  );
                }
              }
              acumulaRegraSintaticaReconhecida(
                "<CMD_IF> ::= 'IF(' <CONDICAO>') {' <CMDS> '} ELSE {' <CMDS> '}' "
              );
            } else {
              registraErroSintatico(
                "Erro Sintatico na linha: " +
                linhaAtual +
                "\nReconhecido ao atingir a coluna: " +
                colunaAtual +
                "\nLinha do Erro: <" +
                linhaFonte +
                ">\n'}' esperado mas encontrei: " +
                lexema
              );
            }
          } else {
            registraErroSintatico(
              "Erro Sintatico na linha: " +
              linhaAtual +
              "\nReconhecido ao atingir a coluna: " +
              colunaAtual +
              "\nLinha do Erro: <" +
              linhaFonte +
              ">\n'{' esperado mas encontrei: " +
              lexema
            );
          }
        } else {
          registraErroSintatico(
            "Erro Sintatico na linha: " +
            linhaAtual +
            "\nReconhecido ao atingir a coluna: " +
            colunaAtual +
            "\nLinha do Erro: <" +
            linhaFonte +
            ">\n')' esperado mas encontrei: " +
            lexema
          );
        }
      } else {
        registraErroSintatico(
          "Erro Sintatico na linha: " +
          linhaAtual +
          "\nReconhecido ao atingir a coluna: " +
          colunaAtual +
          "\nLinha do Erro: <" +
          linhaFonte +
          ">\n'(' esperado mas encontrei: " +
          lexema
        );
      }
    } else {
      registraErroSintatico(
        "Erro Sintatico na linha: " +
        linhaAtual +
        "\nReconhecido ao atingir a coluna: " +
        colunaAtual +
        "\nLinha do Erro: <" +
        linhaFonte +
        ">\n'IF' esperado mas encontrei: " +
        lexema
      );
    }
  }

  // <CMD_WHILE> ::= 'WHILE(' <CONDICAO> ') {' <CMDS> '}'
  private static void cmd_while()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_WHILE) {
      buscaProximoToken();

      if (token == T_OPEN_PAR) {
        buscaProximoToken();
        condicao();
        regraSemantica(15);

        if (token == T_CLOSE_PAR) {
          buscaProximoToken();
          if (token == T_OPEN_BRACKET) {
            buscaProximoToken();
            cmds();
            regraSemantica(16);

            if (token == T_CLOSE_BRACKET) {
              buscaProximoToken();
              acumulaRegraSintaticaReconhecida(
                "<CMD_WHILE> ::= 'WHILE(' <CONDICAO> ') {' <CMDS> '}'"
              );
              regraSemantica(40);
            } else {
              registraErroSintatico(
                "Erro Sintatico na linha: " +
                linhaAtual +
                "\nReconhecido ao atingir a coluna: " +
                colunaAtual +
                "\nLinha do Erro: <" +
                linhaFonte +
                ">\n'}' esperado mas encontrei: " +
                lexema
              );
            }
          } else {
            registraErroSintatico(
              "Erro Sintatico na linha: " +
              linhaAtual +
              "\nReconhecido ao atingir a coluna: " +
              colunaAtual +
              "\nLinha do Erro: <" +
              linhaFonte +
              ">\n'{' esperado mas encontrei: " +
              lexema
            );
          }
        } else {
          registraErroSintatico(
            "Erro Sintatico na linha: " +
            linhaAtual +
            "\nReconhecido ao atingir a coluna: " +
            colunaAtual +
            "\nLinha do Erro: <" +
            linhaFonte +
            ">\n')' esperado mas encontrei: " +
            lexema
          );
        }
      } else {
        registraErroSintatico(
          "Erro Sintatico na linha: " +
          linhaAtual +
          "\nReconhecido ao atingir a coluna: " +
          colunaAtual +
          "\nLinha do Erro: <" +
          linhaFonte +
          ">\n'(' esperado mas encontrei: " +
          lexema
        );
      }
    } else {
      registraErroSintatico(
        "Erro Sintatico na linha: " +
        linhaAtual +
        "\nReconhecido ao atingir a coluna: " +
        colunaAtual +
        "\nLinha do Erro: <" +
        linhaFonte +
        ">\n'while' esperado mas encontrei: " +
        lexema
      );
    }
  }

  // <CMD_FOR> ::= 'FOR(' <CMD_ONLY_ONE_ASSIGNMENT> 'TO' <E> ') {' <CMDS> '}
  private static void cmd_for()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_FOR) {
      buscaProximoToken();
      if (token == T_OPEN_PAR) {
        buscaProximoToken();
        cmd_only_one_assignment();
        if (token == T_TO) {
          buscaProximoToken();
          e(true);
          regraSemantica(30);
          if (token == T_CLOSE_PAR) {
            buscaProximoToken();

            if (token == T_OPEN_BRACKET) {
              buscaProximoToken();
              cmds();

              if (token == T_CLOSE_BRACKET) {
                buscaProximoToken();
                regraSemantica(16);
                regraSemantica(40);
                acumulaRegraSintaticaReconhecida(
                  "<CMD_FOR> ::= 'FOR(' <CMD_ONLY_ONE_ASSIGNMENT> 'TO' <E> ') {' <CMDS> '}'"
                );
              } else {
                registraErroSintatico(
                  "Erro Sintatico na linha: " +
                  linhaAtual +
                  "\nReconhecido ao atingir a coluna: " +
                  colunaAtual +
                  "\nLinha do Erro: <" +
                  linhaFonte +
                  ">\n'}' esperado mas encontrei: " +
                  lexema
                );
              }
            } else {
              registraErroSintatico(
                "Erro Sintatico na linha: " +
                linhaAtual +
                "\nReconhecido ao atingir a coluna: " +
                colunaAtual +
                "\nLinha do Erro: <" +
                linhaFonte +
                ">\n'{' esperado mas encontrei: " +
                lexema
              );
            }
          } else {
            registraErroSintatico(
              "Erro Sintatico na linha: " +
              linhaAtual +
              "\nReconhecido ao atingir a coluna: " +
              colunaAtual +
              "\nLinha do Erro: <" +
              linhaFonte +
              ">\n')' esperado mas encontrei: " +
              lexema
            );
          }
        } else {
          registraErroSintatico(
            "Erro Sintatico na linha: " +
            linhaAtual +
            "\nReconhecido ao atingir a coluna: " +
            colunaAtual +
            "\nLinha do Erro: <" +
            linhaFonte +
            ">\n'TO' esperado mas encontrei: " +
            lexema
          );
        }
      } else {
        registraErroSintatico(
          "Erro Sintatico na linha: " +
          linhaAtual +
          "\nReconhecido ao atingir a coluna: " +
          colunaAtual +
          "\nLinha do Erro: <" +
          linhaFonte +
          ">\n'(' esperado mas encontrei: " +
          lexema
        );
      }
    } else {
      registraErroSintatico(
        "Erro Sintatico na linha: " +
        linhaAtual +
        "\nReconhecido ao atingir a coluna: " +
        colunaAtual +
        "\nLinha do Erro: <" +
        linhaFonte +
        ">\n'FOR' esperado mas encontrei: " +
        lexema
      );
    }
  }

  // <CMD_FOREACH> ::= 'FOREACH(' <VAR> <E> 'IN' <E> ') {' <CMDS> '}'
  private static void cmd_foreach()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_FOREACH) {
      buscaProximoToken();
      if (token == T_OPEN_PAR) {
        buscaProximoToken();
        if (token == T_VAR) {
          buscaProximoToken();
          e(false);

          if (token == T_IN) {
            buscaProximoToken();
            e(true);
            if (token == T_CLOSE_PAR) {
              buscaProximoToken();
              regraSemantica(39);

              if (token == T_OPEN_BRACKET) {
                buscaProximoToken();
                cmds();

                if (token == T_CLOSE_BRACKET) {
                  regraSemantica(16);
                  regraSemantica(40);
                  buscaProximoToken();
                } else {
                  registraErroSintatico(
                    "Erro Sintatico na linha: " +
                    linhaAtual +
                    "\nReconhecido ao atingir a coluna: " +
                    colunaAtual +
                    "\nLinha do Erro: <" +
                    linhaFonte +
                    ">\n'}' esperado mas encontrei: " +
                    lexema
                  );
                }
              } else {
                registraErroSintatico(
                  "Erro Sintatico na linha: " +
                  linhaAtual +
                  "\nReconhecido ao atingir a coluna: " +
                  colunaAtual +
                  "\nLinha do Erro: <" +
                  linhaFonte +
                  ">\n'{' esperado mas encontrei: " +
                  lexema
                );
              }
            } else {
              registraErroSintatico(
                "Erro Sintatico na linha: " +
                linhaAtual +
                "\nReconhecido ao atingir a coluna: " +
                colunaAtual +
                "\nLinha do Erro: <" +
                linhaFonte +
                ">\n')' esperado mas encontrei: " +
                lexema
              );
            }
          } else {
            registraErroSintatico(
              "Erro Sintatico na linha: " +
              linhaAtual +
              "\nReconhecido ao atingir a coluna: " +
              colunaAtual +
              "\nLinha do Erro: <" +
              linhaFonte +
              ">\n'IN' esperado mas encontrei: " +
              lexema
            );
          }
        } else {
          registraErroSintatico(
            "Erro Sintatico na linha: " +
            linhaAtual +
            "\nReconhecido ao atingir a coluna: " +
            colunaAtual +
            "\nLinha do Erro: <" +
            linhaFonte +
            ">\n'VAR' esperado mas encontrei: " +
            lexema
          );
        }
      } else {
        registraErroSintatico(
          "Erro Sintatico na linha: " +
          linhaAtual +
          "\nReconhecido ao atingir a coluna: " +
          colunaAtual +
          "\nLinha do Erro: <" +
          linhaFonte +
          ">\n'(' esperado mas encontrei: " +
          lexema
        );
      }
    } else {
      registraErroSintatico(
        "Erro Sintatico na linha: " +
        linhaAtual +
        "\nReconhecido ao atingir a coluna: " +
        colunaAtual +
        "\nLinha do Erro: <" +
        linhaFonte +
        ">\n'FOREACH' esperado mas encontrei: " +
        lexema
      );
    }
    acumulaRegraSintaticaReconhecida(
      "<CMD_FOREACH> ::= 'FOREACH(' <VAR> <E> 'IN' <E> ') {' <CMDS> '}'"
    );
  }

  // <CMD_CASE_OR_DEFAULT> ::= ''CASE'|'DEFAULT' '<E>':'|':'' <CMDS> 'BREAK;'
  private static void cmd_case_or_default()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_CASE || token == T_DEFAULT) {
      if (token == T_CASE) {
        buscaProximoToken();
        e(true);
        regraSemantica(36);
      } else {
        regraSemantica(37);
        buscaProximoToken();
      }
      if (token == T_COLON) {
        buscaProximoToken();
        cmds();

        if (token == T_BREAK || token == T_CASE || token == T_DEFAULT) {
          if (token == T_CASE) {
            cmd_case_or_default();
          } else if (token == T_BREAK) {
            buscaProximoToken();
            regraSemantica(38);

            if (token == T_SEMICOLON) {
              buscaProximoToken();
              regraSemantica(16);

              if (token == T_CASE || token == T_DEFAULT) {
                cmd_case_or_default();
              }
            } else {
              registraErroSintatico(
                "Erro Sintatico na linha: " +
                linhaAtual +
                "\nReconhecido ao atingir a coluna: " +
                colunaAtual +
                "\nLinha do Erro: <" +
                linhaFonte +
                ">\n';' esperado mas encontrei: " +
                lexema
              );
            }
          }
        } else {
          registraErroSintatico(
            "Erro Sintatico na linha: " +
            linhaAtual +
            "\nReconhecido ao atingir a coluna: " +
            colunaAtual +
            "\nLinha do Erro: <" +
            linhaFonte +
            ">\n'break' ou 'case' esperado mas encontrei: " +
            lexema
          );
        }
      } else {
        registraErroSintatico(
          "Erro Sintatico na linha: " +
          linhaAtual +
          "\nReconhecido ao atingir a coluna: " +
          colunaAtual +
          "\nLinha do Erro: <" +
          linhaFonte +
          ">\n':' esperado mas encontrei: " +
          lexema
        );
      }
    } else {
      registraErroSintatico(
        "Erro Sintatico na linha: " +
        linhaAtual +
        "\nReconhecido ao atingir a coluna: " +
        colunaAtual +
        "\nLinha do Erro: <" +
        linhaFonte +
        ">\n'case' ou 'default' esperados mas encontrei: " +
        lexema
      );
    }
    acumulaRegraSintaticaReconhecida(
      "<CMD_CASE_OR_DEFAULT> ::= ''CASE'|'DEFAULT' '<E>':'|':'' <CMDS> 'BREAK;'"
    );
  }

  //'SWITCH('<E>') {' <CMD_CASE_OR_DEFAULT> '}'
  private static void cmd_switch()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_SWITCH) {
      buscaProximoToken();

      if (token == T_OPEN_PAR) {
        buscaProximoToken();
        e(true);
        regraSemantica(35);

        if (token == T_CLOSE_PAR) {
          buscaProximoToken();
          if (token == T_OPEN_BRACKET) {
            buscaProximoToken();
            cmd_case_or_default();

            if (token == T_CLOSE_BRACKET) {
              buscaProximoToken();
              regraSemantica(40);
              acumulaRegraSintaticaReconhecida(
                "<CMD_SWITCH> ::= 'SWITCH('<E>') {' <CMD_CASE_OR_DEFAULT> '}'"
              );
            } else {
              registraErroSintatico(
                "Erro Sintatico na linha: " +
                linhaAtual +
                "\nReconhecido ao atingir a coluna: " +
                colunaAtual +
                "\nLinha do Erro: <" +
                linhaFonte +
                ">\n'}' esperado mas encontrei: " +
                lexema
              );
            }
          } else {
            registraErroSintatico(
              "Erro Sintatico na linha: " +
              linhaAtual +
              "\nReconhecido ao atingir a coluna: " +
              colunaAtual +
              "\nLinha do Erro: <" +
              linhaFonte +
              ">\n'{' esperado mas encontrei: " +
              lexema
            );
          }
        } else {
          registraErroSintatico(
            "Erro Sintatico na linha: " +
            linhaAtual +
            "\nReconhecido ao atingir a coluna: " +
            colunaAtual +
            "\nLinha do Erro: <" +
            linhaFonte +
            ">\n')' esperado mas encontrei: " +
            lexema
          );
        }
      } else {
        registraErroSintatico(
          "Erro Sintatico na linha: " +
          linhaAtual +
          "\nReconhecido ao atingir a coluna: " +
          colunaAtual +
          "\nLinha do Erro: <" +
          linhaFonte +
          ">\n'(' esperado mas encontrei: " +
          lexema
        );
      }
    } else {
      registraErroSintatico(
        "Erro Sintatico na linha: " +
        linhaAtual +
        "\nReconhecido ao atingir a coluna: " +
        colunaAtual +
        "\nLinha do Erro: <" +
        linhaFonte +
        ">\n'SWITCH' esperado mas encontrei: " +
        lexema
      );
    }
  }

  // <CMD_ASSIGNMENT> ::= <VAR> '=' <E>
  private static void cmd_assignment()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    variable();
    if (token == T_ASSIGNMENT) {
      buscaProximoToken();
      e(true);
      regraSemantica(3);
      acumulaRegraSintaticaReconhecida("<CMD_ASSIGNMENT> ::= <VAR> '=' <E>");
    } else {
      registraErroSintatico(
        "Erro Sintatico na linha: " +
        linhaAtual +
        "\nReconhecido ao atingir a coluna: " +
        colunaAtual +
        "\nLinha do Erro: <" +
        linhaFonte +
        ">\n'=' esperado mas encontrei: " +
        lexema
      );
    }
  }

  private static void cmd_assignment_sem_verificacao_var()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    variableSemVerificacaoExists();
    if (token == T_ASSIGNMENT) {
      buscaProximoToken();
      e(true);
      acumulaRegraSintaticaReconhecida("<CMD_ASSIGNMENT> ::= <VAR> '=' <E>");
    } else {
      registraErroSintatico(
        "Erro Sintatico na linha: " +
        linhaAtual +
        "\nReconhecido ao atingir a coluna: " +
        colunaAtual +
        "\nLinha do Erro: <" +
        linhaFonte +
        ">\n'=' esperado mas encontrei: " +
        lexema
      );
    }
  }

  // <CMD_READ> ::= 'READ' '(' <VAR> ')'
  private static void cmd_read()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_READ) {
      buscaProximoToken();
      if (token == T_OPEN_PAR) {
        buscaProximoToken();
        variable();
        if (token == T_CLOSE_PAR) {
          buscaProximoToken();
          regraSemantica(14);
          acumulaRegraSintaticaReconhecida(
            "<CMD_READ> ::= 'READ' '(' <VAR> ')'"
          );
        } else {
          registraErroSintatico(
            "Erro Sintatico na linha: " +
            linhaAtual +
            "\nReconhecido ao atingir a coluna: " +
            colunaAtual +
            "\nLinha do Erro: <" +
            linhaFonte +
            ">\n')' esperado mas encontrei: " +
            lexema
          );
        }
      } else {
        registraErroSintatico(
          "Erro Sintatico na linha: " +
          linhaAtual +
          "\nReconhecido ao atingir a coluna: " +
          colunaAtual +
          "\nLinha do Erro: <" +
          linhaFonte +
          ">\n'(' esperado mas encontrei: " +
          lexema
        );
      }
    } else {
      registraErroSintatico(
        "Erro Sintatico na linha: " +
        linhaAtual +
        "\nReconhecido ao atingir a coluna: " +
        colunaAtual +
        "\nLinha do Erro: <" +
        linhaFonte +
        ">\n'READ' esperado mas encontrei: " +
        lexema
      );
    }
  }

  // <CMD_WRITE> ::= 'WRITE' '(' <E> ')'
  private static void cmd_write()
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_WRITE) {
      buscaProximoToken();
      if (token == T_OPEN_PAR) {
        buscaProximoToken();
        e(true);
        if (token == T_CLOSE_PAR) {
          buscaProximoToken();
          regraSemantica(25);
          acumulaRegraSintaticaReconhecida(
            "<CMD_WRITE> ::= 'WRITE' '(' <E> ')'"
          );
        } else {
          registraErroSintatico(
            "Erro Sintatico na linha: " +
            linhaAtual +
            "\nReconhecido ao atingir a coluna: " +
            colunaAtual +
            "\nLinha do Erro: <" +
            linhaFonte +
            ">\n')' esperado mas encontrei: " +
            lexema
          );
        }
      } else {
        registraErroSintatico(
          "Erro Sintatico na linha: " +
          linhaAtual +
          "\nReconhecido ao atingir a coluna: " +
          colunaAtual +
          "\nLinha do Erro: <" +
          linhaFonte +
          ">\n'(' esperado mas encontrei: " +
          lexema
        );
      }
    } else {
      registraErroSintatico(
        "Erro Sintatico na linha: " +
        linhaAtual +
        "\nReconhecido ao atingir a coluna: " +
        colunaAtual +
        "\nLinha do Erro: <" +
        linhaFonte +
        ">\n'WRITE' esperado mas encontrei: " +
        lexema
      );
    }
  }

  // <CONDICAO> ::= <E> '>' <E>
  // <CONDICAO> ::= <E> '>=' <E>
  // <CONDICAO> ::= <E> '!=' <E>
  // <CONDICAO> ::= <E> '<=' <E>
  // <CONDICAO> ::= <E> '<' <E>
  // <CONDICAO> ::= <E> '==' <E>
  private static void condicao()
    throws ErroLexicoException, IOException, ErroSintaticoException, ErroSemanticoException {
    e(true);
    switch (token) {
      case T_GREATEST:
        buscaProximoToken();
        e(true);
        regraSemantica(19);
        break;
      case T_LOWEST:
        buscaProximoToken();
        e(true);
        regraSemantica(20);
        break;
      case T_GREATER_OR_EQUAL:
        buscaProximoToken();
        e(true);
        regraSemantica(21);
        break;
      case T_LESS_OR_EQUAL:
        buscaProximoToken();
        e(true);
        regraSemantica(22);
        break;
      case T_EQUAL:
        buscaProximoToken();
        e(true);
        regraSemantica(23);
        break;
      case T_DIFFERENT:
        buscaProximoToken();
        e(true);
        regraSemantica(24);
        break;
      default:
        registraErroSintatico(
          "Erro Sintatico. Linha: " +
          linhaAtual +
          "\nColuna: " +
          colunaAtual +
          "\nErro: <" +
          linhaFonte +
          ">\nEsperava um operador logico. Encontrei: " +
          lexema
        );
    }
    acumulaRegraSintaticaReconhecida(
      "<CONDICAO> ::= <E> ('>'|'>='|'!='|'<='|'<'|'==') <E> "
    );
  }

  // <E> ::= <E> + <T>
  // <E> ::= <E> - <T>
  // <E> ::= <T>
  private static void e(boolean comValidacaoSeTokenExiste)
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    t(comValidacaoSeTokenExiste);
    while ((token == T_PLUS) || (token == T_MINUS)) {
      switch (token) {
        case T_PLUS:
          {
            buscaProximoToken();
            t(comValidacaoSeTokenExiste);
            regraSemantica(5);
          }
          break;
        case T_MINUS:
          {
            buscaProximoToken();
            t(comValidacaoSeTokenExiste);
            regraSemantica(6);
          }
          break;
      }
    }
    acumulaRegraSintaticaReconhecida("<E> ::= <E> + <T>|<E> - <T>|<T> ");
  }

  // <T> ::= <T> * <F>
  // <T> ::= <T> / <F>
  // <T> ::= <T> % <F>
  // <T> ::= <F>
  private static void t(boolean comValidacaoSeTokenExiste)
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    f(comValidacaoSeTokenExiste);
    while (
      (token == T_TIMES) || (token == T_DIVISION) || (token == T_REMAINDER)
    ) {
      switch (token) {
        case T_TIMES:
          {
            buscaProximoToken();
            f(comValidacaoSeTokenExiste);
            regraSemantica(7);
          }
          break;
        case T_DIVISION:
          {
            buscaProximoToken();
            f(comValidacaoSeTokenExiste);
            regraSemantica(8);
          }
          break;
        case T_REMAINDER:
          {
            buscaProximoToken();
            f(comValidacaoSeTokenExiste);
            regraSemantica(9);
          }
          break;
      }
    }
    acumulaRegraSintaticaReconhecida(
      "<T> ::= <T> * <F>|<T> / <F>|<T> % <F>|<F>"
    );
  }

  // <F> ::= -<F>
  // <F> ::= <X> ** <F>
  // <F> ::= <X>
  private static void f(boolean comValidacaoSeTokenExiste)
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    if (token == T_MINUS) {
      buscaProximoToken();
      f(comValidacaoSeTokenExiste);
    } else {
      x(comValidacaoSeTokenExiste);
      while (token == T_POWER) {
        buscaProximoToken();
        x(comValidacaoSeTokenExiste);
        regraSemantica(10);
      }
    }
    acumulaRegraSintaticaReconhecida("<F> ::= -<F>|<X> ** <F>|<X> ");
  }

  // <X> ::= '(' <E> ')'
  // <X> ::= [0-9]+('.'[0-9]+)
  // <X> ::= <VAR>
  private static void x(boolean comValidacaoSeTokenExiste)
    throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
    switch (token) {
      case T_ID:
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida("<X> ::= <VAR>");

        if (comValidacaoSeTokenExiste) {
          regraSemantica(11);
        } else {
          regraSemantica(41);
          regraSemantica(2);
        }
        break;
      case T_NUMBER:
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida("<X> ::= [0-9]+('.'[0-9]+)");
        regraSemantica(12);
        break;
      case T_OPEN_PAR:
        {
          buscaProximoToken();
          e(comValidacaoSeTokenExiste);
          if (token == T_CLOSE_PAR) {
            buscaProximoToken();
            acumulaRegraSintaticaReconhecida("<X> ::= '(' <E> ')'");
          } else {
            registraErroSintatico(
              "Erro Sintatico na linha: " +
              linhaAtual +
              "\nReconhecido ao atingir a coluna: " +
              colunaAtual +
              "\nLinha do Erro: <" +
              linhaFonte +
              ">\n')' esperado mas encontrei: " +
              lexema
            );
          }
          regraSemantica(13);
        }
        break;
      default:
        registraErroSintatico(
          "Erro Sintatico na linha: " +
          linhaAtual +
          "\nReconhecido ao atingir a coluna: " +
          colunaAtual +
          "\nLinha do Erro: <" +
          linhaFonte +
          ">\nFator invalido: encontrei: " +
          lexema
        );
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
    if ((lookAhead >= 'a') && (lookAhead <= 'z')) {
      lookAhead = (char) (lookAhead - 'a' + 'A');
    }

    ponteiro++;
    colunaAtual = ponteiro + 1;
  }

  static void buscaProximoToken() throws IOException, ErroLexicoException {
    int i, j;

    if (lexema != null) {
      ultimoLexema = new String(lexema);
    }

    StringBuffer sbLexema = new StringBuffer("");

    // Salto espaçoes enters e tabs até o inicio do proximo token
    while (
      (lookAhead == 9) ||
      (lookAhead == '\n') ||
      (lookAhead == 8) ||
      (lookAhead == 11) ||
      (lookAhead == 12) ||
      (lookAhead == '\r') ||
      (lookAhead == 32)
    ) {
      movelookAhead();
    }

    /*--------------------------------------------------------------*
     * Caso o primeiro caracter seja alfabetico, procuro capturar a *
     * sequencia de caracteres que se segue a ele e classifica-la   *
     *--------------------------------------------------------------*/
    if ((lookAhead >= 'A') && (lookAhead <= 'Z')) {
      sbLexema.append(lookAhead);
      movelookAhead();

      while (
        ((lookAhead >= 'A') && (lookAhead <= 'Z')) ||
        ((lookAhead >= '0') && (lookAhead <= '9')) ||
        (lookAhead == '_')
      ) {
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
      } else if (lexema.equals("FOREACH")) {
        token = T_FOREACH;
      } else if (lexema.equals("IN")) {
        token = T_IN;
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
      mensagemDeErro =
        "Erro Léxico na linha: " +
        linhaAtual +
        "\nReconhecido ao atingir a coluna: " +
        colunaAtual +
        "\nLinha do Erro: <" +
        linhaFonte +
        ">\nToken desconhecido: " +
        lexema;
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
      case T_FOREACH:
        tokenLexema.append("T_FOREACH");
        break;
      case T_IN:
        tokenLexema.append("T_IN");
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
      JOptionPane.showMessageDialog(
        null,
        "Nome de Arquivo Invalido",
        "Nome de Arquivo Invalido",
        JOptionPane.ERROR_MESSAGE
      );
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
    JFileChooser fileChooser = new JFileChooser("C:\\temp");

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
      JOptionPane.showMessageDialog(
        null,
        "Nome de Arquivo Invalido",
        "Nome de Arquivo Invalido",
        JOptionPane.ERROR_MESSAGE
      );
      return false;
    } else {
      FileWriter fw;
      try {
        fw = new FileWriter(arqDestino);
        BufferedWriter bfw = new BufferedWriter(fw);
        bfw.write(codigoJava.toString());
        bfw.close();
        JOptionPane.showMessageDialog(
          null,
          "Arquivo Salvo: " + arqDestino,
          "Salvando Arquivo",
          JOptionPane.INFORMATION_MESSAGE
        );
      } catch (IOException e) {
        JOptionPane.showMessageDialog(
          null,
          e.getMessage(),
          "Erro de Entrada/Saida",
          JOptionPane.ERROR_MESSAGE
        );
      }
      return true;
    }
  }

  public static void exibeTokens() {
    JTextArea texto = new JTextArea();
    texto.append(tokensIdentificados.toString());
    JOptionPane.showMessageDialog(
      null,
      texto,
      "Tokens Identificados (token/lexema)",
      JOptionPane.INFORMATION_MESSAGE
    );
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
    JOptionPane.showMessageDialog(
      null,
      texto,
      "Analise Lexica",
      JOptionPane.INFORMATION_MESSAGE
    );

    texto.setText(regrasReconhecidas.toString());
    texto.append("\n\nStatus da Compilacao:\n\n");
    texto.append(mensagemDeErro);

    JOptionPane.showMessageDialog(
      null,
      texto,
      "Resumo da Compilacao",
      JOptionPane.INFORMATION_MESSAGE
    );
  }

  static void registraErroSintatico(String msg) throws ErroSintaticoException {
    if (estadoCompilacao == E_NO_ERRORS) {
      estadoCompilacao = E_ERROR_SINTATICO;
      mensagemDeErro = msg;
    }
    throw new ErroSintaticoException(msg);
  }

  static void registraErroSemantico(String msg) {
    if (estadoCompilacao == E_NO_ERRORS) {
      estadoCompilacao = E_ERROR_SEMANTICO;
      mensagemDeErro = msg;
    }
  }

  static void regraSemantica(int numeroRegra) throws ErroSemanticoException {
    System.out.println("Regra Semantica " + numeroRegra);
    switch (numeroRegra) {
      case 0:
        codigoJava.append("import java.lang.Math;\n\n");
        codigoJava.append("import java.util.Scanner;\n\n");
        codigoJava.append("public class Semantico {");
        codigoJava.append("\n");
        codigoJava.append(tabulacao(nivelIdentacao));
        nivelIdentacao++;
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("public static void main( String s[] ) { \n");
        codigoJava.append(tabulacao(nivelIdentacao));
        nivelIdentacao++;
        codigoJava.append("// Feevale compiler C# + misto to Java + misto\n");
        break;
      case 1:
        codigoJava.append("\n");
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("// pass\n\n");
        nivelIdentacao--;
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("}\n");
        nivelIdentacao--;
        codigoJava.append("}\n");
        break;
      case 2:
        insereNaTabelaSimbolos(ultimoLexema);
        break;
      case 3:
        nodo_2 = pilhaSemantica.pop();

        if (pilhaSemantica.tamanho() > 0) {
          nodo_1 = pilhaSemantica.pop();
        }

        System.out.println("Codigo 1 " + nodo_1.getCodigo());

        if (nodo_1 != null) {
          System.out.println("Codigo 2 " + nodo_2.getCodigo());
        }

        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append(
          nodo_1.getCodigoMinusculo() +
          " = " +
          nodo_2.getCodigoMinusculo() +
          ";\n"
        );
        break;
      case 4:
        if (VeSeExisteNaTabelaSimbolos(ultimoLexema)) {
          pilhaSemantica.push(ultimoLexema, 4);
        }
        break;
      case 5:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          nodo_1.getCodigoMinusculo() + "+" + nodo_2.getCodigoMinusculo(),
          5
        );
        break;
      case 6:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          nodo_1.getCodigoMinusculo() + "-" + nodo_2.getCodigoMinusculo(),
          6
        );
        break;
      case 7:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          nodo_1.getCodigoMinusculo() + "*" + nodo_2.getCodigoMinusculo(),
          7
        );
        break;
      case 8:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          nodo_1.getCodigoMinusculo() + "/" + nodo_2.getCodigoMinusculo(),
          8
        );
        break;
      case 9:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          nodo_1.getCodigoMinusculo() + "%" + nodo_2.getCodigoMinusculo(),
          9
        );
        break;
      case 10:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          "Math.pow(" +
          nodo_1.getCodigoMinusculo() +
          ", " +
          nodo_2.getCodigoMinusculo() +
          ")",
          10
        );
        break;
      case 11:
        if (VeSeExisteNaTabelaSimbolos(ultimoLexema)) {
          pilhaSemantica.push(ultimoLexema, 11);
        }
        break;
      case 12:
        pilhaSemantica.push(ultimoLexema, 12);
        break;
      case 13:
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push("(" + nodo_1.getCodigoMinusculo() + ")", 13);
        break;
      case 14:
        nodo_1 = pilhaSemantica.pop();
        codigoJava.append("\n");
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("Scanner scanner = new Scanner(System.in);\n");
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append(
          "System.out.println(\"Informe a variável " +
          nodo_1.getCodigoMinusculo() +
          ": \");\n"
        );
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append(
          nodo_1.getCodigoMinusculo() + " = scanner.nextLine();"
        );
        break;
      case 15:
        nodo_1 = pilhaSemantica.pop();
        codigoJava.append("\n");
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("while( " + nodo_1.getCodigoMinusculo() + ") { \n");
        nivelIdentacao++;
        break;
      case 16:
        nivelIdentacao--;
        break;
      case 17:
        nodo_1 = pilhaSemantica.pop();
        codigoJava.append("\n");
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("if (" + nodo_1.getCodigoMinusculo() + ") { \n");
        nivelIdentacao++;
        break;
      case 18:
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("} else { \n");
        nivelIdentacao++;
        break;
      case 19:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          nodo_1.getCodigoMinusculo() + " > " + nodo_2.getCodigoMinusculo(),
          19
        );
        break;
      case 20:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          nodo_1.getCodigoMinusculo() + " < " + nodo_2.getCodigoMinusculo(),
          20
        );
        break;
      case 21:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          nodo_1.getCodigoMinusculo() + " >= " + nodo_2.getCodigoMinusculo(),
          21
        );
        break;
      case 22:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          nodo_1.getCodigoMinusculo() + " <= " + nodo_2.getCodigoMinusculo(),
          22
        );
        break;
      case 23:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          nodo_1.getCodigoMinusculo() + " == " + nodo_2.getCodigoMinusculo(),
          23
        );
        break;
      case 24:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          nodo_1.getCodigoMinusculo() + " != " + nodo_2.getCodigoMinusculo(),
          24
        );
        break;
      case 25:
        nodo_1 = pilhaSemantica.pop();
        codigoJava.append("\n\n");
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append(
          "System.out.println(" + nodo_1.getCodigoMinusculo() + ");\n"
        );
        break;
      case 26:
        nodo_1 = pilhaSemantica.pop();
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append(
          "for _i" +
          (nivelIdentacao + "") +
          " in range ( " +
          nodo_1.getCodigoMinusculo() +
          " ):\n"
        );
        nivelIdentacao++;
        break;
      case 27:
        nivelIdentacao--;
        break;
      case 28:
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("while ( true ) { \n");
        nivelIdentacao++;
        break;
      case 29:
        nodo_1 = pilhaSemantica.pop();
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("if ( " + nodo_1.getCodigoMinusculo() + " ) { \n");
        nivelIdentacao++;
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("break;\n");
        nivelIdentacao--;
        nivelIdentacao--;
        break;
      case 30:
        nodo_2 = pilhaSemantica.pop(); // exp2
        nodo_1 = pilhaSemantica.pop(); // exp1
        nodo_0 = pilhaSemantica.pop(); // variavel
        codigoJava.append("\n");
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append(
          "for(int " +
          nodo_0.getCodigoMinusculo() +
          "=" +
          nodo_1.getCodigoMinusculo() +
          "; " +
          nodo_0.getCodigoMinusculo() +
          "<=" +
          nodo_2.getCodigoMinusculo() +
          "; " +
          nodo_0.getCodigoMinusculo() +
          "++) { \n"
        );
        nivelIdentacao++;
        break;
      case 31:
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("if (false) { \n");
        nivelIdentacao++;
        codigoJava.append(tabulacao(nivelIdentacao));
        nivelIdentacao--;
        break;
      case 32:
        nodo_1 = pilhaSemantica.pop();
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("else if (" + nodo_1.getCodigoMinusculo() + ") {\n");
        nivelIdentacao++;
        break;
      case 33:
        nodo_1 = pilhaSemantica.pop(); // exp1
        nodo_0 = pilhaSemantica.pop(); // variavel
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append(
          nodo_0.getCodigoMinusculo() +
          " = fatorial( " +
          nodo_1.getCodigoMinusculo() +
          " )\n"
        );
        break;
      case 34:
        nodo_2 = pilhaSemantica.pop();
        nodo_1 = pilhaSemantica.pop();
        pilhaSemantica.push(
          "2 * ( " +
          nodo_1.getCodigoMinusculo() +
          "+" +
          nodo_2.getCodigoMinusculo() +
          ") ",
          34
        );
        break;
      case 35:
        codigoJava.append("\n");
        nodo_1 = pilhaSemantica.pop();
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append(
          "switch ( " + nodo_1.getCodigoMinusculo() + " ) { \n"
        );
        break;
      case 36:
        nodo_1 = pilhaSemantica.pop();
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append(tabulacaoTab(nivelIdentacao));
        codigoJava.append("case " + nodo_1.getCodigoMinusculo() + ": \n");
        codigoJava.append(tabulacao(nivelIdentacao));
        break;
      case 37:
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append(tabulacaoTab(nivelIdentacao));
        codigoJava.append("default:\n");
        codigoJava.append(tabulacao(nivelIdentacao));
        break;
      case 38:
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append(tabulacao(nivelIdentacao));
        nivelIdentacao++;
        codigoJava.append("break;\n");
        break;
      case 39:
        nodo_1 = pilhaSemantica.pop(); // exp1
        nodo_0 = pilhaSemantica.pop(); // variavel
        codigoJava.append("\n");
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append(
          "for(var " +
          nodo_0.getCodigoMinusculo() +
          " : " +
          nodo_1.getCodigoMinusculo() +
          ") { \n"
        );
        nivelIdentacao++;
        break;
      case 40:
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("}\n");
        break;
      case 41:
        if (!tabelaSimbolos.containsKey(ultimoLexema)) {
          pilhaSemantica.push(ultimoLexema, 41);
          break;
        } else {
          throw new ErroSemanticoException(
            "Variavel " + ultimoLexema + " ja declarada! linha: " + linhaAtual
          );
        }
      case 42:
        nodo_0 = pilhaSemantica.pop(); // variavel
        codigoJava.append(nodo_0.getCodigoMinusculo());
        pilhaSemantica.push(ultimoLexema, 42);
        nivelIdentacao++;
        break;
      case 43:
        codigoJava.append("\n");
        codigoJava.append(tabulacao(nivelIdentacao));
        codigoJava.append("var = ");
        break;
      case 44:
        codigoJava.append(", ");
        break;
      case 45:
        codigoJava.append(";\n");
        break;
    }
  }

  private static int buscaTipoNaTabelaSimbolos(String ultimoLexema)
    throws ErroSemanticoException {
    return tabelaSimbolos.get(ultimoLexema);
  }

  private static boolean VeSeExisteNaTabelaSimbolos(String ultimoLexema)
    throws ErroSemanticoException {
    if (!tabelaSimbolos.containsKey(ultimoLexema)) {
      throw new ErroSemanticoException(
        "Variavel " + ultimoLexema + " nao esta declarada! linha: " + linhaAtual
      );
    } else {
      return true;
    }
  }

  private static void insereNaTabelaSimbolos(String ultimoLexema)
    throws ErroSemanticoException {
    if (tabelaSimbolos.containsKey(ultimoLexema)) {
      throw new ErroSemanticoException(
        "Variavel " + ultimoLexema + " ja declarada! linha: " + linhaAtual
      );
    } else {
      tabelaSimbolos.put(ultimoLexema, 0);
    }
  }

  static String tabulacao(int qtd) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < qtd; i++) {
      sb.append("    ");
    }
    return sb.toString();
  }

  static String tabulacaoTab(int qtd) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < qtd; i++) {
      sb.append("  ");
    }
    return sb.toString();
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
      }
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
      }
    }
    return null;
  }
}
