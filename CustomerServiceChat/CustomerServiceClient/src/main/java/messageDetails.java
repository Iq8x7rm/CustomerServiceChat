import java.io.Serializable;

public class messageDetails implements Serializable {

	String text;
	int RepOrClient;  // 0 for Representative, 1 for Client
	
	messageDetails (String text, int RepOrClient) {
		this.text = text;
		this.RepOrClient = RepOrClient;
	}
	
}
