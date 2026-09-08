package Lexico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String codigoFuente;
    private final List<Tokens> tokens = new ArrayList<>();
    
    private int inicio = 0;
    private int actual = 0;
    private int linea = 1;

    // Tabla de palabras reservadas de Swift
    private static final Map<String, TipoToken> palabrasReservadas;
    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("let", TipoToken.LET);
        palabrasReservadas.put("var", TipoToken.VAR);
        palabrasReservadas.put("if", TipoToken.IF);
        palabrasReservadas.put("else", TipoToken.ELSE);
        palabrasReservadas.put("while", TipoToken.WHILE);
        palabrasReservadas.put("print", TipoToken.PRINT);
        
        // Tipos de datos
        palabrasReservadas.put("Int", TipoToken.TIPE_INT);
        palabrasReservadas.put("String", TipoToken.TIPE_STRING);
        palabrasReservadas.put("Double", TipoToken.TIPE_DOUBLE);
        palabrasReservadas.put("Bool", TipoToken.TIPE_BOOL);
        
        // Literales booleanos
        palabrasReservadas.put("true", TipoToken.BOOLEAN_LITERAL);
        palabrasReservadas.put("false", TipoToken.BOOLEAN_LITERAL);
    }

    public Scanner(String codigoFuente) {
        this.codigoFuente = codigoFuente;
    }

    public List<Tokens> escanearTokens() {
        while (!esFin()) {
            inicio = actual;
            escanearToken();
        }
        tokens.add(new Tokens(TipoToken.EOF, "", null, linea));
        return tokens;
    }

    private void escanearToken() {
        char c = avanzar();
        switch (c) {
            // Delimitadores
            case '(': agregarToken(TipoToken.LPAREN); break;
            case ')': agregarToken(TipoToken.RPAREN); break;
            case '{': agregarToken(TipoToken.LBRACE); break;
            case '}': agregarToken(TipoToken.RBRACE); break;
            case ',': agregarToken(TipoToken.COMMA); break;
            case ':': agregarToken(TipoToken.COLON); break;

            // Operadores aritméticos
            case '+': agregarToken(TipoToken.PLUS); break;
            case '-': agregarToken(TipoToken.MINUS); break;
            case '*': agregarToken(TipoToken.MULTIPLY); break;

            // Operadores relacionales y asignación
            case '=':
                agregarToken(coincide('=') ? TipoToken.EQUALS : TipoToken.ASSIGN);
                break;
            case '!':
                if (coincide('=')) {
                    agregarToken(TipoToken.NOT_EQUALS);
                } else {
                    agregarToken(TipoToken.UNKNOWN);
                }
                break;
            case '>':
                agregarToken(TipoToken.GREATER);
                break;
            case '<':
                agregarToken(TipoToken.LESS);
                break;

            // División o comentarios en Swift (// ...)
            case '/':
                if (coincide('/')) {
                    while (mirar() != '\n' && !esFin()) avanzar();
                } else {
                    agregarToken(TipoToken.DIVIDE);
                }
                break;

            // Espacios en blanco y saltos de línea
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                linea++;
                break;

            // Cadenas literales: "hola"
            case '"':
                procesarCadena();
                break;

            default:
                if (esDigito(c)) {
                    procesarNumero();
                } else if (esLetra(c)) {
                    procesarIdentificador();
                } else {
                    agregarToken(TipoToken.UNKNOWN);
                }
                break;
        }
    }

    private void procesarCadena() {
        while (mirar() != '"' && !esFin()) {
            if (mirar() == '\n') linea++;
            avanzar();
        }

        if (esFin()) {
            System.err.println("Error lexico en línea " + linea + ": cadena no cerrada.");
            return;
        }

        avanzar(); // Cierra las comillas
        String valorCadena = codigoFuente.substring(inicio + 1, actual - 1);
        agregarToken(TipoToken.STRING_LITERAL, valorCadena);
    }

    private void procesarNumero() {
        while (esDigito(mirar())) avanzar();

        // Si tiene punto y le sigue un dígito, es un Double
        if (mirar() == '.' && esDigito(mirarSiguiente())) {
            avanzar(); // Consume el '.'
            while (esDigito(mirar())) avanzar();
            double valorDouble = Double.parseDouble(codigoFuente.substring(inicio, actual));
            agregarToken(TipoToken.NUMBER_DOUBLE, valorDouble);
            return;
        }

        int valorEntero = Integer.parseInt(codigoFuente.substring(inicio, actual));
        agregarToken(TipoToken.NUMBER_INT, valorEntero);
    }

    private void procesarIdentificador() {
        while (esAlfanumerico(mirar())) avanzar();

        String texto = codigoFuente.substring(inicio, actual);
        TipoToken tipo = palabrasReservadas.get(texto);

        if (tipo == null) {
            tipo = TipoToken.IDENTIFIER;
            agregarToken(tipo, null);
        } else if (tipo == TipoToken.BOOLEAN_LITERAL) {
            agregarToken(tipo, Boolean.parseBoolean(texto));
        } else {
            agregarToken(tipo, null);
        }
    }

    // Auxiliares
    private boolean esFin() { return actual >= codigoFuente.length(); }
    private char avanzar() { return codigoFuente.charAt(actual++); }
    private char mirar() { return esFin() ? '\0' : codigoFuente.charAt(actual); }
    private char mirarSiguiente() { return (actual + 1 >= codigoFuente.length()) ? '\0' : codigoFuente.charAt(actual + 1); }

    private boolean coincide(char esperado) {
        if (esFin() || codigoFuente.charAt(actual) != esperado) return false;
        actual++;
        return true;
    }

    private boolean esDigito(char c) { return c >= '0' && c <= '9'; }
    private boolean esLetra(char c) { return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_'; }
    private boolean esAlfanumerico(char c) { return esLetra(c) || esDigito(c); }

    private void agregarToken(TipoToken tipo) { agregarToken(tipo, null); }
    private void agregarToken(TipoToken tipo, Object valor) {
        String lexema = codigoFuente.substring(inicio, actual);
        tokens.add(new Tokens(tipo, lexema, valor, linea));
    }
}