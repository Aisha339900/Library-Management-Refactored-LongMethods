import javax.swing.*;
public class Toolbar extends JToolBar {
	public JButton[] button;
	public String[] imageName24 = {"images/Add24.gif", "images/List24.gif",
	                               "images/List24.gif", "images/List24.gif",
	                               "images/Edit24.gif", "images/Delete24.gif",
	                               "images/Add24.gif",  "images/List24.gif",
                                   "images/Edit24.gif","images/Delete24.gif",
	                               "images/Search24.jpg", "images/Export24.gif",
	                               "images/Import24.gif","images/List24.gif",
                                   "images/Notepad24.png","images/Calculator24.png",
                                   "images/EditLibrarian24.gif","images/DeleteLibrarian24.gif",
                                   "images/About24.gif","images/Exit24.gif"};
	public String[] tipText = {"Add Books", "List All Books", "List Available Books",
	                           "List Borrowed Books", "Edit Books", "Remove Books",
	                           "Add Members", "List Members", "Edit Members", "Remove Members",
	                           "Search", "Borrow Books", "Return Books","Issued Book Details",
                                   "Notepad", "Calculator","Change Password","Delete Librarian", "About", "Exit"};
	public Toolbar() {
		button = new JButton[20];
		for (int i = 0; i < imageName24.length; i++) {
			if (i == 6 || i == 10 || i == 11 || i == 14 || i==16)
				addSeparator();
			add(button[i] = new JButton(new ImageIcon(ClassLoader.getSystemResource(imageName24[i]))));
			button[i].setToolTipText(tipText[i]);
		}
	}
}
