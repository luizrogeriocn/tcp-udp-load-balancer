import java.io.*;
import java.net.*;

public class ServidorRunnable implements Runnable{

    protected Socket connectionSocket = null;
    public ListaDeCantinas lista;
    public Mensagem msg;
    
    public ServidorRunnable(Socket connectionSocket, ListaDeCantinas lista, Mensagem msg) {
        this.connectionSocket = connectionSocket;
        this.lista = lista;
        this.msg = msg;
    }

    public void run() {

        	try {
        		while(true){
				String resposta = msg.decifrarMensagem1(lista);
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				outToClient.writeBytes( resposta +'\n' );////////

				break;
        		}
				
			} catch (IOException e) {
				e.printStackTrace();
			}

    }
}