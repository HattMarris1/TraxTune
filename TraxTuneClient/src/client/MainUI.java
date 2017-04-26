package client;

import org.bson.Document;
import sun.applet.Main;

import javax.print.Doc;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private JTextArea ChatTextArea;
    private JButton createChatWithAboveButton;
    private JTextField ChatNameField;
    private JList FriendsList;
    private JList ChatList;
    private JButton switchChatButton;
    private JTextField messageField;
    private JButton sendButton;
    private JList user;
    private String currentChat="";

    public MainUI(Socket server, Document userDetails) {
        addFriendsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //sends a request to the server to add users to this users friends
                List pendList =pendingList.getSelectedValuesList();
                if(!pendList.isEmpty()){
                    ArrayList<String> friendsToAdd = (ArrayList<String>) pendList;
                    Document usersToAdd = new Document("header", "addfriends")
                                .append("users", friendsToAdd);
                    ClientMain.sendDataToServer(usersToAdd);
                }
            }
        });
        sendFriendRequestsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //send a request to put the users name in the other users pending friend requests
                List requestList = addFriendsList.getSelectedValuesList();
                if(!requestList.isEmpty()){
                    ArrayList<String> friendsToAdd = (ArrayList<String>) requestList;
                    Document usersToAdd = new Document("header", "friendRequest")
                            .append("users", friendsToAdd);
                    ClientMain.sendDataToServer(usersToAdd);
                }
            }
        }
        );
        //initialises the frame
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
                //gets the most up to date version of the users account
                Document r = new Document("header","getmyaccount");
                ClientMain.sendDataToServer(r);
            }
        });
        deleteFriendsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //removes users from this users friends list
                List deleteList = currentFriendsList.getSelectedValuesList();
                if(!deleteList.isEmpty()){
                    ArrayList<String> friendsToDelete= (ArrayList<String>) deleteList;
                    Document d = new Document("header","deletefriends")
                            .append("users",friendsToDelete);

                    ClientMain.sendDataToServer(d);
                }

            }
        });
        createChatWithAboveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //creates a new group chat with the users in the listview
                List usersToadd = FriendsList.getSelectedValuesList();
                if (!usersToadd.isEmpty()){
                    ArrayList<String> addUser = (ArrayList<String >) usersToadd;
                    Document d = new Document("header","createchat")
                            .append("users",addUser)
                            .append("chatname",ChatNameField.getText());
                    ClientMain.sendDataToServer(d);
                }

            }
        });
        switchChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get the desired chat from the server
                String chat = (String)ChatList.getSelectedValue();
                Document d = new Document("header","getmessages")
                        .append("chatname",chat);
                currentChat = chat;
                ClientMain.sendDataToServer(d);
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //sends the users chat message to the server, and the chat its for
                String message = messageField.getText();
                if(currentChat != ""&&message!="")
                {
                Document d = new Document("header","sendmessage")
                        .append("message",message)
                        .append("chat",currentChat);
                ClientMain.sendDataToServer(d);
                }
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //sends a logout request when the user closes the window
                super.windowClosing(e);
                ClientMain.sendDataToServer(new Document("header","logout"));
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
    private void getGroupChats(){
        Document userInfoDoc = new Document("header","getmychats");
        ClientMain.sendDataToServer(userInfoDoc);
    }
public void setPeopleYouMayKnow( ArrayList<String> userArray) {
    String[] ud = new String[userArray.size()];
    Object usersObj[] = userArray.toArray(ud);
    addFriendsList.setListData(usersObj);
}
public void setChatMemberListView(ArrayList<String> chatList){
        if(!chatList.isEmpty()){
            String[] chats = new String[chatList.size()];
            Object chatObj[] = chatList.toArray(chats);
            ChatList.setListData(chatObj);
        }
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
        FriendsList.setListData(friendObj);
    }
    ArrayList<String> pendingRequests = (ArrayList<String>)userDetails.get("requests");

    if(pendingRequests!= null){
        String[] pr=new String[pendingRequests.size()];
        Object requestObj[] =  pendingRequests.toArray(pr);
        pendingList.setListData(requestObj);
    }
    getListOfUsers();
    getGroupChats();
}

public void updateChat(Document chatData){
        ArrayList<Document> messages;
       Object o = chatData.get("messages");
       if(o!=null){
           messages = (ArrayList<Document>) chatData.get("messages");
           Iterator<Document> documentIterator = messages.iterator();
           ChatTextArea.setText("");
           while (documentIterator.hasNext()){
               Document temp = documentIterator.next();
               ChatTextArea.append("\n<from= "+temp.getString("from")+"Date :"/*+(temp.getDate("date").toString())*/+"> ");
               ChatTextArea.append(temp.getString("content"));
           }
       }

}

}
