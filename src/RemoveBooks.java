import javax.swing.*;
import java.awt.*;

public class RemoveBooks extends JInternalFrame {

    private final JTextField bookIdField = new JTextField();
    private final JButton removeButton = new JButton("Remove");
    private final JButton exitButton = new JButton("Exit");

    private Books book;
    private int bookId;

    public RemoveBooks() {
        super("Remove Books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Delete16.gif")));

        buildNorth();
        buildCenter();
        buildSouth();

        removeButton.addActionListener(e -> handleRemove());
        exitButton.addActionListener(e -> dispose());

        setVisible(true);
        pack();
    }

    /* --------------------- UI BUILD --------------------- */

    private void buildNorth() {
        JLabel title = new JLabel("BOOK INFORMATION");
        title.setFont(new Font("Tahoma", Font.BOLD, 14));
        JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        north.add(title);
        add("North", north);
    }

    private void buildCenter() {
        JLabel lbl = new JLabel(" Write the Book ID:");
        lbl.setFont(new Font("Tahoma", Font.BOLD, 11));

        bookIdField.setFont(new Font("Tahoma", Font.PLAIN, 11));
        bookIdField.addKeyListener(new DigitOnlyKeyListener());

        JPanel input = new JPanel(new GridLayout(1, 2, 5, 5));
        input.add(lbl);
        input.add(bookIdField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        removeButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnPanel.add(removeButton);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Remove a book:"));
        center.add(input, BorderLayout.CENTER);
        center.add(btnPanel, BorderLayout.SOUTH);

        add("Center", center);
    }

    private void buildSouth() {
        exitButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(exitButton);
        add("South", south);
    }

    /* --------------------- BUSINESS LOGIC --------------------- */

    private boolean isValidInput() {
        try {
            bookId = Integer.parseInt(bookIdField.getText().trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void handleRemove() {
        if (!isValidInput()) {
            DialogUtils.warn(this, "Please enter a valid Book ID.");
            return;
        }
        new Thread(this::processRemoval).start();
    }

    private void processRemoval() {
        book = new Books();
        book.connection("SELECT * FROM Books WHERE BookID = " + bookId);

        if (book.getBookID() < 1) {
            DialogUtils.error(this, "The BookID is incorrect!");
            clearField();
            return;
        }

        int total = book.getNumberOfBooks();
        int available = book.getNumberOfAvailbleBooks();

        if (available < 1) {
            DialogUtils.error(this, "Book cannot be deleted — it is currently borrowed.");
            clearField();
            return;
        }

        if (available == total) deleteBook();
        else updateBookCount(total, available);

        dispose();
    }

    private void deleteBook() {
        book.update("DELETE FROM Books WHERE BookID = " + bookId);
    }

    private void updateBookCount(int total, int available) {
        int newTotal = total - 1;
        int newAvail = available - 1;
        boolean availFlag = newAvail > 0;

        String query =
                "UPDATE Books SET NumberOfBooks=" + newTotal +
                        ", NumberOfAvailbleBooks=" + newAvail +
                        ", Availble=" + availFlag +
                        " WHERE BookID=" + bookId;

        book.update(query);
    }

    /* --------------------- HELPERS --------------------- */

    private void clearField() {
        bookIdField.setText("");
    }
}
