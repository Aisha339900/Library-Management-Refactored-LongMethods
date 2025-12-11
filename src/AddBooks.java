import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AddBooks extends JInternalFrame {

    private final JLabel northLabel = new JLabel("BOOK INFORMATION");
    private final JPanel informationLabelPanel = new JPanel();
    private final JPanel informationTextFieldPanel = new JPanel();

    private final JLabel[] informationLabel = new JLabel[10];
    private final JTextField[] informationTextField = new JTextField[10];
    private final String[] informationString = {
            " The book subject: ", " The book title: ",
            " The name of the Author(s): ", " The name of the Publisher: ",
            " Copyright year for the book: ", " The edition number: ",
            " The number of Pages: ", " ISBN for the book: ",
            " The number of copies: ", " The name of the Library: "
    };

    private final JTextField txtShelfNo = new JTextField();
    private final JButton insertInformationButton = new JButton("Insert the Information");
    private final JButton OKButton = new JButton("Exit");

    private String[] data;
    private boolean available = true;

    public AddBooks() {
        super("Add Books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Add16.gif")));

        setupNorthPanel();
        setupCenterPanel();
        setupSouthPanel();
        attachActions();

        setVisible(true);
        pack();
    }

    /* ---------------------- GUI BUILDERS ---------------------- */

    private void setupNorthPanel() {
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        northPanel.add(northLabel);
        add("North", northPanel);
    }

    private void setupCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Add a new book:"));

        buildLabels();
        centerPanel.add("West", informationLabelPanel);

        buildFields();
        centerPanel.add("East", informationTextFieldPanel);

        JPanel insertPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        insertInformationButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        insertPanel.add(insertInformationButton);

        centerPanel.add("South", insertPanel);
        add("Center", centerPanel);
    }

    private void setupSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        OKButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        southPanel.add(OKButton);
        add("South", southPanel);
    }

    /* ---------------------- LABEL & FIELD HELPERS ---------------------- */

    private void buildLabels() {
        informationLabelPanel.setLayout(new GridLayout(11, 1, 1, 1));
        for (int i = 0; i < informationLabel.length; i++) {
            informationLabel[i] = new JLabel(informationString[i]);
            informationLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));
            informationLabelPanel.add(informationLabel[i]);
        }

        JLabel lblShelf = new JLabel(" Shelf No");
        lblShelf.setFont(new Font("Tahoma", Font.BOLD, 11));
        informationLabelPanel.add(lblShelf);
    }

    private void buildFields() {
        informationTextFieldPanel.setLayout(new GridLayout(11, 1, 1, 1));

        for (int i = 0; i < informationTextField.length; i++) {
            informationTextField[i] = new JTextField(25);
            informationTextField[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
            if (i == 4 || i == 5 || i == 6 || i == 8)
                informationTextField[i].addKeyListener(new NumericKeyListener());
            informationTextFieldPanel.add(informationTextField[i]);
        }

        txtShelfNo.setFont(new Font("Tahoma", Font.PLAIN, 11));
        txtShelfNo.addKeyListener(new NumericKeyListener());
        informationTextFieldPanel.add(txtShelfNo);
    }

    /* ---------------------- VALIDATION ---------------------- */

    private boolean isCorrect() {
        data = new String[10];
        for (int i = 0; i < data.length; i++) {
            String text = informationTextField[i].getText().trim();
            if (text.isEmpty()) return false;
            data[i] = text;
        }
        return true;
    }

    /* ---------------------- ACTION HANDLERS ---------------------- */

    private void attachActions() {
        insertInformationButton.addActionListener(ae -> handleInsertAction());
        OKButton.addActionListener(ae -> dispose());
    }

    private void handleInsertAction() {
        if (!isCorrect()) {
            showWarning("Please, complete the information");
            return;
        }
        new Thread(this::processInsert).start();
    }

    private void processInsert() {
        Books book = new Books();
        book.connection("SELECT * FROM Books WHERE ISBN = '" + data[7] + "'");
        if (data[7].equalsIgnoreCase(book.getISBN())) {
            showError("The book is in the library");
            return;
        }
        insertBookToDatabase();
        dispose();
    }

    /* ---------------------- DATABASE ---------------------- */

    private void insertBookToDatabase() {
        try {
            String sql = "INSERT INTO Books (Subject,Title,Author,Publisher,Copyright,"
                    + "Edition,Pages,ISBN,NumberOfBooks,NumberOfAvailbleBooks,Library,Availble,ShelfNo)"
                    + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

            Class.forName("org.gjt.mm.mysql.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Library", "root", "nielit");
            PreparedStatement ps = con.prepareStatement(sql);

            for (int i = 0; i < 4; i++) ps.setString(i + 1, data[i]);
            ps.setInt(5, Integer.parseInt(data[4]));
            ps.setInt(6, Integer.parseInt(data[5]));
            ps.setInt(7, Integer.parseInt(data[6]));
            ps.setString(8, data[7]);
            ps.setInt(9, Integer.parseInt(data[8]));
            ps.setInt(10, Integer.parseInt(data[8]));
            ps.setString(11, data[9]);
            ps.setBoolean(12, available);
            ps.setInt(13, Integer.parseInt(txtShelfNo.getText()));

            ps.executeUpdate();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    /* ---------------------- POPUPS ---------------------- */

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /* ---------------------- LISTENER ---------------------- */

    class NumericKeyListener extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE
                    && c != KeyEvent.VK_ENTER && c != KeyEvent.VK_DELETE) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null,
                        "This Field Only Accept Integer Number",
                        "WARNING",
                        JOptionPane.DEFAULT_OPTION);
                e.consume();
            }
        }
    }
}
