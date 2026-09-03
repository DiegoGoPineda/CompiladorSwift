package lexico;

public class Tokens {
    public final TipoToken tipo;
    public final String lexema;
    public final Object valor;
    public final int linea;
    // constructor para crear un token con todos los atributos
    public Tokens(TipoToken tipo, String lexema, Object valor, int linea) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.valor = valor;
        this.linea = linea;
    }
    // contructor vacio por si no hay valor en el token como palabras claves
    public Tokens(TipoToken tipo, String lexema, int linea) {
        this(tipo, lexema, null, linea);
    }
    // metodo para imprimir el poderoso token zzz
   @Override
    public String toString() {
        return String.format("Token(Tipo: %s, Lexema: '%s', Valor: %s, Línea: %d)", 
                tipo, lexema, valor != null ? valor : "N/A", linea);
    }

}   