
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;

public class ServerComms implements Runnable {

	int port = 1050;
	ServerSocket serverSocket;
	ArrayList<ServerThread> threads = new ArrayList<ServerThread>();
	static ArrayList<Item> items = new ArrayList<Item>();
	DataPersistence data = new DataPersistence();

	public void stopServer() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Đang đợi kết nối\n");
			while (true) {
				Socket socket = serverSocket.accept();
				ServerThread thread = new ServerThread(socket);
				threads.add(thread);
				thread.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeClients() {
		for (ServerThread t : threads) {
			t.sendMessage(new ObjectMessage(MessageType.CLOSESERVER, new String("Đóng Server")));
		}
	}

	public void terminateClient(ServerThread t, Object o) {
		threads.remove(t);
		String s = (String) o;
		Server.display.append(o + " đã ngắt kết nối với máy chủ");
	}

	public void updateClients(Item item) {
		for (ServerThread t : threads) {
			t.sendMessage(new ObjectMessage(MessageType.UPDATEITEMS, item));
		}
	}

	public void itemCloseClients(Item item) {
		for (ServerThread t : threads) {
			t.sendMessage(new ObjectMessage(MessageType.ITEMCLOSE, item));
		}
	}

	public void updateItemsClients(Item item) {

		boolean thread = false;

		for (ServerThread t : threads) {

			if (item.getCurrentBid() != null && item.getCurrentBid().getBid() > Double.valueOf(item.rPrice)) {
				thread = true;
				t.sendMessage(new NotificationMessage(MessageType.NOTIFYWIN, item.getCurrentBid(), item));
			} else {
				thread = false;
				t.sendMessage(new NotificationMessage(MessageType.NOTIFYFAIL, item.getCurrentBid(), item));
			}
		}

		if (thread) {
			try {
				data.moveFinishedItem(item);
				data.writeToWinLog(item);
				Server.display
						.append("User " + item.getCurrentBid().getID() + " đã giành được vật phẩm " + item.getTitle() + ". \n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		} else {
			data.moveFinishedItem(item);
			Server.display.append("Vật phẩm " + item.getTitle() + " đấu giá thất bại. \n");
			return;
		}
	}

	public void updateClientBids(Item item, Bid bid) {
		for (ServerThread t : threads) {
			t.sendMessage(new ObjectMessage(MessageType.UPDATEBIDS, item));
		}
	}

	public void notifyUserBid(ServerThread thread, Bid bid, Item item) {
		for (ServerThread t : threads) {
			if (!(t.equals(thread)))
				t.sendMessage(new NotificationMessage(MessageType.NOTIFYBID, bid, item));
		}

	}

	public class ServerThread extends Thread {

		ObjectOutputStream output;
		ObjectInputStream input;
		Socket connection;

		BufferedReader br;

		public ServerThread(Socket s) {
			connection = s;
		}

		public void run() {
			try {
				output = new ObjectOutputStream(connection.getOutputStream());
				output.flush();
				input = new ObjectInputStream(connection.getInputStream());
			} catch (IOException e) {
			}

			Message message;

			while (true) {
				if ((message = recieveMessage()) != null) {
					parseMessage(message);
				}
			}

		}

		public void sendMessage(Message message) {

			try {
				output.writeObject(message);
				output.flush();
			} catch (IOException e) {
			}

		}

		public Message recieveMessage() {

			Message message = null;

			try {
				message = (Message) input.readObject();
			} catch (ClassNotFoundException | IOException e) {
			}

			return message;
		}

		public void parseMessage(Message message) {

			switch (message.type) {
			case SHUTDOWNCLIENT:
				terminateClient(this, ((ObjectMessage) message).getObject());
				break;
			case LOGIN:
				LoginMessage loginMessage = (LoginMessage) message;
				try {
					if (data.authenticateUser(loginMessage.ID, loginMessage.password)) {
						Server.display.append(loginMessage.ID + " đã đăng nhập vào máy chủ. \n");
						sendMessage(new ObjectMessage(MessageType.AUTHENTICATED, new String("Đăng nhập thành công")));
					} else {
						sendMessage(new ObjectMessage(MessageType.FAILED, new String("Đăng nhập thất bại")));
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			case USER:
				ObjectMessage newUser = (ObjectMessage) message;
				User user = (User) newUser.getObject();
				boolean create = false;
				try {
					create = data.createUserFile(user);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (create) {
					Server.display.append("New User " + user.getUserId() + " đã được tạo. \n");
					sendMessage(new ObjectMessage(MessageType.USERSUCCESS, "Đăng ký người dùng thành công"));
				} else {
					Server.display.append("Username " + user.getUserId() + " đã tồn tại. \n");
					sendMessage(new ObjectMessage(MessageType.USERFAIL, "Đăng ký người dùng không thành công"));
				}
				return;
			case ITEM:
				ObjectMessage newItem = (ObjectMessage) message;
				Item item = (Item) newItem.getObject();
				items.add(item);
				boolean itemb = false;
				try {
					itemb = data.createItemFile(item, item.getUserID());
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (itemb) {
					Server.display.append("Vật phẩm mới " + item.getTitle() + " đã được tạo. \n");
					updateClients(item);
				} else {
					Server.display.append("Không thể tạo vật phẩm đấu giá " + item.getUserID()
							+ " có nhiều hơn 2 điểm phạt \n");
					sendMessage(new ObjectMessage(MessageType.ITEMFAIL, item));
				}
				return;
			case NEWBID:
				ObjectMessage newBid = (ObjectMessage) message;
				Bid bid = (Bid) newBid.getObject();

				Item bidItem = null;
				try {
					bidItem = data.addBid(bid);
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					items = (ArrayList<Item>) data.createItemList();
				} catch (IOException e) {
					e.printStackTrace();
				}

				Server.display.append("Vật phẩm " + bidItem.getTitle() + " có một giá thầu mới là \u00A3" + bid.getBid()
						+ " from " + bid.getID() + ". \n");

				updateClientBids(bidItem, bid);
				notifyUserBid(this, bid, bidItem);
				return;
			case PENALTY:
				ObjectMessage thismessage = (ObjectMessage) message;
				Item newitem = (Item) thismessage.getObject();
				String userID = newitem.getUserID();

				int point = 0;

				if (newitem.getCurrentBid() != null) {
					if (newitem.getCurrentBid().getBid() > Integer.valueOf(newitem.rPrice)) {
						try {
							point = data.addPenalty(userID);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				if (point < 0) {
					System.out.println("lỗi với pp");
				} else {
					items.remove(newitem);
					data.moveFinishedItem(newitem);
					Server.display.append("Vật phẩm " + newitem.getTitle() + " đã bị người bán rút lại. \n");
					itemCloseClients(newitem);
				}
			}

		}

	}
}