import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.sql.*;
import java.util.StringTokenizer;
import java.util.Vector;

public class PrintingBorrow extends JInternalFrame implements Printable {

    private static final String URL = "jdbc:mysql://localhost:3306/Library";
    private static final int TAB_SIZE = 10;

    private final JTextArea textArea = new JTextArea();
    private Vector<String> lines;

    public PrintingBorrow(String query) {
        super("Printing Borrowed Books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/List16.gif")));

        textArea.setFont(new Font("Tahoma", Font.PLAIN, 9));
        add(new JScrollPane(textArea));

        loadData(query);

        setVisible(true);
        pack();
    }

    /* --------------------- LOAD DATA --------------------- */

    private void loadData(String query) {
        textArea.append("=============== Borrowed Books Information ===============\n\n");

        try (Connection con = DriverManager.getConnection(URL, "root", "nielit");
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) textArea.append(formatEntry(rs));

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        textArea.append("=============== Borrowed Books Information ===============");
    }

    private String formatEntry(ResultSet rs) throws SQLException {
        return "Title: " + rs.getString("Title") + "\n" +
               "DayOfBorrowed: " + rs.getString("DayOfBorrowed") + "\n" +
               "DayOfReturn: " + rs.getString("DayOfReturn") + "\n" +
               "Name: " + rs.getString("Name") + "\n" +
               "Email: " + rs.getString("Email") + "\n\n";
    }

    /* --------------------- PRINT LOGIC --------------------- */

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {

        prepareGraphics(g, pf);

        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();

        if (lines == null)
            lines = wrapLines(fm, (int) pf.getImageableWidth());

        int perPage = Math.max(((int) pf.getImageableHeight()) / lineHeight, 1);
        int totalPages = (int) Math.ceil((double) lines.size() / perPage);

        if (pageIndex >= totalPages) {
            lines = null;
            return NO_SUCH_PAGE;
        }

        int y = fm.getAscent();
        int start = pageIndex * perPage;
        int end = Math.min(lines.size(), start + perPage);

        for (int i = start; i < end; i++) {
            g.drawString(lines.get(i), 0, y);
            y += lineHeight;
        }

        return PAGE_EXISTS;
    }

    private void prepareGraphics(Graphics g, PageFormat pf) {
        g.translate((int) pf.getImageableX(), (int) pf.getImageableY());
        g.setClip(0, 0, (int) pf.getImageableWidth(), (int) pf.getImageableHeight());
        g.setColor(Color.BLACK);
        g.setFont(textArea.getFont());
    }

    /* --------------------- LINE WRAPPING --------------------- */

    private Vector<String> wrapLines(FontMetrics fm, int width) {
        Vector<String> list = new Vector<>();
        String text = textArea.getText();
        String prev = "";

        StringTokenizer st = new StringTokenizer(text, "\n\r", true);
        while (st.hasMoreTokens()) {
            String line = st.nextToken();

            if (line.equals("\r")) continue;
            if (line.equals("\n") && prev.equals("\n")) list.add("");
            prev = line;

            if (line.equals("\n")) continue;

            wrapLine(list, fm, width, line);
        }

        return list;
    }

    private void wrapLine(Vector<String> list, FontMetrics fm, int width, String line) {
        StringTokenizer st = new StringTokenizer(line, " \t", true);
        String current = "";

        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (tok.equals("\t")) tok = expandTab(current);

            if (fm.stringWidth(current + tok) > width && !current.isEmpty()) {
                list.add(current);
                current = tok.trim();
            } else {
                current += tok;
            }
        }

        list.add(current);
    }

    private String expandTab(String current) {
        int spaces = TAB_SIZE - (current.length() % TAB_SIZE);
        return " ".repeat(spaces);
    }
}
