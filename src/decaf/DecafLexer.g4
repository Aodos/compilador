lexer grammar DecafLexer;

@header {
package decaf;
}

options
{
  language=Java;
}

tokens
{
  TK_class
}

LCURLY : '{';
RCURLY : '}';

ID  :
  ('_'|LETRAS)(LETRAS|DIGITOS|'_')+;

CHAR : '\'' (' '..'!' | '#'..'&' | '('..'[' | ']'..'~' | ESC ) '\'';

STRING : '"' (ESC)* '"';

NUMEROS : HEXADECIMAL;

//Arrumando
PALAVRASRESERVADAS : 'boolean' | 'break' | 'callout' | 'class' | 'continue' | 
'else' | 'false' | 'for' | 'int' | 'return' | 'true' | 'void';

fragment
HEXADECIMAL : PREFIXOHEXADECIMAL (DIGITOS|LETRAS)+;

fragment
DECIMAL : (DIGITOS)+;

fragment
PREFIXOHEXADECIMAL : '0x';

fragment
LETRAS : ('a'..'z' | 'A'..'Z');

fragment
DIGITOS : ('0'..'9');

fragment
ESC :  '\\' ('\\' | '\"' | '\'' | 't' | 'n');

SL_COMMENT : '//' (~'\n')* '\n' -> skip;

WS_ : (' ' | '\n' ) -> skip;



