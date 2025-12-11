import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Date;

public class AddMembers extends JInternalFrame {

    private final JLabel[] informationLabel = new JLabel[7];
    private final JTextField[] informationTextField = new JTextField[4];
    private final JPasswordField[] informationPasswordField = new JPasswordField[2];

    private final String[] infoString = {
            " Reg. No: ", " The Password: ", " Rewrite the password: ",
            " The Name: ", " E-Mail: ", " Major: ", " Valid Upto: "
    };

    private final JButton insertInformationButton = new JButton("Insert the Information");
    private final JButton OKButton = new JButton("Exit");

    private DateButton expiry_date;
    private Members member;
    private String[] data;

    public AddMembers() {
        super("Add Members", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Add16.gif")));

        expiry_date = new DateButton();
        expiry_date.setForeground(Color.red);

        buildNorthPanel();
        buildCenterPanel();
        buildSouthPanel();

        insertInformationButton.addActionListener(ae -> handleInsertMember());
        OKButton.addActionListener(ae -> dispose());

        setVisible(true);
        pack();
    }

    /* ------------------------- GUI BUILDERS ------------------------- */

    private void buildNorthPanel() {
        JLabel northLabel = new JLabel("MEMBER INFORMATION");
        northLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northPanel.add(northLabel);
        add("North", northPanel);
    }

    private void buildCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Add a new member:"));

        JPanel labelPanel = new JPanel(new GridLayout(7, 1, 1, 1));
        JPanel fieldPanel = new JPanel(new GridLayout(7, 1, 1, 1));

        for (int i = 0; i < 7; i++) {
            JLabel lbl = new JLabel(infoString[i]);
            lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
            informationLabel[i] = lbl;
            labelPanel.add(lbl);
        }

        fieldPanel.add(buildRegField());
        fieldPanel.add(buildPasswordField(0));
        fieldPanel.add(buildPasswordField(1));
        fieldPanel.add(buildTextField(1));
        fieldPanel.add(buildTextField(2));
        fieldPanel.add(buildTextField(3));
        fieldPanel.add(expiry_date);

        JPanel insertPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        insertInformationButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        insertPanel.add(insertInformationButton);

        centerPanel.add("West", labelPanel);
        centerPanel.add("East", fieldPanel);
        centerPanel.add("South", insertPanel);

        add("Center", centerPanel);
    }

    private JTextField buildRegField() {
        JTextField tf = new JTextField(25);
        tf.setFont(new Font("Tahoma", Font.PLAIN, 11));
        tf.addKeyListener(new DigitOnlyKeyListener());
        informationTextField[0] = tf;
        return tf;
    }

    private JPasswordField buildPasswordField(int index) {
        JPasswordField pf = new JPasswordField(25);
        pf.setFont(new Font("Tahoma", Font.PLAIN, 11));
        informationPasswordField[index] = pf;
        return pf;
    }

    private JTextField buildTextField(int index) {
        JTextField tf = new JTextField(25);
        tf.setFont(new Font("Tahoma", Font.PLAIN, 11));
        informationTextField[index] = tf;
        return tf;
    }

    private void buildSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        OKButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        southPanel.add(OKButton);
        southPanel.setBorder(BorderFactory.createEtchedBorder());
        add("South", southPanel);
    }

    /* ------------------------- VALIDATION ------------------------- */

    private boolean isCorrect() {
        data = new String[6];
        return validateId() && validatePasswords() && validateFields() && validateExpiry();
    }

    private boolean validateId() {
        String id = informationTextField[0].getText().trim();
        if (id.isEmpty()) return false;
        data[0] = id;
        return true;
    }

    private boolean validatePasswords() {
        char[] p1 = informationPasswordField[0].getPassword();
        char[] p2 = informationPasswordField[1].getPassword();

        if (p1.length == 0 || p2.length == 0) return false;
        if (!Arrays.equals(p1, p2)) return false;

        data[1] = new String(p1);
        return true;
    }

    private boolean validateFields() {
        for (int i = 1; i <= 3; i++) {
            String text = informationTextField[i].getText().trim();
            if (text.isEmpty()) return false;
            data[i + 1] = text;
        }
        return true;
    }

    private boolean validateExpiry() {
        String exp = expiry_date.getText().trim();
        if (exp.isEmpty()) return false;
        data[5] = exp;
        return true;
    }

    /* ------------------------- ACTION LOGIC ------------------------- */

    private void handleInsertMember() {
        if (!isCorrect()) {
            DialogUtils.warn(this, "Please, complete the information");
            return;
        }
        if (!validatePasswords()) {
            DialogUtils.error(this, "The password is wrong");
            return;
        }
        new Thread(this::processInsert).start();
    }

    private void processInsert() {
        Date today = new Date();
        Date expiry = expiry_date.getDate();
        if (!today.before(expiry)) {
            DialogUtils.warn(this, "Expiry Date is invalid");
            return;
        }

        member = new Members();
        member.connection("SELECT * FROM Members WHERE RegNo = " + data[0]);

        if (Integer.parseInt(data[0]) == member.getRegNo()) {
            DialogUtils.error(this, "Member is in the Library");
            return;
        }

        insertMember();
        dispose();
    }

    private void insertMember() {
        String q = "INSERT INTO Members (RegNo,Password,Name,EMail,Major,ValidUpto) VALUES ("
                + data[0] + ", '" + data[1] + "','" + data[2] + "','" + data[3] + "','"
                + data[4] + "','" + data[5] + "')";
        member.update(q);
    }

}
