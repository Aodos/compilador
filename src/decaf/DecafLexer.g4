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

CHAR : '\'' (' '..'!' | '#'..'&' | '('..'[' | ']'..'~' | ESC ) '\'';

STRING : '"' (ESC)* '"';

NUMEROS : ( HEXAPRFIXO ('0'..'9'|'a'..'f'|'A'..'F')+) | (DIGITOS)+ ;

OP : '+' | '-' | '*' | '<' | '<=' | '!=' | '&&' | ',' | ';' | '[' | '|' | '=' | '('
| ')' | ']' | '[' | '>' | '>=';

fragment
HEXAPRFIXO : '0x' ;

fragment
LETRAS : ('a'..'z' | 'A'..'Z');

fragment
DIGITOS : ('0'..'9');

fragment
ESC :  '\\' ('\\' | '\"' | '\'' | 't' | 'n');

SL_COMMENT : '//' (~'\n')* '\n' -> skip;

WS_ : (' ' | '\n' | '\t') -> skip;



