import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;

public class ListMembers extends JInternalFrame {

    private JTable table;
    private ResultSetTableModel tableModel;

    private static final String JDBC_DRIVER  = "org.gjt.mm.mysql.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/Library";
    private static final String USER_NAME    = "root";
    private static final String PASSWORD     = "nielit";

    private static final String QUERY =
            "SELECT MemberID, RegNo, Name, EMail, Major, ValidUpto FROM Members";

    public ListMembers() {
        super("Members", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/List16.gif")));

        initModel();
        initUI();

        setVisible(true);
        pack();
    }

    /* -------------------- MODEL -------------------- */

    private void initModel() {
        try {
            tableModel = new ResultSetTableModel(JDBC_DRIVER, DATABASE_URL, USER_NAME, PASSWORD, QUERY);
            tableModel.setQuery(QUERY);
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading members: " + ex.getMessage());
        }
    }

    /* -------------------- UI -------------------- */

    private void initUI() {
        Container cp = getContentPane();

        JLabel title = new JLabel("THE LIST FOR THE MEMBERS");
        title.setFont(new Font("Tahoma", Font.BOLD, 14));

        JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        north.add(title);
        cp.add("North", north);

        table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(750, 220));
        table.setFont(new Font("Tahoma", Font.PLAIN, 12));
        setColumnWidths();

        JScrollPane scroll = new JScrollPane(table);

        JButton printBtn = new JButton(
                "Print Members", 
                new ImageIcon(ClassLoader.getSystemResource("images/Print16.gif"))
        );
        printBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
        printBtn.addActionListener(e -> handlePrint());

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Members:"));
        center.add(printBtn, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        cp.add("Center", center);
    }

    private void setColumnWidths() {
        int[] widths = {30, 20, 150, 120, 20, 60};
        for (int i = 0; i < widths.length; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
        }
    }

    /* -------------------- PRINT -------------------- */

    private void handlePrint() {
        new Thread(this::processPrint).start();
    }

    private void processPrint() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new PrintingMembers(QUERY));
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
