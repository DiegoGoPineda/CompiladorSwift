package Sintactico;

import java.util.List;
import Lexico.TipoToken;
import Lexico.Tokens;

public class AnalizadorSintactico {
    private final List<Tokens> tokens;
    private int actual = 0;
    private boolean huboError = false;

    public AnalizadorSintactico(List<Tokens> tokens) {
        this.tokens = tokens;
    }

    // Punto de entrada: Programa -> Declaracion* EOF
    public boolean analizar() {
        while (!esFin()) {
            declaracion();
        }
        return !huboError;
    }

    // Regla: decide qué sentencia procesar
    private void declaracion() {
        try {
            if (coincide(TipoToken.VAR, TipoToken.LET)) {
                declaracionVariable();
            } else if (coincide(TipoToken.IF)) {
                sentenciaIf();
            } else if (coincide(TipoToken.WHILE)) {
                sentenciaWhile();
            } else if (coincide(TipoToken.PRINT)) {
                sentenciaPrint();
            } else if (coincide(TipoToken.IDENTIFIER)) {
                asignacion();
            } else {
                error(mirar(), "Instrucción no válida o símbolo inesperado.");
                avanzar();
            }
        } catch (RuntimeException e) {
            sincronizar();
        }
    }

    // var|let id (: Tipo)? (= Expresion)?
    private void declaracionVariable() {
        consumir(TipoToken.IDENTIFIER, "Se esperaba el nombre de la variable.");

        if (coincide(TipoToken.COLON)) {
            if (!coincide(TipoToken.TIPE_INT, TipoToken.TIPE_STRING, TipoToken.TIPE_DOUBLE, TipoToken.TIPE_BOOL)) {
                error(mirar(), "Se esperaba un tipo de dato (Int, String, Double, Bool).");
            }
        }

        if (coincide(TipoToken.ASSIGN)) {
            expresion();
        }
    }

    // if Condicion { Bloque } (else { Bloque })?
    private void sentenciaIf() {
        expresion(); // Condición
        consumir(TipoToken.LBRACE, "Se esperaba '{' después de la condición del if.");
        bloque();
        consumir(TipoToken.RBRACE, "Se esperaba '}' al final del bloque if.");

        if (coincide(TipoToken.ELSE)) {
            consumir(TipoToken.LBRACE, "Se esperaba '{' después de 'else'.");
            bloque();
            consumir(TipoToken.RBRACE, "Se esperaba '}' al final del bloque else.");
        }
    }

    // while Condicion { Bloque }
    private void sentenciaWhile() {
        expresion(); // Condición
        consumir(TipoToken.LBRACE, "Se esperaba '{' después de la condición del while.");
        bloque();
        consumir(TipoToken.RBRACE, "Se esperaba '}' al final del bloque while.");
    }

    // print ( Expresion )
    private void sentenciaPrint() {
        consumir(TipoToken.LPAREN, "Se esperaba '(' después de 'print'.");
        expresion();
        consumir(TipoToken.RPAREN, "Se esperaba ')' al cerrar 'print'.");
    }

    // id = Expresion
    private void asignacion() {
        consumir(TipoToken.ASSIGN, "Se esperaba '=' en la asignación.");
        expresion();
    }

    private void bloque() {
        while (!revisar(TipoToken.RBRACE) && !esFin()) {
            declaracion();
        }
    }

    // Jerarquía de expresiones: Comparación -> Aritmética -> Término -> Factor
    private void expresion() {
        comparacion();
    }

    private void comparacion() {
        termino();
        while (coincide(TipoToken.GREATER, TipoToken.LESS, TipoToken.GREATER_EQUAL, TipoToken.LESS_EQUAL, TipoToken.EQUALS, TipoToken.NOT_EQUALS)) {
            termino();
        }
    }

    private void termino() {
        factor();
        while (coincide(TipoToken.PLUS, TipoToken.MINUS)) {
            factor();
        }
    }

    private void factor() {
        primario();
        while (coincide(TipoToken.MULTIPLY, TipoToken.DIVIDE)) {
            primario();
        }
    }

    private void primario() {
        if (coincide(TipoToken.NUMBER_INT, TipoToken.NUMBER_DOUBLE, TipoToken.STRING_LITERAL, TipoToken.BOOLEAN_LITERAL, TipoToken.IDENTIFIER)) {
            return;
        }

        if (coincide(TipoToken.LPAREN)) {
            expresion();
            consumir(TipoToken.RPAREN, "Se esperaba ')' después de la expresión.");
            return;
        }

        error(mirar(), "Expresión no válida.");
    }

    // Métodos auxiliares de control de flujo
    private boolean coincide(TipoToken... tipos) {
        for (TipoToken tipo : tipos) {
            if (revisar(tipo)) {
                avanzar();
                return true;
            }
        }
        return false;
    }

    private Tokens consumir(TipoToken tipo, String mensaje) {
        if (revisar(tipo)) return avanzar();
        error(mirar(), mensaje);
        throw new RuntimeException();
    }

    private boolean revisar(TipoToken tipo) {
        if (esFin()) return false;
        return mirar().tipo == tipo;
    }

    private Tokens avanzar() {
        if (!esFin()) actual++;
        return anterior();
    }

    private boolean esFin() {
        return mirar().tipo == TipoToken.EOF;
    }

    private Tokens mirar() {
        return tokens.get(actual);
    }

    private Tokens anterior() {
        return tokens.get(actual - 1);
    }

    private void error(Tokens token, String mensaje) {
        huboError = true;
        System.err.printf("[Error Sintáctico] Línea %d en '%s': %s%n", token.linea, token.lexema, mensaje);
    }

    // Modo pánico: salta tokens hasta encontrar el inicio de una nueva sentencia
    private void sincronizar() {
        avanzar();
        while (!esFin()) {
            switch (mirar().tipo) {
                case VAR:
                case LET:
                case IF:
                case WHILE:
                case PRINT:
                    return;
                default:
                    avanzar();
            }
        }
    }
}