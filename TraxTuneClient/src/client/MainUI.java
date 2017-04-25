package client;

import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

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
    private JList pendingList;
    private JList currentFriendsList;
    private JList addFriendsList;
    private JButton addFriendsButton;
    private JButton deleteFriendsButton;
    private JButton sendFriendRequestsButton;
    private JList user;

    public MainUI(Socket server, Document userDetails) {

        addFriendsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> friendsToAdd = (ArrayList<String>) addFriendsList.getSelectedValuesList();

                try {
                    Document usersToAdd = new Document("header", "addfriends")
                            .append("users", friendsToAdd);

                }
                catch (Exception e1){
                    System.out.println(e1);
                }
            }
        }
        );

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
        setUserNameLabel(userDetails.getString("name"));
        setDateRegisteredLabel(userDetails.getDate("registration"));
        setLastLoggedInLabel(userDetails.getDate("lastLogin"));

        ArrayList<String> friends = (ArrayList<String>)userDetails.get("friends");
        if(friends!=null){
            String[] f=new String[friends.size()];
            Object friendObj[] =  friends.toArray(f);
            currentFriendsList.setListData(friendObj);
        }
        ArrayList<String> pendingRequests = (ArrayList<String>)userDetails.get("requests");

        if(pendingRequests!= null){
            String[] pr=new String[pendingRequests.size()];
            Object requestObj[] =  pendingRequests.toArray(pr);
            pendingList.setListData(requestObj);
        }

        getListOfUsers();


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

    private void getListOfUsers(){
        Document userInfoDoc = new Document("header","getallusers");

        ClientMain.sendDataToServer(userInfoDoc);
    }
public void setPeopleYouMayKnow( ArrayList<String> userArray){
        String[] ud = new String[userArray.size()];
        Object usersObj[] = userArray.toArray(ud);
        addFriendsList.setListData(usersObj);
    }

}
