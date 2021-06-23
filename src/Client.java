import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class Client extends JFrame {

	static JFrame mainFrame;
	static JPanel mainPanel;
	static ClientComms comms;
	
	public static void main(String[] args) {
		new Client("Auction System");
	}

	public Client(String title){
		super(title);
		init();
		comms = new ClientComms();
	}
	
	public void init(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 350);
		setMinimumSize(new Dimension(300,350));
		setLocationRelativeTo(null);
		mainFrame = this;

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(9,0));
		
		JPanel useridPanel = new JPanel();
		JPanel passwordPanel = new JPanel();
		JPanel loginPanel = new JPanel();

		JTextField useridfield = new JTextField(10);
		JLabel useridlabel = new JLabel("User ID:");
		JPasswordField passwordfield = new JPasswordField(10);
		JLabel passwordlabel = new JLabel("Mật khẩu:");
		JButton login = new JButton("Đăng nhập");
		login.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String userID = useridfield.getText();
				char[] passwords = passwordfield.getPassword();
				String password = String.copyValueOf(passwords);
				LoginMessage message = new LoginMessage(MessageType.LOGIN, userID, password);
				comms.sendMessage(message);
				Message returnedM = comms.listenForMessage();
				if(returnedM.type == MessageType.AUTHENTICATED){
					Client.mainFrame.dispose();
					Client.mainFrame = new AuctionFrame(userID + "'s Auction Frame", userID);
				} else if(returnedM.type == MessageType.FAILED){
					new EFrame("ID người dùng / mật khẩu không chính xác");
				}
			}

		});
		
		passwordfield.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				login.doClick();
			}
			
		});

		useridPanel.add(useridlabel);
		useridPanel.add(useridfield);
		passwordPanel.add(passwordlabel);
		passwordPanel.add(passwordfield);
		loginPanel.add(login);

		JTextField firstnamefield = new JTextField(10);
		JLabel firstnamelabel = new JLabel("Tên: ");
		JTextField lastnamefield = new JTextField(10);
		JLabel lastnamelabel = new JLabel("Họ: ");
		JTextField registeruserid = new JTextField(10);
		JLabel registeruseridlabel = new JLabel("User ID:");
		JTextField registerpassword = new JTextField(10);
		JLabel registerpasswordlabel = new JLabel("Mật khẩu:");

		JButton register = new JButton("Register");
		register.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String firstname = firstnamefield.getText();
				String lastname = lastnamefield.getText();
				String userID = registeruserid.getText();
				String password = registerpassword.getText();
				ObjectMessage message = new ObjectMessage(MessageType.USER, new User(firstname, lastname, userID, password));
				comms.sendMessage(message);
				Message returnedM = comms.listenForMessage();
				if(returnedM.type == MessageType.USERSUCCESS){
					Client.mainFrame.dispose();
					Client.mainFrame = new AuctionFrame(userID + "'s Auction Frame", userID);
				} else if(returnedM.type == MessageType.USERFAIL){
					new REFrame("Username already taken");
				}
			}

		});

		JPanel firstnamePanel = new JPanel();
		JPanel lastnamePanel = new JPanel();
		JPanel registeridPanel = new JPanel();
		JPanel registerpassPanel = new JPanel();
		JPanel registerPanel = new JPanel();

		firstnamePanel.add(firstnamelabel);
		firstnamePanel.add(firstnamefield);
		lastnamePanel.add(lastnamelabel);
		lastnamePanel.add(lastnamefield);
		registeridPanel.add(registeruseridlabel);
		registeridPanel.add(registeruserid);
		registerpassPanel.add(registerpasswordlabel);
		registerpassPanel.add(registerpassword);
		registerPanel.add(register);

		mainPanel.add(useridPanel);
		mainPanel.add(passwordPanel);
		mainPanel.add(loginPanel);
		mainPanel.add(new JPanel());
		mainPanel.add(firstnamePanel);
		mainPanel.add(lastnamePanel);
		mainPanel.add(registeridPanel);
		mainPanel.add(registerpassPanel);
		mainPanel.add(registerPanel);
		add(mainPanel);

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
			String exception ="<html><center>Sai ID hoặc mật khẩu.";

			exceptionlabel.setText(exception);
			
		    setLocationRelativeTo(null);
			e.add(exceptionlabel);
			add(e);

			setVisible(true);
		}

	}
	
	public class REFrame extends JFrame{

		public REFrame(String title){
			super(title);
			init();
		}

		public void init(){

			setSize(400, 100);
			JPanel e = new JPanel();
			e.setLayout(new GridBagLayout());
			JLabel exceptionlabel = new JLabel();
			String exception ="<html><center>Tên người dùng đã được sử dụng. Vui lòng thử lại.";

			exceptionlabel.setText(exception);
			
		    setLocationRelativeTo(null);
			e.add(exceptionlabel);
			add(e);

			setVisible(true);
		}

	}

}
