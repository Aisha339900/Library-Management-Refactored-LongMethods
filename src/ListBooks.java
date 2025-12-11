import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;
public class ListBooks extends JInternalFrame {
    private JPanel northPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private JLabel northLabel = new JLabel("THE LIST FOR THE BOOKS");
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
            "SELECT BookID, Subject, Title, Author, Publisher, Copyright, " +
            "Edition, Pages, NumberOfBooks, ISBN, Library, Availble, ShelfNo FROM Books";
    public ListBooks() {
        super("Books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/List16.gif")));
        initDatabase();
        initTable();
        initNorthPanel();
        initCenterPanel();
        initPrintButton();
        setVisible(true);
        pack();
    }
    private void initDatabase() {
        try {
            tableModel = new ResultSetTableModel(
                    JDBC_DRIVER, DATABASE_URL, USER_NAME, PASSWORD, DEFAULT_QUERY
            );
            tableModel.setQuery(DEFAULT_QUERY);
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.toString());
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
        northLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        northPanel.add(northLabel);
    }
    private void initCenterPanel() {
        Container cp = getContentPane();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Books:"));
        cp.add("North", northPanel);
        cp.add("Center", centerPanel);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
    }
    private void initPrintButton() {
        ImageIcon printIcon = new ImageIcon(ClassLoader.getSystemResource("images/Print16.gif"));
        printButton = new JButton("print the books", printIcon);
        printButton.setToolTipText("Print");
        printButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        centerPanel.add(printButton, BorderLayout.NORTH);
        printButton.addActionListener(e -> handlePrint());
    }
    private void setupColumnWidths() {
        for (int i = 0; i < 13; i++) {
            column = table.getColumnModel().getColumn(i);
            switch (i) {
                case 0 -> column.setPreferredWidth(30);
                case 1 -> column.setPreferredWidth(100);
                case 2 -> column.setPreferredWidth(150);
                case 3 -> column.setPreferredWidth(50);
                case 4 -> column.setPreferredWidth(70);
                case 5, 6, 7 -> column.setPreferredWidth(40);
                case 8 -> column.setPreferredWidth(80);
                case 9 -> column.setPreferredWidth(70);
                case 10, 11, 12 -> column.setPreferredWidth(30);
            }
        }
    }
    private void handlePrint() {
        Thread runner = new Thread(this::processPrintJob);
        runner.start();
    }
    private void processPrintJob() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new PrintingBooks(DEFAULT_QUERY));
            if (!job.printDialog()) return;
            setWaitingCursor(true);
            job.print();
        } catch (PrinterException ex) {
            System.out.println("Printing error: " + ex.toString());
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
