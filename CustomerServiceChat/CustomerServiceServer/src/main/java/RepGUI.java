// GUI for server

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import java.lang.Thread.State;


public class RepGUI extends Application {

	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	public void start (Stage myStage) {
		
		BorderPane IntroBorderPane = new BorderPane();
		
		// Scene with start button, prior to entering main interface
		Scene IntroScene = new Scene(IntroBorderPane, 600, 400);
		
		Button Start = new Button("Start");
		IntroBorderPane.setCenter(Start);
		Start.setOnAction(a -> {
			myStage.setScene(Session(myStage));
		});
		
		
		myStage.setScene(IntroScene);
		myStage.show();
		
	}
	
	
	// Scene with main interface
	public Scene Session (Stage myStage) {
		
		HBox SessionElements;
		
		ListView<TextField> Conversation = new ListView<TextField>();
		Conversation.setPrefWidth(400);
		
		// The message the representative types
		TextField message = new TextField();
		message.setPrefWidth(300);
		message.setPrefHeight(100);
		
		Button sendMessage = new Button("Send Message");
		sendMessage.setPrefWidth(100);
		sendMessage.setPrefHeight(100);
		
		HBox RepCommunication = new HBox(message, sendMessage);
		VBox CommunicationElements = new VBox(Conversation, RepCommunication);
		
		Button NextClient = new Button("Next Client");
		
		Button EndSession = new Button("End Session");
		
		TextField NumClientsWaiting = new TextField("# clients waiting");
		NumClientsWaiting.setMaxWidth(150);
		NumClientsWaiting.setAlignment(Pos.CENTER);
		NumClientsWaiting.setEditable(false);
		
		VBox SessionDetails = new VBox(NextClient, EndSession, NumClientsWaiting);
		SessionDetails.setSpacing(100);
		SessionDetails.setPrefWidth(200);
		SessionDetails.setAlignment(Pos.CENTER);
		
		SessionElements = new HBox(CommunicationElements, SessionDetails);
		
		
		RepServer myRepServer = new RepServer (	
			a -> {
				Platform.runLater(() -> {
					messageDetails myMessageDetails = (messageDetails) a;
					TextField myTextField = new TextField(myMessageDetails.text);
					
					if (myMessageDetails.RepOrClient == 2) {
						myTextField.setStyle("-fx-text-fill: green");
						myTextField.setAlignment(Pos.CENTER);
					}
					if (myMessageDetails.RepOrClient == 1) {
						myTextField.setStyle("-fx-text-fill: black");
						myTextField.setAlignment(Pos.CENTER_LEFT);
					}
					if (myMessageDetails.RepOrClient == 0) {
						myTextField.setStyle("-fx-text-fill: blue");
						myTextField.setAlignment(Pos.CENTER_RIGHT);
					}
					
					Conversation.getItems().add(myTextField);
					
				});
			}, 
			b -> {
				Platform.runLater(() -> {
					NumClientsWaiting.setText(b.toString());
				});
			}
		);
		
		
		// If there is a client waiting, get it
		NextClient.setOnAction (a -> {
			if (myRepServer.allClients.size() == 0) {
				Conversation.getItems().add(new TextField("No clients waiting"));
				NumClientsWaiting.setText(myRepServer.clientsInQueue + " clients waiting");
			}
			else {
				myRepServer.allClients.get(0).start();	
				NextClient.setDisable(true);
				EndSession.setDisable(false);
				sendMessage.setDisable(false);
				
				--myRepServer.clientsInQueue;
				NumClientsWaiting.setText(myRepServer.clientsInQueue + " clients waiting");
				
				Conversation.getItems().clear();
			}
		});
		
		
		// End the session with the current client
		EndSession.setOnAction(
			a -> {
				myRepServer.allClients.get(0).interrupt();
				try {
					myRepServer.allClients.get(0).input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					myRepServer.allClients.get(0).output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				myRepServer.allClients.remove(0);
				
				NextClient.setDisable(false);
				EndSession.setDisable(true);
				sendMessage.setDisable(true);
				
				Conversation.getItems().clear();
			}
		);
		
		
		EndSession.setDisable(true);
		sendMessage.setDisable(true);
		
		
		sendMessage.setOnAction(a -> {
			if (message.getText().equals("") == false) {
				myRepServer.allClients.get(0).sendMessage(message.getText());
				message.clear();
			}
		});
		
		
		myStage.setOnCloseRequest(
			a -> {
				for (int i = 0; i < myRepServer.allClients.size(); ++i) {
					try {
						if (myRepServer.allClients.get(i).getState() == Thread.State.NEW) {
							myRepServer.allClients.get(i).start();
						}
//						try {
//							myRepServer.allClients.get(i).sleep(3000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						myRepServer.allClients.get(i).clientSocket.close();
						//myRepServer.allClients.remove(0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				//myRepServer.myServerSocket.close();
				
			}
						
		);
		
		// If click enter button, typed up message will send
		Scene communicationScene = new Scene(SessionElements, 600, 400);
		communicationScene.setOnKeyPressed(a -> {
			if (a.getCode() == KeyCode.ENTER && sendMessage.isDisabled() == false && message.getText().equals("") == false) {
				myRepServer.allClients.get(0).sendMessage(message.getText());
				message.clear();
			}
		});
		
		return communicationScene;		
		
	}
}
