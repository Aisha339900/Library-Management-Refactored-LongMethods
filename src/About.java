import javax.swing.*;
import java.awt.*;
public class About extends JPanel {
	public About() {
		ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("images/java.jpg"));
		JLabel label1 = new JLabel(icon);
		this.add(label1);
		JLabel label2 = new JLabel("<html><li> Library Management System"
		        + "</li><li><p>Ver# 1.0</li>"
		        + "<li><p>Coded by: NIELIT A Level Student,<br>Ganesh Sharma</li></html>");
		label2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		this.add(label2);
	}
}
