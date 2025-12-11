import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
public class ChangePassword extends JInternalFrame {
	private final JPanel northPanel = new JPanel();
	private final JLabel northLabel = new JLabel("LIBRARIAN INFORMATION");
	private final JPanel centerPanel = new JPanel();
	private final JPanel editPanel = new JPanel();
	private final JPanel editInformationPanel = new JPanel();
	private final JPanel editInformationLabelPanel = new JPanel();
	private final JPanel editInformationTextFieldPanel = new JPanel();
	private final JPanel editButtonPanel = new JPanel();
	private final JLabel editLabel = new JLabel("Old Password: ");
	private final JPasswordField editTextField = new JPasswordField(25);
	private final JButton editButton = new JButton("Edit");
	private final JPanel informationPanel = new JPanel();
	private final JPanel informationLabelPanel = new JPanel();
	private final JLabel[] informationLabel = new JLabel[3];
	private final String[] informaionString = {" User Name: ", " New Password: ", " Confirm Password: "};
	private final JPanel informationTextFieldPanel = new JPanel();
	private final JTextField[] informationTextField = new JTextField[1];
	private final JPasswordField[] informationPasswordField = new JPasswordField[2];
	private final JPanel updateInformationButtonPanel = new JPanel();
	private final JButton updateInformationButton = new JButton("Update");
	private final JPanel southPanel = new JPanel();
	private final JButton okButton = new JButton("Exit");
	private Password pswd;
	private String[] data;
	public ChangePassword() {
		super("Change Password", false, true, false, true);
		setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Edit16.gif")));
		Container cp = getContentPane();
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		northLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		northPanel.add(northLabel);
		cp.add("North", northPanel);
		centerPanel.setLayout(new BorderLayout());
		editPanel.setLayout(new BorderLayout());
		editPanel.setBorder(BorderFactory.createTitledBorder("Old Password "));
		editInformationPanel.setLayout(new BorderLayout());
		editInformationLabelPanel.setLayout(new GridLayout(1, 1, 1, 1));
		editInformationLabelPanel.add(editLabel);
		editLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		editInformationPanel.add("West", editInformationLabelPanel);
		editInformationTextFieldPanel.setLayout(new GridLayout(1, 1, 1, 1));
		editInformationTextFieldPanel.add(editTextField);
		editTextField.setFont(new Font("Tahoma", Font.PLAIN, 11));
		editInformationPanel.add("East", editInformationTextFieldPanel);
		editPanel.add("North", editInformationPanel);
		editButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		editButtonPanel.add(editButton);
		editButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		editPanel.add("Center", editButtonPanel);
		centerPanel.add("North", editPanel);
		informationPanel.setLayout(new BorderLayout());
		informationPanel.setBorder(BorderFactory.createTitledBorder("Edit Login Details: "));
		informationLabelPanel.setLayout(new GridLayout(informationLabel.length, 1, 1, 1));
		informationTextFieldPanel.setLayout(new GridLayout(informationLabel.length, 1, 1, 1));
		for (int i = 0; i < informationLabel.length; i++) {
			informationLabelPanel.add(informationLabel[i] = new JLabel(informaionString[i]));
			informationLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));
			if (i == 0) {
				informationTextFieldPanel.add(informationTextField[i] = new JTextField(25));
				informationTextField[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
				informationTextField[i].setEnabled(false);
			} else {
				informationTextFieldPanel.add(informationPasswordField[i - 1] = new JPasswordField(25));
				informationPasswordField[i - 1].setFont(new Font("Tahoma", Font.PLAIN, 11));
			}
		}
		informationPanel.add("West", informationLabelPanel);
		informationPanel.add("East", informationTextFieldPanel);
		updateInformationButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		updateInformationButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		updateInformationButtonPanel.add(updateInformationButton);
		informationPanel.add("South", updateInformationButtonPanel);
		centerPanel.add("Center", informationPanel);
		cp.add("Center", centerPanel);
		southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		okButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		southPanel.add(okButton);
		southPanel.setBorder(BorderFactory.createEtchedBorder());
		cp.add("South", southPanel);
		editButton.addActionListener(ae -> handleEditPassword());
		updateInformationButton.addActionListener(ae -> handleUpdatePassword());
		okButton.addActionListener(e -> dispose());
		setVisible(true);
		pack();
	}
	public boolean isPasswordCorrect() {
		if (Arrays.equals(informationPasswordField[0].getPassword(), informationPasswordField[1].getPassword())) {
			data[1] = new String(informationPasswordField[0].getPassword());
			return true;
		}
		return false;
	}
	public boolean isCorrect() {
		data = new String[3];
		for (int i = 0; i < informationLabel.length; i++) {
			if (i == 0) {
				if (informationTextField[i].getText().isEmpty()) return false;
				data[i] = informationTextField[i].getText();
			} else if (informationPasswordField[i - 1].getPassword().length == 0) {
				return false;
			}
		}
		return true;
	}
	public boolean isEditCorrect() {
		return editTextField.getPassword().length > 0;
	}
	public void clearTextField() {
		editTextField.setText(null);
		for (int i = 0; i < informationLabel.length; i++) {
			if (i == 0) informationTextField[i].setText(null);
			else informationPasswordField[i - 1].setText(null);
		}
	}
	private void handleEditPassword() {
		if (!isEditCorrect()) {
			showWarning("Please, write the old password");
			return;
		}
		new Thread(this::processEditPassword).start();
	}
	private void processEditPassword() {
		pswd = new Password();
		String oldPassword = new String(editTextField.getPassword());
		boolean exists = pswd.connection("SELECT * FROM Login WHERE Password='" + oldPassword + "'");
		if (!exists) {
			showError("Please, write a correct Password");
			editTextField.setText(null);
			clearTextField();
			return;
		}
		informationTextField[0].setText(pswd.getUsername());
	}
	private void handleUpdatePassword() {
		if (!isCorrect()) {
			showWarning("Please, complete the information");
			return;
		}
		if (!isPasswordCorrect()) {
			showError("New password mismatch !");
			return;
		}
		new Thread(this::processUpdatePassword).start();
	}
	private void processUpdatePassword() {
		pswd = new Password();
		String updateQuery = "UPDATE Login SET Username = '" + data[0] + "', Password = '" + data[1] +
				"' WHERE Username = '" + informationTextField[0].getText() + "'";
		pswd.update(updateQuery);
		dispose();
	}
	private void showWarning(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	private void showError(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
