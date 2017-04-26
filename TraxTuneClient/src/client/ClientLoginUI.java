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
    private JFrame frame;

    public ClientLoginUI() {

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

            }
        });
        RegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = UserNameBox.getText();
                char[] userPassword = PasswordBox.getPassword();

                sendRegistrationInfoToServer(userName,userPassword);

            }
        });

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

private void sendLoginInfoToServer(String userName, char[] password){
    Document userInfoDoc = new Document("header","login")
            .append("userName",userName)
            .append("password",new String(password));
    ClientMain.sendDataToServer(userInfoDoc);

}
private void sendRegistrationInfoToServer(String userName, char[] password){
    Document userInfoDoc = new Document("header","register")
            .append("userName",userName)
            .append("password",new String(password));

    ClientMain.sendDataToServer(userInfoDoc);
}
}
