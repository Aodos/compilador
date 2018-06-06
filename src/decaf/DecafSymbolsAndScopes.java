package decaf;

import java.util.ArrayList;
import java.util.List;

import org.antlr.symtab.FunctionSymbol;

import org.antlr.symtab.GlobalScope;

import org.antlr.symtab.LocalScope;

import org.antlr.symtab.Scope;

import org.antlr.symtab.VariableSymbol;

import org.antlr.symtab.Symbol;
import org.antlr.symtab.Type;
import org.antlr.v4.runtime.misc.NotNull;

import org.antlr.v4.runtime.ParserRuleContext;

import org.antlr.v4.runtime.Token;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import decaf.CompiladorArthurException.MainNaoEncontradoException;
import decaf.CompiladorArthurException.VariavelNaoInstanciadaException;
import decaf.CompiladorArthurException.ArrayNaoValidoException;
import decaf.CompiladorArthurException.NumeroDeArgumentosMetodoInvalidoException;
import decaf.CompiladorArthurException.TipoDeArgumentosMetodoInvalidoException;
import decaf.CompiladorArthurException.RetornoMetodoException;

import decaf.DecafParser.Assing_opContext;
import decaf.DecafParser.ExprContext;
import decaf.DecafParser.Field_declContext;
import decaf.DecafParser.LocationContext;
import decaf.DecafParser.Method_callContext;
import decaf.DecafParser.Method_declContext;
import decaf.DecafParser.ParametroMeth_declContext;
import decaf.DecafParser.StatementContext;
import decaf.DecafParser.Var_declContext;
import decaf.DecafSymbol.Tipos;

/**
 * 
 * This class defines basic symbols and scopes for Decaf language
 * 
 */

public class DecafSymbolsAndScopes extends DecafParserBaseListener {
	
	String ultMethDecl = "";

	ParseTreeProperty<Scope> scopes = new ParseTreeProperty<Scope>();

	GlobalScope globals;

	Scope currentScope; // define symbols in this scope

	List<Method_declContext> listaMetodos = new ArrayList<DecafParser.Method_declContext>();
	List<DecafSymbol> listaDeSimboloGeral = new ArrayList<DecafSymbol>();
	
	private DecafSymbol retornaSimbolo(String nomeVar) {
		DecafSymbol retorno = null;
		
		Scope aux = currentScope;
		boolean aux2 = true;
		
		
			while(aux2) {
				for(DecafSymbol a : listaDeSimboloGeral) {
					if(a.getName().equals(nomeVar) && a.getScopo().getName().equals(aux.getName())) {
						retorno = a;
						break;
					}	
				}
				if(aux.getName().equals(globals.getName())) {
					aux2 = false;
				}
				aux = aux.getEnclosingScope();
			}

		
			if(retorno == null) {
				try {
					throw new VariavelNaoInstanciadaException(nomeVar);
				} catch (VariavelNaoInstanciadaException e) {
					// TODO Auto-generated catch block
					System.out.println(e.toString());
					System.exit(0);
				}
			}
		
		return retorno;
		
	}

	@Override

	public void enterProgram(DecafParser.ProgramContext ctx) {
		globals = new GlobalScope(null);
		pushScope(globals);

	}

	@Override

	public void exitProgram(DecafParser.ProgramContext ctx) {

		System.out.println(globals);

		// Verifica se tem metodo main
		boolean aux = true;
		for (int i = 0; i < currentScope.getAllSymbols().size(); i++) {

			if (currentScope.getAllSymbols().get(i).getName().equals("main")) {
				aux = false;
				break;
			}
		}

		if (aux) {

			try {
				throw new MainNaoEncontradoException();
			} catch (MainNaoEncontradoException e) {
				// TODO Auto-generated catch block
				System.out.println(e.toString());
				System.exit(0);
			}
		}
		// verifica se tem metodo main


	}

	@Override
	public void enterMethod_decl(DecafParser.Method_declContext ctx) {
		
		

		String nome = ctx.ID().getText();
		Type tipo;
		if (ctx.start.getType() == 23) {
			tipo = this.getType(ctx.start.getType());
		} else {
			tipo = this.getType(ctx.TIPO().getText());
		}
		Scope scopo = currentScope;

		FunctionSymbol function = new FunctionSymbol(nome);
		function.setType(tipo);
		currentScope.define(function);

		listaDeSimboloGeral.add(new DecafSymbol(nome, tipo, scopo));

		saveScope(ctx, function);
		listaMetodos.add(ctx);// ?????????
		

		pushScope(function);

		
		ultMethDecl = nome;
		
		
	}

	@Override

	public void exitMethod_decl(DecafParser.Method_declContext ctx) {

		
		popScope();
	}

	@Override
	public void enterParametroMeth_decl(ParametroMeth_declContext ctx) {

		for (int i = 0; i < ctx.ID().size(); i++) {
			String nome = ctx.ID().get(i).getText();
			Type tipo = this.getType(ctx.TIPO().get(i).getText());
			Scope scopo = currentScope;
			VariableSymbol var = new VariableSymbol(nome);
			currentScope.define(var);

			listaDeSimboloGeral.add(new DecafSymbol(nome, tipo, scopo));

		}

	}

	@Override
	public void exitParametroMeth_decl(ParametroMeth_declContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override

	public void enterBlock(DecafParser.BlockContext ctx) {
		LocalScope l = new LocalScope(currentScope);
		saveScope(ctx, currentScope);

		 pushScope(l);
	}

	@Override

	public void exitBlock(DecafParser.BlockContext ctx) {
		 popScope();
	}

	@Override

	public void enterField_decl(DecafParser.Field_declContext ctx) {

		// Verifica se e um array e se e valido
		if (ctx.ECOLC().size() > 0 && ctx.DCOLC().size() > 0) {// se maior que zero e um array
			// verifica se tamanho p array e valido
			if ((Integer.parseInt(ctx.INTLITERAL(0).getText()) < 1)) {
				try {
					throw new ArrayNaoValidoException(ctx.ID(0).getText());
				} catch (ArrayNaoValidoException e) {
					// TODO Auto-generated catch block
					System.out.println(e.toString());
					System.exit(0);
				}
			}
		}

		String nome = ctx.ID().get(0).getText();
		Type tipo = null;
		if (ctx.start.getType() == 22) {
			tipo = this.getType(ctx.start.getText());
		} else {
			// Criar exp
			System.out.println("Tipo var invalido");
		}
		Scope scopo = currentScope;

		VariableSymbol var = new VariableSymbol(nome);
		var.setType(tipo);

		currentScope.define(var); // Define symbol in current scope*/

		listaDeSimboloGeral.add(new DecafSymbol(nome, tipo, scopo));

	}

	@Override

	public void exitField_decl(DecafParser.Field_declContext ctx) {
	}

	@Override
	public void enterVar_decl(Var_declContext ctx) {

		String nome = ctx.ID().get(0).getText();
		Type tipo = null;
		if (ctx.start.getType() == 22) {
			tipo = this.getType(ctx.start.getText());
		} else {
			// Criar exp
		}
		
		
		Scope scopo = currentScope;

		VariableSymbol var = new VariableSymbol(nome);
		var.setType(tipo);

		currentScope.define(var); // Define symbol in current scope*/

		listaDeSimboloGeral.add(new DecafSymbol(nome, tipo, scopo));
	}

	@Override
	public void exitVar_decl(Var_declContext ctx) {
	}

	@Override
	public void enterStatement(StatementContext ctx) {
		
		//verifica se e t1 location assing_op expr PONTVIRGULA
		if(ctx.location() != null) {
			trataLocation(ctx);
		}
		
		//method_call PONTVIRGULA
		if(ctx.method_call() != null) {
			trataMethCall(ctx);
		}
		
		//IF EPAR expr DPAR block (ELSE block)?
		if(ctx.IF() != null) {
			trataIF(ctx);
		}
		
		//FOR ID ATRIB expr VIRGULA expr block
		if(ctx.FOR() != null) {
			trataFOR(ctx);
		}
		
		//RETURN (expr)? PONTVIRGULA
		if(ctx.RETURN() != null) {
			trataRETURN(ctx, ultMethDecl);
		}


	}

	private void trataRETURN(StatementContext ctx, String nomeMeth) {
		// TODO Auto-generated method stub
		//se for void nao deve ter retorno
		for(DecafSymbol a : listaDeSimboloGeral) {
			
			if(a.getName().equals(nomeMeth)) {
				if(a.getTipo().getName().equals("void")) {
					try {
						throw new RetornoMetodoException(a.getName());
					} catch (RetornoMetodoException e) {
						System.out.println(e.toString());
						System.exit(0);
					}
				}
			}
		}
		
		boolean aux = true;
		
		DecafSymbol func = retornaSimbolo(nomeMeth);
		//se for algum tipo verifica se retorno e do tipo instanciado p metodo
		if(ctx.expr(0).location() != null) {
			//tem que retornar simbolo
			DecafSymbol var = retornaSimbolo(ctx.expr(0).location().ID().getText());
			
			
			
			if(!var.getTipo().getName().equals(func.getTipo().getName())) {
				try {
					throw new RetornoMetodoException(func.getName());
				} catch (RetornoMetodoException e) {
					System.out.println(e.toString());
					System.exit(0);
				}
			}
			aux = !aux;
		}
		
		if(ctx.expr(0).MENOS() != null) {
			if(!this.getType(ctx.expr(0).expr(0).start.getType()).getName().equals(func.getTipo().getName())) {
				try {
					throw new RetornoMetodoException(func.getName());
				} catch (RetornoMetodoException e) {
					System.out.println(e.toString());
					System.exit(0);
				}
			}
			
			aux = !aux;
		}
		
		if(aux) {
			if(!this.getType(ctx.expr(0).start.getType()).getName().equals(func.getTipo().getName())) {
				try {
					throw new RetornoMetodoException(func.getName());
				} catch (RetornoMetodoException e) {
					System.out.println(e.toString());
					System.exit(0);
				}
			}
		}
		
	}

	private void trataFOR(StatementContext ctx) {
		// TODO Auto-generated method stub
		
	}

	private void trataIF(StatementContext ctx) {
		// TODO Auto-generated method stub
		
	}

	private void trataMethCall(StatementContext ctx) {
		// TODO Auto-generated method stub
		
	}

	private void trataLocation(StatementContext ctx) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void exitStatement(StatementContext ctx) {
	}

	@Override
	public void enterAssing_op(Assing_opContext ctx) {
	}

	@Override
	public void exitAssing_op(Assing_opContext ctx) {
	}

	@Override
	public void enterMethod_call(Method_callContext ctx) {
		
		if(ctx.CALLOUT() != null) {
			
		}else {
			
			
			
			// Verifica se cumpre tds os argumento do metodo
			for (DecafSymbol i : listaDeSimboloGeral) {
				if (i.getScopo().getName().equals(ctx.ID().getText())) {
					if (i.getScopo().getNumberOfSymbols() > ctx.expr().size()) {
						try {
							throw new NumeroDeArgumentosMetodoInvalidoException(i.getScopo().getName(), "menor");
						} catch (NumeroDeArgumentosMetodoInvalidoException e) {
							// TODO Auto-generated catch block
							System.out.println(e.toString());
							System.exit(0);
						}
					} else if (i.getScopo().getNumberOfSymbols() < ctx.expr().size()) {
						try {
							throw new NumeroDeArgumentosMetodoInvalidoException(i.getScopo().getName(), "maior");
						} catch (NumeroDeArgumentosMetodoInvalidoException e) {
							// TODO Auto-generated catch block
							System.out.println(e.toString());
							System.exit(0);
						}
					}

				}
			}

			// Verifica tipos dos argumentos
			System.out.println();
			for (DecafSymbol i : listaDeSimboloGeral) {
				if (i.getScopo().getName().equals(ctx.ID().getText())) {
					for (int x = 0; x < i.getScopo().getSymbols().size(); x++) {
						for (DecafSymbol a : listaDeSimboloGeral) {
							if (i.getScopo().getSymbols().get(x).getName().equals(a.getName())) {
								
								if(ctx.expr(x).location() != null) {
									DecafSymbol v = retornaSimbolo(ctx.expr(x).location().ID().getText());
									
									if (!a.getTipo().getName()
											.equals(v.getTipo().getName())) {

										try {
											throw new TipoDeArgumentosMetodoInvalidoException(
													v.getTipo().getName(),
													i.getScopo().getSymbols().get(x).getName(), a.getTipo().getName(),
													i.getScopo().getName());
										} catch (TipoDeArgumentosMetodoInvalidoException e) {
											System.out.println(e.toString());
											System.exit(0);
										}

									}
								}else {
									if (!a.getTipo().getName()
											.equals(this.getType(ctx.expr(x).getStart().getType()).getName())) {

										try {
											throw new TipoDeArgumentosMetodoInvalidoException(
													this.getType(ctx.expr(x).getStart().getType()).getName(),
													i.getScopo().getSymbols().get(x).getName(), a.getTipo().getName(),
													i.getScopo().getName());
										} catch (TipoDeArgumentosMetodoInvalidoException e) {
											System.out.println(e.toString());
											System.exit(0);
										}

									}
								}
								
								break;
							}
						}
					}
					break;
				}
			}
		}

		

	}

	@Override
	public void exitMethod_call(Method_callContext ctx) {
	}

	@Override
	public void enterLocation(LocationContext ctx) {
		
		//verifica se variavel existe
		retornaSimbolo(ctx.ID().getText());
		


		if (!(ctx.ECOLC() == null) && !(ctx.DCOLC() == null)) {// se maior que zero e um array
			// verifica se tamanho p array e valido
			
			if(ctx.expr().start.getType() == 28) {
				DecafSymbol aux = retornaSimbolo(ctx.expr().getText());
				if (!aux.getTipo().getName().equals("int")) {
					try {
						throw new ArrayNaoValidoException(ctx.ID().getText());
					} catch (ArrayNaoValidoException e) {
						// TODO Auto-generated catch block
						System.out.println(e.toString());
						System.exit(0);
					}
				}
			}else {
				if ((ctx.expr().start.getType() != DecafParser.INTLITERAL)
						|| (Integer.parseInt(ctx.expr().start.getText()) < 1)) {
					try {
						throw new ArrayNaoValidoException(ctx.ID().getText());
					} catch (ArrayNaoValidoException e) {
						// TODO Auto-generated catch block
						System.out.println(e.toString());
						System.exit(0);
					}
				}
			}
			
		}

	}

	@Override
	public void exitLocation(LocationContext ctx) {
	}

	@Override
	public void enterExpr(ExprContext ctx) {
	}

	@Override
	public void exitExpr(ExprContext ctx) {
	}

	
	
	/**
	 * 
	 * Método que atuliza o escopo para o atual e imprime o valor
	 *
	 * 
	 * 
	 * @param s
	 * 
	 */

	private void pushScope(Scope s) {

		currentScope = s;

		System.out.println("entering: " + currentScope.getName() + ":" + s);

	}

	/**
	 * 
	 * Método que cria um novo escopo no contexto fornecido
	 *
	 * 
	 * 
	 * @param ctx
	 * 
	 * @param s
	 * 
	 */

	void saveScope(ParserRuleContext ctx, Scope s) {

		scopes.put(ctx, s);

	}

	/**
	 * 
	 * Muda para o contexto superior e atualia o escopo
	 * 
	 */

	private void popScope() {

		System.out.println("leaving: " + currentScope.getName() + ":" + currentScope);

		currentScope = currentScope.getEnclosingScope();

	}

	public static void error(Token t, String msg) {

		System.err.printf("line %d:%d %s\n", t.getLine(), t.getCharPositionInLine(),

				msg);

	}

	/**
	 * 
	 * Valida tipos encontrados na linguagem para tipos reais
	 *
	 * 
	 * 
	 * @param tokenType
	 * 
	 * @return
	 * 
	 */

	public Tipos getType(int tokenType) {

		switch (tokenType) {

		case DecafParser.VOID:

			return new DecafSymbol.Tipos("void");

		case DecafParser.INTLITERAL:
			return new DecafSymbol.Tipos("int");
		case DecafParser.BOOLEAN:
			return new DecafSymbol.Tipos("boolean");
		case DecafParser.STRING:
			return new DecafSymbol.Tipos("string");
		case DecafParser.ID:
			return new DecafSymbol.Tipos("var");

		}

		return new DecafSymbol.Tipos("invalido");

	}

	public Tipos getType(String tokenType) {

		switch (tokenType) {

		case "void":
			return new DecafSymbol.Tipos("void");

		case "int":
			return new DecafSymbol.Tipos("int");
		case "boolean":
			return new DecafSymbol.Tipos("boolean");
		case "string":
			return new DecafSymbol.Tipos("string");

		}

		return new DecafSymbol.Tipos("invalido");

	}

}