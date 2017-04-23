package client;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Matthew on 19/04/2017.
 */
public class serverListener  implements Runnable{
    public void run(){
        while (true) {
            try {
                InetAddress address = InetAddress.getByName("localhost");
                Socket server = new Socket(address, 9090);

                DataInputStream inFromServer = new DataInputStream(server.getInputStream());
                String text = inFromServer.readUTF();
            }
        catch (Exception e){
            }
        }
    }
}
