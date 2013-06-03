import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Cliente {
	public static Conexao conexao;
	public static BufferedReader inFromClient;
	
	public static void main(String[] args) throws IOException, InterruptedException{
		inFromClient = new BufferedReader( new InputStreamReader(System.in) );
		conexao = FabricaDeConexao.getConexao(args[0]);
		conexao.criarClientSocket();
		String nome = null;
		String senha = null;
		
		System.out.println("***********Cliente da Cantina Online***********");
		//FAZER O CLIENTE SE AUTENTICAR
		boolean taCerto = false;
		while(!taCerto){
			System.out.println("Por favor insira o nome de usuário: ");
			nome =  inFromClient.readLine();
			System.out.println("Senha:");
			senha =  inFromClient.readLine();
			if(Servidor.listaUsuarios.compararUsuarioSenha(nome, senha))
				taCerto = true;
		}
		
		
		while(true){
			conexao.clientLoop(Servidor.listaUsuarios.getTipoByName(nome));
		}
	}
}
	