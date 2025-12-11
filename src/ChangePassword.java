import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ChangePassword extends JInternalFrame {

    private final JPasswordField oldPasswordField = new JPasswordField(25);
    private final JTextField usernameField = new JTextField(25);
    private final JPasswordField[] newPasswords = {new JPasswordField(25), new JPasswordField(25)};
    private final JButton editButton = new JButton("Edit");
    private final JButton updateButton = new JButton("Update");
    private final JButton exitButton = new JButton("Exit");

    private Password pswd;
    private String[] data;

    public ChangePassword() {
        super("Change Password", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Edit16.gif")));

        buildNorth();
        buildCenter();
        buildSouth();

        editButton.addActionListener(e -> handleEdit());
        updateButton.addActionListener(e -> handleUpdate());
        exitButton.addActionListener(e -> dispose());

        setVisible(true);
        pack();
    }

    /* --------------------------- UI BUILDERS --------------------------- */

    private void buildNorth() {
        JLabel title = new JLabel("LIBRARIAN INFORMATION");
        title.setFont(new Font("Tahoma", Font.BOLD, 14));
        JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        north.add(title);
        add("North", north);
    }

    private void buildCenter() {
        JPanel center = new JPanel(new BorderLayout());

        JPanel oldPanel = new JPanel(new BorderLayout());
        oldPanel.setBorder(BorderFactory.createTitledBorder("Old Password"));
        oldPanel.add(label("Old Password:"), BorderLayout.WEST);
        oldPanel.add(oldPasswordField, BorderLayout.CENTER);
        JPanel olBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        olBtn.add(editButton);
        oldPanel.add(olBtn, BorderLayout.SOUTH);

        JPanel newPanel = new JPanel(new BorderLayout());
        newPanel.setBorder(BorderFactory.createTitledBorder("Edit Login Details"));

        JPanel labelPanel = new JPanel(new GridLayout(3, 1));
        JPanel fieldPanel = new JPanel(new GridLayout(3, 1));

        labelPanel.add(label("User Name:"));
        usernameField.setEnabled(false);
        fieldPanel.add(usernameField);

        labelPanel.add(label("New Password:"));
        fieldPanel.add(newPasswords[0]);

        labelPanel.add(label("Confirm Password:"));
        fieldPanel.add(newPasswords[1]);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(updateButton);

        newPanel.add(labelPanel, BorderLayout.WEST);
        newPanel.add(fieldPanel, BorderLayout.CENTER);
        newPanel.add(btnPanel, BorderLayout.SOUTH);

        center.add(oldPanel, BorderLayout.NORTH);
        center.add(newPanel, BorderLayout.CENTER);

        add("Center", center);
    }

    private void buildSouth() {
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(exitButton);
        add("South", south);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.BOLD, 11));
        return l;
    }

    /* --------------------------- VALIDATION --------------------------- */

    private boolean isEditCorrect() {
        return oldPasswordField.getPassword().length > 0;
    }

    private boolean isCorrect() {
        data = new String[2];
        String username = usernameField.getText().trim();
        if (username.isEmpty()) return false;

        char[] p1 = newPasswords[0].getPassword();
        char[] p2 = newPasswords[1].getPassword();

        if (p1.length == 0 || p2.length == 0) return false;
        if (!Arrays.equals(p1, p2)) return false;

        data[0] = username;
        data[1] = new String(p1);
        return true;
    }

    /* --------------------------- ACTION LOGIC --------------------------- */

    private void handleEdit() {
        if (!isEditCorrect()) {
            warn("Please enter the old password");
            return;
        }
        new Thread(this::processEdit).start();
    }

    private void processEdit() {
        pswd = new Password();
        String old = new String(oldPasswordField.getPassword());

        boolean exists = pswd.connection(
                "SELECT * FROM Login WHERE Password='" + old + "'");

        if (!exists) {
            error("Incorrect old password");
            oldPasswordField.setText("");
            clearFields();
            return;
        }

        usernameField.setText(pswd.getUsername());
    }

    private void handleUpdate() {
        if (!isCorrect()) {
            warn("Please complete the information");
            return;
        }
        new Thread(this::processUpdate).start();
    }

    private void processUpdate() {
        pswd = new Password();
        String q = "UPDATE Login SET Username='" + data[0] +
                "', Password='" + data[1] +
                "' WHERE Username='" + usernameField.getText() + "'";
        pswd.update(q);
        dispose();
    }

    private void clearFields() {
        usernameField.setText("");
        newPasswords[0].setText("");
        newPasswords[1].setText("");
    }

    /* --------------------------- POPUPS --------------------------- */

    private void warn(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
