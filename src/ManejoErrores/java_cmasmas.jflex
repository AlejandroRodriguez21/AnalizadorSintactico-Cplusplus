/* CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO */

package ManejoErrores;

import java_cup.runtime.Symbol;

%%

%class cppLexer
%public
%unicode
%cup
%line
%column

%eofval{
    return new Symbol(
        sym.EOF,
        yyline,
        yycolumn,
        "FIN_DE_ARCHIVO"
    );
%eofval}

%{
    private int erroresLexicos = 0;

    public int getErroresLexicos() {
        return erroresLexicos;
    }

    private Symbol symbol(int type) {
        return new Symbol(
            type,
            yyline,
            yycolumn,
            yytext()
        );
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(
            type,
            yyline,
            yycolumn,
            value
        );
    }
%}

LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

Identifier = [a-zA-Z_][a-zA-Z0-9_]*
IntegerLiteral = [0-9]+
FloatLiteral = [0-9]+\.[0-9]+

%%

<YYINITIAL> {

    /* TIPOS DE DATO */

    "int"       { return symbol(sym.INT); }
    "string"    { return symbol(sym.STRING); }
    "float"     { return symbol(sym.FLOAT); }
    "double"    { return symbol(sym.DOUBLE); }
    "char"      { return symbol(sym.CHAR); }
    "void"      { return symbol(sym.VOID); }
    "bool"      { return symbol(sym.BOOL); }
    "long"      { return symbol(sym.LONG); }
    "short"     { return symbol(sym.SHORT); }

    /* CONTROL DE FLUJO */

    "if"        { return symbol(sym.IF); }
    "else"      { return symbol(sym.ELSE); }
    "for"       { return symbol(sym.FOR); }
    "while"     { return symbol(sym.WHILE); }
    "do"        { return symbol(sym.DO); }
    "switch"    { return symbol(sym.SWITCH); }
    "case"      { return symbol(sym.CASE); }
    "default"   { return symbol(sym.DEFAULT); }
    "break"     { return symbol(sym.BREAK); }
    "continue"  { return symbol(sym.CONTINUE); }

    /* POO Y LIBRERIAS */

    "class"     { return symbol(sym.CLASS); }
    "struct"    { return symbol(sym.STRUCT); }
    "public"    { return symbol(sym.PUBLIC); }
    "private"   { return symbol(sym.PRIVATE); }
    "protected" { return symbol(sym.PROTECTED); }
    "return"    { return symbol(sym.RETURN); }
    "using"     { return symbol(sym.USING); }
    "namespace" { return symbol(sym.NAMESPACE); }
    "std"       { return symbol(sym.STD); }
    "cout"      { return symbol(sym.COUT); }
    "cin"       { return symbol(sym.CIN); }
    "endl"      { return symbol(sym.ENDL); }
    "this"      { return symbol(sym.THIS); }
    "new"       { return symbol(sym.NEW); }
    "delete"    { return symbol(sym.DELETE); }
    "import"    { return symbol(sym.IMPORT); }
    "const"     { return symbol(sym.CONST); }

    /* OPERADORES LOGICOS Y RELACIONALES */

    "=="        { return symbol(sym.IGUAL_IGUAL); }
    "!="        { return symbol(sym.DISTINTO); }
    ">="        { return symbol(sym.MAYOR_IGUAL); }
    "<="        { return symbol(sym.MENOR_IGUAL); }
    "&&"        { return symbol(sym.AND); }
    "||"        { return symbol(sym.OR); }
    "!"         { return symbol(sym.NOT); }

    /* OPERADORES BASICOS */

    "+"         { return symbol(sym.MAS); }
    "-"         { return symbol(sym.MENOS); }
    "*"         { return symbol(sym.POR); }
    "/"         { return symbol(sym.ENTRE); }
    "%"         { return symbol(sym.MODULO); }
    "="         { return symbol(sym.IGUAL); }
    "<"         { return symbol(sym.MENOR); }
    ">"         { return symbol(sym.MAYOR); }
    "<<"        { return symbol(sym.MENOR_MENOR); }
    ">>"        { return symbol(sym.MAYOR_MAYOR); }

    /* PUNTUACION Y AGRUPACION */

    ";"         { return symbol(sym.PUNTO_COMA); }
    ","         { return symbol(sym.COMA); }
    "."         { return symbol(sym.PUNTO); }
    "::"        { return symbol(sym.DOS_PUNTOS); }
    "("         { return symbol(sym.PARENTESIS_A); }
    ")"         { return symbol(sym.PARENTESIS_C); }
    "["         { return symbol(sym.CORCHETE_A); }
    "]"         { return symbol(sym.CORCHETE_C); }
    "{"         { return symbol(sym.LLAVE_A); }
    "}"         { return symbol(sym.LLAVE_C); }

    /* CADENAS Y COMENTARIOS */

    \"[^\"]*\" {
        return symbol(sym.CADENA, yytext());
    }

    "//".* {
        /* Ignorar comentario de linea. */
    }

    "/*"[^]*"*/" {
        /* Ignorar comentario de bloque. */
    }

    /* VALORES E IDENTIFICADORES */

    "true"      { return symbol(sym.TRUE); }
    "false"     { return symbol(sym.FALSE); }

    {Identifier} {
        return symbol(sym.IDENTIFICADOR, yytext());
    }

    {FloatLiteral} {
        return symbol(sym.DECIMAL, Double.valueOf(yytext()));
    }

    {IntegerLiteral} {
        return symbol(sym.ENTERO, Integer.valueOf(yytext()));
    }

    /* ESPACIOS */

    {WhiteSpace} {
        /* Ignorar. */
    }

    /* ERRORES LEXICOS DE LA ACTIVIDAD ANTERIOR */

    [0-9]+[a-zA-Z_]+[a-zA-Z0-9_]* {
        erroresLexicos++;

        System.err.println(
            "Error Lexico: Identificador mal formado"
            + " | Linea: " + (yyline + 1)
            + ", Columna: " + (yycolumn + 1)
            + " | Texto: " + yytext()
        );
    }

    [0-9]+\.[0-9]+\.[0-9\.]+ {
        erroresLexicos++;

        System.err.println(
            "Error Lexico: Literal numerico incorrecto"
            + " | Linea: " + (yyline + 1)
            + ", Columna: " + (yycolumn + 1)
            + " | Texto: " + yytext()
        );
    }

    \"[^\r\n\"]* {
        erroresLexicos++;

        System.err.println(
            "Error Lexico: Cadena mal formada (sin cerrar)"
            + " | Linea: " + (yyline + 1)
            + ", Columna: " + (yycolumn + 1)
            + " | Texto: " + yytext()
        );
    }
}

/* CARACTERES NO RECONOCIDOS */

[^] {
    erroresLexicos++;

    System.err.println(
        "Error Lexico: Caracter no reconocido"
        + " | Linea: " + (yyline + 1)
        + ", Columna: " + (yycolumn + 1)
        + " | Texto: " + yytext()
    );
}