import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.sql.*;
import java.util.StringTokenizer;
import java.util.Vector;

public class PrintingBooks extends JInternalFrame implements Printable {

    private static final String URL = "jdbc:mysql://localhost:3306/Library";
    private static final int TAB_SIZE = 5;

    private final JTextArea textArea = new JTextArea();
    private Vector<String> lines;

    public PrintingBooks(String query) {
        super("Printing Books", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/List16.gif")));

        textArea.setFont(new Font("Tahoma", Font.PLAIN, 9));
        add(new JScrollPane(textArea));

        loadData(query);

        setVisible(true);
        pack();
    }

    /* ---------------------- LOAD DATA ---------------------- */

    private void loadData(String query) {
        textArea.append("=============== Books Information ===============\n\n");

        try (Connection con = DriverManager.getConnection(URL, "root", "nielit");
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) textArea.append(formatEntry(rs));

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        textArea.append("=============== Books Information ===============");
    }

    private String formatEntry(ResultSet rs) throws SQLException {
        return "Subject: " + rs.getString("Subject") + "\n" +
               "Title: " + rs.getString("Title") + "\n" +
               "Author(s): " + rs.getString("Author") + "\n" +
               "Copyright: " + rs.getString("Copyright") + "\n" +
               "Edition: " + rs.getString("Edition") + "\n" +
               "ISBN: " + rs.getString("ISBN") + "\n" +
               "Library: " + rs.getString("Library") + "\n\n";
    }

    /* ---------------------- PRINT LOGIC ---------------------- */

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {

        prepareGraphics(g, pf);

        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();

        if (lines == null)
            lines = wrapLines(fm, (int) pf.getImageableWidth());

        int perPage = Math.max((int) pf.getImageableHeight() / lineHeight, 1);
        int totalPages = (int) Math.ceil((double) lines.size() / perPage);

        if (pageIndex >= totalPages) {
            lines = null;
            return NO_SUCH_PAGE;
        }

        int start = pageIndex * perPage;
        int end = Math.min(lines.size(), start + perPage);

        int y = fm.getAscent();
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

    /* ---------------------- WRAPPING LOGIC ---------------------- */

    private Vector<String> wrapLines(FontMetrics fm, int width) {
        Vector<String> v = new Vector<>();
        String text = textArea.getText();
        String prev = "";

        StringTokenizer st = new StringTokenizer(text, "\n\r", true);
        while (st.hasMoreTokens()) {
            String line = st.nextToken();

            if (line.equals("\r")) continue;
            if (line.equals("\n") && prev.equals("\n")) v.add("");
            prev = line;

            if (!line.equals("\n")) wrapLine(v, fm, width, line);
        }

        return v;
    }

    private void wrapLine(Vector<String> v, FontMetrics fm, int width, String line) {
        StringTokenizer st = new StringTokenizer(line, " \t", true);
        String current = "";

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.equals("\t")) token = expandTab(current);

            if (fm.stringWidth(current + token) > width && !current.isEmpty()) {
                v.add(current);
                current = token.trim();
            } else {
                current += token;
            }
        }

        v.add(current);
    }

    private String expandTab(String current) {
        int spaces = TAB_SIZE - (current.length() % TAB_SIZE);
        return " ".repeat(spaces);
    }
}
