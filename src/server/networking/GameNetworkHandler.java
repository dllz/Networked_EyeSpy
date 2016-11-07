package server.networking;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * Created by Daniel on 2016/11/02.
 */
public class GameNetworkHandler
{
    private OutputStream rOut;
    private PrintWriter out;
    private InputStream rIn;
    private BufferedReader in;
    private Socket connect;
    private String question = "";
    private String answer = "";
    private int points = 0;

    /**
     * Default constructor
     *
     * @param ConnectionToClient Connected server socket
     */
    public GameNetworkHandler(Socket ConnectionToClient) throws IOException {
        // Bind Streams
        System.out.println("Attempting to bind strams");
        connect = ConnectionToClient;
        rIn = new BufferedInputStream(connect.getInputStream());
        rOut = connect.getOutputStream();
        in = new BufferedReader(new InputStreamReader(rIn));
        out = new PrintWriter(rOut);
        System.out.println("Streams binded");
        out.println("GAME MATCHED");
        out.flush();
        System.out.println("First command sent");
    }

    /**
     * The code below is to fix problems with ImageIO.read not clearing all bytes of an image.
     */
    private static void clearInput(InputStream is) throws IOException {
        int extra = is.available();
        if (extra > 0) {
            byte[] buffer = new byte[extra];
            is.read(buffer);
            System.out.println(extra + " " + new String(buffer));
        }
    }
    public void notifyTurn()
    {
        out.println("YOUR TURN");
        out.flush();
    }



    public void sendImage(BufferedImage image) throws IOException
    {
        System.out.println("Sending data" + " " + System.currentTimeMillis());
        //TO-DO Send image code
    }

    public BufferedImage getImage() {
        //TO-DO Get image
        BufferedImage img = null;

        return img;
    }

    public String getAnswer() {
        return this.answer;
    }

    public String getQuestion() {
        return this.question;
    }

    public int getPoints() {
        return points;
    }

    public void addPoint() {
        this.points++;
    }
}
