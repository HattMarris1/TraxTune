package client;

import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import org.bson.Document;

/**
 * Created by Matthew on 19/04/2017.
 */
public class ClientMain implements Runnable{
    public static InetAddress address;
    public static Socket server;
    public static ClientLoginUI LoginScreen;

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
        LoginScreen = new ClientLoginUI(server);
    }

    public static void userLoggedin(){
        //TODO: shut login screen and open the main window
        LoginScreen.close();

    }

    public void run(){
        while (true) try {
            System.out.println("listening for server stuff...");

            ObjectInputStream inFromServer = new ObjectInputStream(server.getInputStream());
            Object objResponse = inFromServer.readObject();
            Document response = (Document) objResponse;
            System.out.println(response);
            if (response.getBoolean("success")==true) {
                //success event
                System.out.println("successfully logged in");
                LoginScreen.close();
            } else {
                System.out.println(response.getString("error"));
            }
        } catch (java.io.IOException e1) {
            System.out.println(e1);
        } catch (ClassNotFoundException e2) {
            System.out.println(e2);
        }
    }
}
