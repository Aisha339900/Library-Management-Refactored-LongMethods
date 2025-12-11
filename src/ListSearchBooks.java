import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;

public class ListSearchBooks extends JInternalFrame {

    private JTable table;
    private ResultSetTableModel tableModel;
    private final String QUERY;

    private static final String JDBC_DRIVER = "org.gjt.mm.mysql.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/Library";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "nielit";

    public ListSearchBooks(String query) {
        super("Searched Books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/List16.gif")));

        this.QUERY = query;

        initModel();
        initUI();

        setVisible(true);
        pack();
    }

    /* ------------------------- MODEL ------------------------- */

    private void initModel() {
        try {
            tableModel = new ResultSetTableModel(JDBC_DRIVER, DATABASE_URL, USER_NAME, PASSWORD, QUERY);
            tableModel.setQuery(QUERY);
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Cannot load search results: " + ex.getMessage());
        }
    }

    /* --------------------------- UI --------------------------- */

    private void initUI() {

        // NORTH TITLE
        JLabel title = new JLabel("THE LIST FOR THE SEARCHED BOOKS");
        title.setFont(new Font("Tahoma", Font.BOLD, 14));
        JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        north.add(title);
        add("North", north);

        // TABLE
        table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(990, 220));
        table.setFont(new Font("Tahoma", Font.PLAIN, 12));
        setColumnWidths();

        JScrollPane scroll = new JScrollPane(table);

        // PRINT BUTTON
        JButton printBtn = new JButton(
                "Print Search Results",
                new ImageIcon(ClassLoader.getSystemResource("images/Print16.gif"))
        );
        printBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
        printBtn.addActionListener(e -> handlePrint());

        // CENTER PANEL
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Books:"));
        center.add(printBtn, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        add("Center", center);
    }

    private void setColumnWidths() {
        int[] widths = {20, 100, 150, 50, 70, 40, 40, 40, 80, 70, 30, 30, 30};
        for (int i = 0; i < widths.length; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
        }
    }

    /* ------------------------ PRINTING ------------------------ */

    private void handlePrint() {
        new Thread(this::processPrint).start();
    }

    private void processPrint() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new PrintingBooks(QUERY));

            if (!job.printDialog()) return;

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            job.print();

        } catch (PrinterException ex) {
            System.out.println("Printing error: " + ex.getMessage());

        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
}
