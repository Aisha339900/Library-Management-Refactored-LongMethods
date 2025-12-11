import java.awt.*;
public class Center {
	JLibrary l;
	public Center(JLibrary l) {
		this.l = l;
	}
	public void LibraryCenter() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		l.setLocation((screenSize.width - l.getWidth()) / 2, (screenSize.height - l.getHeight()) / 2);
	}
}
