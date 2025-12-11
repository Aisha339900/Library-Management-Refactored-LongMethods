import javax.swing.*;
import java.awt.*;
public class Splash {
    public void showSplash(int duration) {
        JWindow splash = new JWindow();
        JPanel content = (JPanel) splash.getContentPane();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        splash.setBounds(0,0,screen.width,screen.height-30);
        JLabel label = new JLabel(new ImageIcon(ClassLoader.getSystemResource("images/splash.JPG")));
        JLabel copyrt = new JLabel("", JLabel.CENTER);
        copyrt.setFont(new Font("Tahoma", Font.BOLD, 10));
        content.setBackground(Color.BLACK);
        content.add(label, BorderLayout.CENTER);
        content.add(copyrt, BorderLayout.SOUTH);
        content.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        splash.setVisible(true);
        try {
            Thread.sleep(duration);
        } catch (Exception e) {
        }
        splash.setVisible(false);
    }
}
