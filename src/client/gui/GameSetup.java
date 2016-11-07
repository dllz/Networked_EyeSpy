package client.gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Created by Daniel on 2016/11/07.
 */
public class GameSetup extends JFrame {
    private final String HOST = "localhost";
    private final int PORT = 7683;
    private JLabel notice;
    private OutputStream rOut;
    private PrintWriter out;
    private InputStream rIn;
    private BufferedReader in;

    /**
     * Sets up connection with server and waits for player match
     */
    public GameSetup() {
        setTitle("Game Setup");
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(200, 75);
        int width = this.getWidth() / 2;
        int height = this.getHeight() / 2;
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - width;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - height;
        this.setLocation(x, y);//displays center screen
        notice = new JLabel();
        add(notice, BorderLayout.CENTER);
        String user = JOptionPane.showInputDialog("Please enter your username");
        String opp = JOptionPane.showInputDialog("Please enter your opponents username");//for matching
        Socket connect = null;
        try {
            connect = new Socket(HOST, PORT);
            notice.setText("Connected to server");//live output to keep you informed
            rIn = new BufferedInputStream(connect.getInputStream());
            rOut = connect.getOutputStream();
            in = new BufferedReader(new InputStreamReader(rIn));
            out = new PrintWriter(connect.getOutputStream());
        } catch (IOException e) {
            notice.setText("Server connection failed");
        }

        boolean waiting = true;
        String line;
        while (waiting) {
            try {
                System.out.println("Reading input stream");
                line = in.readLine();
                if (line.equals("GET PLAYERS")) {
                    out.println(user + " " + opp);//sends the players to server
                    notice.setText("Sending to server");
                    out.flush();
                } else if (line.equals("WAITING")) {
                    notice.setText("Waiting for game match");
                } else if (line.equals("GAME MATCHED")) {
                    waiting = false;
                    this.setVisible(false);
                    ImageGame game = new ImageGame(connect);//sends to the client game gui
                }
            } catch (IOException e) {
                notice.setText("Server connection lost");//incase client disappears
            }

        }

    }

}
