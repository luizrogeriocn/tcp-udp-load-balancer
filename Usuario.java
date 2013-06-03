
public class Usuario {

	private String nome;
	private int tipo;
	private String senha;
	
	public Usuario(String nome, String senha){
		this.nome = nome;
		this.senha = senha;
		this.tipo = 0;
	}
	public Usuario(String nome, String senha, int tipo){
		this.nome = nome;
		this.senha = senha;
		this.tipo = tipo;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	public int getTipo(){
		return this.tipo;
	}
	
	private String getSenha(){
		return this.senha;
	}
	
	public boolean senhaCorreta(String tentativa){
		if(this.getSenha().startsWith(tentativa))
			return true;
		else
			return false;
	}
	
	
}
