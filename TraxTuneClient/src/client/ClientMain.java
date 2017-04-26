package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.bson.Document;

/**
 * Created by Matthew on 19/04/2017.
 */
public class ClientMain implements Runnable{
    private static InetAddress address;
    private static Socket server;
    private static ClientLoginUI LoginScreen;
    private static MainUI mainScreen;
    private static ObjectInputStream inFromServer = null;
    private static ObjectOutputStream outToServer = null;

    private String userName;
    private String id;

    public static void main(String args[]){

        try{
            //address and server for the server
            address = InetAddress.getByName("localhost");
            server  = new Socket(address,7777);

        }
        catch (java.io.IOException e){
            System.err.println(e);
        }
        //start thread to listen for communication from the server
        new Thread(new ClientMain()).start();
        LoginScreen = new ClientLoginUI();
    }


    public void run(){
        try {
            //sets the input and output streams for the server
            outToServer = new ObjectOutputStream(server.getOutputStream());
            inFromServer = new ObjectInputStream(server.getInputStream());
        }
        catch(IOException er){
            er.printStackTrace();
        }
        while (true) try {
            System.out.println("listening for server data...");
            //gets the object from the inputStream
            Object objResponse = inFromServer.readObject();
            Document response = (Document) objResponse;
            System.out.println(response);
            //checks the header element in the BSON document the server sent to determine the type of data
            if (Objects.equals(response.getString("header"), "loginsuccess")) {
                //login successfully
                System.out.println("successfully logged in");
                userName = response.getString("name");
                id = response.getString("_id");
                Document userData = response.get("profile",Document.class);
                LoginScreen.close();
                mainScreen = new MainUI(server, userData);

            }
            else if (Objects.equals(response.getString("header"), "alluserdata")){
                //all the other usernames from the server
                ArrayList<String> userArray;
                userArray = (ArrayList<String>)response.get("users");
                mainScreen.setPeopleYouMayKnow(userArray);
                System.out.println(response.get("users"));
            }
            else if (Objects.equals(response.getString("header"), "refreshaccount")){
                //updates the local userdata with that from the server
                Document userDetails;
                userDetails = (Document) response.get("userdetails");
                mainScreen.refreshAccount(userDetails);
            }
            else if (Objects.equals(response.getString("header"), "mychats")){
                //returns all the chats the user is part of
                ArrayList<String> userList;
                userList = (ArrayList<String>) response.get("chatlist");
                mainScreen.setChatMemberListView(userList);
            }
            else if (Objects.equals(response.getString("header"), "messages")){
                //returns the chat data for a chat
                Document messages= (Document)response.get("chatdata");
                mainScreen.updateChat(messages);
            }
            else {
                System.out.println(response.getString("error"));
            }
        } catch (java.io.IOException e1) {
            System.out.println(e1);
            e1.printStackTrace();
        } catch (ClassNotFoundException e2) {
            System.out.println(e2);
        }
    }

    public static void sendDataToServer(Document data){
        try {
            System.out.println(data);
            outToServer.writeObject(data);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
