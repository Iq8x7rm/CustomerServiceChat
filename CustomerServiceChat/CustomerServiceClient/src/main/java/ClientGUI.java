// GUI for the client

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;

public class ClientGUI extends Application {

	public static void main (String args[]) {
		launch(args);
	}
	
	public void start (Stage myStage) {
		
		// The message the client types
		TextField message = new TextField();
		message.setPrefWidth(300);
		message.setPrefHeight(100);
		
		Button sendMessage = new Button("Send Message");
		sendMessage.setPrefWidth(100);
		sendMessage.setPrefHeight(100);
		sendMessage.setDisable(true);
		
		HBox ClientCommunication = new HBox(message, sendMessage);
		ClientCommunication.setPrefWidth(400);
		ClientCommunication.setAlignment(Pos.CENTER);
		
		ListView<TextField> allMessages = new ListView();
		allMessages.setPrefHeight(500);
		
		VBox  ClientGUIElements = new VBox(allMessages, ClientCommunication);
	
		
		Client theClient = new Client(
			a -> {
				Platform.runLater(() -> {
					messageDetails myMessageDetails = (messageDetails) a;
					TextField myTextField = new TextField(myMessageDetails.text);
					
					if (myMessageDetails.RepOrClient == 0) {
						myTextField.setStyle("-fx-text-fill: black");
						myTextField.setAlignment(Pos.CENTER_LEFT);
					}
					if (myMessageDetails.RepOrClient == 1) {
						myTextField.setStyle("-fx-text-fill: blue");
						myTextField.setAlignment(Pos.CENTER_RIGHT);
					}
					if (myMessageDetails.RepOrClient == 2) {
						myTextField.setStyle("-fx-text-fill: green");
						myTextField.setAlignment(Pos.CENTER);
					}
					
					allMessages.getItems().add(myTextField);
				}); 
			}, 
			b-> {
				Platform.runLater(() -> {
					sendMessage.setDisable((Boolean) b);
				});
			}
		);
		theClient.start();
		
		
		myStage.setOnCloseRequest(a -> {
			try {
				theClient.output.writeObject(new messageDetails("Client has clicked red x", 2));
			} 
			catch (IOException e) {			
				e.printStackTrace();
			}
		});
		
		
		sendMessage.setOnAction(a -> {
			if (message.getText().equals("") == false) {
				theClient.sendMessage(message.getText());
				message.clear();
			}
		});
		
		// If click enter button, typed up message will send
		Scene communicationScene = new Scene(ClientGUIElements, 400, 600);
		communicationScene.setOnKeyPressed(a -> {
			if (a.getCode() == KeyCode.ENTER && sendMessage.isDisabled() == false && message.getText().equals("") == false) {
				theClient.sendMessage(message.getText());
				message.clear();
			}
		});
		
		myStage.setScene(communicationScene);
		myStage.show();	
		
	}
	
}
