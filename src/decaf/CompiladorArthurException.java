package decaf;

@SuppressWarnings("serial")
public class CompiladorArthurException extends Exception {
	private String msg;
	
	public CompiladorArthurException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public String getMessage() {
		return msg;
	}
	
	public static class VariavelNaoInstanciadaException extends CompiladorArthurException{
		public VariavelNaoInstanciadaException(String var) {
			super("Variavel '"+var+"' não instanciada.");
		}
	}
	
	public static class MainNaoEncontradoException extends CompiladorArthurException{
		public MainNaoEncontradoException() {
			super("Método Main não encontrado.");
		}
	}
	
	public static class ArrayNaoValidoException extends CompiladorArthurException{
		public ArrayNaoValidoException(String var) {
			super("Para array '"+var+"' tamanho tem que ser maior que 0 ou tamanho nao e int");
		}
	}
	
	public static class NumeroDeArgumentosMetodoInvalidoException extends CompiladorArthurException{
		public NumeroDeArgumentosMetodoInvalidoException(String nomeMetodo, String msg) {
			super("Numero de Argumentos para metodo '"+nomeMetodo+"' e "+msg+" que o instanciado");
		}
	}
	
	public static class TipoDeArgumentosMetodoInvalidoException extends CompiladorArthurException{
		public TipoDeArgumentosMetodoInvalidoException(String tipoArgumento,String nomeVar, String tipoParametro, String nomeMetodo) {
			super("Tipo errado ("+tipoArgumento+") para variavel "+nomeVar+"("+tipoParametro+") do metodo '"+nomeMetodo+"'");
		}
	}
	
	public static class RetornoMetodoException extends CompiladorArthurException{
		public RetornoMetodoException(String nomeMetodo, String msg) {
			super("Metodo "+nomeMetodo+msg);
		}
	}
	

}
