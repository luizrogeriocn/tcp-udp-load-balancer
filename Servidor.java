import java.io.IOException;

public class Servidor {
	
	public static ListaDeUsuarios listaUsuarios = new ListaDeUsuarios();
	public static ListaDeCantinas listaRef = new ListaDeCantinas();
	public static Conexao conexao;
		
	public static void main(String[] args) throws IOException{
		conexao = FabricaDeConexao.getConexao(args[0]);
		
		conexao.criarServerSocket();

		while(true){
			System.out.println("***********Servidor Escutando***********");
			Mensagem mensagem = conexao.ouvir();
		
			//enviar pacotes
			conexao.servidorEnviar(mensagem, listaRef);

		}
	}
}