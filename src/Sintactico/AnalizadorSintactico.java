package Sintactico;

import Lexico.TipoToken;
import Lexico.Tokens;
import Semantico.GeneradorC3D;
import Semantico.ResultadoExpresion;
import Semantico.Simbolo;
import Semantico.TablaSimbolos;
import java.util.ArrayList;
import java.util.List;

public class AnalizadorSintactico {
    private final List<Tokens> tokens;
    private int actual = 0;
    private boolean huboError = false;
    private final List<String> listaErrores = new ArrayList<>();

    private final TablaSimbolos tablaSimbolos = new TablaSimbolos();
    private final GeneradorC3D c3d = new GeneradorC3D();

    public AnalizadorSintactico(List<Tokens> tokens) {
        this.tokens = tokens;
    }

    public List<String> getListaErrores() { 
        return listaErrores; 
    }

    public GeneradorC3D getGeneradorC3D() { 
        return c3d; 
    }

    public TablaSimbolos getTablaSimbolos() { 
        return tablaSimbolos; 
    }

    public boolean analizar() {
        try {
            while (!esFin()) {
                declaracion();
            }
        } catch (ParseError e) {
            return false;
        }
        return !huboError;
    }

    private void declaracion() {
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
            throw new ParseError();
        }
    }

    private void declaracionVariable() {
        Tokens palabraClave = anterior();
        boolean esConstante = (palabraClave.tipo == TipoToken.LET);

        Tokens idToken = consumir(TipoToken.IDENTIFIER, "Se esperaba el identificador.");
        String nombreVar = idToken.lexema;

        TipoToken tipoDato = null;
        if (coincide(TipoToken.COLON)) {
            if (coincide(TipoToken.TIPE_INT, TipoToken.TIPE_STRING, TipoToken.TIPE_DOUBLE, TipoToken.TIPE_BOOL)) {
                tipoDato = anterior().tipo;
            } else {
                error(mirar(), "Se esperaba un tipo de dato válido (Int, String, Double, Bool).");
                throw new ParseError();
            }
        }

        boolean insertado = tablaSimbolos.insertar(new Simbolo(nombreVar, tipoDato, esConstante, idToken.linea));
        if (!insertado) {
            error(idToken, "Error Semántico: La variable '" + nombreVar + "' ya fue declarada previamente.");
            throw new ParseError();
        }

        if (coincide(TipoToken.ASSIGN)) {
            ResultadoExpresion res = expresion();
            
            // Verificación de compatibilidad de tipos
            if (tipoDato != null && res.getTipoDato() != null && tipoDato != res.getTipoDato()) {
                error(idToken, "Error Semántico: Incompatibilidad de tipos. No se puede asignar '" 
                        + res.getTipoDato() + "' a una variable de tipo '" + tipoDato + "'.");
                throw new ParseError();
            }
            c3d.emitir(nombreVar + " = " + res.getLugar());
        } else {
            if (esConstante) {
                error(idToken, "Error Semántico: La constante 'let " + nombreVar + "' debe inicializarse con un valor.");
                throw new ParseError();
            }
        }
    }

    private void asignacion() {
        Tokens idToken = anterior();
        String nombreVar = idToken.lexema;

        Simbolo sim = tablaSimbolos.buscar(nombreVar);
        if (sim == null) {
            error(idToken, "Error Semántico: La variable '" + nombreVar + "' no ha sido declarada.");
            throw new ParseError();
        }
        if (sim.esConstante()) {
            error(idToken, "Error Semántico: No se puede reasignar un valor a la constante 'let " + nombreVar + "'.");
            throw new ParseError();
        }

        consumir(TipoToken.ASSIGN, "Se esperaba '=' en la asignación.");
        ResultadoExpresion res = expresion();

        if (sim.getTipoDato() != null && res.getTipoDato() != null && sim.getTipoDato() != res.getTipoDato()) {
            error(idToken, "Error Semántico: Tipo incompatible en la asignación a '" + nombreVar 
                    + "'. Se esperaba '" + sim.getTipoDato() + "' pero se obtuvo '" + res.getTipoDato() + "'.");
            throw new ParseError();
        }

        c3d.emitir(nombreVar + " = " + res.getLugar());
    }

    private void sentenciaIf() {
        ResultadoExpresion condicion = expresion();

        // Verificación Semántica: Tipo booleano obligatorio en la condición
        if (condicion.getTipoDato() != TipoToken.TIPE_BOOL) {
            error(anterior(), "Error Semántico: La condición del 'if' debe ser de tipo booleano ('Bool'), pero se obtuvo '" 
                    + condicion.getTipoDato() + "'.");
            throw new ParseError();
        }

        String etiquetaElse = c3d.nuevaEtiqueta();
        String etiquetaFin = c3d.nuevaEtiqueta();

        c3d.emitir("if_false " + condicion.getLugar() + " goto " + etiquetaElse);

        consumir(TipoToken.LBRACE, "Se esperaba '{' para iniciar el bloque del 'if'.");
        bloque();
        consumir(TipoToken.RBRACE, "Se esperaba '}' para cerrar el bloque del 'if'.");

        c3d.emitir("goto " + etiquetaFin);
        c3d.emitirEtiqueta(etiquetaElse);

        if (coincide(TipoToken.ELSE)) {
            if (coincide(TipoToken.IF)) {
                sentenciaIf();
            } else {
                consumir(TipoToken.LBRACE, "Se esperaba '{' después de 'else'.");
                bloque();
                consumir(TipoToken.RBRACE, "Se esperaba '}' para cerrar el bloque 'else'.");
            }
        }

        c3d.emitirEtiqueta(etiquetaFin);
    }

    private void sentenciaWhile() {
        String etiquetaInicio = c3d.nuevaEtiqueta();
        String etiquetaFin = c3d.nuevaEtiqueta();

        c3d.emitirEtiqueta(etiquetaInicio);

        ResultadoExpresion condicion = expresion();

        // Verificación Semántica: Tipo booleano obligatorio en la condición
        if (condicion.getTipoDato() != TipoToken.TIPE_BOOL) {
            error(anterior(), "Error Semántico: La condición del 'while' debe ser de tipo booleano ('Bool'), pero se obtuvo '" 
                    + condicion.getTipoDato() + "'.");
            throw new ParseError();
        }

        c3d.emitir("if_false " + condicion.getLugar() + " goto " + etiquetaFin);

        consumir(TipoToken.LBRACE, "Se esperaba '{' para iniciar el bloque del 'while'.");
        bloque();
        consumir(TipoToken.RBRACE, "Se esperaba '}' para cerrar el bloque del 'while'.");

        c3d.emitir("goto " + etiquetaInicio);
        c3d.emitirEtiqueta(etiquetaFin);
    }

    private void sentenciaPrint() {
        consumir(TipoToken.LPAREN, "Se esperaba '(' después de 'print'.");
        ResultadoExpresion valorImprimir = expresion();
        consumir(TipoToken.RPAREN, "Se esperaba ')' para cerrar la llamada a 'print'.");
        c3d.emitir("param " + valorImprimir.getLugar());
        c3d.emitir("call print, 1");
    }

    private void bloque() {
        while (!revisar(TipoToken.RBRACE) && !esFin()) {
            declaracion();
        }
    }

    // --- Jerarquía de expresiones ---

    private ResultadoExpresion expresion() {
        return comparacion();
    }

    private ResultadoExpresion comparacion() {
        ResultadoExpresion izq = termino();
        while (coincide(TipoToken.GREATER, TipoToken.LESS, TipoToken.GREATER_EQUAL, 
                        TipoToken.LESS_EQUAL, TipoToken.EQUALS, TipoToken.NOT_EQUALS)) {
            Tokens op = anterior();
            ResultadoExpresion der = termino();
            String temp = c3d.nuevoTemporal();
            c3d.emitir(temp + " = " + izq.getLugar() + " " + op.lexema + " " + der.getLugar());
            izq = new ResultadoExpresion(temp, TipoToken.TIPE_BOOL);
        }
        return izq;
    }

    private ResultadoExpresion termino() {
        ResultadoExpresion izq = factor();
        while (coincide(TipoToken.PLUS, TipoToken.MINUS)) {
            Tokens op = anterior();
            ResultadoExpresion der = factor();
            String temp = c3d.nuevoTemporal();
            c3d.emitir(temp + " = " + izq.getLugar() + " " + op.lexema + " " + der.getLugar());
            
            TipoToken tipoFinal = (izq.getTipoDato() == TipoToken.TIPE_DOUBLE || der.getTipoDato() == TipoToken.TIPE_DOUBLE) 
                                  ? TipoToken.TIPE_DOUBLE : izq.getTipoDato();
            izq = new ResultadoExpresion(temp, tipoFinal);
        }
        return izq;
    }

    private ResultadoExpresion factor() {
        ResultadoExpresion izq = primario();
        while (coincide(TipoToken.MULTIPLY, TipoToken.DIVIDE)) {
            Tokens op = anterior();
            ResultadoExpresion der = primario();
            String temp = c3d.nuevoTemporal();
            c3d.emitir(temp + " = " + izq.getLugar() + " " + op.lexema + " " + der.getLugar());
            
            TipoToken tipoFinal = (izq.getTipoDato() == TipoToken.TIPE_DOUBLE || der.getTipoDato() == TipoToken.TIPE_DOUBLE) 
                                  ? TipoToken.TIPE_DOUBLE : izq.getTipoDato();
            izq = new ResultadoExpresion(temp, tipoFinal);
        }
        return izq;
    }

    private ResultadoExpresion primario() {
        if (coincide(TipoToken.NUMBER_INT)) {
            return new ResultadoExpresion(anterior().lexema, TipoToken.TIPE_INT);
        }
        if (coincide(TipoToken.NUMBER_DOUBLE)) {
            return new ResultadoExpresion(anterior().lexema, TipoToken.TIPE_DOUBLE);
        }
        if (coincide(TipoToken.STRING_LITERAL)) {
            return new ResultadoExpresion(anterior().lexema, TipoToken.TIPE_STRING);
        }
        if (coincide(TipoToken.BOOLEAN_LITERAL)) {
            return new ResultadoExpresion(anterior().lexema, TipoToken.TIPE_BOOL);
        }

        if (coincide(TipoToken.IDENTIFIER)) {
            Tokens idToken = anterior();
            Simbolo sim = tablaSimbolos.buscar(idToken.lexema);
            if (sim == null) {
                error(idToken, "Error Semántico: Uso de variable no declarada '" + idToken.lexema + "'.");
                throw new ParseError();
            }
            return new ResultadoExpresion(idToken.lexema, sim.getTipoDato());
        }

        if (coincide(TipoToken.LPAREN)) {
            ResultadoExpresion expr = expresion();
            consumir(TipoToken.RPAREN, "Se esperaba ')' tras la expresión agrupada.");
            return expr;
        }

        error(mirar(), "Se esperaba un literal, identificador o '(' pero se encontró '" + mirar().lexema + "'.");
        throw new ParseError();
    }

    // Auxiliares de lectura y avance
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
        String detalle = String.format("[Error Semántico/Sintáctico] Línea %d en '%s': %s", 
                token.linea, token.lexema.isEmpty() ? "EOF" : token.lexema, mensaje);
        listaErrores.add(detalle);
        System.err.println(detalle);
    }

    private static class ParseError extends RuntimeException {}
}