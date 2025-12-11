import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Date;
public class EditMembers extends JInternalFrame {
	private final JPanel northPanel = new JPanel();
	private final JLabel northLabel = new JLabel("MEMBER INFORMATION");
	private final JPanel centerPanel = new JPanel();
	private final JPanel editPanel = new JPanel();
	private final JPanel editInformationPanel = new JPanel();
	private final JPanel editInformationLabelPanel = new JPanel();
	private final JPanel editInformationTextFieldPanel = new JPanel();
	private final JPanel editButtonPanel = new JPanel();
	private final JLabel editLabel = new JLabel("MemberID: ");
	private final JTextField editTextField = new JTextField(25);
	private final JButton editButton = new JButton("Edit");
	private final JPanel informationPanel = new JPanel();
	private final JPanel informationLabelPanel = new JPanel();
	private final JPanel informationTextFieldPanel = new JPanel();
	private final JLabel[] informationLabel = new JLabel[7];
	private final String[] informaionString = {
			" Reg. No: ", " The Password: ", " Rewrite the password: ",
			" The Name: ", " E-Mail: ", " Major: ", " Valid Upto: "
	};
	private final JTextField[] informationTextField = new JTextField[4];
	private final JPasswordField[] informationPasswordField = new JPasswordField[2];
	private final JPanel updateInformationButtonPanel = new JPanel();
	private final JButton updateInformationButton = new JButton("Update the Information");
	private final JPanel southPanel = new JPanel();
	private final JButton okButton = new JButton("Exit");
	private final DateButton expiry_date = new DateButton();
	private Members member;
	private String[] data;
	public EditMembers() {
		super("Edit Members", false, true, false, true);
		setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Edit16.gif")));
		Container cp = getContentPane();
		expiry_date.setForeground(Color.red);
		buildNorthPanel(cp);
		buildEditPanel();
		buildInformationPanel();
		buildSouthPanel(cp);
		editButton.addActionListener(e -> handleEditMember());
		updateInformationButton.addActionListener(e -> handleUpdateMember());
		okButton.addActionListener(e -> dispose());
		setVisible(true);
		pack();
	}
	private void buildNorthPanel(Container cp) {
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		northLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		northPanel.add(northLabel);
		cp.add("North", northPanel);
	}
	private void buildEditPanel() {
		centerPanel.setLayout(new BorderLayout());
		editPanel.setLayout(new BorderLayout());
		editPanel.setBorder(BorderFactory.createTitledBorder("MemberID: "));
		editInformationLabelPanel.setLayout(new GridLayout(1, 1));
		editInformationLabelPanel.add(editLabel);
		editLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		editInformationTextFieldPanel.setLayout(new GridLayout(1, 1));
		editTextField.setFont(new Font("Tahoma", Font.PLAIN, 11));
		editTextField.addKeyListener(new NumberKeyListener());
		editInformationTextFieldPanel.add(editTextField);
		editInformationPanel.setLayout(new BorderLayout());
		editInformationPanel.add("West", editInformationLabelPanel);
		editInformationPanel.add("East", editInformationTextFieldPanel);
		editButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		editButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		editButtonPanel.add(editButton);
		editPanel.add("North", editInformationPanel);
		editPanel.add("Center", editButtonPanel);
		centerPanel.add("North", editPanel);
	}
	private void buildInformationPanel() {
		informationPanel.setLayout(new BorderLayout());
		informationPanel.setBorder(BorderFactory.createTitledBorder("Edit a member: "));
		informationLabelPanel.setLayout(new GridLayout(7, 1));
		informationTextFieldPanel.setLayout(new GridLayout(7, 1));
		for (int i = 0; i < informationLabel.length; i++) {
			informationLabelPanel.add(informationLabel[i] = new JLabel(informaionString[i]));
			informationLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));
			if (i == 1 || i == 2) {
				JPasswordField pf = new JPasswordField(25);
				pf.setFont(new Font("Tahoma", Font.PLAIN, 11));
				informationPasswordField[i - 1] = pf;
				informationTextFieldPanel.add(pf);
			} else if (i == 0) {
				informationTextField[i] = new JTextField(25);
				informationTextField[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
				informationTextField[i].setEnabled(false);
				informationTextFieldPanel.add(informationTextField[i]);
			} else if (i == 6) {
				informationTextFieldPanel.add(expiry_date);
			} else {
				informationTextField[i - 2] = new JTextField(25);
				informationTextField[i - 2].setFont(new Font("Tahoma", Font.PLAIN, 11));
				informationTextFieldPanel.add(informationTextField[i - 2]);
			}
		}
		updateInformationButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		updateInformationButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		updateInformationButtonPanel.add(updateInformationButton);
		informationPanel.add("West", informationLabelPanel);
		informationPanel.add("East", informationTextFieldPanel);
		informationPanel.add("South", updateInformationButtonPanel);
		centerPanel.add("Center", informationPanel);
	}
	private void buildSouthPanel(Container cp) {
		southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		okButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		southPanel.add(okButton);
		southPanel.setBorder(BorderFactory.createEtchedBorder());
		cp.add("Center", centerPanel);
		cp.add("South", southPanel);
	}
	private void handleEditMember() {
		if (!isEditCorrect()) {
			showWarning("Please, write the MemberID");
			return;
		}
		new Thread(this::processEditMember).start();
	}
	private void processEditMember() {
		member = new Members();
		member.connection("SELECT * FROM Members WHERE MemberID = " + editTextField.getText());
		if (member.getRegNo() <= 0) {
			showError("Please, write a correct MemberID");
			clearTextField();
			editTextField.setText(null);
			return;
		}
		fillFormWithMemberData();
	}
	private void fillFormWithMemberData() {
		informationTextField[0].setText(member.getRegNo() + "");
		informationPasswordField[0].setText(member.getPassword());
		informationPasswordField[1].setText(member.getPassword());
		informationTextField[1].setText(member.getName());
		informationTextField[2].setText(member.getEmail());
		informationTextField[3].setText(member.getMajor());
		expiry_date.setDate(member.getValidUpto());
	}
	private void handleUpdateMember() {
		if (!isCorrect()) {
			showWarning("Please, complete the information");
			return;
		}
		if (!isPasswordCorrect()) {
			showError("Password mismatch");
			return;
		}
		if (!isExpiryValid()) {
			showWarning("Expiry Date is invalid");
			return;
		}
		new Thread(this::processUpdateMember).start();
	}
	private void processUpdateMember() {
		member = new Members();
		String query =
				"UPDATE Members SET RegNo=" + data[0] +
				", Password='" + data[1] +
				"', Name='" + data[2] +
				"', EMail='" + data[3] +
				"', Major='" + data[4] +
				"', ValidUpto='" + data[5] +
				"' WHERE MemberID=" + editTextField.getText();
		member.update(query);
		dispose();
	}
	public boolean isPasswordCorrect() {
		return Arrays.equals(informationPasswordField[0].getPassword(), informationPasswordField[1].getPassword());
	}
	public boolean isCorrect() {
		data = new String[6];
		return validateRegNo() && validatePasswords() && validateBasicFields() && validateExpiryField();
	}
	public boolean isEditCorrect() {
		return !editTextField.getText().isEmpty();
	}
	public boolean isExpiryValid() {
		return new Date().before(expiry_date.getDate());
	}
	private void clearTextField() {
		editTextField.setText(null);
		informationTextField[0].setText(null);
		for (JPasswordField field : informationPasswordField) field.setText(null);
		for (int i = 1; i < informationTextField.length; i++) informationTextField[i].setText(null);
	}
	private void showWarning(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	private void showError(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	private boolean validateRegNo() {
		String reg = informationTextField[0].getText();
		if (reg.isEmpty()) return false;
		data[0] = reg;
		return true;
	}
	private boolean validatePasswords() {
		char[] pass1 = informationPasswordField[0].getPassword();
		char[] pass2 = informationPasswordField[1].getPassword();
		if (pass1.length == 0 || pass2.length == 0) return false;
		if (!Arrays.equals(pass1, pass2)) return false;
		data[1] = new String(pass1);
		return true;
	}
	private boolean validateBasicFields() {
		for (int i = 1; i <= 3; i++) {
			String value = informationTextField[i].getText();
			if (value.isEmpty()) return false;
			data[i + 1] = value;
		}
		return true;
	}
	private boolean validateExpiryField() {
		String exp = expiry_date.getText();
		if (exp.isEmpty()) return false;
		data[5] = exp;
		return true;
	}
	private static class NumberKeyListener extends KeyAdapter {
		public void keyTyped(KeyEvent e) {
			char c = e.getKeyChar();
			if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_ENTER || c == KeyEvent.VK_DELETE)) {
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(null, "This Field Only Accept Integer Number", "WARNING", JOptionPane.DEFAULT_OPTION);
				e.consume();
			}
		}
	}
}
