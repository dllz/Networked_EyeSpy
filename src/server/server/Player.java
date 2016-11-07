package server.server;

import java.net.Socket;

/**
 * Created by Daniel on 2016/11/07.
 */
public class Player {
    private Socket socket;
    private String yourName;
    private String oppName;

    /**
     * Default constructor for Player
     *
     * @param socket   Client socket
     * @param yourName client username
     * @param oppName  opponent username
     */
    public Player(Socket socket, String yourName, String oppName) {

        this.socket = socket;
        this.yourName = yourName;
        this.oppName = oppName;
    }

    /**
     * Gets the client socket
     *
     * @return the client socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Gets the clients username
     *
     * @return the clients username
     */
    public String getYourName() {
        return yourName;
    }

    /**
     * Gets the username of the opponent
     *
     * @return the username of the opponent
     */
    public String getOppName() {
        return oppName;
    }
}
