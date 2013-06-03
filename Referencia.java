import java.net.InetAddress;


public class Referencia {
	private int id;
	private int estoque;
	
	private InetAddress address;
	private int port;
	
	public Referencia(int port, int estoque){
		this.port = port;
		this.estoque = estoque;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public void setEstoque(int estoque){
		this.estoque = estoque;
	}
	
	public int getEstoque(){
		return this.estoque;
	}
	
	public void setAddress(InetAddress address){
		this.address = address;
	}
	
	public InetAddress getAddress(){
		return this.address;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	
}
