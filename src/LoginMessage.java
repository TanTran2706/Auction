
public class LoginMessage extends Message{
	
	private static final long serialVersionUID = -3973352174875249304L;
	String ID;
	String password;

	public LoginMessage(MessageType newType, String newID, String newPassword) {
		super(newType);
		ID = newID;
		password = newPassword;
	}
	
}
