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
public class GameNetworkHandler {
    private OutputStream rOut;
    private PrintWriter out;
    private InputStream rIn;
    private BufferedReader in;
    private Socket connect;
    private String question = "";
    private String answer = "";
    private int points = 0;

    /**
     * Default constructor for the tcp handler for one of the players
     *
     * @param ConnectionToClient Connected client socket
     * @throws IOException when shit goes wrong like usual
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
     * Lets player know that it is their turn
     */
    public void notifyTurn() {
        out.println("YOUR TURN");
        out.flush();
    }

    /**
     * Sends the image to the client
     *
     * @param path BufferedImage to be sent
     * @throws IOException if it fails
     */
    public void sendImage(BufferedImage path) throws IOException {
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

    /**
     * Gets the chosen image from the client
     *
     * @return The receieved bufferedimage
     * @throws IOException if it fails
     */
    public BufferedImage getImage() throws IOException {
        out.println("GET IMAGE");
        out.flush();
        InputStream is = rIn;
        BufferedImage res = null;
        boolean wait = true;
        while(wait)
        {
            String line = in.readLine();
            if (line.equals("SENDING IMAGE"))
            {
                try {
                    byte[] sizeAr = new byte[4];
                    is.read(sizeAr);
                    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
                    byte[] imageAr = new byte[size];
                    is.read(imageAr);
                    res = ImageIO.read(new ByteArrayInputStream(imageAr));
                    wait = false;
                } catch (SocketException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return res;
    }

    /**
     * The answer from client
     * Requests the answer from the client and returns it
     * @return String answer
     */
    public String getAnswer() {
        receiveAnswer();
        return this.answer;
    }

    /**
     * The clients points
     *
     * @return int points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Gets the answer from the client and assigns it to the answer variable
     */
    public void receiveAnswer() {
        try {
            String temp = in.readLine();
            if (temp.equals("SEND ANSWER")) {
                answer = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asks client for answer
     *
     * @return new answer
     */
    public String getQuestion() {
        return this.question;
    }

    /**
     * Sends the points to the user
     *
     * @param you the clients points
     * @param opp the opponents points
     */
    public void sendPoints(int you, int opp) {
        out.println("POINTS");
        out.flush();
        out.println(you + " " + opp);
        out.flush();
    }

    /**
     * Adds 1 to the clients current points
     */
    public void addPoint() {
        this.points++;
    }

    /**
     * Notifies the client that he has lost
     */
    public void sendLose() {
        out.println("YOU LOSE");
        out.flush();
    }

    /**
     * Notifies client that he has won
     */
    public void sendWin() {
        out.println("YOU WIN");
        out.flush();
    }

    /**
     * Gets the question for the user
     */
    public void receiveQuestion() {
        try {
            out.println("SEND QUESTION");
            out.flush();
            String temp = in.readLine();
            question = temp;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
