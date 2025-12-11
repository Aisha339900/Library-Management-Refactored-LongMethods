import javax.swing.*;
import java.awt.*;

public final class DialogUtils {

    private DialogUtils() {
    }

    public static void warn(Component parent, String message) {
        show(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component parent, String message) {
        show(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component parent, String message) {
        show(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void show(Component parent, String message, String title, int type) {
        JOptionPane.showMessageDialog(parent, message, title, type);
    }
}
