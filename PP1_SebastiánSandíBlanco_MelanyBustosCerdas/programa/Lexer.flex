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
    return new Symbol(type, yyline, yycolumn, value);}}
%}

/* Definiciones Regulares*/
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]
Identifier     = [a-zA-Z][a-zA-Z0-9_]*
Integer        = 0 | [1-9][0-9]*
Float          = [0-9]+ \. [0-9]+
String         = \"[^\"]*\"
Char           = '([^'\\\\]|\\\\.)'

%%

/* --- Sección de Reglas Léxicas --- */

<YYINITIAL> {
  /* Operadores*/
  "+"               { return symbol(sym.PLUS, yytext()); }
  "-"               { return symbol(sym.MINUS, yytext()); }
  "//"              { return symbol(sym.INT_DIV, yytext()); }
  "/"               { return symbol(sym.DIV, yytext()); }
  "*"               { return symbol(sym.MULT, yytext()); }
  "%"               { return symbol(sym.MOD, yytext()); }
  "^"               { return symbol(sym.POW, yytext()); }
  "++"              { return symbol(sym.INC, yytext()); }
  "--"              { return symbol(sym.DEC, yytext()); }
  "@"               { return symbol(sym.AND, yytext()); }
  "~"               { return symbol(sym.OR, yytext()); }
  "Σ"               { return symbol(sym.NOT, yytext()); }
  "->"              { return symbol(sym.ARROW, yytext()); }
  "=" { return symbol(sym.ASSIGN, yytext()); }
}