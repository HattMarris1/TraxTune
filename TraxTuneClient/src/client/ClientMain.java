package client;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;

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
    public void run(){
        while (true) {
            try {
                System.out.println("listening for server stuff...");

                DataInputStream inFromServer = new DataInputStream(server.getInputStream());
                String text = inFromServer.readUTF();
            }
        catch (java.io.IOException e){
                System.out.println(e);
            }
        }
    }
}
