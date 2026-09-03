package Lexico;

public enum TipoToken {
    // Palabras reservadas
    LET, VAR, IF, ELSE, WHILE, PRINT,
    TIPE_INT, TIPE_STRING, TIPE_DOUBLE, TIPE_BOOL,

    // Identificadores y literales
    IDENTIFIER, NUMBER_INT, NUMBER_DOUBLE, STRING_LITERAL, BOOLEAN_LITERAL,

    // Operadores
    ASSIGN, PLUS, MINUS, MULTIPLY, DIVIDE,
    EQUALS, NOT_EQUALS,
    GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, // <-- Agregados aquí

    // Delimitadores
    LPAREN, RPAREN, LBRACE, RBRACE, COLON, COMMA,

    // Controles
    EOF, UNKNOWN
}