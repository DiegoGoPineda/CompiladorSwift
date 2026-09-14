package Semantico;

import Lexico.TipoToken;

public class Simbolo {
    private final String nombre;
    private final TipoToken tipoDato;
    private final boolean esConstante; // true si se declaró con 'let'
    private final int linea;

    public Simbolo(String nombre, TipoToken tipoDato, boolean esConstante, int linea) {
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        this.esConstante = esConstante;
        this.linea = linea;
    }

    public String getNombre() { return nombre; }
    public TipoToken getTipoDato() { return tipoDato; }
    public boolean esConstante() { return esConstante; }
    public int getLinea() { return linea; }
}