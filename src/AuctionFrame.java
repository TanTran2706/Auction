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
		JLabel searchlabel = new JLabel("T??m ki???m:");
		JTextField searchBar = new JTextField(20);
		String[] searches = { "---", "T??n s???n ph???m", "Ng?????i b??n", "M?? s???n ph???m", "M?? t???" };
		JComboBox<String> searchbox = new JComboBox<String>(searches);
		JButton notify = new JButton("Th??ng b??o");
		notify.setBorder(null);
		notify.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				notifyframe = new NotificationFrame("Th??ng b??o");
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

		String[] accountString = { "Trang ch???", "T??i kho???n c???a b???n", "T???o v???t ph???m ?????u gi??" };
		JComboBox<String> account = new JComboBox(accountString);
		account.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (account.getSelectedItem().toString().equalsIgnoreCase("T???o v???t ph???m ?????u gi??")) {
					create = new ItemCreateFrame("Create Item");
				}
				if (account.getSelectedItem().toString().equalsIgnoreCase("T??i kho???n c???a b???n")) {
					accountframe = new AccountFrame("Account");
				}
			}

		});

		search.add(account);
		search.add(notify);

		JPanel catergories = new JPanel();
		JLabel catLabel = new JLabel("Th??? lo???i: ");
		catergories.add(catLabel);
		JButton all = new JButton("T???t c???");
		all.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				category = "---";
				drawItems();
			}

		});
		catergories.add(all);
		JButton fashion = new JButton("Th???i trang");
		fashion.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonCriteria = "Th???i trang";
				drawItems();
				buttonCriteria = null;
			}

		});
		catergories.add(fashion);
		JButton electronics = new JButton("Thi???t b??? ??i???n t???");
		electronics.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonCriteria = "Thi???t b??? ??i???n t???";
				drawItems();
				buttonCriteria = null;
			}

		});
		catergories.add(electronics);
		JButton beauty = new JButton("L??m ?????p");
		beauty.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonCriteria = "L??m ?????p";
				drawItems();
				buttonCriteria = null;
			}

		});
		catergories.add(beauty);
		JButton home = new JButton("Nh?? c???a");
		home.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonCriteria = "Nh?? c???a";
				drawItems();
				buttonCriteria = null;
			}

		});
		catergories.add(home);
		JButton books = new JButton("S??ch");
		books.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonCriteria = "S??ch";
				drawItems();
				buttonCriteria = null;
			}

		});
		catergories.add(books);
		JButton outdoors = new JButton("Thi???t b??? du l???ch");
		outdoors.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonCriteria = "Thi???t b??? du l???ch";
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
			case "T??n s???n ph???m":
				if (searchCriteria != null) {
					if (i.getTitle().toLowerCase().contains(searchCriteria.toLowerCase())) {
						buttonDrawing(i);
					}
				}
				break;
			case "Ng?????i b??n":
				if (searchCriteria != null) {
					if (i.getUserID().toLowerCase().contains(searchCriteria.toLowerCase())) {
						buttonDrawing(i);
					}
				}
				break;
			case "M?? s???n ph???m":
				if (searchCriteria != null) {
					if (i.getID().toString().toLowerCase().contains(searchCriteria.toLowerCase())) {
						buttonDrawing(i);
					}
				}
				break;
			case "M?? t???":
				if (searchCriteria != null) {
					if (i.getDescription().toString().toLowerCase().contains(searchCriteria.toLowerCase())) {
						buttonDrawing(i);
					}
				}
				break;
			}

			if (buttonCriteria != null) {
				switch (buttonCriteria) {
				case "Th???i trang":
					if (i.getCategory().equals(buttonCriteria)) {
						buttonDrawing(i);
					}
					break;
				case "Thi???t b??? ??i???n t???":
					if (i.getCategory().equals(buttonCriteria)) {
						buttonDrawing(i);
					}
					break;
				case "L??m ?????p":
					if (i.getCategory().equals(buttonCriteria)) {
						buttonDrawing(i);
					}
					break;
				case "Nh?? c???a":
					if (i.getCategory().equals(buttonCriteria)) {
						buttonDrawing(i);
					}
					break;
				case "S??ch":
					if (i.getCategory().equals(buttonCriteria)) {
						buttonDrawing(i);
					}
					break;
				case "Thi???t b??? du l???ch":
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
		itemButton = new JButton("<html><center>" + i.getTitle() + "<br>" + "???????c t???o b???i: " + i.getUserID() + "<br>"
				+ "Gi?? kh???i ??i???m: " + i.rPrice + "VND" );
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
		JButton bid = new JButton("Tr??? gi??");
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
						BidEFrame frame = new BidEFrame("Tr??? gi?? th???t b???i");
					}
				}
				else {
					if (item.getUserID().equals(userID)) {
						EFrame error = new EFrame("Tr??? gi?? th???t b???i");
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

		JLabel bidlabel = new JLabel("<html><center>" + "Gi?? th???u:" + "<br>");
		bidlabel.setHorizontalAlignment(JLabel.CENTER);
		bidlabel.setFont(new Font(bidlabel.getFont().getFontName(), Font.BOLD, 12));
		bids.add(bidlabel);

		for (Bid b : item.bids) {
			JLabel text = new JLabel();
			text.setText("Gi?? th???u c???a: " + b.getBid() + ", t??? ng?????i d??ng: " + b.getID());
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

		JLabel infolabel = new JLabel("<html><center>" + "Th??ng tin v???t ph???m:" + "<br>");
		infolabel.setHorizontalAlignment(JLabel.CENTER);
		infolabel.setFont(new Font(infolabel.getFont().getFontName(), Font.BOLD, 12));

		JLabel userlabel = new JLabel("Ch??? s??? h???u " + item.getUserID());
		JLabel reserve = new JLabel("Gi?? hi???n t???i: " + item.rPrice);
		JLabel idcode = new JLabel("M?? s???n ph???m:");
		JLabel idcode2 = new JLabel("  " + item.getID());
		JLabel cater = new JLabel("Lo???i m???t h??ng: " + item.getCategory());

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
			String exception = "<html><center>L???i. B???n kh??ng th??? tr??? gi?? tr??n m???t h??ng c???a ri??ng b???n.";

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
			String exception = "<html><center>Gi?? th???u ???? th???p h??n gi?? th???u hi???n t???i, vui l??ng th??? l???i";

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
