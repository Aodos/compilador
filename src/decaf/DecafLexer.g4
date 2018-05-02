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

TK_class: 'class Program';

LCURLY : '{';

RCURLY : '}';

VIRGULA : ',';

PONTVIRGULA : ';';

ECOLC :'[';

DCOLC :']';

EPAR: '(';

DPAR: ')';

ATRIB :'=';

OP_ARITH: '*'|'/'|'%';

MAIS: '+';

MENOS: '-';

EXCAMACAO: '!';

OP_RELACIO: '<'|'>'|'<='|'>=';

OP_EQUID: '=='|'!=';

OP_COND: '&&'|'||';

BREAK : 'break';

CALLOUT: 'callout';

CONTINUE: 'continue';

RETURN: 'return';

TIPO : 'boolean'|'int';

VOID : 'void';

BOOLEAN : VALORBOOLEAN;

fragment
VALORBOOLEAN : 'true'|'false';

IF : 'if';

FOR: 'for';

ELSE: 'else';

fragment
CONTEUDOCHAR: (' '..'!' | '#'..'&' | '('..'[' | ']'..'~');

fragment
LETRAS : ('a'..'z' | 'A'..'Z');

fragment
DIGITOS: ('0'..'9');

fragment
HEXADECIMAL: (DIGITOS|'a'..'f'|'A'..'F')+;

ID  : ('_'|LETRAS)(LETRAS|DIGITOS|'_')*;

INTLITERAL : '0x'(HEXADECIMAL)+|(DIGITOS)+;

fragment
ESC :  '\\' ('\\' | '\"' | '\'' | 't' | 'n');

CHAR : '\'' (CONTEUDOCHAR|LETRAS|ESC) '\'';

STRING : '"' (CONTEUDOCHAR+|LETRAS+|ESC)* '"';

WS_ : (' ' | '\n' | '\t' ) -> skip;

SL_COMMENT : '//' (~'\n')* '\n' -> skip;


