// Logic for the client

import java.util.function.Consumer;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Client extends Thread {

	private Consumer<Serializable> myMessageDetails;  // Sends an instance of messageDetails to client GUI. 
	// This instance's text variable is added as a TextField to the client's GUI's listview
	private Consumer<Serializable> sendMessageBoolean;
	Socket clientSocket;
	ObjectOutputStream output;
	ObjectInputStream input;
	
	Client (Consumer<Serializable> myMessageDetails, Consumer<Serializable> sendMessageBoolean) {
		this.myMessageDetails = myMessageDetails;
		this.sendMessageBoolean = sendMessageBoolean;
		
		this.myMessageDetails.accept(new messageDetails("Wait for Representative to start session", 2));
	}
	
	public void run () {
		
		try {
			clientSocket = new Socket("127.0.0.1", 5555);
			output = new ObjectOutputStream(clientSocket.getOutputStream());
			input = new ObjectInputStream(clientSocket.getInputStream()); 
			clientSocket.setTcpNoDelay(true);
			
			this.sendMessageBoolean.accept(false);
		}
		catch (Exception e) {			
			e.printStackTrace();
		}
		
		while (Client.interrupted() != true) {
			try {
				messageDetails RepMessage = (messageDetails) input.readObject();
				this.myMessageDetails.accept(RepMessage);
			}
			catch (Exception e) {
				this.myMessageDetails.accept(new messageDetails("Connection to representative lost", 2));//e.printStackTrace();
				this.myMessageDetails.accept(new messageDetails("Messages will not be sent", 2));
				
				try {
					if (this.input != null) {
						this.input.close();
					}
				} 
				catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					if (this.output != null) {
						this.output.close();
					}
				} 
				catch (IOException e1) {
					e1.printStackTrace();
				}
				
				this.interrupt();
				
				this.sendMessageBoolean.accept(true);
			}
		}
		
	}
	
	// Send a message to the representative
	public void sendMessage (String message) {
		
		try {
			output.writeObject(new messageDetails(message, 1));
			this.myMessageDetails.accept(new messageDetails(message, 1));
		} 
		catch (IOException e) {}
	}
}
