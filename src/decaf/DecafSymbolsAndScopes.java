package decaf;

import java.util.ArrayList;
import java.util.List;

import org.antlr.symtab.FunctionSymbol;

import org.antlr.symtab.GlobalScope;

import org.antlr.symtab.LocalScope;

import org.antlr.symtab.Scope;

import org.antlr.symtab.VariableSymbol;

import org.antlr.symtab.Symbol;

import org.antlr.v4.runtime.misc.NotNull;

import org.antlr.v4.runtime.ParserRuleContext;

import org.antlr.v4.runtime.Token;

import org.antlr.v4.runtime.tree.ParseTreeProperty;

import decaf.CompiladorArthurException.MainNaoEncontradoException;
import decaf.CompiladorArthurException.VariavelNaoInstanciadaException;
import decaf.CompiladorArthurException.TamanhoNaoValidoException;
import decaf.CompiladorArthurException.NumeroDeArgumentosMetodoInvalidoException;

import decaf.DecafParser.Assing_opContext;
import decaf.DecafParser.ExprContext;
import decaf.DecafParser.Field_declContext;
import decaf.DecafParser.LocationContext;
import decaf.DecafParser.Method_callContext;
import decaf.DecafParser.Method_declContext;
import decaf.DecafParser.StatementContext;
import decaf.DecafParser.Var_declContext;

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

	
	
	@Override

	public void enterProgram(DecafParser.ProgramContext ctx) {
		System.out.println("---------------------Inicio enterProgram----------------------" );
		globals = new GlobalScope(null);
		pushScope(globals);
		System.out.println();
		System.out.println("---------------------Fim enterProgram-------------------------" );
		System.out.println();
		System.out.println();

	}

	@Override

	public void exitProgram(DecafParser.ProgramContext ctx) {
		System.out.println("---------------------Inicio exitProgram------------------" );
		
		System.out.println(globals);
		
		
		//Verifica se tem metodo main
		boolean aux = true;
		for(int i = 0; i < currentScope.getAllSymbols().size(); i++) {
			
			if(currentScope.getAllSymbols().get(i).getName().equals("main")) {
				aux = false;
				break;
			}
		}
		
		if(aux){
			
			try {
				throw new MainNaoEncontradoException();
			} catch (MainNaoEncontradoException e) {
				// TODO Auto-generated catch block
				System.out.println(e.toString());
				System.exit(0);
			}
		}
		//verifica se tem metodo main
		
		System.out.println();
		System.out.println("---------------------Fim exitProgram------------------" );
		System.out.println();
		System.out.println();

	}

	@Override

	public void enterMethod_decl(DecafParser.Method_declContext ctx) {
		System.out.println("---------------------Inicio enterMethod_decl------------------" );
		
		// int typeTokenType = ctx.start.getType();

		// DecafSymbol.Type type = this.getType(typeTokenType);

		// push new scope by making new one that points to enclosing scope
		FunctionSymbol function = new FunctionSymbol(ctx.ID().get(0).getText());
		for(int i = 1; i < ctx.ID().size(); i++) {
			function.define(new VariableSymbol(ctx.ID(i).getText()));
		}
		

		// function.setType(type); // Set symbol type

		currentScope.define(function); // Define function in current scope
		
		saveScope(ctx, function);
		
		//salva metodos
		listaMetodos.add(ctx);
		
		System.out.println();
		

		pushScope(function);
		System.out.println();
		System.out.println("---------------------Fim enterMethod_decl---------------------" );
		System.out.println();
		System.out.println();
	}

	@Override

	public void exitMethod_decl(DecafParser.Method_declContext ctx) {
		System.out.println("---------------------Inicio exitMethod_decl------------------" );
		
		
		
		popScope();
		System.out.println();
		System.out.println("---------------------Fim exitMethod_decl---------------------" );
		System.out.println();
		System.out.println();
		}

	@Override

	public void enterBlock(DecafParser.BlockContext ctx) {
		System.out.println("---------------------Inicio enterBlock------------------" );
		LocalScope l = new LocalScope(currentScope);
		saveScope(ctx, currentScope);

		// pushScope(l);
		System.out.println();
		System.out.println("---------------------Fim enterBlock--------------------" );
		System.out.println();
		System.out.println();
	}

	@Override

	public void exitBlock(DecafParser.BlockContext ctx) {
		System.out.println("---------------------Inicio exitBlock------------------" );
		//popScope();
		System.out.println();
		System.out.println("---------------------Fim exitBlock---------------------" );
		System.out.println();
		System.out.println();
	}

	@Override

	public void enterField_decl(DecafParser.Field_declContext ctx) {
		System.out.println("---------------------Inicio enterField_decl---------------------" );
		
		
		//Verifica se e um array
		if(ctx.ECOLC().size() > 0 && ctx.DCOLC().size() > 0) {//se maior que zero e um array
			//verifica se tamanho p array e valido
			if(Integer.parseInt(ctx.INTLITERAL(0).getText()) == 0) {
				try {
					throw new TamanhoNaoValidoException(ctx.ID(0).getText());
				} catch (TamanhoNaoValidoException e) {
					// TODO Auto-generated catch block
					System.out.println(e.toString());
					System.exit(0);
				}
			}
		}
		
		int typeTokenType = ctx.start.getType();

		VariableSymbol var = new VariableSymbol(ctx.ID().get(0).getText());

		// DecafSymbol.Type type = this.getType(typeTokenType);

		// var.setType(type);
		
		System.out.println(ctx.getText());
		System.out.println(var);

		currentScope.define(var); // Define symbol in current scope
		
		//defineVar(ctx.ID().get(0), ctx.ID().get(0).getSymbol());
		System.out.println();
		System.out.println("---------------------Fim enterField_decl------------------" );
		System.out.println();
		System.out.println();
	}

	@Override

	public void exitField_decl(DecafParser.Field_declContext ctx) {
		System.out.println("---------------------Inicio exitField_decl---------------------" );
		String name = ctx.ID().get(0).getSymbol().getText();

	/*	Symbol var = currentScope.resolve(name);

		if (var == null) {

			this.error(ctx.ID().get(0).getSymbol(), "no such variable: " + name);

		}

		if (var instanceof FunctionSymbol) {

			this.error(ctx.ID().get(0).getSymbol(), name + " is not a variable");

		}*/
		System.out.println();
		System.out.println("---------------------Fim exitField_decl---------------------" );
		System.out.println();
		System.out.println();
	}
	
	@Override
	public void enterVar_decl(Var_declContext ctx) {
		System.out.println("---------------------Inicio enterVar_decl------------------" );

		int typeTokenType = ctx.start.getType();
		
		VariableSymbol var = new VariableSymbol(ctx.ID(0).getText());

		// DecafSymbol.Type type = this.getType(typeTokenType);

		// var.setType(type);

		currentScope.define(var); // Define symbol in current scope
		System.out.println();
		System.out.println("---------------------Fim enterVar_decl---------------------" );
		System.out.println();
		System.out.println();
	}
	
	@Override
	public void exitVar_decl(Var_declContext ctx) {
		System.out.println("---------------------Inicio exitVar_decl------------------" );
		
		System.out.println();
		System.out.println("---------------------Fim exitVar_decl---------------------" );
		System.out.println();
		System.out.println();
	}
	
	@Override
	public void enterStatement(StatementContext ctx) {
		System.out.println("---------------------Inicio enterStatement------------------" );
		
		System.out.println();
		System.out.println("---------------------Fim enterStatement---------------------" );
		System.out.println();
		System.out.println();
	}
	
	@Override
	public void exitStatement(StatementContext ctx) {
		System.out.println("---------------------Inicio exitStatement------------------" );
		System.out.println();
		System.out.println("---------------------Fim exitStatement---------------------" );
		System.out.println();
		System.out.println();
	}
	
	@Override
	public void enterAssing_op(Assing_opContext ctx) {
		System.out.println("---------------------Inicio enterAssing_op------------------" );
		System.out.println();
		System.out.println("---------------------Fim enterAssing_op---------------------" );
		System.out.println();
		System.out.println();
	}
	
	@Override
	public void exitAssing_op(Assing_opContext ctx) {
		System.out.println("---------------------Inicio exitAssing_op------------------" );
		System.out.println();
		System.out.println("---------------------Fim exitAssing_op---------------------" );
		System.out.println();
		System.out.println();
	}
	
	@Override
	public void enterMethod_call(Method_callContext ctx) {
		System.out.println("---------------------Inicio enterMethod_call------------------" );
		
		//Verifica se cumpre tds os argumento do metodo
		for(DecafParser.Method_declContext i : listaMetodos) {
			if(i.ID(0).getText().equals(ctx.ID().getText())) {
				if(scopes.get(i).getNumberOfSymbols() > ctx.expr().size()) {
					try {
						throw new NumeroDeArgumentosMetodoInvalidoException(scopes.get(i).getName(), "menor");
					} catch (NumeroDeArgumentosMetodoInvalidoException e) {
						// TODO Auto-generated catch block
						System.out.println(e.toString());
						System.exit(0);
					}
				}else if (scopes.get(i).getNumberOfSymbols() < ctx.expr().size()){
					try {
						throw new NumeroDeArgumentosMetodoInvalidoException(scopes.get(i).getName(), "maior");
					} catch (NumeroDeArgumentosMetodoInvalidoException e) {
						// TODO Auto-generated catch block
						System.out.println(e.toString());
						System.exit(0);
					}
				}
				
			}
		}
		
		
		
		/*System.out.println("Simbolos de "+scopes.get(teste).getName());
		for(int i = 0; i < scopes.get(teste).getSymbols().size(); i++) {
			System.out.println(scopes.get(teste).getSymbols().get(i));
		}*/
		
		System.out.println();
		System.out.println("---------------------Fim enterMethod_call---------------------" );
		System.out.println();
		System.out.println();
	}
	
	@Override
	public void exitMethod_call(Method_callContext ctx) {
		System.out.println("---------------------Inicio exitMethod_call------------------" );
		System.out.println();
		System.out.println("---------------------Fim exitMethod_call---------------------" );
		System.out.println();
		System.out.println();
	}
	
	@Override
	public void enterLocation(LocationContext ctx) {
		System.out.println("---------------------Inicio enterLocation------------------" );
		
		if(currentScope.getSymbol(ctx.ID().getText()) == null) {
			try {
				throw new VariavelNaoInstanciadaException(ctx.ID().getText());
			} catch (VariavelNaoInstanciadaException e) {
				// TODO Auto-generated catch block
				System.out.println(e.toString());
				System.exit(0);
			}
			
		}		
		
		System.out.println();
		System.out.println("---------------------Fim enterLocation---------------------" );
		System.out.println();
		System.out.println();
	}
	
	@Override
	public void exitLocation(LocationContext ctx) {
		System.out.println("---------------------Inicio exitLocation------------------" );
		System.out.println();
		System.out.println("---------------------Fim exitLocation---------------------" );
		System.out.println();
		System.out.println();
	}
	
	@Override
	public void enterExpr(ExprContext ctx) {
		System.out.println("---------------------Inicio enterExpr------------------" );
		System.out.println();
		System.out.println("---------------------Fim enterExpr---------------------" );
		System.out.println();
		System.out.println();
	}
	
	@Override
	public void exitExpr(ExprContext ctx) {
		System.out.println("---------------------Inicio exitExpr------------------" );
		
		
		
		
		System.out.println();
		System.out.println("---------------------Fim exitExpr---------------------" );
		System.out.println();
		System.out.println();
	}
	
	

	/*void defineVar(DecafParser.Var_declContext typeCtx, Token nameToken) {

		int typeTokenType = typeCtx.start.getType();

		VariableSymbol var = new VariableSymbol(nameToken.getText());

		// DecafSymbol.Type type = this.getType(typeTokenType);

		// var.setType(type);

		currentScope.define(var); // Define symbol in current scope

	}*/

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

	public static DecafSymbol.Type getType(int tokenType) {

		switch (tokenType) {

		case DecafParser.VOID:
			return DecafSymbol.Type.tVOID;

		case DecafParser.INTLITERAL:
			return DecafSymbol.Type.tINT;

		}

		return DecafSymbol.Type.tINVALID;

	}

}