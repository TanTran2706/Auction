import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{
	
	private static final long serialVersionUID = 1710497928954504867L;
	String firstName;
	String familyName;
	String userID;
	String password;
	String[] userBids;
	static ArrayList<Item> won = null;
	int penaltypoints = 0;
	
	public User(String gname, String fname, String userID, String password){
		this.firstName = gname;
		this.familyName = fname;
		this.userID = userID;
		this.password = password;
	}
	
	public String getName(){
		return firstName;
	}
	
	public String getFamName(){
		return familyName;
	}
	
	public String getUserId(){
		return userID;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void addPenaltyPoint(){
		penaltypoints++;
	}
	
	public int getPenaltyPoint(){
		return penaltypoints;
	}
	
}
