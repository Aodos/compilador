/***
 * Excerpted from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
 ***/
package decaf;

import org.antlr.symtab.Scope;
import org.antlr.symtab.Type;

public class DecafSymbol {
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Type getTipo() {
		return tipo;
	}
	public void setTipo(Type tipo) {
		this.tipo = tipo;
	}
	public Scope getScopo() {
		return scopo;
	}
	public void setScopo(Scope scopo) {
		this.scopo = scopo;
	}
	private String name;
	private Type tipo;
	private Scope scopo;
	
	public DecafSymbol(String nome, Type tipo, Scope escopo) {
		this.name = nome;
		this.tipo = tipo;
		this.scopo = escopo;
	}
	
	public static class Tipos implements Type{
		private String nomeTipo;
		
		public Tipos(String nomeTipo) {
			this.nomeTipo = nomeTipo;
		}
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return nomeTipo;
		}

		@Override
		public int getTypeIndex() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	
	
	// A generic programming language symbol
    /*public static enum Type {tINVALID, tVOID, tINT, tFLOAT}

    String name;      // All symbols at least have a name
    Type type;
    Scope scope;      // All symbols know what scope contains them.

    public DecafSymbol(String name) { this.name = name; }
    public DecafSymbol(String name, Type type) { this(name); this.type = type; }
    public String getName() { return name; }

    public String toString() {
        if ( type!=Type.tINVALID ) return '<'+getName()+":"+type+'>';
        return getName();
    }*/
}
