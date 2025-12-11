import javax.swing.*;
import java.awt.*;

public class SearchBooksAndMembers extends JInternalFrame {

    private final JTextField bookKeyField = new JTextField();
    private final JTextField memberKeyField = new JTextField();

    private final JButton searchBooksButton = new JButton("Search");
    private final JButton searchMembersButton = new JButton("Search");
    private final JButton cancelButton = new JButton("Cancel");

    private final JComboBox<String> bookTypeBox =
            new JComboBox<>(new String[]{"BookID", "Subject", "Title", "Author", "Publisher", "ISBN"});

    private final JComboBox<String> memberTypeBox =
            new JComboBox<>(new String[]{"MemberID", "Name", "EMail", "Major"});

    private Books book;
    private Members member;

    public SearchBooksAndMembers() {
        super("Search", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Find16.gif")));

        buildNorth();
        buildCenter();
        buildSouth();
        attachListeners();

        setVisible(true);
        pack();
    }

    /* ------------------------ NORTH ------------------------ */

    private void buildNorth() {
        JLabel title = new JLabel("Search for Books and Members");
        title.setFont(new Font("Tahoma", Font.BOLD, 14));

        JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        north.add(title);
        add("North", north);
    }

    /* ------------------------ CENTER ------------------------ */

    private void buildCenter() {
        JPanel center = new JPanel(new BorderLayout());

        center.add(buildBooksPanel(), BorderLayout.WEST);
        center.add(buildMembersPanel(), BorderLayout.EAST);

        add("Center", center);
    }

    private JPanel buildBooksPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Search for books:"));

        JPanel form = new JPanel(new GridLayout(2, 2, 4, 4));
        JLabel lblType = new JLabel(" Search by: ");
        JLabel lblKey = new JLabel(" Keyword: ");

        bookKeyField.addKeyListener(new DigitOnlyKeyListener(
            () -> bookTypeBox.getSelectedItem().equals("BookID"),
            "This Field Only Accepts Integer Numbers"));

        form.add(lblType);
        form.add(bookTypeBox);
        form.add(lblKey);
        form.add(bookKeyField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(searchBooksButton);

        p.add(form, BorderLayout.NORTH);
        p.add(btnPanel, BorderLayout.SOUTH);

        return p;
    }

    private JPanel buildMembersPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Search for members:"));

        JPanel form = new JPanel(new GridLayout(2, 2, 4, 4));
        JLabel lblType = new JLabel(" Search by: ");
        JLabel lblKey = new JLabel(" Keyword: ");

        memberKeyField.addKeyListener(new DigitOnlyKeyListener(
            () -> memberTypeBox.getSelectedItem().equals("MemberID"),
            "This Field Only Accepts Integer Numbers"));

        form.add(lblType);
        form.add(memberTypeBox);
        form.add(lblKey);
        form.add(memberKeyField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(searchMembersButton);

        p.add(form, BorderLayout.NORTH);
        p.add(btnPanel, BorderLayout.SOUTH);

        return p;
    }

    /* ------------------------ SOUTH ------------------------ */

    private void buildSouth() {
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(cancelButton);
        add("South", south);
    }

    /* ------------------------ LISTENERS ------------------------ */

    private void attachListeners() {
        searchBooksButton.addActionListener(e -> handleBookSearch());
        searchMembersButton.addActionListener(e -> handleMemberSearch());
        cancelButton.addActionListener(e -> dispose());
    }

    /* ------------------------ SEARCH LOGIC ------------------------ */

    private boolean prepareBookSearch(String[] data) {
        data[0] = bookTypeBox.getSelectedItem().toString();
        String key = bookKeyField.getText().trim();
        if (key.isEmpty()) return false;
        data[1] = data[0].equals("BookID") ? key : "'%" + key + "%'";
        return true;
    }

    private boolean prepareMemberSearch(String[] data) {
        data[0] = memberTypeBox.getSelectedItem().toString();
        String key = memberKeyField.getText().trim();
        if (key.isEmpty()) return false;
        data[1] = data[0].equals("MemberID") ? key : "'%" + key + "%'";
        return true;
    }

    private void handleBookSearch() {
        String[] data = new String[2];
        if (!prepareBookSearch(data)) {
            DialogUtils.warn(this, "Please complete the information");
            return;
        }

        String checkQuery = "SELECT * FROM Books WHERE " + data[0] + " LIKE " + data[1];
        String listQuery =
                "SELECT BookID, Subject, Title, Author, Publisher, Copyright,"
                        + "Edition, Pages, NumberOfBooks, ISBN, Library, Availble, ShelfNo "
                        + "FROM Books WHERE " + data[0] + " LIKE " + data[1];

        book = new Books();
        book.connection(checkQuery);

        if (book.getBookID() == 0) {
            DialogUtils.warn(this, "No Match(es)");
            bookKeyField.setText("");
            return;
        }

        openList(new ListSearchBooks(listQuery));
    }

    private void handleMemberSearch() {
        String[] data = new String[2];
        if (!prepareMemberSearch(data)) {
            DialogUtils.warn(this, "Please complete the information");
            return;
        }

        String checkQuery = "SELECT * FROM Members WHERE " + data[0] + " LIKE " + data[1];
        String listQuery = 
                "SELECT MemberID, RegNo, Name, EMail, Major, ValidUpto "
                        + "FROM Members WHERE " + data[0] + " LIKE " + data[1];

        member = new Members();
        member.connection(checkQuery);

        if (member.getMemberID() == 0) {
            DialogUtils.warn(this, "No Match(es)");
            memberKeyField.setText("");
            return;
        }

        openList(new ListSearchMembers(listQuery));
    }

    private void openList(JInternalFrame frame) {
        getParent().add(frame);
        try { frame.setSelected(true); } catch (Exception ignored) {}
        dispose();
    }

    /* ------------------------ HELPERS ------------------------ */

}
