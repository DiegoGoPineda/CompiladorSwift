package Lexico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalizadorLexico {
    private final String entrada;
    private final List<Tokens> listaTokens;
    private int posicion;
    private int linea;

    // Diccionario para palabras reservadas
    private static final Map<String, TipoToken> PALABRAS_RESERVADAS = new HashMap<>();
    static {
        PALABRAS_RESERVADAS.put("let", TipoToken.LET);
        PALABRAS_RESERVADAS.put("var", TipoToken.VAR);
        PALABRAS_RESERVADAS.put("if", TipoToken.IF);
        PALABRAS_RESERVADAS.put("else", TipoToken.ELSE);
        PALABRAS_RESERVADAS.put("while", TipoToken.WHILE);
        PALABRAS_RESERVADAS.put("print", TipoToken.PRINT);
        PALABRAS_RESERVADAS.put("Int", TipoToken.TIPE_INT);
        PALABRAS_RESERVADAS.put("String", TipoToken.TIPE_STRING);
        PALABRAS_RESERVADAS.put("Double", TipoToken.TIPE_DOUBLE);
        PALABRAS_RESERVADAS.put("Bool", TipoToken.TIPE_BOOL);
        PALABRAS_RESERVADAS.put("true", TipoToken.BOOLEAN_LITERAL);
        PALABRAS_RESERVADAS.put("false", TipoToken.BOOLEAN_LITERAL);
    }

    public AnalizadorLexico(String entrada) {
        this.entrada = entrada;
        this.listaTokens = new ArrayList<>();
        this.posicion = 0;
        this.linea = 1;
    }

    public List<Tokens> analizar() {
        int n = entrada.length();

        while (posicion < n) {
            int estado = 0; // Regresa al estado inicial para cada token
            StringBuilder lexema = new StringBuilder();

            while (posicion < n) {
                char c = entrada.charAt(posicion);

                switch (estado) {
                    case 0: // ESTADO INICIAL
                        if (c == ' ' || c == '\t' || c == '\r') {
                            posicion++; // Ignorar espacios en blanco
                        } else if (c == '\n') {
                            linea++;
                            posicion++;
                        } else if (esDigito(c)) { 
                            // 1. PRIORIDAD: Evaluar números primero
                            lexema.append(c);
                            posicion++;
                            estado = 2; // Transición a número entero
                        } else if (esInicioDeIdentificador(c)) { 
                            // 2. Solo letras/subguión/caracteres extendidos para iniciar identificador
                            lexema.append(c);
                            posicion++;
                            estado = 1; // Transición a identificador / palabra reservada
                        } else if (c == '"') {
                            posicion++; // No incluimos la comilla inicial en el valor
                            estado = 4; // Transición a cadena
                        } else if (c == '/') {
                            // Revisar si es comentario de una línea //
                            if (posicion + 1 < n && entrada.charAt(posicion + 1) == '/') {
                                while (posicion < n && entrada.charAt(posicion) != '\n') {
                                    posicion++;
                                }
                            } else {
                                listaTokens.add(new Tokens(TipoToken.DIVIDE, "/", linea));
                                posicion++;
                            }
                        } else if (c == '=') {
                            posicion++;
                            if (posicion < n && entrada.charAt(posicion) == '=') {
                                listaTokens.add(new Tokens(TipoToken.EQUALS, "==", linea));
                                posicion++;
                            } else {
                                listaTokens.add(new Tokens(TipoToken.ASSIGN, "=", linea));
                            }
                        } else if (c == '!') {
                            posicion++;
                            if (posicion < n && entrada.charAt(posicion) == '=') {
                                listaTokens.add(new Tokens(TipoToken.NOT_EQUALS, "!=", linea));
                                posicion++;
                            } else {
                                listaTokens.add(new Tokens(TipoToken.UNKNOWN, "!", linea));
                            }
                        } else if (c == '+') {
                            listaTokens.add(new Tokens(TipoToken.PLUS, "+", linea));
                            posicion++;
                        } else if (c == '-') {
                            listaTokens.add(new Tokens(TipoToken.MINUS, "-", linea));
                            posicion++;
                        } else if (c == '*') {
                            listaTokens.add(new Tokens(TipoToken.MULTIPLY, "*", linea));
                            posicion++;
                        } else if (c == '(') {
                            listaTokens.add(new Tokens(TipoToken.LPAREN, "(", linea));
                            posicion++;
                        } else if (c == ')') {
                            listaTokens.add(new Tokens(TipoToken.RPAREN, ")", linea));
                            posicion++;
                        } else if (c == '{') {
                            listaTokens.add(new Tokens(TipoToken.LBRACE, "{", linea));
                            posicion++;
                        } else if (c == '}') {
                            listaTokens.add(new Tokens(TipoToken.RBRACE, "}", linea));
                            posicion++;
                        } else if (c == ':') {
                            listaTokens.add(new Tokens(TipoToken.COLON, ":", linea));
                            posicion++;
                        } else if (c == ',') {
                            listaTokens.add(new Tokens(TipoToken.COMMA, ",", linea));
                            posicion++;
                        } else if (c == '>') {
                            posicion++;
                            if (posicion < n && entrada.charAt(posicion) == '=') {
                                listaTokens.add(new Tokens(TipoToken.GREATER_EQUAL, ">=", linea));
                                posicion++;
                            } else {
                                listaTokens.add(new Tokens(TipoToken.GREATER, ">", linea));
                            }
                        } else if (c == '<') {
                            posicion++;
                            if (posicion < n && entrada.charAt(posicion) == '=') {
                                listaTokens.add(new Tokens(TipoToken.LESS_EQUAL, "<=", linea));
                                posicion++;
                            } else {
                                listaTokens.add(new Tokens(TipoToken.LESS, "<", linea));
                            }
                        } else {
                            listaTokens.add(new Tokens(TipoToken.UNKNOWN, String.valueOf(c), linea));
                            posicion++;
                        }
                        break;

                    case 1: // ESTADO DE IDENTIFICADORES Y PALABRAS RESERVADAS (CON VALIDACIÓN)
                        if (esContinuacionDeIdentificador(c)) {
                            lexema.append(c);
                            posicion++;
                        } else {
                            String texto = lexema.toString();

                            // Si contiene un carácter fuera del alfabeto válido
                            if (contieneCaracterInvalido(texto)) {
                                listaTokens.add(new Tokens(TipoToken.UNKNOWN, texto, linea));
                            } else {
                                TipoToken tipo = PALABRAS_RESERVADAS.get(texto);

                                if (tipo == null) {
                                    listaTokens.add(new Tokens(TipoToken.IDENTIFIER, texto, linea));
                                } else if (tipo == TipoToken.BOOLEAN_LITERAL) {
                                    listaTokens.add(new Tokens(tipo, texto, Boolean.parseBoolean(texto), linea));
                                } else {
                                    listaTokens.add(new Tokens(tipo, texto, linea));
                                }
                            }
                            estado = 0; // Termina este token
                            break;
                        }
                        break;

                    case 2: // ESTADO: ENTEROS
                        if (esDigito(c)) {
                            lexema.append(c);
                            posicion++;
                        } else if (c == '.' && posicion + 1 < n && esDigito(entrada.charAt(posicion + 1))) {
                            lexema.append(c);
                            posicion++;
                            estado = 3; // Pasa a estado de número decimal
                        } else {
                            // Aceptación de entero
                            String texto = lexema.toString();
                            listaTokens.add(new Tokens(TipoToken.NUMBER_INT, texto, Integer.parseInt(texto), linea));
                            estado = 0;
                            break;
                        }
                        break;

                    case 3: // ESTADO DE ACEPTACIÓN: DECIMALES
                        if (esDigito(c)) {
                            lexema.append(c);
                            posicion++;
                        } else {
                            // Aceptación de double
                            String texto = lexema.toString();
                            listaTokens.add(new Tokens(TipoToken.NUMBER_DOUBLE, texto, Double.parseDouble(texto), linea));
                            estado = 0;
                            break;
                        }
                        break;

                    case 4: // ESTADO: CADENAS LITERALES
                        if (c == '"') {
                            posicion++; // Consumir comilla de cierre
                            String texto = lexema.toString();
                            listaTokens.add(new Tokens(TipoToken.STRING_LITERAL, texto, texto, linea));
                            estado = 0;
                            break;
                        } else if (c == '\n') {
                            linea++;
                            lexema.append(c);
                            posicion++;
                        } else {
                            lexema.append(c);
                            posicion++;
                        }
                        break;
                }

                // Si volvió a estado 0 habiendo consumido un token compuesto, salimos al ciclo principal
                if (estado == 0 && lexema.length() > 0) {
                    break;
                }
            }

            // Manejo de fin de cadena sin cerrar comillas
            if (estado == 4) {
                listaTokens.add(new Tokens(TipoToken.UNKNOWN, lexema.toString(), linea));
            }
        }

        listaTokens.add(new Tokens(TipoToken.EOF, "", linea));
        return listaTokens;
    }

    // Un identificador NO puede empezar con números
    private boolean esInicioDeIdentificador(char c) {
        return esLetraValida(c) || Character.isLetter(c);
    }

    // Durante la lectura del identificador se aceptan letras, números y caracteres no válidos pegados
    private boolean esContinuacionDeIdentificador(char c) {
        return esLetraValida(c) || esDigito(c) || Character.isLetter(c);
    }

    // Verifica si la palabra acumulada tiene algún carácter fuera del alfabeto válido (ASCII de Swift)
    private boolean contieneCaracterInvalido(String texto) {
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (!esLetraValida(c) && !esDigito(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean esLetraValida(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }
}