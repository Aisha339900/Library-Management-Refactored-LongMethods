import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
public class JLibrary extends JFrame implements ActionListener {
    private JPanel searchPanel = new JPanel();
    private JToolBar searchToolBar = new JToolBar();
    private JLabel searchLabel = new JLabel("Book title: ");
    private JTextField searchTextField = new JTextField(15);
    private JButton goButton = new JButton("Go");
    private JDesktopPane desktop = new JDesktopPane();
    private Menubar menu;
    private Toolbar toolbar;
    private StatusBar statusbar = new StatusBar();
    private Map<Object, Runnable> actionMap = new HashMap<>();
    private ListBooks listBooks;
    private AddBooks addBooks;
    private ListAvailbleBooks listAvailble;
    private ListBorrowedBooks listBorrowed;
    private EditBooks editBooks;
    private RemoveBooks removeBooks;
    private BorrowBooks borrowBooks;
    private ReturnBooks returnBooks;
    private AddMembers addMembers;
    private ListMembers listMembers;
    private EditMembers editMembers;
    private RemoveMembers removeMembers;
    private SearchBooksAndMembers search;
    private ListIssuedBooks listIssued;
    private ChangePassword changePassword;
    private DeleteLibrarian deleteUser;
    public JLibrary() {
        super("Library Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Image image = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("images/Host16.gif"));
        setIconImage(image);
        menu = new Menubar();
        toolbar = new Toolbar();
        setJMenuBar(menu);
        initializeUI();
        initializeListeners();
        initializeActionMap();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setVisible(true);
    }
    private void initializeUI() {
        Container cp = getContentPane();
        searchLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        searchTextField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        goButton.setFont(new Font("Tahoma", Font.BOLD, 9));
        searchToolBar.add(searchLabel);
        searchToolBar.add(searchTextField);
        searchToolBar.add(goButton);
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add("Center", toolbar);
        cp.add("North", searchPanel);
        Color clr = new Color(153,153,255);
        desktop.setBackground(clr);
        cp.add("Center", desktop);
        cp.add("South", statusbar);
    }
    private void initializeListeners() {
        goButton.addActionListener(this);
        menu.exit.addActionListener(this);
        menu.addBook.addActionListener(this);
        menu.listBook.addActionListener(this);
        menu.listAvailbleBook.addActionListener(this);
        menu.listBorrowedBook.addActionListener(this);
        menu.editBook.addActionListener(this);
        menu.removeBook.addActionListener(this);
        menu.addMember.addActionListener(this);
        menu.listMember.addActionListener(this);
        menu.editMember.addActionListener(this);
        menu.removeMember.addActionListener(this);
        menu.searchBooksAndMembers.addActionListener(this);
        menu.borrowBook.addActionListener(this);
        menu.returnBook.addActionListener(this);
        menu.listissuedbooks.addActionListener(this);
        menu.notepad.addActionListener(this);
        menu.calculator.addActionListener(this);
        menu.about.addActionListener(this);
        menu.changePassword.addActionListener(this);
        menu.deleteLibrarian.addActionListener(this);
        for (JButton b : toolbar.button) {
            b.addActionListener(this);
        }
    }
    private void initializeActionMap() {
        actionMap.put(menu.exit, this::handleExit);
        actionMap.put(toolbar.button[19], this::handleExit);
        actionMap.put(menu.addBook, this::openAddBook);
        actionMap.put(toolbar.button[0], this::openAddBook);
        actionMap.put(menu.listBook, this::openListBooks);
        actionMap.put(toolbar.button[1], this::openListBooks);
        actionMap.put(menu.listAvailbleBook, this::openListAvailable);
        actionMap.put(toolbar.button[2], this::openListAvailable);
        actionMap.put(menu.listBorrowedBook, this::openListBorrowed);
        actionMap.put(toolbar.button[3], this::openListBorrowed);
        actionMap.put(menu.editBook, this::openEditBook);
        actionMap.put(toolbar.button[4], this::openEditBook);
        actionMap.put(menu.removeBook, this::openRemoveBook);
        actionMap.put(toolbar.button[5], this::openRemoveBook);
        actionMap.put(menu.addMember, this::openAddMember);
        actionMap.put(toolbar.button[6], this::openAddMember);
        actionMap.put(menu.listMember, this::openListMembers);
        actionMap.put(toolbar.button[7], this::openListMembers);
        actionMap.put(menu.editMember, this::openEditMember);
        actionMap.put(toolbar.button[8], this::openEditMember);
        actionMap.put(menu.removeMember, this::openRemoveMember);
        actionMap.put(toolbar.button[9], this::openRemoveMember);
        actionMap.put(menu.searchBooksAndMembers, this::openSearch);
        actionMap.put(toolbar.button[10], this::openSearch);
        actionMap.put(menu.borrowBook, this::openBorrow);
        actionMap.put(toolbar.button[11], this::openBorrow);
        actionMap.put(menu.returnBook, this::openReturn);
        actionMap.put(toolbar.button[12], this::openReturn);
        actionMap.put(menu.listissuedbooks, this::openIssued);
        actionMap.put(toolbar.button[13], this::openIssued);
        actionMap.put(menu.notepad, this::openNotepad);
        actionMap.put(toolbar.button[14], this::openNotepad);
        actionMap.put(menu.calculator, this::openCalculator);
        actionMap.put(toolbar.button[15], this::openCalculator);
        actionMap.put(menu.changePassword, this::openChangePassword);
        actionMap.put(toolbar.button[16], this::openChangePassword);
        actionMap.put(menu.deleteLibrarian, this::openDeleteLibrarian);
        actionMap.put(toolbar.button[17], this::openDeleteLibrarian);
        actionMap.put(menu.about, this::openAbout);
        actionMap.put(toolbar.button[18], this::openAbout);
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        Runnable action = actionMap.get(ae.getSource());
        if (action != null) {
            action.run();
        }
    }
    private void runAsync(Runnable task) {
        Thread runner = new Thread(task);
        runner.start();
    }
    private void openAddBook() {
        runAsync(() -> openInternalFrame(new AddBooks()));
    }
    private void openListBooks() {
        runAsync(() -> openInternalFrame(new ListBooks()));
    }
    private void openListAvailable() {
        runAsync(() -> openInternalFrame(new ListAvailbleBooks()));
    }
    private void openListBorrowed() {
        runAsync(() -> openInternalFrame(new ListBorrowedBooks()));
    }
    private void openEditBook() {
        runAsync(() -> openInternalFrame(new EditBooks()));
    }
    private void openRemoveBook() {
        runAsync(() -> openInternalFrame(new RemoveBooks()));
    }
    private void openAddMember() {
        runAsync(() -> openInternalFrame(new AddMembers()));
    }
    private void openListMembers() {
        runAsync(() -> openInternalFrame(new ListMembers()));
    }
    private void openEditMember() {
        runAsync(() -> openInternalFrame(new EditMembers()));
    }
    private void openRemoveMember() {
        runAsync(() -> openInternalFrame(new RemoveMembers()));
    }
    private void openSearch() {
        runAsync(() -> openInternalFrame(new SearchBooksAndMembers()));
    }
    private void openBorrow() {
        runAsync(() -> openInternalFrame(new BorrowBooks()));
    }
    private void openReturn() {
        runAsync(() -> openInternalFrame(new ReturnBooks()));
    }
    private void openIssued() {
        runAsync(() -> openInternalFrame(new ListIssuedBooks()));
    }
    private void openNotepad() {
        runAsync(() -> {
            try { Runtime.getRuntime().exec("notepad.exe"); } catch (Exception ignored) {}
        });
    }
    private void openCalculator() {
        runAsync(() -> {
            try { Runtime.getRuntime().exec("calc.exe"); } catch (Exception ignored) {}
        });
    }
    private void openChangePassword() {
        runAsync(() -> openInternalFrame(new ChangePassword()));
    }
    private void openDeleteLibrarian() {
        deleteUser = new DeleteLibrarian();
    }
    private void openAbout() {
        runAsync(() ->
                JOptionPane.showMessageDialog(null, new About(),
                        "About Library Management System", JOptionPane.PLAIN_MESSAGE)
        );
    }
    private void openInternalFrame(JInternalFrame frame) {
        desktop.add(frame);
        try { frame.setSelected(true); } catch (Exception ignored) {}
    }
    private void handleExit() {
        dispose();
        System.exit(0);
    }
}
