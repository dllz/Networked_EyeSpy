package server.networking;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

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



    public void sendImage(BufferedImage path) throws IOException
    {
        out.println("SEND IMAGE");
        out.flush();
        BufferedImage img = path;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", byteArrayOutputStream);
        OutputStream os = rOut;
        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
        os.write(size);
        os.write(byteArrayOutputStream.toByteArray());
        os.flush();
    }
    public BufferedImage getImage() throws IOException
    {
        out.println("GET IMAGE");
        out.flush();
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

    public String getAnswer() {
        return this.answer;
    }

    public int getPoints() {
        return points;
    }

    public void receiveAnswer()
    {
        try
        {
            String temp = in.readLine();
            if (temp.equals("SEND ANSWER"))
            {
                answer = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getQuestion() {
        receiveAnswer();
        return this.question;
    }

    public void sendPoints(int you, int opp) {
        out.println("POINTS");
        out.flush();
        out.println(you + " " + opp);
        out.flush();
    }

    public void addPoint() {
        this.points++;
    }

    public void sendLose() {
        out.println("YOU LOSE");
        out.flush();
    }

    public void sendWin() {
        out.println("YOU WIN");
        out.flush();
    }

    public void receiveQuestion()
    {
        try
        {
            out.println("SEND QUESTION");
            out.flush();
            String temp = in.readLine();
            question = temp;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
