package Lexico;

public class Tokens {
    public final TipoToken tipo;
    public final String lexema;
    public final Object valor;
    public final int linea;

    public Tokens(TipoToken tipo, String lexema, Object valor, int linea) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.valor = valor;
        this.linea = linea;
    }

    public Tokens(TipoToken tipo, String lexema, int linea) {
        this(tipo, lexema, null, linea);
    }

    @Override
    public String toString() {
        return String.format("Token(Tipo: %s, Lexema: '%s', Valor: %s, Línea: %d)", 
                tipo, lexema, valor != null ? valor : "N/A", linea);
    }
}