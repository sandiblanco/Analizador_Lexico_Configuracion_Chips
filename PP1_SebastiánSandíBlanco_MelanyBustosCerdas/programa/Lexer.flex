/*
CURSO: Compiladores e Intérpretes
PROYECTO #1: Análisis Léxico
ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
*/

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
%}

/* Definiciones regulares */
LineTerminator = \r|\n|\r\n
WhiteSpace     = [ \t\f] | {LineTerminator}
Identifier     = [a-zA-Z][a-zA-Z0-9_]*
Integer        = 0 | [1-9][0-9]*
Float          = [0-9]+ \. [0-9]+
String         = \"[^\"]*\"
Char           = '([^'\\\\]|\\\\.)'

/* Comentarios */
SingleLineComment = "|" [^\r\n]*
MultiLineComment  = "є" [^"э"]* "э"

%%
/* --- Sección de reglas léxicas --- */

<YYINITIAL> {
  /* Palabras reservadas */
  "world"           { return symbol(sym.WORLD); }
  "local"           { return symbol(sym.LOCAL); }
  "gift"            { return symbol(sym.GIFT); }
  "navidad"         { return symbol(sym.NAVIDAD); }
  "show"            { return symbol(sym.SHOW); }
  "get"             { return symbol(sym.GET); }
  "endl"            { return symbol(sym.ENDL); }
  "coal"            { return symbol(sym.COAL); }
  "return"          { return symbol(sym.RETURN); }
  "break"           { return symbol(sym.BREAK); }

  /* Tipos de datos */
  "entero"          { return symbol(sym.INT); }
  "flotante"        { return symbol(sym.FLOAT); }
  "boolean"         { return symbol(sym.BOOL); }
  "char"            { return symbol(sym.CHAR); }
  "string"          { return symbol(sym.STRING); }

  /* Agrupadores */
  "¿"               { return symbol(sym.LPAREN); }
  "?"               { return symbol(sym.RPAREN); }
  "¡"               { return symbol(sym.LBRACE); }
  "!"               { return symbol(sym.RBRACE); }

  /* Operadores */
  "->"              { return symbol(sym.ARROW); }
  "+"               { return symbol(sym.PLUS); }
  "-"               { return symbol(sym.MINUS); }
  "//"              { return symbol(sym.DIVENT); }
  "/"               { return symbol(sym.DIV); }
  "*"               { return symbol(sym.MULT); }
  "%"               { return symbol(sym.MOD); }
  "^"               { return symbol(sym.ELEV); }
  "++"              { return symbol(sym.INC); }
  "--"              { return symbol(sym.DEC); }
  "@"               { return symbol(sym.AND); }
  "~"               { return symbol(sym.OR); }
  "Σ"               { return symbol(sym.NOT); }
  "->"              { return symbol(sym.ARROW); }
  "="               { return symbol(sym.ASSIGN); }

  /* Relacionales */
  "<="              { return symbol(sym.LOWEQ); }
  ">="              { return symbol(sym.GEQ); }
  "<"               { return symbol(sym.LOWTHAN); }
  ">"               { return symbol(sym.GTHAN); }
  "=="              { return symbol(sym.EQ); }
  "!="              { return symbol(sym.NEQ); }

  /* Comentarios y Espacios */
  {SingleLineComment} { /* Ignorar */ }
  {MultiLineComment}  { /* Ignorar */ }
  {WhiteSpace}        { /* Ignorar */ }
}