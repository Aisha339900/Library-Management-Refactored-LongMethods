import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Date;

public class EditMembers extends JInternalFrame {

    private final JTextField memberIdField = new JTextField(25);
    private final JButton editButton = new JButton("Edit");
    private final JButton updateButton = new JButton("Update");
    private final JButton exitButton = new JButton("Exit");

    private final JTextField[] fields = new JTextField[4];           // RegNo, Name, Email, Major
    private final JPasswordField[] passwordFields = new JPasswordField[2]; // New + Confirm
    private final DateButton expiryDate = new DateButton();

    private final String[] labels = {
            "Reg. No:", "New Password:", "Confirm Password:",
            "Name:", "E-Mail:", "Major:", "Valid Upto:"
    };

    private Members member;
    private String[] data;

    public EditMembers() {
        super("Edit Members", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Edit16.gif")));

        expiryDate.setForeground(Color.red);

        buildNorth();
        buildCenter();
        buildSouth();

        editButton.addActionListener(e -> handleEdit());
        updateButton.addActionListener(e -> handleUpdate());
        exitButton.addActionListener(e -> dispose());

        setVisible(true);
        pack();
    }

    /* ------------------------- UI BUILDERS ------------------------- */

    private void buildNorth() {
        JLabel title = new JLabel("MEMBER INFORMATION");
        title.setFont(new Font("Tahoma", Font.BOLD, 14));
        JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        north.add(title);
        add("North", north);
    }

    private void buildCenter() {
        JPanel main = new JPanel(new BorderLayout());
        main.add(buildEditSection(), BorderLayout.NORTH);
        main.add(buildFormSection(), BorderLayout.CENTER);
        add("Center", main);
    }

    private JPanel buildEditSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("MemberID"));

        JLabel lbl = new JLabel("MemberID:");
        lbl.setFont(new Font("Tahoma", Font.BOLD, 11));

        memberIdField.setFont(new Font("Tahoma", Font.PLAIN, 11));
        memberIdField.addKeyListener(new DigitOnlyKeyListener());

        JPanel top = new JPanel(new BorderLayout());
        top.add(lbl, BorderLayout.WEST);
        top.add(memberIdField, BorderLayout.CENTER);

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btn.add(editButton);

        panel.add(top, BorderLayout.NORTH);
        panel.add(btn, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildFormSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Edit member: "));

        JPanel labelPanel = new JPanel(new GridLayout(labels.length, 1));
        JPanel fieldPanel = new JPanel(new GridLayout(labels.length, 1));

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
            labelPanel.add(lbl);

            JComponent field;

            switch (i) {
                case 0 -> { // RegNo
                    fields[0] = new JTextField(25);
                    fields[0].setEnabled(false);
                    field = fields[0];
                }
                case 1, 2 -> { // Passwords
                    passwordFields[i - 1] = new JPasswordField(25);
                    field = passwordFields[i - 1];
                }
                case 6 -> field = expiryDate;
                default -> {
                    fields[i - 2] = new JTextField(25);
                    field = fields[i - 2];
                }
            }

            field.setFont(new Font("Tahoma", Font.PLAIN, 11));
            fieldPanel.add(field);
        }

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btn.add(updateButton);

        panel.add(labelPanel, BorderLayout.WEST);
        panel.add(fieldPanel, BorderLayout.CENTER);
        panel.add(btn, BorderLayout.SOUTH);

        return panel;
    }

    private void buildSouth() {
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exitButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        south.add(exitButton);
        add("South", south);
    }

    /* ------------------------- VALIDATION ------------------------- */

    private boolean isEditCorrect() {
        return !memberIdField.getText().trim().isEmpty();
    }

    private boolean isCorrect() {
        data = new String[6];

        if (!validateRegNo()) return false;
        if (!validatePasswords()) return false;
        if (!validateBasicFields()) return false;
        return validateExpiry();
    }

    private boolean validateRegNo() {
        String reg = fields[0].getText().trim();
        if (reg.isEmpty()) return false;
        data[0] = reg;
        return true;
    }

    private boolean validatePasswords() {
        char[] p1 = passwordFields[0].getPassword();
        char[] p2 = passwordFields[1].getPassword();

        if (p1.length == 0 || p2.length == 0) return false;
        if (!Arrays.equals(p1, p2)) return false;

        data[1] = new String(p1);
        return true;
    }

    private boolean validateBasicFields() {
        for (int i = 1; i <= 3; i++) {
            String value = fields[i].getText().trim();
            if (value.isEmpty()) return false;
            data[i + 1] = value;
        }
        return true;
    }

    private boolean validateExpiry() {
        String exp = expiryDate.getText().trim();
        if (exp.isEmpty()) return false;
        data[5] = exp;
        return true;
    }

    /* ------------------------- EDIT LOGIC ------------------------- */

    private void handleEdit() {
        if (!isEditCorrect()) {
            DialogUtils.warn(this, "Please enter the MemberID");
            return;
        }
        new Thread(this::processEdit).start();
    }

    private void processEdit() {
        member = new Members();
        member.connection("SELECT * FROM Members WHERE MemberID = " + memberIdField.getText());

        if (member.getRegNo() <= 0) {
            DialogUtils.error(this, "Incorrect MemberID");
            clearFields();
            return;
        }

        fillFields();
    }

    private void fillFields() {
        fields[0].setText(member.getRegNo() + "");
        passwordFields[0].setText(member.getPassword());
        passwordFields[1].setText(member.getPassword());
        fields[1].setText(member.getName());
        fields[2].setText(member.getEmail());
        fields[3].setText(member.getMajor());
        expiryDate.setDate(member.getValidUpto());
    }

    /* ------------------------- UPDATE LOGIC ------------------------- */

    private void handleUpdate() {
        if (!isCorrect()) {
            DialogUtils.warn(this, "Please complete the information");
            return;
        }
        new Thread(this::processUpdate).start();
    }

    private void processUpdate() {
        member = new Members();

        String q = "UPDATE Members SET "
                + "RegNo=" + data[0]
                + ", Password='" + data[1]
                + "', Name='" + data[2]
                + "', EMail='" + data[3]
                + "', Major='" + data[4]
                + "', ValidUpto='" + data[5]
                + "' WHERE MemberID=" + memberIdField.getText();

        member.update(q);
        dispose();
    }

    /* ------------------------- HELPERS ------------------------- */

    private void clearFields() {
        fields[0].setText("");
        for (JPasswordField pf : passwordFields) pf.setText("");
        for (int i = 1; i < fields.length; i++) fields[i].setText("");
    }
}
