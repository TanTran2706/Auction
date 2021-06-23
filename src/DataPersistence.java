import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;

public class DataPersistence {

	BufferedReader br = null;
	FileWriter outputwriter = null;
	File file;
	Integer[] bids;
	byte[] imagebyte;

	public boolean createUserFile(User user) throws IOException{

		File dir = new File("Users");
		if(!dir.exists()){
			dir.mkdirs();
		}

		for(File f : dir.listFiles()){
			if(!(f.isDirectory())){
				if((f.getName().equals(user.userID))){
					return false;
				}
			}
		}

		file = new File("Users/" + user.userID);
		try {
			outputwriter = new FileWriter(file);
			outputwriter.write(user.firstName + " " + user.familyName + "\n");
			outputwriter.write(user.userID + "\n");
			outputwriter.write(user.password + "\n");
			outputwriter.write("Penalty,0\n");
			//each user starts out with 0 penalty points
		} finally{
			if(outputwriter != null){
				outputwriter.close();
			}
		}
		return true;
	}

	public boolean authenticateUser(String userID, String password) throws IOException{
		File IDFile = null;

		int lines = 0;
		String thisID = null;
		String thispass = null;
		String newLine = null;

		File dir = new File("Users");
		if(!dir.exists())
			dir.mkdirs();

		for(File f : dir.listFiles()){
			if(!(f.isDirectory())){
				if(f.getName().equals(userID)){

					IDFile = new File(f.getPath());
					br = new BufferedReader(new FileReader("Users/"+ f.getName()));

					while((newLine = br.readLine()) != null){
						lines++;
						if(lines == 2){
							thisID = newLine; 
						} else if(lines == 3){
							thispass = newLine;
						}
					}
					
					br.close();

				}
			}
		}
		if(thisID == (null) && thispass == (null)){
			return false;
		} else if(thisID.equals(userID) && thispass.equals(password)){
			return true;
		} else{
			return false;
		}
	}

	public boolean createItemFile(Item item, String userID) throws IOException{

		int point = getPenalty(userID);

		File image = new File("Items/" + item.getID() + ".jpg");

		if(item.getImageBytes() != null){
			InputStream in = new ByteArrayInputStream(item.getImageBytes());
			BufferedImage BIimage = ImageIO.read(in);
			ImageIO.write(BIimage, "jpg", image);
			item.setImagePath(image.getPath());
		}

		if(point < 2){

			file = new File("Items/" + item.getID() + ".txt");
			try {
				outputwriter = new FileWriter(file);
				outputwriter.write("Item\n");
				outputwriter.write(item.title + "\n");
				outputwriter.write(item.description + "\n");
				outputwriter.write(item.categoryKey + "\n");
				outputwriter.write(item.start + "\n");
				outputwriter.write(item.finish + "\n");
				outputwriter.write(item.userID + "\n");
				outputwriter.write(item.rPrice + "\n");
				outputwriter.write(item.getID() + "\n");
				if(item.getImagePath() != null)
					outputwriter.write(image.getPath() + "\n");
				else
					outputwriter.write("\n");
				outputwriter.write("Bids:");
			} finally{
				if(outputwriter != null){
					outputwriter.close();
				}
			}

			return true;
		} else{
			return false;
		}
	}

	public List<Item> createItemList() throws IOException{
		List<Item> items = new ArrayList<Item>();

		File dir = new File("Items");
		if(!dir.exists())
			dir.mkdirs();


		for(File f : dir.listFiles()){
			if(!(f.isDirectory()) && f.getName().endsWith(".txt")){
				Item newItem = getItem(f);
				if(!items.contains(newItem)){
					items.add(newItem);
				}
			}
		}
		return items;
	}

	public List<Item> createFinishedItemList() throws IOException{
		List<Item> items = new ArrayList<Item>();

		File dir = new File("Items/Finished");
		if(!dir.exists())
			dir.mkdirs();

		for(File f : dir.listFiles()){
			if(!(f.isDirectory()) && f.getName().endsWith(".txt")){
				Item newItem = getFinishedItem(f);
				if(!items.contains(newItem)){
					items.add(newItem);
				}
			}
		}
		return items;
	}


	public Item addBid(Bid bid) throws IOException{
		File dir = new File("Items");
		if(!dir.exists())
			dir.mkdirs();

		File file;
		for(File f : dir.listFiles()){
			if(!(f.isDirectory())){
				if(f.getName().equals(String.valueOf(bid.getItem().getID()+".txt"))){

					file = new File(f.getPath());
					br = new BufferedReader(new FileReader("Items/"+ f.getName()));
					outputwriter = new FileWriter(file, true);

					try {
						outputwriter.write("\n" + String.valueOf(bid.getBid() +","+ bid.getID()));
					} finally{
						if(outputwriter != null){
							outputwriter.close();
						}
					}
					bid.getItem().setCurrentBid(bid);
					br.close();
					return getItem(f);
				}
			}
		}
		return null;
	}

	public Item getItem(File f) throws IOException{
		String newLine = null;

		br = new BufferedReader(new FileReader(f));
		if((newLine = br.readLine()) != null){
			if(newLine.equals("Item")){
				Item item = new Item(br.readLine(), br.readLine(), br.readLine(), Long.parseLong(br.readLine()), Long.parseLong(br.readLine()),
						br.readLine(), br.readLine(), UUID.fromString(br.readLine()));
				item.setImagePath(br.readLine());

				if(new File(item.getImagePath()).exists()){
					BufferedImage originalImage = ImageIO.read(new File(
							item.getImagePath()));

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(originalImage, "jpg", baos);
					baos.flush();
					imagebyte = baos.toByteArray();
					baos.close();

					item.setImageBytes(imagebyte);
				}

				if(br.readLine().equals("Bids:")){
					while((newLine = br.readLine()) != null){
						String[] parts = newLine.split(",");
						Bid newBid = new Bid(Double.valueOf(parts[0]), parts[1], item);
						item.bids.add(newBid);
						item.setCurrentBid(newBid);
					}
				}
				return item;
			}
			return null;
		}
		return null;
	}

	public Item getFinishedItem(File f) throws IOException{
		String newLine = null;

		br = new BufferedReader(new FileReader("Items/Finished/"+ f.getName()));
		if((newLine = br.readLine()) != null){
			if(newLine.equals("Item")){
				Item item = new Item(br.readLine(), br.readLine(), br.readLine(), Long.parseLong(br.readLine()), Long.parseLong(br.readLine()),
						br.readLine(), br.readLine(), UUID.fromString(br.readLine()));
				if(br.readLine().equals("Bids:")){
					while((newLine = br.readLine()) != null){
						String[] parts = newLine.split(",");
						Bid newBid = new Bid(Double.valueOf(parts[0]), parts[1], item);
						item.bids.add(newBid);
						item.setCurrentBid(newBid);
					}
				}
				return item;
			}
			return null;
		}
		return null;
	}

	public void moveFinishedItem(Item item){
		File dir = new File("Items");
		if(!dir.exists())
			dir.mkdirs();

		for(File f : dir.listFiles()){
			if(!(f.isDirectory())){
				if(f.getName().equals(String.valueOf(item.getID() + ".txt"))){
					try {
						Files.move(f.toPath(), new File("Items/Finished/" + f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
						f.delete();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(f.getName().equals(String.valueOf(item.getID() + ".jpg"))){
					f.delete();
				}
			}
		}
	}

	public void writeToWinLog(Item item) throws IOException{
		File log = new File("WinningLog");
		if(!log.exists()){
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 

		try {
			outputwriter = new FileWriter(log, true);
			outputwriter.write(item.getTitle() + "," + item.getCurrentBid().getID() + "," + item.getUserID() + "," + item.getCurrentBid().getBid()+"\n");
		} finally{
			if(outputwriter != null){
				outputwriter.close();
			}
		}
	}

	public String[][] writeTable() throws IOException{

		String[][] rows = new String[200][4];
		String string;
		int line = 0;

		File log = new File("WinningLog");

		if(log.exists()){

			br = new BufferedReader(new FileReader("WinningLog"));

			while((string = br.readLine()) != null){
				String[] info = string.split(",");
				rows[line][0] = info[0];
				rows[line][1] = info[1];
				rows[line][2] = info[2];
				rows[line][3] = info[3];

				line++;
				
			}
			br.close();
			return rows;
		} else{
			return new String[10][4];
		}
	}

	public int addPenalty(String userID) throws IOException{
		File dir = new File("Users");
		String file = "";
		String newLine = null;
		String penaltyline = null;
		String[] line = null;
		int point = 0;

		for(File f : dir.listFiles()){
			if(!(f.isDirectory())){
				if(f.getName().equals(userID)){

					br = new BufferedReader(new FileReader("Users/" + userID));
					while((newLine = br.readLine()) != null){
						file += newLine + System.lineSeparator();
						if(newLine.startsWith("Penalty")){
							penaltyline = newLine;
							line = newLine.split(",");
							point = Integer.valueOf(line[1]);
						}
					}

					file = file.replaceAll(penaltyline, "Penalty," + (point+1));

					FileOutputStream os = new FileOutputStream("Users/" + userID);
					os.write(file.getBytes());

					os.close();

					return point;
				}
			}
		}
		return -1;
	}

	public int getPenalty(String userID) throws IOException{

		String newLine = null;
		String[] line = null;
		int point = 0;

		File dir = new File("Users");
		for(File f : dir.listFiles()){
			if(!(f.isDirectory())){
				if(f.getName().equals(userID)){

					br = new BufferedReader(new FileReader("Users/" + userID));
					while((newLine = br.readLine()) != null){
						if(newLine.startsWith("Penalty")){
							line = newLine.split(",");
							point = Integer.valueOf(line[1]);
							return point;
						}
					}
				}

			}
		}
		return -1;
	}

	public void createClientImage(Item item) throws IOException{
		File dir = new File("ClientItems");
		if(!dir.exists())
			dir.mkdirs();

		File image = new File("ClientItems/" + item.getID() + ".jpg");

		if(item.getImageBytes() != null){
			InputStream in = new ByteArrayInputStream(item.getImageBytes());
			BufferedImage BIimage = ImageIO.read(in);
			ImageIO.write(BIimage, "jpg", image);
		}
	}
}
