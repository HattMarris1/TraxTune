package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;
import java.net.*;

import com.mongodb.Mongo;
import org.bson.BSON;
import org.bson.Document;

/**
 * Created by Matthew on 27/02/2017.
 */

public class ClientLoginUI {
    private JPanel panel1;
    private JLabel Title;
    private JTextField UserNameBox;
    private JPasswordField PasswordBox;
    private JButton LoginButton;
    private JButton RegisterButton;
    private Socket serverSocket;
    private JFrame frame;

    public ClientLoginUI(Socket server) {

        LoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                String userName = UserNameBox.getText();
                char[] userPassword = PasswordBox.getPassword();
                try {
                    sendLoginInfoToServer(userName, userPassword);
                }
                catch (Exception e1){
                }
                /*
                MessageDigest md = null;
                
                try {

                     md = MessageDigest.getInstance("SHA");
                }
                catch (NoSuchAlgorithmException e1){

                }
                byte[] userPasswordBytes =new byte[userPassword.length];
                for (int i =0;i<userPassword.length;i++){
                    userPasswordBytes[i] = (byte)userPassword[i];
                }

                md.update(userPasswordBytes);
                byte HashText[]=md.digest();
                System.out.println(HashText + userName);
                md.reset();
                */
            }
        });
        RegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = UserNameBox.getText();
                char[] userPassword = PasswordBox.getPassword();
                try {
                    sendRegistrationInfoToServer(userName,userPassword);
                }
                catch (Exception e1){

                }
            }
        });
        serverSocket=server;
        frame = new JFrame("ClientLoginUI");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);

        frame.setVisible(true);

    }

    public void close(){
        frame.setVisible(false);
        frame.dispose();
    }

private void sendLoginInfoToServer(String userName, char[] password)throws Exception{
    Document userInfoDoc = new Document("header","login")
            .append("userName",userName)
            .append("password",new String(password));

    ObjectOutputStream outputStream = new ObjectOutputStream(serverSocket.getOutputStream());
    outputStream.reset();
    outputStream.writeObject(userInfoDoc);
}
private void sendRegistrationInfoToServer(String userName, char[] password) throws Exception{
    Document userInfoDoc = new Document("header","register")
            .append("userName",userName)
            .append("password",new String(password));

    ObjectOutputStream outputStream = new ObjectOutputStream(serverSocket.getOutputStream());
    outputStream.flush();
    outputStream.writeObject(userInfoDoc);
}
}
