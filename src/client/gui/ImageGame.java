package client.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * Created by Daniel on 2016/11/07.
 */
public class ImageGame extends JFrame {
    private Socket client;
    private OutputStream rOut;
    private PrintWriter out;
    private InputStream rIn;
    private BufferedReader in;
    private JLabel points;
    private boolean waitForUser = true;
    private JButton guess;
    private JTextField input;

    /**
     * Intiates the client game gui
     *
     * @param client The socket of the client
     */
    public ImageGame(Socket client) {
        try {
            setTitle("Eye Spy");
            this.client = client;
            rIn = new BufferedInputStream(client.getInputStream());
            rOut = client.getOutputStream();
            in = new BufferedReader(new InputStreamReader(rIn));
            out = new PrintWriter(client.getOutputStream());
            points = new JLabel("You: 0    Opp: 0");
            guess = new JButton("Send Guess");
            input = new JTextField("Enter Guess Here");
            add(points, BorderLayout.NORTH);
            add(input, BorderLayout.WEST);
            add(guess, BorderLayout.EAST);//these are very ugly but im not a visual designer. As the guys at entelect say front end is not visual
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setLocationRelativeTo(null);
            this.setSize(400, 250);
            int width = this.getWidth() / 2;
            int height = this.getHeight() / 2;
            int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - width;
            int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - height;//works out center of screen
            this.setLocation(x, y);
            this.setVisible(true);
            gameHandler();
        } catch (SocketException e) {
            JOptionPane.showMessageDialog(this, "Server connection lost");//incase the server disconnects
        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles server requests and sends responses
     *
     * @throws IOException            yeah shit happens
     * @throws ClassNotFoundException incase i lose the classes
     */
    private void gameHandler() throws IOException, ClassNotFoundException {
        boolean resign = false;
        String line;

        while (!resign) {
            line = in.readLine();
            if (line.equals("YOUR TURN")) {
                setUserWait(true);
                while (waitForUser) { //listens till the user has answered then ignores the button
                    guess.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (input.getText().isEmpty()) {
                                out.println("SEND ANSWER");
                                out.flush();
                                out.println(input.getText());
                                setUserWait(false);
                            }
                        }
                    });
                }
            } else if (line.equals("SEND IMAGE")) //Gets the image from the server
            {
                BufferedImage image = getImage();
                JDialog dialog = new JDialog();
                if (image instanceof Image) {
                    ImageIcon icon = new ImageIcon((Image) image);
                    JLabel label = new JLabel(icon);
                    dialog.add(label);
                    dialog.pack();
                    dialog.setVisible(true);//displays it in a nice frame seperately
                }
            } else if (line.equals("GET IMAGE")) {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(ImageGame.this);//prompts user for the picture he wants to send
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    sendImage(file);
                }
            } else if (line.equals("SEND QUESTION")) {
                out.println(JOptionPane.showInputDialog("What object must the user guess"));//asks the client what object his opponent must guess
                out.flush();
            } else if (line.equals("POINTS")) {
                String[] ts = in.readLine().split("\\s");
                points.setText("You: " + ts[0] + "\tOpp: " + ts[1]);//displays current points
            } else if (line.equals("YOU LOSE")) {
                JOptionPane.showMessageDialog(this, "You lost");
                System.exit(1);//close the game cause its over
            } else if (line.equals("YOU WIN")) {
                JOptionPane.showMessageDialog(this, "You Won");
                System.exit(1);//close the game cause u won and no one can touch u
            }
        }
    }

    /**
     * Has to change userWait here because it can not be referenced inside an action listener
     *
     * @param v the new boolean value
     */
    private void setUserWait(boolean v) {
        waitForUser = v;
    }

    /**
     * Sends the image to the client
     *
     * @param path BufferedImage to be sent
     * @throws IOException if it fails
     */
    public void sendImage(File path) throws IOException {
        BufferedImage img = ImageIO.read(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", byteArrayOutputStream);
        OutputStream os = rOut;
        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
        os.write(size);
        os.write(byteArrayOutputStream.toByteArray());
        os.flush();
    }

    /**
     * Gets the chosen image from the client
     *
     * @return The receieved bufferedimage
     * @throws IOException if it fails
     */
    public BufferedImage getImage() throws IOException {
        InputStream is = rIn;
        BufferedImage res = null;
        try {
            byte[] sizeAr = new byte[4];
            is.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
            byte[] imageAr = new byte[size];
            is.read(imageAr);
            res = ImageIO.read(new ByteArrayInputStream(imageAr));
        } catch (SocketException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return res;
    }

}
