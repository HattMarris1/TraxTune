package client;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * Created by Matthew on 24/04/2017.
 */
public class MainUI {

    private JFrame frame;
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JList list1;
    private JPanel AccountTab;
    private JPanel ChatTab;
    private JLabel UserNameLabel;
    private JLabel DateRegisteredLabel;
    private JLabel LastLoggedInLabel;
    private JLabel NoOfFriendsLabel;

    public MainUI() {
        frame = new JFrame("ClientLoginUI");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);

        frame.setVisible(true);
    }

    public void setUserNameLabel(String userName) {
        UserNameLabel.setText(userName);
    }

    public void setDateRegisteredLabel(Date dateRegistered) {
        DateRegisteredLabel.setText(dateRegistered.toString());
    }

    public void setLastLoggedInLabel(Date dateLastLoggedIn) {
        LastLoggedInLabel.setText(dateLastLoggedIn.toString());
    }

    public void setNoOfFriendsLabel(int noOfFriends) {
        NoOfFriendsLabel.setText(Integer.toString(noOfFriends));
    }
}
