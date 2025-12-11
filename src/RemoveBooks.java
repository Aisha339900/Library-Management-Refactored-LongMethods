public class RemoveBooks extends JInternalFrame {
    private JPanel northPanel = new JPanel();
    private JLabel title = new JLabel("BOOK INFORMATION");
    private JPanel centerPanel = new JPanel();
    private JPanel removePanel = new JPanel();
    private JLabel removeLabel = new JLabel(" Write the Book ID: ");
    private JTextField removeTextField = new JTextField();
    private int data;
    private JPanel removeMemberPanel = new JPanel();
    private JButton removeButton = new JButton("Remove");
    private JPanel southPanel = new JPanel();
    private JButton exitButton = new JButton("Exit");
    private Books book;
    private boolean availble;
    public boolean isCorrect() {
        if (!removeTextField.getText().equals("")) {
            data = Integer.parseInt(removeTextField.getText());
            return true;
        }
        return false;
    }
    public RemoveBooks() {
        super("Remove Books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Delete16.gif")));
        Container cp = getContentPane();
        northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        title.setFont(new Font("Tahoma", Font.BOLD, 14));
        northPanel.add(title);
        cp.add("North", northPanel);
        centerPanel.setLayout(new BorderLayout());
        removePanel.setLayout(new GridLayout(1, 2, 1, 1));
        removePanel.add(removeLabel);
        removePanel.add(removeTextField);
        removeTextField.addKeyListener(new keyListener());
        centerPanel.add("Center", removePanel);
        removeMemberPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        removeMemberPanel.add(removeButton);
        centerPanel.add("South", removeMemberPanel);
        centerPanel.setBorder(BorderFactory.createTitledBorder("Remove a book:"));
        cp.add("Center", centerPanel);
        removeLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        removeTextField.setFont(new Font("Tahoma", Font.PLAIN, 11));
        exitButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        removeButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(exitButton);
        southPanel.setBorder(BorderFactory.createEtchedBorder());
        cp.add("South", southPanel);
        removeButton.addActionListener(e -> handleRemoveAction());
        exitButton.addActionListener(e -> dispose());
        setVisible(true);
        pack();
    }
    private void handleRemoveAction() {
        if (!isCorrect()) {
            showWarning("Please, complete the information");
            return;
        }
        Thread runner = new Thread(this::processRemoval);
        runner.start();
    }
    private void processRemoval() {
        book = new Books();
        book.connection("SELECT * FROM Books WHERE BookID =" + data);
        int bookID = book.getBookID();
        if (bookID < 1) {
            showError("The BookID is wrong!");
            clearField();
            return;
        }
        int totalBooks = book.getNumberOfBooks();
        int availableBooks = book.getNumberOfAvailbleBooks();
        if (availableBooks < 1) {
            showError("Book can't be deleted, as it is already borrowed");
            clearField();
            return;
        }
        if (availableBooks == totalBooks) {
            deleteBook();
        } else {
            updateBookCount(totalBooks, availableBooks);
        }
        dispose();
    }
    private void deleteBook() {
        book.update("DELETE FROM Books WHERE BookID =" + data);
    }
    private void updateBookCount(int totalBooks, int availableBooks) {
        int newTotal = totalBooks - 1;
        int newAvailable = availableBooks - 1;
        availble = newAvailable > 0;
        String query = "UPDATE Books SET NumberOfBooks =" + newTotal +
                ", NumberOfAvailbleBooks=" + newAvailable +
                ", Availble=" + availble +
                " WHERE BookID =" + data;
        book.update(query);
    }
    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    private void clearField() {
        removeTextField.setText(null);
    }
    class keyListener extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!(Character.isDigit(c) ||
                    c == KeyEvent.VK_BACK_SPACE ||
                    c == KeyEvent.VK_ENTER ||
                    c == KeyEvent.VK_DELETE)) {
                getToolkit().beep();
                JOptionPane.showMessageDialog(null,
                        "This Field Only Accept Integer Number",
                        "WARNING", JOptionPane.DEFAULT_OPTION);
                e.consume();
            }
        }
    }
}
