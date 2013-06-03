import java.io.*;

public class Cantina {
	public static Conexao conexao;
	
	public static void main(String[] args) throws IOException, InterruptedException{

		
		conexao = FabricaDeConexao.getConexao(args[0]);
		conexao.criarCantinaSocket(args[1]); //args[0] = protcolo, args[1] = porta.
		conexao.cadastrarCantina(args[0], args[1]);

		while(true){
			//ouvir pacotes
			System.out.println("***********Cantina Escutando***********");
			Mensagem mensagem = conexao.cantinaOuvir();
		
			//enviar pacotes
			conexao.cantinaEnviar(mensagem);
		}
	}
}