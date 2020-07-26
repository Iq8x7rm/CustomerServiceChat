// Logic for the Server

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class RepServer {

	ArrayList<clientThread> allClients = new ArrayList<clientThread>();
	Consumer<Serializable> messages;  // Sends an instance of messageDetails to server GUI. 
		// This instance's text variable is added as a TextField to the server GUI's listview
	Consumer<Serializable> numClientsWaiting;  // Updates textfield in GUI 
		// that shows how many clients waiting
	int clientsInQueue = 0;  // # of clients in queue. Includes those 
		// who have quit their GUI applications. Use with numClientsWaiting
	
	RepServer (Consumer<Serializable> messages, Consumer<Serializable> numClientsWaiting) {
		this.messages = messages;
		
		this.numClientsWaiting = numClientsWaiting;

		// Create and a start a server thread
		serverThread myserverThread = new serverThread();
		myserverThread.start();
	}
	
	
	public class serverThread extends Thread {
		
		public void run () {
			
			try (ServerSocket myServerSocket = new ServerSocket(5555);) {
				myServerSocket.setReuseAddress(true);  // INSERT
				while (true) {
					clientThread theClientThread = new clientThread(myServerSocket.accept());
					allClients.add(theClientThread);
					++clientsInQueue;
					numClientsWaiting.accept(clientsInQueue + " clients waiting");
				}
				
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public class clientThread extends Thread {
		
		Socket clientSocket;
		ObjectOutputStream output;
		ObjectInputStream input;
		
		clientThread (Socket theSocket) {
			clientSocket = theSocket;
		}
		
		public void sendMessage (String message) {
			
			try {
				output.writeObject(new messageDetails(message, 0));
				messages.accept(new messageDetails(message, 0));
			} 
			catch (IOException e) {}
		}
		
		public void run () {
			
			try {
				output = new ObjectOutputStream(clientSocket.getOutputStream());
				input = new ObjectInputStream(clientSocket.getInputStream());
				clientSocket.setTcpNoDelay(true);
			}
			catch (Exception a) {}
			
			try {
				output.writeObject(new messageDetails("You have connected. Session started", 2));
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			
			// Keep reading in messages from the client and update the server's GUI
			messageDetails inputFromClient = new messageDetails("You have connected. Session started", 2);
			while (clientThread.interrupted() != true) {
				
				messages.accept(inputFromClient);
				
				try {
					inputFromClient = (messageDetails) input.readObject();
				}
				catch (Exception a) {
					this.interrupt(); 
					
				}
			}
		}
	}  // end of clientThread
}  // End of RepServer
