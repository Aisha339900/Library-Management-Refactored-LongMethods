import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
public class ReturnBooks extends JInternalFrame implements ActionListener {
    private JPanel northPanel = new JPanel();
    private JLabel title = new JLabel("BOOK INFORMATION");
    private JPanel centerPanel = new JPanel();
    private JPanel informationPanel = new JPanel();
    private JLabel[] informationLabel = new JLabel[2];
    private String[] informationString = {" Write the Book ID:", " Write the Member ID:"};
    private JTextField[] informationTextField = new JTextField[2];
    private String[] data;
    private JLabel lblFinePerDay = new JLabel(" Fine per Day");
    private JTextField txtFinePerDay = new JTextField();
    private JLabel lblTotalFineAmt = new JLabel(" Total fine amount");
    private JTextField txtTotalFineAmt = new JTextField();
    private JPanel returnButtonPanel = new JPanel();
    private JButton returnButton = new JButton("Return");
    private JPanel southPanel = new JPanel();
    private JButton cancelButton = new JButton("Cancel");
    private Books book;
    private Members member;
    private Borrow borrow;
    public ReturnBooks() {
        super("Return books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Import16.gif")));
        Container cp = getContentPane();
        northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        title.setFont(new Font("Tahoma", Font.BOLD, 14));
        northPanel.add(title);
        cp.add("North", northPanel);
        centerPanel.setLayout(new BorderLayout());
        informationPanel.setLayout(new GridLayout(4, 2, 1, 1));
        buildFormInputs();
        buildFineSection();
        buildButtons(cp);
        returnButton.addActionListener(this);
        cancelButton.addActionListener(this);
        setVisible(true);
        pack();
    }
    private void buildFormInputs() {
        for (int i = 0; i < informationLabel.length; i++) {
            informationPanel.add(informationLabel[i] = new JLabel(informationString[i]));
            informationLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));
            informationPanel.add(informationTextField[i] = new JTextField());
            informationTextField[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
            informationTextField[i].addKeyListener(new keyListener());
        }
    }
    private void buildFineSection() {
        informationPanel.add(lblFinePerDay);
        informationPanel.add(txtFinePerDay);
        informationPanel.add(lblTotalFineAmt);
        informationPanel.add(txtTotalFineAmt);
        txtTotalFineAmt.setEditable(false);
        txtFinePerDay.addKeyListener(new keyListener());
        centerPanel.add("Center", informationPanel);
        centerPanel.setBorder(BorderFactory.createTitledBorder("Return a book:"));
    }
    private void buildButtons(Container cp) {
        returnButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        returnButtonPanel.add(returnButton);
        returnButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        centerPanel.add("South", returnButtonPanel);
        cp.add("Center", centerPanel);
        southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        cancelButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        southPanel.add(cancelButton);
        southPanel.setBorder(BorderFactory.createEtchedBorder());
        cp.add("South", southPanel);
    }
    public boolean isCorrect() {
        data = new String[2];
        for (int i = 0; i < informationTextField.length; i++) {
            if (informationTextField[i].getText().isEmpty()) {
                return false;
            }
            data[i] = informationTextField[i].getText();
        }
        return true;
    }
    public void clearTextField() {
        for (JTextField tf : informationTextField) {
            tf.setText(null);
        }
        txtFinePerDay.setText(null);
        txtTotalFineAmt.setText(null);
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == returnButton) {
            processReturnButtonClick();
        }
        if (ae.getSource() == cancelButton) {
            dispose();
        }
    }
    private void processReturnButtonClick() {
        if (!isCorrect()) {
            showWarning("Please, complete the information");
            return;
        }
        Thread runner = new Thread(this::processReturnBook);
        runner.start();
    }
    private void processReturnBook() {
        initObjects();
        if (!borrowRecordExists()) {
            showWarning("The book is not borrowed");
            clearTextField();
            return;
        }
        loadBookAndMember();
        int available = book.getNumberOfAvailbleBooks();
        int borrowed = book.getNumberOfBorrowedBooks() - 1;
        int memberBooks = member.getNumberOfBooks();
        if (memberBooks <= 0) {
            showWarning("Invalid member book count");
            return;
        }
        updateRecords(available, borrowed, memberBooks);
    }
    private void initObjects() {
        book = new Books();
        member = new Members();
        borrow = new Borrow();
    }
    private boolean borrowRecordExists() {
        borrow.connection("SELECT * FROM Borrow WHERE BookID=" + data[0] + " AND MemberID=" + data[1]);
        return borrow.getBookID() == Integer.parseInt(data[0]) &&
               borrow.getMemberID() == Integer.parseInt(data[1]);
    }
    private void loadBookAndMember() {
        book.connection("SELECT * FROM Books WHERE BookID = " + data[0]);
        member.connection("SELECT * FROM Members WHERE MemberID = " + data[1]);
    }
    private void updateRecords(int available, int borrowed, int memberBooks) {
        available += 1;
        memberBooks -= 1;
        updateBookInDB(available, borrowed);
        updateMemberInDB(memberBooks);
        deleteBorrowRecord();
        dispose();
    }
    private void updateBookInDB(int available, int borrowed) {
        String query = "UPDATE Books SET NumberOfAvailbleBooks =" + available +
                ", NumberOfBorrowedBooks =" + borrowed;
        if (available == 1) {
            query += ", Availble = true";
        }
        query += " WHERE BookID =" + data[0];
        book.update(query);
    }
    private void updateMemberInDB(int memberBooks) {
        member.update("UPDATE Members SET NumberOfBooks =" + memberBooks +
                " WHERE MemberID =" + data[1]);
    }
    private void deleteBorrowRecord() {
        borrow.update("DELETE FROM Borrow WHERE BookID =" + data[0] +
                " AND MemberID =" + data[1]);
    }
    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    class keyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_ENTER) {
                getToolkit().beep();
                JOptionPane.showMessageDialog(null,
                        "This Field Only Accepts Integer Numbers",
                        "WARNING",
                        JOptionPane.DEFAULT_OPTION);
                e.consume();
            }
        }
        @Override
        public void keyPressed(KeyEvent k) {
            if (isFineCalculationTrigger(k)) {
                calculateFine();
            }
        }
        private boolean isFineCalculationTrigger(KeyEvent k) {
            return k.getKeyCode() == KeyEvent.VK_TAB ||
                   k.getKeyCode() == KeyEvent.VK_ENTER;
        }
        private void calculateFine() {
            try {
                int finePerDay = Integer.parseInt(txtFinePerDay.getText());
                Date returnDate = fetchReturnDate();
                if (returnDate == null) {
                    showWarning("Member ID or Book ID not found");
                    return;
                }
                computeAndDisplayFine(finePerDay, returnDate);
            } catch (Exception ex) {
                showWarning("Error calculating fine");
            }
        }
        private Date fetchReturnDate() throws Exception {
            Class.forName("org.gjt.mm.mysql.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Library", "root", "nielit");
            Statement st = con.createStatement();
            int bookid = Integer.parseInt(informationTextField[0].getText());
            int memid = Integer.parseInt(informationTextField[1].getText());
            ResultSet rs = st.executeQuery(
                    "SELECT DayOfReturn FROM Borrow WHERE MemberID=" + memid + " AND BookID=" + bookid);
            if (rs.next()) {
                return rs.getDate(1);
            }
            return null;
        }
        private void computeAndDisplayFine(int finePerDay, java.sql.Date returnDate) {
            java.util.Date today = new java.util.Date();
            if (today.after(returnDate)) {
                long diff = today.getTime() - returnDate.getTime();
                int daysLate = (int) (diff / (1000 * 60 * 60 * 24));
                txtTotalFineAmt.setText(String.valueOf(finePerDay * daysLate));
            } else {
                txtTotalFineAmt.setText("0");
            }
        }
    }
}
