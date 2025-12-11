import javax.swing.*;
import java.awt.*;

public class RemoveMembers extends JInternalFrame {

    private final JTextField memberIdField = new JTextField();
    private final JButton removeButton = new JButton("Remove");
    private final JButton exitButton = new JButton("Exit");

    private Members member;
    private String memberId;

    public RemoveMembers() {
        super("Remove Members", false, true, false, true);
        setFrameIcon(new ImageIcon(ClassLoader.getSystemResource("images/Delete16.gif")));

        buildNorth();
        buildCenter();
        buildSouth();

        removeButton.addActionListener(e -> handleRemove());
        exitButton.addActionListener(e -> dispose());

        setVisible(true);
        pack();
    }

    /* -------------------- UI -------------------- */

    private void buildNorth() {
        JLabel title = new JLabel("MEMBER INFORMATION");
        title.setFont(new Font("Tahoma", Font.BOLD, 14));
        JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        north.add(title);
        add("North", north);
    }

    private void buildCenter() {
        JLabel lbl = new JLabel(" Write the Member ID: ");
        lbl.setFont(new Font("Tahoma", Font.BOLD, 11));

        memberIdField.setFont(new Font("Tahoma", Font.PLAIN, 11));
        memberIdField.addKeyListener(new DigitOnlyKeyListener());

        JPanel input = new JPanel(new GridLayout(1, 2, 5, 5));
        input.add(lbl);
        input.add(memberIdField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        removeButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnPanel.add(removeButton);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Remove a member:"));
        center.add(input, BorderLayout.CENTER);
        center.add(btnPanel, BorderLayout.SOUTH);

        add("Center", center);
    }

    private void buildSouth() {
        exitButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(exitButton);
        add("South", south);
    }

    /* -------------------- LOGIC -------------------- */

    private boolean isValidInput() {
        String text = memberIdField.getText().trim();
        if (text.isEmpty()) return false;
        memberId = text;
        return true;
    }

    private void handleRemove() {
        if (!isValidInput()) {
            DialogUtils.warn(this, "Please enter the Member ID.");
            return;
        }
        new Thread(this::processRemoval).start();
    }

    private void processRemoval() {
        member = new Members();
        member.connection("SELECT * FROM Members WHERE MemberID = " + memberId);

        int id = member.getMemberID();
        if (id < 1) {
            DialogUtils.error(this, "The MemberID is incorrect!");
            clearField();
            return;
        }

        int borrowed = member.getNumberOfBooks();
        if (borrowed > 0) {
            DialogUtils.warn(this, "Member has borrowed book(s). Cannot delete.");
            clearField();
            return;
        }

        deleteMember();
        dispose();
    }

    private void deleteMember() {
        member.update("DELETE FROM Members WHERE MemberID = " + memberId);
    }

    /* -------------------- HELPERS -------------------- */

    private void clearField() {
        memberIdField.setText("");
    }
}
