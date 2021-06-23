import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AuctionFrame extends JFrame {

	static public String userID = null;
	static ArrayList<Item> items;
	static ArrayList<Item> finished;
	static JPanel scrollingPanel;
	JPanel itemDisplay;
	static JButton itemButton;
	static JPanel panel;
	DataPersistence dp = new DataPersistence();
	static ItemCreateFrame create;
	static AccountFrame accountframe;
	String category = "---";
	String searchCriteria = null;
	String buttonCriteria = null;
	JPanel information = null;
	Item buttonclick = null;
	JPanel bottom;
	static ArrayList<String> messages;
	static NotificationFrame notifyframe;
	static JPanel top;
	BufferedImage image;

	public AuctionFrame(String title, String newUserID) {
		super(title);
		userID = newUserID;
		messages = new ArrayList<String>();

		this.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
				Client.comms
						.sendMessage(new ObjectMessage(MessageType.SHUTDOWNCLIENT, new String(AuctionFrame.userID)));
				System.out.println("Closing client, sending message to server");
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
		});

		ArrayList<Item> inputItems;
		try {
			inputItems = (ArrayList<Item>) dp.createItemList();
			items = new ArrayList<Item>(new LinkedHashSet<Item>(inputItems));
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<Item> finishedinput;
		try {
			finishedinput = (ArrayList<Item>) dp.createFinishedItemList();
			finished = new ArrayList<Item>(new LinkedHashSet<Item>(finishedinput));
		} catch (IOException e) {
			e.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		init();
		setLocationRelativeTo(null);

		Timer t = new Timer();
		TimerTask task = new AccountTimer();
		t.schedule(task, 0, 1000);
	}

	public void init() {

		panel = new JPanel();
		setContentPane(panel);
		setResizable(false);
		setMinimumSize(new Dimension(900, 600));
		revalidate();

		panel.setLayout(new BorderLayout());

		top = initTop();
		JPanel center = initCenter();
		drawItems();

		panel.add(top, BorderLayout.NORTH);
		panel.add(center, BorderLayout.CENTER);

		setVisible(true);
	}

	private JPanel initTop() {
		JPanel topPanel = new JPanel(new GridLayout(2, 0));

		JPanel search = new JPanel();
		JLabel searchlabel = new JLabel("Tìm kiếm:");
		JTextField searchBar = new JTextField(20);
		String[] searches = { "---", "Tên sản phẩm", "Người bán", "Mã sản phẩm", "Mô tả" };
		JComboBox<String> searchbox = new JComboBox<String>(searches);
		JButton notify = new JButton("Thông báo");
		notify.setBorder(null);
		notify.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				notifyframe = new NotificationFrame("Thông báo");
			}

		});

		searchBar.getDocument().addDocumentListener(new DocumentListener() {

			public void insertUpdate(DocumentEvent e) {
				category = searchbox.getSelectedItem().toString();
				searchCriteria = searchBar.getText();
				drawItems();
				searchCriteria = null;
			}

			public void removeUpdate(DocumentEvent e) {
				category = searchbox.getSelectedItem().toString();
				searchCriteria = searchBar.getText();
				drawItems();
				searchCriteria = null;
			}

			public void changedUpdate(DocumentEvent e) {
			}

		});

		search.add(searchlabel);
		search.add(searchbox);
		search.add(searchBar);

		String[] accountString = { "Trang chủ", "Tài khoản của bạn", "Tạo vật phẩm đấu giá" };
		JComboBox<String> account = new JComboBox(accountString);
		account.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (account.getSelectedItem().toString().equalsIgnoreCase("Tạo vật phẩm đấu giá")) {
					create = new ItemCreateFrame("Create Item");
				}
				if (account.getSelectedItem().toString().equalsIgnoreCase("Tài khoản của bạn")) {
					accountframe = new AccountFrame("Account");
				}
			}

		});

		search.add(account);
		search.add(notify);

		JPanel catergories = new JPanel();
		JLabel catLabel = new JLabel("Thể loại: ");
		catergories.add(catLabel);
		JButton all = new JButton("Tất cả");
		all.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				category = "---";
				drawItems();
			}

		});
		catergories.add(all);
		JButton fashion = new JButton("Thời trang");
		fashion.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonCriteria = "Thời trang";
				drawItems();
				buttonCriteria = null;
			}

		});
		catergories.add(fashion);
		JButton electronics = new JButton("Thiết bị điện tử");
		electronics.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonCriteria = "Thiết bị điện tử";
				drawItems();
				buttonCriteria = null;
			}

		});
		catergories.add(electronics);
		JButton beauty = new JButton("Làm đẹp");
		beauty.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonCriteria = "Làm đẹp";
				drawItems();
				buttonCriteria = null;
			}

		});
		catergories.add(beauty);
		JButton home = new JButton("Nhà cửa");
		home.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonCriteria = "Nhà cửa";
				drawItems();
				buttonCriteria = null;
			}

		});
		catergories.add(home);
		JButton books = new JButton("Sách");
		books.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonCriteria = "Sách";
				drawItems();
				buttonCriteria = null;
			}

		});
		catergories.add(books);
		JButton outdoors = new JButton("Thiết bị du lịch");
		outdoors.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonCriteria = "Thiết bị du lịch";
				drawItems();
				buttonCriteria = null;
			}

		});
		catergories.add(outdoors);

		topPanel.add(search);
		topPanel.add(catergories);
		return topPanel;
	}

	private JPanel initCenter() {
		scrollingPanel = new JPanel();
		JScrollPane scroll = new JScrollPane(scrollingPanel);
		scrollingPanel.setMinimumSize(new Dimension((this.getWidth() / 10) * 3, 600));
		scrollingPanel.setPreferredSize(new Dimension((this.getWidth() / 10) * 3, items.size() * 120));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		itemDisplay = new JPanel();
		itemDisplay.setBorder(BorderFactory.createLoweredBevelBorder());

		JPanel itemPanel = new JPanel(new BorderLayout());
		itemPanel.add(scroll, BorderLayout.WEST);
		itemPanel.add(itemDisplay, BorderLayout.CENTER);

		return itemPanel;
	}

	private synchronized void drawItems() {

		items = new ArrayList<Item>(new LinkedHashSet<Item>(items));
		scrollingPanel.removeAll();
		itemDisplay.removeAll();

		Collections.sort(items, new Comparator<Item>() {

			public int compare(Item o1, Item o2) {
				return o1.getTitle().compareToIgnoreCase(o2.getTitle());
			}

		});

		for (Item i : items) {
			switch (category) {
			case "---":
				if (buttonCriteria == null) {
					buttonDrawing(i);
				}
				break;
			case "Tên sản phẩm":
				if (searchCriteria != null) {
					if (i.getTitle().toLowerCase().contains(searchCriteria.toLowerCase())) {
						buttonDrawing(i);
					}
				}
				break;
			case "Người bán":
				if (searchCriteria != null) {
					if (i.getUserID().toLowerCase().contains(searchCriteria.toLowerCase())) {
						buttonDrawing(i);
					}
				}
				break;
			case "Mã sản phẩm":
				if (searchCriteria != null) {
					if (i.getID().toString().toLowerCase().contains(searchCriteria.toLowerCase())) {
						buttonDrawing(i);
					}
				}
				break;
			case "Mô tả":
				if (searchCriteria != null) {
					if (i.getDescription().toString().toLowerCase().contains(searchCriteria.toLowerCase())) {
						buttonDrawing(i);
					}
				}
				break;
			}

			if (buttonCriteria != null) {
				switch (buttonCriteria) {
				case "Thời trang":
					if (i.getCategory().equals(buttonCriteria)) {
						buttonDrawing(i);
					}
					break;
				case "Thiết bị điện tử":
					if (i.getCategory().equals(buttonCriteria)) {
						buttonDrawing(i);
					}
					break;
				case "Làm đẹp":
					if (i.getCategory().equals(buttonCriteria)) {
						buttonDrawing(i);
					}
					break;
				case "Nhà cửa":
					if (i.getCategory().equals(buttonCriteria)) {
						buttonDrawing(i);
					}
					break;
				case "Sách":
					if (i.getCategory().equals(buttonCriteria)) {
						buttonDrawing(i);
					}
					break;
				case "Thiết bị du lịch":
					if (i.getCategory().equals(buttonCriteria)) {
						buttonDrawing(i);
					}
					break;
				}
			}
		}

		scrollingPanel.validate();
		scrollingPanel.repaint();
	}

	private void buttonDrawing(Item i) {
		itemButton = new JButton("<html><center>" + i.getTitle() + "<br>" + "được tạo bởi: " + i.getUserID() + "<br>"
				+ "Giá khởi điểm: " + i.rPrice + "VND" );
		itemButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonclick = i;
				itemDisplay.removeAll();
				itemPanel(buttonclick);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						validate();
						repaint();
					}
				});
			}
		});

		itemButton.setPreferredSize(new Dimension(((this.getWidth() / 10) * 3) - 30, 100));
		scrollingPanel.add(itemButton);
	}

	private void itemPanel(Item item) {

		JPanel display = new JPanel();
		display.setLayout(new BoxLayout(display, BoxLayout.PAGE_AXIS));

		JPanel description = new JPanel();

		JTextArea descrip = new JTextArea(5, 17);
		descrip.setLineWrap(true);
		descrip.setWrapStyleWord(true);
		descrip.setText(item.getDescription());
		descrip.setEditable(false);
		descrip.setBackground(null);

		description.add(descrip);

		Thread t = new Thread(new Runnable() {

			public void run() {
				if (item.getImagePath() != null && new File(item.getImagePath()).exists()) {
					try {
						image = ImageIO.read(new File(item.getImagePath()));
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					Image newimage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
					ImageIcon newicon = new ImageIcon(newimage);

					JLabel piclabel = new JLabel(newicon);
					description.add(piclabel);
				}
			}
		});
		t.start();

		int panelwidth = (int) (itemDisplay.getWidth() / 2.0);
		int panelheight = (int) (itemDisplay.getHeight() / 1.6);

		JLabel title = new JLabel(item.getTitle());
		title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 14));
		JPanel titlep = new JPanel();
		titlep.add(title);

		JPanel bidding = new JPanel();
		JButton bid = new JButton("Trả giá");
		JTextField bidprice = new JTextField(5);
		bidding.add(bidprice);
		bidding.add(bid);
		bid.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Double price = Double.valueOf(bidprice.getText());
				if (item.getCurrentBid() != null) {
					if (item.getCurrentBid().getBid() < price) {
						if (item.getUserID().equals(userID)) {
							EFrame error = new EFrame("Bidding Error");
						} else {
							Bid bid = new Bid(price, userID, item);
							ObjectMessage message = new ObjectMessage(MessageType.NEWBID, bid);
							Client.comms.sendMessage(message);
						}
					} else {
						BidEFrame frame = new BidEFrame("Trả giá thất bại");
					}
				}
				else {
					if (item.getUserID().equals(userID)) {
						EFrame error = new EFrame("Trả giá thất bại");
					} else {
						Bid bid = new Bid(price, userID, item);
						ObjectMessage message = new ObjectMessage(MessageType.NEWBID, bid);
						Client.comms.sendMessage(message);
					}
				}
			}

		});

		JPanel bids = new JPanel();
		bids.setLayout(new BoxLayout(bids, BoxLayout.PAGE_AXIS));
		bids.setPreferredSize(new Dimension((int) (itemDisplay.getWidth() / 2.8), panelheight));

		JLabel bidlabel = new JLabel("<html><center>" + "Giá thầu:" + "<br>");
		bidlabel.setHorizontalAlignment(JLabel.CENTER);
		bidlabel.setFont(new Font(bidlabel.getFont().getFontName(), Font.BOLD, 12));
		bids.add(bidlabel);

		for (Bid b : item.bids) {
			JLabel text = new JLabel();
			text.setText("Giá thầu của: " + b.getBid() + ", từ người dùng: " + b.getID());
			bids.add(text);
		}

		bottom = new JPanel();
		information = new JPanel();
		bottom.add(bids);
		createInformation(item);

		display.add(titlep);
		display.add(Box.createRigidArea(new Dimension(0, 20)));
		display.add(description);
		display.add(bidding);
		display.add(bottom);
		itemDisplay.add(display);
	}

	private void createInformation(Item item) {
		int panelwidth = (int) (itemDisplay.getWidth() / 2.0);
		int panelheight = (int) (itemDisplay.getHeight() / 1.6);

		information.setLayout(new BoxLayout(information, BoxLayout.PAGE_AXIS));
		information.setPreferredSize(new Dimension(panelwidth, panelheight));

		JLabel infolabel = new JLabel("<html><center>" + "Thông tin vật phẩm:" + "<br>");
		infolabel.setHorizontalAlignment(JLabel.CENTER);
		infolabel.setFont(new Font(infolabel.getFont().getFontName(), Font.BOLD, 12));

		JLabel userlabel = new JLabel("Chủ sở hữu " + item.getUserID());
		JLabel reserve = new JLabel("Giá hiện tại: " + item.rPrice);
		JLabel idcode = new JLabel("Mã sản phẩm:");
		JLabel idcode2 = new JLabel("  " + item.getID());
		JLabel cater = new JLabel("Loại mặt hàng: " + item.getCategory());

		long length = item.getTimeLeftSec();
		long hours;
		String mins, sec;
		String fmins = null, fsec = null;
		if ((hours = (length / 3600)) < 0) {
			hours = 0;
		} else {
			length = length % 3600;
		}
		if (((length / 60)) < 0) {
			mins = "00";
		} else {
			mins = String.valueOf(length / 60);
			fmins = String.format("00", mins);
			length = length % 60;
		}
		sec = String.valueOf(length);
		fsec = String.format("00", sec);

		JLabel endtime = new JLabel("Time Till End: " + hours + ":" + mins + ":" + sec);

		information.add(infolabel);
		information.add(userlabel);
		information.add(reserve);
		information.add(idcode);
		information.add(idcode2);
		information.add(cater);
		information.add(endtime);

		bottom.add(information);
	}

	public class EFrame extends JFrame {

		public EFrame(String title) {
			super(title);
			init();
		}

		public void init() {

			setSize(400, 50);
			JPanel e = new JPanel();
			e.setLayout(new GridBagLayout());
			JLabel exceptionlabel = new JLabel();
			String exception = "<html><center>Lỗi. Bạn không thể trả giá trên mặt hàng của riêng bạn.";

			exceptionlabel.setText(exception);

			setLocationRelativeTo(null);
			e.add(exceptionlabel);
			add(e);

			setVisible(true);
		}

	}

	public class BidEFrame extends JFrame {

		public BidEFrame(String title) {
			super(title);
			init();
		}

		public void init() {

			setSize(450, 50);
			JPanel e = new JPanel();
			e.setLayout(new GridBagLayout());
			JLabel exceptionlabel = new JLabel();
			String exception = "<html><center>Giá thầu đã thấp hơn giá thầu hiện tại, vui lòng thử lại";

			exceptionlabel.setText(exception);

			setLocationRelativeTo(null);
			e.add(exceptionlabel);
			add(e);

			setVisible(true);
		}
	}

	public class AccountTimer extends TimerTask {

		public void run() {
			for (Item i : items) {
				i.timeleft -= 1000;
				if (i.equals(buttonclick)) {
					if (information != null) {
						information.removeAll();
						createInformation(buttonclick);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								validate();
								repaint();
							}
						});
					}
				}
			}
		}
	}

}
