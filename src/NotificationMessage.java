public class NotificationMessage extends Message{
	
	private static final long serialVersionUID = 8211297890273194673L;
	Bid bid;
	Item item;

	public NotificationMessage(MessageType newType, Bid bid, Item item) {
		super(newType);
		this.bid = bid;
		this.item = item;
	}
	
	public Bid getBid(){
		return bid;
	}
	
	public Item getItem(){
		return item;
	}

}
