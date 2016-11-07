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
public class ImageGame extends JFrame{
    private Socket client;
    private OutputStream rOut;
    private PrintWriter out;
    private InputStream rIn;
    private BufferedReader in;
    private JLabel points;
    private boolean waitForUser = true;
    private JButton guess;
    private JTextArea input;

    public ImageGame(Socket client) {
        try
        {
            this.client = client;
            rIn = new BufferedInputStream(client.getInputStream());
            rOut = client.getOutputStream();
            in = new BufferedReader(new InputStreamReader(rIn));
            out = new PrintWriter(client.getOutputStream());
            points = new JLabel();
            guess = new JButton();
            input = new JTextArea();
            add(points, BorderLayout.NORTH);
            add(input, BorderLayout.WEST);
            add(guess, BorderLayout.EAST);
            gameHandler();
        }catch (SocketException e) {
            JOptionPane.showMessageDialog(this, "Server connection lost");
        } catch(IOException e)
        {

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void gameHandler() throws IOException, ClassNotFoundException {
        boolean resign = false;
        String line;

        while(!resign)
        {
            line = in.readLine();
            if (line.equals("YOUR TURN"))
            {
                setUserWait(true);
                while(waitForUser)
                {
                    guess.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent e)
                        {
                            if (input.getText().isEmpty())
                            {
                                out.println("SEND ANSWER");
                                out.flush();
                                out.println(input.getText());
                                setUserWait(false);
                            }
                        }
                    });
                }
            } else if (line.equals("SEND IMAGE"))
            {
                getImage();
            } else if (line.equals("GET IMAGE"))
            {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(ImageGame.this);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = fc.getSelectedFile();
                    sendImage(file);
                }
            } else if (line.equals("SEND QUESTION"))
            {
                out.println(JOptionPane.showInputDialog("What object must the user guess"));
                out.flush();
            }else if (line.equals("POINTS"))
            {
                String[] ts = in.readLine().split("\\s");
                points.setText("You: " + ts[0] + "\tOpp: " + ts[1]);
            }else if (line.equals("YOU LOSE"))
            {
                JOptionPane.showMessageDialog(this, "You lost");
                System.exit(1);
            }else if (line.equals("YOU WIN"))
            {
                JOptionPane.showMessageDialog(this, "You Won");
                System.exit(1);
            }
        }
    }

    private void setUserWait(boolean v)
    {
        waitForUser = v;
    }

    public void sendImage(File path) throws IOException
    {
        BufferedImage img = ImageIO.read(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", byteArrayOutputStream);
        OutputStream os = rOut;
        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
        os.write(size);
        os.write(byteArrayOutputStream.toByteArray());
        os.flush();
    }
    public BufferedImage getImage() throws IOException, ClassNotFoundException
    {
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
