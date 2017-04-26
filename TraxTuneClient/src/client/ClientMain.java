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
            address = InetAddress.getByName("localhost");
            server  = new Socket(address,7777);

        }
        catch (java.io.IOException e){
            System.err.println(e);
        }

        //ClientMain theServerListener = new ClientMain();
        new Thread(new ClientMain()).start();
        LoginScreen = new ClientLoginUI();
    }


    public void run(){
        try {
            outToServer = new ObjectOutputStream(server.getOutputStream());
            inFromServer = new ObjectInputStream(server.getInputStream());

        }
        catch(IOException er){
            er.printStackTrace();
        }
        while (true) try {
            System.out.println("listening for server stuff...");


            Object objResponse = inFromServer.readObject();
            Document response = (Document) objResponse;
            System.out.println(response);
            if (Objects.equals(response.getString("header"), "loginsuccess")) {
                //success event
                System.out.println("successfully logged in");
                userName = response.getString("name");
                id = response.getString("_id");
                Document userData = response.get("profile",Document.class);
                LoginScreen.close();
                mainScreen = new MainUI(server, userData);

            }
            else if (Objects.equals(response.getString("header"), "alluserdata")){
                //call something in MainUI?
                ArrayList<String> userArray;
                userArray = (ArrayList<String>)response.get("users");
                mainScreen.setPeopleYouMayKnow(userArray);
                System.out.println(response.get("users"));
            }
            else if (Objects.equals(response.getString("header"), "refreshaccount")){
                Document userDetails = (Document) response.get("userdetails");
                mainScreen.refreshAccount(userDetails);
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
