import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
public class AddBooks extends JInternalFrame {
    private JPanel northPanel = new JPanel();
    private JLabel northLabel = new JLabel("BOOK INFORMATION");
    private JPanel centerPanel = new JPanel();
    private JPanel informationLabelPanel = new JPanel();
    private JLabel[] informationLabel = new JLabel[10];
    private JLabel lblShelfNo = new JLabel(" Shelf No");
    private JTextField txtShelfNo = new JTextField();
    private String[] informationString = {
        " The book subject: ", " The book title: ",
        " The name of the Author(s): ", " The name of the Publisher: ",
        " Copyright year for the book: ", " The edition number: ", " The number of Pages: ",
        " ISBN for the book: ", " The number of copies: ", " The name of the Library: "};
    private JPanel informationTextFieldPanel = new JPanel();
    private JTextField[] informationTextField = new JTextField[10];
    private JPanel insertInformationButtonPanel = new JPanel();
    private JButton insertInformationButton = new JButton("Insert the Information");
    private JPanel southPanel = new JPanel();
    private JButton OKButton = new JButton("Exit");
    private Books book;
    private String[] data;
    private boolean availble = true;
    public boolean isCorrect() {
        data = new String[10];
        for (int i = 0; i < informationLabel.length; i++) {
            if (!informationTextField[i].getText().equals("")) {
                data[i] = informationTextField[i].getText();
            } else {
                return false;
            }
        }
        return true;
    }
    public void clearTextField() {
        for (int i = 0; i < informationTextField.length; i++) {
            informationTextField[i].setText(null);
        }
        txtShelfNo.setText(null);
    }
    public AddBooks() {
        super("Add Books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Add16.gif")));
        Container cp = getContentPane();
        cp.setBackground(Color.white);
        northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        northLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        northPanel.add(northLabel);
        cp.add("North", northPanel);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Add a new book:"));
        informationLabelPanel.setLayout(new GridLayout(11, 1, 1, 1));
        for (int i = 0; i < informationLabel.length; i++) {
            informationLabelPanel.add(informationLabel[i] = new JLabel(informationString[i]));
            informationLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));
        }
        centerPanel.add("West", informationLabelPanel);
        informationTextFieldPanel.setLayout(new GridLayout(11, 1, 1, 1));
        for (int i = 0; i < informationTextField.length; i++) {
            informationTextFieldPanel.add(informationTextField[i] = new JTextField(25));
            informationTextField[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
            if(i==4 || i==5 || i==6 || i==8 )
                informationTextField[i].addKeyListener(new keyListener());
        }
        lblShelfNo.setFont(new Font("Tahoma", Font.BOLD, 11));
        informationLabelPanel.add(lblShelfNo);
        txtShelfNo.setFont(new Font("Tahoma", Font.PLAIN, 11));
        informationTextFieldPanel.add(txtShelfNo);
        txtShelfNo.addKeyListener(new keyListener());
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
        insertInformationButton.addActionListener(ae -> handleInsertAction());
        OKButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                dispose();
            }
        });
        setVisible(true);
        pack();
    }
    private void handleInsertAction() {
    if (!isCorrect()) {
        JOptionPane.showMessageDialog(null,
                "Please, complete the information",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        return;
    }
    Thread runner = new Thread(this::processInsert);
    runner.start();
}
private void processInsert() {
    book = new Books();
    book.connection("SELECT * FROM Books WHERE ISBN = '" + data[7] + "'");
    String isbn = book.getISBN();
    if (bookExists(isbn)) {
        JOptionPane.showMessageDialog(null,
                "The book is in the library",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }
    insertBookToDatabase();
    dispose();
}
private boolean bookExists(String isbn) {
    return data[7].equalsIgnoreCase(isbn);
}
private void insertBookToDatabase() {
    try {
        String sql = "INSERT INTO Books (Subject,Title,Author,Publisher,Copyright,"
                + "Edition,Pages,ISBN,NumberOfBooks,NumberOfAvailbleBooks,Library,Availble,ShelfNo)"
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Class.forName("org.gjt.mm.mysql.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Library", "root", "nielit");
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, data[0]);
        ps.setString(2, data[1]);
        ps.setString(3, data[2]);
        ps.setString(4, data[3]);
        ps.setInt(5, Integer.parseInt(data[4]));
        ps.setInt(6, Integer.parseInt(data[5]));
        ps.setInt(7, Integer.parseInt(data[6]));
        ps.setString(8, data[7]);
        ps.setInt(9, Integer.parseInt(data[8]));
        ps.setInt(10, Integer.parseInt(data[8]));
        ps.setString(11, data[9]);
        ps.setBoolean(12, availble);
        ps.setInt(13, Integer.parseInt(txtShelfNo.getText()));
        ps.executeUpdate();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.toString());
    }
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
