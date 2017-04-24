package client;

import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import communication.serverResponse;

/**
 * Created by Matthew on 19/04/2017.
 */
public class ClientMain implements Runnable{
    public static InetAddress address;
    public static Socket server;
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
        ClientUI LoginScreen = new ClientUI(server);
    }

    public static void userLoggedin(){
        //TODO: shut login screen and open the main window
    }

    public void run(){
        while (true) try {
            System.out.println("listening for server stuff...");

            ObjectInputStream inFromServer = new ObjectInputStream(server.getInputStream());
            Object objResponse = inFromServer.readObject();
            serverResponse response = (serverResponse) objResponse;
            System.out.println(response);
            if (response.success) {
                //success event
                System.out.println("succesfully logged in");
                main(null);
            } else {
                System.out.println(response.description);
            }
        } catch (java.io.IOException e1) {
            System.out.println(e1);
        } catch (ClassNotFoundException e2) {
            System.out.println(e2);
        }
    }
}
