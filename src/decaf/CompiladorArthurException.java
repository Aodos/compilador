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
	
	public static class TamanhoNaoValidoException extends CompiladorArthurException{
		public TamanhoNaoValidoException(String var) {
			super("Para array '"+var+"' tamanho tem que ser maior que 0.");
		}
	}
	
	public static class NumeroDeArgumentosMetodoInvalidoException extends CompiladorArthurException{
		public NumeroDeArgumentosMetodoInvalidoException(String nomeMetodo, String msg) {
			super("Numero de Argumentos para metodo '"+nomeMetodo+"' e "+msg+" que o instanciado");
		}
	}
	

}
