import java.io.*;
import java.net.*;


public class CantinaRunnable implements Runnable {

	protected Socket connectionSocket = null;
	public Mensagem msg;

	public CantinaRunnable(Socket connectionSocket, Mensagem msg) {
		this.connectionSocket = connectionSocket;
		this.msg = msg;
	}

	public void run() {

		try {
			while (true) {
				String resposta = msg.decifrarMensagem1();
				DataOutputStream cantinaOutput = new DataOutputStream(
						connectionSocket.getOutputStream());
				cantinaOutput.writeBytes(resposta + '\n');
				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}