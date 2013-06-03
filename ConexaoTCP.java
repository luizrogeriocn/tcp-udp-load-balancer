import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class ConexaoTCP implements Conexao {
	public InetAddress IPAddress;
	public int port;
	
	//Usados no tcp server
	private ServerSocket serverSocketTCP;
	private Socket connectionSocket;
	//private BufferedReader inFromClient;
	//private DataOutputStream outToClient;
	
	//usados no tcp client
	//private Socket clientSocketTCP;
	private DataOutputStream outToServer;
	private BufferedReader inFromServer;
	BufferedReader inFromTcpUser;
	
	//usados no tcp cantina
	private ServerSocket cantinaSocketTCP;
	//private DataOutputStream cantinaOutput;
	
	//Conexões de Servidor.
	public void criarServerSocket() throws SocketException, IOException{
		 this.serverSocketTCP = new ServerSocket(9000);
	 }
	
	public Mensagem ouvir() throws IOException{
		this.connectionSocket = this.serverSocketTCP.accept();
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
		String clientSentence = inFromClient.readLine();
		Mensagem mensagem = new Mensagem(clientSentence, this.connectionSocket);
		return mensagem;	
	}
	
	
	public void servidorEnviar(Mensagem mensagem, ListaDeCantinas lista) throws IOException{
		new Thread(
				new ServidorRunnable(
						connectionSocket, lista, mensagem)
				).start();
	}
	
	
	//Conexões de Cantina.
	public void criarCantinaSocket(String arg) throws SocketException, IOException, InterruptedException{
		 this.cantinaSocketTCP = new ServerSocket(Integer.parseInt(arg));
		 this.conectarAoServidor();
	 }
	
	public void cadastrarCantina(String arg0, String arg1) throws IOException{
		
		
		Socket clientSocket = new Socket("localhost", 9000);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());	 
        outToServer.writeBytes("cadastrar:"+arg1+":15:cantina:n"+'\n');
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String modifiedSentence = inFromServer.readLine();
		Mensagem msg = new Mensagem(modifiedSentence, clientSocket);
		msg.decifrarMensagem();
        clientSocket.close();
	}
	
	public Mensagem cantinaOuvir() throws IOException{
		this.connectionSocket = this.cantinaSocketTCP.accept();
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
		String clientSentence = inFromClient.readLine();
		Mensagem mensagem = new Mensagem(clientSentence, this.connectionSocket);
		return mensagem;
	}
	
	public void cantinaEnviar(Mensagem mensagem) throws IOException{
		new Thread(
				new CantinaRunnable(
						connectionSocket, mensagem)
				).start();
	}

	public void cantinaLoop() throws IOException{}
	
	//Conexões de Cliente.
	 public void criarClientSocket() throws SocketException, IOException, InterruptedException{
		 conectarAoServidor();
	 }
	 
	 public void clientLoop(int arg) throws IOException{
		 
		 BufferedReader inFromUdpUser = new BufferedReader( new InputStreamReader(System.in) );
		 System.out.println("************Cliente da Cantina Online************");
		 System.out.println("O que deseja fazer: ");
		 String acao =  inFromUdpUser.readLine();
		 System.out.println("Digite a quantidade: ");
		 int quantidade = Integer.parseInt( inFromUdpUser.readLine() );
		 System.out.println("Qual a sala: ");
		 String id =  inFromUdpUser.readLine();	
		 
		 Socket inicio = new Socket("localhost", 9000); 
		 
		 outToServer = new DataOutputStream(inicio.getOutputStream());
		 String msgformatada = (acao+":9:"+quantidade+":cliente:"+id);
		 outToServer.writeBytes(msgformatada + '\n'); 
		 this.inFromServer = new BufferedReader(new InputStreamReader(inicio.getInputStream()));
		 String modifiedSentence = inFromServer.readLine();
		 
		 Mensagem msg = new Mensagem(modifiedSentence, inicio);
		 int port = Integer.parseInt( msg.getId() );
		 inicio.close();
		  
		 if(port > 0){
			 Socket clientSocket1 = new Socket("localhost", port);
			 DataOutputStream outToServer1 = new DataOutputStream(clientSocket1.getOutputStream());
			
			 String resposta = msg.decifrarMensagem1(); 
			 outToServer1.writeBytes( resposta +'\n' );
			 
			 BufferedReader inFromServer1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
			 String modifiedSentence1 = inFromServer1.readLine();
			 clientSocket1.close();
		 }
		 else if(port < 0){
			 System.out.println("Infelizmente as cantinas não tem estoque disponivel.");
		 }
		 else{
			 System.out.println("Ainda não há cantinas cadastradas.");
		 }
	 }
	 
	 public void conectarAoServidor() throws IOException, InterruptedException{
			boolean flag = false;
			Socket clientSocket;
			
				while(!flag) {
					try {
		                if ( ( ( clientSocket = new Socket("localhost", 9000) ).isConnected() ) ) {
		                	DataOutputStream outToServer1 = new DataOutputStream(clientSocket.getOutputStream());	 
		                    outToServer1.writeBytes("conectar:"+65+":29:cliente:n"+'\n');
		             
		                    flag = true;
		                    try {
		                    	BufferedReader inFromServer1 = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			            		String modifiedSentence = inFromServer1.readLine();
			            		clientSocket.close();
		                    } catch (IOException e) {
							// TODO Auto-generated catch block
		                    	System.out.println("Erro ao inputStream");
		                    }
		                } else {
		                	flag = false;
		                }

				}
					
					catch (IOException e) {
						// TODO Auto-generated catch block
						Thread.sleep(3000);
						System.out.println("TENTANDO SE CONECTAR COM O SERVIDOR NOVAMENTE");
					}
				}
	 }

	 
}