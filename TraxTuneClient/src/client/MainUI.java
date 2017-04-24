package client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Matthew on 24/04/2017.
 */
public class MainUI {

    private JTextArea textArea1;

    public MainUI() {
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



}
