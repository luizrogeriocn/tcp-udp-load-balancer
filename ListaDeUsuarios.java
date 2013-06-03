import java.util.LinkedList;


public class ListaDeUsuarios {
	
	private LinkedList<Usuario> colecao;
	
	public ListaDeUsuarios(){
		this.colecao = new LinkedList<Usuario>();
		this.adicionarUsuario("rogerio", "roger", 1);
		this.adicionarUsuario("usuario", "senha");
	}
	
	public void adicionarUsuario(String nome, String senha){
		this.colecao.addLast( new Usuario(nome, senha) );
	}
	public void adicionarUsuario(String nome, String senha, int tipo){
		this.colecao.addLast( new Usuario(nome, senha, tipo) );
	}
	
	public void removerUsuario(String nome){
		for(int i = 0; i < this.colecao.size(); i++){
			if( this.colecao.get(i).getNome() == nome){
				this.colecao.remove(i);
			}
		}
	}
	
	public int getTipoByName(String nome){
		for(int i = 0; i < this.colecao.size(); i++){
			if( this.colecao.get(i).getNome().startsWith(nome)){
				return ( this.colecao.get(i).getTipo() );
			}
		}
		return 0;
	}
	
	public boolean compararUsuarioSenha(String nome, String senha){
		for(int i = 0; i < this.colecao.size(); i++){
			if( this.colecao.get(i).getNome().startsWith(nome)){
				if( this.colecao.get(i).senhaCorreta(senha) ){
					return true;
				}
			}
		}
		return false;
	}

}
