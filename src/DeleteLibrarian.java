import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
public class DeleteLibrarian {
 int response;
 private Connection connection = null;
 private Statement statement = null;
 private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/Library";
 private static final String USER_NAME="root";
 private static final String PASSWORD="nielit";
 public DeleteLibrarian(){
     response = JOptionPane.showConfirmDialog(null,"Are you sure you want to permanently delete this Librarian?","Confirm",JOptionPane.YES_NO_OPTION,
         JOptionPane.QUESTION_MESSAGE);
 if(response == JOptionPane.YES_OPTION){
         try {
             Class.forName("org.gjt.mm.mysql.Driver");
             connection = DriverManager.getConnection(DATABASE_URL,USER_NAME,PASSWORD);
			statement = connection.createStatement();
			statement.executeUpdate("Delete from Login");
			statement.close();
			connection.close();
		}
                catch (SQLException SQLe) {
			System.out.println(SQLe.toString());
		}
		catch (ClassNotFoundException cnfe) {
			System.out.println(cnfe.toString());
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
            System.exit(0);
	}
 }
}
