import java.util.LinkedList;

public class ListaDeCantinas {

	private LinkedList<Referencia> colecao;
	
	public ListaDeCantinas(){
		this.colecao = new LinkedList<Referencia>();
	}
	
	public void adicionarReferencia(int port, int estoque){
		this.colecao.addLast( new Referencia(port, estoque) );
	}
	
	public void removerReferencia(int port){
		for(int i = 0; i < this.colecao.size(); i++){
			if( this.colecao.get(i).getPort() == port ){
				this.colecao.remove(i);
			}
		}
	}
	
	public int buscarEstoque(int estoque){
		int porta = -1;
		
		for(int i = 0; i < this.colecao.size(); i++){
			if( this.colecao.get(i).getEstoque() >= estoque ){
				porta = this.colecao.get(i).getPort();
				break;	
			}
		}
		return porta;
	}
	
	public boolean referenciaCadastrada(int port){
		boolean result = false;
		
		for(int i = 0; i < this.colecao.size(); i++){
			if( this.colecao.get(i).getPort() == port ){
				System.out.println("Referencia já está cadastrada");
				result = true;
				break;
			}
			else{
				System.out.println("Referencia nao esta cadastrada"+i);
			}
		
		}
		return result;
	}
	
	public void retirarEstoque(int port, int vendido){
		for(int i = 0; i < this.colecao.size(); i++){
			if( this.colecao.get(i).getPort() == port ){
				int estoque = ( this.colecao.get(i).getEstoque() - vendido );
				this.colecao.get(i).setEstoque(estoque);
				System.out.println( "Estoque atualizado: " + this.colecao.get(i).getEstoque() );
			}
		}
	}
	
	public int getEstoqueByPort(int port){
		int result = -1;
		for(int i = 0; i < this.colecao.size(); i++){
			if( this.colecao.get(i).getPort() == port ){
				result = this.colecao.get(i).getEstoque();
			}
		}
		return result;
	}
	
	public boolean listaIsEmpty(){
		if( this.colecao.isEmpty() )
			return true;
		else
			return false;
	}
	
}