import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Server extends JFrame{

	ServerComms comms;
	static JTextArea display;
	DataPersistence data = new DataPersistence();
	static JTable logging;

	public static void main(String[] args) {
		new Server();
	}

	public Server(){
		comms = new ServerComms();
		Thread t = new Thread(comms);
		t.start();
		init();
		Timer time = new Timer();
		ServerTimer task = new ServerTimer();
		time.schedule(task, 0, 1000);
		try {
			ServerComms.items = (ArrayList<Item>) data.createItemList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void init(){
		setSize(300, 500);
		setMinimumSize(new Dimension(300,500));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent arg0){
				comms.stopServer();
			}
		});

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		setContentPane(panel);

		JPanel centerPanel = new JPanel();
		display = new JTextArea(25,22);
		display.setMaximumSize(new Dimension(220, 400));
		display.setMinimumSize(new Dimension(220, 400));
		display.setLineWrap(true);
		display.setWrapStyleWord(true);
		display.setEditable(false);

		centerPanel.add(display);
		centerPanel.setBorder(new TitledBorder("Server"));

		JPanel topPanel = new JPanel();
		JButton log = new JButton("Nhật ký đấu giá");
		log.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				LogFrame log = new LogFrame("Bảng Chiến Thắng");
			}

		});

		JButton stop = new JButton("Dừng Server");
		stop.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				display.append("Dừng Server \n");
				comms.closeClients();
				comms.stopServer();
				System.exit(0);
			}

		});

		topPanel.add(log);
		topPanel.add(stop);

		panel.add(centerPanel, BorderLayout.CENTER);
		panel.add(topPanel, BorderLayout.NORTH);

		display.append("Khởi tạo hiển thị máy chủ \n");

		setVisible(true);

	}

	public class LogFrame extends JFrame{
		
		public LogFrame(String title){
			super(title);
			initLog();
		}

		private void initLog(){
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			setContentPane(panel);
			setSize(500,300);
			setMinimumSize(new Dimension(500,300));
			
			String[] columnnames = {"Vật phẩm", "Người thắng", "Người bán", "Giá"};
			String[][] rows = null;
			try {
				rows = data.writeTable();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			logging = new JTable(rows, columnnames);
			JScrollPane scroll = new JScrollPane(logging);

			
			if(rows != null){
				panel.add(logging.getTableHeader(), BorderLayout.NORTH);
				panel.add(scroll, BorderLayout.CENTER);
			}
			
			setVisible(true);
		}

	}

	public class ServerTimer extends TimerTask{


		public synchronized void run() {

			if(ServerComms.items != null){
				for(Item i : ServerComms.items){
					if(new Date(i.finish).before((new Date()))){
						ServerComms.items.remove(i);
						comms.updateItemsClients(i);
						display.append("Vật phẩm " + i.getTitle() + " đã kết thúc phiên. \n");
						break;
					}
				}
			}
		}

	}
}
