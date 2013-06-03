import java.io.IOException;
import java.net.SocketException;


public interface Conexao {
	
	//Conexões de Servidor.
	public void criarServerSocket()throws SocketException, IOException;
	
	public Mensagem ouvir() throws IOException;
		
	public void servidorEnviar(Mensagem mensagem, ListaDeCantinas lista) throws IOException;
		
	//Conexões de Cantina.
	public void criarCantinaSocket(String arg) throws SocketException, IOException, InterruptedException;
	public Mensagem cantinaOuvir() throws IOException;	
	public void cantinaEnviar(Mensagem mensagem) throws IOException;
	public void cadastrarCantina(String arg0, String arg1) throws IOException;
	
	public void cantinaLoop() throws IOException;
		
	//Conexões de Cliente.
	public void criarClientSocket() throws SocketException, IOException, InterruptedException;
		 
	public void clientLoop(int arg) throws IOException;
}


