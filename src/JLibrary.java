import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class JLibrary extends JFrame implements ActionListener {

    private final JDesktopPane desktop = new JDesktopPane();
    private final JTextField searchField = new JTextField(15);
    private final JButton goButton = new JButton("Go");
    private final StatusBar statusBar = new StatusBar();

    private final Menubar menu = new Menubar();
    private final Toolbar toolbar = new Toolbar();

    private final Map<Object, Runnable> actions = new HashMap<>();

    public JLibrary() {
        super("Library Management System");

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                ClassLoader.getSystemResource("images/Host16.gif")));

        setJMenuBar(menu);
        buildUI();
        registerActions();

        goButton.addActionListener(this);
        for (JButton b : toolbar.button) b.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });

        setVisible(true);
    }

    /* ---------------- UI ---------------- */

    private void buildUI() {
        Container cp = getContentPane();

        JPanel top = new JPanel(new BorderLayout());
        JToolBar searchBar = new JToolBar();
        searchBar.add(new JLabel("Book title: "));
        searchBar.add(searchField);
        searchBar.add(goButton);

        top.add(toolbar, BorderLayout.CENTER);
        cp.add("North", top);

        desktop.setBackground(new Color(153, 153, 255));
        cp.add("Center", desktop);
        cp.add("South", statusBar);
    }

    /* ---------------- ACTION REGISTRATION ---------------- */

    private void register(Object key, Runnable action) {
        actions.put(key, action);
    }

    private void registerActions() {
        // Exit
        register(menu.exit, this::exitApp);
        register(toolbar.button[19], this::exitApp);

        // Book Actions
        register(menu.addBook, () -> openFrame(new AddBooks()));
        register(toolbar.button[0], () -> openFrame(new AddBooks()));

        register(menu.listBook, () -> openFrame(new ListBooks()));
        register(toolbar.button[1], () -> openFrame(new ListBooks()));

        register(menu.listAvailbleBook, () -> openFrame(new ListAvailbleBooks()));
        register(toolbar.button[2], () -> openFrame(new ListAvailbleBooks()));

        register(menu.listBorrowedBook, () -> openFrame(new ListBorrowedBooks()));
        register(toolbar.button[3], () -> openFrame(new ListBorrowedBooks()));

        register(menu.editBook, () -> openFrame(new EditBooks()));
        register(toolbar.button[4], () -> openFrame(new EditBooks()));

        register(menu.removeBook, () -> openFrame(new RemoveBooks()));
        register(toolbar.button[5], () -> openFrame(new RemoveBooks()));

        // Members
        register(menu.addMember, () -> openFrame(new AddMembers()));
        register(toolbar.button[6], () -> openFrame(new AddMembers()));

        register(menu.listMember, () -> openFrame(new ListMembers()));
        register(toolbar.button[7], () -> openFrame(new ListMembers()));

        register(menu.editMember, () -> openFrame(new EditMembers()));
        register(toolbar.button[8], () -> openFrame(new EditMembers()));

        register(menu.removeMember, () -> openFrame(new RemoveMembers()));
        register(toolbar.button[9], () -> openFrame(new RemoveMembers()));

        // Search
        register(menu.searchBooksAndMembers, () -> openFrame(new SearchBooksAndMembers()));
        register(toolbar.button[10], () -> openFrame(new SearchBooksAndMembers()));

        // Borrow / Return / Issued
        register(menu.borrowBook, () -> openFrame(new BorrowBooks()));
        register(toolbar.button[11], () -> openFrame(new BorrowBooks()));

        register(menu.returnBook, () -> openFrame(new ReturnBooks()));
        register(toolbar.button[12], () -> openFrame(new ReturnBooks()));

        register(menu.listissuedbooks, () -> openFrame(new ListIssuedBooks()));
        register(toolbar.button[13], () -> openFrame(new ListIssuedBooks()));

        // Tools
        register(menu.notepad, this::openNotepad);
        register(toolbar.button[14], this::openNotepad);

        register(menu.calculator, this::openCalculator);
        register(toolbar.button[15], this::openCalculator);

        // Password / Delete User
        register(menu.changePassword, () -> openFrame(new ChangePassword()));
        register(toolbar.button[16], () -> openFrame(new ChangePassword()));

        register(menu.deleteLibrarian, () -> openFrame(new DeleteLibrarian()));
        register(toolbar.button[17], () -> openFrame(new DeleteLibrarian()));

        // About
        register(menu.about, this::openAbout);
        register(toolbar.button[18], this::openAbout);
    }

    /* ---------------- ACTIONS ---------------- */

    @Override
    public void actionPerformed(ActionEvent e) {
        Runnable task = actions.get(e.getSource());
        if (task != null) new Thread(task).start();
    }

    private void openFrame(JInternalFrame frame) {
        desktop.add(frame);
        try { frame.setSelected(true); }
        catch (Exception ignored) {}
    }

    private void exitApp() {
        dispose();
        System.exit(0);
    }

    private void openNotepad() {
        try { Runtime.getRuntime().exec("notepad.exe"); }
        catch (Exception ignored) {}
    }

    private void openCalculator() {
        try { Runtime.getRuntime().exec("calc.exe"); }
        catch (Exception ignored) {}
    }

    private void openAbout() {
        JOptionPane.showMessageDialog(null, new About(),
                "About Library Management System", JOptionPane.PLAIN_MESSAGE);
    }
}
