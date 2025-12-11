import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.BooleanSupplier;

public class DigitOnlyKeyListener extends KeyAdapter {

    private final BooleanSupplier guard;
    private final String message;

    public DigitOnlyKeyListener() {
        this(() -> true, "This field accepts digits only");
    }

    public DigitOnlyKeyListener(BooleanSupplier guard) {
        this(guard, "This field accepts digits only");
    }

    public DigitOnlyKeyListener(BooleanSupplier guard, String message) {
        this.guard = guard == null ? () -> true : guard;
        this.message = message;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!guard.getAsBoolean()) return;

        char c = e.getKeyChar();
        if (!Character.isDigit(c) &&
                c != KeyEvent.VK_BACK_SPACE &&
                c != KeyEvent.VK_DELETE &&
                c != KeyEvent.VK_ENTER) {
            Toolkit.getDefaultToolkit().beep();
            DialogUtils.warn(null, message);
            e.consume();
        }
    }
}
