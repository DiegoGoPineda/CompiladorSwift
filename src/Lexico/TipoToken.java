package lexico;
// esta va ser la clase de los tokens zzz

public enum TipoToken{
    // palabras reservadas de swift
    LET, VAR, IF, ELSE, WHILE, PRINT,
    TIPE_INT, TIPE_STRING, TIPE_DOUBLE, TIPE_BOOL,
    // identificadores y literales
    IDENTIFIER, NUMBER_INT, NUMBER_DOUBLE, STRING_LITERAL, BOOLEAN_LITERAL,
    // operadores
    ASSIGN, PLUS, MINUS, MULTIPLY, DIVIDE, EQUALS, NOT_EQUALS, GREATER, LESS,
    // limitadores
    LPAREN, RPAREN, LBRACE, RBRACE, COLON, COMMA,
    // controless
    EOF, UNKNOWN
}