/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;

/**
 * A public class
 */
public class BorrowBooks extends JInternalFrame {

    // ============================
    //       UI COMPONENTS
    // ============================
    private JPanel northPanel = new JPanel();
    private JLabel title = new JLabel("BOOK INFORMATION");

    private JPanel centerPanel = new JPanel();
    private JPanel informationPanel = new JPanel();
    private JLabel[] informationLabel = new JLabel[4];
    private String[] informationString = {
            " Write the Book ID:", 
            " Write the Member ID:",
            " The Current Date:", 
            " The Return Date:"
    };

    private JTextField[] informationTextField = new JTextField[2];
    private DateButton current_date;
    private DateButton return_date;

    private JPanel borrowButtonPanel = new JPanel();
    private JButton borrowButton = new JButton("Borrow");

    private JPanel southPanel = new JPanel();
    private JButton cancelButton = new JButton("Cancel");

    // ============================
    //       BUSINESS OBJECTS
    // ============================
    private Books book;
    private Members member;
    private Borrow borrow;

    private String[] data;

    public BorrowBooks() {
        super("Borrow Books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Export16.gif")));

        Container cp = getContentPane();
        current_date = new DateButton();
        current_date.setForeground(Color.red);
        current_date.setEnabled(false);

        return_date = new DateButton();
        return_date.setForeground(Color.red);

        // NORTH PANEL
        northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        title.setFont(new Font("Tahoma", Font.BOLD, 14));
        northPanel.add(title);
        cp.add("North", northPanel);

        // CENTER PANEL
        centerPanel.setLayout(new BorderLayout());
        informationPanel.setLayout(new GridLayout(4, 2, 1, 1));

        for (int i = 0; i < informationLabel.length; i++) {
            informationPanel.add(informationLabel[i] = new JLabel(informationString[i]));
            informationLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));

            if (i == 2) {
                informationPanel.add(current_date);
            } else if (i == 3) {
                informationPanel.add(return_date);
            } else {
                informationPanel.add(informationTextField[i] = new JTextField());
                informationTextField[i].setFont(new Font("Tahoma", Font.PLAIN, 11));

                if (i == 0 || i == 1) {
                    informationTextField[i].addKeyListener(new keyListener());
                }
            }
        }

        centerPanel.add("Center", informationPanel);

        // BORROW BUTTON PANEL
        borrowButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        borrowButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        borrowButtonPanel.add(borrowButton);
        centerPanel.add("South", borrowButtonPanel);

        centerPanel.setBorder(BorderFactory.createTitledBorder("Borrow a book:"));
        cp.add("Center", centerPanel);

        // SOUTH PANEL
        southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        cancelButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        southPanel.add(cancelButton);
        southPanel.setBorder(BorderFactory.createEtchedBorder());
        cp.add("South", southPanel);

        // ACTIONS
        borrowButton.addActionListener(ae -> handleBorrowAction());
        cancelButton.addActionListener(ae -> dispose());

        setVisible(true);
        pack();
    }

    // ============================
    //     VALIDATION METHODS
    // ============================

    private boolean isCorrect() {
        data = new String[4];

        for (int i = 0; i < informationLabel.length; i++) {

            if (i == 0 || i == 1) {
                if (!informationTextField[i].getText().equals("")) {
                    data[i] = informationTextField[i].getText();
                } else return false;
            }

            if (i == 2) {
                if (!current_date.getText().equals("")) {
                    data[i] = current_date.getText();
                } else return false;
            }

            if (i == 3) {
                if (!return_date.getText().equals("")) {
                    data[i] = return_date.getText();
                } else return false;
            }
        }
        return true;
    }

    private boolean isReturnDateValid() {
        Date today = new Date();
        Date returnDate = return_date.getDate();

        if (!today.before(returnDate)) {
            showWarning("Return Date is invalid");
            return false;
        }
        return true;
    }

    private boolean isBorrowerInfoValid() {
        Date today = new Date();

        book.connection("SELECT * FROM Books WHERE BookID = " + data[0]);
        member.connection("SELECT * FROM Members WHERE MemberID = " + data[1]);

        return isMemberNotExpired(today) && isBookAndMemberFound();
    }

    private boolean isMemberNotExpired(Date today) {
        Date expiry = member.getValidUpto();

        if (!today.before(expiry)) {
            showWarning("Member is Expired");
            return false;
        }
        return true;
    }

    private boolean isBookAndMemberFound() {
        if (book.getBookID() < 1 || member.getMemberID() < 1) {
            showWarning("Member ID or Book ID entered not found on database");
            return false;
        }
        return true;
    }

    private boolean isAlreadyBorrowedByMember() {
        borrow.connection("SELECT * FROM Borrow WHERE BookID=" + data[0] +
                " AND MemberID=" + data[1]);

        return (borrow.getBookID() == Integer.parseInt(data[0])) &&
               (borrow.getMemberID() == Integer.parseInt(data[1]));
    }

    // ============================
    //      MAIN ACTION LOGIC
    // ============================

    private void handleBorrowAction() {
        if (!isCorrect()) {
            showWarning("Please, complete the information");
            return;
        }
        new Thread(this::processBorrowRequest).start();
    }

    private void processBorrowRequest() {
		if (!validateBeforeBorrowing()) return;
		processBookBorrow();
	}


    private void initializeObjects() {
        book = new Books();
        member = new Members();
        borrow = new Borrow();
    }

    private void processBookBorrow() {
    int[] values = calculateBorrowValues();
    int available = values[0];
    int borrowed = values[1];
    int totalForMember = values[2];

    if (!isBookAvailable(available)) return;

    finalizeBorrow(available, borrowed, totalForMember);
}



    private void updateBook(int available, int borrowed) {
        String query = "UPDATE Books SET NumberOfAvailbleBooks=" + available +
                ", NumberOfBorrowedBooks=" + borrowed;

        if (available == 0) query += ", Availble=false ";

        query += " WHERE BookID=" + data[0];
        book.update(query);
    }

    private void updateMember(int totalBooks) {
        member.update("UPDATE Members SET NumberOfBooks=" + totalBooks +
                " WHERE MemberID=" + data[1]);
    }

    private void insertBorrowRecord() {
        borrow.update("INSERT INTO Borrow (BookID, MemberID, DayOfBorrowed, DayOfReturn) VALUES (" +
                data[0] + "," + data[1] + ",'" + data[2] + "','" + data[3] + "')");
    }

    // ============================
    //       UI HELPERS
    // ============================

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void clearTextField() {
        for (int i = 0; i < informationTextField.length; i++) {
            informationTextField[i].setText(null);
        }
    }

    class keyListener extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!(Character.isDigit(c) ||
                    c == KeyEvent.VK_BACK_SPACE ||
                    c == KeyEvent.VK_ENTER ||
                    c == KeyEvent.VK_DELETE)) {
                getToolkit().beep();
                JOptionPane.showMessageDialog(null, "This Field Only Accept Integer Number",
                        "WARNING", JOptionPane.DEFAULT_OPTION);
                e.consume();
            }
        }
    }

	private boolean validateBeforeBorrowing() {

    if (!isReturnDateValid()) return false;

    initializeObjects();

    if (isAlreadyBorrowedByMember()) {
        showWarning("The book is already borrowed by this member");
        clearTextField();
        return false;
    }

    return isBorrowerInfoValid();
}

private boolean isBookAvailable(int available) {
    if (available < 1) {
        showWarning("The book is borrowed");
        return false;
    }
    return true;
}

private int[] calculateBorrowValues() {
    int available = book.getNumberOfAvailbleBooks();
    int borrowed = 1 + book.getNumberOfBorrowedBooks();
    int totalForMember = 1 + member.getNumberOfBooks();
    return new int[]{available, borrowed, totalForMember};
}

private void finalizeBorrow(int available, int borrowed, int totalForMember) {
    updateBook(available - 1, borrowed);
    updateMember(totalForMember);
    insertBorrowRecord();
    dispose();
}



}
