import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class AccountFrame extends JFrame{
	String choosebox = "Open Items";

	JPanel panel;
	JPanel center;
	JPanel items;
	JPanel olditems;
	int penalty = 0;
	DataPersistence data = new DataPersistence();

	public AccountFrame(String title){
		super(title);
		init();
	}

	public void init(){

		center = new JPanel();
		panel = new JPanel();


		setContentPane(panel);
		panel.setLayout(new BorderLayout());

		setSize(400,350);
		setMinimumSize(new Dimension(500,400));
		setLocationRelativeTo(null);

		center = new JPanel (new BorderLayout());
		center.setMinimumSize(new Dimension(400,350));
		center.setPreferredSize(new Dimension(400,350));

		center.add(openBidsPanel());
		panel.add(center, BorderLayout.CENTER);

		String[] strings = {"Vật phẩm đang đấu giá", "Đã bán", "Đang trả giá"};
		JComboBox<String> choose = new JComboBox<String>(strings);
		choose.addItemListener(new ItemListener(){


			@Override
			public void itemStateChanged(ItemEvent e) {

				if(e.getItem().equals("Vật phẩm đang đấu giá")){
					choosebox = "Vật phẩm đang đấu giá";
					center.removeAll();
					center.add(openBidsPanel());
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							validate();
							repaint();
						}
					});
				} else if(e.getItem().equals("Đã bán")){
					choosebox = "Old Items";
					center.removeAll();
					center.add(oldBidsPanel());
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							validate();
							repaint();
						}
					});
				} else if(e.getItem().equals("Đang trả giá")){
					choosebox = "My Bids";
					center.removeAll();
					JPanel bids = myBidsPanel();
					center.add(bids);
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							validate();
							repaint();
						}
					});
				}	
				panel.add(center, BorderLayout.CENTER);
			}

		});

		int point = 0;
		try {
			point = data.getPenalty(AuctionFrame.userID);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JLabel userpoint = new JLabel("Mức độ cảnh báo: " + point + "    ");

		JPanel choosepanel = new JPanel();
		choosepanel.setLayout(new BorderLayout());
		choosepanel.add(choose, BorderLayout.LINE_START);
		choosepanel.add(userpoint, BorderLayout.LINE_END);
		panel.add(choosepanel, BorderLayout.NORTH);


		setVisible(true);
	}

	private JScrollPane openBidsPanel(){
		items = new JPanel();
		JScrollPane itemscroll = new JScrollPane(items);
		itemscroll.setBorder(null);
		items.setMinimumSize(new Dimension(300, 350));
		items.setLayout(new BoxLayout(items, BoxLayout.PAGE_AXIS));

		JLabel itemtitle = new JLabel("<html><center> Vật phẩm của bạn đang đấu giá: <br>");
		itemtitle.setHorizontalAlignment(JLabel.CENTER);
		itemtitle.setFont(new Font(itemtitle.getFont().getFontName(), Font.BOLD, 14));
		items.add(itemtitle);

		int num = 0;

		for(Item i : AuctionFrame.items){
			if(i.getUserID().equals(AuctionFrame.userID)){
				num++;
				JButton item;
				if(i.getCurrentBid() != null){
					item = new JButton("<html><center>" + i.getTitle() + "<br>" + "Giá khởi điểm: " + i.rPrice + "VND" + "<br>" + 
							"Giá hiện tại: " + i.getCurrentBid().getBid() + "<br>"+ "Kết thúc: " + new Date(i.finish) + "<br>" + 
							"<b>Click to withdraw</b>");
				} else{
					item = new JButton("<html><center>" + i.getTitle() + "<br>" + "Giá khởi điểm: " + i.rPrice + "VND" + "<br>" + 
							"Giá hiện tại: " + "<br>"+ "Kết thúc: " + new Date(i.finish) + "<br>" + "<b>Bấm để rút sản phẩm</b>");
				}

				item.setBorder(BorderFactory.createRaisedSoftBevelBorder());
				item.setPreferredSize(new Dimension(300,80));

				item.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						ObjectMessage message = new ObjectMessage(MessageType.PENALTY, i);
						Client.comms.sendMessage(message);
						dispose();
					}

				});

				items.add(item);
			}
		}

		items.setPreferredSize(new Dimension(300, num*100));

		return itemscroll;
	}

	private JScrollPane oldBidsPanel(){
		olditems = new JPanel();
		JScrollPane itemscroll = new JScrollPane(olditems);
		itemscroll.setBorder(null);
		olditems.setMinimumSize(new Dimension(300, 350));
		olditems.setLayout(new BoxLayout(olditems, BoxLayout.PAGE_AXIS));

		JLabel itemtitle = new JLabel("<html><center> Vật phẩm đã bán: <br>");
		itemtitle.setHorizontalAlignment(JLabel.CENTER);
		itemtitle.setFont(new Font(itemtitle.getFont().getFontName(), Font.BOLD, 14));
		olditems.add(itemtitle);

		int num = 0;

		for(Item i : AuctionFrame.finished){
			if(i.getUserID().equals(AuctionFrame.userID)){
				num++;
				JButton item;
				if(i.getCurrentBid() != null){
					if(i.getCurrentBid().getBid() > Double.valueOf(i.rPrice)){
						item = new JButton("<html><center>" + i.getTitle() + "<br>" + "Giá khởi điểm: " + i.rPrice + "VND" + "<br>" + "Chốt đơn: \u00A3" + 
								i.getCurrentBid().getBid() + ", Từ: " + i.getCurrentBid().getID() +  "<br>"+ "Kết thúc: " + new Date(i.finish));
					}
					else{
						item = new JButton("<html><center>" + i.getTitle() + "<br>" + "Giá khởi điểm: " + i.rPrice + "VND" + "<br>" + "Chốt đơn: \u00A3" + 
								i.getCurrentBid().getBid() + ", Từ: " + i.getCurrentBid().getID() +  "<br>"+ "Kết thúc: " + new Date(i.finish));
					}
				} else{
					item = new JButton("<html><center>" + i.getTitle() + "<br>" + "Giá khởi điểm: " + i.rPrice + "VND" + "<br>" + 
							"Giá cuối cùng: " + "<br>"+ "Kết thúc: " + new Date(i.finish));
				}
				item.setBorder(BorderFactory.createRaisedSoftBevelBorder());
				item.setPreferredSize(new Dimension(300,80));
				olditems.add(item);
			}
		}

		olditems.setPreferredSize(new Dimension(300, num*100));

		return itemscroll;
	}

	private JPanel myBidsPanel(){
		JPanel bids = new JPanel();
		bids.setLayout(new BoxLayout(bids, BoxLayout.PAGE_AXIS));
		bids.setMinimumSize(new Dimension(200, 400));
		bids.setPreferredSize(new Dimension(200, 400));

		JLabel bidtitle = new JLabel("<html><center>Giá thầu của tôi: <br>");
		bidtitle.setFont(new Font(bidtitle.getFont().getFontName(), Font.BOLD, 14));
		bidtitle.setHorizontalAlignment(JLabel.CENTER);
		bids.add(bidtitle);

		for(Item i : AuctionFrame.items){
			if(i.getCurrentBid() != null){
				if(i.getCurrentBid().getID().equals(AuctionFrame.userID)){
					JLabel bidlabel = new JLabel("Đấu giá " + i.getTitle() + " cho " + i.getCurrentBid().getBid());
					JPanel bidpanel = new JPanel();
					bidpanel.add(bidlabel);
					bids.add(bidpanel);
				}
			}
		}

		return bids;
	}
}
