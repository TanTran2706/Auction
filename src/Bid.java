import java.io.Serializable;

public class Bid implements Serializable{

	private static final long serialVersionUID = 5969209136114127149L;
	String userID;
	Double bid;
	Item item;
	
	public Bid(Double bid, String userID, Item item){
		this.userID = userID;
		this.bid = bid;
		this.item = item;
	}
	
	public String getID(){
		return userID;
	}
	
	public Double getBid(){
		return bid;
	}
	
	public Item getItem(){
		return item;
	}

}
