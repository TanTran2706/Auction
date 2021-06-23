import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NotificationFrame extends JFrame{

	public NotificationFrame(String title){
		super(title);
		init();
	}

	public void init(){
		setSize(400,350);
		setMinimumSize(new Dimension(400,350));
		setLocationRelativeTo(null);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		add(panel);

		JLabel title = new JLabel("<html><center> Thông báo: <br>");
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 14));
		panel.add(title);

		if(AuctionFrame.messages != null){
			for(String s : AuctionFrame.messages){
				System.out.println(s);
				JLabel message = new JLabel();
				message.setText(s);
				panel.add(message);
			}
		}

		setVisible(true);

	}

}
