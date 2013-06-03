
public class FabricaDeConexao {
	
	public String protocolo;
	
	public static Conexao getConexao(String protocolo) {
        if( protocolo.contentEquals("udp") )
        	return new ConexaoUDP();//conexaoUDP
        else
        	return new ConexaoTCP();//conexaoTCP       
    }
	
	public void setProtocolo(String protocolo){
		this.protocolo = protocolo;
	}
	
}
