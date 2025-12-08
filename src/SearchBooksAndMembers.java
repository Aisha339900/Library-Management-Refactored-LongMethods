/*
 * Refactored SearchBooksAndMembers.java — reduced cognitive complexity
 * Functionality is unchanged, only reorganized for readability.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SearchBooksAndMembers extends JInternalFrame {

    private JPanel northPanel = new JPanel();
    private JLabel title = new JLabel("Search for Books and Members");

    private JPanel center = new JPanel();

    // Books section
    private JPanel centerBooksPanel = new JPanel();
    private JPanel searchBooksPanel = new JPanel();
    private JPanel searchBooksButtonPanel = new JPanel();

    private JLabel searchBooksLabel = new JLabel(" Search by: ");
    private JComboBox searchBooksTypes;
    private String[] booksTypes = {"BookID", "Subject", "Title", "Author", "Publisher", "ISBN"};

    private JLabel booksKey = new JLabel(" Write the Keyword: ");
    private JTextField booksKeyTextField = new JTextField();
    private JButton searchBooksButton = new JButton("Search");

    // Members section
    private JPanel centerMembersPanel = new JPanel();
    private JPanel searchMembersPanel = new JPanel();
    private JPanel searchMembersButtonPanel = new JPanel();

    private JLabel searchMembersLabel = new JLabel(" Search by: ");
    private JComboBox searchMembersTypes;
    private String[] membersTypes = {"MemberID", "Name", "EMail", "Major"};

    private JLabel membersKey = new JLabel(" Write the Keyword: ");
    private JTextField membersKeyTextField = new JTextField();
    private JButton searchMembersButton = new JButton("Search");

    private JPanel southPanel = new JPanel();
    private JButton cancelButton = new JButton("Cancel");

    private String[] booksData;
    private String[] membersData;

    private ListSearchBooks listBooks;
    private ListSearchMembers listMembers;

    private Books book;
    private Members member;

    // --------------------------- Constructor ------------------------------

    public SearchBooksAndMembers() {

        super("Search", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Find16.gif")));
        Container cp = getContentPane();

        buildNorth(cp);
        buildCenter(cp);
        buildSouth(cp);

        attachListeners();
        setVisible(true);
        pack();
    }

    // --------------------------- UI Builders ------------------------------

    private void buildNorth(Container cp) {
        northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        title.setFont(new Font("Tahoma", Font.BOLD, 14));
        northPanel.add(title);
        cp.add("North", northPanel);
    }

    private void buildCenter(Container cp) {
        center.setLayout(new BorderLayout());

        buildBooksPanel();
        buildMembersPanel();

        cp.add("Center", center);
    }

    private void buildBooksPanel() {
        centerBooksPanel.setLayout(new BorderLayout());
        searchBooksPanel.setLayout(new GridLayout(2, 2, 1, 1));

        searchBooksPanel.add(searchBooksLabel);
        searchBooksPanel.add(searchBooksTypes = new JComboBox(booksTypes));

        searchBooksPanel.add(booksKey);
        searchBooksPanel.add(booksKeyTextField);
        booksKeyTextField.addKeyListener(new BooksKeyListener());

        centerBooksPanel.add("North", searchBooksPanel);

        searchBooksButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        searchBooksButtonPanel.add(searchBooksButton);
        centerBooksPanel.add("South", searchBooksButtonPanel);

        centerBooksPanel.setBorder(BorderFactory.createTitledBorder("Search for books:"));
        center.add("West", centerBooksPanel);
    }

    private void buildMembersPanel() {
        centerMembersPanel.setLayout(new BorderLayout());
        searchMembersPanel.setLayout(new GridLayout(2, 2, 1, 1));

        searchMembersPanel.add(searchMembersLabel);
        searchMembersPanel.add(searchMembersTypes = new JComboBox(membersTypes));

        searchMembersPanel.add(membersKey);
        searchMembersPanel.add(membersKeyTextField);
        membersKeyTextField.addKeyListener(new MembersKeyListener());

        centerMembersPanel.add("North", searchMembersPanel);

        searchMembersButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        searchMembersButtonPanel.add(searchMembersButton);
        centerMembersPanel.add("South", searchMembersButtonPanel);

        centerMembersPanel.setBorder(BorderFactory.createTitledBorder("Search for members:"));
        center.add("East", centerMembersPanel);
    }

    private void buildSouth(Container cp) {
        southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(cancelButton);
        southPanel.setBorder(BorderFactory.createEtchedBorder());
        cp.add("South", southPanel);
    }

    // ------------------------ Validation Methods ---------------------------

    private boolean isBooksDataCorrect() {
        booksData = new String[2];
        booksData[0] = searchBooksTypes.getSelectedItem().toString();

        String keyword = booksKeyTextField.getText();
        if (keyword.isEmpty()) return false;

        booksData[1] = booksData[0].equals("BookID") ? keyword : "'%" + keyword + "%'";
        return true;
    }

    private boolean isMembersDataCorrect() {
        membersData = new String[2];
        membersData[0] = searchMembersTypes.getSelectedItem().toString();

        String keyword = membersKeyTextField.getText();
        if (keyword.isEmpty()) return false;

        membersData[1] = membersData[0].equals("MemberID") ? keyword : "'%" + keyword + "%'";
        return true;
    }

    // ---------------------------- Listeners --------------------------------

    private void attachListeners() {

        searchBooksButton.addActionListener(e -> handleBooksSearch());
        searchMembersButton.addActionListener(e -> handleMembersSearch());
        cancelButton.addActionListener(e -> dispose());
    }

    // --------------------------- Search Logic ------------------------------

    private void handleBooksSearch() {

        if (!isBooksDataCorrect()) {
            warn("Please, complete the information");
            return;
        }

        book = new Books();
        String bookQuery = "SELECT * FROM Books WHERE " + booksData[0] + " LIKE " + booksData[1];
        String listQuery = "SELECT BookID, Subject, Title, Author, Publisher," +
                "Copyright, Edition, Pages, NumberOfBooks, ISBN, Library, Availble, ShelfNo " +
                "FROM Books WHERE " + booksData[0] + " LIKE " + booksData[1];

        book.connection(bookQuery);

        if (book.getBookID() != 0) {
            listBooks = new ListSearchBooks(listQuery);
            openList(listBooks);
        } else {
            warn("No Match(es)");
            booksKeyTextField.setText(null);
        }
    }

    private void handleMembersSearch() {

        if (!isMembersDataCorrect()) {
            warn("Please, complete the information");
            return;
        }

        member = new Members();
        String query = "SELECT * FROM Members WHERE " + membersData[0] + " LIKE " + membersData[1];
        String listQuery = "SELECT MemberID, RegNo, Name, EMail, Major, ValidUpto FROM Members WHERE " +
                membersData[0] + " LIKE " + membersData[1];

        member.connection(query);

        if (member.getMemberID() != 0) {
            listMembers = new ListSearchMembers(listQuery);
            openList(listMembers);
        } else {
            warn("No Match(es)");
            membersKeyTextField.setText(null);
        }
    }

    private void openList(JInternalFrame frame) {
        getParent().add(frame);
        try { frame.setSelected(true); } catch (Exception ignored) {}
        dispose();
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    // ------------------------- Key Listeners -------------------------------

    class BooksKeyListener extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            if (searchBooksTypes.getSelectedItem().equals("BookID")) {
                validateDigitOnly(e);
            }
        }
    }

    class MembersKeyListener extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            if (searchMembersTypes.getSelectedItem().equals("MemberID")) {
                validateDigitOnly(e);
            }
        }
    }

    private void validateDigitOnly(KeyEvent e) {
        char c = e.getKeyChar();
        if (!Character.isDigit(c) &&
            c != KeyEvent.VK_BACK_SPACE &&
            c != KeyEvent.VK_DELETE &&
            c != KeyEvent.VK_ENTER) {

            getToolkit().beep();
            JOptionPane.showMessageDialog(null,
                    "This Field Only Accepts Integer Number",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
            e.consume();
        }
    }
}
