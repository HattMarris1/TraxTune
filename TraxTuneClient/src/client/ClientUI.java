package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;
import java.net.*;
import communication.loginInfo;
import communication.registrationInfo;
/**
 * Created by Matthew on 27/02/2017.
 */

public class ClientUI{
    private JPanel panel1;
    private JLabel Title;
    private JTextField UserNameBox;
    private JPasswordField PasswordBox;
    private JButton LoginButton;
    private JButton RegisterButton;
    private Socket serverSocket;


    public ClientUI(Socket server) {

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
        JFrame frame = new JFrame("ClientUI");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);

        frame.setVisible(true);

    }


private void sendLoginInfoToServer(String userName, char[] password)throws Exception{
    loginInfo userInfo = new loginInfo();
    userInfo.name = userName;
    userInfo.password = new String(password);

    ObjectOutputStream outputStream = new ObjectOutputStream(serverSocket.getOutputStream());
    outputStream.reset();
    outputStream.writeObject(userInfo);
}
private void sendRegistrationInfoToServer(String userName, char[] password) throws Exception{
        registrationInfo userInfo = new registrationInfo();
        userInfo.name = userName;
        userInfo.password= new String(password);

    ObjectOutputStream outputStream = new ObjectOutputStream(serverSocket.getOutputStream());
    outputStream.flush();
    outputStream.writeObject(userInfo);
}
}
