import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;

public class ListIssuedBooks extends JInternalFrame {

    private JPanel northPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private JLabel label = new JLabel("THE LIST FOR THE BORROWED BOOKS");

    private JButton printButton;
    private JTable table;
    private JScrollPane scrollPane;
    private TableColumn column;

    private ResultSetTableModel tableModel;

    private static final String JDBC_DRIVER = "org.gjt.mm.mysql.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/Library";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "nielit";

    private static final String DEFAULT_QUERY =
            "SELECT B.BookID, BK.Title, B.MemberID, B.DayOfBorrowed, B.DayOfReturn, " +
            "M.RegNo, M.Name, M.Email " +
            "FROM Borrow AS B, Books AS BK, Members AS M " +
            "WHERE (B.BookID = BK.BookID) AND (B.MemberID = M.MemberID)";


    // ---------------------- Constructor ----------------------
    public ListIssuedBooks() {
        super("Borrowed Books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/List16.gif")));

        initDatabase();
        initTable();
        initNorthPanel();
        initCenterPanel();
        initPrintButton();

        setVisible(true);
        pack();
    }


    // ---------------------- Setup Methods ----------------------

    private void initDatabase() {
        try {
            tableModel = new ResultSetTableModel(
                    JDBC_DRIVER, DATABASE_URL, USER_NAME, PASSWORD, DEFAULT_QUERY
            );
            tableModel.setQuery(DEFAULT_QUERY);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Cannot retrieve data from tables, " + e.getMessage());
        }
    }

    private void initTable() {
        table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(990, 200));
        table.setFont(new Font("Tahoma", Font.PLAIN, 12));
        scrollPane = new JScrollPane(table);
        setupColumnWidths();
    }

    private void initNorthPanel() {
        label.setFont(new Font("Tahoma", Font.BOLD, 14));
        northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        northPanel.add(label);
    }

    private void initCenterPanel() {
        Container cp = getContentPane();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Borrowed books:"));
        
        cp.add("North", northPanel);
        cp.add("Center", centerPanel);

        centerPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void initPrintButton() {
        ImageIcon printIcon = new ImageIcon(ClassLoader.getSystemResource("images/Print16.gif"));
        printButton = new JButton("print the books", printIcon);
        printButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        printButton.setToolTipText("Print");

        centerPanel.add(printButton, BorderLayout.NORTH);
        printButton.addActionListener(e -> handlePrint());
    }


    private void setupColumnWidths() {
        for (int i = 0; i < 8; i++) {
            column = table.getColumnModel().getColumn(i);

            switch (i) {
                case 0 -> column.setPreferredWidth(15);  // BookID
                case 1 -> column.setPreferredWidth(100); // Title
                case 2 -> column.setPreferredWidth(15);  // MemberID
                case 3, 4 -> column.setPreferredWidth(30); // Borrow + Return dates
                case 5 -> column.setPreferredWidth(10);  // RegNo
                case 6 -> column.setPreferredWidth(80);  // Name
                case 7 -> column.setPreferredWidth(100); // Email
            }
        }
    }


    // ---------------------- PRINT LOGIC ----------------------

    private void handlePrint() {
        Thread runner = new Thread(this::processPrintJob);
        runner.start();
    }

    private void processPrintJob() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new PrintingBorrow(DEFAULT_QUERY));

            if (!job.printDialog()) return;

            setWaitingCursor(true);
            job.print();
        } catch (PrinterException ex) {
            System.out.println("Printing error: " + ex);
        } finally {
            setWaitingCursor(false);
        }
    }

    private void setWaitingCursor(boolean waiting) {
        Cursor cursor = waiting ?
                Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) :
                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        setCursor(cursor);
    }
}
