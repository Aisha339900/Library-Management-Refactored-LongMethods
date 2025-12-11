import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Date;
public class AddMembers extends JInternalFrame {
	private JPanel northPanel = new JPanel();
	private JLabel northLabel = new JLabel("MEMBER INFORMATION");
	private JPanel centerPanel = new JPanel();
	private JPanel informationLabelPanel = new JPanel();
	private JLabel[] informationLabel = new JLabel[7];
    private String[] informaionString = {" Reg. No: ", " The Password: ", " Rewrite the password: ",
	                                     " The Name: ", " E-Mail: ", " Major: ", " Valid Upto: "};
	private JPanel informationTextFieldPanel = new JPanel();
	private JTextField[] informationTextField = new JTextField[4];
	private JPasswordField[] informationPasswordField = new JPasswordField[2];
	private JPanel insertInformationButtonPanel = new JPanel();
	private JButton insertInformationButton = new JButton("Insert the Information");
	private JPanel southPanel = new JPanel();
	private JButton OKButton = new JButton("Exit");
	private Members member;
	private String[] data;
    private DateButton expiry_date;
	public boolean isPasswordCorrect() {
        if (Arrays.equals(informationPasswordField[0].getPassword(),informationPasswordField[1].getPassword()))
            data[1] = new String(informationPasswordField[1].getPassword());
        else if(!Arrays.equals(informationPasswordField[0].getPassword(),informationPasswordField[1].getPassword()))
            return false;
        return true;
	}
	public boolean isCorrect() {
		data = new String[6];
		return validateIdField()
				&& validatePasswordFields()
				&& validateTextFields()
				&& validateExpiryDate();
	}
	public void clearTextField() {
		for (int i = 0; i < informationLabel.length; i++) {
			if (i == 0)
				informationTextField[i].setText(null);
			if (i == 1 || i == 2)
				informationPasswordField[i - 1].setText(null);
			if (i == 3 || i == 4 || i == 5)
				informationTextField[i - 2].setText(null);
		}
	}
	public AddMembers() {
		super("Add Members", false, true, false, true);
		setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Add16.gif")));
		Container cp = getContentPane();
        expiry_date = new DateButton();
        expiry_date.setForeground(Color.red);
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		northLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		northPanel.add(northLabel);
		cp.add("North", northPanel);
		centerPanel.setLayout(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createTitledBorder("Add a new member:"));
		informationLabelPanel.setLayout(new GridLayout(7, 1, 1, 1));
		informationTextFieldPanel.setLayout(new GridLayout(7, 1, 1, 1));
		for (int i = 0; i < informationLabel.length; i++) {
			informationLabelPanel.add(informationLabel[i] = new JLabel(informaionString[i]));
			informationLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));
		}
		centerPanel.add("West", informationLabelPanel);
		for (int i = 0; i < informationLabel.length; i++) {
			if (i == 1 || i == 2) {
				informationTextFieldPanel.add(informationPasswordField[i - 1] = new JPasswordField(25));
				informationPasswordField[i - 1].setFont(new Font("Tahoma", Font.PLAIN, 11));
			}
			if (i == 0) {
				informationTextFieldPanel.add(informationTextField[i] = new JTextField(25));
				informationTextField[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
                informationTextField[i].addKeyListener(new keyListener());
			}
			if (i == 3 || i == 4 || i == 5) {
				informationTextFieldPanel.add(informationTextField[i - 2] = new JTextField(25));
				informationTextField[i - 2].setFont(new Font("Tahoma", Font.PLAIN, 11));
                }
            if(i==6)
            {
                informationTextFieldPanel.add(expiry_date);
            }
		}
		centerPanel.add("East", informationTextFieldPanel);
		insertInformationButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		insertInformationButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		insertInformationButtonPanel.add(insertInformationButton);
		centerPanel.add("South", insertInformationButtonPanel);
		cp.add("Center", centerPanel);
		southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		OKButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		southPanel.add(OKButton);
		southPanel.setBorder(BorderFactory.createEtchedBorder());
		cp.add("South", southPanel);
		insertInformationButton.addActionListener(ae -> handleInsertMember());
		OKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				dispose();
			}
		});
		setVisible(true);
		pack();
	}
	private boolean isExpiryDateValid(Date today, Date expiryDate) {
    if (!today.before(expiryDate)) {
        showWarning("Expiry Date is invalid");
        return false;
    }
    return true;
}
	private void handleInsertMember() {
    if (!isCorrect()) {
        showWarning("Please, complete the information");
        return;
    }
    if (!isPasswordCorrect()) {
        showError("The password is wrong");
        return;
    }
    Thread runner = new Thread(this::processMemberInsert);
    runner.start();
}
private void processMemberInsert() {
    Date expiryDate = expiry_date.getDate();
    Date today = new Date();
    if (!isExpiryDateValid(today, expiryDate)) {
        return;
    }
    member = new Members();
    member.connection("SELECT * FROM Members WHERE RegNo = " + data[0]);
    if (isDuplicateMember()) {
        showError("Member is in the Library");
        return;
    }
    insertMemberIntoDatabase();
    dispose();
}
private boolean isDuplicateMember() {
    int regNo = member.getRegNo();
    return Integer.parseInt(data[0]) == regNo;
}
private void insertMemberIntoDatabase() {
    String query = "INSERT INTO Members (RegNo,Password,Name,EMail,Major,ValidUpto) VALUES (" +
            data[0] + ", '" + data[1] + "','" + data[2] + "','" +
            data[3] + "','" + data[4] + "','" + data[5] + "')";
    member.update(query);
}
private void showWarning(String message) {
    JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
}
private void showError(String message) {
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
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
	private boolean validateIdField() {
    if (!informationTextField[0].getText().isEmpty()) {
        data[0] = informationTextField[0].getText();
        return true;
    }
    return false;
}
private boolean validatePasswordFields() {
    for (int i = 0; i < 2; i++) {
        if (informationPasswordField[i].getPassword().length == 0) {
            return false;
        }
    }
    return true;
}
private boolean validateTextFields() {
    for (int i = 1; i <= 3; i++) {
        if (!informationTextField[i].getText().isEmpty()) {
            data[i + 1] = informationTextField[i].getText();
        } else {
            return false;
        }
    }
    return true;
}
private boolean validateExpiryDate() {
    if (!expiry_date.getText().isEmpty()) {
        data[5] = expiry_date.getText();
        return true;
    }
    return false;
}
}
