import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
public class EditBooks extends JInternalFrame {
	private final JPanel northPanel = new JPanel();
	private final JLabel northLabel = new JLabel("BOOK INFORMATION");
	private final JPanel centerPanel = new JPanel();
	private final JPanel editPanel = new JPanel();
	private final JPanel editInformationPanel = new JPanel();
	private final JPanel editInformationLabelPanel = new JPanel();
	private final JPanel editInformationTextFieldPanel = new JPanel();
	private final JPanel editInformationButtonPanel = new JPanel();
	private final JLabel editLabel = new JLabel("BookID: ");
	private final JTextField editTextField = new JTextField(25);
	private final JButton editButton = new JButton("Edit");
	private final JPanel informationPanel = new JPanel();
	private final JPanel informationLabelPanel = new JPanel();
	private final JLabel[] informationLabel = new JLabel[10];
	private final String[] informationString = {
			" The book subject: ", " The book title: ",
			" The name of the Author(s): ", " The name of the Publisher: ",
			" Copyright year for the book: ", " The edition number: ", " The number of Pages: ",
			" ISBN for the book: ", " The number of copies: ", " The name of the Library: "
	};
	private final JPanel informationTextFieldPanel = new JPanel();
	private final JTextField[] informationTextField = new JTextField[10];
	private final JPanel updateInformationButtonPanel = new JPanel();
	private final JButton updateInformationButton = new JButton("Update the Information");
	private final JPanel southPanel = new JPanel();
	private final JButton exitButton = new JButton("Exit");
	private Books book;
	private String[] data;
	private boolean availble;
	private int numberofavailblebooks;
	public EditBooks() {
		super("Edit Books", false, true, false, true);
		setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Edit16.gif")));
		Container cp = getContentPane();
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		northLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		northPanel.add(northLabel);
		cp.add("North", northPanel);
		centerPanel.setLayout(new BorderLayout());
		buildEditSection();
		buildInformationSection();
		cp.add("Center", centerPanel);
		southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		exitButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		southPanel.add(exitButton);
		southPanel.setBorder(BorderFactory.createEtchedBorder());
		cp.add("South", southPanel);
		editButton.addActionListener(ae -> handleEditBook());
		updateInformationButton.addActionListener(ae -> handleUpdateBook());
		exitButton.addActionListener(e -> dispose());
		setVisible(true);
		pack();
	}
	private void buildEditSection() {
		editPanel.setLayout(new BorderLayout());
		editPanel.setBorder(BorderFactory.createTitledBorder("BookID: "));
		editInformationPanel.setLayout(new BorderLayout());
		editInformationLabelPanel.setLayout(new GridLayout(1, 1, 1, 1));
		editInformationLabelPanel.add(editLabel);
		editLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		editInformationPanel.add("West", editInformationLabelPanel);
		editInformationTextFieldPanel.setLayout(new GridLayout(1, 1, 1, 1));
		editTextField.setFont(new Font("Tahoma", Font.PLAIN, 11));
		editTextField.addKeyListener(new DigitOnlyKeyListener());
		editInformationTextFieldPanel.add(editTextField);
		editInformationPanel.add("East", editInformationTextFieldPanel);
		editPanel.add("North", editInformationPanel);
		editInformationButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		editButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		editInformationButtonPanel.add(editButton);
		editPanel.add("Center", editInformationButtonPanel);
		centerPanel.add("North", editPanel);
	}
	private void buildInformationSection() {
		informationPanel.setLayout(new BorderLayout());
		informationPanel.setBorder(BorderFactory.createTitledBorder("Edit a book: "));
		informationLabelPanel.setLayout(new GridLayout(informationLabel.length, 1, 1, 1));
		informationTextFieldPanel.setLayout(new GridLayout(informationTextField.length, 1, 1, 1));
		for (int i = 0; i < informationLabel.length; i++) {
			informationLabelPanel.add(informationLabel[i] = new JLabel(informationString[i]));
			informationLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));
			informationTextFieldPanel.add(informationTextField[i] = new JTextField(25));
			informationTextField[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
			if (i == 4 || i == 5 || i == 6 || i == 8) {
				informationTextField[i].addKeyListener(new DigitOnlyKeyListener());
			}
		}
		informationPanel.add("West", informationLabelPanel);
		informationPanel.add("East", informationTextFieldPanel);
		updateInformationButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		updateInformationButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		updateInformationButtonPanel.add(updateInformationButton);
		informationPanel.add("South", updateInformationButtonPanel);
		centerPanel.add("Center", informationPanel);
	}
	public boolean isCorrect() {
		data = new String[10];
		for (int i = 0; i < informationLabel.length; i++) {
			String value = informationTextField[i].getText();
			if (value.isEmpty()) return false;
			data[i] = value;
		}
		return true;
	}
	public boolean isEditCorrect() {
		return !editTextField.getText().isEmpty();
	}
	public void clearTextField() {
		editTextField.setText(null);
		for (JTextField field : informationTextField) {
			field.setText(null);
		}
	}
	private void handleEditBook() {
		if (!isEditCorrect()) {
			showWarning("Please, write the BookID");
			return;
		}
		new Thread(this::processEditBook).start();
	}
	private void processEditBook() {
		book = new Books();
		book.connection("SELECT * FROM Books WHERE BookID = " + editTextField.getText());
		if (!bookExists()) {
			showError("Please, write a correct BookID");
			editTextField.setText(null);
			clearTextField();
			return;
		}
		fillBookFields();
	}
	private boolean bookExists() {
		return book.getCopyright() > 0;
	}
	private void fillBookFields() {
		informationTextField[0].setText(book.getSubject());
		informationTextField[1].setText(book.getTitle());
		informationTextField[2].setText(book.getAuthor());
		informationTextField[3].setText(book.getPublisher());
		informationTextField[4].setText(book.getCopyright() + "");
		informationTextField[5].setText(book.getEdition() + "");
		informationTextField[6].setText(book.getPages() + "");
		informationTextField[7].setText(book.getISBN());
		informationTextField[8].setText(book.getNumberOfBooks() + "");
		informationTextField[9].setText(book.getLibrary());
	}
	private void handleUpdateBook() {
		if (!isCorrect()) {
			showWarning("Please, complete the information");
			return;
		}
		new Thread(this::processUpdateBook).start();
	}
	private void processUpdateBook() {
		book = new Books();
		book.connection("SELECT * FROM Books WHERE BookID = " + editTextField.getText());
		int borrowed = book.getNumberOfBorrowedBooks();
		int totalCopies = Integer.parseInt(data[8]);
		if (totalCopies < borrowed) {
			showWarning("Number of copies must be larger, as some books are borrowed");
			return;
		}
		updateBookAvailability(totalCopies, borrowed);
		updateBookRecord();
		dispose();
	}
	private void updateBookAvailability(int totalCopies, int borrowed) {
		if (totalCopies > borrowed) {
			availble = true;
			numberofavailblebooks = totalCopies - borrowed;
		} else {
			availble = false;
			numberofavailblebooks = 0;
		}
	}
	private void updateBookRecord() {
		String query = "UPDATE Books SET Subject = '" + data[0] + "',Title = '" + data[1] +
				"',Author = '" + data[2] + "',Publisher = '" + data[3] + "',Copyright =" + data[4] +
				",Edition =" + data[5] + ",Pages =" + data[6] + ",ISBN = '" + data[7] + "',NumberOfBooks =" +
				data[8] + ",NumberOfAvailbleBooks =" + numberofavailblebooks + ",Library = '" +
				data[9] + ",Availble =" + availble + " WHERE BookID =" + editTextField.getText();
		book.update(query);
	}
	private void showWarning(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	private void showError(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
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
