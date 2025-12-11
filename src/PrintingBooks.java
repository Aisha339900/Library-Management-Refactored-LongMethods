public class PrintingBooks extends JInternalFrame implements Printable {
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultset = null;
    private String URL = "jdbc:mysql://localhost:3306/Library";
    private JTextArea textArea = new JTextArea();
    private Vector<String> lines;
    public static final int TAB_SIZE = 5;
    public PrintingBooks(String query) {
        super("Printing Books", false, true, false, true);
        Container cp = getContentPane();
        textArea.setFont(new Font("Tahoma", Font.PLAIN, 9));
        cp.add(textArea);
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        loadBookData(query);
        setVisible(true);
        pack();
    }
    private void loadBookData(String query) {
        try {
            connection = DriverManager.getConnection(URL, "root", "nielit");
            statement = connection.createStatement();
            resultset = statement.executeQuery(query);
            textArea.append("=============== Books Information ===============\n\n");
            while (resultset.next()) {
                textArea.append(formatBookEntry(resultset));
            }
            textArea.append("=============== Books Information ===============");
            resultset.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }
    private String formatBookEntry(ResultSet rs) throws SQLException {
        return "Subject: " + rs.getString("Subject") + "\n" +
               "Title: " + rs.getString("Title") + "\n" +
               "Author(s): " + rs.getString("Author") + "\n" +
               "Copyright: " + rs.getString("Copyright") + "\n" +
               "Edition: " + rs.getString("Edition") + "\n" +
               "ISBN: " + rs.getString("ISBN") + "\n" +
               "Library: " + rs.getString("Library") + "\n\n";
    }
    @Override
    public int print(Graphics pg, PageFormat pageFormat, int pageIndex) throws PrinterException {
        prepareGraphics(pg, pageFormat);
        FontMetrics fm = pg.getFontMetrics();
        int lineHeight = fm.getHeight();
        if (lines == null) {
            lines = getLines(fm, (int) pageFormat.getImageableWidth());
        }
        int numPages = calculatePageCount(lines.size(), lineHeight, (int) pageFormat.getImageableHeight());
        if (pageIndex >= numPages) {
            lines = null;
            return NO_SUCH_PAGE;
        }
        drawPage(pg, fm, pageIndex, lineHeight, (int) pageFormat.getImageableHeight());
        return PAGE_EXISTS;
    }
    private void prepareGraphics(Graphics pg, PageFormat pf) {
        pg.translate((int) pf.getImageableX(), (int) pf.getImageableY());
        int wPage = (int) pf.getImageableWidth();
        int hPage = (int) pf.getImageableHeight();
        pg.setClip(0, 0, wPage, hPage);
        pg.setColor(textArea.getBackground());
        pg.fillRect(0, 0, wPage, hPage);
        pg.setColor(textArea.getForeground());
        pg.setFont(textArea.getFont());
    }
    private int calculatePageCount(int totalLines, int lineHeight, int pageHeight) {
        int linesPerPage = Math.max(pageHeight / lineHeight, 1);
        return (int) Math.ceil((double) totalLines / linesPerPage);
    }
    private void drawPage(Graphics pg, FontMetrics fm, int pageIndex, int lineHeight, int pageHeight) {
        int linesPerPage = Math.max(pageHeight / lineHeight, 1);
        int y = fm.getAscent();
        int startLine = pageIndex * linesPerPage;
        int endLine = Math.min(lines.size(), startLine + linesPerPage);
        for (int i = startLine; i < endLine; i++) {
            pg.drawString(lines.get(i), 0, y);
            y += lineHeight;
        }
    }
    protected Vector<String> getLines(FontMetrics fm, int wPage) {
        Vector<String> v = new Vector<>();
        String text = textArea.getText();
        StringTokenizer st = new StringTokenizer(text, "\n\r", true);
        String prevToken = "";
        while (st.hasMoreTokens()) {
            String line = st.nextToken();
            if (line.equals("\r"))
                continue;
            if (line.equals("\n") && prevToken.equals("\n"))
                v.add("");
            prevToken = line;
            if (line.equals("\n"))
                continue;
            wrapLine(v, fm, wPage, line);
        }
        return v;
    }
    private void wrapLine(Vector<String> v, FontMetrics fm, int wPage, String line) {
        StringTokenizer st2 = new StringTokenizer(line, " \t", true);
        String line2 = "";
        while (st2.hasMoreTokens()) {
            String token = st2.nextToken();
            if (token.equals("\t")) {
                token = expandTab(line2);
            }
            if (fm.stringWidth(line2 + token) > wPage && line2.length() > 0) {
                v.add(line2);
                line2 = token.trim();
            } else {
                line2 += token;
            }
        }
        v.add(line2);
    }
    private String expandTab(String currentLine) {
        int numSpaces = TAB_SIZE - currentLine.length() % TAB_SIZE;
        return " ".repeat(Math.max(0, numSpaces));
    }
}
