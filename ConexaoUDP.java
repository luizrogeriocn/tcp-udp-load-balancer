import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class ConexaoUDP implements Conexao {
	private InetAddress IPAddress;
	int port;
	
	private DatagramSocket cantinaSocketUDP;
	private DatagramSocket serverSocketUDP;
	private DatagramSocket clientSocketUDP;
	private DatagramPacket pacoteRecebido;
	//private DatagramPacket pacoteEnviar;
	BufferedReader inFromUdpUser;
	private byte[] serverBufferReceive = new byte[1024];
	private byte[] cantinaBufferReceive = new byte[1024];
	private byte[] clientBufferReceive = new byte[1024];
	//private byte[] serverBufferSend = new byte[1024];
	//private byte[] cantinaBufferSend = new byte[1024];
	//private byte[] clientBufferSend = new byte[1024];
	
	
	//Conexões de Servidor.
	public void criarServerSocket() throws SocketException{ 
		 this.serverSocketUDP = new DatagramSocket(9000);
	}
	
	public Mensagem ouvir() throws IOException{
		this.pacoteRecebido = new DatagramPacket(this.serverBufferReceive, this.serverBufferReceive.length);
		this.serverSocketUDP.receive(this.pacoteRecebido);
		Mensagem mensagem = new Mensagem( this.pacoteRecebido, this.serverSocketUDP );
		return mensagem;
	}
	
	public void servidorEnviar(Mensagem mensagem, ListaDeCantinas lista) throws IOException{
		mensagem.servidorDecifrarMensagem(lista);
	}
	
	//Conexões de Cantina.
	public void criarCantinaSocket(String arg) throws IOException{
		 this.cantinaSocketUDP = new DatagramSocket(Integer.parseInt(arg));
		 this.cantinaConectarAoServidor();
	 }
	
	public void cadastrarCantina(String arg0, String arg1) throws IOException{ //AJEITAR PRA USAR SO ARG1 ***************
		IPAddress = InetAddress.getByName("localhost");
		this.pacoteRecebido = new DatagramPacket(this.cantinaBufferReceive, this.cantinaBufferReceive.length);
		Mensagem iniciar = new Mensagem("cadastrar", arg1, 20, "cantina", IPAddress, 9000, this.cantinaSocketUDP, "nat" );
		iniciar.enviarMensagem();
		this.cantinaSocketUDP.receive(this.pacoteRecebido);
		Mensagem mensagem = new Mensagem( this.pacoteRecebido, this.cantinaSocketUDP );
		mensagem.decifrarMensagem(); 
	}
	
	public Mensagem cantinaOuvir() throws IOException{
		this.pacoteRecebido = new DatagramPacket(this.serverBufferReceive, this.serverBufferReceive.length);
		this.cantinaSocketUDP.receive(this.pacoteRecebido);
		Mensagem mensagem = new Mensagem( this.pacoteRecebido, this.cantinaSocketUDP );
		return mensagem;
	}
	public void cantinaEnviar(Mensagem mensagem) throws IOException{
		mensagem.decifrarMensagem();
	}
	
	public void cantinaLoop() throws IOException{
		while(true){
			this.pacoteRecebido = new DatagramPacket(this.cantinaBufferReceive, this.cantinaBufferReceive.length);
			this.cantinaSocketUDP.receive(this.pacoteRecebido);
			Mensagem mensagem = new Mensagem( this.pacoteRecebido, this.cantinaSocketUDP );
			mensagem.decifrarMensagem();
		}
	}
	
	//Conexões de Cliente.
	public void criarClientSocket() throws IOException{	 
		 this.clientSocketUDP = new DatagramSocket();
		 this.clientConectarAoServidor();
	 }
	
	public void clientLoop(int arg) throws IOException{
		this.inFromUdpUser = new BufferedReader( new InputStreamReader(System.in) );
		IPAddress = InetAddress.getByName("localhost");
		
		System.out.println("O que deseja fazer: ");
		String acao =  inFromUdpUser.readLine();
		System.out.println("Digite a quantidade(ou porta caso tenha permissao para buscar estoque): ");
		int quantidade = Integer.parseInt( inFromUdpUser.readLine() );
		System.out.println("Qual a sala: ");
		String id =  inFromUdpUser.readLine();	
		
		if(arg != 1){
			Mensagem iniciar = new Mensagem(acao, "", quantidade, "cliente", IPAddress, 9000, this.clientSocketUDP, id);
			iniciar.enviarMensagem();
		}
		else{
			Mensagem iniciar = new Mensagem(acao, "", quantidade, "super", IPAddress, 9000, this.clientSocketUDP, id);
			iniciar.enviarMensagem();
		}
		
		this.pacoteRecebido = new DatagramPacket(clientBufferReceive, clientBufferReceive.length);
		this.clientSocketUDP.receive(pacoteRecebido);

		Mensagem mensagem = new Mensagem(pacoteRecebido, this.clientSocketUDP );
		if(Integer.parseInt(mensagem.getId()) > 1){
			mensagem.decifrarMensagem();
			
			this.pacoteRecebido = new DatagramPacket(clientBufferReceive, clientBufferReceive.length);
			this.clientSocketUDP.receive(pacoteRecebido);
	
			Mensagem mensagem1 = new Mensagem(pacoteRecebido, this.clientSocketUDP );
			mensagem1.decifrarMensagem();	
		}
		else if(Integer.parseInt(mensagem.getId()) == 0){
			 System.out.println("Infelizmente as cantinas não tem estoque disponivel.");
		 }
		 else if(Integer.parseInt(mensagem.getId()) == -1){
			 System.out.println("Ainda não há cantinas cadastradas.");
		 }
		 else{
			 mensagem.decifrarMensagem();
		 }
	}
	
	public void cantinaConectarAoServidor() throws IOException{
		IPAddress = InetAddress.getByName("localhost");
		Mensagem iniciar = new Mensagem("conectar", "id", 1, "cantina", IPAddress, 9000, this.cantinaSocketUDP, "" );
		this.pacoteRecebido = new DatagramPacket(this.cantinaBufferReceive, this.cantinaBufferReceive.length);
		boolean flag = false;
		try {
			this.cantinaSocketUDP.setSoTimeout(3000);
			} catch (SocketException e1) {
			e1.printStackTrace();
			}
			while (!flag) {	
				iniciar.enviarMensagem();
				try {
					this.cantinaSocketUDP.receive(this.pacoteRecebido);
					Mensagem mensagem = new Mensagem( this.pacoteRecebido, this.cantinaSocketUDP );
					mensagem.decifrarMensagem(); 
					flag = true;
				} catch (IOException e) {
					System.out.println("Por favor, aguarde. Estamos esperando uma resposta do Servidor.");
				}
			}
			this.cantinaSocketUDP.setSoTimeout(0);
	}
	public void clientConectarAoServidor() throws IOException{
		IPAddress = InetAddress.getByName("localhost");
		Mensagem iniciar = new Mensagem("conectar", "id", 1, "cantina", IPAddress, 9000, this.clientSocketUDP, "" );
		this.pacoteRecebido = new DatagramPacket(this.clientBufferReceive, this.clientBufferReceive.length);
		boolean flag = false;
		try {
			this.clientSocketUDP.setSoTimeout(3000);
			} catch (SocketException e1) {
			e1.printStackTrace();
			}
			while (!flag) {	
				iniciar.enviarMensagem();
				try {
					this.clientSocketUDP.receive(this.pacoteRecebido);
					Mensagem mensagem = new Mensagem( this.pacoteRecebido, this.clientSocketUDP );
					mensagem.decifrarMensagem(); 
					flag = true;
				} catch (IOException e) {
					System.out.println("Por favor, aguarde. Estamos esperando uma resposta do Servidor.");
				}
			}
			this.clientSocketUDP.setSoTimeout(0);
	}
		
	
	
	
}
