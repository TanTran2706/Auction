public class ObjectMessage extends Message{
	
	private static final long serialVersionUID = -7446324816614247252L;
	Object message;

	public ObjectMessage(MessageType newType, Object object) {
		super(newType);
		message = object;
	}
	
	public Object getObject(){
		return message;
	}

}
