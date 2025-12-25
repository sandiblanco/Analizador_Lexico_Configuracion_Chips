/*
CURSO: Compiladores e Intérpretes
PROYECTO #1: Análisis Léxico
ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
*/
package generados;
import java_cup.runtime.*;

%%

%class Scanner
%unicode
%cup
%line
%column
%public

%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
  private void reportError(String message) {
      System.err.println("Error léxico en línea " + (yyline+1) + ", columna " + (yycolumn+1) + ": " + message);
  }
%}

/* Definiciones regulares */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = [ \t\f] | {LineTerminator}
Identifier     = [a-zA-Z][a-zA-Z0-9_]*
Integer        = 0 | [1-9][0-9]*
Float          = [0-9]+ \. [0-9]+
String         = \"[^\"]*\"
Char           = "'"([^'\\\\]|\\\\.)"'"

/* Comentarios */
SingleLineComment = "|" [^\r\n]*
MultiLineComment  = "є" [^"э"]* "э"

%%
/* --- Sección de reglas léxicas --- */

<YYINITIAL> {
  /* Palabras reservadas */
  "world"           { return symbol(sym.WORLD, yytext()); }
  "local"           { return symbol(sym.LOCAL, yytext()); }
  "gift"            { return symbol(sym.GIFT, yytext()); }
  "navidad"         { return symbol(sym.NAVIDAD, yytext()); }
  "show"            { return symbol(sym.SHOW, yytext()); }
  "get"             { return symbol(sym.GET, yytext()); }
  "endl"            { return symbol(sym.ENDL, yytext()); }
  "coal"            { return symbol(sym.COAL, yytext()); }
  "return"          { return symbol(sym.RETURN, yytext()); }
  "break"           { return symbol(sym.BREAK, yytext()); }

  /* Estructuras de control */
  "decide"          { return symbol(sym.DECIDE, yytext()); }
  "of"              { return symbol(sym.OF, yytext()); }
  "else"            { return symbol(sym.ELSE, yytext()); }
  "end"             { return symbol(sym.END, yytext()); }
  "loop"            { return symbol(sym.LOOP, yytext()); }
  "exit"            { return symbol(sym.EXIT, yytext()); }
  "when"            { return symbol(sym.WHEN, yytext()); }
  "for"             { return symbol(sym.FOR, yytext()); }

  /* Tipos de datos */
  "int"          { return symbol(sym.INT, yytext()); }
  "float"        { return symbol(sym.FLOAT, yytext()); }
  "boolean"         { return symbol(sym.BOOL, yytext()); }
  "char"            { return symbol(sym.CHAR, yytext()); }
  "string"          { return symbol(sym.STRING, yytext()); }

  /* Agrupadores */
  "¿"               { return symbol(sym.LPAREN, yytext()); }
  "?"               { return symbol(sym.RPAREN, yytext()); }
  "¡"               { return symbol(sym.LBRACE, yytext()); }
  "!"               { return symbol(sym.RBRACE, yytext()); }

  /* Operadores */
  "->"              { return symbol(sym.ARROW, yytext()); }
  "+"               { return symbol(sym.PLUS, yytext()); }
  "-"               { return symbol(sym.MINUS, yytext()); }
  "//"              { return symbol(sym.DIVENT, yytext()); }
  "/"               { return symbol(sym.DIV, yytext()); }
  "*"               { return symbol(sym.MULT, yytext()); }
  "%"               { return symbol(sym.MOD, yytext()); }
  "^"               { return symbol(sym.ELEV, yytext()); }
  "++"              { return symbol(sym.INC, yytext()); }
  "--"              { return symbol(sym.DEC, yytext()); }
  "@"               { return symbol(sym.AND, yytext()); }
  "~"               { return symbol(sym.OR, yytext()); }
  "Σ"               { return symbol(sym.NOT, yytext()); }
  "->"              { return symbol(sym.ARROW, yytext()); }
  "="               { return symbol(sym.ASSIGN, yytext()); }

  /* Operadores relacionales */
  "<="              { return symbol(sym.LOWEQ, yytext()); }
  ">="              { return symbol(sym.GEQ, yytext()); }
  "<"               { return symbol(sym.LOWTHAN, yytext()); }
  ">"               { return symbol(sym.GTHAN, yytext()); }
  "=="              { return symbol(sym.EQ, yytext()); }
  "!="              { return symbol(sym.NEQ, yytext()); }

  /* Identificadores y Literales */
  "true"            { return symbol(sym.BOOL_LITERAL, true); }
  "false"           { return symbol(sym.BOOL_LITERAL, false); }
  {Identifier}      { return symbol(sym.ID, yytext()); }
  {Integer}         { return symbol(sym.INT_LITERAL, Integer.parseInt(yytext().toString())); }
  {Float}           { return symbol(sym.FLOAT_LITERAL, Double.parseDouble(yytext().toString())); }
  {String}          { return symbol(sym.STRING_LITERAL, yytext().toString().substring(1, yytext().length()-1)); }
  {Char}            { return symbol(sym.CHAR_LITERAL, yytext().toString().substring(1, yytext().length()-1));}

  /* Comentarios y Espacios */
  {SingleLineComment} { /* Ignorar */ }
  {MultiLineComment}  { /* Ignorar */ }
  {WhiteSpace}        { /* Ignorar */ }
}

/* Manejo de Errores: Modo Pánico  */
[^] {
  reportError("Caracter ilegal <" + yytext() + ">");
}