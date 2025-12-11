import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ReturnBooks extends JInternalFrame implements ActionListener {

    /* ------------ UI Fields ------------ */
    private final JTextField[] fields = {new JTextField(), new JTextField()};
    private final JTextField txtFinePerDay = new JTextField();
    private final JTextField txtTotalFineAmt = new JTextField();

    private final JButton returnButton = new JButton("Return");
    private final JButton cancelButton = new JButton("Cancel");

    /* ------------ Model Objects ------------ */
    private Books book;
    private Members member;
    private Borrow borrow;
    private String[] data;

    public ReturnBooks() {
        super("Return books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Import16.gif")));

        buildNorth();
        buildCenter();
        buildSouth();

        returnButton.addActionListener(this);
        cancelButton.addActionListener(this);

        setVisible(true);
        pack();
    }

    /* ------------ Build UI ------------ */

    private void buildNorth() {
        JLabel t = new JLabel("BOOK INFORMATION");
        t.setFont(new Font("Tahoma", Font.BOLD, 14));
        JPanel n = new JPanel(new FlowLayout(FlowLayout.CENTER));
        n.add(t);
        add("North", n);
    }

    private void buildCenter() {
        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        String[] lbl = {" Write the Book ID:", " Write the Member ID:"};

        for (int i = 0; i < 2; i++) {
            JLabel l = new JLabel(lbl[i]);
            l.setFont(new Font("Tahoma", Font.BOLD, 11));

            fields[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
            fields[i].addKeyListener(new DigitOnlyKeyListener());

            form.add(l);
            form.add(fields[i]);
        }

        form.add(new JLabel(" Fine per Day"));
        form.add(txtFinePerDay);

        form.add(new JLabel(" Total fine amount"));
        txtTotalFineAmt.setEditable(false);
        form.add(txtTotalFineAmt);

        txtFinePerDay.addKeyListener(new FineCalcListener());

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Return a book:"));
        center.add(form, BorderLayout.CENTER);

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btn.add(returnButton);

        center.add(btn, BorderLayout.SOUTH);
        add("Center", center);
    }

    private void buildSouth() {
        JPanel s = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        s.add(cancelButton);
        add("South", s);
    }

    /* ------------ Validation ------------ */

    private boolean isCorrect() {
        data = new String[2];
        for (int i = 0; i < 2; i++) {
            String v = fields[i].getText().trim();
            if (v.isEmpty()) return false;
            data[i] = v;
        }
        return true;
    }

    private void clearInputs() {
        fields[0].setText("");
        fields[1].setText("");
        txtFinePerDay.setText("");
        txtTotalFineAmt.setText("");
    }

    /* ------------ Button Actions ------------ */

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnButton) handleReturn();
        else if (e.getSource() == cancelButton) dispose();
    }

    private void handleReturn() {
        if (!isCorrect()) {
            DialogUtils.warn(this, "Please, complete the information");
            return;
        }
        new Thread(this::processReturn).start();
    }

    /* ------------ Main Logic ------------ */

    private void processReturn() {

        initModels();

        if (!borrowRecordExists()) {
            DialogUtils.warn(this, "The book is not borrowed");
            clearInputs();
            return;
        }

        loadBookAndMember();

        int available = book.getNumberOfAvailbleBooks();
        int borrowed = book.getNumberOfBorrowedBooks() - 1;
        int memberBooks = member.getNumberOfBooks() - 1;

        if (memberBooks < 0) {
            DialogUtils.warn(this, "Invalid member book count");
            return;
        }

        updateRecords(available + 1, borrowed, memberBooks);
        deleteBorrowEntry();

        dispose();
    }

    private void initModels() {
        book = new Books();
        member = new Members();
        borrow = new Borrow();
    }

    private boolean borrowRecordExists() {
        borrow.connection(
                "SELECT * FROM Borrow WHERE BookID=" + data[0] +
                " AND MemberID=" + data[1]);

        return borrow.getBookID() == Integer.parseInt(data[0]) &&
               borrow.getMemberID() == Integer.parseInt(data[1]);
    }

    private void loadBookAndMember() {
        book.connection("SELECT * FROM Books WHERE BookID = " + data[0]);
        member.connection("SELECT * FROM Members WHERE MemberID = " + data[1]);
    }

    private void updateRecords(int newAvailable, int newBorrowed, int memberBookCount) {
        String bookQuery =
                "UPDATE Books SET NumberOfAvailbleBooks=" + newAvailable +
                ", NumberOfBorrowedBooks=" + newBorrowed +
                (newAvailable == 1 ? ", Availble=true" : "") +
                " WHERE BookID=" + data[0];

        book.update(bookQuery);

        String memberQuery =
                "UPDATE Members SET NumberOfBooks=" + memberBookCount +
                " WHERE MemberID=" + data[1];

        member.update(memberQuery);
    }

    private void deleteBorrowEntry() {
        borrow.update(
                "DELETE FROM Borrow WHERE BookID=" + data[0] +
                " AND MemberID=" + data[1]);
    }

    /* ------------ Fine Calculation Listener ------------ */

    private class FineCalcListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_TAB ||
                e.getKeyCode() == KeyEvent.VK_ENTER) {

                calculateFine();
            }
        }

        private void calculateFine() {
            try {
                int finePerDay = Integer.parseInt(txtFinePerDay.getText());
                java.sql.Date dueDate = fetchReturnDate();

                if (dueDate == null) {
                    DialogUtils.warn(ReturnBooks.this, "Member ID or Book ID not found");
                    return;
                }

                long now = System.currentTimeMillis();
                long diff = now - dueDate.getTime();

                int daysLate = diff > 0 ? (int)(diff / (1000 * 60 * 60 * 24)) : 0;
                txtTotalFineAmt.setText(String.valueOf(daysLate * finePerDay));

            } catch (Exception ex) {
                DialogUtils.warn(ReturnBooks.this, "Error calculating fine");
            }
        }

        private java.sql.Date fetchReturnDate() throws Exception {
            Class.forName("org.gjt.mm.mysql.Driver");
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Library", "root", "nielit");
                 Statement st = con.createStatement()) {

                ResultSet rs = st.executeQuery(
                        "SELECT DayOfReturn FROM Borrow WHERE MemberID=" + data[1] +
                        " AND BookID=" + data[0]);

                return rs.next() ? rs.getDate(1) : null;
            }
        }
    }
}
