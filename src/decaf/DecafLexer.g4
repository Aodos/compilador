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

PALAVRASRESERVADAS : 'boolean' | 'break' | 'callout' | 'class' | 'continue' | 
'else' | 'for' | 'int' | 'return' | 'void' | 'if';

BOOLEAN : 'true'|'false';

ID  : 
  ('_'|LETRAS)(LETRAS|DIGITOS|'_')*;

CHAR : '\'' (COTEUDOCHAR| ESC ) '\'';

STRING : '"' (COTEUDOCHAR+ | ESC)* '"';

NUMEROS :  ( '0x' ('0'..'9'|'a'..'f'|'A'..'F')+) | (DIGITOS)+ ;

OP : '+' | '-' | '*' | '<' | '<=' | '!=' | '&&' | ',' | ';' | '[' | '|' | '=' | '('
| ')' | ']' | '[' | '>' | '>=';


fragment
PREFIXOHEXA : '0X';

fragment
COTEUDOCHAR : (' '..'!' | '#'..'&' | '('..'[' | ']'..'~');

fragment
LETRAS : ('a'..'z' | 'A'..'Z');

fragment
DIGITOS : ('0'..'9');

fragment
ESC :  '\\' ('\\' | '\"' | '\'' | 't' | 'n');

SL_COMMENT : '//' (~'\n')* '\n' -> skip;

WS_ : (' ' | '\n' | '\t') -> skip;



