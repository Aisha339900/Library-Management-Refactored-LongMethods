import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;

public class ListBooks extends JInternalFrame {

    private JTable table;
    private ResultSetTableModel tableModel;

    private static final String JDBC_DRIVER  = "org.gjt.mm.mysql.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/Library";
    private static final String USER_NAME    = "root";
    private static final String PASSWORD     = "nielit";

    private static final String QUERY =
            "SELECT BookID, Subject, Title, Author, Publisher, Copyright,"
          + "Edition, Pages, NumberOfBooks, ISBN, Library, Availble, ShelfNo "
          + "FROM Books";

    public ListBooks() {
        super("Books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/List16.gif")));

        initModel();
        initUI();

        setVisible(true);
        pack();
    }

    /* ---------------------- MODEL ---------------------- */

    private void initModel() {
        try {
            tableModel = new ResultSetTableModel(JDBC_DRIVER, DATABASE_URL, USER_NAME, PASSWORD, QUERY);
            tableModel.setQuery(QUERY);
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Cannot load book data: " + ex.getMessage());
        }
    }

    /* ---------------------- UI ---------------------- */

    private void initUI() {
        Container cp = getContentPane();

        JLabel title = new JLabel("THE LIST FOR THE BOOKS");
        title.setFont(new Font("Tahoma", Font.BOLD, 14));

        JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        north.add(title);
        cp.add("North", north);

        table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(990, 220));
        table.setFont(new Font("Tahoma", Font.PLAIN, 12));
        setColumnWidths();

        JScrollPane scroll = new JScrollPane(table);

        JButton printBtn = new JButton(
                "Print Books",
                new ImageIcon(ClassLoader.getSystemResource("images/Print16.gif"))
        );
        printBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
        printBtn.addActionListener(e -> handlePrint());

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Books:"));
        center.add(printBtn, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        cp.add("Center", center);
    }

    private void setColumnWidths() {
        int[] widths = {30, 100, 150, 50, 70, 40, 40, 40, 80, 70, 30, 30, 30};
        for (int i = 0; i < widths.length; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
        }
    }

    /* ---------------------- PRINT ---------------------- */

    private void handlePrint() {
        new Thread(this::runPrint).start();
    }

    private void runPrint() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new PrintingBooks(QUERY));

            if (!job.printDialog()) return;

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            job.print();

        } catch (PrinterException ex) {
            System.out.println("Print error: " + ex.getMessage());

        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
}
