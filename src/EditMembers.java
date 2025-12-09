/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Date;

/**
 * Edit Members UI
 */
public class EditMembers extends JInternalFrame {

    // ===============================
    // UI COMPONENTS
    // ===============================

    private JPanel northPanel = new JPanel();
    private JLabel northLabel = new JLabel("MEMBER INFORMATION");

    private JPanel centerPanel = new JPanel();

    private JPanel editPanel = new JPanel();
    private JPanel editInformationPanel = new JPanel();
    private JPanel editInformationLabelPanel = new JPanel();
    private JPanel editInformationTextFieldPanel = new JPanel();
    private JPanel editButtonPanel = new JPanel();

    private JLabel editLabel = new JLabel("MemberID: ");
    private JTextField editTextField = new JTextField(25);
    private JButton editButton = new JButton("Edit");

    private JPanel informationPanel = new JPanel();
    private JPanel informationLabelPanel = new JPanel();
    private JPanel informationTextFieldPanel = new JPanel();

    private JLabel[] informationLabel = new JLabel[7];
    private String[] informaionString = {
            " Reg. No: ", " The Password: ", " Rewrite the password: ",
            " The Name: ", " E-Mail: ", " Major: ", " Valid Upto: "
    };

    private JTextField[] informationTextField = new JTextField[4];
    private JPasswordField[] informationPasswordField = new JPasswordField[2];

    private JPanel updateInformationButtonPanel = new JPanel();
    private JButton updateInformationButton = new JButton("Update the Information");

    private JPanel southPanel = new JPanel();
    private JButton OKButton = new JButton("Exit");

    // ===============================
    // BUSINESS OBJECTS
    // ===============================

    private Members member;
    private String[] data;
    private DateButton expiry_date;

    // ===============================
    // CONSTRUCTOR
    // ===============================

    public EditMembers() {
        super("Edit Members", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Edit16.gif")));
        Container cp = getContentPane();

        expiry_date = new DateButton();
        expiry_date.setForeground(Color.red);

        buildNorthPanel(cp);
        buildEditPanel();
        buildInformationPanel();
        buildSouthPanel(cp);

        editButton.addActionListener(e -> handleEditMember());
        updateInformationButton.addActionListener(e -> handleUpdateMember());
        OKButton.addActionListener(e -> dispose());

        setVisible(true);
        pack();
    }

    // ===============================
    // BUILD UI SECTIONS
    // ===============================

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

        // Label
        editInformationLabelPanel.setLayout(new GridLayout(1, 1));
        editInformationLabelPanel.add(editLabel);
        editLabel.setFont(new Font("Tahoma", Font.BOLD, 11));

        // Field
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
        OKButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        southPanel.add(OKButton);
        southPanel.setBorder(BorderFactory.createEtchedBorder());
        cp.add("Center", centerPanel);
        cp.add("South", southPanel);
    }

    // ===============================
    // BUSINESS LOGIC
    // ===============================

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

    // ===============================
    // VALIDATION HELPERS
    // ===============================

    public boolean isPasswordCorrect() {
        return Arrays.equals(informationPasswordField[0].getPassword(),
                             informationPasswordField[1].getPassword());
    }

    public boolean isCorrect() {
    data = new String[6];
    return validateRegNo()
            && validatePasswords()
            && validateBasicFields()
            && validateExpiryField();
}


    public boolean isEditCorrect() {
        return !editTextField.getText().equals("");
    }

    public boolean isExpiryValid() {
        return new Date().before(expiry_date.getDate());
    }

    // ===============================
    // UTILITIES
    // ===============================

    private void clearTextField() {
        editTextField.setText(null);

        for (int i = 0; i < informationLabel.length; i++) {
            if (i == 0) informationTextField[i].setText(null);
            else if (i == 1 || i == 2) informationPasswordField[i - 1].setText(null);
            else if (i >= 3 && i <= 5) informationTextField[i - 2].setText(null);
        }
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ===============================
    // KEY LISTENER
    // ===============================

    class NumberKeyListener extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!(Character.isDigit(c) ||
                    c == KeyEvent.VK_BACK_SPACE ||
                    c == KeyEvent.VK_ENTER ||
                    c == KeyEvent.VK_DELETE)) {
                getToolkit().beep();
                JOptionPane.showMessageDialog(null,
                        "This Field Only Accept Integer Number",
                        "WARNING",
                        JOptionPane.DEFAULT_OPTION);
                e.consume();
            }
        }
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

}
