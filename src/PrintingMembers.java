public class PrintingMembers extends JInternalFrame implements Printable {

    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultset = null;

    private String URL = "jdbc:mysql://localhost:3306/Library";

    private JTextArea textArea = new JTextArea();
    private Vector<String> lines;
    public static final int TAB_SIZE = 10;

    public PrintingMembers(String query) {
        super("Printing Members", false, true, false, true);

        Container cp = getContentPane();
        textArea.setFont(new Font("Tahoma", Font.PLAIN, 9));
        cp.add(textArea);

        try {
            Class.forName("org.gjt.mm.mysql.Driver");
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        loadMemberData(query);
        setVisible(true);
        pack();
    }

    // -------------------- DATABASE LOAD (unchanged) --------------------

    private void loadMemberData(String query) {
        try {
            connection = DriverManager.getConnection(URL, "root", "nielit");
            statement = connection.createStatement();
            resultset = statement.executeQuery(query);

            textArea.append("=============== Members Information ===============\n\n");

            while (resultset.next()) {
                textArea.append(formatMemberEntry(resultset));
            }

            textArea.append("=============== Members Information ===============");

            resultset.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    private String formatMemberEntry(ResultSet rs) throws SQLException {
        return "Member ID: " + rs.getString("MemberID") + "\n" +
               "Name: " + rs.getString("Name") + "\n" +
               "Major: " + rs.getString("Major") + "\n" +
               "Valid Upto: " + rs.getString("ValidUpto") + "\n\n";
    }

    // -------------------- REFACTORED PRINT METHOD --------------------

    @Override
    public int print(Graphics pg, PageFormat pf, int pageIndex) throws PrinterException {

        prepareGraphics(pg, pf);

        FontMetrics fm = pg.getFontMetrics();
        int lineHeight = fm.getHeight();

        if (lines == null) {
            lines = getLines(fm, (int) pf.getImageableWidth());
        }

        int pageCount = calculatePageCount(lines.size(), lineHeight, (int) pf.getImageableHeight());
        if (pageIndex >= pageCount) {
            lines = null;
            return NO_SUCH_PAGE;
        }

        drawPage(pg, fm, pageIndex, lineHeight, (int) pf.getImageableHeight());

        return PAGE_EXISTS;
    }

    // -------------------- HELPER METHODS (reduce complexity) --------------------

    private void prepareGraphics(Graphics pg, PageFormat pf) {
        pg.translate((int) pf.getImageableX(), (int) pf.getImageableY());
        int w = (int) pf.getImageableWidth();
        int h = (int) pf.getImageableHeight();

        pg.setClip(0, 0, w, h);
        pg.setColor(textArea.getBackground());
        pg.fillRect(0, 0, w, h);

        pg.setColor(textArea.getForeground());
        pg.setFont(textArea.getFont());
    }

    private int calculatePageCount(int totalLines, int lineHeight, int pageHeight) {
        int perPage = Math.max(pageHeight / lineHeight, 1);
        return (int) Math.ceil((double) totalLines / perPage);
    }

    private void drawPage(Graphics pg, FontMetrics fm, int pageIndex, int lineHeight, int pageHeight) {

        int perPage = Math.max(pageHeight / lineHeight, 1);
        int start = pageIndex * perPage;
        int end = Math.min(lines.size(), start + perPage);

        int y = fm.getAscent();

        for (int i = start; i < end; i++) {
            pg.drawString(lines.get(i), 0, y);
            y += lineHeight;
        }
    }

    // -------------------- TEXT WRAPPING LOGIC (unchanged) --------------------

    protected Vector<String> getLines(FontMetrics fm, int wPage) {
        Vector<String> v = new Vector<>();

        String text = textArea.getText();
        String prev = "";
        StringTokenizer st = new StringTokenizer(text, "\n\r", true);

        while (st.hasMoreTokens()) {
            String line = st.nextToken();

            if (line.equals("\r"))
                continue;

            if (line.equals("\n") && prev.equals("\n"))
                v.add("");

            prev = line;

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
        int spaces = TAB_SIZE - currentLine.length() % TAB_SIZE;
        return " ".repeat(Math.max(0, spaces));
    }
}
