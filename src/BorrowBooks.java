import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
public class BorrowBooks extends JInternalFrame {
	private final JPanel northPanel = new JPanel();
	private final JLabel title = new JLabel("BOOK INFORMATION");
	private final JPanel centerPanel = new JPanel();
	private final JPanel informationPanel = new JPanel();
	private final JLabel[] informationLabel = new JLabel[4];
	private final String[] informationString = {
			" Write the Book ID:",
			" Write the Member ID:",
			" The Current Date:",
			" The Return Date:"
	};
	private final JTextField[] informationTextField = new JTextField[2];
	private final DateButton current_date = new DateButton();
	private final DateButton return_date = new DateButton();
	private final JPanel borrowButtonPanel = new JPanel();
	private final JButton borrowButton = new JButton("Borrow");
	private final JPanel southPanel = new JPanel();
	private final JButton cancelButton = new JButton("Cancel");
	private Books book;
	private Members member;
	private Borrow borrow;
	private String[] data;
	public BorrowBooks() {
		super("Borrow Books", false, true, false, true);
		setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Export16.gif")));
		Container cp = getContentPane();
		configureDates();
		buildNorthPanel(cp);
		buildCenterPanel(cp);
		buildSouthPanel(cp);
		borrowButton.addActionListener(ae -> handleBorrowAction());
		cancelButton.addActionListener(ae -> dispose());
		setVisible(true);
		pack();
	}
	private void configureDates() {
		current_date.setForeground(Color.red);
		current_date.setEnabled(false);
		return_date.setForeground(Color.red);
	}
	private void buildNorthPanel(Container cp) {
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		title.setFont(new Font("Tahoma", Font.BOLD, 14));
		northPanel.add(title);
		cp.add("North", northPanel);
	}
	private void buildCenterPanel(Container cp) {
		centerPanel.setLayout(new BorderLayout());
		informationPanel.setLayout(new GridLayout(4, 2, 1, 1));
		for (int i = 0; i < informationLabel.length; i++) {
			informationPanel.add(informationLabel[i] = new JLabel(informationString[i]));
			informationLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));
			if (i == 2) {
				informationPanel.add(current_date);
			} else if (i == 3) {
				informationPanel.add(return_date);
			} else {
				informationPanel.add(informationTextField[i] = new JTextField());
				informationTextField[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
				informationTextField[i].addKeyListener(new DigitOnlyKeyListener());
			}
		}
		centerPanel.add("Center", informationPanel);
		borrowButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		borrowButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		borrowButtonPanel.add(borrowButton);
		centerPanel.add("South", borrowButtonPanel);
		centerPanel.setBorder(BorderFactory.createTitledBorder("Borrow a book:"));
		cp.add("Center", centerPanel);
	}
	private void buildSouthPanel(Container cp) {
		southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		cancelButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		southPanel.add(cancelButton);
		southPanel.setBorder(BorderFactory.createEtchedBorder());
		cp.add("South", southPanel);
	}
	private boolean isCorrect() {
		data = new String[4];
		for (int i = 0; i < informationLabel.length; i++) {
			if (i <= 1) {
				String value = informationTextField[i].getText();
				if (value.isEmpty()) return false;
				data[i] = value;
			} else if (i == 2) {
				String value = current_date.getText();
				if (value.isEmpty()) return false;
				data[i] = value;
			} else {
				String value = return_date.getText();
				if (value.isEmpty()) return false;
				data[i] = value;
			}
		}
		return true;
	}
	private boolean isReturnDateValid() {
		return new Date().before(return_date.getDate());
	}
	private boolean isBorrowerInfoValid() {
		Date today = new Date();
		book.connection("SELECT * FROM Books WHERE BookID = " + data[0]);
		member.connection("SELECT * FROM Members WHERE MemberID = " + data[1]);
		return isMemberNotExpired(today) && isBookAndMemberFound();
	}
	private boolean isMemberNotExpired(Date today) {
		Date expiry = member.getValidUpto();
		if (!today.before(expiry)) {
			showWarning("Member is Expired");
			return false;
		}
		return true;
	}
	private boolean isBookAndMemberFound() {
		if (book.getBookID() < 1 || member.getMemberID() < 1) {
			showWarning("Member ID or Book ID entered not found on database");
			return false;
		}
		return true;
	}
	private boolean isAlreadyBorrowedByMember() {
		borrow.connection("SELECT * FROM Borrow WHERE BookID=" + data[0] + " AND MemberID=" + data[1]);
		return borrow.getBookID() == Integer.parseInt(data[0]) && borrow.getMemberID() == Integer.parseInt(data[1]);
	}
	private void handleBorrowAction() {
		if (!isCorrect()) {
			showWarning("Please, complete the information");
			return;
		}
		new Thread(this::processBorrowRequest).start();
	}
	private void processBorrowRequest() {
		if (!validateBeforeBorrowing()) return;
		processBookBorrow();
	}
	private boolean validateBeforeBorrowing() {
		if (!isReturnDateValid()) return false;
		initializeObjects();
		if (isAlreadyBorrowedByMember()) {
			showWarning("The book is already borrowed by this member");
			clearTextField();
			return false;
		}
		return isBorrowerInfoValid();
	}
	private void initializeObjects() {
		book = new Books();
		member = new Members();
		borrow = new Borrow();
	}
	private void processBookBorrow() {
		int[] values = calculateBorrowValues();
		int available = values[0];
		int borrowed = values[1];
		int totalForMember = values[2];
		if (!isBookAvailable(available)) return;
		finalizeBorrow(available, borrowed, totalForMember);
	}
	private boolean isBookAvailable(int available) {
		if (available < 1) {
			showWarning("The book is borrowed");
			return false;
		}
		return true;
	}
	private int[] calculateBorrowValues() {
		int available = book.getNumberOfAvailbleBooks();
		int borrowed = 1 + book.getNumberOfBorrowedBooks();
		int totalForMember = 1 + member.getNumberOfBooks();
		return new int[]{available, borrowed, totalForMember};
	}
	private void finalizeBorrow(int available, int borrowed, int totalForMember) {
		updateBook(available - 1, borrowed);
		updateMember(totalForMember);
		insertBorrowRecord();
		dispose();
	}
	private void updateBook(int available, int borrowed) {
		String query = "UPDATE Books SET NumberOfAvailbleBooks=" + available + ", NumberOfBorrowedBooks=" + borrowed;
		if (available == 0) query += ", Availble=false ";
		query += " WHERE BookID=" + data[0];
		book.update(query);
	}
	private void updateMember(int totalBooks) {
		member.update("UPDATE Members SET NumberOfBooks=" + totalBooks + " WHERE MemberID=" + data[1]);
	}
	private void insertBorrowRecord() {
		borrow.update("INSERT INTO Borrow (BookID, MemberID, DayOfBorrowed, DayOfReturn) VALUES (" +
				data[0] + "," + data[1] + ",'" + data[2] + "','" + data[3] + "')");
	}
	private void showWarning(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	private void clearTextField() {
		for (JTextField field : informationTextField) field.setText(null);
	}
	private static class DigitOnlyKeyListener extends KeyAdapter {
		public void keyTyped(KeyEvent e) {
			char c = e.getKeyChar();
			if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_ENTER || c == KeyEvent.VK_DELETE)) {
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(null, "This Field Only Accept Integer Number", "WARNING", JOptionPane.DEFAULT_OPTION);
				e.consume();
			}
		}
	}
}
