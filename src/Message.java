import java.io.Serializable;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 1463858580651891947L;
	MessageType type;
	
	public Message(MessageType newType){
		type = newType;
	}

}
