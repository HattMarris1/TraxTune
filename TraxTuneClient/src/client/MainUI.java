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
import java.util.List;

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
    private JButton refreshButton;
    private JList user;

    public MainUI(Socket server, Document userDetails) {

        addFriendsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List pendList =pendingList.getSelectedValuesList();
                ArrayList<String> friendsToAdd = (ArrayList<String>) pendList;

                try {
                    Document usersToAdd = new Document("header", "addfriends")
                            .append("users", friendsToAdd);
                    ClientMain.sendDataToServer(usersToAdd);
                }
                catch (Exception e1){
                    System.out.println(e1);
                }
            }
        });
        sendFriendRequestsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List requestList = addFriendsList.getSelectedValuesList();
                ArrayList<String> friendsToAdd = (ArrayList<String>) requestList;

                try {
                    Document usersToAdd = new Document("header", "friendRequest")
                            .append("users", friendsToAdd);
                    ClientMain.sendDataToServer(usersToAdd);
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
        refreshAccount(userDetails);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Document r = new Document("header","getmyaccount");
                ClientMain.sendDataToServer(r);
            }
        });
        deleteFriendsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                List deleteList = currentFriendsList.getSelectedValuesList();
                ArrayList<String> friendsToDelete= (ArrayList<String>) deleteList;
                Document d = new Document("header","deletefriends")
                        .append("users",friendsToDelete);

                ClientMain.sendDataToServer(d);
            }
        });
    }

    private void setUserNameLabel(String userName) {
        UserNameLabel.setText(userName);
    }

    private void setDateRegisteredLabel(Date dateRegistered) {

        DateRegisteredLabel.setText(dateRegistered.toString());
    }

    private void setLastLoggedInLabel(Date dateLastLoggedIn) {
        if(dateLastLoggedIn!=null){
            LastLoggedInLabel.setText(dateLastLoggedIn.toString());
        }
        else {
            LastLoggedInLabel.setText((new Date().toString()));
        }

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
public void refreshAccount(Document userDetails){
    System.out.println(userDetails);
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
}
