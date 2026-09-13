package Sintactico;

import Lexico.TipoToken;
import Lexico.Tokens;
import java.util.ArrayList;
import java.util.List;

public class AnalizadorSintactico {
    private final List<Tokens> tokens;
    private int actual = 0;
    private boolean huboError = false;
    private final List<String> listaErrores = new ArrayList<>();

    public AnalizadorSintactico(List<Tokens> tokens) {
        this.tokens = tokens;
    }

    public List<String> getListaErrores() {
        return listaErrores;
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
                error(mirar(), "Instrucción no válida o símbolo inesperado: '" + mirar().lexema + "'");
                avanzar();
            }
        } catch (ParseError e) {
            sincronizar();
        }
    }

    // var id (: Tipo)? (= Expresion)?
    // let id (: Tipo)? = Expresion
    private void declaracionVariable() {
        Tokens palabraClave = anterior();
        consumir(TipoToken.IDENTIFIER, "Se esperaba el identificador (nombre de la variable o constante).");

        boolean tieneTipo = false;
        if (coincide(TipoToken.COLON)) {
            if (coincide(TipoToken.TIPE_INT, TipoToken.TIPE_STRING, TipoToken.TIPE_DOUBLE, TipoToken.TIPE_BOOL)) {
                tieneTipo = true;
            } else {
                error(mirar(), "Se esperaba un tipo de dato válido (Int, String, Double, Bool).");
                throw new ParseError();
            }
        }

        if (coincide(TipoToken.ASSIGN)) {
            expresion();
        } else {
            // Regla semántico-sintáctica de Swift: let requiere inicialización obligatoria
            if (palabraClave.tipo == TipoToken.LET) {
                error(anterior(), "Las constantes 'let' deben inicializarse con un valor '='.");
                throw new ParseError();
            }
            if (!tieneTipo) {
                error(anterior(), "Se requiere especificar el tipo (: Tipo) o asignar un valor (= Expresion).");
                throw new ParseError();
            }
        }
    }

    // if Condicion { Bloque } (else { Bloque })?
    private void sentenciaIf() {
        expresion(); // Condición booleana
        consumir(TipoToken.LBRACE, "Se esperaba '{' para iniciar el bloque del 'if'.");
        bloque();
        consumir(TipoToken.RBRACE, "Se esperaba '}' para cerrar el bloque del 'if'.");

        if (coincide(TipoToken.ELSE)) {
            if (coincide(TipoToken.IF)) {
                sentenciaIf(); // Soporte para 'else if'
            } else {
                consumir(TipoToken.LBRACE, "Se esperaba '{' después de 'else'.");
                bloque();
                consumir(TipoToken.RBRACE, "Se esperaba '}' para cerrar el bloque 'else'.");
            }
        }
    }

    // while Condicion { Bloque }
    private void sentenciaWhile() {
        expresion(); // Condición
        consumir(TipoToken.LBRACE, "Se esperaba '{' para iniciar el bloque del 'while'.");
        bloque();
        consumir(TipoToken.RBRACE, "Se esperaba '}' para cerrar el bloque del 'while'.");
    }

    // print ( Expresion )
    private void sentenciaPrint() {
        consumir(TipoToken.LPAREN, "Se esperaba '(' después de 'print'.");
        expresion();
        consumir(TipoToken.RPAREN, "Se esperaba ')' para cerrar la llamada a 'print'.");
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

    // Jerarquía de expresiones (Precedencia)
    // Expresión -> Comparación
    private void expresion() {
        comparacion();
    }

    // Comparación: ==, !=, >, <, >=, <=
    private void comparacion() {
        termino();
        while (coincide(TipoToken.GREATER, TipoToken.LESS, TipoToken.GREATER_EQUAL, 
                        TipoToken.LESS_EQUAL, TipoToken.EQUALS, TipoToken.NOT_EQUALS)) {
            termino();
        }
    }

    // Término: +, -
    private void termino() {
        factor();
        while (coincide(TipoToken.PLUS, TipoToken.MINUS)) {
            factor();
        }
    }

    // Factor: *, /
    private void factor() {
        primario();
        while (coincide(TipoToken.MULTIPLY, TipoToken.DIVIDE)) {
            primario();
        }
    }

    // Elemento primario: Literales, Identificadores o Expresiones entre paréntesis
    private void primario() {
        if (coincide(TipoToken.NUMBER_INT, TipoToken.NUMBER_DOUBLE, 
                     TipoToken.STRING_LITERAL, TipoToken.BOOLEAN_LITERAL, 
                     TipoToken.IDENTIFIER)) {
            return;
        }

        if (coincide(TipoToken.LPAREN)) {
            expresion();
            consumir(TipoToken.RPAREN, "Se esperaba ')' tras la expresión agrupada.");
            return;
        }

        // Corrección de bucle infinito: emitir error y abortar la rama actual
        error(mirar(), "Se esperaba un literal, identificador o '(' pero se encontró '" + mirar().lexema + "'.");
        throw new ParseError();
    }

    // --- Métodos de utilidad y navegación ---

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
        throw new ParseError();
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
        String detalle = String.format("[Error Sintáctico] Línea %d en '%s': %s", 
                token.linea, token.lexema.isEmpty() ? "EOF" : token.lexema, mensaje);
        listaErrores.add(detalle);
        System.err.println(detalle);
    }

    // Modo pánico: descarta tokens hasta llegar a un delimitador o inicio de nueva sentencia
    private void sincronizar() {
        avanzar();

        while (!esFin()) {
            // Si el token anterior cerraba una llave o sentencia
            if (anterior().tipo == TipoToken.RBRACE) return;

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

    // Clase estática para control interno de excepciones sintácticas
    private static class ParseError extends RuntimeException {}
}