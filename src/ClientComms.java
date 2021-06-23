
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

public class ClientComms {

	int port = 1050;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	String serverName = "localhost";
	ArrayList<Message> messages;
	Object object = new Object();
	DataPersistence data = new DataPersistence();

	public ClientComms(){
		messages = new ArrayList<Message>();
		Socket clientSocket;

		try {
			clientSocket = new Socket(serverName, port);
			output = new ObjectOutputStream(clientSocket.getOutputStream());
			output.flush();
			input = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Timer t = new Timer();
		TimerTask task = new MyTimer();
		t.schedule(task, 0, 50);
	}


	public void sendMessage(Message message){
		try {
			output.writeObject(message);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public Message recieveMessage(){

		Message message = null;

		try {
			message = (Message) input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		return message;
	}

	public Message listenForMessage(){
		Message message = null;

		int i = 0;
		while(i < 80){
			synchronized(object){
				if(messages.size() != 0){
					message = messages.get(0);
					messages.remove(message);
					return message;
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
		}	
		System.out.println("Hết giờ");
		return null;
	}

	public class MyTimer extends TimerTask{

		public void run() {
			Message message = null;

			if((message = recieveMessage()) != null){
				synchronized(object){

					messages.add(message);
				}
			}

			switch(message.type){
			case UPDATEITEMS:
				if(Client.mainFrame instanceof AuctionFrame){
					ObjectMessage newItem = (ObjectMessage) message;
					Item item = (Item) newItem.getObject();

					AuctionFrame.items.add(item);

					try {
						data.createClientImage(item);
					} catch (IOException e) {
						e.printStackTrace();
					}

					if(AuctionFrame.create != null){
						AuctionFrame.create.dispose();
					}

					((AuctionFrame) Client.mainFrame).init();

					if(messages.size() > 0){
						messages.remove(0);
					}
				}
				break;
			case UPDATEBIDS:
				if(Client.mainFrame instanceof AuctionFrame){
					ObjectMessage newItem = (ObjectMessage) message;
					Item item = (Item) newItem.getObject();

					for(Item i : AuctionFrame.items){
						if(i.getTitle().equals(item.getTitle())){
							AuctionFrame.items.remove(i);
							break;
						}
					}

					for(Item i : AuctionFrame.items){
						if(i.equals(item)){
							AuctionFrame.itemButton.doClick();
						}
					}

					AuctionFrame.items.add(item);

					if(AuctionFrame.create != null){
						AuctionFrame.create.dispose();
					}

					((AuctionFrame) Client.mainFrame).init();

					if(messages.size() > 0){
						messages.remove(0);
					}
				}
				break;
			case ITEMEND:
				if(Client.mainFrame instanceof AuctionFrame){
					ObjectMessage newItem = (ObjectMessage) message;
					Item item = (Item) newItem.getObject();

					AuctionFrame.finished.add(item);

					for(Item i : AuctionFrame.items){
						if(i.getTitle().equals(item.getTitle())){
							AuctionFrame.items.remove(i);
							break;
						}
					}

					((AuctionFrame) Client.mainFrame).init();

					if(messages.size() > 0){
						messages.remove(0);
					}
				}
				break;
			case NOTIFYBID:
				if(Client.mainFrame instanceof AuctionFrame){
					NotificationMessage notify = (NotificationMessage) message;
					Item item = (Item) notify.getItem();
					Bid bid = (Bid) notify.getBid();

					if(AuctionFrame.userID.equals(item.getUserID())){
						AuctionFrame.messages.add(bid.getID() + " đã đặt giá thầu " + bid.getBid() + "VND" + " trên " + item.getTitle());
						if(AuctionFrame.notifyframe != null){
							AuctionFrame.notifyframe.init();
							AuctionFrame.notifyframe.repaint();
						}
					}
					SwingUtilities.invokeLater(new Runnable(){

						@Override
						public void run() {
							AuctionFrame.top.validate();
							AuctionFrame.top.repaint();
						}
						
					});
					
					if(messages.size() > 0){
						messages.remove(0);
					}
				}
				break;
			case NOTIFYWIN:
				if(Client.mainFrame instanceof AuctionFrame){
					NotificationMessage notify = (NotificationMessage) message;
					Item item = (Item) notify.getItem();
					Bid bid = (Bid) notify.getBid();

					if(AuctionFrame.userID.equals(item.getUserID())){
						AuctionFrame.messages.add(bid.getID() + " đã giành được món đồ " + item.getTitle() + " với giá thầu là " +  bid.getBid()+ "VND" );
						if(AuctionFrame.notifyframe != null){
							AuctionFrame.notifyframe.init();
							AuctionFrame.notifyframe.repaint();
						}
					}

					if(AuctionFrame.userID.equals(bid.getID())){
						AuctionFrame.messages.add("đã giành được món đồ " + item.getTitle() + " với giá thầu là " + bid.getBid()+ "VND" );
						if(AuctionFrame.notifyframe != null){
							AuctionFrame.notifyframe.init();
							AuctionFrame.notifyframe.repaint();
						}
					}

					AuctionFrame.finished.add(item);

					for(Item i : AuctionFrame.items){
						if(i.getTitle().equals(item.getTitle())){
							AuctionFrame.items.remove(i);
							break;
						}
					}

					((AuctionFrame) Client.mainFrame).init();

					if(messages.size() > 0){
						messages.remove(0);
					}
				}
				break;
			case NOTIFYFAIL:
				if(Client.mainFrame instanceof AuctionFrame){
					NotificationMessage notify = (NotificationMessage) message;
					Item item = (Item) notify.getItem();
					Bid bid = (Bid) notify.getBid();

					if(AuctionFrame.userID.equals(item.getUserID())){
						AuctionFrame.messages.add(item.getTitle() + " đã thất bại vì giá khởi điểm không được đáp ứng");
						if(AuctionFrame.notifyframe != null){
							AuctionFrame.notifyframe.init();
							AuctionFrame.notifyframe.repaint();
						}
					}

					AuctionFrame.finished.add(item);

					for(Item i : AuctionFrame.items){
						if(i.getTitle().equals(item.getTitle())){
							AuctionFrame.items.remove(i);
							break;
						}
					}

					((AuctionFrame) Client.mainFrame).init();

					if(messages.size() > 0){
						messages.remove(0);
					}
				}
				break;
			case ITEMCLOSE:
				if(Client.mainFrame instanceof AuctionFrame){
					ObjectMessage object = (ObjectMessage) message;
					Item item = (Item) object.getObject();

					AuctionFrame.finished.add(item);

					for(Item i : AuctionFrame.items){
						if(i.getTitle().equals(item.getTitle())){
							AuctionFrame.items.remove(i);
							break;
						}
					}

					((AuctionFrame) Client.mainFrame).init();

					if(messages.size() > 0){
						messages.remove(0);
					}
				}
				break;
			case CLOSESERVER:
				System.exit(0);
			}
		}

	}

}
