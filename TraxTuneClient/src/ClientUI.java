import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.net.*;

/**
 * Created by Matthew on 27/02/2017.
 */
public class ClientUI {
    private JPanel panel1;
    private JLabel Title;
    private JTextField UserNameBox;
    private JPasswordField PasswordBox;
    private JButton LoginButton;
    private JButton RegisterButton;

    public ClientUI() {
        LoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                String userName = UserNameBox.getText();
                char[] userPassword = PasswordBox.getPassword();
                try {
                    sendUserNameAndPasswordToServer(userName, userPassword);
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
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ClientUI");
        frame.setContentPane(new ClientUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);

        frame.setVisible(true);
    }
private static void sendUserNameAndPasswordToServer(String userName, char[] password)throws Exception{
    InetAddress address = InetAddress.getByName("localhost");
    Socket server  = new Socket(address,7777);
    DataOutputStream outputStream = new DataOutputStream(server.getOutputStream());
    outputStream.writeUTF(userName);
}
}
