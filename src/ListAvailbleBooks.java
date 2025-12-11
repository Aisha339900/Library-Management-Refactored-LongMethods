import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;

public class ListAvailbleBooks extends JInternalFrame {

    private JTable table;
    private ResultSetTableModel tableModel;

    private static final String JDBC_DRIVER   = "org.gjt.mm.mysql.Driver";
    private static final String DATABASE_URL  = "jdbc:mysql://localhost:3306/Library";
    private static final String USER_NAME     = "root";
    private static final String PASSWORD      = "nielit";
    private static final String QUERY =
            "SELECT BookID,Subject,Title,Author,Publisher,Copyright,Edition," +
                    "Pages,ISBN,Library,ShelfNo FROM Books WHERE Availble = true";

    public ListAvailbleBooks() {
        super("Available Books", false, true, false, true);
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
        } catch (ClassNotFoundException | SQLException ignored) {}
    }

    /* ---------------------- UI ---------------------- */

    private void initUI() {
        Container cp = getContentPane();

        JLabel label = new JLabel("THE LIST FOR THE AVAILABLE BOOKS");
        label.setFont(new Font("Tahoma", Font.BOLD, 14));

        JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        north.add(label);
        cp.add("North", north);

        table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(950, 220));
        table.setFont(new Font("Tahoma", Font.PLAIN, 12));
        setColumnWidths();

        JScrollPane scroll = new JScrollPane(table);

        JButton printButton = new JButton(
                "Print Available Books",
                new ImageIcon(ClassLoader.getSystemResource("images/Print16.gif"))
        );
        printButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        printButton.addActionListener(e -> handlePrint());

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Available Books:"));
        center.add(printButton, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        cp.add("Center", center);
    }

    private void setColumnWidths() {
        int[] widths = {20, 100, 150, 50, 70, 40, 40, 40, 75, 50, 30};
        for (int i = 0; i < widths.length; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
        }
    }

    /* ---------------------- PRINTING ---------------------- */

    private void handlePrint() {
        new Thread(this::runPrintJob).start();
    }

    private void runPrintJob() {
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
