import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ItemCreateFrame extends JFrame {
	
	byte[] imagebyte;

	public ItemCreateFrame(String title){
		super(title);
		init();
	}

	private void init(){
		setSize(400,350);
		setMinimumSize(new Dimension(400,400));
		setLocationRelativeTo(null);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		add(panel);

		JTextField titleField = new JTextField(20);
		JLabel titleLabel = new JLabel("Tên vật phẩm: ");
		JPanel titlePanel = new JPanel();
		titlePanel.add(titleLabel);
		titlePanel.add(titleField);

		JTextField descriptionField = new JTextField(30);
		JLabel descriptionLabel = new JLabel("Mô tả vật phẩm: ");
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setPreferredSize(new Dimension(400,50));
		descriptionPanel.add(descriptionLabel);
		descriptionPanel.add(descriptionField);

		JComboBox choosecat = new JComboBox(Item.categories);
		JLabel catLabel = new JLabel("Chọn loại vật phẩm");
		JPanel categoryPanel = new JPanel();
		categoryPanel.add(catLabel);
		categoryPanel.add(choosecat);

		JTextField priceField = new JTextField(10);
		JLabel priceLabel = new JLabel("Giá khởi điểm:");
		JPanel pricePanel = new JPanel();
		pricePanel.add(priceLabel);
		pricePanel.add(priceField);

		JTextField userID = new JTextField(10);
		userID.setText(AuctionFrame.userID);
		userID.setEditable(false);
		JLabel userIDLabel = new JLabel("User ID:");
		JPanel userIDPanel = new JPanel();
		userIDPanel.add(userIDLabel);
		userIDPanel.add(userID);

		String[] years = {"2021", "2022"};
		JComboBox year  = new JComboBox(years);
		String[] months = {"January", "Febuary", "March", "April", "May", "June", "July", "August", "September", 
				"October", "November", "December"};
		JComboBox month  = new JComboBox(months);
		String[] days = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18",
				"19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
		JComboBox day  = new JComboBox(days);

		JTextField endTime = new JTextField(5);
		JLabel endLabel = new JLabel("End Time (Format HH:mm): ");
		JPanel timeLabelP = new JPanel();
		JPanel timePanel = new JPanel();
		timeLabelP.add(endLabel);
		timePanel.add(endTime);
		timePanel.add(day);
		timePanel.add(month);
		timePanel.add(year);

		JButton imagebutton = new JButton("Chọn ảnh");
		imagebutton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int returnValue = chooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					try {
						BufferedImage originalImage = ImageIO.read(f);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ImageIO.write(originalImage, "jpg", baos);
						baos.flush();
						imagebyte = baos.toByteArray();
						baos.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}				
				}
			}

		});

		JButton createItem = new JButton("Tạo vật phẩm");
		createItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
				UUID ID = UUID.randomUUID();
				String title = titleField.getText();
				String description = descriptionField.getText();
				long end = 0;
				Integer monthitem = 0;
				for(int i = 0; i < months.length; i++){
					if(months[i].equals(month.getSelectedItem())){
						monthitem = i+1;
					}
				}

				try {
					end = dateFormat.parse(monthitem + "/" + day.getSelectedItem() + "/" + year.getSelectedItem() + " " +
							endTime.getText()).getTime();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				String price = priceField.getText();
				String catergory = choosecat.getSelectedItem().toString();
				Item item = new Item(title, description, catergory, new Date().getTime(), end, 
						AuctionFrame.userID, price, ID);
				item.setImageBytes(imagebyte);
				ObjectMessage message = new ObjectMessage(MessageType.ITEM, item);
				Client.comms.sendMessage(message);
				Message returnedM = Client.comms.listenForMessage();
				if(returnedM.type == MessageType.ITEMFAIL){
					EFrame frame = new EFrame("Lỗi mặt hàng");
				}
			}

		});
		JPanel button = new JPanel();
		button.add(imagebutton);
		button.add(createItem);
		
		JPanel message = new JPanel();
		JLabel label = new JLabel("<html><center> Để rút vật phẩm, hãy truy cập trang tài khoản của bạn.");
		message.add(label);

		panel.add(titlePanel);
		panel.add(descriptionPanel);
		panel.add(pricePanel);
		panel.add(categoryPanel);
		panel.add(userIDPanel);
		panel.add(timeLabelP);
		panel.add(timePanel);
		panel.add(button);
		panel.add(message);

		setVisible(true);
	}

	public class EFrame extends JFrame{

		public EFrame(String title){
			super(title);
			init();
		}

		public void init(){

			setSize(400, 100);
			JPanel e = new JPanel();
			e.setLayout(new GridBagLayout());
			JLabel exceptionlabel = new JLabel();
			String exception ="<html><center>Bạn không thể làm một món đồ vì bạn có nhiều hơn 2 <br> điểm phạt";

			exceptionlabel.setText(exception);

			setLocationRelativeTo(null);
			e.add(exceptionlabel);
			add(e);

			setVisible(true);
		}

	}
}
