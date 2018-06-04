parser grammar DecafParser;

@header {
package decaf;
}

options
{
  language=Java;
  tokenVocab=DecafLexer;
}

program: TK_class LCURLY field_decl* method_decl* RCURLY EOF;


field_decl: TIPO ID (VIRGULA TIPO ID)* PONTVIRGULA
	| TIPO ID ECOLC INTLITERAL DCOLC (VIRGULA TIPO ID ECOLC INTLITERAL DCOLC)* PONTVIRGULA ;

method_decl: (TIPO | VOID) ID EPAR (parametroMeth_decl)? DPAR block;

parametroMeth_decl: TIPO ID (VIRGULA TIPO ID)*;

block: LCURLY var_decl* statement* RCURLY;

var_decl: TIPO ID (VIRGULA (TIPO |ID))* PONTVIRGULA;

statement: location assing_op expr PONTVIRGULA
	| method_call PONTVIRGULA
	| IF EPAR expr DPAR block (ELSE block)?
	| FOR ID ATRIB expr VIRGULA expr block
	| RETURN (expr)? PONTVIRGULA
	| BREAK PONTVIRGULA
	| CONTINUE PONTVIRGULA
	| block;

assing_op: ATRIB
	|MAIS ATRIB
	|MENOS ATRIB;

method_call: ID EPAR (expr (VIRGULA expr)*)? DPAR
	| CALLOUT EPAR STRING ((VIRGULA (expr | STRING))*)? DPAR;

location: ID
	|ID ECOLC expr DCOLC;

expr: location
	| method_call
	| (INTLITERAL|CHAR|BOOLEAN)
	| expr (OP_ARITH|OP_RELACIO|OP_EQUID|OP_COND|MAIS|MENOS) expr
	| MENOS expr
	| EXCAMACAO expr
	| EPAR expr DPAR;

