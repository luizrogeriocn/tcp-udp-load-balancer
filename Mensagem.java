import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;


public class Mensagem {
	
	private InetAddress IPAddress;
	private byte[] sendData;
	private int porta;
	private DatagramSocket msgSocket;
	
	private String mensagemFormatada;
	private String[] msg;
	private String id;
	private String acao;
	private String deQuem;
	private int numero;
	private String local;
	
	//Um objeto da classe mensagem é construído a partir do pacote trocado na aplicação distribuída.
	//Ao ser construído, o objeto mensagem já decifra o pacote recebido.
	public Mensagem(DatagramPacket pacote, DatagramSocket socket) throws IOException{
		
		this.mensagemFormatada = new String( pacote.getData() );
		this.msg = this.mensagemFormatada.split(":");
		
		//this.numero = Integer.parseInt( msg[0] );
		//this.acao = msg[1];
		//this.deQuem = msg[2];
		
		this.acao = msg[0];
		this.id = msg[1];
		this.numero = Integer.parseInt( msg[2] );
		this.deQuem = msg[3];
		this.local = msg[4];
		
		this.porta = pacote.getPort();
		this.IPAddress = InetAddress.getByName("localhost");
		this.msgSocket = socket;
	}
	
	//MEXI NESSE CONSTRUTOR: MUDEI A ORDEM DOS PARAMETROS!
	//Um objeto da classe Mensagem pode também ser construído sem a necessidade de um pacote ter sido recebido.
	//Esse construtor será usado para que seja criada a primeira mensagem a ser trocada em um caso de uso.
	public Mensagem(String acao, String id, int numero, String deQuem, InetAddress IPAddress, int porta, DatagramSocket socket, String local){
		
		this.mensagemFormatada = new String( Mensagem.criarMensagem(acao, id, numero, deQuem, local) );
		
		this.numero = numero;
		this.acao = acao;
		this.id = id;
		this.deQuem = deQuem;
		this.local = local;
		
		this.porta = porta;
		this.IPAddress = IPAddress;
		this.msgSocket = socket;
		
	}
	
	//testar unir as duas classes mensagem
	public Mensagem(String mensagem, Socket connectionSocket) throws IOException{
		
		this.mensagemFormatada = mensagem;
		this.msg = this.mensagemFormatada.split(":");
		
		this.acao = msg[0];
		this.id = msg[1];
		this.numero = Integer.parseInt( msg[2] );
		this.deQuem = msg[3];
		this.local = msg[4];
		
		this.IPAddress = connectionSocket.getInetAddress();
		this.porta = connectionSocket.getPort();
		
	}
	public Mensagem(String mensagem) throws IOException{
		
		this.mensagemFormatada = mensagem;
		this.msg = this.mensagemFormatada.split(":");
		
		this.acao = msg[0];
		this.id = msg[1];
		this.numero = Integer.parseInt( msg[2] );
		this.deQuem = msg[3];
		this.local = msg[4];
		
	}
	
	public String getMensagemFormatada(){
		return this.mensagemFormatada;
	}
	
	public int getNumero(){
		return this.numero;
	}
	
	public String getAcao(){
		return this.acao;
	}
	
	public String getDeQuem(){
		return this.deQuem;
	}
	
	public InetAddress getIPAddress(){
		return this.IPAddress;
	}
	
	public int getPorta(){
		return this.porta;
	}
	
	public DatagramSocket getSocket(){
		return this.msgSocket;
	}
	
	public String getId(){
		return this.id;
	}
	public String getLocal(){
		return this.local;
	}

	
	//Método static para criar mensagens no formato correto sem a necessidade de um objeto Mensagem instanciado.
	public static String criarMensagem(String acao, String id, int numero, String deQuem, String local){
		String mensagem = new String(acao +":"+id +":"+String.valueOf(numero)+":"+ deQuem+":"+local);
		return mensagem;
	}
	
	public void enviarMensagem() throws IOException{
		this.sendData = mensagemFormatada.getBytes();
		DatagramPacket packetSend = new DatagramPacket(this.sendData, this.sendData.length, this.IPAddress, this.porta);
		msgSocket.send(packetSend);
	}
	
	
	//Servidor
	public void servidorDecifrarMensagem(ListaDeCantinas lista) throws IOException{	
		if( deQuem.startsWith("cliente") ){
			
			if( acao.startsWith("conectar") ){
				
				System.out.println("Pedido de conexão aceito!");
				
				Mensagem resposta = new Mensagem( "conectado", this.getId(), 1, "servidor", this.getIPAddress(), this.getNumero(), this.getSocket(), "nat" );
				resposta.enviarMensagem();
				
			}
			else if( acao.startsWith("comprar") ){
				
				System.out.println("Pedido de compra recebido, verificar cantinas.");
				
				if( ! lista.listaIsEmpty() ){
					
					int port = lista.buscarEstoque(numero);
					
					if( port != -1 ){
						System.out.println("Cantina disponível encontrada.");
						Mensagem resposta = new Mensagem( "confirmar", Integer.toString(port), this.getNumero(), "servidor", this.getIPAddress(), this.getPorta(), this.getSocket(), this.getLocal() );
						resposta.enviarMensagem();
						System.out.println("Cantina enviada para o cliente.");
						
					}
					else{
						System.out.println("Não há cantina cadastrada com estoque suficiente.");
						Mensagem resposta = new Mensagem( "rejeitar", Integer.toString(port), this.getNumero(), "servidor", this.getIPAddress(), this.getPorta(), this.getSocket(), ""  );
						resposta.enviarMensagem();
						System.out.println("Mensagem enviada com -1 pro cliente saber que não há estoque.");

					}
				}
				else{
					System.out.println("Ainda não existem cantinas online! Tente novamente mais tarde.");

					Mensagem resposta = new Mensagem( "rejeitar", "0", this.getNumero(), "servidor", this.getIPAddress(), this.getPorta(), this.getSocket(), ""  );
					resposta.enviarMensagem();
					System.out.println("Mensagem enviada com 0 pro cliente saber que não há cantina cadastrada.");
				}
			}
			else{
				System.out.println("A aplicação não foi capaz de decifrar a mensagem.");
			}
		}
		else if( deQuem.startsWith("cantina") ){
			if( acao.startsWith("conectar") ){			
				System.out.println("Pedido de conexão aceito!");
				
				Mensagem resposta = new Mensagem( "conectado", this.getId(), 1, "servidor", this.getIPAddress(), this.getPorta(), this.getSocket(), ""  );
				resposta.enviarMensagem();
			}
			else if(acao.startsWith("cadastrar")){
				lista.adicionarReferencia( this.getPorta(), this.getNumero() );
				
				Mensagem resposta = new Mensagem( "cadastrada", this.getId(), this.getNumero(), "servidor", this.getIPAddress(), this.getPorta(), this.getSocket(), ""  );
				resposta.enviarMensagem();
				System.out.println("Cantina cadastrada com sucesso. Foi enviada uma mensagem confirmando!");
			}
			else if( acao.startsWith("vendido") ){
				lista.retirarEstoque( (this.getPorta()), this.getNumero() );
				Mensagem resposta = new Mensagem( "atualizar", this.getId(), lista.getEstoqueByPort(this.getPorta()), "servidor", this.getIPAddress(), this.getPorta(), this.getSocket(), ""  );
				resposta.enviarMensagem();
				System.out.println("Estoque atualizado com sucesso. Foi enviada uma mensagem confirmando!");
			}
			else{
				System.out.println("A aplicação não foi capaz de decifrar a mensagem.");
			}
		}
		else if( deQuem.startsWith("super")){
			if( acao.startsWith("comprar") ){
				
				System.out.println("Pedido de compra recebido, verificar cantinas.");
				
				if( ! lista.listaIsEmpty() ){
					
					int port = lista.buscarEstoque(numero);
					
					if( port != -1 ){
						System.out.println("Cantina disponível encontrada.");
						Mensagem resposta = new Mensagem( "confirmar", Integer.toString(port), this.getNumero(), "servidor", this.getIPAddress(), this.getPorta(), this.getSocket(), this.getLocal() );
						resposta.enviarMensagem();
						System.out.println("Cantina enviada para o cliente.");
						
					}
					else{
						System.out.println("Não há cantina cadastrada com estoque suficiente.");
						Mensagem resposta = new Mensagem( "rejeitar", Integer.toString(port), this.getNumero(), "servidor", this.getIPAddress(), this.getPorta(), this.getSocket(), ""  );
						resposta.enviarMensagem();
						System.out.println("Mensagem enviada com -1 pro cliente saber que não há estoque.");

					}
				}
				else{
					System.out.println("Ainda não existem cantinas online! Tente novamente mais tarde.");

					Mensagem resposta = new Mensagem( "rejeitar", "0", this.getNumero(), "servidor", this.getIPAddress(), this.getPorta(), this.getSocket(), ""  );
					resposta.enviarMensagem();
					System.out.println("Mensagem enviada com 0 pro cliente saber que não há cantina cadastrada.");
				}

			}
			else if(acao.startsWith("buscar")){
				int resultado = lista.getEstoqueByPort(numero);
				System.out.println("aqui"+this.getNumero());
				Mensagem resposta = new Mensagem( "estoque", Integer.toString(-10), resultado, "servidor", this.getIPAddress(), this.getPorta(), this.getSocket(), this.getLocal() );
				resposta.enviarMensagem();
				System.out.println("O estoque solicitado é: "+resultado);
			}
		}
	}
	

	
	
	//DECIFRAR MENSAGEM DA CANTINA E DO CLIENTE!!!
	public void decifrarMensagem() throws IOException{
			
		if( deQuem.startsWith("servidor") ){
			if(acao.startsWith("conectado")){
				System.out.println("conectado com sucesso!");
			}
			else if( acao.startsWith("atualizar") ){
				System.out.println("Servidor alterou estoque com sucesso. Estoque: "+ this.getNumero());		
			}
			else if( acao.startsWith("cadastrada") ){
				System.out.println("Cantina cadastrada com sucesso. Estoque: "+this.getNumero());
			}
			else if(acao.startsWith("confirmar")){
				System.out.println("Cantina recebida. Redirecionando requisição.");

				Mensagem respostaCliente = new Mensagem( "comprar", this.getLocal(), this.getNumero(), "cliente", this.getIPAddress(), Integer.parseInt(this.getId()), this.getSocket(), ""  );
				respostaCliente.enviarMensagem();
				
			}
			else if(acao.startsWith("rejeitar")){
				if(id.startsWith("0")){
					System.out.println("Não existem cantinas cadastradas.");
				}
				else{
					System.out.println("As cantinas não tem estoque suficiente.");
				}
			}
			else if(acao.startsWith("estoque")){
				System.out.println("O estoque da cantina é: "+this.getNumero());
			}	
			else{
				System.out.println("A aplicação não foi capaz de decifrar a mensagem recebida do servidor.");
			}
		}
		else if( deQuem.startsWith("client") ){
			//O QUE FAZER QUANDO O REMETENTE EH o cliente
			if( acao.startsWith("comprar") ){
				//mandar msg confirmando para o cliente e atualizar estoque no servidor
				System.out.println("Pedido recebido. Efetuar venda.");
			
				System.out.println("Venda feita, avisar servidor.");
				Mensagem respostaServidor = new Mensagem( "vendido", Integer.toString(this.getPorta()), this.getNumero(), "cantina", this.getIPAddress(), 9000, this.getSocket(), ""  );
				respostaServidor.enviarMensagem();
				System.out.println("Entrega será feita na sala: "+this.getId());
				Mensagem respostaCliente = new Mensagem( "confirmar", Integer.toString(this.getPorta()), this.getNumero(), "cantina", this.getIPAddress(), this.getPorta(), this.getSocket(), "" );
				respostaCliente.enviarMensagem();
			}
		}
		else if(deQuem.startsWith("cantina")){
			System.out.println("Vou receber meu pedido em breve :)");
		}
	}
	
	//server tcp
	public String decifrarMensagem1(ListaDeCantinas lista) throws IOException{
		String retorno = null;
		if( deQuem.startsWith("cliente") ){
			
			if( acao.startsWith("conectar") ){
				
				System.out.println("Pedido de conexão aceito!");
				
				retorno = ("confirmar:"+this.getPorta()+":"+this.getNumero()+":servidor:nat");
				
			}
			else if( acao.startsWith("comprar") ){
				
				System.out.println("Pedido de compra recebido, verificar cantinas.");
				
				if( ! lista.listaIsEmpty() ){

					int port = lista.buscarEstoque(this.getNumero());
					
					if( port != -1 ){
						System.out.println("Cantina disponível encontrada: "+port);
						retorno = ("confirmar:"+Integer.toString(port)+":"+this.getNumero()+":servidor:nat");
						lista.retirarEstoque( port, this.getNumero() );
						//System.out.println("Estoque atualizado: "+lista.getEstoqueByPort(port));
					}
					else{
						System.out.println("Não há cantina cadastrada com estoque suficiente.");
						System.out.println("Mensagem enviada com -1 pro cliente saber que não há estoque.");
						retorno = ("rejeitar:"+Integer.toString(port)+":"+this.getNumero()+":servidor:nat");
					}
				}
				else{
					System.out.println("Ainda não existem cantinas online! Tente novamente mais tarde.");
					System.out.println("Mensagem enviada com 0 pro cliente saber que não há cantina cadastrada.");
					retorno = ("rejeitar:"+"0"+":"+this.getNumero()+":servidor:nat");
				}
			}
			else{
				System.out.println("A aplicação não foi capaz de decifrar a mensagem.");
			}
		}
		else if( deQuem.startsWith("cantina") ){
			if( acao.startsWith("conectar") ){			
				System.out.println("Pedido de conexão aceito!");
				retorno = ("conectado:"+this.getPorta()+":"+1+":servidor:\n");
			}
			else if(acao.startsWith("cadastrar")){
				lista.adicionarReferencia( Integer.parseInt(this.getId()), this.getNumero() );
				retorno = ("cadastrada:"+this.getId()+":"+this.getNumero()+":servidor: ");
			}
			else if( acao.startsWith("vendido") ){
				lista.retirarEstoque( (Integer.parseInt(this.getId())), this.getNumero() );
				System.out.println("Estoque atualizado com sucesso. Foi enviada uma mensagem confirmando!");
				retorno = ("atualizar:"+this.getId()+":"+lista.getEstoqueByPort(this.getPorta())+":servidor:");
			}
			else{
				System.out.println("A aplicação não foi capaz de decifrar a mensagem.");
			}
		}
		return retorno;
	}
	
	//cantina e cliente tcp
	public String decifrarMensagem1() throws IOException{
		String retorno = null;
		if( deQuem.startsWith("servidor") ){
			if(acao.startsWith("conectado")){
				System.out.println("conectado com sucesso!");
			}
			else if( acao.startsWith("atualizar") ){
				System.out.println("Servidor alterou estoque com sucesso. Estoque: "+ this.getNumero());		
			}
			else if( acao.startsWith("cadastrada") ){
				System.out.println("Cantina cadastrada com sucesso. Estoque: "+this.getNumero());
			}
			else if(acao.startsWith("confirmar")){
				retorno = ("comprar:"+this.getLocal()+":"+this.getNumero()+":cliente: ");
				
			}
			else if(acao.startsWith("rejeitar")){
				if(id.startsWith("0")){
					System.out.println("Não existem cantinas cadastradas.");
				}
				else{
					System.out.println("As cantinas não tem estoque suficiente.");
				}
			}
				
			else{
				System.out.println("A aplicação não foi capaz de decifrar a mensagem recebida do servidor.");
			}
		}
		else if( deQuem.startsWith("client") ){
			if( acao.startsWith("comprar") ){
				System.out.println("Pedido recebido. Efetuar venda.");
				System.out.println("Venda feita, avisar servidor.");
				System.out.println("Entrega será feita na sala: "+this.getLocal());
				retorno = ("entrega:"+this.getId()+":"+this.getNumero()+":cantina: ");
			}
		}
		else if(deQuem.startsWith("cantina")){
			System.out.println("Vou receber meu pedido em breve :)");
		}
		return retorno;
	}
	
}