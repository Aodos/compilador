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

	ParseTreeProperty<Scope> scopes = new ParseTreeProperty<Scope>();

	GlobalScope globals;

	Scope currentScope; // define symbols in this scope

	List<Method_declContext> listaMetodos = new ArrayList<DecafParser.Method_declContext>();
	List<DecafSymbol> listaDeSimboloGeral = new ArrayList<DecafSymbol>();

	@Override

	public void enterProgram(DecafParser.ProgramContext ctx) {
		System.out.println("---------------------Inicio enterProgram----------------------");
		globals = new GlobalScope(null);
		pushScope(globals);
		System.out.println();
		System.out.println("---------------------Fim enterProgram-------------------------");
		System.out.println();
		System.out.println();

	}

	@Override

	public void exitProgram(DecafParser.ProgramContext ctx) {
		System.out.println("---------------------Inicio exitProgram------------------");

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

		for (DecafSymbol a : listaDeSimboloGeral) {
			System.out.println(a.getName());
			System.out.println(a.getScopo());
		}

		System.out.println();
		System.out.println("---------------------Fim exitProgram------------------");
		System.out.println();
		System.out.println();

	}

	@Override
	public void enterMethod_decl(DecafParser.Method_declContext ctx) {
		System.out.println("---------------------Inicio enterMethod_decl------------------");

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

		
		// verifica se metodo tem que retornar, se sim, verifica se tem return, se nao
		// tiver = CompiladorArthurException
		if (!tipo.getName().equals("void")) {
			if (!(ctx.block().statement().size() < 1)) {
				if (ctx.block().statement().get(ctx.block().statement().size() - 1 ).RETURN() == null) {
					try {
						throw new RetornoMetodoException(nome, " esta sem retorno");
					} catch (RetornoMetodoException e) {
						// TODO Auto-generated catch block
						System.out.println(e.toString());
						System.exit(0);
					}
				}
			} else {
				try {
					throw new RetornoMetodoException(nome, " esta sem retorno");
				} catch (RetornoMetodoException e) {
					// TODO Auto-generated catch block
					System.out.println(e.toString());
					System.exit(0);
				}
			}
		} else {
			if (!(ctx.block().statement().size() < 1)) {
				if (!(ctx.block().statement().get(ctx.block().statement().size() - 1 ).RETURN() == null)) {
					try {
						throw new RetornoMetodoException(nome, " do tipo void, nao deve ter retorno");
					} catch (RetornoMetodoException e) {
						// TODO Auto-generated catch block
						System.out.println(e.toString());
						System.exit(0);
					}
				}
			}
		}

		// verifica tipo do retorno se for diferente de void
		System.out.println(tipo.getName());
		if (!tipo.getName().equals("void")) {
			
			if(ctx.block().statement().size() > 1) {
				if (!tipo.getName().equals(this.getType(ctx.block().statement(ctx.block().statement().size() - 1).expr(ctx.block().statement(ctx.block().statement().size() - 1).expr().size() - 1).expr(0).start.getType()).getName())) {
					try {
						throw new RetornoMetodoException(nome, " esta com retorno de tipo diferente do instanciado");
					} catch (RetornoMetodoException e) {
						// TODO Auto-generated catch block
						System.out.println(e.toString());
						System.exit(0);
					}
				}
			}else if(ctx.block().statement().size() == 1) {
				if (!tipo.getName().equals(this.getType(ctx.block().statement(0).expr(0).start.getType()).getName())) {
					try {
						throw new RetornoMetodoException(nome, " esta com retorno de tipo diferente do instanciado");
					} catch (RetornoMetodoException e) {
						// TODO Auto-generated catch block
						System.out.println(e.toString());
						System.exit(0);
					}
				}
			}
			
			
			
		}

		System.out.println();

		System.out.println();
		System.out.println("---------------------Fim enterMethod_decl---------------------");
		System.out.println();
		System.out.println();
	}

	@Override

	public void exitMethod_decl(DecafParser.Method_declContext ctx) {
		System.out.println("---------------------Inicio exitMethod_decl------------------");

		
		popScope();
		System.out.println();
		System.out.println("---------------------Fim exitMethod_decl---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void enterParametroMeth_decl(ParametroMeth_declContext ctx) {
		System.out.println("---------------------Inicio enterParametroMeth_decl------------------");

		for (int i = 0; i < ctx.ID().size(); i++) {
			String nome = ctx.ID().get(i).getText();
			Type tipo = this.getType(ctx.TIPO().get(i).getText());
			Scope scopo = currentScope;
			VariableSymbol var = new VariableSymbol(nome);
			currentScope.define(var);

			listaDeSimboloGeral.add(new DecafSymbol(nome, tipo, scopo));

		}

		System.out.println();
		System.out.println("---------------------Fim enterParametroMeth_decl---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void exitParametroMeth_decl(ParametroMeth_declContext ctx) {
		// TODO Auto-generated method stub
		System.out.println("---------------------Inicio exitParametroMeth_decl------------------");

		System.out.println();
		System.out.println("---------------------Fim exitParametroMeth_decl---------------------");
		System.out.println();
		System.out.println();
	}

	@Override

	public void enterBlock(DecafParser.BlockContext ctx) {
		System.out.println("---------------------Inicio enterBlock------------------");
		LocalScope l = new LocalScope(currentScope);
		saveScope(ctx, currentScope);

		// pushScope(l);
		System.out.println();
		System.out.println("---------------------Fim enterBlock--------------------");
		System.out.println();
		System.out.println();
	}

	@Override

	public void exitBlock(DecafParser.BlockContext ctx) {
		System.out.println("---------------------Inicio exitBlock------------------");
		// popScope();
		System.out.println();
		System.out.println("---------------------Fim exitBlock---------------------");
		System.out.println();
		System.out.println();
	}

	@Override

	public void enterField_decl(DecafParser.Field_declContext ctx) {
		System.out.println("---------------------Inicio enterField_decl---------------------");

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

		System.out.println();
		System.out.println("---------------------Fim enterField_decl------------------");
		System.out.println();
		System.out.println();
	}

	@Override

	public void exitField_decl(DecafParser.Field_declContext ctx) {
		System.out.println("---------------------Inicio exitField_decl---------------------");
		String name = ctx.ID().get(0).getSymbol().getText();

		System.out.println();
		System.out.println("---------------------Fim exitField_decl---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void enterVar_decl(Var_declContext ctx) {
		System.out.println("---------------------Inicio enterVar_decl------------------");

		System.out.println();

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
		System.out.println();
		System.out.println("---------------------Fim enterVar_decl---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void exitVar_decl(Var_declContext ctx) {
		System.out.println("---------------------Inicio exitVar_decl------------------");

		System.out.println();
		System.out.println("---------------------Fim exitVar_decl---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void enterStatement(StatementContext ctx) {
		System.out.println("---------------------Inicio enterStatement------------------");

		/*boolean aux = true;
		boolean aux1 = false;
		Scope escopoAUX = null;

		
		// realiza se statemant e var = exp;
		if (ctx.location() != null) {
			System.out.println("ERRO TA AQUI 1");
			// vefica se tem variavel no scopo atual
			for (Symbol s : currentScope.getAllSymbols()) {
				if (s.getName().equals(ctx.location().start.getText())) {
					aux = false;
					aux1 = true;
					escopoAUX = currentScope;
					System.out.println(ctx.location().start.getText() + " Esta no escopo atual");
					break;
				}
			}
			// verifica se esta no global
			if (aux) {
				for (Symbol sG : globals.getAllSymbols()) {
					if (sG.getName().equals(ctx.location().start.getText())) {
						aux1 = true;
						escopoAUX = globals;
						System.out.println(ctx.location().start.getText() + " Esta no escopo global");
						break;
					}
				}
			}
			if (aux1) {
				for (DecafSymbol a : listaDeSimboloGeral) {
					if (a.getName().equals(ctx.location().start.getText()) && a.getScopo().equals(escopoAUX)) {

						if (ctx.expr(0).location() != null) {

							if (!a.getTipo().getName().equals(this.getType(ctx.expr(0).start.getType()).getName())) {
								// Criar exp
								System.out.println("NAO E DO MSM TIPO");
								System.exit(0);
							}
						} else {
							
							System.out.println("ERRO TA AQUI 2");
							if (ctx.expr(0).EXCAMACAO() != null) {
								DecafSymbol tipoOP = new DecafSymbol(ctx.expr(0).expr(0).start.getText(), null, null);

								if (ctx.expr(0).expr(0).start.getType() == 28) {
									// se OP for id

									boolean auxOP = true;
									for (Symbol s : currentScope.getAllSymbols()) {
										if (s.getName().equals(ctx.expr(0).expr(0).start.getText())) {

											auxOP = false;

											for (DecafSymbol b : listaDeSimboloGeral) {
												if (b.getName().equals(ctx.expr(0).expr(0).start.getText())
														&& b.getScopo().equals(currentScope)) {
													tipoOP.setTipo(b.getTipo());
													break;
												}
											}

											break;
										}
									}
									// verifica se esta no global
									if (auxOP) {
										for (Symbol sG : globals.getAllSymbols()) {
											if (sG.getName().equals(ctx.expr(0).expr(0).start.getText())) {
												for (DecafSymbol b : listaDeSimboloGeral) {
													if (b.getName().equals(ctx.expr(0).expr(0).start.getText())
															&& b.getScopo().equals(globals)) {
														tipoOP.setTipo(b.getTipo());
														break;
													}
												}
												break;
											}
										}
									}
								}

								if (tipoOP.getTipo() == null) {
									if (ctx.expr(0).expr(0).start.getType() != 24) {
										// Criar exp
										System.out.println("operand of ! must be boolean");
										System.exit(0);
									}
								} else {
									if (ctx.expr(0).expr(0).start.getType() != 24
											&& !tipoOP.getTipo().getName().equals("boolean")) {
										// Criar exp
										System.out.println("operand of ! must be boolean");
										System.exit(0);
									}
								}

							} else {
								System.out.println("ERRO TA AQUI 3");
								
								
								if(ctx.assing_op() != null && ctx.assing_op().start.getType() != 10) {
									
									if(ctx.expr(0).start.getType() != 29) {
										// Criar exp
										System.out.println("lhs and rhs of += must be int");
										System.exit(0);
									}
								}else {
									System.out.println("ERRO TA AQUI 4");
									System.out.println(ctx.expr(0).getText());
									
									
									if(ctx.expr(0).expr(0) != null) {
										if (!a.getTipo().getName().equals(retornaTipoExpr(ctx.expr(0)).getName())) {
											// Criar exp
											System.out.println("EXP NAO E DO MSM TIPO DA VARIAVEL");
											System.exit(0);
										}
									}
									
									
									
									
								}
								

							}
						}
					}
				}
			}
		}

		// verifica se statement e if
		if (ctx.IF() != null) {
			aux = true;
			aux1 = false;
			// System.out.println(this.getType(ctx.expr(0).location().ID().getText()).getName());
			// Verifica se argumento do if e um valor de boolean ou var
			boolean auxIF = false;
			if (ctx.expr(0).location() != null) {
				auxIF = true;
			}
			// se for variavel, verifica se e do tipo boolean
			if (auxIF) { // primeiro verifica qual escopo esta a variavel
				for (Symbol s : currentScope.getAllSymbols()) {
					if (s.getName().equals(ctx.expr(0).location().ID().getText())) {
						aux = false;
						aux1 = true;
						escopoAUX = currentScope;
						System.out.println(ctx.expr(0).location().ID().getText() + " Esta no escopo atual");
						break;
					}
				}
				// verifica se esta no global
				if (aux) {
					for (Symbol sG : globals.getAllSymbols()) {
						if (sG.getName().equals(ctx.expr(0).location().ID().getText())) {
							aux1 = true;
							escopoAUX = globals;
							System.out.println(ctx.expr(0).location().ID().getText() + " Esta no escopo global");
							break;
						}
					}
				}

				// Agora verifica se e do mesmo tipo
				if (aux1) {
					for (DecafSymbol a : listaDeSimboloGeral) {
						if (a.getName().equals(ctx.expr(0).location().ID().getText())
								&& a.getScopo().equals(escopoAUX)) {
							if (a.getTipo().getName() != "boolean") {
								// Criar exp
								System.out.println("variavel nao e boolean");
								System.exit(0);
							}
						}
					}
				}
			}

		}

		// verifica se e um for
		if (ctx.FOR() != null) {
			if (ctx.expr(0).start.getType() != 29) {
				// Criar exp
				System.out.println("Primeiro argumento do for nao e inteiro");
				System.exit(0);
			}
		}
		*/

		System.out.println();
		System.out.println("---------------------Fim enterStatement---------------------");
		System.out.println();
		System.out.println();
	}

	private Type retornaTipoExpr(ExprContext expr) {

		DecafSymbol exp1 = null;
		DecafSymbol exp2 = null;
		Type tipoExp = null;

		if (expr.OP_RELACIO() != null || expr.OP_EQUID() != null || expr.OP_COND() != null) {
			tipoExp = new Tipos("boolean");
		}
		if (expr.OP_ARITH() != null || expr.MAIS() != null || expr.MENOS() != null) {
			tipoExp = new Tipos("int");
		}

		if (expr.expr(0).location() != null) {
			exp1 = new DecafSymbol(expr.expr(0).location().getText(),
					this.getType(expr.expr(0).location().start.getType()), null);
		} else {
			exp1 = new DecafSymbol(expr.expr(0).getText(), this.getType(expr.expr(0).start.getType()), null);
		}
		if (expr.expr(1).location() != null) {
			exp2 = new DecafSymbol(expr.expr(1).location().getText(),
					this.getType(expr.expr(1).location().start.getType()), null);
		} else {
			exp2 = new DecafSymbol(expr.expr(1).getText(), this.getType(expr.expr(1).start.getType()), null);
		}

		boolean aux = true;

		if (exp1.getTipo().getName() == "var") {
			for (Symbol s : currentScope.getAllSymbols()) {
				if (s.getName().equals(exp1.getName())) {
					aux = false;

					for (DecafSymbol a : listaDeSimboloGeral) {
						if (a.getName().equals(exp1.getName()) && a.getScopo().equals(currentScope)) {
							exp1.setTipo(a.getTipo());
							break;
						}
					}
					break;
				}
			}
			// verifica se esta no global
			if (aux) {
				for (Symbol sG : globals.getAllSymbols()) {
					if (sG.getName().equals(exp1.getName())) {
						for (DecafSymbol a : listaDeSimboloGeral) {
							if (a.getName().equals(exp1.getName()) && a.getScopo().equals(globals)) {
								exp1.setTipo(a.getTipo());
								break;
							}
						}
						break;
					}
				}
			}
		}

		if (exp2.getTipo().getName() == "var") {
			for (Symbol s : currentScope.getAllSymbols()) {
				if (s.getName().equals(exp2.getName())) {
					aux = false;

					for (DecafSymbol a : listaDeSimboloGeral) {
						if (a.getName().equals(exp2.getName()) && a.getScopo().equals(currentScope)) {
							exp1.setTipo(a.getTipo());
							break;
						}
					}
					break;
				}
			}
			// verifica se esta no global
			if (aux) {
				for (Symbol sG : globals.getAllSymbols()) {
					if (sG.getName().equals(exp2.getName())) {
						for (DecafSymbol a : listaDeSimboloGeral) {
							if (a.getName().equals(exp2.getName()) && a.getScopo().equals(globals)) {
								exp2.setTipo(a.getTipo());
								break;
							}
						}
						break;
					}
				}
			}
		}

		if (expr.OP_RELACIO() != null || expr.OP_EQUID() != null) {
			if (!exp1.getTipo().getName().equals("int")) {
				// Criar exp
				System.out.println("tipo errado para expressao booleana");
				System.exit(0);

			}
			if (!exp2.getTipo().getName().equals("int")) {
				// Criar exp
				System.out.println("tipo errado para expressao booleana");
				System.exit(0);
			}
		}

		/*
		 * System.out.println("nome exp1: " + exp1.getName());
		 * System.out.println("nome exp1: " + exp1.getTipo().getName());
		 * 
		 * System.out.println("nome exp2: " + exp2.getName());
		 * System.out.println("nome exp2: " + exp2.getTipo().getName());
		 */
		return tipoExp;
	}

	@Override
	public void exitStatement(StatementContext ctx) {
		System.out.println("---------------------Inicio exitStatement------------------");
		System.out.println();
		System.out.println("---------------------Fim exitStatement---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void enterAssing_op(Assing_opContext ctx) {
		System.out.println("---------------------Inicio enterAssing_op------------------");
		System.out.println();
		System.out.println("---------------------Fim enterAssing_op---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void exitAssing_op(Assing_opContext ctx) {
		System.out.println("---------------------Inicio exitAssing_op------------------");
		System.out.println();
		System.out.println("---------------------Fim exitAssing_op---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void enterMethod_call(Method_callContext ctx) {
		System.out.println("---------------------Inicio enterMethod_call------------------");

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
							break;
						}
					}
				}
				break;
			}
		}

		System.out.println();

		System.out.println();
		System.out.println("---------------------Fim enterMethod_call---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void exitMethod_call(Method_callContext ctx) {
		System.out.println("---------------------Inicio exitMethod_call------------------");
		System.out.println();
		System.out.println("---------------------Fim exitMethod_call---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void enterLocation(LocationContext ctx) {
		System.out.println("---------------------Inicio enterLocation------------------");
/*
		// verificacao de variavel valida
		if (currentScope.getSymbol(ctx.ID().getText()) == null
				&& currentScope.getEnclosingScope().getSymbol(ctx.ID().getText()) == null) {
			try {
				throw new VariavelNaoInstanciadaException(ctx.ID().getText());
			} catch (VariavelNaoInstanciadaException e) {
				// TODO Auto-generated catch block
				System.out.println(e.toString());
				System.exit(0);
			}

		}

		if (!(ctx.ECOLC() == null) && !(ctx.DCOLC() == null)) {// se maior que zero e um array
			// verifica se tamanho p array e valido
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
		}*/

		System.out.println();
		System.out.println("---------------------Fim enterLocation---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void exitLocation(LocationContext ctx) {
		System.out.println("---------------------Inicio exitLocation------------------");
		System.out.println();
		System.out.println("---------------------Fim exitLocation---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void enterExpr(ExprContext ctx) {
		System.out.println("---------------------Inicio enterExpr------------------");

		System.out.println(ctx.start.getType());

		System.out.println();
		System.out.println("---------------------Fim enterExpr---------------------");
		System.out.println();
		System.out.println();
	}

	@Override
	public void exitExpr(ExprContext ctx) {
		System.out.println("---------------------Inicio exitExpr------------------");

		System.out.println();
		System.out.println("---------------------Fim exitExpr---------------------");
		System.out.println();
		System.out.println();
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