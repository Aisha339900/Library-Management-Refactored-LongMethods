import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EditBooks extends JInternalFrame {

    private final JTextField bookIdField = new JTextField(25);
    private final JButton editButton = new JButton("Edit");
    private final JButton updateButton = new JButton("Update");
    private final JButton exitButton = new JButton("Exit");

    private final JTextField[] fields = new JTextField[10];
    private final String[] labels = {
            " The book subject: ", " The book title: ",
            " The name of the Author(s): ", " The name of the Publisher: ",
            " Copyright year for the book: ", " The edition number: ",
            " The number of Pages: ", " ISBN for the book: ",
            " The number of copies: ", " The name of the Library: "
    };

    private Books book;
    private String[] data;
    private boolean available;
    private int availableBooks;

    public EditBooks() {
        super("Edit Books", false, true, false, true);
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

    /* ------------------------- UI ------------------------- */

    private void buildNorth() {
        JLabel title = new JLabel("BOOK INFORMATION");
        title.setFont(new Font("Tahoma", Font.BOLD, 14));
        JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        north.add(title);
        add("North", north);
    }

    private void buildCenter() {
        JPanel main = new JPanel(new BorderLayout());
        main.add(buildEditSection(), BorderLayout.NORTH);
        main.add(buildBookSection(), BorderLayout.CENTER);
        add("Center", main);
    }

    private JPanel buildEditSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("BookID"));

        JPanel left = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("BookID:");
        lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
        left.add(lbl, BorderLayout.WEST);

        bookIdField.setFont(new Font("Tahoma", Font.PLAIN, 11));
        bookIdField.addKeyListener(new DigitOnly());
        left.add(bookIdField, BorderLayout.CENTER);

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btn.add(editButton);

        panel.add(left, BorderLayout.NORTH);
        panel.add(btn, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBookSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(BorderFactory.createTitledBorder("Edit a book: "));

        JPanel labelPanel = new JPanel(new GridLayout(labels.length, 1));
        JPanel fieldPanel = new JPanel(new GridLayout(labels.length, 1));

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
            labelPanel.add(lbl);

            fields[i] = new JTextField(25);
            fields[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
            if (i == 4 || i == 5 || i == 6 || i == 8)
                fields[i].addKeyListener(new DigitOnly());

            fieldPanel.add(fields[i]);
        }

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btn.add(updateButton);

        section.add(labelPanel, BorderLayout.WEST);
        section.add(fieldPanel, BorderLayout.CENTER);
        section.add(btn, BorderLayout.SOUTH);
        return section;
    }

    private void buildSouth() {
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exitButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        south.add(exitButton);
        add("South", south);
    }

    /* ------------------------- VALIDATION ------------------------- */

    private boolean isEditCorrect() {
        return !bookIdField.getText().trim().isEmpty();
    }

    private boolean isCorrect() {
        data = new String[10];

        for (int i = 0; i < fields.length; i++) {
            String value = fields[i].getText().trim();
            if (value.isEmpty()) return false;
            data[i] = value;
        }
        return true;
    }

    /* ------------------------- EDIT LOGIC ------------------------- */

    private void handleEdit() {
        if (!isEditCorrect()) {
            warn("Please enter the BookID");
            return;
        }
        new Thread(this::processEdit).start();
    }

    private void processEdit() {
        book = new Books();
        book.connection("SELECT * FROM Books WHERE BookID = " + bookIdField.getText());

        if (!bookExists()) {
            error("Invalid BookID");
            bookIdField.setText("");
            clearFields();
            return;
        }

        fillFields();
    }

    private boolean bookExists() {
        return book.getCopyright() > 0;
    }

    private void fillFields() {
        fields[0].setText(book.getSubject());
        fields[1].setText(book.getTitle());
        fields[2].setText(book.getAuthor());
        fields[3].setText(book.getPublisher());
        fields[4].setText(book.getCopyright() + "");
        fields[5].setText(book.getEdition() + "");
        fields[6].setText(book.getPages() + "");
        fields[7].setText(book.getISBN());
        fields[8].setText(book.getNumberOfBooks() + "");
        fields[9].setText(book.getLibrary());
    }

    /* ------------------------- UPDATE LOGIC ------------------------- */

    private void handleUpdate() {
        if (!isCorrect()) {
            warn("Please complete the information");
            return;
        }
        new Thread(this::processUpdate).start();
    }

    private void processUpdate() {
        book = new Books();
        book.connection("SELECT * FROM Books WHERE BookID = " + bookIdField.getText());

        int borrowed = book.getNumberOfBorrowedBooks();
        int totalCopies = Integer.parseInt(data[8]);
        if (totalCopies < borrowed) {
            warn("Copies must be >= borrowed");
            return;
        }

        updateAvailability(totalCopies, borrowed);
        updateRecord();
        dispose();
    }

    private void updateAvailability(int total, int borrowed) {
        available = total > borrowed;
        availableBooks = Math.max(0, total - borrowed);
    }

    private void updateRecord() {
        String q = "UPDATE Books SET "
                + "Subject='" + data[0]
                + "',Title='" + data[1]
                + "',Author='" + data[2]
                + "',Publisher='" + data[3]
                + "',Copyright=" + data[4]
                + ",Edition=" + data[5]
                + ",Pages=" + data[6]
                + ",ISBN='" + data[7]
                + "',NumberOfBooks=" + data[8]
                + ",NumberOfAvailbleBooks=" + availableBooks
                + ",Library='" + data[9]
                + "',Availble=" + available
                + " WHERE BookID=" + bookIdField.getText();

        book.update(q);
    }

    /* ------------------------- HELPERS ------------------------- */

    private void warn(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void clearFields() {
        for (JTextField f : fields) f.setText("");
    }

    /* --------------------- KEY LISTENER ---------------------- */

    private static class DigitOnly extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE
                    && c != KeyEvent.VK_DELETE && c != KeyEvent.VK_ENTER) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null,
                        "Numbers only!", "WARNING", JOptionPane.DEFAULT_OPTION);
                e.consume();
            }
        }
    }
}
