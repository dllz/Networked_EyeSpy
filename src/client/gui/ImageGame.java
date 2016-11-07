package client.gui;

import general.models.*;
import server.processing.GameHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

/**
 * Created by Daniel on 2016/11/07.
 * initializeGui comes from http://stackoverflow.com/questions/21077322/create-a-chess-board-with-jpanel
 *      Authored by Andrew Thompson on Jan 13 2014 at 16:34 Last edited on Apr 13 2016 at 23:44
 */
public class ImageGame extends JFrame{
    private Socket client;
    private OutputStream rOut;
    private PrintWriter out;
    private InputStream rIn;
    private BufferedReader in;
    private JLabel points;
    boolean waitForUser = true;

    public ImageGame(Socket client) {
        try
        {
            this.client = client;
            rIn = new BufferedInputStream(client.getInputStream());
            rOut = client.getOutputStream();
            in = new BufferedReader(new InputStreamReader(rIn));
            out = new PrintWriter(client.getOutputStream());
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

                }
            } else if (line.equals("SEND GAME"))
            {
                getGame();
                redrawBoard(player);
            } else if (line.equals("GET GAME"))
            {
                sendGame();
            } else if (line.equals("YOU LOSE"))
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

    public void sendObject(Game obj) throws IOException
    {
        System.out.println("Sending data" + " " + System.currentTimeMillis());

        dataOut.writeObject(obj);
        dataOut.flush();
    }
    public Game getObject() throws IOException, ClassNotFoundException
    {
        Game res;
        res =  (Game) dataIn.readObject();
        return res;
    }

}
