/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ganesh Sharma
 */

//import the packages for using the classes in them into the program
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JLibrary extends JFrame implements ActionListener {
    //for creating the JPanel
    private JPanel searchPanel = new JPanel();
    //for creating the JToolBar for the program
    private JToolBar searchToolBar = new JToolBar();
    //for creating the label
    private JLabel searchLabel = new JLabel("Book title: ");
    //for creating the JTextField to use it on the searchToolBar
    private JTextField searchTextField = new JTextField(15);
    //for creating the JButton to use it on the searchToolBar
    private JButton goButton = new JButton("Go");
    //for creating JDeskTopPane for using JInternalFrame on the desktop
    private JDesktopPane desktop = new JDesktopPane();
    //for creating JSplitPane
    private JSplitPane splitPane;
    //for creating JScrollPane for JDesktopPane
    private JScrollPane desktopScrollPane;
    private JScrollPane treeScrollPane;
    private Menubar menu;
    private Toolbar toolbar;
    private StatusBar statusbar = new StatusBar();
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
    //constructor of JLibrary
    public JLibrary() {
        //for setting the title for the frame
        super("Library Management System ");
        //for setting the size
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //for setting resizable to false
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image image = kit.getImage(ClassLoader.getSystemResource("images/Host16.gif"));
        setIconImage(image);

        menu = new Menubar();
        toolbar = new Toolbar();
        //for setting the menu bar
        setJMenuBar(menu);
        //for adding the actionListener
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

        //get the graphical user interface components display the desktop
        Container cp = getContentPane();
        //desktop.setBackground(Color.GRAY);
        Color clr=new Color(153,153,255);
        desktop.setBackground(clr);
        cp.add("Center", desktop);
        //for setting the font
        searchLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        //for setting the font
        searchTextField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        goButton.setFont(new Font("Tahoma", Font.BOLD, 9));
        //for adding the searchLable to the searchToolBar
        searchToolBar.add(searchLabel);
        //for adding the searchTextField to searchToolBar
        searchToolBar.add(searchTextField);
        //for adding the goButton to searchToolBar
        searchToolBar.add(goButton);
        //for adding listenerAction for the button
        goButton.addActionListener(this);
        //for setting the layout
        searchPanel.setLayout(new BorderLayout());
        //for adding the toolBar to the searchPanel
        searchPanel.add("Center", toolbar);
        //for adding the searchToolBar to the searchPanel
        //searchPanel.add("South", searchToolBar);
        //for adding the searchPanel to the Container
        cp.add("North", searchPanel);
        //for adding the statusbar to the Container
        cp.add("South", statusbar);

        for (int i = 0; i < toolbar.imageName24.length; i++) {
            //for adding the action to the button
            toolbar.button[i].addActionListener(this);
        }

        //for adding WindowListener to the program
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //show the program
        setVisible(true);
    }
    public void actionPerformed(ActionEvent ae) {
    Object src = ae.getSource();

    if (src == menu.exit || src == toolbar.button[19]) {
        handleExit();
    } else if (src == menu.addBook || src == toolbar.button[0]) {
        openAddBook();
    } else if (src == menu.listBook || src == toolbar.button[1]) {
        openListBooks();
    } else if (src == menu.listAvailbleBook || src == toolbar.button[2]) {
        openListAvailable();
    } else if (src == menu.listBorrowedBook || src == toolbar.button[3]) {
        openListBorrowed();
    } else if (src == menu.editBook || src == toolbar.button[4]) {
        openEditBook();
    } else if (src == menu.removeBook || src == toolbar.button[5]) {
        openRemoveBook();
    } else if (src == menu.addMember || src == toolbar.button[6]) {
        openAddMember();
    } else if (src == menu.listMember || src == toolbar.button[7]) {
        openListMembers();
    } else if (src == menu.editMember || src == toolbar.button[8]) {
        openEditMember();
    } else if (src == menu.removeMember || src == toolbar.button[9]) {
        openRemoveMember();
    } else if (src == menu.searchBooksAndMembers || src == toolbar.button[10]) {
        openSearch();
    } else if (src == menu.borrowBook || src == toolbar.button[11]) {
        openBorrow();
    } else if (src == menu.returnBook || src == toolbar.button[12]) {
        openReturn();
    } else if (src == menu.listissuedbooks || src == toolbar.button[13]) {
        openIssued();
    } else if (src == menu.notepad || src == toolbar.button[14]) {
        openNotepad();
    } else if (src == menu.calculator || src == toolbar.button[15]) {
        openCalculator();
    } else if (src == menu.changePassword || src == toolbar.button[16]) {
        openChangePassword();
    } else if (src == menu.deleteLibrarian || src == toolbar.button[17]) {
        openDeleteLibrarian();
    } else if (src == menu.about || src == toolbar.button[18]) {
        openAbout();
    }
}
// ----------- WINDOW OPENING HELPERS -------------

private void runAsync(Runnable task) {
    Thread runner = new Thread(task);
    runner.start();
}

private void openAddBook() {
    runAsync(() -> {
        addBooks = new AddBooks();
        desktop.add(addBooks);
        try { addBooks.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openListBooks() {
    runAsync(() -> {
        listBooks = new ListBooks();
        desktop.add(listBooks);
        try { listBooks.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openListAvailable() {
    runAsync(() -> {
        listAvailble = new ListAvailbleBooks();
        desktop.add(listAvailble);
        try { listAvailble.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openListBorrowed() {
    runAsync(() -> {
        listBorrowed = new ListBorrowedBooks();
        desktop.add(listBorrowed);
        try { listBorrowed.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openEditBook() {
    runAsync(() -> {
        editBooks = new EditBooks();
        desktop.add(editBooks);
        try { editBooks.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openRemoveBook() {
    runAsync(() -> {
        removeBooks = new RemoveBooks();
        desktop.add(removeBooks);
        try { removeBooks.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openAddMember() {
    runAsync(() -> {
        addMembers = new AddMembers();
        desktop.add(addMembers);
        try { addMembers.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openListMembers() {
    runAsync(() -> {
        listMembers = new ListMembers();
        desktop.add(listMembers);
        try { listMembers.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openEditMember() {
    runAsync(() -> {
        editMembers = new EditMembers();
        desktop.add(editMembers);
        try { editMembers.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openRemoveMember() {
    runAsync(() -> {
        removeMembers = new RemoveMembers();
        desktop.add(removeMembers);
        try { removeMembers.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openSearch() {
    runAsync(() -> {
        search = new SearchBooksAndMembers();
        desktop.add(search);
        try { search.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openBorrow() {
    runAsync(() -> {
        borrowBooks = new BorrowBooks();
        desktop.add(borrowBooks);
        try { borrowBooks.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openReturn() {
    runAsync(() -> {
        returnBooks = new ReturnBooks();
        desktop.add(returnBooks);
        try { returnBooks.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openIssued() {
    runAsync(() -> {
        listIssued = new ListIssuedBooks();
        desktop.add(listIssued);
        try { listIssued.setSelected(true); } catch (Exception ignored) {}
    });
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
    runAsync(() -> {
        changePassword = new ChangePassword();
        desktop.add(changePassword);
        try { changePassword.setSelected(true); } catch (Exception ignored) {}
    });
}

private void openDeleteLibrarian() {
    deleteUser = new DeleteLibrarian();
}

private void openAbout() {
    runAsync(() -> {
        JOptionPane.showMessageDialog(null, new About(), "About Library Management System", JOptionPane.PLAIN_MESSAGE);
    });
}

private void handleExit() {
    dispose();
    System.exit(0);
}

}
