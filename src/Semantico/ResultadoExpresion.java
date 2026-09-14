package Semantico;

import Lexico.TipoToken;

public class ResultadoExpresion {
    private final String lugar;
    private final TipoToken tipoDato;

    public ResultadoExpresion(String lugar, TipoToken tipoDato) {
        this.lugar = lugar;
        this.tipoDato = tipoDato;
    }

    public String getLugar() { 
        return lugar; 
    }

    public TipoToken getTipoDato() { 
        return tipoDato; 
    }
}