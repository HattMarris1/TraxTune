package client;

import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

    public MainUI(Socket server, Document userDetails) {
        frame = new JFrame("ClientLoginUI");
        frame.setSize(1366,768);
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenSize.getWidth() - frame.getWidth()));
        int y = (int) ((screenSize.getHeight() - frame.getHeight()));
        frame.setLocation(0, 0);

        frame.setVisible(true);
        setUserNameLabel(((Document)userDetails.get("profile")).getString("name"));
        setDateRegisteredLabel(((Document)userDetails.get("profile")).getDate("registration"));
        setLastLoggedInLabel(((Document)userDetails.get("profile")).getDate("lastLogin"));
    }

    private void setUserNameLabel(String userName) {
        UserNameLabel.setText(userName);
    }

    private void setDateRegisteredLabel(Date dateRegistered) {

        DateRegisteredLabel.setText(dateRegistered.toString());
    }

    private void setLastLoggedInLabel(Date dateLastLoggedIn) {
        LastLoggedInLabel.setText(dateLastLoggedIn.toString());
    }

    private void setNoOfFriendsLabel(int noOfFriends) {
        NoOfFriendsLabel.setText(Integer.toString(noOfFriends));
    }
}
