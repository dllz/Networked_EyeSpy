package server.server;

import java.io.*;
import java.net.Socket;

/**
 * Created by Daniel on 2016/11/02.
 */
public class TCPForwarder implements Runnable {
    private OutputStream rOut;
    private PrintWriter out;
    private InputStream rIn;
    private BufferedReader in;
    private Socket connect;

    /**
     * forwards to the waiting room
     *
     * @param connect socket you are forwarding
     */
    public TCPForwarder(Socket connect) {
        this.connect = connect;

    }

    /**
     * Thread that binds stream and forwards it.
     * Also notifies the client
     */
    @Override
    public void run() {
        try {
            rIn = new BufferedInputStream(connect.getInputStream());
            rOut = connect.getOutputStream();
            in = new BufferedReader(new InputStreamReader(rIn));
            out = new PrintWriter(rOut);
            System.out.println("Streams binded");
            out.println("GET PLAYERS");
            out.flush();
            System.out.println("Waiting for input");
            String line = in.readLine();
            String[] splits = line.split("\\s");
            System.out.println("Adding to game setup");
            GameSetUp.addPlayer(connect, splits[0], splits[1]);//sends to array
            out.println("WAITING");//notifies client
            out.flush();
        } catch (IOException e) {
            System.out.println("Client Disconnected");//incase user disconnects while waiting
            GameSetUp.removePlayer(connect);
            Thread.yield();
        }

    }
}
