import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
public class RemoveMembers extends JInternalFrame {
	private JPanel northPanel = new JPanel();
	private JLabel title = new JLabel("MEMBER INFORMATION");
	private JPanel centerPanel = new JPanel();
	private JPanel removePanel = new JPanel();
	private JLabel removeLabel = new JLabel(" Write the Member ID: ");
	private JTextField removeTextField = new JTextField();
	private String data;
	private JPanel removeMemberPanel = new JPanel();
	private JButton removeButton = new JButton("Remove");
	private JPanel southPanel = new JPanel();
	private JButton exitButton = new JButton("Exit");
	private Members member;
	public boolean isCorrect() {
		if (!removeTextField.getText().equals("")) {
			data = removeTextField.getText();
			return true;
		}
		else
			return false;
	}
	public RemoveMembers() {
		super("Remove Members", false, true, false, true);
		setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Delete16.gif")));
		Container cp = getContentPane();
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		title.setFont(new Font("Tahoma", Font.BOLD, 14));
		northPanel.add(title);
		cp.add("North", northPanel);
		centerPanel.setLayout(new BorderLayout());
		removePanel.setLayout(new GridLayout(1, 2, 1, 1));
		removePanel.add(removeLabel);
		removePanel.add(removeTextField);
        removeTextField.addKeyListener(new keyListener());
		centerPanel.add("Center", removePanel);
		removeMemberPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		removeMemberPanel.add(removeButton);
		centerPanel.add("South", removeMemberPanel);
		centerPanel.setBorder(BorderFactory.createTitledBorder("Remove a member:"));
		cp.add("Center", centerPanel);
		removeLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		removeTextField.setFont(new Font("Tahoma", Font.PLAIN, 11));
		exitButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		removeButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		southPanel.add(exitButton);
		southPanel.setBorder(BorderFactory.createEtchedBorder());
		cp.add("South", southPanel);
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (isCorrect()) {
					Thread runner = new Thread() {
						public void run() {
							member = new Members();
							member.connection("SELECT * FROM Members WHERE MemberID = " + data);
							int numberOfBooks = member.getNumberOfBooks();
                            int memberid=member.getMemberID();
                            if(memberid>=1)
                            {
							if (numberOfBooks == 0) {
								member.update("DELETE FROM Members WHERE MemberID = " + data);
                                dispose();
							}
							else
								JOptionPane.showMessageDialog(null, "Book(s) borrowed by the member", "Warning", JOptionPane.WARNING_MESSAGE);
						}
                            else
                            {
                            JOptionPane.showMessageDialog(null, "The MemberID is wrong!", "Error", JOptionPane.ERROR_MESSAGE);
                            removeTextField.setText(null);
                            }
                        }
					};
					runner.start();
				}
				else {
					JOptionPane.showMessageDialog(null, "Please, complete the information", "Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				dispose();
			}
		});
		setVisible(true);
		pack();
	}
    class keyListener extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) ||
                        (c == KeyEvent.VK_BACK_SPACE) ||
                        (c == KeyEvent.VK_ENTER) ||
                        (c == KeyEvent.VK_DELETE))) {
                    getToolkit().beep();
                    JOptionPane.showMessageDialog(null, "This Field Only Accept Integer Number", "WARNING",JOptionPane.DEFAULT_OPTION);
                    e.consume();
                 }
            }
    }
}
