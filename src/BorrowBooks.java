import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;

public class BorrowBooks extends JInternalFrame {

    private final JLabel[] infoLabel = new JLabel[4];
    private final String[] infoText = {
            " Write the Book ID:",
            " Write the Member ID:",
            " The Current Date:",
            " The Return Date:"
    };

    private final JTextField[] textField = new JTextField[2];
    private final DateButton current_date = new DateButton();
    private final DateButton return_date = new DateButton();

    private Books book;
    private Members member;
    private Borrow borrow;
    private String[] data;

    private final JButton borrowButton = new JButton("Borrow");
    private final JButton cancelButton = new JButton("Cancel");

    public BorrowBooks() {
        super("Borrow Books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Export16.gif")));
        configureDates();
        buildNorth();
        buildCenter();
        buildSouth();
        borrowButton.addActionListener(e -> handleBorrow());
        cancelButton.addActionListener(e -> dispose());
        setVisible(true);
        pack();
    }

    /* ---------------- GUI BUILD ---------------- */

    private void configureDates() {
        current_date.setForeground(Color.red);
        current_date.setEnabled(false);
        return_date.setForeground(Color.red);
    }

    private void buildNorth() {
        JLabel title = new JLabel("BOOK INFORMATION");
        title.setFont(new Font("Tahoma", Font.BOLD, 14));
        JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        north.add(title);
        add("North", north);
    }

    private void buildCenter() {
        JPanel main = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(4, 2, 1, 1));

        for (int i = 0; i < 4; i++) {
            infoLabel[i] = new JLabel(infoText[i]);
            infoLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));
            panel.add(infoLabel[i]);

            switch (i) {
                case 2 -> panel.add(current_date);
                case 3 -> panel.add(return_date);
                default -> {
                    textField[i] = new JTextField();
                    textField[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
                    textField[i].addKeyListener(new DigitKey());
                    panel.add(textField[i]);
                }
            }
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        borrowButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnPanel.add(borrowButton);

        main.add("Center", panel);
        main.add("South", btnPanel);
        main.setBorder(BorderFactory.createTitledBorder("Borrow a book:"));
        add("Center", main);
    }

    private void buildSouth() {
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        south.add(cancelButton);
        south.setBorder(BorderFactory.createEtchedBorder());
        add("South", south);
    }

    /* ---------------- VALIDATION ---------------- */

    private boolean isCorrect() {
        data = new String[4];

        for (int i = 0; i < 4; i++) {
            String value;
            if (i <= 1) {
                value = textField[i].getText().trim();
            } else if (i == 2) {
                value = current_date.getText().trim();
            } else {
                value = return_date.getText().trim();
            }
            if (value.isEmpty()) return false;
            data[i] = value;
        }
        return true;
    }

    private boolean isReturnDateValid() {
        return new Date().before(return_date.getDate());
    }

    private boolean isBorrowerInfoValid() {
        Date today = new Date();

        book.connection("SELECT * FROM Books WHERE BookID = " + data[0]);
        member.connection("SELECT * FROM Members WHERE MemberID = " + data[1]);

        return memberNotExpired(today) && bookAndMemberFound();
    }

    private boolean memberNotExpired(Date today) {
        if (!today.before(member.getValidUpto())) {
            warn("Member is Expired");
            return false;
        }
        return true;
    }

    private boolean bookAndMemberFound() {
        if (book.getBookID() < 1 || member.getMemberID() < 1) {
            warn("Member ID or Book ID not found");
            return false;
        }
        return true;
    }

    private boolean alreadyBorrowed() {
        borrow.connection("SELECT * FROM Borrow WHERE BookID=" + data[0] +
                " AND MemberID=" + data[1]);

        return borrow.getBookID() == Integer.parseInt(data[0]) &&
                borrow.getMemberID() == Integer.parseInt(data[1]);
    }

    /* ---------------- MAIN LOGIC ---------------- */

    private void handleBorrow() {
        if (!isCorrect()) {
            warn("Please complete the information");
            return;
        }
        new Thread(this::processBorrow).start();
    }

    private void processBorrow() {
        if (!isReturnDateValid()) return;

        initObjects();

        if (alreadyBorrowed()) {
            warn("The book is already borrowed by this member");
            clearFields();
            return;
        }

        if (!isBorrowerInfoValid()) return;

        finalizeBorrow();
    }

    private void initObjects() {
        book = new Books();
        member = new Members();
        borrow = new Borrow();
    }

    private void finalizeBorrow() {
        int available = book.getNumberOfAvailbleBooks();
        int borrowed = book.getNumberOfBorrowedBooks() + 1;
        int memberTotal = member.getNumberOfBooks() + 1;

        if (available < 1) {
            warn("The book is borrowed");
            return;
        }

        book.update(bookUpdateQuery(available - 1, borrowed));
        member.update("UPDATE Members SET NumberOfBooks=" + memberTotal +
                " WHERE MemberID=" + data[1]);

        borrow.update("INSERT INTO Borrow (BookID, MemberID, DayOfBorrowed, DayOfReturn) VALUES (" +
                data[0] + "," + data[1] + ",'" + data[2] + "','" + data[3] + "')");

        dispose();
    }

    private String bookUpdateQuery(int available, int borrowed) {
        String q = "UPDATE Books SET NumberOfAvailbleBooks=" + available +
                ", NumberOfBorrowedBooks=" + borrowed;
        if (available == 0) q += ", Availble=false ";
        return q + " WHERE BookID=" + data[0];
    }

    /* ---------------- HELPERS ---------------- */

    private void warn(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void clearFields() {
        for (JTextField tf : textField) if (tf != null) tf.setText(null);
    }

    /* ---------------- LISTENER ---------------- */

    private static class DigitKey extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE &&
                    c != KeyEvent.VK_ENTER && c != KeyEvent.VK_DELETE) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null,
                        "This Field Only Accept Integer Number",
                        "WARNING", JOptionPane.DEFAULT_OPTION);
                e.consume();
            }
        }
    }
}
